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
import org.apache.jetspeed.container.url.BasePortalURL;
import org.apache.jetspeed.desktop.JetspeedDesktop;
import org.apache.jetspeed.desktop.JetspeedDesktopContext;
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
    
    /** base portal URL to override default URL server info from servlet */
    private BasePortalURL baseUrlAccess = null;
    
    public JetspeedDesktopImpl(String themesRoot, String defaultTheme, String defaultExtension)
    {
        this.themesRoot = themesRoot;
        this.defaultTheme = defaultTheme;
        this.defaultExtension = defaultExtension;
    }

    public JetspeedDesktopImpl(String themesRoot, String defaultTheme, String defaultExtension, BasePortalURL baseUrlAccess)
    {
        this(themesRoot, defaultTheme, defaultExtension);
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
            JetspeedDesktopContext desktopContext = new JetspeedDesktopContextImpl(request, this.baseUrlAccess);
            request.getRequest().setAttribute(JetspeedDesktopContext.DESKTOP_ATTRIBUTE, desktopContext);
            dispatcher.include(request.getRequest(), request.getResponse());
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

    
    public void setDefaultTheme(String defaultTheme)
    {
        this.defaultTheme = defaultTheme;
    }

    protected String getThemeConfigurationPath(String theme)
    {
        return this.themesRoot + "/" +  theme + "/" + JetspeedDesktop.CONFIG_FILE_NAME;
    }
    
    protected String getThemePath(String theme)
    {
        Properties themeConfiguration = (Properties)themesProperties.get(theme);
        if (themeConfiguration == null)
        {
            themeConfiguration = getConfiguration(theme);
        }
        String id = themeConfiguration.getProperty("id");
        if (id == null)
            id = theme;
        String ext = themeConfiguration.getProperty("template.extension");
        if (ext == null)
            ext = this.defaultExtension;
        if (this.themesRoot.endsWith("/"))
            return this.themesRoot + theme + "/" + id + ext;
        else
            return this.themesRoot + "/" + theme + "/" + id + ext;
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
                props.setProperty("id", theme);
                props.setProperty("extension", this.defaultExtension);
            }                
        }
        catch (Exception e)
        {
            log.warn("Failed to load theme configuration.", e);
            props.setProperty("id", theme);
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
    
}
    