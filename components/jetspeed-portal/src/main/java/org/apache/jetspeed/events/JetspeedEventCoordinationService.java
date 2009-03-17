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
package org.apache.jetspeed.events;

import java.io.Serializable;
import java.util.List;

import javax.portlet.Event;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.namespace.QName;

import org.apache.jetspeed.container.PortletWindow;
import org.apache.pluto.container.EventCoordinationService;
import org.apache.pluto.container.EventProvider;
import org.apache.pluto.container.PortletContainer;

/**
 * TODO: Extend from Pluto's service when checked in and building 
 * Extends Pluto Event Coordination with Jetspeed specific Portlet Event processing
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public interface JetspeedEventCoordinationService extends EventCoordinationService
{
    /**
     * Process all events for a given portlet window and given list of events. 
     * @param portletWindow The window to processs events for
     * @param events The list of one or more events
     */
    void processEvents(PortletContainer container, org.apache.pluto.container.PortletWindow portletWindow, HttpServletRequest request, HttpServletResponse response, List<Event> events);
    
    /**
     * 
     * @param qname
     * @param value
     * @param window
     */
    public Event createEvent(HttpServletRequest request, PortletWindow window, QName qname, Serializable value);
    
    /**
     * 
     * @param request
     * @param portletWindow
     * @return
     */
    public EventProvider createEventProvider(HttpServletRequest request, org.apache.pluto.container.PortletWindow portletWindow);
    
    /**
     * Serialize an event value class representation into a Jetspeed-specific seriaizable form, such as String or XML.
     * @param event The instance 'value' of the event. This class must be serializable.
     * @param eventQName The QName identifying this event to the system.
     * @return
     */
    public Serializable serialize(Serializable event, QName eventQName);
    
    /**
     * Deserialize an event from Jetspeed's internal representation of a serialization into a class. Requires calling serialize on this event first.
     * @param event The event holding the serialized internal form of the event value. This value will be serialized back into a defined class.
     * @return The deserialized Event class
     */
    public Serializable deserialize(Event event); 
}