package jo.flash;

import jo.echo.util.BaseServlet;

public class FlashServlet extends BaseServlet
{
    /**
     * 
     */
    private static final long serialVersionUID = -804164273861001737L;

    public FlashServlet()
    {
        this.setSpeechlet(new FlashSpeechlet());        
        mIntentsFile = "Flash.json";
        mUtterancesFile = "Flash.baf";
    }
}
