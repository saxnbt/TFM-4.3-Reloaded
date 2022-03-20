package me.StevenLawson.TotalFreedomMod.httpd;

import me.StevenLawson.TotalFreedomMod.TotalFreedomMod;
import me.StevenLawson.TotalFreedomMod.admin.AdminList;
import me.StevenLawson.TotalFreedomMod.config.ConfigurationEntry;
import me.StevenLawson.TotalFreedomMod.config.MainConfig;
import me.StevenLawson.TotalFreedomMod.util.Utilities;
import org.bukkit.entity.Player;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.UUID;

public class HTTPDPlayersModule extends HTTPDModule {
    public HTTPDPlayersModule(NanoHTTPD.HTTPSession session) {
        super(session);
    }

    @Override
    @SuppressWarnings("unchecked")
    public NanoHTTPD.Response getResponse() {
        final JSONObject responseObject = new JSONObject();

        final JSONArray players = new JSONArray();
        final JSONArray superadmins = new JSONArray();
        final JSONArray telnetadmins = new JSONArray();
        final JSONArray senioradmins = new JSONArray();
        final JSONArray developers = new JSONArray();

        // All online players
        for (Player player : TotalFreedomMod.server.getOnlinePlayers())
        {
            players.add(player.getName());
        }

        // Super admins (non-telnet and non-senior)
        for (UUID superadmin : AdminList.getSuperUUIDs())
        {
            if (AdminList.getSeniorUUIDs().contains(superadmin))
            {
                continue;
            }

            if (AdminList.getTelnetUUIDs().contains(superadmin))
            {
                continue;
            }

            superadmins.add(getName(superadmin));
        }

        // Telnet admins (non-senior)
        for (UUID telnetadmin : AdminList.getTelnetUUIDs())
        {
            if (AdminList.getSeniorUUIDs().contains(telnetadmin))
            {
                continue;
            }
            telnetadmins.add(getName(telnetadmin));
        }

        // Senior admins
        for (UUID senioradmin : AdminList.getSeniorUUIDs())
        {
            senioradmins.add(getName(senioradmin));
        }

        // Developers
        developers.addAll(Utilities.DEVELOPERS);

        responseObject.put("players", players);
        responseObject.put("superadmins", superadmins);
        responseObject.put("telnetadmins", telnetadmins);
        responseObject.put("senioradmins", senioradmins);
        responseObject.put("developers", developers);

        final NanoHTTPD.Response response = new NanoHTTPD.Response(NanoHTTPD.Response.Status.OK, NanoHTTPD.MIME_JSON, responseObject.toString());
        response.addHeader("Access-Control-Allow-Origin", MainConfig.getString(ConfigurationEntry.HTTPD_ORIGIN));
        return response;
    }

    private String getName(UUID uuid)
    {
        return AdminList.getEntry(uuid).getLastLoginName();
    }
}
