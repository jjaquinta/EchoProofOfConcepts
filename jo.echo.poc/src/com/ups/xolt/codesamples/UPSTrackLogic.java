package com.ups.xolt.codesamples;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Properties;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import jo.echo.util.BaseServlet;
import jo.ups2.UPS2Servlet;

import com.ups.xolt.codesamples.accessrequest.jaxb.AccessRequest;
import com.ups.xolt.codesamples.request.jaxb.Request;
import com.ups.xolt.codesamples.request.jaxb.TrackRequest;
import com.ups.xolt.codesamples.response.jaxb.Activity;
import com.ups.xolt.codesamples.response.jaxb.PackageType;
import com.ups.xolt.codesamples.response.jaxb.Shipment;
import com.ups.xolt.codesamples.response.jaxb.TrackResponse;

public class UPSTrackLogic
{
    private static final String LICENSE_NUMBER = "accesskey";
    private static final String USER_NAME = "username";
    private static final String PASSWORD = "password";
    private static final String ENDPOINT_URL="url";
    private static Properties mProps = null; 
    static {
        mProps = new Properties();
        try{
            mProps.load(UPSTrackLogic.class.getClassLoader().getResourceAsStream("com/ups/xolt/codesamples/ups.properties"));
        }catch (Exception e) {
            BaseServlet.log(UPS2Servlet.class, e);
            e.printStackTrace();
        } 
    }
    

    public static TrackResponse lookup(String trackingNumber) {    
        trackingNumber = trackingNumber.toUpperCase();
        StringWriter strWriter = null;
        try {       
            BaseServlet.log(UPS2Servlet.class, "**********************************************");
            BaseServlet.log(UPS2Servlet.class, "Looking up '"+trackingNumber+"'");
            //Create JAXBContext and marshaller for AccessRequest object                    
            JAXBContext accessRequestJAXBC = JAXBContext.newInstance(AccessRequest.class.getPackage().getName() );              
            Marshaller accessRequestMarshaller = accessRequestJAXBC.createMarshaller();
            com.ups.xolt.codesamples.accessrequest.jaxb.ObjectFactory accessRequestObjectFactory = new com.ups.xolt.codesamples.accessrequest.jaxb.ObjectFactory();
            AccessRequest accessRequest = accessRequestObjectFactory.createAccessRequest();
            populateAccessRequest(accessRequest);
             
            //Create JAXBContext and marshaller for TrackRequest object
            JAXBContext trackRequestJAXBC = JAXBContext.newInstance(TrackRequest.class.getPackage().getName() );                
            Marshaller trackRequestMarshaller = trackRequestJAXBC.createMarshaller();
            com.ups.xolt.codesamples.request.jaxb.ObjectFactory requestObjectFactory = new com.ups.xolt.codesamples.request.jaxb.ObjectFactory();
            TrackRequest trackRequest = requestObjectFactory.createTrackRequest();
            populateTrackRequest(trackRequest, trackingNumber);
            
            //Get String out of access request and track request objects.
            strWriter = new StringWriter();                    
            accessRequestMarshaller.marshal(accessRequest, strWriter);
            trackRequestMarshaller.marshal(trackRequest, strWriter);
            strWriter.flush();
            strWriter.close();
            BaseServlet.log(UPS2Servlet.class, "Request: " + strWriter.getBuffer().toString());
            
            String strResults =contactService(strWriter.getBuffer().toString());
            BaseServlet.log(UPS2Servlet.class, "Results: " + strResults);
            
            //Parse response object
            JAXBContext trackResponseJAXBC = JAXBContext.newInstance(TrackResponse.class.getPackage().getName());
            Unmarshaller trackUnmarshaller = trackResponseJAXBC.createUnmarshaller();
            ByteArrayInputStream input = new ByteArrayInputStream(strResults.getBytes());
            Object objResponse = trackUnmarshaller.unmarshal(input);
            TrackResponse trackResponse = (TrackResponse)objResponse;
            BaseServlet.log(UPS2Servlet.class, trackResponse.getResponse().getResponseStatusDescription());
            return trackResponse;           
        } catch (Exception e) {
            for (Throwable ex = e; ex != null; ex = ex.getCause())
            {
            BaseServlet.log(UPS2Servlet.class, ex.getMessage());
            for (StackTraceElement ele : ex.getStackTrace())
                BaseServlet.log(UPS2Servlet.class, "  "+ele.toString());
            }
            e.printStackTrace();
            return null;
        } finally{
            try{
                if(strWriter != null){
                    strWriter.close();
                    strWriter = null;
                }
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    }    
    
    private static String contactService(String xmlInputString) throws Exception{       
        String outputStr = null;
        OutputStream outputStream = null;
        try {

            URL url = new URL(mProps.getProperty(ENDPOINT_URL));
            
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            BaseServlet.log(UPS2Servlet.class, "Client established connection with " + url.toString());
            // Setup HTTP POST parameters
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setUseCaches(false);
            
            outputStream = connection.getOutputStream();        
            outputStream.write(xmlInputString.getBytes());
            outputStream.flush();
            outputStream.close();
            BaseServlet.log(UPS2Servlet.class, "Http status = " + connection.getResponseCode() + " " + connection.getResponseMessage());
            
            outputStr = readURLConnection(connection);      
            connection.disconnect();
        } catch (Exception e) {
            BaseServlet.log(UPS2Servlet.class, "Error sending data to server");
            e.printStackTrace();
            throw e;
        } finally {                     
            if(outputStream != null){
                outputStream.close();
                outputStream = null;
            }
        }       
        return outputStr;
    }
    
    /**
     * This method read all of the data from a URL connection to a String
     */

    public static String readURLConnection(URLConnection uc) throws Exception {
        StringBuffer buffer = new StringBuffer();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(uc.getInputStream()));
            int letter = 0;         
            //reader.readLine();
            while ((letter = reader.read()) != -1){
                buffer.append((char) letter);
            }
            reader.close();
        } catch (Exception e) {
            BaseServlet.log(UPS2Servlet.class, "Could not read from URL: " + e.toString());
            throw e;
        } finally {
            if(reader != null){
                reader.close();
                reader = null;
            }
        }
        return buffer.toString();
    }

