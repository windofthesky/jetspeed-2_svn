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

import java.util.Iterator;
import java.util.Map;

import javax.portlet.PortletMode;
import javax.portlet.WindowState;

import org.apache.jetspeed.engine.core.PortalControlParameter;
import org.apache.jetspeed.engine.core.PortalURL;
import org.apache.jetspeed.request.JetspeedRequestContext;
import org.apache.pluto.om.window.PortletWindow;
import org.apache.pluto.services.information.PortletURLProvider;
import org.apache.pluto.util.NamespaceMapperAccess;

/**
 * Provides access to the Portal URL manipulation 
 * 
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class PortletURLProviderImpl implements PortletURLProvider
{
    private DynamicInformationProviderImpl provider = null;
    private PortletWindow portletWindow = null;
    private PortletMode mode = null;
    private WindowState state = null;
    private boolean action = false;
    private boolean secure = false;
    private boolean clearParameters = false;
    private Map parameters = null;

    private PortalURL portalUrl;
    private PortalControlParameter controlURL;
    public PortletURLProviderImpl(DynamicInformationProviderImpl provider, PortletWindow portletWindow)
    {
        this.provider = provider;
        this.portletWindow = portletWindow;
        portalUrl = JetspeedRequestContext.getRequestContext(provider.request).getRequestedPortalURL();
        controlURL = new PortalControlParameter(portalUrl);
    }

    public void setPortletMode(PortletMode mode)
    {
        this.mode = mode;
    }

    public void setWindowState(WindowState state)
    {
        this.state = state;
    }

    public void setAction()
    {
        action = true;
    }

    public void setSecure()
    {
        secure = true;
    }

    public void clearParameters()
    {

        controlURL.clearRenderParameters(portletWindow);

    }

    public void setParameters(Map parameters)
    {
        this.parameters = parameters;
    }

    public String toString()
    {

        if (mode != null)
        {
            controlURL.setMode(portletWindow, mode);
        }

        if (state != null)
        {
            controlURL.setState(portletWindow, state);
        }

        // STW: Spec reference PLT:12:2
        // Had to move logic directly up into the actual clear call
        //        if (clearParameters)
        //        {
        //        	// clear any existing paramters per the spec
        //            controlURL.clearRenderParameters(portletWindow);
        //        }

        if (action)
        {
            controlURL.setAction(portletWindow);
        }

        if (parameters != null)
        {
            Iterator names = parameters.keySet().iterator();
            while (names.hasNext())
            {
                String name = (String) names.next();
                Object value = parameters.get(name);
                String[] values = value instanceof String ? new String[] {(String) value }
                : (String[]) value;
                if (action)
                {
                    controlURL.setRequestParam(
                        NamespaceMapperAccess.getNamespaceMapper().encode(portletWindow.getId(), name),
                        values);

                }
                else
                {
                    controlURL.setRenderParam(portletWindow, name, values);
                }
            }
        }

        return portalUrl.toString(controlURL, new Boolean(secure));
    }

}
