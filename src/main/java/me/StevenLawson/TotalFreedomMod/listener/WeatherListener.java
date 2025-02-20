package me.StevenLawson.TotalFreedomMod.listener;

import me.StevenLawson.TotalFreedomMod.config.ConfigurationEntry;
import me.StevenLawson.TotalFreedomMod.world.AdminWorld;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.weather.ThunderChangeEvent;
import org.bukkit.event.weather.WeatherChangeEvent;

public class WeatherListener implements Listener {
    @EventHandler(priority = EventPriority.HIGH)
    public void onThunderChange(ThunderChangeEvent event) {
        try {
            if (event.getWorld() == AdminWorld.getInstance().getWorld() && AdminWorld.getInstance().getWeatherMode() != AdminWorld.WeatherMode.OFF) {
                return;
            }
        }
        catch (Exception ex)
        {
        }

        if (event.toThunderState() && ConfigurationEntry.DISABLE_WEATHER.getBoolean())
        {
            event.setCancelled(true);
            return;
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onWeatherChange(WeatherChangeEvent event)
    {
        try
        {
            if (event.getWorld() == AdminWorld.getInstance().getWorld() && AdminWorld.getInstance().getWeatherMode() != AdminWorld.WeatherMode.OFF)
            {
                return;
            }
        }
        catch (Exception ex)
        {
        }

        if (event.toWeatherState() && ConfigurationEntry.DISABLE_WEATHER.getBoolean())
        {
            event.setCancelled(true);
            return;
        }
    }
}
