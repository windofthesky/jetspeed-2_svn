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

import org.apache.commons.configuration.Configuration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.fulcrum.InitializationException;
import org.apache.jetspeed.cps.BaseCommonService;

import org.apache.jetspeed.deployment.AutoDeploymentService;
import org.apache.jetspeed.deployment.DeploymentEventDispatcher;
import org.apache.jetspeed.deployment.fs.FileSystemScanner;
import org.apache.jetspeed.deployment.fs.JARObjectHandlerImpl;
import org.apache.jetspeed.tools.pamanager.CatalinaPAM;

/**
 * <p>
 * CatalinaAutoDeploymentServiceImpl
 * </p>
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 *
 */
public class CatalinaAutoDeploymentServiceImpl extends BaseCommonService implements AutoDeploymentService
{
    protected Log log = LogFactory.getLog("deployment");

    protected FileSystemScanner scanner;

    /**
     * @see org.apache.fulcrum.Service#init()
     */
    public void init() throws InitializationException
    {
        Configuration conf = getConfiguration();
        log.info("Starting auto deployment service: " + getClass().getName());
        long delay = conf.getLong("delay", 10000);
        log.info("Deployment scanning delay: " + delay);

        String server = conf.getString("server", "localhost");
        log.info("Deployment server: " + server);
        int port = conf.getInt("port", 8080);
        log.info("Deployment server port: " + port);
        String userName = conf.getString("user");
        log.info("Deployment server user name: " + userName);
        String password = conf.getString("password");
        String stagingDir = conf.getString("staging.dir", "WEB-INF/deploy");
        log.info("Deployment staging directory: " + stagingDir);
        String targetDir = conf.getString("target.dir", "../");
        log.info("Deployment target directory: " + targetDir);

        File stagingDirFile = new File(getRealPath(stagingDir));
        File targetDirFile = new File(getRealPath(targetDir));
        if (!targetDirFile.exists())
        {
            log.error(targetDirFile.getAbsolutePath() + " does not exist, auto deployment disabled.");
            setInit(false);
            shutdown();
            return;
        }

        if (!stagingDirFile.exists())
        {
            log.error(targetDirFile.getAbsolutePath() + " does not exist, auto deployment disabled.");
            setInit(false);
            shutdown();
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
            setInit(false);
            shutdown();
            return;
        }
        catch (IOException e1)
        {
            log.warn("IOException, auto deployment will be disabled: " + e1.toString());
            setInit(false);
            shutdown();
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
            CatalinaPAM catPAM = new CatalinaPAM(server, port, userName, password);
            DeployPortletAppEventListener dpal = new DeployPortletAppEventListener(targetDir, catPAM);
            DeploymentEventDispatcher dispatcher = new DeploymentEventDispatcher(targetDir);
            HashMap handlers = new HashMap();
            handlers.put("war", JARObjectHandlerImpl.class);
            scanner = new FileSystemScanner(stagingDirFile.getCanonicalPath(), handlers, dispatcher, delay);
            scanner.start();
            log.info("Deployment scanner successfuly started!");
        }
        catch (Exception e)
        {
            log.warn("Unable to intialize Catalina Portlet Application Manager.  Auto deployment will be disabled: " + e.toString(), e);
            setInit(false);
            shutdown();
            return;
        }

    }

    /**
     * @see org.apache.fulcrum.Service#shutdown()
     */
    public void shutdown()
    {
        if (scanner != null)
        {
            scanner.safeStop();
        }
        super.shutdown();
    }

}
