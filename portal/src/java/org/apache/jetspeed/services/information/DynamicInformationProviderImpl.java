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
package org.apache.jetspeed.services.information;

import java.util.Map;
import java.util.Iterator;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Enumeration;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;
import javax.portlet.WindowState;
import javax.portlet.PortletMode;

import org.apache.pluto.services.information.DynamicInformationProvider;
import org.apache.pluto.services.information.PortletActionProvider;
import org.apache.pluto.services.information.ResourceURLProvider;
import org.apache.pluto.om.window.PortletWindow;
import org.apache.pluto.util.NamespaceMapperAccess;
import org.apache.pluto.services.information.PortletURLProvider;
import org.apache.jetspeed.container.session.NavigationalState;
import org.apache.jetspeed.engine.core.PortletActionProviderImpl;
import org.apache.jetspeed.request.RequestContext;
import org.apache.jetspeed.request.RequestContextComponent;
import org.apache.jetspeed.Jetspeed;

/**
 * Provides dynamic information to Pluto Container:
 * 
 * 1.  getPortletURL
 * 2.  getRequestMimetype
 * 3.  getResponseMimetype
 * 4.  getResponseMimetypes
 * 5.  getPortletMode
 * 6.  getPreviousPortletMode
 * 7.  getWindowState
 * 8.  getPreviousWindowState
 * 9.  isPortletModeAllowed
 * 10. isWindowStateAllowed
 * 11. getSupportedPortletModes
 * 12. getSupportedWindowStates
 * 13. getAllParameters
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class DynamicInformationProviderImpl implements DynamicInformationProvider
{
    HttpServletRequest request = null;
    ServletConfig config = null;

    RequestContext context;
    
    public DynamicInformationProviderImpl(HttpServletRequest request,
                                          ServletConfig config)
    {
        this.request = request;
        this.config = config;
        
        // TODO: assemble this dependency when this provider is converted to a component
        RequestContextComponent rcc = (RequestContextComponent)Jetspeed.getComponentManager().getComponent(RequestContextComponent.class);
        this.context = rcc.getRequestContext(request);
    }

    public PortletURLProvider getPortletURLProvider(PortletWindow portletWindow)
    {
        return new PortletURLProviderImpl(this,
                                          portletWindow);
    }

     public String getRequestContentType()
     {
         return context.getMimeType().toString();
     }

     public String getResponseContentType()
     {
         return context.getMimeType().toString();
     }

     public Iterator getResponseContentTypes()
     {
        HashSet responseMimeTypes = new HashSet(NumberOfKnownMimetypes);
        // TODO: need to integrate with capability code       
        responseMimeTypes.add("text/html");
 
        return responseMimeTypes.iterator();
     }

     public PortletMode getPortletMode(PortletWindow portletWindow)
     {
         NavigationalState navState = context.getNavigationalState();
         return navState.getMode(portletWindow);
     }

     public PortletMode getPreviousPortletMode(PortletWindow portletWindow)
     {
         NavigationalState navState = context.getNavigationalState();         
         return navState.getPreviousMode(portletWindow);
     }

     public WindowState getWindowState(PortletWindow portletWindow)
     {
         NavigationalState navState = context.getNavigationalState();         
         return navState.getState(portletWindow);
     }

     public WindowState getPreviousWindowState(PortletWindow portletWindow)
     {
         NavigationalState navState = context.getNavigationalState();         
         return navState.getPreviousState(portletWindow);
     }
    public boolean isPortletModeAllowed(PortletMode mode)
    {
        //checks whether PortletMode is supported as example
        String[] supportedModes = Jetspeed.getContext().getConfiguration().getStringArray("supported.portletmode");
        for (int i=0; i<supportedModes.length; i++)
        {
            if (supportedModes[i].equalsIgnoreCase(mode.toString()))
            {
                return true;
            }
        }
        return false;
    }

    public boolean isWindowStateAllowed(WindowState state)
    {
        //checks whether WindowState is supported as example
        String[] supportedStates = Jetspeed.getContext().getConfiguration().getStringArray("supported.windowstate");
        for (int i=0; i<supportedStates.length; i++)
        {
            if (supportedStates[i].equalsIgnoreCase(state.toString()))
            {
                return true;
            }
        }
        return false;
    }
    
    public java.util.Iterator getSupportedPortletModes()
    {
        HashSet set = new HashSet();

        String[] supportedStates = Jetspeed.getContext().getConfiguration().getStringArray("supported.portletmode");

        for (int i=0; i<supportedStates.length; i++)
        {
            set.add(supportedStates[i].toString());
        }
        return set.iterator();

    }

    public java.util.Iterator getSupportedWindowStates()
    {
        HashSet set = new HashSet();

        String[] supportedStates = Jetspeed.getContext().getConfiguration().getStringArray("supported.windowstate");

        for (int i=0; i<supportedStates.length; i++)
        {
            set.add(supportedStates[i].toString());
        }
        return set.iterator();

    }

    public String getBasePortalURL()
    {
         return context.getNavigationalState().getBaseURL();
    }

    public Map getAllParameters(PortletWindow portletWindow)
    {
        Enumeration parameters = request.getParameterNames();

        Map portletParameters = new HashMap();

        while (parameters.hasMoreElements())
        {
            String name = (String)parameters.nextElement();

            String portletParameter = NamespaceMapperAccess.getNamespaceMapper().decode(portletWindow.getId(),name);

            if (portletParameter!=null) // it is in the portlet's namespace
            {
                portletParameters.put(portletParameter, request.getParameterValues(name) );
            }
        }

        NavigationalState navState = context.getNavigationalState();
        
        Iterator iterator = navState.getRenderParamNames(portletWindow);
        while (iterator.hasNext())
        {
            String name = (String)iterator.next();

            String[] values = navState.getRenderParamValues(portletWindow, name);

            portletParameters.put(name, values );

        }

        return portletParameters;
    }

    private final static int NumberOfKnownMimetypes = 15;

    
    /** 
     * <p>
     * getPortletActionProvider
     * </p>
     * 
     * @see org.apache.pluto.services.information.DynamicInformationProvider#getPortletActionProvider(org.apache.pluto.om.window.PortletWindow)
     * @param arg0
     * @return
     */
    public PortletActionProvider getPortletActionProvider(PortletWindow window)
    {        
        return new PortletActionProviderImpl(request, config, window);
    }

    /** 
     * <p>
     * getResourceURLProvider
     * </p>
     * 
     * @see org.apache.pluto.services.information.DynamicInformationProvider#getResourceURLProvider(org.apache.pluto.om.window.PortletWindow)
     * @param arg0
     * @return
     */
    public ResourceURLProvider getResourceURLProvider(PortletWindow window)
    {
        
        return new ResourceURLProviderImpl(this.context, window);
    }

}
