package saltsheep.ssl.api;

import net.minecraft.entity.Entity;
import net.minecraft.network.play.server.SPacketEntityTeleport;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import noppes.npcs.api.entity.IEntity;
import saltsheep.ssl.SheepScriptLib;

import javax.annotation.Nullable;

public class EntityHelper {
    public static final byte DamageCreative = 0;
    public static final byte BypassesArmor = 1;
    public static final byte DamageAbsolute = 2;
    public static final byte DifficultyScaled = 3;
    public static final byte Explosion = 4;
    public static final byte Fire = 5;
    public static final byte Magic = 6;
    public static final byte Projectile = 7;

    public static void updatePosAndRotation(IEntity<Entity> entity) {
        updatePosAndRotation(entity.getMCEntity());
    }

    public static void updatePosAndRotation(Entity entity) {
        SheepScriptLib.getMCServer().getPlayerList().sendPacketToAllPlayers(new SPacketEntityTeleport(entity));
    }

    public static void damage(@Nullable IEntity<Entity> from, @Nullable IEntity<Entity> target, float amount) {
        damage(from, target, amount, "mob");
    }

    public static void damage(@Nullable IEntity<Entity> from, @Nullable IEntity<Entity> target, float amount, String type) {
        damage(from, target, amount, type, new byte[0]);
    }

    public static void damage(@Nullable IEntity<Entity> from, @Nullable IEntity<Entity> target, float amount, String type, byte[] datas) {
        damage((from == null) ? null : from.getMCEntity(), (target == null) ? null : target.getMCEntity(), amount, type, datas);
    }

    public static void damage(@Nullable Entity from, Entity target, float amount) {
        damage(from, target, amount, "mob");
    }

    public static void damage(@Nullable Entity from, Entity target, float amount, String type) {
        damage(from, target, amount, type, new byte[0]);
    }

    public static void damage(@Nullable Entity from, Entity target, float amount, String type, byte[] datas) {
        DamageSource source = (from != null) ? new EntityDamageSource(type, from) : new DamageSource(type);
        handleData(source, datas);
        target.attackEntityFrom(source, amount);
    }

    private static void handleData(DamageSource source, byte[] datas) {
        for (byte data : datas) {
            switch (data) {
                case 0:
                    source.setDamageAllowedInCreativeMode();
                    break;
                case 1:
                    source.setDamageBypassesArmor();
                    break;
                case 2:
                    source.setDamageIsAbsolute();
                    break;
                case 3:
                    source.setDifficultyScaled();
                    break;
                case 4:
                    source.setExplosion();
                    break;
                case 5:
                    source.setFireDamage();
                    break;
                case 6:
                    source.setMagicDamage();
                    break;
                case 7:
                    source.setProjectile();
                    break;
            }
        }
    }
}