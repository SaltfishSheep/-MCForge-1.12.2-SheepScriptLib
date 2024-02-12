package saltsheep.ssl.puppet.network;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.PacketBuffer;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.roles.JobPuppet;
import saltsheep.ssl.common.IGetter;
import saltsheep.ssl.network.PacketBase;
import saltsheep.ssl.network.PacketSender;
import saltsheep.ssl.network.WrapperBuffer;
import saltsheep.ssl.puppet.handler.IJobPuppetSSL;
import saltsheep.ssl.puppet.handler.JobPuppetSSLData;
import saltsheep.ssl.puppet.handler.tasks.PuppetTaskLoaders;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class SPacketAnimation extends PacketBase {

    private static Map<EntityNPCInterface,SPacketAnimation> caches = new HashMap<>();

    private EntityNPCInterface npc;
    private IGetter<Entity> npcGetter;

    private final Map<Integer,List<JobPuppetSSLData.IAnimationTask>> partsTasks = new HashMap<>();

    public static SPacketAnimation getPacketCache(EntityNPCInterface npc){
        SPacketAnimation packet = caches.get(npc);
        if(packet==null){
            packet = new SPacketAnimation(npc);
            caches.put(npc,packet);
        }
        return packet;
    }

    public static void handleCaches(){
        for(SPacketAnimation packet:caches.values())
            PacketSender.INSTANCE.sendToAll(packet);
        caches.clear();
    }

    public static void resetCaches(){
        caches.clear();
    }

    public SPacketAnimation(){}

    public SPacketAnimation(EntityNPCInterface npc){
        this.npc = npc;
    }

    public SPacketAnimation appendTask(int part, @Nonnull JobPuppetSSLData.IAnimationTask task){
        boolean isAddition = task instanceof IAnimationPacketAddition;
        if(isAddition)
            ((IAnimationPacketAddition)task).beforePacketAttended(this, part);
        getPartTasks(part).add(task);
        if(isAddition)
            ((IAnimationPacketAddition) task).afterPacketAttended(this, part);
        return this;
    }

    public List<JobPuppetSSLData.IAnimationTask> getPartTasks(int part){
        List<JobPuppetSSLData.IAnimationTask> list = partsTasks.get(part);
        if(list==null){
            list = new LinkedList<>();
            partsTasks.put(part,list);
        }
        return list;
    }

    @Override
    public void readBuf(PacketBuffer buf) throws IOException {
        WrapperBuffer buffer = new WrapperBuffer(buf,isRemote);
        npcGetter = buffer.readEntity();
        NBTTagCompound parts = buf.readCompoundTag();
        for(String part:parts.getKeySet()){
            int partId = Integer.valueOf(part);
            NBTTagCompound partCompound = parts.getCompoundTag(part);
            NBTTagList tasksTag = (NBTTagList) partCompound.getTag("tasks");
            int length = tasksTag.tagCount();
            List<JobPuppetSSLData.IAnimationTask> tasksList = getPartTasks(partId);
            for(int i=0;i<length;i++)
                tasksList.add(PuppetTaskLoaders.readTask(tasksTag.getCompoundTagAt(i)));
        }
    }

    @Override
    public void writeBuf(PacketBuffer buf) throws IOException {
        WrapperBuffer buffer = new WrapperBuffer(buf,isRemote);
        NBTTagCompound parts = new NBTTagCompound();
        for(Map.Entry<Integer,List<JobPuppetSSLData.IAnimationTask>> entry:partsTasks.entrySet()){
            NBTTagCompound part = new NBTTagCompound();
            NBTTagList tasks = new NBTTagList();
            for(JobPuppetSSLData.IAnimationTask task:entry.getValue())
                tasks.appendTag(PuppetTaskLoaders.writeTask(task));
            part.setTag("tasks",tasks);
            parts.setTag(String.valueOf(entry.getKey()),part);
        }
        buffer.writeEntity(this.npc);
        buffer.writeCompoundTag(parts);
    }

    @Override
    public void process() {
        npc = (EntityNPCInterface) npcGetter.get();
        if(npc==null)
            return;
        if(!(npc.jobInterface instanceof JobPuppet))
            return;
        JobPuppet puppet = (JobPuppet) npc.jobInterface;
        JobPuppetSSLData data = ((IJobPuppetSSL)puppet).getSSLData();
        if(data==null){
            data = new JobPuppetSSLData();
            ((IJobPuppetSSL)puppet).setSSLData(data);
        }
        for(Map.Entry<Integer,List<JobPuppetSSLData.IAnimationTask>> entry:partsTasks.entrySet()){
            JobPuppetSSLData.RotationController part = data.getPart(puppet,entry.getKey());
            part.appendTasks(entry.getValue());
        }
    }

    public static interface IAnimationPacketAddition {

        public void beforePacketAttended(SPacketAnimation packet, int part);

        public void afterPacketAttended(SPacketAnimation packet, int part);

    }

}
