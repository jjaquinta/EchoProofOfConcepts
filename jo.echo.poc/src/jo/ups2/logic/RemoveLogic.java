package jo.ups2.logic;

import jo.echo.util.EnumerationUtils;
import jo.ups2.data.PackageBean;
import jo.ups2.data.PackagesBean;

import com.amazon.speech.slu.Intent;
import com.amazon.speech.slu.Slot;

public class RemoveLogic
{
    public static String doIntent(Intent intent, PackagesBean list)
    {
        String response = "I'm not quite sure what you mean.[[reprompt=Say 'status', 'add' or 'remove'.]]";
        if (list.getState() == PackagesBean.REMOVE0)
            response = doListPackages(list);
        else if (list.getState() == PackagesBean.REMOVE1)
            response = doPickPackage(intent, list);
        else if (list.getState() == PackagesBean.REMOVE2)
            response = doConfirm(intent, list);
        else
            list.setState(PackagesBean.BASE);
        return response;
    }
    
    private static String doConfirm(Intent intent, PackagesBean list)
    {
        if (intent.getName().equals("YES"))
        {
            int idx = list.getDeleteRequest();
            StringBuffer resp = new StringBuffer();
            resp.append("Deleted "
            +UPSAppLogic.expand(list.getPackages().get(idx).getTrackingID())
            +". ");
            resp.append("[[reprompt=Say 'add' add a new package.]]");
            resp.append("[[card=Deleted "
                    +list.getPackages().get(idx).getTrackingID()
                    +".]]");
            list.getPackages().remove(idx);
            return resp.toString();
        }
        else 
            return doCancel(list);
    }
    
    private static String doPickPackage(Intent intent, PackagesBean list)
    {
        if (intent.getName().equals("NUMBER"))
            return doItemizePackageNumber(intent, list);
        else if (intent.getName().equals("NO"))
            return doCancel(list);
        else if (intent.getName().equals("ABORT"))
            return doCancel(list);
        else
        {
            return "I expected you to say one, two, et cetera. "+doCancel(list);
        }
    }
    
    private static String doItemizePackageNumber(Intent intent, PackagesBean list)
    {
        Slot slot = intent.getSlot("number");
        if (slot != null)
        {
            String value = slot.getValue();
            if (value != null)
            {
                Integer idx = EnumerationUtils.getEnumeration(value);
                if (idx != null)
                {
                    list.setDeleteRequest(idx);
                    list.setState(PackagesBean.REMOVE2);
                    StringBuffer resp = new StringBuffer();
                    resp.append("If you want to delete "
                    +UPSAppLogic.expand(list.getPackages().get(idx).getTrackingID())
                    +" say 'yes'. ");
                    resp.append("[[reprompt=Say yes to delete "
                            +UPSAppLogic.expand(list.getPackages().get(idx).getTrackingID())
                            +".]]");
                    resp.append("[[card=Do you want to delete "
                            +list.getPackages().get(idx).getTrackingID()
                            +".]]");
                    return resp.toString();
                }
            }
        }
        return doCancel(list);
    }
    
    private static String doCancel(PackagesBean list)
    {
        list.setState(PackagesBean.BASE);
        return "Cancelling package removal.[[reprompt=Say 'status' to list package statuses.]]";
    }
    
    private static String doListPackages(PackagesBean list)
    {
        if (list.getPackages().size() == 0)
        {
            list.setState(PackagesBean.BASE);
            return "You have no packages to remove.[[reprompt=Say 'add' to add a package.]]";
        }
        StringBuffer resp = new StringBuffer();
        StringBuffer card = new StringBuffer();
        for (int i = 0; i < list.getPackages().size(); i++)
        {
            PackageBean pack = list.getPackages().get(i);
            resp.append("To remove "+UPSAppLogic.expand(pack.getTrackingID())+", say '"+EnumerationUtils.CARDINAL[i]+". ");
            card.append("To remove "+pack.getTrackingID()+", say '"+EnumerationUtils.CARDINAL[i]+". ");
        }
        resp.append("[[card="+card+"]]");
        list.setState(PackagesBean.REMOVE1);
        return resp.toString();
    }
}
