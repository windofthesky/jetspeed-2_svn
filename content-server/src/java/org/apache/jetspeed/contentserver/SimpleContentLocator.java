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
package org.apache.jetspeed.contentserver;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <p>
 * SimpleContentLocator
 * </p>
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver </a>
 * @version $Id $
 *  
 */
public class SimpleContentLocator implements ContentLocator
{    

    private String rootPath;

    private boolean useCachedLookup;

    private Map fileCache;

    private String URLHint;
    
    private static final Log log = LogFactory.getLog(SimpleContentLocator.class);

    public SimpleContentLocator(String rootPath, String URLHint, boolean useCachedLookup)
    {

        this.rootPath = rootPath;
        this.useCachedLookup = useCachedLookup;
        fileCache = new HashMap();
        this.URLHint = URLHint;
    }

    public long mergeContent(String URI, List lookupPathes, OutputStream os)
    {
        File content = locateContent(URI, lookupPathes);
        if(content != null)
        {
            return setContent(content, os);
        }
        else
        {
            return -1;
        }
    }

    protected File locateContent(String URI, List lookupPathes)
    {
        int rootLen = URLHint.length();
        // int rootStart = URI.indexOf(URLHint);
        int rootStart = URI.lastIndexOf(URLHint);
        File fqFile = null;
        if (rootStart != -1)
        {
            String dir = URI.substring(rootStart + rootLen);
            
            for (int i = 0; i < lookupPathes.size(); i++)
            {
                
                if (useCachedLookup && fileCache.containsKey(lookupPathes.get(i) + ":" + URI))
                {
                    fqFile = (File) fileCache.get(lookupPathes.get(i) + ":"
                            + URI);
                    log.debug("Found cached file for URI: " + URI);
                }
                else
                {
                    // String fqPath = pathes.get(i) + "/html" + dir;
                    String[] sep = new String[]{"", ""} ;
                    
                    if (lookupPathes.get(i).toString().trim().length() > 1)
                    {
                        sep[0] = "/";
                    }
                    
                    if (!dir.startsWith("/"))
                    {
                        sep[1] = "/";
                    }
                    
                    String fqPath = this.rootPath + sep[0] + lookupPathes.get(i)
                                    + sep[1] + dir;

                    fqFile = new File(fqPath);
                    log.debug("Actual content located at: " + fqPath);
                    log.debug("Content exists? " + fqFile.exists());
                    if (!fqFile.exists())
                    {
                        fqFile = null;
                        continue;
                    }
                    
                    if(useCachedLookup)
                    {
                        fileCache.put(lookupPathes.get(i) + ":" + URI, fqFile);
                    }
                    return fqFile;
                }
            }
        }
        
        return null;
        
    }
    
    protected long setContent(File fqFile, OutputStream os)
    {
        BufferedInputStream bis = null;
        try
        {
            // DST: TODO: optimize using larger blocks with Streams helper utility
            bis = new BufferedInputStream(new FileInputStream(fqFile));
            for (int j = bis.read(); j != -1; j = bis.read())
            {
                os.write((byte) j);
            }
            log.debug("Wrote " + fqFile.length()
                    + " to the output stream.");

            return fqFile.length();

        } catch (Exception e)
        {
            e.printStackTrace();
            return -1;
        } 
        finally
        {
            try
            {
                if (bis != null)
                {
                    bis.close();
                }
            } catch (IOException e1)
            {
                // ignore

            }
        }
        
    }

}
