package me.StevenLawson.TotalFreedomMod.commands;

import me.StevenLawson.TotalFreedomMod.Log;
import me.StevenLawson.TotalFreedomMod.TotalFreedomMod;
import me.StevenLawson.TotalFreedomMod.admin.Admin;
import me.StevenLawson.TotalFreedomMod.config.ConfigurationEntry;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.Map.Entry;

@CommandPermissions(level = AdminLevel.SUPER, source = SourceType.ONLY_IN_GAME)
@CommandParameters(description = "Register your connection with the TFM logviewer.", usage = "/<command> [off]")
public class Command_logs extends FreedomCommand {
    public static void deactivateSuperadmin(Admin superadmin) {
        for (String ip : superadmin.getIps()) {
            updateLogsRegistration(null, superadmin.getLastLoginName(), ip, LogsRegistrationMode.DELETE);
        }
    }

    public static void updateLogsRegistration(final CommandSender sender, final Player target, final LogsRegistrationMode mode)
    {
        updateLogsRegistration(sender, target.getName(), target.getAddress().getAddress().getHostAddress().trim(), mode);
    }

    public static void updateLogsRegistration(final CommandSender sender, final String targetName, final String targetIP, final LogsRegistrationMode mode)
    {
        final String logsRegisterURL = ConfigurationEntry.LOGS_URL.getString();
        final String logsRegisterPassword = ConfigurationEntry.LOGS_SECRET.getString();

        if (logsRegisterURL == null || logsRegisterPassword == null || logsRegisterURL.isEmpty() || logsRegisterPassword.isEmpty())
        {
            return;
        }

        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                try
                {
                    if (sender != null)
                    {
                        sender.sendMessage(ChatColor.YELLOW + "Connecting...");
                    }

                    URL url = new URLBuilder(logsRegisterURL)
                            .addQueryParameter("mode", mode.toString())
                            .addQueryParameter("password", logsRegisterPassword)
                            .addQueryParameter("name", targetName)
                            .addQueryParameter("ip", targetIP)
                            .getURL();

                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setConnectTimeout(1000 * 5);
                    connection.setReadTimeout(1000 * 5);
                    connection.setUseCaches(false);
                    connection.setRequestMethod("HEAD");

                    final int responseCode = connection.getResponseCode();

                    if (sender != null)
                    {
                        new BukkitRunnable()
                        {
                            @Override
                            public void run()
                            {
                                if (responseCode == 200)
                                {
                                    sender.sendMessage(ChatColor.GREEN + "Registration " + mode + "d.");
                                }
                                else
                                {
                                    sender.sendMessage(ChatColor.RED + "Error contacting logs registration server.");
                                }
                            }
                        }.runTask(TotalFreedomMod.plugin);
                    }
                } catch (Exception ex) {
                    Log.severe(ex);
                }
            }
        }.runTaskAsynchronously(TotalFreedomMod.plugin);
    }

    @Override
    public boolean run(final CommandSender sender, final org.bukkit.entity.Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole) {
        LogsRegistrationMode mode = LogsRegistrationMode.UPDATE;

        if (args.length == 1) {
            mode = ("off".equals(args[0]) ? LogsRegistrationMode.DELETE : LogsRegistrationMode.UPDATE);
        }

        updateLogsRegistration(sender, sender_p, mode);

        return true;
    }

    public enum LogsRegistrationMode
    {
        UPDATE("update"), DELETE("delete");
        private final String mode;

        LogsRegistrationMode(String mode)
        {
            this.mode = mode;
        }

        @Override
        public String toString()
        {
            return mode;
        }
    }

    private static class URLBuilder
    {
        private final String requestPath;
        private final Map<String, String> queryStringMap = new HashMap<String, String>();

        public URLBuilder(String requestPath)
        {
            this.requestPath = requestPath;
        }

        public URLBuilder addQueryParameter(String key, String value)
        {
            queryStringMap.put(key, value);
            return this;
        }

        public URL getURL() throws MalformedURLException
        {
            List<String> pairs = new ArrayList<String>();
            Iterator<Entry<String, String>> it = queryStringMap.entrySet().iterator();
            while (it.hasNext())
            {
                Entry<String, String> pair = it.next();
                pairs.add(pair.getKey() + "=" + pair.getValue());
            }

            return new URL(requestPath + "?" + StringUtils.join(pairs, "&"));
        }
    }
}
