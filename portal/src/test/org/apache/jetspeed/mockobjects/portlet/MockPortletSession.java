/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2000-2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *    "Apache Jetspeed" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Apache Jetspeed", nor may "Apache" appear in their name, without
 *    prior written permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.jetspeed.mockobjects.portlet;

import java.util.Enumeration;
import java.util.Hashtable;
import javax.portlet.PortletContext;
import javax.portlet.PortletSession;

/**
 * A mock portlet session, useful for unit testing and offline utilities
 * Note: currently doesn't support scoping
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class MockPortletSession implements PortletSession
{
    // Hashtable (not HashMap) makes enumerations easier to work with
    Hashtable attributes = new Hashtable();

    public MockPortletSession()
    {     
    }
    
    
    /* (non-Javadoc)
     * @see javax.portlet.PortletSession#getAttribute(java.lang.String)
     */
    public Object getAttribute(String name)
    {
        return attributes.get(name);
    }
    
    /* (non-Javadoc)
     * @see javax.portlet.PortletSession#getAttribute(java.lang.String, int)
     */
    public Object getAttribute(String name, int scope)
    {
        return attributes.get(name);
    }
    
    /* (non-Javadoc)
     * @see javax.portlet.PortletSession#getAttributeNames(int)
     */
    public Enumeration getAttributeNames(int scope)
    {
        return attributes.keys();
    }
    
    /* (non-Javadoc)
     * @see javax.portlet.PortletSession#getCreationTime()
     */
    public long getCreationTime()
    {
        // TODO Auto-generated method stub
        return 0;
    }
    
    /* (non-Javadoc)
     * @see javax.portlet.PortletSession#getId()
     */
    public String getId()
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    /* (non-Javadoc)
     * @see javax.portlet.PortletSession#getLastAccessedTime()
     */
    public long getLastAccessedTime()
    {
        // TODO Auto-generated method stub
        return 0;
    }
    
    /* (non-Javadoc)
     * @see javax.portlet.PortletSession#getMaxInactiveInterval()
     */
    public int getMaxInactiveInterval()
    {
        // TODO Auto-generated method stub
        return 0;
    }
    
    /* (non-Javadoc)
     * @see javax.portlet.PortletSession#invalidate()
     */
    public void invalidate()
    {
        // TODO Auto-generated method stub
    }
    
    /* (non-Javadoc)
     * @see javax.portlet.PortletSession#isNew()
     */
    public boolean isNew()
    {
        // TODO Auto-generated method stub
        return false;
    }
    
    /* (non-Javadoc)
     * @see javax.portlet.PortletSession#removeAttribute(java.lang.String)
     */
    public void removeAttribute(String name)
    {
        attributes.remove(name);
    }
    
    /* (non-Javadoc)
     * @see javax.portlet.PortletSession#removeAttribute(java.lang.String, int)
     */
    public void removeAttribute(String name, int scope)
    {
        attributes.remove(name);
    }
    
    /* (non-Javadoc)
     * @see javax.portlet.PortletSession#setAttribute(java.lang.String, java.lang.Object)
     */
    public void setAttribute(String name, Object value)
    {
        attributes.put(name, value);
    }

    public Enumeration getAttributeNames()
    {
        return this.getAttributeNames(PortletSession.PORTLET_SCOPE);
    }    
    
    
    /* (non-Javadoc)
     * @see javax.portlet.PortletSession#setAttribute(java.lang.String, java.lang.Object, int)
     */
    public void setAttribute(String name, Object value, int scope)
    {
        attributes.put(name, value);
    }
    
    /* (non-Javadoc)
     * @see javax.portlet.PortletSession#setMaxInactiveInterval(int)
     */
    public void setMaxInactiveInterval(int interval)
    {
        // TODO Auto-generated method stub
    }
    /* (non-Javadoc)
     * @see javax.portlet.PortletSession#getPortletContext()
     */
    public PortletContext getPortletContext()
    {
        // TODO Auto-generated method stub
        return null;
    }
}
