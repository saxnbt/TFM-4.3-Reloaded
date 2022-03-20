package me.StevenLawson.TotalFreedomMod.listener;

import me.StevenLawson.TotalFreedomMod.Server;
import me.StevenLawson.TotalFreedomMod.ban.BanManager;
import me.StevenLawson.TotalFreedomMod.config.ConfigurationEntry;
import me.StevenLawson.TotalFreedomMod.util.Utilities;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;

public class ServerListener implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onServerPing(ServerListPingEvent event) {
        final String ip = event.getAddress().getHostAddress();

        if (BanManager.isIpBanned(ip)) {
            event.setMotd(ChatColor.RED + "You are banned.");
            return;
        }

        if (ConfigurationEntry.ADMIN_ONLY_MODE.getBoolean())
        {
            event.setMotd(ChatColor.RED + "Server is closed.");
            return;
        }

        if (Bukkit.hasWhitelist())
        {
            event.setMotd(ChatColor.RED + "Whitelist enabled.");
            return;
        }

        if (Bukkit.getOnlinePlayers().size() >= Bukkit.getMaxPlayers())
        {
            event.setMotd(ChatColor.RED + "Server is full.");
            return;
        }

        if (!ConfigurationEntry.SERVER_COLORFUL_MOTD.getBoolean())
        {
            event.setMotd(Utilities.colorize(ConfigurationEntry.SERVER_MOTD.getString()
                    .replace("%mcversion%", Server.getVersion())));
            return;
        }
        // Colorful MOTD

        final StringBuilder motd = new StringBuilder();

        for (String word : ConfigurationEntry.SERVER_MOTD.getString().replace("%mcversion%", Server.getVersion()).split(" "))
        {
            motd.append(Utilities.randomChatColor()).append(word).append(" ");
        }

        event.setMotd(Utilities.colorize(motd.toString()));
    }
}
