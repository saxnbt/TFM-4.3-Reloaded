package me.StevenLawson.TotalFreedomMod.discord.command;

import me.StevenLawson.TotalFreedomMod.discord.commands.HelpCommand;
import me.StevenLawson.TotalFreedomMod.discord.commands.ListCommand;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class DiscordCommandManager {
    public List<ExecutableDiscordCommand> commands = new ArrayList<>();

    public void init() {
        commands.add(new ListCommand("list", "Gives a list of online players.", "Server Commands", Collections.singletonList("l"), false));
        commands.add(new HelpCommand("help", "Displays the help command", "Help", false));
    }

    public void parse(String content, User user, Server server, TextChannel channel, String prefix) {
        List<String> args = new ArrayList<>(Arrays.asList(content.split(prefix)));
        args.remove(0);
        String commandOrAlias = args.remove(0);

        for (ExecutableDiscordCommand command : commands) {
            if(command.command.equalsIgnoreCase(commandOrAlias) || command.aliases.contains(commandOrAlias.toLowerCase())) {
                if(command.canExecute(user, server)) {
                    MessageBuilder messageBuilder = command.execute(user, args);
                    messageBuilder.send(channel);
                } else {
                    EmbedBuilder errorEmbed = new EmbedBuilder();
                    errorEmbed.setTitle("Command error");
                    errorEmbed.setColor(Color.RED);
                    errorEmbed.setDescription("You don't have permission to execute this command.");
                    channel.sendMessage(errorEmbed);
                }
            }
        }
    }
}
