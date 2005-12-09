/*
 * Copyright 2005 The Apache Software Foundation.
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
package org.apache.jetspeed.portalsite.view;

import org.apache.jetspeed.om.folder.Folder;

/**
 * This class represents a search path along with a profile
 * locator name used to construct the logical site view. The
 * profiler locator name is uses to identify and group
 * located nodes within the view.
 * 
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id$
 */
public class SiteViewSearchPath
{
    /**
     * locatorName - profile locator name
     */
    private String locatorName;

    /**
     * searchPath - search path
     */
    private String searchPath;

    /**
     * SiteViewSearchPath - validating constructor that strips any trailing
     *                      folder separator from search path
     *
     * @param locatorName profile locator name
     * @param searchPath search path
     */
    public SiteViewSearchPath(String locatorName, String searchPath)
    {
        this.locatorName = locatorName;
        if (searchPath.endsWith(Folder.PATH_SEPARATOR) && !searchPath.equals(Folder.PATH_SEPARATOR))
        {
            this.searchPath = searchPath.substring(0, searchPath.length()-1);
        }
        else
        {
            this.searchPath = searchPath;
        }
    }

    /**
     * toString - return search path
     *
     * @return search path
     */
    public String toString()
    {
        return searchPath;
    }

    /**
     * equals - compare as string to search path
     *
     * @return equals result
     */
    public boolean equals(Object obj)
    {
        if (obj instanceof String)
        {
            return searchPath.equals((String)obj);
        }
        return searchPath.equals(obj.toString());
    }

    /**
     * hashCode - return search path hash code
     *
     * @return hash code
     */
    public int hashCode()
    {
        return searchPath.hashCode();
    }

    /**
     * getLocatorName - return profile locator name
     *
     * @return profile locator name
     */
    public String getLocatorName()
    {
        return locatorName;
    }
}
