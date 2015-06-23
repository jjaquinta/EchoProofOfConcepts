package jo.ups.logic;

import jo.ups.data.PackageBean;
import jo.ups.data.PackagesBean;

import com.amazon.speech.slu.Intent;
import com.amazon.speech.slu.Slot;
import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SpeechletRequest;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.ui.PlainTextOutputSpeech;
import com.amazon.speech.ui.SimpleCard;
import com.ups.xolt.codesamples.response.jaxb.Activity;
import com.ups.xolt.codesamples.response.jaxb.PackageType;
import com.ups.xolt.codesamples.response.jaxb.Shipment;
import com.ups.xolt.codesamples.response.jaxb.TrackResponse;

public class UPSLogic
{
    public static SpeechletResponse interact(SpeechletRequest request, Session session)
    {
        PackagesBean list = getData(session);
        if (request instanceof IntentRequest)
            return doIntent(request, list);
        else
            return doWelcome(request);
    }
    private static SpeechletResponse doWelcome(SpeechletRequest request)
    {
        return buildSpeechletResponse("Welcome to Package Tracker. "
                + "You can use this to track the progress of your UPS shipments. You can start by saying \"show packages\".");
    }
    private static SpeechletResponse doIntent(SpeechletRequest request, PackagesBean list)
    {
        Intent intent = ((IntentRequest)request).getIntent();
        String response = "I'm not quite sure what you want.";
        if (intent.getName().equals("TRACKINGNUMBER"))
            response = doTrackingNumber(intent, list);
        else if (intent.getName().equals("STATUS"))
            response = doStatusNumber(intent, list);
        else if (intent.getName().equals("ADD"))
            response = doAddNumber(intent, list);
        else if (intent.getName().equals("REMOVE"))
            response = doRemoveNumber(intent, list);
        else if (intent.getName().equals("CLEAR"))
            response = doClear(list);
        SpeechletResponse answer = buildSpeechletResponse(response);
        return answer;
    }
    private static String doClear(PackagesBean list)
    {
        list.getPackages().clear();;
        return "Your list has been cleared.";
    }
    private static String doTrackingNumber(Intent intent,
            PackagesBean list)
    {
        String tn = getTrackingNumber(intent);
        if (tn == null)
            return getUnknown(list);
        return getSingleStatus(list, tn);
    }
    private static String doAddNumber(Intent intent,
            PackagesBean list)
    {
        String tn = getTrackingNumber(intent);
        if (tn == null)
            return getUnknown(list);
        PackageBean pack = PackageLogic.getPackage(list, tn);
        if (pack != null)
            return "That is already on your list. If you want a status of that, please say 'status'.";
        pack = PackageLogic.addPackage(list, tn);
        return "That has been added to your list";
    }
    private static String doRemoveNumber(Intent intent,
            PackagesBean list)
    {
        String tn = getTrackingNumber(intent);
        if (tn == null)
            return getUnknown(list);
        PackageBean pack = PackageLogic.getPackage(list, tn);
        if (pack == null)
            return "That is not on your list.";
        PackageLogic.removePackage(list, pack);
        return "That has been removed from your list";
    }
    private static String getSingleStatus(PackagesBean list, String tn)
    {
        PackageBean pack = PackageLogic.getPackage(list, tn);
        if (pack == null)
            pack = PackageLogic.addPackage(list, tn);
        String resp = waitForLookup(list, pack);
        if (resp != null)
            return resp;
        StringBuffer sb = new StringBuffer();
        TrackResponse trackResponse = pack.getInfo();
        if (!"1".equals(trackResponse.getResponse().getResponseStatusCode()))
            sb.append("I was unable to find any information on "+expand(pack.getTrackingID())+". Error response "+trackResponse.getResponse().getResponseStatusDescription()+".");
        else
        {
            if (trackResponse.getShipment().size() > 1)
                sb.append("There are "+trackResponse.getShipment().size()+" shipments. ");
            for (int i = 0; i < trackResponse.getShipment().size(); i++)
            {
                Shipment shipment = trackResponse.getShipment().get(i);
                if (shipment.getPackage().size() > 1)
                    sb.append("  Shipment #"+(i+1)+" has "+shipment.getPackage().size()+" packages. ");
                for (int j = 0; j < shipment.getPackage().size(); j++)
                {
                    PackageType pkg = shipment.getPackage().get(j);
                    if (j > 0)
                        sb.append(" Next package. ");
                    String lastDate = null;
                    for (int k = 0; k < pkg.getActivity().size(); k++)
                    {
                        Activity activity = pkg.getActivity().get(k);
                        sb.append(activity.getStatus().getStatusType().getDescription());
                        sb.append(" at "+expandTime(activity.getTime()));
                        if (!activity.getDate().equals(lastDate))
                            sb.append(" on "+expandDate(activity.getDate()));
                        lastDate = activity.getDate();
                        sb.append(". ");
                    }
                }
            }

        }
        return sb.toString();
    }
    
