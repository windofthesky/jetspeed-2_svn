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

package org.apache.jetspeed.container.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.portlet.Event;
import javax.portlet.PortletMode;
import javax.portlet.WindowState;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.pluto.container.EventProvider;
import org.apache.pluto.container.PortletContainer;
import org.apache.pluto.container.PortletStateAwareResponseContext;
import org.apache.jetspeed.container.PortletWindow;
import org.apache.jetspeed.events.JetspeedEventCoordinationService;

/**
 * @version $Id$
 *
 */
public abstract class PortletStateAwareResponseContextImpl extends PortletResponseContextImpl implements
                PortletStateAwareResponseContext
{
    private List<Event> events;
    
    public PortletStateAwareResponseContextImpl(PortletContainer container, HttpServletRequest containerRequest,
                                                HttpServletResponse containerResponse, PortletWindow window)
    {
        super(container, containerRequest, containerResponse, window);
    }
    
    @Override
    public void close()
    {
        if (!isClosed())
        {
            super.close();
            // TODO
        }
    }
    
    @Override
    public void release()
    {
        events = null;
        // TODO
        super.release();
    }
    
    public List<Event> getEvents()
    {
        if (isReleased())
        {
            return null;
        }
        if (events == null)
        {
            events = new ArrayList<Event>();
        }
        return events;
    }

    public PortletMode getPortletMode()
    {
        return isClosed() ? null : null; //TODO
    }

    public Map<String, String[]> getPublicRenderParameters()
    {
        return isClosed() ? null : null; //TODO
    }

    public Map<String, String[]> getRenderParameters()
    {
        return isClosed() ? null : null; //TODO
    }

    public WindowState getWindowState()
    {
        return isClosed() ? null : null; //TODO
    }

    public void setPortletMode(PortletMode portletMode)
    {
        if (!isClosed())
        {
            //TODO
        }
    }

    public void setWindowState(WindowState windowState)
    {
        if (!isClosed())
        {
            //TODO
        }
    }

    public EventProvider getEventProvider()
    {
        if (!isClosed())
        {
            ((JetspeedEventCoordinationService)getContainer().getRequiredContainerServices().getEventCoordinationService()).createEventProvider(getServletRequest(), getPortletWindow());
        }
        return null;
    }
}
