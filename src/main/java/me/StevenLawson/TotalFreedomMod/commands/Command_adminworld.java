package me.StevenLawson.TotalFreedomMod.commands;

import me.StevenLawson.TotalFreedomMod.admin.AdminList;
import me.StevenLawson.TotalFreedomMod.util.Utilities;
import me.StevenLawson.TotalFreedomMod.world.AdminWorld;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = AdminLevel.OP, source = SourceType.BOTH)
public class Command_adminworld extends FreedomCommand {
    private enum CommandMode {
        TELEPORT, GUEST, TIME, WEATHER
    }

    @Override
    public boolean run(CommandSender sender, org.bukkit.entity.Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole) {
        CommandMode commandMode = null;

        if (args.length == 0) {
            commandMode = CommandMode.TELEPORT;
        } else if (args.length >= 2) {
            if ("guest".equalsIgnoreCase(args[0]))
            {
                commandMode = CommandMode.GUEST;
            }
            else if ("time".equalsIgnoreCase(args[0]))
            {
                commandMode = CommandMode.TIME;
            }
            else if ("weather".equalsIgnoreCase(args[0]))
            {
                commandMode = CommandMode.WEATHER;
            }
        }

        if (commandMode == null)
        {
            return false;
        }

        try
        {
            switch (commandMode)
            {
                case TELEPORT:
                {
                    if (!(sender instanceof Player) || sender_p == null)
                    {
                        return true;
                    }

                    World adminWorld = null;
                    try
                    {
                        adminWorld = AdminWorld.getInstance().getWorld();
                    }
                    catch (Exception ex)
                    {
                    }

                    if (adminWorld == null || sender_p.getWorld() == adminWorld)
                    {
                        playerMsg(sender, "Going to the main world.");
                        sender_p.teleport(server.getWorlds().get(0).getSpawnLocation());
                    }
                    else
                    {
                        if (AdminWorld.getInstance().canAccessWorld(sender_p))
                        {
                            playerMsg(sender, "Going to the AdminWorld.");
                            AdminWorld.getInstance().sendToWorld(sender_p);
                        }
                        else
                        {
                            playerMsg(sender,  "You don't have permission to access the AdminWorld.");
                        }
                    }

                    break;
                }
                case GUEST:
                {
                    if (args.length == 2)
                    {
                        if ("list".equalsIgnoreCase(args[1]))
                        {
                            playerMsg(sender, "AdminWorld guest list: " + AdminWorld.getInstance().guestListToString());
                        }
                        else if ("purge".equalsIgnoreCase(args[1]))
                        {
                            assertCommandPerms(sender, sender_p);
                            AdminWorld.getInstance().purgeGuestList();
                            Utilities.adminAction(sender.getName(), "AdminWorld guest list purged.", false);
                        }
                        else
                        {
                            return false;
                        }
                    }
                    else if (args.length == 3)
                    {
                        assertCommandPerms(sender, sender_p);

                        if ("add".equalsIgnoreCase(args[1]))
                        {
                            final Player player = getPlayer(args[2]);

                            if (player == null)
                            {
                                sender.sendMessage(FreedomCommand.PLAYER_NOT_FOUND);
                                return true;
                            }

                            if (AdminWorld.getInstance().addGuest(player, sender_p))
                            {
                                Utilities.adminAction(sender.getName(), "AdminWorld guest added: " + player.getName(), false);
                            }
                            else
                            {
                                playerMsg(sender, "Could not add player to guest list.");
                            }
                        }
                        else if ("remove".equals(args[1]))
                        {
                            final Player player = AdminWorld.getInstance().removeGuest(args[2]);
                            if (player != null)
                            {
                                Utilities.adminAction(sender.getName(), "AdminWorld guest removed: " + player.getName(), false);
                            }
                            else
                            {
                                playerMsg(sender,  "Can't find guest entry for: " + args[2]);
                            }
                        }
                        else
                        {
                            return false;
                        }
                    }

                    break;
                }
                case TIME:
                {
                    assertCommandPerms(sender, sender_p);

                    if (args.length == 2)
                    {
                        AdminWorld.TimeOfDay timeOfDay = AdminWorld.TimeOfDay.getByAlias(args[1]);
                        if (timeOfDay != null)
                        {
                            AdminWorld.getInstance().setTimeOfDay(timeOfDay);
                            playerMsg(sender, "AdminWorld time set to: " + timeOfDay.name());
                        }
                        else
                        {
                            playerMsg(sender, "Invalid time of day. Can be: sunrise, noon, sunset, midnight");
                        }
                    }
                    else
                    {
                        return false;
                    }

                    break;
                }
                case WEATHER:
                {
                    assertCommandPerms(sender, sender_p);

                    if (args.length == 2)
                    {
                        AdminWorld.WeatherMode weatherMode = AdminWorld.WeatherMode.getByAlias(args[1]);
                        if (weatherMode != null)
                        {
                            AdminWorld.getInstance().setWeatherMode(weatherMode);
                            playerMsg(sender, "AdminWorld weather set to: " + weatherMode.name());
                        }
                        else
                        {
                            playerMsg(sender, "Invalid weather mode. Can be: off, rain, storm");
                        }
                    }
                    else
                    {
                        return false;
                    }

                    break;
                }
                default:
                {
                    return false;
                }
            }
        }
        catch (PermissionDeniedException ex)
        {
            sender.sendMessage(ex.getMessage());
        }

        return true;
    }

    private void assertCommandPerms(CommandSender sender, org.bukkit.entity.Player sender_p) throws PermissionDeniedException {
        if (!(sender instanceof Player) || sender_p == null || !AdminList.isSuperAdmin(sender)) {
            throw new PermissionDeniedException(FreedomCommand.MSG_NO_PERMS);
        }
    }

    private class PermissionDeniedException extends Exception
    {
        private static final long serialVersionUID = 1L;

        private PermissionDeniedException(String string)
        {
            super(string);
        }
    }
}
