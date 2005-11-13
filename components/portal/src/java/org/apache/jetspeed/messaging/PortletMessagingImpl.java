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
package org.apache.jetspeed.messaging;

import java.io.IOException;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.jetspeed.PortalReservedParameters;
import org.apache.jetspeed.container.window.PortletWindowAccessor;
import org.apache.jetspeed.request.RequestContext;
import org.apache.pluto.PortletContainerServices;
import org.apache.pluto.factory.PortletObjectAccess;
import org.apache.pluto.invoker.PortletInvoker;
import org.apache.pluto.invoker.PortletInvokerAccess;
import org.apache.pluto.om.window.PortletWindow;


/**
 * Message 
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: $
 */
public class PortletMessagingImpl 
{
    private PortletWindowAccessor windowAccessor;
    private PortletInvoker invoker;
    private ActionRequest actionRequest;
    private ActionResponse actionResponse;
    
    public PortletMessagingImpl(PortletWindowAccessor windowAccessor)
    {
        this.windowAccessor = windowAccessor;
    }
    
    public void processActionMessage(String portletName, RequestContext jetspeedRequest)
    throws PortletException, IOException
    {
        //RequestContext jetspeedRequest = (RequestContext)request.getAttribute(PortalReservedParameters.REQUEST_CONTEXT_ATTRIBUTE);
        PortletContainerServices.prepare("Jetspeed");

        PortletWindow window = windowAccessor.getPortletWindow("psd-1");
        HttpServletRequest requestForWindow = jetspeedRequest.getRequestForWindow(window);        
        HttpServletResponse responseForWindow = jetspeedRequest.getResponseForWindow(window);
        
        actionRequest = PortletObjectAccess.getActionRequest(window, requestForWindow, responseForWindow);
        actionResponse = PortletObjectAccess.getActionResponse(window, requestForWindow, responseForWindow); 
        
        invoker = PortletInvokerAccess.getPortletInvoker(window.getPortletEntity().getPortletDefinition());
        
        invoker.action(actionRequest, actionResponse);
        
        PortletContainerServices.release();
    }
    
}
