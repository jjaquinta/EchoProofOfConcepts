package jo.jose.logic;

import java.util.Random;

import org.json.simple.JSONObject;

import com.amazon.speech.slu.Intent;

public class BookLogic
{
    private static final Random RND = new Random();

    public static String doBook(Intent intent, JSONObject user)
    {
        String resp = ABOUTS[RND.nextInt(ABOUTS.length)];
        resp += "[[card=++"
                + " http://www.amazon.com/Astoundingly-True-Tale-Jos%C3%A9-Fabuloso-ebook/dp/B00FUTUTIY"
                + " https://www.smashwords.com/books/view/366865"
                + " http://www.barnesandnoble.com/w/the-astoundingly-true-tale-of-jos-fabuloso-jo-jaquinta/1117236807?ean=2940045341882]]";
        resp += "[[reprompt=Say 'excerpt' to hear some of it.]]";
        return resp;
    }

    public static String doExcerpt(Intent intent, JSONObject user)
    {
        String resp = EXCERPTS[RND.nextInt(EXCERPTS.length)];
        resp += "[[card=++"
                + " http://www.amazon.com/Astoundingly-True-Tale-Jos%C3%A9-Fabuloso-ebook/dp/B00FUTUTIY"
                + " https://www.smashwords.com/books/view/366865"
                + " http://www.barnesandnoble.com/w/the-astoundingly-true-tale-of-jos-fabuloso-jo-jaquinta/1117236807?ean=2940045341882]]";
        resp += "[[reprompt=Say 'book' to hear about the book.]]";
        return resp;
    }

    public static String doJose(Intent intent, JSONObject user)
    {
        return "Fabuloso!!!";
    }

    private static final String[] EXCERPTS = {
        "José Fabuloso watched as the level of his beer sank in parallel to the stack of silver bills on the table before him. He took another "
                + "swig of the flat synthetic brew and let the pretty dancer pluck the note from his outstretched hand with her breasts. "
                + "He savored the fact that in the dissolving chaos that his life had become, he could exert control over at least one thing.",
        "The officer pushed them back into the room and started to sweep them all with a scanner. The dancing music had died leaving the "
                + "slightly hysteric newscaster reporting explosions, decompression and power losses all over the station. The officer ceased "
                + "his sweep with his blinking scanner pointing directly at Squirrel's outstretched hand.",
        "\"You aren't going to get away with that so easily\" shouted Squirrel at the two of them. \"Well you're just going to have to get it "
                + "from me in my next life. There's no berth on our ship for a prostitute so just run along and lodge a complaint with the "
                + "Norn's HR department.\"",
        "\"You got the ship back?\" said José with delight. \"No,\" said Melise, taking a sudden left. \"But I have a plan to try to get one "
                + "before this place disintegrates into vacuum.\"",
        "There was a clank and José was brought up short which brought Melise up short. Squirrel smiled triumphantly and held up her hand, "
                + "now manacled to José. \"I don't know what the hell is going on. "
                + "But if you've got a ship, I'm going with you.\" \"Sif's flaming hair\" swore Melise.",
        "\"What's that one there?\" \"It's a Narcissus 2.2\" said O'Riley. \"Yes. It's very too-too. How much does it list for?\" "
                + "\"Well, nothing says 'look at me' quite like a Narcissus. They're pretty top o' the line\" said O'Riley.",
        "Melise, snagged a mug of espresso, \"The bridge is all unlocked and ready to go.\" \"How did you do that?\" asked O'Riley. "
                + "\"The anti-jack on the door took me positively minutes.\" \"Rich people\" Melise sneered. \"They never changed the "
                + "default passwords. I just looked it up on the manual under the dashboard.\"",
    };

    private static final String[] ABOUTS = {
        "The Astoundingly True Tale of José Fabuloso is available on Amzon Books, Google Play, and other good e-book vendors. Just search for José Fabuloso.",
        "Take a stripper, a crazy pilot, an ace accountant, an alcohol obsessed engineer, and a mysterious curmudgeon. Place in a small stolen ship and agitate with stalkers, assassins, and lots of espresso. The result is the Astoundingly True Tale of José Fabuloso!",
        "A poor choice of passengers leaves Jose, Melise and O'Riley stranded on a station in the midst of blowing up. Their efforts to steal a ship to escape the mess are somewhat hampered by Squirrel, a nearly naked stripper manacled to Jose's hand.",
        "Fate seems to have it in for the crew of the José Fabuloso as they are relentlessly pursued by warring criminal factions seeking something they have. If only they knew what and where it was! ",
    };
}
