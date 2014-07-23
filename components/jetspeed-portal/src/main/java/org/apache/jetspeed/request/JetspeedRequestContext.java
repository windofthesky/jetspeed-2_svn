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
package org.apache.jetspeed.request;

import org.apache.commons.collections.list.TreeList;
import org.apache.jetspeed.Jetspeed;
import org.apache.jetspeed.PortalReservedParameters;
import org.apache.jetspeed.capabilities.CapabilityMap;
import org.apache.jetspeed.container.ContainerConstants;
import org.apache.jetspeed.container.PortletWindow;
import org.apache.jetspeed.container.url.PortalURL;
import org.apache.jetspeed.layout.PageLayoutComponent;
import org.apache.jetspeed.om.page.BaseFragmentsElement;
import org.apache.jetspeed.om.page.ContentFragment;
import org.apache.jetspeed.om.page.ContentPage;
import org.apache.jetspeed.om.page.Fragment;
import org.apache.jetspeed.om.page.PageTemplate;
import org.apache.jetspeed.om.page.impl.ContentFragmentImpl;
import org.apache.jetspeed.om.portlet.Language;
import org.apache.jetspeed.om.portlet.PortletDefinition;
import org.apache.jetspeed.om.window.impl.PortletWindowImpl;
import org.apache.jetspeed.pipeline.Pipeline;
import org.apache.jetspeed.portalsite.PortalSiteRequestContext;
import org.apache.jetspeed.portalsite.PortalSiteSessionContext;
import org.apache.jetspeed.portlet.HeadElement;
import org.apache.jetspeed.profiler.ProfileLocator;
import org.apache.jetspeed.profiler.Profiler;
import org.apache.jetspeed.profiler.impl.ProfilerValveImpl;
import org.apache.jetspeed.security.SubjectHelper;
import org.apache.jetspeed.security.User;
import org.apache.jetspeed.util.HeadElementsUtils;
import org.apache.jetspeed.util.JetspeedLocale;
import org.apache.jetspeed.util.KeyValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.Subject;
import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Jetspeed Request Context is associated with each portal request. The request
 * holds the contextual information shared amongst components in the portal,
 * accessed through a common valve pipeline.
 * 
 * @author <a href="mailto:david@bluesunrise.com">David Sean Taylor </a>
 * @version $Id: JetspeedRequestContext.java,v 1.19 2004/05/25 01:37:12 taylor
 *          Exp $
 */
public class JetspeedRequestContext implements RequestContext
{
    protected final static Logger log = LoggerFactory.getLogger(JetspeedRequestContext.class);
    
    private static final String ACTION_ERROR_ATTR = "org.apache.jetspeed.action.error:";
    private static final String INSTANT_WINDOWS_SESSION_KEY = "org.apache.jetspeed.instant.windows";
    
    private final ThreadLocal<PortletWindow> currentWindow = new ThreadLocal<PortletWindow>();
    
    private RequestContextComponent rcc;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private ServletConfig config;
    private HttpSession session;
    private Map locators;
    private ContentPage page;
    private PortletDefinition portletDefinition;
    private Subject subject;
    private Locale locale;
    private Pipeline pipeline;

    private CapabilityMap capabilityMap;
    private String mimeType;
    private String mediaType;
    private PortalURL url;
    private PortletWindow actionWindow;
    private String encoding;
    private String requestPath = null;
    private final Map<String, Object> objects;
    private final Map<String, PortletWindow> portletWindows;
    
    /**
     * Create a new Request Context
     * 
     * @param rcc
     * @param request
     * @param response
     * @param config
     */
    public JetspeedRequestContext(RequestContextComponent rcc, HttpServletRequest request, HttpServletResponse response, ServletConfig config)
    {
        this(rcc, request, response, config, new HashMap<String, Object>());
    }

