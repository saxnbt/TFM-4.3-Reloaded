package me.StevenLawson.TotalFreedomMod.commands;

import me.StevenLawson.TotalFreedomMod.Log;
import me.StevenLawson.TotalFreedomMod.admin.AdminList;
import me.StevenLawson.TotalFreedomMod.ban.BanManager;
import me.StevenLawson.TotalFreedomMod.util.Utilities;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

@CommandPermissions(level = AdminLevel.OP, source = SourceType.BOTH)
public class Command_tfbanlist extends FreedomCommand {
    @Override
    public boolean run(CommandSender sender, org.bukkit.entity.Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole) {
        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("purge")) {
                if (senderIsConsole || AdminList.isSuperAdmin(sender)) {
                    try {
                        Utilities.adminAction(sender.getName(), "Purging the ban list", true);
                        BanManager.purgeUuidBans();
                        sender.sendMessage(ChatColor.GRAY + "Ban list has been purged.");
                    }
                    catch (Exception ex)
                    {
                        Log.severe(ex);
                    }

                    return true;
                }
                else
                {
                    playerMsg(sender, "You do not have permission to purge the ban list, you may only view it.");
                }
            }
        }

        playerMsg(sender, BanManager.getUuidBanList().size() + " UUID bans total");

        return true;
    }
}
