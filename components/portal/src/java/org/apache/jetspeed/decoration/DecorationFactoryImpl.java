/*
 * Copyright 2000-2001,2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.jetspeed.decoration;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Properties;

import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.decoration.caches.SessionPathResolverCache;
import org.apache.jetspeed.om.page.Fragment;
import org.apache.jetspeed.om.page.Page;
import org.apache.jetspeed.request.RequestContext;
import org.apache.jetspeed.util.Path;
import org.springframework.web.context.ServletContextAware;

/**
 * 
 * @author <href a="mailto:weaver@apache.org">Scott T. Weaver</a>
 *
 */
public class DecorationFactoryImpl implements DecorationFactory, ServletContextAware
{
    private static final Log log = LogFactory.getLog(DecorationFactoryImpl.class);
    
    private final Path decorationsPath;
    private final ResourceValidator validator;
    private final String defaultLayoutDecorator;    
    private final String defaultPortletDecorator;
    
    private ServletContext servletContext;    

    public DecorationFactoryImpl(String decorationsPath, ResourceValidator validator, String defaultLayoutDecorator, String defaultPortletDecorator)
    {
        this.decorationsPath = new Path(decorationsPath);
        this.validator = validator;
        this.defaultLayoutDecorator = defaultLayoutDecorator;
        this.defaultPortletDecorator = defaultPortletDecorator;
    }
    
    public Theme getTheme(Page page, RequestContext requestContext)
    {
        return new PageTheme(page, this, requestContext);
    }

    public Decoration getDecoration(Page page, Fragment fragment, RequestContext requestContext)
          
    {
        String decorationName = getDefaultDecorationName(fragment, page);
        Decoration decoration;

        if (fragment.getType().equals(Fragment.PORTLET))
        {
            decoration = getPortletDecoration(decorationName, requestContext);
        }
        else
        {
            decoration = getLayoutDecoration(decorationName, requestContext);
        }

        return decoration;
    }

    public PortletDecoration getPortletDecoration(String name, RequestContext requestContext)
            
    {
        Path basePath = createClientPath(name, requestContext, Fragment.PORTLET);
        
        Properties configuration = getConfiguration(name);
        configuration.setProperty("name", name);
        return new PortletDecorationImpl(configuration, validator, basePath, new SessionPathResolverCache(
                requestContext.getRequest().getSession()));
    }
    
    public LayoutDecoration getLayoutDecoration(String name, RequestContext requestContext)
           
    {
        Path basePath = createClientPath(name, requestContext, Fragment.LAYOUT);

        Properties configuration = getConfiguration(name);
        configuration.setProperty("name", name);
        return new LayoutDecorationImpl(configuration, validator, basePath, new SessionPathResolverCache(
                requestContext.getRequest().getSession()));
    }

    public void setServletContext(ServletContext servletContext)
    {
        this.servletContext = servletContext;

    }

    protected Properties getConfiguration(String name)
    {
        Properties props = new Properties();
        InputStream is = servletContext.getResourceAsStream(decorationsPath + "/" + name + "/" + Decoration.CONFIG_FILE_NAME);
        if (is != null)
        {
            try
            {
                props.load(is);
            }
            catch (IOException e)
            {
                log.warn("Failed to load decoration configuration.", e);
                props.setProperty("id", name);
            }
        }
        else
        {
            props.setProperty("id", name);
        }
        return props;
    }

    protected Path createClientPath(String name, RequestContext requestContext, String decorationType)
    {
        Path basePath = ((Path)decorationsPath.clone())
                          .addSegment(decorationType)
                          .addSegment(name);
        String mediaType = requestContext.getMediaType();
        Locale locale = requestContext.getLocale();
        String language = locale.getLanguage();
        String country = locale.getCountry();
        String variant = locale.getVariant();

        basePath.addSegment(mediaType).addSegment(language);

        if (country != null)
        {
            basePath.addSegment(country);
        }

        if (variant != null)
        {
            basePath.addSegment(variant);
        }
        return basePath;
    }

    protected String getDefaultDecorationName(Fragment fragment, Page page)
    {
        String decoration = fragment.getDecorator();
        if (decoration == null)
        {
            if (fragment.getType().equals(Fragment.PORTLET))
            {
                decoration = page.getDefaultDecorator(Fragment.PORTLET);
            }
            else
            {
                decoration = page.getDefaultDecorator(Fragment.LAYOUT);
            }
        }

        return decoration;
    }

}
