package saltsheep.ssl.event.script;

import net.minecraftforge.fml.common.gameevent.TickEvent;
import saltsheep.ssl.event.EntityUpdateEvent;

public class EntityScriptTickEvent extends SheepEntityCommonEvent {

	public EntityScriptTickEvent(EntityUpdateEvent event) {
		super(event);
	}

	@Override
	public String eventName() {
		if(((EntityUpdateEvent)this.event).phase == TickEvent.Phase.START)
			return "preEntityTick";
		else 
			return "endEntityTick";
	}

}
