/**
 * Created on Jan 14, 2004
 *
 * 
 * @author
 */
package org.apache.jetspeed.deployment.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.components.portletregistry.PortletRegistryComponent;
import org.apache.jetspeed.deployment.DeploymentEvent;
import org.apache.jetspeed.deployment.DeploymentEventListener;
import org.apache.jetspeed.deployment.DeploymentException;
import org.apache.jetspeed.deployment.DeploymentObject;
import org.apache.jetspeed.factory.PortletFactory;
import org.apache.jetspeed.om.common.portlet.MutablePortletApplication;
import org.apache.jetspeed.tools.pamanager.PortletApplicationManagement;
import org.apache.jetspeed.util.DirectoryHelper;
import org.apache.jetspeed.util.FileSystemHelper;
import org.apache.jetspeed.util.descriptor.PortletApplicationWar;
import org.apache.pluto.om.portlet.PortletApplicationDefinition;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

/**
 * <p>
 * DeployportletAppEventListener
 * </p>
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver </a>
 * @version $Id: DeployPortletAppEventListener.java,v 1.13 2004/06/23 18:41:26
 *          weaver Exp $
 *  
 */
public class DeployPortletAppEventListener implements DeploymentEventListener
{

    protected static final Log log = LogFactory.getLog("deployment");
    private String webAppDir;
    private PortletApplicationManagement pam;
    private Map appNameToFile;
    protected PortletRegistryComponent registry;
    private PortletFactory portletFactory;


    /**
     * 
     * @param webAppDir
     * @param pam
     * @param registry
     * @throws FileNotFoundException
     *             the <code>webAppDir</code> directory does not exist.
     */
    public DeployPortletAppEventListener( String webAppDir, PortletApplicationManagement pam,
            PortletRegistryComponent registry, PortletFactory portletFactory  ) throws FileNotFoundException
    {
        File checkFile = new File(webAppDir);
        this.portletFactory = portletFactory;

        if (checkFile.exists())
        {
            this.webAppDir = webAppDir;
        }
        else
        {
            throw new FileNotFoundException("The depoyment directory for portlet applications \""
                    + checkFile.getAbsolutePath() + "\" does not exist.");
        }
        this.pam = pam;
        this.appNameToFile = new HashMap();
        this.registry = registry;
    }

    /**
     * <p>
     * doUnDeploy
     * </p>
     * 
     * @param event
     * @throws DeploymentException
     */
    public void invokeUndeploy( DeploymentEvent event ) throws DeploymentException
    {
        String paName = null;
        try
        {

            boolean isLocal = event.getName().startsWith("jetspeed-");

            String filePath = event.getPath();
            paName = (String) appNameToFile.get(filePath);
            if (paName == null)
            {
                String msg = "Unable to locate application name for archive \"" + filePath + "\"";
                log.warn(msg);
                throw new DeploymentException(msg);
            }

            PortletApplicationWar deployedWar = null;

            PortletApplicationDefinition pa = registry.getPortletApplicationByIdentifier(paName);
            String webAppContextRoot = null;

            if (pa != null)
            {
                webAppContextRoot = pa.getWebApplicationDefinition().getContextRoot();
            }
            else
            {
                webAppContextRoot = "/" + paName;
            }

            if (isLocal)
            {
                log.info("Preparing to unregister portlet application \"" + paName + "\"");
                pam.unregister(paName);
            }
            else
            {
                log.info("Preparing to undeploy portlet application \"" + paName + "\"");
                DirectoryHelper dir = new DirectoryHelper(new File(webAppDir + "/" + paName));
                deployedWar = new PortletApplicationWar(dir, paName, webAppContextRoot );
                pam.undeploy(deployedWar);
            }

            log.info("Portlet application \"" + paName + "\"" + " was successfuly undeployed.");
        }
        catch (Exception e)
        {
            String msg = "Error undeploying portlet app " + paName + ": " + e.toString();
            if (e instanceof DeploymentException)
            {
                throw (DeploymentException) e;
            }
            else
            {
                throw new DeploymentException(msg, e);
            }

        }
    }

    /**
     * <p>
     * doDeploy
     * </p>
     * 
     * @param event
     * @throws DeploymentException
     * @throws PortletApplicationException
     * @throws IOException
     * @throws JDOMException
     */
    public void invokeDeploy( DeploymentEvent event ) throws DeploymentException
    {
        doDeploy(event);

    }

