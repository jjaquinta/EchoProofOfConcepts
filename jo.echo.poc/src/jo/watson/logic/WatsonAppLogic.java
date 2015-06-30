package jo.watson.logic;

import jo.echo.util.ResponseUtils;

import com.amazon.speech.slu.Intent;
import com.amazon.speech.slu.Slot;
import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SpeechletRequest;
import com.amazon.speech.speechlet.SpeechletResponse;

public class WatsonAppLogic
{
    public static final String HEALTHCARE = "healthcare";
    public static final String TRAVEL = "travel";
    
    public static SpeechletResponse interact(SpeechletRequest request, Session session, String dataset)
    {
        String response;
        if (request instanceof IntentRequest)
            response = doIntent(request, dataset);
        else
            response = doWelcome(request, dataset);
        if (dataset.equals(HEALTHCARE))
            response += "[[title=Watson Health]]";
        else if (dataset.equals(TRAVEL))
            response += "[[title=Watson Travel]]";
        else
            response += "[[title=Watson "+dataset+"]]";
        return ResponseUtils.buildSpeechletResponse(response);
    }
    private static String doWelcome(SpeechletRequest request, String dataset)
    {
        StringBuffer resp = new StringBuffer();
        if (dataset.equals(HEALTHCARE))
            resp.append("Welcome to Watson Health. ");
        else if (dataset.equals(TRAVEL))
            resp.append("Welcome to Watson Travel. ");
        else
            resp.append("Welcome to Watson "+dataset+". ");
        resp.append(prompt(dataset));
        resp.append("[[reprompt="+prompt(dataset)+"]] ");
        return resp.toString();
    }
    private static String doIntent(SpeechletRequest request, String dataset)
    {
        Intent intent = ((IntentRequest)request).getIntent();
        StringBuffer resp = new StringBuffer("");
        if (intent.getName().equals("ASK"))
        {
            Slot q = intent.getSlot("question");
            if (q != null)
            {
                String question = q.getValue();
                if (question != null)
                    resp.append(WatsonLogic.ask(question, dataset));
            }
        }
        if (resp.length() == 0)
            resp.append(prompt(dataset));
        resp.append("[[reprompt="+prompt(dataset)+"]] ");
        return resp.toString();
    }
    
    private static String prompt(String dataset)
    {
        if (dataset.equals(HEALTHCARE))
            return "Please ask your healthcare question. ";
        else if (dataset.equals(TRAVEL))
            return "Please ask your travel question. ";
        else
            return "Please ask your "+dataset+" question. ";
    }
}
