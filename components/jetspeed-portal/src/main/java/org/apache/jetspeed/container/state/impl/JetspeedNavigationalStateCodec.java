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

import org.apache.commons.codec.binary.Base64;
import org.apache.jetspeed.JetspeedActions;
import org.apache.jetspeed.PortalContext;
import org.apache.jetspeed.container.PortletWindow;
import org.apache.jetspeed.container.url.PortalURL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.portlet.PortletMode;
import javax.portlet.WindowState;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

/**
 * JetspeedNavigationalStateCodec
 *
 * @author <a href="mailto:ate@apache.org">Ate Douma</a>
 * @version $Id$
 */
public class JetspeedNavigationalStateCodec implements NavigationalStateCodec
{
    /** Commons logging */
    protected final static Logger log = LoggerFactory.getLogger(JetspeedNavigationalStateCodec.class);

    protected static final char PARAMETER_SEPARATOR = '|';
    protected static final char PARAMETER_ELEMENT_SEPARATOR = '=';    
    protected static final char PARAMETER_SEPARATOR_ESCAPE = '\\';
    protected static final char RENDER_WINDOW_ID_KEY = 'a';
    protected static final char ACTION_WINDOW_ID_KEY = 'b';
    protected static final char MODE_KEY = 'c';
    protected static final char STATE_KEY = 'd';
    protected static final char PARAM_KEY = 'e';
    protected static final char CLEAR_PARAMS_KEY = 'f';
    protected static final char RESOURCE_WINDOW_ID_KEY = 'g';
    protected static final char CACHE_LEVEL_KEY = 'h';
    protected static final char RESOURCE_ID_KEY = 'i';
    protected static final char PRIVATE_RENDER_PARAM_KEY = 'j';
    protected static final char ACTION_SCOPE_ID_KEY = 'k';
    protected static final char PUBLIC_RENDER_PARAM_KEY = 'l';
    protected static final char RENDERED_ACTION_SCOPE_ID_KEY = 'm';
    protected static final char PORTLET_MANAGED_MODE_KEY = 'n';
    protected static final char[] URLTYPE_ID_KEYS = { 'b', 'g', 'a' };
    
    protected static final String keytable = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    protected final PortletMode[] portletModes;
    protected final WindowState[] windowStates;
    
