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
package org.apache.jetspeed.container.url.impl;

import java.util.Iterator;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.StringTokenizer;

import javax.portlet.PortletMode;
import javax.portlet.WindowState;

import org.apache.pluto.om.window.PortletWindow;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.container.session.NavigationalStateComponent;
import org.apache.jetspeed.container.url.PortalURL;
import org.apache.jetspeed.request.RequestContext;

/**
 * PortalURL defines the interface for manipulating Jetspeed Portal URLs.
 * These URLs are used internally by the portal and are not available to
 * Portlet Applications. This implementation is compatible with Pluto
 * portal URLs. All navigational state is stored in the URL.
 *
 * @author <a href="mailto:david@bluesunrise.com">David Sean Taylor</a>
 * 
 * @version $Id$
 */
public class PathPortalURL 
    extends
        AbstractPortalURL
    implements 
        PortalURL
{
    private static final Log log = LogFactory.getLog(PathPortalURL.class);


    public PathPortalURL(RequestContext context, NavigationalStateComponent nsc)
    {        
        super(context, nsc);
    }
        

    public boolean isStateFullParameter(String param)
    {
        if (isNavigationalParameter(param))
        {
            String prefix = nsc.getNavigationKey(NavigationalStateComponent.PREFIX);            
            if ((param.startsWith(prefix + nsc.getNavigationKey(NavigationalStateComponent.MODE)))
                || (param.startsWith(prefix + nsc.getNavigationKey(NavigationalStateComponent.PREV_MODE)))
                || (param.startsWith(prefix + nsc.getNavigationKey(NavigationalStateComponent.STATE)))
                || (param.startsWith(prefix + nsc.getNavigationKey(NavigationalStateComponent.PREV_STATE)))
                || (param.startsWith(prefix + nsc.getNavigationKey(NavigationalStateComponent.RENDER_PARAM))))
            {
                return true;
            }
        }
        return false;
    }

        
}
