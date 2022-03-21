package me.StevenLawson.TotalFreedomMod.commands;

import me.StevenLawson.TotalFreedomMod.admin.AdminList;
import me.StevenLawson.TotalFreedomMod.player.PlayerData;
import me.StevenLawson.TotalFreedomMod.util.Utilities;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = AdminLevel.SUPER, source = SourceType.BOTH)
public class Command_halt extends FreedomCommand {
    @Override
    public boolean run(CommandSender sender, org.bukkit.entity.Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole) {
        if (args.length != 1) {
            return false;
        }

        if (args[0].equalsIgnoreCase("all")) {
            Utilities.adminAction(sender.getName(), "Halting all non-superadmins.", true);
            int counter = 0;
            for (Player player : server.getOnlinePlayers())
            {
                if (!AdminList.isSuperAdmin(player))
                {
                    PlayerData.getPlayerData(player).setHalted(true);
                    counter++;
                }
            }
            playerMsg(sender, "Halted " + counter + " players.");
            return true;
        }

        if (args[0].equalsIgnoreCase("purge"))
        {
            Utilities.adminAction(sender.getName(), "Unhalting all players.", true);
            int counter = 0;
            for (Player player : server.getOnlinePlayers())
            {
                PlayerData playerdata = PlayerData.getPlayerData(player);
                if (PlayerData.getPlayerData(player).isHalted())
                {
                    playerdata.setHalted(false);
                    counter++;
                }
            }
            playerMsg(sender, "Unhalted " + counter + " players.");
            return true;
        }

        if (args[0].equalsIgnoreCase("list"))
        {
            PlayerData info;
            int count = 0;
            for (Player hp : server.getOnlinePlayers())
            {
                info = PlayerData.getPlayerData(hp);
                if (info.isHalted())
                {
                    if (count == 0)
                    {
                        playerMsg(sender, "Halted players:");
                    }
                    playerMsg(sender, "- " + hp.getName());
                    count++;
                }
            }
            if (count == 0)
            {
                playerMsg(sender, "There are currently no halted players.");
            }
            return true;
        }

        final Player player = getPlayer(args[0]);

        if (player == null)
        {
            sender.sendMessage(FreedomCommand.PLAYER_NOT_FOUND);
            return true;
        }

        PlayerData playerdata = PlayerData.getPlayerData(player);
        if (!playerdata.isHalted())
        {
            Utilities.adminAction(sender.getName(), "Halting " + player.getName(), true);
            playerdata.setHalted(true);
            return true;
        }
        else
        {
            Utilities.adminAction(sender.getName(), "Unhalting " + player.getName(), true);
            playerdata.setHalted(false);
            return true;
        }
    }
}
