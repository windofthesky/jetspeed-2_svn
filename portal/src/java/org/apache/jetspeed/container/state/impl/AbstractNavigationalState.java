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
package org.apache.jetspeed.container.state.impl;

import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

import javax.portlet.PortletMode;
import javax.portlet.WindowState;

import org.apache.jetspeed.container.state.MutableNavigationalState;
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
    
    public AbstractNavigationalState(NavigationalStateCodec codec)
    {
        this.codec = codec;        
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
            PortletWindowRequestNavigationalState state = requestStates.getPortletWindowNavigationalState(window.getId().toString());
            if (state != null && (state.getWindowState() == null || !state.getWindowState().equals(windowState)))
            {
                state.setWindowState(windowState);
            }
        }
    }

    public void setMode(PortletWindow window, PortletMode portletMode)
    {
        if ( portletMode != null )
        {
            PortletWindowRequestNavigationalState state = requestStates.getPortletWindowNavigationalState(window.getId().toString());
            if (state != null && (state.getPortletMode() == null || !state.getPortletMode().equals(portletMode)))
            {
                state.setPortletMode(portletMode);
            }
        }
    }

    public WindowState getState(PortletWindow window)
    {
        WindowState windowState = null;
        PortletWindowRequestNavigationalState state = requestStates.getPortletWindowNavigationalState(window.getId().toString());
        if (state != null)
        {
            windowState = state.getWindowState();
        }
        return windowState != null ? windowState : WindowState.NORMAL;
    }

    public PortletMode getMode(PortletWindow window)
    {
        PortletMode portletMode = null;
        PortletWindowRequestNavigationalState state = requestStates.getPortletWindowNavigationalState(window.getId().toString());
        if (state != null)
        {
            portletMode = state.getPortletMode();
        }
        return portletMode != null ? portletMode : PortletMode.VIEW;
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

    public String encode(PortletWindow window, Map parameters, PortletMode mode, WindowState state, boolean action)
    throws UnsupportedEncodingException
    {
        return codec.encode(requestStates, window, parameters, mode, state, action, isNavigationalParameterStateFull(),
                isRenderParameterStateFull());
    }

    public String encode(PortletWindow window, PortletMode mode, WindowState state)
    throws UnsupportedEncodingException
    {
        return codec.encode(requestStates, window, mode, state, isNavigationalParameterStateFull(), 
                isRenderParameterStateFull());
    }    
}
