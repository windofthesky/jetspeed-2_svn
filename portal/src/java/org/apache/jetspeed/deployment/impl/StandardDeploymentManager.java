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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.components.portletregistry.PortletRegistryComponent;
import org.apache.jetspeed.deployment.DeploymentEvent;
import org.apache.jetspeed.deployment.DeploymentEventListener;
import org.apache.jetspeed.deployment.DeploymentException;
import org.apache.jetspeed.deployment.DeploymentManager;
import org.apache.jetspeed.deployment.DeploymentObject;
import org.picocontainer.Startable;

/**
 * <p>
 * AutoDeploymentManager
 * </p>
 * Implementation of {@link org.apache.jetspeed.deployment.DeploymentManager}
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver </a>
 * @version $Id$
 *  
 */
public class StandardDeploymentManager implements Startable, DeploymentManager
{
    protected Log log = LogFactory.getLog("deployment");

    protected FileSystemScanner scanner;

    protected PortletRegistryComponent registry;

    protected Collection deploymentListeners;

    protected long scanningDelay;

    protected String stagingDirectory;

    protected File stagingDirectoryAsFile;

    protected Map fileDates;

    protected List deployedFiles;


    /**
     * 
     * @param stagingDirectory
     * @param scanningDelay
     * @param deploymentListeners
     */
    public StandardDeploymentManager( String stagingDirectory, long scanningDelay, Collection deploymentListeners )
    {
        this.scanningDelay = scanningDelay;
        this.stagingDirectory = stagingDirectory;
        this.stagingDirectoryAsFile = new File(stagingDirectory);
        this.deploymentListeners = deploymentListeners;
        this.deployedFiles = new ArrayList();
        this.fileDates = new HashMap();
    }

