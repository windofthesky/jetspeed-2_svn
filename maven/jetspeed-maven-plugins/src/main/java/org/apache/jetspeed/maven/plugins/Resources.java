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
package org.apache.jetspeed.maven.plugins;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.configuration.PlexusConfigurationException;
import org.codehaus.plexus.util.SelectorUtils;

/**
 * @version $Id$
 * 
 */
public class Resources
{
    public static class EntrySet
    {
        private String dir;
        
        private String[] includes;

        private String[] excludes;

        public EntrySet(String dir)
        {
            this.dir = makeRelativePath(dir);
        }

        /**
         * Sets the list of exclude patterns to use. All '/' and '\' characters
         * are replaced by <code>File.separatorChar</code>, so the separator
         * used need not match <code>File.separatorChar</code>.
         * <p>
         * When a pattern ends with a '/' or '\', "**" is appended.
         * 
         * @param excludes
         *            A list of exclude patterns. May be <code>null</code>,
         *            indicating that no files should be excluded. If a non-<code>null</code>
         *            list is given, all elements must be non-<code>null</code>.
         */
        public void setExcludes(String[] excludes)
        {
            if (excludes == null || excludes.length == 0)
            {
                this.excludes = null;
            }
            else
            {
                this.excludes = new String[excludes.length];
                for (int i = 0; i < excludes.length; i++)
                {
                    String pattern;
                    pattern = fixFileSeparator(excludes[i]);
                    if (pattern.endsWith(File.separator))
                    {
                        pattern += "**";
                    }
                    pattern = makeRelativePath(pattern);
                    if ( dir.length() > 0)
                    {
                        pattern = dir + File.separatorChar + pattern;
                    }
                    this.excludes[i] = pattern;
                }
            }
        }

        /**
         * Sets the list of include patterns to use. All '/' and '\' characters
         * are replaced by <code>File.separatorChar</code>, so the separator
         * used need not match <code>File.separatorChar</code>.
         * <p>
         * When a pattern ends with a '/' or '\', "**" is appended.
         * 
         * @param includes
         *            A list of include patterns. May be <code>null</code>,
         *            indicating that all files should be included. If a non-<code>null</code>
         *            list is given, all elements must be non-<code>null</code>.
         */
        public void setIncludes(String[] includes)
        {
            if (includes == null || includes.length == 0)
            {
                this.includes = new String[1];
                if ( dir.length() > 0 )
                {
                    this.includes[0] = dir + File.separator + "**";
                }
                else
                {
                    this.includes[0] = "**";
                }
            }
            else
            {
                this.includes = new String[includes.length];
                for (int i = 0; i < includes.length; i++)
                {
                    String pattern;
                    pattern = fixFileSeparator(includes[i]);
                    if (pattern.endsWith(File.separator))
                    {
                        pattern += "**";
                    }
                    pattern = makeRelativePath(pattern);
                    if ( dir.length() > 0)
                    {
                        pattern = dir + File.separatorChar + pattern;
                    }
                    this.includes[i] = pattern;
                }
            }
        }

        public String getDir()
        {
            return dir;
        }

        public String[] getExcludes()
        {
            return excludes;
        }

        public String[] getIncludes()
        {
            return includes;
        }
    }

    private String dest;
    
    private boolean flat;

    private boolean overwrite = true;

    public EntrySet[] entries;
    
    public Resources()
    {
        dest = "";
        entries = new EntrySet[1];
        entries[0] = new EntrySet("");
        entries[0].setIncludes(null);
    }

