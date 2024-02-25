package saltsheep.ssl.puppet.handler.tasks;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import saltsheep.ssl.puppet.handler.JobPuppetSSLData;

public abstract class TaskBaseTimer extends TaskBase {

    protected double startTime;
    protected long durationTime;

    public TaskBaseTimer(long durationTime){
        this.durationTime = durationTime;
    }

    @Override
    protected void onInit() {
        startTime = getTime();
    }

    @Override
    public boolean canEnd(JobPuppetSSLData.RotationController controller) {
        if(startTime==0)
            return false;
        double nowTime = getTime();
        return nowTime>=(startTime+durationTime);
    }

    @Override
    public void onEnded(JobPuppetSSLData.RotationController controller){
        startTime = 0;//*To avoid the case that someone want to reuse Task.
    }

    @SideOnly(Side.CLIENT)
    public double getTime(){
        double timeAsTicks = Minecraft.getMinecraft().world.getTotalWorldTime() + (Minecraft.getMinecraft().getRenderPartialTicks()%1)*1d;
        return timeAsTicks * 50;
    }

}
