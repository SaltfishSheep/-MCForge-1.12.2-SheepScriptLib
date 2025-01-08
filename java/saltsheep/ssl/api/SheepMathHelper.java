package saltsheep.ssl.api;

import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import noppes.npcs.api.entity.IEntity;


public class SheepMathHelper {
    public static WrapperVec3d getVec3d(IEntity<?> entity) {
        return new WrapperVec3d(entity.getMCEntity().getLookVec());
    }

    public static WrapperVec3d getVec3d(float pitch, float yaw) {
        return new WrapperVec3d(Vec3d.fromPitchYaw(pitch, yaw));
    }

    public static float getYaw(IEntity<?> from, IEntity<?> tar) {
        double x = from.getX();
        double z = from.getZ();
        double xx = tar.getX() - x;
        double xz = tar.getZ() - z;
        double line = Math.sqrt(xx * xx + xz * xz);
        double RO = 0.0D;
        if (xx <= 0.0D) {
            RO = Math.acos(xz / line) / Math.PI * 180.0D;
        } else {
            RO = (2.0D - Math.acos(xz / line) / Math.PI) * 180.0D;
        }
        return (float) Math.abs(RO);
    }

    public static float getPitch(IEntity<?> from, IEntity<?> tar) {
        double x = from.getX() - tar.getX();
        double z = from.getZ() - tar.getZ();
        double distance = Math.sqrt(x * x + z * z);
        double yOffset = from.getY() - tar.getY();
        return (float) (Math.asin(yOffset / distance) / Math.PI * 180.0D);
    }

    public static boolean isInRange(IEntity<?> from, IEntity<?> tar, double length, double width) {
        WrapperVec3d forward = SheepMathHelper.getVec3d(0, from.getRotation());
        WrapperVec3d side = SheepMathHelper.getVec3d(0, from.getRotation() + 90);
        double[] vec = {tar.getX() - from.getX(), tar.getY() - from.getY(), tar.getZ() - from.getZ()};
        double forwardLen = forward.getX() * vec[0] + forward.getZ() * vec[2];
        double sideLen = Math.abs(side.getX() * vec[0] + side.getZ() * vec[2]);
        return forwardLen >= 0 && forwardLen <= length && sideLen <= width * 0.5;
    }

    public static float isInFan(IEntity<?> from, IEntity<?> tar, float rotation) {
        float rotateOffset = from.getRotation() - getYaw(from, tar);
        while (rotateOffset >= 360.0F)
            rotateOffset -= 360.0F;
        while (rotateOffset < 0.0F)
            rotateOffset += 360.0F;
        float semiRotation = rotation / 2.0F;
        if (rotateOffset <= semiRotation || rotateOffset >= 360.0F - semiRotation)
            return rotateOffset;
        return -1.0F;
    }

    public static boolean isInRadius(IEntity<?> from, IEntity<?> tar, double radius) {
        double xOff = from.getX() - tar.getX(), yOff = from.getY() - tar.getY(), zOff = from.getZ() - tar.getZ();
        double distance2 = xOff * xOff + yOff * yOff + zOff * zOff;
        return (distance2 <= radius * radius);
    }

    public static float sin(float num) {
        return MathHelper.sin(num);
    }

    public static float cos(float num) {
        return MathHelper.cos(num);
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
            return new WrapperVec3d(this.vec3dIn.rotatePitch((float) ((pitch / 180.0F) * Math.PI)));
        }

        public WrapperVec3d rotateYaw(float yaw) {
            return new WrapperVec3d(this.vec3dIn.rotateYaw((float) ((yaw / 180.0F) * Math.PI)));
        }
    }
}