package me.StevenLawson.TotalFreedomMod.bridge;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;
import me.StevenLawson.TotalFreedomMod.Log;
import me.StevenLawson.TotalFreedomMod.util.Utilities;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class EssentialsBridge {
    private static Essentials essentialsPlugin = null;

    private EssentialsBridge() {
        throw new AssertionError();
    }

    public static Essentials getEssentialsPlugin() {
        if (essentialsPlugin == null)
        {
            try
            {
                final Plugin essentials = Bukkit.getServer().getPluginManager().getPlugin("Essentials");
                if (essentials != null)
                {
                    if (essentials instanceof Essentials)
                    {
                        essentialsPlugin = (Essentials) essentials;
                    }
                }
            }
            catch (Exception ex)
            {
                Log.severe(ex);
            }
        }
        return essentialsPlugin;
    }

    public static User getEssentialsUser(String username)
    {
        try
        {
            final Essentials essentials = getEssentialsPlugin();
            if (essentials != null)
            {
                return essentials.getUserMap().getUser(username);
            }
        }
        catch (Exception ex)
        {
            Log.severe(ex);
        }
        return null;
    }

    public static void setNickname(String username, String nickname)
    {
        try
        {
            final User user = getEssentialsUser(username);
            if (user != null)
            {
                user.setNickname(nickname);
                user.setDisplayNick();
            }
        }
        catch (Exception ex)
        {
            Log.severe(ex);
        }
    }

    public static String getNickname(String username)
    {
        try
        {
            final User user = getEssentialsUser(username);
            if (user != null)
            {
                return user.getNickname();
            }
        }
        catch (Exception ex)
        {
            Log.severe(ex);
        }
        return null;
    }

    public static long getLastActivity(String username)
    {
        try
        {
            final User user = getEssentialsUser(username);
            if (user != null)
            {
                return Utilities.<Long>getField(user, "lastActivity"); // This is weird
            }
        }
        catch (Exception ex)
        {
            Log.severe(ex);
        }
        return 0L;
    }

    public static boolean isEssentialsEnabled()
    {
        try
        {
            final Essentials essentials = getEssentialsPlugin();
            if (essentials != null)
            {
                return essentials.isEnabled();
            }
        }
        catch (Exception ex)
        {
            Log.severe(ex);
        }
        return false;
    }
}
