/*
 * Copyright 2000-2004 The Apache Software Foundation.
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
package org.apache.portals.messaging;

import java.io.NotSerializableException;
import java.io.Serializable;

import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;


/**
 * PortletMessageComponent
 * Throwaway Naive implementation of Porlet Messages as an abstraction and a place holder for when the next 
 * spec covers inter-portlet communication
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class PortletMessaging
{
    public static final void publish(PortletRequest request, String portletTopic, String messageName, Object message)
    throws NotSerializableException
    {
        String key = portletTopic + ":" + messageName;
        if (message instanceof Serializable)
        {
            request.getPortletSession().setAttribute(key, message, PortletSession.APPLICATION_SCOPE);
        }
        else
        {
            throw new NotSerializableException("Message not serializable for " + key);
        }
    }

    public static final Object consume(PortletRequest request, String portletTopic, String messageName)
    throws NotSerializableException
    {
        String key = portletTopic + ":" + messageName;
        Object object = request.getPortletSession().getAttribute(key, PortletSession.APPLICATION_SCOPE);
        // consume it
        request.getPortletSession().removeAttribute(key, PortletSession.APPLICATION_SCOPE);        
        return object;
    }

    public static final Object receive(PortletRequest request, String portletTopic, String messageName)
    throws NotSerializableException
    {
        String key = portletTopic + ":" + messageName;
        Object object = request.getPortletSession().getAttribute(key, PortletSession.APPLICATION_SCOPE);
        return object;
    }
    
    public static final void cancel(PortletRequest request, String portletTopic, String messageName)
    throws NotSerializableException
    {
        String key = portletTopic + ":" + messageName;
        request.getPortletSession().removeAttribute(key, PortletSession.APPLICATION_SCOPE);
    }

    public static final void publish(PortletRequest request, String messageName, Object message)
    throws NotSerializableException
    {
        String key = messageName;
        if (message instanceof Serializable)
        {
            request.getPortletSession().setAttribute(key, message, PortletSession.PORTLET_SCOPE);
        }
        else
        {
            throw new NotSerializableException("Message not serializable for " + key);
        }
    }

    public static final Object consume(PortletRequest request, String messageName)
    throws NotSerializableException
    {
        String key = messageName;
        Object object = request.getPortletSession().getAttribute(key, PortletSession.PORTLET_SCOPE);
        // consume it
        request.getPortletSession().removeAttribute(key, PortletSession.PORTLET_SCOPE);        
        return object;
    }

    public static final Object receive(PortletRequest request, String messageName)
    throws NotSerializableException
    {
        String key = messageName;
        Object object = request.getPortletSession().getAttribute(key, PortletSession.PORTLET_SCOPE);
        return object;
    }
    
    public static final void cancel(PortletRequest request, String messageName)
    throws NotSerializableException
    {
        String key = messageName;
        request.getPortletSession().removeAttribute(key, PortletSession.PORTLET_SCOPE);
    }
    
}
