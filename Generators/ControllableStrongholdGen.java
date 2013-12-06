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
import Reika.CaveControl.Registry.ControllableBiomes;

public class ControllableStrongholdGen extends MapGenStronghold {

	@Override
	protected boolean canSpawnStructureAtCoords(int x, int z)
	{
		if (CaveOptions.GLOBAL.getState()) {
			return CaveControl.config.getGlobalBoolean(ControlOptions.STRONGHOLDS) ? super.canSpawnStructureAtCoords(x, z) : false;
		}
		else {
			World world = worldObj;
			BiomeGenBase biome = world.getBiomeGenForCoords(x*16, z*16);
			return CaveControl.config.getBoolean(ControllableBiomes.getEntry(biome), ControlOptions.STRONGHOLDS) ? super.canSpawnStructureAtCoords(x, z) : false;
		}
	}
}
