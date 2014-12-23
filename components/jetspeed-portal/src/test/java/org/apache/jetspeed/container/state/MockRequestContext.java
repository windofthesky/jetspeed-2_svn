/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.jetspeed.container.state;

import org.apache.jetspeed.capabilities.CapabilityMap;
import org.apache.jetspeed.container.PortletWindow;
import org.apache.jetspeed.container.url.PortalURL;
import org.apache.jetspeed.layout.PageLayoutComponent;
import org.apache.jetspeed.om.page.ContentFragment;
import org.apache.jetspeed.om.page.ContentPage;
import org.apache.jetspeed.om.portlet.Language;
import org.apache.jetspeed.om.portlet.PortletDefinition;
import org.apache.jetspeed.pipeline.Pipeline;
import org.apache.jetspeed.portlet.HeadElement;
import org.apache.jetspeed.profiler.Profiler;
import org.apache.jetspeed.request.RequestContext;
import org.apache.jetspeed.util.KeyValue;
import org.apache.jetspeed.window.MockPortletWindow;

import javax.security.auth.Subject;
import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Principal;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @version $Id$
 *
 */
public class MockRequestContext implements RequestContext
{
    
    private HttpServletRequest request;
    private HttpServletResponse response;
    private final Map<String, PortletWindow> portletWindows = new HashMap<String, PortletWindow>();
    
