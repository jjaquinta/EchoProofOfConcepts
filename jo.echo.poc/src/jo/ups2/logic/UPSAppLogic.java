package jo.ups2.logic;

import jo.echo.util.EnumerationUtils;
import jo.echo.util.ResponseUtils;
import jo.echo.util.SessionUtils;
import jo.ups2.data.PackageBean;
import jo.ups2.data.PackagesBean;

import com.amazon.speech.slu.Intent;
import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SpeechletRequest;
import com.amazon.speech.speechlet.SpeechletResponse;

public class UPSAppLogic
{
    public static SpeechletResponse interact(SpeechletRequest request, Session session)
    {
        PackagesBean list = getData(session);
        String response;
        if (request instanceof IntentRequest)
            response = doIntent(request, list);
        else
            response = doWelcome(request, list);
        response += "[[title=Package Tracker]]";
        return ResponseUtils.buildSpeechletResponse(response);
    }
    private static String doWelcome(SpeechletRequest request, PackagesBean list)
    {
        list.setState(0);
        return "Welcome to Package Tracker. "
                + "You can use this to track the progress of your UPS shipments. You can start by saying \"show packages\".";
    }
    private static String doIntent(SpeechletRequest request, PackagesBean list)
    {
        Intent intent = ((IntentRequest)request).getIntent();
        String response = "I'm not quite sure what you want.";
        if (intent.getName().equals("HELP"))
            response = doHelp(list);
        else if (list.getState() == PackagesBean.BASE)
            response = BaseLogic.doIntent(intent, list);
        else if ((list.getState() >= PackagesBean.ADD0) && (list.getState() <= PackagesBean.ADD6))
            response = AddLogic.doIntent(intent, list);
        else if ((list.getState() >= PackagesBean.REMOVE0) && (list.getState() <= PackagesBean.REMOVE3))
            response = AddLogic.doIntent(intent, list);
        return response;
    }
    /*
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
    */
    
    static String waitForLookup(PackagesBean list, PackageBean pack)
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

    static String expandDate(String date)
    {
        int month = Integer.parseInt(date.substring(4, 6));
        int day = Integer.parseInt(date.substring(6, 8));
        return MONTHS[month-1]+" "+EnumerationUtils.ORDINAL[day-1];
    }
    static String expandTime(String time)
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
    static String expand(String tn)
    {
        StringBuffer sb = new StringBuffer(tn.length()*2);
        for (char c : tn.toCharArray())
        {
            sb.append(' ');
            sb.append(c);
        }
        return sb.toString();
    }
    
    private static String doHelp(PackagesBean list)
    {
        String huh = "";
        huh += HELP[list.getHelpOff()%HELP.length];
        list.setHelpOff(list.getHelpOff() + 1);
        return huh;
    }
    
    private static final String[] HELP = {
        "Please say 'status' to get an update on previous tracking numbers or tell me a tracking number.",
        "When saying a tracking number, please pronounce each digit separately.",
        "If I'm having trouble undestanding letters, try saying a word that begins with the letter, such as alpha, bravo, charlie.",        
    };
    
    private static PackagesBean getData(Session session)
    {
        String id = SessionUtils.getUserID(session);
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
