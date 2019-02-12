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

import Reika.CaveControl.CaveControl;
import Reika.CaveControl.CaveHooks;
import Reika.CaveControl.Registry.CaveOptions;
import Reika.DragonAPI.DragonOptions;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.BiomeGenOcean;
import net.minecraft.world.gen.structure.MapGenStronghold;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;

public final class ControllableStrongholdGen extends MapGenStronghold {

	private final HashSet<ChunkCoordIntPair> generationChunks = new HashSet();
	private static final int COUNT = CaveOptions.STRONGHOLDCOUNT.getValue();
	private static final int MIN_DIST = CaveOptions.STRONGHOLDMIN.getValue();
	private static final int MAX_DIST = CaveOptions.STRONGHOLDMAX.getValue();
	private static final int PER_RING = COUNT/CaveOptions.STRONGHOLDRINGS.getValue();
	private static final double MAX_ANGLE = 360D/PER_RING;

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
	protected boolean canSpawnStructureAtCoords(int chunkX, int chunkZ) {
		BiomeGenBase biome = ReikaWorldHelper.getNaturalGennedBiomeAt(worldObj, chunkX << 4, chunkZ << 4);
		if (CaveOptions.NOOCEANSTRONGHOLDS.getState() && (biome instanceof BiomeGenOcean || BiomeDictionary.isBiomeOfType(biome, Type.OCEAN)))
			return false;
		return CaveHooks.shouldGenerateStrongholds(biome) ? this.shouldSpawnStructureAtCoords(chunkX, chunkZ) : false;
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
