package saltsheep.ssl.api;

import net.minecraft.util.math.Vec3d;
import noppes.npcs.api.entity.IEntity;

public class SheepMathHelper {

	//*获取实体的Vec3d数据
	public static WrapperVec3d getVec3d(IEntity<?> entity) {
	    return new WrapperVec3d(entity.getMCEntity().getLookVec());
	}
	public static WrapperVec3d getVec3d(float pitch, float yaw) {
	    return new WrapperVec3d(Vec3d.fromPitchYaw(pitch, yaw));
	}
	public static class WrapperVec3d {
		private final Vec3d vec3dIn;
		public WrapperVec3d(Vec3d vec3dIn) {
			this.vec3dIn = vec3dIn;
		}
	    public double getX() {
	    	return this.vec3dIn.x;
	    }
	    public double getY() {
	    	return this.vec3dIn.y;
	    }
	    public double getZ() {
	    	return this.vec3dIn.z;
	    }
	    public WrapperVec3d rotatePitch(float pitch) {
	    	return new WrapperVec3d(this.vec3dIn.rotatePitch((float) (pitch / 180 * Math.PI)));
	    }
	    public WrapperVec3d rotateYaw(float yaw) {
	    	return new WrapperVec3d(this.vec3dIn.rotateYaw((float) (yaw / 180 * Math.PI)));
	    }
	}
	//*角度函数
	public static float getYaw(IEntity<?> from, IEntity<?> tar) {
	    double x = from.getX();
	    double z = from.getZ();
	    double xx = tar.getX() - x;
	    double xz = tar.getZ() - z;
	    double line = Math.sqrt(xx * xx + xz * xz);
	    double RO = 0;
	    if (xx <= 0) {
	        RO = (Math.acos(xz / line) / Math.PI) * 180;
	    } else {
	        RO = (2 - Math.acos(xz / line) / Math.PI) * 180;
	    }
	    return (float) Math.abs(RO);
	}
	public static float getPitch(IEntity<?> from, IEntity<?> tar) {
		double x = from.getX()-tar.getX();
		double z = from.getZ()-tar.getZ();
	    double distance = Math.sqrt(x*x+z*z);
	    double yOffset = from.getY() - tar.getY();
	    return (float) (Math.asin(yOffset / distance) / Math.PI * 180);
	}
	//*是否在眼前矩阵
	public static boolean isInRange(IEntity<?> from,IEntity<?> tar,double length,double width) {
	    double x = from.getX();
	    double z = from.getZ();
	    double xx = tar.getX() - x;
	    double xz = tar.getZ() - z;
	    double line = Math.sqrt(xx * xx + xz * xz);
	    double RO = from.getRotation();
	    double ROX = getYaw(tar, from);
	    RO = Math.PI / 180 * (RO - ROX);
	    double len = line * Math.cos(RO);
	    double wid = Math.abs(line * Math.sin(RO));
	    return wid < width && len < length && len > 0;
	}
	//*是否在眼前扇形，rotation扇形角度（0-360）
	//*如果在，返回rotationOffset(>=0)，如果不在，返回-1
	//*目标在右侧时值大，在左侧时值小
	public static float isInFan(IEntity<?> from, IEntity<?> tar, float rotation) {
	    float rotateOffset = from.getRotation() - getYaw(from, tar);
	    while (rotateOffset >= 360)
	        rotateOffset -= 360;
	    while (rotateOffset < 0)
	        rotateOffset += 360;
	    float semiRotation = rotation / 2;
	    if (rotateOffset <= semiRotation || rotateOffset >= 360 - semiRotation)
	        return rotateOffset;
	    return -1;
	}
	
}
