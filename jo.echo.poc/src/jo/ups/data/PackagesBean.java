package jo.ups.data;

import java.util.ArrayList;
import java.util.List;

public class PackagesBean
{
    private String  mEchoID;
    private String  mWebID;
    private String  mWebPassword;
    private String  mSyncID;
    private List<PackageBean>   mPackages;
    private long    mLastLookup;
    private boolean mLooking;
    private Thread  mAlertAfterLookup;
    private int     mHelpOff;
    
    public PackagesBean()
    {
        mPackages = new ArrayList<PackageBean>();
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
    public List<PackageBean> getPackages()
    {
        return mPackages;
    }
    public void setPackages(List<PackageBean> packages)
    {
        mPackages = packages;
    }

    public long getLastLookup()
    {
        return mLastLookup;
    }

    public void setLastLookup(long lastLookup)
    {
        mLastLookup = lastLookup;
    }

    public boolean isLooking()
    {
        return mLooking;
    }

    public void setLooking(boolean looking)
    {
        mLooking = looking;
    }

    public Thread getAlertAfterLookup()
    {
        return mAlertAfterLookup;
    }

    public void setAlertAfterLookup(Thread alertAfterLookup)
    {
        mAlertAfterLookup = alertAfterLookup;
    }

    public int getHelpOff()
    {
        return mHelpOff;
    }

    public void setHelpOff(int helpOff)
    {
        mHelpOff = helpOff;
    }

}
