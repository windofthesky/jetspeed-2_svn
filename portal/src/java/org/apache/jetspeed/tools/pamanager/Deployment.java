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
package org.apache.jetspeed.tools.pamanager;

import java.util.Map;

/**
 * This is the interface that defines the Deployment-related methods to deploy
 * Portlet Applications.
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @author <a href="mailto:roger.ruttimann@earthlink.net">Roger Ruttimann</a> 
 * @version $Id$
 */
public interface Deployment
{    
    /**
     * Some application servers require connections created before deploying.
     * 
     * @param params map of server specific properties
     */
    public void connect(Map params)
    throws PortletApplicationException;
    
    /**
     * Deploys the specified war file to the webapps dirctory specified.
     * 
     * @param webAppsDir The webapps directory inside the Application Server
     * @param warFile The warFile containing the Portlet Application
     * @param paName The Portlet Application name
     * @throws PortletApplicationException
     */
    public void deploy(String webAppsDir, 
                       String warFile,
                       String paName
                       ) 
    throws PortletApplicationException;
    
    /**
     * Deploys the specified war file to the webapps directory on the Application Server.
     * The appServer parameter specifies a specific Application Server.
     * 
     * @param warFile The warFile containing the Portlet Application
     * @param paName The Portlet Application name
     * @throws PortletApplicationException 
     */
    public void deploy(String warFile,
                       String portletApplicationName) 
        throws PortletApplicationException;
                       
	
    /**
     * Undeploys application.
     * 
     * @param webApplicationName The web application directory name inside the Application Server. 
     *                           Parameter can be ignored for some servers
     * @param portletApplicationName The Portlet Application name
     * @throws PortletApplicationException
     */
    public void undeploy(String webApplicationName,
                         String portletApplicationName) 
    throws PortletApplicationException;


}