    public void addPortletWindow(PortletWindow window)
    {
        portletWindows.put(window.getId().getStringId(), window);
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.request.RequestContext#getActionWindow()
     */
    public PortletWindow getActionWindow()
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.request.RequestContext#getAttribute(java.lang.String)
     */
    public Object getAttribute(String key)
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
     * @see org.apache.jetspeed.request.RequestContext#getCurrentPortletWindow()
     */
    public PortletWindow getCurrentPortletWindow()
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.request.RequestContext#setCurrentPortletWindow(org.apache.jetspeed.container.PortletWindow)
     */
    public void setCurrentPortletWindow(PortletWindow window)
    {
        // TODO Auto-generated method stub
        
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
     * @see org.apache.jetspeed.request.RequestContext#getObjects()
     */
    public Map<String, Object> getObjects()
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.request.RequestContext#getPage()
     */
    public ContentPage getPage()
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.request.RequestContext#getParameterMap()
     */
    public Map getParameterMap()
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.request.RequestContext#getPath()
     */
    public String getPath()
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.request.RequestContext#getPipeline()
     */
    public Pipeline getPipeline()
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
     * @see org.apache.jetspeed.request.RequestContext#getPortletWindow(java.lang.String)
     */
    public PortletWindow getPortletWindow(String windowId)
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.request.RequestContext#getPortletWindow(org.apache.jetspeed.om.page.ContentFragment)
     */
    public PortletWindow getPortletWindow(ContentFragment fragment)
    {
        return new MockPortletWindow(fragment.getId());
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.request.RequestContext#getPreferedLanguage(org.apache.jetspeed.om.portlet.PortletDefinition)
     */
    public Language getPreferedLanguage(PortletDefinition portlet)
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.request.RequestContext#getProfileLocators()
     */
    public Map getProfileLocators()
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.request.RequestContext#getRequest()
     */
    public HttpServletRequest getRequest()
    {
        return request;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.request.RequestContext#getRequestForWindow(org.apache.jetspeed.container.PortletWindow)
     */
    public HttpServletRequest getRequestForWindow(PortletWindow window)
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.request.RequestContext#getRequestParameter(java.lang.String)
     */
    public String getRequestParameter(String key)
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.request.RequestContext#getResponse()
     */
    public HttpServletResponse getResponse()
    {
        return response;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.request.RequestContext#getResponseForWindow(org.apache.jetspeed.container.PortletWindow)
     */
    public HttpServletResponse getResponseForWindow(PortletWindow window)
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.request.RequestContext#getSessionAttribute(java.lang.String)
     */
    public Object getSessionAttribute(String key)
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
     * @see org.apache.jetspeed.request.RequestContext#getUserInfoMap(java.lang.String)
     */
    public Map<String, String> getUserInfoMap(String appName)
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.request.RequestContext#getUserPrincipal()
     */
    public Principal getUserPrincipal()
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.request.RequestContext#locatePage(org.apache.jetspeed.profiler.Profiler, org.apache.jetspeed.layout.PageLayoutComponent, java.lang.String)
     */
    public ContentPage locatePage(Profiler profiler, PageLayoutComponent pageLayoutComponent, String nonProfiledPath)
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.request.RequestContext#popActionFailure(org.apache.jetspeed.container.PortletWindow)
     */
    public Throwable popActionFailure(PortletWindow window)
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.request.RequestContext#setActionFailure(org.apache.jetspeed.container.PortletWindow, java.lang.Throwable)
     */
    public void setActionFailure(PortletWindow window, Throwable actionFailure)
    {
        // TODO Auto-generated method stub
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.request.RequestContext#setActionWindow(org.apache.jetspeed.container.PortletWindow)
     */
    public void setActionWindow(PortletWindow window)
    {
        // TODO Auto-generated method stub
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.request.RequestContext#setAttribute(java.lang.String, java.lang.Object)
     */
    public void setAttribute(String key, Object value)
    {
        // TODO Auto-generated method stub
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.request.RequestContext#setCapabilityMap(org.apache.jetspeed.capabilities.CapabilityMap)
     */
    public void setCapabilityMap(CapabilityMap map)
    {
        // TODO Auto-generated method stub
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.request.RequestContext#setCharacterEncoding(java.lang.String)
     */
    public void setCharacterEncoding(String enc)
    {
        // TODO Auto-generated method stub
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.request.RequestContext#setLocale(java.util.Locale)
     */
    public void setLocale(Locale locale)
    {
        // TODO Auto-generated method stub
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.request.RequestContext#setMediaType(java.lang.String)
     */
    public void setMediaType(String mediaType)
    {
        // TODO Auto-generated method stub
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.request.RequestContext#setMimeType(java.lang.String)
     */
    public void setMimeType(String mimeType)
    {
        // TODO Auto-generated method stub
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.request.RequestContext#setPage(org.apache.jetspeed.om.page.ContentPage)
     */
    public void setPage(ContentPage page)
    {
        // TODO Auto-generated method stub
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.request.RequestContext#setPath(java.lang.String)
     */
    public void setPath(String path)
    {
        // TODO Auto-generated method stub
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.request.RequestContext#setPipeline(org.apache.jetspeed.pipeline.Pipeline)
     */
    public void setPipeline(Pipeline pipeline)
    {
        // TODO Auto-generated method stub
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.request.RequestContext#setPortalURL(org.apache.jetspeed.container.url.PortalURL)
     */
    public void setPortalURL(PortalURL portalUrl)
    {
        // TODO Auto-generated method stub
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.request.RequestContext#setProfileLocators(java.util.Map)
     */
    public void setProfileLocators(Map locators)
    {
        // TODO Auto-generated method stub
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.request.RequestContext#setRequest(javax.servlet.http.HttpServletRequest)
     */
    public void setRequest(HttpServletRequest request)
    {
        this.request = request;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.request.RequestContext#setResponse(javax.servlet.http.HttpServletResponse)
     */
    public void setResponse(HttpServletResponse response)
    {
        this.response = response;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.request.RequestContext#setSessionAttribute(java.lang.String, java.lang.Object)
     */
    public void setSessionAttribute(String key, Object value)
    {
        // TODO Auto-generated method stub
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.request.RequestContext#setSubject(javax.security.auth.Subject)
     */
    public void setSubject(Subject subject)
    {
        // TODO Auto-generated method stub
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.request.RequestContext#getInstantlyCreatedPortletWindow(java.lang.String,java.lang.String)
     */
    public PortletWindow getInstantlyCreatedPortletWindow(String windowId, String portletUniqueName)
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.request.RequestContext#resolvePortletWindow(java.lang.String)
     */
    public PortletWindow resolvePortletWindow(String windowId)
    {
        return portletWindows.get(windowId);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.request.RequestContext#clearThreadContext()
     */
    public void clearThreadContext()
    {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.request.RequestContext#ensureThreadContext()
     */
    public boolean ensureThreadContext()
    {
        // TODO Auto-generated method stub
        return false;
    }
    
    public List<KeyValue<String, HeadElement>> getMergedHeadElements()
    {
        return Collections.emptyList();
    }

}
