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

import javax.portlet.PortletMode;
import javax.portlet.WindowState;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.container.session.NavigationalState;
import org.apache.jetspeed.container.session.NavigationalStateComponent;
import org.apache.jetspeed.container.url.PortalURL;
import org.apache.jetspeed.request.RequestContext;
import org.apache.jetspeed.util.ArgUtil;

/**
 * JetspeedNavigationalStateComponent
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class JetspeedNavigationalStateComponent implements NavigationalStateComponent
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
     * @throws ClassNotFoundException if <code>navClassName</code> or <code>urlClassName</code>
     * do not exist.
     */
    public JetspeedNavigationalStateComponent(String navClassName, String urlClassName, String navigationKeys) throws ClassNotFoundException 
    {
        ArgUtil.assertNotNull(String.class, navClassName, this);
        ArgUtil.assertNotNull(String.class, urlClassName, this);
        ArgUtil.assertNotNull(String.class, navigationKeys, this);
        this.urlClass = Class.forName(urlClassName);
        this.navClass = Class.forName(navClassName);
        this.navClassName = navClassName;
        this.urlClassName  = urlClassName;
        this.navigationKeys = navigationKeys;
        
        StringTokenizer tokenizer = new StringTokenizer(navigationKeys, ", ");
        for (int ix = 0; tokenizer.hasMoreTokens() && ix < NavigationalStateComponent.NAV_MAX; ix++)
        {
            String token = tokenizer.nextToken();
            navigationKeyNames[ix] = token;
        }
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
            else
            {
                state.init(context);
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

    /**
     * Given a navigational key, such as s_14 (state_windowid), return the window id
     * @param key
     * @return The window id from the key
     */
    public String getWindowIdFromKey(String key)
    {
        String delimiter = navigationKeyNames[NavigationalStateComponent.PREFIX];
        StringTokenizer tokenizer = new StringTokenizer(key, delimiter);
        if (!tokenizer.hasMoreTokens())
            return null;
        tokenizer.nextToken(); // navigational directive
        if (!tokenizer.hasMoreTokens())
            return null;        
        return tokenizer.nextToken();                
    }
        
    public WindowState lookupWindowState(String name)
    {
        if (name.equals(WindowState.MAXIMIZED.toString()))
        {
            return WindowState.MAXIMIZED;
        }
        else if (name.equals(WindowState.MAXIMIZED.toString()))
        {
            return WindowState.MINIMIZED;
        }
        else if (name.equals(WindowState.NORMAL.toString()))
        {
            return WindowState.NORMAL;
        }
        return new WindowState(name);
    }

    public PortletMode lookupPortletMode(String name)
    {
        if (name.equals(PortletMode.VIEW.toString()))
        {
            return PortletMode.VIEW;
        }        
        else if (name.equals(PortletMode.EDIT.toString()))
        {
            return PortletMode.EDIT;
        }
        else if (name.equals(PortletMode.HELP.toString()))
        {
            return PortletMode.HELP;
        }        
        return new PortletMode(name);
    }
    
    public boolean hasPortalParameter(HttpServletRequest request, int parameterType)
    {
        String key = getNavigationKey(NavigationalStateComponent.PREFIX) + getNavigationKey(parameterType);
        String pathInfo = request.getPathInfo();
        if (null == pathInfo)
        {
            return false;
        }
        StringTokenizer tokenizer = new StringTokenizer(pathInfo, "/");
        StringBuffer path = new StringBuffer();
        boolean isName = true;
        while (tokenizer.hasMoreTokens())
        {
            String token = tokenizer.nextToken();
            if (isName)
            {
                isName = false;
            }
            else
            {
                isName = true;
                continue;
            }
            if (token.startsWith(key))
            {
                return true;            
            }
        }
        return false;
    }
}
