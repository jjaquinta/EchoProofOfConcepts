package jo.jose.logic;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class PortsLogic
{
    public static JSONObject getNearbyPorts(String portURI, int radius) throws IOException
    {
        URI u;
        try
        {
            u = new URI(portURI);
        }
        catch (URISyntaxException e)
        {
            throw new IOException(e);
        }
        String subURI = "ports://" + u.getAuthority()+"/"+radius*3.26;
        return LookupLogic.query(subURI);
    }

    public static JSONObject findPort(JSONObject ports, String locationName)
    {
        JSONArray ps = getPorts(ports);
        for (Object op : ps)
        {
            JSONObject p = (JSONObject)op;
            if (locationName.equalsIgnoreCase((String)p.get("name")))
                return p;
        }
        return null;
    }

    public static JSONArray getPorts(JSONObject ports)
    {
        return (JSONArray)ports.get("ports");
    }
}
