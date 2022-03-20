package me.StevenLawson.TotalFreedomMod.commands;

import me.StevenLawson.TotalFreedomMod.admin.AdminList;
import me.StevenLawson.TotalFreedomMod.ban.Ban;
import me.StevenLawson.TotalFreedomMod.ban.BanManager;
import me.StevenLawson.TotalFreedomMod.config.ConfigurationEntry;
import me.StevenLawson.TotalFreedomMod.player.Player;
import me.StevenLawson.TotalFreedomMod.player.PlayerList;
import me.StevenLawson.TotalFreedomMod.player.UUIDManager;
import me.StevenLawson.TotalFreedomMod.util.Utilities;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

@CommandPermissions(level = AdminLevel.SUPER, source = SourceType.BOTH)
@CommandParameters(description = "Ban/Unban any player, even those who are not logged in anymore.", usage = "/<command> <purge | <ban | unban> <username>>")
public class Command_glist extends FreedomCommand {
    @Override
    public boolean run(CommandSender sender, org.bukkit.entity.Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole) {
        if (args.length < 1) {
            return false;
        }

        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("purge")) {
                if (AdminList.isSeniorAdmin(sender))
                {
                    PlayerList.purgeAll();
                    playerMsg("Purged playerbase");
                }
                else
                {
                    playerMsg("Only Senior Admins may purge the userlist.");
                }
                return true;
            }
            else
            {
                return false;
            }
        }
        else if (args.length == 2)
        {
            String username;
            final List<String> ips = new ArrayList<String>();

            final org.bukkit.entity.Player player = getPlayer(args[1]);

            if (player == null)
            {
                final me.StevenLawson.TotalFreedomMod.player.Player entry = PlayerList.getEntry(UUIDManager.getUniqueId(args[1]));

                if (entry == null) {
                    playerMsg("Can't find that user. If target is not logged in, make sure that you spelled the name exactly.");
                    return true;
                }

                username = entry.getLastLoginName();
                for (String ip : entry.getIps()) {
                    ips.add(Utilities.getFuzzyIp(ip));
                }
            }
            else
            {
                username = player.getName();
                final Player entry = PlayerList.getEntry(UUIDManager.getUniqueId(player));

                for (String ip : entry.getIps()) {
                    ips.add(Utilities.getFuzzyIp(ip));
                }

            }

            String mode = args[0].toLowerCase();
            if (mode.equalsIgnoreCase("ban"))
            {
                Utilities.adminAction(sender.getName(), "Banning " + username + " and IPs: " + StringUtils.join(ips, ", "), true);

                final org.bukkit.entity.Player target = getPlayer(username, true);
                if (target != null)
                {
                    BanManager.addUuidBan(new Ban(UUIDManager.getUniqueId(target), target.getName()));
                    target.kickPlayer("You have been banned by " + sender.getName() + "\n If you think you have been banned wrongly, appeal here: " + ConfigurationEntry.SERVER_BAN_URL.getString());
                }
                else
                {
                    BanManager.addUuidBan(new Ban(UUIDManager.getUniqueId(username), username));
                }

                for (String ip : ips)
                {
                    BanManager.addIpBan(new Ban(ip, username));
                    BanManager.addIpBan(new Ban(Utilities.getFuzzyIp(ip), username));
                }
            }
            else if (mode.equalsIgnoreCase("unban"))
            {
                Utilities.adminAction(sender.getName(), "Unbanning " + username + " and IPs: " + StringUtils.join(ips, ", "), true);
                BanManager.unbanUuid(UUIDManager.getUniqueId(username));
                for (String ip : ips)
                {

                    BanManager.unbanIp(ip);
                    BanManager.unbanIp(Utilities.getFuzzyIp(ip));
                }
            }
            else
            {
                return false;
            }

            return true;
        }
        else
        {
            return false;
        }
    }
}
