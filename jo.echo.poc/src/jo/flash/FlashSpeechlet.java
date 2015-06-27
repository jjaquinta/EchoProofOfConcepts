package jo.flash;

import jo.flash.logic.FlashAppLogic;

import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.LaunchRequest;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SessionEndedRequest;
import com.amazon.speech.speechlet.SessionStartedRequest;
import com.amazon.speech.speechlet.Speechlet;
import com.amazon.speech.speechlet.SpeechletException;
import com.amazon.speech.speechlet.SpeechletResponse;

/**
 * This sample shows how to create a simple speechlet for handling speechlet requests.
 */
public class FlashSpeechlet implements Speechlet {
    @Override
    public void onSessionStarted(final SessionStartedRequest request, final Session session)
            throws SpeechletException {
    }

    @Override
    public SpeechletResponse onLaunch(final LaunchRequest request, final Session session)
            throws SpeechletException {
        return FlashAppLogic.interact(request, session);
    }

    @Override
    public SpeechletResponse onIntent(final IntentRequest request, final Session session)
            throws SpeechletException {
        return FlashAppLogic.interact(request, session);
    }

    @Override
    public void onSessionEnded(final SessionEndedRequest request, final Session session)
            throws SpeechletException {
    }
}
