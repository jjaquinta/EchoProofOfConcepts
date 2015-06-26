package jo.jose.logic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import jo.d4w.data.PopulatedObjectBean;
import jo.d4w.data.TradeGood;
import jo.d4w.logic.CargoLogic;
import jo.d4w.logic.D4WPopulationLogic;
import jo.d4w.logic.TradeGoodLogic;
import jo.d4w.web.data.DockCargoBean;
import jo.d4w.web.data.OnDockBean;
import jo.d4w.web.logic.URILogic;
import jo.echo.util.BaseServlet;
import jo.echo.util.EnumerationUtils;
import jo.jose.JoseServlet;
import jo.util.html.URIBuilder;
import jo.util.utils.obj.LongUtils;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class OnDockLogic
{
    public static OnDockBean queryOnDock(JSONObject user) throws IOException
    {
        String locationURI = UserLogic.getLocation(user);
        int year = UserLogic.getYear(user);
        int day = UserLogic.getDay(user);
        URIBuilder u = new URIBuilder(locationURI);
        u.setScheme("ondock");
        u.setQuery("date", year+"-"+day);
        JoseServlet.log(JoseServlet.class, "Looking up "+u.toString());
        
        String popURI = "pop://"+u.getAuthority() + u.getPath();
        PopulatedObjectBean port = D4WPopulationLogic.getByURI(popURI);
        JoseServlet.log(JoseServlet.class, "  port.oid "+port.getOID());
        String date = u.getQuery("date");
        if (date == null)
            date = "0";
        int o = date.indexOf('-');
        long endDate;
        if (o >= 0)
            endDate = LongUtils.parseLong(date.substring(0, o))*365 + LongUtils.parseLong(date.substring(o + 1));
        else
            endDate = LongUtils.parseLong(date);
        for (long startDate = endDate - CargoLogic.MAX_CARGO_AVAILABLE; startDate <= endDate; startDate++)
        {
            long seed = port.getOID()^startDate;
            JoseServlet.log(JoseServlet.class, "  seed "+seed);
            Random rnd = new Random(seed);
            Map<TradeGood, Double> weights = new HashMap<TradeGood, Double>();
            TradeGoodLogic.getWeightedGoods(port, weights);
            double totalWeight = 0;
            for (Double weight : weights.values())
                totalWeight += weight;
            
            int q = (int)Math.ceil(Math.log10(port.getPopulation())*(rnd.nextGaussian()/2 + 1));
            while (q-- > 0)
                JoseServlet.log(JoseServlet.class, "    "+rnd.nextLong()+", "+totalWeight);
        }
        
        OnDockBean ondock = (OnDockBean)URILogic.getFromURI(u.toString());
        StringBuffer sb = new StringBuffer();
        for (DockCargoBean cargo : ondock.getCargo())
            sb.append(" "+cargo.getName()+" $"+cargo.getPurchasePrice());
        JoseServlet.log(JoseServlet.class, sb.toString());
        return ondock;
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
        Integer idx = EnumerationUtils.getEnumeration(lotName);
        if (idx != null)
            if (idx <= lots.size())
                return lots.get(idx - 1);
            else
                return null;
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
