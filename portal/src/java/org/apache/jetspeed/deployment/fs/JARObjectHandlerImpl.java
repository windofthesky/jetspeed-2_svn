/*
 * Copyright 2000-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.jetspeed.deployment.fs;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <p>
 * JARObjectHandlerImpl
 * </p>
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 *
 */
public class JARObjectHandlerImpl implements FSObjectHandler
{
	private File jar;
	private JarInputStream content;
	
    private JarFile jarFile;
    
    private static final Log log = LogFactory.getLog("deployment"); 
    /**
     * @see org.apache.jetspeed.deployment.fs.FSObjectHandler#getPath()
     */
    public String getPath()
    {        
        return jar.getAbsolutePath();
    }

    /**
     * @see org.apache.jetspeed.deployment.fs.FSObjectHandler#setPath(java.lang.String)
     */
    public void setPath(String path)
    {
        this.jar = new File(path); 

    }

    /**
     * @see org.apache.jetspeed.deployment.fs.FSObjectHandler#setFile(java.io.File)
     */
    public void setFile(File file) throws IOException
    {        
        jar = file;
        
        // On undeployment, the archive will not exist
        if(jar.exists())
        {
			jarFile = new JarFile(jar);
        }
		
    }
    
	public File getFile()
	{        
		return jar;
	}

    /**
     * @see org.apache.jetspeed.deployment.fs.FSObjectHandler#getAsStream()
     */
    public InputStream getAsStream() throws IOException
    {        
    	if(content == null)
    	{
			content = new JarInputStream(new FileInputStream(jar));
            
    	}
        return content;
    }

    /**
     * @see org.apache.jetspeed.deployment.fs.FSObjectHandler#getAsReader()
     */
    public Reader getAsReader() throws IOException
    {        
        return new InputStreamReader(getAsStream());
    }  
    /**
     * @see org.apache.jetspeed.deployment.fs.FSObjectHandler#close()
     */
    public void close() throws IOException
    {
    	// prevent resource leaking
    	if(jarFile != null)
    	{
			jarFile.close();
    	}    	
    	jar=null;
    	jarFile=null;
        if(content != null)
        {
        	content.close();
        	content = null;
        }

    }

    /**
     * @see org.apache.jetspeed.deployment.fs.FSObjectHandler#getConfiguration()
     */
    public InputStream getConfiguration(String configPath) throws IOException
    {
    	JarEntry jarEntry = jarFile.getJarEntry(configPath);
    	// null indicates this is probably not a deployment
    	// we need to be concerned about 
    	if(jarEntry == null)
    	{
    		return null;
    	}
    	return jarFile.getInputStream(jarEntry);
		
    }

}
