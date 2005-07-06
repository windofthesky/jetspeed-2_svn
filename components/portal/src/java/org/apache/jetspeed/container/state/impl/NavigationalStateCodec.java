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
import java.util.Map;

import javax.portlet.PortletMode;
import javax.portlet.WindowState;

import org.apache.pluto.om.window.PortletWindow;

public interface NavigationalStateCodec
{
    PortletWindowRequestNavigationalStates decode(String parameters, String characterEncoding) throws UnsupportedEncodingException;

    String encode(PortletWindowRequestNavigationalStates states, PortletWindow window, PortletMode portletMode,
            WindowState windowState, boolean navParamsStateFull, boolean renderParamsStateFull) throws UnsupportedEncodingException;

    String encode(PortletWindowRequestNavigationalStates states, PortletWindow window, Map parameters,
            PortletMode portletMode, WindowState windowState, boolean action, boolean navParamsStateFull, 
            boolean renderParamsStateFull) throws UnsupportedEncodingException;
}
