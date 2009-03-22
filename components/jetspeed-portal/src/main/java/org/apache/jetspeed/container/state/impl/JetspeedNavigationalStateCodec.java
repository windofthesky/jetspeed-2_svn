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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.portlet.PortletMode;
import javax.portlet.WindowState;
import javax.xml.namespace.QName;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.JetspeedActions;
import org.apache.jetspeed.PortalContext;
import org.apache.jetspeed.container.url.PortalURL;
import org.apache.jetspeed.container.window.PortletWindowAccessor;
import org.apache.jetspeed.container.PortletWindow;
import org.apache.jetspeed.om.portlet.ContainerRuntimeOption;
import org.apache.jetspeed.om.portlet.PortletApplication;
import org.apache.jetspeed.om.portlet.PortletDefinition;
import org.apache.jetspeed.om.portlet.PublicRenderParameter;

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
    protected static final char RESOURCE_WINDOW_ID_KEY = 'g';
    protected static final char CACHE_LEVEL_KEY = 'h';
    protected static final char RESOURCE_ID_KEY = 'i';
    protected static final char PRIVATE_RENDER_PARAM_KEY = 'j';
    protected static final char PUBLIC_RENDER_PARAM_KEY = 'k';
    protected static final char ACTION_SCOPE_ID_KEY = 'l';
    protected static final char RENDERED_ACTION_SCOPE_ID_KEY = 'm';
    protected static final char[] URLTYPE_ID_KEYS = { 'b', 'g', 'a' };
    
    protected static final String keytable = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    protected final PortletMode[] portletModes;
    protected final WindowState[] windowStates;
    private final PortletWindowAccessor windowAccessor;
    
    public JetspeedNavigationalStateCodec(PortalContext portalContext, PortletWindowAccessor windowAccessor)
    {
        ArrayList<PortletMode> modesList = new ArrayList<PortletMode>();
        this.windowAccessor = windowAccessor;
        
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
            StringBuffer buffer = new StringBuffer();            
            PortletWindowRequestNavigationalState currentState = null;
            String parameter;
            while ( (position = decodeArgument(position, decodedParameters, buffer, PARAMETER_SEPARATOR )) != -1 )
            {
                parameter = buffer.toString();
                currentState = decodeParameter( windowAccessor, states, currentState, parameter);
            }

            // propagate public parameter state to states
            if (states.getPublicRenderParametersMap() != null)
            {
                Iterator<String> windowIdIter = states.getWindowIdIterator();
                while (windowIdIter.hasNext())
                {
                    PortletWindowRequestNavigationalState state = states.getPortletWindowNavigationalState(windowIdIter.next());
                    PortletWindow window = windowAccessor.getPortletWindow(state.getWindowId());
                    if (window != null)
                    {
                        PortletApplication pa = window.getPortletEntity().getPortletDefinition().getApplication();
                        if (pa.getPublicRenderParameters() != null)
                        {
                            for (PublicRenderParameter publicRenderParameter : pa.getPublicRenderParameters())
                            {
                                QName parameterQName = publicRenderParameter.getQName();
                                String[] parameterValues = states.getPublicRenderParametersMap().get(parameterQName);
                                if (parameterValues != null)
                                {
                                    state.setPublicRenderParameters(publicRenderParameter.getIdentifier(), parameterValues);
                                }
                            }
                        }
                    }
                }
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
        String actionWindowId = states.getActionWindow() != null ? states.getActionWindow().getId().toString() : "";
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
        String windowId = window.getId().toString();
        PortletWindowRequestNavigationalState currentState = states.getPortletWindowNavigationalState(windowId);
        PortletWindowRequestNavigationalState targetState = new PortletWindowRequestNavigationalState(windowId, getActionScopedRequestAttributes(window));
        targetState.setPortletMode(portletMode != null ? portletMode : currentState != null ? currentState.getPortletMode() : null);
        targetState.setWindowState(windowState != null ? windowState : currentState != null ? currentState.getWindowState() : null);

        // never retain actionRequest parameters nor session stored renderParameters
        if ( currentState != null && !renderParamsStateFull )
        {
            // retain current request parameters if any
            if ( currentState.getParametersMap() != null )
            {
                Iterator<Map.Entry<String, String[]>> parametersIter = currentState.getParametersMap().entrySet().iterator();
                Map.Entry<String, String[]> entry;
                while ( parametersIter.hasNext())
                {
                    entry = parametersIter.next();
                    targetState.setParameters(entry.getKey(), entry.getValue());
                }
            }
        }
        // encode as requestURL parameters
        return encode(states, windowId, targetState, PortalURL.URLType.RENDER, navParamsStateFull, renderParamsStateFull);
    }

    public String encode(PortletWindowRequestNavigationalStates states, PortletWindow window, Map<String, String[]> parameters,
                         String actionScopeId, boolean actionScopeRendered, String cacheLevel, String resourceId,
                         Map<String, String[]> privateRenderParameters, Map<String, String[]> publicRenderParameters,
                         PortletMode portletMode, WindowState windowState, PortalURL.URLType urlType, boolean navParamsStateFull, 
                         boolean renderParamsStateFull)
    throws UnsupportedEncodingException
    {
        String windowId = window.getId().toString();
        PortletWindowRequestNavigationalState currentState = states.getPortletWindowNavigationalState(windowId);
        PortletWindowRequestNavigationalState targetState = new PortletWindowRequestNavigationalState(windowId, getActionScopedRequestAttributes(window));
        targetState.setPortletMode(portletMode != null ? portletMode : currentState != null ? currentState.getPortletMode() : null);
        targetState.setWindowState(windowState != null ? windowState : currentState != null ? currentState.getWindowState() : null);
        targetState.setParametersMap(parameters);
        targetState.setActionScopeId(actionScopeId);
        targetState.setActionScopeRendered(actionScopeRendered);
        targetState.setCacheLevel(cacheLevel);
        targetState.setResourceId(resourceId);
        targetState.setPrivateRenderParametersMap(privateRenderParameters);
        setStatePublicRenderParametersMap(states, targetState, publicRenderParameters);
        if ( renderParamsStateFull && parameters == null )
        {
            // Indicate that the saved (in the session) render parameters for this PortletWindow must be cleared
            // and not copied when synchronizing the state (encoded as CLEAR_PARAMS_KEY)
            targetState.setClearParameters(true);
        }
        return encode(states, windowId, targetState, urlType, navParamsStateFull, renderParamsStateFull);
    }

    public void setStatePublicRenderParametersMap(PortletWindowRequestNavigationalStates requestStates, PortletWindowRequestNavigationalState requestState, Map<String, String[]> publicRenderParametersMap)
    {
        // set public render parameters map for state
        boolean resetStatesPublicRenderParameters = (requestState.getPublicRenderParametersMap() != null);
        requestState.setPublicRenderParametersMap(publicRenderParametersMap);
        if (resetStatesPublicRenderParameters)
        {
            // reset request states public render parameters map
            requestStates.setPublicRenderParametersMap(null);
            // repopulate request states public render parameters map
            Iterator<String> iter = requestStates.getWindowIdIterator();
            while (iter.hasNext())
            {
                String iterWindowId = iter.next();
                PortletWindowRequestNavigationalState iterRequestState = requestStates.getPortletWindowNavigationalState(iterWindowId);
                if (iterRequestState != requestState)
                {
                    updateStatesPublicRenderParametersMap(requestStates, iterRequestState, iterRequestState.getPublicRenderParametersMap());
                }
            }
        }
        // update request states public render parameters map
        updateStatesPublicRenderParametersMap(requestStates, requestState, publicRenderParametersMap);
    }
        
    public void updateStatesPublicRenderParametersMap(PortletWindowRequestNavigationalStates requestStates, PortletWindowRequestNavigationalState requestState, Map<String, String[]> publicRenderParametersMap)
    {
        if (publicRenderParametersMap != null)
        {
            // update request states public render parameters map
            String windowId = requestState.getWindowId();
            for (Map.Entry<String, String[]> parameter : publicRenderParametersMap.entrySet())
            {
                String parameterName = parameter.getKey();
                String[] parameterValues = parameter.getValue();
                // get qname for request public render parameter name
                QName parameterQName = getPublicRenderParameterQName(windowId, parameterName);
                if (parameterQName != null)
                {
                    requestStates.setPublicRenderParameters(parameterQName, parameterValues);
                }
            }
        }            
    }
        
    public String encode(PortletWindowRequestNavigationalStates states, boolean navParamsStateFull, boolean renderParamsStateFull)
    throws UnsupportedEncodingException
    {
        return encode(states, null, null, PortalURL.URLType.RENDER, navParamsStateFull, renderParamsStateFull);
    }
    
    protected String encode(PortletWindowRequestNavigationalStates states, String targetWindowId, 
                            PortletWindowRequestNavigationalState targetState, PortalURL.URLType urlType,
                            boolean navParamsStateFull, boolean renderParamsStateFull)
    throws UnsupportedEncodingException
    {
        StringBuffer buffer = new StringBuffer();
        boolean haveState = false;
        boolean encodeTargetWindowPublicRenderParams = true;
        
        // skip other states if all non-targeted PortletWindow states are kept in the session
        if (!navParamsStateFull || !renderParamsStateFull)
        {
            // encode individual request states, (skip target request state encoded below)
            Iterator<String> iter = states.getWindowIdIterator();
            while (iter.hasNext())
            {
                String windowId = iter.next();
                PortletWindowRequestNavigationalState requestState = states.getPortletWindowNavigationalState(windowId);
                if ((targetWindowId == null) || !windowId.equals(targetWindowId))
                {
                    encodeTargetWindowPublicRenderParams = false;
                    String encodedState = encodePortletWindowNavigationalState(windowId, requestState, PortalURL.URLType.RENDER, navParamsStateFull, renderParamsStateFull, false);
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
            }
        }
        // encode target request state
        if (targetWindowId != null)
        {
            String encodedState = encodePortletWindowNavigationalState(targetWindowId, targetState, urlType, false, false, encodeTargetWindowPublicRenderParams);
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
        // encode shared public render parameter request states
        // if they have not been encoded on the target window
        if (haveState && !encodeTargetWindowPublicRenderParams)
        {
            String encodedState = encodePublicRenderParameterState(states, urlType, navParamsStateFull, renderParamsStateFull);
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
        // return encoded state
        return (haveState ? encodeParameters(buffer.toString(), states.getCharacterEncoding()) : null);
    }
    
    public boolean getActionScopedRequestAttributes(PortletWindow window)
    {
        // get action scoped request attributes option from portlet definition
        PortletDefinition pd = window.getPortletEntity().getPortletDefinition();
        ContainerRuntimeOption actionScopedRequestAttributesOption = pd.getContainerRuntimeOption(ContainerRuntimeOption.ACTION_SCOPED_REQUEST_ATTRIBUTES_OPTION);
        if (actionScopedRequestAttributesOption == null)
        {
            actionScopedRequestAttributesOption = pd.getApplication().getContainerRuntimeOption(ContainerRuntimeOption.ACTION_SCOPED_REQUEST_ATTRIBUTES_OPTION);                    
        }
        return ((actionScopedRequestAttributesOption != null) && (actionScopedRequestAttributesOption.getValues() != null) && (actionScopedRequestAttributesOption.getValues().size() > 0) && "true".equals(actionScopedRequestAttributesOption.getValues().get(0)));
    }
    
    public boolean getActionScopedRequestAttributes(String windowId)
    {
        // access portlet window and get action scoped request attributes option
        PortletWindow window = windowAccessor.getPortletWindow(windowId);
        if ( window != null )
        {
            return getActionScopedRequestAttributes(window);
        }
        return false;
    }

    public QName getPublicRenderParameterQName(PortletWindow window, String identifier)
    {
        // get public render parameter qname from portlet application
        PortletApplication pa = window.getPortletEntity().getPortletDefinition().getApplication();
        PublicRenderParameter publicRenderParameter = pa.getPublicRenderParameter(identifier);
        return publicRenderParameter.getQName();
    }
    
    public QName getPublicRenderParameterQName(String windowId, String identifier)
    {
        // access portlet window and get public render parameter qname
        PortletWindow window = windowAccessor.getPortletWindow(windowId);
        if (window != null)
        {
            return getPublicRenderParameterQName(window, identifier);
        }
        return null;
    }

    public Map<String, QName> getPublicRenderParameterNamesMap(PortletWindow window)
    {
        // get public render parameter names from portlet application
        PortletApplication pa = window.getPortletEntity().getPortletDefinition().getApplication();
        if (pa.getPublicRenderParameters() != null)
        {
            Map<String, QName> parameterNames = new HashMap<String, QName>();
            for (PublicRenderParameter publicRenderParameter : pa.getPublicRenderParameters())
            {
                parameterNames.put(publicRenderParameter.getIdentifier(), publicRenderParameter.getQName());
            }
            return parameterNames;
        }
        return null;
    }
    
    public Map<String, QName> getPublicRenderParameterNamesMap(String windowId)
    {
        // access portlet window and get public render parameter names
        PortletWindow window = windowAccessor.getPortletWindow(windowId);
        if (window != null)
        {
            return getPublicRenderParameterNamesMap(window);
        }
        return null;
    }

    public boolean hasPublicRenderParameterQNames(PortletWindow window, Set<QName> qnames)
    {
        // test public render parameter qnames from portlet application
        PortletApplication pa = window.getPortletEntity().getPortletDefinition().getApplication();
        if (pa.getPublicRenderParameters() != null)
        {
            for (PublicRenderParameter publicRenderParameter : pa.getPublicRenderParameters())
            {
                if (qnames.contains(publicRenderParameter.getQName()))
                {
                    return true;
                }
            }
        }
        return false;
    }
    
    public boolean hasPublicRenderParameterQNames(String windowId, Set<QName> qnames)
    {
        // access portlet window and test public render parameter qnames
        PortletWindow window = windowAccessor.getPortletWindow(windowId);
        if (window != null)
        {
            return hasPublicRenderParameterQNames(window, qnames);
        }
        return false;
    }

    protected String encodePublicRenderParameterState(PortletWindowRequestNavigationalStates states, 
                                                      PortalURL.URLType urlType, boolean navParamsStateFull, 
                                                      boolean renderParamsStateFull)
    {
        StringBuffer buffer = new StringBuffer();
        boolean encoded = false;
        
        if ((PortalURL.URLType.ACTION.equals(urlType) || !renderParamsStateFull))
        {
            if (states.getPublicRenderParametersMap() != null)
            {
                encoded = encodeParameterMap(encoded, PARAM_KEY, states.getPublicRenderParametersMap(), buffer);
            }
        }
        
        return encoded ? buffer.toString() : "";
    }

    protected String encodePortletWindowNavigationalState(String windowId, PortletWindowRequestNavigationalState state, 
                                                          PortalURL.URLType urlType, boolean navParamsStateFull, 
                                                          boolean renderParamsStateFull, boolean encodePublicRenderParams)
    {
        StringBuffer buffer = new StringBuffer();
        buffer.append(URLTYPE_ID_KEYS[urlType.ordinal()]);
        buffer.append(windowId);
        boolean encoded = !PortalURL.URLType.RENDER.equals(urlType);
        
        if (PortalURL.URLType.ACTION.equals(urlType) || !navParamsStateFull)
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

        if ((PortalURL.URLType.ACTION.equals(urlType) || !renderParamsStateFull))
        {
            if (state.getParametersMap() != null)
            {
                encoded = encodeParameterMap(encoded, PARAM_KEY, state.getParametersMap(), buffer);
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
            if (state.getCacheLevel() != null)
            {
                encoded = true;
                buffer.append(PARAMETER_SEPARATOR);
                buffer.append(CACHE_LEVEL_KEY);
                buffer.append(encodeArgument(state.getActionScopeId(), PARAMETER_SEPARATOR));
            }
            if (state.getResourceId() != null)
            {
                encoded = true;
                buffer.append(PARAMETER_SEPARATOR);
                buffer.append(CACHE_LEVEL_KEY);
                buffer.append(encodeArgument(state.getResourceId(), PARAMETER_SEPARATOR));
            }
            if (state.getPrivateRenderParametersMap() != null)
            {
                encoded = encodeParameterMap(encoded, PRIVATE_RENDER_PARAM_KEY, state.getPrivateRenderParametersMap(), buffer);
            }
        }
        
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
        
        if (encodePublicRenderParams && (state.getPublicRenderParametersMap() != null) && (PortalURL.URLType.ACTION.equals(urlType) || !renderParamsStateFull))
        {
            // generate subset of public render parameters for this state
            Map<QName, String[]> publicRenderParams = new HashMap<QName, String[]>();
            for (Map.Entry<String, String[]> publicRenderParam : state.getPublicRenderParametersMap().entrySet())
            {
                String parameterName = publicRenderParam.getKey();
                String[] parameterValues = publicRenderParam.getValue();
                QName parameterQName = getPublicRenderParameterQName(windowId, parameterName);
                if (parameterQName != null)
                {
                    publicRenderParams.put(parameterQName, parameterValues);
                }
            }
            encoded = encodeParameterMap(encoded, PUBLIC_RENDER_PARAM_KEY, publicRenderParams, buffer);
        }
        
        return encoded ? buffer.toString() : "";
    }
    
    protected boolean encodeParameterMap(boolean encoded, char paramsKey, Map<? extends Object, String[]> params, StringBuffer buffer)
    {
        StringBuffer paramBuffer = new StringBuffer();
        for (Map.Entry<? extends Object, String[]> entry : params.entrySet())
        {
            encoded = true;
            buffer.append(PARAMETER_SEPARATOR);

            Object parameterNameObject = entry.getKey();
            String parameterName = ((parameterNameObject instanceof QName) ? encodeQName((QName)parameterNameObject): parameterNameObject.toString());
            String [] parameterValues = entry.getValue();
            paramBuffer.setLength(0);
            paramBuffer.append(encodeArgument(parameterName, PARAMETER_ELEMENT_SEPARATOR));
            paramBuffer.append(PARAMETER_ELEMENT_SEPARATOR);
            paramBuffer.append(Integer.toHexString(parameterValues.length));
            for ( int i = 0; i < parameterValues.length; i++ )
            {
                paramBuffer.append(PARAMETER_ELEMENT_SEPARATOR);
                paramBuffer.append(encodeArgument(parameterValues[i], PARAMETER_ELEMENT_SEPARATOR));
            }

            buffer.append(paramsKey);
            buffer.append(encodeArgument(paramBuffer.toString(),PARAMETER_SEPARATOR));
        }
        return encoded;
    }
    
    protected PortletWindowRequestNavigationalState decodeParameter(PortletWindowAccessor accessor, PortletWindowRequestNavigationalStates states, PortletWindowRequestNavigationalState currentState, String parameter)
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
                PortletWindow window = accessor.getPortletWindow(windowId);
                if ( window == null )
                {
                    window = accessor.createPortletWindow(windowId);
                }
                currentState = new PortletWindowRequestNavigationalState(windowId, getActionScopedRequestAttributes(window));
                states.addPortletWindowNavigationalState(windowId, currentState);
                if ( parameterType == ACTION_WINDOW_ID_KEY )
                {
                    states.setActionWindow(window);
                }
                else if (parameterType != RENDER_WINDOW_ID_KEY )
                {
                    states.setResourceWindow(window);
                }
                states.setURLType(urlType);
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
                case STATE_KEY:
                {
                    WindowState windowState = decodeWindowState(parameter.charAt(1));
                    if ( windowState != null )
                    {
                        currentState.setWindowState(windowState);
                        if (windowState.equals(WindowState.MAXIMIZED) || windowState.equals(JetspeedActions.SOLO_STATE))
                        {
                            PortletWindow window = accessor.getPortletWindow(currentState.getWindowId());
                            if ( window == null )
                            {
                                window = accessor.createPortletWindow(currentState.getWindowId());
                            }                                    
                            states.setMaximizedWindow(window);
                        }
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
                                // set private render parameter state
                                currentState.setPrivateRenderParameters(parameterName[0], parameterValues[0]);
                                break;
                            }
                            case PUBLIC_RENDER_PARAM_KEY:
                            {
                                // set public render parameter states
                                QName parameterQName = decodeQName(parameterName[0]);
                                if (parameterQName != null)
                                {
                                    states.setPublicRenderParameters(parameterQName, parameterValues[0]);
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
                            currentState.setCacheLevel(parameter);
                            break;
                        }
                        case RESOURCE_ID_KEY:
                        {
                            currentState.setResourceId(parameter);
                            break;                            
                        }
                        case ACTION_SCOPE_ID_KEY:
                        {
                            currentState.setActionScopeId(parameter);
                            currentState.setActionScopeRendered(false);
                            break;
                        }
                        case RENDERED_ACTION_SCOPE_ID_KEY:
                        {
                            currentState.setActionScopeId(parameter);
                            currentState.setActionScopeRendered(true);
                            break;
                        }
                    }
                    break;
                }
            }
        }
        else
        {
            switch (parameterType)
            {
                case PUBLIC_RENDER_PARAM_KEY:
                {
                    String [] parameterName = new String[1];
                    String [][] parameterValues = new String[1][1];
                    if (decodeParamsParameter(parameter, parameterName, parameterValues))
                    {
                        // set public render parameter states
                        QName parameterQName = decodeQName(parameterName[0]);
                        if (parameterQName != null)
                        {
                            states.setPublicRenderParameters(parameterQName, parameterValues[0]);
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
        StringBuffer buffer = new StringBuffer();
        int parameterValueCount = -1;
        int parameterValueIndex = -1;
        while ( (position = decodeArgument(position, parameter, buffer, PARAMETER_ELEMENT_SEPARATOR)) != -1 )
        {
            if ( parameterName[0] == null )
            {
                parameterName[0] = buffer.toString();
                parameterValueCount = -1;                        
            }
            else if ( parameterValueCount == -1 )
            {
                parameterValueCount = Integer.parseInt(buffer.toString(), 16);
                parameterValues[0] = new String[parameterValueCount];
                parameterValueIndex = 0;
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
        value = value.replace('-','/').replace('_','=');
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
        return value.replace('/','-').replace('=','_');
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
    
    protected QName decodeQName(String qnameString)
    {
        int namespacePrefixSeparator = qnameString.indexOf("//:");
        if (namespacePrefixSeparator != -1)
        {
            int prefixLocalpartSeparator = qnameString.indexOf(":", namespacePrefixSeparator+3);
            if (prefixLocalpartSeparator != -1)
            {
                String namespace = (namespacePrefixSeparator > 0) ? qnameString.substring(0, namespacePrefixSeparator) : null;
                String prefix = (prefixLocalpartSeparator-namespacePrefixSeparator > 3) ? qnameString.substring(namespacePrefixSeparator+3, namespacePrefixSeparator): null;
                String localpart = (namespacePrefixSeparator+1 < qnameString.length()) ? qnameString.substring(namespacePrefixSeparator+1) : null;
                if (localpart != null)
                {
                    if (namespace == null)
                    {
                        return new QName(localpart);
                    }
                    else if (prefix == null)
                    {
                        return new QName(namespace, localpart);                    
                    }
                    else
                    {
                        return new QName(namespace, localpart, prefix);                        
                    }
                }
            }
        }
        return null;
    }

    protected String encodeQName(QName qname)
    {
        String namespace = qname.getNamespaceURI();
        namespace = (namespace != null) ? namespace : "";
        String prefix = qname.getPrefix();
        prefix = (prefix != null) ? prefix : "";
        String localpart = qname.getLocalPart();
        
        StringBuilder builder = new StringBuilder();
        builder.append(namespace);
        builder.append("//:");
        builder.append(prefix);
        builder.append(":");
        builder.append(localpart);
        return builder.toString();
    }
}
