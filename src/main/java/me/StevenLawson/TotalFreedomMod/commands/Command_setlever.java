package me.StevenLawson.TotalFreedomMod.commands;

import me.StevenLawson.TotalFreedomMod.util.DeprecationUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.List;

@CommandPermissions(level = AdminLevel.ALL, source = SourceType.BOTH)
@CommandParameters(description = "Set the on/off state of the lever at position x, y, z in world 'worldname'.", usage = "/<command> <x> <y> <z> <worldname> <on|off>")
public class Command_setlever extends FreedomCommand {
    @Override
    public boolean run(CommandSender sender, org.bukkit.entity.Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole) {
        if (args.length != 5) {
            return false;
        }

        double x, y, z;
        try {
            x = Double.parseDouble(args[0]);
            y = Double.parseDouble(args[1]);
            z = Double.parseDouble(args[2]);
        }
        catch (NumberFormatException ex)
        {
            playerMsg("Invalid coordinates.");
            return true;
        }

        World world = null;
        final String needleWorldName = args[3].trim();
        final List<World> worlds = server.getWorlds();
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
            playerMsg("Invalid world name.");
            return true;
        }

        final Location leverLocation = new Location(world, x, y, z);

        final boolean leverOn = (args[4].trim().equalsIgnoreCase("on") || args[4].trim().equalsIgnoreCase("1"));

        final Block targetBlock = leverLocation.getBlock();

        if (targetBlock.getType() == Material.LEVER)
        {
            org.bukkit.material.Lever lever = DeprecationUtil.makeLeverWithData(DeprecationUtil.getData_Block(targetBlock));
            lever.setPowered(leverOn);
            DeprecationUtil.setData_Block(targetBlock, DeprecationUtil.getData_MaterialData(lever));
            targetBlock.getState().update();
        }
        else
        {
            playerMsg("Target block " + targetBlock + "  is not a lever.");
            return true;
        }

        return true;
    }
}
