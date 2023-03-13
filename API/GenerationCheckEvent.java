package Reika.CaveControl.API;

import net.minecraft.world.World;
import net.minecraftforge.event.terraingen.InitMapGenEvent.EventType;

import cpw.mods.fml.common.eventhandler.Cancelable;
import cpw.mods.fml.common.eventhandler.Event;

@Cancelable
public class GenerationCheckEvent extends Event {

	public final World world;
	public final EventType structure;

	public GenerationCheckEvent(World world, EventType s) {
		this.world = world;
		structure = s;
	}

}
