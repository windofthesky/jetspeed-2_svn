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

import java.lang.reflect.Constructor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.container.session.NavigationalState;
import org.apache.jetspeed.container.session.NavigationalStateComponent;
import org.apache.jetspeed.request.RequestContext;

/**
 * JetspeedNavigationalStateComponent
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class JetspeedNavigationalStateComponent implements NavigationalStateComponent 
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
    
    private String contextClassName = null;
    private Class contextClass = null;
    private final static Log log = LogFactory.getLog(JetspeedNavigationalStateComponent.class);
    
    public JetspeedNavigationalStateComponent(String contextClassName)
    {
        this.contextClassName = contextClassName;
    }
            
    public void start()
    {
    }
    
    public void stop()
    {
    }
    
    public NavigationalState create(RequestContext context)
    {
        NavigationalState state = null;
        try
        {
            if (null == contextClass)
            {
                contextClass = Class.forName(contextClassName);
            }

            // TODO: we could use a pooled object implementation here
            Constructor constructor = contextClass.getConstructor(new Class[] {RequestContext.class});
            
            state = (NavigationalState) constructor.newInstance(new Object[] {context});
            
        }
        catch(Exception e)
        {
            String msg = "RequestContextFactory: Failed to create a Class object for RequestContext: " + e.toString();
            log.error(msg);
        }
        return state;
    }
    
    public void store(RequestContext context, NavigationalState navContext)
    {
        // TODO: implement
    }

    public void release(NavigationalState navContext)
    {
        
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
