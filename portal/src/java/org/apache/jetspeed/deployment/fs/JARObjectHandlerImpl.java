/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2000-2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *    "Apache Jetspeed" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Apache Jetspeed", nor may "Apache" appear in their name, without
 *    prior written permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
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
