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
package org.apache.jetspeed.container.session.impl;

import org.apache.jetspeed.container.session.NavigationalState;
import org.apache.jetspeed.container.session.NavigationalStateContext;
import org.apache.jetspeed.request.RequestContext;
import org.picocontainer.Startable;

/**
 * PathNavigationalState is based on Pluto navigational state.
 * All nav state is stored as path parameters in the URL.
 * This implementation does not currently support persisting navigational state
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class PathNavigationalState implements NavigationalState, Startable
{
    static private final String ACTION = "ac";
    static private final String MODE = "md";
    static private final String PREFIX = "_";
    static private final String PREV_MODE = "pm";
    static private final String PREV_STATE = "ps";
    static private final String RENDER_PARAM = "rp";
    static private final String STATE = "st";
    static private final String KEY_DELIMITER = ":";
    static private final String PORTLET_ID = "pid";

    public PathNavigationalState()
    {   
    }
            
    public void start()
    {
    }
    
    public void stop()
    {
    }
    
    public NavigationalStateContext createContext(RequestContext context)
    {
        // TODO: pool
        return new PathNavigationalStateContext(context);
    }
    
    public void storeContext(RequestContext context, NavigationalStateContext navContext)
    {
        // TODO: implement
    }

    public String getActionKey()
    {
        return ACTION;
    }

    public String getRenderParamKey()
    {
        return RENDER_PARAM;
    }
    
    public String getModeKey()
    {
        return MODE;
    }
    
    public String getPreviousModeKey()
    {
        return PREV_MODE;
    }
    
    
    public String getStateKey()
    {
        return STATE;
    }
    
    public String getPreviousStateKey()
    {
        return PREV_STATE;
    }
    
}
