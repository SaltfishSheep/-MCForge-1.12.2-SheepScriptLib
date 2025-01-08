package saltsheep.ssl.puppet.handler.tasks;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import saltsheep.ssl.puppet.handler.JobPuppetSSLData;

public abstract class TaskBaseTimer
        extends TaskBase {
    protected double startTime;
    protected long durationTime;

    public TaskBaseTimer(long durationTime) {
        this.durationTime = durationTime;
    }


    protected void onInit() {
        this.startTime = getTime();
    }


    public boolean canEnd(JobPuppetSSLData.RotationController controller) {
        if (this.startTime == 0.0D)
            return false;
        double nowTime = getTime();
        return (nowTime >= this.startTime + this.durationTime);
    }


    public void onEnded(JobPuppetSSLData.RotationController controller) {
        this.startTime = 0.0D;
    }

    @SideOnly(Side.CLIENT)
    public double getTime() {
        double timeAsTicks = Minecraft.getMinecraft().world.getTotalWorldTime() + (Minecraft.getMinecraft().getRenderPartialTicks() % 1.0F);
        return timeAsTicks * 50.0D;
    }
}