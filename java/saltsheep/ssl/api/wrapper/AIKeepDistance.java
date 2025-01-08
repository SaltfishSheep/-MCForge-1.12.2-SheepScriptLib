package saltsheep.ssl.api.wrapper;

import noppes.npcs.api.entity.IEntityLivingBase;
import saltsheep.ssl.api.SheepAI;

public class AIKeepDistance
        extends SheepAI {
    private final SheepAI.ShouldRun sr;
    private final SheepAI.EntityRun r;
    public boolean canKeep = true;
    public double normalDistance;
    public double shieldDistance;
    private IEntityLivingBase<?> target = null;

    public AIKeepDistance(IEntityLivingBase<?> entity, double normalDistance, double shieldDistance) {
        super(entity, null, null);
        this.sr = (entityIn -> {
            if (!this.canKeep) return false;
            IEntityLivingBase<?> living = (IEntityLivingBase) entityIn;
            boolean hasTarget = (living.getAttackTarget() != null && living.getAttackTarget().isAlive());
            if (!hasTarget) return false;
            double x = living.getX() - living.getAttackTarget().getX();
            double y = living.getY() - living.getAttackTarget().getY();
            double z = living.getZ() - living.getAttackTarget().getZ();
            double distance = Math.sqrt(x * x + y * y + z * z);
            if (distance > this.normalDistance && distance > this.shieldDistance)
                return false;
            this.target = living.getAttackTarget();
            return true;
        });
        this.r = (entityIn -> work(() -> {
        }));
        this.shouldRun = this.sr;
        this.run = this.r;
        this.normalDistance = normalDistance;
        this.shieldDistance = shieldDistance;
    }
}