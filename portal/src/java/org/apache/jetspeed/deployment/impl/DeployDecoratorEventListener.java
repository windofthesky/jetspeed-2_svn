/**
 * Created on Jan 13, 2004
 *
 * 
 * @author
 */
package org.apache.jetspeed.deployment.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.deployment.DeploymentEvent;
import org.apache.jetspeed.deployment.DeploymentEventListener;
import org.apache.jetspeed.deployment.DeploymentException;
import org.apache.jetspeed.deployment.simpleregistry.Entry;
import org.apache.jetspeed.deployment.simpleregistry.SimpleRegistry;
import org.apache.jetspeed.util.DirectoryHelper;
import org.apache.jetspeed.util.FileSystemHelper;

/**
 * <p>
 * DirectFolderEventListener
 * </p>
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver </a>
 * @version $Id: DeployDecoratorEventListener.java,v 1.3 2004/03/25 21:39:22
 *          jford Exp $
 *  
 */
public class DeployDecoratorEventListener implements DeploymentEventListener
{
    protected SimpleRegistry registry;
    protected static final Log log = LogFactory.getLog("deployment");
    protected String deployToDir;

    public DeployDecoratorEventListener( SimpleRegistry registry, String deployToDir ) throws IOException
    {
        this.registry = registry;

        File checkFile = new File(deployToDir);
        if (checkFile.exists())
        {
            this.deployToDir = deployToDir;
        }
        else
        {
            throw new FileNotFoundException("The deployment directory, " + checkFile.getAbsolutePath()
                    + ", does not exist");
        }
    }

    /**
     * @see org.apache.jetspeed.deployment.DeploymentEventListener#invoke(org.apache.jetspeed.deployment.DeploymentEvent)
     */
    public void invokeDeploy( DeploymentEvent event ) throws DeploymentException
    {
        InputStream configStream = null;

        PropertiesConfiguration conf;
        try
        {

            configStream = event.getDeploymentObject().getConfiguration("decorator.properties");
            if (configStream == null)
            {
                return;
            }
            else
            {
                conf = new PropertiesConfiguration();
                conf.load(configStream);
            }
        }
        catch (Exception e1)
        {
            // TODO Auto-generated catch block

            throw new DeploymentException("Error reading configuration from jar: " + e1.toString(), e1);
        }
        finally
        {
            if (configStream != null)
            {
                try
                {
                    configStream.close();
                }
                catch (IOException e)
                {

                }
            }
        }

        String id = conf.getString("id");
        if (id != null)
        {
            log.info("Found decorator deployment archive " + id);
            Entry entry = new Entry();
            entry.setId(id);
            if (!registry.isRegistered(entry))
            {
                log.info("Deploying decorator " + id);
                FileSystemHelper sourceObject = null;
                FileSystemHelper deployObject = null;
                try
                {

                    String mediaType = conf.getString("media.type", "html");
                    log.info("Decorator " + id + " supports media type \"" + mediaType + "\"");
                    String deployPath = deployToDir + File.separator + mediaType + File.separator + id;
                    log.info("Deploying decorator " + id + " to " + deployPath);
                    sourceObject = event.getDeploymentObject().getFileObject();

                    File deployPathFile = new File(deployPath);
                    deployPathFile.mkdirs();
                    deployObject = new DirectoryHelper(deployPathFile);
                    deployObject.copyFrom(sourceObject.getRootDirectory());

                    registry.register(entry);
                    log.info("Registering decorator " + deployToDir + "/" + id);
                }
                catch (Exception e)
                {
                    log.error("Error deploying decorator " + id + ": " + e.toString(), e);

                }
                finally
                {
                    try
                    {
                        if (sourceObject != null)
                        {
                            sourceObject.close();
                        }

                        if (deployObject != null)
                        {
                            deployObject.close();
                        }
                    }
                    catch (IOException e2)
                    {

                    }
                }
            }
            log.info("Decorator " + id + " deployed and registered successfuly.");

        }
        else
        {
            log.error("Unable to register directory, \"id\" attribute not defined in configuration");
        }

    }

    /**
     * <p>
     * invokeUndeploy
     * </p>
     * 
     * @see org.apache.jetspeed.deployment.DeploymentEventListener#invokeUndeploy(org.apache.jetspeed.deployment.DeploymentEvent)
     * @param event
     * @throws DeploymentException
     */
    public void invokeUndeploy( DeploymentEvent event ) throws DeploymentException
    {
        // TODO Auto-generated method stub

    }

    /**
     * <p>
     * invokeRedeploy
     * </p>
     * 
     * @see org.apache.jetspeed.deployment.DeploymentEventListener#invokeRedeploy(org.apache.jetspeed.deployment.DeploymentEvent)
     * @param event
     * @throws DeploymentException
     */
    public void invokeRedeploy( DeploymentEvent event ) throws DeploymentException
    {
        // TODO Auto-generated method stub

    }
}