    public JetspeedNavigationalStateCodec(PortalContext portalContext)
    {
        ArrayList<PortletMode> modesList = new ArrayList<PortletMode>();
        
        // ensure standard modes will be first in the portletModeNames array
        // this ensures those modes are never lost from a bookmarked url when new modes are added somewhere in the
        // middle
        modesList.addAll(JetspeedActions.getStandardPortletModes());
        modesList.addAll(JetspeedActions.getExtendedPortletModes());
        
        portletModes = modesList.toArray(new PortletMode[modesList.size()]);
        if (portletModes.length > keytable.length())
        {
            throw new UnsupportedOperationException("Too many supported PortletModes found. Can only handle max: "+keytable.length());
        }
        
        // ensure standard states will be first in the windowStateNames array
        // this ensures those states are never lost from a bookmarked url when new states are added somewhere in the
        // middle
        ArrayList<WindowState> statesList = new ArrayList<WindowState>();
        statesList.addAll(JetspeedActions.getStandardWindowStates());
        statesList.addAll(JetspeedActions.getExtendedWindowStates());
        
        windowStates = statesList.toArray(new WindowState[statesList.size()]);        
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
            // decode parameters
            String decodedParameters = decodeParameters(parameters, characterEncoding);

            // decode arguments and parameters into states
            int position = 0;
            int length = decodedParameters.length();
            StringBuffer buffer = new StringBuffer();            
            PortletWindowRequestNavigationalState currentState = null;
            String parameter;
            while ( (position = decodeArgument(position, length, decodedParameters, buffer, PARAMETER_SEPARATOR )) != -1 && buffer.length() > 0)
            {
                parameter = buffer.toString();
                currentState = decodeParameter( states, currentState, parameter);
            }
            
            // debug decode
            if ( log.isDebugEnabled() )
            {
                logDecode(states, buffer);
                if ( buffer.length() > 0 )
                {
                    buffer.append("]");
                    log.debug("navstate decoded="+buffer.toString());
                }
            }
        }
        return states;
    }

    private void logDecode(PortletWindowRequestNavigationalStates states, StringBuffer buffer)
    {
        PortletWindowRequestNavigationalState currentState;
        buffer.setLength(0);
        String targetWindowId = states.getTargetWindowId();
        Iterator<String> iter = states.getWindowIdIterator();
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
            currentState = states.getPortletWindowNavigationalState(iter.next());
            buffer.append("window:"+currentState.getWindowId());
            
            if ( targetWindowId != null && currentState.getWindowId().equals(targetWindowId))
            {
                buffer.append(","+states.getURLType()+":true");
            }
            if (currentState.getPortletMode() != null) 
            {
                buffer.append(",mode:"+currentState.getPortletMode());
            }
            if (currentState.getWindowState() != null )
            {
                buffer.append(",state:"+currentState.getWindowState());
            }
            if (currentState.getCacheLevel() != null )
            {
                buffer.append(",cache level:"+currentState.getCacheLevel());
            }
            if (currentState.getResourceId() != null )
            {
                buffer.append(",resource id:"+currentState.getResourceId());
            }
            if (currentState.isActionScopedRequestAttributes())
            {
                if (currentState.getActionScopeId() != null )
                {
                    buffer.append(",action scope id:"+currentState.getActionScopeId());
                    buffer.append(",action scope rendered:"+currentState.isActionScopeRendered());
                }
            }
            if (!currentState.isClearParameters())
            {
                if (currentState.getParametersMap() != null)
                {
                    logDecode("parameters", currentState.getParametersMap(), buffer);
                }
                if (currentState.getPrivateRenderParametersMap() != null)
                {
                    logDecode("private render parameters", currentState.getPrivateRenderParametersMap(), buffer);
                }
                if (currentState.getPublicRenderParametersMap() != null)
                {
                    logDecode("public render parameters", currentState.getPublicRenderParametersMap(), buffer);
                }
            }
            buffer.append("]");
        }
    }
    
    private void logDecode(String parameterNameMap, Map<String, String[]> parametersMap, StringBuffer buffer)
    {
        buffer.append(",");
        buffer.append(parameterNameMap);
        buffer.append(":[");
        boolean first = true;
        Iterator<String> parIter = parametersMap.keySet().iterator();
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
            String name = parIter.next();
            buffer.append(name+":[");
            String[] values = parametersMap.get(name);
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

    public String encode(PortletWindowRequestNavigationalStates states, PortletWindow window, PortletMode portletMode, 
                         WindowState windowState, boolean navParamsStateFull, boolean renderParamsStateFull)
    throws UnsupportedEncodingException
    {
        String windowId = window.getWindowId();
        PortletWindowRequestNavigationalState currentState = states.getPortletWindowNavigationalState(windowId);
        if (currentState == null)
        {
            // inject a new (clean) requestState for this window to optimize for possible following encode url request for it
            currentState = new PortletWindowRequestNavigationalState(windowId);
            currentState.setPortletDefinition(window.getPortletDefinition());
            currentState.resolveActionScopedRequestAttributes();
            states.addPortletWindowNavigationalState(windowId, currentState);
        }
        PortletWindowRequestNavigationalState targetState = new PortletWindowRequestNavigationalState(windowId);
        targetState.setPortletDefinition(window.getPortletDefinition());
        targetState.setPortletMode(portletMode != null ? portletMode : currentState.getPortletMode());
        targetState.setWindowState(windowState != null ? windowState : currentState.getWindowState());

        // never retain actionRequest parameters nor session stored renderParameters
        if ( !renderParamsStateFull )
        {
            // retain current request parameters if any
            targetState.setParametersMap(currentState.getParametersMap());
            targetState.setPublicRenderParametersMap(currentState.getParametersMap());
            if (currentState.isActionScopedRequestAttributes())
            {
                targetState.setActionScopeId(currentState.getActionScopeId());
                targetState.setActionScopeRendered(currentState.isActionScopeRendered());
            }
        }
        // encode as requestURL parameters
        return encode(states, targetState, PortalURL.URLType.RENDER, navParamsStateFull, renderParamsStateFull);
    }

    public String encode(PortletWindowRequestNavigationalStates states, PortletWindow window, Map<String, String[]> parameters,
                         String actionScopeId, boolean actionScopeRendered, String cacheLevel, String resourceId,
                         Map<String, String[]> privateRenderParameters, Map<String, String[]> publicRenderParameters,
                         PortletMode portletMode, WindowState windowState, PortalURL.URLType urlType, boolean navParamsStateFull, 
                         boolean renderParamsStateFull)
    throws UnsupportedEncodingException
    {
        String windowId = window.getWindowId();
        PortletWindowRequestNavigationalState currentState = states.getPortletWindowNavigationalState(windowId);
        if (currentState == null)
        {
            // inject a new (clean) requestState for this window to optimize for possible following encode url request for it
            currentState = new PortletWindowRequestNavigationalState(windowId);
            currentState.setPortletDefinition(window.getPortletDefinition());
            currentState.resolveActionScopedRequestAttributes();
            states.addPortletWindowNavigationalState(windowId, currentState);
        }
        PortletWindowRequestNavigationalState targetState = new PortletWindowRequestNavigationalState(windowId);
        targetState.setPortletDefinition(currentState.getPortletDefinition());
        targetState.setPortletMode(portletMode != null ? portletMode : currentState.getPortletMode());
        targetState.setWindowState(windowState != null ? windowState : currentState.getWindowState());
        targetState.setParametersMap(parameters);
        if (currentState.isActionScopedRequestAttributes())
        {
            targetState.setActionScopedRequestAttributes(true);
            targetState.setActionScopeId(actionScopeId);
            targetState.setActionScopeRendered(actionScopeRendered);
        }        
        targetState.setCacheLevel(cacheLevel);
        targetState.setResourceId(resourceId);
        targetState.setPrivateRenderParametersMap(privateRenderParameters);
        targetState.setTargetPublicRenderParametersMap(publicRenderParameters);
        if ( renderParamsStateFull && parameters == null )
        {
            // Indicate that the saved (in the session) render parameters for this PortletWindow must be cleared
            // and not copied when synchronizing the state (encoded as CLEAR_PARAMS_KEY)
            targetState.setClearParameters(true);
        }
        return encode(states, targetState, urlType, navParamsStateFull, renderParamsStateFull);
    }
    
    public String encode(PortletWindowRequestNavigationalStates states, boolean navParamsStateFull, boolean renderParamsStateFull)
    throws UnsupportedEncodingException
    {
        return encode(states, null, PortalURL.URLType.RENDER, navParamsStateFull, renderParamsStateFull);
    }
    
    protected String encode(PortletWindowRequestNavigationalStates states, 
                            PortletWindowRequestNavigationalState targetState, PortalURL.URLType urlType,
                            boolean navParamsStateFull, boolean renderParamsStateFull)
    throws UnsupportedEncodingException
    {
        StringBuffer buffer = new StringBuffer();
        boolean haveState = false;
        String targetWindowId = targetState != null ? targetState.getWindowId() : null;
        
        if (targetState != null)
        {
            String encodedState = encodePortletWindowNavigationalState(targetState, 
                                                                       urlType, 
                                                                       targetState.getTargetPublicRenderParametersMap(),
                                                                       false, false);
            if (encodedState.length() > 0)
            {
                if (!haveState)
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
        
        // encode individual request states, (skip target request state encoded below)
        Iterator<String> iter = states.getWindowIdIterator();
        while (iter.hasNext())
        {
            String windowId = iter.next();
            PortletWindowRequestNavigationalState requestState = states.getPortletWindowNavigationalState(windowId);
            if (!navParamsStateFull || requestState.isTargetted()) //(!navParamsStateFull || !renderParamsStateFull || requestState.isTargetted())
            {
                if (!(targetWindowId != null && windowId.equals(targetWindowId)))
                {
                    Map<String, String[]> publicRenderParametersMap = null;
                    if (requestState.isTargetted())
                    {
                        publicRenderParametersMap = requestState.getTargetPublicRenderParametersMap();
                    }
                    String encodedState = encodePortletWindowNavigationalState(requestState, 
                                                                               PortalURL.URLType.RENDER, 
                                                                               publicRenderParametersMap, 
                                                                               navParamsStateFull, renderParamsStateFull);
                    if (encodedState.length() > 0)
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
                else
                {
                }
            }
        }
            
        // return encoded state
        return (haveState ? encodeParameters(buffer.toString(), states.getCharacterEncoding()) : null);
    }
    
    protected String encodePortletWindowNavigationalState(PortletWindowRequestNavigationalState state, 
                                                          PortalURL.URLType urlType, 
                                                          Map<String, String[]> publicRenderParametersMap,
                                                          boolean navParamsStateFull, boolean renderParamsStateFull)
    {
        // encode required url type
        String windowId = state.getWindowId();
        StringBuffer buffer = new StringBuffer();
        buffer.append(URLTYPE_ID_KEYS[urlType.ordinal()]);
        buffer.append(windowId);
        boolean encoded = !PortalURL.URLType.RENDER.equals(urlType);
        
        // encode portlet mode and window state
        if (!navParamsStateFull || state.isTargetted() || PortalURL.URLType.ACTION.equals(urlType))
        {
            if (state.getPortletMode() != null)
            {
                buffer.append(PARAMETER_SEPARATOR);
                boolean found = false;
                for ( int i = 0; i < portletModes.length; i++ )
                {
                    if (portletModes[i].equals(state.getPortletMode()))
                    {
                        buffer.append(MODE_KEY);
                        buffer.append(keytable.charAt(i));
                        found = true;
                        break;
                    }
                }
                if (!found)
                {
                    buffer.append(PORTLET_MANAGED_MODE_KEY);
                    buffer.append(state.getPortletMode().toString());
                }
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

        // encode render parameters, (can be optionally be held in session state)
        if (!renderParamsStateFull || state.isTargetted() || PortalURL.URLType.ACTION.equals(urlType))
        {
            if (state.getParametersMap() != null)
            {
                encoded = encodeParameterMap(encoded, PARAM_KEY, false, state.getParametersMap(), buffer);
            }
            if (state.isActionScopedRequestAttributes())
            {
                if (state.getActionScopeId() != null)
                {
                    encoded = true;
                    buffer.append(PARAMETER_SEPARATOR);
                    if (state.isActionScopeRendered())
                    {                        
                        buffer.append(RENDERED_ACTION_SCOPE_ID_KEY);
                    }
                    else
                    {
                        buffer.append(ACTION_SCOPE_ID_KEY);
                    }
                    buffer.append(encodeArgument(state.getActionScopeId(), PARAMETER_SEPARATOR));
                }
            }
        }
        
        // encode resource urls specific parameters
        if (PortalURL.URLType.RESOURCE.equals(urlType))
        {
            if (state.getCacheLevel() != null)
            {
                encoded = true;
                buffer.append(PARAMETER_SEPARATOR);
                buffer.append(CACHE_LEVEL_KEY);
                buffer.append(encodeArgument(state.getCacheLevel(), PARAMETER_SEPARATOR));
            }
            if (state.getResourceId() != null)
            {
                encoded = true;
                buffer.append(PARAMETER_SEPARATOR);
                buffer.append(RESOURCE_ID_KEY);
                buffer.append(encodeArgument(state.getResourceId(), PARAMETER_SEPARATOR));
            }
            if (state.getPrivateRenderParametersMap() != null)
            {
                encoded = encodeParameterMap(encoded, PRIVATE_RENDER_PARAM_KEY, false, state.getPrivateRenderParametersMap(), buffer);
            }
        }

        // encode special case clear render parameters
        if (state.isClearParameters())
        {
            // Special case: for a targeted PortletWindow for which no parameters are specified 
            // indicate its saved (in the session) request parameters must be cleared instead of copying them when
            // synchronizing the state.
            // During decoding this CLEAR_PARAMS_KEY will set the clearParameters flag of the PortletWindowRequestNavigationalState.
            buffer.append(PARAMETER_SEPARATOR);
            buffer.append(CLEAR_PARAMS_KEY);            
            encoded = true;
        }
        
        // encode public render parameters for single state, (can be optionally be held in session state)
        if (!renderParamsStateFull || state.isTargetted() || PortalURL.URLType.ACTION.equals(urlType))
        {
            if (publicRenderParametersMap != null)
            {
                encoded = encodeParameterMap(encoded, PUBLIC_RENDER_PARAM_KEY, true, publicRenderParametersMap, buffer);
            }
        }
        
        // return encoded result if state other than url type is encoded
        return encoded ? buffer.toString() : "";
    }
    
    protected boolean encodeParameterMap(boolean encoded, char paramsKey, boolean allowNullValues, Map<String, String[]> params, StringBuffer buffer)
    {
        StringBuffer paramBuffer = new StringBuffer();
        for (Map.Entry<String, String[]> entry : params.entrySet())
        {
            String parameterName = entry.getKey();
            String [] parameterValues = entry.getValue();
            
            if (parameterValues == null && !allowNullValues)
            {
                continue;
            }
            encoded = true;
            buffer.append(PARAMETER_SEPARATOR);

            paramBuffer.setLength(0);
            paramBuffer.append(encodeArgument(parameterName, PARAMETER_ELEMENT_SEPARATOR));
            paramBuffer.append(PARAMETER_ELEMENT_SEPARATOR);
            if (parameterValues != null)
            {
                paramBuffer.append(Integer.toHexString(parameterValues.length));
                for ( int i = 0; i < parameterValues.length; i++ )
                {
                    paramBuffer.append(PARAMETER_ELEMENT_SEPARATOR);
                    paramBuffer.append(encodeArgument(parameterValues[i], PARAMETER_ELEMENT_SEPARATOR));
                }
            }
            else
            {
                paramBuffer.append('0');
            }

            buffer.append(paramsKey);
            buffer.append(encodeArgument(paramBuffer.toString(),PARAMETER_SEPARATOR));
        }
        return encoded;
    }
    
    protected PortletWindowRequestNavigationalState decodeParameter(PortletWindowRequestNavigationalStates states, PortletWindowRequestNavigationalState currentState, String parameter)
    {
        char parameterType = parameter.charAt(0);
        PortalURL.URLType urlType = null;
        
        switch (parameterType)
        {
            case RENDER_WINDOW_ID_KEY: urlType = PortalURL.URLType.RENDER; break;
            case ACTION_WINDOW_ID_KEY: urlType = PortalURL.URLType.ACTION; break;
            case RESOURCE_WINDOW_ID_KEY: urlType = PortalURL.URLType.RESOURCE; break;
        }
        if (urlType != null)
        {            
            String windowId = parameter.substring(1);
            currentState = states.getPortletWindowNavigationalState(windowId);
            if ( currentState == null )
            {
                states.setURLType(urlType);
                currentState = new PortletWindowRequestNavigationalState(windowId);
                states.addPortletWindowNavigationalState(windowId, currentState);
                states.setTargetWindowId(windowId);
            }
        }
        else if (currentState != null)
        {
            switch (parameterType)
            {
                case MODE_KEY:
                {
                    PortletMode portletMode = decodePortletMode(parameter.charAt(1));
                    if ( portletMode != null )
                    {
                        currentState.setPortletMode(portletMode);
                    }
                    break;
                }
                case PORTLET_MANAGED_MODE_KEY:
                {
                    currentState.setPortletMode(new PortletMode(parameter.substring(1)));
                    break;
                }
                case STATE_KEY:
                {
                    WindowState windowState = decodeWindowState(parameter.charAt(1));
                    if ( windowState != null )
                    {
                        currentState.setWindowState(windowState);
                    }
                    break;
                }
                case PARAM_KEY:
                case PRIVATE_RENDER_PARAM_KEY:
                case PUBLIC_RENDER_PARAM_KEY:
                {
                    String [] parameterName = new String[1];
                    String [][] parameterValues = new String[1][1];
                    if (decodeParamsParameter(parameter, parameterName, parameterValues))
                    {
                        switch (parameterType)
                        {
                            case PARAM_KEY:
                            {
                                // set parameter state
                                currentState.setParameters(parameterName[0], parameterValues[0]);
                                break;
                            }
                            case PRIVATE_RENDER_PARAM_KEY:
                            {
                                // set private render parameter state for resource urls
                                if (PortalURL.URLType.RESOURCE.equals(states.getURLType()))
                                {
                                    currentState.setPrivateRenderParameters(parameterName[0], parameterValues[0]);
                                }
                                break;
                            }
                            case PUBLIC_RENDER_PARAM_KEY:
                            {
                                // set public render parameter states
                                if (parameterValues[0] == null || parameterValues[0].length == 0)
                                {
                                    currentState.setPublicRenderParameters(parameterName[0], (String[])null);
                                }
                                else
                                {
                                    currentState.setPublicRenderParameters(parameterName[0], parameterValues[0]);
                                }
                                break;
                            }
                        }
                    }
                    break;
                }
                case CLEAR_PARAMS_KEY:
                {
                    currentState.setClearParameters(true);
                    break;
                }
                case CACHE_LEVEL_KEY:
                case RESOURCE_ID_KEY:
                case ACTION_SCOPE_ID_KEY:
                case RENDERED_ACTION_SCOPE_ID_KEY:
                {
                    parameter = parameter.substring(1);
                    switch (parameterType)
                    {                        
                        case CACHE_LEVEL_KEY:
                        {
                            // set cache level state for resource urls
                            if (PortalURL.URLType.RESOURCE.equals(states.getURLType()))
                            {
                                currentState.setCacheLevel(parameter);
                            }
                            break;
                        }
                        case RESOURCE_ID_KEY:
                        {
                            // set resource id state for resource urls
                            if (PortalURL.URLType.RESOURCE.equals(states.getURLType()))
                            {
                                currentState.setResourceId(parameter);
                            }
                            break;                            
                        }
                        case ACTION_SCOPE_ID_KEY:
                        {
                            // set action scope state
                            currentState.setActionScopeId(parameter);
                            currentState.setActionScopeRendered(false);
                            break;
                        }
                        case RENDERED_ACTION_SCOPE_ID_KEY:
                        {
                            // set action scope state
                            currentState.setActionScopeId(parameter);
                            currentState.setActionScopeRendered(true);
                            break;
                        }
                    }
                    break;
                }
            }
        }
        return currentState;
    }

    protected boolean decodeParamsParameter(String parameter, String [] parameterName, String [][] parameterValues)
    {
        int position = 1;
        int length = parameter.length();
        StringBuffer buffer = new StringBuffer();
        int parameterValueCount = -1;
        int parameterValueIndex = -1;
        while ( (position = decodeArgument(position, length, parameter, buffer, PARAMETER_ELEMENT_SEPARATOR)) != -1 )
        {
            if ( parameterName[0] == null )
            {
                parameterName[0] = buffer.toString();
                parameterValueCount = -1;                        
            }
            else if ( parameterValueCount == -1 )
            {
                parameterValueCount = Integer.parseInt(buffer.toString(), 16);
                if (parameterValueCount < 0)
                {
                    return false;
                }
                else if (parameterValueCount == 0)
                {
                    parameterValues[0] = null;
                    return true;
                }
                else
                {
                    parameterValues[0] = new String[parameterValueCount];
                    parameterValueIndex = 0;
                }
            }
            else
            {
                parameterValues[0][parameterValueIndex++] = buffer.toString();
                parameterValueCount--;
                if ( parameterValueCount == 0 )
                {
                    return true;
                }
            }
        }
        return false;
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
     * the three '+', '/' and '=' which has some significance in an URL
     * are encoded on top of the Base64 encoding and are first translated back before decoding.
     * 
     * @param value
     * @param characterEncoding String containing the name of the chararacter encoding
     * @return decoded string
     */
    protected String decodeParameters(String value, String characterEncoding)
    throws UnsupportedEncodingException
    {
        value = value.replace('-','/').replace('_','=').replace('.','+');
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
     * the three '+', '/' and '=' which has some significance in an URL
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
        return value.replace('/','-').replace('=','_').replace('+','.');
    }

    protected String encodeArgument( String argument, char terminator )
    {
        int length = argument.length();
        StringBuffer buffer = new StringBuffer(length);
        buffer.setLength(0);
        char c;
        for ( int i = 0; i < length; i++ )
        {
            c = argument.charAt(i);
            if (c == terminator)
            {
                buffer.append(PARAMETER_SEPARATOR_ESCAPE);
            }
            buffer.append(c);
            if ( c == PARAMETER_SEPARATOR_ESCAPE )
            {
                buffer.append(c);
            }
        }
        return buffer.toString();
    }
    
    protected int decodeArgument(int position, int maxLength, String arguments, StringBuffer buffer, char terminator)
    {
        buffer.setLength(0);
        if (position > maxLength)
        {
            return -1;
        }
        char c;
        for ( ; position < maxLength; position++ )
        {
            c = arguments.charAt(position);
            if (c == terminator)
            {    
                position++;
                break;
            }
            else if ( c != PARAMETER_SEPARATOR_ESCAPE )
            {
                buffer.append(c);
            }
            else 
            {
                position++;
                if ( position < maxLength )
                {
                    buffer.append(arguments.charAt(position));
                }
                else
                {
                    break;
                }
            }
        }
        return position; 
    }
}
