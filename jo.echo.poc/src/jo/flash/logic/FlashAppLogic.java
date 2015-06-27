package jo.flash.logic;

import java.io.IOException;

import jo.echo.util.ResponseUtils;
import jo.echo.util.SessionUtils;
import jo.flash.data.FlashState;

import com.amazon.speech.slu.Intent;
import com.amazon.speech.slu.Slot;
import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SpeechletRequest;
import com.amazon.speech.speechlet.SpeechletResponse;

public class FlashAppLogic
{
    private static final long OLD_TIMEOUT = 30*1000L;
    
    public static SpeechletResponse interact(SpeechletRequest request, Session session)
    {
        SpeechletResponse resp = null;
        FlashState state = StateLogic.getState(SessionUtils.getUserID(session));
        try
        {
            if (request instanceof IntentRequest)
                resp = doIntent(request, state);
            else
                resp = doWelcome(request, state);
        }
        catch (IOException e)
        {
            e.printStackTrace();
            resp = ResponseUtils.buildSpeechletResponse("Something went wrong. "+e.getLocalizedMessage());
        }
        state.setLastActivity(System.currentTimeMillis());
        return resp;
    }
    private static SpeechletResponse doWelcome(SpeechletRequest request, FlashState state)
    {
        if (oldState(state))
        {
            state.setCurrentGame(-1);
            return ResponseUtils.buildSpeechletResponse("Welcome to Flash Cards. "
                + "Say the name of the game you would like to play. "
                + "Or say 'list games' on its own for a list of games."
                + "[[reprompt=say 'list games' for a list of games.]]");
        }
        else
            return ResponseUtils.buildSpeechletResponse(nextWord(state));
    }
    private static SpeechletResponse doIntent(SpeechletRequest request, FlashState state) throws IOException
    {
        Intent intent = ((IntentRequest)request).getIntent();
        String response = "I'm not quite sure what you want.";
        if (intent != null)
        {
            String verb = intent.getName();
            if (verb.equals("QUIT"))
                response = doQuit(intent, state);
            else if (verb.equals("SCORE"))
                response = doScore(intent, state);
            else if (verb.equals("WORD"))
                response = doWord(intent, state);
            else if (verb.equals("PLAY"))
                response = doPlay(intent, state);
            else if (verb.equals("NEXT"))
                response = doNext(intent, state);
        }
        if (response.indexOf("[[title=") < 0)
            if (state.getCurrentGame() == -1)
                response += "[[title=Flash Cards]]";
            else
                response += "[[title="+WordsLogic.getList(state.getCurrentGame()).getName()+"]]";
        return ResponseUtils.buildSpeechletResponse(response);
    }
    private static String doQuit(Intent intent, FlashState state) throws IOException
    {
        if (state.getCurrentGame() == -1)
            return "Thank you for playing.[[shouldEndSession=true]]";
        String resp = "Thank you for playing "+WordsLogic.getList(state.getCurrentGame()).getName()+". "
                + "If you want to play another game say the name. "
                + "[[reprompt=say 'list games' for a list of games.]]";
        state.setCurrentGame(-1);
        return resp;
    }
    private static String doScore(Intent intent, FlashState state) throws IOException
    {
        StringBuffer resp = new StringBuffer();
        if (state.getCurrentGame() == -1)
        {
            String[] games = StateLogic.rankGames(state);
            if (games.length == 0)
                resp.append("You have no scores yet!");
            else
            {
                resp.append("Your best game is "+games[0]+". ");
                if (games.length > 1)
                {
                    if (games.length > 2)
                        resp.append("Your next best game is "+games[1]+". ");
                    resp.append("Your worst game is "+games[games.length - 1]+". ");
                }
            }
            resp.append("[[reprompt=say 'list games' for a list of games.]]");
        }
        else
        {
            String[] best = StateLogic.bestWords(state);
            String[] worst = StateLogic.worstWords(state);
            doWordList(resp, best, "best");
            doWordList(resp, worst, "worst");
            resp.append("[[reprompt=say 'next' for the next word, or 'list games' for a list of games.]]");
        }
        return resp.toString();
    }
    private static void doWordList(StringBuffer resp, String[] best, String adjective)
    {
        if (best.length > 0)
        {
            int limit = Math.min(5, best.length);
            if (best.length == 1)
                resp.append("Your "+adjective+" word is "+best[0]);
            else
            {
                resp.append("Your "+adjective+" words are ");
                resp.append(ResponseUtils.wordList(best, limit));
            }
            resp.append(". ");
        }
    }
    private static String doWord(Intent intent, FlashState state) throws IOException
    {
        if (state.getCurrentGame() == -1)
        {
            return "If you want to play a game say the name. "
                    + "[[reprompt=say 'list games' for a list of games.]]";
        }
        if (state.getCurrentWord() == null)
            return nextWord(state);
        boolean right = false;
        String word = null;
        if (intent.getSlot("word") != null)
        {
            word = intent.getSlot("word").getValue();
            for (String name : state.getCurrentWord().getNames())
                if (name.equalsIgnoreCase(word))
                {
                    right = true;
                    break;
                }
        }
        StateLogic.recordResult(state, right);
        StringBuffer resp = new StringBuffer();
        if (right)
            resp.append("Correct! ");
        else
        {
            resp.append("I'm sorry, that's not quite right. ");
            if (word != null)
                resp.append("What I heard was '"+word+"'. ");
            if (state.getCurrentWord().getNames().size() == 1)
                resp.append("The correct answers is '"+state.getCurrentWord().getNames().get(0)+". ");
            else
                resp.append("Correct answers are "+ResponseUtils.wordList(state.getCurrentWord().getNames().toArray(new String[0]), -1)+". ");
            resp.append("Let's try another. ");
        }
        resp.append(nextWord(state));
        resp.append("[[reprompt=If you don't know the word, say 'next' to skip it.]]");
        return resp.toString();
    }
    private static String doPlay(Intent intent, FlashState state) throws IOException
    {
        String game = null;
        Slot s = intent.getSlot("game");
        if (s != null)
            game = s.getValue();
        StringBuffer resp = new StringBuffer();
        if (game == null)
        {
            resp.append("These are the following games available: ");
            resp.append(ResponseUtils.wordList(WordsLogic.GAME_NAMES, -1));
            resp.append(". ");
            resp.append("[[reprompt=Say the name of the game you want to play.]]");            
        }
        else
        {
            int gameIdx = -1;
            for (int i = 0; i < WordsLogic.getNumberOfLists(); i++)
                if (WordsLogic.GAME_NAMES[i].equalsIgnoreCase(game))
                {
                    gameIdx = i;
                    break;
                }
            if (gameIdx == -1)
            {
                resp.append("I don't know what game you meant. Please say it again.");
                resp.append("[[reprompt=Say 'list games' to list all games.]]");
            }
            else
            {
                state.setCurrentGame(gameIdx);
                resp.append("You are now playing "+WordsLogic.GAME_NAMES[gameIdx]+". ");
                resp.append(nextWord(state));
            }
        }
        return resp.toString();
    }
    private static String doNext(Intent intent, FlashState state) throws IOException
    {
        if (state.getCurrentGame() == -1)
        {
            return "If you want to play a game say the name. "
                    + "[[reprompt=say 'list games' for a list of games.]]";
        }
        else
            return nextWord(state);
    }
    
    private static String nextWord(FlashState state)
    {
        StringBuffer resp = new StringBuffer();
        StateLogic.setCurrentWord(state);
        if (state.getCurrentWord() == null)
        {
            resp.append("You have mastered "+WordsLogic.GAME_NAMES[state.getCurrentGame()]+"! Please pick a different game. ");
            resp.append("[[reprompt=Say 'list games' to list all games.]]");
            state.setCurrentGame(-1);
        }
        else
        {
            long elapsed = System.currentTimeMillis() - state.getLastWord();
            state.setLastWord(System.currentTimeMillis());
            if (elapsed > OLD_TIMEOUT)
                resp.append("Please read the word on the screen of the companion app.");
            else
                resp.append("Next word!");
            resp.append("[[title="+state.getCurrentWord().getWord()+"]]");
            resp.append("[[reprompt=Say 'next' to skip this word or 'done' to play another game.]]");
        }
        return resp.toString();
    }
    
    private static boolean oldState(FlashState state)
    {
        return state.getLastActivity() < System.currentTimeMillis() - OLD_TIMEOUT;
    }
}