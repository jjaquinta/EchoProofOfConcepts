package jo.flash.data;

import java.util.HashMap;
import java.util.Map;

public class FlashState
{
    private int mCurrentGame;
    private FlashWord   mCurrentWord;
    private Map<Integer, Map<String, Integer>> mHistory;
    private long mLastActivity;
    private long mLastWord;
    
    public FlashState()
    {
        mCurrentGame = -1;
        mHistory = new HashMap<Integer, Map<String,Integer>>();
    }

    public int getCurrentGame()
    {
        return mCurrentGame;
    }

    public void setCurrentGame(int currentGame)
    {
        mCurrentGame = currentGame;
    }

    public Map<Integer, Map<String, Integer>> getHistory()
    {
        return mHistory;
    }

    public void setHistory(Map<Integer, Map<String, Integer>> history)
    {
        mHistory = history;
    }

    public FlashWord getCurrentWord()
    {
        return mCurrentWord;
    }

    public void setCurrentWord(FlashWord currentWord)
    {
        mCurrentWord = currentWord;
    }

    public long getLastActivity()
    {
        return mLastActivity;
    }

    public void setLastActivity(long lastActivity)
    {
        mLastActivity = lastActivity;
    }

    public long getLastWord()
    {
        return mLastWord;
    }

    public void setLastWord(long lastWord)
    {
        mLastWord = lastWord;
    }
}
