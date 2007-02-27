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

import java.io.IOException;
import java.util.Map;
import java.util.List;
import java.util.Iterator;
import java.util.ResourceBundle;


import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.Jetspeed;
import org.apache.jetspeed.container.url.BasePortalURL;
import org.apache.jetspeed.decoration.DecorationFactory;
import org.apache.jetspeed.decoration.Theme;
import org.apache.jetspeed.desktop.JetspeedDesktop;
import org.apache.jetspeed.desktop.JetspeedDesktopContext;
import org.apache.jetspeed.headerresource.HeaderResource;
import org.apache.jetspeed.headerresource.HeaderResourceFactory;
import org.apache.jetspeed.headerresource.HeaderResourceLib;
import org.apache.jetspeed.om.page.Page;
import org.apache.jetspeed.request.RequestContext;
import org.springframework.web.context.ServletContextAware;

/**
 * Desktop Valve
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @author <a href="mailto:smilek@apache.org">Steve Milek</a>
 * @version $Id: JetspeedDesktopImpl.java $
 */
public class JetspeedDesktopImpl implements JetspeedDesktop, ServletContextAware
{
    private final static String EOL = "\r\n";   // html eol
    private final static String DOJO_CONFIG_LAYOUT_DECORATION_PATH_VAR_NAME = HeaderResource.HEADER_INTERNAL_DOJO_CONFIG_JETSPEED_VAR_NAME + ".layoutDecorationPath";
    private final static String DOJO_CONFIG_LAYOUT_VAR_NAME = HeaderResource.HEADER_INTERNAL_DOJO_CONFIG_JETSPEED_VAR_NAME + ".layoutName";
    private final static String DOJO_CONFIG_PORTLET_DECORATIONS_PATH_VAR_NAME = HeaderResource.HEADER_INTERNAL_DOJO_CONFIG_JETSPEED_VAR_NAME + ".portletDecorationsPath";
    private final static String DOJO_CONFIG_PORTLET_DECORATIONS_ALLOWED_VAR_NAME = HeaderResource.HEADER_INTERNAL_DOJO_CONFIG_JETSPEED_VAR_NAME + ".portletDecorationsAllowed";
    private final static String DOJO_CONFIG_ACTION_LABELS_NAME = HeaderResource.HEADER_INTERNAL_DOJO_CONFIG_JETSPEED_VAR_NAME + ".desktopActionLabels";

    private final static String[] DESKTOP_ACTIONS = new String[] { "menu", "tile", "untile", "heightexpand", "heightnormal", "restore", "removeportlet", "addportlet", "editpage" };
    private final static String DESKTOP_ACTION_RESOURCE_NAME_PREFIX = "desktop.action.";
    
    private static final Log log = LogFactory.getLog( JetspeedDesktopImpl.class );

    private DecorationFactory decorationFactory;
        
    /** desktop pipeline servlet path */
    private String desktopServletPath;
    
    /** default extension for layout templates */
    private String defaultLayoutTemplateExtension;
    
    /** spring-fed servlet context property */
    private ServletContext servletContext;
    
    /** tool for directing output to html &lt;head&gt; */
    private HeaderResourceFactory headerResourceFactory;
    
    /** base portal URL to override default URL server info from servlet */
    private BasePortalURL baseUrlAccess = null;
    
