package saltsheep.ssl.script;

import net.minecraftforge.fml.common.event.FMLEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventBus;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import noppes.npcs.api.wrapper.WrapperNpcAPI;
import saltsheep.ssl.SheepScriptLib;
import saltsheep.ssl.SheepScriptLibConfig;
import saltsheep.ssl.api.Common;
import saltsheep.ssl.api.EventListenHandler;
import saltsheep.ssl.api.EventListenerHandler;

import javax.annotation.Nullable;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class ScriptContainer {

    private Map<String,EventListenerHandler.SheepListener> listeners = new HashMap<String,EventListenerHandler.SheepListener>();
    private Throwable lastError = null;
    private final File script;
    private final ScriptEngine engine = ScriptLoader.engineFactory.getScriptEngine();
    boolean useFMLServerEvents = false;
    String containerName = null;

    public ScriptContainer(File script){
        this.script = script;
        load();
    }

    void load(){
        try {
            containerName = script.getAbsolutePath();
            engine.put("script",this);
            engine.put("station", Common.station);
            engine.eval(new InputStreamReader(new FileInputStream(script), SheepScriptLibConfig.sheepBothSideScript_ScriptCoding));
        } catch (Throwable e) {
            lastError = e;
            e.printStackTrace();
        }
    }

    void unload(){
        try {
            for(EventListenerHandler.SheepListener listener:listeners.values())
                listener.endListen();
            listeners.clear();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
    }

    public void setContainerName(String name){
        containerName = name;
    }

    public void enableFMLServerEvents(){
        useFMLServerEvents = true;
    }

    public void disableFMLServerEvents(){
        useFMLServerEvents = false;
    }

    void invokeFMLServerEvents(FMLEvent event){
        if(useFMLServerEvents&&lastError==null){
            try {
                ((Invocable)engine).invokeFunction("fmlServerFired",event);
            } catch (Throwable e) {
                lastError = e;
                e.printStackTrace();
            }
        }
    }

    @Deprecated
    public void listenNpcsEvent(String listenerName, String eventType, EventListenerHandler.SheepListener.Invoke invoke){
        try {
            Class<? extends Event> clazz = (Class<? extends Event>) EventListenHandler.findClass(eventType);
            listenNpcsEvent(listenerName,clazz,invoke);
        }catch (Throwable error){
            lastError = error;
            error.printStackTrace();
        }
    }

    @Deprecated
    public void listen(String listenerName, @Nullable EventBus bus, String eventType, EventListenerHandler.SheepListener.Invoke invoke){
        try {
            Class<? extends Event> clazz = (Class<? extends Event>) EventListenHandler.findClass(eventType);
            listen(listenerName,bus,clazz,invoke);
        }catch (Throwable error){
            lastError = error;
            error.printStackTrace();
        }
    }

    public void listenNpcsEvent(String listenerName, Class<? extends Event> eventType, EventListenerHandler.SheepListener.Invoke invoke){
        listen(listenerName, WrapperNpcAPI.EVENT_BUS, eventType, invoke);
    }

    public void listen(String listenerName, @Nullable EventBus bus, Class<? extends Event> eventType, EventListenerHandler.SheepListener.Invoke invoke){
        if(listeners.containsKey(listenerName)){
            lastError = new IllegalStateException("The listener has been registered.");
            return;
        }
        EventListenerHandler.SheepListener listener = EventListenHandler.listen(bus, EventPriority.NORMAL, eventType, (event)->{
            if(lastError==null){
                try{
                    invoke.invoke(event);
                }catch (Throwable error){
                    lastError = error;
                    error.printStackTrace();
                }
            }
        });
        listeners.put(listenerName,listener);
    }

    public void unlisten(String listenerName) throws InstantiationException, IllegalAccessException {
        EventListenerHandler.SheepListener listener = listeners.get(listenerName);
        if(listener!=null) {
            listeners.remove(listenerName);
            listener.endListen();
        }
    }

    @Nullable
    public Throwable getLastError(){
        return lastError;
    }

    public void resetLastError(){
        lastError = null;
    }

}
