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

import java.util.Enumeration;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;

/**
 * @author Scott T Weaver
 *
 */
public class HttpSessionWrapper implements HttpSession
{
    private HttpSession session;
    
    public HttpSessionWrapper(HttpSession session)
    {
        this.session = session;
    }
    

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj)
    {
        return session.equals(obj);
    }
    /**
     * @param arg0
     * @return
     */
    public Object getAttribute(String arg0)
    {
        return session.getAttribute(arg0);
    }
    /**
     * @return
     */
    public Enumeration getAttributeNames()
    {
        return session.getAttributeNames();
    }
    /**
     * @return
     */
    public long getCreationTime()
    {
        return session.getCreationTime();
    }
    /**
     * @return
     */
    public String getId()
    {
        return session.getId();
    }
    /**
     * @return
     */
    public long getLastAccessedTime()
    {
        return session.getLastAccessedTime();
    }
    /**
     * @return
     */
    public int getMaxInactiveInterval()
    {
        return session.getMaxInactiveInterval();
    }
    /**
     * @return
     */
    public ServletContext getServletContext()
    {
        return session.getServletContext();
    }
    /**
     * @return
     */
    public HttpSessionContext getSessionContext()
    {
        return session.getSessionContext();
    }
    /**
     * @param arg0
     * @return
     */
    public Object getValue(String arg0)
    {
        return session.getValue(arg0);
    }
    /**
     * @return
     */
    public String[] getValueNames()
    {
        return session.getValueNames();
    }
    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    public int hashCode()
    {
        return session.hashCode();
    }
    /**
     * 
     */
    public void invalidate()
    {
        session.invalidate();
    }
    /**
     * @return
     */
    public boolean isNew()
    {
        return session.isNew();
    }
    /**
     * @param arg0
     * @param arg1
     */
    public void putValue(String arg0, Object arg1)
    {
        session.putValue(arg0, arg1);
    }
    /**
     * @param arg0
     */
    public void removeAttribute(String arg0)
    {
        session.removeAttribute(arg0);
    }
    /**
     * @param arg0
     */
    public void removeValue(String arg0)
    {
        session.removeValue(arg0);
    }
    /**
     * @param arg0
     * @param arg1
     */
    public void setAttribute(String arg0, Object arg1)
    {
        session.setAttribute(arg0, arg1);
    }
    /**
     * @param arg0
     */
    public void setMaxInactiveInterval(int arg0)
    {
        session.setMaxInactiveInterval(arg0);
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        return session.toString();
    }
}
