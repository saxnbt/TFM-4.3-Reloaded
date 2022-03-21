package me.StevenLawson.TotalFreedomMod.commands;

import me.StevenLawson.TotalFreedomMod.Log;
import me.StevenLawson.TotalFreedomMod.TotalFreedomMod;
import me.StevenLawson.TotalFreedomMod.admin.AdminList;
import me.StevenLawson.TotalFreedomMod.ban.BanManager;
import me.StevenLawson.TotalFreedomMod.ban.PermbanList;
import me.StevenLawson.TotalFreedomMod.command.CommandBlocker;
import me.StevenLawson.TotalFreedomMod.config.ConfigurationEntry;
import me.StevenLawson.TotalFreedomMod.config.MainConfig;
import me.StevenLawson.TotalFreedomMod.player.PlayerList;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

@CommandPermissions(level = AdminLevel.ALL, source = SourceType.BOTH)
public class CommandTFM extends FreedomCommand {
    @Override
    public boolean run(CommandSender sender, org.bukkit.entity.Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole) {
        if (args.length == 1) {
            if (!args[0].equals("reload")) {
                return false;
            }

            if (!AdminList.isSuperAdmin(sender)) {
                playerMsg(FreedomCommand.MSG_NO_PERMS);
                return true;
            }

            MainConfig.load();
            AdminList.load();
            PermbanList.load();
            PlayerList.load();
            BanManager.load();
            CommandBlocker.load();

            final String message = String.format("%s v%s.%s reloaded.",
                    TotalFreedomMod.pluginName,
                    TotalFreedomMod.pluginVersion,
                    TotalFreedomMod.buildNumber);

            playerMsg(sender, message);
            Log.info(message);
            return true;
        }

        playerMsg(sender, "TotalFreedomMod for 'Total Freedom', the original all-op server.", ChatColor.GOLD);
        playerMsg(sender, String.format("Version "
                + ChatColor.BLUE + "%s.%s" + ChatColor.GOLD + ", built "
                + ChatColor.BLUE + "%s" + ChatColor.GOLD + " by "
                + ChatColor.BLUE + "%s" + ChatColor.GOLD + ".",
                TotalFreedomMod.pluginVersion,
                TotalFreedomMod.buildNumber,
                TotalFreedomMod.buildDate,
                TotalFreedomMod.buildCreator), ChatColor.GOLD);
        playerMsg(sender,"Running on " + ConfigurationEntry.SERVER_NAME.getString() + ".", ChatColor.GOLD);
        playerMsg(sender,"Created by Madgeek1450 and Prozza.", ChatColor.GOLD);
        playerMsg(sender,"Visit " + ChatColor.AQUA + "http://totalfreedom.me/" + ChatColor.GREEN + " for more information.", ChatColor.GREEN);

        return true;
    }
}
