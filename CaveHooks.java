/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.CaveControl;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ChunkProviderFlat;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.terraingen.InitMapGenEvent.EventType;

import Reika.CaveControl.CaveDefinition.ControlOptions;
import Reika.CaveControl.API.GenerationCheckEvent;
import Reika.CaveControl.Generators.ControllableCaveGen;
import Reika.CaveControl.Generators.ControllableRavineGen;
import Reika.CaveControl.Registry.CaveOptions;
import Reika.DragonAPI.Libraries.World.ReikaChunkHelper;


public class CaveHooks {

	private static long currentSeed = -1;
	private static ControllableCaveGen caveGen;
	private static ControllableRavineGen ravineGen;

	/*
	public static void fillWithBlocks(StructureComponent struct, World world, StructureBoundingBox box, int minX, int minY, int minZ, int maxX, int maxY, int maxZ, Block place, Block repl, boolean skipAir)
	{
		//would soe
		//struct.fillWithBlocks(world, box, minX, minY, minZ, maxX, maxY, maxZ, place, repl, shouldSkipAir(struct, skipAir));
	}

	public static void fillWithMetadataBlocks(StructureComponent struct, World world, StructureBoundingBox box, int minX, int minY, int minZ, int maxX, int maxY, int maxZ, Block place, int placeMeta, Block repl, int replMeta, boolean skipAir)
	{
		//struct.fillWithMetadataBlocks(world, box, minX, minY, minZ, maxX, maxY, maxZ, place, placeMeta, repl, replMeta, shouldSkipAir(struct, skipAir));
	}

	public static void fillWithRandomizedBlocks(StructureComponent struct, World world, StructureBoundingBox box, int minX, int minY, int minZ, int maxX, int maxY, int maxZ, boolean skipAir, Random rand, StructureComponent.BlockSelector blocks)
	{
		//struct.fillWithRandomizedBlocks(world, box, minX, minY, minZ, maxX, maxY, maxZ, shouldSkipAir(struct, skipAir), rand, blocks);
	}

	public static void randomlyFillWithBlocks(StructureComponent struct, World world, StructureBoundingBox box, Random rand, float chance, int minX, int minY, int minZ, int maxX, int maxY, int maxZ, Block place, Block repl, boolean skipAir)
	{
		//struct.randomlyFillWithBlocks(world, box, rand, chance, minX, minY, minZ, maxX, maxY, maxZ, place, repl, shouldSkipAir(struct, skipAir));
	}

	public static boolean shouldSkipAir(StructureComponent struct, boolean skipAir) {
		return struct instanceof Stronghold ? skipAir && !CaveOptions.SOLIDSTRONGHOLD.getState() : skipAir;
	}
	 */
	public static void provideFlatChunk(ChunkProviderFlat f, World world, int x, int z, Chunk c) {
		Block[] columnData = ReikaChunkHelper.getChunkAsColumnData(c);
		if (currentSeed != world.getSeed() || caveGen == null) {
			currentSeed = world.getSeed();
			caveGen = new ControllableCaveGen();
			ravineGen = new ControllableRavineGen();
		}
		if (CaveOptions.FLATCAVES.getState()) {
			caveGen.func_151539_a(f, world, x, z, columnData);
		}
		if (CaveOptions.FLATRAVINES.getState()) {
			ravineGen.func_151539_a(f, world, x, z, columnData);
		}
		ReikaChunkHelper.writeBlockColumnToChunk(c, columnData);
	}

	public static int getDungeonGenAttempts(World world, int cx, int cz) {
		return (int)(8*CaveLoader.instance.getDefinition(world, cx << 4, cz << 4).getFloat(ControlOptions.DUNGEONRATE));
	}

	public static boolean shouldGenerateCaves(World world, int x, int z) {
		if (!canGenInWorld(world, EventType.CAVE))
			return false;
		return CaveLoader.instance.getDefinition(world, x, z).getFloat(ControlOptions.CAVES) > 0;
	}

	public static boolean shouldGenerateRavines(World world, int x, int z) {
		if (!canGenInWorld(world, EventType.RAVINE))
			return false;
		return CaveLoader.instance.getDefinition(world, x, z).getFloat(ControlOptions.RAVINES) > 0;
	}

	public static boolean shouldGenerateMineshafts(World world, int x, int z) {
		if (!canGenInWorld(world, EventType.MINESHAFT))
			return false;
		return CaveLoader.instance.getDefinition(world, x, z).getFloat(ControlOptions.MINESHAFTS) > 0;
	}

	public static boolean shouldGenerateStrongholds(World world, int x, int z) {
		if (!canGenInWorld(world, EventType.STRONGHOLD))
			return false;
		return CaveLoader.instance.getDefinition(world, x, z).getBoolean(ControlOptions.STRONGHOLDS);
	}

	public static boolean shouldGenerateDungeons(World world, int x, int z) {
		if (!canGenInWorld(world, EventType.SCATTERED_FEATURE))
			return false;
		return CaveLoader.instance.getDefinition(world, x, z).getFloat(ControlOptions.DUNGEONRATE) > 0;
	}

	public static boolean fillDeepCavesWithLava(World world, int x, int z) {
		return CaveLoader.instance.getDefinition(world, x, z).getBoolean(ControlOptions.DEEPLAVA);
	}

	public static Block getBlockToFillDeepCaves(World world, int x, int z) {
		boolean water = CaveLoader.instance.getDefinition(world, x, z).getBoolean(ControlOptions.DEEPWATER);
		return water ? Blocks.water : Blocks.air;
	}

	public static boolean canGenInWorld(World world, EventType structure) {
		return !MinecraftForge.EVENT_BUS.post(new GenerationCheckEvent(world, structure));
	}

}
