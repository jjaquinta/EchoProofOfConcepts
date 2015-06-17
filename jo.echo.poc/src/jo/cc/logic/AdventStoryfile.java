package jo.cc.logic;

import java.io.InputStream;

import jo.cc.logic.machine.IStoryfile;

public class AdventStoryfile implements IStoryfile
{
    @Override
    public InputStream openStoryFile()
    {
        return AdventStoryfile.class.getClassLoader().getResourceAsStream("jo/cc/logic/Advent.z5");
    }
}
