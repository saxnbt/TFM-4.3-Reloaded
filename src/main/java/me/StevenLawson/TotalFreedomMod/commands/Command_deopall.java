package me.StevenLawson.TotalFreedomMod.commands;

import me.StevenLawson.TotalFreedomMod.util.Utilities;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = AdminLevel.SUPER, source = SourceType.BOTH)
public class Command_deopall extends FreedomCommand {
    @Override
    public boolean run(CommandSender sender, org.bukkit.entity.Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole) {
        Utilities.adminAction(sender.getName(), "De-opping all players on the server", true);

        for (Player player : server.getOnlinePlayers()) {
            player.setOp(false);
            player.sendMessage(FreedomCommand.YOU_ARE_NOT_OP);
        }

        return true;
    }
}
