package jo.echo.util.cmd;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jo.util.utils.xml.XMLUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class GenerateBaf
{
    private String[] mArgs;
    
    private File mInput;
    private File mOutput;
    
    private Document mDoc;
    private Map<String, List<String>> mWordLists;
    
    public GenerateBaf(String[] args)
    {
        mArgs = args;
        mWordLists = new HashMap<String, List<String>>();
    }
    
    public void run()
    {
        parseArgs();
        mDoc = XMLUtils.readFile(mInput);
        if (mDoc == null)
        {
            System.err.println("Input file '"+mInput+"' does not have valid XML data in it");
            System.exit(1);
        }
        readWordLists();
        try
        {
            writeIntents();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    
    private void writeIntents() throws IOException
    {
        BufferedWriter wtr = null;
        if (mOutput != null)
            wtr = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(mOutput), "utf-8"));
        for (Node i : XMLUtils.findNodes(mDoc, "baf/intent"))
            writeIntent(wtr, new StringBuffer(), i.getFirstChild());
        if (wtr != null)
            wtr.close();
    }
    
    private void writeIntent(BufferedWriter wtr, StringBuffer sb,
            Node elem) throws IOException
    {
        if (elem == null)
        {
            if (wtr == null)
                System.out.println(sb.toString());
            else
            {
                wtr.write(sb.toString());
                wtr.newLine();
            }
            return;
        }
        int mark = sb.length();
        if (elem.getNodeName().equals("#text"))
        {
            sb.append(elem.getNodeValue());
            writeIntent(wtr, sb, elem.getNextSibling());
        }
        else if (elem.getNodeName().equals("insert"))
        {
            String name = XMLUtils.getAttribute(elem, "id");
            if ((name == null) || (name.length() == 0))
            {
                System.err.println("Insert has no ID:");
                System.err.println(XMLUtils.writeString(elem.getParentNode()));
                return;
            }
            if (!mWordLists.containsKey(name))
            {
                System.err.println("No such word list in "+XMLUtils.writeString(elem));
                return;
            }
            List<String> words = mWordLists.get(name);
            for (String word : words)
            {
                sb.append(word);
                writeIntent(wtr, sb, elem.getNextSibling());
                sb.setLength(mark);        
            }
        }
        sb.setLength(mark);        
    }

    private void readWordLists()
    {
        for (Node ws : XMLUtils.findNodes(mDoc, "baf/wordList"))
        {
            String id = XMLUtils.getAttribute(ws, "id");
            if ((id == null) || (id.length() == 0))
            {
                System.err.println("No ID for word list:");
                System.err.println(XMLUtils.writeString(ws));
                continue;
            }
            List<String> words = new ArrayList<String>();
            for (Node w : XMLUtils.findNodes(ws, "word"))
            {
                String word = XMLUtils.getText(w);
                word = word.trim();
                word = word.toLowerCase();
                if (!words.contains(word))
                    words.add(word);
            }
            mWordLists.put(id, words);
        }
    }
    
    private void parseArgs()
    {
        for (int i = 0; i < mArgs.length; i++)
            if ("-i".equalsIgnoreCase(mArgs[i]))
                mInput = new File(mArgs[++i]);
            else if ("-o".equalsIgnoreCase(mArgs[i]))
                mOutput = new File(mArgs[++i]);
        if (mInput == null)
        {
            System.err.println("Specify input file with -i");
            System.exit(1);
        }
        if (!mInput.exists())
        {
            System.err.println("Input file '"+mInput+"' does not exist");
            System.exit(1);
        }
    }
    
    public static void main(String[] argv)
    {
        GenerateBaf app = new GenerateBaf(argv);
        app.run();
    }
}
