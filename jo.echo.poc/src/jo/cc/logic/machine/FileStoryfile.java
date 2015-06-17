package jo.cc.logic.machine;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileStoryfile implements IStoryfile
{
    private File mFile;
    
    public FileStoryfile(File f)
    {
        mFile = f;
    }
    @Override
    public InputStream openStoryFile() throws IOException
    {
        return new FileInputStream(mFile);
    }
}
