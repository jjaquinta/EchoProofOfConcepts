package jo.flash.logic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import jo.flash.data.FlashState;
import jo.flash.data.FlashWord;
import jo.flash.data.FlashWordList;

public class StateLogic
{
    private static Map<String, FlashState> mStates = new HashMap<String, FlashState>();
    private static final Random RND = new Random();
    
    public static FlashState getState(String id)
    {
        FlashState state = mStates.get(id);
        if (state == null)
        {
            state = new FlashState();
            mStates.put(id, state);
        }
        return state;
    }
    
    public static Map<String,Integer> getHistory(FlashState state)
    {
        if (state.getCurrentGame() < 0)
            throw new IllegalStateException("Can't get history when not playing a game");
        Map<String,Integer> history = state.getHistory().get(state.getCurrentGame());
        if (history == null)
        {
            history = new HashMap<String, Integer>();
            state.getHistory().put(state.getCurrentGame(), history);
        }
        return history;
    }
    
    public static FlashWord setCurrentWord(FlashState state)
    {
        if (state.getCurrentGame() < 0)
            throw new IllegalStateException("Can't get word when not playing a game");
        Map<String,Integer> history = getHistory(state);
        FlashWordList list = WordsLogic.getList(state.getCurrentGame());
        int totalWeight = 0;
        for (FlashWord word : list.getWords())
        {
            int weight = getWeight(word, history);
            totalWeight += weight;
        }
        if (totalWeight == 0)
        {
            state.setCurrentWord(null); // mastered
            return null;
        }
        int target = RND.nextInt(totalWeight);
        for (FlashWord word : list.getWords())
        {
            int weight = getWeight(word, history);
            target -= weight;
            if (target < 0)
            {
                state.setCurrentWord(word);
                return word;
            }
        }
        throw new IllegalStateException("We should have found a word!");
    }

    private static int getWeight(FlashWord word, Map<String, Integer> history)
    {
        int weight = 10;
        Integer h = history.get(word.getWord());
        if (h != null)
            weight -= h;
        return weight;
    }
    
    public static void recordResult(FlashState state, boolean correct)
    {
        Map<String,Integer> history = getHistory(state);
        Integer score = history.get(state.getCurrentWord().getWord());
        if (score == null)
            score = 0;
        if (correct)
            score++;
        else
            score--;
        history.put(state.getCurrentWord().getWord(), score);
    }
    
    public static String[] bestWords(FlashState state)
    {
        final Map<String,Integer> history = getHistory(state);
        List<String> best = new ArrayList<String>();
        for (String word : history.keySet())
        {
            Integer score = history.get(word);
            if (score > 0)
                best.add(word);
        }
        Collections.sort(best, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2)
            {
                Integer score1 = history.get(o1);
                Integer score2 = history.get(o2);
                return score1 - score2;
            }
        });
        return best.toArray(new String[0]);
    }
    
    public static String[] worstWords(FlashState state)
    {
        final Map<String,Integer> history = getHistory(state);
        List<String> worst = new ArrayList<String>();
        for (String word : history.keySet())
        {
            Integer score = history.get(word);
            if (score < 0)
                worst.add(word);
        }
        Collections.sort(worst, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2)
            {
                Integer score1 = history.get(o1);
                Integer score2 = history.get(o2);
                return score2 - score1;
            }
        });
        return worst.toArray(new String[0]);
    }
    
    public static String[] rankGames(FlashState state)
    {
        final Map<Integer, Integer> gameRanks = new HashMap<Integer, Integer>();
        for (Integer idx : state.getHistory().keySet())
        {
            Map<String,Integer> history = state.getHistory().get(idx);
            int totalScore = 0;
            for (Integer score : history.values())
                totalScore += score;
            gameRanks.put(idx, totalScore);
        }
        Integer[] indicies = gameRanks.keySet().toArray(new Integer[0]);
        Arrays.sort(indicies, new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2)
            {
                Integer s1 = gameRanks.get(o1);
                Integer s2 = gameRanks.get(o2);
                return s1 - s2;
            }
        });
        List<String> games = new ArrayList<String>();
        for (Integer i : indicies)
            games.add(WordsLogic.GAME_NAMES[i]+" score "+gameRanks.get(i));
        return games.toArray(new String[0]);
    }
}
