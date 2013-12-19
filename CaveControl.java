/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.CaveControl;

import java.net.URL;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.Event.Result;
import net.minecraftforge.event.EventPriority;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.terraingen.InitMapGenEvent;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;
import Reika.CaveControl.Generators.ControllableCaveGen;
import Reika.CaveControl.Generators.ControllableMineshaftGen;
import Reika.CaveControl.Generators.ControllableRavineGen;
import Reika.CaveControl.Generators.ControllableStrongholdGen;
import Reika.CaveControl.Registry.CaveOptions;
import Reika.CaveControl.Registry.ControlOptions;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.Auxiliary.BiomeTypeList;
import Reika.DragonAPI.Base.DragonAPIMod;
import Reika.DragonAPI.Instantiable.IO.ModLogger;
import Reika.DragonAPI.Libraries.ReikaRegistryHelper;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;

@Mod( modid = "CaveControl", name="CaveControl", version="beta", certificateFingerprint = "@GET_FINGERPRINT@", dependencies="required-after:DragonAPI")
@NetworkMod(clientSideRequired = true, serverSideRequired = true)
public class CaveControl extends DragonAPIMod {

	@Instance("CaveControl")
	public static CaveControl instance = new CaveControl();

	public static final CaveConfig config = new CaveConfig(instance, CaveOptions.optionList, null, null, null, 0);

	public static ModLogger logger;

	public static ControllableCaveGen caveGen;
	public static ControllableMineshaftGen mineGen;
	public static ControllableStrongholdGen strongholdGen;
	public static ControllableRavineGen ravineGen;

	//@SidedProxy(clientSide="Reika.CaveCraft.CaveClient", serverSide="Reika.CaveCraft.CaveCommon")
	//public static CaveCommon proxy;

	@Override
	@EventHandler
	public void preload(FMLPreInitializationEvent evt) {
		config.loadSubfolderedConfigFile(evt);
		config.initProps(evt);

		MinecraftForge.TERRAIN_GEN_BUS.register(this);

		logger = new ModLogger(instance, CaveOptions.LOGLOADING.getState(), CaveOptions.DEBUGMODE.getState(), false);

		ReikaRegistryHelper.setupModData(instance, evt);
		ReikaRegistryHelper.setupVersionChecking(evt);
	}

	@Override
	@EventHandler
	public void load(FMLInitializationEvent event) {
		caveGen = new ControllableCaveGen();
		mineGen = new ControllableMineshaftGen();
		strongholdGen = new ControllableStrongholdGen();
		ravineGen = new ControllableRavineGen();
	}

	@Override
	@EventHandler
	public void postload(FMLPostInitializationEvent evt) {

	}

	@Override
	public String getDisplayName() {
		return "CaveControl";
	}

	@Override
	public String getModAuthorName() {
		return "Reika";
	}

	@Override
	public URL getDocumentationSite() {
		return DragonAPICore.getReikaForumPage(instance);
	}

	@Override
	public boolean hasWiki() {
		return false;
	}

	@Override
	public URL getWiki() {
		return null;
	}

	@Override
	public boolean hasVersion() {
		return false;
	}

	@Override
	public String getVersionName() {
		return null;
	}

	@Override
	public ModLogger getModLogger() {
		return logger;
	}

	@ForgeSubscribe(priority = EventPriority.LOWEST)
	public void caveControl(InitMapGenEvent ev) {
		switch(ev.type) {
		case CAVE:
			ev.newGen = caveGen;
			break;
		case MINESHAFT:
			ev.newGen = mineGen;
			break;
		case NETHER_BRIDGE:
			break;
		case RAVINE:
			ev.newGen = ravineGen;
			break;
		case SCATTERED_FEATURE:
			break;
		case STRONGHOLD:
			ev.newGen = strongholdGen;
			break;
		default:
			//ev.newGen = ev.originalGen;
			break;
		}
	}

	@ForgeSubscribe(priority = EventPriority.LOWEST)
	public void dungeonControl(PopulateChunkEvent.Populate ev) {
		if (ev.type == PopulateChunkEvent.Populate.EventType.DUNGEON) {
			if (CaveOptions.GLOBAL.getState())
				ev.setResult(config.getGlobalBoolean(ControlOptions.DUNGEONS) ? ev.getResult() : Result.DENY);
			else {
				World world = ev.world;
				int x = ev.chunkX*16;
				int z = ev.chunkZ*16;
				BiomeGenBase biome = world.getBiomeGenForCoords(x, z);
				ev.setResult(ControlOptions.DUNGEONS.getBoolean(BiomeTypeList.getEntry(biome)) ? ev.getResult() : Result.DENY);
			}
		}
	}

	public static boolean fillDeepCavesWithLava(BiomeGenBase biome) {
		if (CaveOptions.GLOBAL.getState()) {
			return CaveControl.config.getGlobalBoolean(ControlOptions.DEEPLAVA);
		}
		return ControlOptions.DEEPLAVA.getBoolean(BiomeTypeList.getEntry(biome));
	}

	public static byte getBlockToFillDeepCaves(BiomeGenBase biome) {
		if (CaveOptions.GLOBAL.getState()) {
			return CaveControl.config.getGlobalBoolean(ControlOptions.DEEPWATER) ? (byte)Block.waterMoving.blockID : 0;
		}
		return ControlOptions.DEEPWATER.getBoolean(BiomeTypeList.getEntry(biome)) ? (byte)Block.waterMoving.blockID : 0;

	}

}
