package saltsheep.ssl.api;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.network.play.server.SPacketEntityTeleport;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import noppes.npcs.api.entity.IEntity;
import saltsheep.ssl.SheepScriptLib;

public class EntityHelper {
	
	public static void updatePosAndRotation(IEntity<Entity> entity) {
		updatePosAndRotation(entity.getMCEntity());
	}
	
	public static void updatePosAndRotation(Entity entity) {
		SheepScriptLib.getMCServer().getPlayerList().sendPacketToAllPlayers(new SPacketEntityTeleport(entity));
	}
	
	public static void damage(@Nullable IEntity<Entity> from,@Nullable IEntity<Entity> target, float amount) {
		damage(from,target,amount,"mob");
	}
	
	public static void damage(@Nullable IEntity<Entity> from,@Nullable IEntity<Entity> target, float amount, String type) {
		damage(from,target,amount,type,new byte[0]);
	}
	
	public static void damage(@Nullable IEntity<Entity> from,@Nullable IEntity<Entity> target, float amount, String type, byte[] datas) {
		damage(from==null?null:from.getMCEntity(),target==null?null:target.getMCEntity(),amount,type,datas);
	}
	
	public static void damage(@Nullable Entity from, Entity target, float amount) {
		damage(from,target,amount,"mob");
	}
	
	public static void damage(@Nullable Entity from, Entity target, float amount, String type) {
		damage(from,target,amount,type,new byte[0]);
	}
	
	public static void damage(@Nullable Entity from, Entity target, float amount, String type, byte[] datas) {
		DamageSource source = from!=null? new EntityDamageSource(type, from):new DamageSource(type);
		handleData(source, datas);
		target.attackEntityFrom(source, amount);
	}
	
	private static void handleData(DamageSource source, byte[] datas) {
		for(byte data:datas)
			switch(data) {
			case DamageCreative:
				source.setDamageAllowedInCreativeMode();
				break;
			case BypassesArmor:
				source.setDamageBypassesArmor();
				break;
			case DamageAbsolute:
				source.setDamageIsAbsolute();
				break;
			case DifficultyScaled:
				source.setDifficultyScaled();
				break;
			case Explosion:
				source.setExplosion();
				break;
			case Fire:
				source.setFireDamage();
				break;
			case Magic:
				source.setMagicDamage();
				break;
			case Projectile:
				source.setProjectile();
				break;
			}
	}
	
	public static final byte DamageCreative = 0;
	public static final byte BypassesArmor = 1;
	public static final byte DamageAbsolute = 2;
	public static final byte DifficultyScaled = 3;
	public static final byte Explosion = 4;
	public static final byte Fire = 5;
	public static final byte Magic = 6;
	public static final byte Projectile = 7;

}
