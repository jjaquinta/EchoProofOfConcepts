package jo.ups.logic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jo.ups.data.PackageBean;
import jo.ups.data.PackagesBean;

public class PackageLogic
{
    private final static List<PackagesBean>  mLists = new ArrayList<PackagesBean>();
    private final static Map<String, PackagesBean> mIndexByEchoID = new HashMap<String, PackagesBean>();
    private final static Map<String, PackagesBean> mIndexByWebID = new HashMap<String, PackagesBean>();
    private final static Map<String, PackagesBean> mIndexBySyncID = new HashMap<String, PackagesBean>();
    
    public static PackagesBean newInstance()
    {
        PackagesBean list = new PackagesBean();
        addPackages(list);
        return list;
    }

    public static void addPackages(PackagesBean list)
    {
        mLists.add(list);
        if (list.getEchoID() != null)
            mIndexByEchoID.put(list.getEchoID(), list);
        if (list.getWebID() != null)
            mIndexByWebID.put(list.getWebID(), list);
        if (list.getSyncID() != null)
            mIndexBySyncID.put(list.getSyncID(), list);
    }
    
    public static void removePackages(PackagesBean list)
    {
        mLists.remove(list);
        if (list.getEchoID() != null)
            mIndexByEchoID.remove(list.getEchoID());
        if (list.getWebID() != null)
            mIndexByWebID.remove(list.getWebID());
        if (list.getSyncID() != null)
            mIndexBySyncID.remove(list.getSyncID());
    }
    
    public static void updateEchoID(PackagesBean list, String echoID)
    {
        if (list.getEchoID() != null)
            mIndexByEchoID.remove(list.getEchoID());
        list.setEchoID(echoID);
        if (list.getEchoID() != null)
            mIndexByEchoID.put(list.getEchoID(), list);
    }
    
    public static void updateWebID(PackagesBean list, String webID)
    {
        if (list.getWebID() != null)
            mIndexByWebID.remove(list.getWebID());
        list.setWebID(webID);
        if (list.getWebID() != null)
            mIndexByWebID.put(list.getWebID(), list);
    }
    
    public static void updateSyncID(PackagesBean list, String syncID)
    {
        if (list.getSyncID() != null)
            mIndexBySyncID.remove(list.getSyncID());
        list.setSyncID(syncID);
        if (list.getSyncID() != null)
            mIndexBySyncID.put(list.getSyncID(), list);
    }
    
    public static void updatePassword(PackagesBean list, String webPassword)
    {
        list.setWebPassword(webPassword);
    }
    
    public static PackagesBean getByEchoID(String echoID)
    {
        return mIndexByEchoID.get(echoID);
    }
    
    public static PackagesBean getByWebID(String webID)
    {
        return mIndexByWebID.get(webID);
    }
    
    public static PackagesBean getBySyncID(String syncID)
    {
        return mIndexBySyncID.get(syncID);
    }
    
    public static void clearPackages(PackagesBean list)
    {
        list.getPackages().clear();
    }
    
    public static PackagesBean merge(PackagesBean gl1, PackagesBean gl2)
    {
        PackagesBean list = new PackagesBean();
        if (gl1.getEchoID() != null)
            list.setEchoID(gl1.getEchoID());
        else if (gl2.getEchoID() != null)
            list.setEchoID(gl2.getEchoID());
        if (gl1.getWebID() != null)
            list.setWebID(gl1.getWebID());
        else if (gl2.getWebID() != null)
            list.setWebID(gl2.getWebID());
        if (gl1.getWebPassword() != null)
            list.setWebPassword(gl1.getWebPassword());
        else if (gl2.getWebPassword() != null)
            list.setWebPassword(gl2.getWebPassword());
        if (gl1.getSyncID() != null)
            list.setSyncID(gl1.getSyncID());
        else if (gl2.getSyncID() != null)
            list.setSyncID(gl2.getSyncID());
        Map<String,PackageBean> packages = new HashMap<String, PackageBean>();
        for (PackageBean pack : gl2.getPackages())
            packages.put(pack.getTrackingID(), pack);
        for (PackageBean pack : gl1.getPackages())
            packages.put(pack.getTrackingID(), pack);
        list.getPackages().addAll(packages.values());
        removePackages(gl1);
        removePackages(gl2);
        addPackages(list);
        return list;
    }

    public static PackageBean getPackage(PackagesBean list, String tn)
    {
        for (PackageBean pack : list.getPackages())
            if (pack.getTrackingID().equalsIgnoreCase(tn))
                return pack;
        return null;
    }

    public static PackageBean addPackage(PackagesBean list, String tn)
    {
        PackageBean pack = getPackage(list, tn);
        if (pack != null)
            return pack;
        pack = new PackageBean();
        pack.setTrackingID(tn);
        list.getPackages().add(pack);
        list.setLastLookup(0);
        LookupLogic.refresh(list);
        return pack;
    }

    public static void removePackage(PackagesBean list, PackageBean pack)
    {
        list.getPackages().remove(pack);
        list.setLastLookup(0);
        LookupLogic.refresh(list);
    }
}
