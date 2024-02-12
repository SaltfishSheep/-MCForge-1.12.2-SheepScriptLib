package saltsheep.ssl.network;

import net.minecraft.network.PacketBuffer;

import java.io.IOException;

public abstract class PacketScript extends PacketBase {

    public Object data = null;

    public void readBuf(PacketBuffer buf) throws IOException{
        readBuffer(new WrapperBuffer(isRemote,buf),data);
    }

    public void writeBuf(PacketBuffer buf) throws IOException{
        writeBuffer(new WrapperBuffer(isRemote,buf),data);
    }

    public void process(){
        invoke(data);
    }

    public abstract void readBuffer(WrapperBuffer buf, Object data) throws IOException;

    public abstract void writeBuffer(WrapperBuffer buf, Object data) throws IOException;

    public abstract void invoke(Object data);

    public abstract String getRegisterName();

}
