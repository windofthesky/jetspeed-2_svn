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
import java.util.Iterator;
import java.util.Map;

import javax.portlet.PortletMode;
import javax.portlet.WindowState;

import org.apache.jetspeed.JetspeedActions;
import org.apache.jetspeed.cache.JetspeedContentCache;
import org.apache.jetspeed.container.state.MutableNavigationalState;
import org.apache.jetspeed.om.common.portlet.PortletApplication;
import org.apache.pluto.om.window.PortletWindow;

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
    
    protected PortletWindowRequestNavigationalStates getPortletWindowRequestNavigationalStates()
    {
        return requestStates;
    }
    
    public void setState(PortletWindow window, WindowState windowState)
    {
        if ( windowState != null )
        {
            if (!JetspeedActions.getStandardWindowStates().contains(windowState))
            {
                PortletApplication pa = (PortletApplication)window.getPortletEntity().getPortletDefinition().getPortletApplicationDefinition();
                windowState = pa.getMappedWindowState(windowState);
            }
            String windowId = window.getId().toString();
            PortletWindowRequestNavigationalState state = requestStates.getPortletWindowNavigationalState(windowId);
            if (state != null && (state.getWindowState() == null || !state.getWindowState().equals(windowState)))
            {
                state.setWindowState(windowState);
            }
            else
            {
                state = new PortletWindowRequestNavigationalState(windowId);
                requestStates.addPortletWindowNavigationalState(windowId, state);
                state.setWindowState(windowState);
            }
            if (windowState.equals(WindowState.MAXIMIZED))
            {
                requestStates.setMaximizedWindow(window);
            }
        }
    }

    public void setMode(PortletWindow window, PortletMode portletMode)
    {
        if ( portletMode != null )
        {
            if (!JetspeedActions.getStandardPortletModes().contains(portletMode))
            {
                PortletApplication pa = (PortletApplication)window.getPortletEntity().getPortletDefinition().getPortletApplicationDefinition();
                portletMode = pa.getMappedPortletMode(portletMode);
            }
            String windowId = window.getId().toString();
            PortletWindowRequestNavigationalState state = requestStates.getPortletWindowNavigationalState(windowId);
            if (state != null && (state.getPortletMode() == null || !state.getPortletMode().equals(portletMode)))
            {
                state.setPortletMode(portletMode);
            }
            else
            {
                state = new PortletWindowRequestNavigationalState(windowId);
                requestStates.addPortletWindowNavigationalState(windowId, state);
                state.setPortletMode(portletMode);
            }
        }
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
            PortletApplication pa = (PortletApplication)window.getPortletEntity().getPortletDefinition().getPortletApplicationDefinition();
            state = pa.getCustomWindowState(state);
        }
        return state;
    }

    public WindowState getMappedState(PortletWindow window)
    {
        return getMappedState(window.getId().toString());
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
            PortletApplication pa = (PortletApplication)window.getPortletEntity().getPortletDefinition().getPortletApplicationDefinition();
            mode = pa.getCustomPortletMode(mode);
        }
        return mode;
    }

    public PortletMode getMappedMode(PortletWindow window)
    {
        return getMappedMode(window.getId().toString());
    }

    public PortletWindow getMaximizedWindow()
    {
        return requestStates.getMaximizedWindow();
    }

    public Iterator getParameterNames(PortletWindow window)
    {
        PortletWindowRequestNavigationalState state = requestStates.getPortletWindowNavigationalState(window.getId().toString());
        if ( state != null && state.getParametersMap() != null )
        {
            return state.getParametersMap().keySet().iterator();
        }
        else
        {
            return Collections.EMPTY_LIST.iterator();
        }
    }

    public String[] getParameterValues(PortletWindow window, String parameterName)
    {
        PortletWindowRequestNavigationalState state = requestStates.getPortletWindowNavigationalState(window.getId().toString());
        if ( state != null && state.getParametersMap() != null )
        {
            return (String[])state.getParametersMap().get(parameterName);
        }
        else
        {
            return null;
        }
    }

    public PortletWindow getPortletWindowOfAction()
    {
        return requestStates.getActionWindow();
    }
    
    public PortletWindow getPortletWindowOfResource()
    {
        return requestStates.getResourceWindow();
    }

    public String encode(PortletWindow window, Map parameters, PortletMode mode, WindowState state, boolean action)
    throws UnsupportedEncodingException
    {
        if ( mode != null || state != null )
        {
            PortletApplication pa = null;
            if (mode != null && !JetspeedActions.getStandardPortletModes().contains(mode))
            {
                pa = (PortletApplication)window.getPortletEntity().getPortletDefinition().getPortletApplicationDefinition();
                mode = pa.getMappedPortletMode(mode);
            }
            if (state != null && !JetspeedActions.getStandardWindowStates().contains(state))
            {
                if ( pa == null )
                {
                    pa = (PortletApplication)window.getPortletEntity().getPortletDefinition().getPortletApplicationDefinition();
                }
                state = pa.getMappedWindowState(state);
            }
        }
        return codec.encode(requestStates, window, parameters, mode, state, action, isNavigationalParameterStateFull(),
                isRenderParameterStateFull());
    }

    public String encode(PortletWindow window, PortletMode mode, WindowState state)
    throws UnsupportedEncodingException
    {
        if ( mode != null || state != null )
        {
            PortletApplication pa = null;
            if (mode != null && !JetspeedActions.getStandardPortletModes().contains(mode))
            {
                pa = (PortletApplication)window.getPortletEntity().getPortletDefinition().getPortletApplicationDefinition();
                mode = pa.getMappedPortletMode(mode);
            }
            if (state != null && !JetspeedActions.getStandardWindowStates().contains(state))
            {
                if ( pa == null )
                {
                    pa = (PortletApplication)window.getPortletEntity().getPortletDefinition().getPortletApplicationDefinition();
                }
                state = pa.getMappedWindowState(state);
            }
        }
        String encodedState = null;
        Map currentWindowStates = null;
        PortletWindowExtendedNavigationalState windowNavState = null;
        PortletMode targetMode = mode;
        WindowState targetState = state;
        if (this instanceof SessionNavigationalState)
        {
            currentWindowStates = ((SessionNavigationalState)this).getCurrentPageWindowStates();
            if (currentWindowStates != null)
            {
                windowNavState = (PortletWindowExtendedNavigationalState)currentWindowStates.get(window.getId().toString());
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
                    currentWindowStates.put(window.getId().toString(), windowNavState);
                }
                windowNavState.setDecoratorActionEncoding(targetMode, targetState, encodedState);
            }
        }
        return encodedState;
    }

    public Iterator getWindowIdIterator()
    {
        return requestStates.getWindowIdIterator();
    }
    
    public void clearParameters(PortletWindow window)
    {
        PortletWindowRequestNavigationalState state = requestStates.getPortletWindowNavigationalState(window.getId().toString());
        if (state != null)
        {
            Map map = state.getParametersMap();
            if (map != null)
            {
                map.clear();
                state.setClearParameters(true);
            }
        }
    }
}
