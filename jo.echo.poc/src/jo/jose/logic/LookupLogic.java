package jo.jose.logic;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import jo.echo.util.BaseServlet;
import jo.jose.JoseServlet;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class LookupLogic
{
    private static JSONParser   mParser = new JSONParser();
    
    public static JSONObject query(String subUri) throws IOException
    {
        BaseServlet.log(JoseServlet.class, "Loading "+subUri);
        String uri = "http://www.ocean-of-storms.com/d2k_web/api4w?f=json&u="+URLEncoder.encode(subUri, "utf-8");
        URL u = new URL(uri);
        InputStream is = u.openStream();
        JSONObject ret;
        try
        {
            ret = (JSONObject)mParser.parse(new InputStreamReader(is, "utf-8"));
            BaseServlet.log(JoseServlet.class, ret.toJSONString());
        }
        catch (ParseException e)
        {
            ret = null;
            BaseServlet.log(JoseServlet.class, "<null>");
        }
        return ret;
    }
    
    public static void store(String subUri, JSONObject data) throws IOException
    {
        BaseServlet.log(JoseServlet.class, "Saving "+subUri);
        String uri = "http://www.ocean-of-storms.com/d2k_web/store4w";
        URL u = new URL(uri);
        HttpURLConnection conn = (HttpURLConnection)u.openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setDoInput(true);
        String d = "uri="+URLEncoder.encode(subUri, "utf-8");
        String json = data.toJSONString();
        d += "&json="+URLEncoder.encode(json, "utf-8");
        OutputStream os = conn.getOutputStream();
        os.write(d.getBytes("utf-8"));
        os.flush();
        os.close();
        InputStream is = conn.getInputStream();
        StringBuffer resp = new StringBuffer();
        for (;;)
        {
            int ch = is.read();
            if (ch == -1)
                break;
            resp.append((char)ch);
        }
        is.close();
        BaseServlet.log(JoseServlet.class, resp.toString());
        BaseServlet.log(JoseServlet.class, json);
        BaseServlet.log(JoseServlet.class, d);
    }
}
