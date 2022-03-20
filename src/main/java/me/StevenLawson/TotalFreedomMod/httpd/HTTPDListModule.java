package me.StevenLawson.TotalFreedomMod.httpd;

import me.StevenLawson.TotalFreedomMod.admin.AdminList;
import me.StevenLawson.TotalFreedomMod.util.Utilities;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Collection;

public class HTTPDListModule extends HTTPDModule {
    public HTTPDListModule(NanoHTTPD.HTTPSession session) {
        super(session);
    }

    @Override
    public String getBody() {
        final StringBuilder body = new StringBuilder();

        final Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();

        body.append("<p>There are ").append(onlinePlayers.size()).append("/").append(Bukkit.getMaxPlayers()).append(" players online:</p>\r\n");

        body.append("<ul>\r\n");

        for (Player player : onlinePlayers)
        {
            String prefix = "";
            if (AdminList.isSuperAdmin(player))
            {
                if (AdminList.isSeniorAdmin(player))
                {
                    prefix = "[SrA]";
                }
                else
                {
                    prefix = "[SA]";
                }

                if (Utilities.DEVELOPERS.contains(player.getName()))
                {
                    prefix = "[Dev]";
                }

                if (player.getName().equals("markbyron"))
                {
                    prefix = "[Owner]";
                }
            }
            else
            {
                if (player.isOp())
                {
                    prefix = "[OP]";
                }
            }

            body.append("<li>").append(prefix).append(player.getName()).append("</li>\r\n");
        }

        body.append("</ul>\r\n");

        return body.toString();
    }

    @Override
    public String getTitle()
    {
        return "Total Freedom - Online Users";
    }
}
