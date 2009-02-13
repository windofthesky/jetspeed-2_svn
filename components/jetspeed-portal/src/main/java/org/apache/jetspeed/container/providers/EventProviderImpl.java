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
import java.io.StringWriter;
import java.io.Writer;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.portlet.Event;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import javax.xml.stream.FactoryConfigurationError;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.PortalReservedParameters;
import org.apache.jetspeed.aggregator.PortletAccessDeniedException;
import org.apache.jetspeed.components.portletentity.PortletEntityNotStoredException;
import org.apache.jetspeed.container.window.FailedToRetrievePortletWindow;
import org.apache.jetspeed.container.window.PortletWindowAccessor;
import org.apache.jetspeed.om.page.Fragment;
import org.apache.jetspeed.om.portlet.PortletDefinition;
import org.apache.jetspeed.om.window.impl.PortletWindowImpl;
import org.apache.jetspeed.request.RequestContext;
import org.apache.pluto.Constants;
import org.apache.pluto.EventContainer;
import org.apache.pluto.PortletWindow;
import org.apache.pluto.internal.impl.EventImpl;
import org.apache.pluto.om.portlet.EventDefinition;
import org.apache.pluto.om.portlet.EventDefinitionReference;
import org.apache.pluto.om.portlet.PortletApplicationDefinition;
import org.apache.pluto.spi.EventProvider;

/**
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: $
 */
public class EventProviderImpl implements EventProvider, Cloneable
{
    private static Log log = LogFactory.getLog(EventProviderImpl.class);
    private final PortletWindow portletWindow;    
    private final RequestContext rc;
    private final PortletWindowAccessor windowAccessor;
    
    // IMPROVEME: (a lot) this is a simple-non-production implementation of an event queue
    private static Map<String, EventList> eventQueue = new HashMap<String, EventList>();
    
    public EventProviderImpl()
    {
        windowAccessor = null;
        rc = null;
        portletWindow = null;
    }

    public EventProviderImpl(final RequestContext rc, final PortletWindow window, final PortletWindowAccessor windowAccessor)
    {
        this.portletWindow = window;
        this.rc = rc;
        this.windowAccessor = windowAccessor;
    }
        
    public void registerToFireEvent(QName qname, Serializable value)
            throws IllegalArgumentException
    {
        System.out.println("registering to fire events");
        EventList savedEvents = new EventList();        
        if (isDeclaredAsPublishingEvent(qname)) 
        {
            if (value != null && !isValueInstanceOfDefinedClass(qname, value))
            {
                throw new IllegalArgumentException("Payload has not the right class");
            }
            try 
            {
                if (value == null) 
                {
                    savedEvents.addEvent(new EventImpl(qname, value));
                } 
                else if (!(value instanceof Serializable)) 
                {
                    throw new IllegalArgumentException(
                            "Object payload must implement Serializable");
                } 
                else 
                {
                    Writer out = new StringWriter();
                    Class clazz = value.getClass();
                    ClassLoader cl = Thread.currentThread().getContextClassLoader();
                    try
                    {
                        Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
                        JAXBContext jc = JAXBContext.newInstance(clazz);
                        Marshaller marshaller = jc.createMarshaller();
                        JAXBElement<Serializable> element = new JAXBElement<Serializable>(
                                qname, clazz, value);
                        marshaller.marshal(element, out);
                        // marshaller.marshal(value, out);
                    }
                    finally
                    {
                        Thread.currentThread().setContextClassLoader(cl);
                    }
                    out = null;
                    if (out != null) 
                    {
                        savedEvents.addEvent(new EventImpl(qname,
                                (Serializable) out.toString()));
                    } 
                    else 
                    {
                        savedEvents.addEvent(new EventImpl(qname, value));
                    }
                }
            } catch (JAXBException e) 
            {
                // maybe there is no valid jaxb binding
                // TODO wsrp:eventHandlingFailed
                log.error("Event handling failed", e);
            } 
            catch (FactoryConfigurationError e) 
            {
                log.warn(e);
            }
        }
        eventQueue.put(this.portletWindow.getId().toString(), savedEvents);
    }
    
    public static final int MAX_EVENTS_SIZE = 10; // TODO
    
    public void fireEvents(EventContainer eventContainer)
    {
        String eventTargetWindow = this.portletWindow.getId().toString();
        System.out.println("firing events for " + eventTargetWindow);
        EventList savedEvents = this.eventQueue.get(eventTargetWindow);
        if (savedEvents == null)
            return;
        while (savedEvents.hasMoreEvents()
                && savedEvents.getSize() < MAX_EVENTS_SIZE) 
        {
            Event event = getArbitraryEvent(savedEvents);
            savedEvents.setProcessed(event);
            
            List<PortletWindow> windows = getAllPortletsRegisteredForEvent(event);

            // iterate all portlets in the portal
            for (PortletWindow window : windows) 
            {
                
                HttpServletRequest request = rc.getRequestForWindow((org.apache.jetspeed.container.PortletWindow)window);
                //this.request.setAttribute(PortalReservedParameters.FRAGMENT_ATTRIBUTE, fragment);
                request.setAttribute(PortalReservedParameters.PAGE_ATTRIBUTE, rc.getPage());
                request.setAttribute(PortalReservedParameters.REQUEST_CONTEXT_ATTRIBUTE, rc);
                request.setAttribute(PortalReservedParameters.REQUEST_CONTEXT_OBJECTS, rc.getObjects());    
                try
                {
                    eventContainer.fireEvent(request, 
                          rc.getResponseForWindow((org.apache.jetspeed.container.PortletWindow)window), window, event);
                }
                catch (Exception e)
                {
                    // TODO: handle
                    e.printStackTrace();
                }
                this.eventQueue.remove(eventTargetWindow);    
// TODO: threading                
//                PortletWindow window = new PortletWindowImpl(container, config, portalURL);
//                if (portletNames != null) {
//                    for (String portlet : portletNames) {
//                        if (portlet.equals(config.getId())) {
//
//                            // the thread now is a new one, with possible
//                            // waiting,
//                            // for the old to exit
//                            
//
//                            PortletWindowThread portletWindowThread = getPortletWindowThread(
//                                    eventContainer, config, window, containerServletContext);
//
//                            // is this event
//                            portletWindowThread.addEvent(eActual);
//
//                            portletWindowThread.start();
//                        }
//                    }
                }
            }
        
//            waitForEventExecution();
//            try {
//                Thread.sleep(WAITING_CYCLE);
//            } catch (InterruptedException e) {
//                LOG.warn(e);
//            }
        
//        waitForEventExecution();        
    }
    
