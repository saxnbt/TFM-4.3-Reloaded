package me.StevenLawson.TotalFreedomMod.listener;

import me.StevenLawson.TotalFreedomMod.config.ConfigurationEntry;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.vehicle.*;
import org.bukkit.util.Vector;

public class EntityListener implements Listener {
    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityExplode(EntityExplodeEvent event) {
        if (!ConfigurationEntry.ALLOW_EXPLOSIONS.getBoolean()) {
            event.setCancelled(true);
            return;
        }

        event.setYield(0.0F);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onExplosionPrime(ExplosionPrimeEvent event)
    {
        if (!ConfigurationEntry.ALLOW_EXPLOSIONS.getBoolean())
        {
            event.setCancelled(true);
            return;
        }

        event.setRadius((float) ConfigurationEntry.EXPLOSIVE_RADIUS.getDouble().doubleValue());
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityCombust(EntityCombustEvent event)
    {
        if (!ConfigurationEntry.ALLOW_EXPLOSIONS.getBoolean())
        {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityDamage(EntityDamageEvent event)
    {
        switch (event.getCause())
        {
            case LAVA:
            {
                if (!ConfigurationEntry.ALLOW_LAVA_DAMAGE.getBoolean())
                {
                    event.setCancelled(true);
                    return;
                }
            }
        }

        if (ConfigurationEntry.ENABLE_PET_PROTECT.getBoolean())
        {
            Entity entity = event.getEntity();
            if (entity instanceof Tameable)
            {
                if (((Tameable) entity).isTamed())
                {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onCreatureSpawn(CreatureSpawnEvent event)
    {
        if (ConfigurationEntry.MOB_LIMITER_ENABLED.getBoolean())
        {
            if (event.getSpawnReason().equals(CreatureSpawnEvent.SpawnReason.EGG))
            {
                event.setCancelled(true);
                return;
            }

            Entity spawned = event.getEntity();

            if (spawned instanceof EnderDragon)
            {
                if (ConfigurationEntry.MOB_LIMITER_DISABLE_DRAGON.getBoolean())
                {
                    event.setCancelled(true);
                    return;
                }
            }
            else if (spawned instanceof Ghast)
            {
                if (ConfigurationEntry.MOB_LIMITER_DISABLE_GHAST.getBoolean())
                {
                    event.setCancelled(true);
                    return;
                }
            }
            else if (spawned instanceof Slime)
            {
                if (ConfigurationEntry.MOB_LIMITER_DISABLE_SLIME.getBoolean())
                {
                    event.setCancelled(true);
                    return;
                }
            }
            else if (spawned instanceof Giant)
            {
                if (ConfigurationEntry.MOB_LIMITER_DISABLE_GIANT.getBoolean())
                {
                    event.setCancelled(true);
                    return;
                }
            }
            else if (spawned instanceof Bat)
            {
                event.setCancelled(true);
                return;
            }

            int mobLimiterMax = ConfigurationEntry.MOB_LIMITER_MAX.getInteger().intValue();

            if (mobLimiterMax > 0)
            {
                int mobcount = 0;

                for (Entity entity : event.getLocation().getWorld().getLivingEntities())
                {
                    if (!(entity instanceof HumanEntity))
                    {
                        mobcount++;
                    }
                }

                if (mobcount > mobLimiterMax)
                {
                    event.setCancelled(true);
                }
            }
        }
    }

    //FIXME Make patch better

    @EventHandler(priority = EventPriority.HIGH)
    public void onVehicleCollisionEntity(VehicleEntityCollisionEvent event) {
        if(event.getEntity() instanceof Vehicle) {
            event.setCollisionCancelled(true);
            event.setPickupCancelled(true);
            event.setCancelled(true);
            event.getVehicle().remove();
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityDeath(EntityDeathEvent event)
    {
        if (ConfigurationEntry.AUTO_ENTITY_WIPE.getBoolean())
        {
            event.setDroppedExp(0);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onProjectileHit(ProjectileHitEvent event)
    {
        if (ConfigurationEntry.ALLOW_EXPLOSIONS.getBoolean())
        {
            Projectile entity = event.getEntity();
            if (event.getEntityType() == EntityType.ARROW)
            {
                entity.getWorld().createExplosion(entity.getLocation(), 2F);
            }
        }
    }
}
