package saltsheep.ssl.puppet.handler.tasks;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagLong;
import saltsheep.ssl.puppet.handler.JobPuppetSSLData;

import javax.annotation.Nullable;

public class TaskDelay
        extends TaskBaseTimer {
    public TaskDelay(long delayTime) {
        super(delayTime);
    }


    public void onAttended(JobPuppetSSLData.RotationController controller) {
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

    public static class Loader
            implements JobPuppetSSLData.IAnimationTaskLoader {
        public static final JobPuppetSSLData.IAnimationTaskLoader INSTANCE = new Loader();

        public int id() {
            return 1;
        }

        public JobPuppetSSLData.IAnimationTask read(NBTBase taskData) {
            long delay = ((NBTTagLong) taskData).getLong();
            return new TaskDelay(delay);
        }

        @Nullable
        public NBTBase write(JobPuppetSSLData.IAnimationTask task) {
            NBTTagLong tag = new NBTTagLong(((TaskDelay) task).durationTime);
            return tag;
        }
    }
}
