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

import net.sourceforge.myfaces.context.servlet.AbstractAttributeMap;

/**
 * <p>{@link PortletContext} attributes as a Map.</p>
 * 
 * @author <a href="dlestrat@apache.org">David Le Strat</a>
 */
public class ApplicationMap extends AbstractAttributeMap
{
    /** Illegal argument exception message. */
	final private static String ILLEGAL_ARGUMENT = "Only PortletContext supported";
	/** The {@link PortletContext}. */
	final private PortletContext portletContext;

    public ApplicationMap(Object context)
    {
        if (context instanceof PortletContext)
        {
        	this.portletContext = (PortletContext) context;
        }
        else
        {
        	throw new IllegalArgumentException(ILLEGAL_ARGUMENT);
        }
    }

    public Object getAttribute(String key)
    {
        if (null != this.portletContext)
        {
        	return this.portletContext.getAttribute(key);
        }
    	else
    	{
    		throw new IllegalArgumentException(ILLEGAL_ARGUMENT);
    	}
    }

    public void setAttribute(String key, Object value)
    {
    	if (null != this.portletContext)
        {
    		this.portletContext.setAttribute(key, value);
        }
    }

    public void removeAttribute(String key)
    {
    	if (null != this.portletContext)
        {
    		this.portletContext.removeAttribute(key);
        }
    }

    public Enumeration getAttributeNames()
    {
    	if (null != this.portletContext)
        {
    		return this.portletContext.getAttributeNames();
        }
    	else
    	{
    		throw new IllegalArgumentException(ILLEGAL_ARGUMENT);
    	}
    }
}
