package saltsheep.ssl.api.neo;

import noppes.npcs.api.entity.ICustomNpc;
import noppes.npcs.entity.EntityNPCInterface;
import saltsheep.ssl.puppet.handler.JobPuppetSSLData;
import saltsheep.ssl.puppet.handler.tasks.TaskClear;
import saltsheep.ssl.puppet.handler.tasks.TaskDelay;
import saltsheep.ssl.puppet.handler.tasks.TaskPlay;
import saltsheep.ssl.puppet.handler.tasks.TaskReset;
import saltsheep.ssl.puppet.network.SPacketAnimation;

import java.util.HashMap;
import java.util.Map;

public class AnimationNode {
    private final long duration;
    private final Map<Integer, JobPuppetSSLData.IAnimationTask> parts = new HashMap<>();


    public AnimationNode(long duration) {
        this.duration = duration;
        for (int i = 0; i < 6; i++)
            this.parts.put(Integer.valueOf(i), new TaskDelay(duration));
    }

    public AnimationNode setRotation(int part, float x, float y, float z) {
        return setRotation(part, x, y, z, null);
    }

    public AnimationNode setRotation(int part, float x, float y, float z, TaskPlay.PlayMode mode) {
        TaskPlay task = new TaskPlay(this.duration, x / 180.0F - 1.0F, y / 180.0F - 1.0F, z / 180.0F - 1.0F);
        task.mode = (mode == null) ? TaskPlay.PlayMode.NORMAL : mode;
        this.parts.put(Integer.valueOf(part), task);
        return this;
    }

    public AnimationNode clear() {
        for (Integer part : this.parts.keySet())
            this.parts.put(part, new TaskClear());
        return this;
    }

    public AnimationNode reset() {
        for (Integer part : this.parts.keySet())
            this.parts.put(part, new TaskReset());
        return this;
    }

    public void play(ICustomNpc<EntityNPCInterface> npc) {
        play(npc.getMCEntity());
    }

    public void play(EntityNPCInterface npc) {
        SPacketAnimation packet = SPacketAnimation.getPacketCache(npc);
        for (Map.Entry<Integer, JobPuppetSSLData.IAnimationTask> entry : this.parts.entrySet())
            packet.appendTask(entry.getKey().intValue(), entry.getValue());
    }
}