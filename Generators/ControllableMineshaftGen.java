/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.CaveControl.Generators;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.world.World;
import net.minecraft.world.gen.structure.MapGenMineshaft;
import net.minecraftforge.event.terraingen.InitMapGenEvent.EventType;

import Reika.CaveControl.CaveDefinition.ControlOptions;
import Reika.CaveControl.CaveHooks;
import Reika.CaveControl.CaveLoader;

public class ControllableMineshaftGen extends MapGenMineshaft {

	private static final double BASE_FACTOR = 0.004;

	public ControllableMineshaftGen() {
		super(getMap());
	}

	private static Map getMap() {
		Map map = new HashMap();
		map.put("chance", "1");
		return map;
	}

	@Override
	protected boolean canSpawnStructureAtCoords(int chunkX, int chunkZ) {
		if (!CaveHooks.canGenInWorld(worldObj, EventType.MINESHAFT))
			return false;
		return rand.nextDouble() < this.getConfig(worldObj, chunkX, chunkZ, ControlOptions.MINESHAFTS)*BASE_FACTOR ? super.canSpawnStructureAtCoords(chunkX, chunkZ) : false;
	}

	private float getConfig(World world, int chunkX, int chunkZ, ControlOptions c) {
		return CaveLoader.instance.getDefinition(world, chunkX << 4, chunkZ << 4).getFloat(c);
	}
}
