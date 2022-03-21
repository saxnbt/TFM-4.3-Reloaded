package me.StevenLawson.TotalFreedomMod.discord.commands;

import me.StevenLawson.TotalFreedomMod.discord.command.ExecutableDiscordCommand;
import me.StevenLawson.TotalFreedomMod.exception.PenisException;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;

import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

public class UptimeCommand extends ExecutableDiscordCommand {
    public UptimeCommand(String command, String description, String category, boolean isAdmin) {
        super(command, description, category, isAdmin);
    }

    @Override
    public MessageBuilder execute(User user, List<String> args) {
        EmbedBuilder builder = new EmbedBuilder();
        try {
            builder.setTitle("VPS Uptime Information")
                    .setDescription(getSystemUptime());
        } catch (Exception e) {
            builder.setTitle("Command error")
                    .setColor(Color.RED)
                    .setDescription("Something went wrong");
        }
        return new MessageBuilder().addEmbed(builder);
    }

    private String getSystemUptime() throws Exception {
        Process uptimeProc = Runtime.getRuntime().exec("uptime");
        BufferedReader in = new BufferedReader(new InputStreamReader(uptimeProc.getInputStream()));
        String line = in.readLine();
        if (line != null) {
            return line;
        } else {
            throw new PenisException();
        }
    }
}
