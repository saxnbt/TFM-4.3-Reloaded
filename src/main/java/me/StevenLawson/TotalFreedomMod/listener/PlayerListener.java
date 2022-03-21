package me.StevenLawson.TotalFreedomMod.listener;

import me.StevenLawson.TotalFreedomMod.Log;
import me.StevenLawson.TotalFreedomMod.Server;
import me.StevenLawson.TotalFreedomMod.TotalFreedomMod;
import me.StevenLawson.TotalFreedomMod.admin.AdminList;
import me.StevenLawson.TotalFreedomMod.ban.BanManager;
import me.StevenLawson.TotalFreedomMod.discord.bridge.DiscordBridge;
import me.StevenLawson.TotalFreedomMod.command.CommandBlocker;
import me.StevenLawson.TotalFreedomMod.commands.Command_landmine;
import me.StevenLawson.TotalFreedomMod.config.ConfigurationEntry;
import me.StevenLawson.TotalFreedomMod.config.MainConfig;
import me.StevenLawson.TotalFreedomMod.fun.JumpPads;
import me.StevenLawson.TotalFreedomMod.player.*;
import me.StevenLawson.TotalFreedomMod.util.DeprecationUtil;
import me.StevenLawson.TotalFreedomMod.util.SynchronousUtil;
import me.StevenLawson.TotalFreedomMod.util.Utilities;
import me.StevenLawson.TotalFreedomMod.world.AdminWorld;
import me.StevenLawson.TotalFreedomMod.world.RollbackManager;
import me.StevenLawson.TotalFreedomMod.world.RollbackManager.RollbackEntry;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Pattern;

