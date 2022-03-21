package me.StevenLawson.TotalFreedomMod.commands;

import me.StevenLawson.TotalFreedomMod.util.Utilities;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = AdminLevel.SUPER, source = SourceType.BOTH)
public class Command_say extends FreedomCommand {
    @Override
    public boolean run(CommandSender sender, org.bukkit.entity.Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole) {
        if (args.length == 0) {
            return false;
        }

        String message = StringUtils.join(args, " ");

        if (senderIsConsole && Utilities.isFromHostConsole(sender.getName())) {
            if (message.equalsIgnoreCase("WARNING: Server is restarting, you will be kicked"))
            {
                Utilities.bcastMsg("Server is going offline.", ChatColor.GRAY);

                for (Player player : server.getOnlinePlayers())
                {
                    player.kickPlayer("Server is going offline, come back in about 20 seconds.");
                }

                server.shutdown();

                return true;
            }
        }

        Utilities.bcastMsg(String.format("[Server:%s] %s", sender.getName(), message), ChatColor.LIGHT_PURPLE);

        return true;
    }
}
