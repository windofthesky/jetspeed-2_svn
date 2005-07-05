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
package org.apache.jetspeed.container.invoker;

import javax.servlet.ServletConfig;

import org.apache.jetspeed.factory.PortletFactory;
import org.apache.pluto.invoker.PortletInvoker;
import org.apache.pluto.om.portlet.PortletDefinition;

/**
 * JetspeedPortletInvoker extends Pluto's portlet invoker and extends it
 * with lifecycle management. Portlet Invokers can be pooled, and activated
 * and passivated per request cycle.
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: JetspeedPortletInvoker.java 188436 2005-03-23 22:54:30Z ate $
 */
public interface JetspeedPortletInvoker extends PortletInvoker
{
    /**
     * Activating an invoker makes it ready to invoke portlets.
     * If an invoker's state is not activated, it can not invoke.
     * 
     * @param portletFactory The factory to get access to the portlet being invoked.
     * @param portletDefinition The portlet's definition that is being invoked.
     * @param servletConfig The servlet configuration of the portal. 
     * @param containerServlet
     */
    void activate(PortletFactory portletFactory, PortletDefinition portletDefinition, ServletConfig servletConfig);

    /**
     * Activating an invoker makes it ready to invoke portlets.
     * If an invoker's state is not activated, it can not invoke.
     * This second signature allows for activating with an extra property.
     * 
     * @param portletFactory The factory to get access to the portlet being invoked.
     * @param portletDefinition The portlet's definition that is being invoked.
     * @param servletConfig The servlet configuration of the portal. 
     * @param property Implementation specific property
     * @param containerServlet
     */
    void activate(PortletFactory portletFactory, PortletDefinition portletDefinition, ServletConfig servletConfig, String property);
    
    /**
     * Passivates an invoker, freeing it back to the invoker pool.
     * If an invoker's state is passivated, it cannot be used to invoke portlets.
     */
    void passivate();
    
    /**
     * Returns true if the state of this invoke is 'activated', and false if it is 'passivated'.
     * @return True if the current state of the invoker is 'activated' otherwise false.
     */
    boolean isActivated();
}
