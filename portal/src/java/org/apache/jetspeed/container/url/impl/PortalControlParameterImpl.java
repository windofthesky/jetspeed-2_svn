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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.portlet.PortletMode;
import javax.portlet.WindowState;

import org.apache.jetspeed.Jetspeed;
import org.apache.jetspeed.container.session.NavigationalStateComponent;
import org.apache.jetspeed.container.url.*;
import org.apache.jetspeed.container.url.PortalURL;
import org.apache.jetspeed.container.window.PortletWindowAccessor;
import org.apache.pluto.om.window.PortletWindow;
import org.apache.pluto.util.StringUtils;

/**
 * Encode portlet control parameters into URLs using Pluto's portal method.  
 * It also can decode the same parameters.
 * 
 * NOTE: parts of this code were borrowed from Pluto's portal implementation.
 *
 * @author <a href="mailto:david@bluesunrise.com">David Sean Taylor</a>
 * @version $Id$
 */
public class PortalControlParameterImpl implements PortalControlParameter
{
    private Map stateFullControlParameter = null;
    private Map stateLessControlParameter = null;
    private NavigationalStateComponent nav;
    private PortalURL url;

    public PortalControlParameterImpl(PortalURL url, NavigationalStateComponent nav)
    {
        this.nav = nav;
        this.url = url;
    }

    public void init()
    {
        stateFullControlParameter = ((AbstractPortalURL)this.url).getClonedStateFullControlParameter();
        stateLessControlParameter = ((AbstractPortalURL) this.url).getClonedStateLessControlParameter();        
    }
    
    public void clearRenderParameters(PortletWindow portletWindow)
    {
        String prefix = url.getRenderParamKey(portletWindow);
        Iterator keyIterator = stateFullControlParameter.keySet().iterator();

        while (keyIterator.hasNext())
        {
            String name = (String) keyIterator.next();
            if (name.startsWith(prefix))
            {
                keyIterator.remove();
            }
        }
    }

    public PortletMode getMode(PortletWindow window)
    {
        String mode = (String) stateFullControlParameter.get(url.getModeKey(window));
        if (mode != null)
        {
            return nav.lookupPortletMode(mode);
        }
        else
        {
            return PortletMode.VIEW;
        }
    }

    public PortletWindow getPortletWindowOfAction() 
    {
        // Iterator iterator = getStateFullControlParameter().keySet().iterator();
        Iterator iterator = getStateLessControlParameter().keySet().iterator();

        PortletWindow portletWindow = null;
        while (iterator.hasNext())
        {
            String name = (String) iterator.next();
            if (name.startsWith(nav.getNavigationKey(NavigationalStateComponent.ACTION)))
            {
                String id = name.substring(nav.getNavigationKey(NavigationalStateComponent.ACTION).length() + 1);

                PortletWindowAccessor windowAccessor = 
                    (PortletWindowAccessor) Jetspeed.getComponentManager().getComponent(PortletWindowAccessor.class);
                portletWindow = windowAccessor.getPortletWindow(id);
            }
        }

        return portletWindow;
    }

    public PortletMode getPrevMode(PortletWindow window)
    {
        String mode = (String) stateFullControlParameter.get(url.getPrevModeKey(window));
        if (mode != null)
        {
            return nav.lookupPortletMode(mode);
        }
        else
        {
            return null;
        }
    }

    public WindowState getPrevState(PortletWindow window)
    {
        String state = (String) stateFullControlParameter.get(url.getPrevStateKey(window));
        if (state != null)
        {
            return nav.lookupWindowState(state);
        }
        else
        {
            return null;
        }
    }
    
    public Iterator getRenderParamNames(PortletWindow window)
    {
        List returnvalue = new ArrayList();
        String prefix = url.getRenderParamKey(window);
        Iterator keyIterator = stateFullControlParameter.keySet().iterator();

        while (keyIterator.hasNext())
        {
            String name = (String) keyIterator.next();
            if (name.startsWith(prefix))
            {
                returnvalue.add(name.substring(prefix.length() + 1));
            }
        }

        return returnvalue.iterator();
    }

    public String[] getRenderParamValues(PortletWindow window, String paramName)
    {
        String encodedValues = (String) stateFullControlParameter.get(encodeRenderParamName(window, paramName));
        String[] values = decodeRenderParamValues(encodedValues);
        return values;
    }

    public WindowState getState(PortletWindow window)
    {
        String state = (String) stateFullControlParameter.get(url.getStateKey(window));
        if (state != null)
        {
            return nav.lookupWindowState(state);
        }
        else
        {
            return WindowState.NORMAL;
        }
    }

    public Map getStateFullControlParameter()
    {
        return stateFullControlParameter;
    }

