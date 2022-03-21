package me.StevenLawson.TotalFreedomMod.commands;

import me.StevenLawson.TotalFreedomMod.player.PlayerRank;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = AdminLevel.ALL, source = SourceType.BOTH)
@CommandParameters(description = "Shows your rank.", usage = "/<command>")
public class Command_rank extends FreedomCommand {
    @Override
    public boolean run(CommandSender sender, org.bukkit.entity.Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole) {
        if (senderIsConsole && args.length < 1) {
            for (Player player : server.getOnlinePlayers()) {
                playerMsg(player.getName() + " is " + PlayerRank.fromSender(player).getLoginMessage());
            }
            return true;
        }

        if (args.length > 1)
        {
            return false;
        }

        if (args.length == 0)
        {
            playerMsg(sender.getName() + " is " + PlayerRank.fromSender(sender).getLoginMessage(), ChatColor.AQUA);
            return true;
        }

        final Player player = getPlayer(args[0]);

        if (player == null)
        {
            sender.sendMessage(FreedomCommand.PLAYER_NOT_FOUND);
            return true;
        }

        playerMsg(player.getName() + " is " + PlayerRank.fromSender(player).getLoginMessage(), ChatColor.AQUA);

        return true;
    }
}
