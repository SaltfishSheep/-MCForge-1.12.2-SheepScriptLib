package saltsheep.ssl.api;

import java.util.concurrent.Callable;

public abstract class Task implements Cloneable {

    public Task next = null;

    public Runnable stopRun = null;

    public static Task run(Runnable runnable){
        return new TaskRunnable(runnable);
    }

    public static Task delay(int delayMax){
        return new TaskDelay(delayMax);
    }

    public static Task custom(Runnable init, Callable<Boolean> invoke, Callable<Task> clone){
        return new TaskCustom(init,invoke,clone);
    }

    public final void start(){
        TaskHandler.appendChain(this);
    }

    /**
     * @param next The task will be appended to the tail of the task chain.
     * @return The task you input in.(The end of the task chain)
     */
    public final Task append(Task next){
        if(this.next!=null)
            this.next.append(next);
        else
            this.next = next;
        return next;
    }

    public final Task setStopRun(Runnable stopRun){
        this.stopRun = stopRun;
        return this;
    }

    /**
     * If the task is about to start, {@link TaskHandler} will invoke this method,
     * and then invoke its next's method, next and next, until there's no next one.
     */
    public abstract void init();

    /**
     * Invoked by {@link TaskHandler} each tick.
     * @return true if the task is finished, and then {@link TaskHandler} will move pointer to its next task.
     */
    public abstract boolean invoke();

    public abstract Task clone();

    static class TaskRunnable extends Task {

        final Runnable runIn;

        TaskRunnable(Runnable runnable){
            runIn = runnable;
        }

        @Override
        public void init(){
        }

        @Override
        public boolean invoke() {
            runIn.run();
            return true;
        }

        @Override
        public Task clone() {
            Task main = new TaskRunnable(runIn);
            if(next!=null)
                main.next = next.clone();
            return main;
        }

    }

    static class TaskDelay extends Task {

        final int delayMax;
        int delayGone = -1;

        TaskDelay(int delayMax){
            this.delayMax = delayMax;
        }

        @Override
        public void init() {
            delayGone = 0;
        }

        @Override
        public boolean invoke() {
            return (++delayGone)>=delayMax;
        }

        @Override
        public Task clone() {
            Task main = new TaskDelay(delayMax);
            if(next!=null)
                main.next = next.clone();
            return main;
        }

    }

    static class TaskCustom extends Task {

        final Runnable init;
        final Callable<Boolean> invoke;
        final Callable<Task> clone;

        TaskCustom(Runnable init, Callable<Boolean> invoke, Callable<Task> clone){
            this.init = init;
            this.invoke = invoke;
            this.clone = clone;
        }

        @Override
        public void init(){
            this.init.run();
        }

        @Override
        public boolean invoke() {
            try {
                return this.invoke.call();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        }

        @Override
        public Task clone() {
            try {
                return clone.call();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

    }

}
