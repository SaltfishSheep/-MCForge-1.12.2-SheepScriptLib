package saltsheep.ssl.event.script;

import noppes.npcs.api.wrapper.WrapperNpcAPI;
import noppes.npcs.controllers.ScriptController;
import noppes.npcs.controllers.data.ForgeScriptData;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.controllers.data.PlayerScriptData;
import noppes.npcs.entity.EntityNPCInterface;

public class EventPoster {

	/**
	 * @param event the event need post.
	 * @return return true if the event is cancelable and already canceled.
	 */
	public static boolean postPlayerEvent(SheepPlayerEvent event) {
		PlayerScriptData handler = PlayerData.get(event.player.getMCEntity()).scriptData;
		handler.runScript(event.scriptType(), event);
		return WrapperNpcAPI.EVENT_BUS.post(event);
	}
	
	/**
	 * @param event the event need post.
	 * @return return true if the event is cancelable and already canceled.
	 */
	public static boolean postForgeEvent(SheepForgeEvent event) {
		ForgeScriptData handler = ScriptController.Instance.forgeScripts;
		handler.runScript(event.eventName(), event);
		return WrapperNpcAPI.EVENT_BUS.post(event);
	}
	
	/**
	 * @param event the event need post.
	 * @return return true if the event is cancelable and already canceled.
	 */
	public static boolean postNpcEvent(SheepNpcEvent event) {
		if(!(event.npc.getMCEntity() instanceof EntityNPCInterface))
			return false;
		((EntityNPCInterface)event.npc.getMCEntity()).script.runScript(event.scriptType(), event);;
		return WrapperNpcAPI.EVENT_BUS.post(event);
	}
	
}
