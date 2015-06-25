package jo.jose.logic;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import jo.d4w.web.data.PortBean;
import jo.d4w.web.data.PortsBean;
import jo.d4w.web.logic.URILogic;
import jo.echo.util.BaseServlet;
import jo.jose.JoseServlet;

public class PortsLogic
{
    public static PortsBean getNearbyPorts(String portURI, int radius) throws IOException
    {
        URI u;
        try
        {
            u = new URI(portURI);
        }
        catch (URISyntaxException e)
        {
            throw new IOException(e);
        }
        String subURI = "ports://" + u.getAuthority()+"/"+radius*3.26;
        BaseServlet.log(JoseServlet.class, "querying "+subURI);
        return (PortsBean)URILogic.getFromURI(subURI);
    }

    public static PortBean findPort(PortsBean ports, String locationName)
    {
        List<PortBean> ps = getPorts(ports);
        for (PortBean p : ps)
        {
            if (locationName.equalsIgnoreCase(p.getName()))
                return p;
        }
        return null;
    }

    public static List<PortBean> getPorts(PortsBean ports)
    {
        return ports.getPorts();
    }
}
