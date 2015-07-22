/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.CaveControl.Registry;

import Reika.CaveControl.CaveControl;
import Reika.DragonAPI.Interfaces.Configuration.BooleanConfig;

public enum CaveOptions implements BooleanConfig {

	GLOBAL("Use Global Controls", true);

	private String label;
	private boolean defaultState;
	private Class type;

	public static final CaveOptions[] optionList = CaveOptions.values();

	private CaveOptions(String l, boolean d) {
		label = l;
		defaultState = d;
		type = boolean.class;
	}

	public boolean isBoolean() {
		return type == boolean.class;
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

	public boolean isDummiedOut() {
		return type == null;
	}

	@Override
	public boolean getDefaultState() {
		return defaultState;
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
