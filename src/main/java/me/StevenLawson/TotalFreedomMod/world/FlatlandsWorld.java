package me.StevenLawson.TotalFreedomMod.world;

import me.StevenLawson.TotalFreedomMod.Log;
import me.StevenLawson.TotalFreedomMod.config.ConfigurationEntry;
import me.StevenLawson.TotalFreedomMod.gamerule.GameRuleHandler;
import me.StevenLawson.TotalFreedomMod.util.Utilities;
import org.apache.commons.io.FileUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import java.io.File;

public class FlatlandsWorld extends CustomWorld
{
    private static final String GENERATION_PARAMETERS = ConfigurationEntry.FLATLANDS_GENERATE_PARAMS.getString();
    private static final String WORLD_NAME = "flatlands";

    private FlatlandsWorld()
    {
    }

    @Override
    protected World generateWorld()
    {
        if (!ConfigurationEntry.FLATLANDS_GENERATE.getBoolean())
        {
            return null;
        }

        wipeFlatlandsIfFlagged();

        WorldCreator worldCreator = new WorldCreator(WORLD_NAME);
        worldCreator.generateStructures(false);
        worldCreator.type(WorldType.NORMAL);
        worldCreator.environment(World.Environment.NORMAL);
        worldCreator.generator(new CleanroomChunkGenerator(GENERATION_PARAMETERS));

        World world = Bukkit.getServer().createWorld(worldCreator);

        world.setSpawnFlags(false, false);
        world.setSpawnLocation(0, 50, 0);

        Block welcomeSignBlock = world.getBlockAt(0, 50, 0);
        welcomeSignBlock.setType(Material.SIGN_POST);
        org.bukkit.block.Sign welcomeSign = (org.bukkit.block.Sign) welcomeSignBlock.getState();

        org.bukkit.material.Sign signData = (org.bukkit.material.Sign) welcomeSign.getData();
        signData.setFacingDirection(BlockFace.NORTH);

        welcomeSign.setLine(0, ChatColor.GREEN + "Flatlands");
        welcomeSign.setLine(1, ChatColor.DARK_GRAY + "---");
        welcomeSign.setLine(2, ChatColor.YELLOW + "Spawn Point");
        welcomeSign.setLine(3, ChatColor.DARK_GRAY + "---");
        welcomeSign.update();

        GameRuleHandler.commitGameRules();

        return world;
    }

    public static void wipeFlatlandsIfFlagged()
    {
        boolean doFlatlandsWipe = false;
        try
        {
            doFlatlandsWipe = Utilities.getSavedFlag("do_wipe_flatlands");
        }
        catch (Exception ex)
        {
        }

        if (doFlatlandsWipe)
        {
            if (Bukkit.getServer().getWorld("flatlands") == null)
            {
                Log.info("Wiping flatlands.");
                Utilities.setSavedFlag("do_wipe_flatlands", false);
                FileUtils.deleteQuietly(new File("./flatlands"));
            }
            else
            {
                Log.severe("Can't wipe flatlands, it is already loaded.");
            }
        }
    }

    public static FlatlandsWorld getInstance()
    {
        return FlatlandsWorldHolder.INSTANCE;
    }

    private static class FlatlandsWorldHolder {
        private static final FlatlandsWorld INSTANCE = new FlatlandsWorld();
    }
}
