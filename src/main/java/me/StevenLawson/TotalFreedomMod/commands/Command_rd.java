package me.StevenLawson.TotalFreedomMod.commands;

import me.StevenLawson.TotalFreedomMod.util.Utilities;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

@CommandPermissions(level = AdminLevel.SUPER, source = SourceType.BOTH)
public class Command_rd extends FreedomCommand {
    @Override
    public boolean run(CommandSender sender, org.bukkit.entity.Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole) {
        Utilities.adminAction(sender.getName(), "Removing all server entities.", true);
        playerMsg(sender, (Utilities.TFM_EntityWiper.wipeEntities(true, true)) + " entities removed.");

        return true;
    }
}
