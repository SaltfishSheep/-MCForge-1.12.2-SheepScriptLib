package saltsheep.ssl.event.script;

import net.minecraftforge.fml.common.eventhandler.Event;
import noppes.npcs.api.event.NpcEvent;

public abstract class NpcForgeEvent extends SheepForgeEvent {
    private final String hookName;

    public NpcForgeEvent(NpcEvent event, String hookName) {
        super(event);
        this.hookName = hookName;
    }

    public String eventName() {
        return this.hookName;
    }

    public static class NpcForgeCollideEvent extends NpcForgeEvent {
        public NpcForgeCollideEvent(NpcEvent.CollideEvent event) {
            super(event, "npcCollide");
        }
    }

    public static class NpcForgeDamagedEvent extends NpcForgeEvent {
        public NpcForgeDamagedEvent(NpcEvent.DamagedEvent event) {
            super(event, "npcDamaged");
        }
    }

    public static class NpcForgeDiedEvent extends NpcForgeEvent {
        public NpcForgeDiedEvent(NpcEvent.DiedEvent event) {
            super(event, "npcDied");
        }
    }

    public static class NpcForgeInitEvent extends NpcForgeEvent {
        public NpcForgeInitEvent(NpcEvent.InitEvent event) {
            super(event, "npcInit");
        }
    }

    public static class NpcForgeInteractEvent extends NpcForgeEvent {
        public NpcForgeInteractEvent(NpcEvent.InteractEvent event) {
            super(event, "npcInteract");
        }
    }

    public static class NpcForgeKilledEntityEvent extends NpcForgeEvent {
        public NpcForgeKilledEntityEvent(NpcEvent.KilledEntityEvent event) {
            super(event, "npcKill");
        }
    }

    public static class NpcForgeMeleeAttackEvent extends NpcForgeEvent {
        public NpcForgeMeleeAttackEvent(NpcEvent.MeleeAttackEvent event) {
            super(event, "npcMeleeAttack");
        }
    }

    public static class NpcForgeRangedLaunchedEvent extends NpcForgeEvent {
        public NpcForgeRangedLaunchedEvent(NpcEvent.RangedLaunchedEvent event) {
            super(event, "npcRangedAttack");
        }
    }

    public static class NpcForgeTargetEvent extends NpcForgeEvent {
        public NpcForgeTargetEvent(NpcEvent.TargetEvent event) {
            super(event, "npcTarget");
        }
    }

    public static class NpcForgeTargetLostEvent extends NpcForgeEvent {
        public NpcForgeTargetLostEvent(NpcEvent.TargetLostEvent event) {
            super(event, "npcTargetLost");
        }
    }

    public static class NpcForgeTimerEvent extends NpcForgeEvent {
        public NpcForgeTimerEvent(NpcEvent.TimerEvent event) {
            super(event, "npcTimer");
        }
    }

    public static class NpcForgeUpdateEvent extends NpcForgeEvent {
        public NpcForgeUpdateEvent(NpcEvent.UpdateEvent event) {
            super(event, "npcTick");
        }
    }
}