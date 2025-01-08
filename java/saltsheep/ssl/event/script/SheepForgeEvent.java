package saltsheep.ssl.event.script;

import net.minecraftforge.fml.common.eventhandler.Event;
import noppes.npcs.api.event.ForgeEvent;

public abstract class SheepForgeEvent
        extends ForgeEvent {
    public SheepForgeEvent(Event event) {
        super(event);
    }

    public boolean isCancelable() {
        return this.event.isCancelable();
    }

    public boolean isCanceled() {
        return this.event.isCanceled();
    }

    public void setCanceled(boolean cancel) {
        this.event.setCanceled(cancel);
    }

    public abstract String eventName();
}