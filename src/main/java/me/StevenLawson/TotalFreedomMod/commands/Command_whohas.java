package me.StevenLawson.TotalFreedomMod.commands;

import me.StevenLawson.TotalFreedomMod.admin.AdminList;
import me.StevenLawson.TotalFreedomMod.util.DeprecationUtil;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@CommandPermissions(level = AdminLevel.SUPER, source = SourceType.BOTH)
public class Command_whohas extends FreedomCommand {
    @Override
    public boolean run(CommandSender sender, org.bukkit.entity.Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole) {
        if (args.length < 1) {
            return false;
        }

        final boolean doSmite = args.length >= 2 && "smite".equalsIgnoreCase(args[1]);

        final String materialName = args[0];
        Material material = Material.matchMaterial(materialName);
        if (material == null)
        {
            try
            {
                material = DeprecationUtil.getMaterial(Integer.parseInt(materialName));
            }
            catch (NumberFormatException ex)
            {
            }
        }

        if (material == null)
        {
            playerMsg(sender, "Invalid block: " + materialName, ChatColor.RED);
            return true;
        }

        final List<String> players = new ArrayList<String>();

        for (final Player player : server.getOnlinePlayers())
        {
            if (player.getInventory().contains(material))
            {
                players.add(player.getName());
                if (doSmite && !AdminList.isSuperAdmin(player))
                {
                    Command_smite.smite(player);
                }
            }
        }

        if (players.isEmpty())
        {
            playerMsg(sender, "There are no players with that item");
        }
        else
        {
            playerMsg(sender, "Players with item " + material.name() + ": " + StringUtils.join(players, ", "));
        }

        return true;
    }
}
