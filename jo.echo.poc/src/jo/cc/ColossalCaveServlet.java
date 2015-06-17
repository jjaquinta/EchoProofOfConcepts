package jo.cc;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.amazon.speech.Sdk;
import com.amazon.speech.speechlet.servlet.SpeechletServlet;

public class ColossalCaveServlet extends SpeechletServlet
{
    /**
     * 
     */
    private static final long serialVersionUID = 6998188851979224629L;

    static
    {
        System.setProperty(Sdk.DISABLE_REQUEST_SIGNATURE_CHECK_SYSTEM_PROPERTY, "true");
        System.setProperty(Sdk.SUPPORTED_APPLICATION_IDS_SYSTEM_PROPERTY, "");
        System.setProperty(Sdk.TIMESTAMP_TOLERANCE_SYSTEM_PROPERTY, "");
    }
    
    private Throwable mLastException = null;
    
    public ColossalCaveServlet()
    {
        this.setSpeechlet(new ColossalCaveSpeechlet());        
    }
    
    @Override
    protected void doPost(HttpServletRequest arg0, HttpServletResponse arg1)
            throws ServletException, IOException
    {
        try
        {
            super.doPost(arg0, arg1);
        }
        catch (ServletException e1)
        {
            mLastException = e1;
            throw e1;
        }
        catch (IOException e2)
        {
            mLastException = e2;
            throw e2;
        }
    }
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException
    {
        resp.setContentType("text/plain");
        if (mLastException == null)
            resp.getWriter().append("No recent exceptions");
        else
        {
            Throwable t = mLastException;
            while (t != null)
            {
                resp.getWriter().append(t.getLocalizedMessage()+":\r\n");
                for (StackTraceElement ste : t.getStackTrace())
                    resp.getWriter().append("  "+ste.toString()+":\r\n");
                if (t instanceof Exception)
                    t = ((Exception)t).getCause();
                else
                    t = null;
            }
        }        
    }
}
