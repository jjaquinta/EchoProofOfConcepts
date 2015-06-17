package jo.gl.logic;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public class PluralLogic
{
    private static boolean mInit = false;
    private static final Map<String,String> mSingularToPlural = new HashMap<String,String>();
    private static final Map<String,String> mPluralToSingular = new HashMap<String,String>();
    
    private static void init()
    {
        if (mInit)
            return;
        try
        {
            InputStream is = PluralLogic.class.getClassLoader().getResourceAsStream("jo/gl/logic/item_list.csv");
            BufferedReader rdr = new BufferedReader(new InputStreamReader(is, "utf-8"));
            for (;;)
            {
                String inbuf = rdr.readLine();
                if (inbuf == null)
                    break;
                StringTokenizer st = new StringTokenizer(inbuf, ",");
                if (st.countTokens() < 2)
                    continue;
                String singular = st.nextToken().toLowerCase();
                String plural = st.nextToken().toLowerCase();
                if (singular.startsWith("#"))
                    continue;
                mSingularToPlural.put(singular, plural);
                mSingularToPlural.put(plural, plural);
                mPluralToSingular.put(plural, singular);
                mPluralToSingular.put(singular, singular);
                while (st.hasMoreTokens())
                {
                    String tok = st.nextToken().toLowerCase();
                    mSingularToPlural.put(tok, plural);
                    mPluralToSingular.put(tok, singular);
                }
            }
            rdr.close();
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        mInit = true;
    }
    
    public static String makeSingular(String word)
    {
        init();
        if (mPluralToSingular.containsKey(word))
            return mPluralToSingular.get(word.toLowerCase());
        else
            return word.toLowerCase();
    }
    
    public static String makePlural(String word)
    {
        init();
        if (mSingularToPlural.containsKey(word))
            return mSingularToPlural.get(word.toLowerCase());
        else
            return word.toLowerCase();
    }
}
