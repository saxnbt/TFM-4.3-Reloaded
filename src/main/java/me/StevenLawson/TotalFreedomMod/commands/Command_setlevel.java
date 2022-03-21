package me.StevenLawson.TotalFreedomMod.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

@CommandPermissions(level = AdminLevel.OP, source = SourceType.ONLY_IN_GAME)
public class Command_setlevel extends FreedomCommand {
    @Override
    public boolean run(CommandSender sender, org.bukkit.entity.Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole) {
        if (args.length != 1) {
            return false;
        }

        int new_level;

        try {
            new_level = Integer.parseInt(args[0]);

            if (new_level < 0)
            {
                new_level = 0;
            }
            else if (new_level > 50)
            {
                new_level = 50;
            }
        }
        catch (NumberFormatException ex)
        {
            playerMsg(sender, "Invalid level.", ChatColor.RED);
            return true;
        }

        sender_p.setLevel(new_level);

        playerMsg(sender, "You have been set to level " + new_level, ChatColor.AQUA);

        return true;
    }
}
