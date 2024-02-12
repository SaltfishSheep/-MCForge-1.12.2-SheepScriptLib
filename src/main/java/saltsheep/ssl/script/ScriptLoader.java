package saltsheep.ssl.script;

import com.google.common.collect.Lists;
import jdk.nashorn.api.scripting.NashornScriptEngineFactory;
import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.fml.common.event.FMLEvent;
import noppes.npcs.controllers.ScriptController;
import org.apache.logging.log4j.Logger;
import saltsheep.ssl.SheepScriptLib;

import javax.annotation.Nullable;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;
import java.io.File;
import java.util.List;

public class ScriptLoader {

    public static NashornScriptEngineFactory engineFactory;

    static{
        Launch.classLoader.addClassLoaderExclusion("jdk.nashorn.");
        Launch.classLoader.addClassLoaderExclusion("jdk.internal.dynalink");
        try {
            Class clazz = Class.forName("jdk.nashorn.api.scripting.NashornScriptEngineFactory");
            engineFactory = (NashornScriptEngineFactory) clazz.newInstance();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
    }

    public static final ScriptLoader loader = new ScriptLoader();

    private final File scriptPacket = new File(".","SheepBothScripts");

    private List<ScriptContainer> containers = Lists.newLinkedList();

    @Nullable
    public ScriptContainer getContainer(String containerName){
        for(ScriptContainer container:containers)
            if(container.containerName.equals(containerName))
                return container;
        return null;
    }

    public void invokeFMLServerEvents(FMLEvent event){
        for(ScriptContainer container:containers)
            container.invokeFMLServerEvents(event);
    }

    public void unload() {
        SheepScriptLib.getLogger().info("Unloading both side scripts.");
        for(ScriptContainer sc:containers)
            sc.unload();
        containers.clear();
        SheepScriptLib.getLogger().info("Successfully unloading both side scripts.");
    }

    public void load() {
        SheepScriptLib.getLogger().info("Loading both side scripts.");
        if(scriptPacket.exists()&&!scriptPacket.isDirectory())
            throw new RuntimeException("There's a file occupied SheepBothScripts packet's place. Please delete it.");
        if(!scriptPacket.exists())
            if(!scriptPacket.mkdirs())
                throw new RuntimeException("Because of unknown reason, the packet(SheepBothScripts in game root packet) was failed to create. How can that be?");
        loadPacket(scriptPacket);
        SheepScriptLib.getLogger().info("Successfully loading both side scripts.");
    }

    private void loadPacket(File packet){
        for(File file:packet.listFiles()){
            if(file.isDirectory()){
                loadPacket(file);
                continue;
            }else if(file.getName().endsWith(".ssl.js")){
                loadScript(file);
            }
        }
    }

    private void loadScript(File script){
        ScriptContainer container = new ScriptContainer(script);
        containers.add(container);
        Logger log = SheepScriptLib.getLogger();
        log.info("Loading script, file name:"+script.getName()+", container name:"+container.containerName+", script state:"+(container.getLastError()==null?"running":"error"));
    }

}
