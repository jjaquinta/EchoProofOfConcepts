package helloworld;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.amazonaws.services.lambda.runtime.Context;

import jo.echo.lambda.utils.SpeechletLambda;

public class HelloWorldLambda extends SpeechletLambda
{
    /**
     * 
     */
    private static final long serialVersionUID = 6998188851979224629L;

    static
    {
        System.setProperty("com.amazon.speech.speechlet.servlet.disableRequestSignatureCheck", "true");
    }
    
    public HelloWorldLambda()
    {
        this.setSpeechlet(new HelloWorldSpeechlet());        
    }
    
    @Override
    public void handleRequest(InputStream inputStream,
            OutputStream outputStream, Context context) throws IOException
    {
        super.handleRequest(inputStream, outputStream, context);
    }
}
