/**
 * Created on Jan 15, 2004
 *
 * 
 * @author
 */
package org.apache.jetspeed.deployment.impl;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.components.portletregistry.PortletRegistryComponent;
import org.apache.jetspeed.deployment.DeploymentEventDispatcher;
import org.apache.jetspeed.deployment.fs.FileSystemScanner;
import org.apache.jetspeed.deployment.fs.JARObjectHandlerImpl;
import org.apache.jetspeed.tools.pamanager.CatalinaPAM;
import org.apache.jetspeed.tools.pamanager.PortletApplicationManagement;
import org.picocontainer.Startable;

/**
 * <p>
 * CatalinaAutoDeploymentServiceImpl
 * </p>
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 *
 */
public class CatalinaAutoDeploymentServiceImpl implements Startable
{
    protected Log log = LogFactory.getLog("deployment");

    protected FileSystemScanner scanner;

    protected Configuration conf;

    protected Locale defaultLocale;

    protected PortletRegistryComponent registry;

    protected PortletApplicationManagement pam;
    
    public CatalinaAutoDeploymentServiceImpl(Configuration conf, Locale defaultLocale, PortletRegistryComponent registry, PortletApplicationManagement pam)
    {
        this.conf = conf;
        this.defaultLocale = defaultLocale;
        this.registry = registry;
        this.pam = pam; 
    }

    /**
     * @see org.apache.fulcrum.Service#init()
     */
    public void start() 
    {
       
        log.info("Starting auto deployment service: " + getClass().getName());
        long delay = conf.getLong("autodeployment.delay", 10000);
        log.info("Deployment scanning delay: " + delay);

        String server = conf.getString("autodeployment.server", "localhost");
        log.info("Deployment server: " + server);
        int port = conf.getInt("autodeployment.port", 8080);
        log.info("Deployment server port: " + port);
        String userName = conf.getString("autodeployment.user");
        log.info("Deployment server user name: " + userName);
        String password = conf.getString("autodeployment.password");
        String stagingDir = conf.getString("autodeployment.staging.dir", "${applicationRoot}/WEB-INF/deploy");
        log.info("Deployment staging directory: " + stagingDir);
        String targetDir = conf.getString("autodeployment.target.dir", "${applicationRoot}/../");
        log.info("Deployment target directory: " + targetDir);

        File stagingDirFile = new File(stagingDir);
        File targetDirFile = new File(targetDir);
        if (!targetDirFile.exists())
        {
            log.error(targetDirFile.getAbsolutePath() + " does not exist, auto deployment disabled.");
            
            stop();
            return;
        }

        if (!stagingDirFile.exists())
        {
            log.error(targetDirFile.getAbsolutePath() + " does not exist, auto deployment disabled.");
            
            stop();
            return;
        }

		Socket checkSocket = null;
        try
        {
            checkSocket = new Socket(server, port);
        }
        catch (UnknownHostException e1)
        {
        	log.warn("Unknown server, auto deployment will be disabled: " + e1.toString());            
            stop();
            return;
        }
        catch (IOException e1)
        {
            log.warn("IOException, auto deployment will be disabled: " + e1.toString());
            stop();
            return;
        }
        finally
        {
			try
            {
            	// close the server check
            	if(checkSocket != null)
            	{
					checkSocket.close();
            	}                
            }
            catch (IOException e2)
            {
                // do nothing
            }
        }

        try
        {
           

            Map map = new HashMap();
            map.put(CatalinaPAM.PAM_PROPERTY_SERVER, server);
            map.put(CatalinaPAM.PAM_PROPERTY_PORT, new Integer(port));
            map.put(CatalinaPAM.PAM_PROPERTY_USER, userName);
            map.put(CatalinaPAM.PAM_PROPERTY_PASSWORD, password);            
            
            pam.connect(map);
            
            DeployPortletAppEventListener dpal = new DeployPortletAppEventListener(targetDirFile.getCanonicalPath(), pam, registry, defaultLocale);
            DeploymentEventDispatcher dispatcher = new DeploymentEventDispatcher(targetDirFile.getCanonicalPath());
            dispatcher.addDeploymentListener(dpal);
            HashMap handlers = new HashMap();
            handlers.put("war", JARObjectHandlerImpl.class);
            scanner = new FileSystemScanner(stagingDirFile.getCanonicalPath(), handlers, dispatcher, delay);
            scanner.setName("Autodeployment File Scanner Thread");
            scanner.setContextClassLoader(Thread.currentThread().getContextClassLoader());
            scanner.start();
            log.info("Deployment scanner successfuly started!");
        }
        catch (Exception e)
        {
            log.warn("Unable to intialize Catalina Portlet Application Manager.  Auto deployment will be disabled: " + e.toString(), e);
            
            stop();
            return;
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

   
}
