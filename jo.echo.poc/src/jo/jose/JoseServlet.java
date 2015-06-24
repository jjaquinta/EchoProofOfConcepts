package jo.jose;

import jo.echo.util.BaseServlet;

public class JoseServlet extends BaseServlet
{
    /**
     * 
     */
    private static final long serialVersionUID = -804164273861001737L;

    public JoseServlet()
    {
        this.setSpeechlet(new JoseSpeechlet());        
        mIntentsFile = "Jose.json";
        mUtterancesFile = "Jose.baf";
    }
}
