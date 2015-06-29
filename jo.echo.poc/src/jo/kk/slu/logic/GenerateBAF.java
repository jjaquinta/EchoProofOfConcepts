package jo.kk.slu.logic;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashSet;
import java.util.Set;

public class GenerateBAF
{

    public static void main(String[] args) throws IOException
    {
        if (args.length != 2)
            throw new IllegalStateException("Needs two arguments");
        File inFile = new File(args[0]);
        File outFile = new File(args[1]);
        BufferedReader rdr = new BufferedReader(new InputStreamReader(new FileInputStream(inFile), "utf-8"));
        int state = 0;
        Set<String> responses = new HashSet<String>();
        for (;;)
        {
            String inbuf = rdr.readLine();
            if (inbuf == null)
                break;
            inbuf = inbuf.trim();
            inbuf = inbuf.toLowerCase();
            if (inbuf.length() == 0)
            {
                if (state != 0)
                    System.out.println(" },");
                state = 0;
            }
            else if (state == 0)
            {
                System.out.print("{ \""+inbuf+"\", ");
                state = 1;
            }
            else if (state == 1)
            {
                responses.add(inbuf);
                state = 2;
            }
            else if (state == 2)
            {
                System.out.print(" \""+inbuf+"\", ");
                state = 1;
            }
        }
        if (state != 0)
            System.out.println(" },");
        rdr.close();
        BufferedWriter wtr = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outFile), "utf-8"));
        for (String response : responses)
        {
            wtr.append("RESPONSE\t"+response);
            wtr.newLine();
        }
        wtr.append("RESPONSE\t{anything|anything}");wtr.newLine();
        wtr.append("QUIT\tdone");wtr.newLine();
        wtr.append("QUIT\tquit");wtr.newLine();
        wtr.append("QUIT\texit");wtr.newLine();
        wtr.append("QUIT\tI'm done");wtr.newLine();
        wtr.append("QUIT\tgo away");wtr.newLine();
        wtr.newLine();
        wtr.close();
    }


}
