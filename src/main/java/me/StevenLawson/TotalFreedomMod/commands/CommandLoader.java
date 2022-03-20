package me.StevenLawson.TotalFreedomMod.commands;

import me.StevenLawson.TotalFreedomMod.Log;
import me.StevenLawson.TotalFreedomMod.TotalFreedomMod;
import me.StevenLawson.TotalFreedomMod.util.Utilities;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.plugin.Plugin;

import java.io.IOException;
import java.security.CodeSource;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class CommandLoader {
    public static final Pattern COMMAND_PATTERN;
    private static final List<TFM_CommandInfo> COMMAND_LIST;

    static {
        COMMAND_PATTERN = Pattern.compile(CommandHandler.COMMAND_PATH.replace('.', '/') + "/(" + CommandHandler.COMMAND_PREFIX + "[^\\$]+)\\.class");
        COMMAND_LIST = new ArrayList<TFM_CommandInfo>();
    }

    private CommandLoader() {
        throw new AssertionError();
    }

    public static void scan()
    {
        CommandMap commandMap = getCommandMap();
        if (commandMap == null)
        {
            Log.severe("Error loading commandMap.");
            return;
        }
        COMMAND_LIST.clear();
        COMMAND_LIST.addAll(getCommands());

        for (TFM_CommandInfo commandInfo : COMMAND_LIST)
        {
            TFM_DynamicCommand dynamicCommand = new TFM_DynamicCommand(commandInfo);

            Command existing = commandMap.getCommand(dynamicCommand.getName());
            if (existing != null)
            {
                unregisterCommand(existing, commandMap);
            }

            commandMap.register(TotalFreedomMod.plugin.getDescription().getName(), dynamicCommand);
        }

        Log.info("TFM commands loaded.");
    }

    public static void unregisterCommand(String commandName)
    {
        CommandMap commandMap = getCommandMap();
        if (commandMap != null)
        {
            Command command = commandMap.getCommand(commandName.toLowerCase());
            if (command != null)
            {
                unregisterCommand(command, commandMap);
            }
        }
    }

    public static void unregisterCommand(Command command, CommandMap commandMap)
    {
        try
        {
            command.unregister(commandMap);
            HashMap<String, Command> knownCommands = getKnownCommands(commandMap);
            if (knownCommands != null)
            {
                knownCommands.remove(command.getName());
                for (String alias : command.getAliases())
                {
                    knownCommands.remove(alias);
                }
            }
        }
        catch (Exception ex)
        {
            Log.severe(ex);
        }
    }

    @SuppressWarnings("unchecked")
    public static CommandMap getCommandMap()
    {
        final Object commandMap = Utilities.getField(Bukkit.getServer().getPluginManager(), "commandMap");
        if (commandMap != null)
        {
            if (commandMap instanceof CommandMap)
            {
                return (CommandMap) commandMap;
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public static HashMap<String, Command> getKnownCommands(CommandMap commandMap)
    {
        Object knownCommands = Utilities.getField(commandMap, "knownCommands");
        if (knownCommands != null)
        {
            if (knownCommands instanceof HashMap)
            {
                return (HashMap<String, Command>) knownCommands;
            }
        }
        return null;
    }

    private static List<TFM_CommandInfo> getCommands()
    {
        List<TFM_CommandInfo> commandList = new ArrayList<TFM_CommandInfo>();

        try
        {
            CodeSource codeSource = TotalFreedomMod.class.getProtectionDomain().getCodeSource();
            if (codeSource != null)
            {
                ZipInputStream zip = new ZipInputStream(codeSource.getLocation().openStream());
                ZipEntry zipEntry;
                while ((zipEntry = zip.getNextEntry()) != null)
                {
                    String entryName = zipEntry.getName();
                    Matcher matcher = COMMAND_PATTERN.matcher(entryName);
                    if (matcher.find())
                    {
                        try
                        {
                            Class<?> commandClass = Class.forName(CommandHandler.COMMAND_PATH + "." + matcher.group(1));

                            CommandPermissions commandPermissions = commandClass.getAnnotation(CommandPermissions.class);
                            CommandParameters commandParameters = commandClass.getAnnotation(CommandParameters.class);

                            if (commandPermissions != null && commandParameters != null)
                            {
                                TFM_CommandInfo commandInfo = new TFM_CommandInfo(
                                        commandClass,
                                        matcher.group(1).split("_")[1],
                                        commandPermissions.level(),
                                        commandPermissions.source(),
                                        commandPermissions.blockHostConsole(),
                                        commandParameters.description(),
                                        commandParameters.usage(),
                                        commandParameters.aliases());

                                commandList.add(commandInfo);
                            }
                        }
                        catch (ClassNotFoundException ex)
                        {
                            Log.severe(ex);
                        }
                    }
                }
            }
        }
        catch (IOException ex)
        {
            Log.severe(ex);
        }

        return commandList;
    }

    public static class TFM_CommandInfo
    {
        private final String commandName;
        private final Class<?> commandClass;
        private final AdminLevel level;
        private final SourceType source;
        private final boolean blockHostConsole;
        private final String description;
        private final String usage;
        private final List<String> aliases;

        public TFM_CommandInfo(Class<?> commandClass, String commandName, AdminLevel level, SourceType source, boolean blockHostConsole, String description, String usage, String aliases) {
            this.commandName = commandName;
            this.commandClass = commandClass;
            this.level = level;
            this.source = source;
            this.blockHostConsole = blockHostConsole;
            this.description = description;
            this.usage = usage;
            this.aliases = ("".equals(aliases) ? new ArrayList<String>() : Arrays.asList(aliases.split(",")));
        }

        public List<String> getAliases()
        {
            return Collections.unmodifiableList(aliases);
        }

        public Class<?> getCommandClass()
        {
            return commandClass;
        }

        public String getCommandName()
        {
            return commandName;
        }

        public String getDescription()
        {
            return description;
        }

        public String getDescriptionPermissioned()
        {
            String _description = description;

            switch (this.getLevel())
            {
                case SENIOR:
                    _description = "Senior " + (this.getSource() == SourceType.ONLY_CONSOLE ? "Console" : "") + " Command - " + _description;
                    break;
                case SUPER:
                    _description = "Superadmin Command - " + _description;
                    break;
                case OP:
                    _description = "OP Command - " + _description;
                    break;
            }

            return _description;
        }

        public AdminLevel getLevel() {
            return level;
        }

        public SourceType getSource()
        {
            return source;
        }

        public String getUsage()
        {
            return usage;
        }

        public boolean getBlockHostConsole()
        {
            return blockHostConsole;
        }

        @Override
        public String toString()
        {
            StringBuilder sb = new StringBuilder();
            sb.append("commandName: ").append(commandName);
            sb.append("\ncommandClass: ").append(commandClass.getName());
            sb.append("\nlevel: ").append(level);
            sb.append("\nsource: ").append(source);
            sb.append("\nblock_host_console: ").append(blockHostConsole);
            sb.append("\ndescription: ").append(description);
            sb.append("\nusage: ").append(usage);
            sb.append("\naliases: ").append(aliases);
            return sb.toString();
        }
    }

    public static class TFM_DynamicCommand extends Command implements PluginIdentifiableCommand
    {
        private final TFM_CommandInfo commandInfo;

        private TFM_DynamicCommand(TFM_CommandInfo commandInfo)
        {
            super(commandInfo.getCommandName(), commandInfo.getDescriptionPermissioned(), commandInfo.getUsage(), commandInfo.getAliases());

            this.commandInfo = commandInfo;
        }

        @Override
        public boolean execute(CommandSender sender, String commandLabel, String[] args)
        {
            boolean success = false;

            if (!getPlugin().isEnabled())
            {
                return false;
            }

            try
            {
                success = getPlugin().onCommand(sender, this, commandLabel, args);
            }
            catch (Throwable ex)
            {
                throw new CommandException("Unhandled exception executing command '" + commandLabel + "' in plugin " + getPlugin().getDescription().getFullName(), ex);
            }

            if (!success && getUsage().length() > 0)
            {
                for (String line : getUsage().replace("<command>", commandLabel).split("\n"))
                {
                    sender.sendMessage(line);
                }
            }

            return success;
        }

        @Override
        public Plugin getPlugin()
        {
            return TotalFreedomMod.plugin;
        }

        public TFM_CommandInfo getCommandInfo()
        {
            return commandInfo;
        }
    }
}