    public JetspeedRequestContext(RequestContextComponent rcc, HttpServletRequest request, HttpServletResponse response, ServletConfig config, Map<String, Object> objects)
    {
        this.rcc = rcc;
        this.request = request;
        this.response = response;
        this.config = config;
        this.session = request.getSession();
        this.objects = objects;
        this.portletWindows = new HashMap<String,PortletWindow>();

        // set context in Request for later use
        if (null != this.request)
        {
            this.request.setAttribute(PortalReservedParameters.REQUEST_CONTEXT_ATTRIBUTE, this);
            this.request.setAttribute(ContainerConstants.PORTAL_CONTEXT, this.request.getContextPath());
            this.request.setAttribute(PortalReservedParameters.REQUEST_CONTEXT_OBJECTS, objects);
            
            PortalRequestFactory prf = null;
            try
            {
                prf = Jetspeed.getComponentManager().lookupComponent(PortalRequestFactory.class);
            }
            catch (Throwable t)
            {
                // allow undefined
            }
            if ( prf != null )
            {
                this.request = prf.createPortalRequest(this.request);
            }
            else
            {
                // Simply wrap the current request so we maintain the same
                // level of wrapping.
                // This is needed in the ServletPortletInvoker to get back
                // to the original request.
                this.request = new HttpServletRequestWrapper(this.request);
            }
        }        
    }
    
    public HttpServletRequest getRequest()
    {
        return request;
    }
    
    public HttpServletResponse getResponse()
    {
        return response;
    }

    public ServletConfig getConfig()
    {
        return config;
    }

    public Map<String,ProfileLocator> getProfileLocators()
    {
        return locators;
    }

    public void setProfileLocators( Map<String,ProfileLocator> locators )
    {
        this.locators = locators;
    }

    public ContentPage getPage()
    {
        return this.page;
    }

    public void setPage( ContentPage page )
    {
        this.page = page;
        if (page != null)
        {
            getRequest().setAttribute(PortalReservedParameters.PAGE_ATTRIBUTE, page);
        }
    }

    public PortletDefinition getPortletDefinition()
    {
        return portletDefinition;
    }

    public void setPortletDefinition( PortletDefinition portletDefinition )
    {
        this.portletDefinition = portletDefinition;
    }

    /**
     * get the Capability Map
     *  
     */
    public CapabilityMap getCapabilityMap()
    {
        return this.capabilityMap;
    }

    /**
     * Set the Mimetype. Used by the CapabilityValve
     * 
     * @param mimeType
     */
    public void setMimeType( String mimeType )
    {
        this.mimeType = mimeType;
    }

    /**
     * get the mimeType for the request
     *  
     */
    public String getMimeType()
    {
        return this.mimeType;
    }

    /**
     * Set the mediaType. Used by the CapabilityValve
     * 
     * @param mediaType
     */
    public void setMediaType( String mediaType )
    {
        this.mediaType = mediaType;
    }

    /**
     * get the Media Type
     *  
     */
    public String getMediaType()
    {
        return this.mediaType;
    }

    /**
     * Get the target Portlet Action Window
     * 
     * @return PortletWindow The target portlet window
     */
    public PortletWindow getActionWindow()
    {
        return actionWindow;
    }

    /**
     * Sets the target Portlet Action Window
     * 
     * @param portletWindow
     */
    public void setActionWindow( PortletWindow portletWindow )
    {
        this.actionWindow = portletWindow;
    }


    /**
     * Set the capabilityMap. Used by the CapabilityValve
     * 
     * @param map
     */
    public void setCapabilityMap( CapabilityMap map )
    {
        this.capabilityMap = map;
    }

    /**
     * get the character encoding
     * 
     *  
     */
    public String getCharacterEncoding()
    {
        return this.encoding;
    }

    /**
     * set character encoding
     * 
     * @param enc
     */
    public void setCharacterEncoding( String enc )
    {
        String preferedEnc = (String) session.getAttribute(PortalReservedParameters.PREFERED_CHARACTERENCODING_ATTRIBUTE);

        if (preferedEnc == null || !enc.equals(preferedEnc))
        {
            request.setAttribute(PortalReservedParameters.PREFERED_CHARACTERENCODING_ATTRIBUTE, enc);
        }

        this.encoding = enc;
    }

    /**
     * @see org.apache.jetspeed.request.RequestContext#getSubject()
     */
    public Subject getSubject()
    {
        return this.subject;
    }

