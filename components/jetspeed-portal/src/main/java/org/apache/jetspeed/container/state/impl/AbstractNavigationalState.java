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
package org.apache.jetspeed.container.state.impl;

import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.portlet.PortletMode;
import javax.portlet.WindowState;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.namespace.QName;

import org.apache.jetspeed.JetspeedActions;
import org.apache.jetspeed.PortalReservedParameters;
import org.apache.jetspeed.aggregator.PortletContent;
import org.apache.jetspeed.cache.ContentCacheKey;
import org.apache.jetspeed.cache.JetspeedContentCache;
import org.apache.jetspeed.container.state.MutableNavigationalState;
import org.apache.jetspeed.container.state.NavigationalState;
import org.apache.jetspeed.container.url.PortalURL;
import org.apache.jetspeed.om.portlet.PortletApplication;
import org.apache.jetspeed.request.RequestContext;
import org.apache.jetspeed.container.PortletWindow;

/**
 * BaseNavigationalState
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public abstract class AbstractNavigationalState implements MutableNavigationalState
{
    private NavigationalStateCodec codec;
    private PortletWindowRequestNavigationalStates requestStates;
    protected JetspeedContentCache cache;
    protected JetspeedContentCache decorationCache;
    protected Map<String,String[]> requestParameterMap;
    
    public AbstractNavigationalState(NavigationalStateCodec codec, JetspeedContentCache cache)
    {
        this(codec, cache, null);
    }

    public AbstractNavigationalState(NavigationalStateCodec codec, JetspeedContentCache cache, JetspeedContentCache decorationCache)
    {
        this.codec = codec;
        this.cache = cache;
        this.decorationCache = decorationCache;
    }
    
    public void init(String encodedState, String characterEncoding)
    throws UnsupportedEncodingException
    {
        if ( requestStates == null )
        {
            requestStates = codec.decode(encodedState, characterEncoding);
        }
    }
    
    private static boolean changedParameterValues(String[] requestValues, String[] sessionValues)
    {
        if ((requestValues == null) || (sessionValues == null) || (requestValues.length != sessionValues.length))
        {
            return true;
        }
        for (int ix = 0; ix < requestValues.length; ix++)
        {
            if (!requestValues[ix].equals(sessionValues[ix]))
            {
                return true;
            }
        }
        return false;
    }

    private static void removeFromCache(RequestContext context, String id, JetspeedContentCache cache)
    {
        if (cache != null)
        {
            ContentCacheKey cacheKey = cache.createCacheKey(context, id);
            if (cache.isKeyInCache(cacheKey))
            {
                cache.remove(cacheKey);
            }
            cache.invalidate(context);
        }
    }
    
    protected boolean resolvePortletWindows(RequestContext requestContext)
    {
        boolean targetResolved = true;
        for (Iterator<Map.Entry<String,PortletWindowRequestNavigationalState>> entryIter = requestStates.getPortletWindowRequestNavigationalStates().entrySet().iterator(); entryIter.hasNext(); )
        {
            Map.Entry<String,PortletWindowRequestNavigationalState> entry = entryIter.next();
            PortletWindow window = requestContext.resolvePortletWindow(entry.getKey());
            if (window == null || !window.isValid())
            {
                entryIter.remove();
                if (requestStates.getTargetWindowId() != null && entry.getKey().equals(requestStates.getTargetWindowId()))
                {
                    requestStates.setTargetWindowId(null);
                    if (PortalURL.URLType.RENDER != requestStates.getURLType())
                    {
                        targetResolved = false;
                    }
                }
            }
            else
            {
                entry.getValue().setPortletDefinition(window.getPortletDefinition());
                if (requestStates.getTargetWindowId() != null && entry.getKey().equals(requestStates.getTargetWindowId()))
                {
                    if (PortalURL.URLType.ACTION == requestStates.getURLType())
                    {
                        requestStates.setActionWindow(window);
                    }
                    else if (PortalURL.URLType.RESOURCE == requestStates.getURLType())
                    {
                        requestStates.setResourceWindow(window);
                    }
                }
                WindowState windowState = entry.getValue().getWindowState();
                if (windowState != null && (windowState.equals(WindowState.MAXIMIZED) || windowState.equals(JetspeedActions.SOLO_STATE)))
                {
                    if (requestStates.getMaximizedWindow() == null)
                    {
                        requestStates.setMaximizedWindow(window);
                    }
                    else
                    {
                        // multiple maximized windows not possible: corrupted or hacked url?
                        entry.getValue().setWindowState(null);
                    }
                }
            }
        }
        return targetResolved;
    }
    
    @SuppressWarnings("unchecked")
    protected void resolveRequestParameterMap(RequestContext context)
    {
        HttpServletRequest request = context.getRequest();
        requestParameterMap = Collections.unmodifiableMap(new HashMap<String,String[]>(request.getParameterMap()));
        String encoding = (String)request.getAttribute(PortalReservedParameters.PREFERED_CHARACTERENCODING_ATTRIBUTE);
        if (encoding != null && request.getAttribute(PortalReservedParameters.PARAMETER_ALREADY_DECODED_ATTRIBUTE) == null)
        {
            request.setAttribute(PortalReservedParameters.PARAMETER_ALREADY_DECODED_ATTRIBUTE,new Boolean(true));
            for (Map.Entry<String,String[]> entry : requestParameterMap.entrySet())
            {
                String[] paramValues = entry.getValue();
                for (int i = 0; i < paramValues.length; i++)
                {
                    try
                    {
                        paramValues[i] = new String(paramValues[i].getBytes("ISO-8859-1"), encoding);
                    }
                    catch (UnsupportedEncodingException e)
                    {
                        ;
                    }
                }
            }
        }
    }
    
    protected void resolvePublicParametersMap()
    {
        HashMap<QName, String[]> map = null;
        for (PortletWindowRequestNavigationalState state : requestStates.getPortletWindowRequestNavigationalStates().values())
        {
            if (state.getPublicRenderParametersMap() != null)
            {
                state.resolvePublicRenderParametersMapping();
                for (Iterator<Map.Entry<String, String[]>> iter = state.getPublicRenderParametersMap().entrySet().iterator(); iter.hasNext(); )
                {
                    Map.Entry<String, String[]> entry = iter.next();
                    QName qname = state.getPublicRenderParameterQNameByIdentifier(entry.getKey());
                    if (qname != null)
                    {
                        if (map == null)
                        {
                            map = new HashMap<QName, String[]>();
                        }
                        map.put(qname, entry.getValue());
                    }
                    else
                    {
                        iter.remove();
                    }
                }
            }
        }
        requestStates.setPublicRenderParametersMap(map);
    }

    @SuppressWarnings("unchecked")
    protected void syncPublicRequestParameters(RequestContext context, boolean transientNavState)
    {
        Map<QName, ValuesAndWindowUsage> publicRenderParametersMap = null;
        
        // sync public render parameters if not a transient NavState request
        if (!transientNavState && requestStates.getPublicRenderParametersMap() != null)
        {
            HttpSession session = context.getRequest().getSession(true);
            publicRenderParametersMap = (Map<QName, ValuesAndWindowUsage>)session.getAttribute(NavigationalState.PRP_SESSION_KEY);
            
            if (publicRenderParametersMap == null)
            {
                publicRenderParametersMap = Collections.synchronizedMap(new HashMap<QName, ValuesAndWindowUsage>());
                session.setAttribute(NavigationalState.PRP_SESSION_KEY, publicRenderParametersMap);
            }

            for (Iterator<Map.Entry<QName, String[]>> iter = requestStates.getPublicRenderParametersMap().entrySet()
                                                                          .iterator(); iter.hasNext();)
            {
                Map.Entry<QName, String[]> entry = iter.next();
                ValuesAndWindowUsage vawu = publicRenderParametersMap.get(entry.getKey());
                if (vawu == null || changedParameterValues(entry.getValue(), vawu.getValues()))
                {
                    if (vawu != null && vawu.getWindowIds() != null)
                    {
                        for (String windowId : vawu.getWindowIds())
                        {
                            removeFromCache(context, windowId, cache);
                        }
                        for (String pageId : vawu.getPageIds())
                        {
                            removeFromCache(context, pageId, decorationCache);
                        }
                    }
                    if (entry.getValue() == null)
                    {
                        iter.remove();
                        publicRenderParametersMap.remove(entry.getKey());
                    }
                    else if (vawu == null)
                    {
                        publicRenderParametersMap.put(entry.getKey(), new ValuesAndWindowUsage(entry.getValue()));
                    }
                    else
                    {
                        vawu.setValues(entry.getValue());
                    }
                }
            }
        }
        
        if (publicRenderParametersMap == null)
        {
            HttpSession session = context.getRequest().getSession(false);
            if (session != null)
            {
                publicRenderParametersMap = (Map<QName, ValuesAndWindowUsage>)session.getAttribute(NavigationalState.PRP_SESSION_KEY);
            }
        }
        
        if (publicRenderParametersMap != null && !publicRenderParametersMap.isEmpty())
        {
            Map<QName, String[]> map = requestStates.getPublicRenderParametersMap();
            if (map == null)
            {
                map = new HashMap<QName, String[]>();
            }
            for (Map.Entry<QName, ValuesAndWindowUsage> entry : publicRenderParametersMap.entrySet())
            {
                if (!map.containsKey(entry.getKey()))
                {
                    map.put(entry.getKey(), entry.getValue().getValues());
                }
            }
            requestStates.setPublicRenderParametersMap(map);
        }
    }
 
    
    protected void resetRequestPortletWindowPublicRenderParameters()
    {
        for (PortletWindowRequestNavigationalState state : requestStates.getPortletWindowRequestNavigationalStates().values())
        {
            state.setPublicRenderParametersMap(null);
        }
    }

    protected PortletWindowRequestNavigationalStates getPortletWindowRequestNavigationalStates()
    {
        return requestStates;
    }
    
    public WindowState getMappedState(String windowId)
    {
        WindowState windowState = null;
        PortletWindowRequestNavigationalState state = requestStates.getPortletWindowNavigationalState(windowId);
        if (state != null)
        {
            windowState = state.getWindowState();
        }
        return windowState != null ? windowState : WindowState.NORMAL;
    }

    /**
     * @deprecated
     */
    public WindowState getState(String windowId)
    {
        return getMappedState(windowId);
    }

    public WindowState getState(PortletWindow window)
    {
        WindowState state = getMappedState(window.getId().toString());
        if (state != null && !JetspeedActions.getStandardWindowStates().contains(state))
        {
            PortletApplication pa = window.getPortletDefinition().getApplication();
            state = pa.getCustomWindowState(state);
        }
        return state;
    }

    public WindowState getMappedState(PortletWindow window)
    {
        return getMappedState(window.getId().toString());
    }

    public void removeState(PortletWindow window)
    {
        requestStates.removePortletWindowNavigationalState(window.getId().toString());
    }
    
    public void setState(PortletWindow window, WindowState windowState)
    {
        if (window.isValid())
        {
            if ( windowState != null )
            {
                if (!JetspeedActions.getStandardWindowStates().contains(windowState))
                {
                    PortletApplication pa = window.getPortletDefinition().getApplication();
                    windowState = pa.getMappedWindowState(windowState);
                }
            }
            String windowId = window.getId().toString();
            PortletWindowRequestNavigationalState state = requestStates.getPortletWindowNavigationalState(windowId);
            if (state != null)
            {
                if ((state.getWindowState() == null && windowState != null) || 
                    (windowState == null && state.getWindowState() != null) || 
                    (windowState != null && !state.getWindowState().equals(windowState)))
                {
                    state.setWindowState(windowState);
                }
            }
            else
            {
                state = new PortletWindowRequestNavigationalState(windowId);
                state.setPortletDefinition(window.getPortletDefinition());
                state.resolveActionScopedRequestAttributes();
                requestStates.addPortletWindowNavigationalState(windowId, state);
                state.setWindowState(windowState);
            }
            if (windowState != null && windowState.equals(WindowState.MAXIMIZED))
            {
                requestStates.setMaximizedWindow(window);
            }
        }
    }

    public PortletMode getMappedMode(String windowId)
    {
        PortletMode portletMode = null;
        PortletWindowRequestNavigationalState state = requestStates.getPortletWindowNavigationalState(windowId);
        if (state != null)
        {
            portletMode = state.getPortletMode();
        }
        return portletMode != null ? portletMode : PortletMode.VIEW;
    }
    
    /**
     * @deprecated
     */
    public PortletMode getMode(String windowId)
    {
        return getMappedMode(windowId);
    }
    
    public PortletMode getMode(PortletWindow window)
    {
        PortletMode mode = getMappedMode(window.getId().toString());
        if (mode != null && !JetspeedActions.getStandardPortletModes().contains(mode))
        {
            PortletApplication pa = window.getPortletDefinition().getApplication();
            mode = pa.getCustomPortletMode(mode);
        }
        return mode;
    }

    public PortletMode getMappedMode(PortletWindow window)
    {
        return getMappedMode(window.getId().toString());
    }

    public void setMode(PortletWindow window, PortletMode portletMode)
    {
        if (window.isValid())
        {
            if ( portletMode != null )
            {
                if (!JetspeedActions.getStandardPortletModes().contains(portletMode))
                {
                    PortletApplication pa = window.getPortletDefinition().getApplication();
                    portletMode = pa.getMappedPortletMode(portletMode);
                }
            }
            String windowId = window.getId().toString();
            PortletWindowRequestNavigationalState state = requestStates.getPortletWindowNavigationalState(windowId);
            if (state != null)
            {
                if ((state.getPortletMode() == null && portletMode != null) || 
                    (portletMode == null && state.getPortletMode() != null) || 
                    (portletMode != null && !state.getPortletMode().equals(portletMode)))
                {
                    state.setPortletMode(portletMode);
                }
            }
            else
            {
                state = new PortletWindowRequestNavigationalState(windowId);
                state.setPortletDefinition(window.getPortletDefinition());
                state.resolveActionScopedRequestAttributes();
                requestStates.addPortletWindowNavigationalState(windowId, state);
                state.setPortletMode(portletMode);
            }
        }
    }
    
    public Map<String,String[]> getRequestParameterMap()
    {
        if (requestParameterMap == null)
        {
            requestParameterMap = Collections.emptyMap();
        }
        return requestParameterMap;
    }

    public Map<String, String[]> getParameterMap(PortletWindow window)
    {
        PortletWindowRequestNavigationalState state = requestStates.getPortletWindowNavigationalState(window.getId().toString());
        if ( state != null && state.getParametersMap() != null )
        {
            return state.getParametersMap();
        }
        return null;
    }
        
    public void clearParameters(PortletWindow window)
    {
        PortletWindowRequestNavigationalState state = requestStates.getPortletWindowNavigationalState(window.getId().toString());
        if (state != null)
        {
            Map<String, String[]> map = state.getParametersMap();
            if (map != null)
            {
                map.clear();
                state.setClearParameters(true);
            }
        }
    }
    
    public void setParametersMap(PortletWindow window, Map<String, String[]> parametersMap)
    {
        PortletWindowRequestNavigationalState state = requestStates.getPortletWindowNavigationalState(window.getId().toString());
        if (state != null)
        {
            state.setParametersMap(parametersMap);
        }
    }
    
    public boolean isActionScopedRequestAttributes(PortletWindow window)
    {
        PortletWindowRequestNavigationalState state = requestStates.getPortletWindowNavigationalState(window.getId().toString());
        return ((state != null) ? state.isActionScopedRequestAttributes() : false);
    }
    
    public String getActionScopeId(PortletWindow window)
    {
        PortletWindowRequestNavigationalState state = requestStates.getPortletWindowNavigationalState(window.getId().toString());
        return ((state != null) ? state.getActionScopeId() : null);
    }
    
    public void setActionScopeId(PortletWindow window, String actionScopeId)
    {
        PortletWindowRequestNavigationalState state = requestStates.getPortletWindowNavigationalState(window.getId().toString());
        if (state != null)
        {
            state.setActionScopeId(actionScopeId);
        }
    }

    public boolean isActionScopeRendered(PortletWindow window)
    {
        PortletWindowRequestNavigationalState state = requestStates.getPortletWindowNavigationalState(window.getId().toString());
        return ((state != null) ? state.isActionScopeRendered() : false);
    }

    public void setActionScopeRendered(PortletWindow window, boolean actionScopeRendered)
    {
        PortletWindowRequestNavigationalState state = requestStates.getPortletWindowNavigationalState(window.getId().toString());
        if (state != null)
        {
            state.setActionScopeRendered(actionScopeRendered);
        }
    }

    public String getCacheLevel(PortletWindow window)
    {
        PortletWindowRequestNavigationalState state = requestStates.getPortletWindowNavigationalState(window.getId().toString());
        return ((state != null) ? state.getCacheLevel() : null);
    }

    public void setCacheLevel(PortletWindow window, String cacheLevel)
    {
        PortletWindowRequestNavigationalState state = requestStates.getPortletWindowNavigationalState(window.getId().toString());
        if (state != null)
        {
            state.setCacheLevel(cacheLevel);
        }
    }

    public String getResourceID(PortletWindow window)
    {
        PortletWindowRequestNavigationalState state = requestStates.getPortletWindowNavigationalState(window.getId().toString());
        return ((state != null) ? state.getResourceId() : null);
    }
    
    public void setResourceId(PortletWindow window, String resourceId)
    {
        PortletWindowRequestNavigationalState state = requestStates.getPortletWindowNavigationalState(window.getId().toString());
        if (state != null)
        {
            state.setResourceId(resourceId);
        }
    }

    public Map<String, String[]> getPrivateRenderParameterMap(PortletWindow window)
    {
        PortletWindowRequestNavigationalState state = requestStates.getPortletWindowNavigationalState(window.getId().toString());
        return ((state != null) ? state.getPrivateRenderParametersMap() : null);
    }
    
    public void setPrivateRenderParametersMap(PortletWindow window, Map<String, String[]> privateRenderParametersMap)
    {
        PortletWindowRequestNavigationalState state = requestStates.getPortletWindowNavigationalState(window.getId().toString());
        if (state != null)
        {
            state.setPrivateRenderParametersMap(privateRenderParametersMap);
        }
    }

    public Map<String, String[]> getPublicRenderParameterMap(PortletWindow window)
    {
        PortletWindowRequestNavigationalState state = requestStates.getPortletWindowNavigationalState(window.getId().toString());
        if (state == null && requestStates.getPublicRenderParametersMap() != null && window.isValid() && window.getPortletDefinition().getSupportedPublicRenderParameters() != null)
        {
        	// Window doesn't have any state of its own yet but potentially could require access to the public render parameters
        	// This could also be an instantly created PortletWindow for which we also need to support access to the public render parameters
        	// need to create and inject a new state object on the fly to be able to resolve this
            state = new PortletWindowRequestNavigationalState(window.getWindowId());
            state.setPortletDefinition(window.getPortletDefinition());
            state.resolveActionScopedRequestAttributes();
            requestStates.addPortletWindowNavigationalState(window.getWindowId(), state);
        }
        if (state != null)
        {
            return requestStates.getPublicRenderParametersMap(window.getWindowId());
        }
        return null;
    }
    
    public void setPublicRenderParametersMap(PortletWindow window, Map<String, String[]> publicRenderParametersMap)
    {
        if (publicRenderParametersMap != null)
        {
            PortletWindowRequestNavigationalState targetState = requestStates.getPortletWindowNavigationalState(window.getId().toString());
            if (targetState != null)
            {
                synchronized (requestStates)
                {
                    Map<String, String[]> targets = new HashMap<String, String[]>();
                    Map<QName, String[]> qtargets = new HashMap<QName, String[]>();
                    Map<QName, String[]> rsPrpMap = requestStates.getPublicRenderParametersMap();
                    if (rsPrpMap == null)
                    {
                        rsPrpMap = new HashMap<QName,String[]>();
                    }
                    for (Map.Entry<String,String[]> entry : publicRenderParametersMap.entrySet())
                    {
                        QName qname = targetState.getPublicRenderParameterQNameByIdentifier(entry.getKey());
                        if (qname != null)
                        {
                            qtargets.put(qname, entry.getValue());
                            targets.put(entry.getKey(),entry.getValue());
                            if (entry.getValue() == null)
                            {
                                rsPrpMap.remove(qname);
                            }
                            else
                            {
                                rsPrpMap.put(qname, entry.getValue());
                            }
                        }
                    }
                    if (!rsPrpMap.isEmpty())
                    {
                        requestStates.setPublicRenderParametersMap(rsPrpMap);
                    }
                    targetState.setTargetPublicRenderParametersMap(targets);
                    // now symc with the requestStates publicParametersMap and other possible targetted states
                    
                    for (PortletWindowRequestNavigationalState state : requestStates.getPortletWindowRequestNavigationalStates().values())
                    {
                        if (state != targetState)
                        {
                            if (state.getTargetPublicRenderParametersMap() != null)
                            {
                                for (Map.Entry<QName, String[]> entry : qtargets.entrySet())
                                {
                                    String identifier = state.getPublicRenderParameterIdentifierByQName(entry.getKey());
                                    if (identifier != null && state.getTargetPublicRenderParametersMap().containsKey(identifier))
                                    {
                                        // update outgoing value
                                        state.getTargetPublicRenderParametersMap().put(identifier, entry.getValue());
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    public void setTargetted(PortletWindow window)
    {
        PortletWindowRequestNavigationalState state = requestStates.getPortletWindowNavigationalState(window.getId().toString());
        if (state != null)
        {
            state.setTargetted(true);
        }
    }
    
    public PortalURL.URLType getURLType()
    {
        return requestStates.getURLType();
    }
    
    public PortletWindow getMaximizedWindow()
    {
        return requestStates.getMaximizedWindow();
    }

    public PortletWindow getPortletWindowOfAction()
    {
        return requestStates.getActionWindow();
    }
    
    public PortletWindow getPortletWindowOfResource()
    {
        return requestStates.getResourceWindow();
    }

    public String encode(PortletWindow window, Map<String, String[]> parameters, String actionScopeId, boolean actionScopeRendered,
                         String cacheLevel, String resourceId, Map<String, String[]> privateRenderParameters, Map<String, String[]> publicRenderParameters,
                         PortletMode mode, WindowState state, boolean action)
    throws UnsupportedEncodingException
    {
        return encode(window, parameters, actionScopeId, actionScopeRendered, cacheLevel, resourceId, privateRenderParameters, publicRenderParameters,
                      mode, state, action ? PortalURL.URLType.ACTION : PortalURL.URLType.RENDER);
    }

    public String encode(PortletWindow window, Map<String, String[]> parameters, String actionScopeId, boolean actionScopeRendered,
                         String cacheLevel, String resourceId, Map<String, String[]> privateRenderParameters, Map<String, String[]> publicRenderParameters,
                         PortletMode mode, WindowState state, PortalURL.URLType urlType)
    throws UnsupportedEncodingException
    {
        if (!window.isValid())
        {
            throw new IllegalStateException("Cannot encode Navigational State for invalid window: "+window.getId());
        }
        if ( mode != null || state != null )
        {
            PortletApplication pa = null;
            if (mode != null && !JetspeedActions.getStandardPortletModes().contains(mode))
            {
                pa = window.getPortletDefinition().getApplication();
                mode = pa.getMappedPortletMode(mode);
            }
            if (state != null && !JetspeedActions.getStandardWindowStates().contains(state))
            {
                if ( pa == null )
                {
                    pa = window.getPortletDefinition().getApplication();
                }
                state = pa.getMappedWindowState(state);
            }
        }
        return codec.encode(requestStates, window, parameters, actionScopeId, actionScopeRendered, cacheLevel, resourceId, privateRenderParameters, publicRenderParameters,
                            mode, state, urlType, isNavigationalParameterStateFull(), isRenderParameterStateFull());
    }

    public String encode(PortletWindow window, PortletMode mode, WindowState state)
    throws UnsupportedEncodingException
    {
        if (!window.isValid())
        {
            throw new IllegalStateException("Cannot encode Navigational State for invalid window: "+window.getId());
        }
        String windowId = window.getWindowId();
        if ( mode != null || state != null )
        {
            PortletApplication pa = window.getPortletDefinition().getApplication();
            if (mode != null && !JetspeedActions.getStandardPortletModes().contains(mode))
            {
                mode = pa.getMappedPortletMode(mode);
            }
            if (state != null && !JetspeedActions.getStandardWindowStates().contains(state))
            {
                state = pa.getMappedWindowState(state);
            }
        }
        String encodedState = null;
        Map<String, PortletWindowBaseNavigationalState> currentWindowStates = null;
        PortletWindowExtendedNavigationalState windowNavState = null;
        PortletMode targetMode = mode;
        WindowState targetState = state;
        if (this instanceof SessionNavigationalState)
        {
            currentWindowStates = ((SessionNavigationalState)this).getCurrentPageWindowStates();
            if (currentWindowStates != null)
            {
                windowNavState = (PortletWindowExtendedNavigationalState)currentWindowStates.get(windowId);
                if (windowNavState != null)
                {
                    if (targetMode == null)
                    {
                        targetMode = windowNavState.getPortletMode();
                    }
                    if (targetState == null)
                    {
                        targetState = windowNavState.getWindowState();
                    }
                   encodedState = windowNavState.getDecoratorActionEncoding(targetMode, targetState);
                }
            }
        }
        if (encodedState == null)
        {
            encodedState = codec.encode(requestStates, window, mode, state, isNavigationalParameterStateFull(), isRenderParameterStateFull());
            if (currentWindowStates != null)
            {
                if (windowNavState == null)
                {
                    windowNavState = new PortletWindowExtendedNavigationalState();
                    windowNavState.setActionScopedRequestAttributes(requestStates.getPortletWindowNavigationalState(windowId).isActionScopedRequestAttributes());
                    currentWindowStates.put(windowId, windowNavState);
                }
                windowNavState.setDecoratorActionEncoding(targetMode, targetState, encodedState);
            }
        }
        return encodedState;
    }
    
    public String encode() throws UnsupportedEncodingException
    {
        return codec.encode(requestStates, isNavigationalParameterStateFull(), isRenderParameterStateFull());
    }

    public Iterator<String> getWindowIdIterator()
    {
        return requestStates.getWindowIdIterator();
    }
    
    @SuppressWarnings("unchecked")
    public void registerPortletContentCachedForPublicRenderParameters(RequestContext context, PortletContent content)
    {
        String windowId = content.getCacheKey().getWindowId();
        PortletWindowRequestNavigationalState state = requestStates.getPortletWindowNavigationalState(windowId);
        if (state != null && state.getPublicRenderParametersMap() != null && !state.getPublicRenderParametersMap().isEmpty())
        {
            HttpSession session = context.getRequest().getSession(true);
            
            synchronized (session)
            {
                String pageId = context.getPage().getId();
                Map<QName, ValuesAndWindowUsage> publicRenderParametersMap = (Map<QName, ValuesAndWindowUsage>)session.getAttribute(NavigationalState.PRP_SESSION_KEY);
                if (publicRenderParametersMap == null)
                {
                    // should not be possible, because having publicRenderParameters implies the publicRenderParametersMap already must have been created
                    publicRenderParametersMap = Collections.synchronizedMap(new HashMap<QName, ValuesAndWindowUsage>());
                    session.setAttribute(PRP_SESSION_KEY, publicRenderParametersMap);
                }
                for (String identifier : state.getPublicRenderParametersMap().keySet())
                {
                    QName qname = state.getPublicRenderParameterQNameByIdentifier(identifier);
                    ValuesAndWindowUsage usage = publicRenderParametersMap.get(qname);
                    if (usage != null)
                    {
                        usage.registerWindowUsage(pageId, windowId);
                    }
                }
            }
        }
    }
}
