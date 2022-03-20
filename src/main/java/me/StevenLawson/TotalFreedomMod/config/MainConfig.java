package me.StevenLawson.TotalFreedomMod.config;

import me.StevenLawson.TotalFreedomMod.Log;
import me.StevenLawson.TotalFreedomMod.TotalFreedomMod;
import org.apache.commons.io.FileUtils;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;
import java.util.EnumMap;
import java.util.List;

//@Deprecated
public class MainConfig
{
    public static final File CONFIG_FILE = new File(TotalFreedomMod.plugin.getDataFolder(), TotalFreedomMod.CONFIG_FILENAME);
    //
    private static final EnumMap<ConfigurationEntry, Object> ENTRY_MAP;
    private static final TFM_Defaults DEFAULTS;

    static
    {
        ENTRY_MAP = new EnumMap<ConfigurationEntry, Object>(ConfigurationEntry.class);

        TFM_Defaults tempDefaults = null;
        try
        {
            try
            {
                InputStream defaultConfig = getDefaultConfig();
                tempDefaults = new TFM_Defaults(defaultConfig);
                for (ConfigurationEntry entry : ConfigurationEntry.values())
                {
                    ENTRY_MAP.put(entry, tempDefaults.get(entry.getConfigName()));
                }
                defaultConfig.close();
            }
            catch (IOException ex)
            {
                Log.severe(ex);
            }

            copyDefaultConfig(CONFIG_FILE);

            load();
        }
        catch (Exception ex)
        {
            Log.severe(ex);
        }

        DEFAULTS = tempDefaults;
    }

    private MainConfig()
    {
        throw new AssertionError();
    }

    public static void load()
    {
        try
        {
            YamlConfiguration config = new YamlConfiguration();

            config.load(CONFIG_FILE);

            for (ConfigurationEntry entry : ConfigurationEntry.values())
            {
                String path = entry.getConfigName();
                if (config.contains(path))
                {
                    Object value = config.get(path);
                    if (value == null || entry.getType().isAssignableFrom(value.getClass()))
                    {
                        ENTRY_MAP.put(entry, value);
                    }
                    else
                    {
                        Log.warning("Value for " + entry.getConfigName() + " is of type " + value.getClass().getSimpleName() + ". Needs to be " + entry.getType().getSimpleName() + ". Using default value.");
                    }
                }
                else
                {
                    Log.warning("Missing configuration entry " + entry.getConfigName() + ". Using default value.");
                }
            }
        }
        catch (FileNotFoundException ex)
        {
            Log.severe(ex);
        }
        catch (IOException ex)
        {
            Log.severe(ex);
        }
        catch (InvalidConfigurationException ex)
        {
            Log.severe(ex);
        }
    }

    public static String getString(ConfigurationEntry entry)
    {
        try
        {
            return get(entry, String.class);
        }
        catch (IllegalArgumentException ex)
        {
            Log.severe(ex);
        }
        return null;
    }

    public static void setString(ConfigurationEntry entry, String value)
    {
        try
        {
            set(entry, value, String.class);
        }
        catch (IllegalArgumentException ex)
        {
            Log.severe(ex);
        }
    }

    public static Double getDouble(ConfigurationEntry entry)
    {
        try
        {
            return get(entry, Double.class);
        }
        catch (IllegalArgumentException ex)
        {
            Log.severe(ex);
        }
        return null;
    }

    public static void setDouble(ConfigurationEntry entry, Double value)
    {
        try
        {
            set(entry, value, Double.class);
        }
        catch (IllegalArgumentException ex)
        {
            Log.severe(ex);
        }
    }

    public static Boolean getBoolean(ConfigurationEntry entry)
    {
        try
        {
            return get(entry, Boolean.class);
        }
        catch (IllegalArgumentException ex)
        {
            Log.severe(ex);
        }
        return null;
    }

    public static void setBoolean(ConfigurationEntry entry, Boolean value)
    {
        try
        {
            set(entry, value, Boolean.class);
        }
        catch (IllegalArgumentException ex)
        {
            Log.severe(ex);
        }
    }

    public static Integer getInteger(ConfigurationEntry entry)
    {
        try
        {
            return get(entry, Integer.class);
        }
        catch (IllegalArgumentException ex)
        {
            Log.severe(ex);
        }
        return null;
    }

    public static void setInteger(ConfigurationEntry entry, Integer value)
    {
        try
        {
            set(entry, value, Integer.class);
        }
        catch (IllegalArgumentException ex)
        {
            Log.severe(ex);
        }
    }

    public static List getList(ConfigurationEntry entry)
    {
        try
        {
            return get(entry, List.class);
        }
        catch (IllegalArgumentException ex)
        {
            Log.severe(ex);
        }
        return null;
    }

    public static <T> T get(ConfigurationEntry entry, Class<T> type) throws IllegalArgumentException
    {
        Object value = ENTRY_MAP.get(entry);
        try
        {
            return type.cast(value);
        }
        catch (ClassCastException ex)
        {
            throw new IllegalArgumentException(entry.name() + " is not of type " + type.getSimpleName());
        }
    }

    public static <T> void set(ConfigurationEntry entry, T value, Class<T> type) throws IllegalArgumentException
    {
        if (!type.isAssignableFrom(entry.getType()))
        {
            throw new IllegalArgumentException(entry.name() + " is not of type " + type.getSimpleName());
        }
        if (value != null && !type.isAssignableFrom(value.getClass()))
        {
            throw new IllegalArgumentException("Value is not of type " + type.getSimpleName());
        }
        ENTRY_MAP.put(entry, value);
    }

    private static void copyDefaultConfig(File targetFile)
    {
        if (targetFile.exists())
        {
            return;
        }

        Log.info("Installing default configuration file template: " + targetFile.getPath());

        try
        {
            InputStream defaultConfig = getDefaultConfig();
            FileUtils.copyInputStreamToFile(defaultConfig, targetFile);
            defaultConfig.close();
        }
        catch (IOException ex)
        {
            Log.severe(ex);
        }
    }

    private static InputStream getDefaultConfig()
    {
        return TotalFreedomMod.plugin.getResource(TotalFreedomMod.CONFIG_FILENAME);
    }

    public static TFM_Defaults getDefaults()
    {
        return DEFAULTS;
    }

    public static class TFM_Defaults
    {
        private YamlConfiguration defaults = null;

        private TFM_Defaults(InputStream defaultConfig)
        {
            try
            {
                defaults = new YamlConfiguration();
                final InputStreamReader isr = new InputStreamReader(defaultConfig);
                defaults.load(isr);
                isr.close();
            }
            catch (IOException ex)
            {
                Log.severe(ex);
            }
            catch (InvalidConfigurationException ex)
            {
                Log.severe(ex);
            }
        }

        public Object get(String path)
        {
            return defaults.get(path);
        }
    }
}
