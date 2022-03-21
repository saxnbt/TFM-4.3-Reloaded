package me.StevenLawson.TotalFreedomMod.discord.command;

import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

import java.util.List;

public interface DiscordCommand {
    /**
     * Can the user execute the command?
     * @param user The user who is attempting execution
     * @param server Where the user is attempting execution from
     * @return If it can be executed
     */
    boolean canExecute(User user, Server server);

    /**
     * Execute the command, and return the results
     * @param user The user who executed the command
     * @param args The arguments they executed it with
     * @return The results as a MessageBuilder
     */
    MessageBuilder execute(User user, List<String> args);
}
