package jo.jose.logic;

import java.io.IOException;
import java.util.List;

import jo.d4w.web.data.DockCargoBean;
import jo.d4w.web.data.OnDockBean;
import jo.d4w.web.data.PortBean;
import jo.d4w.web.data.PortsBean;
import jo.echo.util.ResponseUtils;

import org.json.simple.JSONObject;

import com.amazon.speech.slu.Intent;
import com.amazon.speech.slu.Slot;
import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SpeechletRequest;
import com.amazon.speech.speechlet.SpeechletResponse;

public class JoseAppLogic
{
    public static SpeechletResponse interact(SpeechletRequest request, Session session)
    {
        try
        {
            JSONObject user = getUser(session);
            if (request instanceof IntentRequest)
                return doIntent(request, user);
            else
                return doWelcome(request, user);
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return ResponseUtils.buildSpeechletResponse("Something went wrong. "+e.getLocalizedMessage());
        }
    }
    private static SpeechletResponse doWelcome(SpeechletRequest request, JSONObject user)
    {
        return ResponseUtils.buildSpeechletResponse("Welcome to Jos\u00e9 Fabuloso. "
                + "You've read the book, now play the game! "
                + "You are currently at "+UserLogic.getLocationName(user)+", and have "+UserLogic.getHoldSpace(user)+" tons of space free in your hold.");
    }
    private static SpeechletResponse doIntent(SpeechletRequest request, JSONObject user) throws IOException
    {
        Intent intent = ((IntentRequest)request).getIntent();
        String response = "I'm not quite sure what you want.";
        if (intent != null)
        {
            String verb = intent.getName();
            if (verb.equals("LOCATION"))
                response = doLocation(intent, user);
            else if (verb.equals("JUMP"))
                response = doJump(intent, user);
            else if (verb.equals("RESET"))
                response = doReset(intent, user);
            else if (verb.equals("SPACE"))
                response = doSpace(intent, user);
            else if (verb.equals("MONEY"))
                response = doMoney(intent, user);
            else if (verb.equals("NEARBY"))
                response = doNearby(intent, user);
            else if (verb.equals("DATE"))
                response = doDate(intent, user);
            else if (verb.equals("FORSALE"))
                response = doForSale(intent, user);
            else if (verb.equals("BUY"))
                response = doBuy(intent, user);
            else if (verb.equals("HOLD"))
                response = doHold(intent, user);
            else if (verb.equals("PRICE"))
                response = doPrice(intent, user);
            else if (verb.equals("SELL"))
                response = doSell(intent, user);
            else if (verb.equals("BOOK"))
                response = BookLogic.doBook(intent, user);
            else if (verb.equals("EXCERPT"))
                response = BookLogic.doExcerpt(intent, user);
            else if (verb.equals("JOSE"))
                response = BookLogic.doJose(intent, user);
        }
        return ResponseUtils.buildSpeechletResponse("[[title=Jos\u00e9 Fabuloso]]"+response);
    }
    private static String doReset(Intent intent, JSONObject user) throws IOException
    {
        user = UserLogic.doNewUser(UserLogic.getName(user));
        return "It was all a bad dream... "+doLocation(intent, user);
    }
    private static String doDate(Intent intent, JSONObject user) throws IOException
    {
        int year = UserLogic.getYear(user);
        int day = UserLogic.getDay(user);
        return "It is day "+day+" of year "+year+".";
    }
    private static String doSpace(Intent intent, JSONObject user) throws IOException
    {
        return "You have "+UserLogic.getHoldSpace(user)+" tons of space free in your hold."
                + "[[reprompt=To see what's in your hold, say 'hold'.]]";
    }
    private static String doMoney(Intent intent, JSONObject user) throws IOException
    {
        return "You have "+UserLogic.getMoney(user)+" talents."
                + "[[reprompt=To see what you can buy, say 'dock'.]]";
    }
    private static String doNearby(Intent intent, JSONObject user) throws IOException
    {
        PortsBean ports = PortsLogic.getNearbyPorts(UserLogic.getLocation(user), UserLogic.getJump(user));
        if (ports.getPorts().size() == 1)
            return "How very odd. There's nothing nearby!";
        String atName = UserLogic.getLocationName(user);
        PortBean at = PortsLogic.findPort(ports, atName);
        StringBuffer sb = new StringBuffer();
        PortBean sample = null;
        for (PortBean port : PortsLogic.getPorts(ports))
        {
            String pName = PortLogic.getName(port);
            if (pName.equals(atName))
                continue;
            sample = port;
            String dist = PortLogic.distanceDescription(port, at);
            sb.append(pName+" is "+dist+" away. ");
        }
        sb.append("[[reprompt=To travel to "+PortLogic.getName(sample)+", for example, say 'go to "+PortLogic.getName(sample)+"'.]]");
        return sb.toString();
    }
    private static String doLocation(Intent intent, JSONObject user) throws IOException
    {
        PortBean port = resolveLocation(intent, user);
        if (port == null)
            if (intent.getSlot("location") != null)
                return "I'm not quite sure where you mean."
                    + "[[reprompt=To see what is nearby, say 'nearby'.]]";
            else
                port = PortLogic.lookup(UserLogic.getLocation(user));
        String resp = "The port of "+PortLogic.getName(port)
                +" has a population of "+PortLogic.getPopulationDescription(port)
                +", a tech tier of "+PortLogic.getTechTierDescription(port)
                +", and an economy that mostly focuses on "+PortLogic.getProductionFocus(port)
                +"."
                +"[[card=++ Overall productivity is "+port.getPopStats().getProductivity()+","
                        + " with a material productivity of "+port.getPopStats().getMaterialProductivity()+","
                        + " with an agricultural productivity of "+port.getPopStats().getAgriculturalProductivity()+","
                        + " and an energy productivity of "+port.getPopStats().getEnergyProductivity()+" ]]"
                + "[[reprompt=To see what other ports are nearby, say 'nearby'.]]";
        
        return resp;
    }    
    private static String doJump(Intent intent, JSONObject user) throws IOException
    {
        PortBean port = resolveLocation(intent, user);
        if (port == null)
            if (intent.getSlot("location") != null)
                return "I'm not quite sure where you mean. Say 'nearby' to see nearby worlds."
                    + "[[reprompt=To see what other ports are nearby, say 'nearby'.]]";
            else
                return "You need to tell me where to go."
                + "[[reprompt=To see what other ports are nearby, say 'nearby'.]]";
        String loc = PortLogic.getID(port);
        UserLogic.setLocation(user, loc);
        return "You have arrived at "+UserLogic.getLocationName(user)+"."
                + "[[reprompt=Say 'price all' to see what you can sell your cargo for, or 'dock' to see what's for sale.]]";
    }    
    private static String doForSale(Intent intent, JSONObject user) throws IOException
    {
        OnDockBean ondock = OnDockLogic.queryOnDock(user);
        Slot slot = intent.getSlot("lotnum");
        if ((slot == null) || (slot.getValue() == null))
        {
            List<DockCargoBean> lots = OnDockLogic.getFilteredLots(ondock, user);
            if (lots.size() == 0)
                return "There isn't anything for sale that you can afford or fit."
                        + "[[reprompt=Say 'dock all' to list all items, or check out the neighborhood for other ports.]]";
            return listLots(lots);
        }
        else if ("everything".equals(slot.getValue()) || "all".equals(slot.getValue()))
            return listLots(OnDockLogic.getLots(ondock));
        DockCargoBean lot = resolveLot(intent, user, ondock);
        if (lot == null)
            return "I'm not quite sure where you mean. "
                    + "[[reprompt=Say 'dock' to see everything for sale.]]";
        String name = OnDockLogic.getName(lot);
        int size = OnDockLogic.getSize(lot);
        long price = OnDockLogic.getPurchasePrice(lot);
        return size+" tons of "+name+" for "+price+" talents. "
            + "[[reprompt=Say 'buy "+name+"' to buy it.]]";
    }    
    private static String doBuy(Intent intent, JSONObject user) throws IOException
    {
        OnDockBean ondock = OnDockLogic.queryOnDock(user);
        Slot slot = intent.getSlot("lotnum");
        if ((slot == null) || (slot.getValue() == null))
            return "I'm not quite sure where you mean."
                    + "[[reprompt=Say 'buy' and the lot number or name to purcase a lot.]]";
        else if ("everything".equals(slot.getValue()) || "all".equals(slot.getValue()))
        {
            StringBuffer msg = new StringBuffer();
            for (DockCargoBean lot : ondock.getCargo())
                msg.append(UserLogic.buy(user, lot));
            return msg.toString();
        }
        DockCargoBean lot = resolveLot(intent, user, ondock);
        if (lot == null)
            return "I'm not quite sure which lot you mean."
                    + "[[reprompt=Say 'dock' to see everything for sale.]]";
        return UserLogic.buy(user, lot);
    }    
    private static String doHold(Intent intent, JSONObject user) throws IOException
    {
        List<DockCargoBean> hold = UserLogic.getInHold(user);
        if (hold.size() == 0)
            return "Your hold is empty."
                    + "[[reprompt=Say 'dock' to list what's for sale here.]]";
        int tot = 0;
        StringBuffer msg = new StringBuffer();
        for (DockCargoBean lot : hold)
        {
            msg.append(lot.getSize()+" tons of "+lot.getName()+". ");
            tot += lot.getSize();
        }
        if (hold.size() > 1)
            msg.insert(0, "You have "+tot+" tons of cargo in your hold. ");
        msg.append("[[reprompt=Say 'dock' to list what's for sale here.]]");
        return msg.toString();
    }    
    private static String doPrice(Intent intent, JSONObject user) throws IOException
    {
        List<DockCargoBean> hold = UserLogic.getInHold(user);
        if (hold.size() == 0)
            return "Your hold is empty."
                    +"[[reprompt=Say 'dock' to list what's for sale here.]]";
        Slot slot = intent.getSlot("lotnum");
        if ((slot == null) || (slot.getValue() == null))
            return "I'm not quite sure where you mean."
                    + "[[reprompt=Say 'price' and the lot number or name to price out a lot.]]";
        else if ("everything".equals(slot.getValue()) || "all".equals(slot.getValue()))
        {
            StringBuffer msg = new StringBuffer();
            for (DockCargoBean lot : hold)
                msg.append(OnDockLogic.price(lot));
            return msg.toString();
        }
        DockCargoBean lot = OnDockLogic.findLot(hold, slot.getValue());
        if (lot == null)
            return "I'm not quite sure which lot you mean. "
                    + "[[reprompt=Say 'price all' to see what you can get for your cargo.]]";
        return OnDockLogic.price(lot);
    }    
    private static String doSell(Intent intent, JSONObject user) throws IOException
    {
        List<DockCargoBean> hold = UserLogic.getInHold(user);
        if (hold.size() == 0)
            return "Your hold is empty.[[reprompt=Say 'dock' to see what's for sale.]]";
        Slot slot = intent.getSlot("lotnum");
        if ((slot == null) || (slot.getValue() == null))
            return "I'm not quite sure where you mean.[[reprompt=Say 'sell' and the lot number or name to sell a lot.]]";
        else if ("everything".equals(slot.getValue()) || "all".equals(slot.getValue()))
        {
            StringBuffer msg = new StringBuffer();
            for (DockCargoBean lot : hold)
                msg.append(UserLogic.sell(user, lot));
            return msg.toString();
        }
        DockCargoBean lot = OnDockLogic.findLot(hold, slot.getValue());
        if (lot == null)
            return "I'm not quite sure which lot you mean.[[reprompt=Say 'price all' to see what you can get for your cargo.]]";
        return UserLogic.sell(user, lot);
    }    
    
