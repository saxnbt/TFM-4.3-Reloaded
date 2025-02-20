package me.StevenLawson.TotalFreedomMod.commands;

import me.StevenLawson.TotalFreedomMod.TotalFreedomMod;
import me.StevenLawson.TotalFreedomMod.config.ConfigurationEntry;
import me.StevenLawson.TotalFreedomMod.gamerule.GameRuleHandler;
import me.StevenLawson.TotalFreedomMod.gamerule.GameRuleHandler.TFM_GameRule;
import me.StevenLawson.TotalFreedomMod.util.Utilities;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

@CommandPermissions(level = AdminLevel.SUPER, source = SourceType.BOTH)
@CommandParameters(description = "Toggles TotalFreedomMod settings", usage = "/<command> [option] [value] [value]")
public class Command_toggle extends FreedomCommand {
    @Override
    public boolean run(CommandSender sender, org.bukkit.entity.Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole) {
        if (args.length == 0) {
            playerMsg("Available toggles: ");
            playerMsg("- waterplace");
            playerMsg("- fireplace");
            playerMsg("- lavaplace");
            playerMsg("- fluidspread");
            playerMsg("- lavadmg");
            playerMsg("- firespread");
            playerMsg("- prelog");
            playerMsg("- lockdown");
            playerMsg("- petprotect");
            playerMsg("- droptoggle");
            playerMsg("- nonuke");
            playerMsg("- explosives");
            return false;
        }

        if (args[0].equals("waterplace"))
        {
            toggle("Water placement is", ConfigurationEntry.ALLOW_WATER_PLACE);
            return true;
        }

        if (args[0].equals("fireplace"))
        {
            toggle("Fire placement is", ConfigurationEntry.ALLOW_FIRE_PLACE);
            return true;
        }

        if (args[0].equals("lavaplace"))
        {
            toggle("Lava placement is", ConfigurationEntry.ALLOW_LAVA_PLACE);
            return true;
        }

        if (args[0].equals("fluidspread"))
        {
            toggle("Fluid spread is", ConfigurationEntry.ALLOW_FLUID_SPREAD);
            return true;
        }

        if (args[0].equals("lavadmg"))
        {
            toggle("Lava damage is", ConfigurationEntry.ALLOW_LAVA_DAMAGE);
            return true;
        }

        if (args[0].equals("firespread"))
        {
            toggle("Fire spread is", ConfigurationEntry.ALLOW_FIRE_SPREAD);
            GameRuleHandler.setGameRule(TFM_GameRule.DO_FIRE_TICK, ConfigurationEntry.ALLOW_FIRE_SPREAD.getBoolean());
            return true;
        }

        if (args[0].equals("prelog"))
        {
            toggle("Command prelogging is", ConfigurationEntry.ENABLE_PREPROCESS_LOG);
            return true;
        }

        if (args[0].equals("lockdown"))
        {
            Utilities.adminAction(sender.getName(), (TotalFreedomMod.lockdownEnabled ? "De-a" : "A") + "ctivating server lockdown", true);
            TotalFreedomMod.lockdownEnabled = !TotalFreedomMod.lockdownEnabled;
            return true;
        }

        if (args[0].equals("petprotect"))
        {
            toggle("Tamed pet protection is", ConfigurationEntry.ENABLE_PET_PROTECT);
            return true;
        }

        if (args[0].equals("droptoggle"))
        {
            toggle("Automatic entity wiping is", ConfigurationEntry.AUTO_ENTITY_WIPE);
            return true;
        }

        if (args[0].equals("nonuke"))
        {
            if (args.length >= 2)
            {
                try
                {
                    ConfigurationEntry.NUKE_MONITOR_RANGE.setDouble(Math.max(1.0, Math.min(500.0, Double.parseDouble(args[1]))));
                }
                catch (NumberFormatException nfex)
                {
                }
            }

            if (args.length >= 3)
            {
                try
                {
                    ConfigurationEntry.NUKE_MONITOR_COUNT_BREAK.setInteger(Math.max(1, Math.min(500, Integer.parseInt(args[2]))));
                }
                catch (NumberFormatException nfex)
                {
                }
            }

            toggle("Nuke monitor is", ConfigurationEntry.NUKE_MONITOR_ENABLED);

            if (ConfigurationEntry.NUKE_MONITOR_ENABLED.getBoolean())
            {
                playerMsg("Anti-freecam range is set to " + ConfigurationEntry.NUKE_MONITOR_RANGE.getDouble() + " blocks.");
                playerMsg("Block throttle rate is set to " + ConfigurationEntry.NUKE_MONITOR_COUNT_BREAK.getInteger() + " blocks destroyed per 5 seconds.");
            }

            return true;
        }
        if (args[0].equals("explosives"))
        {
            if (args.length == 2)
            {
                try
                {
                    ConfigurationEntry.EXPLOSIVE_RADIUS.setDouble(Math.max(1.0, Math.min(30.0, Double.parseDouble(args[1]))));
                }
                catch (NumberFormatException ex)
                {
                    playerMsg(ex.getMessage());
                    return true;
                }
            }

            toggle("Explosions are", ConfigurationEntry.ALLOW_EXPLOSIONS);

            if (ConfigurationEntry.ALLOW_EXPLOSIONS.getBoolean())
            {
                playerMsg("Radius set to " + ConfigurationEntry.EXPLOSIVE_RADIUS.getDouble());
            }
            return true;
        }

        return false;
    }

    private void toggle(String name, ConfigurationEntry entry)
    {
        playerMsg(name + " now " + (entry.setBoolean(!entry.getBoolean()) ? "enabled." : "disabled."));
    }
}
