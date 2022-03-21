package me.StevenLawson.TotalFreedomMod.discord.command;

import com.google.common.collect.ImmutableList;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

import java.util.ArrayList;
import java.util.List;

public class ExecutableDiscordCommand implements DiscordCommand {
    public String command;
    public String description;
    public String category;
    public List<String> aliases;
    //TODO: Add support for more complex permissions
    public boolean isAdmin;

    /**
     * Creates a command
     * @param command The string the command is execute by, e.g. list
     * @param description What the command does
     * @param category The category of the command
     * @param aliases Other strings that should execute the command
     * @param isAdmin If the command should only be accessible by administrators
     */
    public ExecutableDiscordCommand(String command, String description, String category, List<String> aliases, boolean isAdmin) {
        this.command = command;
        this.description = description;
        this.category = category;
        this.aliases = ImmutableList.copyOf(aliases);
        this.isAdmin = isAdmin;
    }


    /**
     * Creates a command
     * @param command The string the command is execute by, e.g. list
     * @param description What the command does
     * @param category The category of the command
     * @param isAdmin If the command should only be accessible by administrators
     */
    public ExecutableDiscordCommand(String command, String description, String category, boolean isAdmin) {
        this.command = command;
        this.description = description;
        this.category = category;
        this.aliases = ImmutableList.copyOf(new ArrayList<>());
        this.isAdmin = isAdmin;
    }

    public boolean canExecute(User user, Server server) {
        if(this.isAdmin) {
            for (Role role : user.getRoles(server)) {
                if(role.getName().toLowerCase().contains("admin")) {
                    return true;
                }
            }

            return false;
        }

        return true;
    }

    public MessageBuilder execute(User user, List<String> args) {
        return new MessageBuilder().setContent("");
    }
}
