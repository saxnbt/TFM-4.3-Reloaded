package me.StevenLawson.TotalFreedomMod.discord.bridge;

import me.StevenLawson.TotalFreedomMod.Log;
import me.StevenLawson.TotalFreedomMod.config.ConfigurationEntry;
import me.StevenLawson.TotalFreedomMod.config.MainConfig;
import me.StevenLawson.TotalFreedomMod.discord.command.DiscordCommandManager;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.MessageAttachment;
import org.javacord.api.entity.message.MessageAuthor;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

import java.util.Optional;
import java.util.regex.Pattern;

public class DiscordBridge {
    private static DiscordApi DISCORD_API;
    private static TextChannel CHANNEL;
    public static DiscordCommandManager COMMAND_MANAGER;

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
            COMMAND_MANAGER = new DiscordCommandManager();
            COMMAND_MANAGER.init();

            CHANNEL.addMessageCreateListener((message) -> {
                String content = message.getMessageContent();
                String prefix = MainConfig.getString(ConfigurationEntry.DISCORD_PREFIX);
                MessageAuthor author = message.getMessage().getAuthor();

                if (author.isBotUser() || !message.isServerMessage()) return;
                Optional<Server> server = message.getServer();
                Optional<User> user = author.asUser();

                if(prefix == null) {
                    Log.severe("Bot prefix does not exist. Stopping bot...");
                    stop();
                    return;
                }

                if(!server.isPresent()) {
                    Log.warning("Discord server wasn't present in message, this may be a sign you've not properly configured the intents for your bot.");
                    return;
                }

                if(!user.isPresent()) {
                    Log.warning("Unable to get user of message author. This may be a sign you've not properly configured the intents for your bot.");
                    return;
                }

                if (content.toLowerCase().startsWith(prefix)) {
                    COMMAND_MANAGER.parse(content, user.get(), server.get(), message.getChannel(), prefix);
                } else {
                    String format = MainConfig.getString(ConfigurationEntry.DISCORD_FORMAT);
                    format = format.replace("{TAG}", author.getDiscriminatedName());
                    format = format.replace("{USERNAME}", author.getName());
                    BaseComponent[] components = TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', String.format(format, content)));
                    TextComponent component = new TextComponent("");

                    for (BaseComponent baseComponent : components) {
                        component.addExtra(baseComponent);
                    }

                    if(message.getMessageAttachments().size() > 0) {
                        int i = 0;
                        for (MessageAttachment messageAttachment : message.getMessageAttachments()) {
                            String url = messageAttachment.getProxyUrl().toString();
                            ClickEvent clickEvent = new ClickEvent(ClickEvent.Action.OPEN_URL, url);
                            TextComponent warningComponent = new TextComponent("WARNING: By clicking on this text, your client will open:\n\n");
                            warningComponent.setColor(net.md_5.bungee.api.ChatColor.RED);
                            warningComponent.setBold(true);
                            TextComponent urlComponent = new TextComponent(url);
                            urlComponent.setColor(net.md_5.bungee.api.ChatColor.DARK_AQUA);
                            urlComponent.setUnderlined(true);
                            urlComponent.setBold(false);
                            warningComponent.addExtra(urlComponent);
                            HoverEvent hoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{warningComponent});
                            TextComponent mediaComponent = new TextComponent((i == 0 && content.isEmpty()) ? "[Media]" : " [Media]");
                            mediaComponent.setColor(net.md_5.bungee.api.ChatColor.YELLOW);
                            mediaComponent.setClickEvent(clickEvent);
                            mediaComponent.setHoverEvent(hoverEvent);
                            component.addExtra(mediaComponent);
                            i++;
                        }
                    }

                    Bukkit.spigot().broadcast(component);
                    Log.info(component.toPlainText());
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
