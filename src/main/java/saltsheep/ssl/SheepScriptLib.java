package saltsheep.ssl;

import jdk.nashorn.api.scripting.NashornScriptEngine;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.common.eventhandler.EventBus;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import noppes.npcs.api.entity.IEntity;
import noppes.npcs.api.wrapper.NPCWrapper;
import noppes.npcs.api.wrapper.PlayerWrapper;
import org.apache.logging.log4j.Logger;

import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import saltsheep.ssl.api.Common;
import saltsheep.ssl.api.EventListenHandler;
import saltsheep.ssl.script.ScriptLoader;

import javax.script.ScriptEngine;
import javax.script.ScriptException;
import java.lang.reflect.Field;

@Mod(modid = SheepScriptLib.MODID, name = SheepScriptLib.NAME, version = SheepScriptLib.VERSION, useMetadata = true, dependencies="required-after:customnpcs;required-after:sheepkeylib", acceptableRemoteVersions = "*")
public class SheepScriptLib
{
    public static final String MODID = "sheepscriptlib";
    public static final String NAME = "SheepScriptLib";
    public static final String VERSION = "1.19";
    public static SheepScriptLib instance;

    private static Logger logger;

    public SheepScriptLib() {
    	instance = this;
    }
    
    @EventHandler
    public void preInit(FMLPreInitializationEvent event){
        logger = event.getModLog();
        Proxy.preInit();
        //NetworkHandler.register();
    }

    @EventHandler
    public void init(FMLInitializationEvent event){
        Proxy.init();
        //EventListenHandler.listen(MinecraftForge.EVENT_BUS, EventPriority.HIGHEST,"net.minecraftforge.event.entity.EntityJoinWorldEvent",(e)->logger.info(e));
    }

    @EventHandler
    public static void onServerAboutToStart(FMLServerAboutToStartEvent event){
        Proxy.aboutToStartServer(event);
    }
    
    @EventHandler
    public static void onServerStarting(FMLServerStartingEvent event){
        Proxy.startingServer(event);
	}
    
    @EventHandler
    public static void onServerStarted(FMLServerStartedEvent event) {
    	Proxy.startedServer(event);
    }

    @EventHandler
    public static void onServerStopping(FMLServerStoppingEvent event){
        Proxy.stoppingServer(event);
    }

    @EventHandler
    public static void onServerStopped(FMLServerStoppedEvent event){
        Proxy.stoppedServer(event);
    }
    
    public static Logger getLogger() {
    	return logger;
    }
    
    public static MinecraftServer getMCServer() {
    	return FMLCommonHandler.instance().getMinecraftServerInstance();
    }
    
    public static void printError(Throwable error) {
        StringBuilder messages = new StringBuilder();
        for(StackTraceElement stackTrace : error.getStackTrace()) {
            messages.append("\n").append(stackTrace.toString());
        }
        logger.error("Warning!The mod of me(Saltfish_Sheep) meet an error:\n"+"Error Type:"+error.getClass()+"-"+error.getMessage()+"\n"+messages);
    }
    
    public static void info(String str) {
    	logger.info(str);
    }
    
    public static void info(Object obj) {
    	if(obj == null)
    		logger.info("null has such obj.");
    	else
    		logger.info(obj.toString());
    }

    public static void sayError(Throwable error, IEntity<?> entity){
        StringBuilder messages = new StringBuilder();
        messages.append("Error Type:").append(error.getClass()).append('-').append(error.getMessage()).append('\n');
        for(StackTraceElement stackTrace : error.getStackTrace()) {
            messages.append("\n").append(stackTrace.toString());
        }
        if(entity.getType()==2)
            ((NPCWrapper)entity).say(messages.toString());
        if(entity.getType()==1)
            ((PlayerWrapper)entity).message(messages.toString());
    }
    
    
}
