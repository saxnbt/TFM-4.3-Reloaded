package me.StevenLawson.TotalFreedomMod.discord.commands;

import me.StevenLawson.TotalFreedomMod.config.ConfigurationEntry;
import me.StevenLawson.TotalFreedomMod.config.MainConfig;
import me.StevenLawson.TotalFreedomMod.discord.bridge.DiscordBridge;
import me.StevenLawson.TotalFreedomMod.discord.command.ExecutableDiscordCommand;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;

import java.awt.*;
import java.util.*;
import java.util.List;

public class HelpCommand extends ExecutableDiscordCommand {
    public HelpCommand(String command, String description, String category, boolean isAdmin) {
        super(command, description, category, isAdmin);
    }

    @Override
    public MessageBuilder execute(User user, List<String> args) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(Color.GREEN);
        embedBuilder.setTitle("Help Command");
        Map<String, List<ExecutableDiscordCommand>> categories = new HashMap<>();

        for (ExecutableDiscordCommand command : DiscordBridge.COMMAND_MANAGER.commands) {
            if(!categories.containsKey(command.category)) {
                categories.put(command.category, new ArrayList<>(Collections.singletonList(command)));
            } else {
                List<ExecutableDiscordCommand> commands = categories.get(command.category);
                commands.add(command);
                categories.put(command.category, commands);
            }
        }

        for (String category : categories.keySet()) {
            List<ExecutableDiscordCommand> commands = categories.get(category);
            StringBuilder value = new StringBuilder();

            for (ExecutableDiscordCommand command : commands) {
                value.append(String.format("**%s%s** - %s%n", MainConfig.getString(ConfigurationEntry.DISCORD_PREFIX), command.command, command.description));
            }

            embedBuilder.addField(category, value.toString().trim(), false);
        }

        return new MessageBuilder().addEmbeds(embedBuilder);
    }
}
