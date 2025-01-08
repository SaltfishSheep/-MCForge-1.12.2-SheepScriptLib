package saltsheep.ssl.event.script;

import noppes.npcs.api.entity.IPlayer;
import noppes.npcs.api.event.PlayerEvent;
import noppes.npcs.constants.EnumScriptType;

public abstract class SheepPlayerEvent
        extends PlayerEvent {
    public SheepPlayerEvent(IPlayer<?> player) {
        super(player);
    }

    public abstract EnumScriptType scriptType();
}