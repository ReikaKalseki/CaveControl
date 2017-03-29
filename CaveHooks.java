package Reika.CaveControl;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ChunkProviderFlat;
import Reika.CaveControl.CaveDefinition.ControlOptions;
import Reika.CaveControl.Registry.CaveOptions;
import Reika.DragonAPI.Libraries.World.ReikaChunkHelper;


public class CaveHooks {

	public static void provideFlatChunk(ChunkProviderFlat f, World world, int x, int z, Chunk c) {
		Block[] columnData = ReikaChunkHelper.getChunkAsColumnData(c);
		if (CaveOptions.FLATCAVES.getState()) {
			CaveControl.caveGen.func_151539_a(f, world, x, z, columnData);
		}
		if (CaveOptions.FLATRAVINES.getState()) {
			CaveControl.ravineGen.func_151539_a(f, world, x, z, columnData);
		}
		ReikaChunkHelper.writeBlockColumnToChunk(c, columnData);
	}

	public static int getDungeonGenAttempts(World world, int x, int z) {
		BiomeGenBase biome = world.getBiomeGenForCoords(x << 4, z << 4);
		return (int)(8*CaveLoader.instance.getDefinition(biome).getFloat(ControlOptions.DUNGEONRATE));
	}

	public static boolean shouldGenerateCaves(BiomeGenBase b) {
		return CaveLoader.instance.getDefinition(b).getFloat(ControlOptions.CAVES) > 0;
	}

	public static boolean shouldGenerateRavines(BiomeGenBase b) {
		return CaveLoader.instance.getDefinition(b).getFloat(ControlOptions.RAVINES) > 0;
	}

	public static boolean shouldGenerateMineshafts(BiomeGenBase b) {
		return CaveLoader.instance.getDefinition(b).getFloat(ControlOptions.MINESHAFTS) > 0;
	}

	public static boolean shouldGenerateStrongholds(BiomeGenBase b) {
		return CaveLoader.instance.getDefinition(b).getBoolean(ControlOptions.STRONGHOLDS);
	}

	public static boolean shouldGenerateDungeons(BiomeGenBase b) {
		return CaveLoader.instance.getDefinition(b).getFloat(ControlOptions.DUNGEONRATE) > 0;
	}

	public static boolean fillDeepCavesWithLava(BiomeGenBase biome) {
		return CaveLoader.instance.getDefinition(biome).getBoolean(ControlOptions.DEEPLAVA);
	}

	public static Block getBlockToFillDeepCaves(BiomeGenBase biome) {
		boolean water = CaveLoader.instance.getDefinition(biome).getBoolean(ControlOptions.DEEPWATER);
		return water ? Blocks.water : Blocks.air;

	}

}
