package saltsheep.ssl.puppet.handler.tasks;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagLong;
import saltsheep.ssl.puppet.handler.JobPuppetSSLData;

import javax.annotation.Nullable;

public class TaskDelay extends TaskBaseTimer {

    public TaskDelay(long delayTime){
        super(delayTime);
    }

    @Override
    public void onAttended(JobPuppetSSLData.RotationController controller) {
    }

    @Override
    public float getCurrentX(JobPuppetSSLData.RotationController controller) {
        return controller.xStart;
    }

    @Override
    public float getCurrentY(JobPuppetSSLData.RotationController controller) {
        return controller.yStart;
    }

    @Override
    public float getCurrentZ(JobPuppetSSLData.RotationController controller) {
        return controller.zStart;
    }

    @Override
    public JobPuppetSSLData.IAnimationTaskLoader getLoader() {
        return Loader.INSTANCE;
    }

    public static class Loader implements JobPuppetSSLData.IAnimationTaskLoader {

        public final static JobPuppetSSLData.IAnimationTaskLoader INSTANCE = new Loader();

        private Loader(){}

        @Override
        public int id() {
            return 1;
        }

        @Override
        public JobPuppetSSLData.IAnimationTask read(NBTBase taskData) {
            long delay = ((NBTTagLong)taskData).getLong();
            return new TaskDelay(delay);
        }

        @Override
        @Nullable
        public NBTBase write(JobPuppetSSLData.IAnimationTask task) {
            NBTTagLong tag = new NBTTagLong(((TaskDelay)task).durationTime);
            return tag;
        }

    }

}
