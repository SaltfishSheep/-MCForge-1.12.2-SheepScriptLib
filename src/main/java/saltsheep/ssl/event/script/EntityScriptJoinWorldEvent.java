package saltsheep.ssl.event.script;

import net.minecraft.world.WorldServer;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import noppes.npcs.api.IWorld;

public class EntityScriptJoinWorldEvent extends SheepEntityCommonEvent {

	public final IWorld world;
	
	public EntityScriptJoinWorldEvent(EntityJoinWorldEvent event) {
		super(event);
		this.world = Helper.getIWorld((WorldServer) event.getWorld());
	}

	@Override
	public String eventName() {
		return "entitySpawn";
	}
	
	

}
