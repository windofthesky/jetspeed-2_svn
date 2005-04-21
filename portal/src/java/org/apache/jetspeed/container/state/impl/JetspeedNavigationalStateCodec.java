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
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;

import javax.portlet.PortletMode;
import javax.portlet.WindowState;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.Jetspeed;
import org.apache.jetspeed.container.window.PortletWindowAccessor;
import org.apache.pluto.om.window.PortletWindow;

/**
 * JetspeedNavigationalStateCodec
 *
 * @author <a href="mailto:ate@apache.org">Ate Douma</a>
 * @version $Id$
 */
public class JetspeedNavigationalStateCodec implements NavigationalStateCodec
{
    /** Commons logging */
    protected final static Log log = LogFactory.getLog(JetspeedNavigationalStateCodec.class);

    protected static final char PARAMETER_SEPARATOR = '|';
    protected static final char PARAMETER_ELEMENT_SEPARATOR = '=';    
    protected static final char RENDER_WINDOW_ID_KEY = 'a';
    protected static final char ACTION_WINDOW_ID_KEY = 'b';
    protected static final char MODE_KEY = 'c';
    protected static final char STATE_KEY = 'd';
    protected static final char PARAM_KEY = 'e';
    protected static final char CLEAR_PARAMS_KEY = 'f';
    
    protected static final String keytable = "01234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    protected final PortletMode[] portletModes;
    protected final WindowState[] windowStates;
    
    public JetspeedNavigationalStateCodec()
    {
        Object o;
        ArrayList list = new ArrayList();
        Enumeration portletModesEnum = Jetspeed.getContext().getSupportedPortletModes();
        
        // ensure standard modes will be first in the portletModeNames array
        // this ensures those modes are never lost from a bookmarked url when new modes are added somewhere in the
        // middle
        list.add(PortletMode.VIEW);
        list.add(PortletMode.EDIT);
        list.add(PortletMode.HELP);
        
        while ( portletModesEnum.hasMoreElements() )
        {
            o = portletModesEnum.nextElement();
            if ( !list.contains(o) )
            {
                list.add(o);
            }
        }
        portletModes = (PortletMode[])list.toArray(new PortletMode[list.size()]);
        if (portletModes.length > keytable.length())
        {
            throw new UnsupportedOperationException("Too many supported PortletModes found. Can only handle max: "+keytable.length());
        }
        
        list.clear();
        
        // ensure standard states will be first in the windowStateNames array
        // this ensures those states are never lost from a bookmarked url when new states are added somewhere in the
        // middle
        list.add(WindowState.NORMAL);
        list.add(WindowState.MAXIMIZED);
        list.add(WindowState.MINIMIZED);
        Enumeration windowStatesEnum = Jetspeed.getContext().getSupportedWindowStates();
        while ( windowStatesEnum.hasMoreElements() )
        {
            o = windowStatesEnum.nextElement();
            if ( !list.contains(o) )
            {
                list.add(o);
            }
        }
        windowStates = (WindowState[])list.toArray(new WindowState[list.size()]);        
        if (windowStates.length > keytable.length())
        {
            throw new UnsupportedOperationException("Too many supported WindowModes found. Can only handle max: "+keytable.length());
        }
    }
    
