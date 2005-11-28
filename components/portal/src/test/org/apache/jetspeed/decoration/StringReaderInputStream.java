package org.apache.jetspeed.decoration;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;

public class StringReaderInputStream extends InputStream
{
    private final StringReader reader;
    
    public StringReaderInputStream(StringReader reader)
    {
        super();
        this.reader = reader;
    }
    
    public StringReaderInputStream(String value)
    {
        this(new StringReader(value));
    }

    public int read() throws IOException
    {
        return reader.read();
    }
   
    

}
