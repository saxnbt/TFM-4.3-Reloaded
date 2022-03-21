package me.StevenLawson.TotalFreedomMod.commands;

import me.StevenLawson.TotalFreedomMod.config.ConfigurationEntry;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@CommandPermissions(level = AdminLevel.OP, source = SourceType.ONLY_IN_GAME)
public class Command_landmine extends FreedomCommand {
    @Override
    public boolean run(CommandSender sender, org.bukkit.entity.Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole) {
        if (!ConfigurationEntry.LANDMINES_ENABLED.getBoolean()) {
            playerMsg(sender, "The landmine is currently disabled.", ChatColor.GREEN);
            return true;
        }

        if (!ConfigurationEntry.ALLOW_EXPLOSIONS.getBoolean()) {
            playerMsg(sender, "Explosions are currently disabled.", ChatColor.GREEN);
            return true;
        }

        double radius = 2.0;

        if (args.length >= 1)
        {
            if ("list".equalsIgnoreCase(args[0]))
            {
                final Iterator<TFM_LandmineData> landmines = TFM_LandmineData.landmines.iterator();
                while (landmines.hasNext())
                {
                    playerMsg(sender, landmines.next().toString());
                }
                return true;
            }

            try
            {
                radius = Math.max(2.0, Math.min(6.0, Double.parseDouble(args[0])));
            }
            catch (NumberFormatException ex)
            {
            }
        }

        final Block landmine = sender_p.getLocation().getBlock().getRelative(BlockFace.DOWN);
        landmine.setType(Material.TNT);
        TFM_LandmineData.landmines.add(new TFM_LandmineData(landmine.getLocation(), sender_p, radius));

        playerMsg(sender, "Landmine planted - Radius = " + radius + " blocks.", ChatColor.GREEN);

        return true;
    }

    public static class TFM_LandmineData
    {
        public static final List<TFM_LandmineData> landmines = new ArrayList<TFM_LandmineData>();
        public final Location location;
        public final Player player;
        public final double radius;

        public TFM_LandmineData(Location location, Player player, double radius)
        {
            this.location = location;
            this.player = player;
            this.radius = radius;
        }

        @Override
        public String toString()
        {
            return this.location.toString() + ", " + this.radius + ", " + this.player.getName();
        }
    }
}
