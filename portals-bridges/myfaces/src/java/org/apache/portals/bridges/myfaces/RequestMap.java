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
package org.apache.portals.bridges.myfaces;

import java.util.Enumeration;

import javax.portlet.PortletContext;
import javax.portlet.PortletRequest;

import org.apache.portals.bridges.myfaces.AbstractAttributeMap;

/**
 * <p>{@link PortletRequest} attributes Map.</p>
 * <p>
 * See MyFaces project for servlet implementation.
 * </p>
 * 
 * @author <a href="dlestrat@apache.org">David Le Strat</a>
 */
public class RequestMap extends AbstractAttributeMap
{
	/** Illegal argument exception message. */
	final private static String ILLEGAL_ARGUMENT = "Only PortletContext supported";
	/** The {@link PortletContext}. */
	private final PortletRequest portletRequest;

    /**
     * @param request The request.
     */
    public RequestMap(Object request)
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
     * @see org.apache.portals.bridges.myfaces.AbstractAttributeMap#getAttribute(java.lang.String)
     */
    public Object getAttribute(String key)
    {
        if (null != this.portletRequest)
        {
        	return this.portletRequest.getAttribute(key);
        }
        else
        {
        	throw new IllegalArgumentException(ILLEGAL_ARGUMENT);
        }
    }

    /**
     * @see org.apache.portals.bridges.myfaces.AbstractAttributeMap#setAttribute(java.lang.String, java.lang.Object)
     */
    public void setAttribute(String key, Object value)
    {
    	if (null != this.portletRequest)
        {
    		this.portletRequest.setAttribute(key, value);
        }
    }

    /**
     * @see org.apache.portals.bridges.myfaces.AbstractAttributeMap#removeAttribute(java.lang.String)
     */
    public void removeAttribute(String key)
    {
    	if (null != this.portletRequest)
        {
    		this.portletRequest.removeAttribute(key);
        }
    }

    /**
     * @see org.apache.portals.bridges.myfaces.AbstractAttributeMap#getAttributeNames()
     */
    public Enumeration getAttributeNames()
    {
    	if (null != this.portletRequest)
        {
    		return this.portletRequest.getAttributeNames();
        }
    	else
        {
        	throw new IllegalArgumentException(ILLEGAL_ARGUMENT);
        }
    }
}
