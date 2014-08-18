/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.CaveControl.Generators;

import Reika.CaveControl.CaveControl;
import Reika.CaveControl.Registry.CaveOptions;
import Reika.CaveControl.Registry.ControlOptions;
import Reika.DragonAPI.Auxiliary.BiomeTypeList;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.gen.MapGenCaves;

public class ControllableCaveGen extends MapGenCaves {

	@Override
	protected void func_151538_a(World world, int local_chunkX, int local_chunkZ, int chunkX, int chunkZ, Block[] columnData)
	{
		if (CaveControl.config.shouldGenerateCaves()) {
			float factor = this.getFactor(world, local_chunkX, local_chunkZ);
			if (factor > 0) {
				int density = (int)(15/factor); //15 is default

				int caveCount = rand.nextInt(rand.nextInt(rand.nextInt(40) + 1) + 1);

				if (rand.nextInt(Math.max(density, 1)) != 0)
					caveCount = 0;

				for (int i = 0; i < caveCount; i++) {
					//ReikaJavaLibrary.pConsole((par2*16)+", "+par3+", "+(par4*16)+":"+par5);//+": Chunk Data:"+Arrays.toString(par6ArrayOfByte));
					double caveX = local_chunkX * 16 + rand.nextInt(16);
					double caveY = rand.nextInt(rand.nextInt(120) + 8);
					double caveZ = local_chunkZ * 16 + rand.nextInt(16);

					if (this.doGenAt(caveY)) {
						int nodeCount = 1;

						if (rand.nextInt(Math.max(1, (int)(4/Math.sqrt(factor)))) == 0) {
							this.func_151542_a(rand.nextLong(), chunkX, chunkZ, columnData, caveX, caveY, caveZ);
							nodeCount += rand.nextInt(4);
						}

						for (int k = 0; k < nodeCount; k++) {
							float f = rand.nextFloat() * (float)Math.PI * 2.0F;
							float f1 = (rand.nextFloat() - 0.5F) * 2.0F / 8.0F;
							float f2 = rand.nextFloat() * 2.0F + rand.nextFloat();

							if (rand.nextInt(10) == 0)
								f2 *= rand.nextFloat() * rand.nextFloat() * 3.0F + 1.0F;

							this.func_151541_a(rand.nextLong(), chunkX, chunkZ, columnData, caveX, caveY, caveZ, f2, f, f1, 0, 0, 1.0D);
						}
					}
				}
			}
		}
	}

	private boolean doGenAt(double y) {
		return true;
	}

	private float getFactor(World world, int chunkX, int chunkZ) {
		if (CaveOptions.GLOBAL.getState()) {
			return CaveControl.config.getGlobalFloat(ControlOptions.CAVES);
		}
		int x = chunkX*16;
		int z = chunkZ*16;
		BiomeGenBase biome = world.getBiomeGenForCoords(x, z);
		return ControlOptions.CAVES.getValue(BiomeTypeList.getEntry(biome));
	}

	@Override
	protected void digBlock(Block[] data, int index, int x, int y, int z, int chunkX, int chunkZ, boolean foundTop)
	{
		super.digBlock(data, index, x, y, z, chunkX, chunkZ, foundTop);
		BiomeGenBase biome = worldObj.getBiomeGenForCoords(x + chunkX * 16, z + chunkZ * 16);

		//Edit data[index] to edit the block being written into by a cave; data[0] is the bottom bedrock layer
		Block blockID = data[index];

		if (!CaveControl.fillDeepCavesWithLava(biome)) {
			if (blockID == Blocks.flowing_lava || blockID == Blocks.lava) {
				Block id = CaveControl.getBlockToFillDeepCaves(biome);
				data[index] = id;
				if (id == Blocks.air) { //Smooth cave floors to y=4 with stone (so to avoid jagged bedrock floors)
					for (int i = 1; i < 4; i++) { //not y=0 since that is always solid bedrock
						Block inPlace = data[i];
						if (inPlace == Blocks.air)
							data[i] = Blocks.stone;
					}
				}
				else {

				}
			}
		}
	}

	@Override
	protected boolean isOceanBlock(Block[] data, int index, int x, int y, int z, int chunkX, int chunkZ)
	{
		BiomeGenBase biome = worldObj.getBiomeGenForCoords(x + chunkX * 16, z + chunkZ * 16);
		if (ControlOptions.DEEPWATER.getBoolean(BiomeTypeList.getEntry(biome)))
			return false;
		return data[index] == Blocks.flowing_water || data[index] == Blocks.water;
	}
}
