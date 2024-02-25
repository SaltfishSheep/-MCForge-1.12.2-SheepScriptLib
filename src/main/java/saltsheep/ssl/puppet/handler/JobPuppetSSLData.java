package saltsheep.ssl.puppet.handler;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.roles.JobPuppet;

import javax.annotation.Nullable;
import java.util.*;

public class JobPuppetSSLData {

    Map<JobPuppet.PartConfig,RotationController> parts = new HashMap<>();

    public void appendTask(JobPuppet.PartConfig part, IAnimationTask task){
        RotationController controller = parts.get(part);
        if(controller==null){
            controller = new RotationController();
            parts.put(part,controller);
        }
        controller.appendTask(task);
    }

    public RotationController getPart(JobPuppet owner, int part){
        return this.getPart((JobPuppet.PartConfig) owner.getPart(part));
    }

    public RotationController getPart(JobPuppet.PartConfig partConfig){
        RotationController controller = parts.get(partConfig);
        if(controller==null){
            controller = new RotationController();
            parts.put(partConfig,controller);
        }
        return controller;
    }

    /**
     * Happening before getRotation.
     */
    public boolean canReplaceRotation(JobPuppet.PartConfig part){
        return (!part.disabled)&&parts.containsKey(part);
    }

    public float getRotationX(JobPuppet.PartConfig part){
        return parts.get(part).getRotationX();
    }

    public float getRotationY(JobPuppet.PartConfig part){
        return parts.get(part).getRotationY();
    }

    public float getRotationZ(JobPuppet.PartConfig part){
        return parts.get(part).getRotationZ();
    }

    public class RotationController{

        public float xStart = 0f;
        public float yStart = 0f;
        public float zStart = 0f;
        public Queue<IAnimationTask> tasks = new LinkedList<>();

        public void appendTask(IAnimationTask task){
            tasks.offer(task);
            task.onAttended(this);
        }

        public void appendTasks(List<IAnimationTask> tasks){
            for(IAnimationTask task:tasks){
                appendTask(task);
            }
        }

        private IAnimationTask get(){
            IAnimationTask first = tasks.peek();
            while (first!=null&&first.canEnd(this)){
                //System.out.println(first+"canEnd");
                tasks.poll();
                first.onEnded(this);
                first = tasks.peek();
            }
            if(first!=null&&(!first.isInit()))
                first.init();
            return first;
        }

        public float getRotationX(){
            IAnimationTask first = get();
            if(first==null)
                return xStart;
            return first.getCurrentX(this);
        }

        public float getRotationY(){
            IAnimationTask first = get();
            if(first==null)
                return yStart;
            return first.getCurrentY(this);
        }

        public float getRotationZ(){
            IAnimationTask first = get();
            if(first==null)
                return zStart;
            return first.getCurrentZ(this);
        }


    }

    public static interface IAnimationTask {

        public abstract boolean isInit();

        public abstract void init();

        /**
         * Invoke after task was attended to tasks list.
         */
        public abstract void onAttended(RotationController controller);

        /**
         * Invoke after task was polled out of the tasks list.
         */
        public abstract void onEnded(RotationController controller);

        public abstract boolean canEnd(RotationController controller);

        public abstract float getCurrentX(RotationController controller);

        public abstract float getCurrentY(RotationController controller);

        public abstract float getCurrentZ(RotationController controller);

        public abstract IAnimationTaskLoader getLoader();

    }

    public static interface IAnimationTaskLoader {

        public abstract int id();

        public abstract IAnimationTask read(NBTBase taskData);

        @Nullable
        public abstract NBTBase write(IAnimationTask task);

    }

}
