package me.StevenLawson.TotalFreedomMod.commands;

import me.StevenLawson.TotalFreedomMod.config.ConfigurationEntry;
import me.StevenLawson.TotalFreedomMod.player.PlayerData;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

@CommandPermissions(level = AdminLevel.OP, source = SourceType.ONLY_IN_GAME)
@CommandParameters(description = "Modern weaponry, FTW. Use 'draw' to start firing, 'sling' to stop firing.", usage = "/<command> <draw | sling>")
public class Command_mp44 extends FreedomCommand {
    @Override
    public boolean run(CommandSender sender, org.bukkit.entity.Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole) {
        if (!ConfigurationEntry.MP44_ENABLED.getBoolean()) {
            playerMsg("The mp44 is currently disabled.", ChatColor.GREEN);
            return true;
        }

        if (args.length == 0) {
            return false;
        }

        PlayerData playerdata = PlayerData.getPlayerData(sender_p);

        if (args[0].equalsIgnoreCase("draw"))
        {
            playerdata.armMP44();

            playerMsg("mp44 is ARMED! Left click with gunpowder to start firing, left click again to quit.", ChatColor.GREEN);
            playerMsg("Type /mp44 sling to disable.  -by Madgeek1450", ChatColor.GREEN);

            sender_p.setItemInHand(new ItemStack(Material.SULPHUR, 1));
        }
        else
        {
            playerdata.disarmMP44();

            sender.sendMessage(ChatColor.GREEN + "mp44 Disarmed.");
        }

        return true;
    }
}
