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
package org.apache.jetspeed.request;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.WeakHashMap;

import javax.security.auth.Subject;
import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.jetspeed.Jetspeed;
import org.apache.jetspeed.aggregator.ContentDispatcher;
import org.apache.jetspeed.capabilities.CapabilityMap;
import org.apache.jetspeed.container.state.NavigationalStateComponent;
import org.apache.jetspeed.container.url.PortalURL;
import org.apache.jetspeed.engine.servlet.ServletRequestFactory;
import org.apache.jetspeed.engine.servlet.ServletResponseFactory;
import org.apache.jetspeed.om.common.MutableLanguage;
import org.apache.jetspeed.om.impl.LanguageImpl;
import org.apache.jetspeed.om.page.Page;
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
    private HttpServletRequest request;
    private HttpServletResponse response;
    private ServletConfig config;
    private Map locators;
    private Page page;
    private PortletDefinition portletDefinition;
    private Subject subject;
    private Locale locale;
    private ContentDispatcher dispatcher;

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

    /**
     * Create a new Request Context
     * 
     * @param pc
     * @param request
     * @param response
     * @param config
     */
    public JetspeedRequestContext( HttpServletRequest request, HttpServletResponse response, ServletConfig config,
            NavigationalStateComponent navcomponent, UserInfoManager userInfoMgr )
    {
        this.request = request;
        this.response = response;
        this.config = config;
        this.userInfoMgr = userInfoMgr;
        this.requestsForWindows = new HashMap();
        this.responsesForWindows = new HashMap();

        // set context in Request for later use
        if (null != this.request)
        {
            this.request.setAttribute(RequestContext.REQUEST_PORTALENV, this);
        }

        if (navcomponent != null)
        {
            url = navcomponent.createURL(this);
        }

    }

    private JetspeedRequestContext()
    {
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

    public Page getPage()
    {
        return this.page;
    }

    public void setPage( Page page )
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
     * Set the capabilityMap. Used by the CapabilityValve
     * 
     * @param capabilityMap
     */
    public void setCapabilityMap( CapabilityMap map )
    {
        this.capabilityMap = map;
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
        String preferedEnc = (String) request.getSession().getAttribute(RequestContext.PREFERED_CHARACTERENCODING_KEY);

        if (preferedEnc == null || !enc.equals(preferedEnc))
        {
            request.setAttribute(RequestContext.PREFERED_CHARACTERENCODING_KEY, enc);
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
        if (!responsesForWindows.containsKey(window.getId()))
        {
            ServletResponseFactory rspFac = (ServletResponseFactory) Jetspeed.getEngine().getFactory(
                    HttpServletResponse.class);
            HttpServletResponse wrappedResponse = rspFac.getServletResponse(response);
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
        Locale preferedLocale = (Locale) request.getSession().getAttribute(RequestContext.PREFERED_LOCALE_SESSION_KEY);

        if (preferedLocale == null || !locale.equals(preferedLocale))
        {
            request.getSession().setAttribute(RequestContext.PREFERED_LANGUAGE_SESSION_KEY, new WeakHashMap());
            request.getSession().setAttribute(RequestContext.PREFERED_LOCALE_SESSION_KEY, locale);
            request.setAttribute(RequestContext.PREFERED_LOCALE_SESSION_KEY, locale);
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
        return request.getSession().getAttribute(key);
    }

    /**
     * @see org.apache.jetspeed.request.RequestContext#setSessionAttribute(java.lang.String,
     *      java.lang.Object)
     */
    public void setSessionAttribute( String key, Object value )
    {
        request.getSession().setAttribute(key, value);
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
}
