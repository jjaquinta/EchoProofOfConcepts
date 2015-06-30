package jo.watson.logic;

import java.util.Properties;

import jo.echo.util.BaseServlet;
import jo.ups2.UPS2Servlet;
import jo.watson.WatsonServlet;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.ups.xolt.codesamples.UPSTrackLogic;

public class WatsonLogic
{
    private static final String USER_NAME = "username";
    private static final String PASSWORD = "password";
    private static final String ENDPOINT_URL="baseURL";
    private static Properties mProps = null; 
    static {
        mProps = new Properties();
        try{
            mProps.load(UPSTrackLogic.class.getClassLoader().getResourceAsStream("jo/watson/logic/watson.properties"));
        }catch (Exception e) {
            BaseServlet.log(UPS2Servlet.class, e);
            e.printStackTrace();
        } 
    }
    
    public static Object ask(String question, String dataset)
    {
        //create the { 'question' : {
        //  'questionText:'...',
        //  'evidenceRequest': { 'items': 5} } json as requested by the service
        JSONObject questionJson = new JSONObject();
        questionJson.put("questionText",question);
        questionJson.put("items",1);

        JSONObject postData = new JSONObject();
        postData.put("question",questionJson);
        String baseURL = mProps.getProperty(ENDPOINT_URL);
        String username = mProps.getProperty(USER_NAME);
        String password = mProps.getProperty(PASSWORD);
        
        try {
            JSONArray pipelines = (JSONArray)BlueMixLogic.connect(baseURL+ "/v1/question/"+dataset, username, password, postData);
            // the response has two pipelines, lets use the first one
            JSONObject answersJson = (JSONObject) pipelines.get(0);
            JSONArray answers = (JSONArray) ((JSONObject) answersJson.get("question")).get("evidencelist");

            if (answers.size() > 0) 
            {
                JSONObject answer = (JSONObject) answers.get(0);
                String text = (String)answer.get("text");
                double p = Double.parseDouble((String)answer.get("value"));
                p = Math.floor(p * 100);
                text += "(confidence "+Double.toString(p) + "%)";
                return text;
            }
        } catch (Exception e) {
            BaseServlet.log(WatsonServlet.class, e);
            return "I got an error while trying to find that out "+e;
        }

        return "I don't know the answer to that with any confidence.";
    }

}
