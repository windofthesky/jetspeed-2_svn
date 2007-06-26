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
package org.apache.jetspeed.decoration;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
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
import org.apache.jetspeed.desktop.JetspeedDesktop;
import org.springframework.web.context.ServletContextAware;

/**
 *
 * @author <href a="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @author <a href="mailto:smilek@apache.org">Steve Milek</a>
 * @see org.apache.jetspeed.decoration.DecorationFactory
 */
public class DecorationFactoryImpl implements DecorationFactory, ServletContextAware
{
    private static final Log log = LogFactory.getLog(DecorationFactoryImpl.class);

    private final Path decorationsPath;
    private final Path portletDecorationsPath;
    private final Path layoutDecorationsPath;
    private final String portletDecorationsPathStr;
    private final String layoutDecorationsPathStr;
    
    private final ResourceValidator validator;
    private final PortletRegistry registry;
    
    private ServletContext servletContext;

    private String defaultDesktopLayoutDecoration = null;
    private String defaultDesktopPortletDecoration = null;
    
    private Set layoutDecorationsDir = Collections.EMPTY_SET;
    private Set portletDecorationsDir = Collections.EMPTY_SET;
    private Set desktopLayoutDecorationsDir = Collections.EMPTY_SET;
    private Set desktopPortletDecorationsDir = Collections.EMPTY_SET;
    
    private Set layoutDecorationsList = Collections.EMPTY_SET;
    private Set portletDecorationsList = Collections.EMPTY_SET;
    private Set desktopLayoutDecorationsList = Collections.EMPTY_SET;
    private Set desktopPortletDecorationsList = Collections.EMPTY_SET;
    
    private Map portletDecoratorProperties = new HashMap();
    private Map layoutDecoratorProperties = new HashMap();

    public DecorationFactoryImpl( String decorationsPath, 
                                  ResourceValidator validator )
    {
        this( null, decorationsPath, validator );
    }

    public DecorationFactoryImpl( PortletRegistry registry,
            String decorationsPath, 
            ResourceValidator validator )
    {
        this.registry =  registry;
        this.decorationsPath = new Path( decorationsPath );
        this.layoutDecorationsPath = getBasePath( Fragment.LAYOUT );
        this.layoutDecorationsPathStr = this.layoutDecorationsPath.toString();
        this.portletDecorationsPath = getBasePath( Fragment.PORTLET );
        this.portletDecorationsPathStr = this.portletDecorationsPath.toString();
        this.validator = validator;
    }
        
    public ResourceValidator getResourceValidator()
    {
        return validator;
    }    

    public Theme getTheme( Page page, RequestContext requestContext )
    {
        return new PageTheme(page, this, requestContext);
    }
    
    public Decoration getDecoration( Page page, Fragment fragment, RequestContext requestContext )
    {
        String decorationName = getDefaultDecorationName( fragment, page );
        Decoration decoration;

        // use layout decoration for top level layout root fragments
        //    and use portlet decoration for all other fragments
        if ( fragment.getType().equals( Fragment.LAYOUT ) )
        {
            decoration = getLayoutDecoration( decorationName, requestContext );
        }
        else
        {
            decoration = getPortletDecoration( decorationName, requestContext );
        }

        if ( isDesktopEnabled( requestContext ) )
        {   // assure that selected decoration supports /desktop
            //    otherwise get default desktop decoration for fragment type
            if ( decoration == null || ! decoration.supportsDesktop() )
            {
                String defaultDecoration = null;
                if (fragment.getType().equals( Fragment.LAYOUT ) )
                {
                    defaultDecoration = getDefaultDesktopLayoutDecoration();
                    decoration = getLayoutDecoration( defaultDecoration, requestContext );
                }
                else
                {
                    defaultDecoration = getDefaultDesktopPortletDecoration();
                    decoration = getPortletDecoration( defaultDecoration, requestContext );
                }
                if ( decoration == null )
                {
                    String errMsg = "Cannot locate default desktop " + fragment.getType() + " decoration " + ( defaultDecoration == null ? "null" : ("\"" + defaultDecoration + "\"") ) + " (decoration " + ( defaultDecoration == null ? "null" : ("\"" + decorationName + "\"") ) + " specified for page could either not be located or does not support desktop). No desktop compatible " + fragment.getType() + " decoration is available.";
                    log.equals( errMsg );
                }
            }
        }
        return decoration;
    }

