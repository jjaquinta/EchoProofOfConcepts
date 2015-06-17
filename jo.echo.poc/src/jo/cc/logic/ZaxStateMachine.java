package jo.cc.logic;

import java.awt.Dimension;
import java.awt.Point;
import java.io.File;
import java.util.StringTokenizer;
import java.util.Vector;

import jo.cc.logic.machine.FileStoryfile;
import jo.cc.logic.machine.IStoryfile;
import jo.cc.logic.machine.ZCPU;
import jo.cc.logic.machine.ZUserInterface;

public class ZaxStateMachine implements ZUserInterface
{
    public static void main(String[] argv)
    {
        ZaxStateMachine app = new ZaxStateMachine();
        app.run(new FileStoryfile(new File("C:\\Users\\Jo\\Documents\\ws\\if\\jo.zax\\data\\Advent.z5")));
    }
    
    private ZCPU mCPU;
    private Thread  mCPUThread;
    private StringBuffer mLastText;
    private StringBuffer mNextText;
    private boolean mDone;
    
    public ZaxStateMachine()
    {
        mCPU = new ZCPU(this);
        mLastText = new StringBuffer();
        mNextText = new StringBuffer();
    }
    
    public void run(IStoryfile file)
    {
        mCPU.initialize(file);
        mCPUThread = mCPU.start();
    }
    
    public String getLastText()
    {
        return mLastText.toString();
    }
    
    public void say(String text)
    {
        mNextText.append(text);
        mCPUThread.interrupt();
    }
    
    public boolean isDone()
    {
        return mDone;
    }

    @Override
    public void fatal(String errmsg)
    {
        System.out.println("fatal("+errmsg+")");
    }

    @Override
    public void initialize(int ver)
    {
        System.out.println("initialize("+ver+")");
    }

    @Override
    public void setTerminatingCharacters(@SuppressWarnings("rawtypes") Vector chars)
    {
        System.out.println("setTerminatingCharacters(#"+chars.size()+")");
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
        //Dimension ret = mUI.getWindowSize(window);
        //System.out.println("getWindowSize="+ret);
        //return ret;
        throw new IllegalStateException();
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
        //Point ret = mUI.getCursorPoint();
        //System.out.println("getCursorPoint="+ret);
        //return ret;
        throw new IllegalStateException();
    }

    @Override
    public void showStatusBar(String s, int a, int b, boolean flag)
    {
        //mUI.showStatusBar(s, a, b, flag);
    }

    @Override
    public void splitScreen(int lines)
    {
        //System.out.println("splitScreen("+lines+")");
        //mUI.splitScreen(lines);
    }

    int mCurrentWindow;
    
    @Override
    public void setCurrentWindow(int window)
    {
        //System.out.println("setCurrentWindow("+window+")");
        //mUI.setCurrentWindow(window);
        mCurrentWindow = window;
    }

    @Override
    public void setCursor(int x, int y)
    {
        //System.out.println("setCursor("+x+", "+y+")");
        //mUI.setCursor(x, y);
    }

    @Override
    public void setColor(int fg, int bg)
    {
        //mUI.setColor(fg, bg);
    }

    @Override
    public void setTextStyle(int style)
    {
        //mUI.setTextStyle(style);
    }

    @Override
    public void setFont(int font)
    {
        //mUI.setFont(font);
    }

    int linePos = 0;
    
    @Override
    public int readLine(StringBuffer sb, int time)
    {        
        while (mNextText.length() == 0)
        {
            try
            {
                Thread.sleep(30000);
            }
            catch (InterruptedException e)
            {
            }
        }
        sb.append(mNextText.toString());
        mLastText.setLength(0);
        mNextText.setLength(0);
        return 10;
    }

    @Override
    public int readChar(int time)
    {
        //int ret = mUI.readChar(time);
        //System.out.println("readChar("+time+") = "+ret);
        //return ret;
        throw new IllegalStateException();
    }

    @Override
    public void showString(String s)
    {
        if (mCurrentWindow == 0)
        {
            for (StringTokenizer st = new StringTokenizer(s, "\r\n"); st.hasMoreTokens(); )
            {
                String txt = st.nextToken();
                if (">".equals(txt))
                    continue;
                mLastText.append(txt);
                mLastText.append("\n");
            }
        }
        //mUI.showString(s);
    }

    @Override
    public void scrollWindow(int lines)
    {
        //System.out.println("scrollWindow("+lines+")");
        //mUI.scrollWindow(lines);
    }

    @Override
    public void eraseLine(int s)
    {
        //System.out.println("eraseLine("+s+")");
        //mUI.eraseLine(s);
    }

    @Override
    public void eraseWindow(int window)
    {
        //System.out.println("eraseWindow("+window+")");
        //mUI.eraseWindow(window);
    }

    @Override
    public String getFilename(String title, String suggested, boolean saveFlag)
    {
        //String fname = mUI.getFilename(title, suggested, saveFlag);
        //System.out.println("getFilename("+title+", "+suggested+", "+saveFlag+") = "+fname);
        //return fname;
        throw new IllegalStateException();
    }

    @Override
    public void quit()
    {
        mDone = true;
    }

    @Override
    public void restart()
    {
        System.out.println("restart");
        //mUI.restart();
    }

}
