package saltsheep.ssl.puppet.handler;

import net.minecraft.nbt.NBTBase;
import noppes.npcs.roles.JobPuppet;

import javax.annotation.Nullable;
import java.util.*;

public class JobPuppetSSLData {
    Map<JobPuppet.PartConfig, RotationController> parts = new HashMap<>();

    public void appendTask(JobPuppet.PartConfig part, IAnimationTask task) {
        RotationController controller = this.parts.get(part);
        if (controller == null) {
            controller = new RotationController();
            this.parts.put(part, controller);
        }
        controller.appendTask(task);
    }

    public RotationController getPart(JobPuppet owner, int part) {
        return getPart((JobPuppet.PartConfig) owner.getPart(part));
    }

    public RotationController getPart(JobPuppet.PartConfig partConfig) {
        RotationController controller = this.parts.get(partConfig);
        if (controller == null) {
            controller = new RotationController();
            this.parts.put(partConfig, controller);
        }
        return controller;
    }


    public boolean canReplaceRotation(JobPuppet.PartConfig part) {
        return (!part.disabled && this.parts.containsKey(part));
    }

    public float getRotationX(JobPuppet.PartConfig part) {
        RotationController controller = this.parts.get(part);
        return (controller != null) ? controller.getRotationX() : 0.0F;
    }

    public float getRotationY(JobPuppet.PartConfig part) {
        RotationController controller = this.parts.get(part);
        return (controller != null) ? controller.getRotationY() : 0.0F;
    }

    public float getRotationZ(JobPuppet.PartConfig part) {
        RotationController controller = this.parts.get(part);
        return (controller != null) ? controller.getRotationZ() : 0.0F;
    }

    public interface IAnimationTaskLoader {
        int id();

        JobPuppetSSLData.IAnimationTask read(NBTBase param1NBTBase);

        @Nullable
        NBTBase write(JobPuppetSSLData.IAnimationTask param1IAnimationTask);
    }

    public interface IAnimationTask {
        boolean isInit();

        void init();

        void onAttended(JobPuppetSSLData.RotationController param1RotationController);

        void onEnded(JobPuppetSSLData.RotationController param1RotationController);

        boolean canEnd(JobPuppetSSLData.RotationController param1RotationController);

        float getCurrentX(JobPuppetSSLData.RotationController param1RotationController);

        float getCurrentY(JobPuppetSSLData.RotationController param1RotationController);

        float getCurrentZ(JobPuppetSSLData.RotationController param1RotationController);

        JobPuppetSSLData.IAnimationTaskLoader getLoader();
    }

    public class RotationController {
        public float xStart = 0.0F;
        public float yStart = 0.0F;
        public float zStart = 0.0F;
        public Queue<JobPuppetSSLData.IAnimationTask> tasks = new LinkedList<>();

        public void appendTask(JobPuppetSSLData.IAnimationTask task) {
            this.tasks.offer(task);
            task.onAttended(this);
        }

        public void appendTasks(List<JobPuppetSSLData.IAnimationTask> tasks) {
            for (JobPuppetSSLData.IAnimationTask task : tasks) {
                appendTask(task);
            }
        }

        private JobPuppetSSLData.IAnimationTask get() {
            JobPuppetSSLData.IAnimationTask first = this.tasks.peek();
            while (first != null && first.canEnd(this)) {

                this.tasks.poll();
                first.onEnded(this);
                first = this.tasks.peek();
            }
            if (first != null && !first.isInit())
                first.init();
            return first;
        }

        public float getRotationX() {
            JobPuppetSSLData.IAnimationTask first = get();
            if (first == null)
                return this.xStart;
            return first.getCurrentX(this);
        }

        public float getRotationY() {
            JobPuppetSSLData.IAnimationTask first = get();
            if (first == null)
                return this.yStart;
            return first.getCurrentY(this);
        }

        public float getRotationZ() {
            JobPuppetSSLData.IAnimationTask first = get();
            if (first == null)
                return this.zStart;
            return first.getCurrentZ(this);
        }
    }

}