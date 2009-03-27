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
package org.apache.jetspeed.container.providers;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.portlet.PortletMode;
import javax.portlet.PortletSecurityException;
import javax.portlet.ResourceURL;
import javax.portlet.WindowState;

import org.apache.jetspeed.container.PortletWindow;
import org.apache.jetspeed.container.state.MutableNavigationalState;
import org.apache.jetspeed.container.url.PortalURL;
import org.apache.pluto.container.PortletURLProvider;

/**
 * Provides access to the Portal URL manipulation 
 * TODO: 2.2 implement Portlet API 2.0 features: see methods below throwing UnsupportedOperationException
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class PortletURLProviderImpl implements PortletURLProvider
{
    private PortletWindow portletWindow;
    private TYPE type;
    private boolean secure;
    private PortletMode portletMode;
    private WindowState windowState;
    private String cacheLevel;
    private String resourceID;
    private Map<String, String[]> renderParameters;
    private Map<String, String[]> publicRenderParameters;
    private Map<String, List<String>> properties;
    private String actionScopeID;
    private boolean actionScopeRendered;

    private PortalURL url;
    
    public PortletURLProviderImpl(PortalURL url, PortletWindow portletWindow, TYPE type)
    {
        this.url = url;
        this.portletWindow = portletWindow;
        this.type = type;
    }

    public TYPE getType()
    {
        return type;
    }
    
    public void setPortletMode(PortletMode mode)
    {
        this.portletMode = mode;
    }
    
    public PortletMode getPortletMode()
    {
        return portletMode;
    }

    public void setWindowState(WindowState state)
    {
        this.windowState = state;
    }

    public WindowState getWindowState()
    {
        return windowState;
    }

    public void setSecure(boolean secure) throws PortletSecurityException {
        this.secure = secure;
    }
    
    public boolean isSecure()
    {
        return secure;
    }
    
    public Map<String,String[]> getRenderParameters()
    {
        if (renderParameters == null)
        {
            renderParameters = new HashMap<String,String[]>();
        }
        return renderParameters;
    }
    
    public Map<String,String[]> getPublicRenderParameters()
    {
        if (publicRenderParameters == null)
        {
            publicRenderParameters = new HashMap<String,String[]>();
        }
        return publicRenderParameters;
    }
    
    public String getCacheability()
    {
        return cacheLevel;
    }

    public void setCacheability(String cacheLevel)
    {
        this.cacheLevel = cacheLevel;
    }

    public String getResourceID()
    {
        return resourceID;
    }

    public void setResourceID(String resourceID)
    {
        this.resourceID = resourceID;
    }

    public String getActionScopeID()
    {
        return actionScopeID;
    }
    
    public void setActionScopeID(String actionScopeID)
    {
        this.actionScopeID = actionScopeID;
    }
    
    public boolean isActionScopeRendered()
    {
        return actionScopeRendered;
    }
    
    public void setActionScopeRendered(boolean actionScopeRendered)
    {
        this.actionScopeRendered = actionScopeRendered;
    }
    
    public void apply()
    {
        apply(false);
    }

    public String toURL()
    {
        return apply(true);
    }
    
    private String apply(boolean toURL)
    {
        PortalURL.URLType urlType;
        Map<String, String[]> privateRenderParms = null;
        Map<String, String[]> renderParms = null;
        if (TYPE.ACTION == type)
        {
            urlType = PortalURL.URLType.ACTION;
        }
        else if (TYPE.RENDER == type)
        {
            urlType = PortalURL.URLType.RENDER;
        }
        else
        {
            urlType = PortalURL.URLType.RESOURCE;
            if (!ResourceURL.FULL.equals(cacheLevel))
            {
                privateRenderParms = url.getNavigationalState().getParameterMap(portletWindow);
            }
        }
        if (renderParameters != null)
        {
            if (publicRenderParameters == null)
            {
                renderParms = renderParameters;
            }
            else
            {
                renderParms = new HashMap<String,String[]>();
                for (Map.Entry<String,String[]> entry : renderParameters.entrySet())
                {
                    if (!publicRenderParameters.containsKey(entry.getKey()))
                    {
                        renderParms.put(entry.getKey(), entry.getValue());
                    }
                }
            }
        }
        
        if (toURL)
        {
            return url.createPortletURL(portletWindow, renderParms, actionScopeID, actionScopeRendered, cacheLevel, resourceID, privateRenderParms, publicRenderParameters, portletMode, windowState, urlType, secure);
        }
        else
        {
            MutableNavigationalState navState = (MutableNavigationalState)url.getNavigationalState();
            // block possible other concurrent processEvent trying to do the same
            synchronized (navState)
            {
                navState.setTargetted(portletWindow);
                navState.setMode(portletWindow, portletMode);
                navState.setState(portletWindow, windowState);
                navState.setActionScopeId(portletWindow, actionScopeID);
                navState.setActionScopeRendered(portletWindow, actionScopeRendered);
                navState.setCacheLevel(portletWindow, cacheLevel);
                navState.setResourceId(portletWindow, resourceID);
                navState.setPrivateRenderParametersMap(portletWindow, privateRenderParms);                
                if (publicRenderParameters == null)
                {
                    navState.setParametersMap(portletWindow, renderParms);
                }
                else
                {
                    if (renderParameters == null)
                    {
                        navState.setParametersMap(portletWindow, null);
                    }
                    else
                    {
                        for (String key : publicRenderParameters.keySet())
                        {
                            renderParameters.remove(key);
                        }
                        navState.setParametersMap(portletWindow, renderParameters);
                    }
                    navState.setPublicRenderParametersMap(portletWindow, publicRenderParameters);
                }
            }
            return null;
        }
    }

    public void write(Writer out, boolean escapeXML) throws IOException
    {
        String result = toURL();
        if (escapeXML)
        {
            result = result.replaceAll("&", "&amp;");
            result = result.replaceAll("<", "&lt;");
            result = result.replaceAll(">", "&gt;");
            result = result.replaceAll("\'", "&#039;");
            result = result.replaceAll("\"", "&#034;");
        }
        out.write(result);
    }

    public Map<String, List<String>> getProperties()
    {
        if (properties == null)
        {
            properties = new HashMap<String, List<String>>();
        }
        return properties;
    }
}
