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
package org.apache.jetspeed.engine.servlet;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;

import javax.servlet.http.HttpSession;

import org.apache.jetspeed.Jetspeed;
import org.apache.jetspeed.container.namespace.JetspeedNamespaceMapper;
import org.apache.jetspeed.container.namespace.JetspeedNamespaceMapperFactory;
import org.apache.pluto.om.common.ObjectID;

/**
 * @author Scott T Weaver
 *  
 */
public class NamespaceEncodedSession extends HttpSessionWrapper
{

    private JetspeedNamespaceMapper nameSpaceMapper;

    private ObjectID webAppId;

    private HashSet mappedNames = new HashSet();

    /**
     * @param session
     */
    public NamespaceEncodedSession(HttpSession session, ObjectID webAppId)
    {
        super(session);
        this.nameSpaceMapper = ((JetspeedNamespaceMapperFactory) Jetspeed.getComponentManager().getComponent(
                org.apache.pluto.util.NamespaceMapper.class)).getJetspeedNamespaceMapper();
        this.webAppId = webAppId;
    }

    /**
     * <p>
     * setAttribute
     * </p>
     * 
     * @see javax.servlet.ServletRequest#setAttribute(java.lang.String,
     *      java.lang.Object)
     * @param arg0
     * @param arg1
     */
    public void setAttribute(String name, Object value)
    {

        if (name == null)
        {
            throw new IllegalArgumentException("Attribute name == null");
        }

        if (skipEncode(name))
        {
            super.setAttribute(name, value);
        }
        else
        {
            String encodedKey = nameSpaceMapper.encode(webAppId, name);
            mappedNames.add(name);
            super.setAttribute(encodedKey, value);
        }

    }

    /**
     * @see javax.servlet.http.HttpServletRequest#getAttribute(java.lang.String)
     */
    public Object getAttribute(String name)
    {
        if (skipEncode(name))
        {
            return super.getAttribute(name);
        }
        else
        {
            return super.getAttribute(nameSpaceMapper.encode(webAppId, name));
        }
    }

    private boolean skipEncode(String name)
    {
        return name.startsWith(nameSpaceMapper.getPrefix()) || name.startsWith("javax.portlet") || name.startsWith("javax.servlet") || name.startsWith("org.apache.jetspeed");
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.servlet.http.HttpSession#getAttributeNames()
     */
    public Enumeration getAttributeNames()
    {
        Enumeration names = super.getAttributeNames();
        while (names.hasMoreElements())
        {
            String name = (String) names.nextElement();
            if (skipEncode(name))
            {
                mappedNames.add(name);
            }
        }

        return Collections.enumeration(mappedNames);
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.servlet.http.HttpSession#removeAttribute(java.lang.String)
     */
    public void removeAttribute(String name)
    {
        if (skipEncode(name))
        {
            super.removeAttribute(name);
        }
        else
        {
            mappedNames.add(name);
            super.removeAttribute(nameSpaceMapper.encode(webAppId, name));
        }
    }
}