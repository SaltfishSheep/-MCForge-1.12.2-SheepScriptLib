package saltsheep.ssl.api;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventBus;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.IEventListener;
import noppes.npcs.controllers.ScriptController;

import javax.annotation.Nullable;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import java.lang.reflect.Field;

public class EventListenHandler {

    /*
    private static ScriptEngine jsListenFC;
    static{
        try {
            jsListenFC = ScriptController.Instance.getEngineByName("ecmascript");
            jsListenFC.eval("var SheepListener = Java.type(\"saltsheep.ssl.api.EventListenerHandler.SheepListener\");\n" +
                    "var MinecraftForge=Java.type(\"net.minecraftforge.common.MinecraftForge\");\n" +
                    "var IEventListener;\n" +
                    "var EventPriority;\n" +
                    "try{\n" +
                    "   var IEventListener=Java.type(\"net.minecraftforge.fml.common.eventhandler.IEventListener\");\n" +
                    "   var EventPriority=Java.type(\"net.minecraftforge.fml.common.eventhandler.EventPriority\");\n" +
                    "}catch(err){\n" +
                    "   var IEventListener=Java.type(\"cpw.mods.fml.common.eventhandler.IEventListener\");\n" +
                    "   var EventPriority=Java.type(\"cpw.mods.fml.common.eventhandler.EventPriority\");\n" +
                    "}\n" +
                    "function listenEvent(eventType,priority,fc,bus){\n" +
                    "   var Event=Java.type(eventType);\n" +
                    "   var event=new Event();\n" +
                    "   var sheepListener = new SheepListener(bus,eventType,fc);\n" +
                    "   var EventListener=Java.extend(IEventListener,{\n" +
                    "      invoke:function(event){\n" +
                    "         sheepListener.invoke(event);\n" +
                    "      }\n" +
                    "   });\n" +
                    "   sheepListener.listenerIn = new EventListener();\n" +
                    "   var busID=bus.class.getDeclaredField(\"busID\");\n" +
                    "   busID.setAccessible(true);\n" +
                    "   busID=busID.getInt(bus);\n" +
                    "   event.getListenerList().register(busID,priority,sheepListener.listenerIn);\n" +
                    "   return sheepListener;\n" +
                    "}\n" +
                    "function unListenEvent(listener){\n" +
                    " var Event=Java.type(listener.eventType);\n" +
                    " var event=new Event();\n" +
                    " var bus=listener.bus;\n" +
                    " var busID=bus.class.getDeclaredField(\"busID\");\n" +
                    " busID.setAccessible(true);\n" +
                    " busID=busID.getInt(bus);\n" +
                    " event.getListenerList().unregister(busID,listener.listenerIn);\n" +
                    "}");
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
    */

    public static Field busIDField = null;
    static{
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
            //*Find inner class.
            if(!className.contains("."))
                throw new ClassNotFoundException();
            char[] asChars = className.toCharArray();
            replace:for(int i=asChars.length-1;i>=0;i--) {
                if (asChars[i] == '.') {
                    asChars[i] = '$';
                    break replace;
                }
            }
            className = new String(asChars);
            return findClass(className);
        }
    }

    @Deprecated
    public static EventListenerHandler.SheepListener listen(@Nullable EventBus bus, EventPriority priority, String eventType, EventListenerHandler.SheepListener.Invoke invoke){
        try {
            Class<? extends Event> clazz = (Class<? extends Event>) findClass(eventType);
            return listen(bus,priority,clazz,invoke);
        }catch (Throwable error){
            error.printStackTrace();
            return null;
        }
    }

    public static EventListenerHandler.SheepListener listen(@Nullable EventBus bus, EventPriority priority, Class<? extends Event> eventType, EventListenerHandler.SheepListener.Invoke invoke) {
        try {
            bus = bus == null ? MinecraftForge.EVENT_BUS : bus;
            EventListenerHandler.SheepListener listener = new EventListenerHandler.SheepListener(bus,eventType,priority,invoke);
            listener.startListen();
            return listener;
        } catch (Throwable e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void unListen(EventListenerHandler.SheepListener listener){
        try {
            if(listener==null)
                return;
            listener.endListen();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

}
