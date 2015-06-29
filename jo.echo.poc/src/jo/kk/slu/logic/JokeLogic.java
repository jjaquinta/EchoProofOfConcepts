package jo.kk.slu.logic;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import jo.kk.slu.data.KnockKnockState;

public class JokeLogic
{
    private static final Random RND = new Random();
    private static final Map<String, KnockKnockState> mStates = new HashMap<String, KnockKnockState>();
    
    public static KnockKnockState getState(String userID)
    {
        KnockKnockState state = mStates.get(userID);
        if (state == null)
        {
            state = new KnockKnockState();
            mStates.put(userID, state);
        }
        if ((state.getJoke() == null) || (state.getCall() >= state.getJoke().length))
        {
            state.setJoke(JOKES[RND.nextInt(JOKES.length)]);
            state.setCall(0);
        }
        return state;
    }
    
    private static final String[][] JOKES = {
        { "knock knock.",  "canoe.",  "canoe help me with my homework?",  },
        { "knock knock",  "merry.",  "merry christmas!",  },
        { "knock knock.",  "orange.",  "orange you going to let me in?",  },
        { "knock knock.",  "anee.",  "anee one you like!",  },
        { "knock knock",  "iva.",  "i’ve a sore hand from knocking!",  },
        { "knock knock.",  "dozen.",  "dozen anybody want to let me in?",  },
        { "knock knock.",  "needle.",  "needle little money for the movies.",  },
        { "knock knock.",  "henrietta.",  "henrietta worm that was in his apple.",  },
        { "knock knock.",  "avenue.",  "avenue knocked on this door before?",  },
        { "knock knock.",  "harry.",  "harry up, it’s cold out here!",  },
        { "knock knock.",  "a herd.",  "a herd you were home, so i came over!",  },
        { "knock knock.",  "adore.",  "adore is between us. open up!",  },
        { "knock knock.",  "otto.",  "otto know. i’ve got amnesia.",  },
        { "knock knock.",  "king tut.",  "king tut-key fried chicken!",  },
        { "knock knock.",  "lettuce.",  "lettuce in it’s cold out here.",  },
        { "knock knock.",  "noah.",  "noah good place we can get something to eat?",  },
        { "knock knock.",  "robin.",  "robin the piggy bank again.",  },
        { "knock knock.",  "dwayne.",  "dwayne the bathtub, it’s overflowing!",  },
        { "knock knock.",  "imma.",  "imma gettin’ old open the door!",  },
        { "knock knock.",  "banana.",  "knock knock.",  "banana.",  "knock knock.",  "orange.",  "orange you glad i didn’t say banana!",  },
        { "knock knock.",  "boo.",  "gosh, don’t cry it’s just a knock knock joke.",  },
        { "knock knock.",  "impatient cow.",  "mooooo!",  },
        { "knock knock",  "a little old lady.",  "i didn’t know you could yodel.",  },
        { "knock knock",  "sadie.",  "sadie magic word and watch me disappear!",  },
        { "knock knock,",  "olive.",  "olive you!",  },
        { "will you remember me in 2 minutes?",  "knock knock.",  "hey, you didn’t remember me!",  },
        { "knock knock.",  "justin.",  "justin time for dinner.",  },
        { "knock knock.",  "kirtch.",  "god bless you!",  },
        { "will you remember me in a minute?",  "will you remember me in a week?",  "will you remember me in a year?",  "knock knock.",  "you didn’t remember me!",  },
        { "knock knock.",  "luke.",  "luke through the the peep hole and find out.",  },
        { "knock knock.",  "ivor.",  "ivor you let me in or i`ll climb through the window.",  },
        { "knock knock.",  "claire.",  "claire the way, i’m coming through!",  },
        { "knock knock.",  "arfur.",  "arfur got!",  },
        { "knock knock.",  "abby.",  "abby birthday to you!",  },
        { "knock knock.",  "nana.",  "nana your business.",  },
        { "knock knock.",  "ya.",  "wow. you sure are excited to see me!",  },
        { "knock knock.",  "cows go",  "cows don’t go who, they go moo!",  },
        { "knock knock.",  "etch.",  "bless you!",  },
        { "knock knock.",  "roach.",  "roach you a letter, did you get it?",  },
        { "knock knock.",  "aida.",  "aida sandwich for lunch today.",  },
        { "knock knock.",  "iona.",  "iona new car!",  },
        { "knock knock.",  "scold.",  "scold enough out here to go ice skating.",  },
        { "knock knock.",  "police.",  "police hurry up, it’s chilly outside!",  },
        { "knock knock",  "justin.",  "just in the neighborhood, thought i would drop by.",  },
        { "knock knock",  "ben.",  "ben knocking for 10 minutes.",  },
        { "knock knock.",  "two knee.",  "two-knee fish!",  },
        { "knock knock.",  "hoo.",  "are you a owl?",  },
        { "knock knock.",  "i am.",  "you mean you don’t know who you are?",  },
        { "knock knock.",  "isabell.",  "is a bell working?",  },
        { "knock knock.",  "tank.",  "your welcome!",  },
        { "knock knock",  "alex.",  "alex-plain later!",  },
        { "knock knock!",  "ketchup.",  "ketchup with me and i’ll tell you!",  },
        { "knock knock!",  "annie.",  "annie body home?",  },
        { "knock knock!",  "watson.",  "what’s on tv tonight?",  },
        { "knock knock!",  "cook.",  "hey! who are you calling cuckoo?",  },
        { "knock knock!",  "spell.",  "w-h-o",  },
        { "knock knock.",  "dishes.",  "dish is a nice place!",  },
        { "knock knock.",  "althea.",  "althea later alligator!",  },
        { "knock knock.",  "norma lee.",  "norma lee i don’t go around knocking on doors, but i just had to meet you!",  },
        { "knock knock.",  "cd.",  "cd guy on your doorstep?",  },
        { "knock knock.",  "somebody too short to ring the doorbell!",  },
        { "knock knock.",  "iowa.",  "iowa big apology to the owner of that red car!",  },
        { "knock knock.",  "abbot.",  "abbot you don’t know who this is!",  },
        { "knock knock.",  "viper.",  "viper nose, it’s running!",  },
    };
}
