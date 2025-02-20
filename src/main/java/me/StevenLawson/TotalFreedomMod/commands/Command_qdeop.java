package me.StevenLawson.TotalFreedomMod.commands;

import me.StevenLawson.TotalFreedomMod.util.Utilities;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@CommandPermissions(level = AdminLevel.SUPER, source = SourceType.BOTH)
@CommandParameters(description = "Quick De-Op - deop someone based on a partial name.", usage = "/<command> <partialname>")
public class Command_qdeop extends FreedomCommand {
    @Override
    public boolean run(CommandSender sender, org.bukkit.entity.Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole) {
        if (args.length < 1) {
            return false;
        }

        boolean silent = false;
        if (args.length == 2) {
            silent = args[1].equalsIgnoreCase("-s");
        }

        final String targetName = args[0].toLowerCase();

        final List<String> matchedPlayerNames = new ArrayList<String>();
        for (final Player player : server.getOnlinePlayers())
        {
            if (player.getName().toLowerCase().contains(targetName) || player.getDisplayName().toLowerCase().contains(targetName))
            {
                if (player.isOp())
                {
                    matchedPlayerNames.add(player.getName());
                    player.setOp(false);
                    player.sendMessage(FreedomCommand.YOU_ARE_NOT_OP);
                }
            }
        }

        if (!matchedPlayerNames.isEmpty())
        {
            if (!silent)
            {
                Utilities.adminAction(sender.getName(), "De-opping " + StringUtils.join(matchedPlayerNames, ", "), false);
            }
        }
        else
        {
            playerMsg("No targets matched.");
        }

        return true;
    }
}
