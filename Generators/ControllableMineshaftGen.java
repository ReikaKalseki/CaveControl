/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.CaveControl.Generators;

import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.gen.structure.MapGenMineshaft;
import Reika.CaveControl.CaveControl;
import Reika.CaveControl.Registry.CaveOptions;
import Reika.CaveControl.Registry.ControlOptions;
import Reika.DragonAPI.Auxiliary.BiomeTypeList;

public class ControllableMineshaftGen extends MapGenMineshaft {

	@Override
	protected boolean canSpawnStructureAtCoords(int chunkX, int chunkZ)
	{
		if (CaveOptions.GLOBAL.getState()) {
			return CaveControl.config.getGlobalBoolean(ControlOptions.MINESHAFTS) ? super.canSpawnStructureAtCoords(chunkX, chunkZ) : false;
		}
		else {
			World world = worldObj;
			BiomeGenBase biome = world.getBiomeGenForCoords(chunkX*16, chunkZ*16);
			return CaveControl.config.getBoolean(BiomeTypeList.getEntry(biome), ControlOptions.MINESHAFTS) ? super.canSpawnStructureAtCoords(chunkX, chunkZ) : false;
		}
	}
}
