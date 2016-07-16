/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.CaveControl.Generators;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.gen.structure.MapGenStronghold;
import Reika.CaveControl.CaveControl;
import Reika.CaveControl.Registry.CaveOptions;
import Reika.CaveControl.Registry.ControlOptions;
import Reika.DragonAPI.Auxiliary.BiomeTypeList;

public class ControllableStrongholdGen extends MapGenStronghold {

	private static final Random rand = new Random();
	private static final boolean global = CaveOptions.GLOBAL.getState();

	public ControllableStrongholdGen() {
		super(getMap());
	}

	private static Map getMap() {
		Map map = new HashMap();
		map.put("distance", String.valueOf(32D*CaveControl.config.getGlobalFloat(ControlOptions.STRONGHOLDDIST)));
		int n = (int)CaveControl.config.getGlobalFloat(ControlOptions.STRONGHOLDCOUNT);
		map.put("count", String.valueOf(n));
		map.put("spread", String.valueOf(n));
		return map;
	}

	@Override
	protected boolean canSpawnStructureAtCoords(int chunkX, int chunkZ)
	{
		if (CaveOptions.GLOBAL.getState()) {
			return CaveControl.config.getGlobalBoolean(ControlOptions.STRONGHOLDS) ? super.canSpawnStructureAtCoords(chunkX, chunkZ) : false;
		}
		else {
			World world = worldObj;
			BiomeGenBase biome = world.getBiomeGenForCoords(chunkX*16, chunkZ*16);
			return CaveControl.config.getBoolean(BiomeTypeList.getEntry(biome), ControlOptions.STRONGHOLDS) ? super.canSpawnStructureAtCoords(chunkX, chunkZ) : false;
		}
	}
}