public class PlayerListener implements Listener {
    public static final List<String> BLOCKED_MUTED_CMDS = Arrays.asList(StringUtils.split("say,main.java.me,msg,m,tell,r,reply,mail,email", ","));
    public static final int MSG_PER_HEARTBEAT = 10;
    public static final int DEFAULT_PORT = 25565;
    public static final int MAX_XY_COORD = 30000000;

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerInteract(PlayerInteractEvent event) {
        final org.bukkit.entity.Player player = event.getPlayer();
        final PlayerData playerdata = PlayerData.getPlayerData(player);

        switch (event.getAction())
        {
            case RIGHT_CLICK_AIR:
            case RIGHT_CLICK_BLOCK:
            {
                switch (event.getMaterial())
                {
                    case WATER_BUCKET:
                    {
                        if (ConfigurationEntry.ALLOW_WATER_PLACE.getBoolean())
                        {
                            break;
                        }

                        player.getInventory().setItem(player.getInventory().getHeldItemSlot(), new ItemStack(Material.COOKIE, 1));
                        player.sendMessage(ChatColor.GRAY + "Water buckets are currently disabled.");
                        event.setCancelled(true);
                        break;
                    }

                    case LAVA_BUCKET:
                    {
                        if (ConfigurationEntry.ALLOW_LAVA_PLACE.getBoolean())
                        {
                            break;
                        }

                        player.getInventory().setItem(player.getInventory().getHeldItemSlot(), new ItemStack(Material.COOKIE, 1));
                        player.sendMessage(ChatColor.GRAY + "Lava buckets are currently disabled.");
                        event.setCancelled(true);
                        break;
                    }

                    case EXPLOSIVE_MINECART:
                    {
                        if (ConfigurationEntry.ALLOW_TNT_MINECARTS.getBoolean())
                        {
                            break;
                        }

                        player.getInventory().clear(player.getInventory().getHeldItemSlot());
                        player.sendMessage(ChatColor.GRAY + "TNT minecarts are currently disabled.");
                        event.setCancelled(true);
                        break;
                    }
                }
                break;
            }

            case LEFT_CLICK_AIR:
            case LEFT_CLICK_BLOCK:
            {
                switch (event.getMaterial())
                {
                    case STICK:
                    {
                        if (!AdminList.isSuperAdmin(player))
                        {
                            break;
                        }

                        event.setCancelled(true);

                        final Location location = DeprecationUtil.getTargetBlock(player, null, 5).getLocation();
                        final List<RollbackEntry> entries = RollbackManager.getEntriesAtLocation(location);

                        if (entries.isEmpty())
                        {
                            Utilities.playerMsg(player, "No block edits at that location.");
                            break;
                        }

                        Utilities.playerMsg(player, "Block edits at ("
                                + ChatColor.WHITE + "x" + location.getBlockX()
                                + ", y" + location.getBlockY()
                                + ", z" + location.getBlockZ()
                                + ChatColor.BLUE + ")" + ChatColor.WHITE + ":", ChatColor.BLUE);
                        for (RollbackEntry entry : entries)
                        {
                            Utilities.playerMsg(player, " - " + ChatColor.BLUE + entry.author + " " + entry.getType() + " "
                                    + StringUtils.capitalize(entry.getMaterial().toString().toLowerCase()) + (entry.data == 0 ? "" : ":" + entry.data));
                        }

                        break;
                    }

                    case BONE:
                    {
                        if (!playerdata.mobThrowerEnabled())
                        {
                            break;
                        }

                        Location player_pos = player.getLocation();
                        Vector direction = player_pos.getDirection().normalize();

                        LivingEntity rezzed_mob = (LivingEntity) player.getWorld().spawnEntity(player_pos.add(direction.multiply(2.0)), playerdata.mobThrowerCreature());
                        rezzed_mob.setVelocity(direction.multiply(playerdata.mobThrowerSpeed()));
                        playerdata.enqueueMob(rezzed_mob);

                        event.setCancelled(true);
                        break;
                    }

                    case SULPHUR:
                    {
                        if (!playerdata.isMP44Armed())
                        {
                            break;
                        }

                        event.setCancelled(true);

                        if (playerdata.toggleMP44Firing())
                        {
                            playerdata.startArrowShooter(TotalFreedomMod.plugin);
                        }
                        else
                        {
                            playerdata.stopArrowShooter();
                        }
                        break;
                    }

                    case BLAZE_ROD:
                    {
                        if (!ConfigurationEntry.ALLOW_EXPLOSIONS.getBoolean())
                        {
                            break;
                        }

                        if (!AdminList.isSeniorAdmin(player, true))
                        {
                            break;
                        }

                        event.setCancelled(true);
                        Block targetBlock;

                        if (event.getAction().equals(Action.LEFT_CLICK_AIR))
                        {
                            targetBlock = DeprecationUtil.getTargetBlock(player, null, 120);
                        }
                        else
                        {
                            targetBlock = event.getClickedBlock();
                        }

                        if (targetBlock == null)
                        {
                            player.sendMessage("Can't resolve target block.");
                            break;
                        }

                        player.getWorld().createExplosion(targetBlock.getLocation(), 4F, true);
                        player.getWorld().strikeLightning(targetBlock.getLocation());

                        break;
                    }

                    case CARROT:
                    {
                        if (!ConfigurationEntry.ALLOW_EXPLOSIONS.getBoolean())
                        {
                            break;
                        }

                        if (!AdminList.isSeniorAdmin(player, true))
                        {
                            break;
                        }

                        Location location = player.getLocation().clone();

                        Vector playerPostion = location.toVector().add(new Vector(0.0, 1.65, 0.0));
                        Vector playerDirection = location.getDirection().normalize();

                        double distance = 150.0;
                        Block targetBlock = DeprecationUtil.getTargetBlock(player, null, Math.round((float) distance));
                        if (targetBlock != null)
                        {
                            distance = location.distance(targetBlock.getLocation());
                        }

                        final List<Block> affected = new ArrayList<Block>();

                        Block lastBlock = null;
                        for (double offset = 0.0; offset <= distance; offset += (distance / 25.0))
                        {
                            Block block = playerPostion.clone().add(playerDirection.clone().multiply(offset)).toLocation(player.getWorld()).getBlock();

                            if (!block.equals(lastBlock))
                            {
                                if (block.isEmpty())
                                {
                                    affected.add(block);
                                    block.setType(Material.TNT);
                                }
                                else
                                {
                                    break;
                                }
                            }

                            lastBlock = block;
                        }

                        new BukkitRunnable()
                        {
                            @Override
                            public void run()
                            {
                                for (Block tntBlock : affected)
                                {
                                    TNTPrimed tnt = tntBlock.getWorld().spawn(tntBlock.getLocation(), TNTPrimed.class);
                                    tnt.setFuseTicks(5);
                                    tntBlock.setType(Material.AIR);
                                }
                            }
                        }.runTaskLater(TotalFreedomMod.plugin, 30L);

                        event.setCancelled(true);
                        break;
                    }

                    case RAW_FISH:
                    {
                        final int RADIUS_HIT = 5;
                        final int STRENGTH = 4;

                        // Clownfish
                        if (DeprecationUtil.getData_MaterialData(event.getItem().getData()) == 2)
                        {
                            if (AdminList.isSeniorAdmin(player, true) || AdminList.isTelnetAdmin(player, true))
                            {
                                boolean didHit = false;

                                final Location playerLoc = player.getLocation();
                                final Vector playerLocVec = playerLoc.toVector();

                                final List<org.bukkit.entity.Player> players = player.getWorld().getPlayers();
                                for (final org.bukkit.entity.Player target : players)
                                {
                                    if (target == player)
                                    {
                                        continue;
                                    }

                                    final Location targetPos = target.getLocation();
                                    final Vector targetPosVec = targetPos.toVector();

                                    try
                                    {
                                        if (targetPosVec.distanceSquared(playerLocVec) < (RADIUS_HIT * RADIUS_HIT))
                                        {
                                            Utilities.setFlying(player, false);
                                            target.setVelocity(targetPosVec.subtract(playerLocVec).normalize().multiply(STRENGTH));
                                            didHit = true;
                                        }
                                    }
                                    catch (IllegalArgumentException ex)
                                    {
                                    }
                                }

                                if (didHit)
                                {
                                    final Sound[] sounds = Sound.values();
                                    for (Sound sound : sounds)
                                    {
                                        if (sound.toString().contains("HIT"))
                                        {
                                            playerLoc.getWorld().playSound(randomOffset(playerLoc, 5.0), sound, 100.0f, randomDoubleRange(0.5, 2.0).floatValue());
                                        }
                                    }
                                }
                            }
                            else
                            {
                                final StringBuilder msg = new StringBuilder();
                                final char[] chars = (player.getName() + " is a clown.").toCharArray();
                                for (char c : chars)
                                {
                                    msg.append(Utilities.randomChatColor()).append(c);
                                }
                                Utilities.bcastMsg(msg.toString());

                                player.getInventory().getItemInHand().setType(Material.POTATO_ITEM);
                            }

                            event.setCancelled(true);
                            break;
                        }
                    }
                }
                break;
            }
        }
    }
    private static final Random RANDOM = new Random();