    public Map getStateLessControlParameter()
    {
        return stateLessControlParameter;
    }

    public boolean isOnePortletWindowMaximized()
    {
        Iterator iterator = stateFullControlParameter.keySet().iterator();
        while (iterator.hasNext())
        {
            String name = (String) iterator.next();
            if (name.startsWith(nav.getNavigationKey(NavigationalStateComponent.STATE)))
            {
                if (stateFullControlParameter.get(name).equals(WindowState.MAXIMIZED.toString()))
                {
                    return true;
                }
            }
        }
        return false;
    }

    public void setAction(PortletWindow window)
    {
        getStateFullControlParameter().put(url.getActionKey(window), nav.getNavigationKey(NavigationalStateComponent.ACTION).toUpperCase());
    }

    public void setMode(PortletWindow window, PortletMode mode)
    {
        Object prevMode = stateFullControlParameter.get(url.getModeKey(window));
        if (prevMode != null)
            stateFullControlParameter.put(url.getPrevModeKey(window), prevMode);
        // set current mode
        stateFullControlParameter.put(url.getModeKey(window), mode.toString());
    }

    public void setRenderParam(PortletWindow window, String name, String[] values)
    {
        String encodedKey = encodeRenderParamName(window, name);
        String encodedValue = encodeRenderParamValues(values);
        stateFullControlParameter.put(encodedKey, encodedValue);
        // setRequestParam(encodedKey, values);
    }

    public void setState(PortletWindow window, WindowState state)
    {
        Object prevState = stateFullControlParameter.get(url.getStateKey(window));
        if (prevState != null)
            stateFullControlParameter.put(url.getPrevStateKey(window), prevState);
        stateFullControlParameter.put(url.getStateKey(window), state.toString());
    }

    public String getPIDValue()
    {
        String value = (String) stateLessControlParameter.get(getPortletIdKey());
        return value == null ? "" : value;
    }

    private String getPortletIdKey()
    {
        return nav.getNavigationKey(NavigationalStateComponent.ID);
    }

    public String decodeParameterName(String paramName)
    {
        int length = nav.getNavigationKey(NavigationalStateComponent.PREFIX).length();
        return paramName.substring(length);
    }
    
    public String encodeParameter(String param)
    {
        return nav.getNavigationKey(NavigationalStateComponent.PREFIX) + param;
    }

    public String encodeRenderParamName(PortletWindow window, String paramName)
    {
        StringBuffer returnvalue = new StringBuffer(50);
        returnvalue.append(nav.getNavigationKey(NavigationalStateComponent.RENDER_PARAM));
        returnvalue.append("_");
        returnvalue.append(window.getId().toString());
        returnvalue.append("_");
        returnvalue.append(paramName);
        return returnvalue.toString();
    }

    public String encodeRenderParamValues(String[] paramValues)
    {
        StringBuffer returnvalue = new StringBuffer(100);
        returnvalue.append(paramValues.length);
        for (int i = 0; i < paramValues.length; i++)
        {
            returnvalue.append("_");
            returnvalue.append(encodeValue(paramValues[i]));
        }
        return returnvalue.toString();
    }

    public String decodeParameterValue(String paramName, String paramValue)
    {
        return paramValue;
    }

    private String decodeRenderParamName(String encodedParamName)
    {
        StringTokenizer tokenizer = new StringTokenizer(encodedParamName, "_");
        if (!tokenizer.hasMoreTokens())
            return null;
        String constant = tokenizer.nextToken();
        if (!tokenizer.hasMoreTokens())
            return null;
        String objectId = tokenizer.nextToken();
        if (!tokenizer.hasMoreTokens())
            return null;
        String name = tokenizer.nextToken();
        return name;
    }

    private String[] decodeRenderParamValues(String encodedParamValues)
    {
        StringTokenizer tokenizer = new StringTokenizer(encodedParamValues, "_");
        if (!tokenizer.hasMoreTokens())
            return null;
        String _count = tokenizer.nextToken();
        int count = Integer.valueOf(_count).intValue();
        String[] values = new String[count];
        for (int i = 0; i < count; i++)
        {
            if (!tokenizer.hasMoreTokens())
                return null;
            values[i] = decodeValue(tokenizer.nextToken());
        }
        return values;
    }

    private String decodeValue(String value)
    {
        value = StringUtils.replace(value, "0x1", "_");
        value = StringUtils.replace(value, "0x2", ".");
        return value;
    }

    private String encodeValue(String value)
    {
        value = StringUtils.replace(value, "_", "0x1");
        value = StringUtils.replace(value, ".", "0x2");
        return value;
    }
    
    
}
