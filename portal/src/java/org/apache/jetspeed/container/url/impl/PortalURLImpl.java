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

import javax.servlet.http.HttpServletRequest;

import org.apache.pluto.om.window.PortletWindow;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.Jetspeed;
import org.apache.jetspeed.container.session.impl.*;
import org.apache.jetspeed.container.url.*;
import org.apache.jetspeed.request.RequestContext;
import org.apache.jetspeed.request.RequestContextComponent;

/**
 * As part of its content, a portlet may need to create URLs that reference the portlet itself.
 * For example, when a user acts on a URL that references a portlet (i.e., by clicking a link
 * or submitting a form) the result is a new client request to the portal targeted to the portlet.
 * Those URLs are called portlet URLs.
 * 
 * NOTE: parts of this code was borrowed from Pluto's portal implementation.
 *
 * @author <a href="mailto:david@bluesunrise.com">David Sean Taylor</a>
 * 
 * @version $Id$
 */
public class PortalURLImpl implements PortalURL
{
    private static final Log log = LogFactory.getLog(PortalURLImpl.class);

    private RequestContext context;
    private List startGlobalNavigation = new ArrayList();
    private List startLocalNavigation = new ArrayList();
    private HashMap startControlParameter = new HashMap();
    private HashMap startStateLessControlParameter = new HashMap();
    private boolean analyzed = false;
    private boolean secure;

    private String serverName;
    private String serverScheme;
    private String contextPath;
    private String basePath;
    private int serverPort;

    /**
     * Creates and URL pointing to the home of the portal
     * 
     * @param env     the portal environment
     */
    public PortalURLImpl(RequestContext context)
    {
        this.context = context;
        init(context);
    }

    public void init(RequestContext context)
    {
        if (null != context.getRequest())
        {
            this.serverName = context.getRequest().getServerName();
            this.serverPort = context.getRequest().getServerPort();
            this.serverScheme = context.getRequest().getScheme();
            this.contextPath = context.getRequest().getContextPath();
            this.basePath = contextPath + context.getRequest().getServletPath();
        }
    }

    /**
     * Creates and URL pointing to the home of the portal
     * 
     * @param request     the servlet request
     */
    public PortalURLImpl(HttpServletRequest request)
    {
        // TODO: assemble this
        RequestContextComponent rcc = (RequestContextComponent)Jetspeed.getComponentManager().getComponent(RequestContextComponent.class);
        this.context = rcc.getRequestContext(request);
        init(context);
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
            result.append(PortalControlParameter.encodeParameter(name));
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
        return toString(null, null);
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
                if (PortalControlParameter.isControlParameter(token))
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
                    if ((PortalControlParameter.isStateFullParameter(name)))
                    {
                        startControlParameter.put(
                            PortalControlParameter.decodeParameterName(name),
                            PortalControlParameter.decodeParameterValue(name, token));
                    }
                    else
                    {
                        startStateLessControlParameter.put(
                            PortalControlParameter.decodeParameterName(name),
                            PortalControlParameter.decodeParameterValue(name, token));
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
            PortalControlParameter.encodeRenderParamName(portletWindow, name),
            PortalControlParameter.encodeRenderParamValues(values));

    }

    public void clearRenderParameters(PortletWindow portletWindow)
    {
        String prefix = PortalControlParameter.getRenderParamKey(portletWindow);
        Iterator keyIterator = startControlParameter.keySet().iterator();

        while (keyIterator.hasNext())
        {
            String name = (String) keyIterator.next();
            if (name.startsWith(prefix))
            {
                keyIterator.remove();
            }
        }
    }

    public String getBaseURL()
    {
        return getBaseURLBuffer().toString();
    }

    public String getContext()
    {
        StringBuffer result = getBaseURLBuffer();
        result.append(this.contextPath);
        return result.toString();
    }

    private StringBuffer getBaseURLBuffer()
    {
        StringBuffer buffer = new StringBuffer();
        buffer.append(this.serverScheme);
        buffer.append("://");
        buffer.append(this.serverName);
        if ((this.serverScheme.equals(HTTP) && this.serverPort != 80)
            || (this.serverScheme.equals(HTTPS) && this.serverPort != 443))
        {
            buffer.append(":");
            buffer.append(this.serverPort);
        }
        return buffer;
    }

}
