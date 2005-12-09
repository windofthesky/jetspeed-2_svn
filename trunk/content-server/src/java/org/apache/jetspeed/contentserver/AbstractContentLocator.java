/*
 * Copyright 2000-2001,2004 The Apache Software Foundation.
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
package org.apache.jetspeed.contentserver;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <p>
 * AbstractContentLocator
 * </p>
 * <p>
 * 
 * </p>
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver </a>
 * @version $Id$
 * 
 */
public abstract class AbstractContentLocator implements ContentLocator
{

    protected String rootPath;

    protected boolean useCachedLookup;

    protected static final Map fileCache = new HashMap();    

    protected String[] URLHints;

    protected static final Log log = LogFactory.getLog(SimpleContentLocator.class);

    protected String contextRoot;

    protected String URI;

    protected List lookupPathes;

    private String basePath;

    protected File contentFile;

    public AbstractContentLocator(String rootPath, String[] URLHints, boolean useCachedLookup, String contextRoot,
            String URI, List lookupPathes) throws FileNotFoundException
    {
        this.contextRoot = contextRoot;
        this.rootPath = rootPath;
        this.useCachedLookup = useCachedLookup;
        this.URLHints = URLHints;
        this.URI = URI;
        this.lookupPathes = lookupPathes;
        String realPath = getRealPath();
        if (realPath != null)
        {
            this.contentFile = new File(realPath);
        }
        else
        {
            throw new FileNotFoundException("Target path " + URI + " not found withint the content locations provided");
        }

    }

    public OutputStream getOutputStream() throws IOException
    {

        BufferedOutputStream bos = new BufferedOutputStream(new ByteArrayOutputStream((int) contentFile.length()));
        writeToOutputStream(bos);
        return bos;
    }

    public long writeToOutputStream(OutputStream stream) throws IOException
    {

        InputStream is = getInputStream();

        if (is != null)
        {
            try
            {
                // DST: TODO: optimize using larger blocks with Streams helper
                // utility
                long size = 0;
                for (int j = is.read(); j != -1; j = is.read())
                {
                    stream.write((byte) j);
                    size++;
                }
                return size;

            }
            finally
            {
                try
                {
                    if (is != null)
                    {
                        is.close();
                    }
                }
                catch (IOException e1)
                {
                    // ignore

                }
            }
        }
        else
        {
            return -1;
        }
    }

    /**
     * <p>
     * getInputStream
     * </p>
     * 
     * @see org.apache.jetspeed.contentserver.ContentLocator#getInputStream(java.lang.String,
     *      java.util.List)
     * @param URI
     * @param lookupPathes
     * @return
     * @throws IOException
     * @throws FileNotFoundException
     *             if the content cannot be found
     */
    public InputStream getInputStream() throws IOException
    {
        String realPath = getRealPath();

        if (realPath == null)
        {
            throw new FileNotFoundException("The " + URI + " could not be resolved by the ContentLocator");
        }

        if (contentFile != null)
        {
              return new BufferedInputStream(new FileInputStream(contentFile));
        }
        else
        {
            throw new FileNotFoundException("Failed to load content source " + realPath);
        }

    }

    public String getBasePath()
    {
        if (basePath == null)
        {
            String absPath = getRealPath();

            if (absPath != null)
            {
                absPath = absPath.replace('\\', '/');
                int startOffset = absPath.indexOf(contextRoot) + contextRoot.length();
                basePath = absPath.substring(startOffset, absPath.length());
            }
            else
            {
                basePath = URI;
            }
        }
        return basePath;

    }

    public Date getLastModified()
    {
        return new Date(contentFile.lastModified());
    }

}
