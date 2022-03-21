package me.StevenLawson.TotalFreedomMod.commands;

import me.StevenLawson.TotalFreedomMod.Log;
import me.StevenLawson.TotalFreedomMod.TotalFreedomMod;
import me.StevenLawson.TotalFreedomMod.admin.AdminList;
import me.StevenLawson.TotalFreedomMod.player.PlayerData;
import me.StevenLawson.TotalFreedomMod.util.Utilities;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collection;

public abstract class FreedomCommand implements CommandExecutor {
    public static final String MSG_NO_PERMS = ChatColor.YELLOW + "You do not have permission to use this command.";
    public static final String YOU_ARE_OP = ChatColor.YELLOW + "You are now op!";
    public static final String YOU_ARE_NOT_OP = ChatColor.YELLOW + "You are no longer op!";
    public static final String NOT_FROM_CONSOLE = "This command may not be used from the console.";
    public static final String PLAYER_NOT_FOUND = ChatColor.GRAY + "Player not found!";
    protected TotalFreedomMod plugin = TotalFreedomMod.plugin;
    protected Server server = Bukkit.getServer();

    public FreedomCommand() {
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!senderHasPermission(sender)) {
            sender.sendMessage(FreedomCommand.MSG_NO_PERMS);
            return true;
        }

        Player sender_p = null;
        if(sender instanceof Player) {
            sender_p = (Player)sender;
        }

        return run(sender, sender_p, command, label, args, sender_p == null);
    }

    abstract public boolean run(final CommandSender sender, final org.bukkit.entity.Player sender_p, final Command cmd, final String commandLabel, final String[] args, final boolean senderIsConsole);

    public void playerMsg(final CommandSender sender, final String message, final ChatColor color)
    {
        if (sender == null)
        {
            return;
        }
        sender.sendMessage(color + message);
    }

    public void playerMsg(final CommandSender sender, final String message)
    {
        playerMsg(sender, message, ChatColor.GRAY);
    }

    public void playerMsg(final String message, final ChatColor color) {
        // NOP
    }

    public void playerMsg(final String message) {
        // NOP
    }


    public boolean senderHasPermission(CommandSender commandSender)
    {
        final CommandPermissions permissions = this.getClass().getAnnotation(CommandPermissions.class);

        if (permissions == null)
        {
            Log.warning(this.getClass().getName() + " is missing permissions annotation.");
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

            return !blockHostConsole || !Utilities.isFromHostConsole(commandSender.getName());
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

            return PlayerData.getPlayerData(senderPlayer).isSuperadminIdVerified();
        }

        if (level == AdminLevel.SUPER && !isSuper) {
            return false;
        }

        return level != AdminLevel.OP || senderPlayer.isOp();
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
