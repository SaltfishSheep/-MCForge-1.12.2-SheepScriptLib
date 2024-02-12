package saltsheep.ssl.nouse;

import java.io.IOException;
import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.network.PacketBuffer;
import saltsheep.ssl.network.PacketBase;
import saltsheep.ssl.nouse.AnimationTask;

public class SPacketNPCAnimation extends PacketBase {

	//private boolean willOverride = false;
	private List<AnimationTask> tasks;
	
	public SPacketNPCAnimation() {
		this.tasks = Lists.newArrayList();
	}

	public void addTask(AnimationTask task){
		tasks.add(task);
	}

	public void clearTask(){
		tasks.clear();
	}

	public boolean needSend(){
		return !tasks.isEmpty();
	}
	
	@Override
	public void readBuf(PacketBuffer buf) throws IOException {
		//willOverride = buf.readBoolean();
		int size = buf.readInt();
		for(int i=0;i<size;i++){
			int id = buf.readInt();
			this.tasks.add(AnimationTask.get(id,buf));
		}
	}

	@Override
	public void writeBuf(PacketBuffer buf) throws IOException {
		//buf.writeBoolean(willOverride);
		int size = tasks.size();
		buf.writeInt(size);
		for(int i=0;i<size;i++){
			buf.writeInt(tasks.get(i).id());
			tasks.get(i).writeBuf(buf);
		}
	}

	@Override
	public void process() {
		for(AnimationTask task:tasks)
			task.process();
		tasks.clear();
	}

}
