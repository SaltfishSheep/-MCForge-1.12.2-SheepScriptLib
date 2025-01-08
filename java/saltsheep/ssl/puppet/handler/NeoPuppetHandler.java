package saltsheep.ssl.puppet.handler;

import net.minecraft.client.renderer.GlStateManager;
import noppes.npcs.client.renderer.RenderNPCInterface;
import noppes.npcs.entity.EntityCustomNpc;
import noppes.npcs.roles.JobPuppet;
import saltsheep.ssl.SheepScriptLibConfig;


public class NeoPuppetHandler {
    public static void applyRotationsAfter(RenderNPCInterface render, Object npc, float pt) {
        if (!SheepScriptLibConfig.neoPuppet_enable || !SheepScriptLibConfig.neoPuppet_modelRotateWithoutBody_enable)
            return;
        if (npc instanceof EntityCustomNpc) {
            if (((EntityCustomNpc) npc).advanced.job != 9)
                return;
            JobPuppet job = (JobPuppet) ((EntityCustomNpc) npc).jobInterface;
            if (job.isActive() && !job.body.disabled &&
                    canReplaceRotation(job)) {
                JobPuppet.PartConfig body = job.body;
                JobPuppet.PartConfig body2 = job.body2;
                GlStateManager.translate(0.0D, ((EntityCustomNpc) npc).height / 2.0D, 0.0D);
                GlStateManager.rotate(((IJobPuppetSSL) job).getSSLData().getRotationY(job.body) * 180.0F, 0.0F, 1.0F, 0.0F);
                GlStateManager.rotate(((IJobPuppetSSL) job).getSSLData().getRotationX(job.body) * 180.0F, 1.0F, 0.0F, 0.0F);
                GlStateManager.rotate(((IJobPuppetSSL) job).getSSLData().getRotationZ(job.body) * 180.0F, 0.0F, 0.0F, 1.0F);
                GlStateManager.translate(0.0D, -((EntityCustomNpc) npc).height / 2.0D, 0.0D);
            }
        }
    }

    public static boolean canReplaceRotation(JobPuppet puppet) {
        if (!SheepScriptLibConfig.neoPuppet_enable)
            return false;
        JobPuppetSSLData data = ((IJobPuppetSSL) puppet).getSSLData();
        return data != null;
    }

    public static float getRotationXAgency(JobPuppet puppet, JobPuppet.PartConfig part) {
        if (SheepScriptLibConfig.neoPuppet_modelRotateWithoutBody_enable && part == puppet.body)
            return 0.0F;
        return ((IJobPuppetSSL) puppet).getSSLData().getRotationX(part);
    }

    public static float getRotationYAgency(JobPuppet puppet, JobPuppet.PartConfig part) {
        if (SheepScriptLibConfig.neoPuppet_modelRotateWithoutBody_enable && part == puppet.body)
            return 0.0F;
        return ((IJobPuppetSSL) puppet).getSSLData().getRotationY(part);
    }

    public static float getRotationZAgency(JobPuppet puppet, JobPuppet.PartConfig part) {
        if (SheepScriptLibConfig.neoPuppet_modelRotateWithoutBody_enable && part == puppet.body)
            return 0.0F;
        return ((IJobPuppetSSL) puppet).getSSLData().getRotationZ(part);
    }
}