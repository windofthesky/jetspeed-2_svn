/**
 * Created on Jan 14, 2004
 *
 * 
 * @author
 */
package org.apache.jetspeed.deployment.impl;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.Jetspeed;
import org.apache.jetspeed.components.portletregsitry.PortletRegistryComponent;
import org.apache.jetspeed.deployment.DeploymentEvent;
import org.apache.jetspeed.deployment.DeploymentEventListener;
import org.apache.jetspeed.deployment.DeploymentException;
import org.apache.jetspeed.deployment.fs.FSObjectHandler;
import org.apache.jetspeed.tools.pamanager.Deployment;
import org.apache.jetspeed.tools.pamanager.PortletApplicationException;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

/**
 * <p>
 * DeployportletAppEventListener
 * </p>
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 *
 */
public class DeployPortletAppEventListener implements DeploymentEventListener
{

    protected static final Log log = LogFactory.getLog("deployment");
    private String webAppDir;
    private Deployment pam;
    private Map appNameToFile;


    public DeployPortletAppEventListener(String webAppDir, Deployment pam)
    {
        this.webAppDir = webAppDir;
        this.pam = pam;
		this.appNameToFile = new HashMap();
    }

    /**
     * @see org.apache.jetspeed.deployment.DeploymentEventListener#invoke(org.apache.jetspeed.deployment.DeploymentEvent)
     */
    public void invoke(DeploymentEvent event) throws DeploymentException
    {
        if (event.getEventType().equals(DeploymentEvent.EVENT_TYPE_DEPLOY))
        {
            try
            {

                FSObjectHandler handler = (FSObjectHandler) event.getHandler();
                InputStream portletXmlStream = handler.getConfiguration("WEB-INF/portlet.xml");
                if (portletXmlStream == null)
                {
                    return;
                }
                else
                {
                    log.info("Loading portlet application from web archive " + handler.getPath());
                    SAXBuilder builder = new SAXBuilder();
                    Document portletXml = builder.build(portletXmlStream);
                    Element portletApp = portletXml.getRootElement();
                    String id = portletApp.getAttributeValue("id");
                    if (id == null)
                    {
                        throw new PortletApplicationException("<portlet-app> requires a unique \"id\" attribute.");
                    }
					PortletRegistryComponent regsitry = (PortletRegistryComponent) Jetspeed.getComponentManager().getComponent(PortletRegistryComponent.class);
                    if (regsitry.getPortletApplicationByIndetifier(id) != null)
                    {
                        log.info("Portlet application \"" + id + "\"" + " already been registered.  Skipping initial deployment.");
                        // still need to register the filename to the app name so undeploy works correctly
						appNameToFile.put(handler.getFile().getName(), id);
                        return;
                    }

                    log.info("Preparing to deploy portlet app \"" + id + "\"");
               
                    pam.deploy(webAppDir, handler.getPath(), id);
               
					
					appNameToFile.put(handler.getFile().getName(), id);
                    log.info("Portlet app \"" + id + "\" " + "successfuly deployed.");
                    
                }

            }
            catch (Exception e1)
            {

                String msg = "Error deploying portlet app: " + e1.toString();
                throw new DeploymentException(msg, e1);
            }
        }
        else if (event.getEventType().equals(DeploymentEvent.EVENT_TYPE_UNDEPLOY))
        {
			String paName = null;
            try
            {
                FSObjectHandler handler = (FSObjectHandler) event.getHandler();
                File fileThatWasRemoved = handler.getFile();
                String fileName = fileThatWasRemoved.getName();
                paName = (String) appNameToFile.get(fileName);
                if(paName == null)
                {
                	String msg = "Unable to locate application name for archive \""+fileName+"\"";
                    log.warn(msg);
                	throw new DeploymentException(msg);
                }
                log.info("Preparing to undeploy portlet application \""+paName+"\"");
                pam.undeploy(webAppDir, paName);
                log.info("Portlet application \""+paName+"\""+" was successfuly undeployed.");
            }
            catch (Exception e)
            {
				String msg = "Error undeploying portlet app "+paName+": " + e.toString();
				if(e instanceof DeploymentException)
				{
					throw (DeploymentException) e;
				}
				else
				{
					throw new DeploymentException(msg, e);
				}
				 
            }
            
        }

    }

}