    private static String waitForLookup(PackagesBean list, PackageBean pack)
    {
        if (list.isLooking())
        {   // wait up to one second
            list.setAlertAfterLookup(Thread.currentThread());
            try
            {
                Thread.sleep(1000);
            }
            catch (InterruptedException e)
            {
            }
            list.setAlertAfterLookup(null);
            if (list.isLooking())
            {
                return "Looking up information. Please ask again shortly.";
            }
        }
        if ((pack != null) && (pack.getInfo() == null))
            return "I was unable to find any information on "+expand(pack.getTrackingID())+".";
        return null;
    }
    
    private static String doStatusNumber(Intent intent,
            PackagesBean list)
    {
        String tn = getTrackingNumber(intent);
        if (tn != null)
            return getSingleStatus(list, tn);
        String resp = waitForLookup(list, null);
        if (resp != null)
            return resp;
        StringBuffer sb = new StringBuffer();
        String lastDate = null;
        for (PackageBean pack : list.getPackages())
        {
            sb.append("Tracking "+expand(pack.getTrackingID())+". ");
            TrackResponse trackResponse = pack.getInfo();
            if (trackResponse.getShipment().size() > 1)
                sb.append("There are "+trackResponse.getShipment().size()+" shipments. ");
            for (int i = 0; i < trackResponse.getShipment().size(); i++)
            {
                Shipment shipment = trackResponse.getShipment().get(i);
                if (shipment.getPackage().size() > 1)
                    sb.append("  Shipment #"+(i+1)+" has "+shipment.getPackage().size()+" packages. ");
                for (int j = 0; j < shipment.getPackage().size(); j++)
                {
                    PackageType pkg = shipment.getPackage().get(j);
                    if (shipment.getPackage().size() > 1)
                        sb.append(" "+DAYS[j+1]+" package. ");
                    Activity activity = pkg.getActivity().get(0);
                    sb.append(activity.getStatus().getStatusType().getDescription());
                    sb.append(" at "+expandTime(activity.getTime()));
                    if (!activity.getDate().equals(lastDate))
                        sb.append(" on "+expandDate(activity.getDate()));
                    lastDate = activity.getDate();
                    sb.append(". ");
                }
            }
        }
        return sb.toString();
    }
    
    private static String[] MONTHS =
    {
        "January",
        "February",
        "March",
        "April",
        "May",
        "June",
        "July",
        "August",
        "September",
        "October",
        "November",
        "December",
    };
    
    private static String[] DAYS =
    {
        "first",
        "second",
        "third",
        "fourth",
        "fifth",
        "sixth",
        "seventh",
        "eighth",
        "ninth",
        "tenth",
        "eleventh",
        "twelth",
        "thirteenth",
        "fourteenth",
        "fifteenth",
        "sixteenth",
        "seventeenth",
        "eighteenth",
        "ninteenth",
        "twentieth",
        "twenty first",
        "twenty second",
        "twenty third",
        "twenty fourth",
        "twenty fifth",
        "twenty sixth",
        "twenty seventh",
        "twenty eighth",
        "twenty ninth",
        "thirtieth",
        "thirtyfirst"
    };
    
