package saltsheep.ssl.event;

import net.minecraft.entity.Entity;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.relauncher.Side;

public class EntityUpdateEvent extends EntityEvent {
	
	public final Side side;
	public final Phase phase;

	public EntityUpdateEvent(Entity entity, Side side, Phase phase) {
		super(entity);
		this.side = side;
		this.phase = phase;
	}

}
