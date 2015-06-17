package jo.cc.logic.machine;

import java.io.IOException;
import java.io.InputStream;

public interface IStoryfile
{
    public InputStream openStoryFile() throws IOException;
}
