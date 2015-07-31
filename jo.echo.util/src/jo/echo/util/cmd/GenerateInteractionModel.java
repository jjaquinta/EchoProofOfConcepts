package jo.echo.util.cmd;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class GenerateInteractionModel
{
    private String[] mArgs;
    private JSONParser  mParser;
    
    private File mModelInput;
    private File mIntentOutput;
    private File mUtteranceOutput;
    
    private JSONObject mDoc;
    private Map<String, List<String>> mDictionaries;
    
    public GenerateInteractionModel(String[] args)
    {
        mArgs = args;
        mDictionaries = new HashMap<String, List<String>>();
        mParser = new JSONParser();
    }
    
    public void run()
    {
        parseArgs();
        readModel();
        scanWordLists();
        try
        {
            writeIntents();
            writeUtterances();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private void readModel()
    {
        try
        {
            FileReader rdr = new FileReader(mModelInput);
            try
            {
                mDoc = (JSONObject)mParser.parse(rdr);
            }
            catch (ParseException e)
            {
                System.err.println("Model file '"+mModelInput+"' does not have valid JSON data in it");
                e.printStackTrace();
                System.exit(1);
            }
            rdr.close();
        }
        catch (IOException e)
        {
            System.err.println("Error reading model file '"+mModelInput+"'");
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    @SuppressWarnings("unchecked")
    private void writeIntents() throws IOException
    {
        JSONObject json = new JSONObject();
        JSONArray intents = new JSONArray();
        json.put("intents", intents);
        JSONArray is = (JSONArray)mDoc.get("intents");
        if (is == null)
            throw new IllegalStateException("No intents defined in model!");
        for (Object io : is)
        {
            JSONObject i = (JSONObject)io;
            JSONObject intent = new JSONObject();
            intents.add(intent);
            String intentName = (String)i.get("intent");
            if (intentName == null)
                throw new IllegalStateException("No name defined in intent: "+i.toJSONString());
            intent.put("intent", intentName);
            JSONArray slots = new JSONArray();
            intent.put("slots", slots);
            JSONArray ss = (JSONArray)i.get("slots");
            if (ss != null)
                for (Object so : ss)
                {
                    JSONObject s = (JSONObject)so;
                    JSONObject slot = new JSONObject();
                    slots.add(slot);
                    String slotName = (String)s.get("name");
                    String type = (String)s.get("type");
                    slot.put("name", slotName);
                    slot.put("type", type);
                }
        }
        FileWriter wtr = new FileWriter(mIntentOutput);
        wtr.write(json.toJSONString());
        wtr.close();
    }
    
    private void writeUtterances() throws IOException
    {
        JSONArray is = (JSONArray)mDoc.get("intents");
        if (is == null)
            throw new IllegalStateException("No intents defined in model!");
        BufferedWriter wtr = new BufferedWriter(new FileWriter(mUtteranceOutput));
        for (Object io : is)
            writeUtterancesFromIntent(wtr, (JSONObject)io);
        wtr.close();
    }

    private void writeUtterancesFromIntent(BufferedWriter wtr, JSONObject i)
            throws IOException
    {
        String intentName = (String)i.get("intent");
        Map<String,Set<String>> baseSlotValues = getBaseValues(i);
        Set<String> baseUtterances = getBaseUtterances(i);
        Map<String,List<String>> slotValues = new HashMap<String, List<String>>();
        List<String> utterances = new ArrayList<String>();
        setupBasics(baseSlotValues, slotValues, baseUtterances, utterances);
        String[] slotNames = baseSlotValues.keySet().toArray(new String[0]);
        boolean doneUtterances = false;
        boolean[] doneValues = new boolean[baseSlotValues.size()];
        Set<String> uttered = new HashSet<String>();
        for (;;)
        {
            boolean doneAll = doneUtterances;
            for (boolean doneValue : doneValues)
                doneAll = doneAll & doneValue;
            if (doneAll)
                break;
            String utterance = utterances.get(0);
            utterances.remove(0);
            utterance = insertValuesIntoUtterance(i, utterance, slotValues);
            if (!uttered.contains(utterance))
            {
                wtr.write(intentName+"\t"+utterance);
                wtr.newLine();
                uttered.add(utterance); // no-slot utterances can repeat
            }
            doneUtterances = updateValues(slotNames, slotValues,
                    baseSlotValues, doneValues, utterances, baseUtterances,
                    doneUtterances);
        }
    }

    private String insertValuesIntoUtterance(JSONObject i, String utterance,
            Map<String, List<String>> slotValues)
    {
        for (int o = 0; o < utterance.length(); )
        {
            o = utterance.indexOf('{', o);
            if (o < 0)
                break;
            int e = utterance.indexOf('}', o);
            if (e < 0)
                throw new IllegalArgumentException("Ill formatted utterance in "+utterance+" of "+i.toJSONString());
            String slotName = utterance.substring(o + 1, e);
            List<String> slotValue = slotValues.get(slotName);
            if (slotValue == null)
                throw new IllegalArgumentException("Unknown slot '"+slotName+"' in "+utterance+" of "+i.toJSONString());
            String value = slotValue.get(0);
            slotValue.remove(0);
            utterance = utterance.substring(0, o + 1) + value + "|" + utterance.substring(o + 1);
            o = e;
        }
        return utterance;
    }

    private boolean updateValues(String[] slotNames,
            Map<String, List<String>> slotValues,
            Map<String, Set<String>> baseSlotValues, boolean[] doneValues,
            List<String> utterances, Set<String> baseUtterances,
            boolean doneUtterances)
    {
        if (utterances.size() == 0)
        {
            utterances.addAll(baseUtterances);
            doneUtterances = true;
        }
        for (int slot = 0; slot < slotNames.length; slot++)
            if (slotValues.get(slotNames[slot]).size() == 0)
            {
                slotValues.get(slotNames[slot]).addAll(baseSlotValues.get(slotNames[slot]));
                doneValues[slot] = true;
            }
        return doneUtterances;
    }

    private void setupBasics(Map<String, Set<String>> baseSlotValues,
            Map<String, List<String>> slotValues, Set<String> baseUtterances,
            List<String> utterances)
    {
        for (String slot : baseSlotValues.keySet())
        {
            List<String> values = new ArrayList<String>();
            values.addAll(baseSlotValues.get(slot));
            slotValues.put(slot, values);
        }
        utterances.addAll(baseUtterances);
    }

    private Set<String> getBaseUtterances(JSONObject i)
    {
        JSONArray us = (JSONArray)i.get("utterances");
        Set<String> baseUtterances = new HashSet<String>();
        for (Object uo : us)
        {
            String u = (String)uo;
            baseUtterances.add(u);
        }
        return baseUtterances;
    }
    
    private Map<String,Set<String>> getBaseValues(JSONObject i)
    {
        JSONArray ss = (JSONArray)i.get("slots");
        Map<String,Set<String>> baseSlotValues = new HashMap<String, Set<String>>();
        if (ss == null)
            return baseSlotValues;
        for (Object so : ss)
        {
            JSONObject s = (JSONObject)so;
            String slotName = (String)s.get("name");
            JSONArray vs = (JSONArray)s.get("values");
            Set<String> values = new HashSet<String>();
            if (vs != null)
                for (Object vo : vs)
                {
                    String v = (String)vo;
                    if (v.startsWith("~"))
                    {
                        List<String> list = mDictionaries.get(v.substring(1));
                        if (list == null)
                            throw new IllegalStateException("Cannot find dictionary '"+v+"', "+s.toJSONString());
                        values.addAll(list);
                    }
                    else
                        values.add(v);
                }
            baseSlotValues.put(slotName, values);
        }
        return baseSlotValues;
    }

    private void scanWordLists()
    {
        JSONObject dictionaries = (JSONObject)mDoc.get("dictionaries");
        if (dictionaries == null)
            return; // no dictionaries
        for (Object name : dictionaries.keySet())
        {
            JSONArray ws =(JSONArray)dictionaries.get(name);
            List<String> words = new ArrayList<String>();
            for (Object w : ws)
                words.add(w.toString().toLowerCase());
            mDictionaries.put(name.toString(), words);
        }
    }
    
    private void parseArgs()
    {
        for (int i = 0; i < mArgs.length; i++)
            if ("-model".equalsIgnoreCase(mArgs[i]) || "-m".equalsIgnoreCase(mArgs[i]))
                mModelInput = new File(mArgs[++i]);
            else if ("-intents".equalsIgnoreCase(mArgs[i]) || "-i".equalsIgnoreCase(mArgs[i]))
                mIntentOutput = new File(mArgs[++i]);
            else if ("-utterances".equalsIgnoreCase(mArgs[i]) || "-u".equalsIgnoreCase(mArgs[i]))
                mUtteranceOutput = new File(mArgs[++i]);
        if (mModelInput == null)
        {
            System.err.println("Specify model input file with -m");
            System.exit(1);
        }
        if (!mModelInput.exists())
        {
            System.err.println("Input file '"+mModelInput+"' does not exist");
            System.exit(1);
        }
        if (mIntentOutput == null)
        {
            String name = mModelInput.toString();
            int o = name.lastIndexOf('.');
            if (o >= 0)
                name = name.substring(0, o);
            name += ".json";
            mIntentOutput = new File(name);
        }
        if (mUtteranceOutput == null)
        {
            String name = mModelInput.toString();
            int o = name.lastIndexOf('.');
            if (o >= 0)
                name = name.substring(0, o);
            name += ".baf";
            mUtteranceOutput = new File(name);
        }
    }
    
    public static void main(String[] argv)
    {
        GenerateInteractionModel app = new GenerateInteractionModel(argv);
        app.run();
    }
}
