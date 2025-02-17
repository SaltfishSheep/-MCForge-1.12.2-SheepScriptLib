package saltsheep.ssl.api;

import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import saltsheep.ssl.SheepScriptLibConfig;
import saltsheep.ssl.common.NodeNI;

import javax.annotation.Nonnull;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.UUID;


public class TaskHandler {
    public static TaskHandler instance = new TaskHandler();

    private LinkedList<NodeNI<Task>> taskChains = new LinkedList<>();

    private LinkedList<NodeNI<Task>> taskChainsCaches = new LinkedList<>();

    private static boolean isUpdating = false;

    public void appendChain(@Nonnull Task task) {
        if (!SheepScriptLibConfig.task_enable)
            return;
        if (SheepScriptLibConfig.task_overrideOld)
            this.stop(task);
        Task each = task;
        UUID identify = UUID.randomUUID();
        while (each != null) {
            each.init();
            each.tempIdentify = identify;
            each = each.next;
        }
        NodeNI<Task> newNode = new NodeNI(task);
        if (isUpdating)
            updateTask(newNode);
        taskChainsCaches.add(newNode);
    }

    public void resetOnServerStopping() {
        applyCaches();
        stopAll();
    }

    public void stop(Task task) {
        if (task.tempIdentify != null)
            stop(task.tempIdentify);
    }

    public void stop(UUID identify) {
        Iterator<NodeNI<Task>> iterator = taskChains.iterator();
        while (iterator.hasNext()) {
            NodeNI<Task> chain = iterator.next();
            if (chain.value.tempIdentify.equals(identify)) {
                invokeStopRun(chain);
                iterator.remove();
                break;
            }
        }
    }

    public void stopAll() {
        for (NodeNI<Task> chain : taskChains)
            invokeStopRun(chain);
        taskChains.clear();
    }

    private void invokeStopRun(NodeNI<Task> chain) {
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

    private void applyCaches() {
        taskChains.addAll(taskChainsCaches);
        taskChainsCaches.clear();
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
}