    public Principal getUserPrincipal()
    {
        return SubjectHelper.getBestPrincipal(getSubject(), User.class);
    }
    
    /**
     * @see org.apache.jetspeed.request.RequestContext#setSubject(javax.security.auth.Subject)
     */
    public void setSubject( Subject subject )
    {
        this.subject = subject;
    }

    /**
     * @see org.apache.jetspeed.request.RequestContext#getLocale()
     */
    public Locale getLocale()
    {
        return this.locale;
    }

    /**
     * @see org.apache.jetspeed.request.RequestContext#setLocale(java.util.Locale)
     */
    public void setLocale( Locale locale )
    {
        Locale preferedLocale = (Locale) session.getAttribute(PortalReservedParameters.PREFERED_LOCALE_ATTRIBUTE);

        if (preferedLocale == null || !locale.equals(preferedLocale))
        {
            // PREFERED_LANGUAGE_ATTRIBUTE doesn't seem to be used anywhere anymore, and as a WeakHashMap isn't
            // Serializable, "fixing" that problem (JS2-174) by simply not putting it in the session anymore
            // request.getSession().setAttribute(PortalReservedParameters.PREFERED_LANGUAGE_ATTRIBUTE, new WeakHashMap());
            session.setAttribute(PortalReservedParameters.PREFERED_LOCALE_ATTRIBUTE, locale);
            request.setAttribute(PortalReservedParameters.PREFERED_LOCALE_ATTRIBUTE, locale);
        }

        this.locale = locale;
    }

    /**
     * @see org.apache.jetspeed.request.RequestContext#getRequestParameter(java.lang.String)
     */
    public String getRequestParameter( String key )
    {
        return request.getParameter(key);
    }
    
    /**
     * @see org.apache.jetspeed.request.RequestContext#getParameterMap()
     */
    public Map getParameterMap()
    {
        return request.getParameterMap();
    }

    public Object getRequestAttribute( String key )
    {
        return request.getAttribute(key);
    }

    /**
     * @see org.apache.jetspeed.request.RequestContext#getSessionAttribute(java.lang.String)
     */
    public Object getSessionAttribute( String key )
    {
        return session.getAttribute(key);
    }

    /**
     * @see org.apache.jetspeed.request.RequestContext#setSessionAttribute(java.lang.String,
     *      java.lang.Object)
     */
    public void setSessionAttribute( String key, Object value )
    {
        session.setAttribute(key, value);
    }

    /**
     * @see org.apache.jetspeed.request.RequestContext#setAttribute(java.lang.String,
     *      java.lang.Object)
     */
    public void setAttribute( String key, Object value )
    {
        request.setAttribute(key, value);
    }

    /**
     * @see org.apache.jetspeed.request.RequestContext#getAttribute(java.lang.String)
     */
    public Object getAttribute( String key )
    {
        return request.getAttribute(key);
    }

    /**
     * @see org.apache.jetspeed.request.RequestContext#getPath()
     */
    public String getPath()
    {
        if (this.requestPath == null)
        {
            this.requestPath = getPortalURL().getPath();
        }
        return this.requestPath;
    }
    
    public void setPortalURL(PortalURL url)
    {
        if ( this.url != null )
            throw new IllegalStateException("PortalURL already set");
        if ( url == null )
            throw new IllegalArgumentException("PortalURL may not be nullified");
        this.url = url;
    }

    public PortalURL getPortalURL()
    {
        return url;
    }

    public Map<String, String> getUserInfoMap(String appName)
    {
        return rcc.getUserInfoManager().getUserInfoMap(appName, this);
    }