    public JetspeedDesktopImpl( DecorationFactory decorationFactory, HeaderResourceFactory headerResourceFactory, String desktopServletPath, String defaultLayoutTemplateExtension )
    {
        this( decorationFactory, headerResourceFactory, desktopServletPath, defaultLayoutTemplateExtension, null, null, null );
    }
    public JetspeedDesktopImpl( DecorationFactory decorationFactory, HeaderResourceFactory headerResourceFactory, String desktopServletPath, String defaultLayoutTemplateExtension, String defaultDesktopLayoutDecoration, String defaultDesktopPortletDecoration )
    {
        this( decorationFactory, headerResourceFactory, desktopServletPath, defaultLayoutTemplateExtension, defaultDesktopLayoutDecoration, defaultDesktopPortletDecoration, null );
    }
    public JetspeedDesktopImpl( DecorationFactory decorationFactory, HeaderResourceFactory headerResourceFactory, String desktopServletPath, String defaultLayoutTemplateExtension, String defaultDesktopLayoutDecoration, String defaultDesktopPortletDecoration, BasePortalURL baseUrlAccess )
    {
        this.decorationFactory = decorationFactory;
        this.headerResourceFactory = headerResourceFactory;
        
        if ( desktopServletPath != null && desktopServletPath.length() > 0 )
        {
            if ( desktopServletPath.charAt( 0 ) != '/' )
                desktopServletPath = "/" + desktopServletPath;
        }
        this.desktopServletPath = desktopServletPath;
        if ( this.desktopServletPath == null || this.desktopServletPath.length() == 0 )
        {
            log.warn( "JetspeedDesktopImpl initialization is incomplete due to undefined desktop servlet path." );
            this.desktopServletPath = null;
        }
        
        this.defaultLayoutTemplateExtension = defaultLayoutTemplateExtension;
        
        // set default layout and portlet decorations only if they are not currently undefined
        if ( defaultDesktopLayoutDecoration != null && defaultDesktopLayoutDecoration.length() > 0 )
        {
            String existingDefaultDesktopLayoutDecoration = decorationFactory.getDefaultDesktopLayoutDecoration();
            if ( existingDefaultDesktopLayoutDecoration == null || existingDefaultDesktopLayoutDecoration.length() == 0 )
            {
                decorationFactory.setDefaultDesktopLayoutDecoration( defaultDesktopLayoutDecoration );
            }
        }
        if ( defaultDesktopPortletDecoration != null && defaultDesktopPortletDecoration.length() > 0 )
        {
            String existingDefaultDesktopPortletDecoration = decorationFactory.getDefaultDesktopPortletDecoration();
            if ( existingDefaultDesktopPortletDecoration == null || existingDefaultDesktopPortletDecoration.length() == 0 )
            {
                decorationFactory.setDefaultDesktopPortletDecoration( defaultDesktopPortletDecoration );
            }
        }
        
        this.baseUrlAccess = baseUrlAccess;
    }
    
