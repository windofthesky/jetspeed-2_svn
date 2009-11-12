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

import java.io.File;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.jetspeed.decoration.Decoration.ActionsOption;
import org.apache.jetspeed.decoration.Decoration.TitleOption;
import org.apache.jetspeed.util.Path;

/**
 * Base class implementing the most common methods shared between
 * LayoutDecorations and PortletDecorations.
 * 
 * 
 * @author <href a="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @author <a href="mailto:smilek@apache.org">Steve Milek</a>
 * 
 * @see org.apache.jetspeed.decoration.Decoration
 * @see org.apache.jetspeed.decoration.LayoutDecoration
 * @see org.apache.jetspeed.decoration.PortletDecoration
 * 
 */
public class BaseDecoration implements Decoration, Serializable
{
    private static final Logger log = LoggerFactory.getLogger(BaseDecoration.class);
    
    protected static final String NO_SUCH_RESOURCE = "no_such_resource";
    protected transient Properties config;
    private transient ResourceValidator validator;        
    private final String name;
    private final Path basePath;
    private final Path baseClientPath;
    private transient PathResolverCache cache;
    private final String commonStylesheet;
    private final String portalStylesheet;
    private final String desktopStylesheet;
    private List actions;
    private String currentModeAction;
    private String currentStateAction;
    private boolean supportsDesktop;
    private Decoration.ActionsOption actionsOption = ActionsOption.SHOW;
    private Decoration.TitleOption titleOption = TitleOption.SHOW;
    private String dragHandleOption;
    
    static final String[] ACTIONS_NAMES = 
    {
        "show", "hide", "dropdown", "hover"    
    };
    static final ActionsOption[] ACTIONS_VALUES = 
    {
        ActionsOption.SHOW, ActionsOption.HIDE, ActionsOption.DROPDOWN, ActionsOption.HOVER     
    };
    static final String[] TITLE_NAMES = 
    {
        "show", "hide"    
    };
    static final TitleOption[] TITLE_VALUES = 
    {
        TitleOption.SHOW, TitleOption.HIDE     
    };
    
    /**
     * 
     * @param config <code>java.util.Properties</code> object containing configuration infomation
     * for this Decoration.
     * @param validator The ResourceValidator to be used in looking up fully-qualified resource pathes
     * @param baseClientPath The "root" of the decroation hierarchy.
     * 
     * @throws InvalidDecorationConfigurationException
     */
    public BaseDecoration( Properties config, ResourceValidator validator, Path basePath, Path baseClientPath, PathResolverCache cache ) 
    {        
        this.config = config;
        this.validator = validator;
        this.basePath = basePath;
        this.baseClientPath= baseClientPath;
        this.cache = cache;
        
        this.name = config.getProperty( "name" );
        String temp = config.getProperty(Decoration.OPTION_ACTIONS);
        if (temp != null)
        {
            for (int ix = 0; ix < ACTIONS_NAMES.length; ix++)
            {
                if (temp.equalsIgnoreCase(ACTIONS_NAMES[ix]))
                {
                    this.actionsOption = ACTIONS_VALUES[ix];
                    break;
                }
            }
        }
        temp = config.getProperty(Decoration.OPTION_TITLE);
        if (temp != null)
        {
            for (int ix = 0; ix < TITLE_NAMES.length; ix++)
            {
                if (temp.equalsIgnoreCase(TITLE_NAMES[ix]))
                {
                    this.titleOption = TITLE_VALUES[ix];
                    break;
                }
            }
        }        
        this.dragHandleOption = config.getProperty(Decoration.OPTION_DRAGHANDLE, ".PTitle");
        if (dragHandleOption.equalsIgnoreCase("none"))
            dragHandleOption = null;
        this.commonStylesheet = config.getProperty( "stylesheet", DEFAULT_COMMON_STYLE_SHEET );
        
        this.supportsDesktop = "true".equalsIgnoreCase( config.getProperty( Decoration.DESKTOP_SUPPORTED_PROPERTY ) );
        if ( this.supportsDesktop )
        {
            this.portalStylesheet = config.getProperty( "stylesheet.portal", DEFAULT_PORTAL_STYLE_SHEET );
            this.desktopStylesheet = config.getProperty( "stylesheet.desktop", DEFAULT_DESKTOP_STYLE_SHEET );
        }
        else
        {
            this.portalStylesheet = null;
            this.desktopStylesheet = null;
        }
        
        if (log.isDebugEnabled())
        {
            log.debug( "BaseDecoration basePath: " + basePath.toString() );
            log.debug( "BaseDecoration baseClientPath: " + baseClientPath.toString() );
        }
    }
    
    public void init(Properties config, ResourceValidator validator, PathResolverCache cache)
    {
        this.config = config;
        this.validator = validator;
        this.cache = cache;
    }    

