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
package org.apache.jetspeed.request;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletConfig;

import org.apache.jetspeed.PortalContext;
import org.apache.jetspeed.om.profile.Profile;
import org.apache.jetspeed.services.factory.FactoryManager;
import org.apache.jetspeed.capability.CapabilityMap;
import org.apache.jetspeed.engine.core.PortalControlParameter;
import org.apache.jetspeed.engine.core.PortalURL;
import org.apache.jetspeed.engine.core.PortalURLImpl;
import org.apache.jetspeed.engine.servlet.ServletRequestFactory;
import org.apache.jetspeed.engine.servlet.ServletResponseFactory;
import org.apache.pluto.om.window.PortletWindow;

import org.apache.pluto.om.portlet.PortletDefinition;

/**
 * Jetspeed Request Context is associated with each portal request.
 * The request holds the contextual information shared amongst components 
 * in the portal, accessed through a common valve pipeline. 
 *
 * @author <a href="mailto:david@bluesunrise.com">David Sean Taylor</a>
 * @version $Id$
 */
public class JetspeedRequestContext implements RequestContext
{
    private PortalContext pc;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private ServletConfig config;
    private Profile profile;
    private PortletDefinition portletDefinition;

    private CapabilityMap capabilityMap;
    private String mimeType;
    private String mediaType;
    private PortalURL requestedPortalURL;
    private PortletWindow actionWindow;
    private String encoding;

    public final static String REQUEST_PORTALENV = "org.apache.jetspeed.request.RequestContext";

    /**
     * Create a new Request Context
     * 
     * @param pc
     * @param request
     * @param response
     * @param config
     */
    public JetspeedRequestContext(PortalContext pc, HttpServletRequest request, HttpServletResponse response, ServletConfig config)
    {
        this.pc = pc;
        this.request = request;
        this.response = response;
        this.config = config;

        // set context in Request for later use
        if (null != this.request)
        {
            this.request.setAttribute(REQUEST_PORTALENV, this);
        }
        requestedPortalURL = new PortalURLImpl(this);
    }

    private JetspeedRequestContext()
    {
    }

    /**
     * The servlet request can always get you back to the Request Context if you need it
     * This static accessor provides this capability 
     * 
     * @param request
     * @return RequestContext
     */
    public static RequestContext getRequestContext(HttpServletRequest request)
    {
        return (RequestContext) request.getAttribute(REQUEST_PORTALENV);
    }

    public PortalContext getPortalContext()
    {
        return pc;
    }

    public HttpServletRequest getRequest()
    {
        return request;
    }

    public HttpServletResponse getResponse()
    {
        return response;
    }

    public ServletConfig getConfig()
    {
        return config;
    }

    public Profile getProfile()
    {
        return profile;
    }

    public void setProfile(Profile profile)
    {
        this.profile = profile;
    }

    public PortletDefinition getPortletDefinition()
    {
        return portletDefinition;
    }

    public void setPortletDefinition(PortletDefinition portletDefinition)
    {
        this.portletDefinition = portletDefinition;
    }

    /** Set the capabilityMap. Used by the CapabilityValve
       * 
       * @param capabilityMap 
       */
    public void setCapabilityMap(CapabilityMap map)
    {
        this.capabilityMap = map;
    }

    /** get the Capability Map
     * 
     */
    public CapabilityMap getCapabilityMap()
    {
        return this.capabilityMap;
    }

    /** Set the Mimetype. Used by the CapabilityValve
     * 
     * @param mimeType 
     */
    public void setMimeType(String mimeType)
    {
        this.mimeType = mimeType;
    }

    /** get the mimeType for the request
     * 
     */
    public String getMimeType()
    {
        return this.mimeType;
    }

    /** Set the mediaType. Used by the CapabilityValve
     * 
     * @param mediaType 
     */
    public void setMediaType(String mediaType)
    {
        this.mediaType = mediaType;
    }

    /** get the Media Type
     * 
     */
    public String getMediaType()
    {
        return this.mediaType;
    }

    public PortalURL getRequestedPortalURL()
    {
        return requestedPortalURL;
    }

    public void changeRequestedPortalURL(PortalURL url, PortalControlParameter control)
    {
        requestedPortalURL = url;
        requestedPortalURL.analyzeControlInformation(control);
    }

    /**
     * Get the target Portlet Action Window
     *
     * @return PortletWindow The target portlet window
     */
    public PortletWindow getActionWindow()
    {
        return actionWindow;
    }

    /**
     * Sets the target Portlet Action Window
     * 
     * @param window
     */
    public void setActionWindow(PortletWindow portletWindow)
    {
        this.actionWindow = portletWindow;
    }

    /**
     * get the character encoding
     * 
     * 
     */
    public String getCharacterEncoding()
    {
        return this.encoding;
    }

    /**
     * set character encoding
     * 
     * @param enc 
     */
    public void setCharacterEncoding(String enc)
    {
        this.encoding = enc;
    }

    /** 
     * <p>
     * getRequestForWindow
     * </p>
     * 
     * @see org.apache.jetspeed.request.RequestContext#getRequestForWindow(org.apache.pluto.om.window.PortletWindow)
     * @param window
     * @return
     */
    public HttpServletRequest getRequestForWindow(PortletWindow window)
    {
        ServletRequestFactory reqFac =
            (ServletRequestFactory) FactoryManager.getFactory(javax.servlet.http.HttpServletRequest.class);
        HttpServletRequest requestWrapper = reqFac.getServletRequest(request, window);
        return requestWrapper;
    }

    /** 
     * <p>
     * getResponseForWindow
     * </p>
     * 
     * @see org.apache.jetspeed.request.RequestContext#getResponseForWindow(org.apache.pluto.om.window.PortletWindow)
     * @param window
     * @return
     */
    public HttpServletResponse getResponseForWindow(PortletWindow window)
    {
        ServletResponseFactory rspFac = (ServletResponseFactory) FactoryManager.getFactory(HttpServletResponse.class);
        HttpServletResponse wrappedResponse = rspFac.getServletResponse(response);
        return wrappedResponse;
    }

}
