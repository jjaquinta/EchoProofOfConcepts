package jo.ups2.data;

import com.ups.xolt.codesamples.response.jaxb.TrackResponse;

public class PackageBean
{
    private String  mTrackingID;
    private TrackResponse   mInfo;

    public String getTrackingID()
    {
        return mTrackingID;
    }

    public void setTrackingID(String trackingID)
    {
        mTrackingID = trackingID;
    }

    public TrackResponse getInfo()
    {
        return mInfo;
    }

    public void setInfo(TrackResponse info)
    {
        mInfo = info;
    }
}
