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
import net.minecraft.world.gen.MapGenCaves;
import Reika.CaveControl.CaveControl;
import Reika.CaveControl.Registry.CaveOptions;
import Reika.CaveControl.Registry.ControlOptions;
import Reika.CaveControl.Registry.ControllableBiomes;

public class ControllableCaveGen extends MapGenCaves {

	@Override
	protected void recursiveGenerate(World world, int par2, int par3, int par4, int par5, byte[] par6ArrayOfByte)
	{
		if (CaveControl.config.shouldGenerateCaves()) {
			float factor = this.getFactor(world, par2, par4);
			if (factor > 0) {
				int density = (int)(15/factor);
				int i1 = rand.nextInt(rand.nextInt(rand.nextInt(40) + 1) + 1);

				if (rand.nextInt(Math.max(density, 1)) != 0)
				{
					i1 = 0;
				}

				for (int j1 = 0; j1 < i1; ++j1)
				{
					//ReikaJavaLibrary.pConsole((par2*16)+", "+par3+", "+(par4*16)+":"+par5);//+": Chunk Data:"+Arrays.toString(par6ArrayOfByte));
					double d0 = par2 * 16 + rand.nextInt(16);
					double d1 = rand.nextInt(rand.nextInt(120) + 8);
					double d2 = par3 * 16 + rand.nextInt(16);
					int k1 = 1;

					if (rand.nextInt(density >= 3 ? 2 : 4) == 0)
					{
						this.generateLargeCaveNode(rand.nextLong(), par4, par5, par6ArrayOfByte, d0, d1, d2);
						k1 += rand.nextInt(4);
					}

					for (int l1 = 0; l1 < k1; ++l1)
					{
						float f = rand.nextFloat() * (float)Math.PI * 2.0F;
						float f1 = (rand.nextFloat() - 0.5F) * 2.0F / 8.0F;
						float f2 = rand.nextFloat() * 2.0F + rand.nextFloat();

						if (rand.nextInt(10) == 0)
						{
							f2 *= rand.nextFloat() * rand.nextFloat() * 3.0F + 1.0F;
						}

						this.generateCaveNode(rand.nextLong(), par4, par5, par6ArrayOfByte, d0, d1, d2, f2, f, f1, 0, 0, 1.0D);
					}
				}
			}
		}
	}

	private float getFactor(World world, int chunkX, int chunkZ) {
		if (CaveOptions.GLOBAL.getState()) {
			return CaveControl.config.getGlobalFloat(ControlOptions.CAVES);
		}
		int x = chunkX*16;
		int z = chunkZ*16;
		BiomeGenBase biome = world.getBiomeGenForCoords(x, z);
		return ControlOptions.CAVES.getValue(ControllableBiomes.getEntry(biome));
	}
}