    public PortletWindowRequestNavigationalStates decode(String parameters, String characterEncoding)
    throws UnsupportedEncodingException
    {
        PortletWindowRequestNavigationalStates states = new PortletWindowRequestNavigationalStates(characterEncoding);
        if ( parameters != null && parameters.length() > 0 ) 
        {
            String decodedParameters = decodeParameters(parameters, characterEncoding);
            
            int position = 0;
            StringBuffer buffer = new StringBuffer();
            PortletWindowAccessor accessor = (PortletWindowAccessor) Jetspeed.getComponentManager().getComponent(PortletWindowAccessor.class);
            PortletWindowRequestNavigationalState currentState = null;
            String parameter;
            while ( (position = decodeArgument(position, decodedParameters, buffer, PARAMETER_SEPARATOR )) != -1 )
            {
                parameter = buffer.toString();
                currentState = decodeParameter( accessor, states, currentState, parameter);
            }
            if ( log.isDebugEnabled() )
            {
                buffer.setLength(0);
                
                String actionWindowId = states.getActionWindow() != null ? states.getActionWindow().getId().toString() : "";
                
                Iterator iter = states.getWindowIdIterator();
                while ( iter.hasNext() )
                {
                    if ( buffer.length() == 0 )
                    {
                        buffer.append("[[");
                    }
                    else
                    {
                        buffer.append(",[");
                    }
                    currentState = states.getPortletWindowNavigationalState((String)iter.next());
                    buffer.append("window:"+currentState.getWindowId());
                    
                    if ( currentState.getWindowId().equals(actionWindowId))
                    {
                        buffer.append(",action:true");
                    }
                    if (currentState.getPortletMode() != null) 
                    {
                        buffer.append(",mode:"+currentState.getPortletMode());
                    }
                    if (currentState.getWindowState() != null )
                    {
                        buffer.append(",state:"+currentState.getWindowState());
                    }
                    if (!currentState.isClearParameters())
                    {
                        if (currentState.getParametersMap() != null)
                        {
                            buffer.append(",parameters:[");
                            boolean first = true;
                            Iterator parIter = currentState.getParametersMap().keySet().iterator();
                            while ( parIter.hasNext() ) 
                            {
                                if ( first )
                                {
                                    first = false;
                                }
                                else
                                {
                                    buffer.append(",");
                                }
                                String name = (String)parIter.next();
                                buffer.append(name+":[");
                                String[] values = (String[])currentState.getParametersMap().get(name);
                                for ( int i = 0; i < values.length; i++ )
                                {
                                    if ( i > 0 )
                                    {
                                        buffer.append(",");
                                    }                                    
                                    buffer.append(values[i]);
                                }
                                buffer.append("]");
                            }
                        }
                    }
                    buffer.append("]");
                }
                if ( buffer.length() > 0 )
                {
                    buffer.append("]");
                    log.debug("navstate decoded="+buffer.toString());
                }
            }
        }
        return states;
    }
    
    public String encode(PortletWindowRequestNavigationalStates states, PortletWindow window, PortletMode portletMode, 
            WindowState windowState, boolean navParamsStateFull, boolean renderParamsStateFull)
    throws UnsupportedEncodingException
    {
        String windowId = window.getId().toString();
        PortletWindowRequestNavigationalState currentState = states.getPortletWindowNavigationalState(windowId);
        PortletWindowRequestNavigationalState targetState = new PortletWindowRequestNavigationalState(windowId);        
        targetState.setPortletMode(portletMode != null ? portletMode : currentState != null ? currentState.getPortletMode() : null);
        targetState.setWindowState(windowState != null ? windowState : currentState != null ? currentState.getWindowState() : null);

        // never retain actionRequest parameters nor session stored renderParameters
        if ( currentState != null && !renderParamsStateFull )
        {
            // retain current request parameters if any
            if ( currentState.getParametersMap() != null )
            {
                Iterator parametersIter = currentState.getParametersMap().entrySet().iterator();
                Map.Entry entry;
                while ( parametersIter.hasNext())
                {
                    entry = (Map.Entry)parametersIter.next();
                    targetState.setParameters((String)entry.getKey(), (String[])entry.getValue());
                }
            }
        }
        // encode as requestURL parameters
        return encode(states, windowId, targetState, false, navParamsStateFull, renderParamsStateFull);
    }

