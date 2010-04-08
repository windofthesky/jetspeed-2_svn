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
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.jetspeed.components.portletregistry.PortletRegistry;
import org.apache.jetspeed.deployment.DeploymentEvent;
import org.apache.jetspeed.deployment.DeploymentEventListener;
import org.apache.jetspeed.deployment.DeploymentException;
import org.apache.jetspeed.deployment.DeploymentManager;
import org.apache.jetspeed.deployment.DeploymentObject;
import org.apache.jetspeed.deployment.DeploymentStatus;

/**
 * <p>
 * StandardDeploymentManager
 * </p>
 * Implementation of {@link org.apache.jetspeed.deployment.DeploymentManager}
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver </a>
 * @version $Id$
 */
public class StandardDeploymentManager implements DeploymentManager
{
    private static final FileFilter readmeIgnoringFileFilter = new FileFilter()
    {
        public boolean accept(File file)
        {
            return !file.getName().equalsIgnoreCase("README.txt");
        }
    };
    
    protected Logger               log = LoggerFactory.getLogger("deployment");
    protected FileSystemScanner scanner;
    protected PortletRegistry   registry;
    protected Collection        deploymentListeners;
    protected long              scanningDelay;
    protected String            stagingDirectories;
    protected File[]            stagingDirectoriesAsFiles;
    protected HashMap           ignoredFiles;

    /**
     * @param stagingDirectories
     * @param scanningDelay
     * @param deploymentListeners
     */
    public StandardDeploymentManager(String stagingDirectories, long scanningDelay, Collection deploymentListeners)
    {
        this.scanningDelay = scanningDelay;
        this.stagingDirectories = stagingDirectories;
        StringTokenizer dirTokenizer = new StringTokenizer(stagingDirectories, ",");
        this.stagingDirectoriesAsFiles = new File[dirTokenizer.countTokens()];
        int i = 0;
        while (dirTokenizer.hasMoreTokens())
        {
            this.stagingDirectoriesAsFiles[i] = new File(dirTokenizer.nextToken());
            i++;
        }

        this.deploymentListeners = deploymentListeners;
        this.ignoredFiles = new HashMap();
    }

    /**
     * <p>
     * start
     * </p>
     * 
     * @see org.picocontainer.Startable#start()
     */
    public void start()
    {

        log.info("Starting auto deployment service: " + getClass().getName());

        log.info("Deployment scanning delay: " + scanningDelay);

        log.info("Deployment staging directory: " + stagingDirectories);

        for (int i = 0; i < stagingDirectoriesAsFiles.length; i++)
        {
            if (!stagingDirectoriesAsFiles[i].exists())
            {
                log
                                .error(stagingDirectoriesAsFiles[i].getAbsolutePath()
                                       + " does not exist, auto deployment disabled.");
                stop();
                return;
            }
        }

        // initialize listeners (where needed)
        Iterator itr = deploymentListeners.iterator();
        while (itr.hasNext())
        {
            ((DeploymentEventListener) itr.next()).initialize();
        }

        if (scanningDelay > -1)
        {
            try
            {
                scanner = new FileSystemScanner(Thread.currentThread().getThreadGroup(),
                                                "Autodeployment File Scanner Thread");

                scanner.setDaemon(true);
                // scanner.setContextClassLoader(Thread.currentThread().getContextClassLoader());
                scanner.setContextClassLoader(getClass().getClassLoader());
                scanner.start();
                log.info("Deployment scanner successfuly started!");
            }
            catch (Exception e)
            {
                log.warn(
                         "Unable to intialize Catalina Portlet Application Manager.  Auto deployment will be disabled: "
                                                                                                    + e.toString(), e);

                stop();
                return;
            }
        }
        else
        {
            log.info("Scanning delay set to " + scanningDelay
                     + " has disabled automatic scanning of staging directory.");
        }

    }

    /**
     * <p>
     * stop
     * </p>
     * 
     * @see org.picocontainer.Startable#stop()
     */
    public void stop()
    {
        if (scanner != null)
        {
            scanner.safeStop();
        }
    }
    
    public synchronized DeploymentStatus deploy(File aFile) throws DeploymentException
    {
        DeploymentObject deploymentObject = new StandardDeploymentObject(aFile);
        DeploymentEvent event = null;
        try
        {
            event = new DeploymentEventImpl(deploymentObject);
            dispatch(event);
        }
        finally
        {
            if ( deploymentObject != null )
            {
                try
                {
                    deploymentObject.close();
                }
                catch (IOException e)
                {                    
                }
            }
        }
        return event;
    }

