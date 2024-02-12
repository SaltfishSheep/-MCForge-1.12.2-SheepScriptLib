package saltsheep.ssl.event.script;

import net.minecraft.entity.player.EntityPlayerMP;
import noppes.npcs.api.entity.IPlayer;
import noppes.npcs.constants.EnumScriptType;
import saltsheep.ssl.SheepScriptLib;

public class PlayerKeyDownCNPCEvent extends SheepPlayerEvent {

	public final int key;
	public final long downTime;
	
	public PlayerKeyDownCNPCEvent(String playerName, int key, long downTime) {
		this(Helper.getIPlayer(SheepScriptLib.getMCServer().getPlayerList().getPlayerByUsername(playerName)), key, downTime);
	}
	
	public PlayerKeyDownCNPCEvent(EntityPlayerMP player, int key, long downTime) {
		this(Helper.getIPlayer(player),key,downTime);
	}
	
	public PlayerKeyDownCNPCEvent(IPlayer<?> player, int key, long downTime) {
		super(player);
		this.key = key;
		this.downTime = downTime;
	}

	@Override
	public EnumScriptType scriptType() {
		return SheepScriptType.KEY_DOWN;
	}

}
