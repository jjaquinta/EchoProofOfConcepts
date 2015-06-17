package jo.gl.logic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jo.gl.data.GroceryListBean;

public class ListLogic
{
    private final static List<GroceryListBean>  mLists = new ArrayList<GroceryListBean>();
    private final static Map<String, GroceryListBean> mIndexByEchoID = new HashMap<String, GroceryListBean>();
    private final static Map<String, GroceryListBean> mIndexByWebID = new HashMap<String, GroceryListBean>();
    private final static Map<String, GroceryListBean> mIndexBySyncID = new HashMap<String, GroceryListBean>();
    
    public static GroceryListBean newInstance()
    {
        GroceryListBean list = new GroceryListBean();
        addList(list);
        return list;
    }

    public static void addList(GroceryListBean list)
    {
        mLists.add(list);
        if (list.getEchoID() != null)
            mIndexByEchoID.put(list.getEchoID(), list);
        if (list.getWebID() != null)
            mIndexByWebID.put(list.getWebID(), list);
        if (list.getSyncID() != null)
            mIndexBySyncID.put(list.getSyncID(), list);
    }
    
    public static void removeList(GroceryListBean list)
    {
        mLists.remove(list);
        if (list.getEchoID() != null)
            mIndexByEchoID.remove(list.getEchoID());
        if (list.getWebID() != null)
            mIndexByWebID.remove(list.getWebID());
        if (list.getSyncID() != null)
            mIndexBySyncID.remove(list.getSyncID());
    }
    
    public static void updateEchoID(GroceryListBean list, String echoID)
    {
        if (list.getEchoID() != null)
            mIndexByEchoID.remove(list.getEchoID());
        list.setEchoID(echoID);
        if (list.getEchoID() != null)
            mIndexByEchoID.put(list.getEchoID(), list);
    }
    
    public static void updateWebID(GroceryListBean list, String webID)
    {
        if (list.getWebID() != null)
            mIndexByWebID.remove(list.getWebID());
        list.setWebID(webID);
        if (list.getWebID() != null)
            mIndexByWebID.put(list.getWebID(), list);
    }
    
    public static void updateSyncID(GroceryListBean list, String syncID)
    {
        if (list.getSyncID() != null)
            mIndexBySyncID.remove(list.getSyncID());
        list.setSyncID(syncID);
        if (list.getSyncID() != null)
            mIndexBySyncID.put(list.getSyncID(), list);
    }
    
    public static void updatePassword(GroceryListBean list, String webPassword)
    {
        list.setWebPassword(webPassword);
    }
    
    public static GroceryListBean getByEchoID(String echoID)
    {
        return mIndexByEchoID.get(echoID);
    }
    
    public static GroceryListBean getByWebID(String webID)
    {
        return mIndexByWebID.get(webID);
    }
    
    public static GroceryListBean getBySyncID(String syncID)
    {
        return mIndexBySyncID.get(syncID);
    }
    
    public static void adjustItem(GroceryListBean list, String item, Integer delta)
    {
        if (delta == null)
        {
            list.getGroceries().remove(item);
        }
        else
        {
            Integer current = list.getGroceries().get(item);
            if (current == null)
                current = delta;
            else
                current += delta;
            if (current < 0)
                current = 0;
            list.getGroceries().put(item,  current);
        }
    }
    
    public static void clearList(GroceryListBean list)
    {
        list.getGroceries().clear();
    }
    
    public static GroceryListBean merge(GroceryListBean gl1, GroceryListBean gl2)
    {
        GroceryListBean list = new GroceryListBean();
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
        for (String grocery : gl1.getGroceries().keySet())
            list.getGroceries().put(grocery, gl1.getGroceries().get(grocery));
        for (String grocery : gl2.getGroceries().keySet())
            if (!list.getGroceries().containsKey(grocery))
                list.getGroceries().put(grocery, gl1.getGroceries().get(grocery));
        removeList(gl1);
        removeList(gl2);
        addList(list);
        return list;
    }
}
