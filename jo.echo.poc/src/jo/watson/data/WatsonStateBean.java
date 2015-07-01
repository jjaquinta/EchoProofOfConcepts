package jo.watson.data;


public class WatsonStateBean
{
    public static final int BASE = 0;
    public static final int QUERY_SHOW = 1;
    public static final int SETTINGS_LOW = 3;
    
    private Boolean mShowLowConfidence;
    private int mState;
    private String  mQuestion;
    private WatsonAnswerBean mAnswer;
    
    public WatsonStateBean()
    {
        mShowLowConfidence = null;
    }

    public Boolean isShowLowConfidence()
    {
        return mShowLowConfidence;
    }

    public void setShowLowConfidence(Boolean showLowConfidence)
    {
        mShowLowConfidence = showLowConfidence;
    }

    public int getState()
    {
        return mState;
    }

    public void setState(int state)
    {
        mState = state;
    }

    public String getQuestion()
    {
        return mQuestion;
    }

    public void setQuestion(String question)
    {
        mQuestion = question;
    }

    public WatsonAnswerBean getAnswer()
    {
        return mAnswer;
    }

    public void setAnswer(WatsonAnswerBean answer)
    {
        mAnswer = answer;
    }
}
