package me.StevenLawson.TotalFreedomMod.commands;

import me.StevenLawson.TotalFreedomMod.config.ConfigurationEntry;
import me.StevenLawson.TotalFreedomMod.util.Utilities;
import me.StevenLawson.TotalFreedomMod.world.ProtectedArea;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

@CommandPermissions(level = AdminLevel.SUPER, source = SourceType.ONLY_IN_GAME)
public class Command_setspawnworld extends FreedomCommand {
    @Override
    public boolean run(CommandSender sender, org.bukkit.entity.Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole) {
        Location pos = sender_p.getLocation();
        sender_p.getWorld().setSpawnLocation(pos.getBlockX(), pos.getBlockY(), pos.getBlockZ());

        playerMsg(sender, "Spawn location for this world set to: " + Utilities.formatLocation(sender_p.getWorld().getSpawnLocation()));

        if (ConfigurationEntry.PROTECTAREA_ENABLED.getBoolean() && ConfigurationEntry.PROTECTAREA_SPAWNPOINTS.getBoolean()) {
            ProtectedArea.addProtectedArea("spawn_" + sender_p.getWorld().getName(), pos, ConfigurationEntry.PROTECTAREA_RADIUS.getDouble());
        }

        return true;
    }
}
