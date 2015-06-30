package jo.watson;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jo.echo.util.BaseServlet;
import jo.watson.logic.WatsonAppLogic;

public class WatsonServlet extends BaseServlet
{
    /**
     * 
     */
    private static final long serialVersionUID = -804164273861001737L;
    
    private static Map<Thread, String> mDatasetCache = new HashMap<Thread, String>();

    public WatsonServlet()
    {
        this.setSpeechlet(new WatsonSpeechlet());        
        mIntentsFile = "Watson.json";
        mUtterancesFile = "Watson.baf";
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException
    {
        String dataset = req.getParameter("dataset");
        if (dataset == null)
            setDataset(WatsonAppLogic.HEALTHCARE);
        else
            setDataset(dataset);
        super.doPost(req, resp);
    }
    
    public static void setDataset(String dataset)
    {
        mDatasetCache.put(Thread.currentThread(), dataset);
    }
    
    public static String getDataset()
    {
        return mDatasetCache.get(Thread.currentThread());
    }
}
