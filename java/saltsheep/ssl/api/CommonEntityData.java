package saltsheep.ssl.api;

import com.google.common.collect.Maps;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.event.FMLServerStoppedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public class CommonEntityData<K, V>
        extends HashMap<K, V> {
    private static final Map<Entity, CommonEntityData> datas = Maps.newHashMap();

    public static void check() {
        Set<Entity> removes = new HashSet<>();
        for (Entity entity : datas.keySet()) {
            if (!entity.isEntityAlive() || !entity.isAddedToWorld())
                removes.add(entity);
        }
        for (Entity entity : removes)
            datas.remove(entity);
    }

    public static void reset() {
        datas.clear();
    }

    public static CommonEntityData<String, Object> getData(@Nonnull Entity entity) {
        CommonEntityData<Object, Object> data = datas.get(entity);
        if (data == null) {
            data = new CommonEntityData<>();
            datas.put(entity, data);
        }
        return (CommonEntityData) data;
    }

    public static class Handler {
        public static Handler subscriber = new Handler();

        @SubscribeEvent
        public void onServerTick(TickEvent.ServerTickEvent event) {
            if (event.phase != TickEvent.Phase.END)
                return;
            CommonEntityData.check();
        }

        public void onServerStopped(FMLServerStoppedEvent event) {
            CommonEntityData.reset();
        }
    }
}