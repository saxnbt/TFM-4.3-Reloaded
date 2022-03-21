package me.StevenLawson.TotalFreedomMod.commands;

import me.StevenLawson.TotalFreedomMod.ban.Ban;
import me.StevenLawson.TotalFreedomMod.ban.BanManager;
import me.StevenLawson.TotalFreedomMod.player.UUIDManager;
import me.StevenLawson.TotalFreedomMod.util.Utilities;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.Date;

@CommandPermissions(level = AdminLevel.SUPER, source = SourceType.BOTH)
@CommandParameters(description = "Temporarily ban someone.", usage = "/<command> [playername] [duration] [reason]")
public class Command_tempban extends FreedomCommand {
    private static final SimpleDateFormat date_format = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");

    @Override
    public boolean run(CommandSender sender, org.bukkit.entity.Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole) {
        if (args.length < 1) {
            return false;
        }

        final Player player = getPlayer(args[0]);

        if (player == null) {
            playerMsg(FreedomCommand.PLAYER_NOT_FOUND);
            return true;
        }

        final StringBuilder message = new StringBuilder("Temporarily banned " + player.getName());

        Date expires = Utilities.parseDateOffset("30m");
        if (args.length >= 2)
        {
            Date parsed_offset = Utilities.parseDateOffset(args[1]);
            if (parsed_offset != null)
            {
                expires = parsed_offset;
            }
        }
        message.append(" until ").append(date_format.format(expires));

        String reason = "Banned by " + sender.getName();
        if (args.length >= 3)
        {
            reason = StringUtils.join(ArrayUtils.subarray(args, 2, args.length), " ") + " (" + sender.getName() + ")";
            message.append(", Reason: \"").append(reason).append("\"");
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

        Utilities.adminAction(sender.getName(), message.toString(), true);

        BanManager.addIpBan(new Ban(Utilities.getIp(player), player.getName(), sender.getName(), expires, reason));
        BanManager.addUuidBan(new Ban(UUIDManager.getUniqueId(player), player.getName(), sender.getName(), expires, reason));

        player.kickPlayer(sender.getName() + " - " + message);

        return true;
    }
}
