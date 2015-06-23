package jo.ups;

import jo.echo.util.BaseServlet;

public class UPSServlet extends BaseServlet
{
    /**
     * 
     */
    private static final long serialVersionUID = -804164273861001737L;

    public UPSServlet()
    {
        this.setSpeechlet(new UPSSpeechlet());        
        mIntentsFile = "PackageTracker.json";
        mUtterancesFile = "PackageTracker.baf";
    }
}
