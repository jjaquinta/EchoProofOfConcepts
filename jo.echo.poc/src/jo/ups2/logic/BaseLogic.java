package jo.ups2.logic;

import java.util.ArrayList;
import java.util.List;

import jo.echo.util.EnumerationUtils;
import jo.echo.util.ResponseUtils;
import jo.ups2.data.PackageBean;
import jo.ups2.data.PackagesBean;

import com.amazon.speech.slu.Intent;
import com.ups.xolt.codesamples.response.jaxb.Activity;
import com.ups.xolt.codesamples.response.jaxb.PackageType;
import com.ups.xolt.codesamples.response.jaxb.Shipment;
import com.ups.xolt.codesamples.response.jaxb.TrackResponse;

public class BaseLogic
{
    public static String doIntent(Intent intent, PackagesBean list)
    {
        String response = "I'm not quite sure what you mean.[[reprompt=Say 'status', 'add' or 'remove'.]]";
        if (intent.getName().equals("NUMBERS"))
            response = doNumbers(intent, list);
        else if (intent.getName().equals("STATUS"))
            response = doStatus(list);
        else if (intent.getName().equals("REMOVE"))
            response = doRemove(intent, list);
        else if (intent.getName().equals("ADD"))
            response = doAdd(list);
        else if (intent.getName().equals("CLEAR"))
            response = doClear(list);
        else if (intent.getName().equals("LIST"))
            response = doList(list);
        return response;
    }

    private static String doNumbers(Intent intent, PackagesBean list)
    {
        list.setState(PackagesBean.ADD0);
        list.setCurrentNumber("");
        return AddLogic.doIntent(intent, list);
    }

    private static String doStatus(PackagesBean list)
    {
        String resp = UPSAppLogic.waitForLookup(list, null);
        if (resp != null)
            return resp;
        StringBuffer sb = new StringBuffer();
        String lastDate = null;
        for (PackageBean pack : list.getPackages())
        {
            sb.append("Tracking "+UPSAppLogic.expand(pack.getTrackingID())+". ");
            TrackResponse trackResponse = pack.getInfo();
            if (trackResponse == null)
                sb.append("No information yet on this package.");
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
                        if (shipment.getPackage().size() > 1)
                            sb.append(" "+EnumerationUtils.ORDINAL[j+1]+" package. ");
                        Activity activity = pkg.getActivity().get(0);
                        sb.append(activity.getStatus().getStatusType().getDescription());
                        sb.append(" at "+UPSAppLogic.expandTime(activity.getTime()));
                        if (!activity.getDate().equals(lastDate))
                            sb.append(" on "+UPSAppLogic.expandDate(activity.getDate()));
                        lastDate = activity.getDate();
                        sb.append(". ");
                    }
                }
            }
        }
        sb.append("[[reprompt=Say 'add' to add a new tracking number.]]");
        return sb.toString();
    }

    private static String doRemove(Intent intent, PackagesBean list)
    {
        list.setState(PackagesBean.REMOVE0);
        return RemoveLogic.doIntent(intent, list);
    }

    private static String doList(PackagesBean list)
    {
        if (list.getPackages().size() == 0)
        {
            list.setState(PackagesBean.BASE);
            return "You have no packages.[[reprompt=Say 'add' to add a package.]]";
        }
        List<String> packs = new ArrayList<String>();
        List<String> card = new ArrayList<String>();
        for (int i = 0; i < list.getPackages().size(); i++)
        {
            PackageBean pack = list.getPackages().get(i);
            packs.add(UPSAppLogic.expand(pack.getTrackingID()));
            card.add(pack.getTrackingID());
        }
        StringBuffer resp = new StringBuffer();
        resp.append("You are tracking "+ResponseUtils.wordList(packs)+". ");
        resp.append("[[card="+"You are tracking "+ResponseUtils.wordList(card)+"."+"]]");
        list.setState(PackagesBean.REMOVE1);
        return resp.toString();
    }

    private static String doAdd(PackagesBean list)
    {
        list.setState(PackagesBean.ADD0);
        list.setCurrentNumber("");
        return "Please say the first three digits of your tracking number.[[reprompt=++]]";
    }

    private static String doClear(PackagesBean list)
    {
        list.getPackages().clear();
        return "Your list has been cleared.[[reprompt=say 'add' to add a new tracking number.]]";
    }
    
}
