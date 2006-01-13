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
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.Set;

import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.components.portletregistry.PortletRegistry;
import org.apache.jetspeed.decoration.caches.SessionPathResolverCache;
import org.apache.jetspeed.om.common.portlet.MutablePortletApplication;
import org.apache.jetspeed.om.common.portlet.PortletDefinitionComposite;
import org.apache.jetspeed.om.page.Fragment;
import org.apache.jetspeed.om.page.Page;
import org.apache.jetspeed.request.RequestContext;
import org.apache.jetspeed.util.Path;
import org.springframework.web.context.ServletContextAware;

/**
 *
 * @author <href a="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @see org.apache.jetspeed.decoration.DecorationFactory
 */
public class DecorationFactoryImpl implements DecorationFactory, ServletContextAware
{
    private static final Log log = LogFactory.getLog(DecorationFactoryImpl.class);

    private final Path decorationsPath;
    private final ResourceValidator validator;
    private final String defaultLayoutDecorator;
    private final String defaultPortletDecorator;
    private final PortletRegistry registry;

    private ServletContext servletContext;
    
    private Set layoutDecorationsDir = Collections.EMPTY_SET;
    private Set portletDecorationsDir = Collections.EMPTY_SET;
    
    private Set layoutDecorationsList = Collections.EMPTY_SET;
    private Set portletDecorationsList = Collections.EMPTY_SET;

    public DecorationFactoryImpl(String decorationsPath, ResourceValidator validator, String defaultLayoutDecorator, String defaultPortletDecorator)
    {
        this.registry = null;
        this.decorationsPath = new Path(decorationsPath);
        this.validator = validator;
        this.defaultLayoutDecorator = defaultLayoutDecorator;
        this.defaultPortletDecorator = defaultPortletDecorator;
    }

    public DecorationFactoryImpl(PortletRegistry registry,
                                 String decorationsPath,
                                 ResourceValidator validator,
                                 String defaultLayoutDecorator,
                                 String defaultPortletDecorator)
    {
        this.registry =  registry;
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

        // use layout decoration for top level layout root
        // fragments; portlet layouts for all others
        if (fragment.getType().equals(Fragment.LAYOUT) && fragment.equals(page.getRootFragment()))
        {
            decoration = getLayoutDecoration(decorationName, requestContext);
        }
        else
        {
            decoration = getPortletDecoration(decorationName, requestContext);
        }

        return decoration;
    }

    public PortletDecoration getPortletDecoration(String name, RequestContext requestContext)

    {
        Path basePath = createClientPath(name, requestContext, Fragment.PORTLET);

        Properties configuration = getConfiguration(name, Fragment.PORTLET);
        configuration.setProperty("name", name);
        return new PortletDecorationImpl(configuration, validator, basePath, new SessionPathResolverCache(
                requestContext.getRequest().getSession()));
    }

    public LayoutDecoration getLayoutDecoration(String name, RequestContext requestContext)

    {
        Path basePath = createClientPath(name, requestContext, Fragment.LAYOUT);

        Properties configuration = getConfiguration(name, Fragment.LAYOUT);
        configuration.setProperty("name", name);
        return new LayoutDecorationImpl(configuration, validator, basePath, new SessionPathResolverCache(
                requestContext.getRequest().getSession()));
    }

    public void setServletContext(ServletContext servletContext)
    {
        this.servletContext = servletContext;

    }

