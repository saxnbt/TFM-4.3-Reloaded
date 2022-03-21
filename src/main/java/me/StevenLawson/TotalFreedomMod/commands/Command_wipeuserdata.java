package me.StevenLawson.TotalFreedomMod.commands;

import me.StevenLawson.TotalFreedomMod.util.Utilities;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.io.File;

@CommandPermissions(level = AdminLevel.SENIOR, source = SourceType.ONLY_CONSOLE)
@CommandParameters(description = "Removes essentials playerdata", usage = "/<command>")
public class Command_wipeuserdata extends FreedomCommand {
    @Override
    public boolean run(CommandSender sender, org.bukkit.entity.Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole) {
        if (!server.getPluginManager().isPluginEnabled("Essentials")) {
            playerMsg("Essentials is not enabled on this server");
            return true;
        }

        Utilities.adminAction(sender.getName(), "Wiping Essentials playerdata", true);

        Utilities.deleteFolder(new File(server.getPluginManager().getPlugin("Essentials").getDataFolder(), "userdata"));

        playerMsg("All playerdata deleted.");
        return true;
    }
}
