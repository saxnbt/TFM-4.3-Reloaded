package me.StevenLawson.TotalFreedomMod.listener;

import me.StevenLawson.TotalFreedomMod.Log;
import me.StevenLawson.TotalFreedomMod.TotalFreedomMod;
import me.StevenLawson.TotalFreedomMod.admin.AdminList;
import me.StevenLawson.TotalFreedomMod.config.ConfigurationEntry;
import me.StevenLawson.TotalFreedomMod.player.HeartBeat;
import me.StevenLawson.TotalFreedomMod.player.PlayerData;
import me.StevenLawson.TotalFreedomMod.util.Utilities;
import me.StevenLawson.TotalFreedomMod.world.ProtectedArea;
import me.StevenLawson.TotalFreedomMod.world.RollbackManager;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.inventory.ItemStack;

public class BlockListener implements Listener {
    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockBurn(BlockBurnEvent event) {
        if (!ConfigurationEntry.ALLOW_FIRE_SPREAD.getBoolean()) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockIgnite(BlockIgniteEvent event)
    {
        if (!ConfigurationEntry.ALLOW_FIRE_PLACE.getBoolean())
        {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onBlockBreak(BlockBreakEvent event)
    {
        final Player player = event.getPlayer();
        final Location location = event.getBlock().getLocation();

        if (ConfigurationEntry.NUKE_MONITOR_ENABLED.getBoolean())
        {
            final PlayerData playerdata = PlayerData.getPlayerData(player);

            final Location playerLocation = player.getLocation();

            final double nukeMonitorRange = ConfigurationEntry.NUKE_MONITOR_RANGE.getDouble().doubleValue();

            boolean outOfRange = false;
            if (!playerLocation.getWorld().equals(location.getWorld()))
            {
                outOfRange = true;
            }
            else if (playerLocation.distanceSquared(location) > (nukeMonitorRange * nukeMonitorRange))
            {
                outOfRange = true;
            }

            if (outOfRange)
            {
                if (playerdata.incrementAndGetFreecamDestroyCount() > ConfigurationEntry.FREECAM_TRIGGER_COUNT.getInteger())
                {
                    Utilities.bcastMsg(player.getName() + " has been flagged for possible freecam nuking.", ChatColor.RED);
                    Utilities.autoEject(player, "Freecam (extended range) block breaking is not permitted on this server.");

                    playerdata.resetFreecamDestroyCount();

                    event.setCancelled(true);
                    return;
                }
            }

            final Long lastRan = HeartBeat.getLastRan();
            if (lastRan == null || lastRan + TotalFreedomMod.HEARTBEAT_RATE * 1000L < System.currentTimeMillis())
            {
                // Log.warning("Heartbeat service timeout - can't check block place/break rates.");
            }
            else
            {
                if (playerdata.incrementAndGetBlockDestroyCount() > ConfigurationEntry.NUKE_MONITOR_COUNT_BREAK.getInteger())
                {
                    Utilities.bcastMsg(player.getName() + " is breaking blocks too fast!", ChatColor.RED);
                    Utilities.autoEject(player, "You are breaking blocks too fast. Nukers are not permitted on this server.");

                    playerdata.resetBlockDestroyCount();

                    event.setCancelled(true);
                    return;
                }
            }
        }

        if (ConfigurationEntry.PROTECTAREA_ENABLED.getBoolean())
        {
            if (!AdminList.isSuperAdmin(player))
            {
                if (ProtectedArea.isInProtectedArea(location))
                {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockPlace(BlockPlaceEvent event)
    {
        Player player = event.getPlayer();
        Location blockLocation = event.getBlock().getLocation();

        if (ConfigurationEntry.NUKE_MONITOR_ENABLED.getBoolean())
        {
            PlayerData playerdata = PlayerData.getPlayerData(player);

            Location playerLocation = player.getLocation();

            double nukeMonitorRange = ConfigurationEntry.NUKE_MONITOR_RANGE.getDouble().doubleValue();

            boolean outOfRange = false;
            if (!playerLocation.getWorld().equals(blockLocation.getWorld()))
            {
                outOfRange = true;
            }
            else if (playerLocation.distanceSquared(blockLocation) > (nukeMonitorRange * nukeMonitorRange))
            {
                outOfRange = true;
            }

            if (outOfRange)
            {
                if (playerdata.incrementAndGetFreecamPlaceCount() > ConfigurationEntry.FREECAM_TRIGGER_COUNT.getInteger())
                {
                    Utilities.bcastMsg(player.getName() + " has been flagged for possible freecam building.", ChatColor.RED);
                    Utilities.autoEject(player, "Freecam (extended range) block building is not permitted on this server.");

                    playerdata.resetFreecamPlaceCount();

                    event.setCancelled(true);
                    return;
                }
            }

            Long lastRan = HeartBeat.getLastRan();
            if (lastRan == null || lastRan + TotalFreedomMod.HEARTBEAT_RATE * 1000L < System.currentTimeMillis())
            {
                //Log.warning("Heartbeat service timeout - can't check block place/break rates.");
            }
            else
            {
                if (playerdata.incrementAndGetBlockPlaceCount() > ConfigurationEntry.NUKE_MONITOR_COUNT_PLACE.getInteger())
                {
                    Utilities.bcastMsg(player.getName() + " is placing blocks too fast!", ChatColor.RED);
                    Utilities.autoEject(player, "You are placing blocks too fast.");

                    playerdata.resetBlockPlaceCount();

                    event.setCancelled(true);
                    return;
                }
            }
        }

        if (ConfigurationEntry.PROTECTAREA_ENABLED.getBoolean())
        {
            if (!AdminList.isSuperAdmin(player))
            {
                if (ProtectedArea.isInProtectedArea(blockLocation))
                {
                    event.setCancelled(true);
                    return;
                }
            }
        }

        switch (event.getBlockPlaced().getType())
        {
            case LAVA:
            case STATIONARY_LAVA:
            {
                if (ConfigurationEntry.ALLOW_LAVA_PLACE.getBoolean())
                {
                    Log.info(String.format("%s placed lava @ %s", player.getName(), Utilities.formatLocation(event.getBlock().getLocation())));

                    player.getInventory().clear(player.getInventory().getHeldItemSlot());
                }
                else
                {
                    player.getInventory().setItem(player.getInventory().getHeldItemSlot(), new ItemStack(Material.COOKIE, 1));
                    player.sendMessage(ChatColor.GRAY + "Lava placement is currently disabled.");

                    event.setCancelled(true);
                }
                break;
            }
            case WATER:
            case STATIONARY_WATER:
            {
                if (ConfigurationEntry.ALLOW_WATER_PLACE.getBoolean())
                {
                    Log.info(String.format("%s placed water @ %s", player.getName(), Utilities.formatLocation(event.getBlock().getLocation())));

                    player.getInventory().clear(player.getInventory().getHeldItemSlot());
                }
                else
                {
                    player.getInventory().setItem(player.getInventory().getHeldItemSlot(), new ItemStack(Material.COOKIE, 1));
                    player.sendMessage(ChatColor.GRAY + "Water placement is currently disabled.");

                    event.setCancelled(true);
                }
                break;
            }
            case FIRE:
            {
                if (ConfigurationEntry.ALLOW_FIRE_PLACE.getBoolean())
                {
                    Log.info(String.format("%s placed fire @ %s", player.getName(), Utilities.formatLocation(event.getBlock().getLocation())));

                    player.getInventory().clear(player.getInventory().getHeldItemSlot());
                }
                else
                {
                    player.getInventory().setItem(player.getInventory().getHeldItemSlot(), new ItemStack(Material.COOKIE, 1));
                    player.sendMessage(ChatColor.GRAY + "Fire placement is currently disabled.");

                    event.setCancelled(true);
                }
                break;
            }
            case TNT:
            {
                if (ConfigurationEntry.ALLOW_EXPLOSIONS.getBoolean())
                {
                    Log.info(String.format("%s placed TNT @ %s", player.getName(), Utilities.formatLocation(event.getBlock().getLocation())));

                    player.getInventory().clear(player.getInventory().getHeldItemSlot());
                }
                else
                {
                    player.getInventory().setItem(player.getInventory().getHeldItemSlot(), new ItemStack(Material.COOKIE, 1));

                    player.sendMessage(ChatColor.GRAY + "TNT is currently disabled.");
                    event.setCancelled(true);
                }
                break;
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onRollbackBlockBreak(BlockBreakEvent event)
    {
        if (!AdminList.isSuperAdmin(event.getPlayer()))
        {
            RollbackManager.blockBreak(event);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onRollbackBlockPlace(BlockPlaceEvent event)
    {
        if (!AdminList.isSuperAdmin(event.getPlayer()))
        {
            RollbackManager.blockPlace(event);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockFromTo(BlockFromToEvent event)
    {
        if (!ConfigurationEntry.ALLOW_FLUID_SPREAD.getBoolean())
        {
            event.setCancelled(true);
        }
    }
}
