package me.StevenLawson.TotalFreedomMod.httpd;

import me.StevenLawson.TotalFreedomMod.Log;
import me.StevenLawson.TotalFreedomMod.httpd.NanoHTTPD.HTTPSession;
import me.StevenLawson.TotalFreedomMod.httpd.NanoHTTPD.Method;
import me.StevenLawson.TotalFreedomMod.httpd.NanoHTTPD.Response;

import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public abstract class HTTPDModule {
    protected final String uri;
    protected final Method method;
    protected final Map<String, String> headers;
    protected final Map<String, String> params;
    protected final Socket socket;
    protected final HTTPSession session;

    public HTTPDModule(HTTPSession session) {
        this.uri = session.getUri();
        this.method = session.getMethod();
        this.headers = session.getHeaders();
        this.params = session.getParms();
        this.socket = session.getSocket();
        this.session = session;
    }

    public String getBody()
    {
        return null;
    }

    public String getTitle()
    {
        return null;
    }

    public String getStyle()
    {
        return null;
    }

    public String getScript()
    {
        return null;
    }

    public Response getResponse()
    {
        return new HTTPDPageBuilder(getBody(), getTitle(), getStyle(), getScript()).getResponse();
    }

    protected final Map<String, String> getFiles()
    {
        Map<String, String> files = new HashMap<String, String>();

        try
        {
            session.parseBody(files);
        }
        catch (Exception ex)
        {
            Log.severe(ex);
        }

        return files;
    }
}
