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
    private static final String USER_PATH_PATTERN = ".*"+Folder.USER_FOLDER+"[^"+Folder.PATH_SEPARATOR+"]+";
    
    /**
     * locatorName - profile locator name
     */
    private String locatorName;

    /**
     * searchPath - search path
     */
    private String searchPath;

    /**
     * userPath - user path flag
     */
    private boolean userPath;

    /**
     * principalPath - principal path flag
     */
    private boolean principalPath;

    /**
     * pathDepth - path depth
     */
    private int pathDepth;

    /**
     * SiteViewSearchPath - root path constructor
     *
     * @param locatorName profile locator name
     */
    public SiteViewSearchPath(String locatorName)
    {
        this(locatorName, Folder.PATH_SEPARATOR, false, false, 0);
    }
    
    /**
     * SiteViewSearchPath - validating constructor that strips any trailing
     *                      folder separator from search path
     *
     * @param locatorName profile locator name
     * @param searchPath search path
     */
    public SiteViewSearchPath(String locatorName, String searchPath)
    {
        this(locatorName, searchPath, searchPath.matches(USER_PATH_PATTERN), principalSearchPath(searchPath), searchPathDepth(searchPath));
    }
    
    /**
     * searchPathDepth - compute search path depth
     *
     * @param searchPath search path
     */
    private static int searchPathDepth(String searchPath)
    {
        // count control/reserved folder names in path
        int depth = 0;
        int length = searchPath.length();
        int prefixLength = Folder.RESERVED_FOLDER_PREFIX.length();
        int index = searchPath.indexOf(Folder.PATH_SEPARATOR_CHAR);
        while (index != -1)
        {
            if (searchPath.regionMatches(index+1, Folder.RESERVED_FOLDER_PREFIX, 0, prefixLength))
            {
                depth++;
            }
            index = searchPath.indexOf(Folder.PATH_SEPARATOR_CHAR, index+1);
        }
        return depth;
    }

    /**
     * principalSearchPath - compute principal search path flag
     *
     * @param principal search path flag
     */
    private static boolean principalSearchPath(String searchPath)
    {
        // test reserved principal folder names in path
        return (searchPath.contains(Folder.USER_FOLDER) || searchPath.contains(Folder.ROLE_FOLDER) || searchPath.contains(Folder.GROUP_FOLDER));
    }

    /**
     * SiteViewSearchPath - validating constructor that strips any trailing
     *                      folder separator from search path
     *
     * @param locatorName profile locator name
     * @param searchPath search path
     * @param userPath user path flag
     * @param principalPath principal path flag
     * @param pathDepth path depth
     */
    public SiteViewSearchPath(String locatorName, String searchPath, boolean userPath, boolean principalPath, int pathDepth)
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
        this.userPath = userPath;
        this.principalPath = principalPath;
        this.pathDepth = pathDepth;
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
            return searchPath.equals(obj);
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
    
    /**
     * isUserPath - return user path flag
     *
     * @return user path flag
     */
    public boolean isUserPath()
    {
        return userPath;
    }

    /**
     * isPrincipalPath - return principal path flag
     *
     * @return principal path flag
     */
    public boolean isPrincipalPath()
    {
        return principalPath;
    }
    
    /**
     * getPathDepth - return path depth
     *
     * @return path depth
     */
    public int getPathDepth()
    {
        return pathDepth;
    }
}
