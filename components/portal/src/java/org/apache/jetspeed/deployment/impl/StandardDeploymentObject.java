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
package org.apache.jetspeed.deployment.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.jetspeed.deployment.DeploymentObject;

/**
 * <p>
 * DeploymentObject
 * </p>
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver </a>
 * @version $Id$
 */
public class StandardDeploymentObject implements DeploymentObject
{
    protected File    deploymentObject;
    protected ZipFile zipFile;

    /**
     * @throws IOException
     */
    public StandardDeploymentObject(File deploymentObject) throws FileNotDeployableException
    {
        if (verifyExtension(deploymentObject))
        {
            this.deploymentObject = deploymentObject;
        }
        else
        {
            throw new FileNotDeployableException("File type for " + deploymentObject.getName()
                                                 + " is not supported by StandardDeploymentObject.");
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
        if (zipFile != null)
        {
            zipFile.close();
            zipFile = null;
        }
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
    public InputStream getConfiguration(String configPath) throws IOException
    {
        ZipFile zipFile = getZipFile();
        ZipEntry entry = zipFile.getEntry(configPath);
        if (entry != null)
        {
            return zipFile.getInputStream(entry);
        }
        return null;
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
        return deploymentObject.getName();
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
        return deploymentObject.getAbsolutePath();
    }

    public ZipFile getZipFile() throws IOException
    {
        if (zipFile == null)
        {
            zipFile = new ZipFile(deploymentObject);
        }
        return zipFile;
    }

    public File getFile()
    {
        return deploymentObject;
    }

    protected boolean verifyExtension(File file)
    {
        String fileName = file.getName();
        int dot = fileName.lastIndexOf('.');
        if (dot != -1)
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