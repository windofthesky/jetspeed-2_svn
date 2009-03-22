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
import java.util.Map;
import java.util.Set;

import javax.portlet.PortletMode;
import javax.portlet.WindowState;
import javax.xml.namespace.QName;

import org.apache.jetspeed.container.PortletWindow;
import org.apache.jetspeed.container.url.PortalURL;

public interface NavigationalStateCodec
{
    PortletWindowRequestNavigationalStates decode(String parameters, String characterEncoding)
        throws UnsupportedEncodingException;

    String encode(PortletWindowRequestNavigationalStates states, PortletWindow window, PortletMode portletMode,
                  WindowState windowState, boolean navParamsStateFull, boolean renderParamsStateFull)
        throws UnsupportedEncodingException;

    String encode(PortletWindowRequestNavigationalStates states, PortletWindow window, Map<String, String[]> parameters,
                  String actionScopeId, boolean actionScopeRendered, String cacheLevel, String resourceId,
                  Map<String, String[]> privateRenderParameters, Map<String, String[]> publicRenderParameters,
                  PortletMode portletMode, WindowState windowState, PortalURL.URLType urlType, boolean navParamsStateFull, 
                  boolean renderParamsStateFull)
        throws UnsupportedEncodingException;

    String encode(PortletWindowRequestNavigationalStates states, boolean navParamsStateFull, boolean renderParamsStateFull) 
        throws UnsupportedEncodingException;

    void setStatePublicRenderParametersMap(PortletWindowRequestNavigationalStates requestStates, PortletWindowRequestNavigationalState requestState, Map<String, String[]> publicRenderParametersMap);
    void updateStatesPublicRenderParametersMap(PortletWindowRequestNavigationalStates requestStates, PortletWindowRequestNavigationalState requestState, Map<String, String[]> publicRenderParametersMap);

    boolean getActionScopedRequestAttributes(PortletWindow window);
    boolean getActionScopedRequestAttributes(String windowId);

    QName getPublicRenderParameterQName(PortletWindow window, String identifier);
    QName getPublicRenderParameterQName(String windowId, String identifier);

    Map<String, QName> getPublicRenderParameterNamesMap(PortletWindow window);
    Map<String, QName> getPublicRenderParameterNamesMap(String windowId);

    boolean hasPublicRenderParameterQNames(PortletWindow window, Set<QName> qnames);
    boolean hasPublicRenderParameterQNames(String windowId, Set<QName> qnames);
}
