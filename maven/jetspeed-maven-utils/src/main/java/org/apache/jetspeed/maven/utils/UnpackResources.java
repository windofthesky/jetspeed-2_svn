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
package org.apache.jetspeed.maven.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.zip.ZipFile;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.codehaus.plexus.util.SelectorUtils;

/**
 * @version $Id$
 * 
 */
public class UnpackResources
{
    public static class Resource
    {
        private String path;
        private String destination;
        private Boolean overwrite;
        private Boolean flat;
        private String include;
        private String exclude;
        private String name;
    }
    
    private static class UnpackResource
    {
        private static class EntrySet
        {
            private String dir;
            
            private String[] includes;

            private String[] excludes;

            public EntrySet(String dir)
            {
                this.dir = makeRelativePath(dir);
            }

            /**
             * Sets the list of exclude patterns to use. All '/' and '\'
             * characters are replaced by <code>File.separatorChar</code>, so
             * the separator used need not match <code>File.separatorChar</code>.
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
             * Sets the list of include patterns to use. All '/' and '\'
             * characters are replaced by <code>File.separatorChar</code>, so
             * the separator used need not match <code>File.separatorChar</code>.
             * <p>
             * When a pattern ends with a '/' or '\', "**" is appended.
             * 
             * @param includes
             *            A list of include patterns. May be <code>null</code>,
             *            indicating that all files should be included. If a
             *            non-<code>null</code> list is given, all elements
             *            must be non-<code>null</code>.
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

        private boolean overwrite;
        
        private String name;

        public EntrySet entrySet;
        
        public UnpackResource(boolean overwrite)
        {
            dest = "";
            this.overwrite = overwrite;
            entrySet = new EntrySet("");
            entrySet.setIncludes(null);
        }

        public UnpackResource(Resource resource, Boolean defaultOverwrite)
        {
            dest = makeRelativePath(resource.destination);
            overwrite = getValue(resource.overwrite, defaultOverwrite).booleanValue();
            flat = getValue(resource.flat, Boolean.FALSE).booleanValue();
            name = resource.name;

            if (resource.path != null || resource.include != null || resource.exclude != null )
            {
                entrySet = new EntrySet(resource.path);
                entrySet.setIncludes(getPatterns(resource.include));
                entrySet.setExcludes(getPatterns(resource.exclude));
            }
            else
            {
                entrySet = new EntrySet("");
                entrySet.setIncludes(null);
            }
        }
        
        public boolean isOverwrite()
        {
            return overwrite;
        }

        private static String[] getPatterns(String patternList)
        {
            StringTokenizer t = null;
            int count = 0;
            if (patternList != null)
            {
                t = new StringTokenizer(patternList, ", ");
                count = t.countTokens();
            }
            String[] tokens = new String[count];
            for (int i = 0; i < count; i++ )
            {
                tokens[i] = t.nextToken();
            }
            return tokens;
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
                if ( match == true && entrySet.excludes != null )
                {
                    for ( int j = 0; j < entrySet.excludes.length; j++ )
                    {
                        if ( SelectorUtils.matchPath(entrySet.excludes[j], entryName ) )
                        {
                            match = false;
                            break;
                        }
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
                        destFileName = baseDirectory + File.separator + dest + File.separator + ((name == null) ? entryName : name);
                    }
                    else
                    {
                        destFileName = baseDirectory + ((name == null) ? entryName : name);
                    }
                }
            }
            return destFileName;
        }
    }
    
    private String artifact;
    private String file;
    private String targetDirectory;
    private Boolean overwrite = Boolean.TRUE;
    private Resource[] resources;
    
    private static String getValue(String value, String defaultValue)
    {
        return value != null ? value : defaultValue;
    }
    
    private static Boolean getValue(Boolean value, Boolean defaultValue)
    {
        return value != null ? value : defaultValue;
    }
    
    public void unpack(Artifacts artifacts, String defaultTargetDirectory, Log log, boolean verbose) throws MojoExecutionException
    {
        File resourceFile = null;
        if (artifact == null && file == null && artifacts.size() == 1)
        {
            resourceFile = artifacts.getFirstArtifact().getFile();
        }
        else if ((artifact == null && file == null) || (artifact != null && file != null))
        {
            throw new MojoExecutionException("Either specify artifact or file: artifacts.size="+artifacts.size());
        }
        else if (artifact != null)
        {
            Artifact artifactObj = artifacts.get(artifact);
            if (artifactObj == null)
            {
                throw new MojoExecutionException("unpack artifact "+artifact+" not defined as plugin dependency");
            }
            resourceFile = artifactObj.getFile();
        }
        else // (file != null)
        {
            resourceFile = new File(file);
            if (!resourceFile.exists() || !resourceFile.isFile())
            {
                throw new MojoExecutionException("Invalid or non-existing file: "+resourceFile);
            }
        }
        
        UnpackResource[] unpackResources = null;
        if (resources == null)
        {
            unpackResources = new UnpackResource[1];
            unpackResources[0] = new UnpackResource(overwrite.booleanValue());
        }
        else
        {
            unpackResources = new UnpackResource[resources.length];
            for (int i = 0; i < resources.length; i++)
            {
                unpackResources[i] = new UnpackResource(resources[i], overwrite);
            }            
        }
        
        targetDirectory = getValue(targetDirectory, defaultTargetDirectory);
        File targetBaseDir = new File(targetDirectory);
        if ( targetBaseDir.exists())
        {
            if (!targetBaseDir.isDirectory())
            {
                throw new MojoExecutionException("Invalid target directory "+targetBaseDir.getAbsolutePath()+": not a directory");
            }
        }
        else
        {
            targetBaseDir.mkdirs();
        }
        
        FileEntryCollection fileEntryCollection = null;
        ZipFile zipFile = null;
        
        try
        {
            if (!resourceFile.isDirectory())
            {
                zipFile = new ZipFile( resourceFile );
                fileEntryCollection = new FileEntryCollection( zipFile );
            }
            else
            {
                fileEntryCollection = new FileEntryCollection( resourceFile );
            }
            
            FileEntry fileEntry = null;
            File firstDestFile;
            
            Enumeration<? extends FileEntry> entries = fileEntryCollection.entries();
            
            while ( entries.hasMoreElements() )
            {
                fileEntry = entries.nextElement();
                
                if (!fileEntry.isDirectory())
                {
                    firstDestFile = null;
                    for ( int i = 0; i < unpackResources.length; i++ )
                    {
                        String destFileName = unpackResources[i].getDestFileName(fileEntry.getName(), targetDirectory);
                        if ( destFileName != null )
                        {
                            File destFile = new File(destFileName);
                            if ( destFile.exists() )
                            {
                                if (!destFile.isFile() )
                                {
                                    throw new MojoExecutionException("Destination "+destFile.getAbsolutePath()+" already exists and is not a file");
                                }
                                if ( destFile.lastModified() >= fileEntry.getTime() || !unpackResources[i].isOverwrite() )
                                {
                                    if (verbose)
                                    {
                                        log.info(fileEntry.getName()+" skipped: already exists at "+destFile.getAbsolutePath());
                                    }
                                    else
                                    {
                                        log.debug(fileEntry.getName()+" skipped: already exists at "+destFile.getAbsolutePath());
                                    }
                                    continue;
                                }
                            }
                            else
                            {
                                destFile.getParentFile().mkdirs();
                            }
                            byte[] buffer = new byte[1024];
                            int length = 0;
                            InputStream is = null;
                            FileOutputStream fos = null;
                            try
                            {
                                if (firstDestFile == null)
                                {
                                    firstDestFile = destFile;
                                    is = fileEntryCollection.getInputStream(fileEntry);
                                }
                                else
                                {
                                    is = new FileInputStream(firstDestFile);
                                }
                                fos = new FileOutputStream( destFile );

                                while ( ( length =
                                    is.read( buffer ) ) >= 0 )
                                {
                                    fos.write( buffer, 0, length );
                                }

                                fos.close();
                                fos = null;
                            }
                            finally
                            {
                                if (is != null)
                                {
                                    try
                                    {
                                        is.close();
                                    }
                                    catch (IOException e)
                                    {                                        
                                    }
                                }
                                
                                if ( fos != null )
                                {
                                    try
                                    {
                                        fos.close();
                                    }
                                    catch ( IOException e )
                                    {
                                    }
                                }
                            }
                            destFile.setLastModified(fileEntry.getTime());
                            if (verbose)
                            {
                                log.info(fileEntry.getName()+" extracted to "+destFile.getAbsolutePath());
                            }
                            else
                            {
                                log.debug(fileEntry.getName()+" extracted to "+destFile.getAbsolutePath());
                            }
                        }
                    }
                }
            }
        }
        catch ( IOException ioe )
        {
            throw new MojoExecutionException("Error while unpacking " + resourceFile.getPath(), ioe);
        }
        finally
        {
            if ( zipFile != null )
            {
                try
                {
                    zipFile.close();
                }
                catch ( IOException e )
                {
                }
            }
        }
    }
}
