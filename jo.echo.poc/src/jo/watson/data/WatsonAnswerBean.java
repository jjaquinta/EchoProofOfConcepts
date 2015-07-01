package jo.watson.data;

public class WatsonAnswerBean
{
    private String  mAnswer;
    private double  mConfidence;
    
    public String getAnswer()
    {
        return mAnswer;
    }
    public void setAnswer(String answer)
    {
        mAnswer = answer;
    }
    public double getConfidence()
    {
        return mConfidence;
    }
    public void setConfidence(double confidence)
    {
        mConfidence = confidence;
    }
}
