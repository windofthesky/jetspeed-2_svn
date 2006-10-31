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
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.Jetspeed;
import org.apache.jetspeed.container.url.BasePortalURL;
import org.apache.jetspeed.desktop.JetspeedDesktop;
import org.apache.jetspeed.desktop.JetspeedDesktopContext;
import org.apache.jetspeed.headerresource.HeaderResource;
import org.apache.jetspeed.headerresource.HeaderResourceFactory;
import org.apache.jetspeed.om.page.Page;
import org.apache.jetspeed.request.RequestContext;
import org.springframework.web.context.ServletContextAware;

/**
 * Desktop Valve
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: $
 */
public class JetspeedDesktopImpl implements JetspeedDesktop, ServletContextAware
{    
    private static final String TEMPLATE_EXTENSION_ATTR = "template.extension";

    private static final String ID_ATTR = "id";
    
    private static final String RESOURCE_FILE_ATTR =  "resource.file";

    private final static String EOL = "\r\n";   // html eol    
    private final static String INIT_FUNCTION_NAME = "jetspeed.initializeDesktop";
    private final static String DOJO_CONFIG_THEME_ROOT_URL_VAR_NAME = "djConfig.desktopThemeRootUrl";
    
    private static final Log log = LogFactory.getLog( JetspeedDesktopImpl.class );

    /** the webapp relative root of all themes */
    private String themesRoot;
    
    /** default theme when no theme supplied in desktop page */
    private String defaultTheme;
    
    /** default extension for theme templates */
    private String defaultExtension;
    
    /** property settings for each theme available */
    private Map themesProperties = new HashMap();

    /** spring-fed servlet context property */
    private ServletContext servletContext;
    
    /** tool for directing output to html &lt;head&gt; */
    private HeaderResourceFactory headerResourceFactory;
    
    /** base portal URL to override default URL server info from servlet */
    private BasePortalURL baseUrlAccess = null;
    
    public JetspeedDesktopImpl(String themesRoot, String defaultTheme, String defaultExtension, HeaderResourceFactory headerResourceFactory )
    {
        this( themesRoot, defaultTheme, defaultExtension, headerResourceFactory, null );
    }

    public JetspeedDesktopImpl(String themesRoot, String defaultTheme, String defaultExtension, HeaderResourceFactory headerResourceFactory, BasePortalURL baseUrlAccess)
    {
        this.themesRoot = themesRoot;
        this.defaultTheme = defaultTheme;
        this.defaultExtension = defaultExtension;
        this.headerResourceFactory = headerResourceFactory;
        this.baseUrlAccess = baseUrlAccess;
    }
    
