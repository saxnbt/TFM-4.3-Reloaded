package me.StevenLawson.TotalFreedomMod.commands;

import me.StevenLawson.TotalFreedomMod.player.PlayerData;
import me.StevenLawson.TotalFreedomMod.util.Utilities;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

@CommandPermissions(level = AdminLevel.SUPER, source = SourceType.BOTH)
public class Command_orbit extends FreedomCommand {
    @Override
    public boolean run(CommandSender sender, org.bukkit.entity.Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole) {
        if (args.length == 0) {
            return false;
        }

        Player player = getPlayer(args[0]);

        if (player == null) {
            playerMsg(sender, FreedomCommand.PLAYER_NOT_FOUND, ChatColor.RED);
            return true;
        }

        PlayerData playerdata = PlayerData.getPlayerData(player);

        double strength = 10.0;

        if (args.length >= 2)
        {
            if (args[1].equals("stop"))
            {
                playerMsg(sender, "Stopped orbiting " + player.getName());
                playerdata.stopOrbiting();
                return true;
            }

            try
            {
                strength = Math.max(1.0, Math.min(150.0, Double.parseDouble(args[1])));
            }
            catch (NumberFormatException ex)
            {
                playerMsg(sender, ex.getMessage(), ChatColor.RED);
                return true;
            }
        }

        player.setGameMode(GameMode.SURVIVAL);
        playerdata.startOrbiting(strength);

        player.setVelocity(new Vector(0, strength, 0));
        Utilities.adminAction(sender.getName(), "Orbiting " + player.getName(), false);

        return true;
    }
}
