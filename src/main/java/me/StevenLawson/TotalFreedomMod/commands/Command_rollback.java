package me.StevenLawson.TotalFreedomMod.commands;

import me.StevenLawson.TotalFreedomMod.util.Utilities;
import me.StevenLawson.TotalFreedomMod.world.RollbackManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

@CommandPermissions(level = AdminLevel.SUPER, source = SourceType.BOTH, blockHostConsole = true)
@CommandParameters(description = "Issues a rollback on a player", usage = "/<command> <[partialname] | undo [partialname] purge [partialname] | purgeall>", aliases = "rb")
public class Command_rollback extends FreedomCommand {
    @Override
    public boolean run(CommandSender sender, org.bukkit.entity.Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole) {
        if (args.length == 0 || args.length > 2) {
            return false;
        }

        if (args.length == 1) {
            if ("purgeall".equals(args[0])) {
                Utilities.adminAction(sender.getName(), "Purging all rollback history", false);
                playerMsg("Purged all rollback history for " + RollbackManager.purgeEntries() + " players.");
            }
            else
            {
                final String playerName = RollbackManager.findPlayer(args[0]);

                if (playerName == null)
                {
                    playerMsg("That player has no entries stored.");
                    return true;
                }

                if (RollbackManager.canUndoRollback(playerName))
                {
                    playerMsg("That player has just been rolled back.");
                }

                Utilities.adminAction(sender.getName(), "Rolling back player: " + playerName, false);
                playerMsg("Rolled back " + RollbackManager.rollback(playerName) + " edits for " + playerName + ".");
                playerMsg("If this rollback was a mistake, use /rollback undo " + playerName + " within 40 seconds to reverse the rollback.");
            }
            return true;
        }

        if (args.length == 2)
        {
            if ("purge".equalsIgnoreCase(args[0]))
            {
                final String playerName = RollbackManager.findPlayer(args[1]);

                if (playerName == null)
                {
                    playerMsg("That player has no entries stored.");
                    return true;
                }

                playerMsg("Purged " + RollbackManager.purgeEntries(playerName) + " rollback history entries for " + playerName + ".");
                return true;
            }

            if ("undo".equalsIgnoreCase(args[0]))
            {
                final String playerName = RollbackManager.findPlayer(args[1]);

                if (playerName == null)
                {
                    playerMsg("That player hasn't been rolled back recently.");
                    return true;
                }

                Utilities.adminAction(sender.getName(), "Reverting rollback for player: " + playerName, false);
                playerMsg("Reverted " + RollbackManager.undoRollback(playerName) + " edits for " + playerName + ".");
                return true;
            }
        }

        return false;
    }
}
