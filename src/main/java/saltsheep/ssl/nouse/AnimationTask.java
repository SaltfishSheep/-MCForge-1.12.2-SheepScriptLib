package saltsheep.ssl.nouse;

import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import noppes.npcs.entity.EntityNPCInterface;
import saltsheep.ssl.api.Animation;
import saltsheep.ssl.api.AnimationHandler;
import saltsheep.ssl.network.PacketBase;

import java.io.IOException;

public abstract class AnimationTask extends PacketBase {

    protected EntityNPCInterface npc;

    public AnimationTask(){}

    public AnimationTask(EntityNPCInterface npc){
        this.npc = npc;
    }

    public abstract int id();

    @Override
    public void writeBuf(PacketBuffer buf){
        buf.writeInt(npc.getEntityId());
        this.writeNBT(buf);
    }

    @Override
    public void readBuf(PacketBuffer buf) throws IOException{
        this.npc = (EntityNPCInterface) Minecraft.getMinecraft().world.getEntityByID(buf.readInt());
        this.readNBT(buf);
    }

    public abstract void writeNBT(PacketBuffer buf);

    public abstract void readNBT(PacketBuffer buf) throws IOException;

    public abstract void process();

    public static AnimationTask get(int id,PacketBuffer buf) throws IOException {
        AnimationTask task = null;
        switch (id){
            case 0:
                task = new AnimationInfo();
                break;
            case 1:
                task = new AnimationStop();
                break;
            case 2:
                task = new AnimationPartEnabled();
                break;
        }
        if(task!=null)
            task.readBuf(buf);
        return task;
    }

    public static class AnimationInfo extends AnimationTask{

        private Animation newAni = null;

        public AnimationInfo(){
            super();
        }

        public AnimationInfo(EntityNPCInterface npc, Animation ani) {
            super(npc);
            this.newAni = ani;
        }

        @Override
        public int id() {
            return 0;
        }

        @Override
        public void writeNBT(PacketBuffer buf) {
            if(newAni==null) {
                buf.writeBoolean(false);
                return;
            }
            buf.writeBoolean(true);
            buf.writeCompoundTag(Animation.toNBT(newAni));
        }

        @Override
        public void readNBT(PacketBuffer buf) throws IOException {
            boolean hasAni = buf.readBoolean();
            if(hasAni)
                newAni = Animation.fromNBT(buf.readCompoundTag());
        }

        @Override
        public void process() {
            AnimationHandler.addAnimation(this.npc,this.newAni);
        }

    }

    public static class AnimationStop extends AnimationTask{

        public AnimationStop(){
            super();
        }

        public AnimationStop(EntityNPCInterface npc) {
            super(npc);
        }

        @Override
        public int id() {
            return 1;
        }

        @Override
        public void writeNBT(PacketBuffer buf) {
        }

        @Override
        public void readNBT(PacketBuffer buf) throws IOException {
        }

        @Override
        public void process() {
            AnimationHandler.stopAnimation(this.npc);
        }
    }

    public static class AnimationPartEnabled extends AnimationTask{

        private int part = -1;
        private boolean isUseAnimation = false;

        public AnimationPartEnabled(){
            super();
        }

        public AnimationPartEnabled(EntityNPCInterface npc, int part, boolean isUseAnimation) {
            super(npc);
            this.part = part;
            this.isUseAnimation = isUseAnimation;
        }

        @Override
        public int id() {
            return 2;
        }

        @Override
        public void writeNBT(PacketBuffer buf) {
            buf.writeInt(part);
            buf.writeBoolean(isUseAnimation);
        }

        @Override
        public void readNBT(PacketBuffer buf) throws IOException {
            this.part = buf.readInt();
            this.isUseAnimation = buf.readBoolean();
        }

        @Override
        public void process() {
            if(part==-1)
                return;
            AnimationHandler.setPartUseAnimation(npc,part,isUseAnimation);
        }
    }

}
