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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.StringTokenizer;

/**
 * <h2>Overview</h2>
 * <p>
 * The Path object is used to standard used to standardize the creation of
 * mutation of path-like structures. For: example /foo/bar/index.html.
 * </p>
 * 
 * <h2>Rules for Interperting Pathes</h2>
 * <p>
 * Below are the rules for how the constructor interprets literal paths.
 * <strong>NOTE</strong> the {@link addSegment(String)} interprets string
 * pathes in a somewhat different manner. <table>
 * <tr>
 * <th>Literal Path</th>
 * <th>Interpretation</th>
 * </tr>
 * <td> <i>/foo/bar/index.html</i> </td>
 * <td> <code>foo</code> and <code>bar</code> will be considered directory
 * segments while <code>index.html</code> will be considered a file segment.
 * This means that the <code>baseName</code> will be set to <i>index</i> and
 * the <code>fileExtension</code> will be set to <i>.html</i> </td>
 * <tr>
 * <td> <i>/foo/bar/</i>, <i>/foo/bar</i>, <i>foo/bar/</i> <i>foo/bar</i>
 * </td>
 * <td>
 * <p>
 * <code>foo</code> and <code>bar</code> will be considered directory
 * segments. <code>baseName</code> and <code>fileExtension</code> will be
 * left as <code>null</code>.
 * <p>
 * I cases where a file has no extension you must use the
 * {@link setFileSegment(String))} to manually set the file. This causes the
 * <code>baseName</code> to be set to the file name specified and the
 * <code>fileExtension</code> will be set to the empty string ("").
 * </p>
 * </td>
 * </tr>
 * </table>
 * 
 * 
 * 
 * @author <href a="mailto:weaver@apache.org">Scott T. Weaver</a>
 * 
 */
public class Path implements Serializable, Cloneable
{
    public static final String PATH_SEPERATOR = "/";

    private String path;

    private final LinkedList segments;

    private String fileName;

    private String baseName;

    private String fileExtension;

    private String queryString;

    private String pathOnly;

    public Path()
    {
        segments = new LinkedList();
    }

    public Path(String path)
    {
        this.segments = new LinkedList();
        this.path = path.replace('\\', '/');
        
        if(!this.path.startsWith("/"))
        {
            this.path ="/"+this.path;
        }

        parsePathSegments(segments, this.path, false);

        parseFileInfo(true);

        parseQueryString(path);
    }

    protected final void parsePathSegments(LinkedList segments, String path, boolean prepend)
    {
        String[] split = path.split("\\?");
        pathOnly = split[0];
        StringTokenizer t = new StringTokenizer(pathOnly, PATH_SEPERATOR);
        int index = 0;
        while (t.hasMoreTokens())
        {
            if (prepend)
            {
                segments.add(index, t.nextToken());
                index++;
            }
            else
            {
                if (fileName == null)
                {
                    segments.add(t.nextToken());
                }
                else if (fileName != null && segments.size() > 1)
                {
                    segments.add(segments.size() - 1, t.nextToken());
                }
                else if (fileName != null && segments.size() < 2)
                {
                    segments.add(0, t.nextToken());
                }
            }

        }
    }

    protected final void parseQueryString(String path)
    {
        String[] split = path.split("\\?");
        if (split.length > 1)
        {
            queryString = split[1];
        }
        else
        {
            queryString = null;
        }
    }

    protected final void parseFileInfo(boolean expectExtension)
    {
        fileName = (String) segments.getLast();
        int extIndex = fileName.lastIndexOf('.');
        if (extIndex > -1)
        {
            baseName = fileName.substring(0, extIndex);
            fileExtension = fileName.substring(extIndex);
        }
        else if (!expectExtension)
        {
            baseName = fileName;
            fileExtension = "";
        }
        else
        {
            // File segement must have been removed
            fileName = null;
            baseName = null;
            fileExtension = null;
            // Remove the query string also
            queryString = null;
        }
    }

    /**
     * Returns the segement of the path at the specified index <code>i</code>.
     * 
     * @param i
     *            index containing the segment to return.
     * @return Segment at index <code>i</code>
     * @throws ArrayIndexOutOfBoundsException
     *             if the index is not within the bounds of this Path.
     */
    public String getSegment(int i)
    {
        return (String) segments.get(i);
    }

