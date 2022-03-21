package me.StevenLawson.TotalFreedomMod;

import me.StevenLawson.TotalFreedomMod.admin.AdminList;
import me.StevenLawson.TotalFreedomMod.ban.Ban;
import me.StevenLawson.TotalFreedomMod.ban.BanManager;
import me.StevenLawson.TotalFreedomMod.ban.PermbanList;
import me.StevenLawson.TotalFreedomMod.bridge.EssentialsBridge;
import me.StevenLawson.TotalFreedomMod.config.ConfigurationEntry;
import me.StevenLawson.TotalFreedomMod.player.UUIDManager;
import me.StevenLawson.TotalFreedomMod.util.SynchronousUtil;
import me.StevenLawson.TotalFreedomMod.util.Utilities;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import net.minecraft.server.v1_8_R3.PropertyManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

import static me.StevenLawson.TotalFreedomMod.listener.PlayerListener.DEFAULT_PORT;

public class Server
{
    public static final String COMPILE_NMS_VERSION = "v1_8_R2";
    public static final Pattern USERNAME_REGEX = Pattern.compile("^[\\w\\d_]{3,20}$");

    public static void setOnlineMode(boolean mode)
    {
        final PropertyManager manager = MinecraftServer.getServer().getPropertyManager();
        manager.setProperty("online-mode", mode);
        manager.savePropertiesFile();
    }

    public static int purgeWhitelist()
    {
        String[] whitelisted = MinecraftServer.getServer().getPlayerList().getWhitelisted();
        int size = whitelisted.length;
        for (EntityPlayer player : MinecraftServer.getServer().getPlayerList().players)
        {
            MinecraftServer.getServer().getPlayerList().getWhitelist().remove(player.getProfile());
        }

        try
        {
            MinecraftServer.getServer().getPlayerList().getWhitelist().save();
        }
        catch (Exception ex)
        {
            Log.warning("Could not purge the whitelist!");
            Log.warning(ex);
        }
        return size;
    }

    public static boolean isWhitelisted()
    {
        return MinecraftServer.getServer().getPlayerList().getHasWhitelist();
    }

    public static List<?> getWhitelisted()
    {
        return Arrays.asList(MinecraftServer.getServer().getPlayerList().getWhitelisted());
    }

    public static String getVersion()
    {
        return MinecraftServer.getServer().getVersion();
    }

