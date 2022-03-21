package me.StevenLawson.TotalFreedomMod.discord.commands;

import me.StevenLawson.TotalFreedomMod.bridge.EssentialsBridge;
import me.StevenLawson.TotalFreedomMod.config.ConfigurationEntry;
import me.StevenLawson.TotalFreedomMod.config.MainConfig;
import me.StevenLawson.TotalFreedomMod.discord.command.ExecutableDiscordCommand;
import me.StevenLawson.TotalFreedomMod.player.PlayerRank;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ListCommand extends ExecutableDiscordCommand {
    public ListCommand(String command, String description, String category, List<String> aliases, boolean isAdmin) {
        super(command, description, category, aliases, isAdmin);
    }

    @Override
    public MessageBuilder execute(User user, List<String> args) {
        EmbedBuilder builder = new EmbedBuilder()
                .setTitle(String.format("Player List - %s", MainConfig.getString(ConfigurationEntry.SERVER_NAME)))
                .setDescription(String.format("There are %s / %s online players", Bukkit.getOnlinePlayers().size(), Bukkit.getMaxPlayers()));

        List<PlayerRank> inGameRanks = new ArrayList<>();

        for (Player player : Bukkit.getOnlinePlayers()) {
            com.earth2me.essentials.User essentialsUser = EssentialsBridge.getEssentialsUser(player.getDisplayName());

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

        return new MessageBuilder().addEmbed(builder);
    }
}
