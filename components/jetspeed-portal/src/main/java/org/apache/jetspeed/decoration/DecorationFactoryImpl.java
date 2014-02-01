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

import org.apache.jetspeed.cache.CacheElement;
import org.apache.jetspeed.cache.JetspeedCache;
import org.apache.jetspeed.components.portletregistry.PortletRegistry;
import org.apache.jetspeed.decoration.caches.SessionPathResolverCache;
import org.apache.jetspeed.desktop.JetspeedDesktop;
import org.apache.jetspeed.om.page.ContentFragment;
import org.apache.jetspeed.om.page.ContentPage;
import org.apache.jetspeed.om.portlet.PortletApplication;
import org.apache.jetspeed.om.portlet.PortletDefinition;
import org.apache.jetspeed.request.RequestContext;
import org.apache.jetspeed.util.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.ServletContextAware;

import javax.servlet.ServletContext;
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
import java.util.SortedSet;
import java.util.TreeSet;

/**
 *
 * @author <href a="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @author <a href="mailto:smilek@apache.org">Steve Milek</a>
 * @see org.apache.jetspeed.decoration.DecorationFactory
 */
public class DecorationFactoryImpl implements DecorationFactory, ServletContextAware
{
    private static final Logger log = LoggerFactory.getLogger(DecorationFactoryImpl.class);

    private final Path decorationsPath;
    private final Path portletDecorationsPath;
    private final Path layoutDecorationsPath;
    private final String portletDecorationsPathStr;
    private final String layoutDecorationsPathStr;
    
    private final ResourceValidator validator;
    private final PortletRegistry registry;
    
    /** cache to hold decoration Properties objects **/
    private JetspeedCache decorationConfigurationCache;
    
    private ServletContext servletContext;

