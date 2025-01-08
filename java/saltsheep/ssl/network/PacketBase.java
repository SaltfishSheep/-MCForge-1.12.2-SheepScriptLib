package saltsheep.ssl.network;

import net.minecraft.network.PacketBuffer;

import java.io.IOException;

public abstract class PacketBase {
    public boolean isRemote;

    public abstract void readBuf(PacketBuffer paramPacketBuffer) throws IOException;

    public abstract void writeBuf(PacketBuffer paramPacketBuffer) throws IOException;

    public abstract void process();

    public String getRegisterName() {
        return getClass().getCanonicalName();
    }
}