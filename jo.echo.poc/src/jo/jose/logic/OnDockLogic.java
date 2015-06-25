package jo.jose.logic;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jo.d4w.web.data.DockCargoBean;
import jo.d4w.web.data.OnDockBean;
import jo.d4w.web.logic.URILogic;
import jo.echo.util.BaseServlet;
import jo.jose.JoseServlet;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class OnDockLogic
{
    public static OnDockBean queryOnDock(JSONObject user) throws IOException
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
        return (OnDockBean)URILogic.getFromURI(subURI);
    }
    
    public static List<DockCargoBean> getLots(OnDockBean ondock)
    {
        return ondock.getCargo();
    }
    
    public static List<DockCargoBean> getFilteredLots(OnDockBean ondock, JSONObject user)
    {
        long money = UserLogic.getMoney(user);
        int space = UserLogic.getHoldSpace(user);
        List<DockCargoBean> filter = new ArrayList<DockCargoBean>();
        Set<String> taken = new HashSet<String>();
        for (Object o : (JSONArray)user.get("hold"))
            taken.add((String)o);
        for (DockCargoBean lot : getLots(ondock))
        {
            if (taken.contains(lot.getURI()))
                continue;
            int size = OnDockLogic.getSize(lot);
            if (size > space)
                continue;
            long purchasePrice = OnDockLogic.getPurchasePrice(lot);
            if (purchasePrice > money)
                continue;
            filter.add(lot);
        }
        return filter;
    }
    
    public static String getName(DockCargoBean lot)
    {
        return lot.getName();
    }
    
    public static int getSize(DockCargoBean lot)
    {
        return lot.getSize();
    }
    
    public static long getPurchasePrice(DockCargoBean lot)
    {
        return lot.getPurchasePrice();
    }

    public static DockCargoBean findLot(List<DockCargoBean> lots, String lotName)
    {
        BaseServlet.log(JoseServlet.class, "Looking for '"+lotName+"'");
        for (DockCargoBean lot : lots)
        {
            String name = getName(lot);
            BaseServlet.log(JoseServlet.class, "Comparing to '"+name+"'");
            if (name.equalsIgnoreCase(lotName))
                return lot;
        }
        BaseServlet.log(JoseServlet.class, "None found");
        return null;
    }

    public static long getSalePrice(DockCargoBean lot)
    {
        return lot.getSalePrice();
    }
    
    public static String price(DockCargoBean lot)
    {
        return "You can sell "+lot.getSize()+" tons of "+lot.getName()+" for "+lot.getSalePrice()+". ";
    }
}
