package me.StevenLawson.TotalFreedomMod.commands;

import me.StevenLawson.TotalFreedomMod.Log;
import me.StevenLawson.TotalFreedomMod.command.CommandBlocker;
import me.StevenLawson.TotalFreedomMod.util.DeprecationUtil;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@CommandPermissions(level = AdminLevel.ALL, source = SourceType.BOTH)
@CommandParameters(description = "No Description Yet", usage = "/<command>")
public class Command_cbtool extends FreedomCommand {
    @Override
    public boolean run(CommandSender sender, org.bukkit.entity.Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole) {
        if (args.length < 1) {
            return false;
        }

        if ("targetblock".equalsIgnoreCase(args[0]) && sender instanceof Player) {
            Block targetBlock = DeprecationUtil.getTargetBlock(sender_p, null, 100);
            playerMsg("Your target block: " + targetBlock.getLocation().toString());
            return true;
        }

        try
        {
            final StringBuffer generatedCommand = new StringBuffer();

            final Matcher matcher = Pattern.compile("\\[(.+?)\\]").matcher(StringUtils.join(args, " ").trim());
            while (matcher.find())
            {
                matcher.appendReplacement(generatedCommand, processSubCommand(matcher.group(1)));
            }
            matcher.appendTail(generatedCommand);

            if (CommandBlocker.isCommandBlocked(generatedCommand.toString(), sender, false))
            {
                return true;
            }

            server.dispatchCommand(sender, generatedCommand.toString());
        }
        catch (SubCommandFailureException ex)
        {
        }
        catch (Exception ex)
        {
            Log.severe(ex);
        }

        return true;
    }

    private String processSubCommand(final String subcommand) throws SubCommandFailureException
    {
        final String[] args = StringUtils.split(subcommand, " ");

        if (args.length == 1)
        {
            throw new SubCommandFailureException("Invalid subcommand name.");
        }

        return SubCommand.getByName(args[0]).getExecutable().execute(ArrayUtils.remove(args, 0));
    }

    private enum SubCommand
    {
        PLAYER_DETECT("playerdetect", new SubCommandExecutable()
        {
            @Override
            public String execute(String[] args) throws SubCommandFailureException
            {
                if (args.length != 5)
                {
                    throw new SubCommandFailureException("Invalid # of arguments.");
                }

                double x, y, z;
                try
                {
                    x = Double.parseDouble(args[0].trim());
                    y = Double.parseDouble(args[1].trim());
                    z = Double.parseDouble(args[2].trim());
                }
                catch (NumberFormatException ex)
                {
                    throw new SubCommandFailureException("Invalid coordinates.");
                }

                World world = null;
                final String needleWorldName = args[3].trim();
                final List<World> worlds = Bukkit.getWorlds();
                for (final World testWorld : worlds)
                {
                    if (testWorld.getName().trim().equalsIgnoreCase(needleWorldName))
                    {
                        world = testWorld;
                        break;
                    }
                }

                if (world == null)
                {
                    throw new SubCommandFailureException("Invalid world name.");
                }

                final Location testLocation = new Location(world, x, y, z);

                double radius;
                try
                {
                    radius = Double.parseDouble(args[4].trim());
                }
                catch (NumberFormatException ex)
                {
                    throw new SubCommandFailureException("Invalid radius.");
                }

                final double radiusSq = radius * radius;

                final List<Player> worldPlayers = testLocation.getWorld().getPlayers();
                for (final Player testPlayer : worldPlayers)
                {
                    if (testPlayer.getLocation().distanceSquared(testLocation) < radiusSq)
                    {
                        return testPlayer.getName();
                    }
                }

                throw new SubCommandFailureException("No player found in range.");
            }
        }),
        PLAYER_DETECT_BOOLEAN("playerdetectboolean", new SubCommandExecutable()
        {
            @Override
            public String execute(String[] args) throws SubCommandFailureException
            {
                try
                {
                    PLAYER_DETECT.getExecutable().execute(args);
                }
                catch (SubCommandFailureException ex)
                {
                    return "0";
                }

                return "1";
            }
        });
        //
        private final String name;
        private final SubCommandExecutable executable;

        SubCommand(String subCommandName, SubCommandExecutable subCommandImpl)
        {
            this.name = subCommandName;
            this.executable = subCommandImpl;
        }

        public SubCommandExecutable getExecutable()
        {
            return executable;
        }

        public String getName()
        {
            return name;
        }

        public static SubCommand getByName(String needle) throws SubCommandFailureException
        {
            needle = needle.trim();
            for (SubCommand subCommand : values())
            {
                if (subCommand.getName().equalsIgnoreCase(needle))
                {
                    return subCommand;
                }
            }
            throw new SubCommandFailureException("Invalid subcommand name.");
        }
    }

    private interface SubCommandExecutable
    {
        String execute(String[] args) throws SubCommandFailureException;
    }

    private static class SubCommandFailureException extends Exception
    {
        public SubCommandFailureException()
        {
        }

        public SubCommandFailureException(String message)
        {
            super(message);
        }
    }
}
