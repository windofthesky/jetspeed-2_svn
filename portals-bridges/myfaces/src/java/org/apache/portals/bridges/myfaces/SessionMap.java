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
import javax.portlet.PortletSession;

import net.sourceforge.myfaces.context.AbstractAttributeMap;
import net.sourceforge.myfaces.util.NullEnumeration;

/**
 * HttpSession attibutes as Map.
 * 
 * @author Anton Koinov (latest modification by $Author$)<br>
 *         Refactored to support Portlets by <a href="dlestrat@apache.org">David Le Strat</a>
 */
public class SessionMap extends AbstractAttributeMap
{
	/** Illegal argument exception message. */
	final private static String ILLEGAL_ARGUMENT = "Only PortletContext supported";
	/** The {@link PortletRequest}. */
	private final PortletRequest portletRequest;

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

    protected Object getAttribute(String key)
    {
        if (null != this.portletRequest)
        {
        	PortletSession portletSession = this.portletRequest.getPortletSession(false);
        	return (portletSession == null) 
        		? null : portletSession.getAttribute(key.toString());
        }
        else
        {
        	throw new IllegalArgumentException(ILLEGAL_ARGUMENT);
        }
    }

    protected void setAttribute(String key, Object value)
    {
    	if (null != this.portletRequest)
        {
    		this.portletRequest.getPortletSession(true).setAttribute(key, value);
        }
    }

    protected void removeAttribute(String key)
    {
    	if (null != this.portletRequest)
    	{
    		PortletSession portletSession = this.portletRequest.getPortletSession(false);;
    		if (null != portletSession)
    		{
    			portletSession.removeAttribute(key);
    		}
    	}
    }

    protected Enumeration getAttributeNames()
    {
    	if (null != this.portletRequest)
    	{
    		PortletSession portletSession = this.portletRequest.getPortletSession(false);;
    		return (portletSession == null)
            	? NullEnumeration.instance()
                : portletSession.getAttributeNames();
    	}
    	else
        {
        	throw new IllegalArgumentException(ILLEGAL_ARGUMENT);
        }
    }

}