    public void render( RequestContext request )    
    {
        String layoutDecorationTemplatePath = null;
        boolean layoutDecorationTemplatePathWasAssigned = false;
        try
        {
            Page page = request.getPage();
            
            // enable desktop
            request.setAttribute( JetspeedDesktop.DESKTOP_ENABLED_REQUEST_ATTRIBUTE, Boolean.TRUE );
            
            // get decorations
            Theme theme = decorationFactory.getTheme( page, request );
            
            HeaderResource hr = getHeaderResourceFactory().getHeaderResouce( request );
            JetspeedDesktopContext desktopContext = new JetspeedDesktopContextImpl( request, this.baseUrlAccess, theme, hr, defaultLayoutTemplateExtension );
            
            String layoutTemplateIdPropertyName = null;
            if ( "true".equals( request.getRequest().getParameter( "jsprintmode" ) ) )
                layoutTemplateIdPropertyName = JetspeedDesktopContext.LAYOUT_PRINT_TEMPLATE_ID_PROP;
            
            layoutDecorationTemplatePath = desktopContext.getLayoutTemplatePath( layoutTemplateIdPropertyName );
            layoutDecorationTemplatePathWasAssigned = true;
            
            RequestDispatcher dispatcher = request.getRequest().getRequestDispatcher( layoutDecorationTemplatePath );
            
            hr.dojoEnable();
            
            request.getRequest().setAttribute( JetspeedDesktopContext.DESKTOP_CONTEXT_ATTRIBUTE, desktopContext );
            request.getRequest().setAttribute( JetspeedDesktopContext.DESKTOP_REQUEST_CONTEXT_ATTRIBUTE, request );
            request.getRequest().setAttribute( JetspeedDesktopContext.DESKTOP_COMPONENT_MANAGER_ATTRIBUTE, Jetspeed.getComponentManager() );
            
            String portletDecorationsBasePath = decorationFactory.getPortletDecorationsBasePath();
            String portletDecorationsBaseRelative = portletDecorationsBasePath;
            if ( portletDecorationsBaseRelative != null && portletDecorationsBaseRelative.length() > 1 && portletDecorationsBaseRelative.indexOf( '/' ) == 0 )
            {
                portletDecorationsBaseRelative = portletDecorationsBaseRelative.substring( 1 );
            }
            StringBuffer dojoConfigAddOn = new StringBuffer();
            dojoConfigAddOn.append( "    " ).append( DOJO_CONFIG_LAYOUT_DECORATION_PATH_VAR_NAME ).append( " = \"" ).append( desktopContext.getLayoutBasePath() ).append( "\";" ).append( EOL );
            dojoConfigAddOn.append( "    " ).append( DOJO_CONFIG_LAYOUT_VAR_NAME ).append( " = \"" ).append( desktopContext.getLayoutDecorationName() ).append( "\";" ).append( EOL );
            dojoConfigAddOn.append( "    " ).append( DOJO_CONFIG_PORTLET_DECORATIONS_PATH_VAR_NAME ).append( " = \"" ).append( portletDecorationsBasePath ).append( "\";" ).append( EOL );
            String portletDecorationNamesContent = HeaderResourceLib.makeJSONStringArray( theme.getPortletDecorationNames() );
            dojoConfigAddOn.append( "    " ).append( DOJO_CONFIG_PORTLET_DECORATIONS_ALLOWED_VAR_NAME ).append( " = " ).append( portletDecorationNamesContent ).append( ";" );
            hr.addHeaderSectionFragment( DOJO_CONFIG_LAYOUT_VAR_NAME, HeaderResource.HEADER_SECTION_DOJO_CONFIG, dojoConfigAddOn.toString() );
            
            if ( hr.isHeaderSectionIncluded( HeaderResource.HEADER_SECTION_DESKTOP_STYLE_LAYOUT ) )
            {
                hr.setHeaderSectionType( HeaderResource.HEADER_SECTION_DESKTOP_STYLE_LAYOUT, HeaderResource.HEADER_TYPE_LINK_TAG );
                StringBuffer desktopThemeStyleLink = new StringBuffer();
                int stylesheetCount = 0;
                Iterator stylesheetIter = theme.getStyleSheets().iterator();
                while ( stylesheetIter.hasNext() )
                {
                    String stylesheetHref = (String)stylesheetIter.next();
                    if ( stylesheetHref != null && stylesheetHref.length() > 0 )
                    {
                        if ( ! stylesheetHref.startsWith( portletDecorationsBaseRelative ) )
                        {   // exclude portlet decorations - in desktop these are loaded via javascript
                            if ( stylesheetCount > 0 )
                            {
                                desktopThemeStyleLink.append( EOL );
                            }
                            desktopThemeStyleLink.append( "<link rel=\"stylesheet\" type=\"text/css\" media=\"screen, projection\" href=\"" );
                            desktopThemeStyleLink.append( desktopContext.getPortalResourceUrl( stylesheetHref ) ).append( "\"/>" );
                            stylesheetCount++;
                        }
                    }
                }
                hr.addHeaderSectionFragment( "desktop.style.layout", HeaderResource.HEADER_SECTION_DESKTOP_STYLE_LAYOUT, desktopThemeStyleLink.toString() );
            }
            
            // desktop action labels
            StringBuffer desktopActionLabels = new StringBuffer();
            ResourceBundle messages = desktopContext.getLayoutResourceBundle( request.getLocale() );
            for ( int i = 0 ; i < DESKTOP_ACTIONS.length ; i++ )
            {
                String actionLabel = messages.getString( DESKTOP_ACTION_RESOURCE_NAME_PREFIX + DESKTOP_ACTIONS[ i ] );
                if ( actionLabel != null )
                {
                    if ( desktopActionLabels.length() == 0 )
                    {
                        desktopActionLabels.append( "{ " );
                    }
                    else
                    {
                        desktopActionLabels.append( ", " );
                    }
                    desktopActionLabels.append( DESKTOP_ACTIONS[ i ] ).append( ": \"" ).append( actionLabel ).append( "\"" );
                }
            }
            if ( desktopActionLabels.length() > 0 )
            {
                dojoConfigAddOn = new StringBuffer();
                dojoConfigAddOn.append( "    " ).append( DOJO_CONFIG_ACTION_LABELS_NAME ).append( " = " ).append( desktopActionLabels.toString() ).append( " };" ).append( EOL );
                hr.addHeaderSectionFragment( DOJO_CONFIG_ACTION_LABELS_NAME, HeaderResource.HEADER_SECTION_DOJO_CONFIG, dojoConfigAddOn.toString() );
            }
            
            dispatcher.include( request.getRequest(), request.getResponse() );
        }
        catch ( Exception e )
        {
            try
            {
                if ( layoutDecorationTemplatePathWasAssigned )
                {
                    layoutDecorationTemplatePath = ( layoutDecorationTemplatePath == null || layoutDecorationTemplatePath.length() == 0 ? "null" : layoutDecorationTemplatePath );
                    log.error( "Failed to include desktop layout decoration at path " + layoutDecorationTemplatePath, e );
                    request.getResponse().getWriter().println( "Desktop layout decoration " + layoutDecorationTemplatePath + " is not available" );
                }
                else
                {
                    log.error( "Failed to initialize for inclusion of desktop layout decoration", e );
                    request.getResponse().getWriter().println( "Failed to initialize for inclusion of desktop layout decoration" );
                }
            }
            catch ( IOException ioe )
            {
                log.error( "Failed to write desktop layout decoration exception information to servlet output writer", ioe );
            }
        }
    }
    
