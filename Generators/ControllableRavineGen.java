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
import net.minecraft.world.gen.MapGenRavine;
import Reika.CaveControl.CaveControl;
import Reika.CaveControl.Registry.CaveOptions;
import Reika.CaveControl.Registry.ControlOptions;
import Reika.CaveControl.Registry.ControllableBiomes;

public class ControllableRavineGen extends MapGenRavine {

	@Override
	protected void recursiveGenerate(World world, int par2, int par3, int par4, int par5, byte[] par6ArrayOfByte)
	{
		if (CaveControl.config.shouldGenerateRavines()) {
			float factor = this.getFactor(world, par2, par4);
			if (factor > 0) {
				if (rand.nextInt(Math.max(1, (int)(50/factor))) == 0)
				{
					double d0 = par2 * 16 + rand.nextInt(16);
					double d1 = rand.nextInt(rand.nextInt(40) + 8) + 20;
					double d2 = par3 * 16 + rand.nextInt(16);
					byte b0 = 1;

					for (int i1 = 0; i1 < b0; ++i1)
					{
						float f = rand.nextFloat() * (float)Math.PI * 2.0F;
						float f1 = (rand.nextFloat() - 0.5F) * 2.0F / 8.0F;
						float f2 = (rand.nextFloat() * 2.0F + rand.nextFloat()) * 2.0F;
						this.generateRavine(rand.nextLong(), par4, par5, par6ArrayOfByte, d0, d1, d2, f2, f, f1, 0, 0, 3.0D);
					}
				}
			}
		}
	}

	private float getFactor(World world, int chunkX, int chunkZ) {
		if (CaveOptions.GLOBAL.getState()) {
			return CaveControl.config.getGlobalFloat(ControlOptions.RAVINES);
		}
		int x = chunkX*16;
		int z = chunkZ*16;
		BiomeGenBase biome = world.getBiomeGenForCoords(x, z);
		return ControlOptions.RAVINES.getValue(ControllableBiomes.getEntry(biome));
	}
}
