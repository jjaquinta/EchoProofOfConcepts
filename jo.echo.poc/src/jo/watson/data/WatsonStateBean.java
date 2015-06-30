package jo.watson.data;

public class WatsonStateBean
{
    private boolean mShowLowConfidence;
    
    public WatsonStateBean()
    {
        mShowLowConfidence = true;
    }

    public boolean isShowLowConfidence()
    {
        return mShowLowConfidence;
    }

    public void setShowLowConfidence(boolean showLowConfidence)
    {
        mShowLowConfidence = showLowConfidence;
    }
}
