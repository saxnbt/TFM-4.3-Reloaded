package me.StevenLawson.TotalFreedomMod.commands;

import me.StevenLawson.TotalFreedomMod.admin.AdminList;
import me.StevenLawson.TotalFreedomMod.util.Utilities;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = AdminLevel.OP, source = SourceType.BOTH)
@CommandParameters(description = "Manager operators", usage = "/<command> <count | purge>")
public class Command_ops extends FreedomCommand {
    @Override
    public boolean run(CommandSender sender, org.bukkit.entity.Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole) {
        if (args.length != 1) {
            return false;
        }

        if (args[0].equals("count")) {
            int totalOps = server.getOperators().size();
            int onlineOps = 0;

            for (Player player : server.getOnlinePlayers())
            {
                if (player.isOp())
                {
                    onlineOps++;
                }
            }

            playerMsg("Online OPs: " + onlineOps);
            playerMsg("Offline OPs: " + (totalOps - onlineOps));
            playerMsg("Total OPs: " + totalOps);

            return true;
        }

        if (args[0].equals("purge"))
        {
            if (!AdminList.isSuperAdmin(sender))
            {
                playerMsg(FreedomCommand.MSG_NO_PERMS);
                return true;
            }

            Utilities.adminAction(sender.getName(), "Purging all operators", true);

            for (OfflinePlayer player : server.getOperators())
            {
                player.setOp(false);
                if (player.isOnline())
                {
                    playerMsg(player.getPlayer(), FreedomCommand.YOU_ARE_NOT_OP);
                }
            }
            return true;
        }

        return false;
    }
}
