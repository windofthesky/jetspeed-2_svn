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
package org.apache.jetspeed.deployment.fs;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.deployment.DeploymentEvent;
import org.apache.jetspeed.deployment.DeploymentEventDispatcher;
import org.apache.jetspeed.deployment.DeploymentEventImpl;

/**
 * <p>
 * FileSystemWatcher
 * </p>
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 *
 */
public class FileSystemScanner extends Thread
{
    private String directoryToWatch;
    private File directoryToWatchFile;
    private FilenameFilter filter;
    private Map fileTypeHandlers;
    private long delay;
    private DeploymentEventDispatcher dispatcher;
    private Map fileDates;
    private boolean started = true;
    private List deployedFiles;

    private static final Log log = LogFactory.getLog("deployment");

    public FileSystemScanner(String directoryToWatch, Map fileTypeHandlers, DeploymentEventDispatcher dispatcher, long delay)
        throws FileNotFoundException, IOException
    {
        this.directoryToWatch = directoryToWatch;
        this.directoryToWatchFile = new File(directoryToWatch);
        if (!directoryToWatchFile.exists())
        {
            throw new FileNotFoundException(directoryToWatchFile.getCanonicalFile() + " does not exist.");
        }
        this.fileTypeHandlers = fileTypeHandlers;
        this.delay = delay;
        this.dispatcher = dispatcher;
        this.deployedFiles = new ArrayList();
        this.fileDates = new HashMap();

        Set fileExtensions = fileTypeHandlers.keySet();
        this.filter = new AllowedFileTypeFilter((String[]) fileExtensions.toArray(new String[1]));
        setPriority(MIN_PRIORITY);
    }

    /**
     * @see java.lang.Runnable#run()
     */
    public void run()
    {
        while (started)
        {
            String[] stagedFiles = getStagedFiles();
            undeployRemovedArtifacts(stagedFiles);
            redeployChangedArtifacts(stagedFiles);
            deployNewArtifacts(stagedFiles);

            try
            {
                sleep(delay);
            }
            catch (InterruptedException e)
            {

            }
        }
    }

    /**
     * notifies a switch variable that exits the watcher's montior loop started in the <code>run()</code>
     * method.      
     *
     */
    public void safeStop()
    {
        started = false;
    }

    private void deployNewArtifacts(String[] stagedFiles)
    {
        for (int i = 0; i < stagedFiles.length; i++)
        {
            // check for new deployment
            File aFile = new File(directoryToWatchFile, stagedFiles[i]);
            if (!isDeployed(stagedFiles[i]))
            {

                try
                {
                    FSObjectHandler objHandler = getFSObjectHandler(aFile);

                    DeploymentEvent event = new DeploymentEventImpl(DeploymentEvent.EVENT_TYPE_DEPLOY, objHandler);
                    dispatcher.dispatch(event);
                    // we are responsible for reclaiming the FSObject's resource
                    objHandler.close();
                    deployedFiles.add(stagedFiles[i]);
                    // record the lastModified so we can watch for re-deployment                    
                    long lastModified = aFile.lastModified();
                    fileDates.put(stagedFiles[i], new Long(lastModified));

                }
                catch (Exception e1)
                {
                    log.error("Error deploying " + aFile.getAbsolutePath(), e1);
                }
            }
        }
    }

    protected boolean isDeployed(String fileName)
    {
        return deployedFiles.contains(fileName);
    }

    private void redeployChangedArtifacts(String[] stagedFiles)
    {

    }

    private void undeployRemovedArtifacts(String[] stagedFiles)
    {
        List fileList = Arrays.asList(stagedFiles);
        
        
        
        for (int i=0; i<deployedFiles.size(); i++)
        {
            // get a current list of all the files in the deploy directory
			String fileName = (String) deployedFiles.get(i);
            File aFile = new File(directoryToWatchFile, fileName);
            
            // File is still on the file system, so skip it
            if(fileList.contains(fileName))
            {            	
            	continue;
            }

            try
            {
                FSObjectHandler objHandler = getFSObjectHandler(aFile);

                DeploymentEvent event = new DeploymentEventImpl(DeploymentEvent.EVENT_TYPE_UNDEPLOY, objHandler);                
                dispatcher.dispatch(event);
                // we are responsible for reclaiming the FSObject's resource
                objHandler.close();
                deployedFiles.remove(i);
                fileDates.remove(fileName);

            }
            catch (Exception e1)
            {
                log.error("Error undeploying " + aFile.getAbsolutePath(), e1);
            }

        }

    }

    protected String[] getStagedFiles()
    {
        return this.directoryToWatchFile.list(this.filter);
        // return this.directoryToWatchFile.listFiles();
    }

    protected FSObjectHandler getFSObjectHandler(File file) throws Exception
    {
        String name = file.getName();
        int extIndex = name.lastIndexOf('.');
        if (extIndex != -1 && (extIndex + 1) < name.length())
        {
            String extension = name.substring(extIndex + 1);
            Class fsoClass = (Class) fileTypeHandlers.get(extension);
            FSObjectHandler fso = (FSObjectHandler) fsoClass.newInstance();
            fso.setFile(file);
            return fso;
        }
        else
        {
            FSObjectHandler fso = new FileObjectHandler();
            fso.setFile(file);
            return fso;
        }
    }

    class AllowedFileTypeFilter implements FilenameFilter
    {
        private String[] fileTypes;

        public AllowedFileTypeFilter(String[] fileTypes)
        {
            this.fileTypes = fileTypes;
        }

        /**
         * @see java.io.FilenameFilter#accept(java.io.File, java.lang.String)
         */
        public boolean accept(File dir, String name)
        {
            if (fileTypes != null)
            {

                int extIndex = name.lastIndexOf('.');
                if (extIndex != -1 && (extIndex + 1) < name.length())
                {
                    String extension = name.substring(extIndex + 1);
                    for (int i = 0; i < fileTypes.length; i++)
                    {
                        if (fileTypes[i].equalsIgnoreCase(extension))
                        {
                            return true;
                        }
                    }
                }
            }
            else
            {
                // null fileTypes == directories only!
                return new File(dir + File.separator + name).isDirectory();

            }
            return false;
        }

    }

}
