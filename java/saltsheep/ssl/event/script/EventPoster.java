package saltsheep.ssl.event.script;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.Event;
import noppes.npcs.api.wrapper.WrapperNpcAPI;
import noppes.npcs.controllers.ScriptController;
import noppes.npcs.controllers.data.ForgeScriptData;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.controllers.data.PlayerScriptData;
import noppes.npcs.entity.EntityNPCInterface;

public class EventPoster {
    public static boolean postPlayerEvent(SheepPlayerEvent event) {
        PlayerScriptData handler = (PlayerData.get(event.player.getMCEntity())).scriptData;
        handler.runScript(event.scriptType(), event);
        return WrapperNpcAPI.EVENT_BUS.post(event);
    }

    public static boolean postForgeEvent(SheepForgeEvent event) {
        ForgeScriptData handler = ScriptController.Instance.forgeScripts;
        handler.runScript(event.eventName(), event);
        return WrapperNpcAPI.EVENT_BUS.post(event);
    }

    public static boolean postNpcEvent(SheepNpcEvent event) {
        if (!(event.npc.getMCEntity() instanceof EntityNPCInterface))
            return false;
        ((EntityNPCInterface) event.npc.getMCEntity()).script.runScript(event.scriptType(), event);
        return WrapperNpcAPI.EVENT_BUS.post(event);
    }
}
