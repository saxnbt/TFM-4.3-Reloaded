package me.StevenLawson.TotalFreedomMod.commands;

import me.StevenLawson.TotalFreedomMod.admin.AdminList;
import me.StevenLawson.TotalFreedomMod.player.PlayerData;
import me.StevenLawson.TotalFreedomMod.util.Utilities;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = AdminLevel.SUPER, source = SourceType.BOTH)
public class Command_stfu extends FreedomCommand {
    @Override
    public boolean run(CommandSender sender, org.bukkit.entity.Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole) {
        if (args.length == 0 || args.length > 2) {
            return false;
        }

        if (args[0].equalsIgnoreCase("list")) {
            playerMsg(sender, "Muted players:");
            PlayerData info;
            int count = 0;
            for (Player mp : server.getOnlinePlayers())
            {
                info = PlayerData.getPlayerData(mp);
                if (info.isMuted())
                {
                    playerMsg(sender, "- " + mp.getName());
                    count++;
                }
            }
            if (count == 0)
            {
                playerMsg(sender, "- none");
            }
        }
        else if (args[0].equalsIgnoreCase("purge"))
        {
            Utilities.adminAction(sender.getName(), "Unmuting all players.", true);
            PlayerData info;
            int count = 0;
            for (Player mp : server.getOnlinePlayers())
            {
                info = PlayerData.getPlayerData(mp);
                if (info.isMuted())
                {
                    info.setMuted(false);
                    count++;
                }
            }
            playerMsg(sender, "Unmuted " + count + " players.");
        }
        else if (args[0].equalsIgnoreCase("all"))
        {
            Utilities.adminAction(sender.getName(), "Muting all non-Superadmins", true);

            PlayerData playerdata;
            int counter = 0;
            for (Player player : server.getOnlinePlayers())
            {
                if (!AdminList.isSuperAdmin(player))
                {
                    playerdata = PlayerData.getPlayerData(player);
                    playerdata.setMuted(true);
                    counter++;
                }
            }

            playerMsg(sender, "Muted " + counter + " players.");
        }
        else
        {
            final Player player = getPlayer(args[0]);

            if (player == null)
            {
                sender.sendMessage(FreedomCommand.PLAYER_NOT_FOUND);
                return true;
            }

            PlayerData playerdata = PlayerData.getPlayerData(player);
            if (playerdata.isMuted())
            {
                Utilities.adminAction(sender.getName(), "Unmuting " + player.getName(), true);
                playerdata.setMuted(false);
                playerMsg(sender, "Unmuted " + player.getName());
            }
            else
            {
                if (!AdminList.isSuperAdmin(player))
                {
                    Utilities.adminAction(sender.getName(), "Muting " + player.getName(), true);
                    playerdata.setMuted(true);

                    if (args.length == 2 && args[1].equalsIgnoreCase("-s"))
                    {
                        Command_smite.smite(player);
                    }

                    playerMsg(sender, "Muted " + player.getName());
                }
                else
                {
                    playerMsg(sender, player.getName() + " is a superadmin, and can't be muted.");
                }
            }
        }

        return true;
    }
}
