package jo.jose.logic;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class OnDockLogic
{
    public static JSONObject queryOnDock(JSONObject user) throws IOException
    {
        String locationURI = UserLogic.getLocation(user);
        URI u;
        try
        {
            u = new URI(locationURI);
        }
        catch (URISyntaxException e)
        {
            throw new IOException(e);
        }
        int year = UserLogic.getYear(user);
        int day = UserLogic.getDay(user);
        String subURI = "ondock://" + u.getAuthority()+u.getPath()+"?date="+year+"-"+day;
        return LookupLogic.query(subURI);
    }
    
    public static JSONArray getLots(JSONObject ondock)
    {
        return (JSONArray)ondock.get("cargo");
    }
    
    @SuppressWarnings("unchecked")
    public static JSONArray getFilteredLots(JSONObject ondock, JSONObject user)
    {
        int money = UserLogic.getMoney(user);
        int space = UserLogic.getHoldSpace(user);
        JSONArray filter = new JSONArray();
        for (Object o : getLots(ondock))
        {
            JSONObject lot = (JSONObject)o;
            int size = OnDockLogic.getSize(lot);
            if (size > space)
                continue;
            int purchasePrice = OnDockLogic.getPurchasePrice(lot);
            if (purchasePrice > money)
                continue;
            // TODO: check if in hold
            filter.add(lot);
        }
        return filter;
    }
    
    public static String getName(JSONObject lot)
    {
        return (String)lot.get("name");
    }
    
    public static int getSize(JSONObject lot)
    {
        return ((Number)lot.get("size")).intValue();
    }
    
    public static int getPurchasePrice(JSONObject lot)
    {
        return ((Number)lot.get("purchasePrice")).intValue();
    }

    public static JSONObject findLot(JSONArray lots, String lotName)
    {
        for (Object o : lots)
        {
            JSONObject lot = (JSONObject)o;
            String name = getName(lot);
            if (name.equals(lotName))
                return lot;
        }
        return null;
    }
}
