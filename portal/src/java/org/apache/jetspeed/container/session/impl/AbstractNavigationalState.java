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
package org.apache.jetspeed.container.session.impl;

import org.apache.jetspeed.container.session.NavigationalStateComponent;
import org.apache.jetspeed.request.RequestContext;
import org.apache.jetspeed.util.ArgUtil;

/**
 * BaseNavigationalState
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public abstract class AbstractNavigationalState 
{
    protected RequestContext context;
    protected NavigationalStateComponent nav;
    
    public static final String HTTP = "http";
    public static final String HTTPS = "https";
        
    public AbstractNavigationalState(RequestContext context, NavigationalStateComponent nav)
    {
        this.context = context;
        this.nav = nav;        
    }
        
    public void init(RequestContext context)
    {
        ArgUtil.assertNotNull(RequestContext.class, context, this, "init()");
        this.context = context;
    }
    
    public boolean isNavigationalParameter(String token)
    {
        return token.startsWith(nav.getNavigationKey(NavigationalStateComponent.PREFIX));
    }
    
    public boolean isStateFullParameter(String param)
    {
        if (isNavigationalParameter(param))
        {
            String prefix = nav.getNavigationKey(NavigationalStateComponent.PREFIX);            
            if ((param.startsWith(prefix + nav.getNavigationKey(NavigationalStateComponent.MODE)))
                || (param.startsWith(prefix + nav.getNavigationKey(NavigationalStateComponent.PREV_MODE)))
                || (param.startsWith(prefix + nav.getNavigationKey(NavigationalStateComponent.STATE)))
                || (param.startsWith(prefix + nav.getNavigationKey(NavigationalStateComponent.PREV_STATE)))
                || (param.startsWith(prefix + nav.getNavigationKey(NavigationalStateComponent.RENDER_PARAM))))
            {
                return true;
            }
        }
        return false;
    }
    
    
    
}