    /**
     * Populates the access request object.
     * @param accessRequest
     */
    private static void populateAccessRequest(AccessRequest accessRequest){
        accessRequest.setAccessLicenseNumber(mProps.getProperty(LICENSE_NUMBER));
        accessRequest.setUserId(mProps.getProperty(USER_NAME));
        accessRequest.setPassword(mProps.getProperty(PASSWORD));
    }
   
    /**
     * Populate TrackRequest object
     * @param trackRequest
     */
    private static void populateTrackRequest(TrackRequest trackRequest, String trackingNumber){    
        Request request = new Request(); 
          
        List<String> optoinsList = request.getRequestOption();
        optoinsList.add("activity"); //If the request option here is of 2 ~ 15, then Signature tracking must validate the rights to signature tracking.
        request.setRequestAction("Track");
        trackRequest.setRequest(request);
        trackRequest.setTrackingNumber(trackingNumber);
        trackRequest.setIncludeFreight("01");
    }

    public static void main(String[] argv)
    {
        TrackResponse trackResponse = lookup("1Z12345E0291980793");
        BaseServlet.log(UPS2Servlet.class, "Response Status: " + trackResponse.getResponse().getResponseStatusCode());
        System.out.println("Response Status Description: " + trackResponse.getResponse().getResponseStatusDescription());
        System.out.println(trackResponse.getShipment().size()+" shipments");
        for (int i = 0; i < trackResponse.getShipment().size(); i++)
        {
            Shipment shipment = trackResponse.getShipment().get(i);
            System.out.println("  Shipment #"+(i+1)+" has "+shipment.getPackage().size()+" packages");
            for (int j = 0; j < shipment.getPackage().size(); j++)
            {
                PackageType pkg = shipment.getPackage().get(j);
                System.out.println("    Package "+pkg.getTrackingNumber()+" has "+pkg.getActivity().size()+" activities");
                for (int k = 0; k < pkg.getActivity().size(); k++)
                {
                    Activity activity = pkg.getActivity().get(k);
                    System.out.println("      At "+activity.getTime()+" on "+activity.getDate()+": "+activity.getStatus().getStatusType().getDescription());
                }
            }
        }

    }
}
