package saltsheep.ssl.puppet.handler;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.event.FMLServerStoppedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import noppes.npcs.roles.JobPuppet;
import saltsheep.ssl.puppet.network.SPacketAnimation;

public class NeoPuppetHandler {

    public static NeoPuppetHandler subscriber = new NeoPuppetHandler();

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event){
        if(event.phase != TickEvent.Phase.END)
            return;
        SPacketAnimation.handleCaches();
    }

    public void onServerStopped(FMLServerStoppedEvent event){
        SPacketAnimation.resetCaches();
    }

    public static boolean canReplaceRotation(JobPuppet puppet, JobPuppet.PartConfig part){
        JobPuppetSSLData data = ((IJobPuppetSSL)puppet).getSSLData();
        if(data==null)
            return false;
        return data.canReplaceRotation(part);
    }

    public static float getRotationXAgency(JobPuppet puppet, JobPuppet.PartConfig part){
        return ((IJobPuppetSSL)puppet).getSSLData().getRotationX(part);
    }

    public static float getRotationYAgency(JobPuppet puppet, JobPuppet.PartConfig part){
        return ((IJobPuppetSSL)puppet).getSSLData().getRotationY(part);
    }

    public static float getRotationZAgency(JobPuppet puppet, JobPuppet.PartConfig part){
        return ((IJobPuppetSSL)puppet).getSSLData().getRotationZ(part);
    }

}
