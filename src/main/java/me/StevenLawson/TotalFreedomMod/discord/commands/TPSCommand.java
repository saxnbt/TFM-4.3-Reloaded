package me.StevenLawson.TotalFreedomMod.discord.commands;

import com.earth2me.essentials.utils.DateUtil;
import me.StevenLawson.TotalFreedomMod.Server;
import me.StevenLawson.TotalFreedomMod.discord.command.ExecutableDiscordCommand;
import org.bukkit.Bukkit;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;

import java.util.List;

public class TPSCommand extends ExecutableDiscordCommand {
    public TPSCommand(String command, String description, String category, boolean isAdmin) {
        super(command, description, category, isAdmin);
    }

    @Override
    public MessageBuilder execute(User user, List<String> args) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("Server lag information");
        builder.addField("TPS", String.valueOf(Math.floor(Server.getTPS())));
        builder.addField("Uptime", Server.getUptime());
        builder.addField("Maximum Memory", String.format("%s MB", Math.ceil(Server.getMaxMem())));
        builder.addField("Allocated Memory", String.format("%s MB", Math.floor(Server.getTotalMem())));
        builder.addField("Free Memory", String.format("%s MB", Math.ceil(Server.getFreeMem())));
        return new MessageBuilder().addEmbed(builder);
    }
}
