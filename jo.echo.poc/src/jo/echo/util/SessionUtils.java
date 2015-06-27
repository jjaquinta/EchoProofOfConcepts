package jo.echo.util;

import com.amazon.speech.speechlet.Session;

public class SessionUtils
{
    public static String getUserID(Session session)
    {
        String id = session.getSessionId();
        if (session.getUser() != null)
            if (session.getUser().getUserId() != null)
                id = session.getUser().getUserId();
        return id;
    }
}
