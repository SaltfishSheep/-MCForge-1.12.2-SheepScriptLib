package saltsheep.ssl;

import net.minecraft.command.ICommand;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.*;
import noppes.npcs.api.wrapper.WrapperNpcAPI;
import noppes.npcs.controllers.ServerCloneController;
import saltsheep.ssl.api.*;
import saltsheep.ssl.command.CommandSSLReloadScript;
import saltsheep.ssl.event.ForgeEventHandler;
import saltsheep.ssl.event.NPCEventSubscriber;
import saltsheep.ssl.network.NetworkHandler;
import saltsheep.ssl.puppet.handler.NeoPuppetEventHandler;
import saltsheep.ssl.script.ScriptLoader;

public class Proxy {
    public static void preInit() {
        NetworkHandler.register();
        WrapperNpcAPI.EVENT_BUS.register(NPCEventSubscriber.subscriber);
        MinecraftForge.EVENT_BUS.register(AnimationHandler.subscriber);
        MinecraftForge.EVENT_BUS.register(CommonEntityData.Handler.subscriber);
        if (SheepScriptLibConfig.sheepAI_enable)
            MinecraftForge.EVENT_BUS.register(SheepAIHandler.subscriber);
        if (SheepScriptLibConfig.task_enable)
            MinecraftForge.EVENT_BUS.register(TaskHandler.instance);
        if (SheepScriptLibConfig.sheepBothSideScript_Use)
            ScriptLoader.loader.load();
        MinecraftForge.EVENT_BUS.register(ForgeEventHandler.subscriber);
        MinecraftForge.EVENT_BUS.register(NeoPuppetEventHandler.subscriber);
    }


    public static void init() {
    }


    public static void aboutToStartServer(FMLServerAboutToStartEvent event) {
        if (SheepScriptLibConfig.sheepBothSideScript_Use)
            ScriptLoader.loader.invokeFMLServerEvents((FMLEvent) event);
    }

    public static void startingServer(FMLServerStartingEvent event) {
        if (SheepScriptLibConfig.sheepBothSideScript_Use) {
            event.registerServerCommand((ICommand) new CommandSSLReloadScript());
            ScriptLoader.loader.invokeFMLServerEvents((FMLEvent) event);
        }
    }

    public static void startedServer(FMLServerStartedEvent event) {
        if (SheepScriptLibConfig.betterClone_Use)
            ServerCloneController.Instance = (ServerCloneController) new BetterCloneController();
        if (SheepScriptLibConfig.sheepBothSideScript_Use)
            ScriptLoader.loader.invokeFMLServerEvents((FMLEvent) event);
    }

    public static void stoppingServer(FMLServerStoppingEvent event) {
        if (SheepScriptLibConfig.sheepBothSideScript_Use)
            ScriptLoader.loader.invokeFMLServerEvents((FMLEvent) event);
        if (SheepScriptLibConfig.sheepAI_enable)
            SheepAIHandler.resetOnServerStopping();
        if (SheepScriptLibConfig.task_enable)
            TaskHandler.instance.resetOnServerStopping();
    }

    public static void stoppedServer(FMLServerStoppedEvent event) {
        if (SheepScriptLibConfig.sheepBothSideScript_Use)
            ScriptLoader.loader.invokeFMLServerEvents((FMLEvent) event);
        if (SheepScriptLibConfig.neoPuppet_enable)
            NeoPuppetEventHandler.subscriber.onServerStopped(event);
        CommonEntityData.Handler.subscriber.onServerStopped(event);
    }
}