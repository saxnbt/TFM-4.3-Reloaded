package me.StevenLawson.TotalFreedomMod.listener;

import me.StevenLawson.TotalFreedomMod.admin.AdminList;
import me.StevenLawson.TotalFreedomMod.util.Utilities;
import me.StevenLawson.TotalFreedomMod.world.ProtectedArea;
import me.StevenLawson.worldedit.LimitChangedEvent;
import me.StevenLawson.worldedit.SelectionChangedEvent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class WorldEditListener implements Listener {

    @EventHandler
    public void onSelectionChange(final SelectionChangedEvent event) {
        final Player player = event.getPlayer();

        if (AdminList.isSuperAdmin(player)) {
            return;
        }

        if (ProtectedArea.isInProtectedArea(
                event.getMinVector(),
                event.getMaxVector(),
                event.getWorld().getName()))
        {

            player.sendMessage(ChatColor.RED + "The region that you selected contained a protected area. Selection cleared.");
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onLimitChanged(LimitChangedEvent event)
    {
        final Player player = event.getPlayer();

        if (AdminList.isSuperAdmin(player))
        {
            return;
        }

        if (!event.getPlayer().equals(event.getTarget()))
        {
            player.sendMessage(ChatColor.RED + "Only admins can change the limit for other players!");
            event.setCancelled(true);
        }

        if (event.getLimit() < 0 || event.getLimit() > 10000)
        {
            player.setOp(false);
            Utilities.bcastMsg(event.getPlayer().getName() + " tried to set their WorldEdit limit to " + event.getLimit() + " and has been de-opped", ChatColor.RED);
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "You cannot set your limit higher than 10000 or to -1!");
        }
    }

}