    public void fireDeploymentEvent()
    {
        File[] stagedFiles = getAllStagedFiles();
        for (int i = 0; i < stagedFiles.length; i++)
        {
            // check for new deployment
            File aFile = stagedFiles[i];
            if (aFile.isFile() && !ignoreFile(aFile))
            {
                DeploymentStatus status = null;
                Exception de = null;
                try
                {
                    status = deploy(aFile);
                }
                catch (Exception e)
                {                    
                    de = e;
                }
                
                if ( status != null && status.getStatus() == DeploymentStatus.STATUS_OKAY )
                {
                    if (aFile.exists())
                    {
                        log.info("File: " + aFile.getAbsolutePath() + " deployed");
                        boolean result = aFile.delete();
                        if (!result)
                        {
                           	log.error("Failed to remove: " + aFile);
                        }
                    }
                }
                else
                {
                    if (status == null || status.getStatus() == DeploymentStatus.STATUS_EVAL)
                    {
                        log.warn("Unrecognized file " + aFile.getAbsolutePath());
                    }
                    else if ( de != null )
                    {
                        log.error("Failure deploying " + aFile.getAbsolutePath(), de);
                    }
                    else
                    {
                        log.error("Failure deploying " + aFile.getAbsolutePath());
                    }
                    ignoredFiles.put(aFile.getAbsolutePath(), new Long(aFile.lastModified()));
                }
            }
        }
    }

    /**
     * <p>
     * dispatch
     * </p>
     * 
     * @see org.apache.jetspeed.deployment.DeploymentManager#dispatch(org.apache.jetspeed.deployment.DeploymentEvent)
     * @param event
     */
    public void dispatch(DeploymentEvent event)
    {
        try
        {
            Iterator itr = deploymentListeners.iterator();
            while (itr.hasNext())
            {
                DeploymentEventListener listener = (DeploymentEventListener) itr.next();
                listener.invokeDeploy(event);
                if (event.getStatus() != DeploymentStatus.STATUS_EVAL)
                {
                    break;
                }
            }
        }
        catch (DeploymentException e)
        {
            log.error(e.getMessage(), e);
            event.setStatus(DeploymentStatus.STATUS_FAILED);
        }
    }

    /**
     * <p>
     * ignoreFile
     * </p>
     * 
     * @param fileName
     * @return
     */
    protected boolean ignoreFile(File aFile)
    {
        Long previousModified = (Long) ignoredFiles.get(aFile.getAbsolutePath());
        if (previousModified != null)
        {
            if (previousModified.longValue() != aFile.lastModified())
            {
                ignoredFiles.remove(aFile.getAbsolutePath());
            }
            else
            {
                return true;
            }
        }
        return false;
    }

    /**
     * <p>
     * getAllStagedFiles
     * </p>
     * 
     * @return
     */
    protected File[] getAllStagedFiles()
    {
        ArrayList fileList = new ArrayList();
        for (int i = 0; i < stagingDirectoriesAsFiles.length; i++)
        {
            fileList.addAll(Arrays.asList(stagingDirectoriesAsFiles[i].listFiles(readmeIgnoringFileFilter)));
        }

        return (File[]) fileList.toArray(new File[fileList.size()]);
    }

    public class FileSystemScanner extends Thread
    {

        private boolean started = true;

        public FileSystemScanner(ThreadGroup threadGroup, String name) throws FileNotFoundException, IOException
        {
            super(threadGroup, name);
            setPriority(MIN_PRIORITY);
        }

        /**
         * @see java.lang.Runnable#run()
         */
        public synchronized void run()
        {
            // use a double scanningDelay at startup to give the App Server some time to wake up...
            // see: http://issues.apache.org/jira/browse/JS2-261
            try
            {
                //
                // this is just too abusive for server class machines
                // and modern CPU/RAM configurations... if one REALLY
                // needs to slow the startup sequence, edit this setting
                // in WEB-INF/conf/jetspeed.properties:
                //
                // autodeployment.delay=10000
                //
                //wait(scanningDelay*2);
                wait(scanningDelay);
            }
            catch (InterruptedException e)
            {
            }
            while (started)
            {
                fireDeploymentEvent();
                try
                {
                    wait(scanningDelay);
                }
                catch (InterruptedException e)
                {

                }
            }
        }

        /**
         * notifies a switch variable that exits the watcher's monitor loop started in the <code>run()</code> method.
         */
        public void safeStop()
        {
            // stop this monitor thread
            synchronized (this)
            {
                started = false;
                notifyAll();
            }
            // wait for monitor thread stop
            try
            {
                join(scanningDelay);
            }
            catch (InterruptedException ie)
            {
            }
        }

    }

}
