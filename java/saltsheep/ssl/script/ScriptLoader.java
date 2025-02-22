package saltsheep.ssl.script;

import com.google.common.collect.Lists;
import jdk.nashorn.api.scripting.NashornScriptEngineFactory;
import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.fml.common.event.FMLEvent;
import org.apache.logging.log4j.Logger;
import saltsheep.ssl.SheepScriptLib;
import saltsheep.ssl.SheepScriptLibConfig;

import javax.annotation.Nullable;
import javax.script.ScriptException;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class ScriptLoader {
    public static final ScriptLoader loader = new ScriptLoader();
    public static NashornScriptEngineFactory engineFactory;

    static {
        Launch.classLoader.addClassLoaderExclusion("jdk.nashorn.");
        Launch.classLoader.addClassLoaderExclusion("jdk.internal.dynalink");
        try {
            Class<?> clazz = Class.forName("jdk.nashorn.api.scripting.NashornScriptEngineFactory");
            engineFactory = (NashornScriptEngineFactory) clazz.newInstance();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
    }

    private final File scriptPacket = new File(".", "SheepBothScripts");

    private final List<ScriptContainer> containers = Lists.newLinkedList();

    @Nullable
    public ScriptContainer getContainer(String containerName) {
        for (ScriptContainer container : this.containers) {
            if (container.containerName.equals(containerName))
                return container;
        }
        return null;
    }

    public void invokeFMLServerEvents(FMLEvent event) {
        for (ScriptContainer container : this.containers)
            container.invokeFMLServerEvents(event);
    }

    public void unload() {
        SheepScriptLib.getLogger().info("Unloading both side scripts.");
        for (ScriptContainer sc : this.containers)
            sc.unload();
        this.containers.clear();
        SheepScriptLib.getLogger().info("Successfully unloading both side scripts.");
    }

    public void load() {
        SheepScriptLib.getLogger().info("Loading both side scripts.");
        if (this.scriptPacket.exists() && !this.scriptPacket.isDirectory())
            throw new RuntimeException("There's a file occupied SheepBothScripts packet's place. Please delete it.");
        if (!this.scriptPacket.exists() &&
                !this.scriptPacket.mkdirs())
            throw new RuntimeException("Because of unknown reason, the packet(SheepBothScripts in game root packet) was failed to create. How can that be?");
        loadPacket(this.scriptPacket, "");
        SheepScriptLib.getLogger().info("Successfully loading both side scripts.");
    }

    public void loadCore() {
        if (!this.scriptPacket.exists() || !this.scriptPacket.isDirectory())
            return;
        File corePacket = new File(this.scriptPacket, "core");
        if (!corePacket.exists() || !corePacket.isDirectory())
            return;
        try {
            for (File file : corePacket.listFiles())
                if (file.getName().endsWith(".sslcore.js"))
                    engineFactory.getScriptEngine().eval(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
        } catch (ScriptException | FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void loadPacket(File packet, String relativePath) {
        for (File file : packet.listFiles()) {
            if (file.isDirectory()) {
                loadPacket(file, relativePath + file.getName() + '.');
            } else if (file.getName().endsWith(".ssl.js")) {
                loadScript(file, relativePath + file.getName().replace(".ssl.js", ""));
            }
        }
    }

    private void loadScript(File script, String relativePath) {
        ScriptContainer container = new ScriptContainer(script, relativePath);
        this.containers.add(container);
        Logger log = SheepScriptLib.getLogger();
        log.info("Loading script, file name:" + script.getName() + ", container name:" + container.containerName + ", script state:" + ((container.getLastError() == null) ? "running" : "error"));
    }

}