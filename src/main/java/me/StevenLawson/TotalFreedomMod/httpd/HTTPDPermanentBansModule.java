package me.StevenLawson.TotalFreedomMod.httpd;

import me.StevenLawson.TotalFreedomMod.TotalFreedomMod;

import java.io.File;

public class HTTPDPermanentBansModule extends HTTPDModule {
    public HTTPDPermanentBansModule(NanoHTTPD.HTTPSession session) {
        super(session);
    }

    @Override
    public NanoHTTPD.Response getResponse() {
        File permbanFile = new File(TotalFreedomMod.plugin.getDataFolder(), TotalFreedomMod.PERMBAN_FILENAME);
        if (permbanFile.exists()) {
            return HTTPDManager.serveFileBasic(new File(TotalFreedomMod.plugin.getDataFolder(), TotalFreedomMod.PERMBAN_FILENAME));
        }
        else
        {
            return new NanoHTTPD.Response(NanoHTTPD.Response.Status.NOT_FOUND, NanoHTTPD.MIME_PLAINTEXT,
                    "Error 404: Not Found - The requested resource was not found on this server.");
        }
    }
}
