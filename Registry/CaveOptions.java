/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.CaveControl.Registry;

import Reika.CaveControl.CaveControl;
import Reika.DragonAPI.Interfaces.Configuration.BooleanConfig;
import Reika.DragonAPI.Interfaces.Configuration.DecimalConfig;
import Reika.DragonAPI.Interfaces.Configuration.IntegerConfig;

public enum CaveOptions implements BooleanConfig, DecimalConfig, IntegerConfig {

	FLATCAVES("Generate Caves in Superflat", false),
	FLATRAVINES("Generate Ravines in Superflat", false),
	STRONGHOLDDIST("Stronghold Distance Factor", 1F),
	STRONGHOLDCOUNT("Stronghold Count", 3);

	private String label;
	private boolean defaultState;
	private int defaultValue;
	private float defaultFloat;
	private Class type;

	public static final CaveOptions[] optionList = CaveOptions.values();

	private CaveOptions(String l, boolean d) {
		label = l;
		defaultState = d;
		type = boolean.class;
	}

	private CaveOptions(String l, int d) {
		label = l;
		defaultValue = d;
		type = int.class;
	}

	private CaveOptions(String l, float d) {
		label = l;
		defaultFloat = d;
		type = float.class;
	}

	public boolean isBoolean() {
		return type == boolean.class;
	}

	public boolean isNumeric() {
		return type == int.class;
	}

	public boolean isDecimal() {
		return type == float.class;
	}

	public Class getPropertyType() {
		return type;
	}

	public String getLabel() {
		return label;
	}

	public boolean getState() {
		return (Boolean)CaveControl.config.getControl(this.ordinal());
	}

	public int getValue() {
		return (Integer)CaveControl.config.getControl(this.ordinal());
	}

	public float getFloat() {
		return (Float)CaveControl.config.getControl(this.ordinal());
	}

	public boolean isDummiedOut() {
		return type == null;
	}

	@Override
	public boolean getDefaultState() {
		return defaultState;
	}

	public int getDefaultValue() {
		return defaultValue;
	}

	public float getDefaultFloat() {
		return defaultFloat;
	}

	@Override
	public boolean isEnforcingDefaults() {
		return false;
	}

	@Override
	public boolean shouldLoad() {
		return true;
	}

}
