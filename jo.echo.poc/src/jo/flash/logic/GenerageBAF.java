package jo.flash.logic;

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

import jo.flash.data.FlashWord;

public class GenerageBAF
{

    public static void main(String[] args) throws IOException
    {
        if (args.length != 2)
            throw new IllegalStateException("Needs two arguments");
        File inFile = new File(args[0]);
        File outFile = new File(args[1]);
        BufferedReader rdr = new BufferedReader(new InputStreamReader(new FileInputStream(inFile), "utf-8"));
        BufferedWriter wtr = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outFile), "utf-8"));
        for (;;)
        {
            String inbuf = rdr.readLine();
            if (inbuf == null)
                break;
            wtr.append(inbuf);
            wtr.newLine();
        }
        rdr.close();
        for (String gameName : WordsLogic.GAME_NAMES)
        {
            wtr.append("PLAY\t{"+gameName+"|game}");
            wtr.newLine();
            wtr.append("PLAY\tplay {"+gameName+"|game}");
            wtr.newLine();
        }
        Set<String> words = new HashSet<String>();
        for (int i = 0; i < WordsLogic.getNumberOfLists(); i++)
            for (FlashWord word : WordsLogic.getList(i).getWords())
                words.addAll(word.getNames());
        for (String word : words)
        {
            wtr.append("WORD\t{"+word+"|word}");
            wtr.newLine();
        }
        wtr.close();
    }

}
