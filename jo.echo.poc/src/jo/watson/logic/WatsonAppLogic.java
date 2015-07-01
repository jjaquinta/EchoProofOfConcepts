package jo.watson.logic;

import java.util.HashMap;
import java.util.Map;

import jo.echo.util.ResponseUtils;
import jo.echo.util.SessionUtils;
import jo.watson.data.WatsonAnswerBean;
import jo.watson.data.WatsonStateBean;

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

    public static final double CONFIDENCE_THRESHOLD = 50;
    
    public static SpeechletResponse interact(SpeechletRequest request, Session session, String dataset)
    {
        WatsonStateBean state = getData(session);
        String response;
        if (request instanceof IntentRequest)
            response = doIntent(request, dataset, state);
        else
            response = doWelcome(request, dataset, state);
        if (dataset.equals(HEALTHCARE))
            response += "[[title=Watson Health]]";
        else if (dataset.equals(TRAVEL))
            response += "[[title=Watson Travel]]";
        else
            response += "[[title=Watson "+dataset+"]]";
        return ResponseUtils.buildSpeechletResponse(response);
    }
    private static String doWelcome(SpeechletRequest request, String dataset, WatsonStateBean state)
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
        state.setState(WatsonStateBean.BASE);
        return resp.toString();
    }
    private static String doIntent(SpeechletRequest request, String dataset, WatsonStateBean state)
    {
        Intent intent = ((IntentRequest)request).getIntent();
        StringBuffer resp = new StringBuffer("");
        if (state.getState() == WatsonStateBean.BASE)
            doBaseState(resp, intent, state, dataset);
        else if (state.getState() == WatsonStateBean.QUERY_SHOW)
            doQueryShow(resp, intent, state, dataset);
        else if (state.getState() == WatsonStateBean.SETTINGS_LOW)
            doSettingsLow(resp, intent, state, dataset);
        if (resp.length() == 0)
            resp.append(prompt(dataset));
        if (resp.indexOf("[[reprompt=") < 0)
            resp.append("[[reprompt="+prompt(dataset)+"]] ");
        return resp.toString();
    }
    
    private static void doBaseState(StringBuffer resp, Intent intent,
            WatsonStateBean state, String dataset)
    {
        if (intent.getName().equals("ASK"))
            doAsk(resp, intent, state, dataset);
        else if (intent.getName().equals("SETTINGS"))
            doSettings(resp, intent, state, dataset);
    }
    
    private static void doQueryShow(StringBuffer resp, Intent intent,
            WatsonStateBean state, String dataset)
    {
        if (intent.getName().equals("ASK"))
        {
            doAsk(resp, intent, state, dataset);
            state.setState(0);
        }
        else if (intent.getName().equals("YES"))
        {
            respondWithAnswer(resp, state.getAnswer(), state);
        }
        else if (intent.getName().equals("NO"))
        {
            resp.append("OK. ");
            resp.append(prompt(dataset));
            state.setState(0);
        }
        else if (intent.getName().equals("ALWAYS"))
        {
            state.setShowLowConfidence(Boolean.TRUE);
            respondWithAnswer(resp, state.getAnswer(), state);
        }
        else if (intent.getName().equals("NEVER"))
        {
            state.setShowLowConfidence(Boolean.FALSE);
            resp.append("OK. ");
            resp.append(prompt(dataset));
            state.setState(0);
        }
    }
    
    private static void doSettingsLow(StringBuffer resp, Intent intent,
            WatsonStateBean state, String dataset)
    {
        if (intent.getName().equals("ASK"))
        {
            doAsk(resp, intent, state, dataset);
            state.setState(0);
        }
        else
        {
            if (state.isShowLowConfidence() == null)
            {
                if (intent.getName().equals("NO"))
                    state.setShowLowConfidence(Boolean.FALSE);
                else if (intent.getName().equals("ALWAYS"))
                    state.setShowLowConfidence(Boolean.TRUE);
            }
            else if (state.isShowLowConfidence())
            {
                if (intent.getName().equals("NO"))
                    state.setShowLowConfidence(null);
                else if (intent.getName().equals("NEVER"))
                    state.setShowLowConfidence(Boolean.FALSE);
            }
            else 
            {
                if (intent.getName().equals("NO"))
                    state.setShowLowConfidence(null);
                else if (intent.getName().equals("ALWAYS"))
                    state.setShowLowConfidence(Boolean.TRUE);
            }
        }
        resp.append("OK. ");
        if (state.isShowLowConfidence() == null)
            resp.append("I will ask for clarification if there is an answer of low confidence. ");
        else if (state.isShowLowConfidence())
            resp.append("I will always give answers no matter their confidence. ");
        else 
            resp.append("I will never give answers of low confidence. ");
        state.setState(0);
    }
    private static void doSettings(StringBuffer resp, Intent intent,
            WatsonStateBean state, String dataset)
    {
        if (state.isShowLowConfidence() == null)
        {
            resp.append("Currently I ask for clarification if there is an answer of low confidence. ");
            resp.append("Should I continue to ask? ");
            resp.append("[[reprompt=Please say 'yes' to continue to ask, 'no', to no longer ask, or 'always' to respond with low confidence answers.]]");
        }
        else if (state.isShowLowConfidence())
        {
            resp.append("Currently I always give answers no matter their confidence. ");
            resp.append("Should I continue to always give them? ");
            resp.append("[[reprompt=Please say 'yes' to continue to always answer, 'no', to ask for confirmation, or 'never' to never low confidence answers.]]");
        }
        else 
        {
            resp.append("Currently I never give answers of low confidence. ");
            resp.append("Should I continue to never give them? ");
            resp.append("[[reprompt=Please say 'yes' to continue to never answer, 'no', to ask for confirmation, or 'always' to always give low confidence answers.]]");
        }
        state.setState(WatsonStateBean.SETTINGS_LOW);
    }
    private static void doAsk(StringBuffer resp, Intent intent,
            WatsonStateBean state, String dataset)
    {
        Slot q = intent.getSlot("question");
        if (q != null)
        {
            String question = q.getValue();
            if (question != null)
            {
                state.setQuestion(question);
                WatsonAnswerBean answer = WatsonLogic.ask(question, dataset);
                if (answer == null)
                    resp.append("I was unable to get an answer to that question.");
                else
                {
                    if ((state.isShowLowConfidence() == Boolean.TRUE) || (answer.getConfidence() > CONFIDENCE_THRESHOLD))
                        respondWithAnswer(resp, answer, state);
                    else if (state.isShowLowConfidence() == Boolean.FALSE)
                        resp.append("There are no answers with a suitable confidence rating. ");                        
                    else
                    {
                        resp.append("The only answer I can find only has a confidence rating of "+answer.getConfidence()+"%. ");
                        resp.append("Do you still want to hear it? ");
                        resp.append("[[reprompt=Please say 'yes' if you want to hear the answer.]]");
                        state.setAnswer(answer);
                        state.setState(WatsonStateBean.QUERY_SHOW);
                    }
                }
            }
        }
    }
    private static void respondWithAnswer(StringBuffer resp,
            WatsonAnswerBean answer, WatsonStateBean state)
    {
        resp.append(answer.getAnswer()+" (Confidence "+answer.getConfidence()+"%)");
        state.setState(0);
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
    private static final Map<String, WatsonStateBean> mStates = new HashMap<String, WatsonStateBean>();
    
    private static WatsonStateBean getData(Session session)
    {
        String userID = SessionUtils.getUserID(session);
        WatsonStateBean state = mStates.get(userID);
        if (state == null)
        {
            state = new WatsonStateBean();
            mStates.put(userID, state);
        }
        return state;
    }

}
