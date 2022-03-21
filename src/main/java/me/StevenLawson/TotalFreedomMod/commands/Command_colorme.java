package me.StevenLawson.TotalFreedomMod.commands;

import me.StevenLawson.TotalFreedomMod.bridge.EssentialsBridge;
import me.StevenLawson.TotalFreedomMod.util.Utilities;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.Iterator;
import java.util.Map;

@CommandPermissions(level = AdminLevel.OP, source = SourceType.ONLY_IN_GAME)
public class Command_colorme extends FreedomCommand {
    @Override
    public boolean run(CommandSender sender, org.bukkit.entity.Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole) {
        if (args.length != 1) {
            return false;
        }

        if ("list".equalsIgnoreCase(args[0])) {
            playerMsg(sender, "Colors: " + StringUtils.join(Utilities.CHAT_COLOR_NAMES.keySet(), ", "));
            return true;
        }

        final String needle = args[0].trim().toLowerCase();
        ChatColor color = null;
        final Iterator<Map.Entry<String, ChatColor>> it = Utilities.CHAT_COLOR_NAMES.entrySet().iterator();
        while (it.hasNext())
        {
            final Map.Entry<String, ChatColor> entry = it.next();
            if (entry.getKey().contains(needle))
            {
                color = entry.getValue();
                break;
            }
        }

        if (color == null)
        {
            playerMsg(sender, "Invalid color: " + needle + " - Use \"/colorme list\" to list colors.");
            return true;
        }

        final String newNick = color + ChatColor.stripColor(sender_p.getDisplayName()).trim() + ChatColor.WHITE;

        EssentialsBridge.setNickname(sender.getName(), newNick);

        playerMsg(sender, "Your nickname is now: " + newNick);

        return true;
    }
}
