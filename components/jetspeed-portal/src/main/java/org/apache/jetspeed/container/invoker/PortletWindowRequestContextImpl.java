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

package org.apache.jetspeed.container.invoker;

import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;

import org.apache.jetspeed.container.PortletWindow;
import org.apache.jetspeed.container.PortletWindowRequestContext;
import org.apache.jetspeed.factory.PortletInstance;
import org.apache.pluto.container.PortletRequestContext;
import org.apache.pluto.container.PortletResponseContext;

/**
 * @version $Id$
 *
 */
public class PortletWindowRequestContextImpl implements PortletWindowRequestContext
{
    private final Action action;
    private final PortletWindow portletWindow;
    private final PortletRequest portletRequest;
    private final PortletResponseContext portletResponseContext;
    private final PortletRequestContext portletRequestContext;
    private final PortletResponse portletResponse;
    private final PortletInstance portletInstance;

    public PortletWindowRequestContextImpl(Action action, PortletWindow portletWindow, PortletRequest portletRequest,
                                           PortletResponseContext portletResponseContext,
                                           PortletRequestContext portletRequestContext,
                                           PortletResponse portletResponse, PortletInstance portletInstance)
    {
        this.action = action;
        this.portletWindow = portletWindow;
        this.portletRequest = portletRequest;
        this.portletResponseContext = portletResponseContext;
        this.portletRequestContext = portletRequestContext;
        this.portletResponse = portletResponse;
        this.portletInstance = portletInstance;
    }

    public Action getAction()
    {
        return action;
    }

    public PortletWindow getPortletWindow()
    {
        return portletWindow;
    }

    public PortletRequest getPortletRequest()
    {
        return portletRequest;
    }

    public PortletResponse getPortletResponse()
    {
        return portletResponse;
    }

    public PortletResponseContext getPortletResponseContext()
    {
        return portletResponseContext;
    }

    public PortletRequestContext getPortletRequestContext()
    {
        return portletRequestContext;
    }

    public PortletInstance getPortletInstance()
    {
        return portletInstance;
    }
}
