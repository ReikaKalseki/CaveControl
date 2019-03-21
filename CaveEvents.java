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

import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.event.terraingen.InitMapGenEvent;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;

import Reika.CaveControl.Generators.ControllableCaveGen;
import Reika.CaveControl.Generators.ControllableMineshaftGen;
import Reika.CaveControl.Generators.ControllableRavineGen;
import Reika.CaveControl.Generators.ControllableStrongholdGen;

import cpw.mods.fml.common.eventhandler.Event.Result;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;


public class CaveEvents {

	public static final CaveEvents instance = new CaveEvents();

	private CaveEvents() {

	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void caveControl(InitMapGenEvent ev) {
		switch(ev.type) {
			case CAVE:
				ev.newGen = new ControllableCaveGen();
				break;
			case MINESHAFT:
				ev.newGen = new ControllableMineshaftGen();
				break;
			case NETHER_BRIDGE:
				break;
			case RAVINE:
				ev.newGen = new ControllableRavineGen();
				break;
			case SCATTERED_FEATURE:
				break;
			case STRONGHOLD:
				ev.newGen = new ControllableStrongholdGen();
				break;
			default:
				//ev.newGen = ev.originalGen;
				break;
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void dungeonControl(PopulateChunkEvent.Populate ev) {
		if (ev.type == PopulateChunkEvent.Populate.EventType.DUNGEON) {
			World world = ev.world;
			int x = ev.chunkX*16;
			int z = ev.chunkZ*16;
			BiomeGenBase biome = world.getBiomeGenForCoords(x, z);
			if (!CaveHooks.shouldGenerateDungeons(biome))
				ev.setResult(Result.DENY);
		}
	}

}
