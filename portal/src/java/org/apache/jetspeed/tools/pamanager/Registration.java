/*
 * Copyright 2000-2001,2004 The Apache Software Foundation.
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
 * Registration interface for registering and deregistering portlet applications in the registry.
 * Registration does not include deployment operations. See the <link>Deployment</link> interface.
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @author <a href="mailto:mavery@einnovation.com">Matt Avery</a>
 * @version $Id$
 * @see org.apache.jetspeed.util.desciptor.PortletApplicationWar
 */
public interface Registration
{
    /**
     * Register a portlet application into the registry.
     * A PA should not be allowed to be registered if another PA with the same name or web application name exists. 
     * 
     * @param paWar PortletApplicationWar to register
     * @throws PortletApplicationException
     */
    void register(PortletApplicationWar paWar) 
        throws PortletApplicationException;
        
    /**
     * Unregister a portlet application from the registry.
     * A PA should not be allowed to be unregistered if any portlets are referenced 
     * in a portal page.
     * 
     * @param portletApplicationName The name of the portlet application to be unregistered. Can be same as web application.
     * @throws PortletApplicationException
     */
    void unregister(String paName) 
        throws PortletApplicationException;
    
}
