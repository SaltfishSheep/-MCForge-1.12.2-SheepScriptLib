package saltsheep.ssl.api.neo;

import noppes.npcs.api.entity.ICustomNpc;
import noppes.npcs.entity.EntityNPCInterface;
import saltsheep.ssl.api.Animation;
import saltsheep.ssl.puppet.network.SPacketAnimation;

public class AnimationHandler {
    public static void addAnimation(ICustomNpc<EntityNPCInterface> npc, Animation ani) {
        addAnimation(npc.getMCEntity(), ani);
    }

    public static void addAnimation(EntityNPCInterface npc, Animation ani) {
        addAnimation(npc, translate(ani));
    }

    public static void addAnimation(ICustomNpc<EntityNPCInterface> npc, AnimationNode ani) {
        addAnimation(npc.getMCEntity(), ani);
    }

    public static void addAnimation(EntityNPCInterface npc, AnimationNode ani) {
        ani.play(npc);
    }

    public static void stopAnimation(ICustomNpc<EntityNPCInterface> npc) {
        stopAnimation(npc.getMCEntity());
    }

    public static void stopAnimation(EntityNPCInterface npc) {
        AnimationNode node = new AnimationNode(0L);
        node.clear();
        node.play(npc);
    }

    public static void resetAnimation(ICustomNpc<EntityNPCInterface> npc) {
        resetAnimation(npc.getMCEntity());
    }

    public static void resetAnimation(EntityNPCInterface npc) {
        AnimationNode node = new AnimationNode(0L);
        node.reset();
        node.play(npc);
    }

    public static void setPartUseAnimation(ICustomNpc<EntityNPCInterface> npc, int part, boolean isUseAnimation) {
        setPartUseAnimation(npc.getMCEntity(), part, isUseAnimation);
    }

    public static void setPartUseAnimation(EntityNPCInterface npc, int part, boolean isUseAnimation) {
        SPacketAnimation.getPacketCache(npc).setPartUse(part, isUseAnimation);
    }

    public static AnimationNode translate(Animation ani) {
        long duration = (ani.getRunTicks() * 50L);
        AnimationNode node = new AnimationNode(duration);
        Animation.PartRotation[] parts = ani.getNextRotations();
        for (int i = 0; i < parts.length; i++) {
            if (parts[i] != null)
                node.setRotation(i, (parts[i]).x, (parts[i]).y, (parts[i]).z);
        }
        return node;
    }
}