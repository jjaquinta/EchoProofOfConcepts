package jo.echo.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.amazon.speech.Sdk;
import com.amazon.speech.slu.Slot;
import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.LaunchRequest;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SessionEndedRequest;
import com.amazon.speech.speechlet.SessionStartedRequest;
import com.amazon.speech.speechlet.Speechlet;
import com.amazon.speech.speechlet.SpeechletException;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.speechlet.servlet.SpeechletServlet;
import com.amazon.speech.ui.PlainTextOutputSpeech;

public class BaseServlet extends SpeechletServlet
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
    protected String mIntentsFile;
    protected String mUtterancesFile;
    private static Map<Object, StringBuffer> mLogMessages = new HashMap<Object, StringBuffer>();
    
    public BaseServlet()
    {
        mLogMessages.put(this, new StringBuffer());
    }
    
    @Override
    public void setSpeechlet(Speechlet speechlet)
    {
        super.setSpeechlet(new SpeechletWrapper(speechlet));
    }

    @Override
    public Speechlet getSpeechlet()
    {
        Speechlet base = super.getSpeechlet();
        if (base instanceof SpeechletWrapper)
            base = ((SpeechletWrapper)base).getBase();
        return base;
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
        StringBuffer log = mLogMessages.get(getClass());
        String fetch = req.getParameter("fetch");
        resp.setContentType("text/plain");
        if ("intents".equals(fetch))
            doGetResource(req, resp, mIntentsFile);
        else if ("utterances".equals(fetch))
            doGetResource(req, resp, mUtterancesFile);
        else if ((mLastException != null) || ((log != null) && (log.length() > 0)))
        {
            if ((log != null) && (log.length() > 0))
            {
                resp.getWriter().write(log.toString());
                log.setLength(0);
            }
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
        else
            resp.getWriter().append("Hello from the "+getClass().getSimpleName()+" app.");
    }

    protected void doGetResource(HttpServletRequest req, HttpServletResponse resp, String file)
            throws ServletException, IOException
    {
        if (file == null)
            return;
        String path = getClass().getPackage().getName().replace('.', '/');
        String resource = path + "/slu/"+file;
        InputStream is = getClass().getClassLoader().getResourceAsStream(resource);
        for (;;)
        {
            int ch = is.read();
            if (ch < 0)
                break;
            resp.getOutputStream().write(ch);
        }
        is.close();
    }
    
    public static void log(Class<?> wrt, String msg)
    {
        StringBuffer log = mLogMessages.get(wrt);
        if (log == null)
        {
            log = new StringBuffer();
            mLogMessages.put(wrt, log);
        }
        log.append(msg);
        log.append("\r\n");
        System.out.println(msg);
    }
    
    class SpeechletWrapper implements Speechlet {
        private Speechlet mBase;
        
        public SpeechletWrapper(Speechlet base)
        {
            mBase = base;
        }
        
        @Override
        public void onSessionStarted(final SessionStartedRequest request, final Session session)
                throws SpeechletException {
            log("onSessionStarted requestId="+request.getRequestId()+", sessionId="+session.getSessionId());
            mBase.onSessionStarted(request, session);
        }

        @Override
        public SpeechletResponse onLaunch(final LaunchRequest request, final Session session)
                throws SpeechletException {
            log("onLaunch requestId="+request.getRequestId()+", sessionId="+session.getSessionId());
            SpeechletResponse response = mBase.onLaunch(request, session);
            logResponse(response);
            return response;
        }

        @Override
        public SpeechletResponse onIntent(final IntentRequest request, final Session session)
                throws SpeechletException {
            log("onIntent requestId="+request.getRequestId()+", sessionId="+session.getSessionId());
            if (request.getIntent() != null)
            {
                StringBuffer sb = new StringBuffer();
                sb.append(request.getIntent().getName()+":");
                for (Slot s : request.getIntent().getSlots().values())
                    sb.append(" "+s.getName()+"="+s.getValue());
                log(sb.toString());
            }
            SpeechletResponse response = mBase.onIntent(request, session);
            logResponse(response);
            return response;
        }

        @Override
        public void onSessionEnded(final SessionEndedRequest request, final Session session)
                throws SpeechletException {
            log("onSessionEnded requestId="+request.getRequestId()+", sessionId="+session.getSessionId());
            mBase.onSessionEnded(request, session);
        }

        private void logResponse(SpeechletResponse response)
        {
            if (response.getOutputSpeech() instanceof PlainTextOutputSpeech)
                log(((PlainTextOutputSpeech)response.getOutputSpeech()).getText());
        }
        
        public Speechlet getBase()
        {
            return mBase;
        }

        public void setBase(Speechlet base)
        {
            mBase = base;
        }
    }

}
