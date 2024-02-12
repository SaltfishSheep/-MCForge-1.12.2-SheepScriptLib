package saltsheep.ssl.api.wrapper;

import net.minecraft.entity.SharedMonsterAttributes;
import noppes.npcs.api.entity.IEntityLivingBase;
import saltsheep.ssl.api.SheepAI;
import saltsheep.ssl.api.SheepMathHelper;

public class AIKeepDistance extends SheepAI {
	
	public boolean canKeep = true;
	
	public double normalDistance;
	public double shieldDistance;
	private IEntityLivingBase<?> target = null;
	private final ShouldRun sr = entity->{
		if (!canKeep)
	        return false;
		IEntityLivingBase<?> living = (IEntityLivingBase<?>) entity;
	    boolean hasTarget = living.getAttackTarget() != null && living.getAttackTarget().isAlive();
	    if(!hasTarget)
	    	return false;
	    double x = living.getX()-living.getAttackTarget().getX();
	    double y = living.getY()-living.getAttackTarget().getY();
	    double z = living.getZ()-living.getAttackTarget().getZ();
	    double distance = Math.sqrt(x*x+y*y+z*z);
	    if (distance > normalDistance&&distance > shieldDistance)
	        return false;
	    target = living.getAttackTarget();
	    return true;
	};
	private final EntityRun r = entity->{
		work(()-> {
	        if (target.getType() == 1 | target.getType() == 2) {
	        	IEntityLivingBase<?> living = (IEntityLivingBase<?>) entity;
	            //*是否需要绕行（仅限持盾时会绕行）
	            float rotateOffset = SheepMathHelper.isInFan(target, entity, 180);
	            boolean needDetour = target.getMCEntity().isActiveItemStackBlocking() && rotateOffset != -1 && shieldDistance>=0;
	            double x = entity.getX()-target.getX();
	            double y = entity.getY()-target.getY();
	            double z = entity.getZ()-target.getZ();
	            double nowDistance = Math.sqrt(x*x+y*y+z*z);
	            double distanceMark = needDetour ? shieldDistance : normalDistance;
	            if (nowDistance > distanceMark)
	                return;
	            double speed = living.getMCEntity().getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getAttributeValue()*20;
	            double angle = entity.getRotation();
	            double anglex = Math.PI / 180 * (angle);
	            double nX = 0.03 * speed * Math.sin(anglex);
	            double nZ = -0.03 * speed * Math.cos(anglex);
	            if (needDetour) {
	                double walkRight = rotateOffset <= 90 ? -1 : 1;
	                double anglex1 = Math.PI / 180 * (angle + 90);
	                double nX1 = walkRight * 0.03 * speed * Math.sin(anglex1);
	                double nZ1 = walkRight * -0.03 * speed * Math.cos(anglex1);
	                nX += nX1;
	                nZ += nZ1;
	            }
	            entity.setMotionX(nX);
	            entity.setMotionZ(nZ);
	        }
	    });
	};

	public AIKeepDistance(IEntityLivingBase<?> entity, double normalDistance, double shieldDistance) {
		super(entity, null, null);
		this.shouldRun = sr;
		this.run = this.r;
		this.normalDistance = normalDistance;
		this.shieldDistance = shieldDistance;
	}

}