    public String encode(PortletWindowRequestNavigationalStates states, PortletWindow window, Map parameters, 
            PortletMode portletMode, WindowState windowState, boolean action, boolean navParamsStateFull, 
            boolean renderParamsStateFull)
    throws UnsupportedEncodingException
    {
        String windowId = window.getId().toString();
        PortletWindowRequestNavigationalState currentState = states.getPortletWindowNavigationalState(windowId);
        PortletWindowRequestNavigationalState targetState = new PortletWindowRequestNavigationalState(windowId);
        targetState.setPortletMode(portletMode != null ? portletMode : currentState != null ? currentState.getPortletMode() : null);
        targetState.setWindowState(windowState != null ? windowState : currentState != null ? currentState.getWindowState() : null);
        
        Iterator parametersIter = parameters.entrySet().iterator();

        Map.Entry entry;
        // fill in the new parameters
        while ( parametersIter.hasNext())
        {
            entry = (Map.Entry)parametersIter.next();
            targetState.setParameters((String)entry.getKey(), (String[])entry.getValue());
        }
        if ( renderParamsStateFull && targetState.getParametersMap() == null )
        {
            // Indicate that the saved (in the session) render parameters for this PortletWindow must be cleared
            // and not copied when synchronizing the state (encoded as CLEAR_PARAMS_KEY)
            targetState.setClearParameters(true);
        }
        return encode(states, windowId, targetState, action, navParamsStateFull, renderParamsStateFull);
    }

    protected String encode(PortletWindowRequestNavigationalStates states, String targetWindowId, 
            PortletWindowRequestNavigationalState targetState, boolean action, boolean navParamsStateFull, 
            boolean renderParamsStateFull)
    throws UnsupportedEncodingException
    {
        StringBuffer buffer = new StringBuffer();
        String encodedState;
        boolean haveState = false;
        
        // skip other states if all non-targeted PortletWindow states are kept in the session
        if ( !navParamsStateFull || !renderParamsStateFull )
        {
            PortletWindowRequestNavigationalState pwfns;
            String windowId;
            Iterator iter = states.getWindowIdIterator();
            while ( iter.hasNext() )
            {
                windowId = (String)iter.next();
                pwfns = states.getPortletWindowNavigationalState(windowId);
                if ( windowId.equals(targetWindowId))
                {
                    // skip it for now, it will be encoded as the last one below
                }
                else
                {
                    encodedState = encodePortletWindowNavigationalState(windowId, pwfns, false, navParamsStateFull, 
                            renderParamsStateFull);
                    if ( encodedState.length() > 0 )
                    {
                        if ( !haveState )
                        {
                            haveState = true;
                        }
                        else
                        {
                            buffer.append(PARAMETER_SEPARATOR);
                        }
                        buffer.append(encodedState);
                    }
                }
            }
        }
        encodedState = encodePortletWindowNavigationalState(targetWindowId, targetState, action, false, false); 
        if ( encodedState.length() > 0 )
        {
            if ( !haveState )
            {
                haveState = true;
            }
            else
            {
                buffer.append(PARAMETER_SEPARATOR);
            }
            buffer.append(encodedState);
        }
        
        String encodedNavState = null;
        if ( haveState )
        {
            encodedNavState = encodeParameters(buffer.toString(), states.getCharacterEncoding());
        }
        return encodedNavState;
    }
    