    /**
     * Gets the configuration (decorator.properties) object for the decoration.
     * @param name Name of the Decoration.
     * @return <code>java.util.Properties</code> representing the configuration
     * object.
     */
    protected Properties getConfiguration(String name, String type)
    {
        Properties props = new Properties();
        InputStream is = servletContext.getResourceAsStream(decorationsPath + "/"+ type +"/"+ name + "/" + Decoration.CONFIG_FILE_NAME);
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
            log.warn("Could not locate the decorator.properties configuration file for decoration \""+name+
                 "\".  This decoration may not exist.");
            props.setProperty("id", name);
        }
        return props;
    }

    /**
     * Creates a <code>org.apache.jetspeed.util.Path</code> object based
     * off of the user's client browser and locale.
     *
     * @param name Decroator's name
     * @param requestContext  Current portal request.
     * @param decorationType Type of decoration, either <code>layout</code>
     * or <code>portlet</code>
     * @return
     *
     * @see Path
     * @see RequestContext
     */
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

    /**
     * Returns a the default decoration name for the specific Fragment type.
     *
     * @param fragment Fragment whose default decroation has been requested
     * @param page Page this fragment belongs to.
     * @return Default decorator name.
     *
     * @see Page
     * @see Fragment
     */
    protected String getDefaultDecorationName(Fragment fragment, Page page)
    {
        // get specified decorator
        String decoration = fragment.getDecorator();
        if (decoration == null)
        {
            if (fragment.getType().equals(Fragment.LAYOUT))
            {
                if (fragment.equals(page.getRootFragment()))
                {
                    // use page specified layout decorator name
                    decoration = page.getEffectiveDefaultDecorator(Fragment.LAYOUT);
                }
                else
                {
                    // use default nested layout portlet decorator name
                    decoration = DEFAULT_NESTED_LAYOUT_PORTLET_DECORATOR;
                }
            }
            else
            {
                // use page specified default portlet decorator name
                decoration = page.getEffectiveDefaultDecorator(Fragment.PORTLET);
            }
        }

        return decoration;
    }

    public void clearCache(RequestContext requestContext)
    {
        new SessionPathResolverCache(requestContext.getRequest().getSession()).clear();
    }


    /**
     * Get the portal-wide list of page decorations.
     *
     * @return A list of page decorations of type <code>Decoration</code>
     */
    public Set getPageDecorations(RequestContext request)
    {
        Set decorations = servletContext.getResourcePaths(decorationsPath.toString()+"/layout");
        if(!layoutDecorationsDir.equals(decorations))
        {
            layoutDecorationsList = getListing(decorations);
            layoutDecorationsDir = decorations;
            
        }
        return layoutDecorationsList;
    }

        /**
     * Get the portal-wide list of portlet decorations.
     *
     * @return A list of portlet decorations of type <code>String</code>
     */
    public Set getPortletDecorations(RequestContext request)
    {
        Set decorations = servletContext.getResourcePaths(decorationsPath.toString()+"/portlet");
        if(!portletDecorationsDir.equals(decorations))
        {
            portletDecorationsList = getListing(decorations);
            portletDecorationsDir = decorations;
            
        }
        return portletDecorationsList;
    }

    /**
     * Get the portal-wide list of available layouts.
     *
     * @return A list of layout portlets of type <code>PortletDefinitionComposite</code>
     */
    public List getLayouts(RequestContext request)
    {
        List list = new LinkedList();
        Iterator portlets = registry.getAllPortletDefinitions().iterator();
        while (portlets.hasNext())
        {
            PortletDefinitionComposite portlet = (PortletDefinitionComposite)portlets.next();
            MutablePortletApplication muta = (MutablePortletApplication)portlet.getPortletApplicationDefinition();
            String appName = muta.getName();
            if (appName == null)
                continue;
            if (!appName.equals("jetspeed-layouts"))
                continue;

            String uniqueName = appName + "::" + portlet.getName();
            list.add(new LayoutInfoImpl(uniqueName,
                     portlet.getDisplayNameText(request.getLocale()),
                     portlet.getDescriptionText(request.getLocale())));

        }
        return list;
    }
    
    protected Set getListing(Set rawList)
    {
        Iterator itr = rawList.iterator();
        Set filteredList = new HashSet();
        while(itr.hasNext())
        {
            Path path = new Path((String) itr.next());
            if(path.getFileName() == null && validator.resourceExists(path.toString()+"decorator.properties"))
            {
                int offset = path.length() - 1;
                filteredList.add(path.getSegment(offset));
            }
        }
        return filteredList;
    }

}
