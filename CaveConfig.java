/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.CaveControl;

import java.util.HashMap;

import Reika.CaveControl.Registry.CaveOptions;
import Reika.CaveControl.Registry.ControlOptions;
import Reika.DragonAPI.Auxiliary.BiomeTypeList;
import Reika.DragonAPI.Base.DragonAPIMod;
import Reika.DragonAPI.Exception.RegistrationException;
import Reika.DragonAPI.Instantiable.IO.ControlledConfig;
import Reika.DragonAPI.Interfaces.ConfigList;
import Reika.DragonAPI.Interfaces.IDRegistry;

public class CaveConfig extends ControlledConfig {

	public CaveConfig(DragonAPIMod mod, ConfigList[] option, IDRegistry[] id, int cfg) {
		super(mod, option, id, cfg);
	}

	private HashMap<BiomeTypeList, HashMap<ControlOptions, Object>> options = new HashMap();
	private Object[] global = new Object[ControlOptions.optionList.length];

	@Override
	protected void loadAdditionalData() {
		for (int i = 0; i < BiomeTypeList.biomeList.length; i++) {
			BiomeTypeList biome = BiomeTypeList.biomeList[i];
			String biomename = biome.displayName;
			for (int j = 0; j < ControlOptions.optionList.length; j++) {
				ControlOptions o = ControlOptions.optionList[j];
				String optionname = o.displayName;
				if (o.isBoolean)
					this.addDataEntry(biome, o, config.get(biomename, optionname, o.defaultState).getBoolean(o.defaultState));
				else
					this.addDataEntry(biome, o, config.get(biomename, optionname, o.defaultValue).getDouble(o.defaultValue));
			}
		}

		for (int j = 0; j < ControlOptions.optionList.length; j++) {
			ControlOptions o = ControlOptions.optionList[j];
			String optionname = o.displayName;
			if (o.isBoolean)
				global[o.ordinal()] = (config.get("$Global Control", optionname, o.defaultState).getBoolean(o.defaultState));
			else
				global[o.ordinal()] = (config.get("$Global Control", optionname, o.defaultValue).getDouble(o.defaultValue));
		}
	}

	private void addDataEntry(BiomeTypeList biome, ControlOptions option, Object value) {
		HashMap<ControlOptions, Object> map = options.get(biome);
		if (map == null) {
			map = new HashMap();
		}
		map.put(option, value);
		options.put(biome, map);
	}

	public float getFloat(BiomeTypeList biome, ControlOptions type) {
		try {
			return ((Double)options.get(biome).get(type)).floatValue();
		}
		catch (NullPointerException e) {
			//throw new RegistrationException(CaveControl.instance, "No data loaded for biome type "+biome.displayName+"!");
			return 1;
		}
		catch (ClassCastException e) {
			throw new RegistrationException(CaveControl.instance, "Invalid data for biome type "+biome.displayName+" and option "+type.displayName+"!");
		}
	}

	public boolean getBoolean(BiomeTypeList biome, ControlOptions type) {
		try {
			return (Boolean)options.get(biome).get(type);
		}
		catch (NullPointerException e) {
			//throw new RegistrationException(CaveControl.instance, "No data loaded for biome type "+biome.displayName+"!");
			return true;
		}
		catch (ClassCastException e) {
			throw new RegistrationException(CaveControl.instance, "Invalid data for biome type "+biome.displayName+" and option "+type.displayName+"!");
		}
	}

	public float getGlobalFloat(ControlOptions type) {
		try {
			return ((Double)global[type.ordinal()]).floatValue();
		}
		catch (ClassCastException e) {
			throw new RegistrationException(CaveControl.instance, "Invalid data for global option "+type.displayName+"!");
		}
	}

	public boolean getGlobalBoolean(ControlOptions type) {
		try {
			return (Boolean)global[type.ordinal()];
		}
		catch (ClassCastException e) {
			throw new RegistrationException(CaveControl.instance, "Invalid data for global option "+type.displayName+"!");
		}
	}

	public boolean shouldGenerateCaves() {
		return !CaveOptions.GLOBAL.getState() || (Double)global[ControlOptions.CAVES.ordinal()] > 0;
	}

	public boolean shouldGenerateRavines() {
		return !CaveOptions.GLOBAL.getState() || (Double)global[ControlOptions.RAVINES.ordinal()] > 0;
	}
}
