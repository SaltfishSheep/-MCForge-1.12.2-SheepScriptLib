package saltsheep.ssl.network;

import saltsheep.ssl.nouse.SPacketNPCAnimation;
import saltsheep.ssl.puppet.network.SPacketAnimation;

public class NetworkHandler {
	
	public static void register() {
		PacketSender.CHANNEL.register(PacketSender.INSTANCE);
		PacketSender.INSTANCE.registerPacket(SPacketTest.class);
		PacketSender.INSTANCE.registerPacket(SPacketAnimation.class);
		//PacketSender.INSTANCE.registerPacket(SPacketNPCAnimation.class);
	}
	
}