    /**
     * <p>
     * doDeploy
     * </p>
     * 
     * @param event
     * @throws DeploymentException
     */
    protected void doDeploy( DeploymentEvent event ) throws DeploymentException
    {
        InputStream portletXmlStream = null;
        try
        {
            DeploymentObject deploymentObj = event.getDeploymentObject();
            portletXmlStream = deploymentObj.getConfiguration("WEB-INF/portlet.xml");
            if (portletXmlStream == null)
            {
                return;
            }

            String fileName = deploymentObj.getName();
            boolean isLocal = fileName.startsWith("jetspeed-");

            log.info("Loading portlet application from web archive " + deploymentObj.getPath());
            SAXBuilder builder = new SAXBuilder();
            Document portletXml = builder.build(portletXmlStream);
            Element portletApp = portletXml.getRootElement();
            String id = portletApp.getAttributeValue("id");
            if (id == null)
            {

                String warFileName = fileName;
                int extensionIdx = warFileName.lastIndexOf(".war");
                id = warFileName.substring(0, extensionIdx);
                log.info("Application id not defined in portlet.xml so using war name " + id);
            }

            PortletApplicationWar paWar = new PortletApplicationWar(deploymentObj.getFileObject(), id, "/" + id );

            if (registry.getPortletApplicationByIdentifier(id) != null
                    && !event.getEventType().equals(DeploymentEvent.EVENT_TYPE_REDEPLOY))
            {
                log.info("Portlet application \"" + id + "\""
                        + " already been registered.  Skipping initial deployment.");
                // still need to register the filename to the app name so
                // undeploy works correctly
                appNameToFile.put(deploymentObj.getPath(), id);
                if (isLocal)
                {                
                    portletFactory.addClassLoader(
                        registry.getPortletApplicationByIdentifier(id).getId().toString(),
                        paWar.createClassloader(getClass().getClassLoader()));
                }
                else
                {
                    try
                    {
                        ClassLoader classloader = createPortletClassloader(getClass().getClassLoader(), id);
                        if (classloader != null)
                        {
                            portletFactory.addClassLoader(
                                registry.getPortletApplicationByIdentifier(id).getId().toString(),
                                classloader);
                        }
                    }
                    catch (IOException e1)
                    {
                        log.info("Could not add Portlet Class Loader: " + id);
                    }
                }
                return;
            }

            log.info("Preparing to (re) deploy portlet app \"" + id + "\"");

            if (event.getEventType().equals(DeploymentEvent.EVENT_TYPE_DEPLOY))
            {

                if (isLocal)
                {
                    log.info(fileName + " will be registered as a local portlet applicaiton.");                    
                    pam.register(paWar);
                    MutablePortletApplication mpa = registry.getPortletApplicationByIdentifier(id);
                    if (mpa != null)
                    {
                        portletFactory.addClassLoader(
                            mpa.getId().toString(),
                            paWar.createClassloader(getClass().getClassLoader()));
                    }
                }
                else
                {
                    log.info("Deploying portlet applicaion WAR " + fileName);
                    pam.deploy(paWar);
                    try
                    {
                        ClassLoader classloader = createPortletClassloader(getClass().getClassLoader(), id);
                        if (classloader != null)
                        {
                            MutablePortletApplication mpa = registry.getPortletApplicationByIdentifier(id);
                            if (mpa != null)
                            {
                                portletFactory.addClassLoader(mpa.getId().toString(), classloader);
                            }
                        }
                    }
                    catch (IOException e1)
                    {
                        log.info("Could not add Portlet Class Loader: " + id);
                    }
                }
            }
            else if (event.getEventType().equals(DeploymentEvent.EVENT_TYPE_REDEPLOY))
            {
                if (isLocal)
                {
                    //TODO: get this working
                    
                }
                else
                {
                    log.info("Re-deploying portlet applicaion WAR " + fileName);
                    pam.redeploy(paWar);
                }
                
            }

            appNameToFile.put(deploymentObj.getPath(), id);
            log.info("Portlet app \"" + id + "\" " + "successfuly (re)deployed.");
        }
        catch (Exception e)
        {
            String msg = "Error (re)deploying portlet app: " + e.toString();
            throw new DeploymentException(msg, e);
        }
        finally
        {
            if (portletXmlStream != null)
            {
                try
                {
                    portletXmlStream.close();
                }
                catch (IOException e1)
                {
                    // ignore
                }
            }
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
    public void invokeRedeploy( DeploymentEvent event ) throws DeploymentException
    {
        doDeploy(event);
    }
    
    /**
     * <p>
     * createPortletClassloader
     * </p>
     * 
     * @param parent
     * @param id
     * @return
     * @throws IOException
     */
    private ClassLoader createPortletClassloader(ClassLoader parent, String id) throws IOException
    {
        String portletAppDirectory = pam.getDeploymentPath(id);
        FileSystemHelper target = new DirectoryHelper(new File(portletAppDirectory));
        PortletApplicationWar targetWar = new PortletApplicationWar(target, id, "/" + id);
        return targetWar.createClassloader(parent);
    }
}