    public static void handlePlayerPreLogin(AsyncPlayerPreLoginEvent event)
    {
        final String ip = event.getAddress().getHostAddress().trim();
        final boolean isAdmin = AdminList.isSuperAdminSafe(null, ip);

        // Check if the player is already online
        for (Player onlinePlayer : TotalFreedomMod.server.getOnlinePlayers())
        {
            if (!onlinePlayer.getName().equalsIgnoreCase(event.getName()))
            {
                continue;
            }

            if (!isAdmin) {
                event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, "Your username is already logged into this server.");
            } else {
                event.allow();
                SynchronousUtil.playerKick(onlinePlayer, "An admin just logged in with the username you are using.");
            }
            return;
        }
    }

    public static void handlePlayerLogin(PlayerLoginEvent event)
    {
        final org.bukkit.Server server = TotalFreedomMod.server;
        final Player player = event.getPlayer();
        final String username = player.getName();
        final String ip = event.getAddress().getHostAddress().trim();
        final UUID uuid = UUIDManager.newPlayer(player, ip);

        // Check username length
        if (username.length() < 3 || username.length() > TotalFreedomMod.MAX_USERNAME_LENGTH)
        {
            event.disallow(Result.KICK_OTHER, "Your username is an invalid length (must be between 3 and 20 characters long).");
            return;
        }

        // Check username characters
        if (!USERNAME_REGEX.matcher(username).find())
        {
            event.disallow(Result.KICK_OTHER, "Your username contains invalid characters.");
            return;
        }

        // Check force-IP match
        if (ConfigurationEntry.FORCE_IP_ENABLED.getBoolean())
        {
            final String hostname = event.getHostname().replace("FML", ""); // Forge fix - https://github.com/TotalFreedom/TotalFreedomMod/issues/493
            final String connectAddress = ConfigurationEntry.SERVER_ADDRESS.getString();
            final int connectPort = TotalFreedomMod.server.getPort();

            if (!hostname.equalsIgnoreCase(connectAddress + ":" + connectPort) && !hostname.equalsIgnoreCase(connectAddress + ".:" + connectPort))
            {
                final int forceIpPort = ConfigurationEntry.FORCE_IP_PORT.getInteger();
                event.disallow(PlayerLoginEvent.Result.KICK_OTHER,
                        ConfigurationEntry.FORCE_IP_KICKMSG.getString()
                        .replace("%address%", ConfigurationEntry.SERVER_ADDRESS.getString() + (forceIpPort == DEFAULT_PORT ? "" : ":" + forceIpPort)));
                return;
            }

        }

        // Check if player is admin
        // Not safe to use Utilities.isSuperAdmin(player) because player.getAddress() will return a null until after player login.
        final boolean isAdmin = AdminList.isSuperAdminSafe(uuid, ip);

        // Validation below this point
        if (isAdmin) // Player is superadmin
        {
            // Force-allow log in
            event.allow();

            int count = server.getOnlinePlayers().size();
            if (count >= server.getMaxPlayers())
            {
                for (Player onlinePlayer : server.getOnlinePlayers())
                {
                    if (!AdminList.isSuperAdmin(onlinePlayer))
                    {
                        onlinePlayer.kickPlayer("You have been kicked to free up room for an admin.");
                        count--;
                    }

                    if (count < server.getMaxPlayers())
                    {
                        break;
                    }
                }
            }

            if (count >= server.getMaxPlayers())
            {
                event.disallow(Result.KICK_OTHER, "The server is full and a player could not be kicked, sorry!");
                return;
            }

            return;
        }

        // Player is not an admin
        // Server full check
        if (server.getOnlinePlayers().size() >= server.getMaxPlayers())
        {
            event.disallow(Result.KICK_FULL, "Sorry, but this server is full.");
            return;
        }

        // Admin-only mode
        if (ConfigurationEntry.ADMIN_ONLY_MODE.getBoolean())
        {
            event.disallow(Result.KICK_OTHER, "Server is temporarily open to admins only.");
            return;
        }

        // Lockdown mode
        if (TotalFreedomMod.lockdownEnabled)
        {
            event.disallow(Result.KICK_OTHER, "Server is currently in lockdown mode.");
            return;
        }

        // Whitelist
        if (isWhitelisted())
        {
            if (!getWhitelisted().contains(username.toLowerCase()))
            {
                event.disallow(Result.KICK_OTHER, "You are not whitelisted on this server.");
                return;
            }
        }

        // UUID ban
        if (BanManager.isUuidBanned(uuid))
        {
            final Ban ban = BanManager.getByUuid(uuid);
            event.disallow(Result.KICK_OTHER, ban.getKickMessage());
            return;
        }

        // IP ban
        if (BanManager.isIpBanned(ip))
        {
            final Ban ban = BanManager.getByIp(ip);
            event.disallow(Result.KICK_OTHER, ban.getKickMessage());
            return;
        }

        // Permbanned IPs
        for (String testIp : PermbanList.getPermbannedIps())
        {
            if (Utilities.fuzzyIpMatch(testIp, ip, 4))
            {
                event.disallow(Result.KICK_OTHER,
                        ChatColor.RED + "Your IP address is permanently banned from this server.\n"
                        + "Release procedures are available at\n"
                        + ChatColor.GOLD + ConfigurationEntry.SERVER_PERMBAN_URL.getString());
                return;
            }
        }

        // Permbanned usernames
        for (String testPlayer : PermbanList.getPermbannedPlayers())
        {
            if (testPlayer.equalsIgnoreCase(username))
            {
                event.disallow(Result.KICK_OTHER,
                        ChatColor.RED + "Your username is permanently banned from this server.\n"
                        + "Release procedures are available at\n"
                        + ChatColor.GOLD + ConfigurationEntry.SERVER_PERMBAN_URL.getString());
                return;
            }
        }
    }

    public double getTPS() {
        return EssentialsBridge.getEssentialsPlugin().getTimer().getAverageTPS();
    }
}
