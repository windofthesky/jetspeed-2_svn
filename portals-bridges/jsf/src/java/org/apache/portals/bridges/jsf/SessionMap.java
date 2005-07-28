/*
 * Copyright 2004 The Apache Software Foundation.
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
package org.apache.portals.bridges.jsf;

import java.util.Enumeration;

import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;

import org.apache.portals.bridges.jsf.AbstractAttributeMap;
import org.apache.portals.bridges.jsf.NullEnumeration;

/**
 * <p>
 * Session attibutes as Map.
 * </p>
 * <p>
 * See MyFaces project for servlet implementation.
 * </p>
 * 
 * @author <a href="dlestrat@apache.org">David Le Strat </a>
 */
public class SessionMap extends AbstractAttributeMap
{
    /** Illegal argument exception message. */
    final private static String ILLEGAL_ARGUMENT = "Only PortletContext supported";

    /** The {@link PortletRequest}. */
    private final PortletRequest portletRequest;

    /**
     * @param request The request.
     */
    public SessionMap(Object request)
    {
        if (request instanceof PortletRequest)
        {
            this.portletRequest = (PortletRequest) request;
        }
        else
        {
            throw new IllegalArgumentException(ILLEGAL_ARGUMENT);
        }
    }

    /**
     * @see org.apache.portals.bridges.jsf.AbstractAttributeMap#getAttribute(java.lang.String)
     */
    protected Object getAttribute(String key)
    {
        if (null != this.portletRequest)
        {
            PortletSession portletSession = this.portletRequest.getPortletSession(false);
            return (portletSession == null) ? null : portletSession.getAttribute(key.toString());
        }
        else
        {
            throw new IllegalArgumentException(ILLEGAL_ARGUMENT);
        }
    }

    /**
     * @see org.apache.portals.bridges.jsf.AbstractAttributeMap#setAttribute(java.lang.String,
     *      java.lang.Object)
     */
    protected void setAttribute(String key, Object value)
    {
        if (null != this.portletRequest)
        {
            this.portletRequest.getPortletSession(true).setAttribute(key, value);
        }
    }

    /**
     * @see org.apache.portals.bridges.jsf.AbstractAttributeMap#removeAttribute(java.lang.String)
     */
    protected void removeAttribute(String key)
    {
        if (null != this.portletRequest)
        {
            PortletSession portletSession = this.portletRequest.getPortletSession(false);
            ;
            if (null != portletSession)
            {
                portletSession.removeAttribute(key);
            }
        }
    }

    /**
     * @see org.apache.portals.bridges.jsf.AbstractAttributeMap#getAttributeNames()
     */
    protected Enumeration getAttributeNames()
    {
        if (null != this.portletRequest)
        {
            PortletSession portletSession = this.portletRequest.getPortletSession(false);
            ;
            return (portletSession == null) ? NullEnumeration.instance() : portletSession.getAttributeNames();
        }
        else
        {
            throw new IllegalArgumentException(ILLEGAL_ARGUMENT);
        }
    }

}