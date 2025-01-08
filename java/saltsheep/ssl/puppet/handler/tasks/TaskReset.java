package saltsheep.ssl.puppet.handler.tasks;

import net.minecraft.nbt.NBTBase;
import saltsheep.ssl.puppet.handler.JobPuppetSSLData;

import javax.annotation.Nullable;


public class TaskReset
        extends TaskClear {
    public void onEnded(JobPuppetSSLData.RotationController controller) {
        controller.xStart = 0.0F;
        controller.yStart = 0.0F;
        controller.zStart = 0.0F;
    }


    public float getCurrentX(JobPuppetSSLData.RotationController controller) {
        return 0.0F;
    }


    public float getCurrentY(JobPuppetSSLData.RotationController controller) {
        return 0.0F;
    }


    public float getCurrentZ(JobPuppetSSLData.RotationController controller) {
        return 0.0F;
    }


    public JobPuppetSSLData.IAnimationTaskLoader getLoader() {
        return Loader.INSTANCE;
    }

    public static class Loader
            implements JobPuppetSSLData.IAnimationTaskLoader {
        public static final JobPuppetSSLData.IAnimationTaskLoader INSTANCE = new Loader();


        public int id() {
            return 3;
        }


        public JobPuppetSSLData.IAnimationTask read(NBTBase taskData) {
            return new TaskReset();
        }


        @Nullable
        public NBTBase write(JobPuppetSSLData.IAnimationTask task) {
            return null;
        }
    }
}