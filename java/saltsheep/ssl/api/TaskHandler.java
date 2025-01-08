package saltsheep.ssl.api;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import saltsheep.ssl.common.NodeNI;

import javax.annotation.Nonnull;
import java.util.LinkedList;


public class TaskHandler {
    public static TaskHandler instance = new TaskHandler();

    private LinkedList<NodeNI<Task>> taskChains = new LinkedList<>();

    private LinkedList<NodeNI<Task>> taskChainsCaches = new LinkedList<>();

    private static boolean isUpdating = false;

    public void appendChain(@Nonnull Task task) {
        Task each = task;
        while (each != null) {
            each.init();
            each = each.next;
        }
        NodeNI<Task> newNode = new NodeNI(task);
        if (isUpdating)
            updateTask(newNode);
        taskChainsCaches.add(newNode);
    }

    public void resetOnServerStopping() {
        applyCaches();
        for (NodeNI<Task> chain : taskChains) {
            while (chain.value != null) {
                try {
                    if (chain.value.stopRun != null)
                        chain.value.stopRun.run();
                } catch (Throwable error) {
                    error.printStackTrace();
                }
                chain.value = chain.value.next;
            }
        }
        taskChains.clear();
    }

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END)
            return;
        applyCaches();
        isUpdating = true;
        LinkedList<NodeNI<Task>> chainsShouldRemoved = new LinkedList<>();
        for (NodeNI<Task> chain : taskChains) {
            updateTask(chain);
            if (chain.value == null)
                chainsShouldRemoved.add(chain);
        }
        taskChains.removeAll(chainsShouldRemoved);
        isUpdating = false;
    }

    private void applyCaches() {
        taskChains.addAll(taskChainsCaches);
    }

    private void updateTask(NodeNI<Task> chain) {
        while (chain.value != null) {
            try {
                if (chain.value.runTick()) {
                    chain.value = chain.value.next;
                    continue;
                }
                break;
            } catch (Throwable error) {
                error.printStackTrace();
                chain.value = chain.value.next;
            }
        }
    }
}