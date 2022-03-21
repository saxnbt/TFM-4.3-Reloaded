package me.StevenLawson.TotalFreedomMod.commands;

import me.StevenLawson.TotalFreedomMod.bridge.EssentialsBridge;
import me.StevenLawson.TotalFreedomMod.util.Utilities;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = AdminLevel.SUPER, source = SourceType.BOTH)
public class Command_denick extends FreedomCommand {
    @Override
    public boolean run(CommandSender sender, org.bukkit.entity.Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole) {
        Utilities.adminAction(sender.getName(), "Removing all nicknames", false);

        for (Player player : server.getOnlinePlayers()) {
            EssentialsBridge.setNickname(player.getName(), null);
        }

        return true;
    }
}
