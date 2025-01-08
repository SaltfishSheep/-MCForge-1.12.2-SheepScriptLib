package saltsheep.ssl.network;

import com.google.common.collect.Maps;
import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLEventChannel;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;
import saltsheep.ssl.SheepScriptLib;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;


public class PacketSender {
    public static final PacketSender INSTANCE = new PacketSender();
    static final FMLEventChannel CHANNEL = NetworkRegistry.INSTANCE.newEventDrivenChannel("SHEEPSSLPACKET");
    private static final String NAME = "SHEEPSSLPACKET";
    final Map<String, PacketBuilder> registry = Maps.newHashMap();

    public PacketSender getInstance() {
        return INSTANCE;
    }

    public void registerPacket(Class<? extends PacketBase> packetC) {
        registerPacket(packetC.getCanonicalName(), new PacketBuilderClass(packetC));
    }

    public void registerPacket(String registerName, PacketBuilder packetBuilder) {
        this.registry.put(registerName, packetBuilder);
    }

    private PacketBuffer write(PacketBase packet) throws IOException {
        PacketBuffer data = new PacketBuffer(Unpooled.buffer());
        data.writeString(packet.getRegisterName());
        packet.writeBuf(data);
        return data;
    }

    public void sendToDim(PacketBase packet, int dim) {
        try {
            CHANNEL.sendToDimension(new FMLProxyPacket(write(packet), "SHEEPSSLPACKET"), dim);
        } catch (Throwable error) {
            SheepScriptLib.printError(error);
        }
    }

    public void sendAroundPos(PacketBase packet, int dim, double x, double y, double z, double range) {
        try {
            CHANNEL.sendToAllAround(new FMLProxyPacket(write(packet), "SHEEPSSLPACKET"), new NetworkRegistry.TargetPoint(dim, x, y, z, range));
        } catch (Throwable error) {
            SheepScriptLib.printError(error);
        }
    }

    public void sendToPlayer(PacketBase packet, EntityPlayerMP player) {
        try {
            CHANNEL.sendTo(new FMLProxyPacket(write(packet), "SHEEPSSLPACKET"), player);
        } catch (Throwable error) {
            SheepScriptLib.printError(error);
        }
    }

    public void sendToPlayers(PacketBase packet, Collection<EntityPlayerMP> players) {
        try {
            PacketBuffer buf = write(packet);
            for (EntityPlayerMP player : players)
                CHANNEL.sendTo(new FMLProxyPacket(buf, "SHEEPSSLPACKET"), player);
        } catch (Throwable error) {
            SheepScriptLib.printError(error);
        }
    }

    public void sendToAll(PacketBase packet) {
        try {
            CHANNEL.sendToAll(new FMLProxyPacket(write(packet), "SHEEPSSLPACKET"));
        } catch (Throwable error) {
            SheepScriptLib.printError(error);
        }
    }

    public void sendToServer(PacketBase packet) {
        try {
            CHANNEL.sendToServer(new FMLProxyPacket(write(packet), "SHEEPSSLPACKET"));
        } catch (Throwable error) {
            SheepScriptLib.printError(error);
        }
    }

    @SubscribeEvent
    public void receiveByClient(FMLNetworkEvent.ClientCustomPacketEvent event) throws InstantiationException, IllegalAccessException, IOException {
        receive(event.getPacket(), true);
    }

    @SubscribeEvent
    public void receiveByServer(FMLNetworkEvent.ServerCustomPacketEvent event) throws InstantiationException, IllegalAccessException, IOException {
        receive(event.getPacket(), false);
    }

    private void receive(FMLProxyPacket fmlP, boolean isRemote) throws InstantiationException, IllegalAccessException, IOException {
        PacketBuffer buf = new PacketBuffer(fmlP.payload());
        String registerName = buf.readString(32767);
        PacketBase packet = this.registry.get(registerName).build();
        packet.isRemote = isRemote;
        packet.readBuf(buf);
        if (isRemote) {
            Minecraft.getMinecraft().addScheduledTask(packet::process);
        } else {
            SheepScriptLib.getMCServer().addScheduledTask(packet::process);
        }
    }

    public interface PacketBuilder {
        PacketBase build() throws InstantiationException, IllegalAccessException;
    }

    public static class PacketBuilderClass
            implements PacketBuilder {
        Class<? extends PacketBase> clazz;

        public PacketBuilderClass(Class<? extends PacketBase> clazz) {
            this.clazz = clazz;
        }

        public PacketBase build() throws InstantiationException, IllegalAccessException {
            return this.clazz.newInstance();
        }
    }
}