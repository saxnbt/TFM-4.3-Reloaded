package me.StevenLawson.TotalFreedomMod.commands;

import me.StevenLawson.TotalFreedomMod.Log;
import me.StevenLawson.TotalFreedomMod.Server;
import me.StevenLawson.TotalFreedomMod.admin.AdminList;
import me.StevenLawson.TotalFreedomMod.util.Utilities;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = AdminLevel.ALL, source = SourceType.BOTH)
@CommandParameters(description = "Switch server online-mode on and off.", usage = "/<command> <on | off>")
public class Command_onlinemode extends FreedomCommand {
    @Override
    public boolean run(CommandSender sender, org.bukkit.entity.Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole) {
        if (args.length < 1) {
            playerMsg("Server is currently running with 'online-mode=" + (server.getOnlineMode() ? "true" : "false") + "'.", ChatColor.WHITE);
            playerMsg("\"/onlinemode on\" and \"/onlinemode off\" can be used to change online mode from the console.", ChatColor.WHITE);
        } else {
            boolean online_mode;

            if (sender instanceof Player && !AdminList.isSeniorAdmin(sender, true))
            {
                playerMsg(FreedomCommand.MSG_NO_PERMS);
                return true;
            }

            if (args[0].equalsIgnoreCase("on"))
            {
                online_mode = true;
            }
            else if (args[0].equalsIgnoreCase("off"))
            {
                online_mode = false;
            }
            else
            {
                return false;
            }

            try
            {
                Server.setOnlineMode(online_mode);

                if (online_mode)
                {
                    for (Player player : server.getOnlinePlayers())
                    {
                        player.kickPlayer("Server is activating \"online-mode=true\". Please reconnect.");
                    }
                }

                Utilities.adminAction(sender.getName(), "Turning player validation " + (online_mode ? "on" : "off") + ".", true);

                server.reload();
            }
            catch (Exception ex)
            {
                Log.severe(ex);
            }
        }

        return true;
    }
}
