package jo.cc;

import jo.echo.util.BaseServlet;

public class ColossalCaveServlet extends BaseServlet
{
    /**
     * 
     */
    private static final long serialVersionUID = 6998188851979224629L;
    
    public ColossalCaveServlet()
    {
        this.setSpeechlet(new ColossalCaveSpeechlet());
        mIntentsFile = "ColossalCave.json";
        mUtterancesFile = "ColossalCave.baf";
    }
}
