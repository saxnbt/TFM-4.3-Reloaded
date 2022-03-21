package me.StevenLawson.TotalFreedomMod.commands;

import me.StevenLawson.TotalFreedomMod.Log;
import me.StevenLawson.TotalFreedomMod.TotalFreedomMod;
import me.StevenLawson.TotalFreedomMod.admin.AdminList;
import me.StevenLawson.TotalFreedomMod.player.PlayerData;
import me.StevenLawson.TotalFreedomMod.util.Utilities;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collection;

public abstract class FreedomCommand {
    public static final String MSG_NO_PERMS = ChatColor.YELLOW + "You do not have permission to use this command.";
    public static final String YOU_ARE_OP = ChatColor.YELLOW + "You are now op!";
    public static final String YOU_ARE_NOT_OP = ChatColor.YELLOW + "You are no longer op!";
    public static final String NOT_FROM_CONSOLE = "This command may not be used from the console.";
    public static final String PLAYER_NOT_FOUND = ChatColor.GRAY + "Player not found!";
    protected TotalFreedomMod plugin;
    protected Server server;
    private CommandSender commandSender;
    private Class<?> commandClass;

    public FreedomCommand() {
    }

    abstract public boolean run(final CommandSender sender, final org.bukkit.entity.Player sender_p, final Command cmd, final String commandLabel, final String[] args, final boolean senderIsConsole);

    public void setup(final TotalFreedomMod plugin, final CommandSender commandSender, final Class<?> commandClass)
    {
        this.plugin = plugin;
        this.server = plugin.getServer();
        this.commandSender = commandSender;
        this.commandClass = commandClass;
    }

    public void playerMsg(final CommandSender sender, final String message, final ChatColor color)
    {
        if (sender == null)
        {
            return;
        }
        sender.sendMessage(color + message);
    }

    public void playerMsg(final String message, final ChatColor color)
    {
        playerMsg(commandSender, message, color);
    }

    public void playerMsg(final CommandSender sender, final String message)
    {
        playerMsg(sender, message, ChatColor.GRAY);
    }

    public void playerMsg(final String message)
    {
        playerMsg(commandSender, message);
    }

    public boolean senderHasPermission()
    {
        final CommandPermissions permissions = commandClass.getAnnotation(CommandPermissions.class);

        if (permissions == null)
        {
            Log.warning(commandClass.getName() + " is missing permissions annotation.");
            return true;
        }

        boolean isSuper = AdminList.isSuperAdmin(commandSender);
        boolean isSenior = false;

        if (isSuper)
        {
            isSenior = AdminList.isSeniorAdmin(commandSender);
        }

        final AdminLevel level = permissions.level();
        final SourceType source = permissions.source();
        final boolean blockHostConsole = permissions.blockHostConsole();

        if (!(commandSender instanceof Player))
        {
            if (source == SourceType.ONLY_IN_GAME)
            {
                return false;
            }

            if (level == AdminLevel.SENIOR && !isSenior) {
                return false;
            }

            if (blockHostConsole && Utilities.isFromHostConsole(commandSender.getName()))
            {
                return false;
            }

            return true;
        }

        final Player senderPlayer = (Player) commandSender;

        if (source == SourceType.ONLY_CONSOLE)
        {
            return false;
        }

        if (level == AdminLevel.SENIOR) {
            if (!isSenior) {
                return false;
            }

            if (!PlayerData.getPlayerData(senderPlayer).isSuperadminIdVerified()) {
                return false;
            }

            return true;
        }

        if (level == AdminLevel.SUPER && !isSuper) {
            return false;
        }

        if (level == AdminLevel.OP && !senderPlayer.isOp()) {
            return false;
        }

        return true;
    }

    public Player getPlayer(final String partialName)
    {
        return getPlayer(partialName, false);
    }

    public Player getPlayer(final String partialName, final boolean exact)
    {
        if (partialName == null || partialName.isEmpty())
        {
            return null;
        }

        final Collection<? extends Player> players = server.getOnlinePlayers();

        // Check exact matches first.
        for (final Player player : players)
        {
            if (partialName.equalsIgnoreCase(player.getName()))
            {
                return player;
            }
        }

        if (exact)
        {
            return null;
        }

        // Then check partial matches in name.
        for (final Player player : players)
        {
            if (player.getName().toLowerCase().contains(partialName.toLowerCase()))
            {
                return player;
            }
        }

        // Then check partial matches in display name.
        for (final Player player : players)
        {
            if (player.getDisplayName().toLowerCase().contains(partialName.toLowerCase()))
            {
                return player;
            }
        }

        return null;
    }
}
