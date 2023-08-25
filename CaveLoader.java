/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.CaveControl;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

import com.google.common.base.Charsets;

import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

import Reika.CaveControl.CaveDefinition.ControlOptions;
import Reika.DragonAPI.IO.ReikaFileReader;
import Reika.DragonAPI.Instantiable.Data.Maps.MultiMap;
import Reika.DragonAPI.Instantiable.IO.LuaBlock;
import Reika.DragonAPI.Instantiable.IO.LuaBlock.LuaBlockDatabase;
import Reika.DragonAPI.Libraries.Java.ReikaStringParser;
import Reika.DragonAPI.Libraries.World.ReikaBiomeHelper;


public class CaveLoader {

	public static final CaveLoader instance = new CaveLoader();

	private LuaBlockDatabase data;

	private final MultiMap<BiomeGenBase, CaveDefinition> entries = new MultiMap<BiomeGenBase, CaveDefinition>().setNullEmpty().setOrdered((c1, c2) -> c1.compareTo(c2));
	private CaveDefinition global;

	private CaveLoader() {
		data = new LuaBlockDatabase();
		CaveLuaBlock base = new CaveLuaBlock("global", null, data);
		base.putData("type", "global");
		for (int i = 0; i < ControlOptions.optionList.length; i++) {
			ControlOptions c = ControlOptions.optionList[i];
			base.putData(c.luaTag, String.valueOf(c.defaultValue));
		}
		data.addBlock("global", base);
		//global = new CaveDefinition("global", base);
	}

	private final File getSaveFolder() {
		return new File(CaveControl.config.getConfigFolder(), "Cave_Definitions");
	}

	public void generateGlobalFile() {
		File f = new File(this.getSaveFolder(), "global.lua");
		f.getParentFile().mkdirs();
		if (!f.exists()) {
			try {
				f.createNewFile();
				ArrayList<String> li = data.getBlock("global").writeToStrings();
				ReikaFileReader.writeLinesToFile(f, li, true, Charsets.UTF_8);
			}
			catch (Exception e) {
				throw new RuntimeException(e);
			}
		}

		f = new File(this.getSaveFolder(), "info.txt");
		if (f.exists())
			f.delete();
		try {
			f.createNewFile();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}

		ArrayList<String> li = new ArrayList();
		li.add("This file is not loaded as a config; it is here to document the parameters and explain the configs' use.");
		li.add("CaveControl uses the LuaBlock system, meaning one or more prototypes/definitions are defined similar to lua tables.");
		li.add("These blocks can be defined in any order, spread across any number of valid files (.txt, .lua, .ini, .cfg, .yml).");
		li.add("Each block has a number of required parameters, detailed below:");
		li.add("===========================================================================");
		li.add("\"type\": The ID of the biome for which the definition applies. IDs are determined based on the biome name and ID;");
		li.add("For example, vanilla plains would be "+sampleIDString(BiomeGenBase.plains)+", and mega taiga is "+sampleIDString(BiomeGenBase.megaTaiga));
		li.add("To specify by ID only, use '*' for the name.");
		li.add("Note that mutated and subbiomes (eg Ice Spikes (Ice Plains), ForestHills, Extreme Hills Edge, and Flower Forest) cannot have their own definitions; they inherit from the parent biome.");
		li.add("");
		li.add("\"inherit\": Unspecified parameters are inherited from this parent. This prevents you from having to redefine all of the parameters");
		li.add("repeatedly when only changing one or two values. Inheritance is normally from 'global', which is the base data used for all biomes.");
		li.add("These two parameters MUST be specified.");
		li.add("===========================================================================");
		li.add("Cave generation parameters:");
		for (int i = 0; i < ControlOptions.optionList.length; i++) {
			ControlOptions c = ControlOptions.optionList[i];
			li.add("\""+ReikaStringParser.padToLength(c.luaTag+"\":", 20, " ")+"\t"+ReikaStringParser.padToLength(c.name, 30, " ")+"\t(vanilla and default = "+c.defaultValue+")");
		}
		li.add("===========================================================================");
		li.add("Consult global.lua for an example definition file. Feel free to modify it as the base template from which others extend,");
		li.add("but it should always have one and only one definition - that of the base template.");
		li.add("Note: If a biome has no specific template specified, it entirely uses the behavior defined in global.");
		ReikaFileReader.writeLinesToFile(f, li, true, Charsets.UTF_8);
	}

