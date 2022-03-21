package me.StevenLawson.TotalFreedomMod.commands;

import me.StevenLawson.TotalFreedomMod.player.PlayerData;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = AdminLevel.SUPER, source = SourceType.BOTH)
public class Command_lastcmd extends FreedomCommand {
    @Override
    public boolean run(CommandSender sender, org.bukkit.entity.Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole) {
        if (args.length == 0) {
            return false;
        }

        final Player player = getPlayer(args[0]);

        if (player == null) {
            playerMsg(sender, FreedomCommand.PLAYER_NOT_FOUND);
            return true;
        }

        final PlayerData playerdata = PlayerData.getPlayerData(player);

        if (playerdata != null)
        {
            String lastCommand = playerdata.getLastCommand();
            if (lastCommand.isEmpty())
            {
                lastCommand = "(none)";
            }
            playerMsg(sender, player.getName() + " - Last Command: " + lastCommand, ChatColor.GRAY);
        }

        return true;
    }
}
