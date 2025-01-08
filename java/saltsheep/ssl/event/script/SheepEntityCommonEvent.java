package saltsheep.ssl.event.script;

import net.minecraft.entity.Entity;
import noppes.npcs.api.entity.IEntity;

public abstract class SheepEntityCommonEvent
        extends SheepForgeEvent {
    public final IEntity<?> entity;
    private final Entity entityIn;

    public SheepEntityCommonEvent(net.minecraftforge.event.entity.EntityEvent event) {
        super(event);
        this.entityIn = event.getEntity();
        this.entity = Helper.getIEntity(event.getEntity());
    }

    public boolean isPlayer() {
        return this.entityIn instanceof net.minecraft.entity.player.EntityPlayer;
    }

    public boolean isNpc() {
        return this.entityIn instanceof noppes.npcs.entity.EntityNPCInterface;
    }

    public boolean isLiving() {
        return this.entityIn instanceof net.minecraft.entity.EntityLivingBase;
    }

    public boolean isEntityItem() {
        return this.entityIn instanceof net.minecraft.entity.item.EntityItem;
    }

    public boolean isArrow() {
        return this.entityIn instanceof net.minecraft.entity.projectile.EntityArrow;
    }

    public boolean isNpcProjectile() {
        return this.entityIn instanceof noppes.npcs.entity.EntityProjectile;
    }

    public boolean isThrowable() {
        return this.entityIn instanceof net.minecraft.entity.projectile.EntityThrowable;
    }
}