	public int loadConfigs() {
		int ret = 0;
		this.reset();
		CaveControl.logger.log("Loading configs.");
		File f = this.getSaveFolder(); //parent dir
		if (f.exists()) {
			this.loadFiles(f);
			ret += this.parseConfigs();

			CaveControl.logger.log("Configs loaded.");
		}
		else {
			try {
				f.mkdirs();
			}
			catch (Exception e) {
				e.printStackTrace();
				CaveControl.logger.logError("Could not create cave config folder!");
			}
		}
		return ret;
	}

	private void loadFiles(File parent) {
		ArrayList<File> files = ReikaFileReader.getAllFilesInFolder(parent, ".lua", ".ini", ".cfg", ".txt", ".yml");
		for (File f : files) {
			if (!f.getName().equals("info.txt"))
				data.loadFromFile(f);
		}
	}

	private int parseConfigs() {
		int ret = 0;
		LuaBlock root = data.getRootBlock();
		for (LuaBlock b : root.getChildren()) {
			try {
				String s = b.getString("type");
				if (!s.equals("global") && !b.containsKey("inherit"))
					throw new IllegalArgumentException("Non-global entries must define a parent to inherit from!");
				data.addBlock(s, b);
				CaveDefinition cave = new CaveDefinition(s, b);
				if (s.equals("global")) {
					global = cave;
				}
				else {
					CaveControl.logger.debug("Loaded cave definition:\n"+cave);
					String[] parts = s.split("#");
					if (parts.length != 2)
						throw new IllegalArgumentException("Invalid fomratting");
					int id = Integer.parseInt(parts[0]);
					String name = parts[1];
					BiomeGenBase biome = BiomeGenBase.biomeList[id];
					if (biome == null)
						throw new IllegalArgumentException("No such biome ID "+id);
					LuaBlock dims = b.getChild("dimensions");
					if (dims != null) {
						for (String val : dims.getDataValues())
							cave.addDimensionID(Integer.parseInt(val));
					}
					if (biome.biomeName.equals(name) || biome.biomeName.replace(" ", "").equals(name) || "*".equals(name))
						entries.addValue(biome, cave);
					else
						throw new IllegalArgumentException("Biome ID "+id+" maps to "+biome.biomeName+", not "+name);
				}
			}
			catch (Exception e) {
				CaveControl.logger.logError("Could not parse config section "+b.getString("type")+": ");
				e.printStackTrace();
				ret++;
			}
		}
		CaveControl.logger.log("All config entries parsed.");
		return ret;
	}

	private void reset() {
		LuaBlock base = data.getBlock("global");
		data = new LuaBlockDatabase();
		entries.clear();
		data.addBlock("global", base);
		global = null;
	}

	private static String sampleIDString(BiomeGenBase b) {
		return ReikaStringParser.stripSpaces(b.biomeID+"#"+b.biomeName);
	}

	/** BLOCK COORDS */
	public CaveDefinition getDefinition(World world, int x, int z) {
		BiomeGenBase b = this.getEffectiveBiome(world, x, z);
		Collection<CaveDefinition> c = entries.get(b);
		if (c == null)
			return global;
		for (CaveDefinition d : c) {
			if (d.isApplicable(world))
				return d;
		}
		return global;
	}

	/** BLOCK COORDS */
	public BiomeGenBase getEffectiveBiome(World world, int x, int z) {
		BiomeGenBase b = world.getBiomeGenForCoords(x, z);
		if (b == null) {
			CaveControl.logger.logError("Found null biome at "+x+", "+z+"?!");
			return null;
		}
		b = ReikaBiomeHelper.getParentBiomeType(b, true);
		return b;
	}

	static class CaveLuaBlock extends LuaBlock {

		CaveLuaBlock(String n, LuaBlock lb, LuaBlockDatabase db) {
			super(n, lb, db);

			/*
			for (int i = 0; i < ControlOptions.optionList.length; i++) {
				ControlOptions c = ControlOptions.optionList[i];
				requiredElements.add(c.luaTag);
			}
			requiredElements.add("inherit");
			 */
		}

	}

}
