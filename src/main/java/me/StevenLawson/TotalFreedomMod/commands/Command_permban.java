package me.StevenLawson.TotalFreedomMod.commands;

import me.StevenLawson.TotalFreedomMod.ban.PermbanList;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

@CommandPermissions(level = AdminLevel.SUPER, source = SourceType.BOTH, blockHostConsole = true)
public class Command_permban extends FreedomCommand {
    @Override
    public boolean run(CommandSender sender, org.bukkit.entity.Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole) {
        if (args.length != 1) {
            return false;
        }

        if (args[0].equalsIgnoreCase("list")) {
            dumplist(sender);
        }
        else if (args[0].equalsIgnoreCase("reload"))
        {
            if (!senderIsConsole)
            {
                sender.sendMessage(FreedomCommand.MSG_NO_PERMS);
                return true;
            }
            playerMsg(sender, "Reloading permban list...", ChatColor.RED);
            PermbanList.load();
            dumplist(sender);
        }
        else
        {
            return false;
        }

        return true;
    }

    private void dumplist(CommandSender sender)
    {
        if (PermbanList.getPermbannedPlayers().isEmpty())
        {
            playerMsg(sender, "No permanently banned player names.");
        }
        else
        {
            playerMsg(sender, PermbanList.getPermbannedPlayers().size() + " permanently banned players:");
            playerMsg(sender, StringUtils.join(PermbanList.getPermbannedPlayers(), ", "));
        }

        if (PermbanList.getPermbannedIps().isEmpty())
        {
            playerMsg(sender, "No permanently banned IPs.");
        }
        else
        {
            playerMsg(sender, PermbanList.getPermbannedIps().size() + " permanently banned IPs:");
            playerMsg(sender, StringUtils.join(PermbanList.getPermbannedIps(), ", "));
        }
    }
}
