package me.StevenLawson.TotalFreedomMod.commands;

import me.StevenLawson.TotalFreedomMod.ban.Ban;
import me.StevenLawson.TotalFreedomMod.ban.BanManager;
import me.StevenLawson.TotalFreedomMod.player.UUIDManager;
import me.StevenLawson.TotalFreedomMod.util.Utilities;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = AdminLevel.SUPER, source = SourceType.BOTH)
public class Command_tban extends FreedomCommand {
    @Override
    public boolean run(CommandSender sender, org.bukkit.entity.Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole) {
        if (args.length != 1) {
            return false;
        }

        final Player player = getPlayer(args[0]);

        if (player == null) {
            playerMsg(sender, FreedomCommand.PLAYER_NOT_FOUND, ChatColor.RED);
            return true;
        }

        // strike with lightning effect:
        final Location targetPos = player.getLocation();
        for (int x = -1; x <= 1; x++)
        {
            for (int z = -1; z <= 1; z++)
            {
                final Location strike_pos = new Location(targetPos.getWorld(), targetPos.getBlockX() + x, targetPos.getBlockY(), targetPos.getBlockZ() + z);
                targetPos.getWorld().strikeLightning(strike_pos);
            }
        }

        Utilities.adminAction(sender.getName(), "Tempbanning: " + player.getName() + " for 5 minutes.", true);
        BanManager.addUuidBan(
                new Ban(UUIDManager.getUniqueId(player), player.getName(), sender.getName(), Utilities.parseDateOffset("5m"), ChatColor.RED + "You have been temporarily banned for 5 minutes."));
        BanManager.addIpBan(
                new Ban(Utilities.getIp(player), player.getName(), sender.getName(), Utilities.parseDateOffset("5m"), ChatColor.RED + "You have been temporarily banned for 5 minutes."));

        player.kickPlayer(ChatColor.RED + "You have been temporarily banned for five minutes. Please read totalfreedom.me for more info.");

        return true;
    }
}
