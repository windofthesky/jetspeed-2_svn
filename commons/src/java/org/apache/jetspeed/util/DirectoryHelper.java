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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

/**
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 */
public class DirectoryHelper
    extends
        AbstractFileSystemHelper
    implements 
        FileSystemHelper
{

    protected File directory;
    /**
     * 
     */
    public DirectoryHelper(File directory)
    {
        super();
        if(!directory.exists())
        {
            directory.mkdirs();
        }
        
        if(!directory.isDirectory())
        {
            throw new IllegalArgumentException("DirectoryHelper(File) requires directory not a file.");
        }
        this.directory = directory;
        

        
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
    public void copyFrom( File srcDirectory ) throws IOException
    {
        copyFrom(srcDirectory, new FileFilter() {
            public boolean accept(File pathname)
            {
               return true;
            }
           });
    }
    
    /**
     * <p>
     * copyFrom
     * </p>
     *
     * @see org.apache.jetspeed.util.FileSystemHelper#copyFrom(java.io.File, java.io.FileFilter)
     * @param directory
     * @param fileFilter
     * @throws IOException
     */
    public void copyFrom( File srcDirectory, FileFilter fileFilter ) throws IOException
    {
        if(!srcDirectory.isDirectory())
        {
            throw new IllegalArgumentException("DirectoryHelper.copyFrom(File) requires directory not a file.");
        }
        copyFiles(srcDirectory, directory, fileFilter);        

    }
    /**
     * 
     * <p>
     * copyFiles
     * </p>
     *
     * @param srcDir Source directory to copy from.
     * @param dstDir Destination directory to copy to.
     * @throws IOException
     * @throws FileNotFoundException

     */
    protected void copyFiles(File srcDir, File dstDir, FileFilter fileFilter) throws IOException
    {
        FileChannel srcChannel = null;
        FileChannel dstChannel = null;

        try
        {
        File[] children = srcDir.listFiles(fileFilter);
        for(int i=0; i<children.length; i++)
        {
            File child = children[i];
            if(child.isFile())
            {
                File toFile = new File(dstDir, child.getName());
                toFile.createNewFile();
                srcChannel = new FileInputStream(child).getChannel();
                dstChannel = new FileOutputStream(toFile).getChannel();
                dstChannel.transferFrom(srcChannel, 0, srcChannel.size());
                srcChannel.close();
                dstChannel.close();
            }
            else
            {
                File newSubDir = new File(dstDir, child.getName());
                newSubDir.mkdir();
                copyFiles(child, newSubDir, fileFilter);
            }
        }
        }
        finally
        {
            if ( srcChannel != null && srcChannel.isOpen() )
            {
                try
                {
                    srcChannel.close();
                }
                catch (Exception e)
                {
                    
                }
            }
            if ( dstChannel != null && dstChannel.isOpen() )
            {
                try
                {
                    dstChannel.close();
                }
                catch (Exception e)
                {
                    
                }
            }
        }
    }

    /**
     * <p>
     * remove
     * </p>
     *
     * @see org.apache.jetspeed.util.FileSystemHelper#remove()
     * 
     */
    public boolean remove()
    {
        return doRemove(directory);
    }
    
    /**
     * 
     * <p>
     * doRemove
     * </p>
     *
     * @param file
     * @return <code>true</code> if the removal war successful, otherwise returns
     * <code>false</code>.
     */
    protected boolean doRemove(File file)
    {
        if (file.isDirectory())
		{
			String[] children = file.list();
			for (int i = 0; i < children.length; i++)
			{
				boolean success = doRemove(new File(file, children[i]));
				if (!success)
				{
					return false;
				}
			}
		}

		// The directory is now empty so delete it OR it is a plain file
		return file.delete();        
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
        return directory;
    }

    /**
     * <p>
     * close
     * </p>
     *
     * @see org.apache.jetspeed.util.FileSystemHelper#close()
     * 
     */
    public void close()
    {
        // TODO Auto-generated method stub

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
        return getRootDirectory().getAbsolutePath();
    }
        
}
