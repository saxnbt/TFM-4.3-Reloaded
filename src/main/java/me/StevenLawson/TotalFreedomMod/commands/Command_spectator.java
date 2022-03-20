package me.StevenLawson.TotalFreedomMod.commands;

import me.StevenLawson.TotalFreedomMod.util.Utilities;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = AdminLevel.SUPER, source = SourceType.BOTH)
@CommandParameters(description = "Quickly change your own gamemode to spectator, or define someone's username to change theirs.", usage = "/<command> [partialname]", aliases = "gmsp")
public class Command_spectator extends FreedomCommand {
    @Override
    public boolean run(CommandSender sender, org.bukkit.entity.Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole) {
        if (senderIsConsole) {
            if (args.length == 0) {
                sender.sendMessage("When used from the console, you must define a target user to change gamemode on.");
                return true;
            }
        }

        Player player;
        if (args.length == 0)
        {
            player = sender_p;
        }
        else
        {
            if (args[0].equalsIgnoreCase("-a"))
            {

                for (Player targetPlayer : server.getOnlinePlayers())
                {
                    targetPlayer.setGameMode(GameMode.SPECTATOR);
                }

                Utilities.adminAction(sender.getName(), "Changing everyone's gamemode to spectator", false);
                return true;
            }


            player = getPlayer(args[0]);

            if (player == null)
            {
                sender.sendMessage(FreedomCommand.PLAYER_NOT_FOUND);
                return true;
            }

        }

        playerMsg("Setting " + player.getName() + " to game mode 'Spectator'.");
        playerMsg(player, sender.getName() + " set your game mode to 'Spectator'.");
        player.setGameMode(GameMode.SPECTATOR);

        return true;
    }
}
