/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
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

import java.io.IOException;

import javax.portlet.PortletException;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.servlet.ServletConfig;

import org.apache.jetspeed.factory.PortletFactory;
import org.apache.jetspeed.om.portlet.PortletDefinition;
import org.apache.jetspeed.container.FilterManager;
import org.apache.jetspeed.container.PortletWindow;
import org.apache.pluto.container.PortletRequestContext;

/**
 * JetspeedPortletInvoker extends Pluto's portlet invoker model and extends it
 * with lifecycle management. 
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public interface JetspeedPortletInvoker 
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
     * Passivates an invoker, freeing it back to the invoker pool.
     * If an invoker's state is passivated, it cannot be used to invoke portlets.
     */
    void passivate();
    
    /**
     * Returns true if the state of this invoke is 'activated', and false if it is 'passivated'.
     * @return True if the current state of the invoker is 'activated' otherwise false.
     */
    boolean isActivated();

    /**
     * Invoke an action
     * @param requestContext
     * @param portletRequest
     * @param portletResponse
     * @param action
     * @param filter
     * @throws PortletException
     * @throws IOException
     */
    void invoke(PortletRequestContext requestContext, PortletRequest portletRequest, PortletResponse portletResponse, 
                PortletWindow.Action action, FilterManager filter)
        throws PortletException, IOException;
    
}