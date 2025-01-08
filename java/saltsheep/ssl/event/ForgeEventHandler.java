package saltsheep.ssl.event;

import net.minecraft.entity.Entity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import saltsheep.keylib.key.PlayerKeyDownEvent;
import saltsheep.keylib.key.PlayerKeyUpEvent;
import saltsheep.ssl.event.script.*;

import java.util.List;

public class ForgeEventHandler {
    public static ForgeEventHandler subscriber = new ForgeEventHandler();

    @SubscribeEvent
    public void onEntityJoinWorld(EntityJoinWorldEvent event) {
        if ((event.getWorld()).isRemote)
            return;
        EventPoster.postForgeEvent(new EntityScriptJoinWorldEvent(event));
    }

    @SubscribeEvent
    public void onWorldTick(TickEvent.WorldTickEvent event) {
        Side side = event.side;
        TickEvent.Phase phase = event.phase;
        List<Entity> entities = event.world.getEntities(Entity.class, entity -> true);
        for (Entity entity : entities) {
            EntityUpdateEvent entityEvent = new EntityUpdateEvent(entity, side, phase);
            if (side == Side.SERVER)
                EventPoster.postForgeEvent(new EntityScriptTickEvent(entityEvent));
            MinecraftForge.EVENT_BUS.post(entityEvent);
        }
    }

    @SubscribeEvent
    public void onKeyDown(PlayerKeyDownEvent event) {
        EventPoster.postPlayerEvent(new PlayerKeyDownCNPCEvent(event.player, event.key, event.time));
    }

    @SubscribeEvent
    public void onKeyUp(PlayerKeyUpEvent event) {
        EventPoster.postPlayerEvent(new PlayerKeyUpCNPCEvent(event.player, event.key, event.time));
    }
}