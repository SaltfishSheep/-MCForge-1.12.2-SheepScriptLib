package saltsheep.ssl.script;

import net.minecraftforge.fml.common.event.FMLEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventBus;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import noppes.npcs.api.wrapper.WrapperNpcAPI;
import saltsheep.ssl.SheepScriptLibConfig;
import saltsheep.ssl.api.Common;
import saltsheep.ssl.api.EventListenHandler;
import saltsheep.ssl.api.EventListenerHandler;

import javax.annotation.Nullable;
import javax.script.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;


public class ScriptContainer {
    private final File script;
    private final String scriptRelativePath;
    private final ScriptEngine engine = ScriptLoader.engineFactory.getScriptEngine();
    boolean useFMLServerEvents = false;
    String containerName = null;
    private Map<String, EventListenerHandler.SheepListener> listeners = new HashMap<>();
    private Throwable lastError = null;

    public ScriptContainer(File script, String scriptRelativePath) {
        this.script = script;
        this.scriptRelativePath = scriptRelativePath;
        load();
    }

    void load() {
        try {
            this.containerName = scriptRelativePath;
            Bindings bindings = engine.createBindings();
            bindings.put(ScriptEngine.FILENAME, this.containerName);
            bindings.put("script", this);
            bindings.put("station", Common.station);
            this.engine.setBindings(bindings, ScriptContext.ENGINE_SCOPE);
            this.engine.eval(new InputStreamReader(new FileInputStream(this.script), SheepScriptLibConfig.sheepBothSideScript_ScriptCoding));
        } catch (Throwable e) {
            this.lastError = e;
            e.printStackTrace();
        }
    }

    void unload() {
        try {
            for (EventListenerHandler.SheepListener listener : this.listeners.values())
                listener.endListen();
            this.listeners.clear();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
    }

    public void setContainerName(String name) {
        this.containerName = name;
    }

    public void enableFMLServerEvents() {
        this.useFMLServerEvents = true;
    }

    public void disableFMLServerEvents() {
        this.useFMLServerEvents = false;
    }

    void invokeFMLServerEvents(FMLEvent event) {
        if (this.useFMLServerEvents && this.lastError == null) {
            try {
                ((Invocable) this.engine).invokeFunction("fmlServerFired", new Object[]{event});
            } catch (Throwable e) {
                this.lastError = e;
                e.printStackTrace();
            }
        }
    }

    @Deprecated
    public void listenNpcsEvent(String listenerName, String eventType, EventListenerHandler.SheepListener.Invoke invoke) {
        try {
            Class<? extends Event> clazz = (Class<? extends Event>) EventListenHandler.findClass(eventType);
            listenNpcsEvent(listenerName, clazz, invoke);
        } catch (Throwable error) {
            this.lastError = error;
            error.printStackTrace();
        }
    }

    @Deprecated
    public void listen(String listenerName, @Nullable EventBus bus, String eventType, EventListenerHandler.SheepListener.Invoke invoke) {
        try {
            Class<? extends Event> clazz = (Class<? extends Event>) EventListenHandler.findClass(eventType);
            listen(listenerName, bus, clazz, invoke);
        } catch (Throwable error) {
            this.lastError = error;
            error.printStackTrace();
        }
    }

    public void listenNpcsEvent(String listenerName, Class<? extends Event> eventType, EventListenerHandler.SheepListener.Invoke invoke) {
        listen(listenerName, WrapperNpcAPI.EVENT_BUS, eventType, invoke);
    }

    public void listen(String listenerName, @Nullable EventBus bus, Class<? extends Event> eventType, EventListenerHandler.SheepListener.Invoke invoke) {
        if (this.listeners.containsKey(listenerName)) {
            this.lastError = new IllegalStateException("The listener has been registered.");
            return;
        }
        EventListenerHandler.SheepListener listener = EventListenHandler.listen(bus, EventPriority.NORMAL, eventType, event -> {
            if (this.lastError == null) {
                try {
                    invoke.invoke(event);
                } catch (Throwable error) {
                    this.lastError = error;
                    error.printStackTrace();
                }
            }
        });
        this.listeners.put(listenerName, listener);
    }

    public void unlisten(String listenerName) throws InstantiationException, IllegalAccessException {
        EventListenerHandler.SheepListener listener = this.listeners.get(listenerName);
        if (listener != null) {
            this.listeners.remove(listenerName);
            listener.endListen();
        }
    }

    @Nullable
    public Throwable getLastError() {
        return this.lastError;
    }

    public void resetLastError() {
        this.lastError = null;
    }
}