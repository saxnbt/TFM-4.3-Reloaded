package me.StevenLawson.TotalFreedomMod.commands;

import me.StevenLawson.TotalFreedomMod.admin.Admin;
import me.StevenLawson.TotalFreedomMod.admin.AdminList;
import me.StevenLawson.TotalFreedomMod.config.ConfigurationEntry;
import me.StevenLawson.TotalFreedomMod.deprecated.twitter.TwitterHandler;
import me.StevenLawson.TotalFreedomMod.player.PlayerData;
import me.StevenLawson.TotalFreedomMod.util.DeprecationUtil;
import me.StevenLawson.TotalFreedomMod.util.Utilities;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = AdminLevel.OP, source = SourceType.BOTH)
@CommandParameters(description = "Manage superadmins.", usage = "/<command> <list | clean | clearme [ip] | <add | remove | info> <username>>")
public class Command_saconfig extends FreedomCommand {
    @Override
    public boolean run(CommandSender sender, org.bukkit.entity.Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole) {
        final SAConfigMode mode;
        try {
            mode = SAConfigMode.findMode(args, sender, senderIsConsole);
        } catch (final PermissionsException ex) {
            playerMsg(ex.getMessage());
            return true;
        }
        catch (final FormatException ex)
        {
            playerMsg(ex.getMessage());
            return false;
        }

        switch (mode)
        {
            case LIST:
            {
                playerMsg("Superadmins: " + StringUtils.join(AdminList.getSuperNames(), ", "), ChatColor.GOLD);

                break;
            }
            case CLEAN:
            {
                Utilities.adminAction(sender.getName(), "Cleaning superadmin list", true);
                AdminList.cleanSuperadminList(true);
                playerMsg("Superadmins: " + StringUtils.join(AdminList.getSuperNames(), ", "), ChatColor.YELLOW);

                break;
            }
            case CLEARME:
            {
                final Admin admin = AdminList.getEntry(sender_p);

                if (admin == null)
                {
                    playerMsg("Could not find your admin entry! Please notify a developer.", ChatColor.RED);
                    return true;
                }

                final String ip = Utilities.getIp(sender_p);

                if (args.length == 1)
                {
                    Utilities.adminAction(sender.getName(), "Cleaning my supered IPs", true);

                    int counter = admin.getIps().size() - 1;
                    admin.clearIPs();
                    admin.addIp(ip);

                    AdminList.saveAll();

                    playerMsg(counter + " IPs removed.");
                    playerMsg(admin.getIps().get(0) + " is now your only IP address");
                }
                else
                {
                    if (!admin.getIps().contains(args[1]))
                    {
                        playerMsg("That IP is not registered to you.");
                    }
                    else if (ip.equals(args[1]))
                    {
                        playerMsg("You cannot remove your current IP.");
                    }
                    else
                    {
                        Utilities.adminAction(sender.getName(), "Removing a supered IP", true);

                        admin.removeIp(args[1]);

                        AdminList.saveAll();

                        playerMsg("Removed IP " + args[1]);
                        playerMsg("Current IPs: " + StringUtils.join(admin.getIps(), ", "));
                    }
                }

                break;
            }
            case INFO:
            {
                Admin superadmin = AdminList.getEntry(args[1].toLowerCase());

                if (superadmin == null)
                {
                    final Player player = getPlayer(args[1]);
                    if (player != null)
                    {
                        superadmin = AdminList.getEntry(player.getName().toLowerCase());
                    }
                }

                if (superadmin == null)
                {
                    playerMsg("Superadmin not found: " + args[1]);
                }
                else
                {
                    playerMsg(superadmin.toString());
                }

                break;
            }
            case ADD:
            {
                OfflinePlayer player = getPlayer(args[1], true); // Exact case-insensitive match.

                if (player == null)
                {
                    final Admin superadmin = AdminList.getEntry(args[1]);

                    if (superadmin == null)
                    {
                        playerMsg(FreedomCommand.PLAYER_NOT_FOUND);
                        return true;
                    }

                    player = DeprecationUtil.getOfflinePlayer(server, superadmin.getLastLoginName());
                }

                Utilities.adminAction(sender.getName(), "Adding " + player.getName() + " to the superadmin list", true);
                AdminList.addSuperadmin(player);

                if (player.isOnline())
                {
                    final PlayerData playerdata = PlayerData.getPlayerData(player.getPlayer());

                    if (playerdata.isFrozen())
                    {
                        playerdata.setFrozen(false);
                        playerMsg(player.getPlayer(), "You have been unfrozen.");
                    }
                }

                break;
            }
            case REMOVE:
            {
                String targetName = args[1];

                final Player player = getPlayer(targetName, true); // Exact case-insensitive match.

                if (player != null)
                {
                    targetName = player.getName();
                }

                if (!AdminList.getLowercaseSuperNames().contains(targetName.toLowerCase()))
                {
                    playerMsg("Superadmin not found: " + targetName);
                    return true;
                }

                Utilities.adminAction(sender.getName(), "Removing " + targetName + " from the superadmin list", true);
                AdminList.removeSuperadmin(DeprecationUtil.getOfflinePlayer(server, targetName));

                // Twitterbot
                if (ConfigurationEntry.TWITTERBOT_ENABLED.getBoolean())
                {
                    TwitterHandler.delTwitterVerbose(targetName, sender);
                }

                break;
            }
        }

        return true;
    }

