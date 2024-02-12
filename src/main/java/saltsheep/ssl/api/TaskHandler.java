package saltsheep.ssl.api;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import saltsheep.ssl.common.NodeNI;
import saltsheep.ssl.common.NodeSI;

import javax.annotation.Nonnull;
import java.util.LinkedList;

public class TaskHandler {

    public static TaskHandler subscriber = new TaskHandler();

    static LinkedList<NodeNI<Task>> taskChains = new LinkedList<>();

    static void appendChain(@Nonnull Task task){
        Task each = task;
        while(each!=null){
            each.init();
            each = each.next;
        }
        NodeNI<Task> newNode = new NodeNI<>(task);
        taskChains.add(newNode);
    }

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event){
        if(event.phase != TickEvent.Phase.START)
            return;
        LinkedList<NodeNI<Task>> chainsShouldRemoved = new LinkedList<>();
        for(NodeNI<Task> chain:taskChains){
            chain:while(chain.value!=null){
                try {
                    if (chain.value.invoke())
                        chain.value = chain.value.next;
                    else
                        break chain;
                }catch (Throwable error){
                    error.printStackTrace();
                    chain.value = chain.value.next;
                }
            }
            if(chain.value==null)
                chainsShouldRemoved.add(chain);
        }
        taskChains.removeAll(chainsShouldRemoved);
    }

    public static void resetOnServerStopping(){
        for(NodeNI<Task> chain:taskChains){
            chain:while(chain.value!=null){
                try {
                    if (chain.value.stopRun != null)
                        chain.value.stopRun.run();
                }catch (Throwable error){
                    error.printStackTrace();
                }
                chain.value = chain.value.next;
            }
        }
        taskChains.clear();
    }

}
