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
import java.util.LinkedList;
import java.util.List;

import javax.portlet.Event;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.jetspeed.JetspeedActions;
import org.apache.jetspeed.aggregator.PortletTrackingManager;
import org.apache.jetspeed.container.PortletWindow;
import org.apache.jetspeed.container.providers.EventProviderImpl;
import org.apache.jetspeed.om.page.ContentFragment;
import org.apache.jetspeed.om.portlet.PortletDefinition;
import org.apache.jetspeed.request.RequestContext;
import org.apache.jetspeed.security.SecurityAccessController;
import org.apache.jetspeed.statistics.PortalStatistics;
import org.apache.pluto.container.EventProvider;
import org.apache.pluto.container.PortletContainer;
import org.apache.pluto.container.om.portlet.EventDefinition;
import org.apache.pluto.container.om.portlet.EventDefinitionReference;
import org.apache.pluto.container.om.portlet.PortletApplicationDefinition;

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
public class EventCoordinationServiceImpl implements JetspeedEventCoordinationService
{
    private static Logger log = LoggerFactory.getLogger(EventProviderImpl.class);

    private final PortalStatistics statistics;
    private final PortletTrackingManager portletTracking;
    private final SecurityAccessController accessController;
    private boolean checkSecurityConstraints = true;

    public EventCoordinationServiceImpl(final PortletEventQueue eventQueue,
            final PortalStatistics statistics, PortletTrackingManager portletTracking, SecurityAccessController accessController,
            boolean checkSecurityConstraints)
    {
        this.statistics = statistics;
        this.portletTracking = portletTracking;
        this.accessController = accessController;
        this.checkSecurityConstraints = checkSecurityConstraints;
    }

    public EventProvider createEventProvider(HttpServletRequest request, org.apache.pluto.container.PortletWindow portletWindow)
    {
        return new EventProviderImpl(request, (PortletWindow)portletWindow, this);
    }

    public Event createEvent(HttpServletRequest request, PortletWindow portletWindow, QName qname, Serializable value ) throws IllegalArgumentException
    {
        if (isDeclaredAsPublishingEvent(portletWindow, qname))
        {
            if (value != null && !isValueInstanceOfDefinedClass(portletWindow, qname, value)) 
            { 
                throw new IllegalArgumentException(
                    "Payload has not the right class"); 
            }
            if (value == null)
            {
                return new ProcessEventImpl(portletWindow, qname, null, null, this);
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
                    return new ProcessEventImpl(portletWindow, qname, value.getClass().getName(), (Serializable) result, this);
                }
                else
                {
                    return new ProcessEventImpl(portletWindow, qname, value.getClass().getName(), value, this);
                }
            }
        }
        return null;
    }

    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.events.JetspeedEventCoordinationService#processEvents(org.apache.pluto.container.PortletContainer, org.apache.pluto.container.PortletWindow, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, java.util.List)
     */
    public void processEvents(PortletContainer container, org.apache.pluto.container.PortletWindow wnd,
                              HttpServletRequest servletRequest, HttpServletResponse servletResponse, List<Event> events)
    {
        PortletWindow portletWindow = (PortletWindow)wnd;
        long start = System.currentTimeMillis();        
        for (Event portletEvent : events)
        {
            ProcessEvent event = (ProcessEvent)portletEvent;
            if (event.isProcessed())
                continue;
            event.setProcessed(true);
            List<PortletWindow> windows = getAllPortletsRegisteredForEvent(portletWindow.getRequestContext(), event);
            for (PortletWindow window : windows)
            {
                try
                {
                    container.doEvent(window, servletRequest, servletResponse, event);
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
            statistics.logPortletAccess(portletWindow.getRequestContext(), portletWindow.getPortletDefinition().getUniqueName(), 
                                        PortalStatistics.HTTP_EVENT, end - start);
        }
    }

    private boolean isDeclaredAsPublishingEvent(PortletWindow portletWindow, QName qname)
    {
        PortletDefinition pd = portletWindow.getPortletDefinition();
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
        PortletApplicationDefinition app = portletWindow.getPortletDefinition().getApplication();
        List<? extends EventDefinition> events = app.getEventDefinitions();
        if (events != null)
        {
            for (EventDefinition def : events)
            {
                if (def.getValueType() != null)
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
        }
        return true;
    }

    private List<PortletWindow> getAllPortletsRegisteredForEvent(RequestContext rc, Event event)
    {
        ContentFragment root = rc.getPage().getRootFragment();
        List<PortletWindow> eventTargets = new LinkedList<PortletWindow>();
        return getPortletsRegisteredOnPage(rc, root, event, eventTargets);
    }

    private List<PortletWindow> getPortletsRegisteredOnPage(RequestContext rc, ContentFragment fragment, Event event, List<PortletWindow> eventTargets)
    {
        List<ContentFragment> fragments = fragment.getFragments();
        if (fragments != null && fragments.size() > 0)
        {
            for (ContentFragment child : fragments)
            {
                getPortletsRegisteredOnPage(rc, child, event, eventTargets);
            }
        }
        // might need to create the PortletWindow if yet undefined
        PortletWindow portletWindow = rc.getPortletWindow(fragment);
        if (portletWindow == null || !portletWindow.isValid())
            return eventTargets;

        PortletDefinition portlet = portletWindow.getPortletDefinition();

        if (checkSecurityConstraints && !checkSecurityConstraint(portlet, fragment)) 
        {
            return eventTargets;
        }

        if (portletTracking.isOutOfService(portletWindow))
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

    protected boolean checkSecurityConstraint(PortletDefinition portlet, ContentFragment fragment)
    {
        if (fragment.getType().equals(ContentFragment.PORTLET))
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
                finally
                {
                    if (xml == null)
                    {
                        Thread.currentThread().setContextClassLoader(savedLoader);
                    }
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
                finally
                {
                    Thread.currentThread().setContextClassLoader(savedLoader);                            
                }
            }
        }
        return deserializedValue;
    }
}
