package saltsheep.ssl.api;

import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import noppes.npcs.api.entity.ICustomNpc;
import noppes.npcs.api.entity.data.role.IJobPuppet;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.roles.JobPuppet;
import saltsheep.ssl.event.EntityUpdateEvent;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Deprecated
public class AnimationHandler {
    public static AnimationHandler subscriber = new AnimationHandler();

    private static void checkAnimations(EntityNPCInterface npc) {
        Map<String, Object> tempdata = CommonEntityData.getData(npc);
        if (!tempdata.containsKey("puppetAnimations") || !(tempdata.get("puppetAnimations") instanceof List))
            tempdata.put("puppetAnimations", Collections.synchronizedList(new LinkedList()));
    }

    public static void addAnimation(ICustomNpc<EntityNPCInterface> npc, Animation ani) {
        addAnimation(npc.getMCEntity(), ani);
    }

    public static void addAnimation(EntityNPCInterface npc, Animation ani) {
        checkAnimations(npc);
        Map<String, Object> tempdata = CommonEntityData.getData(npc);
        ((List<Animation>) tempdata.get("puppetAnimations")).add(ani);
    }

    public static void stopAnimation(ICustomNpc<EntityNPCInterface> npc) {
        stopAnimation(npc.getMCEntity());
    }

    public static void stopAnimation(EntityNPCInterface npc) {
        Map<String, Object> tempdata = CommonEntityData.getData(npc);
        tempdata.remove("puppetAnimations");
        tempdata.remove("aniRunTicks");
        for (int part = 0; part < 6; part++) {
            IJobPuppet.IJobPuppetPart start = ((IJobPuppet) npc.jobInterface).getPart(part);
            IJobPuppet.IJobPuppetPart end = ((IJobPuppet) npc.jobInterface).getPart(part + 6);
            if (start.getRotationX() != end.getRotationX() || start.getRotationY() != end.getRotationY() || start.getRotationZ() != end.getRotationZ()) {
                start.setRotation(end.getRotationX(), end.getRotationY(), end.getRotationZ());
            }
        }
    }

    public static void setPartUseAnimation(ICustomNpc<EntityNPCInterface> npc, int part, boolean isUseAnimation) {
        setPartUseAnimation(npc.getMCEntity(), part, isUseAnimation);
    }

    public static void setPartUseAnimation(EntityNPCInterface npc, int part, boolean isUseAnimation) {
        if (!(npc.jobInterface instanceof IJobPuppet))
            return;
        while (part > 5)
            part -= 6;
        while (part < 0)
            part += 6;
        ((JobPuppet.PartConfig) ((IJobPuppet) npc.jobInterface).getPart(part)).disabled = !isUseAnimation;
        npc.updateClient = true;
    }

    public static boolean needManageAnimation(EntityNPCInterface npc) {
        Map<String, Object> tempdata = CommonEntityData.getData(npc);
        return tempdata.containsKey("puppetAnimations") && tempdata.get("puppetAnimations") instanceof List && ((List) tempdata.get("puppetAnimations")).isEmpty();
    }

    @SubscribeEvent
    public void updateAnimation(EntityUpdateEvent event) {
        if (event.side != Side.SERVER || event.phase != TickEvent.Phase.START || !(event.getEntity() instanceof EntityNPCInterface))
            return;
        EntityNPCInterface npc = (EntityNPCInterface) event.getEntity();
        checkAnimations(npc);
        Map<String, Object> tempdata = CommonEntityData.getData(npc);

        List<Animation> animations = (List<Animation>) tempdata.get("puppetAnimations");

        if (!animations.isEmpty() && npc.jobInterface instanceof IJobPuppet)
            while (animations.get(0).updateAni(npc)) {
                animations.remove(0);
                if (animations.isEmpty()) {
                    break;
                }
            }
        if (npc.updateClient) {
            npc.updateClient();
        }
    }
}