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

import org.apache.jetspeed.util.descriptor.PortletApplicationWar;

/**
 * This is the interface that defines the Deployment-related methods to deploy
 * Portlet Applications.
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @author <a href="mailto:roger.ruttimann@earthlink.net">Roger Ruttimann</a> 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @author <a href="mailto:mavery@einnovation.com">Matt Avery</a>
 * @version $Id$
 * @see org.apache.jetspeed.util.desciptor.PortletApplicationWar
 */
public interface Deployment
{    
    
    /**
     * Deploys the specified war file to the webapps dirctory specified.
     * 
     * @param paWar PortletApplicationWar to deploy
     * @throws PortletApplicationException
     */
    public void deploy( PortletApplicationWar paWar ) 
    throws PortletApplicationException;
    
    /**
     * Undeploys application.
     * 
     * @param paWar PortletApplicaionWar to undeploy
     * @throws PortletApplicationException
     */
    public void undeploy(PortletApplicationWar paWar) 
    throws PortletApplicationException;
    
    String getDeploymentPath(String webAppName);
    
    public void redeploy(PortletApplicationWar paWar) 
    throws PortletApplicationException;


}

