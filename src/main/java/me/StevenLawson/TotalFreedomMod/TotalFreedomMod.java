package me.StevenLawson.TotalFreedomMod;

import com.google.common.base.Function;
import me.StevenLawson.TotalFreedomMod.admin.AdminList;
import me.StevenLawson.TotalFreedomMod.announcer.Announcer;
import me.StevenLawson.TotalFreedomMod.ban.BanManager;
import me.StevenLawson.TotalFreedomMod.ban.PermbanList;
import me.StevenLawson.TotalFreedomMod.bridge.DiscordBridge;
import me.StevenLawson.TotalFreedomMod.command.CommandBlocker;
import me.StevenLawson.TotalFreedomMod.commands.Commands;
import me.StevenLawson.TotalFreedomMod.commands.FreedomCommand;
import me.StevenLawson.TotalFreedomMod.config.ConfigurationEntry;
import me.StevenLawson.TotalFreedomMod.deprecated.bridge.BukkitTelnetBridge;
import me.StevenLawson.TotalFreedomMod.gamerule.GameRuleHandler;
import me.StevenLawson.TotalFreedomMod.httpd.HTTPDManager;
import me.StevenLawson.TotalFreedomMod.listener.*;
import me.StevenLawson.TotalFreedomMod.player.HeartBeat;
import me.StevenLawson.TotalFreedomMod.player.PlayerList;
import me.StevenLawson.TotalFreedomMod.player.UUIDManager;
import me.StevenLawson.TotalFreedomMod.util.Utilities;
import me.StevenLawson.TotalFreedomMod.world.AdminWorld;
import me.StevenLawson.TotalFreedomMod.world.FlatlandsWorld;
import me.StevenLawson.TotalFreedomMod.world.ProtectedArea;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

public class TotalFreedomMod extends JavaPlugin {
    public static final long HEARTBEAT_RATE = 5L; // Seconds
    public static final long SERVICE_CHECKER_RATE = 120L;
    public static final int MAX_USERNAME_LENGTH = 20;
    //
    public static final String CONFIG_FILENAME = "config.yml";
    public static final String SUPERADMIN_FILENAME = "superadmin.yml";
    public static final String PERMBAN_FILENAME = "permban.yml";
    public static final String UUID_FILENAME = "uuids.db";
    public static final String PROTECTED_AREA_FILENAME = "protectedareas.dat";
    public static final String SAVED_FLAGS_FILENAME = "savedflags.dat";
    //
    @Deprecated
    public static final String YOU_ARE_NOT_OP = FreedomCommand.YOU_ARE_NOT_OP;
    //
    public static String buildNumber = "1";
    public static String buildDate = Utilities.dateToString(new Date());
    public static String buildCreator = "Unknown";
    //
    public static org.bukkit.Server server;
    public static TotalFreedomMod plugin;
    public static String pluginName;
    public static String pluginVersion;
    //
    public static boolean lockdownEnabled = false;
    public static Map<Player, Double> fuckoffEnabledFor = new HashMap<>();
    public static Logger logger;


    @Override
    public void onLoad() {
        TotalFreedomMod.plugin = this;
        TotalFreedomMod.logger = this.getLogger();
        TotalFreedomMod.server = plugin.getServer();
        TotalFreedomMod.pluginName = plugin.getDescription().getName();
        TotalFreedomMod.pluginVersion = plugin.getDescription().getVersion();

        Log.setPluginLogger(plugin.getLogger());
        Log.setServerLogger(server.getLogger());

        setAppProperties();
    }

