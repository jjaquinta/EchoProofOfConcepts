package jo.gl.logic;
import java.util.ArrayList;
import java.util.List;

import jo.gl.data.GroceryListBean;

import com.amazon.speech.slu.Intent;
import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SpeechletRequest;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.ui.PlainTextOutputSpeech;
import com.amazon.speech.ui.SimpleCard;

public class GroceryAppLogic
{
    public static SpeechletResponse interact(SpeechletRequest request, Session session)
    {
        GroceryListBean list = getList(session);
        if (request instanceof IntentRequest)
            return doIntent(request, list);
        else
            return doWelcome(request);
    }
    private static SpeechletResponse doWelcome(SpeechletRequest request)
    {
        return buildSpeechletResponse("Welcome to Gocery List. "
                + "You can use this to add and delete items from your list. You can start by saying \"show list\".");
    }
    private static SpeechletResponse doIntent(SpeechletRequest request, GroceryListBean list)
    {
        Intent intent = ((IntentRequest)request).getIntent();
        String response = "I'm not quite sure what you want.";
        if (intent != null)
        {
            String verb = intent.getName();
            String item = null;
            Integer quantity = null;
            if (intent.getSlot("item") != null)
            {
                item = intent.getSlot("item").getValue();
                item = PluralLogic.makeSingular(item);
            }
            if (intent.getSlot("quantity") != null)
            {
                String q = intent.getSlot("quantity").getValue();
                quantity = toInteger(q);
            }
            if ("ADD".equals(verb))
                response = doAdd(list, item, quantity);
            else if ("QUERY".equals(verb))
                response = doQuery(list, item, quantity);
            else if ("QUERYALL".equals(verb))
                response = doQueryAll(list, item, quantity);
            else if ("DELETE".equals(verb))
                response = doDelete(list, item, quantity);
            else if ("DELETEALL".equals(verb))
                response = doDeleteAll(list, item, quantity);
            else if ("SYNCHRONIZE".equals(verb))
                response = doSynchronize(list, item, quantity);
            else if ("SEND".equals(verb))
                response = doSend(list, item, quantity);
        }
        return buildSpeechletResponse(response);
    }
    private static String doAdd(GroceryListBean list, String item,
            Integer quantity)
    {
        if (quantity == null)
            quantity = 1;
        ListLogic.adjustItem(list, item, quantity);
        return reportItemQuantity(list, item);
    }
    private static String reportItemQuantity(GroceryListBean list, String item)
    {
        Integer current = list.getGroceries().get(item);
        if ((current == null) || (current == 0))
            return "You have no more "+PluralLogic.makePlural(item)+" on your list.";
        else if (current == 1)
            return "You have one "+PluralLogic.makeSingular(item)+" on your list.";
        else
            return "You have "+current+" "+PluralLogic.makePlural(item)+" on your list.";
    }
    private static String doQuery(GroceryListBean list, String item,
            Integer quantity)
    {
        return reportItemQuantity(list, item);
    }
    private static String doQueryAll(GroceryListBean list, String item,
            Integer quantity)
    {
        List<String> items = new ArrayList<String>();
        for (String i : list.getGroceries().keySet())
        {
            Integer quan = list.getGroceries().get(i);
            if (quan != null)
                if (quan == 1)
                    items.add("one "+PluralLogic.makeSingular(i));
                else if (quan > 1)
                    items.add(quan+" "+PluralLogic.makePlural(i));
        }
        if (items.size() == 0)
            return "You don't have anything on your list. Say 'add apple' to add an apple to your list.";
        StringBuffer sb = new StringBuffer();
        sb.append("You have ");
        for (int idx = 0; idx < items.size(); idx++)
        {
            if (idx == items.size() - 1)
                sb.append(" and ");
            else if (idx > 0)
                sb.append(", ");
            sb.append(items.get(idx));
        }
        sb.append(".");
        return sb.toString();
    }
    private static String doDelete(GroceryListBean list, String item,
            Integer quantity)
    {
        if (quantity == null)
            quantity = 1;
        ListLogic.adjustItem(list, item, -quantity);
        return reportItemQuantity(list, item);
    }
    private static String doDeleteAll(GroceryListBean list, String item,
            Integer quantity)
    {
        ListLogic.clearList(list);
        return "Your list has been cleared.";
    }
    private static String doSynchronize(GroceryListBean list, String item,
            Integer quantity)
    {
        return "This feature isn't supported yet.";
    }
    private static String doSend(GroceryListBean list, String item,
            Integer quantity)
    {
        return "This feature isn't supported yet.";
    }
    private static GroceryListBean getList(Session session)
    {
        String id = session.getSessionId();
        if (session.getUser() != null)
            if (session.getUser().getUserId() != null)
                id = session.getUser().getUserId();
        GroceryListBean list = ListLogic.getByEchoID(id);
        if (list == null)
        {
            list = ListLogic.newInstance();
            ListLogic.updateEchoID(list, id);
        }
        return list;
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
    private static SpeechletResponse buildSpeechletResponse(String output) {
        String title = "Grocery List";
        // Create the Simple card content.
        SimpleCard card = new SimpleCard();
        card.setTitle(title);
        card.setContent(output);
        // Create the plain text output.
        PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
        speech.setText(output);
        // Create the speechlet response.
        SpeechletResponse response = new SpeechletResponse();
        response.setShouldEndSession(false);
        response.setOutputSpeech(speech);
        response.setCard(card);
        return response;
    }
    
    private static Integer toInteger(String txt)
    {
        try
        {
            return Integer.parseInt(txt);
        }
        catch (NumberFormatException e)
        {
            return null;
        }
    }
}
