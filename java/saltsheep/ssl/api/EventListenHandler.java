package saltsheep.ssl.api;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventBus;
import net.minecraftforge.fml.common.eventhandler.EventPriority;

import javax.annotation.Nullable;
import java.lang.reflect.Field;


public class EventListenHandler {
    public static Field busIDField = null;

    static {
        try {
            busIDField = EventBus.class.getDeclaredField("busID");
            busIDField.setAccessible(true);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public static Class<?> findClass(String className) throws ClassNotFoundException {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {

            if (!className.contains("."))
                throw new ClassNotFoundException();
            char[] asChars = className.toCharArray();
            for (int i = asChars.length - 1; i >= 0; i--) {
                if (asChars[i] == '.') {
                    asChars[i] = '$';
                    break;
                }
            }
            className = new String(asChars);
            return findClass(className);
        }
    }

    @Deprecated
    public static EventListenerHandler.SheepListener listen(@Nullable EventBus bus, EventPriority priority, String eventType, EventListenerHandler.SheepListener.Invoke invoke) {
        try {
            Class<? extends Event> clazz = (Class) findClass(eventType);
            return listen(bus, priority, clazz, invoke);
        } catch (Throwable error) {
            error.printStackTrace();
            return null;
        }
    }

    public static EventListenerHandler.SheepListener listen(@Nullable EventBus bus, EventPriority priority, Class<? extends Event> eventType, EventListenerHandler.SheepListener.Invoke invoke) {
        try {
            bus = (bus == null) ? MinecraftForge.EVENT_BUS : bus;
            EventListenerHandler.SheepListener listener = new EventListenerHandler.SheepListener(bus, eventType, priority, invoke);
            listener.startListen();
            return listener;
        } catch (Throwable e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void unListen(EventListenerHandler.SheepListener listener) {
        try {
            if (listener == null)
                return;
            listener.endListen();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}