package me.StevenLawson.TotalFreedomMod.commands;

import me.StevenLawson.TotalFreedomMod.command.CommandBlocker;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = AdminLevel.SUPER, source = SourceType.BOTH)
public class Command_gcmd extends FreedomCommand {
    @Override
    public boolean run(CommandSender sender, org.bukkit.entity.Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole) {
        if (args.length < 2) {
            return false;
        }

        final Player player = getPlayer(args[0]);

        if (player == null) {
            sender.sendMessage(FreedomCommand.PLAYER_NOT_FOUND);
            return true;
        }

        final String outCommand = StringUtils.join(args, " ", 1, args.length);

        if (CommandBlocker.isCommandBlocked(outCommand, sender))
        {
            return true;
        }

        try
        {
            playerMsg(sender, "Sending command as " + player.getName() + ": " + outCommand);
            if (server.dispatchCommand(player, outCommand))
            {
                playerMsg(sender, "Command sent.");
            }
            else
            {
                playerMsg(sender, "Unknown error sending command.");
            }
        }
        catch (Throwable ex)
        {
            playerMsg(sender, "Error sending command: " + ex.getMessage());
        }

        return true;
    }
}
