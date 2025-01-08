package saltsheep.ssl.puppet.network;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.PacketBuffer;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.roles.JobPuppet;
import saltsheep.ssl.SheepScriptLibConfig;
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

public class SPacketAnimation
        extends PacketBase {
    private static final Map<EntityNPCInterface, SPacketAnimation> caches = new HashMap<>();
    private final Map<Integer, List<JobPuppetSSLData.IAnimationTask>> partsTasks = new HashMap<>();
    private final Map<Integer, Boolean> partsUse = new HashMap<>();
    private EntityNPCInterface npc;
    private IGetter<Entity> npcGetter;

    public SPacketAnimation() {
    }

    public SPacketAnimation(EntityNPCInterface npc) {
        this.npc = npc;
    }

    public static SPacketAnimation getPacketCache(EntityNPCInterface npc) {
        SPacketAnimation packet = caches.get(npc);
        if (packet == null) {
            packet = new SPacketAnimation(npc);
            caches.put(npc, packet);
        }
        return packet;
    }

    public static void handleCaches() {
        for (SPacketAnimation packet : caches.values())
            PacketSender.INSTANCE.sendToAll(packet);
        caches.clear();
    }

    public static void resetCaches() {
        caches.clear();
    }

    public SPacketAnimation appendTask(int part, @Nonnull JobPuppetSSLData.IAnimationTask task) {
        if (!SheepScriptLibConfig.neoPuppet_enable)
            return this;
        boolean isAddition = task instanceof IAnimationPacketAddition;
        if (isAddition)
            ((IAnimationPacketAddition) task).beforePacketAttended(this, part);
        getPartTasks(part).add(task);
        if (isAddition)
            ((IAnimationPacketAddition) task).afterPacketAttended(this, part);
        return this;
    }

    public List<JobPuppetSSLData.IAnimationTask> getPartTasks(int part) {
        List<JobPuppetSSLData.IAnimationTask> list = this.partsTasks.get(part);
        if (list == null) {
            list = new LinkedList<>();
            this.partsTasks.put(part, list);
        }
        return list;
    }

    public void setPartUse(int part, boolean isUse) {
        this.partsUse.put(part, isUse);
    }


    public void readBuf(PacketBuffer buf) throws IOException {
        WrapperBuffer buffer = new WrapperBuffer(buf, this.isRemote);
        this.npcGetter = buffer.readEntity();
        NBTTagCompound parts = buf.readCompoundTag();
        for (String part : parts.getKeySet()) {
            int partId = Integer.parseInt(part);
            NBTTagCompound partCompound = parts.getCompoundTag(part);
            NBTTagList tasksTag = (NBTTagList) partCompound.getTag("tasks");
            int length = tasksTag.tagCount();
            List<JobPuppetSSLData.IAnimationTask> tasksList = getPartTasks(partId);
            for (int j = 0; j < length; j++)
                tasksList.add(PuppetTaskLoaders.readTask(tasksTag.getCompoundTagAt(j)));
        }
        int size = buffer.readInt();
        for (int i = 0; i < size; i++) {
            int key = buffer.readInt();
            boolean value = buffer.readBoolean();
            this.partsUse.put(key, value);
        }
    }


    public void writeBuf(PacketBuffer buf) throws IOException {
        WrapperBuffer buffer = new WrapperBuffer(buf, this.isRemote);
        NBTTagCompound parts = new NBTTagCompound();
        for (Map.Entry<Integer, List<JobPuppetSSLData.IAnimationTask>> entry : this.partsTasks.entrySet()) {
            NBTTagCompound part = new NBTTagCompound();
            NBTTagList tasks = new NBTTagList();
            for (JobPuppetSSLData.IAnimationTask task : entry.getValue())
                tasks.appendTag((NBTBase) PuppetTaskLoaders.writeTask(task));
            part.setTag("tasks", (NBTBase) tasks);
            parts.setTag(String.valueOf(entry.getKey()), (NBTBase) part);
        }
        buffer.writeEntity(this.npc);
        buffer.writeCompoundTag(parts);
        buffer.writeInt(this.partsUse.size());
        for (Map.Entry<Integer, Boolean> entry : this.partsUse.entrySet()) {
            buffer.writeInt(entry.getKey());
            buffer.writeBoolean(entry.getValue());
        }
    }


    public void process() {
        this.npc = (EntityNPCInterface) this.npcGetter.get();
        if (this.npc == null)
            return;
        if (!(this.npc.jobInterface instanceof JobPuppet))
            return;
        JobPuppet puppet = (JobPuppet) this.npc.jobInterface;
        JobPuppetSSLData data = ((IJobPuppetSSL) puppet).getSSLData();
        if (data == null) {
            data = new JobPuppetSSLData();
            ((IJobPuppetSSL) puppet).setSSLData(data);
        }
        for (Map.Entry<Integer, List<JobPuppetSSLData.IAnimationTask>> entry : this.partsTasks.entrySet()) {
            JobPuppetSSLData.RotationController part = data.getPart(puppet, entry.getKey());
            part.appendTasks(entry.getValue());
        }
        for (Map.Entry<Integer, Boolean> entry : this.partsUse.entrySet())
            ((JobPuppet.PartConfig) puppet.getPart(entry.getKey().intValue())).disabled = !entry.getValue();
    }

    public interface IAnimationPacketAddition {
        void beforePacketAttended(SPacketAnimation param1SPacketAnimation, int param1Int);

        void afterPacketAttended(SPacketAnimation param1SPacketAnimation, int param1Int);
    }
}