/**
 * Created on Jan 14, 2004
 *
 * 
 * @author
 */
package org.apache.jetspeed.deployment.impl;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.deployment.DeploymentEvent;
import org.apache.jetspeed.deployment.DeploymentEventListener;
import org.apache.jetspeed.deployment.DeploymentException;
import org.apache.jetspeed.deployment.DeploymentHandler;
import org.apache.jetspeed.deployment.fs.FSObjectHandler;
import org.apache.jetspeed.tools.pamanager.FileSystemPAM;
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
    private String dbAalias;
    
    

	public DeployPortletAppEventListener(String webAppDir, String dbAlias)
	{
		this.webAppDir = webAppDir;
		this.dbAalias = dbAlias;
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
                    log.info("Loading portlet application from web archive "+handler.getPath());
                    SAXBuilder builder = new SAXBuilder();
                    Document portletXml = builder.build(portletXmlStream);
                    Element portletApp = portletXml.getRootElement();
                    String id = portletApp.getAttributeValue("id");
                    if(id == null)
                    {
						throw new PortletApplicationException("<portlet-app> requires a unique \"id\" attribute.");
                    }             
                    log.info("Preparing to deploy portlet app \""+id+"\"");
                    FileSystemPAM pam = new FileSystemPAM();
                    pam.deploy(webAppDir, handler.getPath(), id, dbAalias, 0);
					log.info("Portlet app \""+id+"\" "+"successfuly deployed.");
                }
            }
            catch (Exception e1)
            {

                throw new DeploymentException("Error deploying portlet app: " + e1.toString(), e1);
            }
        }

    }

}
