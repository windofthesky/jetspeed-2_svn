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
package org.apache.jetspeed.container.url.impl;

import org.apache.jetspeed.container.session.NavigationalStateComponent;
import org.apache.jetspeed.container.url.PortalURL;
import org.apache.jetspeed.request.RequestContext;
import org.apache.pluto.om.window.PortletWindow;

/**
 * PortalURL defines the interface for manipulating Jetspeed Portal URLs.
 * These URLs are used internally by the portal and are not available to
 * Portlet Applications. This class provids common implementation 
 * for all concrete Portal URL implementations.
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public abstract class AbstractPortalURL 
{
    protected String serverName;
    protected String serverScheme;
    protected String contextPath;
    protected String basePath;
    protected int serverPort;
    protected NavigationalStateComponent nsc;
    protected RequestContext context;
    
    public AbstractPortalURL(RequestContext context, NavigationalStateComponent nsc)
    {
        this.context = context;
        this.nsc = nsc;        
        init(context);
    }
    
    public void init(RequestContext context)
    {
        if (null != context.getRequest())
        {
            this.serverName = context.getRequest().getServerName();
            this.serverPort = context.getRequest().getServerPort();
            this.serverScheme = context.getRequest().getScheme();
            this.contextPath = context.getRequest().getContextPath();
            this.basePath = contextPath + context.getRequest().getServletPath();
        }    
    }

    public String getBaseURL()
    {
        return getBaseURLBuffer().toString();
    }
    
    protected StringBuffer getBaseURLBuffer()
    {
        StringBuffer buffer = new StringBuffer();
        buffer.append(this.serverScheme);
        buffer.append("://");
        buffer.append(this.serverName);
        if ((this.serverScheme.equals(PortalURL.HTTP) && this.serverPort != 80)
            || (this.serverScheme.equals(PortalURL.HTTPS) && this.serverPort != 443))
        {
            buffer.append(":");
            buffer.append(this.serverPort);
        }
        return buffer;
    }
    
    public boolean isNavigationalParameter(String token)
    {
        return token.startsWith(nsc.getNavigationKey(NavigationalStateComponent.PREFIX));
    }
    
    String getStateKey(PortletWindow window)
    {
        return nsc.getNavigationKey(NavigationalStateComponent.STATE) + "_" + window.getId().toString();
    }
    
    String getModeKey(PortletWindow window)
    {
        return nsc.getNavigationKey(NavigationalStateComponent.MODE) + "_" + window.getId().toString();
    }
    
}
