package me.StevenLawson.TotalFreedomMod.commands;

import me.StevenLawson.TotalFreedomMod.bridge.EssentialsBridge;
import me.StevenLawson.TotalFreedomMod.util.Utilities;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@CommandPermissions(level = AdminLevel.SUPER, source = SourceType.BOTH)
@CommandParameters(description = "Essentials Interface Command - Remove distracting things from nicknames of all players on server.", usage = "/<command>", aliases = "nc")
public class Command_nickclean extends FreedomCommand {
    private static final ChatColor[] BLOCKED = new ChatColor[]
            {
                    ChatColor.MAGIC,
                    ChatColor.STRIKETHROUGH,
            };
    private static final Pattern REGEX = Pattern.compile("\\u00A7[" + StringUtils.join(BLOCKED, "") + "]");

    @Override
    public boolean run(CommandSender sender, org.bukkit.entity.Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole) {
        Utilities.adminAction(sender.getName(), "Cleaning all nicknames.", false);

        for (final Player player : server.getOnlinePlayers()) {
            final String playerName = player.getName();
            final String nickName = EssentialsBridge.getNickname(playerName);
            if (nickName != null && !nickName.isEmpty() && !nickName.equalsIgnoreCase(playerName)) {
                final Matcher matcher = REGEX.matcher(nickName);
                if (matcher.find())
                {
                    final String newNickName = matcher.replaceAll("");
                    playerMsg(ChatColor.RESET + playerName + ": \"" + nickName + ChatColor.RESET + "\" -> \"" + newNickName + ChatColor.RESET + "\".");
                    EssentialsBridge.setNickname(playerName, newNickName);
                }
            }
        }

        return true;
    }
}
