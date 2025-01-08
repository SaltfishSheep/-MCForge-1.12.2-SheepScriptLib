package saltsheep.ssl.puppet.handler.tasks;

import net.minecraft.nbt.NBTBase;
import saltsheep.ssl.puppet.handler.JobPuppetSSLData;
import saltsheep.ssl.puppet.network.SPacketAnimation;

import javax.annotation.Nullable;


public class TaskClear
        extends TaskBase
        implements SPacketAnimation.IAnimationPacketAddition {
    protected void onInit() {
    }

    public void onAttended(JobPuppetSSLData.RotationController controller) {
        JobPuppetSSLData.IAnimationTask task = controller.tasks.poll();
        while (task != null) {
            task.onEnded(controller);
            task = controller.tasks.poll();
        }
    }


    public void onEnded(JobPuppetSSLData.RotationController controller) {
    }


    public boolean canEnd(JobPuppetSSLData.RotationController controller) {
        return true;
    }


    public float getCurrentX(JobPuppetSSLData.RotationController controller) {
        return controller.xStart;
    }


    public float getCurrentY(JobPuppetSSLData.RotationController controller) {
        return controller.yStart;
    }


    public float getCurrentZ(JobPuppetSSLData.RotationController controller) {
        return controller.zStart;
    }


    public JobPuppetSSLData.IAnimationTaskLoader getLoader() {
        return Loader.INSTANCE;
    }


    public void beforePacketAttended(SPacketAnimation packet, int part) {
        packet.getPartTasks(part).clear();
    }


    public void afterPacketAttended(SPacketAnimation packet, int part) {
    }

    public static class Loader
            implements JobPuppetSSLData.IAnimationTaskLoader {
        public static final JobPuppetSSLData.IAnimationTaskLoader INSTANCE = new Loader();


        public int id() {
            return 0;
        }


        public JobPuppetSSLData.IAnimationTask read(NBTBase taskData) {
            return new TaskClear();
        }


        @Nullable
        public NBTBase write(JobPuppetSSLData.IAnimationTask task) {
            return null;
        }
    }
}