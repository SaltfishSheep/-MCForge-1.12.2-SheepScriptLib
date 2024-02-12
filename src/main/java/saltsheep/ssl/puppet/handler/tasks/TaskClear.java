package saltsheep.ssl.puppet.handler.tasks;

import net.minecraft.nbt.NBTBase;
import saltsheep.ssl.puppet.handler.JobPuppetSSLData;
import saltsheep.ssl.puppet.network.SPacketAnimation;

import javax.annotation.Nullable;

public class TaskClear extends TaskBase implements SPacketAnimation.IAnimationPacketAddition {

    @Override
    protected void onInit() {
    }

    @Override
    public void onAttended(JobPuppetSSLData.RotationController controller) {
        JobPuppetSSLData.IAnimationTask task = controller.tasks.poll();
        while(task!=null){
            task.onEnded(controller);
            task = controller.tasks.poll();
        }
    }

    @Override
    public void onEnded(JobPuppetSSLData.RotationController controller) {
    }

    @Override
    public boolean canEnd(JobPuppetSSLData.RotationController controller) {
        return true;
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

    @Override
    public void beforePacketAttended(SPacketAnimation packet, int part) {
        packet.getPartTasks(part).clear();
    }

    @Override
    public void afterPacketAttended(SPacketAnimation packet, int part) {
    }

    public static class Loader implements JobPuppetSSLData.IAnimationTaskLoader {

        public final static JobPuppetSSLData.IAnimationTaskLoader INSTANCE = new Loader();

        private Loader(){}

        @Override
        public int id() {
            return 0;
        }

        @Override
        public JobPuppetSSLData.IAnimationTask read(NBTBase taskData) {
            return new TaskClear();
        }

        @Override
        @Nullable
        public NBTBase write(JobPuppetSSLData.IAnimationTask task) {
            return null;
        }

    }

}
