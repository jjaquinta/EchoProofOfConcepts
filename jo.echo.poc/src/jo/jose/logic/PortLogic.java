package jo.jose.logic;

import java.io.IOException;

import org.json.simple.JSONObject;

public class PortLogic
{
    public static JSONObject lookup(String subURI) throws IOException
    {
        return LookupLogic.query(subURI);
    }

    public static String getName(JSONObject port)
    {
        return (String)port.get("name");
    }
    
    public static long getPopulation(JSONObject port)
    {
        JSONObject popStats = getPopStats(port);
        return ((Number)popStats.get("population")).longValue();
    }
    
    public static double getMaterialProductivity(JSONObject port)
    {
        JSONObject popStats = getPopStats(port);
        return ((Number)popStats.get("materialProductivity")).doubleValue();
    }
    
    public static double getAgriculturalProductivity(JSONObject port)
    {
        JSONObject popStats = getPopStats(port);
        return ((Number)popStats.get("agriculturalProductivity")).doubleValue();
    }
    
    public static double getEnergyProductivity(JSONObject port)
    {
        JSONObject popStats = getPopStats(port);
        return ((Number)popStats.get("energyProductivity")).doubleValue();
    }
    
    private static JSONObject getPopStats(JSONObject port)
    {
        return (JSONObject)port.get("popStats");
    }

    public static String getPopulationDescription(JSONObject port)
    {
        long pop = getPopulation(port);
        if (pop >= 1000000000)
            return (pop/1000000000)+" billion";
        if (pop >= 1000000)
            return (pop/1000000)+" million";
        if (pop >= 1000)
            return (pop/1000)+" thousand";
        return Long.toString(pop);
    }

    public static String getTechTierDescription(JSONObject port)
    {
        JSONObject popStats = getPopStats(port);
        return (String)popStats.get("techTierDesc");
    }

    public static String getProductionFocus(JSONObject port)
    {
        double material = getMaterialProductivity(port);
        double agricultural = getAgriculturalProductivity(port);
        double energy = getEnergyProductivity(port);
        if ((material > agricultural) && (material > energy))
            return "raw materials";
        if ((energy > agricultural) && (energy > material))
            return "energy production";
        if ((agricultural > energy) && (agricultural > material))
            return "agriculture";
        return "diverse resources";
    }

    public static String distanceDescription(JSONObject p1, JSONObject p2)
    {
        double x1 = ((Number)p1.get("x")).doubleValue();
        double y1 = ((Number)p1.get("y")).doubleValue();
        double z1 = ((Number)p1.get("z")).doubleValue();
        double x2 = ((Number)p2.get("x")).doubleValue();
        double y2 = ((Number)p2.get("y")).doubleValue();
        double z2 = ((Number)p2.get("z")).doubleValue();
        double d = Math.sqrt((x1 - x2)*(x1 - x2) + (y1 - y2)*(y1 - y2) + (z1 - z2)*(z1 - z2));
        d /= 3.26;
        String num = String.format("%.1f parsecs", d);
        return num;
    }

    public static String getID(JSONObject port)
    {
        return (String)port.get("id");
    }
}
