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
package org.apache.jetspeed.deployment.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.apache.jetspeed.deployment.DeploymentObject;
import org.apache.jetspeed.util.DirectoryHelper;
import org.apache.jetspeed.util.FileSystemHelper;
import org.apache.jetspeed.util.JarHelper;

/**
 * @author scott
 *
 */
public class StandardDeploymentObject implements DeploymentObject
{
    protected FileSystemHelper fsh;
    
    /**
     * @throws IOException
     * 
     */
    public StandardDeploymentObject(File deploymentObject) throws IOException, FileNotDeployableException
    {
        super();
        if(deploymentObject.isDirectory())
        {
            fsh = new DirectoryHelper(deploymentObject);
        }
        else if(verifyExtension(deploymentObject))
        {
            boolean deleteOnClose = !deploymentObject.getName().startsWith("jetspeed-");           
            fsh = new JarHelper(deploymentObject, deleteOnClose);
        }
        else
        {
            throw new FileNotDeployableException("File type for "+deploymentObject.getName()+" is not supported by StandardDeploymentObject.");
        }
        
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
        fsh.close();

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
            return new FileInputStream(new File(fsh.getRootDirectory(), configPath));
        }
        catch (FileNotFoundException e)
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
        return fsh.getRootDirectory().getName();
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
        return fsh.getSourcePath();
    }

    /**
     * <p>
     * getFileObject
     * </p>
     *
     * @see org.apache.jetspeed.deployment.DeploymentObject#getFileObject()
     * @return
     */
    public FileSystemHelper getFileObject()
    {
        return fsh;
    }
    
    protected boolean verifyExtension(File file)
    {
        String fileName = file.getName();
        int dot = fileName.lastIndexOf('.');
        if(dot != -1)
        {
            String ext = fileName.substring(dot);
            return ext.equals(".war") || ext.equals(".jar") || ext.equals(".zip");
        }
        else
        {
            return false;
        }
        
       
    }

}
