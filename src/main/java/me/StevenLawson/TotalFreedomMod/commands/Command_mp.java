package me.StevenLawson.TotalFreedomMod.commands;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;

@CommandPermissions(level = AdminLevel.SUPER, source = SourceType.BOTH)
public class Command_mp extends FreedomCommand {
    @Override
    public boolean run(CommandSender sender, org.bukkit.entity.Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole) {
        playerMsg(sender, "Purging all mobs...");
        playerMsg(sender, purgeMobs() + " mobs removed.");
        return true;
    }

    public static int purgeMobs()
    {
        int removed = 0;
        for (World world : Bukkit.getWorlds())
        {
            for (Entity ent : world.getLivingEntities())
            {
                if (ent instanceof Creature || ent instanceof Ghast || ent instanceof Slime || ent instanceof EnderDragon || ent instanceof Ambient)
                {
                    ent.remove();
                    removed++;
                }
            }
        }

        return removed;
    }
}
