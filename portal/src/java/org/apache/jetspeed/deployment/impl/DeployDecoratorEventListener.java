/**
 * Created on Jan 13, 2004
 *
 * 
 * @author
 */
package org.apache.jetspeed.deployment.impl;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
    protected static final Log log = LogFactory.getLog("deployment");

    protected static final String DEPLOYMENT_OBJECT_PATH_ATTR = "DEPLOYMENT_OBJECT_PATH";
    protected static final String DEPLOYMENT_CONFIGURATION_ATTR = "DEPLOYMENT_CONFIGURATION";

    protected SimpleRegistry registry;
    protected String deployToDir;

    public DeployDecoratorEventListener(SimpleRegistry registry, String deployToDir) throws IOException
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
     * <p>
     * invokeDeploy
     * </p>
     * 
     * @see org.apache.jetspeed.deployment.DeploymentEventListener#invokeDeploy(org.apache.jetspeed.deployment.DeploymentEvent)
     * @param event
     * @throws DeploymentException
     */
    public void invokeDeploy(DeploymentEvent event) throws DeploymentException
    {
        // get decorator configuration if available
        PropertiesConfiguration conf = getDecoratorConfiguration(event);
        // silently return if configuration not available, (assumes
        // probably not a decorator)
        if (conf == null)
        {
            return;
        }

        // process decorator by id
        String id = conf.getString("id");
        if (id != null)
        {
            log.info("Found decorator deployment archive " + id);
            Entry entry = new Entry();
            entry.setId(id);
            entry.setAttribute(DEPLOYMENT_OBJECT_PATH_ATTR, event.getDeploymentObject().getPath());
            entry.setAttribute(DEPLOYMENT_CONFIGURATION_ATTR, conf);

            FileSystemHelper sourceObject = null;
            FileSystemHelper deployObject = null;
            try
            {
                // construct decorator deploy path
                String baseDeployPath = getBaseDeployPath(conf);
                String deployPath = baseDeployPath + File.separator + id;
                File deployPathFile = new File(deployPath);
                
                // undeploy decorator if it already exists and is a redeploy or
                // skip deployment if initial deployment
                if (deployPathFile.exists())
                {
                    if (event.getEventType().equals(DeploymentEvent.EVENT_TYPE_REDEPLOY))
                    {
                        invokeUndeploy(event);
                    }
                    else if (event.getEventType().equals(DeploymentEvent.EVENT_TYPE_DEPLOY))
                    {
                        log.info("Skipping initial deployment of decorator " + id + " to " + deployPath);
                        
                        // register deployed decorator
                        registry.register(entry);
                        log.info("Registering decorator " + id);
                        return;
                    }
                }
                
                // redeploy/deploy decorator w/o META_INF jar metadata
                log.info("Deploying decorator " + id + " to " + deployPath);
                deployPathFile.mkdirs();
                deployObject = new DirectoryHelper(deployPathFile);
                sourceObject = event.getDeploymentObject().getFileObject();
                deployObject.copyFrom(sourceObject.getRootDirectory());
                File metaInf = new File(deployPathFile, "META-INF");
                if (metaInf.exists())
                {
                    DirectoryHelper cleanup = new DirectoryHelper(metaInf);
                    cleanup.remove();
                    cleanup.close();
                }
                
                // detect language/country localized decorator components
                final List localeSpecificDeployPathsList = getLocaleSpecificDeployPaths(deployPathFile);
                
                // deploy individual locale specific decorator components
                Iterator deployPathsIter = localeSpecificDeployPathsList.iterator();
                while (deployPathsIter.hasNext())
                {
                    File localeDeployPathFile = (File) deployPathsIter.next();
                    
                    // deploy to locale specific location
                    File deployToPathFile = new File(baseDeployPath + localeDeployPathFile.getPath().substring(deployPath.length()) + File.separator + id);
                    log.info("Deploying decorator " + id + " to " + deployToPathFile.getPath());
                    deployToPathFile.mkdirs();
                    
                    // deploy decorator components by moving from deployed decorator
                    File [] filesToDeploy = localeDeployPathFile.listFiles(new FileFilter()
                        {
                            public boolean accept(File pathname)
                            {
                                return !localeSpecificDeployPathsList.contains(pathname);
                            }
                        });
                    for (int i = 0; (i < filesToDeploy.length); i++)
                    {
                        filesToDeploy[i].renameTo(new File(deployToPathFile, filesToDeploy[i].getName()));
                    }
                }
                
                // cleanup locale specific deployment directories
                Iterator cleanupDeployPathsIter = localeSpecificDeployPathsList.iterator();
                while (cleanupDeployPathsIter.hasNext())
                {
                    File cleanupLocaleDeployPathFile = (File) cleanupDeployPathsIter.next();
                    if (cleanupLocaleDeployPathFile.exists())
                    {
                        DirectoryHelper cleanup = new DirectoryHelper(cleanupLocaleDeployPathFile);
                        cleanup.remove();
                        cleanup.close();
                    }
                }
                
                // register
                registry.register(entry);
                log.info("Registering decorator " + id);
                
                log.info("Decorator " + id + " deployed and registered successfuly.");
            }
            catch (Exception e)
            {
                log.error("Error deploying or registering decorator " + id + ": " + e.toString(), e);
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
    public void invokeUndeploy(DeploymentEvent event) throws DeploymentException
    {
        // get deployment configuration from decorator configuration
        // if available or lookup based on registered attributes
        PropertiesConfiguration conf = getDecoratorConfiguration(event);
        if ((conf == null) && (event.getPath() != null))
        {
            Iterator registrationsIter = registry.getRegistry().iterator();
            while ((conf == null) && registrationsIter.hasNext())
            {
                Entry entry = (Entry)registrationsIter.next();
                String deploymentObjectPath = (String) entry.getAttribute(DEPLOYMENT_OBJECT_PATH_ATTR);
                if (event.getPath().equals(deploymentObjectPath))
                {
                    conf = (PropertiesConfiguration) entry.getAttribute(DEPLOYMENT_CONFIGURATION_ATTR);
                }
            }
        }
        // silently return if configuration not available, (assumes
        // probably not a decorator)
        if (conf == null)
        {
            return;
        }

        // process decorator by id
        String id = conf.getString("id");
        if (id != null)
        {
            log.info("Found decorator deployment configuration " + id);

            try
            {
                // find and construct decorator deploy path
                String baseDeployPath = getBaseDeployPath(conf);
                String deployPath = baseDeployPath + File.separator + id;
                
                // undeploy decorator
                File deployPathFile = new File(deployPath);
                if (deployPathFile.exists())
                {
                    log.info("Undeploying decorator " + id + " at " + deployPath);
                    DirectoryHelper cleanup = new DirectoryHelper(deployPathFile);
                    cleanup.remove();
                    cleanup.close();
                }
                
                // detect language/country localized decorator components
                final List localeSpecificDeployPathsList = getLocaleSpecificDeployPaths(new File(baseDeployPath));
                
                // undeploy individual locale specific decorator components
                Iterator deployPathsIter = localeSpecificDeployPathsList.iterator();
                while (deployPathsIter.hasNext())
                {
                    File localeDeployPathFile = new File((File) deployPathsIter.next(), id);
                    if (localeDeployPathFile.exists())
                    {
                        log.info("Undeploying decorator " + id + " at " + localeDeployPathFile.getPath());
                        DirectoryHelper cleanup = new DirectoryHelper(localeDeployPathFile);
                        cleanup.remove();
                        cleanup.close();
                        localeDeployPathFile.getParentFile().delete();
                    }
                }
                
                // deregister
                Entry entry = new Entry();
                entry.setId(id);
                registry.deRegister(entry);
                log.info("Deregistering decorator " + id);

                log.info("Decorator " + id + " undeployed and deregistered successfuly.");
            }
            catch (Exception e)
            {
                log.error("Error undeploying or deregistering decorator " + id + ": " + e.toString(), e);
            }
        }
        else
        {
            log.error("Unable to deregister directory, \"id\" attribute not defined in configuration or configuration not available");
        }
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
    public void invokeRedeploy(DeploymentEvent event) throws DeploymentException
    {
        invokeDeploy(event);
    }

    /**
     * <p>
     * getDecorationConfiguration
     * </p>
     * 
     * @param event
     @ @return configuration 
     * @throws DeploymentException
     */
    private PropertiesConfiguration getDecoratorConfiguration(DeploymentEvent event) throws DeploymentException
    {
        InputStream stream = null;
        try
        {
            if (event.getDeploymentObject() == null)
            {
                return null;
            }
            stream = event.getDeploymentObject().getConfiguration("decorator.properties");
            if (stream == null)
            {
                return null;
            }
            else
            {
                PropertiesConfiguration configuration = new PropertiesConfiguration();
                configuration.load(stream);
                return configuration;
            }
        }
        catch (Exception e1)
        {
            throw new DeploymentException("Error reading configuration from jar: " + e1.toString(), e1);
        }
        finally
        {
            if (stream != null)
            {
                try
                {
                    stream.close();
                }
                catch (IOException e)
                {

                }
            }
        }
    }

    /**
     * <p>
     * getBaseDeployPath
     * </p>
     * 
     * @param configuration
     * @return base deploy path
     */
    private String getBaseDeployPath(PropertiesConfiguration configuration)
    {
        // construct decorator deploy base path
        String decorates = configuration.getString("decorates", "generic");
        String layoutType = decorates;
        if (layoutType.equalsIgnoreCase("any"))
        {
            layoutType = "generic";
        }
        String mediaType = configuration.getString("media.type", "html");
        return deployToDir + File.separator + layoutType + File.separator + mediaType;
    }

    /**
     * <p>
     * getLocaleSpecificDeployPaths
     * </p>
     * 
     * @param rootPath
     * @return locale paths list
     */
    private List getLocaleSpecificDeployPaths(File rootPath)
    {
        // detect language/country localized deploy paths
        List localeSpecificDeployPathsList = new ArrayList();
        File [] localeLanguageSpecificRoots = rootPath.listFiles(new FileFilter()
            {
                public boolean accept(File pathname)
                {
                    // filter language code dirs, (assume length test is accurate enough)
                    return (pathname.isDirectory() && (pathname.getName().length() == 2));
                }
            });
        for (int i = 0; (i < localeLanguageSpecificRoots.length); i++)
        {
            localeSpecificDeployPathsList.add(localeLanguageSpecificRoots[i]);
            File [] localeCountrySpecificPaths = localeLanguageSpecificRoots[i].listFiles(new FileFilter()
                {
                    public boolean accept(File pathname)
                    {
                        // filter country code dirs, (assume length test is accurate enough)
                        return (pathname.isDirectory() && (pathname.getName().length() == 2));
                    }
                });
            for (int j = 0; (j < localeCountrySpecificPaths.length); j++)
            {
                localeSpecificDeployPathsList.add(localeCountrySpecificPaths[j]);
            }
        }
        return localeSpecificDeployPathsList;
    }
}
