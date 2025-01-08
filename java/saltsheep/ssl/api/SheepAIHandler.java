package saltsheep.ssl.api;

import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import noppes.npcs.api.entity.IEntity;
import noppes.npcs.api.entity.IEntityLivingBase;
import saltsheep.ssl.api.wrapper.AIKeepDistance;
import saltsheep.ssl.common.GOCMap;

import java.util.*;

public class SheepAIHandler {
    private static final GOCMap<Entity, GOCMap<String, List<SheepAI>>> aiGroups = new GOCMap<>(() -> new GOCMap<>(LinkedList::new));
    private static final GOCMap<Entity, Map<String, SheepAI>> aiGroupsRun = new GOCMap<>(HashMap::new);
    private static final Set<SheepAI> stoppingAIs = new HashSet<>();
    public static SheepAIHandler subscriber = new SheepAIHandler();

    public static void addAI(IEntity<?> entity, String group, SheepAI ai) {
        ((List<SheepAI>) ((GOCMap) aiGroups.getOrCreate(entity.getMCEntity())).getOrCreate(group)).add(ai);
    }


    public static void stopAll(IEntity<?> entity) {
        Map<String, SheepAI> groupsRun = aiGroupsRun.get(entity.getMCEntity());
        if (groupsRun == null)
            return;
        for (SheepAI ai : groupsRun.values()) {
            if (ai.state.canStop())
                ai.stopThread();
        }
    }

    public static void clearAI(IEntity<?> entity) {
        stopAll(entity);
        unloadEntityGroups(entity.getMCEntity());
    }


    public static void resetOnServerStopping() {
        aiGroups.clear();
        for (Map<String, SheepAI> eachEntity : aiGroupsRun.values()) {
            for (SheepAI eachAI : eachEntity.values())
                eachAI.stopThreadForced();
        }
        aiGroupsRun.clear();
    }

    private static void updateEntitiesGroups() {
        List<Map.Entry<Entity, GOCMap<String, List<SheepAI>>>> list = new LinkedList<>(aiGroups.entrySet());
        for (Map.Entry<Entity, GOCMap<String, List<SheepAI>>> entry : list) {
            Entity entity = entry.getKey();

            if (!entity.isAddedToWorld() || !entity.isEntityAlive()) {
                unloadEntityGroups(entity);
                continue;
            }
            updateEntityGroups(entity, entry.getValue());
        }
    }

    private static void unloadEntityGroups(Entity entity) {
        aiGroups.remove(entity);
    }

    private static void updateEntityGroups(Entity entity, GOCMap<String, List<SheepAI>> groups) {
        Map<String, SheepAI> groupsRun = aiGroupsRun.getOrCreate(entity);

        for (Map.Entry<String, List<SheepAI>> group : groups.entrySet()) {

            if (groupsRun.containsKey(group.getKey())) {
                continue;
            }

            for (SheepAI ai : group.getValue()) {
                if (ai.apply()) {
                    groupsRun.put(group.getKey(), ai);
                }
            }
        }
    }

    private static void updateEntitiesRunningAIs() {
        List<Map.Entry<Entity, Map<String, SheepAI>>> allEntitiesSet = new LinkedList<>(aiGroupsRun.entrySet());
        for (Map.Entry<Entity, Map<String, SheepAI>> entry : allEntitiesSet) {
            Entity entity = entry.getKey();
            Map<String, SheepAI> groupsRun = entry.getValue();
            updateEntityRunningAIsStopping(entity, groupsRun);
            updateEntityRunningAIs(entity, groupsRun);
            unloadEntityEndedAIs(entity, groupsRun);
        }
    }

    private static void updateEntityRunningAIsStopping(Entity entity, Map<String, SheepAI> groupsRun) {
        for (SheepAI ai : groupsRun.values()) {

            if ((!entity.isEntityAlive() || !entity.isAddedToWorld()) && ai.state.canStop()) {
                ai.stopThread();
            }

            if (ai.state.isStopping() && ai.state.isWaiting() && ai.state.canWaitingInterruptedByStopping()) {
                ai.continueRun();
            }
        }
    }

    private static void updateEntityRunningAIs(Entity entity, Map<String, SheepAI> groupsRun) {
        for (SheepAI ai : groupsRun.values()) {


            ai.untilWaitingOrEnded();
            while (!ai.works.isEmpty())
                ai.works.pop().run();
            ai.checkWait();
        }
    }

    private static void unloadEntityEndedAIs(Entity entity, Map<String, SheepAI> groupsRun) {
        List<Map.Entry<String, SheepAI>> entries = new LinkedList<>(groupsRun.entrySet());
        for (Map.Entry<String, SheepAI> entry : entries) {
            SheepAI ai = entry.getValue();
            if (!ai.state.isRunning()) {
                ai.state.reset();
                groupsRun.remove(entry.getKey());
            }
        }

        if (groupsRun.isEmpty()) {
            aiGroupsRun.remove(entity);
        }
    }

    public static SheepAI getKeepDistanceAI(IEntityLivingBase<?> entity, double normalDistance, double shieldDistance) {
        return new AIKeepDistance(entity, normalDistance, shieldDistance);
    }

    @SubscribeEvent
    public void serverTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.START)
            return;
        updateEntitiesGroups();
        updateEntitiesRunningAIs();
    }
}