package me.StevenLawson.TotalFreedomMod.commands;

import me.StevenLawson.TotalFreedomMod.admin.AdminList;
import me.StevenLawson.TotalFreedomMod.util.Utilities;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

@CommandPermissions(level = AdminLevel.SUPER, source = SourceType.BOTH)
public class Command_invis extends FreedomCommand {
    @Override
    public boolean run(CommandSender sender, org.bukkit.entity.Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole) {
        boolean smite = false;
        if (args.length >= 1) {
            if (args[0].equalsIgnoreCase("smite")) {
                Utilities.adminAction(sender.getName(), "Smiting all invisible players", true);
                smite = true;
            } else
            {
                return false;
            }
        }

        List<String> players = new ArrayList<String>();
        int smites = 0;

        for (Player player : server.getOnlinePlayers())
        {
            if (player.hasPotionEffect(PotionEffectType.INVISIBILITY))
            {
                players.add(player.getName());
                if (smite && !AdminList.isSuperAdmin(player))
                {
                    player.setHealth(0.0);
                    smites++;
                }
            }
        }

        if (players.isEmpty())
        {
            playerMsg(sender, "There are no invisible players");
            return true;
        }

        if (smite)
        {
            playerMsg(sender, "Smitten " + smites + " players");
        }
        else
        {
            playerMsg(sender, "Invisble players (" + players.size() + "): " + StringUtils.join(players, ", "));
        }

        return true;
    }
}
