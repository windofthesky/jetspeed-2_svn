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

import java.io.File;
import java.util.List;

/**
 * <p>
 * SimpleContentLocator
 * </p>
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver </a>
 * @version $Id$
 *  
 */
public class SimpleContentLocator extends AbstractContentLocator implements ContentLocator
{
    
    protected String realPath;
    
    /**
     * @param rootPath
     * @param URLHints
     * @param useCachedLookup
     * @param contextRoot
     * @param URI
     * @param lookupPathes
     */
    public SimpleContentLocator( String rootPath, String[] URLHints, boolean useCachedLookup, String contextRoot, String URI, List lookupPathes )
    {
        super(rootPath, URLHints, useCachedLookup, contextRoot, URI, lookupPathes);
    }  

    /**
     * <p>
     * getRealPath
     * </p>
     * 
     * @see org.apache.jetspeed.contentserver.ContentLocator#getRealPath()
     * @return
     */
    public String getRealPath()
    {
        if (realPath == null)
        {
            for (int j = 0; j < URLHints.length; j++)
            {
                String URLHint = URLHints[j];
                int rootLen = URLHint.length();
                // int rootStart = URI.indexOf(URLHint);
                int rootStart = URI.lastIndexOf(URLHint);
                File fqFile = null;
                if (rootStart != -1)
                {
                    String dir = null;
                    if (rootLen > 1)
                    {
                        dir = URI.substring(rootStart + rootLen);
                    }
                    else
                    {
                        dir = URI.substring(contextRoot.length());

                    }

                    for (int i = 0; i < lookupPathes.size(); i++)
                    {

                        if (useCachedLookup && fileCache.containsKey(lookupPathes.get(i) + ":" + URI))
                        {
                            realPath = (String) fileCache.get(lookupPathes.get(i) + ":" + URI);
                            log.debug("Found cached file for URI: " + URI);
                            return realPath;
                        }
                        else
                        {
                            // String fqPath = pathes.get(i) + "/html" + dir;
                            String[] sep = new String[]{"", ""};

                            if (lookupPathes.get(i).toString().trim().length() > 1)
                            {
                                sep[0] = "/";
                            }

                            if (!dir.startsWith("/"))
                            {
                                sep[1] = "/";
                            }

                            String fqPath = this.rootPath + sep[0] + lookupPathes.get(i) + sep[1] + dir;

                            fqFile = new File(fqPath);
                            log.debug("Actual content located at: " + fqPath);
                            log.debug("Content exists? " + fqFile.exists());
                            if (!fqFile.exists())
                            {
                                fqFile = null;
                                continue;
                            }

                            if (useCachedLookup)
                            {
                                fileCache.put(lookupPathes.get(i) + ":" + URI, fqPath);
                            }
                            realPath = fqPath;
                            return realPath;
                        }
                    }
                }
            }
        }

        return realPath;
    }
}