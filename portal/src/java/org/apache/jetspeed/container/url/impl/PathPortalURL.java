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

    private List startGlobalNavigation = new ArrayList();
    private List startLocalNavigation = new ArrayList();
    private HashMap startControlParameter = new HashMap();
    private HashMap startStateLessControlParameter = new HashMap();
    private boolean analyzed = false;
    private boolean secure;
    private PortalControlParameter pcp;


    public PathPortalURL(RequestContext context, NavigationalStateComponent nsc)
    {        
        super(context, nsc);
        pcp = new PortalControlParameter(this, nsc);
        pcp.init();
    }

    public PortalControlParameter getControlParameter()
    {
        return this.pcp;
    }
        
    /**
     * Adds a navigational information pointing to a portal part, e.g. PageGroups
     * or Pages
     * 
     * @param nav    the string pointing to a portal part
     */
    public void addGlobalNavigation(String nav)
    {
        startGlobalNavigation.add(nav);
    }

    /**
     * Sets the local navigation. Because the local navigation is always handled
     * by the Browser, therefore the local navigation cleared.
     */
    public void setLocalNavigation()
    {
        startLocalNavigation = new ArrayList();
    }

    /**
     * Adds a navigational information pointing to a local portal part inside
     * of a global portal part, e.g. a portlet on a page
     * 
     * @param nav    the string pointing to a local portal part
     */
    public void addLocalNavigation(String nav)
    {
        startLocalNavigation.add(nav);
    }

    /**
     * Returns true if the given string is part of the global navigation of this URL
     * 
     * @param nav    the string to check
     * @return true, if the string is part of the navigation
     */
    public boolean isPartOfGlobalNavigation(String nav)
    {
        return startGlobalNavigation.contains(nav);
    }

    /**
     * Returns true if the given string is part of the local navigation of this URL
     * 
     * @param nav    the string to check
     * @return true, if the string is part of the navigation
     */
    public boolean isPartOfLocalNavigation(String nav)
    {
        return startLocalNavigation.contains(nav);
    }

    public String getGlobalNavigationAsString()
    {
        StringBuffer result = new StringBuffer(200);
        Iterator iterator = startGlobalNavigation.iterator();
        if (iterator.hasNext())
        {
            result.append((String) iterator.next());
            while (iterator.hasNext())
            {
                result.append("/");
                result.append((String) iterator.next());
            }
        }
        return result.toString();
    }

    public String getLocalNavigationAsString()
    {
        StringBuffer result = new StringBuffer(30);
        Iterator iterator = startLocalNavigation.iterator();
        if (iterator.hasNext())
        {
            result.append((String) iterator.next());
            while (iterator.hasNext())
            {
                result.append(".");
                result.append((String) iterator.next());
            }
        }
        return result.toString();
    }

    public String getControlParameterAsString(PortalControlParameter controlParam)
    {
        Map stateFullParams = startControlParameter;
        Map stateLessParams = null;
        if (controlParam != null)
        {
            stateFullParams = controlParam.getStateFullControlParameter();
            stateLessParams = controlParam.getStateLessControlParameter();
        }

        StringBuffer result = new StringBuffer(100);
        Iterator iterator = stateFullParams.keySet().iterator();
        while (iterator.hasNext())
        {
            if (iterator.hasNext())
                result.append("/");
            String name = (String) iterator.next();
            result.append(pcp.encodeParameter(name));
            result.append("/");
            result.append((String) stateFullParams.get(name));
        }

        return result.toString();
    }

    public String getRequestParameterAsString(PortalControlParameter controlParam)
    {
        if (controlParam != null)
        {
            Map requestParams = controlParam.getRequestParameter();

            StringBuffer result = new StringBuffer(100);
            Iterator iterator = requestParams.keySet().iterator();
            boolean hasNext = iterator.hasNext();
            if (hasNext)
            {
                result.append("?");
            }

            while (hasNext)
            {

                String name = (String) iterator.next();
                Object value = requestParams.get(name);
                String[] values = value instanceof String ? new String[] {(String) value }
                : (String[]) value;

                int i;

                result.append(name);
                result.append("=");
                result.append(values[0]);
                for (i = 1; i < values.length; i++)
                {
                    result.append("&");
                    result.append(name);
                    result.append("=");
                    result.append(values[i]);
                };

                hasNext = iterator.hasNext();
                if (hasNext)
                    result.append("&");
            }

            return result.toString();
        }
        return "";
    }

    
    public String toString()
    {
        return toString(false);
    }

    public String toString(boolean secure)
    {        
        return toString(pcp, new Boolean(secure));
    }
    
    public String toString(PortalControlParameter controlParam, Boolean p_secure)
    {
        StringBuffer buffer = getBaseURLBuffer();
        buffer.append(this.basePath);

        String global = getGlobalNavigationAsString();
        if (global.length() > 0)
        {
            buffer.append("/");
            buffer.append(global);
        }

        String control = getControlParameterAsString(controlParam);
        if (control.length() > 0)
        {
            buffer.append(control);
        }

        String requestParam = getRequestParameterAsString(controlParam);
        if (requestParam.length() > 0)
        {
            buffer.append(requestParam);
        }

        String local = getLocalNavigationAsString();
        if (local.length() > 0)
        {
            buffer.append("#");
            buffer.append(local);
        }

        String finalUrl = buffer.toString();
        log.debug("PortalUrl before encode: " + finalUrl);
        return context.getResponse().encodeURL(finalUrl);
    }

    Map getClonedStateFullControlParameter()
    {
        analyzeRequestInformation();
        return (Map) startControlParameter.clone();
    }

    Map getClonedStateLessControlParameter()
    {
        analyzeRequestInformation();
        return (Map) startStateLessControlParameter.clone();
    }

    public void analyzeControlInformation(PortalControlParameter control)
    {
        startControlParameter = (HashMap) control.getStateFullControlParameter();
        startStateLessControlParameter = (HashMap) control.getStateLessControlParameter();
    }

    void analyzeRequestInformation()
    {
        if (analyzed)
            return;

        startGlobalNavigation = new ArrayList();
        startLocalNavigation = new ArrayList();
        startControlParameter = new HashMap();
        startStateLessControlParameter = new HashMap();

        // check the complete pathInfo for
        // * navigational information
        // * control information
        String pathInfo = context.getRequest().getPathInfo();

        if (pathInfo != null)
        {
            StringTokenizer tokenizer = new StringTokenizer(pathInfo, "/.");

            int mode = 0; // 0=navigation, 1=control information
            String name = null;
            while (tokenizer.hasMoreTokens())
            {
                String token = tokenizer.nextToken();
                
                if (pcp.isNavigationalParameter(token))
                {
                    mode = 1;
                    name = token;
                }
                else if (mode == 0)
                {
                    startGlobalNavigation.add(token);
                }
                else if (mode == 1)
                {
                    if ((pcp.isStateFullParameter(name)))
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
                    mode = 0;
                }
            }
        }
        analyzed = true;

    }

    public void setRenderParameter(PortletWindow portletWindow, String name, String[] values)
    {
        startControlParameter.put(
            pcp.encodeRenderParamName(portletWindow, name),
            pcp.encodeRenderParamValues(values));

    }

    public String getContext()
    {
        StringBuffer result = getBaseURLBuffer();
        result.append(this.contextPath);
        return result.toString();
    }

    public Iterator getRenderParamNames(PortletWindow window)
    {
        return pcp.getRenderParamNames(window);
    }
    
    public String[] getRenderParamValues(PortletWindow window, String paramName)
    {
        return pcp.getRenderParamValues(window, paramName);
    }

    public PortletWindow getPortletWindowOfAction()
    {
        return pcp.getPortletWindowOfAction();
    }
    
    public void clearRenderParameters(PortletWindow portletWindow)
    {
        pcp.clearRenderParameters(portletWindow);
    }
        
    public void setAction(PortletWindow window)
    {
        pcp.setAction(window);
    }
    
    public void setRequestParam(String name, String[] values)
    {
        pcp.setRequestParam(name, values);
    }
    
    public void setRenderParam(PortletWindow window, String name, String[] values)
    {
        pcp.setRenderParam(window, name, values);
    }
    
    public void setMode(PortletWindow window, PortletMode mode) 
    {
        pcp.setMode(window, mode);
    }
    
    public void setState(PortletWindow window, WindowState state) 
    {
        pcp.setState(window, state);
    }
        
    public PortletMode getPortletMode(PortletWindow window)
    {
        return pcp.getMode(window);
    }
    
    public WindowState getState(PortletWindow window)
    {
        return pcp.getState(window);
    }
    
    public PortletMode getMode(PortletWindow window)
    {
        return pcp.getMode(window);
    }
    
    public PortletMode getPreviousMode(PortletWindow window)
    {
        return pcp.getPrevMode(window);
    }
    
    public WindowState getPreviousState(PortletWindow window)
    {
        return pcp.getPrevState(window);
    }
        
}
