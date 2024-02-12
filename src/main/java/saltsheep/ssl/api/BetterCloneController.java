package saltsheep.ssl.api;

import java.io.File;
import java.util.Map;

import com.google.common.collect.Maps;

import net.minecraft.command.ICommandSender;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.controllers.ServerCloneController;
import saltsheep.ssl.SheepScriptLib;

public class BetterCloneController extends ServerCloneController {

	public final Map<Integer,Map<String,NBTTagCompound>> tempData = Maps.newHashMap();
	
	public BetterCloneController() {
		super();
		new CloneLoader(this,this.getDir()).start();
	}
	
	@Override
	public NBTTagCompound getCloneData(ICommandSender player, String name, int tab) {
		synchronized(tempData) {
			if(this.getTabmap(tab).containsKey(name))
				return this.getTabmap(tab).get(name).copy();
			NBTTagCompound data = super.getCloneData(player, name, tab);
			this.getTabmap(tab).put(name, data);
			return data;
		}
	}
	
	@Override
	public void saveClone(int tab, String name, NBTTagCompound compound){
		synchronized(tempData) {
			super.saveClone(tab,name,compound);
        	getTabmap(tab).put(name,compound);
		}
    }
	
	@Override
	public boolean removeClone(String name, int tab) {
		synchronized(tempData) {
			this.getTabmap(tab).remove(name);
			return super.removeClone(name, tab);
		}
	}
	
	public Map<String,NBTTagCompound> getTabmap(int tab) {
		synchronized(tempData) {
			Map<String,NBTTagCompound> tabmap = tempData.get(tab);
			if(tabmap==null){
				tabmap = Maps.newHashMap();
				tempData.put(tab,tabmap);
			}
			return tabmap;
		}
	}
	
	public static class CloneLoader extends Thread {
		private final BetterCloneController controller;
		private final File clonesDir;
		public CloneLoader(BetterCloneController controller, File clonesDir) {
			this.controller = controller;
			this.clonesDir = clonesDir;
		}
		public void run() {
			try {
				for(int i=1;i<=9;i++) {
					readPacket(i);
				}
				SheepScriptLib.info("All npc clone is already read!");
			}catch(Throwable error) {
				SheepScriptLib.printError(error);
			}
		}
		private void readPacket(int tab) {
			if(tab<1)
				return;
			if(tab>9)
				return;
			File tabF = new File(clonesDir,String.valueOf(tab));
			if(!tabF.exists())
				return;
			if(!tabF.isDirectory())
				return;
			for(String each:tabF.list()) {
				if(!each.toLowerCase().endsWith(".json"))
					continue;
				String cloneName = each.substring(0, each.length()-5);
				this.controller.getCloneData(null, cloneName, tab);
			}
		}
	}
	
}
