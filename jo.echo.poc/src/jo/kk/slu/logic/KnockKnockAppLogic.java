package jo.kk.slu.logic;

import jo.echo.util.ResponseUtils;
import jo.echo.util.SessionUtils;
import jo.kk.slu.data.KnockKnockState;

import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SessionEndedRequest;
import com.amazon.speech.speechlet.SpeechletRequest;
import com.amazon.speech.speechlet.SpeechletResponse;

public class KnockKnockAppLogic
{
    public static SpeechletResponse interact(SpeechletRequest request, Session session)
    {
        KnockKnockState state = getData(session);
        String response;
        if (request instanceof SessionEndedRequest)
        {
            state.setJoke(null);
            state.setCall(-1);
            return null;
        }
        else if ((request instanceof IntentRequest) && (((IntentRequest)request).getIntent().getName().equals("QUIT")))
        {
            response = "Goodbye!";
            response += "[[shouldEndSession=true]]";
        }
        else
        {
            response = state.getJoke()[state.getCall()];
            state.setCall(state.getCall() + 1);
            if (state.getCall() == state.getJoke().length)
                response += "[[shouldEndSession=true]]";
        }
        response += "[[title=Knock Knock]]";
        return ResponseUtils.buildSpeechletResponse(response);
    }
     
    private static KnockKnockState getData(Session session)
    {
        String id = SessionUtils.getUserID(session);
        return JokeLogic.getState(id);
    }

}
