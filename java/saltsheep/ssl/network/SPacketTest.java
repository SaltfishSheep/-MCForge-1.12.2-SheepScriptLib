package saltsheep.ssl.network;

import net.minecraft.network.PacketBuffer;

public class SPacketTest
        extends PacketBase {
    public void readBuf(PacketBuffer buf) {
    }

    public void writeBuf(PacketBuffer buf) {
    }

    public void process() {
        System.out.println(5000);
    }
}