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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.portlet.PortletMode;
import javax.portlet.WindowState;
import javax.servlet.http.HttpSession;

import org.apache.jetspeed.container.session.NavigationalState;
import org.apache.jetspeed.container.session.NavigationalStateComponent;
import org.apache.jetspeed.request.RequestContext;
import org.apache.pluto.om.window.PortletWindow;

/**
 * SessionNavigationalState, stores nav state in the session, not on URL
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class SessionNavigationalState
    extends
        AbstractNavigationalState
    implements 
        NavigationalState 
{    
    private static final String STATES_KEY = "org.apache.jetspeed.container.session.impl.states";
    private static final String MODES_KEY = "org.apache.jetspeed.container.session.impl.modes";
    private static final String PSTATES_KEY = "org.apache.jetspeed.container.session.impl.pstates";
    private static final String PMODES_KEY = "org.apache.jetspeed.container.session.impl.pmodes";
    
    private static Object lock = new Object();
    
    private HttpSession session;
    private Map states;
    private Map modes;
    private Map pstates;
    private Map pmodes;
    private List renderParams = null;
    
    public SessionNavigationalState(RequestContext context, NavigationalStateComponent nav)
    {
        super(context, nav);
        session = context.getRequest().getSession();
        states = (Map)session.getAttribute(STATES_KEY);
        if (null == states)
        {
            synchronized (lock)
            {
                states = new HashMap();
                session.setAttribute(STATES_KEY, states);
            }
        }
        
        modes = (Map)session.getAttribute(MODES_KEY);
        if (null == modes)
        {
            synchronized (lock)
            {            
                modes = new HashMap();
                session.setAttribute(MODES_KEY, modes);
            }
        }
        
        pstates = (Map)session.getAttribute(PSTATES_KEY);
        if (null == pstates)
        {
            synchronized (lock)
            {
                pstates = new HashMap();
                session.setAttribute(PSTATES_KEY, pstates);
            }
        }
        
        pmodes = (Map)session.getAttribute(PMODES_KEY);
        if (null == pmodes)
        {
            synchronized (lock)
            {            
                pmodes = new HashMap();
                session.setAttribute(PMODES_KEY, pmodes);
            }
        }               
    }
        
    public WindowState getState(PortletWindow window) 
    {
        WindowState state = (WindowState)states.get(window.getId().toString());
        if (state == null)
        {
            // optimize, no need to add it if its default            
            return WindowState.NORMAL;
        }
        return state;
    }

    public void setState(PortletWindow window, WindowState state) 
    {
        states.put(window.getId().toString(), state);
    }
    
    public PortletMode getMode(PortletWindow window) 
    {
        PortletMode mode = (PortletMode)modes.get(window.getId().toString());
        if (mode == null)
        {
            // optimize, no need to add it if its default
            return PortletMode.VIEW;
        }
        return mode;
    }

    public void setMode(PortletWindow window, PortletMode mode) 
    {
        modes.put(window.getId().toString(), mode);        
    }
    
    public PortletMode getPreviousMode(PortletWindow window)
    {
        PortletMode mode = (PortletMode)pmodes.get(window.getId().toString());
        if (mode == null)
        {
            // optimize, no need to add it if its default
            return PortletMode.VIEW;
        }
        return mode;        
    }
    
    public WindowState getPreviousState(PortletWindow window)
    {
        WindowState state = (WindowState)pstates.get(window.getId().toString());
        if (state == null)
        {
            // optimize, no need to add it if its default            
            return WindowState.NORMAL;
        }
        return state;        
    }
    
    ///////////////////////////////////////////////////////////
    
    public Iterator getRenderParamNames(PortletWindow window)
    {
        if (null != renderParams)
        {
            return renderParams.iterator();
        }

        renderParams = new ArrayList();       
        analyzeNavigationalParameters(context.getRequest().getPathInfo());
        return renderParams.iterator();
    }
    

    private void analyzeNavigationalParameters(String pathInfo)
    {
        if (pathInfo != null)
        {
            StringTokenizer tokenizer = new StringTokenizer(pathInfo, "/.");

            int mode = 0; // 0=navigation, 1=control information
            String name = null;
            while (tokenizer.hasMoreTokens())
            {
                String token = tokenizer.nextToken();
                
                if (isNavigationalParameter(token))
                {
                    mode = 1;
                    name = token;
                }
                else if (mode == 0)
                {
                    if (null == renderParams)
                    {
                        renderParams = new ArrayList();
                    }
                    renderParams.add(token);
                }
                else if (mode == 1)
                {
                    /*
                    if ((isStateFullParameter(name)))
                    {
                        startControlParameter.put(
                            pcp.decodeParameterName(name),
                            pcp.decodeParameterValue(name, token));
                    }
                    else
                    {
                        startStateLessControlParameter.put(
                            pcp.decodeParameterName(name),
                            pcp.decodeParameterValue(name, token));
                    }
                    */
                    mode = 0;
                }
            }
        }
        
    }
    
    
    public String[] getRenderParamValues(PortletWindow window, String paramName)
    {
        return null;
    }

    public PortletWindow getPortletWindowOfAction()
    {
        return null;
    }
    
    public void clearRenderParameters(PortletWindow portletWindow)
    {
    }
    
    public void setAction(PortletWindow window)
    {        
    }
    
    public void setRequestParam(String name, String[] values)
    {
    }
    
    public void setRenderParam(PortletWindow window, String name, String[] values)
    {
    }
    
    public String toString()
    {
        return toString(false);
    }

    public String toString(boolean secure)
    {        
        return "";
    }
    
    public String getBaseURL()
    {
        return "";
    }
    
    
}
