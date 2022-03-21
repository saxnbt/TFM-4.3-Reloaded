package me.StevenLawson.TotalFreedomMod.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

@CommandPermissions(level = AdminLevel.ALL, source = SourceType.ONLY_IN_GAME)
public class Command_localspawn extends FreedomCommand {
    @Override
    public boolean run(CommandSender sender, org.bukkit.entity.Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole) {
        sender_p.teleport(sender_p.getWorld().getSpawnLocation());
        playerMsg(sender, "Teleported to spawnpoint for world \"" + sender_p.getWorld().getName() + "\".");
        return true;
    }
}
