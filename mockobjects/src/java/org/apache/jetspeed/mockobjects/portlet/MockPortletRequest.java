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

import java.security.Principal;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;

import javax.portlet.PortalContext;
import javax.portlet.PortletMode;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;
import javax.portlet.WindowState;

/**
 * A mock portlet request, useful for unit testing and offline utilities
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class MockPortletRequest implements PortletRequest
{
    MockPortletSession session = null;
    
    public MockPortletRequest()
    {
        session = new MockPortletSession();     
    }
    
    /* (non-Javadoc)
     * @see javax.portlet.PortletRequest#isWindowStateAllowed(javax.portlet.WindowState)
     */
    public boolean isWindowStateAllowed(WindowState state)
    {
        // TODO Auto-generated method stub
        return false;
    }
    /* (non-Javadoc)
     * @see javax.portlet.PortletRequest#isPortletModeAllowed(javax.portlet.PortletMode)
     */
    public boolean isPortletModeAllowed(PortletMode mode)
    {
        // TODO Auto-generated method stub
        return false;
    }
    /* (non-Javadoc)
     * @see javax.portlet.PortletRequest#getPortletMode()
     */
    public PortletMode getPortletMode()
    {
        // TODO Auto-generated method stub
        return null;
    }
    /* (non-Javadoc)
     * @see javax.portlet.PortletRequest#getWindowState()
     */
    public WindowState getWindowState()
    {
        // TODO Auto-generated method stub
        return null;
    }
    /* (non-Javadoc)
     * @see javax.portlet.PortletRequest#getPreferences()
     */
    public PortletPreferences getPreferences()
    {
        // TODO Auto-generated method stub
        return null;
    }
    /* (non-Javadoc)
     * @see javax.portlet.PortletRequest#getPortletSession()
     */
    public PortletSession getPortletSession()
    {
        return session;
    }
    /* (non-Javadoc)
     * @see javax.portlet.PortletRequest#getPortletSession(boolean)
     */
    public PortletSession getPortletSession(boolean create)
    {
        if (session == null)
        {
            session = new MockPortletSession();
        }
        return session;
    }
    /* (non-Javadoc)
     * @see javax.portlet.PortletRequest#getProperty(java.lang.String)
     */
    public String getProperty(String name)
    {
        // TODO Auto-generated method stub
        return null;
    }
    /* (non-Javadoc)
     * @see javax.portlet.PortletRequest#getProperties(java.lang.String)
     */
    public Enumeration getProperties(String name)
    {
        // TODO Auto-generated method stub
        return null;
    }
    /* (non-Javadoc)
     * @see javax.portlet.PortletRequest#getPropertyNames()
     */
    public Enumeration getPropertyNames()
    {
        // TODO Auto-generated method stub
        return null;
    }
    /* (non-Javadoc)
     * @see javax.portlet.PortletRequest#getPortalContext()
     */
    public PortalContext getPortalContext()
    {
        // TODO Auto-generated method stub
        return null;
    }
    /* (non-Javadoc)
     * @see javax.portlet.PortletRequest#getAuthType()
     */
    public String getAuthType()
    {
        // TODO Auto-generated method stub
        return null;
    }
    /* (non-Javadoc)
     * @see javax.portlet.PortletRequest#getContextPath()
     */
    public String getContextPath()
    {
        // TODO Auto-generated method stub
        return null;
    }
    /* (non-Javadoc)
     * @see javax.portlet.PortletRequest#getRemoteUser()
     */
    public String getRemoteUser()
    {
        // TODO Auto-generated method stub
        return null;
    }
    /* (non-Javadoc)
     * @see javax.portlet.PortletRequest#getUserPrincipal()
     */
    public Principal getUserPrincipal()
    {
        // TODO Auto-generated method stub
        return null;
    }
    /* (non-Javadoc)
     * @see javax.portlet.PortletRequest#isUserInRole(java.lang.String)
     */
    public boolean isUserInRole(String role)
    {
        // TODO Auto-generated method stub
        return false;
    }
    /* (non-Javadoc)
     * @see javax.portlet.PortletRequest#getAttribute(java.lang.String)
     */
    public Object getAttribute(String name)
    {
        // TODO Auto-generated method stub
        return null;
    }
    /* (non-Javadoc)
     * @see javax.portlet.PortletRequest#getAttributeNames()
     */
    public Enumeration getAttributeNames()
    {
        // TODO Auto-generated method stub
        return null;
    }
    /* (non-Javadoc)
     * @see javax.portlet.PortletRequest#getParameter(java.lang.String)
     */
    public String getParameter(String name)
    {
        // TODO Auto-generated method stub
        return null;
    }
    /* (non-Javadoc)
     * @see javax.portlet.PortletRequest#getParameterNames()
     */
    public Enumeration getParameterNames()
    {
        // TODO Auto-generated method stub
        return null;
    }
    /* (non-Javadoc)
     * @see javax.portlet.PortletRequest#getParameterValues(java.lang.String)
     */
    public String[] getParameterValues(String name)
    {
        // TODO Auto-generated method stub
        return null;
    }
    /* (non-Javadoc)
     * @see javax.portlet.PortletRequest#getParameterMap()
     */
    public Map getParameterMap()
    {
        // TODO Auto-generated method stub
        return null;
    }
    /* (non-Javadoc)
     * @see javax.portlet.PortletRequest#isSecure()
     */
    public boolean isSecure()
    {
        // TODO Auto-generated method stub
        return false;
    }
    /* (non-Javadoc)
     * @see javax.portlet.PortletRequest#setAttribute(java.lang.String, java.lang.Object)
     */
    public void setAttribute(String name, Object o)
    {
        // TODO Auto-generated method stub
    }
    /* (non-Javadoc)
     * @see javax.portlet.PortletRequest#removeAttribute(java.lang.String)
     */
    public void removeAttribute(String name)
    {
        // TODO Auto-generated method stub
    }
    /* (non-Javadoc)
     * @see javax.portlet.PortletRequest#getRequestedSessionId()
     */
    public String getRequestedSessionId()
    {
        // TODO Auto-generated method stub
        return null;
    }
    /* (non-Javadoc)
     * @see javax.portlet.PortletRequest#isRequestedSessionIdValid()
     */
    public boolean isRequestedSessionIdValid()
    {
        // TODO Auto-generated method stub
        return false;
    }
    /* (non-Javadoc)
     * @see javax.portlet.PortletRequest#getResponseContentType()
     */
    public String getResponseContentType()
    {
        // TODO Auto-generated method stub
        return null;
    }
    /* (non-Javadoc)
     * @see javax.portlet.PortletRequest#getResponseContentTypes()
     */
    public Enumeration getResponseContentTypes()
    {
        // TODO Auto-generated method stub
        return null;
    }
    /* (non-Javadoc)
     * @see javax.portlet.PortletRequest#getLocale()
     */
    public Locale getLocale()
    {
        // TODO Auto-generated method stub
        return null;
    }
    /* (non-Javadoc)
     * @see javax.portlet.PortletRequest#getScheme()
     */
    public String getScheme()
    {
        // TODO Auto-generated method stub
        return null;
    }
    /* (non-Javadoc)
     * @see javax.portlet.PortletRequest#getServerName()
     */
    public String getServerName()
    {
        // TODO Auto-generated method stub
        return null;
    }
    /* (non-Javadoc)
     * @see javax.portlet.PortletRequest#getServerPort()
     */
    public int getServerPort()
    {
        // TODO Auto-generated method stub
        return 0;
    }
    
    public Enumeration getLocales()
    {
        return null;
    }
    
}
