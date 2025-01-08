package saltsheep.ssl.event.script;

import net.minecraft.entity.player.EntityPlayerMP;
import noppes.npcs.api.entity.IPlayer;
import noppes.npcs.constants.EnumScriptType;
import saltsheep.ssl.SheepScriptLib;

public class PlayerKeyUpCNPCEvent extends SheepPlayerEvent {
    public final int key;
    public final long upTime;

    public PlayerKeyUpCNPCEvent(String playerName, int key, long upTime) {
        this(Helper.getIPlayer(SheepScriptLib.getMCServer().getPlayerList().getPlayerByUsername(playerName)), key, upTime);
    }

    public PlayerKeyUpCNPCEvent(EntityPlayerMP player, int key, long upTime) {
        this(Helper.getIPlayer(player), key, upTime);
    }

    public PlayerKeyUpCNPCEvent(IPlayer<?> player, int key, long upTime) {
        super(player);
        this.key = key;
        this.upTime = upTime;
    }

    public EnumScriptType scriptType() {
        return SheepScriptType.KEY_UP;
    }
}