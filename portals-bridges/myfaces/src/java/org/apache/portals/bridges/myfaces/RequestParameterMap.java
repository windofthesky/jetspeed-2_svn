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

import javax.portlet.PortletRequest;

import net.sourceforge.myfaces.context.AbstractAttributeMap;

/**
 * <p>{@link PortletRequest} parameters as Map.</p>
 * 
 * @author <a href="dlestrat@apache.org">David Le Strat</a>
 */
public class RequestParameterMap extends AbstractAttributeMap
{
	/** Illegal argument exception message. */
	final private static String ILLEGAL_ARGUMENT = "Only PortletContext supported";
	/** The {@link PortletRequest}. */
	private final PortletRequest portletRequest;

    public RequestParameterMap(Object request)
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

    public Object getAttribute(String key)
    {
        if (null != this.portletRequest)
        {
        	return this.portletRequest.getParameter(key);
        }
        else
        {
        	throw new IllegalArgumentException(ILLEGAL_ARGUMENT);
        }
    }

    public void setAttribute(String key, Object value)
    {
        throw new UnsupportedOperationException("Cannot set PortletRequest Parameter");
    }

    public void removeAttribute(String key)
    {
        throw new UnsupportedOperationException("Cannot remove PortletRequest Parameter");
    }

    public Enumeration getAttributeNames()
    {
        if (null != this.portletRequest)
        {
        	return this.portletRequest.getParameterNames();
        }
        else
        {
        	throw new IllegalArgumentException(ILLEGAL_ARGUMENT);
        }
    }
}