    private enum SAConfigMode {
        LIST("list", AdminLevel.OP, SourceType.BOTH, 1, 1),
        CLEAN("clean", AdminLevel.SENIOR, SourceType.BOTH, 1, 1),
        CLEARME("clearme", AdminLevel.SUPER, SourceType.ONLY_IN_GAME, 1, 2),
        INFO("info", AdminLevel.SUPER, SourceType.BOTH, 2, 2),
        ADD("add", AdminLevel.SUPER, SourceType.ONLY_CONSOLE, 2, 2),
        REMOVE("remove", AdminLevel.SUPER, SourceType.ONLY_CONSOLE, 2, 2);
        private final String modeName;
        private final AdminLevel adminLevel;
        private final SourceType sourceType;
        private final int minArgs;
        private final int maxArgs;

        SAConfigMode(String modeName, AdminLevel adminLevel, SourceType sourceType, int minArgs, int maxArgs) {
            this.modeName = modeName;
            this.adminLevel = adminLevel;
            this.sourceType = sourceType;
            this.minArgs = minArgs;
            this.maxArgs = maxArgs;
        }

        private static SAConfigMode findMode(final String[] args, final CommandSender sender, final boolean senderIsConsole) throws PermissionsException, FormatException
        {
            if (args.length == 0)
            {
                throw new FormatException("Invalid number of arguments.");
            }

            boolean isSuperAdmin = AdminList.isSuperAdmin(sender);
            boolean isSeniorAdmin = isSuperAdmin && AdminList.isSeniorAdmin(sender, false);

            for (final SAConfigMode mode : values())
            {
                if (mode.modeName.equalsIgnoreCase(args[0]))
                {
                    if (mode.adminLevel == AdminLevel.SUPER) {
                        if (!isSuperAdmin) {
                            throw new PermissionsException(FreedomCommand.MSG_NO_PERMS);
                        }
                    } else if (mode.adminLevel == AdminLevel.SENIOR) {
                        if (!isSeniorAdmin) {
                            throw new PermissionsException(FreedomCommand.MSG_NO_PERMS);
                        }
                    }

                    if (mode.sourceType == SourceType.ONLY_IN_GAME)
                    {
                        if (senderIsConsole)
                        {
                            throw new PermissionsException("This command may only be used in-game.");
                        }
                    }
                    else if (mode.sourceType == SourceType.ONLY_CONSOLE)
                    {
                        if (!senderIsConsole)
                        {
                            throw new PermissionsException("This command may only be used from the console.");
                        }
                    }

                    if (args.length >= mode.minArgs && args.length <= mode.maxArgs)
                    {
                        return mode;
                    }
                    else
                    {
                        throw new FormatException("Invalid number of arguments for mode: " + mode.modeName);
                    }
                }
            }

            throw new FormatException("Invalid mode.");
        }
    }

    private static class PermissionsException extends Exception
    {
        public PermissionsException(final String message)
        {
            super(message);
        }
    }

    private static class FormatException extends Exception
    {
        public FormatException(final String message)
        {
            super(message);
        }
    }
}
