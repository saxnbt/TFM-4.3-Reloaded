package me.StevenLawson.TotalFreedomMod.commands;

import me.StevenLawson.TotalFreedomMod.admin.AdminList;
import me.StevenLawson.TotalFreedomMod.util.Utilities;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

@CommandPermissions(level = AdminLevel.OP, source = SourceType.BOTH)
public class Command_potion extends FreedomCommand {
    @Override
    public boolean run(CommandSender sender, org.bukkit.entity.Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole) {
        if (args.length == 1 || args.length == 2) {
            if (args[0].equalsIgnoreCase("list")) {
                List<String> potionEffectTypeNames = new ArrayList<String>();
                for (PotionEffectType potion_effect_type : PotionEffectType.values()) {
                    if (potion_effect_type != null) {
                        potionEffectTypeNames.add(potion_effect_type.getName());
                    }
                }
                playerMsg(sender, "Potion effect types: " + StringUtils.join(potionEffectTypeNames, ", "), ChatColor.AQUA);
            }
            else if (args[0].equalsIgnoreCase("clearall"))
            {
                if (!(AdminList.isSuperAdmin(sender) || senderIsConsole))
                {
                    playerMsg(sender, FreedomCommand.MSG_NO_PERMS);
                    return true;
                }
                Utilities.adminAction(sender.getName(), "Cleared all potion effects from all players", true);
                for (Player target : server.getOnlinePlayers())
                {
                    for (PotionEffect potion_effect : target.getActivePotionEffects())
                    {
                        target.removePotionEffect(potion_effect.getType());
                    }
                }
            }
            else if (args[0].equalsIgnoreCase("clear"))
            {
                Player target = sender_p;

                if (args.length == 2)
                {
                    target = getPlayer(args[1]);

                    if (target == null)
                    {
                        playerMsg(sender, FreedomCommand.PLAYER_NOT_FOUND, ChatColor.RED);
                        return true;
                    }
                }

                if (!target.equals(sender_p))
                {
                    if (!AdminList.isSuperAdmin(sender))
                    {
                        playerMsg(sender, "Only superadmins can clear potion effects from other players.");
                        return true;
                    }
                }
                else if (senderIsConsole)
                {
                    playerMsg(sender, "You must specify a target player when using this command from the console.");
                    return true;
                }

                for (PotionEffect potion_effect : target.getActivePotionEffects())
                {
                    target.removePotionEffect(potion_effect.getType());
                }

                playerMsg(sender, "Cleared all active potion effects " + (!target.equals(sender_p) ? "from player " + target.getName() + "." : "from yourself."), ChatColor.AQUA);
            }
            else
            {
                return false;
            }
        }
        else if (args.length == 4 || args.length == 5)
        {
            if (args[0].equalsIgnoreCase("add"))
            {
                Player target = sender_p;

                if (args.length == 5)
                {

                    target = getPlayer(args[4]);

                    if (target == null)
                    {
                        playerMsg(sender, FreedomCommand.PLAYER_NOT_FOUND, ChatColor.RED);
                        return true;
                    }
                }

                if (!target.equals(sender_p))
                {
                    if (!AdminList.isSuperAdmin(sender))
                    {
                        sender.sendMessage("Only superadmins can apply potion effects to other players.");
                        return true;
                    }
                }
                else if (senderIsConsole)
                {
                    sender.sendMessage("You must specify a target player when using this command from the console.");
                    return true;
                }

                PotionEffectType potion_effect_type = PotionEffectType.getByName(args[1]);
                if (potion_effect_type == null)
                {
                    sender.sendMessage(ChatColor.AQUA + "Invalid potion effect type.");
                    return true;
                }

                int duration;
                try
                {
                    duration = Integer.parseInt(args[2]);
                    duration = Math.min(duration, 100000);
                }
                catch (NumberFormatException ex)
                {
                    playerMsg(sender, "Invalid potion duration.", ChatColor.RED);
                    return true;
                }

                int amplifier;
                try
                {
                    amplifier = Integer.parseInt(args[3]);
                    amplifier = Math.min(amplifier, 100000);
                }
                catch (NumberFormatException ex)
                {
                    playerMsg(sender, "Invalid potion amplifier.", ChatColor.RED);
                    return true;
                }

                PotionEffect new_effect = potion_effect_type.createEffect(duration, amplifier);
                target.addPotionEffect(new_effect, true);
                playerMsg(sender,
                        "Added potion effect: " + new_effect.getType().getName()
                        + ", Duration: " + new_effect.getDuration()
                        + ", Amplifier: " + new_effect.getAmplifier()
                        + (!target.equals(sender_p) ? " to player " + target.getName() + "." : " to yourself."), ChatColor.AQUA);

                return true;
            }
            else
            {
                return false;
            }
        }
        else
        {
            return false;
        }
        return true;
    }
}
