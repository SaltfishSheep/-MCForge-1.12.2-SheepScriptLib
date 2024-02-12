package saltsheep.ssl.api;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import noppes.npcs.api.IWorld;
import noppes.npcs.api.entity.IEntity;
import noppes.npcs.api.entity.IEntityLivingBase;
import noppes.npcs.api.entity.IProjectile;
import noppes.npcs.entity.EntityProjectile;
import saltsheep.ssl.event.script.Helper;

public class ProjectileHelper {
	
	public static IProjectile<?> shoot(IWorld world,@Nullable IEntityLivingBase<?> thrower, double x, double y, double z, String itemName, float speed, float damage, float inaccuracy) {
		EntityLivingBase entity = thrower==null? null:thrower.getMCEntity();
		return Helper.getIProjectile(shoot(world.getMCWorld(),entity,x,y,z,itemName,speed,damage,inaccuracy));
	}

	public static EntityProjectile shoot(World world,@Nullable EntityLivingBase thrower, double x, double y, double z, String itemName, float speed, float damage, float inaccuracy) {
		EntityProjectile ep = new EntityProjectile(world, thrower, new ItemStack(Item.getByNameOrId(itemName)), false);
		ep.setPosition(x, y, z);
		ep.damage = damage;
		ep.setSpeed((int) speed);
		if(thrower!=null)
			ep.shoot(thrower, thrower.rotationPitch, thrower.rotationYaw, 0, speed, inaccuracy);
		else
			ep.shoot(x, y, z, speed, inaccuracy);
		world.spawnEntity(ep);
		return ep;
	}
	
	public static IProjectile<?> shoot(IWorld world,@Nonnull IEntityLivingBase<?> thrower, float pitch, float yaw, double x, double y, double z, String itemName, float speed, float damage, float inaccuracy) {
		return Helper.getIProjectile(shoot(world.getMCWorld(),thrower.getMCEntity(),pitch,yaw,x,y,z,itemName,speed,damage,inaccuracy));
	}
	
	public static EntityProjectile shoot(World world,@Nonnull EntityLivingBase thrower, float pitch, float yaw, double x, double y, double z, String itemName, float speed, float damage, float inaccuracy) {
		EntityProjectile ep = new EntityProjectile(world, thrower, new ItemStack(Item.getByNameOrId(itemName)), false);
		ep.setPosition(x, y, z);
		ep.damage = damage;
		ep.setSpeed((int) speed);
		ep.shoot(thrower, pitch, yaw, 0, speed, inaccuracy);
		world.spawnEntity(ep);
		return ep;
	}
	
	public static IProjectile<?> shootNoSpawn(IWorld world,@Nullable IEntityLivingBase<?> thrower, double x, double y, double z, String itemName, float speed, float damage, float inaccuracy) {
		EntityLivingBase entity = thrower==null? null:thrower.getMCEntity();
		return Helper.getIProjectile(shootNoSpawn(world.getMCWorld(),entity,x,y,z,itemName,speed,damage,inaccuracy));
	}
	
	public static EntityProjectile shootNoSpawn(World world,@Nullable EntityLivingBase thrower, double x, double y, double z, String itemName, float speed, float damage, float inaccuracy) {
		EntityProjectile ep = new EntityProjectile(world, thrower, new ItemStack(Item.getByNameOrId(itemName)), false);
		ep.setPosition(x, y, z);
		ep.damage = damage;
		ep.setSpeed((int) speed);
		if(thrower!=null)
			ep.shoot(thrower, thrower.rotationPitch, thrower.rotationYaw, 0, speed, inaccuracy);
		else
			ep.shoot(x, y, z, speed, inaccuracy);
		return ep;
	}
	
	public static IProjectile<?> shootNoSpawn(IWorld world,@Nonnull IEntityLivingBase<?> thrower, float pitch, float yaw, double x, double y, double z, String itemName, float speed, float damage, float inaccuracy) {
		return Helper.getIProjectile(shootNoSpawn(world.getMCWorld(),thrower.getMCEntity(),pitch,yaw,x,y,z,itemName,speed,damage,inaccuracy));
	}
	
	public static EntityProjectile shootNoSpawn(World world,@Nonnull EntityLivingBase thrower, float pitch, float yaw, double x, double y, double z, String itemName, float speed, float damage, float inaccuracy) {
		EntityProjectile ep = new EntityProjectile(world, thrower, new ItemStack(Item.getByNameOrId(itemName)), false);
		ep.setPosition(x, y, z);
		ep.damage = damage;
		ep.setSpeed((int) speed);
		ep.shoot(thrower, pitch, yaw, 0, speed, inaccuracy);
		world.spawnEntity(ep);
		return ep;
	}
	
	public static void spawn(IWorld world, IEntity<?> entity) {
		spawn(world.getMCWorld(),entity.getMCEntity());
	}
	
	public static void spawn(World world, Entity entity) {
		world.spawnEntity(entity);
	}
	
}
