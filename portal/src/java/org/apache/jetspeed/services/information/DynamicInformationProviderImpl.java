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
import org.apache.jetspeed.engine.core.PortalControlParameter;
import org.apache.jetspeed.engine.core.PortalURL;
import org.apache.jetspeed.engine.core.PortletActionProviderImpl;
import org.apache.jetspeed.request.JetspeedRequestContext;
import org.apache.jetspeed.request.RequestContext;
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

    PortalURL currentURL = null;
    PortalControlParameter control = null;

    public DynamicInformationProviderImpl(HttpServletRequest request,
                                          ServletConfig config)
    {
        this.request = request;
        this.config = config;
        
        currentURL = JetspeedRequestContext.getRequestContext(request).getRequestedPortalURL();
        control = new PortalControlParameter(currentURL);
    }

    public PortletURLProvider getPortletURLProvider(PortletWindow portletWindow)
    {
        return new PortletURLProviderImpl(this,
                                          portletWindow);
    }

     public String getRequestContentType()
     {
         RequestContext context = JetspeedRequestContext.getRequestContext(this.request);
         return context.getMimeType().toString();
     }

     public String getResponseContentType()
     {
         RequestContext context = JetspeedRequestContext.getRequestContext(this.request);
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
         return control.getMode(portletWindow);
     }

     public PortletMode getPreviousPortletMode(PortletWindow portletWindow)
     {
         return control.getPrevMode(portletWindow);
     }

     public WindowState getWindowState(PortletWindow portletWindow)
     {
         return control.getState(portletWindow);
     }

     public WindowState getPreviousWindowState(PortletWindow portletWindow)
     {
         return control.getPrevState(portletWindow);
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
         return currentURL.getBaseURL();
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

        Iterator iterator = control.getRenderParamNames(portletWindow);
        while (iterator.hasNext())
        {
            String name = (String)iterator.next();

            String[] values = control.getRenderParamValues(portletWindow, name);

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
        
        return new ResourceURLProviderImpl(this, window);
    }

}
