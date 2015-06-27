package jo.jose.logic;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import jo.d4w.web.data.DockCargoBean;
import jo.d4w.web.logic.URILogic;
import jo.echo.util.BaseServlet;
import jo.jose.JoseServlet;
import jo.util.html.URIBuilder;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class UserLogic
{
    private static JSONParser   mParser = new JSONParser();
    
    public static String getLocationName(JSONObject user)
    {
        String location = (String)user.get("location");
        int o = location.lastIndexOf('/');
        String locationName = location.substring(o + 1);
        return locationName;
    }
    
    public static String getLocation(JSONObject user)
    {
        return (String)user.get("location");
    }
    
    public static int getHoldSpace(JSONObject user)
    {
        return ((Number)user.get("totalHoldSpace")).intValue();
    }
    
    public static void setHoldSpace(JSONObject user, int space)
    {
        user.put("totalHoldSpace", space);
    }
    
    public static long getMoney(JSONObject user)
    {
        return ((Number)user.get("money")).intValue();
    }
    
    public static void setMoney(JSONObject user, long money)
    {
        user.put("money", money);
    }
    
    public static int getYear(JSONObject user)
    {
        return ((Number)user.get("year")).intValue();
    }
    
    public static int getDay(JSONObject user)
    {
        return ((Number)user.get("day")).intValue();
    }
    
    public static int getJump(JSONObject user)
    {
        return ((Number)user.get("jump")).intValue();
    }
    
    public static String getName(JSONObject user)
    {
        return (String)user.get("name");
    }
    
    public static JSONObject getUser(String id)
    {
        String subURI = "store://jose@"+id;
        BaseServlet.log(JoseServlet.class, "querying "+subURI);
        String data = (String)URILogic.getFromURI(subURI);
        BaseServlet.log(JoseServlet.class, "got "+data);
        if (data == null)
        {
            return null;
        }
        else
        {
            JSONObject user;
            try
            {
                user = (JSONObject)mParser.parse(data);
            }
            catch (ParseException e)
            {
                return null;
            }
            return user;
        }
    }
    
    private static void setUser(JSONObject user)
    {
        String subURI = "store://jose@"+UserLogic.getName(user);
        try
        {
            subURI += "?data="+URLEncoder.encode(user.toJSONString(), "utf-8");
        }
        catch (UnsupportedEncodingException e)
        {
            throw new IllegalStateException(e);
        }
        BaseServlet.log(JoseServlet.class, "Storing user "+subURI);
        String resp = (String)URILogic.getFromURI(subURI);
        BaseServlet.log(JoseServlet.class, "  stored "+resp);
    }

    public static JSONObject doNewUser(String id) throws IOException
    {
        BaseServlet.log(JoseServlet.class, "Creating new user");
        JSONObject user;
        user = new JSONObject();
        user.put("name", id);
        user.put("password", "echo");
        user.put("totalHoldSpace", 100);
        user.put("jump", 1);
        user.put("man", 1);
        user.put("hold", new JSONArray());
        user.put("money", 10000);
        user.put("location", "port://11101070f@070f00010001/Bedale");
        user.put("year", 1100);
        user.put("day", 1);
        user.put("quest", "quest:///");
        setUser(user);
        return user;
    }

    public static void setLocation(JSONObject user, String loc) throws IOException
    {
        user.put("location", loc);
        int day = getDay(user);
        day += 7;
        if (day > 365)
        {
            int year = getYear(user);
            year++;
            user.put("year", year);
            day -= 365;
        }
        user.put("day", day);
        setUser(user);
    }

    @SuppressWarnings("unchecked")
    public static String buy(JSONObject user, DockCargoBean lot, boolean verbose)
    {
        long money = getMoney(user);
        int space = getHoldSpace(user);
        long price = OnDockLogic.getPurchasePrice(lot);
        int size = OnDockLogic.getSize(lot);
        if (price > money)
        {
            if (verbose)
                return OnDockLogic.getName(lot)+" costs "+price+" talents but you only have "+money+". ";
            else
                return null;
        }
        if (size > space)
        {
            if (verbose)
                return OnDockLogic.getName(lot)+" takes up "+size+" tons of space, but you only have "+space+". ";
            else
                return null;
        }
        money -= price;
        space -= size;
        JSONArray hold = (JSONArray)user.get("hold");
        hold.add(lot.getURI());
        setMoney(user, money);
        setHoldSpace(user, space);
        setUser(user);
        if (verbose)
            return "You bought "+size+" tons of "+OnDockLogic.getName(lot)+" for "+price+" talents. ";
        else
            return size+" tons of "+OnDockLogic.getName(lot)+" for "+price;
    }

    public static boolean isAnythingInHold(JSONObject user)
    {
        JSONArray jhold = (JSONArray)user.get("hold");
        return ((jhold != null) && (jhold.size() >  0));
    }
    
    public static List<DockCargoBean> getInHold(JSONObject user)
    {
        String location = getLocation(user);
        List<DockCargoBean> hold = new ArrayList<DockCargoBean>();
        JSONArray jhold = (JSONArray)user.get("hold");
        for (Object o : jhold)
        {
            URIBuilder uri = new URIBuilder((String)o);
            uri.setQuery("at", location);
            DockCargoBean lot = (DockCargoBean)URILogic.getFromURI(uri.toString());
            if (lot != null)
                hold.add(lot);
        }
        return hold;
    }

    public static String sell(JSONObject user, DockCargoBean lot)
    {
        long money = getMoney(user);
        int space = getHoldSpace(user);
        long price = OnDockLogic.getSalePrice(lot);
        int size = OnDockLogic.getSize(lot);
        money += price;
        space += size;
        JSONArray hold = (JSONArray)user.get("hold");
        boolean foundIt = false;
        URIBuilder lookingFor = new URIBuilder(lot.getURI());
        Properties p = lookingFor.getQuery();
        p.remove("at");
        lookingFor.setQuery(p);
        BaseServlet.log(JoseServlet.class, "Looking for '"+lot.getURI()+"'");
        for (int i = 0; i < hold.size(); i++)
        {
            String uri = (String)hold.get(i);
            URIBuilder found = new URIBuilder(uri);
            BaseServlet.log(JoseServlet.class, "  Checking '"+uri+"'");
            if (lookingFor.equals(found))
            {
                hold.remove(i);
                foundIt = true;
                break;
            }
        }
        if (!foundIt)
            return "Error: Can't find "+lot.getURI()+" for some reason!?!";
        setMoney(user, money);
        setHoldSpace(user, space);
        setUser(user);
        return "You sold "+size+" tons of "+OnDockLogic.getName(lot)+" for "+price+" talents. ";
    }

    public static void updateLastAccess(JSONObject user)
    {
        user.put("lastAccess", System.currentTimeMillis());
        setUser(user);
    }

    public static long getLastAccess(JSONObject user)
    {
        Long la = (Long)user.get("lastAccess");
        if (la == null)
            return 0;
        else
            return la;
    }
}
