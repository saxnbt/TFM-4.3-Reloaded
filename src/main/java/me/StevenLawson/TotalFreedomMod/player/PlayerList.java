package me.StevenLawson.TotalFreedomMod.player;

import com.google.common.collect.Sets;
import me.StevenLawson.TotalFreedomMod.Log;
import me.StevenLawson.TotalFreedomMod.TotalFreedomMod;
import me.StevenLawson.TotalFreedomMod.config.Configuration;
import me.StevenLawson.TotalFreedomMod.util.Utilities;
import org.bukkit.Bukkit;

import java.io.File;
import java.util.*;

public class PlayerList {

    private static final Map<UUID, Player> PLAYER_LIST = new HashMap<UUID, Player>();

    private PlayerList() {
        throw new AssertionError();
    }

    public static Set<Player> getAllPlayers() {
        return Collections.unmodifiableSet(Sets.newHashSet(PLAYER_LIST.values()));
    }

    public static void load() {
        PLAYER_LIST.clear();

        // Load online players
        for (org.bukkit.entity.Player player : Bukkit.getOnlinePlayers()) {
            getEntry(player);
        }

        Log.info("Loaded playerdata for " + PLAYER_LIST.size() + " players");
    }

    public static void saveAll() {
        for (Player entry : PLAYER_LIST.values()) {
            save(entry);
        }
    }

    // May return null
    public static Player getEntry(UUID uuid) {
        if (PLAYER_LIST.containsKey(uuid)) {
            return PLAYER_LIST.get(uuid);
        }

        final File configFile = getConfigFile(uuid);

        if (!configFile.exists()) {
            return null;
        }

        final Player entry = new Player(uuid, getConfig(uuid));

        if (entry.isComplete()) {
            PLAYER_LIST.put(uuid, entry);
            return entry;
        } else {
            Log.warning("Could not load entry: Entry is not complete!");
            configFile.delete();
        }

        return null;
    }

    public static Player getEntry(org.bukkit.entity.Player player) {
        final UUID uuid = UUIDManager.getUniqueId(player);
        Player entry = getEntry(uuid);

        if (entry != null) {
            return entry;
        }

        final long unix = Utilities.getUnixTime();
        entry = new Player(uuid);
        entry.setFirstLoginName(player.getName());
        entry.setLastLoginName(player.getName());
        entry.setFirstLoginUnix(unix);
        entry.setLastLoginUnix(unix);
        entry.addIp(Utilities.getIp(player));

        save(entry);
        PLAYER_LIST.put(uuid, entry);

        return entry;
    }

    public static void removeEntry(org.bukkit.entity.Player player) {
        final UUID uuid = UUIDManager.getUniqueId(player);

        if (!PLAYER_LIST.containsKey(uuid)) {
            return;
        }

        save(PLAYER_LIST.get(uuid));

        PLAYER_LIST.remove(uuid);
    }

    public static boolean existsEntry(org.bukkit.entity.Player player) {
        return existsEntry(UUIDManager.getUniqueId(player));
    }

    public static boolean existsEntry(UUID uuid) {
        return getConfigFile(uuid).exists();
    }

    public static void setUniqueId(Player entry, UUID newUuid) {
        if (entry.getUniqueId().equals(newUuid)) {
            Log.warning("Not setting new UUID: UUIDs match!");
            return;
        }

        // Add new entry
        final Player newEntry = new Player(
                newUuid,
                entry.getFirstLoginName(),
                entry.getLastLoginName(),
                entry.getFirstLoginUnix(),
                entry.getLastLoginUnix(),
                entry.getIps());
        newEntry.save();
        PLAYER_LIST.put(newUuid, newEntry);

        // Remove old entry
        PLAYER_LIST.remove(entry.getUniqueId());
        final File oldFile = getConfigFile(entry.getUniqueId());
        if (oldFile.exists() && !oldFile.delete()) {
            Log.warning("Could not delete config: " + getConfigFile(entry.getUniqueId()).getName());
        }
    }

    public static void purgeAll() {
        for (File file : getConfigFolder().listFiles()) {
            file.delete();
        }

        // Load online players
        load();
    }

    public static File getConfigFolder() {
        return new File(TotalFreedomMod.plugin.getDataFolder(), "players");
    }

    public static File getConfigFile(UUID uuid) {
        return new File(getConfigFolder(), uuid + ".yml");
    }

    public static Configuration getConfig(UUID uuid) {
        final Configuration config = new Configuration(TotalFreedomMod.plugin, getConfigFile(uuid), false);
        config.load();
        return config;
    }

    public static void save(Player entry) {
        if (!entry.isComplete()) {
            throw new IllegalArgumentException("Entry is not complete!");
        }

        final Configuration config = getConfig(entry.getUniqueId());
        config.set("firstjoinname", entry.getFirstLoginName());
        config.set("lastjoinname", entry.getLastLoginName());
        config.set("firstjoinunix", entry.getFirstLoginUnix());
        config.set("lastjoinunix", entry.getLastLoginUnix());
        config.set("ips", entry.getIps());
        config.save();
    }
}