    public PortletDecoration getPortletDecoration( String name, RequestContext requestContext )
    {
        Path basePath = getPortletDecorationBasePath( name );
        Path baseClientPath = createClientPath( name, (Path)basePath.clone(), requestContext, Fragment.PORTLET );
        Properties configuration = getConfiguration( name, Fragment.PORTLET );
        SessionPathResolverCache sessionPathResolver = new SessionPathResolverCache( requestContext.getRequest().getSession() );
        return new PortletDecorationImpl( configuration, validator, basePath, baseClientPath, sessionPathResolver );
    }

    public LayoutDecoration getLayoutDecoration( String name, RequestContext requestContext )
    {
        Path basePath = getLayoutDecorationBasePath( name );
        Path baseClientPath = createClientPath( name, (Path)basePath.clone(), requestContext, Fragment.LAYOUT );
        Properties configuration = getConfiguration( name, Fragment.LAYOUT );
        SessionPathResolverCache sessionPathResolver = new SessionPathResolverCache( requestContext.getRequest().getSession() );
        return new LayoutDecorationImpl( configuration, validator, basePath, baseClientPath, sessionPathResolver );
    }    
    
    public boolean isDesktopEnabled( RequestContext requestContext )
    {
        Boolean desktopEnabled = (Boolean)requestContext.getAttribute( JetspeedDesktop.DESKTOP_ENABLED_REQUEST_ATTRIBUTE );
        return ( desktopEnabled != null && desktopEnabled.booleanValue() ? true : false );
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
    public Properties getConfiguration( String name, String type )
    {
        Properties props = null;
        if ( type.equals( Fragment.PORTLET ) )
        {
            props = (Properties)this.portletDecoratorProperties.get( name );
            
        }
        else
        {
            props = (Properties)this.layoutDecoratorProperties.get( name );
        }        
        if ( props != null )
        {
            return props;
        }
        
        props = new Properties();
        InputStream is = null;
        
        // load Decoration.CONFIG_FILE_NAME (decorator.properties)
        try
        {
            is = servletContext.getResourceAsStream( decorationsPath + "/" + type + "/" + name + "/" + Decoration.CONFIG_FILE_NAME );
            if (is != null)
            {                
                props.load( is );
            }
            else
            {
                log.warn( "Could not locate the " + Decoration.CONFIG_FILE_NAME + " configuration file for decoration \"" + name + "\".  This decoration may not exist." );
                props.setProperty( "id", name );
                props.setProperty( "name", name );
            }
        }
        catch ( Exception e )
        {
            log.warn( "Failed to load the " + Decoration.CONFIG_FILE_NAME + " configuration file for decoration \"" + name + "\".", e );
            props.setProperty( "id", name );
            props.setProperty( "name", name );
        }
        finally
        {
            if ( is != null )
            {
                try
                {
                    is.close();
                }
                catch (IOException e)
                {
                    log.warn("Failed to close decoration configuration.", e);
                }
            }
            String decorationIdPropVal = props.getProperty( "id" );
            String decorationNamePropVal = props.getProperty( "name" );
            if ( decorationIdPropVal == null )
            {
                if ( decorationNamePropVal != null )
                {
                    decorationIdPropVal = decorationNamePropVal;
                }
                else
                {
                    decorationIdPropVal = name;
                }
                props.setProperty( "id", decorationIdPropVal );
            }
            
            if ( decorationNamePropVal == null )
            {
                props.setProperty( "name", decorationIdPropVal );
            }
        }
        
        // load Decoration.CONFIG_DESKTOP_FILE_NAME (decoratordesktop.properties)
        try
        {
            is = servletContext.getResourceAsStream( decorationsPath + "/" + type + "/" + name + "/" + Decoration.CONFIG_DESKTOP_FILE_NAME );
            if ( is != null )
            {                
                props.load( is );
                if ( props.getProperty( Decoration.DESKTOP_SUPPORTED_PROPERTY ) == null )
                {
                    props.setProperty( Decoration.DESKTOP_SUPPORTED_PROPERTY, "true" );
                }
            }
            else
            {
                log.debug( "Could not locate the " + Decoration.CONFIG_DESKTOP_FILE_NAME + " configuration file for decoration \"" + name + "\".  This decoration may not exist." );
            }
        }
        catch ( Exception e )
        {
            log.warn( "Failed to load the " + Decoration.CONFIG_DESKTOP_FILE_NAME + " configuration file for decoration \"" + name + "\".", e );
        }
        finally
        {
            if ( is != null )
            {
                try
                {
                    is.close();
                }
                catch ( IOException e )
                {
                    log.warn( "Failed to close decoration desktop configuration.", e );
                }
            }
            if ( props.getProperty( Decoration.DESKTOP_SUPPORTED_PROPERTY ) == null )
            {
                props.setProperty( Decoration.DESKTOP_SUPPORTED_PROPERTY, "false" );
            }
        }

        if ( type.equals( Fragment.PORTLET ) )
        {
            this.portletDecoratorProperties.put( name, props );
        }
        else
        {
            this.layoutDecoratorProperties.put( name, props );
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
    protected Path createClientPath( String name, RequestContext requestContext, String decorationType )
    {
        return createClientPath( name, null, requestContext, decorationType );
    }   
    
    private Path createClientPath( String name, Path basePath, RequestContext requestContext, String decorationType )
    {
        if ( basePath == null )
            basePath = getBasePath( name, decorationType );
        String mediaType = requestContext.getMediaType();
        Locale locale = requestContext.getLocale();
        String language = locale.getLanguage();
        String country = locale.getCountry();
        String variant = locale.getVariant();

        basePath.addSegment( mediaType ).addSegment( language );

        if ( country != null )
        {
            basePath.addSegment( country );
        }

        if (variant != null)
        {
            basePath.addSegment( variant );
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

    protected Path getBasePath( String decorationType )
    {
        Path basePath = ((Path)decorationsPath.clone()).addSegment(decorationType);
        return basePath;
    }
    
    protected Path getBasePath( String name, String decorationType )
    {
        Path basePath = ((Path)decorationsPath.clone()).addSegment(decorationType).addSegment(name);
        return basePath;
    }
    
    protected Path getLayoutDecorationBasePath( String name )
    {
        Path basePath = ((Path)layoutDecorationsPath.clone()).addSegment(name);
        return basePath;
    }
    protected Path getPortletDecorationBasePath( String name )
    {
        Path basePath = ((Path)portletDecorationsPath.clone()).addSegment(name);
        return basePath;
    }
    
    public String getLayoutDecorationsBasePath()
    {
        return this.layoutDecorationsPathStr;
    }
    
    public String getPortletDecorationsBasePath()
    {
        return this.portletDecorationsPathStr;
    }

    /**
     * Get the portal-wide list of page decorations.
     *
     * @return A list of page decorations of type <code>Decoration</code>
     */
    public Set getPageDecorations( RequestContext request )
    {
        Set decorations = servletContext.getResourcePaths( decorationsPath.toString() + "/layout" );
        if( ! layoutDecorationsDir.equals( decorations ) )
        {
            layoutDecorationsList = getListing( decorations, Decoration.CONFIG_FILE_NAME );
            layoutDecorationsDir = decorations;
            
        }
        return layoutDecorationsList;
    }
    
    /**
     * Get the portal-wide list of available desktop page decorations.
     * 
     * @return A list of desktop skins of type <code>String</code>
     */        
    public Set getDesktopPageDecorations( RequestContext request )
    {
        Set decorations = servletContext.getResourcePaths( decorationsPath.toString() + "/layout" );
        if( ! desktopLayoutDecorationsDir.equals( decorations ) )
        {
            desktopLayoutDecorationsList = getListing( decorations, Decoration.CONFIG_DESKTOP_FILE_NAME );
            desktopLayoutDecorationsDir = decorations;
            
        }
        return desktopLayoutDecorationsList;
    }

    /**
     * Get the portal-wide list of portlet decorations.
     *
     * @return A list of portlet decorations of type <code>String</code>
     */
    public Set getPortletDecorations( RequestContext request )
    {
        Set decorations = servletContext.getResourcePaths( decorationsPath.toString() + "/portlet" );
        if( ! portletDecorationsDir.equals( decorations ) )
        {
            portletDecorationsList = getListing( decorations, Decoration.CONFIG_FILE_NAME );
            portletDecorationsDir = decorations;
            
        }
        return portletDecorationsList;
    }
    
    /**
     * Get the portal-wide list of desktop portlet decorations.
     *
     * @return A list of portlet decorations of type <code>String</code>
     */
    public Set getDesktopPortletDecorations( RequestContext request )
    {
        Set decorations = servletContext.getResourcePaths( decorationsPath.toString() + "/portlet" );
        if( ! desktopPortletDecorationsDir.equals( decorations ) )
        {
            desktopPortletDecorationsList = getListing( decorations, Decoration.CONFIG_DESKTOP_FILE_NAME );
            desktopPortletDecorationsDir = decorations;
            
        }
        return desktopPortletDecorationsList;
    }
    
    /**
     * Get the portal-wide list of available layouts.
     *
     * @return A list of layout portlets of type <code>PortletDefinitionComposite</code>
     */
    public List getLayouts( RequestContext request )
    {
        List list = new LinkedList();
        Iterator portlets = registry.getAllPortletDefinitions().iterator();
        while ( portlets.hasNext() )
        {
            PortletDefinitionComposite portlet = (PortletDefinitionComposite)portlets.next();
            MutablePortletApplication muta = (MutablePortletApplication)portlet.getPortletApplicationDefinition();
            String appName = muta.getName();
            if ( appName == null )
                continue;
            if ( ! appName.equals( "jetspeed-layouts" ) )
                continue;

            String uniqueName = appName + "::" + portlet.getName();
            list.add( new LayoutInfoImpl( uniqueName,
                      portlet.getDisplayNameText( request.getLocale() ),
                      portlet.getDescriptionText( request.getLocale() ) ) );

        }
        return list;
    }
    
    protected Set getListing(Set rawList, String propsFile)
    {
        Iterator itr = rawList.iterator();
        Set filteredList = new HashSet();
        while(itr.hasNext())
        {
            Path path = new Path((String) itr.next());
            if(path.getFileName() == null && validator.resourceExists(path.toString() + propsFile))
            {
                int offset = path.length() - 1;
                filteredList.add(path.getSegment(offset));
            }
        }
        return filteredList;
    }

    public String getDefaultDesktopLayoutDecoration()
    {
        synchronized ( this )
        {
            return this.defaultDesktopLayoutDecoration;
        }
    }
    public void setDefaultDesktopLayoutDecoration( String newOne )
    {
        synchronized ( this )
        {
            this.defaultDesktopLayoutDecoration = newOne;
        }
    }
    public String getDefaultDesktopPortletDecoration()
    {
        synchronized ( this )
        {
            return this.defaultDesktopPortletDecoration;
        }
    }
    public void setDefaultDesktopPortletDecoration( String newOne )
    {
        synchronized ( this )
        {
            this.defaultDesktopPortletDecoration = newOne;
        }
    }
}
