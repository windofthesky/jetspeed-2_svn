/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2000-2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *    "Apache Jetspeed" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Apache Jetspeed", nor may "Apache" appear in their name, without
 *    prior written permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.jetspeed.engine.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;

import javax.portlet.PortletMode;
import javax.portlet.WindowState;

import org.apache.jetspeed.aggregator.PortletWindowFactory;
import org.apache.jetspeed.exception.JetspeedException;
import org.apache.jetspeed.registry.JetspeedPortletRegistry;
import org.apache.pluto.om.portlet.PortletDefinition;
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
public class PortalControlParameter
{
    static public final String ACTION = "ac";

    static public final String MODE = "md";
    static public final String PREFIX = "_";
    static public final String PREV_MODE = "pm";
    static public final String PREV_STATE = "ps";
    static public final String RENDER_PARAM = "rp";
    static public final String STATE = "st";
    static public final String KEY_DELIMITER = ":";
    static public final String PORTLET_ID = "pid";

    public static String decodeParameterName(String paramName)
    {
        return paramName.substring(PREFIX.length());
    }

    public static String decodeParameterValue(String paramName, String paramValue)
    {
        return paramValue;
    }

    private static String decodeRenderParamName(String encodedParamName)
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

    private static String[] decodeRenderParamValues(String encodedParamValues)
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

    private static String decodeValue(String value)
    {
        value = StringUtils.replace(value, "0x1", "_");
        value = StringUtils.replace(value, "0x2", ".");
        return value;
    }

    public static String encodeParameter(String param)
    {
        return PREFIX + param;
    }

    public static String encodeRenderParamName(PortletWindow window, String paramName)
    {
        StringBuffer returnvalue = new StringBuffer(50);
        returnvalue.append(RENDER_PARAM);
        returnvalue.append("_");
        returnvalue.append(window.getId().toString());
        returnvalue.append("_");
        returnvalue.append(paramName);
        return returnvalue.toString();
    }

    public static String encodeRenderParamValues(String[] paramValues)
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

    private static String encodeValue(String value)
    {
        value = StringUtils.replace(value, "_", "0x1");
        value = StringUtils.replace(value, ".", "0x2");
        return value;
    }

    public static String getRenderParamKey(PortletWindow window)
    {
        return RENDER_PARAM + "_" + window.getId().toString();
    }

    public static boolean isControlParameter(String param)
    {
        return param.startsWith(PREFIX);
    }

    public static boolean isStateFullParameter(String param)
    {
        if (isControlParameter(param))
        {
            if ((param.startsWith(PREFIX + MODE))
                || (param.startsWith(PREFIX + PREV_MODE))
                || (param.startsWith(PREFIX + STATE))
                || (param.startsWith(PREFIX + PREV_STATE))
                || (param.startsWith(PREFIX + RENDER_PARAM)))
            {
                return true;
            }
        }
        return false;
    }
    private Map requestParameter = new HashMap();
    private Map stateFullControlParameter = null;
    private Map stateLessControlParameter = null;

    private PortalURL url;

    public PortalControlParameter(PortalURL url)
    {
        this.url = url;
        stateFullControlParameter = ((PortalURLImpl)this.url).getClonedStateFullControlParameter();
        stateLessControlParameter = ((PortalURLImpl) this.url).getClonedStateLessControlParameter();
    }

