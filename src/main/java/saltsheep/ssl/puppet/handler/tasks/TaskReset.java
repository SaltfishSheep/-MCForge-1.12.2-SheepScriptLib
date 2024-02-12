package saltsheep.ssl.puppet.handler.tasks;

import net.minecraft.nbt.NBTBase;
import saltsheep.ssl.puppet.handler.JobPuppetSSLData;

import javax.annotation.Nullable;

public class TaskReset extends TaskClear {

    @Override
    public void onEnded(JobPuppetSSLData.RotationController controller) {
        controller.xStart = 0f;
        controller.yStart = 0f;
        controller.zStart = 0f;
    }

    @Override
    public float getCurrentX(JobPuppetSSLData.RotationController controller) {
        return 0f;
    }

    @Override
    public float getCurrentY(JobPuppetSSLData.RotationController controller) {
        return 0f;
    }

    @Override
    public float getCurrentZ(JobPuppetSSLData.RotationController controller) {
        return 0f;
    }

    @Override
    public JobPuppetSSLData.IAnimationTaskLoader getLoader() {
        return Loader.INSTANCE;
    }

    public static class Loader implements JobPuppetSSLData.IAnimationTaskLoader{

        public static final JobPuppetSSLData.IAnimationTaskLoader INSTANCE = new Loader();

        @Override
        public int id() {
            return 3;
        }

        @Override
        public JobPuppetSSLData.IAnimationTask read(NBTBase taskData) {
            return new TaskReset();
        }

        @Nullable
        @Override
        public NBTBase write(JobPuppetSSLData.IAnimationTask task) {
            return null;
        }
    }

}
