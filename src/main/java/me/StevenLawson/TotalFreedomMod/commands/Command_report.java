package me.StevenLawson.TotalFreedomMod.commands;

import me.StevenLawson.TotalFreedomMod.admin.AdminList;
import me.StevenLawson.TotalFreedomMod.util.Utilities;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = AdminLevel.OP, source = SourceType.ONLY_IN_GAME, blockHostConsole = true)
public class Command_report extends FreedomCommand {
    @Override
    public boolean run(CommandSender sender, org.bukkit.entity.Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole) {
        if (args.length < 2) {
            return false;
        }

        Player player = getPlayer(args[0]);

        if (player == null) {
            playerMsg(sender, PLAYER_NOT_FOUND);
            return true;
        }

        if (sender instanceof Player)
        {
            if (player.equals(sender_p))
            {
                playerMsg(sender, ChatColor.RED + "Please, don't try to report yourself.");
                return true;
            }
        }

        if (AdminList.isSuperAdmin(player))
        {
            playerMsg(sender, ChatColor.RED + "You can not report an admin.");
            return true;
        }

        String report = StringUtils.join(ArrayUtils.subarray(args, 1, args.length), " ");
        Utilities.reportAction(sender_p, player, report);

        playerMsg(sender, ChatColor.GREEN + "Thank you, your report has been successfully logged.");

        return true;
    }
}
