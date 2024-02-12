package saltsheep.ssl.puppet.handler.tasks;

import saltsheep.ssl.puppet.handler.JobPuppetSSLData;

public abstract class TaskBaseTimer extends TaskBase {

    protected long startTime;
    protected long durationTime;

    public TaskBaseTimer(long durationTime){
        this.durationTime = durationTime;
    }

    @Override
    protected void onInit() {
        startTime = System.currentTimeMillis();
    }

    @Override
    public boolean canEnd(JobPuppetSSLData.RotationController controller) {
        if(startTime==0)
            return false;
        long nowTime = System.currentTimeMillis();
        return nowTime>=(startTime+durationTime);
    }

}
