package me.StevenLawson.TotalFreedomMod.commands;

import me.StevenLawson.TotalFreedomMod.Server;
import me.StevenLawson.TotalFreedomMod.admin.AdminList;
import me.StevenLawson.TotalFreedomMod.util.DeprecationUtil;
import me.StevenLawson.TotalFreedomMod.util.Utilities;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = AdminLevel.OP, source = SourceType.BOTH)
public class Command_whitelist extends FreedomCommand {
    @Override
    public boolean run(CommandSender sender, org.bukkit.entity.Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole) {
        if (args.length < 1) {
            return false;
        }

        // list
        if (args[0].equalsIgnoreCase("list")) {
            playerMsg(sender, "Whitelisted players: " + Utilities.playerListToNames(server.getWhitelistedPlayers()));
            return true;
        }

        // count
        if (args[0].equalsIgnoreCase("count"))
        {
            int onlineWPs = 0;
            int offlineWPs = 0;
            int totalWPs = 0;

            for (OfflinePlayer player : server.getWhitelistedPlayers())
            {
                if (player.isOnline())
                {
                    onlineWPs++;
                }
                else
                {
                    offlineWPs++;
                }
                totalWPs++;
            }

            playerMsg(sender, "Online whitelisted players: " + onlineWPs);
            playerMsg(sender, "Offline whitelisted players: " + offlineWPs);
            playerMsg(sender, "Total whitelisted players: " + totalWPs);

            return true;
        }

        // all commands past this line are superadmin-only
        if (!(senderIsConsole || AdminList.isSuperAdmin(sender)))
        {
            sender.sendMessage(FreedomCommand.MSG_NO_PERMS);
            return true;
        }

        // on
        if (args[0].equalsIgnoreCase("on"))
        {
            Utilities.adminAction(sender.getName(), "Turning the whitelist on.", true);
            server.setWhitelist(true);
            return true;
        }

        // off
        if (args[0].equalsIgnoreCase("off"))
        {
            Utilities.adminAction(sender.getName(), "Turning the whitelist off.", true);
            server.setWhitelist(false);
            return true;
        }

        // add
        if (args[0].equalsIgnoreCase("add"))
        {
            if (args.length < 2)
            {
                return false;
            }

            String search_name = args[1].trim().toLowerCase();

            OfflinePlayer player = getPlayer(search_name);

            if (player == null)
            {
                player = DeprecationUtil.getOfflinePlayer(server, search_name);
            }

            Utilities.adminAction(sender.getName(), "Adding " + player.getName() + " to the whitelist.", false);
            player.setWhitelisted(true);
            return true;
        }

        // remove
        if ("remove".equals(args[0]))
        {
            if (args.length < 2)
            {
                return false;
            }

            String search_name = args[1].trim().toLowerCase();

            OfflinePlayer player = getPlayer(search_name);

            if (player == null)
            {
                player = DeprecationUtil.getOfflinePlayer(server, search_name);
            }

            if (player.isWhitelisted())
            {
                Utilities.adminAction(sender.getName(), "Removing " + player.getName() + " from the whitelist.", false);
                player.setWhitelisted(false);
                return true;
            }
            else
            {
                playerMsg(sender, "That player is not whitelisted");
                return true;
            }

        }

        // addall
        if (args[0].equalsIgnoreCase("addall"))
        {
            Utilities.adminAction(sender.getName(), "Adding all online players to the whitelist.", false);
            int counter = 0;
            for (Player player : server.getOnlinePlayers())
            {
                if (!player.isWhitelisted())
                {
                    player.setWhitelisted(true);
                    counter++;
                }
            }

            playerMsg(sender, "Whitelisted " + counter + " players.");
            return true;
        }

        // all commands past this line are console/telnet only
        if (!senderIsConsole)
        {
            sender.sendMessage(FreedomCommand.MSG_NO_PERMS);
            return true;
        }

        //purge
        if (args[0].equalsIgnoreCase("purge"))
        {
            Utilities.adminAction(sender.getName(), "Removing all players from the whitelist.", false);
            playerMsg(sender, "Removed " + Server.purgeWhitelist() + " players from the whitelist.");

            return true;
        }

        // none of the commands were executed
        return false;
    }
}
