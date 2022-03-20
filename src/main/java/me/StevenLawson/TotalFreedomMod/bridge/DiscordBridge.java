package me.StevenLawson.TotalFreedomMod.bridge;

import me.StevenLawson.TotalFreedomMod.Log;
import me.StevenLawson.TotalFreedomMod.config.ConfigurationEntry;
import me.StevenLawson.TotalFreedomMod.config.MainConfig;
import me.StevenLawson.TotalFreedomMod.player.PlayerRank;
import org.bukkit.Bukkit;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.MessageAuthor;
import org.javacord.api.entity.message.embed.EmbedBuilder;

import java.util.ArrayList;
import java.util.Optional;
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
                    // TODO: Make this shitty code better (I was very tired from a blood test whilst writing this)

                    EmbedBuilder builder = new EmbedBuilder()
                            .setTitle(String.format("Player List - %s", MainConfig.getString(ConfigurationEntry.SERVER_NAME)))
                            .setDescription(String.format("There are %s / %s online players", Bukkit.getOnlinePlayers().size(), Bukkit.getMaxPlayers()));
                    ArrayList<String> seniorAdmins = new ArrayList<>();
                    ArrayList<String> developers = new ArrayList<>();
                    ArrayList<String> impostors = new ArrayList<>();
                    ArrayList<String> deopped = new ArrayList<>();
                    ArrayList<String> operators = new ArrayList<>();
                    ArrayList<String> superAdmins = new ArrayList<>();
                    ArrayList<String> superTelnetAdmins = new ArrayList<>();
                    ArrayList<String> owners = new ArrayList<>();

                    for (org.bukkit.entity.Player player : Bukkit.getOnlinePlayers()) {
                        PlayerRank rank = PlayerRank.fromSender(player);

                        if (EssentialsBridge.getEssentialsUser(player.getName()).isVanished()) {
                            continue;
                        }

                        if (rank.equals(PlayerRank.IMPOSTOR)) {
                            impostors.add(player.getName());
                        } else if (rank.equals(PlayerRank.DEVELOPER)) {
                            developers.add(player.getName());
                        } else if (rank.equals(PlayerRank.NON_OP)) {
                            deopped.add(player.getName());
                        } else if (rank.equals(PlayerRank.SENIOR)) {
                            seniorAdmins.add(player.getName());
                        } else if (rank.equals(PlayerRank.OWNER)) {
                            owners.add(player.getName());
                        } else if (rank.equals(PlayerRank.OP)) {
                            operators.add(player.getName());
                        } else if (rank.equals(PlayerRank.SUPER)) {
                            superAdmins.add(player.getName());
                        } else if (rank.equals(PlayerRank.TELNET)) {
                            superTelnetAdmins.add(player.getName());
                        }
                    }

                    if (owners.size() > 0) {
                        builder.addField(String.format("Owners (%s)", owners.size()), String.join(", ", owners));
                    }

                    if (seniorAdmins.size() > 0) {
                        builder.addField(String.format("Senior Admins (%s)", seniorAdmins.size()), String.join(", ", seniorAdmins));
                    }

                    if (developers.size() > 0) {
                        builder.addField(String.format("Developers (%s)", developers.size()), String.join(", ", developers));
                    }

                    if (superTelnetAdmins.size() > 0) {
                        builder.addField(String.format("Super Telnet Admins (%s)", superTelnetAdmins.size()), String.join(", ", superTelnetAdmins));
                    }

                    if (superAdmins.size() > 0) {
                        builder.addField(String.format("Super Admins (%s)", superAdmins.size()), String.join(", ", seniorAdmins));
                    }

                    if (operators.size() > 0) {
                        builder.addField(String.format("Operators (%s)", operators.size()), String.join(", ", operators));
                    }

                    if (deopped.size() > 0) {
                        builder.addField(String.format("Non-Operators (%s)", deopped.size()), String.join(", ", deopped));
                    }

                    if (impostors.size() > 0) {
                        builder.addField(String.format("Impostors (%s)", impostors.size()), String.join(", ", impostors));
                    }

                    CHANNEL.sendMessage(builder);
                } else {
                    String format = MainConfig.getString(ConfigurationEntry.DISCORD_FORMAT);
                    format = format.replace("{TAG}", author.getDiscriminatedName());
                    format = format.replace("{USERNAME}", author.getName());

                    Bukkit.broadcastMessage(String.format(format, content));
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

        return message.replaceAll(colors.pattern(), "").replaceAll(pings.pattern(), "@\u200B");
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
