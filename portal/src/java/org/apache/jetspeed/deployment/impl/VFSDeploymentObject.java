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
package org.apache.jetspeed.deployment.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.FileSystemManager;
import org.apache.commons.vfs.FileType;
import org.apache.commons.vfs.impl.DefaultFileSystemManager;
import org.apache.jetspeed.deployment.DeploymentObject;

/**
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver </a>
 * @version $Id$
 */
public class VFSDeploymentObject implements DeploymentObject
{
    
    protected FileSystemManager fsManager;
    protected String path;
    protected String name;
    protected FileObject fsStructure;
    protected FileObject fsObject;

    /**
     * @throws IOException
     * 
     */
    public VFSDeploymentObject(String path, FileSystemManager fsManager) throws IOException
    {
       this(new File(path), fsManager);      
    }
    
    public VFSDeploymentObject(File deployArtifact, FileSystemManager fsManager) throws IOException
    {
        if(!deployArtifact.exists())
        {
            throw new FileNotFoundException("The deployment artifact "+deployArtifact.getAbsolutePath()+" does not exist");
        }
        this.name = deployArtifact.getName();
        this.fsManager = fsManager;
        path = deployArtifact.getAbsolutePath();
        fsObject = fsManager.toFileObject(deployArtifact);
        if(fsObject.getType().equals(FileType.FILE))
        {            
            try
            {
                fsStructure = fsManager.createFileSystem(fsObject);
            }
            catch (FileSystemException e)
            {
                // This is here to prevent non-archive files from blowing us up
                fsStructure = fsObject;
            }
        }
        else
        {
            fsStructure = fsObject;
        }
                
    }

    /**
     * <p>
     * getAsStream
     * </p>
     *
     * @see org.apache.jetspeed.deployment.DeploymentObject#getAsStream()
     * @return
     * @throws IOException
     */
    public InputStream getAsStream() throws IOException
    {       
        return fsStructure.getContent().getInputStream();
    }

    /**
     * <p>
     * getAsReader
     * </p>
     *
     * @see org.apache.jetspeed.deployment.DeploymentObject#getAsReader()
     * @return
     * @throws IOException
     */
    public Reader getAsReader() throws IOException
    {
        return new InputStreamReader(getAsStream());
    }

    /**
     * <p>
     * close
     * </p>
     *
     * @see org.apache.jetspeed.deployment.DeploymentObject#close()
     * @throws IOException
     */
    public void close() throws IOException
    {
        fsStructure.close();
        fsObject.close();
        fsManager.getFilesCache().removeFile(fsStructure.getFileSystem(), fsStructure.getName());
        fsManager.getFilesCache().removeFile(fsObject.getFileSystem(), fsObject.getName());
        //((DefaultFileSystemManager)fsManager).close();
        //((DefaultFileSystemManager)fsManager).init();
    }

    /**
     * <p>
     * getConfiguration
     * </p>
     *
     * @see org.apache.jetspeed.deployment.DeploymentObject#getConfiguration(java.lang.String)
     * @param configPath
     * @return
     * @throws IOException
     */
    public InputStream getConfiguration( String configPath ) throws IOException
    {       
        try
        {
            FileObject configObj = fsStructure.resolveFile(configPath);
            if(configObj != null && configObj.exists())
            {
                return configObj.getContent().getInputStream();
            }
            else
            {
                return null;
            }
        }
        catch (FileSystemException e)
        {
           return null;
        }
       
    }

    /**
     * <p>
     * getName
     * </p>
     *
     * @see org.apache.jetspeed.deployment.DeploymentObject#getName()
     * @return
     */
    public String getName()
    {
        return name;
    }
    /**
     * <p>
     * getPath
     * </p>
     *
     * @see org.apache.jetspeed.deployment.DeploymentObject#getPath()
     * @return
     */
    public String getPath()
    {
        return path;
    }
    /**
     * <p>
     * getFileObject
     * </p>
     *
     * @see org.apache.jetspeed.deployment.DeploymentObject#getFileObject()
     * @return
     */
    public FileObject getFileObject()
    {       
        return fsStructure;
    }
}
