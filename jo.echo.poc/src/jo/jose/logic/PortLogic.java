package jo.jose.logic;

import java.io.IOException;

import jo.d4w.data.PopulatedObjectBean;
import jo.d4w.web.data.PortBean;
import jo.d4w.web.logic.URILogic;

public class PortLogic
{
    public static PortBean lookup(String subURI) throws IOException
    {
        return (PortBean)URILogic.getFromURI(subURI);
    }

    public static String getName(PortBean port)
    {
        return port.getName();
    }
    
    public static long getPopulation(PortBean port)
    {
        PopulatedObjectBean popStats = getPopStats(port);
        return popStats.getPopulation();
    }
    
    public static double getMaterialProductivity(PortBean port)
    {
        PopulatedObjectBean popStats = getPopStats(port);
        return popStats.getMaterialProductivity();
    }
    
    public static double getAgriculturalProductivity(PortBean port)
    {
        PopulatedObjectBean popStats = getPopStats(port);
        return popStats.getAgriculturalProductivity();
    }
    
    public static double getEnergyProductivity(PortBean port)
    {
        PopulatedObjectBean popStats = getPopStats(port);
        return popStats.getEnergyProductivity();
    }
    
    private static PopulatedObjectBean getPopStats(PortBean port)
    {
        return port.getPopStats();
    }

    public static String getPopulationDescription(PortBean port)
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

    public static String getTechTierDescription(PortBean port)
    {
        PopulatedObjectBean popStats = getPopStats(port);
        return PopulatedObjectBean.TECH_DESCRIPTION[popStats.getTechTier()];
    }

    public static String getProductionFocus(PortBean port)
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

    public static String distanceDescription(PortBean p1, PortBean p2, boolean verbose)
    {
        double x1 = p1.getX();
        double y1 = p1.getY();
        double z1 = p1.getZ();
        double x2 = p2.getX();
        double y2 = p2.getY();
        double z2 = p2.getZ();
        double d = Math.sqrt((x1 - x2)*(x1 - x2) + (y1 - y2)*(y1 - y2) + (z1 - z2)*(z1 - z2));
        d /= 3.26;
        String num = String.format(verbose ? "%.1f parsecs" : "%.1f", d);
        return num;
    }

    public static String getID(PortBean port)
    {
        return port.getURI();
    }
}
