package jo.watson.logic;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import jo.echo.util.BaseServlet;
import jo.watson.WatsonServlet;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class BlueMixLogic
{
    private static JSONParser mParser = new JSONParser();
    
    public static Object connect(String httpsURL, String user, String pass, JSONObject data) throws IOException
    {
            URL watsonURL = new URL(httpsURL);
            HttpsURLConnection con = (HttpsURLConnection)watsonURL.openConnection();
            String userPassword = user + ":" + pass;
            String encoding = Base64.encodeBytes(userPassword.getBytes());
            con.setRequestProperty("Authorization", "Basic " + encoding);
            con.setRequestProperty("Accept", "application/json");
            con.setRequestProperty("X-SyncTimeout", "30");
            con.setRequestProperty("Content-Type", "application/json");
            if (data != null)
            {
                con.setDoOutput(true);
                OutputStream os = con.getOutputStream();
                String jsonString = data.toJSONString();
                BaseServlet.log(WatsonServlet.class, "Sending: "+jsonString);
                os.write(jsonString.getBytes("utf-8"));
                os.flush();
            }
            InputStream ins = con.getInputStream();
            Object jobj;
            try
            {
                jobj = mParser.parse(new InputStreamReader(ins, "utf-8"));
                if (jobj instanceof JSONArray)
                    BaseServlet.log(WatsonServlet.class, "Receiving: "+((JSONArray)jobj).toJSONString());
            }
            catch (ParseException e)
            {
                throw new IOException(e);
            }
            return jobj;
    }
}
