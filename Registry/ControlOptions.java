/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.CaveControl.Registry;

import Reika.CaveControl.CaveControl;
import Reika.DragonAPI.Auxiliary.BiomeTypeList;

public enum ControlOptions {

	CAVES("Cave Density Multiplier", 1F),
	RAVINES("Ravine Frequency Multiplier", 1F),
	MINESHAFTS("Generate Abandoned Mineshafts", true),
	DUNGEONS("Generate Dungeons", true),
	STRONGHOLDS("Generate Strongholds", true);

	public final boolean defaultState;
	public final float defaultValue;
	public final boolean isBoolean;
	public final String displayName;

	public static final ControlOptions[] optionList = values();

	private ControlOptions(String n, boolean flag) {
		defaultState = flag;
		defaultValue = 1;
		isBoolean = true;
		displayName = n;
	}

	private ControlOptions(String n, float val) {
		defaultValue = val;
		defaultState = true;
		isBoolean = false;
		displayName = n;
	}

	public float getValue(BiomeTypeList biome) {
		if (biome == null) {
			CaveControl.logger.debug(biome+" has no data!");
			return CaveControl.config.getGlobalFloat(this);
		}
		return CaveControl.config.getFloat(biome, this);
	}

	public boolean getFlag(BiomeTypeList biome) {
		if (biome == null) {
			CaveControl.logger.debug(biome+" has no data!");
			return CaveControl.config.getGlobalBoolean(this);
		}
		return CaveControl.config.getBoolean(biome, this);
	}
}