    @Override
    public void onEnable()
    {
        Log.info("Made by Madgeek1450 and Prozza");
        Log.info("Compiled " + buildDate + " by " + buildCreator);

        final Utilities.MethodTimer timer = new Utilities.MethodTimer();
        timer.start();

        if (!Server.COMPILE_NMS_VERSION.equals(Utilities.getNmsVersion()))
        {
            Log.warning(pluginName + " is compiled for " + Server.COMPILE_NMS_VERSION + " but the server is running "
                    + "version " + Utilities.getNmsVersion() + "!");
            Log.warning("This might result in unexpected behaviour!");
        }

        Utilities.deleteCoreDumps();

        // Create backups
        Utilities.createBackups(CONFIG_FILENAME, true);
        Utilities.createBackups(SUPERADMIN_FILENAME);
        Utilities.createBackups(PERMBAN_FILENAME);

        // Load services
        UUIDManager.load();
        AdminList.load();
        PermbanList.load();
        PlayerList.load();
        BanManager.load();
        Announcer.load();
        ProtectedArea.load();
        DiscordBridge.load();

        // Start SuperAdmin service
        server.getServicesManager().register(Function.class, AdminList.SUPERADMIN_SERVICE, plugin, ServicePriority.Normal);

        final PluginManager pm = server.getPluginManager();
        pm.registerEvents(new EntityListener(), plugin);
        pm.registerEvents(new BlockListener(), plugin);
        pm.registerEvents(new PlayerListener(), plugin);
        pm.registerEvents(new WeatherListener(), plugin);
        pm.registerEvents(new ServerListener(), plugin);

        // Bridge
        pm.registerEvents(new BukkitTelnetBridge(), plugin);
        pm.registerEvents(new WorldEditListener(), plugin);

        try {
            FlatlandsWorld.getInstance().getWorld();
        } catch (Exception ex) {
            Log.warning("Could not load world: Flatlands");
        }

        try {
            AdminWorld.getInstance().getWorld();
        }
        catch (Exception ex)
        {
            Log.warning("Could not load world: AdminWorld");
        }

        // Initialize game rules
        GameRuleHandler.setGameRule(GameRuleHandler.TFM_GameRule.DO_DAYLIGHT_CYCLE, !ConfigurationEntry.DISABLE_NIGHT.getBoolean(), false);
        GameRuleHandler.setGameRule(GameRuleHandler.TFM_GameRule.DO_FIRE_TICK, ConfigurationEntry.ALLOW_FIRE_SPREAD.getBoolean(), false);
        GameRuleHandler.setGameRule(GameRuleHandler.TFM_GameRule.DO_MOB_LOOT, false, false);
        GameRuleHandler.setGameRule(GameRuleHandler.TFM_GameRule.DO_MOB_SPAWNING, !ConfigurationEntry.MOB_LIMITER_ENABLED.getBoolean(), false);
        GameRuleHandler.setGameRule(GameRuleHandler.TFM_GameRule.DO_TILE_DROPS, false, false);
        GameRuleHandler.setGameRule(GameRuleHandler.TFM_GameRule.MOB_GRIEFING, false, false);
        GameRuleHandler.setGameRule(GameRuleHandler.TFM_GameRule.NATURAL_REGENERATION, true, false);
        GameRuleHandler.commitGameRules();

        // Disable weather
        if (ConfigurationEntry.DISABLE_WEATHER.getBoolean())
        {
            for (World world : server.getWorlds())
            {
                world.setThundering(false);
                world.setStorm(false);
                world.setThunderDuration(0);
                world.setWeatherDuration(0);
            }
        }

        // Heartbeat
        new HeartBeat(plugin).runTaskTimer(plugin, HEARTBEAT_RATE * 20L, HEARTBEAT_RATE * 20L);

        // Start services
        HTTPDManager.start();

        timer.update();

        // Register commands
        Commands.registerCommands(this);


        Log.info("Version " + pluginVersion + " for " + Server.COMPILE_NMS_VERSION + " enabled in " + timer.getTotal() + "ms");

        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                CommandBlocker.load();

                // Add spawnpoints later - https://github.com/TotalFreedom/TotalFreedomMod/issues/438
                ProtectedArea.autoAddSpawnpoints();
            }
        }.runTaskLater(plugin, 20L);
    }

    @Override
    public void onDisable()
    {
        HTTPDManager.stop();
        BanManager.save();
        UUIDManager.close();
        DiscordBridge.stop();

        server.getScheduler().cancelTasks(plugin);

        Log.info("Plugin disabled");
    }

    private static void setAppProperties()
    {
        TotalFreedomMod.buildNumber = "1337";
        TotalFreedomMod.buildCreator = "You!";
    }
}
