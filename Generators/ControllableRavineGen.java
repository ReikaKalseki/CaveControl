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

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.gen.MapGenRavine;
import Reika.CaveControl.CaveControl;
import Reika.CaveControl.Registry.CaveOptions;
import Reika.CaveControl.Registry.ControlOptions;
import Reika.DragonAPI.Auxiliary.BiomeTypeList;

public class ControllableRavineGen extends MapGenRavine {

	@Override
	protected void func_151538_a(World world, int local_chunkX, int local_chunkZ, int chunkX, int chunkZ, Block[] columnData)
	{
		if (CaveControl.config.shouldGenerateRavines()) {
			float factor = this.getFactor(world, local_chunkX, local_chunkZ);
			if (factor > 0) {
				if (rand.nextInt(Math.max(1, (int)(50/factor))) == 0) {
					double ravineX = local_chunkX * 16 + rand.nextInt(16);
					double ravineY = rand.nextInt(rand.nextInt(40) + 8) + 20;
					double ravineZ = local_chunkZ * 16 + rand.nextInt(16);

					float f = rand.nextFloat() * (float)Math.PI * 2.0F;
					float f1 = (rand.nextFloat() - 0.5F) * 2.0F / 8.0F;
					float f2 = (rand.nextFloat() * 2.0F + rand.nextFloat()) * 2.0F;
					this.func_151540_a(rand.nextLong(), chunkX, chunkZ, columnData, ravineX, ravineY, ravineZ, f2, f, f1, 0, 0, 3.0D);
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
		return ControlOptions.RAVINES.getValue(BiomeTypeList.getEntry(biome));
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
