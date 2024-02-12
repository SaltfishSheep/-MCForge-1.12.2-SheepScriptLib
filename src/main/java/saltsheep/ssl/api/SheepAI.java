package saltsheep.ssl.api;

import com.google.common.collect.Lists;
import noppes.npcs.api.entity.IEntity;
import saltsheep.ssl.SheepScriptLib;
import saltsheep.ssl.SheepScriptLibConfig;

import javax.annotation.Nullable;
import java.lang.Thread.State;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.util.LinkedList;
import java.util.concurrent.*;

public class SheepAI {

	//*线程池
	private static ThreadPoolExecutor threadPool = new ThreadPoolExecutor(SheepScriptLibConfig.sheepAI_CoreThread, Integer.MAX_VALUE,
			1L, TimeUnit.SECONDS,
			new SynchronousQueue<Runnable>());

	protected IEntity<?> entity;
	protected ShouldRun shouldRun;
	protected EntityRun run;
	@Nullable
	protected EndedRun endedRun;
	private AIRunnable aiRunnable;
	AIState state = new AIState();

	/**
	 * 由SheepAIHandler托管
	 * 在SheepAI内定义，在线程的执行状态添加，在线程的休眠状态处理
	 * 同时保证了高效和同步性
	 */
	LinkedList<Runnable> works = Lists.newLinkedList();

	public SheepAI(IEntity<?> entity,ShouldRun shouldRun,EntityRun run) {
		this.entity = entity;
		this.shouldRun = shouldRun;
		this.run = run;
		this.aiRunnable = new AIRunnable(this);
	}

	public SheepAI(IEntity<?> entity,ShouldRun shouldRun,EntityRun run,EndedRun endedRun){
		this(entity,shouldRun,run);
		this.endedRun = endedRun;
	}

	public SheepAI(IEntity<?> entity, ShouldRun shouldRun, EntityRun run, EndedRun endedRun, boolean canStopDefault){
		this(entity,shouldRun,run,endedRun);
		this.state.canStopDefault = canStopDefault;
	}

	public SheepAI(IEntity<?> entity, ShouldRun shouldRun, EntityRun run, boolean canStopDefault){
		this(entity,shouldRun,run);
		this.state.canStopDefault = canStopDefault;
	}

	@OfferToRuninner
	public void work(Runnable run) {
		if(!this.state.isRunning()||this.state.isStopping())
			return;
		if(!this.state.isInnerThread())
			return;
		this.works.add(()->{
			try {
				run.run();
			}catch (Throwable error){
				if(SheepScriptLibConfig.sheepAI_PrintErrorUse==0){
					SheepScriptLib.printError(error);
				}else{
					SheepScriptLib.sayError(error,this.entity);
				}
			}
		});
	}

	@OfferToRuninner
	public void waitTick(int tick) throws InterruptedException {
		if(!this.state.isRunning()||this.state.isStopping())
			return;
		if(!this.state.isInnerThread())
			return;
		if(tick<=0)
			return;
		//System.out.println("inputTicks:"+tick);
		this.state.stopTick = tick;
		this.suspendRun();
	}

	@OfferToRuninner
	public void setCanStop(boolean canStop) {
		if(!this.state.isRunning())
			return;
		if(!this.state.isInnerThread())
			return;
		this.state.canStop = canStop;
	}

	@OfferToThird
	/**
	 * 该方法工作的原理是：
	 * 标记状态进入暂停中，禁止继续布置work和waitTick工作
	 * 为此，必须等待线程暂停
	 */
	public void stopThread() {
		if(!this.state.isRunning())
			return;
		if(this.state.isInnerThread())
			return;
		untilWaitingOrEnded();
		try {
			//*要求canStop才能stop
			if(this.state.canStop())
				this.state.markStopping();
		}catch(Throwable error) {
			error.printStackTrace();
		}
	}

	@ManageByHandler
	void stopThreadForced(){
		if(!this.state.isRunning())
			return;
		untilWaitingOrEnded();
		this.state.markStoppingForced();
		this.continueRun();
	}

