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
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URLClassLoader;
import java.util.LinkedList;
import java.util.List;

import javax.portlet.Event;
import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.JetspeedActions;
import org.apache.jetspeed.PortalReservedParameters;
import org.apache.jetspeed.aggregator.PortletTrackingManager;
import org.apache.jetspeed.container.PortletWindow;
import org.apache.jetspeed.container.providers.EventProviderImpl;
import org.apache.jetspeed.container.window.PortletWindowAccessor;
import org.apache.jetspeed.om.page.Fragment;
import org.apache.jetspeed.om.portlet.PortletDefinition;
import org.apache.jetspeed.request.RequestContext;
import org.apache.jetspeed.security.SecurityAccessController;
import org.apache.jetspeed.statistics.PortalStatistics;
import org.apache.pluto.EventContainer;
import org.apache.pluto.om.portlet.EventDefinition;
import org.apache.pluto.om.portlet.EventDefinitionReference;
import org.apache.pluto.om.portlet.PortletApplicationDefinition;
import org.apache.pluto.spi.EventProvider;

/**
 * Event Coordination service default implementation. Uses JAXB to serialize/deserialize events.
 * 
 * 2.2.1 TODO: future options 
 * (1)optionsNotifyCurrentPage, optionsNotifyAnyPortlet 
 * (2) use WorkerMonitor to process events in parallel.
 *   Will require the WorkMonitor polymorphically processing off of base class
 *   'BaseMonitorJob' to both RenderingJob and new class EventJob 
 * (3) Support fragment to window mapping
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class EventCoordinationServiceImpl implements EventCoordinationService
{
    private static Log log = LogFactory.getLog(EventProviderImpl.class);

    private final PortletWindowAccessor windowAccessor;
    private final PortletEventQueue eventQueue;
    private final PortalStatistics statistics;
    private final PortletTrackingManager portletTracking;
    private final SecurityAccessController accessController;
    private boolean checkSecurityConstraints = true;

    public EventCoordinationServiceImpl(final PortletWindowAccessor windowAccessor, final PortletEventQueue eventQueue,
            final PortalStatistics statistics, PortletTrackingManager portletTracking, SecurityAccessController accessController,
            boolean checkSecurityConstraints)
    {
        this.windowAccessor = windowAccessor;
        this.eventQueue = eventQueue;
        this.statistics = statistics;
        this.portletTracking = portletTracking;
        this.accessController = accessController;
        this.checkSecurityConstraints = checkSecurityConstraints;
    }

    public EventProvider createEventProvider(HttpServletRequest request, org.apache.pluto.PortletWindow portletWindow)
    {
        return new EventProviderImpl(request, portletWindow, this);
    }

    public void registerToFireEvent(QName qname, Serializable value, org.apache.pluto.PortletWindow wnd) throws IllegalArgumentException
    {
        PortletWindow portletWindow = (PortletWindow)wnd;
        if (isDeclaredAsPublishingEvent(portletWindow, qname))
        {
            if (value != null && !isValueInstanceOfDefinedClass(portletWindow, qname, value)) 
            { 
                throw new IllegalArgumentException(
                    "Payload has not the right class"); 
            }
            if (value == null)
            {
                throw new IllegalArgumentException("Object payload must be not null");
            }
            else if (!(value instanceof Serializable))
            {
                throw new IllegalArgumentException("Object payload must implement Serializable");
            }
            else
            {
                String result = null;
                Serializable out  = serialize(value, qname);
                if (out != null)
                {
                    result = out.toString();
                }
                if (result != null)
                {
                    eventQueue.publishEvent(new ProcessEventImpl(portletWindow, qname, value.getClass().getName(), (Serializable) result, this));
                }
                else
                {
                    eventQueue.publishEvent(new ProcessEventImpl(portletWindow, qname, value.getClass().getName(), value, this));
                }
            }
        }
    }

    public void fireEvents(EventContainer eventContainer, org.apache.pluto.PortletWindow wnd, HttpServletRequest servletRequest)
    {
        PortletWindow portletWindow = (PortletWindow)wnd;
        RequestContext rc = (RequestContext) servletRequest.getAttribute(PortalReservedParameters.REQUEST_CONTEXT_ATTRIBUTE);
        List<ProcessEvent> events = eventQueue.dequeueEvents(portletWindow);
        if (events == null)
            return;
        long start = System.currentTimeMillis();        
        for (ProcessEvent event : events)
        {
            if (event.isProcessed())
                continue;
            event.setProcessed(true);
            List<PortletWindow> windows = getAllPortletsRegisteredForEvent(rc, event);
            for (PortletWindow window : windows)
            {
                HttpServletRequest request = rc.getRequestForWindow(window);
                request.setAttribute(PortalReservedParameters.PAGE_ATTRIBUTE, rc.getPage());
                request.setAttribute(PortalReservedParameters.REQUEST_CONTEXT_ATTRIBUTE, rc);
                request.setAttribute(PortalReservedParameters.REQUEST_CONTEXT_OBJECTS, rc.getObjects());
                try
                {
                    eventContainer.fireEvent(request, rc.getResponseForWindow(window), window, event);
                }
                catch (Exception e)
                {
                    log.error("Failed to process event: " + event, e);
                }
            }
        }
        long end = System.currentTimeMillis();        
        if (statistics != null)
        {
            statistics.logPortletAccess(rc, portletWindow.getPortletEntity().getPortletDefinition().getUniqueName(), PortalStatistics.HTTP_EVENT, end - start);
        }
    }

    private boolean isDeclaredAsPublishingEvent(PortletWindow portletWindow, QName qname)
    {
        PortletDefinition pd = (PortletDefinition) portletWindow.getPortletEntity().getPortletDefinition();
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
                if (qname.equals(name)) { return true; }
            }
        }
        return false;
    }

    private boolean isValueInstanceOfDefinedClass(PortletWindow portletWindow, QName qname, Serializable value)
    {
        PortletApplicationDefinition app = portletWindow.getPortletEntity().getPortletDefinition().getApplication();
        List<? extends EventDefinition> events = app.getEventDefinitions();
        if (events != null)
        {
            for (EventDefinition def : events)
            {
                if (def.getQName() != null)
                {
                    if (def.getQName().equals(qname)) { return value.getClass().getName().equals(def.getValueType()); }
                }
                else
                {
                    QName tmp = new QName(app.getDefaultNamespace(), def.getName());
                    if (tmp.equals(qname)) { return value.getClass().getName().equals(def.getValueType()); }
                }
            }
        }
        return true;
    }

    private List<PortletWindow> getAllPortletsRegisteredForEvent(RequestContext rc, Event event)
    {
        Fragment root = rc.getPage().getRootFragment();
        List<PortletWindow> eventTargets = new LinkedList<PortletWindow>();
        return getPortletsRegisteredOnPage(root, event, eventTargets);
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
        PortletWindow portletWindow = windowAccessor.getPortletWindow(fragment.getId());
        if (portletWindow == null)
            return eventTargets;

        PortletDefinition portlet = (PortletDefinition) portletWindow.getPortletEntity().getPortletDefinition();

        if (checkSecurityConstraints && !checkSecurityConstraint(portlet, fragment)) 
        {
            return eventTargets;
        }

        if (portletTracking.isOutOfService((org.apache.jetspeed.container.PortletWindow)portletWindow))
        {
            return eventTargets;
        }
        List<? extends EventDefinitionReference> processingEvents = portlet.getSupportedProcessingEvents();
        if (isEventSupported(processingEvents, event.getQName(), portlet.getApplication().getDefaultNamespace()))
        {
            eventTargets.add(portletWindow);
        }
        return eventTargets;
    }

    protected boolean checkSecurityConstraint(PortletDefinition portlet, Fragment fragment)
    {
        if (fragment.getType().equals(Fragment.PORTLET))
        {
            
            if (accessController != null) // TODO: MASK_EVENT 
            { 
                return accessController.checkPortletAccess(portlet, JetspeedActions.MASK_VIEW);
            }
        }
        return true;
    }

    private boolean isEventSupported(List<? extends EventDefinitionReference> supportedEvents, QName eventName, String defaultNamespace)
    {
        if (supportedEvents != null)
        {
            for (EventDefinitionReference ref : supportedEvents)
            {
                QName refQName = ref.getQualifiedName(defaultNamespace);
                if (refQName != null && refQName.equals(eventName)) { return true; }
            }
        }
        return false;
    }

    // TODO: implement after Ate's refactoring completed
    public void processEvents(org.apache.pluto.PortletWindow portletWindow, List<Event> events)
    {

    }

    public Serializable serialize(Serializable value, QName eventQName) 
    {
        Serializable xmlData = null;
        if (value != null) 
        {
            ClassLoader savedLoader = Thread.currentThread().getContextClassLoader();
            try 
            {                                                
                Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());                
                JAXBContext jaxbContext = JAXBContext.newInstance(value.getClass());
                Marshaller marshaller = jaxbContext.createMarshaller();
                Writer out = new StringWriter();
                JAXBElement<Serializable> element = new JAXBElement(eventQName, value.getClass(), value);
                marshaller.marshal(element, out);
                xmlData = out.toString(); 
            } 
            catch(JAXBException e) 
            {
                log.error("Failed to serialize: " + value, e);
            }
            finally
            {
                Thread.currentThread().setContextClassLoader(savedLoader);
            }
            
        }
        return xmlData;
    }    
    
    
 
    
    public Serializable deserialize(Event event)  
    {
        ProcessEvent processEvent = (ProcessEvent)event;
        ClassLoader savedLoader = Thread.currentThread().getContextClassLoader();                
        Serializable deserializedValue = null;
        Serializable value = processEvent.getRawValue();
        if (value != null) 
        {
            Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());                            
            XMLStreamReader xml = null;
            if (value instanceof String) 
            {
                try 
                {                    
                    xml = XMLInputFactory.newInstance().createXMLStreamReader(new StringReader((String) value));
                } 
                catch (Exception e)
                {
                    log.error("Failed to stream for de-serialization: " + value, e);
                    xml = null;
                }
            }           
            if (xml != null) 
            {
                try 
                {
                    Class<? extends Serializable> clazz = savedLoader.loadClass(processEvent.getClassName()).asSubclass(Serializable.class);
                    JAXBContext jaxbContext = JAXBContext.newInstance(clazz);
                    Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
                    JAXBElement result = unmarshaller.unmarshal(xml, clazz);
                    deserializedValue = (Serializable) result.getValue();
                } 
                catch (Exception e)
                {
                    log.error("Failed to de-serializee: " + value, e);
                    xml = null;
                }

            }
        }
        Thread.currentThread().setContextClassLoader(savedLoader);        
        return deserializedValue;
    }
    
}
