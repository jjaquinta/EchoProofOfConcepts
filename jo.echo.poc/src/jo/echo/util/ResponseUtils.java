package jo.echo.util;

import java.util.Collection;
import java.util.Properties;

import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.ui.PlainTextOutputSpeech;
import com.amazon.speech.ui.Reprompt;
import com.amazon.speech.ui.SimpleCard;

public class ResponseUtils
{
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
    public static SpeechletResponse buildSpeechletResponse(String output) {
        Properties props = new Properties();
        output = parseProps(output, props);
        // Create the Simple card content.
        SimpleCard card = new SimpleCard();
        if (props.containsKey("title"))
            card.setTitle(props.getProperty("title"));
        if (props.containsKey("card"))
        {
            String cardText = props.getProperty("card");
            if (cardText.startsWith("++"))
                cardText = output + cardText.substring(2);
            card.setContent(cardText);
        }
        else
            card.setContent(output);
        // Create the plain text output.
        PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
        speech.setText(output);
        // Create the speechlet response.
        SpeechletResponse response = new SpeechletResponse();
        if (props.containsKey("shouldEndSession"))
            response.setShouldEndSession(Boolean.parseBoolean(props.getProperty("shouldEndSession")));
        else
            response.setShouldEndSession(false);
        response.setOutputSpeech(speech);
        if (props.containsKey("reprompt"))
        {
            String repromptText = props.getProperty("reprompt");
            if (repromptText.startsWith("++"))
                repromptText = output + repromptText.substring(2);
            PlainTextOutputSpeech repromptSpeech = new PlainTextOutputSpeech();
            repromptSpeech.setText(repromptText);
            Reprompt reprompt = new Reprompt();
            reprompt.setOutputSpeech(repromptSpeech);
            response.setReprompt(reprompt);
        }
        response.setCard(card);
        return response;
    }

    private static String parseProps(String output, Properties props)
    {
        for (;;)
        {
            int start = output.indexOf("[[");
            if (start < 0)
                break;
            int stop = output.indexOf("]]", start);
            if (stop < 0)
                break;
            String kv = output.substring(start + 2, stop);
            output = output.substring(0, start) + output.substring(stop + 2);
            int o = kv.indexOf('=');
            if (o >= 0)
                props.put(kv.substring(0, o).trim(), kv.substring(o + 1).trim());
        }
        return output;
    }
    
    public static String wordList(Collection<String> words)
    {
        return wordList(words.toArray(new String[0]), -1);
    }
    
    public static String wordList(String[] words, int limit)
    {
        StringBuffer resp = new StringBuffer();
        if (limit < 0)
            limit = words.length;
        for (int i = 0; i < limit; i++)
        {
            if (i > 0)
                resp.append(", ");
            if (i == limit - 1)
                resp.append(" and ");
            resp.append(words[i]);
        }
        return resp.toString();
    }
}
