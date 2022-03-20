package me.StevenLawson.TotalFreedomMod.httpd;

import me.StevenLawson.TotalFreedomMod.config.ConfigurationEntry;

import java.io.File;

public class LogFileModule extends HTTPDFileModule {
    public LogFileModule(NanoHTTPD.HTTPSession session) {
        super(session);
    }

    @Override
    public NanoHTTPD.Response getResponse() {
        if (ConfigurationEntry.LOGS_SECRET.getString().equals(params.get("password")))
        {
            return serveFile("latest.log", params, new File("./logs"));
        }
        else
        {
            return new NanoHTTPD.Response(NanoHTTPD.Response.Status.FORBIDDEN, NanoHTTPD.MIME_PLAINTEXT, "Incorrect password.");
        }
    }
}
