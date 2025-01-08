package saltsheep.ssl.network;

import net.minecraft.network.PacketBuffer;

import java.io.IOException;

public abstract class PacketScript
        extends PacketBase {
    public Object data = null;

    public void readBuf(PacketBuffer buf) throws IOException {
        readBuffer(new WrapperBuffer(this.isRemote, buf), this.data);
    }

    public void writeBuf(PacketBuffer buf) throws IOException {
        writeBuffer(new WrapperBuffer(this.isRemote, buf), this.data);
    }

    public void process() {
        invoke(this.data);
    }

    public abstract void readBuffer(WrapperBuffer paramWrapperBuffer, Object paramObject) throws IOException;

    public abstract void writeBuffer(WrapperBuffer paramWrapperBuffer, Object paramObject) throws IOException;

    public abstract void invoke(Object paramObject);

    public abstract String getRegisterName();
}