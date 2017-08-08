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

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.gen.MapGenRavine;
import Reika.CaveControl.CaveDefinition.ControlOptions;
import Reika.CaveControl.CaveHooks;
import Reika.CaveControl.CaveLoader;

public class ControllableRavineGen extends MapGenRavine {

	@Override
	protected void func_151538_a(World world, int local_chunkX, int local_chunkZ, int chunkX, int chunkZ, Block[] columnData) {
		BiomeGenBase biome = worldObj.getBiomeGenForCoords(chunkX*16, chunkZ*16);
		if (CaveHooks.shouldGenerateRavines(biome)) {
			float factor = this.getFactor(world, local_chunkX, local_chunkZ);
			if (factor > 0) {
				if (rand.nextInt(Math.max(1, (int)(50/factor))) == 0) {
					double ravineX = local_chunkX * 16 + rand.nextInt(16);
					double ravineY = rand.nextInt(rand.nextInt(40) + 8) + 20;
					double ravineZ = local_chunkZ * 16 + rand.nextInt(16);

					float f = rand.nextFloat() * (float)Math.PI * 2.0F;
					float f1 = (rand.nextFloat() - 0.5F) * 2.0F / 8.0F;
					float f2 = (rand.nextFloat() * 2.0F + rand.nextFloat()) * 2.0F;
					this.generateRavine(rand.nextLong(), chunkX, chunkZ, columnData, ravineX, ravineY, ravineZ, f2, f, f1, 0, 0, 3.0D);
				}
			}
		}
	}

	protected void generateRavine(long seed, int cx, int cz, Block[] data, double rx, double ry, double rz, float f2, float f, float f1, int i1, int i2, double d1)
	{
		super.func_151540_a(seed, cx, cz, data, rx, ry, rz, f2, f, f1, i1, i2, d1);
	}

	private float getFactor(World world, int chunkX, int chunkZ) {
		int x = chunkX*16;
		int z = chunkZ*16;
		BiomeGenBase biome = world.getBiomeGenForCoords(x, z);
		return CaveLoader.instance.getDefinition(biome).getFloat(ControlOptions.RAVINES);
	}

	@Override
	protected void digBlock(Block[] data, int index, int x, int y, int z, int chunkX, int chunkZ, boolean foundTop)
	{
		super.digBlock(data, index, x, y, z, chunkX, chunkZ, foundTop);
		BiomeGenBase biome = worldObj.getBiomeGenForCoords(x + chunkX * 16, z + chunkZ * 16);

		//Edit data[index] to edit the block being written into by a cave; data[0] is the bottom bedrock layer
		Block blockID = data[index];

		if (!CaveHooks.fillDeepCavesWithLava(biome)) {
			if (blockID == Blocks.flowing_lava || blockID == Blocks.lava) {
				Block id = CaveHooks.getBlockToFillDeepCaves(biome);
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
		if (CaveHooks.getBlockToFillDeepCaves(biome) == Blocks.water)
			return false;
		return data[index] == Blocks.flowing_water || data[index] == Blocks.water;
	}
}
