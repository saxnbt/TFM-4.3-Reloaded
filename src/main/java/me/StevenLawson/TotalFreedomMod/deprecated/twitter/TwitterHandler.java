package me.StevenLawson.TotalFreedomMod.deprecated.twitter;

import me.StevenLawson.TotalFreedomMod.Log;
import me.StevenLawson.TotalFreedomMod.config.ConfigurationEntry;
import me.StevenLawson.TotalFreedomMod.util.Utilities;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

@Deprecated
public class TwitterHandler
{
    private TwitterHandler()
    {
        throw new AssertionError();
    }

    public static String getTwitter(String player)
    {
        return request("action=gettwitter&player=" + player);
    }

    public static String setTwitter(String player, String twitter)
    {
        if (twitter.startsWith("@"))
        {
            twitter = twitter.replaceAll("@", "");
        }
        return request("action=settwitter&player=" + player + "&twitter=" + twitter);
    }

    public static String delTwitter(String player)
    {
        return request("action=deltwitter&player=" + player);
    }

    public static void delTwitterVerbose(String targetName, CommandSender sender)
    {
        final String reply = delTwitter(targetName);
        if ("ok".equals(reply))
        {
            Utilities.adminAction(sender.getName(), "Removing " + targetName + " from TwitterBot", true);
        }
        else if ("disabled".equals(reply))
        {
            Utilities.playerMsg(sender, "Warning: Could not check if player has a twitter handle!");
            Utilities.playerMsg(sender, "TwitterBot has been temporarily disabled, please wait until it gets re-enabled", ChatColor.RED);
        }
        else if ("failed".equals(reply))
        {
            Utilities.playerMsg(sender, "Warning: Could not check if player has a twitter handle!");
            Utilities.playerMsg(sender, "There was a problem querying the database, please let a developer know.", ChatColor.RED);
        }
        else if ("false".equals(reply))
        {
            Utilities.playerMsg(sender, "Warning: Could not check if player has a twitter handle!");
            Utilities.playerMsg(sender, "There was a problem with the database, please let a developer know.", ChatColor.RED);
        }
        else if ("cannotauth".equals(reply))
        {
            Utilities.playerMsg(sender, "Warning: Could not check if player has a twitter handle!");
            Utilities.playerMsg(sender, "The database password is incorrect, please let a developer know.", ChatColor.RED);
        }
        else if ("notfound".equals(reply))
        {
            Utilities.playerMsg(sender, targetName + " did not have a twitter handle registered to their name.", ChatColor.GREEN);
        }
    }

    public static String isEnabled()
    {
        return request("action=getstatus");
    }

    public static String setEnabled(String status)
    {
        return request("action=setstatus&status=" + status);
    }

    private static String request(String queryString)
    {
        String line = "failed";

        final String twitterbotURL = ConfigurationEntry.TWITTERBOT_URL.getString();
        final String twitterbotSecret = ConfigurationEntry.TWITTERBOT_SECRET.getString();

        if (twitterbotURL != null && twitterbotSecret != null && !twitterbotURL.isEmpty() && !twitterbotSecret.isEmpty())
        {
            try
            {
                URL getUrl = new URL(twitterbotURL + "?auth=" + twitterbotSecret + "&" + queryString);
                URLConnection urlConnection = getUrl.openConnection();
                // Read the response
                BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                line = in.readLine();
                in.close();
            }
            catch (Exception ex)
            {
                Log.severe(ex);
            }
        }

        return line;
    }
}