    private static Location randomOffset(Location a, double magnitude)
    {
        return a.clone().add(randomDoubleRange(-1.0, 1.0) * magnitude, randomDoubleRange(-1.0, 1.0) * magnitude, randomDoubleRange(-1.0, 1.0) * magnitude);
    }

    private static Double randomDoubleRange(double min, double max)
    {
        return min + (RANDOM.nextDouble() * ((max - min) + 1.0));
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerTeleport(PlayerTeleportEvent event)
    {
        final org.bukkit.entity.Player player = event.getPlayer();
        final PlayerData playerdata = PlayerData.getPlayerData(player);
        // Check absolute value to account for negatives
        if (Math.abs(event.getTo().getX()) >= MAX_XY_COORD || Math.abs(event.getTo().getZ()) >= MAX_XY_COORD)
        {
            event.setCancelled(true); // illegal position, cancel it
        }

        if (!AdminList.isSuperAdmin(player) && playerdata.isFrozen())
        {
            Utilities.setFlying(player, true);
            event.setTo(playerdata.getFreezeLocation());
            return; // Don't process adminworld validation
        }

        AdminWorld.getInstance().validateMovement(event);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerMove(PlayerMoveEvent event)
    {
        final Location from = event.getFrom();
        final Location to = event.getTo();
        try
        {
            if (from.getWorld() == to.getWorld() && from.distanceSquared(to) < (0.0001 * 0.0001))
            {
                // If player just rotated, but didn't move, don't process this event.
                return;
            }
        }
        catch (IllegalArgumentException ex)
        {
        }

        if (!AdminWorld.getInstance().validateMovement(event))
        {
            return;
        }

        final org.bukkit.entity.Player player = event.getPlayer();
        final PlayerData playerdata = PlayerData.getPlayerData(player);

        for (Entry<org.bukkit.entity.Player, Double> fuckoff : TotalFreedomMod.fuckoffEnabledFor.entrySet())
        {
            org.bukkit.entity.Player fuckoffPlayer = fuckoff.getKey();

            if (fuckoffPlayer.equals(player) || !fuckoffPlayer.isOnline())
            {
                continue;
            }

            double fuckoffRange = fuckoff.getValue();

            Location playerLocation = player.getLocation();
            Location fuckoffLocation = fuckoffPlayer.getLocation();

            double distanceSquared;
            try
            {
                distanceSquared = playerLocation.distanceSquared(fuckoffLocation);
            }
            catch (IllegalArgumentException ex)
            {
                continue;
            }

            if (distanceSquared < (fuckoffRange * fuckoffRange))
            {
                event.setTo(fuckoffLocation.clone().add(playerLocation.subtract(fuckoffLocation).toVector().normalize().multiply(fuckoffRange * 1.1)));
                break;
            }
        }

        // Freeze
        if (!AdminList.isSuperAdmin(player) && playerdata.isFrozen())
        {
            Utilities.setFlying(player, true);
            event.setTo(playerdata.getFreezeLocation());
        }

        if (playerdata.isCaged())
        {
            Location targetPos = player.getLocation().add(0, 1, 0);

            boolean outOfCage;
            if (!targetPos.getWorld().equals(playerdata.getCagePos().getWorld()))
            {
                outOfCage = true;
            }
            else
            {
                outOfCage = targetPos.distanceSquared(playerdata.getCagePos()) > (2.5 * 2.5);
            }

            if (outOfCage)
            {
                playerdata.setCaged(true, targetPos, playerdata.getCageMaterial(PlayerData.CageLayer.OUTER), playerdata.getCageMaterial(PlayerData.CageLayer.INNER));
                playerdata.regenerateHistory();
                playerdata.clearHistory();
                Utilities.buildHistory(targetPos, 2, playerdata);
                Utilities.generateHollowCube(targetPos, 2, playerdata.getCageMaterial(PlayerData.CageLayer.OUTER));
                Utilities.generateCube(targetPos, 1, playerdata.getCageMaterial(PlayerData.CageLayer.INNER));
            }
        }

        if (playerdata.isOrbiting())
        {
            if (player.getVelocity().length() < playerdata.orbitStrength() * (2.0 / 3.0))
            {
                player.setVelocity(new Vector(0, playerdata.orbitStrength(), 0));
            }
        }

        if (JumpPads.getMode().isOn())
        {
            JumpPads.PlayerMoveEvent(event);
        }

        if (!(ConfigurationEntry.LANDMINES_ENABLED.getBoolean() && ConfigurationEntry.ALLOW_EXPLOSIONS.getBoolean()))
        {
            return;
        }

        final Iterator<Command_landmine.TFM_LandmineData> landmines = Command_landmine.TFM_LandmineData.landmines.iterator();
        while (landmines.hasNext())
        {
            final Command_landmine.TFM_LandmineData landmine = landmines.next();

            final Location location = landmine.location;
            if (location.getBlock().getType() != Material.TNT)
            {
                landmines.remove();
                continue;
            }

            if (landmine.player.equals(player))
            {
                break;
            }

            if (!player.getWorld().equals(location.getWorld()))
            {
                continue;
            }

            if (!(player.getLocation().distanceSquared(location) <= (landmine.radius * landmine.radius)))
            {
                break;
            }

            landmine.location.getBlock().setType(Material.AIR);

            final TNTPrimed tnt1 = location.getWorld().spawn(location, TNTPrimed.class);
            tnt1.setFuseTicks(40);
            tnt1.setPassenger(player);
            tnt1.setVelocity(new Vector(0.0, 2.0, 0.0));

            final TNTPrimed tnt2 = location.getWorld().spawn(player.getLocation(), TNTPrimed.class);
            tnt2.setFuseTicks(1);

            player.setGameMode(GameMode.SURVIVAL);
            landmines.remove();
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onLeavesDecay(LeavesDecayEvent event)
    {
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerChat(AsyncPlayerChatEvent event)
    {
        try
        {
            final org.bukkit.entity.Player player = event.getPlayer();
            String message = event.getMessage().trim();

            final PlayerData playerdata = PlayerData.getPlayerDataSync(player);

            // Check for spam
            final Long lastRan = HeartBeat.getLastRan();
            if (lastRan == null || lastRan + TotalFreedomMod.HEARTBEAT_RATE * 1000L < System.currentTimeMillis())
            {
                //Log.warning("Heartbeat service timeout - can't check block place/break rates.");
            }
            else
            {
                if (playerdata.incrementAndGetMsgCount() > MSG_PER_HEARTBEAT)
                {
                    SynchronousUtil.bcastMsg(player.getName() + " was automatically kicked for spamming chat.", ChatColor.RED);
                    SynchronousUtil.autoEject(player, "Kicked for spamming chat.");

                    playerdata.resetMsgCount();

                    event.setCancelled(true);
                    return;
                }
            }

            // Check for message repeat
            if (playerdata.getLastMessage().equalsIgnoreCase(message))
            {
                SynchronousUtil.playerMsg(player, "Please do not repeat messages.");
                event.setCancelled(true);
                return;
            }

            playerdata.setLastMessage(message);

            // Check for muted
            if (playerdata.isMuted())
            {
                if (!AdminList.isSuperAdminSync(player))
                {
                    SynchronousUtil.playerMsg(player, ChatColor.RED + "You are muted, STFU! - You will be unmuted in 5 minutes.");
                    event.setCancelled(true);
                    return;
                }

                playerdata.setMuted(false);
            }

            // Strip color from messages
            message = ChatColor.stripColor(message);

            // Truncate messages that are too long - 100 characters is vanilla client max
            if (message.length() > 100)
            {
                message = message.substring(0, 100);
                SynchronousUtil.playerMsg(player, "Message was shortened because it was too long to send.");
            }

            // Check for caps
            if (message.length() >= 6)
            {
                int caps = 0;
                for (char c : message.toCharArray())
                {
                    if (Character.isUpperCase(c))
                    {
                        caps++;
                    }
                }
                if (((float) caps / (float) message.length()) > 0.65) //Compute a ratio so that longer sentences can have more caps.
                {
                    message = message.toLowerCase();
                }
            }

            // Check for adminchat
            if (playerdata.inAdminChat())
            {
                SynchronousUtil.adminChatMessage(player, message, false);
                event.setCancelled(true);
                return;
            }

            // Finally, set message
            event.setMessage(ChatColor.translateAlternateColorCodes('&', message));

            // Set the tag
            if (playerdata.getTag() != null) {
                event.setFormat(ChatColor.translateAlternateColorCodes('&', MainConfig.getString(ConfigurationEntry.CHAT_FORMAT)).replace("{RANK}", playerdata.getTag().replaceAll("%", "%%")));
            }

            DiscordBridge.transmitMessage(String.format(event.getFormat(), event.getPlayer().getDisplayName(), event.getMessage()).replaceAll("([`_~*])", "\\\\$1"));
        }
        catch (Exception ex)
        {
            Log.severe(ex);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event)
    {
        String command = event.getMessage();
        final org.bukkit.entity.Player player = event.getPlayer();

        final PlayerData playerdata = PlayerData.getPlayerData(player);
        playerdata.setLastCommand(command);

        if (playerdata.incrementAndGetMsgCount() > MSG_PER_HEARTBEAT)
        {
            Utilities.bcastMsg(player.getName() + " was automatically kicked for spamming commands.", ChatColor.RED);
            Utilities.autoEject(player, "Kicked for spamming commands.");

            playerdata.resetMsgCount();

            Utilities.TFM_EntityWiper.wipeEntities(true, true);

            event.setCancelled(true);
            return;
        }

        if (playerdata.allCommandsBlocked())
        {
            Utilities.playerMsg(player, "Your commands have been blocked by an admin.", ChatColor.RED);
            event.setCancelled(true);
            return;
        }

        // Block commands if player is muted
        if (playerdata.isMuted())
        {
            if (!AdminList.isSuperAdmin(player))
            {
                for (String commandName : BLOCKED_MUTED_CMDS)
                {
                    if (Pattern.compile("^/" + commandName.toLowerCase() + " ").matcher(command.toLowerCase()).find())
                    {
                        player.sendMessage(ChatColor.RED + "That command is blocked while you are muted.");
                        event.setCancelled(true);
                        return;
                    }
                }
            }
            else
            {
                playerdata.setMuted(false);
            }
        }

        if (ConfigurationEntry.ENABLE_PREPROCESS_LOG.getBoolean())
        {
            Log.info(String.format("[PREPROCESS_COMMAND] %s(%s): %s", player.getName(), ChatColor.stripColor(player.getDisplayName()), command), true);
        }

        // Blocked commands
        if (CommandBlocker.isCommandBlocked(command, player, true))
        {
            // CommandBlocker handles messages and broadcasts
            event.setCancelled(true);
        }

        if (!AdminList.isSuperAdmin(player))
        {
            for (org.bukkit.entity.Player pl : Bukkit.getOnlinePlayers())
            {
                if (AdminList.isSuperAdmin(pl) && PlayerData.getPlayerData(pl).cmdspyEnabled())
                {
                    Utilities.playerMsg(pl, player.getName() + ": " + command);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerDropItem(PlayerDropItemEvent event)
    {
        if (ConfigurationEntry.AUTO_ENTITY_WIPE.getBoolean())
        {
            if (event.getPlayer().getWorld().getEntities().size() > 750)
            {
                event.setCancelled(true);
            }
            else
            {
                event.getItemDrop().remove();
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerKick(PlayerKickEvent event)
    {
        playerLeave(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event)
    {
        playerLeave(event.getPlayer());
    }

    private void playerLeave(org.bukkit.entity.Player player)
    {
        TotalFreedomMod.fuckoffEnabledFor.remove(player);

        final PlayerData playerdata = PlayerData.getPlayerData(player);

        playerdata.disarmMP44();

        if (playerdata.isCaged()) {
            playerdata.regenerateHistory();
            playerdata.clearHistory();
        }

        if (PlayerList.existsEntry(player)) {
            DiscordBridge.transmitMessage(String.format("**%s left the server**", player.getDisplayName()));
        }

        PlayerList.removeEntry(player);
        Log.info("[EXIT] " + player.getName() + " left the game.", true);

    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event)
    {
        final org.bukkit.entity.Player player = event.getPlayer();
        final String ip = Utilities.getIp(player);
        final Player playerEntry;
        Log.info("[JOIN] " + Utilities.formatPlayer(player) + " joined the game with IP address: " + ip, true);
        // Check absolute value to account for negatives
        if (Math.abs(player.getLocation().getX()) >= MAX_XY_COORD || Math.abs(player.getLocation().getZ()) >= MAX_XY_COORD)
        {
            player.teleport(player.getWorld().getSpawnLocation()); // Illegal position, teleport to spawn
        }
        // Handle PlayerList entry (persistent)
        if (PlayerList.existsEntry(player))
        {
            playerEntry = PlayerList.getEntry(player);
            playerEntry.setLastLoginUnix(Utilities.getUnixTime());
            playerEntry.setLastLoginName(player.getName());
            playerEntry.addIp(ip);
            playerEntry.save();
        }
        else
        {
            playerEntry = PlayerList.getEntry(player);
            Log.info("Added new player: " + Utilities.formatPlayer(player));
        }

        // Generate PlayerData (non-persistent)
        final PlayerData playerdata = PlayerData.getPlayerData(player);
        playerdata.setSuperadminIdVerified(false);

        if (AdminList.isSuperAdmin(player))
        {
            for (String storedIp : playerEntry.getIps())
            {
                BanManager.unbanIp(storedIp);
                BanManager.unbanIp(Utilities.getFuzzyIp(storedIp));
            }

            BanManager.unbanUuid(UUIDManager.getUniqueId(player));

            player.setOp(true);

            // Verify strict IP match
            if (!AdminList.isIdentityMatched(player))
            {
                playerdata.setSuperadminIdVerified(false);
                Utilities.bcastMsg("Warning: " + player.getName() + " is an admin, but is using an account not registered to one of their ip-list.", ChatColor.RED);
            }
            else
            {
                playerdata.setSuperadminIdVerified(true);
                AdminList.updateLastLogin(player);
            }
        }

        // Handle admin impostors
        if (AdminList.isAdminImpostor(player))
        {
            Utilities.bcastMsg("Warning: " + player.getName() + " has been flagged as an impostor and has been frozen!", ChatColor.RED);
            Utilities.bcastMsg(ChatColor.AQUA + player.getName() + " is " + PlayerRank.getLoginMessage(player));
            player.getInventory().clear();
            player.setOp(false);
            player.setGameMode(GameMode.SURVIVAL);
            PlayerData.getPlayerData(player).setFrozen(true);
        }
        else if (AdminList.isSuperAdmin(player) || Utilities.DEVELOPERS.contains(player.getName()))
        {
            Utilities.bcastMsg(ChatColor.AQUA + player.getName() + " is " + PlayerRank.getLoginMessage(player));
        }

        //TODO: Cleanup
        String name = player.getName();
        if (Utilities.DEVELOPERS.contains(player.getName()))
        {
            name = ChatColor.DARK_PURPLE + name;
            PlayerData.getPlayerData(player).setTag("&8[&5Developer&8]");
        }
        else if (AdminList.isSuperAdmin(player))
        {
            if (ConfigurationEntry.SERVER_OWNERS.getList().contains(name))
            {
                name = ChatColor.BLUE + name;
                PlayerData.getPlayerData(player).setTag("&8[&9Owner&8]");
            }
            else if (AdminList.isSeniorAdmin(player))
            {
                name = ChatColor.LIGHT_PURPLE + name;
                PlayerData.getPlayerData(player).setTag("&8[&dSenior Admin&8]");
            }
            else if (AdminList.isTelnetAdmin(player, true))
            {
                name = ChatColor.DARK_GREEN + name;
                PlayerData.getPlayerData(player).setTag("&8[&2Telnet Admin&8]");
            }
            else
            {
                name = ChatColor.AQUA + name;
                PlayerData.getPlayerData(player).setTag("&8[&BSuper Admin&8]");
            }
        }

        try
        {
            player.setPlayerListName(StringUtils.substring(name, 0, 16));
        }
        catch (IllegalArgumentException ex)
        {
        }

        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                if (ConfigurationEntry.ADMIN_ONLY_MODE.getBoolean()) {
                    player.sendMessage(ChatColor.RED + "Server is currently closed to non-superadmins.");
                }

                if (TotalFreedomMod.lockdownEnabled) {
                    Utilities.playerMsg(player, "Warning: Server is currenty in lockdown-mode, new players will not be able to join!", ChatColor.RED);
                }
            }
        }.runTaskLater(TotalFreedomMod.plugin, 20L * 1L);

        if (!player.hasPlayedBefore()) {
            if (Boolean.TRUE.equals(MainConfig.getBoolean(ConfigurationEntry.SERVER_OP_ON_JOIN))) {
                PlayerRank rank = PlayerRank.fromSender(player);

                if (rank.equals(PlayerRank.NON_OP)) {
                    player.setOp(true);
                }
            }
        }

        DiscordBridge.transmitMessage(String.format("**%s joined the server**", player.getDisplayName()));
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerPreLogin(AsyncPlayerPreLoginEvent event)
    {
        Server.handlePlayerPreLogin(event);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerLogin(PlayerLoginEvent event)
    {
        Server.handlePlayerLogin(event);
    }
}