    private static String expandDate(String date)
    {
        int month = Integer.parseInt(date.substring(4, 6));
        int day = Integer.parseInt(date.substring(6, 8));
        return MONTHS[month-1]+" "+DAYS[day-1];
    }
    private static String expandTime(String time)
    {
        int hour = Integer.parseInt(time.substring(0, 2));
        int minute = Integer.parseInt(time.substring(2, 4));
        if (minute == 0)
        {
            if (hour == 0)
                return "midnight";
            if (hour == 12)
                return "noon";
            if (hour < 12)
                return hour+" a m";
            else
                return ((hour - 12)+" p m");
        }
        StringBuffer sb = new StringBuffer();
        if (hour == 0)
            sb.append("12");
        else if (hour <= 12)
            sb.append(Integer.toString(hour));
        else
            sb.append(Integer.toString(hour - 12));
        if (minute < 10)
            sb.append(" oh "+minute+" ");
        else
            sb.append(" "+minute+" ");
        if (hour < 12)
            sb.append(" a m");
        else
            sb.append(" p m");
        return sb.toString();
    }
    private static String expand(String tn)
    {
        StringBuffer sb = new StringBuffer(tn.length()*2);
        for (char c : tn.toCharArray())
        {
            sb.append(' ');
            sb.append(c);
        }
        return sb.toString();
    }
    
    private static final String[] HELP = {
        "Please say 'status' to get an update on previous tracking numbers or tell me a tracking number.",
        "When saying a tracking number, please pronounce each digit separately.",
        "If I'm having trouble undestanding letters, try saying a word that begins with the letter, such as alpha, bravo, charlie.",        
    };
    
    private static String getUnknown(PackagesBean list)
    {
        String huh = "I didn't understand that. ";
        huh += HELP[list.getHelpOff()%HELP.length];
        list.setHelpOff(list.getHelpOff() + 1);
        return huh;
    }
    
    private static final String[] DIGITS = {
        "firstDigit",
        "secondDigit",
        "thirdDigit",
        "fourthDigit",
        "fifthDigit",
        "sixthDigit",
        "seventhDigit",
        "eighthDigit",
        "ninthDigit",
        "tenthDigit",
        "eleventhDigit",
        "twelthDigit",
        "thirteenthDigit",
        "fourteenthDigit",
        "fifteenthDigit",
        "sixteenthDigit",
        "seventeenthDigit",
        "eighteenthDigit",
    };
    
    private static String getTrackingNumber(Intent intent)
    {
        StringBuffer tn = new StringBuffer();
        for (int i = 0; i < DIGITS.length; i++)
        {
            Slot s = intent.getSlot(DIGITS[i]);
            if (s == null)
                return null;
            String v = s.getValue();
            if (v.length() == 0)
                return null;
            if ("one".equalsIgnoreCase(v))
                tn.append("1");
            else if ("two".equalsIgnoreCase(v))
                tn.append("2");
            else if ("three".equalsIgnoreCase(v))
                tn.append("3");
            else if ("four".equalsIgnoreCase(v))
                tn.append("4");
            else if ("five".equalsIgnoreCase(v))
                tn.append("5");
            else if ("six".equalsIgnoreCase(v))
                tn.append("6");
            else if ("seven".equalsIgnoreCase(v))
                tn.append("7");
            else if ("eight".equalsIgnoreCase(v))
                tn.append("8");
            else if ("nine".equalsIgnoreCase(v))
                tn.append("9");
            else if ("oh".equalsIgnoreCase(v))
                tn.append("0");
            else if ("zero".equalsIgnoreCase(v))
                tn.append("0");
            else if ("owe".equalsIgnoreCase(v))
                tn.append("0");
            else
                tn.append(v.charAt(0));
        }
        return tn.toString();
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
        String title = "Package Tracker";
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
    
    private static PackagesBean getData(Session session)
    {
        String id = session.getSessionId();
        if (session.getUser() != null)
            if (session.getUser().getUserId() != null)
                id = session.getUser().getUserId();
        PackagesBean list = PackageLogic.getByEchoID(id);
        if (list == null)
        {
            list = PackageLogic.newInstance();
            PackageLogic.updateEchoID(list, id);
        }
        LookupLogic.refresh(list);
        return list;
    }

}
