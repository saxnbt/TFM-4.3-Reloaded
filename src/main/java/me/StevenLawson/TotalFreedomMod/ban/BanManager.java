package me.StevenLawson.TotalFreedomMod.ban;

import me.StevenLawson.TotalFreedomMod.Log;
import me.StevenLawson.TotalFreedomMod.TotalFreedomMod;
import me.StevenLawson.TotalFreedomMod.ban.Ban.BanType;
import me.StevenLawson.TotalFreedomMod.config.Configuration;
import me.StevenLawson.TotalFreedomMod.config.ConfigurationEntry;
import me.StevenLawson.TotalFreedomMod.player.UUIDManager;
import me.StevenLawson.TotalFreedomMod.player.UUIDManager.TFM_UuidResolver;
import me.StevenLawson.TotalFreedomMod.util.Utilities;
import org.bukkit.entity.Player;

import java.util.*;

public class BanManager
{
    private static final List<Ban> ipBans;
    private static final List<Ban> uuidBans;
    private static final List<UUID> unbannableUUIDs;

    static
    {
        ipBans = new ArrayList<Ban>();
        uuidBans = new ArrayList<Ban>();
        unbannableUUIDs = new ArrayList<UUID>();
    }

    private BanManager()
    {
        throw new AssertionError();
    }

    public static void load()
    {
        ipBans.clear();
        uuidBans.clear();
        unbannableUUIDs.clear();

        final Configuration config = new Configuration(TotalFreedomMod.plugin, "bans.yml", true);
        config.load();

        for (String banString : config.getStringList("ips"))
        {
            try
            {
                addIpBan(new Ban(banString, BanType.IP));
            }
            catch (RuntimeException ex)
            {
                Log.warning("Could not load IP ban: " + banString);
            }
        }

        for (String banString : config.getStringList("uuids"))
        {
            try
            {
                addUuidBan(new Ban(banString, BanType.UUID));
            }
            catch (RuntimeException ex)
            {
                Log.warning("Could not load UUID ban: " + banString);
            }
        }

        // Save the config
        save();
        Log.info("Loaded " + ipBans.size() + " IP bans and " + uuidBans.size() + " UUID bans");

        @SuppressWarnings("unchecked")
        final TFM_UuidResolver resolver = new TFM_UuidResolver((List<String>) ConfigurationEntry.UNBANNABLE_USERNAMES.getList());

        for (UUID uuid : resolver.call().values())
        {
            unbannableUUIDs.add(uuid);
        }

        Log.info("Loaded " + unbannableUUIDs.size() + " unbannable UUIDs");
    }

    public static void save()
    {
        final Configuration config = new Configuration(TotalFreedomMod.plugin, "bans.yml", true);
        config.load();

        final List<String> newIpBans = new ArrayList<String>();
        final List<String> newUuidBans = new ArrayList<String>();

        for (Ban savedBan : ipBans)
        {
            if (!savedBan.isExpired())
            {
                newIpBans.add(savedBan.toString());
            }
        }

        for (Ban savedBan : uuidBans)
        {
            if (!savedBan.isExpired() && !unbannableUUIDs.contains(UUID.fromString(savedBan.getSubject())))
            {
                newUuidBans.add(savedBan.toString());
            }
        }

        config.set("ips", newIpBans);
        config.set("uuids", newUuidBans);

        // Save config
        config.save();
    }

    public static List<Ban> getIpBanList()
    {
        return Collections.unmodifiableList(ipBans);
    }

    public static List<Ban> getUuidBanList()
    {
        return Collections.unmodifiableList(uuidBans);
    }

    public static Ban getByIp(String ip)
    {
        for (Ban ban : ipBans)
        {
            if (ban.isExpired())
            {
                continue;
            }

            wildcardCheck:
            if (ban.getSubject().contains("*"))
            {
                final String[] subjectParts = ban.getSubject().split("\\.");
                final String[] ipParts = ip.split("\\.");

                for (int i = 0; i < 4; i++)
                {
                    if (!(subjectParts[i].equals("*") || subjectParts[i].equals(ipParts[i])))
                    {
                        break wildcardCheck;
                    }
                }

                return ban;
            }

            if (ban.getSubject().equals(ip))
            {
                return ban;
            }
        }
        return null;
    }

    public static Ban getByUuid(UUID uuid)
    {
        for (Ban ban : uuidBans)
        {
            if (ban.getSubject().equalsIgnoreCase(uuid.toString()))
            {
                if (ban.isExpired())
                {
                    continue;
                }

                return ban;
            }
        }
        return null;
    }

    public static void unbanIp(String ip)
    {
        final Ban ban = getByIp(ip);

        if (ban == null)
        {
            return;
        }

        removeBan(ban);
        save();
    }

    public static void unbanUuid(UUID uuid)
    {
        final Ban ban = getByUuid(uuid);

        if (ban == null)
        {
            return;
        }

        removeBan(ban);
    }

    public static boolean isIpBanned(String ip)
    {
        return getByIp(ip) != null;
    }

    public static boolean isUuidBanned(UUID uuid)
    {
        return getByUuid(uuid) != null;
    }

    public static void addUuidBan(Player player)
    {
        addUuidBan(new Ban(UUIDManager.getUniqueId(player), player.getName()));
    }

    public static void addUuidBan(Ban ban)
    {
        if (!ban.isComplete())
        {
            throw new RuntimeException("Could not add UUID ban, Invalid format!");
        }

        if (ban.isExpired())
        {
            return;
        }

        if (uuidBans.contains(ban))
        {
            return;
        }

        if (unbannableUUIDs.contains(UUID.fromString(ban.getSubject())))
        {
            return;
        }

        uuidBans.add(ban);
        save();
    }

    public static void addIpBan(Player player)
    {
        addIpBan(new Ban(Utilities.getIp(player), player.getName()));
    }

    public static void addIpBan(Ban ban)
    {
        if (!ban.isComplete())
        {
            throw new RuntimeException("Could not add IP ban, Invalid format!");
        }

        if (ban.isExpired())
        {
            return;
        }

        if (ipBans.contains(ban))
        {
            return;
        }

        ipBans.add(ban);
        save();
    }

    public static void removeBan(Ban ban)
    {
        final Iterator<Ban> ips = ipBans.iterator();
        while (ips.hasNext())
        {
            if (ips.next().getSubject().equalsIgnoreCase(ban.getSubject()))
            {
                ips.remove();
            }
        }

        final Iterator<Ban> uuids = uuidBans.iterator();
        while (uuids.hasNext())
        {
            if (uuids.next().getSubject().equalsIgnoreCase(ban.getSubject()))
            {
                uuids.remove();
            }
        }

        save();
    }

    public static void purgeIpBans()
    {
        ipBans.clear();
        save();
    }

    public static void purgeUuidBans()
    {
        uuidBans.clear();
        save();
    }
}