	@ManageByHandler
	boolean apply() {
		try {
			//*如果AI还没有被Handler重置状态，或者已经正在运行，或者不符合运行需求，直接返回
			if(this.state.isRunning()||!this.shouldRun.shouldRun(this.entity))
				return false;
			//*当没有正在运行的线程，且可以运行时
			this.state.reset();
			this.state.markStarted();
			threadPool.submit(this.aiRunnable);
			return true;
		}catch(Throwable error) {
			error.printStackTrace();
			return false;
		}
	}

	@ManageByHandler
	void untilWaitingOrEnded(){
		/**
		 * 确保线程的work是在当刻添加当刻执行，或者在stopThread之后静候结束
		 */
		while(this.state.isRunning()&&!this.state.isWaiting()) {
			try {
				Thread.sleep(0,10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	@ManageByHandler
	void checkWait() {
		if(!this.state.isRunning())
			return;
		if(this.state.isWaiting()) {
			//System.out.println("restTicks:"+this.stopTick);
			this.state.stopTick--;
			if (this.state.isWaitingEnd())
				continueRun();
		}
	}

	private void suspendRun() throws InterruptedException {
		if(this.state.isRunning()&&!this.state.isWaiting())
			this.aiRunnable.lock.wait();
	}

	@ManageByHandler
	void continueRun() {
		if(this.state.isRunning()&&this.state.isWaiting())
			synchronized (this.aiRunnable.lock){
				this.aiRunnable.lock.notify();
			}
	}

	public static interface EndedRun {
		public void endedRun(IEntity<?> entity);
	}

	public static interface ShouldRun{
		public boolean shouldRun(IEntity<?> entity);
	}

	public static interface EntityRun{
		public void run(IEntity<?> entity);
	}

	public static class AIState {

		private boolean isStarted = false;
		private boolean isEnded = false;
		private boolean isStopping = false;
		private int stopTick = -1;
		private boolean canStopDefault = true;
		private boolean canStop = true;

		private Thread currentThread = null;

		void reset(){
			this.isStarted = false;
			this.isEnded = false;
			this.isStopping = false;
			this.stopTick = -1;
			this.canStop = this.canStopDefault;
			this.currentThread = null;
		}

		private void markThread(){
			this.currentThread = Thread.currentThread();
		}

		private void markStarted(){
			isStarted = true;
		}

		private void markEnded(){
			isEnded = true;
		}

		private void markStopping(){
			isStopping = true;
		}

		private void markStoppingForced(){
			isStopping = true;
			stopTick = -2000;
		}

		boolean isRunning(){
			return isStarted&&!isEnded;
		}

		boolean isStopping(){
			return isStarted&&isStopping;
		}

		boolean isStoppingForced(){
			return isStopping()&&stopTick == -2000;
		}

		boolean canStop(){
			return canStop;
		}

		boolean hasMarkedThread(){
			return this.currentThread != null;
		}

		boolean isWaiting(){
			return hasMarkedThread()&&currentThread.getState()==State.WAITING;
		}

		boolean isWaitingEnd(){
			return stopTick<=0;
		}

		boolean canWaitingInterruptedByStopping(){
			return stopTick!=-1000;
		}

		boolean isInnerThread(){
			return Thread.currentThread()==this.currentThread;
		}

	}

	private static class AIRunnable implements Runnable {

		private final Object lock = new Object();
		private final SheepAI creator;
		public AIRunnable(SheepAI creator) {
			this.creator = creator;
		}
		@Override
		public void run() {
			this.creator.state.markThread();
			try {
				synchronized (lock) {
					this.creator.run.run(this.creator.entity);
					if(creator.endedRun!=null)
						this.creator.works.add(()->creator.endedRun.endedRun(creator.entity));
					if(SheepScriptLibConfig.sheepAI_endedWait&&!this.creator.state.isStoppingForced()) {
						this.creator.state.stopTick = -1000;
						lock.wait();
					}
				}
			}catch (Throwable error){
				if(SheepScriptLibConfig.sheepAI_PrintErrorUse==0){
					SheepScriptLib.printError(error);
				}else{
					SheepScriptLib.sayError(error,this.creator.entity);
				}
			}
			this.creator.state.markEnded();
		}

	}

	@Target(ElementType.METHOD)
	private static @interface OfferToRuninner{}

	@Target(ElementType.METHOD)
	private static @interface OfferToThird{}

	@Target(ElementType.METHOD)
	private static @interface ManageByHandler{}

}
