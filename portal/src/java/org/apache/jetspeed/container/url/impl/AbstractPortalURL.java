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

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;

import javax.portlet.PortletMode;
import javax.portlet.WindowState;

import org.apache.jetspeed.container.session.NavigationalStateComponent;
import org.apache.jetspeed.container.url.*;
import org.apache.jetspeed.container.url.PortalURL;
import org.apache.jetspeed.request.RequestContext;
import org.apache.pluto.om.window.PortletWindow;

/**
 * PortalURL defines the interface for manipulating Jetspeed Portal URLs.
 * These URLs are used internally by the portal and are not available to
 * Portlet Applications. This class provids common implementation 
 * for all concrete Portal URL implementations.
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public abstract class AbstractPortalURL implements PortalURL
{
    protected String serverName;
    protected String serverScheme;
    protected String contextPath;
    protected String basePath;
    protected int serverPort;
    protected NavigationalStateComponent nsc;
    protected RequestContext context;
    protected Map requestParameters = new HashMap();
    protected boolean analyzed = false;
    protected boolean secure;
    protected List startGlobalNavigation = new ArrayList();
    protected List startLocalNavigation = new ArrayList();
    protected HashMap startControlParameter = new HashMap();
    protected HashMap startStateLessControlParameter = new HashMap();
    protected PortalControlParameter pcp;
    
    
    public AbstractPortalURL(RequestContext context, NavigationalStateComponent nsc)
    {
        this.context = context;
        this.nsc = nsc;        
        init(context);
        pcp = new PortalControlParameterImpl(this, nsc);
        pcp.init();
    }
    
    public void init(RequestContext context)
    {
        if (null != context.getRequest())
        {
            this.serverName = context.getRequest().getServerName();
            this.serverPort = context.getRequest().getServerPort();
            this.serverScheme = context.getRequest().getScheme();
            this.contextPath = context.getRequest().getContextPath();
            if (contextPath == null)
            {
                contextPath = "";
            }
            String servletPath = context.getRequest().getServletPath();
            if (servletPath == null)
            {
                servletPath = "";
            }
            this.basePath = contextPath + servletPath;
            
            if (basePath == null)
            {
                basePath = "";
            }
                        
        }    
    }

    public String getBaseURL()
    {
        return getBaseURLBuffer().toString();
    }
    
    protected StringBuffer getBaseURLBuffer()
    {
        StringBuffer buffer = new StringBuffer();
        buffer.append(this.serverScheme);
        buffer.append("://");
        buffer.append(this.serverName);
        if ((this.serverScheme.equals(PortalURL.HTTP) && this.serverPort != 80)
            || (this.serverScheme.equals(PortalURL.HTTPS) && this.serverPort != 443))
        {
            buffer.append(":");
            buffer.append(this.serverPort);
        }
        return buffer;
    }
    
    public boolean isNavigationalParameter(String token)
    {
        return token.startsWith(nsc.getNavigationKey(NavigationalStateComponent.PREFIX));
    }    
    
    public boolean isRenderParameter(String token)
    {
        String prefix = nsc.getNavigationKey(NavigationalStateComponent.PREFIX);
        if ( token != null && (token.startsWith(prefix + nsc.getNavigationKey(NavigationalStateComponent.RENDER_PARAM))))
        {
            return true;
        }
        return false;    
    }
    
    public String getRenderParamKey(PortletWindow window)
    {
        return nsc.getNavigationKey(NavigationalStateComponent.RENDER_PARAM) + "_" + window.getId().toString();
    }
    
    public void setRequestParam(String name, String[] values)
    {
        requestParameters.put(name, values);
    }

    public Map getRequestParameters()
    {
        return requestParameters;
    }
        
    public String getRequestParametersAsString()
    {
        if (requestParameters != null)
        {
            StringBuffer result = new StringBuffer(100);
            Iterator iterator = requestParameters.keySet().iterator();
            boolean hasNext = iterator.hasNext();
            if (hasNext)
            {
                result.append("?");
            }

            while (hasNext)
            {

                String name = (String) iterator.next();
                Object value = requestParameters.get(name);
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
    
    
    
    public String getStateKey(PortletWindow window)
    {
        return nsc.getNavigationKey(NavigationalStateComponent.STATE) + "_" + window.getId().toString();
    }
    
    public String getModeKey(PortletWindow window)
    {
        return nsc.getNavigationKey(NavigationalStateComponent.MODE) + "_" + window.getId().toString();
    }
    
    public String getActionKey(PortletWindow window)
    {        
        return nsc.getNavigationKey(NavigationalStateComponent.ACTION) + "_" + window.getId().toString();
    }
    
    public String getPrevModeKey(PortletWindow window)
    {
        return nsc.getNavigationKey(NavigationalStateComponent.PREV_MODE) + "_" + window.getId().toString();
    }
    
    public String getPrevStateKey(PortletWindow window)
    {
        return nsc.getNavigationKey(NavigationalStateComponent.PREV_STATE) + "_" + window.getId().toString();
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
                        

        if (context.getRequest().getPathInfo() != null)
        {
            String pathInfo = new String(context.getRequest().getPathInfo());            
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
                    startGlobalNavigation.add(token);
                }
                else if (mode == 1)
                {
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
                    mode = 0;
                }
            }
        }
        analyzed = true;

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

        String params = getRequestParametersAsString();
        if (params.length() > 0)
        {
            buffer.append(params);
        }

        String local = getLocalNavigationAsString();
        if (local.length() > 0)
        {
            buffer.append("#");
            buffer.append(local);
        }

        String finalUrl = buffer.toString();
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
    
    public abstract boolean isStateFullParameter(String tag);
    
    public PortalControlParameter getControlParameters()
    {
        return this.pcp;
    }
    
}
