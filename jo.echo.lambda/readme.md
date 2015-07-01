Creating a Skill with Java and Lambda
=====================================

Amazon has a Lambda service that provides an easy way to deploy and update code. It is kind of an easy wrapper around AWS. (You can find more details [here](http://aws.amazon.com/lambda/).) You can create an Echo skill using Lambda. The main advantage is that you don't need to manage your own SSL certificate. That's often a painful and error prone part to it. So it's a big plus. 

How to use Lambda to deploy a Node.js skill is [well covered](https://developer.amazon.com/public/solutions/alexa/alexa-skills-kit/docs/developing-an-alexa-skill-as-a-lambda-function). But Java is less so. We will walk you through the steps you need here. This will use Eclipse as the IDE, but similar steps should work in principle for other IDEs.

First, install the AWS Toolkit into Eclipse. Instructions can be found [here](http://docs.aws.amazon.com/AWSToolkitEclipse/latest/GettingStartedGuide/Welcome.html).
Then create an AWS Lambda Java project. Pick a name for your package and handler class. The Input Type and other information isn't important, since we're going to overwrite that.

You can name it whatever you want, but it is import in the “Project Layout” section of the first page that you select “Use project folder as root for sources and class files”. This will make it easier to export to Lambda in the end.

Click “Next” to get to the Java Settings page. Select the “src” folder and select “Remove 'src' folder from build path”. Then select the project root and click “Add project 'xxx' to build path”. Remove the “/bin” from the end of the “Default output folder”. Click Finish. Then delete the same classes and packages it creates for you.

(We do this to make it easier to export our Lambda function as a zip file. If Amazon have fixed their “Upload function to AWS Lambda” function, then you probably don't need to do all this.)

Once created, make a folder called “lib” in the root of your project. As per step 4 & 5 from Using The Alexa Skills Kit Samples – Java, section Creating the AWS Java Web Project and Importing the Code (https://developer.amazon.com/public/solutions/alexa/alexa-skills-kit/docs/using-the-alexa-skills-kit-samples-java#creating-the-aws-java-web-project-and-importing-the-code) you need to copy these files into the lib directory from the AlexSkillsKit:
* commons-codec-1.6.jar
* commons-lang3-3.x.jar
* jackson-annotations-2.3.2.jar
* jackson-core-2.3.2.jar
* jackson-databind-2.3.2.jar
* JakartaCommons-IO-2.4.jar
* joda-time-2.3.jar
* log4j.1.2.17.jar
* servlet-api-3.0.jar
* slf4j-api-1.7.4.jar
* slf4j-log4j12-1.7.4.jar
* alexa-skills-kit-1.0.jar 
These need to be added to the build path.

From the jo.echo.lambda sample project on [GitHub](https://github.com/jjaquinta/EchoProofOfConcepts) add the jo.echo.lambda.utils package and its contents. This contains SpeechletLambda, which is a replacement for the SpeechletServer used in the Alexa Skills Kit sample.

You can now create your handler and speechlet for your application. As a short cut, you can copy in the helloworld example from the Alexa Skills Kit. If you do, rename HelloWorldServlet to HelloWorldLambda. Then change it from inheriting from SpeechletServlet to SpeechletLambda.

There seems to be a bit of oddness in how the class of a Lambda function is instantiated. I have found that the constructor on it is not called if the handler method is not on the class. To get around this, you need to override the handler method on your skill's class. This can just simple call the super class method. Just inserting this code should work fine:
    @Override
    public void handleRequest(InputStream inputStream,
            OutputStream outputStream, Context context) throws IOException
    {
        super.handleRequest(inputStream, outputStream, context);
    }

You are now ready to export your Lambda function as a ZIP file. Right click your project and select Export. Choose General → File System. Within your project choose the “lib” directory, and any directory containing class files. Pick a scratch directory to export them to. Once exported, go to that directory on your file system. Use whatever zip utility you have with your operating system and create a zip file of all the contents of that directory. Note: the ZIP file needs to have the contents of that directory in the root of the zip. (This is why we can't create this directly from Eclipse. That puts all the files in a sub-directory.)

You can now follow the instructions in [Developing An Alexa Skill as a Lambda Function](https://developer.amazon.com/public/solutions/alexa/alexa-skills-kit/docs/developing-an-alexa-skill-as-a-lambda-function). When filling out the “Lambda function code” section, select “Java8” as the Runtime, and give it the ZIP file you just exported. For Handler, you need to give the dot-delimited path to the class and package you picked for your handler and add ::handleRequest to the end. This would be helloworld.HelloWorldLambda::handleRequest for the hello world example. Be sure to get this right. You don't appear to be able to go back and edit it once you've added it! Select a role, test your handler, and hook it up to the Echo following the normal directions.

Troubleshooting.
----------------
If, when you test, you get a “Class not found” message, it's probably because there is a missing library. Check your ZIP file and make sure the “lib” directory is in the root of the ZIP file and that it contains all the jar files your skill needs.

If your test runs, but only returns back a JSON block like this:
    {
      "version": "1.0",
      "sessionAttributes": {}
    }
with a “java.lang.NullPointerException” reported in “SpeechletLambda.java:118” listed in the execution log, you probably forgot to override the handler class.