    protected String encodePortletWindowNavigationalState(String windowId, PortletWindowRequestNavigationalState state, 
            boolean action, boolean navParamsStateFull, boolean renderParamsStateFull)
    {
        StringBuffer buffer = new StringBuffer();
        buffer.append(action ? ACTION_WINDOW_ID_KEY : RENDER_WINDOW_ID_KEY);
        buffer.append(windowId);
        boolean encoded = action;
        
        if ( action || !navParamsStateFull )
        {
            if (state.getPortletMode() != null)
            {
                buffer.append(PARAMETER_SEPARATOR);
                buffer.append(MODE_KEY);
                buffer.append(encodePortletMode(state.getPortletMode()));
                encoded = true;
            }

            if (state.getWindowState() != null)
            {
                buffer.append(PARAMETER_SEPARATOR);
                buffer.append(STATE_KEY);
                buffer.append(encodeWindowState(state.getWindowState()));
                encoded = true;
            }
        }

        if (state.getParametersMap() != null && (action || !renderParamsStateFull) )
        {
            Map.Entry entry;
            String   parameterName;
            String[] parameterValues;

            StringBuffer paramBuffer = new StringBuffer();
            Iterator iter = state.getParametersMap().entrySet().iterator();
            while ( iter.hasNext() )
            {
                encoded = true;
                entry = (Map.Entry)iter.next();
                parameterName = (String)entry.getKey();
                parameterValues = (String[])entry.getValue();
               
                buffer.append(PARAMETER_SEPARATOR);
                buffer.append(PARAM_KEY);
                
                paramBuffer.setLength(0);
                paramBuffer.append(encodeArgument(parameterName, PARAMETER_ELEMENT_SEPARATOR));
                paramBuffer.append(PARAMETER_ELEMENT_SEPARATOR);
                paramBuffer.append(parameterValues.length);
                for ( int i = 0; i < parameterValues.length; i++ )
                {
                    paramBuffer.append(PARAMETER_ELEMENT_SEPARATOR);
                    paramBuffer.append(encodeArgument(parameterValues[i], PARAMETER_ELEMENT_SEPARATOR));
                }
                
                buffer.append(encodeArgument(paramBuffer.toString(),PARAMETER_SEPARATOR));
            }
        }
        else if ( state.isClearParameters() )
        {
            // Special case: for a targeted PortletWindow for which no parameters are specified 
            // indicate its saved (in the session) request parameters must be cleared instead of copying them when
            // synchronizing the state.
            // During decoding this CLEAR_PARAMS_KEY will set the clearParameters flag of the PortletWindowRequestNavigationalState.
            buffer.append(PARAMETER_SEPARATOR);
            buffer.append(CLEAR_PARAMS_KEY);            
            encoded = true;
        }
        return encoded ? buffer.toString() : "";
    }
    
    protected PortletWindowRequestNavigationalState decodeParameter(PortletWindowAccessor accessor, PortletWindowRequestNavigationalStates states, PortletWindowRequestNavigationalState currentState, String parameter)
    {
        char parameterType = parameter.charAt(0);
        if (parameterType == RENDER_WINDOW_ID_KEY || parameterType == ACTION_WINDOW_ID_KEY )
        {            
            String windowId = parameter.substring(1);
            currentState = states.getPortletWindowNavigationalState(windowId);
            if ( currentState == null )
            {
                PortletWindow window = accessor.getPortletWindow(windowId);
                if ( window != null )
                {
                    currentState = new PortletWindowRequestNavigationalState(windowId);
                    states.addPortletWindowNavigationalState(windowId, currentState);
                    if ( parameterType == ACTION_WINDOW_ID_KEY )
                    {
                        states.setActionWindow(window);
                    }
                }
            }
        }
        else if ( currentState != null )
        {
            switch ( parameterType )
            {
                case MODE_KEY:
                {
                    char c = parameter.charAt(1);
                    PortletMode portletMode = decodePortletMode(parameter.charAt(1));
                    if ( portletMode != null )
                    {
                        currentState.setPortletMode(portletMode);
                    }
                    break;
                }
                case STATE_KEY:
                {
                    char c = parameter.charAt(1);
                    WindowState windowState = decodeWindowState(parameter.charAt(1));
                    if ( windowState != null )
                    {
                        currentState.setWindowState(windowState);
                        if (windowState.equals(WindowState.MAXIMIZED))
                        {
                            states.setMaximizedWindow(accessor.getPortletWindow(currentState.getWindowId()));
                        }
                    }
                    break;
                }
                case PARAM_KEY:
                {
                    int position = 1;
                    StringBuffer buffer = new StringBuffer();
                    String parameterName = null;
                    int parameterValueCount = -1;
                    String parameterValues[] = null;
                    int parameterValueIndex = -1;
                    while ( (position = decodeArgument(position, parameter, buffer, PARAMETER_ELEMENT_SEPARATOR)) != -1 )
                    {
                        if ( parameterName == null )
                        {
                            parameterName = buffer.toString();
                            parameterValueCount = -1;                        
                        }
                        else if ( parameterValueCount == -1 )
                        {
                            parameterValueCount = Integer.parseInt(buffer.toString(), 16);
                            parameterValues = new String[parameterValueCount];
                            parameterValueIndex = 0;
                        }
                        else
                        {
                            parameterValues[parameterValueIndex++] = buffer.toString();
                            parameterValueCount--;
                            if ( parameterValueCount == 0 )
                            {
                                currentState.setParameters(parameterName, parameterValues);
                                break;
                            }
                        }
                    }
                    break;
                }
                case CLEAR_PARAMS_KEY:
                {
                    currentState.setClearParameters(true);
                }
            }
        }
        return currentState;
        
    }
    
