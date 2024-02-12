package saltsheep.ssl.event;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import noppes.npcs.api.event.NpcEvent;
import saltsheep.ssl.event.script.EventPoster;
import saltsheep.ssl.event.script.NpcForgeEvent;

public class NPCEventSubscriber {

	public static NPCEventSubscriber subscriber = new NPCEventSubscriber();
	
	@SubscribeEvent
	public void onNpcCollide(NpcEvent.CollideEvent event) {
		EventPoster.postForgeEvent(new NpcForgeEvent.NpcForgeCollideEvent(event));
	}
	
	@SubscribeEvent
	public void onNpcDamaged(NpcEvent.DamagedEvent event) {
		EventPoster.postForgeEvent(new NpcForgeEvent.NpcForgeDamagedEvent(event));
	}
	
	@SubscribeEvent
	public void onNpcDied(NpcEvent.DiedEvent event) {
		EventPoster.postForgeEvent(new NpcForgeEvent.NpcForgeDiedEvent(event));
	}
	
	@SubscribeEvent
	public void onNpcInit(NpcEvent.InitEvent event) {
		EventPoster.postForgeEvent(new NpcForgeEvent.NpcForgeInitEvent(event));
	}
	
	@SubscribeEvent
	public void onNpcInteract(NpcEvent.InteractEvent event) {
		EventPoster.postForgeEvent(new NpcForgeEvent.NpcForgeInteractEvent(event));
	}
	
	@SubscribeEvent
	public void onNpcKilledEntity(NpcEvent.KilledEntityEvent event) {
		EventPoster.postForgeEvent(new NpcForgeEvent.NpcForgeKilledEntityEvent(event));
	}
	
	@SubscribeEvent
	public void onNpcMeleeAttack(NpcEvent.MeleeAttackEvent event) {
		EventPoster.postForgeEvent(new NpcForgeEvent.NpcForgeMeleeAttackEvent(event));
	}
	
	@SubscribeEvent
	public void onNpcRangedLaunched(NpcEvent.RangedLaunchedEvent event) {
		EventPoster.postForgeEvent(new NpcForgeEvent.NpcForgeRangedLaunchedEvent(event));
	}
	
	@SubscribeEvent
	public void onNpcTarget(NpcEvent.TargetEvent event) {
		EventPoster.postForgeEvent(new NpcForgeEvent.NpcForgeTargetEvent(event));
	}
	
	@SubscribeEvent
	public void onNpcTargetLost(NpcEvent.TargetLostEvent event) {
		EventPoster.postForgeEvent(new NpcForgeEvent.NpcForgeTargetLostEvent(event));
	}
	
	@SubscribeEvent
	public void onNpcTimer(NpcEvent.TimerEvent event) {
		EventPoster.postForgeEvent(new NpcForgeEvent.NpcForgeTimerEvent(event));
	}
	
	@SubscribeEvent
	public void onNpcUpdate(NpcEvent.UpdateEvent event) {
		EventPoster.postForgeEvent(new NpcForgeEvent.NpcForgeUpdateEvent(event));
	}
	
}
