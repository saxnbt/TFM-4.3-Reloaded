package me.StevenLawson.TotalFreedomMod.commands;

import me.StevenLawson.TotalFreedomMod.Log;
import me.StevenLawson.TotalFreedomMod.util.Utilities;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;

@CommandPermissions(level = AdminLevel.SENIOR, source = SourceType.ONLY_CONSOLE, blockHostConsole = true)
@CommandParameters(description = "Update server files.", usage = "/<command>")
public class Command_tfupdate extends FreedomCommand {
    public static final String[] FILES =
            {
            };

    @Override
    public boolean run(CommandSender sender, org.bukkit.entity.Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole) {
        if (FILES.length == 0) {
            playerMsg("This command is disabled.");
            return true;
        }

        if (!sender.getName().equalsIgnoreCase("madgeek1450")) {
            playerMsg(FreedomCommand.MSG_NO_PERMS);
            return true;
        }

        for (final String url : FILES)
        {
            new BukkitRunnable()
            {
                @Override
                public void run()
                {
                    try
                    {
                        Log.info("Downloading: " + url);

                        File file = new File("./updates/" + url.substring(url.lastIndexOf("/") + 1));
                        if (file.exists())
                        {
                            file.delete();
                        }
                        if (!file.getParentFile().exists())
                        {
                            file.getParentFile().mkdirs();
                        }

                        Utilities.downloadFile(url, file, true);
                    }
                    catch (Exception ex)
                    {
                        Log.severe(ex);
                    }
                }
            }.runTaskAsynchronously(plugin);
        }

        return true;
    }
}
