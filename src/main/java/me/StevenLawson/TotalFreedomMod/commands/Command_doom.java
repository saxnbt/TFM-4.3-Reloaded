package me.StevenLawson.TotalFreedomMod.commands;

import me.StevenLawson.TotalFreedomMod.admin.AdminList;
import me.StevenLawson.TotalFreedomMod.ban.Ban;
import me.StevenLawson.TotalFreedomMod.ban.BanManager;
import me.StevenLawson.TotalFreedomMod.player.PlayerList;
import me.StevenLawson.TotalFreedomMod.util.Utilities;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

@CommandPermissions(level = AdminLevel.SENIOR, source = SourceType.ONLY_CONSOLE)
public class Command_doom extends FreedomCommand {
    @Override
    public boolean run(final CommandSender sender, org.bukkit.entity.Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole) {
        if (args.length != 1) {
            return false;
        }

        final Player player = getPlayer(args[0]);

        if (player == null) {
            sender.sendMessage(FreedomCommand.PLAYER_NOT_FOUND);
            return true;
        }

        Utilities.adminAction(sender.getName(), "Casting oblivion over " + player.getName(), true);
        Utilities.bcastMsg(player.getName() + " will be completely obliterated!", ChatColor.RED);

        final String ip = player.getAddress().getAddress().getHostAddress().trim();

        // remove from superadmin
        if (AdminList.isSuperAdmin(player))
        {
            Utilities.adminAction(sender.getName(), "Removing " + player.getName() + " from the superadmin list.", true);
            AdminList.removeSuperadmin(player);
        }

        // remove from whitelist
        player.setWhitelisted(false);

        // deop
        player.setOp(false);

        // ban IPs
        for (String playerIp : PlayerList.getEntry(player).getIps())
        {
            BanManager.addIpBan(new Ban(playerIp, player.getName()));
        }

        // ban uuid
        BanManager.addUuidBan(player);

        // set gamemode to survival
        player.setGameMode(GameMode.SURVIVAL);

        // clear inventory
        player.closeInventory();
        player.getInventory().clear();

        // ignite player
        player.setFireTicks(10000);

        //removed explosion (it bypasses TFM's explosive toggle and makes a BIG hole that no one likes fixing)

        // Shoot the player in the sky
        player.setVelocity(player.getVelocity().clone().add(new Vector(0, 20, 0)));

        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                // strike lightning
                player.getWorld().strikeLightning(player.getLocation());

                // kill (if not done already)
                player.setHealth(0.0);
            }
        }.runTaskLater(plugin, 2L * 20L);

        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                // message
                Utilities.adminAction(sender.getName(), "Banning " + player.getName() + ", IP: " + Utilities.getFuzzyIp(ip), true);

                //removed explosion (it bypasses TFM's explosive toggle and makes a BIG hole that no one likes fixing)

                // kick player
                player.kickPlayer(ChatColor.RED + "FUCKOFF, and get your shit together!");
            }
        }.runTaskLater(plugin, 3L * 20L);

        return true;
    }
}
