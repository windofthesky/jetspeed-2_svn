/*
 * Created on Jun 25, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
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
import org.apache.jetspeed.deployment.DeploymentObject;

/**
 * @author scott
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class VFSDeploymentObject implements DeploymentObject
{
    
    protected FileSystemManager fsManager;
    protected String path;
    protected String name;
    protected FileObject fsStructure;

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
        FileObject fsObject = fsManager.toFileObject(deployArtifact);
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