    /**
     * 
     * <p>
     * start
     * </p>
     * 
     * @see org.picocontainer.Startable#start()
     *  
     */
    public void start()
    {

        log.info("Starting auto deployment service: " + getClass().getName());

        log.info("Deployment scanning delay: " + scanningDelay);

        log.info("Deployment staging directory: " + stagingDirectory);

        if (!stagingDirectoryAsFile.exists())
        {
            log.error(stagingDirectoryAsFile.getAbsolutePath() + " does not exist, auto deployment disabled.");
            stop();
            return;
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
     * 
     * <p>
     * stop
     * </p>
     * 
     * @see org.picocontainer.Startable#stop()
     *  
     */
    public void stop()
    {
        if (scanner != null)
        {
            scanner.safeStop();
        }
    }

    public void fireDeploymentEvent()
    {
        String[] stagedFiles = stagingDirectoryAsFile.list();
        for (int i = 0; i < stagedFiles.length; i++)
        {
            // check for new deployment
            File aFile = new File(stagingDirectoryAsFile, stagedFiles[i]);
            if (!isDeployed(stagedFiles[i]))
            {
                DeploymentObject deploymentObject = null;
                try
                {
                    try
                    {
                        deploymentObject = new StandardDeploymentObject(aFile);
                    }
                    catch (FileNotDeployableException e)
                    {
                        // log.info(e.getMessage());                        
                        continue;
                    }

                    DeploymentEvent event = new DeploymentEventImpl(DeploymentEvent.EVENT_TYPE_DEPLOY, deploymentObject);
                    dispatch(event);
                    if (event.getStatus() == DeploymentEvent.STATUS_OKAY)
                    {
                        deployedFiles.add(stagedFiles[i]);
                        // record the lastModified so we can watch for
                        // re-deployment
                        long lastModified = aFile.lastModified();
                        fileDates.put(stagedFiles[i], new Long(lastModified));
                    }
                    else
                    {
                        log.error("Error deploying " + aFile.getAbsolutePath());
                    }

                }
                catch (Exception e1)
                {
                    log.error("Error deploying " + aFile.getAbsolutePath(), e1);
                }
                finally
                {
                    if (deploymentObject != null)
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
            }
        }
    }

    /**
     * 
     * <p>
     * fireUndeploymentEvent
     * </p>
     * 
     * @see org.apache.jetspeed.deployment.DeploymentManager#fireUndeploymentEvent()
     *  
     */
    public void fireUndeploymentEvent()
    {
        List fileList = Arrays.asList(StandardDeploymentManager.this.stagingDirectoryAsFile.list());

        for (int i = 0; i < deployedFiles.size(); i++)
        {
            // get a current list of all the files in the deploy directory
            String fileName = (String) deployedFiles.get(i);
            File aFile = new File(stagingDirectoryAsFile, fileName);

            // File is still on the file system, so skip it
            if (fileList.contains(fileName))
            {
                continue;
            }

       
            try
            {

                DeploymentEvent event = new DeploymentEventImpl(DeploymentEvent.EVENT_TYPE_UNDEPLOY, aFile.getName(),
                        aFile.getAbsolutePath());
                dispatch(event);

                if (event.getStatus() == DeploymentEvent.STATUS_OKAY)
                {
                    deployedFiles.remove(i);
                    fileDates.remove(fileName);
                }
                else
                {
                    log.error("Error undeploying " + aFile.getAbsolutePath());
                }

            }
            catch (Exception e1)
            {
                log.error("Error undeploying " + aFile.getAbsolutePath(), e1);
            }   

        }

    }

    /**
     * 
     * <p>
     * dispatch
     * </p>
     * 
     * @see org.apache.jetspeed.deployment.DeploymentManager#dispatch(org.apache.jetspeed.deployment.DeploymentEvent)
     * @param event
     */
    public void dispatch( DeploymentEvent event )
    {
        Iterator itr = deploymentListeners.iterator();
        while (itr.hasNext())
        {
            DeploymentEventListener listener = (DeploymentEventListener) itr.next();
            try
            {
                if (event.getEventType().equals(DeploymentEvent.EVENT_TYPE_DEPLOY))
                {
                    listener.invokeDeploy(event);
                }
                else if (event.getEventType().equals(DeploymentEvent.EVENT_TYPE_UNDEPLOY))
                {
                    listener.invokeUndeploy(event);
                }
                else if (event.getEventType().equals(DeploymentEvent.EVENT_TYPE_REDEPLOY))
                {
                    listener.invokeRedeploy(event);
                }

                if (event.getStatus() < 0)
                {
                    event.setStatus(DeploymentEvent.STATUS_OKAY);
                }
            }
            catch (DeploymentException e)
            {
                log.error(e.toString(), e);
                event.setStatus(DeploymentEvent.STATUS_FAILED);
            }
        }
    }

    /**
     * 
     * <p>
     * fireReDeploymentEvent
     * </p>
     * 
     * @see org.apache.jetspeed.deployment.DeploymentManager#fireRedeploymentEvent()
     *  
     */
    public void fireRedeploymentEvent()
    {
        List fileList = Arrays.asList(StandardDeploymentManager.this.stagingDirectoryAsFile.list());
        
        for (int i = 0; i < deployedFiles.size(); i++)
        {
            // get a current list of all the files in the deploy directory
            String fileName = (String) deployedFiles.get(i);
            File aFile = new File(stagingDirectoryAsFile, fileName);

            // File is not on the file system, so skip it
            Long longDateObj = ((Long) fileDates.get(fileName));
            if (longDateObj == null)
            {
                continue;
            }

            long lastModifiedDate = longDateObj.longValue();
            long currentModifiedDate = aFile.lastModified();

            if (currentModifiedDate > lastModifiedDate)
            {

                DeploymentObject deploymentObject = null;
                try
                {
                    deploymentObject = new StandardDeploymentObject(aFile);
                    DeploymentEvent event = new DeploymentEventImpl(DeploymentEvent.EVENT_TYPE_REDEPLOY, deploymentObject);
                    log.info("Re-deploying "+aFile.getAbsolutePath());
                    dispatch(event);

                    if (event.getStatus() == DeploymentEvent.STATUS_OKAY)
                    {                        
                        fileDates.put(fileName, new Long(currentModifiedDate));
                    }
                    else
                    {
                        log.error("Error redeploying " + aFile.getAbsolutePath());
                    }

                }
                catch (Exception e1)
                {
                    log.error("Error undeploying " + aFile.getAbsolutePath(), e1);
                }
                finally
                {
                    if (deploymentObject != null)
                    {
                        try
                        {
                            // we are responsible for reclaiming the FSObject's
                            // resource
                            deploymentObject.close();
                        }
                        catch (IOException e)
                        {

                        }
                    }

                }

            }
        }

    }

    /**
     * 
     * <p>
     * isDeployed
     * </p>
     * 
     * @param fileName
     * @return
     */
    protected boolean isDeployed( String fileName )
    {
        return deployedFiles.contains(fileName);
    }

    public class FileSystemScanner extends Thread
    {

        private boolean started = true;

        public FileSystemScanner( ThreadGroup threadGroup, String name ) throws FileNotFoundException, IOException
        {
            super(threadGroup, name);
            setPriority(MIN_PRIORITY);
        }

        /**
         * @see java.lang.Runnable#run()
         */
        public void run()
        {
            while (started)
            {
                fireUndeploymentEvent();
                fireRedeploymentEvent();
                fireDeploymentEvent();

                try
                {
                    sleep(scanningDelay);
                }
                catch (InterruptedException e)
                {

                }
            }
        }

        /**
         * notifies a switch variable that exits the watcher's montior loop
         * started in the <code>run()</code> method.
         *  
         */
        public void safeStop()
        {
            started = false;
        }

    }

}