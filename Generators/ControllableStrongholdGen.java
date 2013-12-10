/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.CaveControl.Generators;

import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.gen.structure.MapGenStronghold;
import Reika.CaveControl.CaveControl;
import Reika.CaveControl.Registry.CaveOptions;
import Reika.CaveControl.Registry.ControlOptions;
import Reika.DragonAPI.Auxiliary.BiomeTypeList;

public class ControllableStrongholdGen extends MapGenStronghold {

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
