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
package org.apache.jetspeed.engine.core;

import javax.portlet.PortletMode;
import javax.portlet.WindowState;
import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;

import org.apache.pluto.om.window.PortletWindow;
import org.apache.pluto.services.information.PortletActionProvider;
import org.apache.jetspeed.request.RequestContext;
import org.apache.jetspeed.request.JetspeedRequestContext;

/**
 * Handle operations that the portlet may perform in an action method.
 * This service is request based.
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class PortletActionProviderImpl
implements PortletActionProvider
{
    HttpServletRequest request = null;
    ServletConfig config = null;

    public PortletActionProviderImpl(HttpServletRequest request,
                                          ServletConfig config)
    {
        this.request = request;
        this.config = config;
    }

    /* (non-Javadoc)
     * @see org.apache.pluto.services.information.PortletActionProvider#changePortletMode(PortletWindow, PortletMode)
     */
    public void changePortletMode(
        PortletWindow portletWindow,
        PortletMode mode) 
    {
        RequestContext context = JetspeedRequestContext.getRequestContext(request);

        PortalURL url = context.getRequestedPortalURL();
        PortalControlParameter controlURL = new PortalControlParameter(url);
        if (!(controlURL.getMode(portletWindow).equals(mode))
            && mode != null) 
        {
            controlURL.setMode(portletWindow, mode);
            context.changeRequestedPortalURL(url, controlURL);
        }

    }

    /* (non-Javadoc)
     * @see org.apache.pluto.services.information.PortletActionProvider#changePortletWindowState(PortletWindow, WindowState)
     */
    public void changePortletWindowState(
        PortletWindow portletWindow,
        WindowState state) 
    {
        RequestContext context = JetspeedRequestContext.getRequestContext(request);

        PortalURL url = context.getRequestedPortalURL();
        PortalControlParameter controlURL = new PortalControlParameter(url);

        if (!(controlURL.getState(portletWindow).equals(state))
            && state != null) 
        {
            controlURL.setState(portletWindow, state);
            context.changeRequestedPortalURL(url, controlURL);
        }
    }
    
}
