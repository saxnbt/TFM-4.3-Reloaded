package me.StevenLawson.TotalFreedomMod.bridge;

import com.earth2me.essentials.User;
import me.StevenLawson.TotalFreedomMod.Log;
import me.StevenLawson.TotalFreedomMod.config.ConfigurationEntry;
import me.StevenLawson.TotalFreedomMod.config.MainConfig;
import me.StevenLawson.TotalFreedomMod.player.PlayerList;
import me.StevenLawson.TotalFreedomMod.player.PlayerRank;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.MessageAuthor;
import org.javacord.api.entity.message.embed.EmbedBuilder;

import java.util.*;
import java.util.regex.Pattern;

public class DiscordBridge {
    private static DiscordApi DISCORD_API;
    private static TextChannel CHANNEL;

    public static void load() {

        if (Boolean.FALSE.equals(MainConfig.getBoolean(ConfigurationEntry.DISCORD_IS_ENABLED))) {
            return;
        }

        try {
            DISCORD_API = new DiscordApiBuilder()
                    .setToken(MainConfig.getString(ConfigurationEntry.DISCORD_TOKEN))
                    .login()
                    .join();

            Optional<TextChannel> channelFuture = DISCORD_API.getTextChannelById(MainConfig.getString(ConfigurationEntry.DISCORD_CHANNEL));

            if (!channelFuture.isPresent()) {
                Log.warning("TFM 4.3 Reloaded could not find your channel, stopping!");

                return;
            }

            CHANNEL = channelFuture.get();

            CHANNEL.addMessageCreateListener((message) -> {
                String content = message.getMessageContent();
                MessageAuthor author = message.getMessage().getAuthor();
                if (author.isBotUser() || content.isEmpty()) return;

                if (content.equalsIgnoreCase(String.format("%sl", MainConfig.getString(ConfigurationEntry.DISCORD_PREFIX)))) {
                    EmbedBuilder builder = new EmbedBuilder()
                            .setTitle(String.format("Player List - %s", MainConfig.getString(ConfigurationEntry.SERVER_NAME)))
                            .setDescription(String.format("There are %s / %s online players", Bukkit.getOnlinePlayers().size(), Bukkit.getMaxPlayers()));

                    List<PlayerRank> inGameRanks = new ArrayList<>();

                    for (Player player : Bukkit.getOnlinePlayers()) {
                        User essentialsUser = EssentialsBridge.getEssentialsUser(player.getDisplayName());

                        if(essentialsUser != null) {
                            if(essentialsUser.isVanished()) continue;
                        }

                        PlayerRank rank = PlayerRank.fromSender(player);

                        if(!inGameRanks.contains(rank)) inGameRanks.add(rank);
                    }

                    Collections.sort(inGameRanks);
                    Collections.reverse(inGameRanks);

                    for (PlayerRank inGameRank : inGameRanks) {
                        List<String> inGame = inGameRank.getInGameUsernames();

                        if(inGame.size() > 0) {
                            builder.addField(String.format("%s (%s)", inGameRank.getPlural(), inGame.size()), String.join(", ", inGame));
                        }
                    }

                    CHANNEL.sendMessage(builder);
                } else {
                    String format = MainConfig.getString(ConfigurationEntry.DISCORD_FORMAT);
                    format = format.replace("{TAG}", author.getDiscriminatedName());
                    format = format.replace("{USERNAME}", author.getName());

                    Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', String.format(format, content)));
                }
            });
        } catch (Exception e) {
            Log.warning("Uh oh! It looks like TFM 4.3 Reloaded Discord couldn't start! Please check you have defined the bot's token & channel and also given it the correct permissions! (Read Messages and Send Messages)");
            Log.warning("If you've already set that up however, you may to read the exception below.");
            Log.warning("If this is a bug with TFM 4.3 Reloaded, please report it at https://github.com/TheDeus-Group/TFM-4.3-Reloaded/issues or https://code.cat.casa/TheDeus-Group/TFM-4.3-Reloaded/issues");
            e.printStackTrace();
            return;
        }

        Log.info("TFM 4.3 Reloaded Discord started.");

        transmitMessage("**Server has started**");
    }

    public static String sanitizeMessage(String message) {
        Pattern colors = Pattern.compile("§.", Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);
        Pattern pings = Pattern.compile("@", Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);

        if (message.length() > 2000) {
            message = message.substring(0, 2000);
        }

        return message.replaceAll(colors.pattern(), "").replaceAll(pings.pattern(), "@\u200B").replaceAll("([`_~*])", "\\\\$1");
    }

    public static void transmitMessage(String message) {
        transmitMessage(message, false);
    }

    public static void transmitMessage(String message, boolean disconnectAfterwards) {
        if (CHANNEL == null) return;
        if (!disconnectAfterwards) {
            CHANNEL.sendMessage(sanitizeMessage(message));
        } else {
            try {
                CHANNEL.sendMessage(sanitizeMessage(message)).get();
            } catch (Exception ignored) {
            }
            DISCORD_API.disconnect();
        }
    }

    public static void stop() {

        if (Boolean.FALSE.equals(MainConfig.getBoolean(ConfigurationEntry.DISCORD_IS_ENABLED))) {
            return;
        }

        transmitMessage("**Server has stopped**", true);

        Log.info("TFM 4.3 Reloaded Discord stopped.");
    }
}
