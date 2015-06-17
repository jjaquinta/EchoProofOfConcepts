package jo.cc.logic;

import java.awt.Dimension;
import java.awt.Point;
import java.util.StringTokenizer;
import java.util.Vector;

import jo.cc.logic.machine.ZUserInterface;

public class ZaxWrapper implements ZUserInterface
{
    private ZUserInterface mUI;
    private String mLastText = "";
    private boolean mPickUpAxe;
    
    public ZaxWrapper(ZUserInterface ui)
    {
        mUI = ui;
    }

    @Override
    public void fatal(String errmsg)
    {
        System.out.println("fatal("+errmsg+")");
        mUI.fatal(errmsg);
    }

    @Override
    public void initialize(int ver)
    {
        System.out.println("initialize("+ver+")");
        mUI.initialize(ver);
    }

    @Override
    public void setTerminatingCharacters(@SuppressWarnings("rawtypes") Vector chars)
    {
        System.out.println("setTerminatingCharacters(#"+chars.size()+")");
        mUI.setTerminatingCharacters(chars);
    }

    @Override
    public boolean hasStatusLine()
    {
        //boolean ret = mUI.hasStatusLine();
        //System.out.println("hasStatusLine="+ret);
        //return ret;
        return false;
    }

    @Override
    public boolean hasUpperWindow()
    {
        //boolean ret = mUI.hasUpperWindow();
        //System.out.println("hasUpperWindow="+ret);
        //return ret;
        return false;
    }

    @Override
    public boolean defaultFontProportional()
    {
        //boolean ret = mUI.defaultFontProportional();
        //System.out.println("defaultFontProportional="+ret);
        //return ret;
        return false;
    }

    @Override
    public boolean hasColors()
    {
        //boolean ret = mUI.hasColors();
        //System.out.println("hasColors="+ret);
        //return ret;
        return false;
    }

    @Override
    public boolean hasBoldface()
    {
        //boolean ret = mUI.hasBoldface();
        //System.out.println("hasBoldface="+ret);
        //return ret;
        return false;
    }

    @Override
    public boolean hasItalic()
    {
        //boolean ret = mUI.hasItalic();
        //System.out.println("hasItalic="+ret);
        //return ret;
        return false;
    }

    @Override
    public boolean hasFixedWidth()
    {
        //boolean ret = mUI.hasFixedWidth();
        //System.out.println("hasFixedWidth="+ret);
        //return ret;
        return false;
    }

    @Override
    public Dimension getScreenCharacters()
    {
        //Dimension ret = mUI.getScreenCharacters();
        //System.out.println("getScreenCharacters="+ret);
        //return ret;
        return new Dimension(80, 25);
    }

    @Override
    public Dimension getScreenUnits()
    {
        //Dimension ret = mUI.getScreenUnits();
        //System.out.println("getScreenUnits="+ret);
        //return ret;
        return new Dimension(1440, 425);
    }

    @Override
    public Dimension getFontSize()
    {
        //Dimension ret = mUI.getFontSize();
        //System.out.println("getFontSize="+ret);
        //return ret;
        return new Dimension(18, 17);
    }

    @Override
    public Dimension getWindowSize(int window)
    {
        Dimension ret = mUI.getWindowSize(window);
        System.out.println("getWindowSize="+ret);
        return ret;
    }

    @Override
    public int getDefaultForeground()
    {
        //int ret = mUI.getDefaultForeground();
        //System.out.println("getDefaultForeground="+ret);
        //return ret;
        return 9;
    }

    @Override
    public int getDefaultBackground()
    {
        //int ret = mUI.getDefaultBackground();
        //System.out.println("getDefaultBackground="+ret);
        //return ret;
        return 6;
    }

    @Override
    public Point getCursorPoint()
    {
        Point ret = mUI.getCursorPoint();
        System.out.println("getCursorPoint="+ret);
        return ret;
    }

    @Override
    public void showStatusBar(String s, int a, int b, boolean flag)
    {
        mUI.showStatusBar(s, a, b, flag);
    }

    @Override
    public void splitScreen(int lines)
    {
        //System.out.println("splitScreen("+lines+")");
        mUI.splitScreen(lines);
    }

    int mCurrentWindow;
    
    @Override
    public void setCurrentWindow(int window)
    {
        //System.out.println("setCurrentWindow("+window+")");
        mUI.setCurrentWindow(window);
        mCurrentWindow = window;
    }

    @Override
    public void setCursor(int x, int y)
    {
        //System.out.println("setCursor("+x+", "+y+")");
        mUI.setCursor(x, y);
    }

    @Override
    public void setColor(int fg, int bg)
    {
        mUI.setColor(fg, bg);
    }

    @Override
    public void setTextStyle(int style)
    {
        mUI.setTextStyle(style);
    }

    @Override
    public void setFont(int font)
    {
        mUI.setFont(font);
    }

    int linePos = 0;
    
    @Override
    public int readLine(StringBuffer sb, int time)
    {        
        int ret;
        if (mPickUpAxe || mLastText.indexOf("The dwarf throws a nasty little axe at you, misses, curses, and runs away.") >= 0)
        {
            sb.append("GET AXE");
            ret = 10;            
            mPickUpAxe = false;
        }
        else if ((mLastText.indexOf("A threatening little dwarf comes out of the shadows!") >= 0)
                || (mLastText.indexOf("The dwarf stalks after you...") >= 0)
                || (mLastText.indexOf("The dwarf throws a nasty little knife at you, but misses!") >= 0))
        {
            sb.append("THROW AXE AT DWARF");
            ret = 10;            
            mPickUpAxe = true;
        }
        else if (linePos < WALKTHROUGH.length)
        {
            while (WALKTHROUGH[linePos].startsWith("#"))
                linePos++;
            sb.append(WALKTHROUGH[linePos++]);
            ret = 10;            
        }
        else
        {
            ret = mUI.readLine(sb, time);
        }
        System.out.println(">"+sb);
        return ret;
    }

    @Override
    public int readChar(int time)
    {
        int ret = mUI.readChar(time);
        System.out.println("readChar("+time+") = "+ret);
        return ret;
    }

    @Override
    public void showString(String s)
    {
        if (mCurrentWindow == 0)
        {
            for (StringTokenizer st = new StringTokenizer(s, "\r\n"); st.hasMoreElements(); )
                System.out.println(st.nextElement());
            mLastText = s;
        }
        mUI.showString(s);
    }

    @Override
    public void scrollWindow(int lines)
    {
        //System.out.println("scrollWindow("+lines+")");
        mUI.scrollWindow(lines);
    }

    @Override
    public void eraseLine(int s)
    {
        //System.out.println("eraseLine("+s+")");
        mUI.eraseLine(s);
    }

    @Override
    public void eraseWindow(int window)
    {
        //System.out.println("eraseWindow("+window+")");
        mUI.eraseWindow(window);
    }

    @Override
    public String getFilename(String title, String suggested, boolean saveFlag)
    {
        String fname = mUI.getFilename(title, suggested, saveFlag);
        System.out.println("getFilename("+title+", "+suggested+", "+saveFlag+") = "+fname);
        return fname;
    }

    @Override
    public void quit()
    {
        System.out.println("quit");
        mUI.quit();
        System.exit(0);
    }

    @Override
    public void restart()
    {
        System.out.println("restart");
        mUI.restart();
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
