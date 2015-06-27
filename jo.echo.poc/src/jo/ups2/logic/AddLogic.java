package jo.ups2.logic;

import jo.echo.util.EnumerationUtils;
import jo.ups2.data.PackageBean;
import jo.ups2.data.PackagesBean;

import com.amazon.speech.slu.Intent;
import com.amazon.speech.slu.Slot;

public class AddLogic
{

    public static String doIntent(Intent intent, PackagesBean list)
    {
        String response = "I'm not quite sure what you mean.[[reprompt=Say 'status', 'add' or 'remove'.]]";
        if (intent.getName().equals("NUMBERS"))
            response = doNumbers(intent, list);
        else if (intent.getName().equals("YES"))
            response = doYes(list);
        else if (intent.getName().equals("NO"))
            response = doNo(list);
        else if (intent.getName().equals("ABORT"))
            response = doAbort(list);
        return response;
    }

    private static String doYes(PackagesBean list)
    {
        StringBuffer resp = new StringBuffer();
        if (list.getState() < PackagesBean.ADD6)
        {
            resp.append("Great! Can you please say the ");
            resp.append(EnumerationUtils.ORDINAL[(list.getState() - PackagesBean.ADD0)*3 + 0]);
            resp.append(", ");
            resp.append(EnumerationUtils.ORDINAL[(list.getState() - PackagesBean.ADD0)*3 + 1]);
            resp.append(", and ");
            resp.append(EnumerationUtils.ORDINAL[(list.getState() - PackagesBean.ADD0)*3 + 2]);
            resp.append(" digits of the tracking number.");
            resp.append("[[reprompt=");
            resp.append("Please say the ");
            resp.append(EnumerationUtils.ORDINAL[(list.getState() - PackagesBean.ADD0)*3 + 0]);
            resp.append(", ");
            resp.append(EnumerationUtils.ORDINAL[(list.getState() - PackagesBean.ADD0)*3 + 1]);
            resp.append(", and ");
            resp.append(EnumerationUtils.ORDINAL[(list.getState() - PackagesBean.ADD0)*3 + 2]);
            resp.append(" digits of the tracking number.");
            resp.append("]]");
            resp.append("[[card=");
            resp.append("The number so far is "+list.getCurrentNumber()+".");
            resp.append("]]");            
        }
        else
        {
            PackageBean pack = PackageLogic.getPackage(list, list.getCurrentNumber());
            if (pack != null)
                resp.append("That is already on your list. If you want a status of that, please say 'status'.");
            else
            {
                pack = PackageLogic.addPackage(list, list.getCurrentNumber());
                resp.append("Great! adding "+UPSAppLogic.expand(list.getCurrentNumber())+" to the list of tracked packages.");
            }
            resp.append("[[reprompt=Say 'status' to list your packages status.]]");
            list.setState(PackagesBean.BASE);
        }
        return resp.toString();
    }

    private static String doNo(PackagesBean list)
    {
        if (list.getState() > PackagesBean.ADD0)
            list.setState(list.getState() - 1);;
        String current = list.getCurrentNumber();
        current = current.substring(0, (list.getState() - PackagesBean.ADD0)*DIGITS.length);
        list.setCurrentNumber(current);
        StringBuffer resp = new StringBuffer();
        resp.append("Lets try that again. Can you please say the ");
        resp.append(EnumerationUtils.ORDINAL[(list.getState() - PackagesBean.ADD0)*3 + 0]);
        resp.append(", ");
        resp.append(EnumerationUtils.ORDINAL[(list.getState() - PackagesBean.ADD0)*3 + 1]);
        resp.append(", and ");
        resp.append(EnumerationUtils.ORDINAL[(list.getState() - PackagesBean.ADD0)*3 + 2]);
        resp.append(" digits of the tracking number.");
        resp.append("[[reprompt=");
        resp.append("Please say the ");
        resp.append(EnumerationUtils.ORDINAL[(list.getState() - PackagesBean.ADD0)*3 + 0]);
        resp.append(", ");
        resp.append(EnumerationUtils.ORDINAL[(list.getState() - PackagesBean.ADD0)*3 + 1]);
        resp.append(", and ");
        resp.append(EnumerationUtils.ORDINAL[(list.getState() - PackagesBean.ADD0)*3 + 2]);
        resp.append(" digits of the tracking number.");
        resp.append("]]");
        resp.append("[[card=");
        resp.append("The number so far is "+list.getCurrentNumber()+".");
        resp.append("]]");
        return resp.toString();
    }

