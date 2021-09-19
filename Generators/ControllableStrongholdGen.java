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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.BiomeGenOcean;
import net.minecraft.world.gen.structure.MapGenStronghold;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;

import Reika.CaveControl.CaveControl;
import Reika.CaveControl.CaveHooks;
import Reika.CaveControl.CaveLoader;
import Reika.CaveControl.Registry.CaveOptions;
import Reika.DragonAPI.DragonOptions;
import Reika.DragonAPI.Auxiliary.ModularLogger;

public final class ControllableStrongholdGen extends MapGenStronghold {

	private final HashSet<ChunkCoordIntPair> generationChunks = new HashSet();
	private static final int COUNT = CaveOptions.STRONGHOLDCOUNT.getValue();
	private static final int MIN_DIST = CaveOptions.STRONGHOLDMIN.getValue();
	private static final int MAX_DIST = CaveOptions.STRONGHOLDMAX.getValue();
	private static final int PER_RING = COUNT/CaveOptions.STRONGHOLDRINGS.getValue();
	private static final double MAX_ANGLE = 360D/PER_RING;

	private static final String LOGGER_TAG = "StrongholdChunks";

	static {
		ModularLogger.instance.addLogger(CaveControl.instance, LOGGER_TAG);
	}

	public ControllableStrongholdGen() {
		super(new HashMap()/*getMap()*/);
	}
	/*
	private static Map getMap() {
		Map map = new HashMap();
		map.put("distance", String.valueOf(32D*CaveOptions.STRONGHOLDDIST.getFloat()));
		int n = CaveOptions.STRONGHOLDCOUNT.getValue();
		map.put("count", String.valueOf(n));
		//map.put("spread", String.valueOf(n));
		return map;
	}*/

	@Override
	public void func_151538_a(World world, int x, int z, int x2, int z2, Block[] data) {
		super.func_151538_a(world, x, z, x2, z2, data);
		if (ModularLogger.instance.isEnabled(LOGGER_TAG)) {
			BiomeGenBase biome = CaveLoader.instance.getEffectiveBiome(world, x*16, z*16);
			ModularLogger.instance.log(LOGGER_TAG, "Stronghold generation criteria @ "+(x*16)+", "+(z*16)+"in biome: "+biome.biomeName);
			boolean oceanFail = CaveOptions.NOOCEANSTRONGHOLDS.getState() && (biome instanceof BiomeGenOcean || BiomeDictionary.isBiomeOfType(biome, Type.OCEAN));
			boolean shouldGenInBiome = CaveHooks.shouldGenerateStrongholds(world, x*16, z*16);
			boolean chunk = this.shouldSpawnStructureAtCoords(x, z);
			ModularLogger.instance.log(LOGGER_TAG, "Should gen in biome: "+shouldGenInBiome+"; Valid Chunk: "+chunk+"; Ocean-blocked: "+oceanFail);
		}
	}

	@Override
	public ChunkPosition func_151545_a(World world, int x, int y, int z) {
		ChunkPosition ret = super.func_151545_a(world, x, y, z);
		if (world.checkChunksExist(x, y, z, x, y, z))
			return ret;
		double d = ret != null ? (ret.chunkPosX-x)*(ret.chunkPosX-x)+(ret.chunkPosZ-z)*(ret.chunkPosZ-z) : Double.POSITIVE_INFINITY;
		for (ChunkCoordIntPair p : generationChunks) {
			ChunkPosition ctr = p.func_151349_a(64);
			double d2 = (ctr.chunkPosX-x)*(ctr.chunkPosX-x)+(ctr.chunkPosZ-z)*(ctr.chunkPosZ-z);
			if (d2 < d) {
				ret = ctr;
				d = d2;
			}
		}
		return ret;
	}

	@Override
	protected boolean canSpawnStructureAtCoords(int chunkX, int chunkZ) {
		if (CaveOptions.NOOCEANSTRONGHOLDS.getState()) {
			BiomeGenBase biome = CaveLoader.instance.getEffectiveBiome(worldObj, chunkX, chunkZ);
			if (biome instanceof BiomeGenOcean || BiomeDictionary.isBiomeOfType(biome, Type.OCEAN)) {
				if (ModularLogger.instance.isEnabled(LOGGER_TAG)) {
					if (CaveHooks.shouldGenerateStrongholds(worldObj, chunkX << 4, chunkZ << 4) && this.shouldSpawnStructureAtCoords(chunkX, chunkZ))
						CaveControl.logger.log("Planned stronghold generation @ "+chunkX+", "+chunkZ+" disallowed: "+biome.biomeName);
				}
				return false;
			}
		}
		return CaveHooks.shouldGenerateStrongholds(worldObj, chunkX << 4, chunkZ << 4) ? this.shouldSpawnStructureAtCoords(chunkX, chunkZ) : false;
	}

	private boolean shouldSpawnStructureAtCoords(int x, int z) {
		if (generationChunks.isEmpty()) {
			this.calculateLocations();
		}
		return generationChunks.contains(new ChunkCoordIntPair(x, z));
	}

	private void calculateLocations() {
		Random rand = new Random(worldObj.getSeed());

		int ox = DragonOptions.WORLDCENTERX.getValue();
		int oz = DragonOptions.WORLDCENTERZ.getValue();

		//BREAKS THINGS
		//ox = ox-500+rand.nextInt(1001);
		//oz = oz-500+rand.nextInt(1001);

		double ang = 0;
		double minAng = Math.min(10, MAX_ANGLE/4);

		int ringCount = 0;
		double rFactor = 1;
		for (int idx = 0; idx < COUNT; idx++) {
			double r = MIN_DIST+rand.nextInt(MAX_DIST-MIN_DIST+1);
			r *= rFactor;
			int cx = ox+(int)Math.round(Math.cos(ang)*r);
			int cz = oz+(int)Math.round(Math.sin(ang)*r);

			ChunkPosition biome = worldObj.getWorldChunkManager().findBiomePosition(cx+8, cz+8, 112, field_151546_e, rand);
			if (biome != null) {
				cx = biome.chunkPosX;
				cz = biome.chunkPosZ;
			}

			this.registerStrongholdChunk(idx, cx >> 4, cz >> 4);
			ringCount++;
			ang += minAng+(MAX_ANGLE-minAng)*rand.nextDouble();
			if (ang >= 360 || ringCount >= PER_RING) {
				rFactor *= CaveOptions.STRONGHOLDRINGSCALE.getDefaultFloat();
				ringCount = 0;
			}
		}
	}

	private void registerStrongholdChunk(int idx, int cx, int cz) {
		CaveControl.logger.debug("Registered possible stronghold #"+idx+" location: "+cx*16+", "+cz*16);
		generationChunks.add(new ChunkCoordIntPair(cx, cz));
		if (Math.abs(cx) >= 100000 || Math.abs(cz) >= 100000) {
			CaveControl.logger.log("Warning: Very far stronghold #"+idx+" @ "+cx*16+", "+cz*16+"!");
		}
	}

	@Override
	protected List getCoordList() {
		ArrayList<ChunkPosition> li = new ArrayList();
		for (ChunkCoordIntPair p : generationChunks) {
			li.add(p.func_151349_a(64));
		}
		return li;
	}
}
