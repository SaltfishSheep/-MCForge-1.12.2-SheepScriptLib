package saltsheep.ssl.api;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.relauncher.Side;
import noppes.npcs.api.entity.ICustomNpc;
import noppes.npcs.api.entity.data.role.IJobPuppet;
import noppes.npcs.api.entity.data.role.IJobPuppet.IJobPuppetPart;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.roles.JobPuppet.PartConfig;
import saltsheep.ssl.event.EntityUpdateEvent;

@Deprecated
public class AnimationHandler {

	public static AnimationHandler subscriber = new AnimationHandler();

	/*
	private static SPacketNPCAnimation packet = new SPacketNPCAnimation();

	static Field prevTicksField = null;
	static Field startTickField = null;
	static Field valField = null;
	static Field valNextField = null;

	static{
		try {
			prevTicksField = JobPuppet.class.getDeclaredField("prevTicks");
			startTickField = JobPuppet.class.getDeclaredField("startTick");
			valField = JobPuppet.class.getDeclaredField("val");
			valNextField = JobPuppet.class.getDeclaredField("valNext");
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		}
		prevTicksField.setAccessible(true);
		startTickField.setAccessible(true);
		valField.setAccessible(true);
		valNextField.setAccessible(true);
	}
	*/

	/*
	@SubscribeEvent
	public void updateModle(RenderLivingEvent.Pre<EntityNPCInterface> event){
		if(!(event.getEntity() instanceof EntityNPCInterface)||event.getRenderer().getMainModel().getClass()!= ModelPlayerAlt.class)
			return;
		System.out.println(event.getRenderer().getMainModel());
	}
	*/
	
	@SubscribeEvent
	public void updateAnimation(EntityUpdateEvent event) {
		if(event.side!=Side.SERVER||event.phase!=Phase.START||!(event.getEntity() instanceof EntityNPCInterface))
			return;
		EntityNPCInterface npc = (EntityNPCInterface) event.getEntity();
		checkAnimations(npc);
		java.util.Map<String,Object> tempdata = CommonEntityData.getData(npc);
		@SuppressWarnings("unchecked")
		List<Animation> animations = (List<Animation>) tempdata.get("puppetAnimations");
		//*If remove old animation and start new animation,its runTicks is 0.
		if(!animations.isEmpty()&&npc.jobInterface instanceof IJobPuppet) {
			while(animations.get(0).updateAni(npc)) {
				animations.remove(0);
				if(animations.isEmpty()) 
					break;
			}
		}
		if(npc.updateClient)
			npc.updateClient();
	}
	/*
	@SubscribeEvent
	public void checkPacket(TickEvent.ServerTickEvent event){
		if(event.side!=Side.SERVER||event.phase!=Phase.START)
			return;
		System.out.println(SheepScriptLib.getMCServer().worlds[0].getTotalWorldTime());
		if(packet.needSend()){
			PacketSender.INSTANCE.sendToAll(packet);
			packet.clearTask();
		}
	}
	*/
	
	private static void checkAnimations(EntityNPCInterface npc) {
		java.util.Map<String,Object> tempdata = CommonEntityData.getData(npc);
		if(!tempdata.containsKey("puppetAnimations")||!(tempdata.get("puppetAnimations") instanceof List))
			tempdata.put("puppetAnimations", Collections.synchronizedList(new LinkedList<Animation>()));
	}

	public static void addAnimation(ICustomNpc<EntityNPCInterface> npc,Animation ani){
		addAnimation(npc.getMCEntity(),ani);
	}

	@SuppressWarnings("unchecked")
	public static void addAnimation(EntityNPCInterface npc,Animation ani) {
		checkAnimations(npc);
		java.util.Map<String,Object> tempdata = CommonEntityData.getData(npc);
		((List<Animation>)tempdata.get("puppetAnimations")).add(ani);
		/*
		if(!npc.world.isRemote)
			packet.addTask(new AnimationTask.AnimationInfo(npc,ani));
			*/
	}

	public static void stopAnimation(ICustomNpc<EntityNPCInterface> npc){
		stopAnimation(npc.getMCEntity());
	}
	
	public static void stopAnimation(EntityNPCInterface npc) {
		java.util.Map<String,Object> tempdata = CommonEntityData.getData(npc);
		tempdata.remove("puppetAnimations");
		tempdata.remove("aniRunTicks");
		for(int part=0;part<6;part++) {
			IJobPuppetPart start = ((IJobPuppet) npc.jobInterface).getPart(part);
			IJobPuppetPart end = ((IJobPuppet) npc.jobInterface).getPart(part+6);
			if(start.getRotationX()!=end.getRotationX()||start.getRotationY()!=end.getRotationY()||start.getRotationZ()!=end.getRotationZ())
				start.setRotation(end.getRotationX(), end.getRotationY(), end.getRotationZ());
		}
		//*临时代码！
		//npc.updateClient();
		/*
		if(!npc.world.isRemote)
			packet.addTask(new AnimationTask.AnimationStop(npc));
			*/
	}

	public static void setPartUseAnimation(ICustomNpc<EntityNPCInterface> npc, int part, boolean isUseAnimation) {
		setPartUseAnimation(npc.getMCEntity(),part,isUseAnimation);
	}
	
	public static void setPartUseAnimation(EntityNPCInterface npc, int part, boolean isUseAnimation) {
		if(!(npc.jobInterface instanceof IJobPuppet))
			return;
		while(part>5)
			part -= 6;
		while(part<0)
			part += 6;
		((PartConfig)((IJobPuppet)npc.jobInterface).getPart(part)).disabled = !isUseAnimation;
		npc.updateClient = true;
		/*
		if(!npc.world.isRemote)
			packet.addTask(new AnimationTask.AnimationPartEnabled(npc,part,isUseAnimation));
			*/
	}

	public static boolean needManageAnimation(EntityNPCInterface npc){
		java.util.Map<String,Object> tempdata = CommonEntityData.getData(npc);
		if(!tempdata.containsKey("puppetAnimations")||!(tempdata.get("puppetAnimations") instanceof List)||!((List) tempdata.get("puppetAnimations")).isEmpty())
			return false;
		return true;
	}
	
	/*public static void setAutoMoveLeg(ICustomNpc<?> npc, boolean isAuto) {
		npc.getTempdata().put("puppetAutoLeg", isAuto);
	}
	
	public static boolean isAutoMoveLeg(ICustomNpc<?> npc) {
		if(!npc.getTempdata().has("puppetAutoLeg"))
			return false;
		return (Boolean) npc.getTempdata().get("puppetAutoLeg");
	}*/
	
}