    public boolean isDesktopEnabled( RequestContext requestContext )
    {
        return this.decorationFactory.isDesktopEnabled( requestContext );
    }
    
    public ServletContext getServletContext()
    {
        return servletContext;
    }

    public void setServletContext(ServletContext servletContext)
    {
        this.servletContext = servletContext;
    }
    
    public HeaderResourceFactory getHeaderResourceFactory()
    {
        return this.headerResourceFactory;
    }
    
    // get portal urls - each of these methods is copied from HeaderResourceImpl.java
 
    /**
     * Desktop servlet path ( e.g. /desktop )
     * 
     * @return portal base url
     */
    public String getDesktopServletPath()
    {
        return this.desktopServletPath;
    }
    
    /**
     * Portal base url ( e.g. http://localhost:8080/jetspeed )
     * 
     * @return portal base url
     */
    public String getPortalBaseUrl( RequestContext context )
    {
        return HeaderResourceLib.getPortalBaseUrl( context, this.baseUrlAccess );
    }
    
    /**
     * Portal base url ( e.g. http://localhost:8080/jetspeed )
     * 
     * @return portal base url
     */
    public String getPortalBaseUrl( RequestContext context, boolean encode )
    {
        String baseurl = getPortalBaseUrl( context );
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
    public String getPortalResourceUrl( RequestContext context, String relativePath )
    {
        return getPortalResourceUrl( context, relativePath, false );
    }
    
    /**
     * Portal base url with relativePath argument appended ( e.g. http://localhost:8080/jetspeed/javascript/dojo/ )
     * 
     * @return portal base url with relativePath argument appended
     */
    public String getPortalResourceUrl( RequestContext context, String relativePath, boolean encode )
    {
        return HeaderResourceLib.getPortalResourceUrl( relativePath, getPortalBaseUrl( context ), encode, context );
    }
    
    /**
     * Portal base servlet url ( e.g. http://localhost:8080/jetspeed/desktop/ )
     * 
     * @return portal base servlet url
     */
    public String getPortalUrl( RequestContext context )
    {
        return HeaderResourceLib.getPortalUrl( getPortalBaseUrl( context ), context, getDesktopServletPath() );
    }
    
    /**
     * Portal base servlet url ( e.g. http://localhost:8080/jetspeed/desktop/ )
     * 
     * @return portal base servlet url
     */
    public String getPortalUrl( RequestContext context, boolean encode )
    {
        return getPortalUrl( context, null, encode );
    }
    
    /**
     * Portal base servlet url with relativePath argument appended ( e.g. http://localhost:8080/jetspeed/desktop/default-page.psml )
     * 
     * @return portal base servlet url with relativePath argument appended
     */
    public String getPortalUrl( RequestContext context, String relativePath )
    {
        return getPortalUrl( context, relativePath, false );
    }
    
    /**
     * Portal base servlet url with relativePath argument appended ( e.g. http://localhost:8080/jetspeed/desktop/default-page.psml )
     * 
     * @return portal base servlet url with relativePath argument appended
     */
    public String getPortalUrl( RequestContext context, String relativePath, boolean encode )
    {
        return HeaderResourceLib.getPortalResourceUrl( relativePath, getPortalUrl( context ), encode, context );
    }
}
    
