package jo.gl.data;

import java.util.HashMap;
import java.util.Map;

public class GroceryListBean
{
    private String  mEchoID;
    private String  mWebID;
    private String  mWebPassword;
    private String  mSyncID;
    private Map<String, Integer>    mGroceries;
    
    public GroceryListBean()
    {
        mGroceries = new HashMap<String, Integer>();
    }

    public String getEchoID()
    {
        return mEchoID;
    }

    public void setEchoID(String echoID)
    {
        mEchoID = echoID;
    }

    public String getWebID()
    {
        return mWebID;
    }

    public void setWebID(String webID)
    {
        mWebID = webID;
    }

    public String getWebPassword()
    {
        return mWebPassword;
    }

    public void setWebPassword(String webPassword)
    {
        mWebPassword = webPassword;
    }

    public String getSyncID()
    {
        return mSyncID;
    }

    public void setSyncID(String syncID)
    {
        mSyncID = syncID;
    }

    public Map<String, Integer> getGroceries()
    {
        return mGroceries;
    }

    public void setGroceries(Map<String, Integer> groceries)
    {
        mGroceries = groceries;
    }
}
