package saltsheep.ssl.api;

import com.google.common.collect.Maps;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.common.event.FMLServerStoppedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class CommonEntityData<K,V> extends HashMap<K,V>{

    private static Map<Entity,CommonEntityData> datas = Maps.newHashMap();

    public static void check(){
        for(Entity entity:datas.keySet())
            if(!(entity.isEntityAlive()&&entity.isAddedToWorld()))
                datas.remove(entity);
    }

    public static void reset(){
        datas.clear();
    }

    public static CommonEntityData<String,Object> getData(@Nonnull Entity entity){
        CommonEntityData data = datas.get(entity);
        if(data==null){
            data = new CommonEntityData();
            datas.put(entity,data);
        }
        return data;
    }

    public static class Handler{

        public static Handler subscriber = new Handler();

        @SubscribeEvent
        public void onServerTick(TickEvent.ServerTickEvent event){
            if(event.phase!= TickEvent.Phase.END)
                return;
            check();
        }

        public void onServerStopped(FMLServerStoppedEvent event){
            reset();
        }

    }

}
