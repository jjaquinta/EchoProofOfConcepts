package jo.cc.logic;

import java.io.File;

import jo.cc.logic.machine.FileStoryfile;


public class ZaxAutoPlay
{
    private String mFile;
    private ZaxStateMachine mMachine;
    private boolean mPickUpAxe;
    private int mLinePos = 0;
    
    public ZaxAutoPlay(String file)
    {
        mFile = file;
    }
    
    public void run()
    {
        mMachine = new ZaxStateMachine();
        mMachine.run(new FileStoryfile(new File(mFile)));
        while (!mMachine.isDone())
        {
            try
            {
                Thread.sleep(1000);
            }
            catch (InterruptedException e)
            {
            }
            String txt = mMachine.getLastText();
            System.out.println(txt);
            String toSay = null;
            if (mPickUpAxe || txt.indexOf("The dwarf throws a nasty little axe at you, misses, curses, and runs away.") >= 0)
            {
                toSay = "GET AXE";
                mPickUpAxe = false;
            }
            else if ((txt.indexOf("A threatening little dwarf comes out of the shadows!") >= 0)
                    || (txt.indexOf("The dwarf stalks after you...") >= 0)
                    || (txt.indexOf("The dwarf throws a nasty little knife at you, but misses!") >= 0))
            {
                toSay = "THROW AXE AT DWARF";
                mPickUpAxe = true;
            }
            else if (mLinePos < WALKTHROUGH.length)
            {
                while (WALKTHROUGH[mLinePos].startsWith("#"))
                    mLinePos++;
                toSay = WALKTHROUGH[mLinePos++];
            }
            else
            {
                toSay = "QUIT";
            }
            System.out.println(">"+toSay);
            mMachine.say(toSay);
        }
    }
    
    public static void main(String[] argv)
    {
        ZaxAutoPlay app = new ZaxAutoPlay("C:\\Users\\Jo\\Documents\\ws\\if\\jo.zax\\data\\Advent.z5");
        app.run();
    }

