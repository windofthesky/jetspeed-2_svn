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
package org.apache.jetspeed.mockobjects.request;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.security.auth.Subject;
import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.jetspeed.aggregator.ContentDispatcher;
import org.apache.jetspeed.capability.CapabilityMap;
import org.apache.jetspeed.container.session.NavigationalState;
import org.apache.jetspeed.container.url.PortalURL;
import org.apache.jetspeed.om.page.Page;
import org.apache.jetspeed.profiler.ProfileLocator;
import org.apache.jetspeed.request.RequestContext;
import org.apache.pluto.om.window.PortletWindow;

/**
 * MockRequestContext
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class MockRequestContext implements RequestContext
{
    private Map requestParameters = new HashMap();
    private Map requestAttributes = new HashMap();
    private Map sessionAttributes = new HashMap();
    private String path;
        
    /* (non-Javadoc)
     * @see org.apache.jetspeed.request.RequestContext#getActionWindow()
     */
    public PortletWindow getActionWindow()
    {
        // TODO Auto-generated method stub
        return null;
    }
    /* (non-Javadoc)
     * @see org.apache.jetspeed.request.RequestContext#getCapabilityMap()
     */
    public CapabilityMap getCapabilityMap()
    {
        // TODO Auto-generated method stub
        return null;
    }
    /* (non-Javadoc)
     * @see org.apache.jetspeed.request.RequestContext#getCharacterEncoding()
     */
    public String getCharacterEncoding()
    {
        // TODO Auto-generated method stub
        return null;
    }
    /* (non-Javadoc)
     * @see org.apache.jetspeed.request.RequestContext#getConfig()
     */
    public ServletConfig getConfig()
    {
        // TODO Auto-generated method stub
        return null;
    }
    /* (non-Javadoc)
     * @see org.apache.jetspeed.request.RequestContext#getContentDispatcher()
     */
    public ContentDispatcher getContentDispatcher()
    {
        // TODO Auto-generated method stub
        return null;
    }
    /* (non-Javadoc)
     * @see org.apache.jetspeed.request.RequestContext#getLocale()
     */
    public Locale getLocale()
    {
        // TODO Auto-generated method stub
        return null;
    }
    /* (non-Javadoc)
     * @see org.apache.jetspeed.request.RequestContext#getMediaType()
     */
    public String getMediaType()
    {
        // TODO Auto-generated method stub
        return null;
    }
    /* (non-Javadoc)
     * @see org.apache.jetspeed.request.RequestContext#getMimeType()
     */
    public String getMimeType()
    {
        // TODO Auto-generated method stub
        return null;
    }
    /* (non-Javadoc)
     * @see org.apache.jetspeed.request.RequestContext#getNavigationalState()
     */
    public NavigationalState getNavigationalState()
    {
        // TODO Auto-generated method stub
        return null;
    }
    /* (non-Javadoc)
     * @see org.apache.jetspeed.request.RequestContext#getPage()
     */
    public Page getPage()
    {
        // TODO Auto-generated method stub
        return null;
    }
    /* (non-Javadoc)
     * @see org.apache.jetspeed.request.RequestContext#getPortalURL()
     */
    public PortalURL getPortalURL()
    {
        // TODO Auto-generated method stub
        return null;
    }
    /* (non-Javadoc)
     * @see org.apache.jetspeed.request.RequestContext#getProfileLocator()
     */
    public ProfileLocator getProfileLocator()
    {
        // TODO Auto-generated method stub
        return null;
    }
    /* (non-Javadoc)
     * @see org.apache.jetspeed.request.RequestContext#getRequest()
     */
    public HttpServletRequest getRequest()
    {
        // TODO Auto-generated method stub
        return null;
    }
    /* (non-Javadoc)
     * @see org.apache.jetspeed.request.RequestContext#getRequestForWindow(org.apache.pluto.om.window.PortletWindow)
     */
    public HttpServletRequest getRequestForWindow( PortletWindow window )
    {
        // TODO Auto-generated method stub
        return null;
    }
    /* (non-Javadoc)
     * @see org.apache.jetspeed.request.RequestContext#getResponse()
     */
    public HttpServletResponse getResponse()
    {
        // TODO Auto-generated method stub
        return null;
    }
    /* (non-Javadoc)
     * @see org.apache.jetspeed.request.RequestContext#getResponseForWindow(org.apache.pluto.om.window.PortletWindow)
     */
    public HttpServletResponse getResponseForWindow( PortletWindow window )
    {
        // TODO Auto-generated method stub
        return null;
    }
    /* (non-Javadoc)
     * @see org.apache.jetspeed.request.RequestContext#getSubject()
     */
    public Subject getSubject()
    {
        // TODO Auto-generated method stub
        return null;
    }
    /* (non-Javadoc)
     * @see org.apache.jetspeed.request.RequestContext#setActionWindow(org.apache.pluto.om.window.PortletWindow)
     */
    public void setActionWindow( PortletWindow window )
    {
        // TODO Auto-generated method stub

    }
    /* (non-Javadoc)
     * @see org.apache.jetspeed.request.RequestContext#setCapabilityMap(org.apache.jetspeed.capability.CapabilityMap)
     */
    public void setCapabilityMap( CapabilityMap map )
    {
        // TODO Auto-generated method stub

    }
    /* (non-Javadoc)
     * @see org.apache.jetspeed.request.RequestContext#setCharacterEncoding(java.lang.String)
     */
    public void setCharacterEncoding( String enc )
    {
        // TODO Auto-generated method stub

    }
    /* (non-Javadoc)
     * @see org.apache.jetspeed.request.RequestContext#setContentDispatcher(org.apache.jetspeed.aggregator.ContentDispatcher)
     */
    public void setContentDispatcher( ContentDispatcher dispatcher )
    {
        // TODO Auto-generated method stub

    }
    /* (non-Javadoc)
     * @see org.apache.jetspeed.request.RequestContext#setLocale(java.util.Locale)
     */
    public void setLocale( Locale locale )
    {
        // TODO Auto-generated method stub

    }
    /* (non-Javadoc)
     * @see org.apache.jetspeed.request.RequestContext#setMediaType(java.lang.String)
     */
    public void setMediaType( String mediaType )
    {
        // TODO Auto-generated method stub

    }
    /* (non-Javadoc)
     * @see org.apache.jetspeed.request.RequestContext#setMimeType(java.lang.String)
     */
    public void setMimeType( String mimeType )
    {
        // TODO Auto-generated method stub

    }
    /* (non-Javadoc)
     * @see org.apache.jetspeed.request.RequestContext#setPage(org.apache.jetspeed.om.page.Page)
     */
    public void setPage( Page page )
    {
        // TODO Auto-generated method stub

    }
    /* (non-Javadoc)
     * @see org.apache.jetspeed.request.RequestContext#setProfileLocator(org.apache.jetspeed.profiler.ProfileLocator)
     */
    public void setProfileLocator( ProfileLocator locator )
    {
        // TODO Auto-generated method stub

    }
    /* (non-Javadoc)
     * @see org.apache.jetspeed.request.RequestContext#setSubject(javax.security.auth.Subject)
     */
    public void setSubject( Subject subject )
    {
        // TODO Auto-generated method stub

    }
    public MockRequestContext()
    {
       // super(null, null, null, null);
    }

    public MockRequestContext(String path)
    {
        // super(null, null, null, null);
        this.path = path;
    }
        
    /* (non-Javadoc)
     * @see org.apache.jetspeed.request.RequestContext#getRequestParameter(java.lang.String)
     */
    public String getRequestParameter(String key)
    {
        return (String)requestParameters.get(key);
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.request.RequestContext#getParameterMap()
     */
    public Map getParameterMap()
    {
        return requestParameters;    
    }
            
    /* (non-Javadoc)
     * @see org.apache.jetspeed.request.RequestContext#setSessionAttribute(java.lang.String, java.lang.Object)
     */
    public void setSessionAttribute(String key, Object value)
    {
        this.sessionAttributes.put(key, value);
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.request.RequestContext#getSessionAttribute(java.lang.String)
     */
    public Object getSessionAttribute(String key)
    {
        return this.sessionAttributes.get(key);
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.request.RequestContext#setAttribute(java.lang.String, java.lang.Object)
     */
    public void setAttribute(String key, Object value)
    {
        requestAttributes.put(key, value);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.request.RequestContext#getAttribute(java.lang.String)
     */
    public Object getAttribute(String key)
    {
        return requestAttributes.get(key);    
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.request.RequestContext#getPath()
     */
    public String getPath()
    {
        return path;
    }
    
}
