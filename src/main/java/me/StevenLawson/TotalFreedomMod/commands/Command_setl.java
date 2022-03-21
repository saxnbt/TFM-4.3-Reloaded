package me.StevenLawson.TotalFreedomMod.commands;

import me.StevenLawson.TotalFreedomMod.bridge.WorldEditBridge;
import me.StevenLawson.TotalFreedomMod.util.Utilities;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = AdminLevel.SUPER, source = SourceType.BOTH)
public class Command_setl extends FreedomCommand {
    @Override
    public boolean run(CommandSender sender, org.bukkit.entity.Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole) {
        Utilities.adminAction(sender.getName(), "Setting everyone's Worldedit block modification limit to 2500.", true);
        for (final Player player : server.getOnlinePlayers()) {
            WorldEditBridge.setLimit(player, 2500);
        }
        return true;
    }
}
