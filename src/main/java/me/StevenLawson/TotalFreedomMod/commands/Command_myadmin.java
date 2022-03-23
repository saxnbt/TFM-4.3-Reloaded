package me.StevenLawson.TotalFreedomMod.commands;

import me.StevenLawson.TotalFreedomMod.admin.AdminList;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

@CommandPermissions(level = AdminLevel.SUPER, source = SourceType.ONLY_IN_GAME)
@CommandParameters(description = "Manages your admin login message and other utilities.", usage = "/<command> <clearloginmsg | setlogin <message>>")
public class Command_myadmin extends FreedomCommand {
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole) {
        final UUID uuid = sender_p.getUniqueId(); // Get the sender's uuid as a variable.
        if(args.length == 1) {
            if ("clearloginmsg".equalsIgnoreCase(args[0])) {
                playerMsg(ChatColor.GRAY + "Cleared your custom login message."); // Notify player that the login message has been set.

                AdminList.getEntry(uuid).setCustomLoginMessage(""); // Set the custom login message to the value.
                AdminList.save(AdminList.getEntry(uuid)); // Save the modified value to the super admin configuration.

                AdminList.updateIndexLists(); // Update and refresh configuration.
                return true;
            } else {
                return false;
            }
        } else if (args.length >= 2) {
            if ("setlogin".equalsIgnoreCase(args[0])) {
                final String inputMessage = StringUtils.join(args, " ", 1, args.length); // Parse the input provided.
                playerMsg(ChatColor.GRAY + "Set your custom login message."); // Notify player that the login message has been set.
                playerMsg(ChatColor.GRAY + "Your login message will show up as: \n" + ChatColor.AQUA + sender_p.getPlayer().getName() + " is " + inputMessage);
                        AdminList.getEntry(uuid).setCustomLoginMessage(inputMessage); // Set the custom login message to the value.
                AdminList.save(AdminList.getEntry(uuid)); // Save the modified value to the super admin configuration.

                AdminList.updateIndexLists(); // Update and refresh configuration.
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
}
