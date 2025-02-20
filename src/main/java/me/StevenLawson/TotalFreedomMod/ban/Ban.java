package me.StevenLawson.TotalFreedomMod.ban;

import me.StevenLawson.TotalFreedomMod.config.ConfigurationEntry;
import me.StevenLawson.TotalFreedomMod.util.Utilities;
import org.bukkit.ChatColor;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Ban
{
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
    public static final Pattern IP_BAN_REGEX;
    public static final Pattern UUID_BAN_REGEX;

    static
    {
        // 192.168.1.254:LocalHost:Prozza:0:none
        // 127.0.*.*:TestUserName:BannedByNotch:123567:Test reason
        IP_BAN_REGEX = Pattern.compile(
                "^((?:(?:\\*|(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?))\\.){3}(?:\\*|(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)))"
                + ":([\\w\\s]+)"
                + ":([\\w]+)"
                + ":(\\d+)"
                + ":([\\s\\S]+)$");
        // 245d2f30-61fb-4840-9cd3-298b3920f4a4:Cobrex:Prozza:0:Example reason
        UUID_BAN_REGEX = Pattern.compile(
                "^([0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12})"
                + ":([\\w\\s]+)"
                + ":([\\w]+)"
                + ":(\\d+)"
                + ":([\\s\\S]+)$");
    }
    private final BanType type;
    private final boolean complete;
    private String subject; // uuid or IP
    private String lastLoginName;
    private String by;
    private long expireUnix;
    private String reason;

    public Ban(String ip, String lastLoginName)
    {
        this(ip, lastLoginName, null, null, null);
    }

    public Ban(String ip, String lastLoginName, String sender, Date expire, String reason)
    {
        this(ip, lastLoginName, sender, expire, reason, BanType.IP);
    }

    public Ban(UUID uuid, String lastLoginName)
    {
        this(uuid, lastLoginName, null, null, null);
    }

    public Ban(UUID uuid, String lastLoginName, String sender, Date expire, String reason)
    {
        this(uuid.toString(), lastLoginName, sender, expire, reason, BanType.UUID);
    }

    private Ban(String subject, String lastLoginName, String sender, Date expire, String reason, BanType type)
    {
        this.type = type;
        this.subject = subject;
        this.lastLoginName = (lastLoginName == null ? "none" : lastLoginName);
        this.by = (sender == null ? "none" : sender);
        this.expireUnix = (expire == null ? 0 : Utilities.getUnixTime(expire));
        this.reason = (reason == null ? "none" : reason);
        complete = true;
    }

    public Ban(String banString, BanType type)
    {
        this.type = type;

        final Matcher matcher = (type == BanType.IP ? IP_BAN_REGEX.matcher(banString) : UUID_BAN_REGEX.matcher(banString));

        if (!matcher.find())
        {
            complete = false;
            return;
        }

        subject = matcher.group(1);
        lastLoginName = matcher.group(2);
        by = matcher.group(3);
        expireUnix = Long.valueOf(matcher.group(4));
        reason = Utilities.colorize(matcher.group(5));
        complete = true;
    }

    public enum BanType
    {
        IP,
        UUID
    }

    public BanType getType()
    {
        return type;
    }

    public String getSubject()
    {
        return subject;
    }

    public String getLastLoginName()
    {
        return lastLoginName;
    }

    public String getBannedBy()
    {
        return by;
    }

    public long getExpireUnix()
    {
        return expireUnix;
    }

    public String getReason()
    {
        return reason;
    }

    public boolean isExpired()
    {
        return expireUnix != 0 && expireUnix < Utilities.getUnixTime();
    }

    public boolean isComplete()
    {
        return complete;
    }

    public String getKickMessage()
    {
        final StringBuilder message = new StringBuilder("You");

        message.append(type == BanType.IP ? "r IP address is" : " are").append(" temporarily banned from this server.");
        message.append("\nAppeal at ").append(ChatColor.GOLD).append(ConfigurationEntry.SERVER_BAN_URL.getString());

        if (!reason.equals("none"))
        {
            message.append("\nReason: ").append(reason);
        }

        if (getExpireUnix() != 0)
        {
            message.append("\nYour ban will be removed on ").append(DATE_FORMAT.format(Utilities.getUnixDate(expireUnix)));
        }

        return message.toString();
    }

    // subject:lastLoginName:bannedBy:expireUnix:reason
    @Override
    public String toString()
    {
        return subject + ":" + lastLoginName + ":" + by + ":" + expireUnix + ":" + Utilities.decolorize(reason);
    }

    @Override
    public boolean equals(Object object)
    {
        if (object == null)
        {
            return false;
        }

        if (!(object instanceof Ban))
        {
            return false;
        }

        final Ban ban = (Ban) object;

        if (toString().equals(ban.toString()))
        {
            return true;
        }

        if (getType() != ban.getType())
        {
            return false;
        }

        return getSubject().equals(ban.getSubject());
    }

    @Override
    public int hashCode()
    {
        final int prime = 37;
        int result = 1;
        result = prime * result + getType().hashCode();
        result = prime * result + getSubject().hashCode();
        return result;
    }
}
