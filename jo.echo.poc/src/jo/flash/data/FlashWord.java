package jo.flash.data;

import java.util.ArrayList;
import java.util.List;

public class FlashWord
{
    private String          mWord;
    private List<String>    mNames;
    
    public FlashWord()
    {
        mNames = new ArrayList<String>();
    }
    
    public String getWord()
    {
        return mWord;
    }
    public void setWord(String word)
    {
        mWord = word;
    }
    public List<String> getNames()
    {
        return mNames;
    }
    public void setNames(List<String> names)
    {
        mNames = names;
    }
}
