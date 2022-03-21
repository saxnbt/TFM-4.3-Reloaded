package me.StevenLawson.TotalFreedomMod.commands;

import me.StevenLawson.TotalFreedomMod.admin.AdminList;
import me.StevenLawson.TotalFreedomMod.util.Utilities;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = AdminLevel.SENIOR, source = SourceType.BOTH, blockHostConsole = true)
public class Command_kicknoob extends FreedomCommand {
    @Override
    public boolean run(CommandSender sender, org.bukkit.entity.Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole) {
        Utilities.adminAction(sender.getName(), "Disconnecting all non-superadmins.", true);

        for (Player player : server.getOnlinePlayers()) {
            if (!AdminList.isSuperAdmin(player)) {
                player.kickPlayer(ChatColor.RED + "Disconnected by admin.");
            }
        }

        return true;
    }
}
