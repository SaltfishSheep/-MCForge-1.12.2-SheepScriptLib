package saltsheep.ssl.event.script;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.WorldServer;
import noppes.npcs.api.IWorld;
import noppes.npcs.api.entity.ICustomNpc;
import noppes.npcs.api.entity.IEntity;
import noppes.npcs.api.entity.IPlayer;
import noppes.npcs.api.entity.IProjectile;
import noppes.npcs.api.wrapper.WrapperNpcAPI;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.entity.EntityProjectile;

public class Helper {

	public static IPlayer<?> getIPlayer(EntityPlayer player) {
		return (IPlayer<?>) WrapperNpcAPI.Instance().getIEntity(player);
	}
	
	public static ICustomNpc<?> getINpc(EntityNPCInterface npc){
		return (ICustomNpc<?>) WrapperNpcAPI.Instance().getIEntity(npc);
	}
	
	public static IProjectile<?> getIProjectile(EntityProjectile projectile){
		return (IProjectile<?>) WrapperNpcAPI.Instance().getIEntity(projectile);
	}
	
	public static IEntity<?> getIEntity(Entity entity){
		return WrapperNpcAPI.Instance().getIEntity(entity);
	}
	
	public static IWorld getIWorld(WorldServer world) {
		return WrapperNpcAPI.Instance().getIWorld(world);
	}
	
}
