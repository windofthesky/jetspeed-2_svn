/**
 * Created on Jan 13, 2004
 *
 * 
 * @author
 */
package org.apache.jetspeed.deployment.fs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import javax.imageio.stream.FileImageInputStream;

import org.apache.commons.configuration.Configuration;

/**
 * <p>
 * FileObjectHandler
 * </p>
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 *
 */
public class FileObjectHandler implements FSObjectHandler
{

    protected File file;
	private InputStream content;

    /**
     * @see org.apache.jetspeed.deployment.fs.FSObjectHandler#getPath()
     */
    public String getPath()
    {        
        return file.getAbsolutePath();
    }

    /**
     * @see org.apache.jetspeed.deployment.fs.FSObjectHandler#setPath(java.lang.String)
     */
    public void setPath(String path)
    {
        file = new File(path);
    }

    /**
     * @see org.apache.jetspeed.deployment.fs.FSObjectHandler#setFile(java.io.File)
     */
    public void setFile(File file)
    {
        this.file = file;

    }

    /**
     * @see org.apache.jetspeed.deployment.fs.FSObjectHandler#getAsStream()
     */
    public InputStream getAsStream() throws IOException
    {
        if (content == null)
        {
            content = new FileInputStream(file);
        }
        return content;
    }

    /**
     * @see org.apache.jetspeed.deployment.fs.FSObjectHandler#getAsReader()
     */
    public Reader getAsReader() throws IOException
    {
        return new FileReader(file);
    }

    /**
     * @see org.apache.jetspeed.deployment.fs.FSObjectHandler#close()
     */
    public void close() throws IOException
    {
        if(content != null)
        {
        	content.close();	
        }
    }

    /**
     * @see org.apache.jetspeed.deployment.fs.FSObjectHandler#getConfiguration()
     */
    public InputStream getConfiguration(String configPath)
    {
        // TODO Auto-generated method stub
        return null;
    }

}
