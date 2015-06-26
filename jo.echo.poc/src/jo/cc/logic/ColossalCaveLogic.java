package jo.cc.logic;
import java.util.HashMap;
import java.util.Map;

import com.amazon.speech.slu.Intent;
import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SpeechletRequest;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.ui.PlainTextOutputSpeech;
import com.amazon.speech.ui.SimpleCard;

public class ColossalCaveLogic
{
    private final static Map<String, ZaxStateMachine> mMachines = new HashMap<String, ZaxStateMachine>();
    
    public static SpeechletResponse interact(SpeechletRequest request, Session session)
    {
        ZaxStateMachine state = getState(session);
        if (request instanceof IntentRequest)
            doSay(request, state);
        try
        {
            Thread.sleep(1000);
        }
        catch (InterruptedException e)
        {
        }
        SpeechletResponse resp = buildSpeechletResponse(state);
        return resp;
    }
    private static void doSay(SpeechletRequest request, ZaxStateMachine state)
    {
        Intent intent = ((IntentRequest)request).getIntent();
        if (intent != null)
        {
            String verb = intent.getName();
            String directObject = null;
            String prep = null;
            String indirectObject = null;
            if (intent.getSlot("directObject") != null)
                directObject = intent.getSlot("directObject").getValue();
            if (intent.getSlot("indirectObject") != null)
                indirectObject = intent.getSlot("indirectObject").getValue();
            if (indirectObject != null)
                if ("unlock".equalsIgnoreCase(verb))
                    prep = "with";
                else if ("pour".equalsIgnoreCase(verb))
                    prep = "on";
                else if ("give".equalsIgnoreCase(verb))
                    prep = "to";
                else if ("throw".equalsIgnoreCase(verb))
                    prep = "at";
            StringBuffer toSay = new StringBuffer(verb);
            if (directObject != null)
            {
                toSay.append(" ");
                toSay.append(directObject);
                if ((prep != null) && (indirectObject != null))
                {
                    toSay.append(" ");
                    toSay.append(prep);
                    toSay.append(" ");
                    toSay.append(indirectObject);
                }
            }
            state.say(toSay.toString());
        }
    }
    private static ZaxStateMachine getState(Session session)
    {
        String id = session.getSessionId();
        if (session.getUser() != null)
            if (session.getUser().getUserId() != null)
                id = session.getUser().getUserId();
        ZaxStateMachine machine = mMachines.get(id);
        if (machine == null)
        {
            machine = new ZaxStateMachine();
            machine.run(new AdventStoryfile());
            mMachines.put(id, machine);
        }
        return machine;
    }
    /**
     * Creates and returns the visual and spoken response with shouldEndSession flag.
     *
     * @param title
     *            title for the companion application home card
     * @param output
     *            output content for speech and companion application home card
     * @param shouldEndSession
     *            should the session be closed
     * @return SpeechletResponse spoken and visual response for the given input
     */
    private static SpeechletResponse buildSpeechletResponse(ZaxStateMachine state) {
        String title = "Colossal Cave";
        String output = state.getLastText();
        System.out.println(output);
        for (String[] sub : SUBS)
            output = output.replace(sub[0], sub[1]);
        System.out.println(output);
        boolean shouldEndSession = state.isDone();
        // Create the Simple card content.
        SimpleCard card = new SimpleCard();
        card.setTitle(title);
        card.setContent(output);

        // Create the plain text output.
        PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
        speech.setText(output);

        // Create the speechlet response.
        SpeechletResponse response = new SpeechletResponse();
        response.setShouldEndSession(shouldEndSession);
        response.setOutputSpeech(speech);
        response.setCard(card);
        return response;
    }

    private static final String[][] SUBS =
        {
            { "(Please type HELP for instructions and information.)", "Please say HELP for instructions and information." },
            { "ADVENTURE", "" },
            { "The Interactive Original", "" },
            { "By Will Crowther (1976) and Don Woods (1977)", "" },
            { "Reconstructed in three steps by:", "" },
            { "Donald Ekman, David M. Baggett (1993) and Graham Nelson (1994)", "" },
            { "[In memoriam Stephen Bishop (1820?-1857): GN]", "" },
            { "Release 9 / Serial number 060321 / Inform v6.30 Library 6/11 S", "" },
            { "XYZZY", "shazam" },
            { "PLUGH", "plug" },
            { "E/W", "east-west" },
            { "N/S", "north-south" },
        };
}
