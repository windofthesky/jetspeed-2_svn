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
package org.apache.jetspeed.desktop.impl;

import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.jetspeed.container.url.BasePortalURL;
import org.apache.jetspeed.decoration.LayoutDecoration;
import org.apache.jetspeed.decoration.Theme;
import org.apache.jetspeed.desktop.JetspeedDesktopContext;
import org.apache.jetspeed.headerresource.HeaderResource;
import org.apache.jetspeed.headerresource.HeaderResourceLib;
import org.apache.jetspeed.request.RequestContext;

/**
 * Jetspeed Desktop 
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @author <a href="mailto:smilek@apache.org">Steve Milek</a>
 * @version $Id: JetspeedDesktopContextImpl.java $
 */
public class JetspeedDesktopContextImpl implements JetspeedDesktopContext
{
    // Jetspeed Request Context
    RequestContext context;
    
    // base portal url to override default url server info from servlet
    private BasePortalURL baseUrlAccess = null;
    
    private LayoutDecoration layoutDecoration;
    
    // default extension for layout templates
    private String defaultLayoutTemplateExtension;
    
    
    // ... save generated portal urls to avoid duplicate effort
    private String portalBaseUrl;
    private String portalUrl;
    
    private HeaderResource headerResource;
    
    public JetspeedDesktopContextImpl( RequestContext context, BasePortalURL baseUrlAccess, Theme theme, HeaderResource headerResource, String defaultLayoutTemplateExtension )
    {
        // String layoutDecorator, String layoutDecoratorRootPath, String resourceName
        this.context = context;
        this.baseUrlAccess = baseUrlAccess;
        this.layoutDecoration = theme.getPageLayoutDecoration();
        this.headerResource = headerResource;
        this.defaultLayoutTemplateExtension = defaultLayoutTemplateExtension;
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
    
    public String getLayoutDecorationName()
    {
        return layoutDecoration.getName();
    }
    
    public String getLayoutTemplatePath()
    {
        return getLayoutTemplatePath( null );
    }
    public String getLayoutTemplatePath( String layoutTemplateIdPropertyName )
    {
        String id = null;
        if ( layoutTemplateIdPropertyName != null )
        {
            id = layoutDecoration.getProperty( layoutTemplateIdPropertyName );
        }
        
        if ( id == null || id.length() == 0 )
        {
            id = layoutDecoration.getProperty( LAYOUT_TEMPLATE_ID_PROP );
        }
        
        if ( id == null || id.length() == 0 )
        {
            id = LAYOUT_TEMPLATE_ID_DEFAULT;
        }
        
        String ext = layoutDecoration.getProperty( LAYOUT_DESKTOP_TEMPLATE_EXTENSION_PROP );
        if ( ext == null )
            ext = layoutDecoration.getProperty( LAYOUT_TEMPLATE_EXTENSION_PROP );
        if ( ext == null )
        {
            ext = this.defaultLayoutTemplateExtension;
        }
        return layoutDecoration.getBasePath( id + ext );
    }
    
    public String getLayoutBasePath()
    {
        return layoutDecoration.getBasePath();
    }
    public String getLayoutBasePath( String relativePath )
    {
        return layoutDecoration.getBasePath( relativePath );
    }
    
    public String getLayoutBaseUrl()
    {
        return getPortalResourceUrl( getLayoutBasePath(), false );
    }
    public String getLayoutBaseUrl( String relativePath )
    {
        return getPortalResourceUrl( getLayoutBasePath( relativePath ), false );
    }
    
    public ResourceBundle getLayoutResourceBundle( Locale locale )
    {
        return layoutDecoration.getResourceBundle( locale, this.context );
    }
    
    public HeaderResource getHeaderResource()
    {
        return this.headerResource;
    }
}