    /**
     * <p>
     * Adds this segment to the end of the path but before the current file
     * segment, if one exists. For consistency Segments added via this method
     * are <strong>ALWAYS</strong> considered directories even when matching a
     * standrad file pattern i.e. <i>index.html</i>
     * </p>
     * <p>
     * If you need to set the file segment, please use the setFileSegment()
     * method.
     * </p>
     * 
     * @param segment
     * @return
     */
    public Path addSegment(String segment)
    {
        parsePathSegments(segments, segment, false);
        rebuildPath();
        return this;
    }

    public Path setFileSegement(String fileSegment)
    {
        // Remove existing file segment
        if (baseName != null)
        {
            segments.removeLast();
        }

        segments.add(fileSegment);
        parseFileInfo(false);
        rebuildPath();
        return this;
    }

    public Path getSubPath(int beginAtSegment)
    {
       return getSubPath(beginAtSegment, segments.size());
    }
    
    public Path getSubPath(int beginAtSegment, int endSegment)
    {
        StringBuffer newPathString = new StringBuffer();
        for (int i = beginAtSegment; i < endSegment; i++)
        {
            newPathString.append("/").append((String) segments.get(i));
        }

        if (queryString != null)
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
        return segments.size();
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
        if (obj instanceof Path)
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
        return (getClass().getName() + "::" + path).hashCode();
    }

    public Path prepend(String pathSegment)
    {
        parsePathSegments(segments, pathSegment, true);
        rebuildPath();
        return this;
    }

    public Path prepend(Path pathToAdd)
    {
        for (int i = 0; i < pathToAdd.length(); i++)
        {
            segments.add(i, pathToAdd.getSegment(i));
        }
        rebuildPath();
        return this;
    }

    public String remove(int i)
    {
        String removedSegment;
        if ((segments.size() - 1) == i && fileName != null)
        {
            removedSegment = removeFileSegment();
        }
        else
        {
            removedSegment = (String) segments.remove(i);
            rebuildPath();
        }

        return removedSegment;
    }

    public void removeQueryString()
    {
        queryString = null;
    }

    public String removeFileSegment()
    {
        if (fileName != null)
        {
            String fileSegment = (String) segments.removeLast();
            fileName = null;
            baseName = null;
            fileExtension = null;
            // Remove the query string also
            queryString = null;
            rebuildPath();
            return fileSegment;
        }
        else
        {
            return null;
        }
    }

    /**
     * Removes the last directory segment in this path. This method <strong>WILL
     * NOT</strong> remove the fileSegment, but path segment immediately before
     * it.
     * 
     * @return segment removed.
     */
    public String removeLastPathSegment()
    {
        if (fileName != null)
        {
            if (segments.size() > 1)
            {
                return remove((segments.size() - 2));
            }
            else
            {
                return null;
            }
        }
        else
        {
            String segment = (String) segments.removeLast();
            rebuildPath();
            return segment;
        }
    }

    protected final void rebuildPath()
    {
        Iterator itr = segments.iterator();
        StringBuffer newPath = new StringBuffer();
        while (itr.hasNext())
        {
            newPath.append(PATH_SEPERATOR).append((String) itr.next());
        }

        pathOnly = newPath.toString();

        if (queryString != null)
        {
            newPath.append("?").append(queryString);
        }

        path = newPath.toString();
    }

    public Object clone()
    {
        return new Path(path);
    }

    public int indexOf(String segment)
    {
        return segments.indexOf(segment.replaceAll("[\\\\ | /]", ""));
    }

    public void replaceVariable(String name, String value)
    {
        for (int i = 0; i < segments.size(); i++)
        {
            String segment = (String) segments.get(i);
            if (("{" + name + "}").equals(segment))
            {
                segments.set(i, value);
            }
        }
        rebuildPath();
    }
    
    public Path getChild(String childPath)
    {
        if(fileName != null)
        {
            return getSubPath(0, (segments.size() - 1)).addSegment(childPath);
        }
        else
        {
            return getSubPath(0, segments.size()).addSegment(childPath);
        }
        
    }
    
    public Path getChild(Path childPath)
    {
        Path child = null;
        if(fileName != null)
        {
            child = getSubPath(0, (segments.size() - 1));
            
        }
        else
        {
            child = getSubPath(0, segments.size());
        }
        
        child.addSegment(childPath.toString());
        return child;        
    }
    
    public Path getParent()
    {
        if(fileName != null)
        {
            
           return getSubPath(0, segments.size()-1);       
        }
        else
        {
            if(segments.size() > 1)
            {
                return getSubPath(0, segments.size()-1);
            }
            else
            {
                return new Path();
            }
        }
    }
}
