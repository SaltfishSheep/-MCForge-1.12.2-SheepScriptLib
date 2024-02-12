package saltsheep.ssl.puppet.handler.tasks;

import saltsheep.ssl.puppet.handler.JobPuppetSSLData;

public abstract class TaskBase implements JobPuppetSSLData.IAnimationTask {

    private boolean isInit = false;

    public boolean isInit(){
        return isInit;
    }

    public void init(){
        onInit();
        isInit = true;
    }

    protected abstract void onInit();

}