    public void clearRenderParameters(PortletWindow portletWindow)
    {
        String prefix = getRenderParamKey(portletWindow);
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

    private String getActionKey(PortletWindow window)
    {
        return ACTION + "_" + window.getId().toString();
    }

    public PortletMode getMode(PortletWindow window)
    {
        String mode = (String) stateFullControlParameter.get(getModeKey(window));
        if (mode != null)
            return new PortletMode(mode);
        else
            return PortletMode.VIEW;
    }

    private String getModeKey(PortletWindow window)
    {
        return MODE + "_" + window.getId().toString();
    }

    public PortletWindow getPortletWindowOfAction() throws JetspeedException
    {
        Iterator iterator = getStateLessControlParameter().keySet().iterator();
        PortletWindow portletWindow = null;
        while (iterator.hasNext())
        {
            String name = (String) iterator.next();
            if (name.startsWith(ACTION))
            {
                String id = name.substring(ACTION.length() + 1);
                /*
                
                TODO: BROKEN: need to go the profiler to get the profile, psml, and then window for an entity
                this is normally done in the aggregator valve, need to sort out the sequence 
                
                Fragment fragment = org.apache.pluto.portalImpl.services.pageregistry.PageRegistry.getFragment(id);
                if(fragment instanceof PortletFragment) {
                    return ((PortletFragment)fragment).getPortletWindow();
                }
                */

                StringTokenizer idTokenizer = new StringTokenizer(id, KEY_DELIMITER);
                String portletName = idTokenizer.nextToken();
                String sequence = idTokenizer.nextToken();
                String entityName = idTokenizer.nextToken();

                PortletDefinition portletDefinition = JetspeedPortletRegistry.getPortletDefinitionByUniqueName(entityName + "::" + portletName);
                if (portletDefinition == null)
                {
                    throw new JetspeedException("Failed to load: " + portletName + " from registry");
                }

                portletWindow = PortletWindowFactory.getWindow(portletDefinition, entityName);

            }
        }

        return portletWindow;
    }

    public PortletMode getPrevMode(PortletWindow window)
    {
        String mode = (String) stateFullControlParameter.get(getPrevModeKey(window));
        if (mode != null)
            return new PortletMode(mode);
        else
            return null;
    }
    private String getPrevModeKey(PortletWindow window)
    {
        return PREV_MODE + "_" + window.getId().toString();
    }

    public WindowState getPrevState(PortletWindow window)
    {
        String state = (String) stateFullControlParameter.get(getPrevStateKey(window));
        if (state != null)
            return new WindowState(state);
        else
            return null;
    }
    private String getPrevStateKey(PortletWindow window)
    {
        return PREV_STATE + "_" + window.getId().toString();
    }

    public Iterator getRenderParamNames(PortletWindow window)
    {
        ArrayList returnvalue = new ArrayList();
        String prefix = getRenderParamKey(window);
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

    public Map getRequestParameter()
    {
        return requestParameter;
    }

    public WindowState getState(PortletWindow window)
    {
        String state = (String) stateFullControlParameter.get(getStateKey(window));
        if (state != null)
            return new WindowState(state);
        else
            return WindowState.NORMAL;
    }

    public Map getStateFullControlParameter()
    {
        return stateFullControlParameter;
    }

    private String getStateKey(PortletWindow window)
    {
        return STATE + "_" + window.getId().toString();
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
            if (name.startsWith(STATE))
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
        getStateFullControlParameter().put(getActionKey(window), ACTION.toUpperCase());
    }

    public void setMode(PortletWindow window, PortletMode mode)
    {
        Object prevMode = stateFullControlParameter.get(getModeKey(window));
        if (prevMode != null)
            stateFullControlParameter.put(getPrevModeKey(window), prevMode);
        // set current mode
        stateFullControlParameter.put(getModeKey(window), mode.toString());
    }

    public void setRenderParam(PortletWindow window, String name, String[] values)
    {
        String encodedKey = encodeRenderParamName(window, name);
        String encodedValue = encodeRenderParamValues(values);
        stateFullControlParameter.put(encodedKey, encodedValue);
        // setRequestParam(encodedKey, values);
    }

    /*
        public void setRequestParam(String name, String value)
        {
            requestParameter.put(name, value );
        }
    */
    public void setRequestParam(String name, String[] values)
    {
        requestParameter.put(name, values);
    }

    public void setState(PortletWindow window, WindowState state)
    {
        Object prevState = stateFullControlParameter.get(getStateKey(window));
        if (prevState != null)
            stateFullControlParameter.put(getPrevStateKey(window), prevState);
        stateFullControlParameter.put(getStateKey(window), state.toString());
    }

    public String getPIDValue()
    {
        String value = (String) stateLessControlParameter.get(getPortletIdKey());
        return value == null ? "" : value;
    }

    private String getPortletIdKey()
    {
        return PORTLET_ID;
    }

}