    public String getName()
    {        
        return name;
    }
    
    public String getBasePath()
    {
        return basePath.toString();
    }
    
    public String getBasePath( String relativePath )
    {
        if ( relativePath == null )
        {
            return basePath.toString();
        }
        return basePath.addSegment( relativePath ).toString();
    }
        
    public String getResource( String path )
    {        
        Path workingPath = baseClientPath.getChild( path );
        
        String hit = cache.getPath( workingPath.toString()); 
        if(  hit != null )
        {
            return hit;
        }
        else
        {
            String locatedPath = getResource( baseClientPath, new Path( path ) );
            if( ! locatedPath.startsWith( NO_SUCH_RESOURCE ) )
            {
                if( ! path.startsWith( "/" ) )
                {
                    locatedPath = locatedPath.substring( 1 );
                }
                cache.addPath( workingPath.toString(), locatedPath );
                return locatedPath;
            }
        }
	    return null;
    }
    
    /**
     * Recursively tries to locate a resource.
     * 
     * @param rootPath initial path to start looking for the <code>searchPath.</code>
     * The "pruning" of the rootPath of subsequest recursive calls follows the logic
     * detailed in the {@link Decoration#getResource(String)} method.
     * @param searchPath relative path to the resource we wish to locate.
     * @return
     * 
     * @see Decoration
     */
    protected String getResource( Path rootPath, Path searchPath )
    {
        String pathString = rootPath.getChild( searchPath ).toString();
        if( validator.resourceExists( pathString ) )
        {
            return pathString;
        }
        else if( rootPath.length() > 0 )
        {
            
            return getResource( rootPath.removeLastPathSegment(), searchPath );
        }
        else
        {
            return NO_SUCH_RESOURCE + searchPath.getFileExtension();
        }
    }

    public String getStyleSheet()
    {
        if ( this.commonStylesheet != null )
        {
            return getResource( this.commonStylesheet );
        }
        return null;
    }
    public String getStyleSheetPortal()
    {
        if ( this.portalStylesheet != null )
        {
            return getResource( this.portalStylesheet );
        }
        return null;
    }
    public String getStyleSheetDesktop()
    {
        if ( this.desktopStylesheet != null )
        {
            return getResource( this.desktopStylesheet );
        }
        return null;
    }

    public List getActions()
    {
       if(actions != null)
       {
           return actions;
       }
       else
       {
           return Collections.EMPTY_LIST;
       }
    }

    public void setActions( List actions )
    {
        this.actions = actions;
    }

    public String getProperty( String name )
    {
        return config.getProperty( name );
    }

    public String getBaseCSSClass()
    {
        return config.getProperty( Decoration.BASE_CSS_CLASS_PROP, getName() );
    }

    public String getCurrentModeAction()
    {
        return this.currentModeAction;
    }
    public void setCurrentModeAction( String currentModeAction )
    {
        this.currentModeAction = currentModeAction;
    }

    public String getCurrentStateAction()
    {
        return this.currentStateAction;
    }
    public void setCurrentStateAction( String currentStateAction )
    {
        this.currentStateAction = currentStateAction;
    }
    
    public String getResourceBundleName()
    {
        return config.getProperty( Decoration.RESOURCE_BUNDLE_PROP );
    }
    
    public ResourceBundle getResourceBundle( Locale locale, org.apache.jetspeed.request.RequestContext context )
    {
        String resourceDirName = context.getConfig().getServletContext()
                .getRealPath( getResource( RESOURCES_DIRECTORY_NAME ) );
        File resourceDir = new File( resourceDirName );
        String resourceName = getResourceBundleName();
        if ( resourceName == null )
        {
            throw new NullPointerException( "Decoration cannot get ResourceBundle due to null value for decoration property " + Decoration.RESOURCE_BUNDLE_PROP + "." );
        }
        if ( !resourceDir.isDirectory() )
        {
            throw new MissingResourceException(
                    "Can't find the resource directory: " + resourceDirName,
                    resourceName + "_" + locale, "" );
        }
        URL[] urls = new URL[1];
        try
        {
            urls[0] = resourceDir.toURL();
        }
        catch ( MalformedURLException e )
        {
            throw new MissingResourceException(
                    "The resource directory cannot be parsed as a URL: "
                            + resourceDirName, resourceName + "_" + locale, "");
        }
        return ResourceBundle.getBundle( resourceName, locale, new URLClassLoader( urls ) );
    }
    
    public boolean supportsDesktop()
    {
        return this.supportsDesktop;
    }
    
    public ActionsOption getActionsOption()
    {
        return this.actionsOption;
    }
    public TitleOption getTitleOption()
    {
        return this.titleOption;
    }
    public String getDragHandle() // returns null for not supported
    {
        return this.getDragHandle();
    }
    
}
