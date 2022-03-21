package me.StevenLawson.TotalFreedomMod.commands;

import me.StevenLawson.TotalFreedomMod.player.PlayerData;
import me.StevenLawson.TotalFreedomMod.util.Utilities;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

@CommandPermissions(level = AdminLevel.SUPER, source = SourceType.BOTH)
public class Command_o extends FreedomCommand {
    @Override
    public boolean run(CommandSender sender, org.bukkit.entity.Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole) {
        if (args.length == 0) {
            if (senderIsConsole) {
                playerMsg(sender, "Only in-game players can toggle AdminChat.");
                return true;
            }

            PlayerData userinfo = PlayerData.getPlayerData(sender_p);
            userinfo.setAdminChat(!userinfo.inAdminChat());
            playerMsg(sender, "Toggled Admin Chat " + (userinfo.inAdminChat() ? "on" : "off") + ".");
        }
        else
        {
            Utilities.adminChatMessage(sender, StringUtils.join(args, " "), senderIsConsole);
        }

        return true;
    }
}
