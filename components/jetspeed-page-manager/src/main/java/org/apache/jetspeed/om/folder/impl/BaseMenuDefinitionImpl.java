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
package org.apache.jetspeed.om.folder.impl;

import org.apache.jetspeed.om.folder.MenuDefinition;
import org.apache.jetspeed.om.folder.MenuDefinitionElement;
import org.apache.jetspeed.page.impl.DatabasePageManagerUtils;

import java.util.List;

/**
 * BaseMenuDefinitionImpl
 * 
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id:$
 */
public abstract class BaseMenuDefinitionImpl extends BaseMenuDefinitionMetadata implements MenuDefinition
{
    private String name;
    private String options;
    private int depth;
    private boolean paths;
    private boolean regexp;
    private String profile;
    private String order;
    private String skin;
    private String title;
    private String shortTitle;
    private List<MenuDefinitionElement> elements;

    /**
     * accessElements
     *
     * Access mutable persistent collection member for List wrappers.
     *
     * @return persistent collection
     */
    public List<MenuDefinitionElement> accessElements()
    {
        // create initial collection if necessary
        if (elements == null)
        {
            elements = DatabasePageManagerUtils.createList();
        }
        return elements;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.folder.MenuDefinition#getName()
     */
    public String getName()
    {
        return name;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.folder.MenuDefinition#setName(java.lang.String)
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.folder.MenuDefinition#getOptions()
     */
    public String getOptions()
    {
        return options;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.folder.MenuDefinition#setOptions(java.lang.String)
     */
    public void setOptions(String options)
    {
        this.options = options;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.folder.MenuDefinition#getDepth()
     */
    public int getDepth()
    {
        return depth;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.folder.MenuDefinition#setDepth(int)
     */
    public void setDepth(int depth)
    {
        this.depth = depth;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.folder.MenuDefinition#getPaths()
     */
    public boolean isPaths()
    {
        return paths;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.folder.MenuDefinition#setPaths(boolean)
     */
    public void setPaths(boolean paths)
    {
        this.paths = paths;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.folder.MenuDefinition#getRegexp()
     */
    public boolean isRegexp()
    {
        return regexp;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.folder.MenuDefinition#setRegexp(boolean)
     */
    public void setRegexp(boolean regexp)
    {
        this.regexp = regexp;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.folder.MenuDefinition#getProfile()
     */
    public String getProfile()
    {
        return profile;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.folder.MenuDefinition#setProfile(java.lang.String)
     */
    public void setProfile(String locatorName)
    {
        profile = locatorName;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.folder.MenuDefinition#getOrder()
     */
    public String getOrder()
    {
        return order;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.folder.MenuDefinition#setOrder(java.lang.String)
     */
    public void setOrder(String order)
    {
        this.order = order;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.folder.MenuDefinition#getSkin()
     */
    public String getSkin()
    {
        return skin;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.folder.MenuDefinition#setSkin(java.lang.String)
     */
    public void setSkin(String name)
    {
        skin = name;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.folder.MenuDefinition#getTitle()
     */
    public String getTitle()
    {
        return title;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.folder.MenuDefinition#setTitle(java.lang.String)
     */
    public void setTitle(String title)
    {
        this.title = title;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.folder.MenuDefinition#getShortTitle()
     */
    public String getShortTitle()
    {
        return shortTitle; 
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.folder.MenuDefinition#setShortTitle(java.lang.String)
     */
    public void setShortTitle(String title)
    {
        this.shortTitle = title;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.folder.MenuDefinition#getMenuElements()
     */
    public abstract List<MenuDefinitionElement> getMenuElements();

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.folder.MenuDefinition#setMenuElements(java.util.List)
     */
    public void setMenuElements(List<MenuDefinitionElement> elements)
    {
        // set menu elements by replacing
        // existing entries with new elements if
        // new collection is specified
        List<MenuDefinitionElement> menuElements = getMenuElements();
        if (elements != menuElements)
        {
            // replace all menu elements
            menuElements.clear();
            if (elements != null)
            {
                menuElements.addAll(elements);
            }
        }
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object o)
    {
        if (o instanceof BaseMenuDefinitionImpl)
        {
            if (name != null)
            {
                return name.equals(((BaseMenuDefinitionImpl)o).getName());
            }
            else
            {
                return (((BaseMenuDefinitionImpl)o).getName() == null);
            }
        }
        return false;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    public int hashCode()
    {
        if (name != null)
        {
            return name.hashCode();
        }
        return 0;
    }
}
