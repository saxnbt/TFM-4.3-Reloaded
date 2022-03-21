package me.StevenLawson.TotalFreedomMod.commands;

import me.StevenLawson.TotalFreedomMod.Log;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

@CommandPermissions(level = AdminLevel.SUPER, source = SourceType.BOTH)
public class Command_premium extends FreedomCommand {
    @Override
    public boolean run(CommandSender sender, org.bukkit.entity.Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole) {
        if (args.length != 1) {
            return false;
        }

        final Player player = getPlayer(args[0]);
        final String name;

        if (player != null)
        {
            name = player.getName();
        }
        else
        {
            name = args[0];
        }

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try
            {
                final URL getUrl = new URL("https://api.mojang.com/users/profiles/minecraft/" + name);
                final HttpsURLConnection urlConnection = (HttpsURLConnection) getUrl.openConnection();
                final String message = (urlConnection.getResponseCode() == 200 ? ChatColor.DARK_GREEN + "Yes" :  ChatColor.RED + "No");
                urlConnection.disconnect();

                Bukkit.getScheduler().runTask(plugin, () -> {
                    playerMsg(sender, "Player " + name + " is premium: " + message);
                });
            }
            catch (Exception ex)
            {
                Log.severe(ex);
                Bukkit.getScheduler().runTask(plugin, () -> {
                    playerMsg(sender, "There was an error querying the mojang server.", ChatColor.RED);
                });
            }
        });

        return true;
    }
}