    public void render(RequestContext request)    
    {
        Page page = request.getPage();
        String theme = page.getSkin();
        if (theme == null)
        {
            theme = defaultTheme;
        }        
        String path = getThemePath(theme);               
        try
        {
            RequestDispatcher dispatcher = request.getRequest().getRequestDispatcher(path);
            
            HeaderResource hr = getHeaderResourceFactory().getHeaderResouce( request );
            
            JetspeedDesktopContext desktopContext = new JetspeedDesktopContextImpl(
                    request, this.baseUrlAccess, theme, getThemeRootPath( theme ), getResourceName( theme ), hr );
            request.getRequest().setAttribute( JetspeedDesktopContext.DESKTOP_ATTRIBUTE, desktopContext );
            request.getRequest().setAttribute( "JS2RequestContext", request );
            request.getRequest().setAttribute( "JS2ComponentManager", Jetspeed.getComponentManager() );
            
            StringBuffer dojoConfigAddOn = new StringBuffer();
            dojoConfigAddOn.append( "    " ).append( DOJO_CONFIG_THEME_ROOT_URL_VAR_NAME ).append( " = \"" ).append( desktopContext.getDesktopThemeRootUrl() ).append( "\";" );
            hr.addHeaderSectionFragment( DOJO_CONFIG_THEME_ROOT_URL_VAR_NAME, HeaderResource.HEADER_SECTION_DOJO_CONFIG, dojoConfigAddOn.toString() );
            
            if ( hr.isHeaderSectionIncluded( HeaderResource.HEADER_SECTION_DESKTOP_STYLE_DESKTOPTHEME ) )
            {
                hr.setHeaderSectionType( HeaderResource.HEADER_SECTION_DESKTOP_STYLE_DESKTOPTHEME, HeaderResource.HEADER_TYPE_LINK_TAG );
                StringBuffer desktopThemeStyleLink = new StringBuffer();
                desktopThemeStyleLink.append( "<link rel=\"stylesheet\" type=\"text/css\" media=\"screen, projection\" href=\"" ).append( desktopContext.getDesktopThemeRootUrl() ).append( "/css/styles.css\"/>" );
                hr.addHeaderSectionFragment( "desktop.style.desktoptheme", HeaderResource.HEADER_SECTION_DESKTOP_STYLE_DESKTOPTHEME, desktopThemeStyleLink.toString() );
            }
            if ( hr.isHeaderSectionIncluded( HeaderResource.HEADER_SECTION_DESKTOP_INIT ) )
            {
                hr.setHeaderSectionType( HeaderResource.HEADER_SECTION_DESKTOP_INIT, HeaderResource.HEADER_TYPE_SCRIPT_BLOCK_START );
                StringBuffer desktopInitScript = new StringBuffer();
                desktopInitScript.append( "    function jsDesktopInit() {" );
                desktopInitScript.append( INIT_FUNCTION_NAME ).append( "(\"" );
                desktopInitScript.append( desktopContext.getDesktopTheme() );
                desktopInitScript.append( "\", \"");
                desktopInitScript.append( desktopContext.getDesktopThemeRootUrl() );
                desktopInitScript.append( "\"); }" ).append( EOL );
                desktopInitScript.append( "    function doRender(bindArgs,portletEntityId) { " );
                desktopInitScript.append( "jetspeed.doRender(bindArgs,portletEntityId); }" ).append( EOL );
                desktopInitScript.append( "    function doAction(bindArgs,portletEntityId) { " );
                desktopInitScript.append( "jetspeed.doAction(bindArgs,portletEntityId); }" ).append( EOL );
                desktopInitScript.append( "    dojo.addOnLoad( window.jsDesktopInit );" );
                hr.addHeaderSectionFragment( "desktop.init", HeaderResource.HEADER_SECTION_DESKTOP_INIT, desktopInitScript.toString() );
            }
            
            dispatcher.include( request.getRequest(), request.getResponse() );
        }
        catch (Exception e)
        {
            try
            {
                log.error("Failed to include Desktop theme " + path, e);
                request.getResponse().getWriter().println("Desktop theme " + theme + " is not available");
            }
            catch (IOException ioe)
            {
                log.error("Failed to write exception information to servlet output writer", ioe);
            }
        
        }
    }
    
    public String getDefaultTheme()
    {
        return defaultTheme;
    }

    
    protected void setDefaultTheme(String defaultTheme)
    {
        this.defaultTheme = defaultTheme;
    }

    protected String getThemeConfigurationPath(String theme)
    {
        return this.themesRoot + "/" +  theme + "/" + JetspeedDesktop.CONFIG_FILE_NAME;
    }
    
    protected String getResourceName(String theme)
    {
        Properties themeConfiguration = (Properties)themesProperties.get(theme);
        if (themeConfiguration == null)
        {
            themeConfiguration = getConfiguration(theme);
        }
        return themeConfiguration.getProperty(RESOURCE_FILE_ATTR);
    }
    
    protected String getThemePath(String theme)
    {
        Properties themeConfiguration = (Properties)themesProperties.get(theme);
        if (themeConfiguration == null)
        {
            themeConfiguration = getConfiguration(theme);
        }
        String id = themeConfiguration.getProperty(ID_ATTR);
        if (id == null)
            id = theme;
        String ext = themeConfiguration.getProperty(TEMPLATE_EXTENSION_ATTR);
        if (ext == null)
            ext = this.defaultExtension;
        return getThemeRootPath(theme) + "/" + id + ext;
    }

    protected String getThemeRootPath(String theme)
    {
        if (this.themesRoot.endsWith("/"))
            return this.themesRoot + theme;
        else
            return this.themesRoot + "/" + theme;
    }
    
    protected Properties getConfiguration(String theme)
    {
        Properties props = (Properties)this.themesProperties.get(theme);
        if (props != null)
        {
            return props;
        }
        
        props = new Properties();
        InputStream is = null;
        try
        {
            is = this.servletContext.getResourceAsStream(getThemeConfigurationPath(theme));
            if (is != null)
            {                
                props.load(is);
            }
            else
            {
                log.warn("Could not locate the theme.properties configuration file for theme \""
                        + theme +
                     "\".  This theme may not exist.");
                props.setProperty(ID_ATTR, theme);
                props.setProperty("extension", this.defaultExtension);
            }                
        }
        catch (Exception e)
        {
            log.warn("Failed to load theme configuration.", e);
            props.setProperty(ID_ATTR, theme);
        }
        finally
        {
            if (is != null)
            {
                try
                {
                    is.close();
                }
                catch (IOException e)
                {
                    log.warn("Failed to close them configuration.", e);
                }
            }
        }
        this.themesProperties.put(theme, props);
        return props;
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
}
    
