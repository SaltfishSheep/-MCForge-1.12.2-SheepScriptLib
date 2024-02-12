package saltsheep.ssl.network;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.relauncher.Side;

import java.io.IOException;

public abstract class PacketBase {

	/**
	 * Set at packet received {@link saltsheep.ssl.network.PacketSender}
	 */

	public boolean isRemote;

	public abstract void readBuf(PacketBuffer buf) throws IOException;

	public abstract void writeBuf(PacketBuffer buf) throws IOException;

	public abstract void process();

	public String getRegisterName(){
		return this.getClass().getCanonicalName();
	}
	
}
