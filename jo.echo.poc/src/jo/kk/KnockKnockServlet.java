package jo.kk;

import jo.echo.util.BaseServlet;

public class KnockKnockServlet extends BaseServlet
{
    /**
     * 
     */
    private static final long serialVersionUID = -804164273861001737L;

    public KnockKnockServlet()
    {
        this.setSpeechlet(new KnockKnockSpeechlet());        
        mIntentsFile = "KnockKnock.json";
        mUtterancesFile = "KnockKnock.baf";
    }
}
