/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2000-2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *    "Apache Jetspeed" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Apache Jetspeed", nor may "Apache" appear in their name, without
 *    prior written permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.jetspeed.tools.pamanager;



import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.registry.JetspeedPortletRegistry;
import org.apache.jetspeed.tools.pamanager.servletcontainer.TomcatManager;


/**
 * This is the catalina specific implemenation for deplyment of Portlet Applications.
 *
 * @author <a href="mailto:roger.ruttimann@earthlink.net">Roger Ruttimann</a> 
  * @version $Id$
 */


public class CatalinaPAM extends FileSystemPAM implements Deployment, Lifecycle
{
    // Implementaion of deplyment interface

    private TomcatManager tomcatManager;
    protected static final Log log = LogFactory.getLog("deployment");

    public CatalinaPAM(String server, int port, String user, String password) throws PortletApplicationException
    {
    	useDefaultPluginFordeploy = true;
		
        try
        {
            tomcatManager = new TomcatManager(server, port, user, password);
        }
        catch (Exception e)
        {
            throw new PortletApplicationException(e);
        }

    }

    // Interface not supported by this implementation 
    public void deploy(String webAppsDir, String warFile, String paName) throws PortletApplicationException
    {
        
        try
        {            
			super.deploy(webAppsDir, warFile, paName);
            //checkResponse(tomcatManager.install(warFile, paName));
            checkResponse(tomcatManager.install(webAppsDir + "/" + paName, paName));
        }
        catch (PortletApplicationException pe)
        {
            throw pe;
        }
        catch (Exception e)
        {
            throw new PortletApplicationException(e);
        }
    }

    /**
     * Deploys the specified war file to the webapps directory on the Application Server.
     * The appServer parameter specifies a specific Application Server.
     * 
     * 
     * @param warFile The warFile containing the Portlet Application
     * @param paName The Portlet Application name
     * @throws PortletApplicationException
     */

    public void deploy(String warFile, String paName) throws PortletApplicationException
    {
		super.deploy(null, warFile, paName, 0);	
        try
        {
            checkResponse(tomcatManager.install(warFile, paName));
        }
        catch (PortletApplicationException pe)
        {
            throw pe;
        }
        catch (Exception e)
        {
            throw new PortletApplicationException(e);
        }
    }

    /**
     * Prepares the specified war file for deployment.
     * 
     * @param paName The Portlet Application name 
     * @throws PortletApplicationException
     */

    public void undeploy(String paName) throws PortletApplicationException
    {
        try
        {
            checkResponse(tomcatManager.remove(paName));
            super.undeploy(paName);
        }
        catch (UnsupportedOperationException usoe)
        {
            // ignore FS PAM not suporting this
        }
        catch (Exception e)
        {
            throw new PortletApplicationException(e);
        }
    }

    /** Undeploys application.
    * 
    * @param webAppsDir The webapps directory inside the Application Server
    * @param paName The Portlet Application name 
    * @throws PortletApplicationException
    */

    public void undeploy(String webAppsDir, String paName) throws PortletApplicationException
    {
        try
        {
            checkResponse(tomcatManager.remove(paName));
            super.undeploy(webAppsDir, paName);
        }
        catch (PortletApplicationException pe)
        {
            throw pe;
        }
        catch (Exception e)
        {
            throw new PortletApplicationException(e);
        }
    }

    // Implementaion of Lifecycle interface
    /**
    * Starts the specified Portlet Application on the Application Server
    * 
    * @param paName The Portlet Application name 
    * @throws PortletApplicationException
    */

    public void start(String paName) throws PortletApplicationException
    {
        try
        {
            checkResponse(tomcatManager.start(paName));
        }
        catch (PortletApplicationException pe)
        {
            throw pe;
        }
        catch (Exception e)
        {
            throw new PortletApplicationException(e);
        }

    }

    /**
     * Stops a portlet application from running on the Application Server
     * 
     * @param paName The Portlet Application name 
     * @throws PortletApplicationException
     */

    public void stop(String paName) throws PortletApplicationException
    {
        try
        {
            checkResponse(tomcatManager.stop(paName));
        }
        catch (PortletApplicationException pe)
        {
            throw pe;
        }
        catch (Exception e)
        {
            throw new PortletApplicationException(e);
        }
    }

    /**
     * Reloads a portlet application.
     * 
     * @param paName The Portlet Application name 
     * @throws PortletApplicationException
     */

    public void reload(String paName) throws PortletApplicationException
    {
        try
        {
            checkResponse(tomcatManager.reload(paName));
        }
        catch (PortletApplicationException pe)
        {
            throw pe;
        }
        catch (Exception e)
        {
            throw new PortletApplicationException(e);
        }
    }

    /**
     * 
     * @param response
     * @throws PortletApplicationException
     */
    private void checkResponse(String response) throws PortletApplicationException
    {
        if (response == null || !response.startsWith("OK"))
        {
            if (response == null)
            {
                response = "null response";
            }

            throw new PortletApplicationException("Catalina container action failed, \"" + response + "\"");
        }
        else
        {
            log.info("Catalina deployment response: " + response);
        }
    }

    /**
     * @see org.apache.jetspeed.tools.pamanager.Deployment#deploy(java.lang.String, java.lang.String, java.lang.String, java.lang.String, int)
     */
    public void deploy(String webAppsDir, String warFile, String paName, String deploymentDbAlias, int startState)
        throws PortletApplicationException
    {

		super.deploy(webAppsDir, warFile, paName, startState);		
        try
        {
            checkResponse(tomcatManager.install(warFile, paName));
        }
        catch (PortletApplicationException pe)
        {
            throw pe;
        }
        catch (Exception e)
        {
            throw new PortletApplicationException(e);
        }
    }

    /**
     * @see org.apache.jetspeed.tools.pamanager.FileSystemPAM#identifyDeploymentSystem()
     */
    protected void identifyDeploymentSystem() throws IOException
    {
		JetspeedPortletRegistry.setDeploymentSystem(null, null);
    }

}
