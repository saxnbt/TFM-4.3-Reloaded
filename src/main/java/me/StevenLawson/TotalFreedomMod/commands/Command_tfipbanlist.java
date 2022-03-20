package me.StevenLawson.TotalFreedomMod.commands;

import me.StevenLawson.TotalFreedomMod.Log;
import me.StevenLawson.TotalFreedomMod.admin.AdminList;
import me.StevenLawson.TotalFreedomMod.ban.BanManager;
import me.StevenLawson.TotalFreedomMod.util.Utilities;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

@CommandPermissions(level = AdminLevel.OP, source = SourceType.BOTH)
@CommandParameters(description = "Shows all banned IPs. Superadmins may optionally use 'purge' to clear the list.", usage = "/<command> [purge]")
public class Command_tfipbanlist extends FreedomCommand {
    @Override
    public boolean run(CommandSender sender, org.bukkit.entity.Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole) {
        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("purge")) {
                if (senderIsConsole || AdminList.isSuperAdmin(sender)) {
                    try {
                        BanManager.purgeIpBans();
                        Utilities.adminAction(sender.getName(), "Purging the IP ban list", true);

                        sender.sendMessage(ChatColor.GRAY + "IP ban list has been purged.");
                    }
                    catch (Exception ex)
                    {
                        Log.severe(ex);
                    }

                    return true;
                }
                else
                {
                    playerMsg("You do not have permission to purge the IP ban list, you may only view it.");
                }
            }
        }

        playerMsg(BanManager.getIpBanList().size() + " IPbans total");

        return true;
    }
}
