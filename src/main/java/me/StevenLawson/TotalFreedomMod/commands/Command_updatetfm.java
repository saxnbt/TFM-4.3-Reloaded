package me.StevenLawson.TotalFreedomMod.commands;

import me.StevenLawson.TotalFreedomMod.config.ConfigurationEntry;
import me.StevenLawson.TotalFreedomMod.config.MainConfig;
import me.StevenLawson.TotalFreedomMod.util.Utilities;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.io.File;

@CommandPermissions(level = AdminLevel.SENIOR, source = SourceType.BOTH)
@CommandParameters(description = "For developers only - update TFM.", usage = "/<command>")
public class Command_updatetfm extends FreedomCommand {
    @Override
    public boolean run(CommandSender sender, org.bukkit.entity.Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole) {
        playerMsg("Updating TFM, please wait...", ChatColor.RED);
        Utilities.adminAction(sender.getName(), "Updating TFM", true);
        String path = MainConfig.getString(ConfigurationEntry.TFM_BUILD_SHELLSCRIPT);
        String directory = new File(path).getParent();

        try {
            Process uptimeProc = Runtime.getRuntime().exec(String.format("bash -c \"cd %s; %s\"", directory, path));
            uptimeProc.waitFor();
            playerMsg("Updated TFM! Reloading...");
            Utilities.adminAction(sender.getName(), "Update successful, reloading TFM...", false);
            Bukkit.dispatchCommand(sender, "plugman reload TotalFreedomMod");
        } catch (Exception e) {
            playerMsg("Failed to update TFM! Check the logs for more details.");
            Utilities.adminAction(sender.getName(), "Could not update TFM", true);
            e.printStackTrace();
        }


        return true;
    }
}