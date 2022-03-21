package me.StevenLawson.TotalFreedomMod.commands;

import me.StevenLawson.TotalFreedomMod.fun.JumpPads;
import me.StevenLawson.TotalFreedomMod.util.Utilities;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

@CommandPermissions(level = AdminLevel.SUPER, source = SourceType.BOTH)
public class Command_jumppads extends FreedomCommand {
    @Override
    public boolean run(CommandSender sender, org.bukkit.entity.Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole) {
        if (args.length == 0 || args.length > 2) {
            return false;
        }

        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("info")) {
                playerMsg(sender, "Jumppads: " + (JumpPads.getMode().isOn() ? "Enabled" : "Disabled"), ChatColor.BLUE);
                playerMsg(sender, "Sideways: " + (JumpPads.getMode() == JumpPads.JumpPadMode.NORMAL_AND_SIDEWAYS ? "Enabled" : "Disabled"), ChatColor.BLUE);
                playerMsg(sender, "Strength: " + (JumpPads.getStrength() * 10 - 1), ChatColor.BLUE);
                return true;
            }

            if ("off".equals(args[0]))
            {
                Utilities.adminAction(sender.getName(), "Disabling Jumppads", false);
                JumpPads.setMode(JumpPads.JumpPadMode.OFF);
            }
            else
            {
                Utilities.adminAction(sender.getName(), "Enabling Jumppads", false);
                JumpPads.setMode(JumpPads.JumpPadMode.MADGEEK);
            }
        }
        else
        {
            if (JumpPads.getMode() == JumpPads.JumpPadMode.OFF)
            {
                playerMsg(sender, "Jumppads are currently disabled, please enable them before changing jumppads settings.");
                return true;
            }

            if (args[0].equalsIgnoreCase("sideways"))
            {
                if ("off".equals(args[1]))
                {
                    Utilities.adminAction(sender.getName(), "Setting Jumppads mode to: Madgeek", false);
                    JumpPads.setMode(JumpPads.JumpPadMode.MADGEEK);
                }
                else
                {
                    Utilities.adminAction(sender.getName(), "Setting Jumppads mode to: Normal and Sideways", false);
                    JumpPads.setMode(JumpPads.JumpPadMode.NORMAL_AND_SIDEWAYS);
                }
            }
            else if (args[0].equalsIgnoreCase("strength"))
            {
                final float strength;
                try
                {
                    strength = Float.parseFloat(args[1]);
                }
                catch (NumberFormatException ex)
                {
                    playerMsg(sender, "Invalid Strength");
                    return true;
                }

                if (strength > 10 || strength < 1)
                {
                    playerMsg(sender, "Invalid Strength: The strength may be 1 through 10.");
                    return true;
                }

                Utilities.adminAction(sender.getName(), "Setting Jumppads strength to: " + strength, false);
                JumpPads.setStrength((strength / 10) + 0.1F);
            }
            else
            {
                return false;
            }
        }

        return true;
    }
}
