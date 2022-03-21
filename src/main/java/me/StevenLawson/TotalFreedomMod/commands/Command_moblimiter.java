package me.StevenLawson.TotalFreedomMod.commands;

import me.StevenLawson.TotalFreedomMod.config.ConfigurationEntry;
import me.StevenLawson.TotalFreedomMod.gamerule.GameRuleHandler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

@CommandPermissions(level = AdminLevel.SUPER, source = SourceType.ONLY_CONSOLE)
public class Command_moblimiter extends FreedomCommand {
    @Override
    public boolean run(CommandSender sender, org.bukkit.entity.Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole) {
        if (args.length < 1) {
            return false;
        }

        if (args[0].equalsIgnoreCase("on")) {
            ConfigurationEntry.MOB_LIMITER_ENABLED.setBoolean(true);
        }
        else if (args[0].equalsIgnoreCase("off"))
        {
            ConfigurationEntry.MOB_LIMITER_ENABLED.setBoolean(false);
        }
        else if (args[0].equalsIgnoreCase("dragon"))
        {
            ConfigurationEntry.MOB_LIMITER_DISABLE_DRAGON.setBoolean(!ConfigurationEntry.MOB_LIMITER_DISABLE_DRAGON.getBoolean());
        }
        else if (args[0].equalsIgnoreCase("giant"))
        {
            ConfigurationEntry.MOB_LIMITER_DISABLE_GIANT.setBoolean(!ConfigurationEntry.MOB_LIMITER_DISABLE_GIANT.getBoolean());
        }
        else if (args[0].equalsIgnoreCase("slime"))
        {
            ConfigurationEntry.MOB_LIMITER_DISABLE_SLIME.setBoolean(!ConfigurationEntry.MOB_LIMITER_DISABLE_SLIME.getBoolean());
        }
        else if (args[0].equalsIgnoreCase("ghast"))
        {
            ConfigurationEntry.MOB_LIMITER_DISABLE_GHAST.setBoolean(!ConfigurationEntry.MOB_LIMITER_DISABLE_GHAST.getBoolean());
        }
        else
        {
            if (args.length < 2)
            {
                return false;
            }

            if (args[0].equalsIgnoreCase("setmax"))
            {
                try
                {
                    ConfigurationEntry.MOB_LIMITER_MAX.setInteger(Math.max(0, Math.min(2000, Integer.parseInt(args[1]))));
                }
                catch (NumberFormatException nfex)
                {
                }
            }
        }

        if (ConfigurationEntry.MOB_LIMITER_ENABLED.getBoolean())
        {
            sender.sendMessage("Moblimiter enabled. Maximum mobcount set to: " + ConfigurationEntry.MOB_LIMITER_MAX.getInteger() + ".");

            playerMsg(sender, "Dragon: " + (ConfigurationEntry.MOB_LIMITER_DISABLE_DRAGON.getBoolean() ? "disabled" : "enabled") + ".");
            playerMsg(sender, "Giant: " + (ConfigurationEntry.MOB_LIMITER_DISABLE_GIANT.getBoolean() ? "disabled" : "enabled") + ".");
            playerMsg(sender, "Slime: " + (ConfigurationEntry.MOB_LIMITER_DISABLE_SLIME.getBoolean() ? "disabled" : "enabled") + ".");
            playerMsg(sender, "Ghast: " + (ConfigurationEntry.MOB_LIMITER_DISABLE_GHAST.getBoolean() ? "disabled" : "enabled") + ".");
        }
        else
        {
            playerMsg(sender, "Moblimiter is disabled. No mob restrictions are in effect.");
        }

        GameRuleHandler.setGameRule(GameRuleHandler.TFM_GameRule.DO_MOB_SPAWNING, !ConfigurationEntry.MOB_LIMITER_ENABLED.getBoolean());

        return true;
    }
}
