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
import Reika.DragonAPI.Interfaces.Configuration.ConfigList;
import Reika.DragonAPI.Interfaces.Registry.IDRegistry;

public class CaveConfig extends ControlledConfig {

	private HashMap<BiomeTypeList, HashMap<ControlOptions, DataElement>> options = new HashMap();
	private DataElement[] global = new DataElement[ControlOptions.optionList.length];

	public CaveConfig(DragonAPIMod mod, ConfigList[] option, IDRegistry[] id) {
		super(mod, option, id);

		for (int i = 0; i < BiomeTypeList.biomeList.length; i++) {
			BiomeTypeList biome = BiomeTypeList.biomeList[i];
			String biomename = biome.displayName;
			for (int j = 0; j < ControlOptions.optionList.length; j++) {
				ControlOptions o = ControlOptions.optionList[j];
				String optionname = o.displayName;
				if (o.isBoolean)
					this.addDataEntry(biome, o, this.registerAdditionalOption(biomename, optionname, o.defaultState));
				else
					this.addDataEntry(biome, o, this.registerAdditionalOption(biomename, optionname, o.defaultValue));
			}
		}

		for (int j = 0; j < ControlOptions.optionList.length; j++) {
			ControlOptions o = ControlOptions.optionList[j];
			String optionname = o.displayName;
			if (o.isBoolean)
				global[o.ordinal()] = this.registerAdditionalOption("$Global Control", optionname, o.defaultState);
			else
				global[o.ordinal()] = this.registerAdditionalOption("$Global Control", optionname, o.defaultValue);
		}
	}

	private void addDataEntry(BiomeTypeList biome, ControlOptions option, DataElement value) {
		HashMap<ControlOptions, DataElement> map = options.get(biome);
		if (map == null) {
			map = new HashMap();
		}
		map.put(option, value);
		options.put(biome, map);
	}

	public float getFloat(BiomeTypeList biome, ControlOptions type) {
		try {
			return ((Float)options.get(biome).get(type).getData()).floatValue();
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
			return (Boolean)options.get(biome).get(type).getData();
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
			return ((Float)global[type.ordinal()].getData()).floatValue();
		}
		catch (ClassCastException e) {
			throw new RegistrationException(CaveControl.instance, "Invalid data for global option "+type.displayName+"!");
		}
	}

	public boolean getGlobalBoolean(ControlOptions type) {
		try {
			return (Boolean)global[type.ordinal()].getData();
		}
		catch (ClassCastException e) {
			throw new RegistrationException(CaveControl.instance, "Invalid data for global option "+type.displayName+"!");
		}
	}

	public boolean shouldGenerateCaves() {
		return !CaveOptions.GLOBAL.getState() || (Float)global[ControlOptions.CAVES.ordinal()].getData() > 0;
	}

	public boolean shouldGenerateRavines() {
		return !CaveOptions.GLOBAL.getState() || (Float)global[ControlOptions.RAVINES.ordinal()].getData() > 0;
	}
}
