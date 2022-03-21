package me.StevenLawson.TotalFreedomMod.commands;

import me.StevenLawson.TotalFreedomMod.bridge.EssentialsBridge;
import me.StevenLawson.TotalFreedomMod.util.Utilities;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = AdminLevel.OP, source = SourceType.ONLY_IN_GAME)
public class Command_nicknyan extends FreedomCommand {
    @Override
    public boolean run(CommandSender sender, org.bukkit.entity.Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole) {
        if (args.length != 1) {
            return false;
        }

        if ("off".equals(args[0])) {
            EssentialsBridge.setNickname(sender.getName(), null);
            playerMsg(sender, "Nickname cleared.");
            return true;
        }

        final String nickPlain = ChatColor.stripColor(Utilities.colorize(args[0].trim()));

        if (!nickPlain.matches("^[a-zA-Z_0-9\u00a7]+$"))
        {
            playerMsg(sender, "That nickname contains invalid characters.");
            return true;
        }
        else if (nickPlain.length() < 4 || nickPlain.length() > 30)
        {
            playerMsg(sender, "Your nickname must be between 4 and 30 characters long.");
            return true;
        }

        for (Player player : Bukkit.getOnlinePlayers())
        {
            if (player == sender_p)
            {
                continue;
            }
            if (player.getName().equalsIgnoreCase(nickPlain) || ChatColor.stripColor(player.getDisplayName()).trim().equalsIgnoreCase(nickPlain))
            {
                playerMsg(sender, "That nickname is already in use.");
                return true;
            }
        }

        final StringBuilder newNick = new StringBuilder();

        final char[] chars = nickPlain.toCharArray();
        for (char c : chars)
        {
            newNick.append(Utilities.randomChatColor()).append(c);
        }

        newNick.append(ChatColor.WHITE);

        EssentialsBridge.setNickname(sender.getName(), newNick.toString());

        playerMsg(sender, "Your nickname is now: " + newNick);

        return true;
    }
}
