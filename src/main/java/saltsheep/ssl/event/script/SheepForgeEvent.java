package saltsheep.ssl.event.script;

import net.minecraftforge.fml.common.eventhandler.Event;
import noppes.npcs.api.event.ForgeEvent;

public abstract class SheepForgeEvent extends ForgeEvent  {

	public SheepForgeEvent(Event event) {
		super(event);
	}
	
	@Override
	public boolean isCancelable() {
		return this.event.isCancelable();
	}
	
	@Override
	public void setCanceled(boolean cancel) {
		this.event.setCanceled(cancel);
	}
	
	@Override
	public boolean isCanceled() {
		return this.event.isCanceled();
	}
	
	public abstract String eventName();

}
