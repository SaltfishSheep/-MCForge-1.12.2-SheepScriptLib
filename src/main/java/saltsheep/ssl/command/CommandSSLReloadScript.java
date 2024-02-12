package saltsheep.ssl.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import saltsheep.ssl.script.ScriptLoader;

public class CommandSSLReloadScript extends CommandBase {
    @Override
    public String getName() {
        return "sslreloadscript";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "§c/sslreloadscript";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        ScriptLoader.loader.unload();
        ScriptLoader.loader.load();
        sender.sendMessage(new TextComponentString("Reload scripts successfully, only useful to the owner of server."));
    }
}
