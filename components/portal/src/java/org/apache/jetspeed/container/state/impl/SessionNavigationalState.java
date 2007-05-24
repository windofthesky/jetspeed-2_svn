/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.jetspeed.container.state.impl;

import javax.portlet.WindowState;
import javax.servlet.http.HttpSession;

import org.apache.jetspeed.JetspeedActions;
import org.apache.jetspeed.cache.JetspeedCache;
import org.apache.jetspeed.container.state.NavigationalState;
import org.apache.jetspeed.request.RequestContext;

/**
 * SessionNavigationalState, stores nav parameters in the session, not on URL
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class SessionNavigationalState extends AbstractNavigationalState
{    
    public SessionNavigationalState(NavigationalStateCodec codec, JetspeedCache cache)
    {
        super(codec, cache);
    }

    public synchronized void sync(RequestContext context)
    {
        PortletWindowRequestNavigationalStates requestStates = getPortletWindowRequestNavigationalStates();
        
        // First check if a maximized window is set in the request.
        // This can mean a window with state MAXIMIZED *or* SOLO.
        // With a SOLO, skip all synchroniziations!!!
        String requestMaximizedWindowId = null;
        
        if ( requestStates.getMaximizedWindow() != null )
        {
            requestMaximizedWindowId = requestStates.getMaximizedWindow().getId().toString();
            WindowState state = requestStates.getPortletWindowNavigationalState(requestMaximizedWindowId).getWindowState();
            if (JetspeedActions.SOLO_STATE.equals(state))
            {
                // skip *any* synchronizations when in SOLO state
                HttpSession session = context.getRequest().getSession();
                if ( session != null )
                {
                    PortletWindowSessionNavigationalStates sessionStates = (PortletWindowSessionNavigationalStates)session.getAttribute(NavigationalState.NAVSTATE_SESSION_KEY);
                    if ( sessionStates != null )
                    {
                        sessionStates.removeFromCache(context, requestMaximizedWindowId, cache);
                    }
                }
                return;
            }
        }
        
        HttpSession session = context.getRequest().getSession();
        if ( session != null )
        {
            PortletWindowSessionNavigationalStates sessionStates = (PortletWindowSessionNavigationalStates)session.getAttribute(NavigationalState.NAVSTATE_SESSION_KEY);
            if ( sessionStates == null )
            {
                sessionStates = new PortletWindowSessionNavigationalStates(isRenderParameterStateFull());
                session.setAttribute(NavigationalState.NAVSTATE_SESSION_KEY, sessionStates);
            }
            sessionStates.sync(context, context.getPage(), requestStates, cache);
        }
    }
    
    public boolean isNavigationalParameterStateFull()
    {
        return true;
    }

    public boolean isRenderParameterStateFull()
    {
        return false;
    }
}
