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

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.jetspeed.PortalContext;
import org.apache.jetspeed.capability.CapabilityMap;
import org.apache.jetspeed.engine.core.PortalControlParameter;
import org.apache.jetspeed.engine.core.PortalURL;
import org.apache.jetspeed.om.profile.Profile;
import org.apache.jetspeed.util.MimeType;
import org.apache.pluto.om.window.PortletWindow;
/**
 * Portal Request Context is associated with each request 
 *
 * @author <a href="mailto:david@bluesunrise.com">David Sean Taylor</a>
 * @version $Id$
 */
public interface RequestContext
{
    /**
     * Gets the HTTP Servlet Request 
     * 
     * @return HttpServletRequest
     */
    public HttpServletRequest getRequest();

    /**
     * Gets the HTTP Servlet Response
     * 
     * @return HttpServletResponse
     */
    public HttpServletResponse getResponse();

    /**
     * Gets the HTTP Servlet Config 
     * 
     * @return ServletConfig
     */
    public ServletConfig getConfig();

    /**
     * Gets the Jetspeed Portal Context 
     * 
     * @return HttpServletRequest
     */
    public PortalContext getPortalContext();

    /**
     * Gets the target page profile for this request 
     * 
     * @return Profile
     */
    public Profile getProfile();

    /**
     * Sets the target page profile for this request 
     * 
     * @param profile The target profile
     */
    public void setProfile(Profile profile);
    
    /** 
     * Set the capabilityMap. Used by the CapabilityValve
     * 
     * @param capabilityMap 
     */
    public void setCapabilityMap(CapabilityMap map);
    
    /** 
     * Get the Capability Map
     * 
     */
    public CapabilityMap getCapabilityMap();

    /** 
     * Set the Mimetype. Set by the CapabilityValve
     * 
     * @param mimeType 
     */
    public void setMimeType(String mimeType);

    /** 
     * Get the mimeType for the request
     * 
     */
    public String getMimeType();
        
    /** 
     * Set the mediaType. Set by the CapabilityValve
     * 
     * @param mediaType 
     */
    public void setMediaType(String mediaType);
    
    /** 
     * get the Media Type 
     * 
     */
    public String getMediaType();

    /**
     * Get the requested Portlet URL for this request
     * 
     * @return PortletURL the requested Portlet URL
     */    
    public PortalURL getRequestedPortalURL();

    /**
     * Change the Portlet URL to a new portal URL 
     * 
     * @param url
     * @param control
     */    
    public void changeRequestedPortalURL(PortalURL url, PortalControlParameter control);

    /**
     * Get the target Action Window
     *
     * @return PortletWindow The target portlet action window
     */
    public PortletWindow getActionWindow();
    
    /**
     * Sets the target Portlet Window
     * 
     * @param window
     */
    public void setActionWindow(PortletWindow window);
    
    /**
     * get the character encoding
     * 
     * 
     */
    public String getCharacterEncoding();
    
    /**
     * set character encoding
     * 
     * @param enc 
     */
    public void setCharacterEncoding(String enc);
    
}
