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

import java.util.Collections;
import java.util.List;
import java.util.Properties;

import org.apache.jetspeed.util.Path;

/**
 * Base class implementing the most common methods shared between
 * LayoutDecorations and PortletDecorations.
 * 
 * 
 * @author <href a="mailto:weaver@apache.org">Scott T. Weaver</a>
 * 
 * @see org.apache.jetspeed.decoration.Decoration
 * @see org.apache.jetspeed.decoration.LayoutDecoration
 * @see org.apache.jetspeed.decoration.PortletDecoration
 * 
 */
public class BaseDecoration implements Decoration
{
    protected static final String NO_SUCH_RESOURCE = "no_such_resource";
    protected final Properties config;
    private final ResourceValidator validator;
    private final String name;
    private final Path basePath;
    private final PathResolverCache cache;
    private final String styleSheet;
    private List actions;
    
    /**
     * 
     * @param config <code>java.util.Properties</code> object containing configuration infomation
     * for this Decoration.
     * @param validator The ResourceValidator to be used in looking up fully-qualified resource pathes
     * @param basePath The "root" of the decroation hierarchy.
     * 
     * @throws InvalidDecorationConfigurationException
     */
    public BaseDecoration(Properties config, ResourceValidator validator, Path basePath, PathResolverCache cache) 
    {        
        this.config = config;
        this.validator = validator;
        this.basePath= basePath;
        this.cache = cache;
        this.styleSheet = config.getProperty("stylesheet", DEFAULT_STYLE_SHEET);
        
        this.name = config.getProperty("name");      
    }

    public String getName()
    {        
        return name;
    }

    public String getResource(String path)
    {        
        Path workingPath = basePath.getChild(path);
        
        if(cache.getPath(workingPath.toString()) != null)
        {
            return cache.getPath(workingPath.toString());
        }
        else
        {
            String locatedPath = getResource((Path)basePath.clone(), new Path(path));
            if(!locatedPath.startsWith(NO_SUCH_RESOURCE))
            {
                if(!path.startsWith("/"))
                {
                    locatedPath = locatedPath.substring(1);
                }
                cache.addPath(workingPath.toString(), locatedPath);
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
    protected String getResource(Path rootPath, Path searchPath)
    {
        String pathString = rootPath.getChild(searchPath).toString();
        if(validator.resourceExists(pathString))
        {
            return pathString;
        }
        else if((rootPath.length()) > 0)
        {
            rootPath.removeLastPathSegment();
            return getResource(rootPath,searchPath);
        }
        else
        {
            return NO_SUCH_RESOURCE+searchPath.getFileExtension();
        }
    }

    public String getStyleSheet()
    {       
        return getResource(styleSheet);
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

    public void setActions(List actions)
    {
        this.actions = actions;
    }

    public String getProperty(String name)
    {
        return config.getProperty(name);
    }

    public String getBaseCSSClass()
    {
        return config.getProperty(Decoration.BASE_CSS_CLASS_PROP, getName());
    }
    
}