    private boolean isDeclaredAsPublishingEvent(QName qname) 
    {
        PortletDefinition pd = (PortletDefinition)this.portletWindow.getPortletEntity().getPortletDefinition();         
        List<? extends EventDefinitionReference> events = pd.getSupportedPublishingEvents();
        if (events != null) 
        {
            String defaultNamespace = pd.getApplication().getDefaultNamespace();
            for (EventDefinitionReference ref : events) 
            {
                QName name = ref.getQualifiedName(defaultNamespace);
                if (name == null)
                {
                    continue;
                }
                if (qname.equals(name)) 
                {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isValueInstanceOfDefinedClass(QName qname, Serializable value) 
    {
        PortletApplicationDefinition app = portletWindow.getPortletEntity().getPortletDefinition().getApplication();
        List<? extends EventDefinition> events = app.getEventDefinitions();
        if (events != null) 
        {
            for (EventDefinition def : events)
            {
                if (def.getQName() != null)
                {
                    if (def.getQName().equals(qname))
                    {
                        return value.getClass().getName().equals(def.getValueType());
                    }
                }
                else
                {
                    QName tmp = new QName(app.getDefaultNamespace(),def.getName());
                    if (tmp.equals(qname))
                    {
                        return value.getClass().getName().equals(def.getValueType());
                    }
                }
            }
        }
        return true;
    }
    
    private Event getArbitraryEvent(EventList savedEvents) 
    {
        Event eActual = null;
        for (Event event : savedEvents.getEvents()) 
        {
            if (savedEvents.isNotProcessed(event)) 
            {
                eActual = event;
            }
        }
        return eActual;
    }
    
    private boolean optionsNotifyCurrentPage = true;
    private boolean optionsNotifyAnyPortlet = false;
    
    private List<PortletWindow> getAllPortletsRegisteredForEvent(Event event)
    {
        if (optionsNotifyCurrentPage)
        {
            Fragment root = rc.getPage().getRootFragment();
            List<PortletWindow> eventTargets = new LinkedList<PortletWindow>();            
            return getPortletsRegisteredOnPage(root, event, eventTargets);
        }
        else if (optionsNotifyAnyPortlet)
        {
            
        }
        return null;
    }
 
    private List<PortletWindow> getPortletsRegisteredOnPage(Fragment fragment, Event event, List<PortletWindow> eventTargets)
    {
        List<Fragment> fragments = fragment.getFragments();
        if (fragments != null && fragments.size() > 0)
        {
            for (Fragment child : fragments)
            {
                getPortletsRegisteredOnPage(child, event, eventTargets);
            }
        }
        PortletWindow portletWindow = windowAccessor.getPortletWindow(fragment.getId()); // TODO: support fragment to window id
        if (portletWindow == null)
            return eventTargets;
        
        PortletDefinition portlet = (PortletDefinition) portletWindow.getPortletEntity().getPortletDefinition();
        
// TODO: check security        
//        if (checkSecurityConstraints && !checkSecurityConstraint(portletDefinition, fragment))
//        {
//            throw new PortletAccessDeniedException("Access Denied.");
//        }
        
// TODO: check portlet tracking        
//        if (portletTracking.isOutOfService(portletWindow))
//        {
//            log.info("Taking portlet out of service: " + portletDefinition.getUniqueName() + " for window " + fragment.getId());
//            fragment.overrideRenderedContent(OUT_OF_SERVICE_MESSAGE);
//            return;
//        }
        
        List<? extends EventDefinitionReference> processingEvents = portlet.getSupportedProcessingEvents();
        if (isEventSupported(processingEvents, event.getQName(), portlet.getApplication().getDefaultNamespace())) 
        {
            if (portlet.getPortletName().equals(portlet.getPortletName())) // BOZO: WTF?
            {
                eventTargets.add(portletWindow);
            }
        }
        return eventTargets;
    }
    

    private boolean isEventSupported(List<? extends EventDefinitionReference> supportedEvents, QName eventName, String defaultNamespace)
    {
        if (supportedEvents != null)
        {
            for (EventDefinitionReference ref : supportedEvents)
            {
                QName refQName = ref.getQualifiedName(defaultNamespace);
                if (refQName != null && refQName.equals(eventName))
                {
                    return true;
                }
            }
        }
        return false;
    }    
}