    private String defaultDesktopLayoutDecoration = null;
    private String defaultDesktopPortletDecoration = null;
    private String defaultLayoutDecoration = null;
    private String defaultPortletDecoration = null;
    
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
        this( null, decorationsPath, validator, null, null, null );
    }
    
    public DecorationFactoryImpl( String decorationsPath, 
                                  ResourceValidator validator,
                                  JetspeedCache decorationConfigurationCache )
    {
        this( null, decorationsPath, validator, decorationConfigurationCache, null, null );
    }

    public DecorationFactoryImpl( PortletRegistry registry,
                                  String decorationsPath, 
                                  ResourceValidator validator,
                                  JetspeedCache decorationConfigurationCache,
                                  String defLayoutDecoration,
                                  String defPortletDecoration)
    {
        this.registry =  registry;
        this.decorationsPath = new Path( decorationsPath );
        this.layoutDecorationsPath = getBasePath( ContentFragment.LAYOUT );
        this.layoutDecorationsPathStr = this.layoutDecorationsPath.toString();
        this.portletDecorationsPath = getBasePath( ContentFragment.PORTLET );
        this.portletDecorationsPathStr = this.portletDecorationsPath.toString();
        this.validator = validator;
        this.decorationConfigurationCache = decorationConfigurationCache;
        this.defaultLayoutDecoration = defLayoutDecoration;
        this.defaultPortletDecoration = defPortletDecoration;
    }
        
    public ResourceValidator getResourceValidator()
    {
        return validator;
    }
    
    protected JetspeedCache getDecorationConfigurationCache()
    {
    	return decorationConfigurationCache;
    }

    public Theme getTheme( ContentPage page, RequestContext requestContext )
    {
        return new PageTheme(page, this, requestContext);
    }
    
    public Decoration getDecoration( ContentPage page, ContentFragment fragment, RequestContext requestContext )
    {
        String decorationName = getDefaultDecorationName( fragment, page );
        Decoration decoration;

        // use layout decoration for top level layout root fragments
        //    and use portlet decoration for all other fragments
        boolean isLayout = fragment.getType().equals( ContentFragment.LAYOUT );
        if ( isLayout )
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
                if ( isLayout )
                {
                	if ( decoration == null || fragment.equals( page.getRootFragment() ) )
                	{
                		defaultDecoration = getDefaultDesktopLayoutDecoration();
                		decoration = getLayoutDecoration( defaultDecoration, requestContext );
                	}
                }
                else
                {
                    defaultDecoration = getDefaultDesktopPortletDecoration();
                    decoration = getPortletDecoration( defaultDecoration, requestContext );
                }
                if ( decoration == null )
                {
                    String errMsg = "Cannot locate default desktop " + fragment.getType() + " decoration " + ( defaultDecoration == null ? "null" : ("\"" + defaultDecoration + "\"") ) + " (decoration " + ( defaultDecoration == null ? "null" : ("\"" + decorationName + "\"") ) + " specified for page could either not be located or does not support desktop). No desktop compatible " + fragment.getType() + " decoration is available.";
                    log.error( errMsg );
                }
            }
        }
        return decoration;
    }

    public PortletDecoration getPortletDecoration( String name, RequestContext requestContext )
    {
        Path basePath = getPortletDecorationBasePath( name );
        Path baseClientPath = createClientPath( name, basePath, requestContext, ContentFragment.PORTLET );
        Properties configuration = getConfiguration( name, ContentFragment.PORTLET );
        SessionPathResolverCache sessionPathResolver = new SessionPathResolverCache( requestContext.getRequest().getSession() );
        return new PortletDecorationImpl( configuration, validator, basePath, baseClientPath, sessionPathResolver );
    }

    public LayoutDecoration getLayoutDecoration( String name, RequestContext requestContext )
    {
        Path basePath = getLayoutDecorationBasePath( name );
        Path baseClientPath = createClientPath( name, basePath, requestContext, ContentFragment.LAYOUT );
        Properties configuration = getConfiguration( name, ContentFragment.LAYOUT );
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

    protected Properties getCachedConfiguration( String name, String type )
    {
    	if ( decorationConfigurationCache == null )
    	{
    		if ( type.equals( ContentFragment.PORTLET ) )
    		{
    			return (Properties)this.portletDecoratorProperties.get( name );
    		}
    		else
    		{
    			return (Properties)this.layoutDecoratorProperties.get( name );
    		}
    	}
    	CacheElement cachedElement = decorationConfigurationCache.get( getCachedConfigurationKey( type, name ) );
        if (cachedElement != null)
        	return (Properties)cachedElement.getContent();  
        return null;
    }
    protected void setCachedConfiguration( String name, String type, Properties props )
    {
    	if ( decorationConfigurationCache == null )
    	{
    		if ( type.equals( ContentFragment.PORTLET ) )
    		{
    			this.portletDecoratorProperties.put( name, props );
    		}
    		else
    		{
    			this.layoutDecoratorProperties.put( name, props );
    		}
    	}
    	else
    	{
    		CacheElement cachedElement = decorationConfigurationCache.createElement( getCachedConfigurationKey( type, name ), props );
    		cachedElement.setTimeToIdleSeconds(decorationConfigurationCache.getTimeToIdleSeconds());
    		cachedElement.setTimeToLiveSeconds(decorationConfigurationCache.getTimeToLiveSeconds());
    		decorationConfigurationCache.put( cachedElement );
    	}
    }
    protected String getCachedConfigurationKey( String type, String name )
    {
    	return type + "."  + name;
    }
    
    /**
     * Gets the configuration (decorator.properties) object for the decoration.
     * @param name Name of the Decoration.
     * @return <code>java.util.Properties</code> representing the configuration
     * object.
     */
    public Properties getConfiguration( String name, String type )
    {
        Properties props = getCachedConfiguration( name, type );
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
        catch ( Throwable e )
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
        catch ( Throwable e )
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
        
        setCachedConfiguration( name, type, props );
        
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

        basePath = basePath.addSegment( mediaType ).addSegment( language );

        if ( country != null )
        {
            basePath = basePath.addSegment( country );
        }

        if (variant != null)
        {
            basePath = basePath.addSegment( variant );
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
     * @param page
     * @param fragment
     */
    protected String getDefaultDecorationName(ContentFragment fragment, ContentPage page)
    {
        // get specified decorator
        String decoration = fragment.getDecorator();
        if (decoration == null)
        {
            if (fragment.getType().equals(ContentFragment.LAYOUT))
            {
                if (fragment.equals(page.getRootFragment()))
                {
                    // use page specified layout decorator name
                    decoration = page.getEffectiveDefaultDecorator(ContentFragment.LAYOUT);
                    if (decoration == null)
                    {
                        decoration = this.defaultLayoutDecoration;
                    }                    
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
                decoration = page.getEffectiveDefaultDecorator(ContentFragment.PORTLET);
                if (decoration == null)
                {
                    decoration = this.defaultPortletDecoration;
                }                
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
        return decorationsPath.addSegment(decorationType);
    }
    
    protected Path getBasePath( String name, String decorationType )
    {
        return decorationsPath.addSegment(decorationType).addSegment(name);
    }
    
    protected Path getLayoutDecorationBasePath( String name )
    {
        return layoutDecorationsPath.addSegment(name);
    }
    protected Path getPortletDecorationBasePath( String name )
    {
        return portletDecorationsPath.addSegment(name);
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
    public Set<String> getPageDecorations( RequestContext request )
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
    public Set<String> getDesktopPageDecorations( RequestContext request )
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
    public Set<String> getPortletDecorations( RequestContext request )
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
    public Set<String> getDesktopPortletDecorations( RequestContext request )
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
    public List<LayoutInfo> getLayouts( RequestContext request )
    {
        List list = new LinkedList();
        Iterator portlets = registry.getAllDefinitions().iterator();
        while ( portlets.hasNext() )
        {
            PortletDefinition portlet = (PortletDefinition)portlets.next();
            PortletApplication app = (PortletApplication)portlet.getApplication();
            String appName = app.getName();
            if ( appName == null )
                continue;
            if ( ! appName.equals( "jetspeed-layouts" ) )
                continue;

            String uniqueName = appName + "::" + portlet.getPortletName();
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
        SortedSet decoraters = new TreeSet();
        decoraters.addAll(filteredList);
        return decoraters;
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
    public String getDefaultPortletDecoration()
    {
        return this.defaultPortletDecoration;
    }
    
    public String getDefaultLayoutDecoration()
    {
        return this.defaultLayoutDecoration;        
    }    
}