    /**
     * 
     * <p>
     * getPreferedLanguage
     * </p>
     * 
     * @see org.apache.jetspeed.request.RequestContext#getPreferedLanguage(org.apache.jetspeed.om.portlet.PortletDefinition)
     * @param portlet
     * @return
     */
    public Language getPreferedLanguage( PortletDefinition portlet )
    {
        // TODO cannot get a proper Language when changing a locale by Locale
        // Selector
        // HttpSession session = request.getSession();
        // Map languageMap = (Map)
        // session.getAttribute(PREFERED_LANGUAGE_SESSION_KEY);
        // Language language = (Language) languageMap.get(portlet);
        // if(language != null)
        // {
        //     return language;
        // }
        
        Language language = portlet.getLanguage(locale);
        
        if (language == null)
        {
            Enumeration locales = request.getLocales();
            while (language == null && locales.hasMoreElements())
            {
                Locale aLocale = (Locale) locales.nextElement();
                language = portlet.getLanguage(aLocale);
            }
        }     
        if (language == null)
        {
            // defaultLocale will always be present, even if not persistent
            language = portlet.getLanguage(JetspeedLocale.getDefaultLocale());
        }
        return language;
    }

    /**
     * <p>
     * setPath
     * </p>
     * 
     * @see org.apache.jetspeed.request.RequestContext#setPath(java.lang.String)
     * @param path
     */
    public void setPath( String path )
    {
        this.requestPath = path;
    }


    /* (non-Javadoc)
     * @see org.apache.jetspeed.request.RequestContext#getActionFailure()
     */
    public Throwable popActionFailure(PortletWindow window)
    {

        String key = ACTION_ERROR_ATTR + window.getId();
        Throwable t = (Throwable) session.getAttribute(key);
        session.removeAttribute(key);
        return t;

    }


    /*
     * (non-Javadoc)
     * 
     * @see org.apache.jetspeed.request.RequestContext#setActionFailed(java.lang.Throwable)
     */
    public void setActionFailure(PortletWindow window, Throwable actionFailure)
    {
        setSessionAttribute(ACTION_ERROR_ATTR + window.getId(),
                actionFailure);
    }
    
    /**
     * Get the current executing pipeline
     * 
     * @return Pipeline
     */
    public Pipeline getPipeline()
    {
        return pipeline;
    }
    
    
    /**
     * Set the current pipeline
     * @param pipeline
     */
    public void setPipeline(Pipeline pipeline)
    {
        this.pipeline = pipeline;
    }

    
    /**
     * @param request The request to set.
     */
    public void setRequest(HttpServletRequest request)
    {
        this.request = request;
    }
    
    /**
     * @param response The request to set.
     */
    public void setResponse(HttpServletResponse response)
    {
        this.response = response;
    }
    
    public ContentPage locatePage(Profiler profiler, PageLayoutComponent pageLayoutComponent, String nonProfiledPath)
    {
        try
        {
            String pathSave = this.getPath();           
            this.setPath(nonProfiledPath);
            ContentPage realPage = this.getPage();
            this.setPage(null);                
            Map locators = null;
            ProfileLocator locator = profiler.getProfile(this, ProfileLocator.PAGE_LOCATOR);
            if ( locator != null )
            {
                locators = new HashMap();
                locators.put(ProfileLocator.PAGE_LOCATOR, locator);
            }               
            PortalSiteSessionContext sessionContext = (PortalSiteSessionContext)getSessionAttribute(ProfilerValveImpl.PORTAL_SITE_SESSION_CONTEXT_ATTR_KEY);
            String userPrincipal = ((getUserPrincipal() != null) ? getUserPrincipal().getName() : null);
            PortalSiteRequestContext requestContext = sessionContext.newRequestContext(locators, userPrincipal, true, true);
            BaseFragmentsElement managedPageOrTemplate = requestContext.getManagedPageOrTemplate();
            PageTemplate managedPageTemplate = requestContext.getManagedPageTemplate();
            Map managedFragmentDefinitions = requestContext.getManagedFragmentDefinitions();
            ContentPage cpage = pageLayoutComponent.newContentPage(managedPageOrTemplate, managedPageTemplate, managedFragmentDefinitions);
            //System.out.println("page is " + cpage.getPath());
            this.setPage(realPage);            
            this.setPath(pathSave);
            return cpage;
        }
        catch (Throwable t)
        {
            t.printStackTrace();
        }
        return null;
    }    

    public Map<String, Object> getObjects()
    {
        return objects;
    }
    
    public PortletWindow getCurrentPortletWindow()
    {
        return currentWindow.get();
    }
    
