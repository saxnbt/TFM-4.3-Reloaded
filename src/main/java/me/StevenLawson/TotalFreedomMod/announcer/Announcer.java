package me.StevenLawson.TotalFreedomMod.announcer;

import me.StevenLawson.TotalFreedomMod.TotalFreedomMod;
import me.StevenLawson.TotalFreedomMod.config.ConfigurationEntry;
import me.StevenLawson.TotalFreedomMod.util.Utilities;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Announcer
{
    private static final List<String> ANNOUNCEMENTS = new ArrayList<String>();
    private static boolean enabled;
    private static long interval;
    private static String prefix;
    private static BukkitRunnable announcer;

    private Announcer()
    {
        throw new AssertionError();
    }

    public static boolean isEnabled()
    {
        return enabled;
    }

    public static List<String> getAnnouncements()
    {
        return Collections.unmodifiableList(ANNOUNCEMENTS);
    }

    public static long getTickInterval()
    {
        return interval;
    }

    public static String getPrefix()
    {
        return prefix;
    }

    public static void load()
    {
        stop();

        ANNOUNCEMENTS.clear();

        for (Object announcement : ConfigurationEntry.ANNOUNCER_ANNOUNCEMENTS.getList())
        {
            ANNOUNCEMENTS.add(Utilities.colorize((String) announcement));
        }

        enabled = ConfigurationEntry.ANNOUNCER_ENABLED.getBoolean();
        interval = ConfigurationEntry.ANNOUNCER_INTERVAL.getInteger() * 20L;
        prefix = Utilities.colorize(ConfigurationEntry.ANNOUNCER_PREFIX.getString());

        if (enabled)
        {
            start();
        }
    }

    public static boolean isStarted()
    {
        return announcer != null;
    }

    public static void start()
    {
        if (isStarted())
        {
            return;
        }

        announcer = new BukkitRunnable()
        {
            private int current = 0;

            @Override
            public void run()
            {
                current++;

                if (current >= ANNOUNCEMENTS.size())
                {
                    current = 0;
                }

                Utilities.bcastMsg(prefix + ANNOUNCEMENTS.get(current));
            }
        };

        announcer.runTaskTimer(TotalFreedomMod.plugin, interval, interval);
    }

    public static void stop()
    {
        if (announcer == null)
        {
            return;
        }

        try
        {
            announcer.cancel();
        }
        finally
        {
            announcer = null;
        }
    }
}
