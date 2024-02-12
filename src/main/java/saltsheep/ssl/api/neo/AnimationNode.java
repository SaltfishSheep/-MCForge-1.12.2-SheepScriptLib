package saltsheep.ssl.api.neo;


import noppes.npcs.entity.EntityNPCInterface;
import saltsheep.ssl.puppet.handler.JobPuppetSSLData;
import saltsheep.ssl.puppet.handler.tasks.*;
import saltsheep.ssl.puppet.network.SPacketAnimation;

import java.util.HashMap;
import java.util.Map;

public class AnimationNode {

    private Map<Integer, JobPuppetSSLData.IAnimationTask> parts = new HashMap<>();

    //*ms
    private final long duration;

    public AnimationNode(long duration){
        this.duration = duration;
        for(int i=0;i<5;i++)
            parts.put(i, new TaskDelay(duration));
    }

    public AnimationNode setRotation(int part, float x, float y, float z){
        return this.setRotation(part,x,y,z,null);
    }

    public AnimationNode setRotation(int part, float x, float y, float z, TaskPlay.PlayMode mode){
        TaskPlay task = new TaskPlay(duration,x/180f-1,y/180f-1,z/180f-1);
        task.mode = mode==null? TaskPlay.PlayMode.NORMAL:mode;
        parts.put(part,task);
        return this;
    }

    public AnimationNode clear(){
        for(Integer part:parts.keySet())
            parts.put(part, new TaskClear());
        return this;
    }

    public AnimationNode reset() {
        for(Integer part:parts.keySet())
            parts.put(part, new TaskReset());
        return this;
    }

    public void play(EntityNPCInterface npc){
        SPacketAnimation packet = SPacketAnimation.getPacketCache(npc);
        for(Map.Entry<Integer, JobPuppetSSLData.IAnimationTask> entry:parts.entrySet())
            packet.appendTask(entry.getKey(),entry.getValue());
    }

}
