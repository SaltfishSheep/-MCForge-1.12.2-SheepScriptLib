package saltsheep.ssl.puppet.handler;

import net.minecraftforge.fml.common.event.FMLServerStoppedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import saltsheep.ssl.puppet.network.SPacketAnimation;

public class NeoPuppetEventHandler {
    public static NeoPuppetEventHandler subscriber = new NeoPuppetEventHandler();

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END)
            return;
        SPacketAnimation.handleCaches();
    }

    public void onServerStopped(FMLServerStoppedEvent event) {
        SPacketAnimation.resetCaches();
    }
}