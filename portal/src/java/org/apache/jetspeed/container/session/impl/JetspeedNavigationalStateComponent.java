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
import java.lang.reflect.Field;
import java.util.StringTokenizer;

import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.container.session.NavigationalState;
import org.apache.jetspeed.container.session.NavigationalStateComponent;
import org.apache.jetspeed.container.url.PortalURL;
import org.apache.jetspeed.request.RequestContext;
import org.picocontainer.Startable;

/**
 * JetspeedNavigationalStateComponent
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class JetspeedNavigationalStateComponent implements NavigationalStateComponent, Startable 
{
    static private final String PREFIX = "_";    
    static private final String ACTION = "ac";
    static private final String MODE = "md";
    static private final String STATE = "st";
    static private final String RENDER_PARAM = "rp";
    static private final String PORTLET_ID = "pid";    
    static private final String PREV_MODE = "pm";
    static private final String PREV_STATE = "ps";
    static private final String KEY_DELIMITER = ":";
    
    private String navClassName = null;
    private String urlClassName = null;
    private Class navClass = null;
    private Class urlClass = null;
    private String navigationKeys;
    private String navigationKeyNames[] = new String[]
    {
            PREFIX, ACTION, MODE, STATE, RENDER_PARAM, PORTLET_ID, PREV_MODE, PREV_STATE, KEY_DELIMITER
    };
    
    private final static Log log = LogFactory.getLog(JetspeedNavigationalStateComponent.class);

    private static final String SESSION_BASED_FIELD = "SESSION_BASED";
    private static final String NAVSTATE_SESSION_KEY = "org.apache.jetspeed.navstate";
    
    
    /**
     * @param navClassName  name of the class implementing Navigational State instances
     * @param urlClassName  name of the class implementing Portal URL instances
     * @param navigationsKeys comma-separated list of navigation keys
     */
    public JetspeedNavigationalStateComponent(String navClassName, String urlClassName, String navigationKeys)
    {
        this.navClassName = navClassName;
        this.urlClassName  = urlClassName;
        this.navigationKeys = navigationKeys;
    }
            
    public void start()
    {        
        StringTokenizer tokenizer = new StringTokenizer(navigationKeys, ", ");
        for (int ix = 0; tokenizer.hasMoreTokens() && ix < NavigationalStateComponent.NAV_MAX; ix++)
        {
            String token = tokenizer.nextToken();
            navigationKeyNames[ix] = token;
        }
    }
    
    public void stop()
    {
    }
        
    public NavigationalState create(RequestContext context)
    {
        NavigationalState state = null;
        try
        {
            boolean sessionBased = false;
            
            if (null == navClass)
            {
                navClass = Class.forName(navClassName);
            }
            
            Field field = navClass.getField(SESSION_BASED_FIELD);
            if (field != null)
            {
                sessionBased = field.getBoolean(null);
            }
            
            HttpSession session = context.getRequest().getSession();
            
            if (sessionBased && session != null)
            {
                state = (NavigationalState)session.getAttribute(NAVSTATE_SESSION_KEY);
            }
            
            if (state == null)
            {
                Constructor constructor = navClass.getConstructor(new Class[] {RequestContext.class, NavigationalStateComponent.class});            
                state = (NavigationalState) constructor.newInstance(new Object[] {context, this});
            }
            
            if (sessionBased && session != null)
            {
                session.setAttribute(NAVSTATE_SESSION_KEY, state);
            }
            
        }
        catch(Exception e)
        {
            String msg = "JetspeedNavigationalStateComponent: Failed to create a Class object for " + navClassName + ": " + e.toString();
            System.out.println(msg);
            log.error(msg);
        }
        return state;
    }

    public PortalURL createURL(RequestContext context)
    {
        PortalURL url = null;
        try
        {
            if (null == urlClass)
            {
                urlClass = Class.forName(urlClassName);
            }

            Constructor constructor = urlClass.getConstructor(new Class[] {RequestContext.class, NavigationalStateComponent.class});
            
            url = (PortalURL) constructor.newInstance(new Object[] {context, this});
            
        }
        catch(Exception e)
        {
            String msg = "JetspeedNavigationalStateComponent: Failed to create a Class object for " + urlClassName + ": " + e.toString();
            System.out.println(msg);
            log.error(msg);
        }
        return url;
    }
    

    public void store(RequestContext context, NavigationalState navContext)
    {
        // TODO: implement
    }

    public void release(NavigationalState navContext)
    {
        
    }
    
    public String getNavigationKey(int key)
    {
        if (key < NavigationalStateComponent.NAV_MAX && key >= 0)
        {
            return navigationKeyNames[key];
        }
        return "";        
    }

}
