package me.StevenLawson.TotalFreedomMod.commands;

import me.StevenLawson.TotalFreedomMod.admin.AdminList;
import me.StevenLawson.TotalFreedomMod.util.DeprecationUtil;
import me.StevenLawson.TotalFreedomMod.util.Utilities;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = AdminLevel.OP, source = SourceType.BOTH)
public class Command_op extends FreedomCommand {
    @Override
    public boolean run(CommandSender sender, org.bukkit.entity.Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole) {
        if (args.length != 1) {
            return false;
        }

        if (args[0].equalsIgnoreCase("all") || args[0].equalsIgnoreCase("everyone")) {
            playerMsg(sender, "Correct usage: /opall");
            return true;
        }

        OfflinePlayer player = null;
        for (Player onlinePlayer : server.getOnlinePlayers())
        {
            if (args[0].equalsIgnoreCase(onlinePlayer.getName()))
            {
                player = onlinePlayer;
            }
        }

        // if the player is not online
        if (player == null)
        {
            if (AdminList.isSuperAdmin(sender) || senderIsConsole)
            {
                player = DeprecationUtil.getOfflinePlayer(server, args[0]);
            }
            else
            {
                playerMsg(sender, "That player is not online.");
                playerMsg(sender, "You don't have permissions to OP offline players.", ChatColor.YELLOW);
                return true;
            }
        }

        Utilities.adminAction(sender.getName(), "Opping " + player.getName(), false);
        player.setOp(true);

        return true;
    }
}
