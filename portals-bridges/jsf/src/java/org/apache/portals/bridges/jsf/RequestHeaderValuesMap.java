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

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.portlet.PortletRequest;


/**
 * <p>
 * This must be the set of properties available via the javax.portlet.PortletRequest methods getProperties()
 * and getPropertyNames(). As such, HTTP headers will only be included if they were provided by the portlet
 * container, and additional properties provided by the portlet container may also be included.
 * </p>
 * <p>
 * See MyFaces project for servlet implementation.
 * </p>
 * 
 * @author <a href="dlestrat@apache.org">David Le Strat </a>
 */
public class RequestHeaderValuesMap extends AbstractAttributeMap
{
    /** The portlet request. */
    private final PortletRequest portletRequest;
    
    /** Value cache. */
    private final Map valueCache = new HashMap();

    /**
     * @param portletRequest The {@link PortletRequest}.
     */
    RequestHeaderValuesMap(PortletRequest portletRequest)
    {
        this.portletRequest = portletRequest;
    }

    /**
     * @see org.apache.portals.bridges.jsf.AbstractAttributeMap#getAttribute(java.lang.String)
     */
    protected Object getAttribute(String key)
    {
        Object ret = valueCache.get(key);
        if (ret == null)
        {
            valueCache.put(key, ret = toArray(portletRequest.getProperties(key)));
        }
        return ret;
    }

    /**
     * @see org.apache.portals.bridges.jsf.AbstractAttributeMap#setAttribute(java.lang.String, java.lang.Object)
     */
    protected void setAttribute(String key, Object value)
    {
        throw new UnsupportedOperationException(
            "Cannot set PortletRequest Properties");
    }

    /**
     * @see org.apache.portals.bridges.jsf.AbstractAttributeMap#removeAttribute(java.lang.String)
     */
    protected void removeAttribute(String key)
    {
        throw new UnsupportedOperationException(
            "Cannot remove PortletRequest Properties");
    }

    /**
     * @see org.apache.portals.bridges.jsf.AbstractAttributeMap#getAttributeNames()
     */
    protected Enumeration getAttributeNames()
    {
        return portletRequest.getPropertyNames();
    }

    /**
     * @param e The enumeration.
     * @return An array of strings.
     */
    private String[] toArray(Enumeration e)
    {
        List ret = new ArrayList();

        while (e.hasMoreElements())
        {
            ret.add(e.nextElement());
        }

        return (String[]) ret.toArray(new String[ret.size()]);
    }
}