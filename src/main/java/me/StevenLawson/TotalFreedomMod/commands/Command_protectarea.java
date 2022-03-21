package me.StevenLawson.TotalFreedomMod.commands;

import me.StevenLawson.TotalFreedomMod.config.ConfigurationEntry;
import me.StevenLawson.TotalFreedomMod.world.ProtectedArea;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

@CommandPermissions(level = AdminLevel.SUPER, source = SourceType.BOTH)
public class Command_protectarea extends FreedomCommand {

    @Override
    public boolean run(CommandSender sender, org.bukkit.entity.Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole) {
        if (!ConfigurationEntry.PROTECTAREA_ENABLED.getBoolean()) {
            playerMsg(sender, "Protected areas are currently disabled in the TotalFreedomMod configuration.");
            return true;
        }

        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("list"))
            {
                playerMsg(sender, "Protected Areas: " + StringUtils.join(ProtectedArea.getProtectedAreaLabels(), ", "));
            }
            else if (args[0].equalsIgnoreCase("clear"))
            {
                ProtectedArea.clearProtectedAreas();

                playerMsg(sender, "Protected Areas Cleared.");
            }
            else
            {
                return false;
            }

            return true;
        }
        else if (args.length == 2)
        {
            if ("remove".equals(args[0]))
            {
                ProtectedArea.removeProtectedArea(args[1]);

                playerMsg(sender, "Area removed. Protected Areas: " + StringUtils.join(ProtectedArea.getProtectedAreaLabels(), ", "));
            }
            else
            {
                return false;
            }

            return true;
        }
        else if (args.length == 3)
        {
            if (args[0].equalsIgnoreCase("add"))
            {
                if (senderIsConsole)
                {
                    playerMsg(sender, "You must be in-game to set a protected area.");
                    return true;
                }

                Double radius;
                try
                {
                    radius = Double.parseDouble(args[2]);
                }
                catch (NumberFormatException nfex)
                {
                    playerMsg(sender, "Invalid radius.");
                    return true;
                }

                if (radius > ProtectedArea.MAX_RADIUS || radius < 0.0D)
                {
                    playerMsg(sender, "Invalid radius. Radius must be a positive value less than " + ProtectedArea.MAX_RADIUS + ".");
                    return true;
                }

                ProtectedArea.addProtectedArea(args[1], sender_p.getLocation(), radius);

                playerMsg(sender, "Area added. Protected Areas: " + StringUtils.join(ProtectedArea.getProtectedAreaLabels(), ", "));
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
