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

import java.security.Principal;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import javax.security.auth.Subject;
import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.jetspeed.Jetspeed;
import org.apache.jetspeed.PortalReservedParameters;
import org.apache.jetspeed.aggregator.ContentDispatcher;
import org.apache.jetspeed.aggregator.ContentDispatcherCtrl;
import org.apache.jetspeed.capabilities.CapabilityMap;
import org.apache.jetspeed.container.url.PortalURL;
import org.apache.jetspeed.engine.servlet.ServletRequestFactory;
import org.apache.jetspeed.engine.servlet.ServletResponseFactory;
import org.apache.jetspeed.om.common.MutableLanguage;
import org.apache.jetspeed.om.impl.LanguageImpl;
import org.apache.jetspeed.om.page.ContentPage;
import org.apache.jetspeed.om.page.ContentPageImpl;
import org.apache.jetspeed.pipeline.Pipeline;
import org.apache.jetspeed.portalsite.PortalSiteRequestContext;
import org.apache.jetspeed.portalsite.PortalSiteSessionContext;
import org.apache.jetspeed.profiler.ProfileLocator;
import org.apache.jetspeed.profiler.Profiler;
import org.apache.jetspeed.profiler.impl.ProfilerValveImpl;
import org.apache.jetspeed.security.SubjectHelper;
import org.apache.jetspeed.security.User;
import org.apache.jetspeed.userinfo.UserInfoManager;
import org.apache.pluto.om.common.Language;
import org.apache.pluto.om.common.LanguageSet;
import org.apache.pluto.om.common.ObjectID;
import org.apache.pluto.om.portlet.PortletDefinition;
import org.apache.pluto.om.window.PortletWindow;

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
    private static final String ACTION_ERROR_ATTR = "org.apache.jetspeed.action.error:";
    private HttpServletRequest request;
    private HttpServletResponse response;
    private ServletConfig config;
    private HttpSession session;
    private Map locators;
    private ContentPage page;
    private PortletDefinition portletDefinition;
    private Subject subject;
    private Locale locale;
    private ContentDispatcher dispatcher;
    private Pipeline pipeline;

    private CapabilityMap capabilityMap;
    private String mimeType;
    private String mediaType;
    private PortalURL url;
    private PortletWindow actionWindow;
    private String encoding;
    private String requestPath = null;
    /** The user info manager. */
    private UserInfoManager userInfoMgr;
    private Map requestsForWindows;
    private Map responsesForWindows;
    private final Map objects;
    
    /**
     * Create a new Request Context
     * 
     * @param pc
     * @param request
     * @param response
     * @param config
     */
    public JetspeedRequestContext( HttpServletRequest request, HttpServletResponse response, ServletConfig config,
            UserInfoManager userInfoMgr )
    {
        this(request, response, config, userInfoMgr, new HashMap());
    }

    public JetspeedRequestContext( HttpServletRequest request, HttpServletResponse response, ServletConfig config,
            UserInfoManager userInfoMgr, Map objects)
    {
        this.request = request;
        this.response = response;
        this.config = config;
        this.session = request.getSession();
        this.userInfoMgr = userInfoMgr;
        this.requestsForWindows = new HashMap();
        this.responsesForWindows = new HashMap();
        this.objects = objects;

        // set context in Request for later use
        if (null != this.request)
        {
            this.request.setAttribute(PortalReservedParameters.REQUEST_CONTEXT_ATTRIBUTE, this);
            PortalRequestFactory prf = null;
            try
            {
                prf = (PortalRequestFactory)Jetspeed.getComponentManager().getComponent(PortalRequestFactory.class);
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

    public Map getProfileLocators()
    {
        return locators;
    }

    public void setProfileLocators( Map locators )
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
    }

    public PortletDefinition getPortletDefinition()
    {
        return portletDefinition;
    }

    public void setPortletDefinition( PortletDefinition portletDefinition )
    {
        this.portletDefinition = portletDefinition;
    }

    public ContentDispatcher getContentDispatcher()
    {
        return dispatcher;
    }

    public void setContentDispatcher( ContentDispatcher dispatcher )
    {
        this.dispatcher = dispatcher;
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
     * @param window
     */
    public void setActionWindow( PortletWindow portletWindow )
    {
        this.actionWindow = portletWindow;
    }


    /**
     * Set the capabilityMap. Used by the CapabilityValve
     * 
     * @param capabilityMap
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
     * <p>
     * getRequestForWindow
     * </p>
     * 
     * @see org.apache.jetspeed.request.RequestContext#getRequestForWindow(org.apache.pluto.om.window.PortletWindow)
     * @param window
     * @return
     */
    public HttpServletRequest getRequestForWindow( PortletWindow window )
    {
        if (!requestsForWindows.containsKey(window.getId()))
        {
            ServletRequestFactory reqFac = (ServletRequestFactory) Jetspeed.getEngine().getFactory(
                    javax.servlet.http.HttpServletRequest.class);
            HttpServletRequest requestWrapper = reqFac.getServletRequest(request, window);
            requestsForWindows.put(window.getId(), requestWrapper);
            return requestWrapper;
        }
        else
        {
            return (HttpServletRequest) requestsForWindows.get(window.getId());
        }

    }

    /**
     * <p>
     * getResponseForWindow
     * </p>
     * 
     * @see org.apache.jetspeed.request.RequestContext#getResponseForWindow(org.apache.pluto.om.window.PortletWindow)
     * @param window
     * @return
     */
    public HttpServletResponse getResponseForWindow( PortletWindow window )
    {
        HttpServletResponse wrappedResponse = null;

        if (!responsesForWindows.containsKey(window.getId()))
        {
            if (getContentDispatcher() != null)
            {
                wrappedResponse = ((ContentDispatcherCtrl) getContentDispatcher()).getResponseForWindow(window, this);
            }
            else
            {
                ServletResponseFactory rspFac = (ServletResponseFactory) Jetspeed.getEngine().getFactory(
                        HttpServletResponse.class);
                wrappedResponse = rspFac.getServletResponse(this.response);

            }

            responsesForWindows.put(window.getId(), wrappedResponse);
            return wrappedResponse;

        }
        else
        {
            return (HttpServletResponse) responsesForWindows.get(window.getId());
        }
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
    
    public void setRequestParameter(String key, String value)
    {
        request.getParameterMap().put(key, value);
    }

    /**
     * @see org.apache.jetspeed.request.RequestContext#getParameterMap()
     */
    public Map getParameterMap()
    {
        return request.getParameterMap();
    }

    /**
     * @see org.apache.jetspeed.request.RequestContext#getRequestAttribute(java.lang.String)
     */
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

    /**
     * @see org.apache.jetspeed.request.RequestContext#getUserInfoMap(org.apache.pluto.om.common.ObjectID)
     */
    public Map getUserInfoMap( ObjectID oid )
    {
        return userInfoMgr.getUserInfoMap(oid, this);
    }

    /**
     * 
     * <p>
     * getPreferedLanguage
     * </p>
     * 
     * @see org.apache.jetspeed.request.RequestContext#getPreferedLanguage(org.apache.pluto.om.portlet.PortletDefinition)
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
        LanguageSet languageSet = portlet.getLanguageSet();
        Language language = languageSet.get(locale);

        Enumeration locales = request.getLocales();
        while (locales.hasMoreElements() && language == null)
        {
            Locale aLocale = (Locale) locales.nextElement();
            language = languageSet.get(aLocale);
        }

        Iterator langItr = languageSet.iterator();
        if (langItr.hasNext() && language == null)
        {
            language = (Language) langItr.next();
        }
        
        if (language == null)
        {
            language = languageSet.get(languageSet.getDefaultLocale());
        }

        if (language == null)
        {
            MutableLanguage languageCtl = new LanguageImpl();
            languageCtl.setLocale(locale);
            languageCtl.setShortTitle(portlet.getName());
            languageCtl.setTitle(portlet.getName());
            language = languageCtl;
        }

        // languageMap.put(portlet, language);
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
    
    public ContentPage locatePage(Profiler profiler, String nonProfiledPath)
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
            PortalSiteRequestContext requestContext = sessionContext.newRequestContext(locators, true, true);
            ContentPage cpage = new ContentPageImpl(requestContext.getManagedPage());
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

    public Map getObjects()
    {
        return objects;
    }
}
