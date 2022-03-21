package me.StevenLawson.TotalFreedomMod.commands;

import me.StevenLawson.TotalFreedomMod.admin.AdminList;
import me.StevenLawson.TotalFreedomMod.config.TagConfiguration;
import me.StevenLawson.TotalFreedomMod.player.PlayerData;
import me.StevenLawson.TotalFreedomMod.util.Utilities;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

@CommandPermissions(level = AdminLevel.OP, source = SourceType.BOTH)
@CommandParameters(description = "Sets yourself a prefix", usage = "/<command> <(-s) set <tag..> | off | clear <player> | clearall>")
public class Command_tag extends FreedomCommand {
    public static final List<String> FORBIDDEN_WORDS = Arrays.asList("admin", "owner", "moderator", "developer", "console");

    @Override
    public boolean run(CommandSender sender, org.bukkit.entity.Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole) {
        if (args.length == 1) {
            if ("list".equalsIgnoreCase(args[0])) {
                playerMsg("Tags for all online players:");

                for (final Player player : server.getOnlinePlayers()) {
                    final PlayerData playerdata = PlayerData.getPlayerData(player);
                    if (playerdata.getTag() != null)
                    {
                        playerMsg(player.getName() + ": " + playerdata.getTag());
                    }
                }

                return true;
            }
            else if ("clearall".equalsIgnoreCase(args[0]))
            {
                if (!AdminList.isSuperAdmin(sender))
                {
                    playerMsg(FreedomCommand.MSG_NO_PERMS);
                    return true;
                }

                Utilities.adminAction(sender.getName(), "Removing all tags", false);

                int count = 0;
                for (final Player player : server.getOnlinePlayers())
                {
                    final PlayerData playerdata = PlayerData.getPlayerData(player);
                    if (playerdata.getTag() != null)
                    {
                        count++;
                        if(TagConfiguration.getTag(playerdata.getUniqueId().toString()) != null){
                            String playerTag = TagConfiguration.getTag(playerdata.getUniqueId().toString());
                            if(!"".equalsIgnoreCase(playerTag)){
                                TagConfiguration.saveTag(sender_p.getUniqueId().toString(), "");
                            }
                        }
                        playerdata.setTag(null);
                    }
                }

                playerMsg(count + " tag(s) removed.");

                return true;
            }
            else if ("off".equalsIgnoreCase(args[0]))
            {
                if (senderIsConsole)
                {
                    playerMsg("\"/tag off\" can't be used from the console. Use \"/tag clear <player>\" or \"/tag clearall\" instead.");
                }
                else
                {
                    PlayerData.getPlayerData(sender_p).setTag(null);
                    if(TagConfiguration.getTag(sender_p.getUniqueId().toString()) != null){
                        String playerTag = TagConfiguration.getTag(sender_p.getUniqueId().toString());
                        if(!"".equalsIgnoreCase(playerTag)){
                            TagConfiguration.saveTag(sender_p.getUniqueId().toString(), "");
                        }
                    }
                    playerMsg("Your tag has been removed.");
                }

                return true;
            }
            else
            {
                return false;
            }
        }
        else if (args.length >= 2)
        {
            if ("clear".equalsIgnoreCase(args[0]))
            {
                if (!AdminList.isSuperAdmin(sender))
                {
                    playerMsg(FreedomCommand.MSG_NO_PERMS);
                    return true;
                }

                final Player player = getPlayer(args[1]);
                if (player == null)
                {
                    playerMsg(FreedomCommand.PLAYER_NOT_FOUND);
                    return true;
                }
                PlayerData.getPlayerData(player).setTag(null);
                if(TagConfiguration.getTag(player.getUniqueId().toString()) != null){
                    String playerTag = TagConfiguration.getTag(player.getUniqueId().toString());
                    if(!"".equalsIgnoreCase(playerTag)){
                        TagConfiguration.saveTag(player.getUniqueId().toString(), "");
                    }
                }
                playerMsg("Removed " + player.getName() + "'s tag.");

                return true;
            }
            else if ("set".equalsIgnoreCase(args[0]))
            {
                final String inputTag = StringUtils.join(args, " ", 1, args.length);
                final String outputTag = Utilities.colorize(StringUtils.replaceEachRepeatedly(StringUtils.strip(inputTag),
                        new String[]
                        {
                            "" + ChatColor.COLOR_CHAR, "&k"
                        },
                        new String[]
                        {
                            "", ""
                        })) + ChatColor.RESET;

                if (!AdminList.isSuperAdmin(sender))
                {
                    final String rawTag = ChatColor.stripColor(outputTag).toLowerCase();

                    if (rawTag.length() > 20)
                    {
                        playerMsg("That tag is too long (Max is 20 characters).");
                        return true;
                    }

                    for (String word : FORBIDDEN_WORDS)
                    {
                        if (rawTag.contains(word))
                        {
                            {
                                if(word.equals("developer") && Utilities.DEVELOPERS.contains(sender_p.getName())) {
                                    PlayerData.getPlayerData(sender_p).setTag(outputTag);
                                    playerMsg("Tag set to '" + outputTag + ChatColor.GRAY + "'.");
                                } else
                                    playerMsg("That tag contains a forbidden word.");
                                return true;
                            }
                        }
                    }
                }

                PlayerData.getPlayerData(sender_p).setTag(outputTag);
                playerMsg("Tag set to '" + outputTag + ChatColor.GRAY + "'.");

                return true;
            }
            else if ("-s".equalsIgnoreCase(args[0]) && "set".equalsIgnoreCase(args[1]))
            {
                final String inputTag = StringUtils.join(args, " ", 2, args.length);
                final String outputTag = Utilities.colorize(StringUtils.replaceEachRepeatedly(StringUtils.strip(inputTag),
                        new String[]
                                {
                                        "" + ChatColor.COLOR_CHAR, "&k"
                                },
                        new String[]
                                {
                                        "", ""
                                })) + ChatColor.RESET;

                if (!AdminList.isSuperAdmin(sender))
                {
                    final String rawTag = ChatColor.stripColor(outputTag).toLowerCase();

                    if (rawTag.length() > 20)
                    {
                        playerMsg("That tag is too long (Max is 20 characters).");
                        return true;
                    }

                    for (String word : FORBIDDEN_WORDS)
                    {
                        if (rawTag.contains(word))
                        {
                            if(word.equals("developer") && Utilities.DEVELOPERS.contains(sender_p.getName())) {
                                PlayerData.getPlayerData(sender_p).setTag(outputTag);
                                TagConfiguration.saveTag(sender_p.getUniqueId().toString(), outputTag);
                                playerMsg("Tag set to '" + outputTag + ChatColor.GRAY + "'. (saved)");
                            } else
                                playerMsg("That tag contains a forbidden word.");
                                return true;
                        }
                    }
                }

                PlayerData.getPlayerData(sender_p).setTag(outputTag);
                TagConfiguration.saveTag(sender_p.getUniqueId().toString(), outputTag);
                playerMsg("Tag set to '" + outputTag + ChatColor.GRAY + "'. (saved)");
                return true;
            } else
            {
                return false;
            }
        }
        else
        {
            return false;
        }
    }
}
