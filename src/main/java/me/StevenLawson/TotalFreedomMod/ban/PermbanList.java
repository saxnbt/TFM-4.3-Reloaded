package me.StevenLawson.TotalFreedomMod.ban;

import me.StevenLawson.TotalFreedomMod.Log;
import me.StevenLawson.TotalFreedomMod.TotalFreedomMod;
import me.StevenLawson.TotalFreedomMod.config.Configuration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PermbanList
{

    private static final List<String> PERMBANNED_PLAYERS;
    private static final List<String> PERMBANNED_IPS;

    static
    {
        PERMBANNED_PLAYERS = new ArrayList<String>();
        PERMBANNED_IPS = new ArrayList<String>();
    }

    private PermbanList()
    {
        throw new AssertionError();
    }

    public static List<String> getPermbannedPlayers()
    {
        return Collections.unmodifiableList(PERMBANNED_PLAYERS);
    }

    public static List<String> getPermbannedIps()
    {
        return Collections.unmodifiableList(PERMBANNED_IPS);
    }

    public static void load()
    {
        PERMBANNED_PLAYERS.clear();
        PERMBANNED_IPS.clear();

        final Configuration config = new Configuration(TotalFreedomMod.plugin, TotalFreedomMod.PERMBAN_FILENAME, true);
        config.load();

        for (String playername : config.getKeys(false))
        {
            PERMBANNED_PLAYERS.add(playername.toLowerCase().trim());

            List<String> playerIps = config.getStringList(playername);
            for (String ip : playerIps)
            {
                ip = ip.trim();
                if (!PERMBANNED_IPS.contains(ip))
                {
                    PERMBANNED_IPS.add(ip);
                }
            }
        }

        Log.info("Loaded " + PERMBANNED_PLAYERS.size() + " permanently banned players and " + PERMBANNED_IPS.size() + " permanently banned IPs.");
    }
}
