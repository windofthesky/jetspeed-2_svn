/**
 * Created on Jan 13, 2004
 *
 * 
 * @author
 */
package org.apache.jetspeed.deployment.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.deployment.DeploymentEvent;
import org.apache.jetspeed.deployment.DeploymentEventListener;
import org.apache.jetspeed.deployment.DeploymentException;
import org.apache.jetspeed.deployment.simpleregistry.Entry;
import org.apache.jetspeed.deployment.simpleregistry.SimpleRegistry;
import org.apache.jetspeed.deployment.simpleregistry.SimpleRegistryException;

/**
 * <p>
 * DirectFolderEventListener
 * </p>
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 *
 */
public class DeployDecoratorEventListener implements DeploymentEventListener
{
    protected SimpleRegistry registry;
    protected static final Log log = LogFactory.getLog("deployment");

    public DeployDecoratorEventListener(SimpleRegistry registry)
    {
        this.registry = registry;
    }

    /**
     * @see org.apache.jetspeed.deployment.DeploymentEventListener#invoke(org.apache.jetspeed.deployment.DeploymentEvent)
     */
    public void invoke(DeploymentEvent event) throws DeploymentException
    {
        // In most cases we are already looking at the target deployment directory
        // and all we need to do is register the folder name
        if (event.getEventType().equals(DeploymentEvent.EVENT_TYPE_DEPLOY))
        {
			PropertiesConfiguration conf;
            try
            {

                InputStream configStream = event.getHandler().getConfiguration("decorator.properties");
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
            catch (IOException e1)
            {
                // TODO Auto-generated catch block

                throw new DeploymentException("Error reading configuration from jar: " + e1.toString(), e1);
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
                    try
                    {

                        String mediaType = conf.getString("media.type", "html");
                        log.info("Decorator " + id + " supports media type \"" + mediaType + "\"");
                        String deployPath = event.getDeploymentRoot() + File.separator + mediaType + File.separator + id;
                        log.info("Deploying decorator " + id + " to " + deployPath);
                        JarInputStream jis = (JarInputStream) event.getHandler().getAsStream();
                        JarEntry jarEntry = jis.getNextJarEntry();
                        while (jarEntry != null)
                        {
                            String entryName = jarEntry.getName();
                            log.info("Deploying " + entryName + " for decorator " + id);
                            File fullPath = new File(deployPath + File.separator + entryName);
                            if (!fullPath.exists())
                            {
                                // First create parnets
                                fullPath.getParentFile().mkdirs();
                                fullPath.createNewFile();

                            }

                            FileOutputStream fos = new FileOutputStream(fullPath);
                            byte[] buf = new byte[1024];
                            int len;
                            while ((len = jis.read(buf)) > 0)
                            {
                                fos.write(buf, 0, len);
                            }

                            jarEntry = jis.getNextJarEntry();
                        }

                        registry.register(entry);
                        log.info("Registering decorator " + event.getDeploymentRoot() + "/" + id);
                    }
                    catch (Exception e)
                    {
                        log.error("Error deploying decorator " + id + ": " + e.toString(), e);

                    }
                }
                log.info("Decorator " + id + " deployed and registered successfuly.");

            }
            else
            {
                log.error("Unable to register directory, \"id\" attribute not defined in configuration");
            }

        }

    }

}