    public Resources(PlexusConfiguration config) throws PlexusConfigurationException
    {
        EntrySet entrySet;
        List includes = new ArrayList();
        List excludes = new ArrayList();
        List entries;

        dest = makeRelativePath(config.getAttribute("dest"));
        overwrite = Boolean.valueOf(config.getAttribute("overwrite", "true")).booleanValue();
        flat = Boolean.valueOf(config.getAttribute("flat", "false")).booleanValue();

        if (config.getAttribute("dir") != null || config.getAttribute("includes") != null || config.getAttribute("excludes") != null )
        {
            entrySet = new EntrySet(config.getAttribute("dir"));
            addPatterns(config.getAttribute("includes"), includes);
            addPatterns(config.getAttribute("excludes"), excludes);
            entrySet.setIncludes((String[]) includes.toArray(new String[includes.size()]));
            entrySet.setExcludes((String[]) excludes.toArray(new String[excludes.size()]));
            this.entries = new EntrySet[1];
            this.entries[0] = entrySet;
        }
        else
        {
            entries = new ArrayList();
            PlexusConfiguration[] children = config.getChildren("entryset");
            for (int i = 0; i < children.length; i++)
            {
                includes.clear();
                excludes.clear();
                entrySet = new EntrySet(children[i].getAttribute("dir"));
                addPatterns(children[i].getAttribute("includes"), includes);
                addPatterns(children[i].getAttribute("excludes"), excludes);
                addChildPatterns(children[i].getChildren("include"), includes);
                addChildPatterns(children[i].getChildren("exclude"), excludes);
                entrySet.setIncludes((String[]) includes.toArray(new String[includes.size()]));
                entrySet.setExcludes((String[]) excludes.toArray(new String[excludes.size()]));
                
                entries.add(entrySet);
            }
            this.entries = (EntrySet[])entries.toArray(new EntrySet[entries.size()]);
        }
    }
    
    public boolean isOverwrite()
    {
        return overwrite;
    }

    private static void addPatterns(String patternList, List list)
    {
        if (patternList != null)
        {
            StringTokenizer t = new StringTokenizer(patternList, ", ");
            while (t.hasMoreTokens())
            {
                list.add(t.nextToken());
            }
        }
    }

    private static void addChildPatterns(PlexusConfiguration[] children, List list) throws PlexusConfigurationException
    {
        for (int i = 0; i < children.length; i++)
        {
            String attr = children[i].getAttribute("name");
            if (attr != null)
            {
                list.add(attr);
            }
        }
    }
    
    private static String fixFileSeparator(String name)
    {
        return name != null ? name.trim().replace('/', File.separatorChar).replace('\\', File.separatorChar) : null;
    }
    
    private static String makeRelativePath(String dir)
    {
        if ( dir != null )
        {
            dir = fixFileSeparator(dir);
            while (dir.startsWith(File.separator)) 
            {
                dir = dir.substring(File.separator.length());
            }
            while (dir.endsWith(File.separator)) 
            {
                dir = dir.substring(0,dir.length()-File.separator.length());
            }
        }
        else
        {
            dir = "";
        }
        return dir;
    }

    public String getDestFileName(String entryName, String baseDirectory)
    {
        String destFileName = null;
        if ( entryName != null )
        {
            boolean match = false;
            entryName = makeRelativePath(entryName);
            for ( int i = 0; i < entries.length; i++ )
            {
                EntrySet entrySet = entries[i];
                if ( entrySet.includes != null )
                {
                    for ( int j = 0; j < entrySet.includes.length; j++ )
                    {
                        if ( SelectorUtils.matchPath(entrySet.includes[j], entryName ) )
                        {
                            match = true;
                            break;
                        }
                    }
                }
                else
                {
                    match = true;
                }
                if ( match == true && entries[i].excludes != null )
                {
                    for ( int j = 0; j < entrySet.excludes.length; j++ )
                        if ( SelectorUtils.matchPath(entrySet.excludes[j], entryName ) )
                        {
                            match = false;
                            break;
                        }
                }
                if ( match )
                {                
                    if ( entrySet.getDir().length() > 0 )
                    {
                        entryName = entryName.substring(entrySet.getDir().length()+File.separator.length());
                    }
                    if ( flat )
                    {
                        int index = entryName.lastIndexOf( File.separator );
                        entryName = ( index >= 0 ? entryName.substring( index + 1 ) : entryName );
                    }
                    if ( baseDirectory == null )
                    {
                        baseDirectory = "";
                    }
                    else if (!baseDirectory.endsWith(File.separator))
                    {
                        baseDirectory += File.separator;
                    }
                    if (dest.length() > 0 )
                    {
                        destFileName = baseDirectory + File.separator + dest + File.separator + entryName;
                    }
                    else
                    {
                        destFileName = baseDirectory + entryName;
                    }
                    break;
                }
            }
        }
        return destFileName;
    }
}
