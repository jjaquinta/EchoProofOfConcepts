package jo.ups2;

import jo.echo.util.BaseServlet;

public class UPS2Servlet extends BaseServlet
{
    /**
     * 
     */
    private static final long serialVersionUID = -804164273861001737L;

    public UPS2Servlet()
    {
        this.setSpeechlet(new UPS2Speechlet());        
        mIntentsFile = "PackageTracker.json";
        mUtterancesFile = "PackageTracker.baf";
    }
}
