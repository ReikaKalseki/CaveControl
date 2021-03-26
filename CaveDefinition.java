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

import java.util.EnumMap;
import java.util.HashMap;

import Reika.DragonAPI.Instantiable.IO.LuaBlock;



public class CaveDefinition {

	private static final HashMap<String, ControlOptions> lookups = new HashMap();

	private final EnumMap<ControlOptions, String> options = new EnumMap(ControlOptions.class);

	public CaveDefinition(String name, LuaBlock b) {
		for (int i = 0; i < ControlOptions.optionList.length; i++) {
			ControlOptions c = ControlOptions.optionList[i];
			//if (!b.containsKeyInherit(c.luaTag))
			//	throw new IllegalArgumentException("Error in definition for '"+name+"': Missing parameter '"+c.luaTag+"'!");
			String val = b.getString(c.luaTag);
			if (LuaBlock.isErrorCode(val))
				throw new IllegalArgumentException("Error in definition for '"+name+"': Missing parameter '"+c.luaTag+"', found "+val+"!");
			options.put(c, val);
		}
	}

	public int getInt(ControlOptions c) {
		return Integer.parseInt(options.get(c));
	}

	public boolean getBoolean(ControlOptions c) {
		return Boolean.parseBoolean(options.get(c));
	}

	public float getFloat(ControlOptions c) {
		return Float.parseFloat(options.get(c));
	}

	public static enum ControlOptions {
		CAVES("Cave Density Multiplier", "density", 1F),
		RAVINES("Ravine Frequency Multiplier", "ravines", 1F),
		MINESHAFTS("Mineshafts Frequency Multiplier", "mineshafts", 1F),
		DUNGEONRATE("Dungeon Spawn Factor", "dungeons", 1F),
		STRONGHOLDS("Generate Strongholds", "gen_strongholds", true),
		DEEPLAVA("Fill Deep Caves with Lava", "lava_caves", true),
		DEEPWATER("Fill Deep Caves with Water", "water_caves", false),
		CAVENODESIZE("Cave Node Size Multiplier", "cave_size", 1F),
		BIGCAVENODE("Large Cave Node Chance", "large_cave_chance", 25F),
		LARGENODESIZE("Large Cave Node Size Factor", "large_cave_size", 1F),
		LARGENODEVAR("Large Cave Node Size Variance", "large_cave_variance", 6F),
		MINIMUMY("Cave Generation Y Min", "min_y", 0),
		MAXIMUMY("Cave Generation Y Max", "max_y", 256),
		VSTRETCH("Vertical Scale Factor", "vscale", 1F);

		public static final ControlOptions[] optionList = values();

		public final String name;
		public final String luaTag;
		public final Object defaultValue;

		private ControlOptions(String n, String tag, Object value) {
			name = n;
			luaTag = tag;
			defaultValue = value;
			lookups.put(tag, this);
		}

		private Object parseValue(String s) {
			if (defaultValue instanceof Integer)
				return Integer.parseInt(s);
			else if (defaultValue instanceof Float)
				return Float.parseFloat(s);
			else if (defaultValue instanceof Boolean)
				return Boolean.parseBoolean(s);
			else
				return s;
		}
	}
}
