package jo.ups.logic;

import jo.ups.data.PackageBean;
import jo.ups.data.PackagesBean;

import com.ups.xolt.codesamples.UPSTrackLogic;
import com.ups.xolt.codesamples.response.jaxb.TrackResponse;

public class LookupLogic
{
    private static final long REFRESH_TIMEOUT = 300*1000L; // five minutes

    public static void refresh(final PackagesBean packages)
    {
        if (packages.getLastLookup() > System.currentTimeMillis() - REFRESH_TIMEOUT)
            return;
        if (packages.getPackages().size() == 0)
            return;
        packages.setLooking(true);
        Thread t = new Thread("Lookup") { public void run() { doRefresh(packages); } };
        t.start();
    }

    private static void doRefresh(PackagesBean packages)
    {
        packages.setLastLookup(System.currentTimeMillis());
        for (PackageBean pack : packages.getPackages().toArray(new PackageBean[0]))
            refresh(pack);
        packages.setLooking(false);
        if (packages.getAlertAfterLookup() != null)
        {
            packages.getAlertAfterLookup().interrupt();
            packages.setAlertAfterLookup(null);
        }
    }

    private static void refresh(PackageBean pack)
    {
        TrackResponse resp = UPSTrackLogic.lookup(pack.getTrackingID());
        pack.setInfo(resp);
    }
}
