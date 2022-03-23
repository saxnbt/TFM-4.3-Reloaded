package me.StevenLawson.TotalFreedomMod.commands;

import me.StevenLawson.TotalFreedomMod.config.ConfigurationEntry;
import me.StevenLawson.TotalFreedomMod.config.MainConfig;
import me.StevenLawson.TotalFreedomMod.util.SynchronousUtil;
import me.StevenLawson.TotalFreedomMod.util.Utilities;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;

@CommandPermissions(level = AdminLevel.SUPER, source = SourceType.BOTH)
@CommandParameters(description = "For developers only - update TFM.", usage = "/<command>", aliases = ("tfmupdate,update,compiletfm,tfmcompile,build,buildtfm,tfmbuild"))
public class Command_updatetfm extends FreedomCommand {
    @Override
    public boolean run(CommandSender sender, org.bukkit.entity.Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole) {
        playerMsg("Compiling TFM, please wait...", ChatColor.RED);
        Utilities.adminAction(sender.getName(), "Updating TFM, lag will occur", true);
        String path = MainConfig.getString(ConfigurationEntry.TFM_BUILD_SHELLSCRIPT);
        File file = new File(path);
        String directory = file.getParent();

        try {
            String command = String.format("cd %s && ./%s", directory, file.getName());
            ProcessBuilder proc = new ProcessBuilder("/bin/bash", "-c", command);
            Process process = proc.start();
            process.waitFor();
            playerMsg("Compiling TFM! Reloading...");
            Utilities.adminAction(sender.getName(), "Compilation successful, reloading TFM...", false);
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                onlinePlayer.kickPlayer(ChatColor.RED + "Reloading TFM, please rejoin.");
            }

            Bukkit.dispatchCommand(sender, "plugman reload TotalFreedomMod");
        } catch (Exception e) {
            playerMsg("Failed to update TFM! Check the logs for more details.");
            Utilities.adminAction(sender.getName(), "Could not update TFM", true);
            e.printStackTrace();
        }


        return true;
    }
}