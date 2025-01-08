package saltsheep.ssl.api;

import net.minecraft.entity.Entity;
import noppes.npcs.api.entity.IEntity;

import java.util.concurrent.Callable;

public abstract class Task
        implements Cloneable {
    public Task next = null;

    public Runnable stopRun = null;

    boolean isError = false;

    public static Task run(Runnable runnable) {
        return new TaskRunnable(runnable);
    }

    public static Task delay(int delayMax) {
        return new TaskDelay(delayMax);
    }

    public static Task custom(Runnable init, Callable<Boolean> invoke, Callable<Task> clone) {
        return new TaskCustom(init, invoke, clone);
    }

    public static Task repeat(Callable<Boolean> invoke, int period) {
        return new TaskRepeat(invoke, period);
    }

    public static Task repeatUnique(IEntity entity, String taskId, Callable<Boolean> invoke, int period) {
        return new TaskRepeatEntity(entity, taskId, invoke, period);
    }

    public static Task repeatUnique(Entity entity, String taskId, Callable<Boolean> invoke, int period) {
        return new TaskRepeatEntity(entity, taskId, invoke, period);
    }

    public void start() {
        TaskHandler.instance.appendChain(this);
    }

    public final Task append(Task next) {
        if (this.next != null) {
            this.next.append(next);
        } else {
            this.next = next;
        }
        return this;
    }

    public Task append(Runnable runnable) {
        return this.append(Task.run(runnable));
    }

    public Task append(int delayMax) {
        return this.append(Task.delay(delayMax));
    }

    public Task append(Runnable init, Callable<Boolean> invoke, Callable<Task> clone) {
        return this.append(Task.custom(init, invoke, clone));
    }

    public Task append(Callable<Boolean> invoke, int period) {
        return this.append(Task.repeat(invoke, period));
    }

    public Task append(IEntity entity, String taskId, Callable<Boolean> invoke, int period) {
        return this.append(Task.repeatUnique(entity, taskId, invoke, period));
    }

    public Task append(Entity entity, String taskId, Callable<Boolean> invoke, int period) {
        return this.append(Task.repeatUnique(entity, taskId, invoke, period));
    }

    public final Task setStopRun(Runnable stopRun) {
        this.stopRun = stopRun;
        return this;
    }

    public boolean runTick() {
        if (this.isError)
            return true;
        try {
            return invoke();
        } catch (Exception e) {
            e.printStackTrace();
            this.isError = true;
            Task next = this.next;
            while (next != null) {
                next.isError = true;
                next = next.next;
            }

            return true;
        }
    }


    public void init() {
        this.isError = false;
    }


    public abstract boolean invoke() throws Exception;


    public abstract Task clone();


    static class TaskRunnable
            extends Task {
        final Runnable runIn;

        TaskRunnable(Runnable runnable) {
            this.runIn = runnable;
        }


        public boolean invoke() {
            this.runIn.run();
            return true;
        }


        public Task clone() {
            Task main = new TaskRunnable(this.runIn);
            if (this.next != null)
                main.next = this.next.clone();
            return main;
        }
    }

    static class TaskDelay
            extends Task {
        final int delayMax;
        int delayGone = -1;

        TaskDelay(int delayMax) {
            this.delayMax = delayMax;
        }


        public void init() {
            this.delayGone = -1;
        }


        public boolean invoke() {
            return (++this.delayGone >= this.delayMax);
        }


        public Task clone() {
            Task main = new TaskDelay(this.delayMax);
            if (this.next != null)
                main.next = this.next.clone();
            return main;
        }
    }

    static class TaskRepeat
            extends Task {
        final Callable<Boolean> invoke;
        final int period;
        int timeGone = -1;

        TaskRepeat(Callable<Boolean> invoke, int period) {
            this.invoke = invoke;
            this.period = Math.max(1, period);
        }


        public void init() {
            this.timeGone = -1;
        }


        public boolean invoke() throws Exception {
            if (++this.timeGone >= this.period) {
                this.timeGone = 0;
                return this.invoke.call().booleanValue();
            }
            return false;
        }


        public Task clone() {
            Task main = new TaskRepeat(this.invoke, this.period);
            if (this.next != null)
                main.next = this.next.clone();
            return main;
        }
    }

    static class TaskRepeatEntity
            extends TaskRepeat {
        final Entity entity;
        final String id;
        boolean isWorking = false;

        TaskRepeatEntity(IEntity entity, String id, Callable<Boolean> invoke, int period) {
            this(entity.getMCEntity(), id, invoke, period);
        }

        TaskRepeatEntity(Entity entity, String id, Callable<Boolean> invoke, int period) {
            super(invoke, period);
            this.entity = entity;
            this.id = id;
        }


        public void init() {
            super.init();
            TaskRepeatEntity last = (TaskRepeatEntity) CommonEntityData.getData(this.entity).get("TaskUnique:" + this.id);
            if (last != null)
                last.isWorking = false;
            this.isWorking = true;
            CommonEntityData.getData(this.entity).put("TaskUnique:" + this.id, this);
        }


        public boolean invoke() throws Exception {
            boolean flag = (!this.isWorking || !this.entity.isEntityAlive() || !this.entity.isAddedToWorld() || super.invoke());
            if (flag) {
                this.isWorking = false;
                return true;
            }
            return false;
        }


        public Task clone() {
            Task main = new TaskRepeatEntity(this.entity, this.id, this.invoke, this.period);
            if (this.next != null)
                main.next = this.next.clone();
            return main;
        }
    }

    static class TaskCustom
            extends Task {
        final Runnable init;
        final Callable<Boolean> invoke;
        final Callable<Task> clone;

        TaskCustom(Runnable init, Callable<Boolean> invoke, Callable<Task> clone) {
            this.init = init;
            this.invoke = invoke;
            this.clone = clone;
        }


        public void init() {
            if (this.init != null) {
                this.init.run();
            }
        }

        public boolean invoke() throws Exception {
            return this.invoke.call().booleanValue();
        }


        public Task clone() {
            try {
                return this.clone.call();
            } catch (Exception e) {
                e.printStackTrace();

                return null;
            }
        }
    }
}