package saltsheep.ssl.event.script;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraftforge.fml.common.eventhandler.Event;
import noppes.npcs.api.entity.IEntity;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.entity.EntityProjectile;

public abstract class SheepEntityCommonEvent extends SheepForgeEvent {
	
	public final IEntity<?> entity;
	private final Entity entityIn;
	
	public SheepEntityCommonEvent(net.minecraftforge.event.entity.EntityEvent event) {
		super((Event) event);
		this.entityIn = event.getEntity();
		this.entity = Helper.getIEntity(event.getEntity());
	}
	
	public boolean isPlayer() {
		return this.entityIn instanceof EntityPlayer;
	}
	
	public boolean isNpc() {
		return this.entityIn instanceof EntityNPCInterface;
	}
	
	public boolean isLiving() {
		return this.entityIn instanceof EntityLivingBase;
	}
	
	public boolean isEntityItem() {
		return this.entityIn instanceof EntityItem;
	}
	
	public boolean isArrow() {
		return this.entityIn instanceof EntityArrow;
	}
	
	public boolean isNpcProjectile() {
		return this.entityIn instanceof EntityProjectile;
	}
	
	public boolean isThrowable() {
		return this.entityIn instanceof EntityThrowable;
	}

}
