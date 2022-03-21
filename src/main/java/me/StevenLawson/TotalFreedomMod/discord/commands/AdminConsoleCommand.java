package me.StevenLawson.TotalFreedomMod.discord.commands;

import me.StevenLawson.TotalFreedomMod.TotalFreedomMod;
import me.StevenLawson.TotalFreedomMod.discord.command.ExecutableDiscordCommand;
import me.StevenLawson.TotalFreedomMod.discord.sender.DiscordCommandSender;
import me.StevenLawson.TotalFreedomMod.util.SynchronousUtil;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.embed.Embed;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;

import java.awt.*;
import java.util.List;

public class AdminConsoleCommand extends ExecutableDiscordCommand {
    private final EmbedBuilder success = new EmbedBuilder().setTitle("Success").setColor(Color.GREEN).setDescription("Command sent.");
    private final EmbedBuilder error = new EmbedBuilder().setTitle("Command error").setColor(Color.RED).setDescription("An error occured. Check your DMs for more info.");

    public AdminConsoleCommand(String command, String description, String category, List<String> aliases, boolean isAdmin) {
        super(command, description, category, aliases, isAdmin);
    }

    @Override
    public MessageBuilder execute(User user, List<String> args) {
        try {
            new BukkitRunnable() {

                @Override
                public void run() {
                    DiscordCommandSender discordCommandSender = new DiscordCommandSender(user);
                    String command = String.join(" ", args);
                    Bukkit.dispatchCommand(discordCommandSender, command);
                }
            }.runTask(TotalFreedomMod.plugin);

            return new MessageBuilder().addEmbed(success);
        } catch (Exception e) {
            user.sendMessage(String.valueOf(e));
            return new MessageBuilder().addEmbed(error);
        }
    }
}
