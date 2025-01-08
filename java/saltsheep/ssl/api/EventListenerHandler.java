package saltsheep.ssl.api;

import com.google.common.collect.Maps;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventBus;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.IEventListener;

import javax.annotation.Nullable;
import java.util.Map;

public class EventListenerHandler {
    public static Map<String, SheepListener> listeners = Maps.newHashMap();

    @Nullable
    public static SheepListener getListener(String listenerName) {
        return listeners.get(listenerName);
    }

    public static void listen(String listenerName, @Nullable EventBus bus, @Nullable String priority, Class<? extends Event> eventType, SheepListener.Invoke invoke) {
        EventPriority ep = EventPriority.NORMAL;
        if (priority != null) {
            switch (priority) {
                case "HIGHEST":
                    ep = EventPriority.HIGHEST;
                    break;
                case "HIGH":
                    ep = EventPriority.HIGH;
                    break;
                case "LOW":
                    ep = EventPriority.LOW;
                    break;
                case "LOWEST":
                    ep = EventPriority.LOWEST;
                    break;
            }
        }
        listen(listenerName, bus, ep, eventType, invoke);
    }

    public static void listen(String listenerName, @Nullable EventBus bus, @Nullable EventPriority priority, Class<? extends Event> eventType, SheepListener.Invoke invoke) {
        if (listeners.containsKey(listenerName)) {
            throw new IllegalStateException("The listener has been registered.");
        }
        if (priority == null)
            priority = EventPriority.NORMAL;
        SheepListener listener = EventListenHandler.listen(bus, priority, eventType, invoke);
        if (listener != null) {
            listeners.put(listenerName, listener);
        } else {
            throw new IllegalStateException("Because of unknown reason, the listener has been registered.");
        }
    }

    public static void unlisten(String listenerName) {
        if (!listeners.containsKey(listenerName))
            return;
        SheepListener l = listeners.get(listenerName);
        EventListenHandler.unListen(l);
        listeners.remove(listenerName);
    }

    @Deprecated
    public static void listen(String listenerName, @Nullable EventBus bus, @Nullable String priority, String eventType, SheepListener.Invoke invoke) {
        try {
            Class<? extends Event> clazz = (Class) EventListenHandler.findClass(eventType);
            listen(listenerName, bus, priority, clazz, invoke);
        } catch (Throwable error) {
            error.printStackTrace();
        }
    }

    @Deprecated
    public static void listen(String listenerName, @Nullable EventBus bus, @Nullable EventPriority priority, String eventType, SheepListener.Invoke invoke) {
        try {
            Class<? extends Event> clazz = (Class) EventListenHandler.findClass(eventType);
            listen(listenerName, bus, priority, clazz, invoke);
        } catch (Throwable error) {
            error.printStackTrace();
        }
    }

    @Deprecated
    public static void removeListener(String listenerName) {
        unlisten(listenerName);
    }

    public interface Invoke {
        void invoke(Event param1Event);
    }

    public static class SheepListener {
        public final EventBus bus;
        public final Class<? extends Event> eventType;
        public final EventPriority priority;
        public final Invoke invoke;
        public final IEventListener listenerIn;
        public boolean isListening = false;
        private Throwable lastError = null;

        public SheepListener(EventBus bus, Class<? extends Event> eventType, EventPriority priority, Invoke invoke) {
            this.bus = bus;
            this.eventType = eventType;
            this.priority = priority;
            this.invoke = invoke;
            this.listenerIn = (event1 -> invoke(event1));
        }

        public void invoke(Event event) {
            if (this.lastError == null) {
                try {
                    this.invoke.invoke(event);
                } catch (Throwable error) {
                    this.lastError = error;
                }
            }
        }

        public void startListen() throws IllegalAccessException, InstantiationException {
            if (this.isListening)
                return;
            this.eventType.newInstance().getListenerList().register(EventListenHandler.busIDField.getInt(this.bus), this.priority, this.listenerIn);
            this.isListening = true;
        }

        public void endListen() throws IllegalAccessException, InstantiationException {
            if (!this.isListening)
                return;
            this.eventType.newInstance().getListenerList().unregister(EventListenHandler.busIDField.getInt(this.bus), this.listenerIn);
            this.isListening = false;
        }

        @Nullable
        public Throwable getLastError() {
            return this.lastError;
        }

        public interface Invoke {
            void invoke(Event param2Event);
        }
    }
}