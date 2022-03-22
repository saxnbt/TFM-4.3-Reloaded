package me.StevenLawson.TotalFreedomMod.commands;

import me.StevenLawson.TotalFreedomMod.admin.AdminList;
import me.StevenLawson.TotalFreedomMod.config.ConfigurationEntry;
import me.StevenLawson.TotalFreedomMod.util.Utilities;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = AdminLevel.SUPER, source = SourceType.BOTH, blockHostConsole = true)
@CommandParameters(description = "Close server to non-superadmins.", usage = "/<command> [on | off]")
public class Command_adminmode extends FreedomCommand {
    @Override
    public boolean run(CommandSender sender, org.bukkit.entity.Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole) {
        if (args.length != 1) {
            return false;
        }

        if (args[0].equalsIgnoreCase("off")) {
            ConfigurationEntry.ADMIN_ONLY_MODE.setBoolean(false);
            Utilities.adminAction(sender.getName(), "Deactivating adminmode.", true);
            return true;
        }
        else if (args[0].equalsIgnoreCase("on"))
        {
            ConfigurationEntry.ADMIN_ONLY_MODE.setBoolean(true);
            Utilities.adminAction(sender.getName(), "Activating adminmode.", true);
            for (Player player : server.getOnlinePlayers())
            {
                if (!AdminList.isSuperAdmin(player))
                {
                    player.kickPlayer("Server is now in adminmode.");
                }
            }
            return true;
        }

        return false;
    }
}