    private static String doAbort(PackagesBean list)
    {
        list.setState(PackagesBean.BASE);
        return "Cancelling add package.[[reprompt=Say 'add' to add a package, 'status' to see the status of tracked package.]]";
    }

    private static String doNumbers(Intent intent, PackagesBean list)
    {
        StringBuffer resp = new StringBuffer();
        String num = getTrackingNumber(intent);
        if (num != null)
        {
            String current = list.getCurrentNumber();
            current = current.substring(0, (list.getState() - PackagesBean.ADD0)*DIGITS.length);
            current += num;
            list.setCurrentNumber(current);
            list.setState(list.getState() + 1);
            resp.append("I heard '"+UPSAppLogic.expand(num)+"'. ");
            if (list.getState() < PackagesBean.ADD2)
            {
                resp.append("If that is correct, say 'yes'");
                if (list.getState() < PackagesBean.ADD6)
                    resp.append(" or the next three digits");
                resp.append(". ");
                resp.append("If it is wrong, say 'no'.");
            }
            resp.append("[[reprompt=");
            resp.append("If '"+UPSAppLogic.expand(num)+"' is correct, say 'yes'");
            if (list.getState() < PackagesBean.ADD6)
                resp.append(" or the next three digits");
            resp.append(". ");
            resp.append("If it is wrong, say 'no'.");
            resp.append("]]");
            resp.append("[[card=");
            resp.append("The number so far is "+current+". ");
            resp.append("If that is correct, say 'yes'");
            if (list.getState() < PackagesBean.ADD6)
                resp.append(" or the next three digits");
            resp.append(". ");
            resp.append("If it is wrong, say 'no'.");
            resp.append("]]");
        }
        else
        {
            resp.append("I did not understand that. Can you please say the ");
            resp.append(EnumerationUtils.ORDINAL[(list.getState() - PackagesBean.ADD0)*3 + 0]);
            resp.append(", ");
            resp.append(EnumerationUtils.ORDINAL[(list.getState() - PackagesBean.ADD0)*3 + 1]);
            resp.append(", and ");
            resp.append(EnumerationUtils.ORDINAL[(list.getState() - PackagesBean.ADD0)*3 + 2]);
            resp.append(" digits of the tracking number.");
            resp.append("[[reprompt=");
            resp.append("Please say the ");
            resp.append(EnumerationUtils.ORDINAL[(list.getState() - PackagesBean.ADD0)*3 + 0]);
            resp.append(", ");
            resp.append(EnumerationUtils.ORDINAL[(list.getState() - PackagesBean.ADD0)*3 + 1]);
            resp.append(", and ");
            resp.append(EnumerationUtils.ORDINAL[(list.getState() - PackagesBean.ADD0)*3 + 2]);
            resp.append(" digits of the tracking number.");
            resp.append("]]");
            resp.append("[[card=");
            resp.append("The number so far is "+list.getCurrentNumber()+".");
            resp.append("]]");
        }
        return resp.toString();
    }
    
    private static final String[] DIGITS = {
        "firstDigit",
        "secondDigit",
        "thirdDigit",
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
            Integer num = EnumerationUtils.getCardinal(v);
            if (num != null)
                tn.append(String.valueOf(num));
            else if ("oh".equalsIgnoreCase(v))
                tn.append("0");
            else if ("owe".equalsIgnoreCase(v))
                tn.append("0");
            else
                tn.append(v.charAt(0));
        }
        return tn.toString();
    }

}
