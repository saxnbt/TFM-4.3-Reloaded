package me.StevenLawson.TotalFreedomMod.commands;

import me.StevenLawson.TotalFreedomMod.admin.Admin;
import me.StevenLawson.TotalFreedomMod.admin.AdminList;
import me.StevenLawson.TotalFreedomMod.player.Player;
import me.StevenLawson.TotalFreedomMod.player.PlayerList;
import me.StevenLawson.TotalFreedomMod.player.UUIDManager;
import me.StevenLawson.TotalFreedomMod.player.UUIDManager.TFM_UuidResolver;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.*;

@CommandPermissions(level = AdminLevel.SENIOR, source = SourceType.ONLY_CONSOLE)
@CommandParameters(description = "Provides uuid tools", usage = "/<command> <purge | recalculate>")
public class Command_uuid extends FreedomCommand {
    @Override
    public boolean run(CommandSender sender, org.bukkit.entity.Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole) {
        if (args.length != 1) {
            return false;
        }

        if ("purge".equals(args[0])) {
            playerMsg("Purged " + UUIDManager.purge() + " cached UUIDs.");
            return true;
        }

        if ("recalculate".equals(args[0]))
        {
            playerMsg("Recalculating UUIDs...");

            // Playerlist uuids
            final Set<Player> players = PlayerList.getAllPlayers();
            final List<String> names = new ArrayList<String>();

            for (Player player : players)
            {
                names.add(player.getLastLoginName());
            }

            final Map<String, UUID> playerUuids = new TFM_UuidResolver(names).call();

            int updated = 0;
            for (String name : playerUuids.keySet())
            {
                for (Player player : players)
                {
                    if (!player.getLastLoginName().equalsIgnoreCase(name))
                    {
                        continue;
                    }

                    if (player.getUniqueId().equals(playerUuids.get(name)))
                    {
                        continue;
                    }

                    PlayerList.setUniqueId(player, playerUuids.get(name));
                    UUIDManager.rawSetUUID(name, playerUuids.get(name));
                    updated++;
                    break;
                }
            }

            playerMsg("Recalculated " + updated + " player UUIDs");
            names.clear();

            // Adminlist UUIDs
            final Set<Admin> admins = AdminList.getAllAdmins();
            for (Admin admin : admins)
            {
                names.add(admin.getLastLoginName());
            }

            final Map<String, UUID> adminUuids = new TFM_UuidResolver(names).call();

            updated = 0;
            for (String name : adminUuids.keySet())
            {
                for (Admin admin : admins)
                {
                    if (!admin.getLastLoginName().equalsIgnoreCase(name))
                    {
                        continue;
                    }

                    if (admin.getUniqueId().equals(adminUuids.get(name)))
                    {
                        continue;
                    }

                    AdminList.setUuid(admin, admin.getUniqueId(), adminUuids.get(name));
                    UUIDManager.rawSetUUID(name, adminUuids.get(name));
                    updated++;
                    break;
                }
            }

            playerMsg("Recalculated " + updated + " admin UUIDs");

            return true;
        }

        return false;
    }
}
