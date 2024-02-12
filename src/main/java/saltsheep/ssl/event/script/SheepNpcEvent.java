package saltsheep.ssl.event.script;

import noppes.npcs.api.entity.ICustomNpc;
import noppes.npcs.api.event.NpcEvent;
import noppes.npcs.constants.EnumScriptType;

public abstract class SheepNpcEvent extends NpcEvent {

	public SheepNpcEvent(ICustomNpc<?> npc) {
		super(npc);
	}
	
	public abstract EnumScriptType scriptType();

}
