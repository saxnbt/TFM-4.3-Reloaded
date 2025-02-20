package me.StevenLawson.TotalFreedomMod.commands;

import me.StevenLawson.TotalFreedomMod.admin.AdminList;
import me.StevenLawson.TotalFreedomMod.player.PlayerData;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = AdminLevel.SUPER, source = SourceType.BOTH, blockHostConsole = true)
@CommandParameters(description = "Warns a player.", usage = "/<command> <player> <reason>")
public class Command_warn extends FreedomCommand {
    @Override
    public boolean run(CommandSender sender, org.bukkit.entity.Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole) {
        if (args.length < 2) {
            return false;
        }

        Player player = getPlayer(args[0]);

        if (player == null) {
            playerMsg(PLAYER_NOT_FOUND);
            return true;
        }

        if (sender instanceof Player)
        {
            if (player.equals(sender_p))
            {
                playerMsg(ChatColor.RED + "Please, don't try to warn yourself.");
                return true;
            }
        }

        if (AdminList.isSuperAdmin(player))
        {
            playerMsg(ChatColor.RED + "You can not warn admins");
            return true;
        }

        String warnReason = StringUtils.join(ArrayUtils.subarray(args, 1, args.length), " ");

        playerMsg(ChatColor.RED + "[WARNING] " + warnReason);
        playerMsg(ChatColor.GREEN + "You have successfully warned " + player.getName());

        PlayerData.getPlayerData(player).incrementWarnings();

        return true;
    }
}
