package me.StevenLawson.TotalFreedomMod.config;

import me.StevenLawson.TotalFreedomMod.TotalFreedomMod;
import me.StevenLawson.TotalFreedomMod.player.PlayerRank;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class TagConfiguration {
    private static Plugin plugin = JavaPlugin.getPlugin(TotalFreedomMod.class);
    private static final FileConfiguration playerdata = TotalFreedomMod.getPlugin(TotalFreedomMod.class).getCustomConfig();
    private static File cfile = new File(plugin.getDataFolder(), "playerTags.yml");
    public static void saveTag(String playerUuid, String tag) {
        try {
            // Set tag value and save.
            playerdata.set(playerUuid, tag);
            playerdata.save(cfile);
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public static String getTag(String playerUuid){
        if(playerdata.get(playerUuid) != null && playerdata.isString(playerUuid)) {
            if(!playerdata.getString(playerUuid).isEmpty() && !"".equalsIgnoreCase(playerdata.getString(playerUuid))){
                // Return the value if exists, checking will be done on join event.
                return playerdata.getString(playerUuid);
            } else {
                try {
                    // Try and write player tags if not added
                    playerdata.set(playerUuid, "");
                    playerdata.save(cfile);
                } catch (IOException e){
                    e.printStackTrace();
                }
                return PlayerRank.fromSender(Bukkit.getPlayer(UUID.fromString(playerUuid))).getPrefix();
            }
        } else return null;
    }
}