    private static String listLots(List<DockCargoBean> lots)
    {
        StringBuffer sb = new StringBuffer();
        for (DockCargoBean lot : lots)
        {
            String name = OnDockLogic.getName(lot);
            int size = OnDockLogic.getSize(lot);
            long price = OnDockLogic.getPurchasePrice(lot);
            sb.append(size+" tons of "+name+" for "+price+" talents. ");
        }
        return sb.toString();
    }
    private static DockCargoBean resolveLot(Intent intent, JSONObject user, OnDockBean ondock)
    {
        Slot slot = intent.getSlot("lotnum");
        if ((slot == null) || (slot.getValue() == null))
            return null;
        String lotName = slot.getValue();
        if ((lotName == null) || (lotName.length() == 0))
            return null;
        DockCargoBean lot = OnDockLogic.findLot(OnDockLogic.getFilteredLots(ondock, user), lotName);
        return lot;
    }
    private static PortBean resolveLocation(Intent intent, JSONObject user) throws IOException
    {
        Slot slot = intent.getSlot("location");
        if ((slot == null) || (slot.getValue() == null))
            return null;
        String locationName = slot.getValue();
        if ((locationName == null) || (locationName.length() == 0))
            return null;
        PortsBean ports = PortsLogic.getNearbyPorts(UserLogic.getLocation(user), UserLogic.getJump(user));
        return PortsLogic.findPort(ports, locationName);
    }
    
    private static JSONObject getUser(Session session) throws IOException
    {
        String id = getUserID(session);
        JSONObject user = UserLogic.getUser(id);
        if (user == null)
            user = UserLogic.doNewUser(id);
        return user;
    }
    private static String getUserID(Session session)
    {
        String id = session.getSessionId();
        if (session.getUser() != null)
            if (session.getUser().getUserId() != null)
                id = session.getUser().getUserId();
        return id;
    }
}