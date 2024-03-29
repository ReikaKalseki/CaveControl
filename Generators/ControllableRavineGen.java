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
import net.minecraft.world.gen.MapGenRavine;

import Reika.CaveControl.CaveDefinition.ControlOptions;
import Reika.CaveControl.CaveHooks;
import Reika.CaveControl.CaveLoader;

public class ControllableRavineGen extends MapGenRavine {

	@Override
	protected void func_151538_a(World world, int local_chunkX, int local_chunkZ, int chunkX, int chunkZ, Block[] columnData) {
		if (CaveHooks.shouldGenerateRavines(world, chunkX*16, chunkZ*16)) {
			float factor = this.getFactor(world, local_chunkX, local_chunkZ);
			if (factor > 0) {
				if (rand.nextInt(Math.max(1, (int)(50/factor))) == 0) {
					int ravineX = local_chunkX * 16 + rand.nextInt(16);
					int ravineY = rand.nextInt(rand.nextInt(40) + 8) + 20;
					int ravineZ = local_chunkZ * 16 + rand.nextInt(16);

					if (this.doGenAt(world, ravineX, ravineY, ravineZ)) {
						float f = rand.nextFloat() * (float)Math.PI * 2.0F;
						float f1 = (rand.nextFloat() - 0.5F) * 2.0F / 8.0F;
						float f2 = (rand.nextFloat() * 2.0F + rand.nextFloat()) * 2.0F;
						this.generateRavine(rand.nextLong(), chunkX, chunkZ, columnData, ravineX, ravineY, ravineZ, f2, f, f1, 0, 0, 3.0D);
					}
				}
			}
		}
	}

	private boolean doGenAt(World world, int x, int y, int z) {
		int min = (int)this.getConfig(world, x >> 4, z >> 4, ControlOptions.MINIMUMY);
		int max = (int)this.getConfig(world, x >> 4, z >> 4, ControlOptions.MAXIMUMY);
		return y >= min && y <= max;
	}

	private float getConfig(World world, int chunkX, int chunkZ, ControlOptions c) {
		return CaveLoader.instance.getDefinition(world, chunkX << 4, chunkZ << 4).getFloat(c);
	}

	protected void generateRavine(long seed, int cx, int cz, Block[] data, double rx, double ry, double rz, float f2, float f, float f1, int i1, int i2, double d1) {
		super.func_151540_a(seed, cx, cz, data, rx, ry, rz, f2, f, f1, i1, i2, d1);
	}

	private float getFactor(World world, int chunkX, int chunkZ) {
		return CaveLoader.instance.getDefinition(world, chunkX*16, chunkZ*16).getFloat(ControlOptions.RAVINES);
	}

	@Override
	protected void digBlock(Block[] data, int index, int x, int y, int z, int chunkX, int chunkZ, boolean foundTop)
	{
		super.digBlock(data, index, x, y, z, chunkX, chunkZ, foundTop);
		int dx = x + chunkX * 16;
		int dz = z + chunkZ * 16;
		//Edit data[index] to edit the block being written into by a cave; data[0] is the bottom bedrock layer
		Block blockID = data[index];

		if (!CaveHooks.fillDeepCavesWithLava(worldObj, dx, dz)) {
			if (blockID == Blocks.flowing_lava || blockID == Blocks.lava) {
				Block id = CaveHooks.getBlockToFillDeepCaves(worldObj, dx, dz);
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
		if (CaveHooks.getBlockToFillDeepCaves(worldObj, x + chunkX * 16, z + chunkZ * 16) == Blocks.water)
			return false;
		return data[index] == Blocks.flowing_water || data[index] == Blocks.water;
	}
}
