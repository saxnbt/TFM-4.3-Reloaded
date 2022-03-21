package me.StevenLawson.TotalFreedomMod.commands;

import me.StevenLawson.TotalFreedomMod.ban.BanManager;
import me.StevenLawson.TotalFreedomMod.player.PlayerData;
import me.StevenLawson.TotalFreedomMod.player.UUIDManager;
import me.StevenLawson.TotalFreedomMod.util.Utilities;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Iterator;

@CommandPermissions(level = AdminLevel.SUPER, source = SourceType.BOTH)
public class Command_gadmin extends FreedomCommand {
    private enum GadminMode {
        LIST("list"),
        KICK("kick"),
        NAMEBAN("nameban"),
        IPBAN("ipban"),
        BAN("ban"),
        OP("op"),
        DEOP("deop"),
        CI("ci"),
        FR("fr"),
        SMITE("smite");
        private final String modeName;

        private GadminMode(String command)
        {
            this.modeName = command;
        }

        public String getModeName()
        {
            return modeName;
        }

        public static GadminMode findMode(String needle)
        {
            for (final GadminMode mode : GadminMode.values())
            {
                if (needle.equalsIgnoreCase(mode.getModeName()))
                {
                    return mode;
                }
            }
            return null;
        }
    }

    @Override
    public boolean run(CommandSender sender, org.bukkit.entity.Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole) {
        if (args.length == 0) {
            return false;
        }

        final GadminMode mode = GadminMode.findMode(args[0].toLowerCase());
        if (mode == null) {
            playerMsg(sender, "Invalid mode: " + args[0], ChatColor.RED);
            return true;
        }

        final Iterator<? extends Player> it = server.getOnlinePlayers().iterator();

        if (mode == GadminMode.LIST)
        {
            playerMsg(sender, "[ Real Name ] : [ Display Name ] - Hash:");
            while (it.hasNext())
            {
                final Player player = it.next();
                final String hash = UUIDManager.getUniqueId(player).toString().substring(0, 4);
                sender.sendMessage(ChatColor.GRAY + String.format("[ %s ] : [ %s ] - %s",
                        player.getName(),
                        ChatColor.stripColor(player.getDisplayName()),
                        hash));
            }
            return true;
        }

        if (args.length < 2)
        {
            return false;
        }

        Player target = null;
        while (it.hasNext() && target == null)
        {
            final Player player = it.next();
            final String hash = UUIDManager.getUniqueId(player).toString().substring(0, 4);

            if (hash.equalsIgnoreCase(args[1]))
            {
                target = player;
            }
        }

        if (target == null)
        {
            playerMsg(sender, "Invalid player hash: " + args[1], ChatColor.RED);
            return true;
        }

        switch (mode)
        {
            case KICK:
            {
                Utilities.adminAction(sender.getName(), String.format("Kicking: %s.", target.getName()), false);
                target.kickPlayer("Kicked by Administrator");

                break;
            }
            case NAMEBAN:
            {
                BanManager.addUuidBan(target);

                Utilities.adminAction(sender.getName(), String.format("Banning Name: %s.", target.getName()), true);
                target.kickPlayer("Username banned by Administrator.");

                break;
            }
            case IPBAN:
            {
                String ip = target.getAddress().getAddress().getHostAddress();
                String[] ip_parts = ip.split("\\.");
                if (ip_parts.length == 4)
                {
                    ip = String.format("%s.%s.*.*", ip_parts[0], ip_parts[1]);
                }
                Utilities.adminAction(sender.getName(), String.format("Banning IP: %s.", ip), true);
                BanManager.addIpBan(target);

                target.kickPlayer("IP address banned by Administrator.");

                break;
            }
            case BAN:
            {
                String ip = target.getAddress().getAddress().getHostAddress();
                String[] ip_parts = ip.split("\\.");
                if (ip_parts.length == 4)
                {
                    ip = String.format("%s.%s.*.*", ip_parts[0], ip_parts[1]);
                }
                Utilities.adminAction(sender.getName(), String.format("Banning Name: %s, IP: %s.", target.getName(), ip), true);

                BanManager.addUuidBan(target);
                BanManager.addIpBan(target);

                target.kickPlayer("IP and username banned by Administrator.");

                break;
            }
            case OP:
            {
                Utilities.adminAction(sender.getName(), String.format("Opping %s.", target.getName()), false);
                target.setOp(false);
                target.sendMessage(FreedomCommand.YOU_ARE_OP);

                break;
            }
            case DEOP:
            {
                Utilities.adminAction(sender.getName(), String.format("Deopping %s.", target.getName()), false);
                target.setOp(false);
                target.sendMessage(FreedomCommand.YOU_ARE_NOT_OP);

                break;
            }
            case CI:
            {
                target.getInventory().clear();

                break;
            }
            case FR:
            {
                PlayerData playerdata = PlayerData.getPlayerData(target);
                playerdata.setFrozen(!playerdata.isFrozen());

                playerMsg(sender, target.getName() + " has been " + (playerdata.isFrozen() ? "frozen" : "unfrozen") + ".");
                target.sendMessage(ChatColor.AQUA + "You have been " + (playerdata.isFrozen() ? "frozen" : "unfrozen") + ".");

                break;
            }
            case SMITE:
            {
                Command_smite.smite(target);

                break;
            }
        }

        return true;
    }
}
