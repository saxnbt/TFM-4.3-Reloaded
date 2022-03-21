package me.StevenLawson.TotalFreedomMod.commands;

import me.StevenLawson.TotalFreedomMod.util.Utilities;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = AdminLevel.SENIOR, source = SourceType.ONLY_CONSOLE, blockHostConsole = true)
@CommandParameters(description = "Wipe the flatlands map. Requires manual restart after command is used.", usage = "/<command>")
public class Command_wipeflatlands extends FreedomCommand {
    @Override
    public boolean run(final CommandSender sender, org.bukkit.entity.Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole) {
        Utilities.setSavedFlag("do_wipe_flatlands", true);

        Utilities.bcastMsg("Server is going offline for flatlands wipe.", ChatColor.GRAY);

        for (Player player : server.getOnlinePlayers()) {
            player.kickPlayer("Server is going offline for flatlands wipe, come back in a few minutes.");
        }

        server.shutdown();

        return true;
    }
}
