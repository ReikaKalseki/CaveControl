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
import java.util.Random;

import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.BiomeGenOcean;
import net.minecraft.world.gen.structure.MapGenStronghold;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import Reika.CaveControl.CaveHooks;
import Reika.CaveControl.Registry.CaveOptions;

public class ControllableStrongholdGen extends MapGenStronghold {

	private static final Random rand = new Random();

	public ControllableStrongholdGen() {
		super(getMap());
	}

	private static Map getMap() {
		Map map = new HashMap();
		map.put("distance", String.valueOf(32D*CaveOptions.STRONGHOLDDIST.getFloat()));
		int n = CaveOptions.STRONGHOLDCOUNT.getValue();
		map.put("count", String.valueOf(n));
		//map.put("spread", String.valueOf(n));
		return map;
	}

	@Override
	protected boolean canSpawnStructureAtCoords(int chunkX, int chunkZ) {
		World world = worldObj;
		BiomeGenBase biome = world.getBiomeGenForCoords(chunkX*16, chunkZ*16);
		if (CaveOptions.NOOCEANSTRONGHOLDS.getState() && (biome instanceof BiomeGenOcean || BiomeDictionary.isBiomeOfType(biome, Type.OCEAN)))
			return false;
		return CaveHooks.shouldGenerateStrongholds(biome) ? super.canSpawnStructureAtCoords(chunkX, chunkZ) : false;
	}
}
