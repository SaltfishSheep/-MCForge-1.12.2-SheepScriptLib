package saltsheep.ssl.api;

import java.util.Map;

import javax.annotation.Nullable;

import com.google.common.collect.Maps;

import net.minecraftforge.fml.common.eventhandler.*;

public class EventListenerHandler {
	
	public static Map<String,SheepListener> listeners = Maps.newHashMap();
	
	@Nullable
	public static SheepListener getListener(String listenerName) {
		return listeners.get(listenerName);
	}

	public static void listen(String listenerName,@Nullable EventBus bus,@Nullable String priority, Class<? extends Event> eventType, SheepListener.Invoke invoke) {
		EventPriority ep = EventPriority.NORMAL;
		if(priority!=null) {
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
		listen(listenerName,bus,ep,eventType,invoke);
	}

	public static void listen(String listenerName,@Nullable EventBus bus,@Nullable EventPriority priority, Class<? extends Event> eventType, SheepListener.Invoke invoke) {
		if(listeners.containsKey(listenerName)) {
			throw new IllegalStateException("The listener has been registered.");
		}
		if(priority==null)
			priority = EventPriority.NORMAL;
		SheepListener listener = EventListenHandler.listen(bus,priority,eventType,invoke);
		if(listener!=null)
			listeners.put(listenerName, listener);
		else
			throw new IllegalStateException("Because of unknown reason, the listener has been registered.");
	}

	public static void unlisten(String listenerName){
		if(!listeners.containsKey(listenerName))
			return;
		SheepListener l = listeners.get(listenerName);
		EventListenHandler.unListen(l);
		listeners.remove(listenerName);
	}

	@Deprecated
	public static void listen(String listenerName,@Nullable EventBus bus,@Nullable String priority, String eventType, SheepListener.Invoke invoke) {
		try {
			Class<? extends Event> clazz = (Class<? extends Event>) EventListenHandler.findClass(eventType);
			listen(listenerName,bus,priority,clazz,invoke);
		}catch (Throwable error){
			error.printStackTrace();
		}
	}

	@Deprecated
	public static void listen(String listenerName,@Nullable EventBus bus,@Nullable EventPriority priority, String eventType, SheepListener.Invoke invoke) {
		try {
			Class<? extends Event> clazz = (Class<? extends Event>) EventListenHandler.findClass(eventType);
			listen(listenerName,bus,priority,clazz,invoke);
		}catch (Throwable error){
			error.printStackTrace();
		}
	}

	@Deprecated
	public static void removeListener(String listenerName) {
		unlisten(listenerName);
	}

	public static class SheepListener {

		public final EventBus bus;
		public final Class<? extends Event> eventType;
		public final EventPriority priority;
		public final Invoke invoke;
		public final IEventListener listenerIn;
		private Throwable lastError = null;
		public boolean isListening = false;

		public SheepListener(EventBus bus, Class<? extends Event> eventType, EventPriority priority, Invoke invoke){
			this.bus = bus;
			this.eventType = eventType;
			this.priority = priority;
			this.invoke = invoke;
			listenerIn = (event1)->invoke(event1);
		}

		public void invoke(Event event) {
			if(lastError==null) {
				try {
					this.invoke.invoke(event);
				} catch (Throwable error) {
					lastError = error;
				}
			}
		}

		public void startListen() throws IllegalAccessException, InstantiationException {
			if(isListening)
				return;
			eventType.newInstance().getListenerList().register(EventListenHandler.busIDField.getInt(bus),priority,listenerIn);
			isListening = true;
		}

		public void endListen() throws IllegalAccessException, InstantiationException {
			if(!isListening)
				return;
			eventType.newInstance().getListenerList().unregister(EventListenHandler.busIDField.getInt(bus),listenerIn);
			isListening = false;
		}

		@Nullable
		public Throwable getLastError(){
			return lastError;
		}

		public static interface Invoke{
			public void invoke(Event event);
		}
	}

}
