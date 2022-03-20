package me.StevenLawson.TotalFreedomMod.commands;

import me.StevenLawson.TotalFreedomMod.admin.AdminList;
import me.StevenLawson.TotalFreedomMod.player.PlayerData;
import me.StevenLawson.TotalFreedomMod.util.Utilities;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = AdminLevel.SUPER, source = SourceType.BOTH)
@CommandParameters(description = "Freeze players (toggles on and off).", usage = "/<command> [target | purge]")
public class Command_fr extends FreedomCommand {
    private static boolean allFrozen = false;

    @Override
    public boolean run(CommandSender sender, org.bukkit.entity.Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole) {
        if (args.length == 0) {
            allFrozen = !allFrozen;

            if (allFrozen) {
                Utilities.adminAction(sender.getName(), "Freezing all players", false);

                setAllFrozen(true);
                playerMsg("Players are now frozen.");

                for (Player player : Bukkit.getOnlinePlayers())
                {
                    if (!AdminList.isSuperAdmin(player))
                    {
                        playerMsg(player, "You have been frozen due to rulebreakers, you will be unfrozen soon.", ChatColor.RED);
                    }
                }
            }
            else
            {
                Utilities.adminAction(sender.getName(), "Unfreezing all players", false);
                setAllFrozen(false);
                playerMsg("Players are now free to move.");
            }
        }
        else
        {
            if (args[0].toLowerCase().equals("purge"))
            {
                setAllFrozen(false);
                Utilities.adminAction(sender.getName(), "Unfreezing all players", false);
            }
            else
            {
                final Player player = getPlayer(args[0]);

                if (player == null)
                {
                    playerMsg(FreedomCommand.PLAYER_NOT_FOUND, ChatColor.RED);
                    return true;
                }

                final PlayerData playerdata = PlayerData.getPlayerData(player);
                playerdata.setFrozen(!playerdata.isFrozen());

                playerMsg(player.getName() + " has been " + (playerdata.isFrozen() ? "frozen" : "unfrozen") + ".");
                playerMsg(player, "You have been " + (playerdata.isFrozen() ? "frozen" : "unfrozen") + ".", ChatColor.AQUA);
            }
        }

        return true;
    }

    public static void setAllFrozen(boolean freeze)
    {
        allFrozen = freeze;
        for (PlayerData data : PlayerData.PLAYER_DATA.values())
        {
            data.setFrozen(freeze);
        }
    }
}
