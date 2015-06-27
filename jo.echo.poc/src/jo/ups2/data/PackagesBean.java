package jo.ups2.data;

import java.util.ArrayList;
import java.util.List;

public class PackagesBean
{
    public static final int BASE = 0;
    public static final int ADD0 = 10;
    public static final int ADD1 = 11;
    public static final int ADD2 = 12;
    public static final int ADD3 = 13;
    public static final int ADD4 = 14;
    public static final int ADD5 = 15;
    public static final int ADD6 = 16;
    public static final int REMOVE0 = 20;
    public static final int REMOVE1 = 21;
    public static final int REMOVE2 = 22;
    public static final int REMOVE3 = 23;
    
    private String  mEchoID;
    private String  mWebID;
    private String  mWebPassword;
    private String  mSyncID;
    private List<PackageBean>   mPackages;
    private long    mLastLookup;
    private boolean mLooking;
    private Thread  mAlertAfterLookup;
    private int     mHelpOff;
    private int     mState;
    private String  mCurrentNumber;
    private int     mDeleteRequest;
    
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

    public int getState()
    {
        return mState;
    }

    public void setState(int state)
    {
        mState = state;
    }

    public String getCurrentNumber()
    {
        return mCurrentNumber;
    }

    public void setCurrentNumber(String currentNumber)
    {
        mCurrentNumber = currentNumber;
    }

    public int getDeleteRequest()
    {
        return mDeleteRequest;
    }

    public void setDeleteRequest(int deleteRequest)
    {
        mDeleteRequest = deleteRequest;
    }

}
