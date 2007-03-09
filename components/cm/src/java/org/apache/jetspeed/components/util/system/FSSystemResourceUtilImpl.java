/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.jetspeed.components.util.system;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * <p>
 * FSSystemResourceUtilImpl
 * </p>
 * <p>
 *   Locates resources relative to the root file system path
 * </p>
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 *
 */
public class FSSystemResourceUtilImpl implements SystemResourceUtil
{
    private String systemRoot;

    /**
     * 
     * @param systemRoot  The root from which all resource
     * URLs will be constructed.
     */
    public FSSystemResourceUtilImpl(String systemRoot) throws IOException
    {
        String absPath = new File(systemRoot).getCanonicalPath();
        // Append trailing seperator
        if (endsWithSeperator(absPath))
        {
			this.systemRoot = absPath;
        }
        else
        {
			this.systemRoot = absPath + File.separator;            
        }

    }

    /**
     * @see org.apache.jetspeed.components.util.system.SystemResourceUtil#getSystemRoot()
     */
    public String getSystemRoot()
    {
        return systemRoot;
    }

    /**
     * @see org.apache.jetspeed.components.util.system.SystemResourceUtil#getURL(java.lang.String)
     */
    public URL getURL(String relativePath) throws MalformedURLException
    {
        if (beginsWithSeperator(relativePath) && relativePath.length() > 1)
        {
            return new File(systemRoot + relativePath.substring(1)).toURL();
        }
        else
        {
            return new File(systemRoot + relativePath).toURL();
        }

    }

    private boolean endsWithSeperator(String path)
    {
        return path.endsWith("/") || path.endsWith(File.separator);
    }

    private boolean beginsWithSeperator(String path)
    {
        return path.startsWith("/") || path.startsWith(File.separator);
    }

}
