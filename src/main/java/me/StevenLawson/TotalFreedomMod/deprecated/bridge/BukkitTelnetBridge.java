package me.StevenLawson.TotalFreedomMod.deprecated.bridge;

import me.StevenLawson.BukkitTelnet.api.TelnetCommandEvent;
import me.StevenLawson.BukkitTelnet.api.TelnetPreLoginEvent;
import me.StevenLawson.BukkitTelnet.api.TelnetRequestDataTagsEvent;
import me.StevenLawson.TotalFreedomMod.admin.Admin;
import me.StevenLawson.TotalFreedomMod.admin.AdminList;
import me.StevenLawson.TotalFreedomMod.bridge.EssentialsBridge;
import me.StevenLawson.TotalFreedomMod.command.CommandBlocker;
import me.StevenLawson.TotalFreedomMod.player.PlayerData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.Iterator;
import java.util.Map;

@Deprecated
public class BukkitTelnetBridge implements Listener {
    @EventHandler(priority = EventPriority.NORMAL)
    public void onTelnetPreLogin(TelnetPreLoginEvent event) {

        final String ip = event.getIp();
        if (ip == null || ip.isEmpty()) {
            return;
        }

        final Admin admin = AdminList.getEntryByIp(ip, true);

        if (admin == null || !admin.isActivated() || !admin.isTelnetAdmin())
        {
            return;
        }

        event.setBypassPassword(true);
        event.setName(admin.getLastLoginName());
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onTelnetCommand(TelnetCommandEvent event)
    {
        if (CommandBlocker.isCommandBlocked(event.getCommand(), event.getSender()))
        {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onTelnetRequestDataTags(TelnetRequestDataTagsEvent event)
    {
        final Iterator<Map.Entry<Player, Map<String, Object>>> it = event.getDataTags().entrySet().iterator();
        while (it.hasNext())
        {
            final Map.Entry<Player, Map<String, Object>> entry = it.next();
            final Player player = entry.getKey();
            final Map<String, Object> playerTags = entry.getValue();

            boolean isAdmin = false;
            boolean isTelnetAdmin = false;
            boolean isSeniorAdmin = false;

            final Admin admin = AdminList.getEntry(player);
            if (admin != null)
            {
                boolean isActivated = admin.isActivated();

                isAdmin = isActivated;
                isTelnetAdmin = isActivated && admin.isTelnetAdmin();
                isSeniorAdmin = isActivated && admin.isSeniorAdmin();
            }

            playerTags.put("tfm.admin.isAdmin", isAdmin);
            playerTags.put("tfm.admin.isTelnetAdmin", isTelnetAdmin);
            playerTags.put("tfm.admin.isSeniorAdmin", isSeniorAdmin);

            playerTags.put("tfm.playerdata.getTag", PlayerData.getPlayerData(player).getTag());

            playerTags.put("tfm.essentialsBridge.getNickname", EssentialsBridge.getNickname(player.getName()));
        }
    }
}
