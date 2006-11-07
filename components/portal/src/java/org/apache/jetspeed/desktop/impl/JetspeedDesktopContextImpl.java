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
package org.apache.jetspeed.desktop.impl;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.jetspeed.container.url.BasePortalURL;
import org.apache.jetspeed.desktop.JetspeedDesktopContext;
import org.apache.jetspeed.headerresource.HeaderResource;
import org.apache.jetspeed.headerresource.HeaderResourceLib;
import org.apache.jetspeed.request.RequestContext;

/**
 * Jetspeed Desktop 
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: JetspeedDesktopContextImpl.java $
 */
public class JetspeedDesktopContextImpl implements JetspeedDesktopContext
{
    // Jetspeed Request Context
    RequestContext context;
    
    // base portal url to override default url server info from servlet
    private BasePortalURL baseUrlAccess = null;
    
    private String themeRootPath = null;
    private String theme = null;
    private String resourceName = null;
    
    // ... save generated portal urls to avoid duplicate effort
    private String portalBaseUrl;
    private String portalUrl;
    
    private HeaderResource headerResource;
    
    public JetspeedDesktopContextImpl( RequestContext context, BasePortalURL baseUrlAccess, String theme, String themeRootPath, String resourceName, HeaderResource headerResource )
    {
        this.context = context;
        this.baseUrlAccess = baseUrlAccess;
        this.theme = theme;
        this.themeRootPath = themeRootPath;
        this.resourceName = resourceName;
        this.headerResource = headerResource;
    }
    
    
    // get portal urls - each of these methods is copied from HeaderResourceImpl.java
    
    /**
     * Portal base url ( e.g. http://localhost:8080/jetspeed )
     * 
     * @return portal base url
     */
    public String getPortalBaseUrl()
    {
        if ( this.portalBaseUrl == null )
        {
            this.portalBaseUrl = HeaderResourceLib.getPortalBaseUrl( context, this.baseUrlAccess );
        }
        return this.portalBaseUrl;
    }
    
    /**
     * Portal base url ( e.g. http://localhost:8080/jetspeed )
     * 
     * @return portal base url
     */
    public String getPortalBaseUrl( boolean encode )
    {
        String baseurl = getPortalBaseUrl();
        if ( ! encode )
        {
            return baseurl;
        }
        else
        {
            return context.getResponse().encodeURL( baseurl );
        }
    }
        
    /**
     * Portal base url with relativePath argument appended ( e.g. http://localhost:8080/jetspeed/javascript/dojo/ )
     * 
     * @return portal base url with relativePath argument appended
     */
    public String getPortalResourceUrl( String relativePath )
    {
        return getPortalResourceUrl( relativePath, false );
    }
    
    /**
     * Portal base url with relativePath argument appended ( e.g. http://localhost:8080/jetspeed/javascript/dojo/ )
     * 
     * @return portal base url with relativePath argument appended
     */
    public String getPortalResourceUrl( String relativePath, boolean encode )
    {
        return HeaderResourceLib.getPortalResourceUrl( relativePath, getPortalBaseUrl(), encode, context );
    }
    
    /**
     * Portal base servlet url ( e.g. http://localhost:8080/jetspeed/desktop/ )
     * 
     * @return portal base servlet url
     */
    public String getPortalUrl()
    {
        if ( this.portalUrl == null )
        {
            this.portalUrl = HeaderResourceLib.getPortalUrl( getPortalBaseUrl(), context );
        }
        return this.portalUrl;
    }
    
    /**
     * Portal base servlet url ( e.g. http://localhost:8080/jetspeed/desktop/ )
     * 
     * @return portal base servlet url
     */
    public String getPortalUrl( boolean encode )
    {
        return getPortalUrl( null, encode );
    }
    
    /**
     * Portal base servlet url with relativePath argument appended ( e.g. http://localhost:8080/jetspeed/desktop/default-page.psml )
     * 
     * @return portal base servlet url with relativePath argument appended
     */
    public String getPortalUrl( String relativePath )
    {
        return getPortalUrl( relativePath, false );
    }
    
    /**
     * Portal base servlet url with relativePath argument appended ( e.g. http://localhost:8080/jetspeed/desktop/default-page.psml )
     * 
     * @return portal base servlet url with relativePath argument appended
     */
    public String getPortalUrl( String relativePath, boolean encode )
    {
        return HeaderResourceLib.getPortalResourceUrl( relativePath, getPortalUrl(), encode, context );
    }
    
    
    public String getDesktopThemeResourceUrl( String relativePath )
    {
        return getPortalResourceUrl( getDesktopThemeResource( relativePath ), false );
    }

    public String getDesktopThemeResource( String relativePath )
    {
        if ( relativePath.startsWith( "/" ) )
        {
            return themeRootPath + relativePath;
        }
        else
        {
            return themeRootPath + "/" + relativePath;
        }
    }

    public String getDesktopThemeRootUrl()
    {
        return getPortalResourceUrl( themeRootPath, false );
    }
    
    public String getDesktopTheme()
    {
        return theme;
    }
    
    public ResourceBundle getResourceBundle(Locale locale)
    {
        String resourceDirName = context.getConfig().getServletContext()
                .getRealPath( getDesktopThemeResource( RESOURCES_DIRECTORY_NAME ) );
        File resourceDir = new File(resourceDirName);
        if (resourceName == null)
        {
            throw new NullPointerException( "The resource file is null." );
        }
        if ( !resourceDir.isDirectory() )
        {
            throw new MissingResourceException(
                    "Can't find the resource directory: " + resourceDirName,
                    resourceName + "_" + locale, "");
        }
        URL[] urls = new URL[1];
        try
        {
            urls[0] = resourceDir.toURL();
        }
        catch (MalformedURLException e)
        {
            throw new MissingResourceException(
                    "The resource directory cannot be parsed as a URL: "
                            + resourceDirName, resourceName + "_" + locale, "");
        }
        return ResourceBundle.getBundle(resourceName, locale,
                new URLClassLoader(urls));
    }
    
    public HeaderResource getHeaderResource()
    {
        return this.headerResource;
    }
}
