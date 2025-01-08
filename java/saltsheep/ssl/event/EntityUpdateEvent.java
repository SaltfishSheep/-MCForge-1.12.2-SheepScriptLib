package saltsheep.ssl.event;

import net.minecraft.entity.Entity;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;

public class EntityUpdateEvent
        extends EntityEvent {
    public final Side side;
    public final TickEvent.Phase phase;

    public EntityUpdateEvent(Entity entity, Side side, TickEvent.Phase phase) {
        super(entity);
        this.side = side;
        this.phase = phase;
    }
}