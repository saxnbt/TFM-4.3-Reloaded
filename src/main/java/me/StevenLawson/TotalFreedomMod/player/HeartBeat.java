package me.StevenLawson.TotalFreedomMod.player;

import me.StevenLawson.TotalFreedomMod.TotalFreedomMod;
import me.StevenLawson.TotalFreedomMod.bridge.EssentialsBridge;
import me.StevenLawson.TotalFreedomMod.config.ConfigurationEntry;
import me.StevenLawson.TotalFreedomMod.util.Utilities;
import me.StevenLawson.TotalFreedomMod.world.AdminWorld;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class HeartBeat extends BukkitRunnable
{
    private static final long AUTO_KICK_TIME = (long) ConfigurationEntry.AUTOKICK_TIME.getInteger() * 1000L;
    private final TotalFreedomMod plugin;
    private final Server server;
    private static Long lastRan = null;

    public HeartBeat(TotalFreedomMod instance)
    {
        this.plugin = instance;
        this.server = plugin.getServer();
    }

    public static Long getLastRan()
    {
        return lastRan;
    }

    @Override
    public void run()
    {
        lastRan = System.currentTimeMillis();

        final boolean doAwayKickCheck = ConfigurationEntry.AUTOKICK_ENABLED.getBoolean()
                && EssentialsBridge.isEssentialsEnabled()
                && ((server.getOnlinePlayers().size() / server.getMaxPlayers()) > ConfigurationEntry.AUTOKICK_THRESHOLD.getDouble());

        for (Player player : server.getOnlinePlayers())
        {
            final PlayerData playerdata = PlayerData.getPlayerData(player);
            playerdata.resetMsgCount();
            playerdata.resetBlockDestroyCount();
            playerdata.resetBlockPlaceCount();

            if (doAwayKickCheck)
            {
                final long lastActivity = EssentialsBridge.getLastActivity(player.getName());
                if (lastActivity > 0 && lastActivity + AUTO_KICK_TIME < System.currentTimeMillis())
                {
                    player.kickPlayer("Automatically kicked by server for inactivity.");
                }
            }
        }

        if (ConfigurationEntry.AUTO_ENTITY_WIPE.getBoolean())
        {
            Utilities.TFM_EntityWiper.wipeEntities(!ConfigurationEntry.ALLOW_EXPLOSIONS.getBoolean(), false);
        }

        if (ConfigurationEntry.DISABLE_WEATHER.getBoolean())
        {
            for (World world : server.getWorlds())
            {
                try
                {
                    if (world == AdminWorld.getInstance().getWorld() && AdminWorld.getInstance().getWeatherMode() != AdminWorld.WeatherMode.OFF)
                    {
                        continue;
                    }
                }
                catch (Exception ex)
                {
                }

                if (world.getWeatherDuration() > 0)
                {
                    world.setThundering(false);
                    world.setWeatherDuration(0);
                }
                else if (world.getThunderDuration() > 0)
                {
                    world.setStorm(false);
                    world.setThunderDuration(0);
                }
            }
        }
    }
}
