package me.StevenLawson.TotalFreedomMod.commands;

import me.StevenLawson.TotalFreedomMod.player.PlayerData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

@CommandPermissions(level = AdminLevel.SUPER, source = SourceType.ONLY_IN_GAME)
public class Command_cmdspy extends FreedomCommand {
    @Override
    public boolean run(CommandSender sender, org.bukkit.entity.Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole) {

        PlayerData playerdata = PlayerData.getPlayerData(sender_p);
        playerdata.setCommandSpy(!playerdata.cmdspyEnabled());
        playerMsg(sender, "CommandSpy " + (playerdata.cmdspyEnabled() ? "enabled." : "disabled."));

        return true;
    }
}
