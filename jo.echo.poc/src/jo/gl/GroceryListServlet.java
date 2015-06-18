package jo.gl;

import jo.echo.util.BaseServlet;

public class GroceryListServlet extends BaseServlet
{
    /**
     * 
     */
    private static final long serialVersionUID = -804164273861001737L;

    public GroceryListServlet()
    {
        this.setSpeechlet(new GroceryListSpeechlet());        
        mIntentsFile = "GroceryList.json";
        mUtterancesFile = "GroceryList.baf";
    }
}
