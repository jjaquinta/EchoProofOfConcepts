// ZMemory - The ZMemory class implements the ZMachine's memory.
// It allocates real memory dynamically as it loads an initial
// story-file; it then allows bytes and words to be fetched and
// stored.
//
// Copyright (c) 1996-1997, Matthew E. Kimmel.
package jo.cc.logic.machine;

import java.io.*;

class ZMemory extends Object {
    private ZUserInterface zui;
    byte[] data;
    int dataLength;

    // The initialize routine sets things up and loads a game
    // into memory.  It is passed the ZUserInterface object
    // for this ZMachine and the filename of the story-file.
    void initialize(ZUserInterface ui,IStoryfile storyFile)
    {
        InputStream fis;
        DataInputStream dis;
        zui = ui;

        // Read in the story file
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            fis = storyFile.openStoryFile();
            dis = new DataInputStream(fis);
            for (;;)
            {
                int ch = dis.read();
                if (ch < 0)
                    break;
                baos.write(ch);
            }
            fis.close();
        }
        catch (IOException ioex) {
            zui.fatal("I/O error loading storyfile.");
        }
        data = baos.toByteArray();
        dataLength = data.length;
    }

    // Fetch a byte from the specified address
    int fetchByte(int addr)
    {
        if (addr > (dataLength - 1))
            zui.fatal("Memory fault: address " + addr);
        int i = (data[addr] & 0xff);
        return i;
    }

    // Store a byte at the specified address
    void putByte(int addr,int b)
    {
        if (addr > (dataLength - 1))
            zui.fatal("Memory fault: address " + addr);
        data[addr] = (byte)(b & 0xff);
    }

    // Fetch a word from the specified address
    int fetchWord(int addr)
    {
        int i;

        if (addr > (dataLength - 1))
            zui.fatal("Memory fault: address " + addr);
        i = (((data[addr] << 8) | (data[addr+1] & 0xff)) & 0xffff);
        return i;
    }

    // Store a word at the specified address
    void putWord(int addr,int w)
    {
        if (addr > (dataLength - 1))
            zui.fatal("Memory fault: address " + addr);
        data[addr] = (byte)((w >> 8) & 0xff);
        data[addr+1] = (byte)(w & 0xff);
    }

	// Dump the specified amount of memory, starting at the specified address,
	// to the specified DataOutputStream.
	void dumpMemory(DataOutputStream dos,int addr,int len) throws IOException
	{
		dos.write(data,addr,len);
	}

	// Read in memory stored by dumpMemory.
	void readMemory(DataInputStream dis,int addr,int len) throws IOException
	{
		dis.read(data,addr,len);
	}
}