    public void setCurrentPortletWindow(PortletWindow window)
    {
        if (window != null && !window.isValid())
        {
            throw new IllegalStateException("Invalid window: "+window.getId()+" should not be invoked");
        }
        currentWindow.set(window);
    }
    
    public PortletWindow getPortletWindow(String windowId)
    {
        return portletWindows.get(windowId);
    }
    
    public PortletWindow getPortletWindow(ContentFragment fragment)
    {
        PortletWindow window = portletWindows.get(fragment.getId());
        if (window == null)
        {
            window = createPortletWindow(fragment);
        }
        return window;
    }
    
    public PortletWindow getInstantlyCreatedPortletWindow(String windowId, String portletUniqueName)
    {
        if (portletWindows.get(windowId) != null)
        {
            throw new IllegalArgumentException("PortletWindow "+windowId+" already exists");
        }
        return getInstantlyCreatedPortletWindow(windowId, portletUniqueName, true);
    }
    
    public PortletWindow resolvePortletWindow(String windowId)
    {
        PortletWindow window = portletWindows.get(windowId);
        if (window == null)
        {
            // ensure RootContentFragment is initialized
            getPage().getRootFragment();
            ContentFragment fragment = getPage().getFragmentById(windowId);
            if (fragment == null)
            {
                window = getInstantlyCreatedPortletWindow(windowId, null, false);
            }
            else
            {
                window = createPortletWindow(fragment);
            }
        }
        return window;
    }
    
    private PortletWindow createPortletWindow(ContentFragment fragment)
    {
        PortletWindow window = null;
        PortletDefinition pd = rcc.getPortletRegistry().getPortletDefinitionByUniqueName(fragment.getName(), true);
        if (pd != null)
        {
            window = new PortletWindowImpl(this, fragment, pd);
        }
        else
        {
            // invalid window: create one anyway so that this error condition is only "recorded" once for this request
            window = new PortletWindowImpl(this, fragment);
            fragment.overrideRenderedContent("Failed to retrieve Portlet Definition for " + fragment.getName());
            log.error(fragment.getOverriddenContent());
        }
        portletWindows.put(window.getWindowId(), window);
        return window;
    }
    
    @SuppressWarnings("unchecked")
    private PortletWindow getInstantlyCreatedPortletWindow(String windowId, String portletUniqueName, boolean register)
    {        
        boolean registered = false;
        HttpSession session = getRequest().getSession();
        
        if (session != null)
        {
            Map<String,Map<String,String>> pages = null;
            
            synchronized (session)
            {
                pages = (Map<String,Map<String,String>>) session.getAttribute(INSTANT_WINDOWS_SESSION_KEY);
            }
            
            if (pages != null)
            {
                Map<String,String> instantWindows = pages.get(getPage().getId());
                
                if (instantWindows != null)
                {
                    String uniqueName = instantWindows.get(windowId);
                    
                    if (uniqueName != null)
                    {
                        if (portletUniqueName != null)
                        {
                            if (!portletUniqueName.equals(uniqueName))
                            {
                                // odd condition but store new value of portletUniqueName in session
                                instantWindows.put(windowId, portletUniqueName);
                            }
                        }
                        else
                        {
                            portletUniqueName = uniqueName;
                        }
                        
                        registered = true;
                    }                        
                }
            }
        }
        PortletWindow window = null;
        if (portletUniqueName == null)
        {
            //  JS2-1298: allow fragments to be looked up by exact name, not with ContentFragment paths __
            ContentFragment fragment = getPage().getFragmentByFragmentId(windowId);
            if (fragment != null) {
                portletUniqueName = fragment.getName();
                window = createPortletWindow(fragment);
                registerInstantWindow(window.getWindowId(), portletUniqueName);
                registerInstantWindow(windowId, portletUniqueName);
                return window;
            }
        }
        if (portletUniqueName != null)
        {
            // dynamically create instantly rendered content fragment and window
            ContentFragmentImpl fragment = new ContentFragmentImpl(windowId, true);
            fragment.setType(Fragment.PORTLET);
            fragment.setName(portletUniqueName);
            window = createPortletWindow(fragment);
            
            if (register && !registered && window.isValid())
            {
                registerInstantWindow(windowId, portletUniqueName);
            }
        }
        
        return window;
    }

