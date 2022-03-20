package me.StevenLawson.TotalFreedomMod.player;

import com.google.common.collect.ImmutableList;
import me.StevenLawson.TotalFreedomMod.admin.Admin;
import me.StevenLawson.TotalFreedomMod.admin.AdminList;
import me.StevenLawson.TotalFreedomMod.config.ConfigurationEntry;
import me.StevenLawson.TotalFreedomMod.util.Utilities;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static me.StevenLawson.TotalFreedomMod.util.Utilities.DEVELOPERS;
import static me.StevenLawson.TotalFreedomMod.util.Utilities.getPluginFile;

public enum PlayerRank implements Comparator<PlayerRank>, Comparable<PlayerRank>
{
    IMPOSTOR("an " + ChatColor.YELLOW + ChatColor.UNDERLINE + "Impostor", ChatColor.YELLOW.toString() + ChatColor.UNDERLINE + "[IMP]", "Impostors", 0),
    NON_OP("a " + ChatColor.GREEN + "Non-OP", ChatColor.GREEN.toString(), "Non-Operators", 1),
    OP("an " + ChatColor.RED + "OP", ChatColor.RED + "[OP]", "Operators" ,2),
    SUPER("a " + ChatColor.GOLD + "Super Admin", ChatColor.GOLD + "[SA]", "Super Admins", 3),
    TELNET("a " + ChatColor.DARK_GREEN + "Super Telnet Admin", ChatColor.DARK_GREEN + "[STA]", "Super Telnet Admins", 4),
    SENIOR("a " + ChatColor.LIGHT_PURPLE + "Senior Admin", ChatColor.LIGHT_PURPLE + "[SrA]", "Senior Admins", 5),
    DEVELOPER("a " + ChatColor.DARK_PURPLE + "Developer", ChatColor.DARK_PURPLE + "[Dev]", "Developers", 6),
    OWNER("the " + ChatColor.BLUE + "Owner", ChatColor.BLUE + "[Owner]", "Owners", 7),
    CONSOLE("The " + ChatColor.DARK_PURPLE + "Console", ChatColor.DARK_PURPLE + "[Console]", 8);
    private final String loginMessage;
    private final String prefix;

    private final String plural;
    private final int ordinal;


    PlayerRank(String loginMessage, String prefix, int ordinal) {
        this.loginMessage = loginMessage;
        this.prefix = prefix;
        this.plural = "";
        this.ordinal = ordinal;
    }

    PlayerRank(String loginMessage, String prefix, String plural, int ordinal)
    {
        this.loginMessage = loginMessage;
        this.prefix = prefix;
        this.plural = plural;
        this.ordinal = ordinal;
    }

    public static String getLoginMessage(CommandSender sender)
    {
        // Handle console
        if (!(sender instanceof Player))
        {
            return fromSender(sender).getLoginMessage();
        }

        // Handle admins
        final Admin entry = AdminList.getEntry((Player) sender);
        if (entry == null)
        {
            // Player is not an admin
            return fromSender(sender).getLoginMessage();
        }

        // Custom login message
        final String loginMessage = entry.getCustomLoginMessage();

        if (loginMessage == null || loginMessage.isEmpty())
        {
            return fromSender(sender).getLoginMessage();
        }

        return ChatColor.translateAlternateColorCodes('&', loginMessage);
    }

    public static PlayerRank fromSender(CommandSender sender)
    {
        if (!(sender instanceof Player))
        {
            return CONSOLE;
        }

        if (AdminList.isAdminImpostor((Player) sender))
        {
            return IMPOSTOR;
        }

        if (DEVELOPERS.contains(sender.getName()))
        {
            return DEVELOPER;
        }

        final Admin entry = AdminList.getEntryByIp(Utilities.getIp((Player) sender));

        final PlayerRank rank;

        if (entry != null && entry.isActivated())
        {
            if (ConfigurationEntry.SERVER_OWNERS.getList().contains(sender.getName()))
            {
                return OWNER;
            }

            if (entry.isSeniorAdmin())
            {
                rank = SENIOR;
            }
            else if (entry.isTelnetAdmin())
            {
                rank = TELNET;
            }
            else
            {
                rank = SUPER;
            }
        }
        else
        {
            if (sender.isOp())
            {
                rank = OP;
            }
            else
            {
                rank = NON_OP;
            }

        }
        return rank;
    }

    public String getPrefix()
    {
        return prefix;
    }

    public String getLoginMessage()
    {
        return loginMessage;
    }


    public String getPlural() {
        return plural;
    }

    public int getOrdinal() {
        return ordinal;
    }

    public List<Player> getWithRank() {
        List<Player> inGame = new ArrayList<>();

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if(fromSender(onlinePlayer).equals(this)) inGame.add(onlinePlayer);
        }

        return ImmutableList.copyOf(inGame);
    }

    public List<String> getInGameUsernames() {
        List<String> inGame = new ArrayList<>();

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if(fromSender(onlinePlayer).equals(this)) inGame.add(onlinePlayer.getName());
        }

        return ImmutableList.copyOf(inGame);

    }

    @Override
    public int compare(PlayerRank o1, PlayerRank o2) {
        return o1.ordinal - o2.ordinal;
    }
}