    protected PortletMode decodePortletMode(char mode)
    {
        PortletMode portletMode = null;
        int index = keytable.indexOf(mode);
        if (index > -1 && index < portletModes.length)
        {
            portletMode = portletModes[index];
        }
        return portletMode;
    }
    
    protected char encodePortletMode(PortletMode portletMode)
    {
        for ( int i = 0; i < portletModes.length; i++ )
        {
            if (portletModes[i].equals(portletMode))
                return keytable.charAt(i);
        }
        throw new IllegalArgumentException("Unsupported PortletMode: "+portletMode);
    }
    
    protected WindowState decodeWindowState(char state)
    {
        WindowState windowState = null;
        int index = keytable.indexOf(state);
        if (index > -1 && index < windowStates.length)
        {
            windowState = windowStates[index];
        }
        return windowState;
    }
    
    protected char encodeWindowState(WindowState windowState)
    {
        for ( int i = 0; i < windowStates.length; i++ )
        {
            if (windowStates[i].equals(windowState))
                return keytable.charAt(i);
        }
        throw new IllegalArgumentException("Unsupported WindowState: "+windowState);
    }
    
    /** 
     * Decodes a Base64 encoded string.
     * 
     * Because the encoded string is used in an URL
     * the two '/' and '=' which has some significance in an URL
     * are encoded on top of the Base64 encoding and are first translated back before decoding.
     * 
     * @param value
     * @param characterEncoding String containing the name of the chararacter encoding
     * @return decoded string
     */
    protected String decodeParameters(String value, String characterEncoding)
    throws UnsupportedEncodingException
    {
        value = StringUtils.replace(value,"-","/");
        value = StringUtils.replace(value,"_","=");
        if ( characterEncoding != null )
        {
            return new String(Base64.decodeBase64(value.getBytes(characterEncoding)), characterEncoding);
        }
        else
        {
            return new String(Base64.decodeBase64(value.getBytes()));
        }
    }

    /** 
     * Encodes a string with Base64.
     * 
     * Because the encoded string is used in an URL
     * the two '/' and '=' which has some significance in an URL
     * are encoded on top of/after the Base64 encoding
     *  
     * @param value
     * @return encoded string
     */
    protected String encodeParameters(String value, String characterEncoding)
    throws UnsupportedEncodingException
    {
        if ( characterEncoding != null )
        {
            value = new String(Base64.encodeBase64(value.getBytes(characterEncoding)));
        }
        else
        {
            value = new String(Base64.encodeBase64(value.getBytes()));
        }
        value = StringUtils.replace(value,"/","-");
        value = StringUtils.replace(value,"=","_");
        return value;
    }

    protected String encodeArgument( String argument, char escape )
    {
        int length = argument.length();
        StringBuffer buffer = new StringBuffer(length);
        buffer.setLength(0);
        char c;
        for ( int i = 0; i < length; i++ )
        {
            c = argument.charAt(i);
            buffer.append(c);
            if ( c == escape )
            {
                buffer.append(c);
            }
        }
        return buffer.toString();
    }
    
    protected int decodeArgument(int position, String arguments, StringBuffer buffer, char escape)
    {
        int maxLength = arguments.length();
        buffer.setLength(0);
        char c;
        for ( ; position < maxLength; position++ )
        {
            c = arguments.charAt(position);
            if ( c != escape )
            {
                buffer.append(c);
            }
            else 
            {
                if ( c == escape && position < maxLength-1 && arguments.charAt(position+1) == escape )
                {
                    buffer.append(c);
                    position++;
                }
                else
                {
                    position++;
                    break;
                }
            }
        }
        return buffer.length() > 0 ? position : -1; 
    }
}