    private void registerInstantWindow(String windowId, String portletUniqueName)
    {
        if (session == null)
        {
            session = getRequest().getSession(true);
        }

        Map<String,Map<String,String>> pages = null;

        synchronized (session)
        {
            pages = (Map<String,Map<String,String>>) session.getAttribute(INSTANT_WINDOWS_SESSION_KEY);

            if (pages == null)
            {
                pages = Collections.synchronizedMap(new HashMap<String,Map<String,String>>());
                session.setAttribute(INSTANT_WINDOWS_SESSION_KEY, pages);
            }
        }

        String pageId = getPage().getId();
        Map<String,String> instantWindows = pages.get(pageId);

        if (instantWindows == null)
        {
            instantWindows = Collections.synchronizedMap(new HashMap<String,String>());
            pages.put(pageId, instantWindows);
        }

        instantWindows.put(windowId, portletUniqueName);
    }

    @SuppressWarnings("unchecked")
    public void registerInstantlyCreatedPortletWindow(PortletWindow portletWindow)
    {
        if (!portletWindow.isValid())
        {
            throw new IllegalStateException("Invalid window "+portletWindow.getId()+" should not be registered");
        }
        
        HttpSession session = getRequest().getSession(true);
        
        Map<String,Map<String,String>> pages = null;
        
        synchronized (session)
        {
            pages = (Map<String,Map<String,String>>) session.getAttribute(INSTANT_WINDOWS_SESSION_KEY);
            
            if (pages == null)
            {
                pages = Collections.synchronizedMap(new HashMap<String,Map<String,String>>());
                session.setAttribute(INSTANT_WINDOWS_SESSION_KEY, pages);
            }
        }
        
        String pageId = getPage().getId();
        Map<String,String> instantWindows = pages.get(pageId);
        
        if (instantWindows == null)
        {
            instantWindows = Collections.synchronizedMap(new HashMap<String,String>());
            pages.put(pageId, instantWindows);
        }
        
        instantWindows.put(portletWindow.getWindowId(), portletWindow.getPortletDefinition().getUniqueName());
    }

    public boolean ensureThreadContext()
    {
        RequestContext current = rcc.getRequestContext();
        rcc.setRequestContext(this);
        return current == null || current != this;
    }
    
    public void clearThreadContext()
    {
        rcc.setRequestContext(null);
    }
    
    public List<KeyValue<String, HeadElement>> getMergedHeadElements()
    {
        ContentPage page = getPage();
        ContentFragment root = page.getRootFragment();
        List<KeyValue<String, HeadElement>> headElements = getPortletWindow(root).getHeadElements();
        
        HttpSession session = getRequest().getSession();
        
        if (session == null) 
        {
            return headElements;
        }
        
        Map<String,Map<String,String>> pages = null;
        
        synchronized (session)
        {
            pages = (Map<String,Map<String,String>>) session.getAttribute(INSTANT_WINDOWS_SESSION_KEY);
        }
        
        if (pages == null) 
        {
            return headElements;
        }
        
        Map<String,String> instantWindows = pages.get(page.getId());
        
        if (instantWindows == null || instantWindows.isEmpty())
        {
            return headElements;
        }
        
        List<String> windowIds = null;
        
        synchronized (instantWindows) 
        {
            windowIds = new ArrayList<String>(instantWindows.keySet());
        }
        
        if (windowIds.isEmpty())
        {
            return headElements;
        }
        
        List<KeyValue<String, HeadElement>> mergedHeadElements = new TreeList(headElements);
        
        for (String windowId : windowIds)
        {
            PortletWindow window = portletWindows.get(windowId);
            
            if (window != null)
            {
                HeadElementsUtils.aggregateHeadElements(mergedHeadElements, window.getHeadElements());
            }
        }
        
        HeadElementsUtils.mergeHeadElementsByHint(mergedHeadElements);
        
        return mergedHeadElements;
    }
    
}
