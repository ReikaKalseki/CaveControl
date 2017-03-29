package Reika.CaveControl;

import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.event.terraingen.InitMapGenEvent;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;
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
				ev.newGen = CaveControl.caveGen;
				break;
			case MINESHAFT:
				ev.newGen = CaveControl.mineGen;
				break;
			case NETHER_BRIDGE:
				break;
			case RAVINE:
				ev.newGen = CaveControl.ravineGen;
				break;
			case SCATTERED_FEATURE:
				break;
			case STRONGHOLD:
				ev.newGen = CaveControl.strongholdGen;
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