    public static final String[] WALKTHROUGH =
    {
        "# When first see Dwarf, get axe; from then on,",
        "#  \"throw axe at dwarf ; get axe\" repeatedly to kill him.",
        "# When pirate appears, go to \"West End of Hall of Mists\", then",
        "#   S; E; S; N; GET ALL; SE; N; D returns you to \"Orange River Chamber\". ",
        "# DO NOT use the coins in the vending machine - you need the coins to",
        "# get all points.  Don't eat the food either - you need to use it.",

        "E",
        "GET ALL",
        "W",
        "S",
        "S",
        "S",
        "UNLOCK GRATE WITH KEYS",
        "OPEN GRATE",
        "D",
        "W",
        "GET CAGE",
        "W",
        "LIGHT LAMP",

        "# Now in Debris room / Xyzzy room.",
        "# Don't get rod now; it's trouble, and not needed yet/ever.",

        "W",
        "W",
        "GET BIRD",
        "W",
        "D",

        "# In Hall of Mists.",
        "# Advent1.sav",

        "S",
        "GET NUGGET",
        "N",
        "N",

        "# Hall of Mountain King, where Snake starts.",
        "DROP BIRD",
        "GET BIRD",
        "N",

        "# We'll leave keys in Low N/S Passage",
        "DROP KEYS",
        "GET SILVER",
        "N",

        "# Now at \"Y2\".",
        "PLUGH",


        "# Drop.  Cage & Food give no points, and we'll need the food later,",
        "# but right now we need space to carry stuff.  We'll bring bottle to feed plant",
        "DROP SILVER",
        "DROP NUGGET",
        "DROP CAGE",
        "DROP FOOD",
        "PLUGH",

        "# Advent2.sav",
        "# Get rug from dragon, ming vase, and its required pillow.",
        "S",
        "S",

        "# At Hall of Mountain King; now go to secret area with dragon.",
        "SW",
        "W",
        "KILL DRAGON",
        "# With bare hands?",
        "YES",
        "# We did it!",
        "GET RUG",
        "E",
        "D",
        "N",
        "N",

        "# Swiss Cheese room. Let's start watering the plant.",
        "W",
        "W",
        "D",

        "# Advent3",
        "POUR WATER ON PLANT",
        "# We'll need to get more water, but there's some in the wellhouse.",
        "U",
        "E",
        "E",

        "# Back to Swiss Cheese room.",
        "# May need to go NW more than once to enter Oriental Room:",
        "NW",

        "# In Oriental room. Don't get vase yet; we need to view the Plover room.",
        "N",
        "W",

        "# Now in Alcove.",

        "DROP ALL",
        "E",
        "GET EMERALD",
        "W",
        "GET ALL",
        "NW",
        "S",

        "# Back in Oriental room; now get vase.",
        "GET VASE",
        "SE",

        "# Now get pillow, else we can't drop vase.",
        "E",
        "GET PILLOW",
        "W",
        "NE",

        "# Bedquilt.",
        "E",
        "U",
        "E",

        "# Save here Advent4.",
        "U",
        "N",
        "PLUGH",

        "DROP PILLOW",
        "DROP RUG",
        "DROP VASE",
        "DROP EMERALD",

        "FILL BOTTLE",


        "# Get the pyramid.",
        "GET FOOD",
        "PLUGH",

        "# Advent5.",
        "PLOVER",
        "NE",
        "GET PYRAMID",
        "S",
        "PLOVER",
        "PLUGH",
        "DROP PYRAMID",
        "PLUGH",

        "# Deal with troll/bear",
        "S",
        "GET KEYS",
        "D",
        "W",
        "D",
        "W",

        "# Now at Bedquilt",
        "W",
        "W",
        "W",
        "D",
        "POUR WATER ON BEANSTALK",
        "# Get ready for door, later.",
        "U",
        "E",
        "D",
        "# This fills the bottle with the oil here, which we need for the door.",
        "FILL BOTTLE",
        "U",
        "W",
        "D",
        "# We're back..",
        "CLIMB BEANSTALK",
        "W",
        "GET EGGS",
        "N",
        "OIL DOOR",
        "N",
        "# Advent6",
        "GET TRIDENT",
        "W",
        "D",
        "# Large Low room - temporarily drop this.",
        "DROP TRIDENT",
        "SW",
        "U",
        "NE",
        "# Advent7",
        "# We give eggs to troll, because we can make them magically reappear later.",
        "GIVE EGGS",
        "NE",
        "NE",
        "E",
        "D",
        "S",
        "E",
        "# In Barren Room - get it?",
        "GIVE FOOD TO BEAR",
        "UNLOCK CHAIN WITH KEYS",
        "GET BEAR",
        "GET CHAIN",
        "# Now bring out",
        "W",
        "W",
        "N",
        "NE",
        "E",
        "GET SPICES",
        "W",
        "S",
        "W",
        "W",
        "# On NE Side of Chasm",
        "SW",
        "DROP BEAR",
        "SW",
        "SW",
        "D",
        "GET TRIDENT",
        "SE",
        "# Now at Oriental Room",
        "SE",
        "NE",
        "E",
        "# Advent8",
        "# At complex junction.  Drop keys, no longer needed.",
        "DROP KEYS",

        "# Get that last extra point:",
        "E",
        "GET MAGAZINE",
        "E",
        "DROP MAGAZINE",
        "W",
        "U",

        "# Back at complex junction. Open the clam.",
        "N",
        "U",
        "U",
        "S",

        "# Back at Complex Junction again!",
        "U",
        "E",
        "U",
        "N",
        "PLUGH",
        "DROP TRIDENT",
        "DROP CHAIN",
        "DROP SPICES",
        "DROP PEARL",

        "# Advent9",

        "# Back for the eggs.",
        "PLUGH",
        "S",
        "D",
        "W",
        "D",
        "W",
        "W",
        "# Swiss cheese room",
        "W",
        "W",
        "D",
        "CLIMB BEANSTALK",
        "W",
        "FEE",
        "FIE",
        "FOE",
        "FOO",
        "GET EGGS",
        "# Advent10",
        "# If the pirate steals at this point, no big deal.",
        "S",
        "E",
        "U",
        "E",
        "E",
        "# Swiss cheese room",
        "NE",
        "# Bedquilt",
        "E",
        "U",
        "E",
        "U",
        "N",
        "PLUGH",
        "DROP EGGS",


        "# Get Jewelry, coins, diamond",
        "PLUGH",
        "S",
        "S",
        "S",
        "GET JEWELRY",
        "N",
        "W",
        "GET COINS",
        "W",
        "W",
        "E",
        "E",
        "GET DIAMONDS",
        "N",
        "W",
        "N",
        "E",
        "# West Side Chamber",
        "E",
        "N",
        "N",
        "PLUGH",

        "# NOTE: I could drop bottle after oiling door, then have room to get eggs",
        "# on first trip.  That'd be even faster, but I didn't need the speed.",

        "# NOTE: wave rod for faster crossing by creating crystal bridge across fissure.",
        "# The crystal bridge isn't needed, so I didn't do it.",

        "# Here's how to get to pirate's den from Y2:",
        "S",
        "S",
        "W",
        "W",
        "W",
        "E",
        "# West End of Hall of Mists, about to enter maze \"all alike\":",
        "S",
        "E",
        "S",
        "N",
        "GET ALL",
        "SE",
        "N",
        "D",
        "# Now at Orange River Chamber",
        "E",
        "E",
        "GET ROD",
        "XYZZY",
        "DROP ALL",
        "GET LAMP",
        "GET AXE",
        "PLUGH",
        "# Repeatedly \"wait\".",



        "# Transported to Repository.",
        "SW",
        "GET ALL",
        "NE",
        "DROP ALL",
        "SW",
        "BLAST",
    };
}
