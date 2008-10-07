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

import javax.portlet.PortletMode;
import javax.portlet.WindowState;

import org.apache.jetspeed.container.session.NavigationalStateComponent;
import org.apache.jetspeed.container.url.PortalURL;
import org.apache.jetspeed.request.RequestContext;

/**
 * PortalURL defines the interface for manipulating Jetspeed Portal URLs.
 * These URLs are used internally by the portal and are not available to
 * Portlet Applications. This implementation stores its navigational state
 * in the session and does not encode navigational state in the URL.
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class SessionPortalURL 
    extends 
        AbstractPortalURL 
    implements 
        PortalURL 
{
    private String stateKey = null;
    private WindowState state = null;
    private String modeKey = null;
    private PortletMode mode = null;
    
    public SessionPortalURL(RequestContext context, NavigationalStateComponent nsc)
    {
        super(context, nsc);        
        //analyze();
    }
        
    public boolean isStateFullParameter(String param)
    {
        return isRenderParameter(param);
    }
    
}
