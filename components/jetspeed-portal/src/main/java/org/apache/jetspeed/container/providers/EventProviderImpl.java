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
package org.apache.jetspeed.container.providers;

import java.io.Serializable;

import javax.servlet.http.HttpServletRequest;
import javax.xml.namespace.QName;

import org.apache.jetspeed.container.PortletWindow;
import org.apache.jetspeed.events.EventCoordinationService;
import org.apache.pluto.EventContainer;
import org.apache.pluto.spi.EventProvider;

/**
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: $
 */
public class EventProviderImpl implements EventProvider, Cloneable
{
    private final PortletWindow portletWindow;    
    private final HttpServletRequest request;
    private final EventCoordinationService eventCoordinator;
    
    public EventProviderImpl(final HttpServletRequest request, final org.apache.pluto.PortletWindow window, final EventCoordinationService eventCoordinator)
    {
        this.request = request;
        this.portletWindow = (PortletWindow)window;
        this.eventCoordinator = eventCoordinator;
    }
    
    // FOR ATE's REFACTORING:
    //public Event createEvent(QName name, Serializable value)
   // throws IllegalArgumentException;
    
    
    public void registerToFireEvent(QName qname, Serializable value)
            throws IllegalArgumentException
    {
        eventCoordinator.registerToFireEvent(qname, value, portletWindow);
    }

    public void fireEvents(EventContainer eventContainer)
    {
        eventCoordinator.fireEvents(eventContainer, portletWindow, request);
    }
        
}
