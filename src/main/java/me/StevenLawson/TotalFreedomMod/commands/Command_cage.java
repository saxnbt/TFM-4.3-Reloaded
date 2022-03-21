package me.StevenLawson.TotalFreedomMod.commands;

import me.StevenLawson.TotalFreedomMod.player.PlayerData;
import me.StevenLawson.TotalFreedomMod.util.Utilities;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = AdminLevel.SUPER, source = SourceType.BOTH)
@CommandParameters(description = "Place a cage around someone.", usage = "/<command> <purge | off | <partialname> [outermaterial] [innermaterial]>")
public class Command_cage extends FreedomCommand {
    @Override
    public boolean run(CommandSender sender, org.bukkit.entity.Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole) {
        if (args.length == 0) {
            return false;
        }

        if ("off".equals(args[0]) && sender instanceof Player) {
            Utilities.adminAction(sender.getName(), "Uncaging " + sender.getName(), true);
            PlayerData playerdata = PlayerData.getPlayerData(sender_p);

            playerdata.setCaged(false);
            playerdata.regenerateHistory();
            playerdata.clearHistory();

            return true;
        }
        else if ("purge".equals(args[0]))
        {
            Utilities.adminAction(sender.getName(), "Uncaging all players", true);

            for (Player player : server.getOnlinePlayers())
            {
                PlayerData playerdata = PlayerData.getPlayerData(player);
                playerdata.setCaged(false);
                playerdata.regenerateHistory();
                playerdata.clearHistory();
            }

            return true;
        }

        final Player player = getPlayer(args[0]);

        if (player == null)
        {
            sender.sendMessage(FreedomCommand.PLAYER_NOT_FOUND);
            return true;
        }

        PlayerData playerdata = PlayerData.getPlayerData(player);

        Material outerMaterial = Material.GLASS;
        Material innerMaterial = Material.AIR;

        if (args.length >= 2)
        {
            if ("off".equals(args[1]))
            {
                Utilities.adminAction(sender.getName(), "Uncaging " + player.getName(), true);

                playerdata.setCaged(false);
                playerdata.regenerateHistory();
                playerdata.clearHistory();

                return true;
            }
            else
            {
                if ("darth".equalsIgnoreCase(args[1]))
                {
                    outerMaterial = Material.SKULL;
                }
                else if (Material.matchMaterial(args[1]) != null)
                {
                    outerMaterial = Material.matchMaterial(args[1]);
                }
            }
        }

        if (args.length >= 3)
        {
            if (args[2].equalsIgnoreCase("water"))
            {
                innerMaterial = Material.STATIONARY_WATER;
            }
            else if (args[2].equalsIgnoreCase("lava"))
            {
                innerMaterial = Material.STATIONARY_LAVA;
            }
        }

        Location targetPos = player.getLocation().clone().add(0, 1, 0);
        playerdata.setCaged(true, targetPos, outerMaterial, innerMaterial);
        playerdata.regenerateHistory();
        playerdata.clearHistory();
        Utilities.buildHistory(targetPos, 2, playerdata);
        Utilities.generateHollowCube(targetPos, 2, outerMaterial);
        Utilities.generateCube(targetPos, 1, innerMaterial);

        player.setGameMode(GameMode.SURVIVAL);

        if (outerMaterial != Material.SKULL)
        {
            Utilities.adminAction(sender.getName(), "Caging " + player.getName(), true);
        }
        else
        {
            Utilities.adminAction(sender.getName(), "Caging " + player.getName() + " in PURE_DARTH", true);
        }

        return true;
    }
}
