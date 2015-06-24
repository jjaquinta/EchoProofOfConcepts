package jo.jose.logic;

import java.io.IOException;

import jo.echo.util.BaseServlet;
import jo.jose.JoseServlet;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class UserLogic
{
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
    
    public static int getMoney(JSONObject user)
    {
        return ((Number)user.get("money")).intValue();
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
    
    public static JSONObject getUser(String id) throws IOException
    {
        JSONObject user = LookupLogic.query("jose://"+id);
        return user;
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
        user.put("location", "port://2010712@071200010000/Havelock");
        user.put("year", 1100);
        user.put("day", 1);
        user.put("quest", "quest:///");
        LookupLogic.store("jose://"+id, user);
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
        LookupLogic.store("jose://"+UserLogic.getName(user), user);
    }
}
