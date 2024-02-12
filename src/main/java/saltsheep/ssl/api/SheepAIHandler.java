package saltsheep.ssl.api;

import com.google.common.collect.Lists;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import noppes.npcs.api.entity.IEntity;
import noppes.npcs.api.entity.IEntityLivingBase;
import saltsheep.ssl.api.wrapper.AIKeepDistance;
import saltsheep.ssl.common.GOCMap;

import java.util.*;
import java.util.Map.Entry;

public class SheepAIHandler {

	public static SheepAIHandler subscriber = new SheepAIHandler();

	private static GOCMap<Entity,GOCMap<String,List<SheepAI>>> aiGroups = new GOCMap<Entity,GOCMap<String,List<SheepAI>>>(
			()-> new GOCMap<String,List<SheepAI>>(
				()->new LinkedList<SheepAI>()
		)
	);

	private static GOCMap<Entity,Map<String,SheepAI>> aiGroupsRun = new GOCMap<Entity,Map<String,SheepAI>>(
			()->new HashMap<String,SheepAI>()
	);

	private static Set<SheepAI> stoppingAIs = new HashSet<>();

	public static void addAI(IEntity<?> entity, String group, SheepAI ai) {
		//checkGroup(entity,group);
		//((Map<String,List<SheepAI>>)entity.getTempdata().get("aiGroups")).get(group).add(ai);
		aiGroups.getOrCreate(entity.getMCEntity()).getOrCreate(group).add(ai);
	}

	/*
	@SuppressWarnings("unchecked")
	public static void checkGroup(IEntity<?> entity, String group) {
		if(!entity.getTempdata().has("aiGroups"))
			entity.getTempdata().put("aiGroups", new HashMap<String,List<SheepAI>>());
		Map<String,List<SheepAI>> groups = (Map<String, List<SheepAI>>) entity.getTempdata().get("aiGroups");
		if(!groups.containsKey(group))
			groups.put(group, new LinkedList<SheepAI>());
	}
	*/

	public static void stopAll(IEntity<?> entity) {
		Map<String, SheepAI> groupsRun = aiGroupsRun.get(entity.getMCEntity());
		if(groupsRun==null)
			return;
		for(SheepAI ai:groupsRun.values())
			if(ai.state.canStop())
				ai.stopThread();
	}
	
	public static void clearAI(IEntity<?> entity) {
		stopAll(entity);
		unloadEntityGroups(entity.getMCEntity());
		//entity.getTempdata().remove("aiGroups");
		//entity.getTempdata().remove("aiGroupsRun");
	}

	public static void resetOnServerStopping(){
		aiGroups.clear();
		for(Map<String,SheepAI> eachEntity:aiGroupsRun.values())
			for(SheepAI eachAI:eachEntity.values())
				eachAI.stopThreadForced();
		aiGroupsRun.clear();
	}

	@SubscribeEvent
	public void serverTick(TickEvent.ServerTickEvent event){
		if(event.phase!=Phase.START)
			return;
		updateEntitiesGroups();
		updateEntitiesRunningAIs();
	}

	private static void updateEntitiesGroups() {
		List<Entry<Entity,GOCMap<String,List<SheepAI>>>> list = new LinkedList<>(aiGroups.entrySet());
		for(Entry<Entity,GOCMap<String,List<SheepAI>>> entry:list){
			Entity entity = entry.getKey();
			//*在实体死后注销注册表，防止再次运行AI
			if(!entity.isAddedToWorld()||!entity.isEntityAlive()){
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
		/**
		 * 一组AI同一时间只有一个能够执行
		 * 这是为什么要使用continue group
		 */
		group:for (Entry<String, List<SheepAI>> group : groups.entrySet()) {
			//*确保同一时间只有一个AI能够执行
			if (groupsRun.containsKey(group.getKey()))
				continue group;
			/**
			 * 检查是否有新的，可以执行的AI
			 * 在检查睡眠之前，以保证此AI的运行在同一刻执行，这很重要
			 */
			for (SheepAI ai : group.getValue()) {
				if (ai.apply()) {
					groupsRun.put(group.getKey(), ai);
					continue group;
				}
			}
		}
	}

	private static void updateEntitiesRunningAIs() {
		List<Entry<Entity, Map<String, SheepAI>>> allEntitiesSet = new LinkedList<>(aiGroupsRun.entrySet());
		for(Entry<Entity, Map<String, SheepAI>> entry:allEntitiesSet){
			Entity entity = entry.getKey();
			Map<String,SheepAI> groupsRun = entry.getValue();
			updateEntityRunningAIsStopping(entity,groupsRun);
			updateEntityRunningAIs(entity,groupsRun);
			unloadEntityEndedAIs(entity,groupsRun);
		}
	}

	private static void updateEntityRunningAIsStopping(Entity entity, Map<String, SheepAI> groupsRun) {
		for(SheepAI ai:groupsRun.values()){
			/**
			 * 出现暂停有两种情况：
			 * 一：由其他地方已调用过stopThread
			 * 二：实体已移除，而AI仍在运行
			 * 此处处理的是第二种情况，而第二种情况同样有两种可能性：
			 * 一：可以暂停，立即暂停
			 * 二：不可以暂停，于是放仍其继续托管
			 * 此处处理的是第二种情况的第一种可能性
			 */
			if((!(entity.isEntityAlive()&&entity.isAddedToWorld()))&&ai.state.canStop())
				ai.stopThread();
				//groupsRun.remove(entryIn.getKey());
			/**
			 * 在上一级的处理中，ai接受到stopThread的命令后有两种情况
			 * 一：AI在暂停时，线程末尾已经没有额外的waitTick
			 * 二：AI在暂停时，线程末尾还存在有waitTick工作
			 * 对于第二种情况，应当释放线程，使其自然结束
			 */
			if(ai.state.isStopping()&&ai.state.isWaiting()&&ai.state.canWaitingInterruptedByStopping())
				ai.continueRun();
		}

	}

	private static void updateEntityRunningAIs(Entity entity, Map<String, SheepAI> groupsRun) {
		for(SheepAI ai: groupsRun.values()){
			/**
			 * 如果没有进入线程休眠状态，则等到线程添加完work进入休眠，或执行完毕
			 * 执行完任务后，计时器+1（即已经过的刻数）
			 * 假设waitTicks=1,那么在该刻的checkWait就会释放线程，而work会在下一刻运行
			 */
			ai.untilWaitingOrEnded();
			while (!ai.works.isEmpty())
				ai.works.pop().run();
			ai.checkWait();
		}
	}

	private static void unloadEntityEndedAIs(Entity entity, Map<String, SheepAI> groupsRun) {
		List<Entry<String,SheepAI>> entries = new LinkedList<>(groupsRun.entrySet());
		for(Entry<String,SheepAI> entry:entries){
			SheepAI ai = entry.getValue();
			if(!ai.state.isRunning()){
				ai.state.reset();
				groupsRun.remove(entry.getKey());
			}
		}
		//*若实体的AI全部完成，则删除map
		if(groupsRun.isEmpty())
			aiGroupsRun.remove(entity);
	}

	/*
	private static void stopAI(SheepAI ai){
		ai.stopThread();
		//stoppingAIs.add(ai);
	}
	*/
	
	public static SheepAI getKeepDistanceAI(IEntityLivingBase<?> entity, double normalDistance, double shieldDistance) {
		return new AIKeepDistance(entity,normalDistance,shieldDistance);
	}
	
}
