package saltsheep.ssl.api;

import com.google.common.collect.Lists;
import noppes.npcs.api.entity.IEntity;
import saltsheep.ssl.SheepScriptLib;
import saltsheep.ssl.SheepScriptLibConfig;

import javax.annotation.Nullable;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.util.LinkedList;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


public class SheepAI {
    private static final ThreadPoolExecutor threadPool = new ThreadPoolExecutor(SheepScriptLibConfig.sheepAI_CoreThread, 2147483647, 1L, TimeUnit.SECONDS, new SynchronousQueue<>());

    protected IEntity<?> entity;

    protected ShouldRun shouldRun;

    protected EntityRun run;
    @Nullable
    protected EndedRun endedRun;
    AIState state = new AIState();
    LinkedList<Runnable> works = Lists.newLinkedList();
    private final AIRunnable aiRunnable;

    public SheepAI(IEntity<?> entity, ShouldRun shouldRun, EntityRun run) {
        this.entity = entity;
        this.shouldRun = shouldRun;
        this.run = run;
        this.aiRunnable = new AIRunnable(this);
    }

    public SheepAI(IEntity<?> entity, ShouldRun shouldRun, EntityRun run, EndedRun endedRun) {
        this(entity, shouldRun, run);
        this.endedRun = endedRun;
    }

    public SheepAI(IEntity<?> entity, ShouldRun shouldRun, EntityRun run, EndedRun endedRun, boolean canStopDefault) {
        this(entity, shouldRun, run, endedRun);
        this.state.canStopDefault = canStopDefault;
    }

    public SheepAI(IEntity<?> entity, ShouldRun shouldRun, EntityRun run, boolean canStopDefault) {
        this(entity, shouldRun, run);
        this.state.canStopDefault = canStopDefault;
    }

    @OfferToRuninner
    public void work(Runnable run) {
        if (!this.state.isRunning() || this.state.isStopping())
            return;
        if (!this.state.isInnerThread())
            return;
        this.works.add(() -> {
            try {
                run.run();
            } catch (Throwable error) {
                if (SheepScriptLibConfig.sheepAI_PrintErrorUse == 0) {
                    SheepScriptLib.printError(error);
                } else {
                    SheepScriptLib.sayError(error, this.entity);
                }
            }
        });
    }

    @OfferToRuninner
    public void waitTick(int tick) throws InterruptedException {
        if (!this.state.isRunning() || this.state.isStopping())
            return;
        if (!this.state.isInnerThread())
            return;
        if (tick <= 0) {
            return;
        }
        this.state.stopTick = tick;
        suspendRun();
    }

    @OfferToRuninner
    public void setCanStop(boolean canStop) {
        if (!this.state.isRunning())
            return;
        if (!this.state.isInnerThread())
            return;
        this.state.canStop = canStop;
    }


    @OfferToThird
    public void stopThread() {
        if (!this.state.isRunning())
            return;
        if (this.state.isInnerThread())
            return;
        untilWaitingOrEnded();

        try {
            if (this.state.canStop())
                this.state.markStopping();
        } catch (Throwable error) {
            error.printStackTrace();
        }
    }

    @ManageByHandler
    void stopThreadForced() {
        if (!this.state.isRunning())
            return;
        untilWaitingOrEnded();
        this.state.markStoppingForced();
        continueRun();
    }


    @ManageByHandler
    boolean apply() {
        try {
            if (this.state.isRunning() || !this.shouldRun.shouldRun(this.entity)) {
                return false;
            }
            this.state.reset();
            this.state.markStarted();
            threadPool.submit(this.aiRunnable);
            return true;
        } catch (Throwable error) {
            error.printStackTrace();
            return false;
        }
    }


    @ManageByHandler
    void untilWaitingOrEnded() {
        while (this.state.isRunning() && !this.state.isWaiting()) {
            try {
                Thread.sleep(0L, 10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @ManageByHandler
    void checkWait() {
        if (!this.state.isRunning())
            return;
        if (this.state.isWaiting()) {

            this.state.stopTick--;
            if (this.state.isWaitingEnd())
                continueRun();
        }
    }

    private void suspendRun() throws InterruptedException {
        if (this.state.isRunning() && !this.state.isWaiting())
            this.aiRunnable.lock.wait();
    }

    @ManageByHandler
    void continueRun() {
        if (this.state.isRunning() && this.state.isWaiting())
            synchronized (this.aiRunnable.lock) {
                this.aiRunnable.lock.notify();
            }
    }

    public interface EndedRun {
        void endedRun(IEntity<?> param1IEntity);
    }

    public interface ShouldRun {
        boolean shouldRun(IEntity<?> param1IEntity);
    }

    public interface EntityRun {
        void run(IEntity<?> param1IEntity);
    }

    @Target({ElementType.METHOD})
    private @interface OfferToRuninner {
    }

    @Target({ElementType.METHOD})
    private @interface OfferToThird {
    }

    @Target({ElementType.METHOD})
    private @interface ManageByHandler {
    }

    public static class AIState {
        private boolean isStarted = false;
        private boolean isEnded = false;
        private boolean isStopping = false;
        private int stopTick = -1;

        private boolean canStopDefault = true;
        private boolean canStop = true;
        private Thread currentThread = null;

        void reset() {
            this.isStarted = false;
            this.isEnded = false;
            this.isStopping = false;
            this.stopTick = -1;
            this.canStop = this.canStopDefault;
            this.currentThread = null;
        }

        private void markThread() {
            this.currentThread = Thread.currentThread();
        }

        private void markStarted() {
            this.isStarted = true;
        }

        private void markEnded() {
            this.isEnded = true;
        }

        private void markStopping() {
            this.isStopping = true;
        }

        private void markStoppingForced() {
            this.isStopping = true;
            this.stopTick = -2000;
        }

        boolean isRunning() {
            return (this.isStarted && !this.isEnded);
        }

        boolean isStopping() {
            return (this.isStarted && this.isStopping);
        }

        boolean isStoppingForced() {
            return (isStopping() && this.stopTick == -2000);
        }

        boolean canStop() {
            return this.canStop;
        }

        boolean hasMarkedThread() {
            return (this.currentThread != null);
        }

        boolean isWaiting() {
            return (hasMarkedThread() && this.currentThread.getState() == Thread.State.WAITING);
        }

        boolean isWaitingEnd() {
            return (this.stopTick <= 0);
        }

        boolean canWaitingInterruptedByStopping() {
            return (this.stopTick != -1000);
        }

        boolean isInnerThread() {
            return (Thread.currentThread() == this.currentThread);
        }
    }

    private static class AIRunnable
            implements Runnable {
        private final Object lock = new Object();
        private final SheepAI creator;

        public AIRunnable(SheepAI creator) {
            this.creator = creator;
        }

        public void run() {
            this.creator.state.markThread();
            try {
                synchronized (this.lock) {
                    this.creator.run.run(this.creator.entity);
                    if (this.creator.endedRun != null)
                        this.creator.works.add(() -> this.creator.endedRun.endedRun(this.creator.entity));
                    if (SheepScriptLibConfig.sheepAI_endedWait && !this.creator.state.isStoppingForced()) {
                        this.creator.state.stopTick = -1000;
                        this.lock.wait();
                    }
                }
            } catch (Throwable error) {
                if (SheepScriptLibConfig.sheepAI_PrintErrorUse == 0) {
                    SheepScriptLib.printError(error);
                } else {
                    SheepScriptLib.sayError(error, this.creator.entity);
                }
            }
            this.creator.state.markEnded();
        }
    }
}