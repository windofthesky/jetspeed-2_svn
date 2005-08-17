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
package org.apache.jetspeed.util;

import java.io.Serializable;
import java.util.StringTokenizer;

public class Path implements Serializable
{
    public static final String PATH_SEPERATOR = "/";

    private final String path;

    private final String[] segments;

    private final String fileName;

    private final String baseName;

    private final String fileExtension;

    private final String queryString;
    
    private final String pathOnly;

    public Path(String path)
    {
        this.path = path;
        String[] split = path.split("\\?");
        pathOnly = split[0];
        StringTokenizer t = new StringTokenizer(pathOnly, PATH_SEPERATOR);
        segments = new String[t.countTokens()];

        int i = 0;

        while (t.hasMoreTokens())
        {
            segments[i] = t.nextToken();
            i++;
        }
        fileName = segments[(segments.length - 1)];
        int extIndex = fileName.lastIndexOf('.');
        
        if(extIndex > -1)
        {
            baseName = fileName.substring(0, extIndex);
            fileExtension = fileName.substring(extIndex);
        }
        else
        {
            baseName =  fileName;
            fileExtension = null;
        }

        if (split.length > 1)
        {
            queryString = split[1];
        }
        else
        {
            queryString = null;
        }
    }

    public String getSegment(int i)
    {
        return segments[i];
    }
    
    public Path getSubPath(int beginAtSegment)
    {
        StringBuffer newPathString = new StringBuffer();
        for(int i=beginAtSegment; i<segments.length; i++)
        {
            newPathString.append("/").append(segments[i]);
        }
        
        if(queryString != null)
        {
            newPathString.append("?").append(queryString);
        }
        
        return new Path(newPathString.toString());
    }

    public String getBaseName()
    {
        return baseName;
    }
    

    public String getFileExtension()
    {
        return fileExtension;
    }
    

    public String getFileName()
    {
        return fileName;
    }
    

    public String getQueryString()
    {
        return queryString;
    }
    
    public int length()
    {
        return segments.length;
    }    
        
    public String toString()
    {
        return path;
    }
    
    public String pathOnly()
    {
        return pathOnly;
    }

    public boolean equals(Object obj)
    {
        if(obj instanceof Path)
        {
            return ((Path) obj).path.equals(this.path);
        }
        else
        {
            return false;
        }
    }

    public int hashCode()
    {        
        return (getClass().getName()+"::"+path).hashCode();
    }
    


}
