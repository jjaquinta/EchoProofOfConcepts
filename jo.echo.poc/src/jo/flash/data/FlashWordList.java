package jo.flash.data;

import java.util.ArrayList;
import java.util.List;

public class FlashWordList
{
    private String  mName;
    private List<FlashWord> mWords;
    
    public FlashWordList()
    {
        mWords = new ArrayList<FlashWord>();
    }
    
    public String getName()
    {
        return mName;
    }
    public void setName(String name)
    {
        mName = name;
    }
    public List<FlashWord> getWords()
    {
        return mWords;
    }
    public void setWords(List<FlashWord> words)
    {
        mWords = words;
    }
}
