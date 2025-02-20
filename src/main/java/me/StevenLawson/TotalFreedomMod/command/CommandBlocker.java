package me.StevenLawson.TotalFreedomMod.command;

import me.StevenLawson.TotalFreedomMod.Log;
import me.StevenLawson.TotalFreedomMod.admin.AdminList;
import me.StevenLawson.TotalFreedomMod.commands.CommandLoader;
import me.StevenLawson.TotalFreedomMod.config.ConfigurationEntry;
import me.StevenLawson.TotalFreedomMod.util.Utilities;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandBlocker
{
    private static final Map<String, CommandBlockerEntry> BLOCKED_COMMANDS;

    static
    {
        BLOCKED_COMMANDS = new HashMap<String, CommandBlockerEntry>();
    }

    private CommandBlocker()
    {
        throw new AssertionError();
    }

    public static void load()
    {
        BLOCKED_COMMANDS.clear();

        final CommandMap commandMap = CommandLoader.getCommandMap();
        if (commandMap == null)
        {
            Log.severe("Error loading commandMap.");
            return;
        }

        @SuppressWarnings("unchecked")
        List<String> blockedCommands = (List<String>) ConfigurationEntry.BLOCKED_COMMANDS.getList();
        for (String rawEntry : blockedCommands)
        {
            final String[] parts = rawEntry.split(":");
            if (parts.length < 3 || parts.length > 4)
            {
                Log.warning("Invalid command blocker entry: " + rawEntry);
                continue;
            }

            final CommandBlockerRank rank = CommandBlockerRank.fromToken(parts[0]);
            final CommandBlockerAction action = CommandBlockerAction.fromToken(parts[1]);
            String commandName = parts[2].toLowerCase().substring(1);
            final String message = (parts.length > 3 ? parts[3] : null);

            if (rank == null || action == null || commandName == null || commandName.isEmpty())
            {
                Log.warning("Invalid command blocker entry: " + rawEntry);
                continue;
            }

            final String[] commandParts = commandName.split(" ");
            String subCommand = null;
            if (commandParts.length > 1)
            {
                commandName = commandParts[0];
                subCommand = StringUtils.join(commandParts, " ", 1, commandParts.length).trim().toLowerCase();
            }

            final Command command = commandMap.getCommand(commandName);

            // Obtain command from alias
            if (command == null)
            {
                Log.info("Blocking unknown command: /" + commandName);
            }
            else
            {
                commandName = command.getName().toLowerCase();
            }

            if (BLOCKED_COMMANDS.containsKey(commandName))
            {
                Log.warning("Not blocking: /" + commandName + " - Duplicate entry exists!");
                continue;
            }

            final CommandBlockerEntry blockedCommandEntry = new CommandBlockerEntry(rank, action, commandName, subCommand, message);
            BLOCKED_COMMANDS.put(blockedCommandEntry.getCommand(), blockedCommandEntry);

            if (command != null)
            {
                for (String alias : command.getAliases())
                {
                    BLOCKED_COMMANDS.put(alias.toLowerCase(), blockedCommandEntry);
                }
            }
        }

        Log.info("Loaded " + BLOCKED_COMMANDS.size() + " blocked commands");
    }

    public static boolean isCommandBlocked(String command, CommandSender sender)
    {
        return isCommandBlocked(command, sender, false);
    }

    public static boolean isCommandBlocked(String command, CommandSender sender, boolean doAction)
    {
        if (command == null || command.isEmpty())
        {
            return false;
        }

        command = command.toLowerCase().trim();

        if (command.split(" ")[0].contains(":"))
        {
            Utilities.playerMsg(sender, "Plugin-specific commands are disabled.");
            return true;
        }

        if (command.startsWith("/"))
        {
            command = command.substring(1);
        }

        final String[] commandParts = command.split(" ");
        String subCommand = null;
        if (commandParts.length > 1)
        {
            subCommand = StringUtils.join(commandParts, " ", 1, commandParts.length).toLowerCase();
        }

        final CommandBlockerEntry entry = BLOCKED_COMMANDS.get(commandParts[0]);

        if (entry == null)
        {
            return false;
        }

        if (entry.getSubCommand() != null)
        {
            if (subCommand == null || !subCommand.startsWith(entry.getSubCommand()))
            {
                return false;
            }
        }

        if (entry.getRank().hasPermission(sender))
        {
            return false;
        }

        if (doAction)
        {
            entry.doActions(sender);
        }

        return true;
    }

    public enum CommandBlockerRank
    {
        ANYONE("a", 0),
        OP("o", 1),
        SUPER("s", 2),
        TELNET("t", 3),
        SENIOR("c", 4),
        NOBODY("n", 5);
        //
        private final String token;
        private final int level;

        CommandBlockerRank(String token, int level)
        {
            this.token = token;
            this.level = level;
        }

        public String getToken()
        {
            return this.token;
        }

        public boolean hasPermission(CommandSender sender)
        {
            return fromSender(sender).level >= this.level;
        }

        public static CommandBlockerRank fromSender(CommandSender sender)
        {
            if (!(sender instanceof Player))
            {
                return TELNET;
            }

            if (AdminList.isSuperAdmin(sender))
            {
                if (AdminList.isSeniorAdmin(sender))
                {
                    return SENIOR;
                }
                return SUPER;
            }

            if (sender.isOp())
            {
                return OP;
            }

            return ANYONE;

        }

        public static CommandBlockerRank fromToken(String token)
        {
            for (CommandBlockerRank rank : CommandBlockerRank.values())
            {
                if (rank.getToken().equalsIgnoreCase(token))
                {
                    return rank;
                }
            }
            return ANYONE;
        }
    }

    public enum CommandBlockerAction
    {
        BLOCK("b"),
        BLOCK_AND_EJECT("a"),
        BLOCK_UNKNOWN("u");
        private final String token;

        CommandBlockerAction(String token)
        {
            this.token = token;
        }

        public String getToken()
        {
            return this.token;
        }

        public static CommandBlockerAction fromToken(String token)
        {
            for (CommandBlockerAction action : CommandBlockerAction.values())
            {
                if (action.getToken().equalsIgnoreCase(token))
                {
                    return action;
                }
            }
            return null;
        }
    }

    public static class CommandBlockerEntry
    {
        private final CommandBlockerRank rank;
        private final CommandBlockerAction action;
        private final String command;
        private final String subCommand;
        private final String message;

        private CommandBlockerEntry(CommandBlockerRank rank, CommandBlockerAction action, String command, String message)
        {
            this(rank, action, command, null, message);
        }

        private CommandBlockerEntry(CommandBlockerRank rank, CommandBlockerAction action, String command, String subCommand, String message)
        {
            this.rank = rank;
            this.action = action;
            this.command = command;
            this.subCommand = (subCommand == null ? null : subCommand.toLowerCase().trim());
            this.message = (message == null || message.equals("_") ? "That command is blocked." : message);
        }

        public CommandBlockerAction getAction()
        {
            return this.action;
        }

        public String getCommand()
        {
            return this.command;
        }

        public String getSubCommand()
        {
            return this.subCommand;
        }

        public String getMessage()
        {
            return this.message;
        }

        public CommandBlockerRank getRank()
        {
            return this.rank;
        }

        private void doActions(CommandSender sender)
        {
            if (action == CommandBlockerAction.BLOCK_AND_EJECT && sender instanceof Player)
            {
                Utilities.autoEject((Player) sender, "You used a prohibited command: " + command);
                Utilities.bcastMsg(sender.getName() + " was automatically kicked for using harmful commands.", ChatColor.RED);
                return;
            }

            if (action == CommandBlockerAction.BLOCK_UNKNOWN)
            {
                Utilities.playerMsg(sender, "Unknown command. Type \"help\" for help.", ChatColor.RESET);
                return;
            }

            Utilities.playerMsg(sender, Utilities.colorize(message));
        }
    }
}
