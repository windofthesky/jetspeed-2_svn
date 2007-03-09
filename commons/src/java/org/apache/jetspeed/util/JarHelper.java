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
package org.apache.jetspeed.util;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver </a>
 * 
 * Creates a a temp directory to which a JarFile is expanded and can be
 * manipulated. All operations are performed by an internal instance of
 * {@link DirectoryHelper}.
 *  
 */
public class JarHelper 
    extends
        AbstractFileSystemHelper
    implements 
        FileSystemHelper
{
    protected JarFile jarFile;
    protected DirectoryHelper dirHelper;
    protected File file;
    private boolean deleteOnClose;
    protected File jarRoot;

    /**
     * 
     * @param jarFile
     * @throws IOException
     */
    public JarHelper( File file, boolean deleteOnClose ) throws IOException
    {
        this.jarFile = new JarFile(file);
        this.deleteOnClose = deleteOnClose;
        this.file = file;
        String tmpDirPath = System.getProperty("java.io.tmpdir");
        File tmpDir = new File(tmpDirPath);
        jarRoot = null;

        jarRoot = new File(tmpDir, "jetspeed-jar-tmp/" + file.getName());

        if (!jarRoot.exists())
        {
            jarRoot.mkdirs();
        }
        jarRoot.deleteOnExit();

        Enumeration entries = this.jarFile.entries();
        while (entries.hasMoreElements())
        {
            JarEntry jarEntry = (JarEntry) entries.nextElement();
            String name = jarEntry.getName();
            if (jarEntry.isDirectory())
            {
                File newDir = new File(jarRoot, name);
                newDir.mkdir();
                newDir.deleteOnExit();
            }
            else
            {
                copyEntryToFile(jarFile, jarRoot, jarEntry);
            }
        }

        dirHelper = new DirectoryHelper(jarRoot);
    }

    /**
     * <p>
     * copyEntryToFile
     * </p>
     * 
     * @param jarFile
     * @param jarRoot
     * @param jarEntry
     * @throws IOException
     * @throws FileNotFoundException
     */
    protected void copyEntryToFile( JarFile jarFile, File jarRoot, JarEntry jarEntry ) throws IOException,
            FileNotFoundException
    {
        String name = jarEntry.getName();
        File file = new File(jarRoot, name);
        if (!file.getParentFile().exists())
        {
            file.getParentFile().mkdirs();
            file.getParentFile().deleteOnExit();
        }
        file.createNewFile();
        file.deleteOnExit();

        InputStream is = null;
        OutputStream os = null;
        try
        {
            is = jarFile.getInputStream(jarEntry);
            os = new FileOutputStream(file);

            byte[] buf = new byte[1024];
            int len;
            while ((len = is.read(buf)) > 0)
            {
                os.write(buf, 0, len);
            }

        }
        finally
        {
            if (is != null)
            {
                is.close();
            }

            if (os != null)
            {
                os.close();
            }
        }
    }

    /**
     * <p>
     * copyFrom
     * </p>
     *
     * @param directory
     * @param fileFilter
     * @throws IOException
     */
    public void copyFrom( File directory, FileFilter fileFilter ) throws IOException
    {
        dirHelper.copyFrom(directory, fileFilter);
    }
    /**
     * <p>
     * copyFrom
     * </p>
     * 
     * @see org.apache.jetspeed.util.FileSystemHelper#copyFrom(java.io.File)
     * @param directory
     * @throws IOException
     */
    public void copyFrom( File directory ) throws IOException
    {
        dirHelper.copyFrom(directory);
    }

    /**
     * <p>
     * getRootDirectory
     * </p>
     * 
     * @see org.apache.jetspeed.util.FileSystemHelper#getRootDirectory()
     * @return
     */
    public File getRootDirectory()
    {
        return dirHelper.getRootDirectory();
    }

    /**
     * <p>
     * remove
     * </p>
     * 
     * @see org.apache.jetspeed.util.FileSystemHelper#remove()
     * @return
     */
    public boolean remove()
    {
        return dirHelper.remove();
    }

    /**
     * <p>
     * close
     * </p>
     * 
     * @throws IOException
     * 
     * @see org.apache.jetspeed.util.FileSystemHelper#close()
     *  
     */
    public void close() throws IOException
    {
        jarFile.close();
        if (deleteOnClose)
        {
            // remove all temporary files
            dirHelper.remove();
        }
        
        dirHelper.close();
    }

    /**
     * <p>
     * getSourcePath
     * </p>
     * 
     * @see org.apache.jetspeed.util.FileSystemHelper#getSourcePath()
     * @return
     */
    public String getSourcePath()
    {
        return file.getAbsolutePath();
    }
}