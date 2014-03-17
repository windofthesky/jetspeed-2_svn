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
package org.apache.jetspeed.om.folder.psml;

import org.apache.commons.lang.StringUtils;
import org.apache.jetspeed.om.folder.MenuDefinition;
import org.apache.jetspeed.om.folder.MenuDefinitionElement;

import java.util.ArrayList;
import java.util.List;

/**
 * This class implements the MenuDefinition
 * interface in a persistent object form for use by
 * the page manager component.
 * 
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id$
 */
public class MenuDefinitionImpl extends MenuMetadataImpl implements MenuDefinition
{
    /**
     * name - name of menu definition
     */
    private String name;

    /**
     * options - comma separated option paths specification for menu
     */
    private String options;

    /**
     * depth - depth of inclusion for folder options
     */
    private int depth;

    /**
     * paths - generate ordered path options for options
     */
    private boolean paths;

    /**
     * regexp - interpret specified optionsas regexp
     */
    private boolean regexp;

    /**
     * profile - profile locator name filter for options
     */
    private String profile;
    
    /**
     * order - comma separated list of ordering patterns for options
     */
    private String order;
    
    /**
     * skin - skin name for menu
     */
    private String skin;
    
    /**
     * title - title for menu
     */
    private String title;

    /**
     * shortTitle - short title for menu
     */
    private String shortTitle;

    /**
     * menuElements - ordered polymorphic list of menu options nested
     *                menu, separator, include, and exclude definitions
     */
    private List<MenuDefinitionElement> menuElements;

    /**
     * menuElementImpls - ordered homogeneous list of menu elements
     */
    private List<MenuElementImpl> menuElementImpls;

    /**
     * MenuDefinitionImpl - constructor
     */
    public MenuDefinitionImpl()
    {
    }

    /**
     * getName - get menu name
     *
     * @return menu name
     */
    public String getName()
    {
        return name;
    }

    /**
     * setName - set menu name
     *
     * @param name menu name
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * getOptions - get comma separated menu options if not specified as elements
     *
     * @return option paths specification
     */
    public String getOptions()
    {
        return options;
    }

    /**
     * setOptions - set comma separated menu options if not specified as elements
     *
     * @param options option paths specification
     */
    public void setOptions(String options)
    {
        this.options = options;
    }

    /**
     * getDepth - get depth of inclusion for folder menu options
     *
     * @return inclusion depth
     */
    public int getDepth()
    {
        return depth;
    }

    /**
     * setDepth - set depth of inclusion for folder menu options
     *
     * @param depth inclusion depth
     */
    public void setDepth(int depth)
    {
        this.depth = depth;
    }

    /**
     * isPaths - get generate ordered path options for specified options
     *
     * @return paths options flag
     */
    public boolean isPaths()
    {
        return paths;
    }
    
    /**
     * setPaths - set generate ordered path options for specified options
     *
     * @param paths paths options flag
     */
    public void setPaths(boolean paths)
    {
        this.paths = paths;
    }
    
    /**
     * isRegexp - get regexp flag for interpreting specified options
     *
     * @return regexp flag
     */
    public boolean isRegexp()
    {
        return regexp;
    }

    /**
     * setRegexp - set regexp flag for interpreting specified options
     *
     * @param regexp regexp flag
     */
    public void setRegexp(boolean regexp)
    {
        this.regexp = regexp;
    }

    /**
     * getProfile - get profile locator used to filter specified options
     *
     * @return profile locator name
     */
    public String getProfile()
    {
        return profile;
    }

    /**
     * setProfile - set profile locator used to filter specified options
     *
     * @param locatorName profile locator name
     */
    public void setProfile(String locatorName)
    {
        profile = locatorName;
    }

    /**
     * getOrder - get comma separated regexp ordering patterns for options
     *
     * @return ordering patterns list
     */
    public String getOrder()
    {
        return order;
    }

    /**
     * setOrder - set comma separated regexp ordering patterns for options
     *
     * @param order ordering patterns list
     */
    public void setOrder(String order)
    {
        this.order = order;
    }

    /**
     * getSkin - get skin name for menu
     *
     * @return skin name
     */
    public String getSkin()
    {
        return skin;
    }

    /**
     * setSkin - set skin name for menu
     *
     * @param name skin name
     */
    public void setSkin(String name)
    {
        skin = name;
    }

    /**
     * getTitle - get default title for menu
     *
     * @return title text
     */
    public String getTitle()
    {
        return title;
    }

    /**
     * setTitle - set default title for menu
     *
     * @param title title text
     */
    public void setTitle(String title)
    {
        this.title = title;
    }

    /**
     * getShortTitle - get default short title for menu
     *
     * @return short title text
     */
    public String getShortTitle()
    {
        return shortTitle; 
    }

    /**
     * setShortTitle - set default short title for menu
     *
     * @param title short title text
     */
    public void setShortTitle(String title)
    {
        this.shortTitle = title;
    }

    /**
     * getMenuElements - get ordered list of menu options,
     *                   nested menus, separators, included
     *                   menu, and excluded menu elements
     *
     * @return element list
     */
    public List<MenuDefinitionElement> getMenuElements()
    {
        return menuElements;
    }

    /**
     * setMenuElements - set ordered list of menu elements
     *
     * @param elements element list
     */
    public void setMenuElements(List<MenuDefinitionElement> elements)
    {
        menuElements = elements;
    }

    /**
     * getMenuElementImpls - get ordered list of wrapped menu elements
     *
     * @return element list
     */
    public List<MenuElementImpl> getMenuElementImpls()
    {
        return menuElementImpls;
    }

    /**
     * setMenuElementImpls - set ordered list of menu elements using
     *                       a list of wrapped menu elements
     *
     * @param elements element list
     */
    public void setMenuElementImpls(List<MenuElementImpl> elements)
    {
        menuElementImpls = elements;
    }

    /**
     * unmarshalled - notification that this instance has been
     *                loaded from the persistent store
     */
    public void unmarshalled()
    {
        // notify super class implementation
        super.unmarshalled();

        // unwrap menu elements and propagate
        // unmarshalled notification
        if (menuElementImpls != null)
        {
            menuElements = new ArrayList<MenuDefinitionElement>(menuElementImpls.size());
            for (MenuElementImpl menuElementImpl : menuElementImpls)
            {
                // unwrap menu element
                MenuDefinitionElement menuElement = menuElementImpl.getElement();
                menuElements.add(menuElement);

                // propagate unmarshalled notification
                if (menuElement instanceof MenuMetadataImpl)
                {
                    ((MenuMetadataImpl)menuElement).unmarshalled();
                }
            }
        }
        else
        {
            menuElements = null;            
        }
    }

    /**
     * marshalling - notification that this instance is to
     *               be saved to the persistent store
     */
    public void marshalling()
    {
        // wrap menu elements and propagate
        // marshalling notification
        if (menuElements != null)
        {
            menuElementImpls = new ArrayList<MenuElementImpl>(menuElements.size());
            for (MenuDefinitionElement menuDefinitionElement : menuElements)
            {
                // wrap menu element
                menuElementImpls.add(new MenuElementImpl(menuDefinitionElement));

                // propagate marshalling notification
                if (menuDefinitionElement instanceof MenuDefinitionImpl)
                {
                    ((MenuDefinitionImpl)menuDefinitionElement).unmarshalled();
                }
            }
        }
        else
        {
            menuElementImpls = null;            
        }
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj)
    {
        if (!(obj instanceof MenuDefinition))
        {
            return false;
        }
        else
        {
            MenuDefinition definition = (MenuDefinition) obj;
            if (!StringUtils.equals(definition.getName(),name) || !StringUtils.equals(definition.getOptions(),options) || definition.getDepth() != depth ||
                definition.isPaths() != paths || definition.isRegexp() != regexp || !StringUtils.equals(definition.getProfile(),profile) ||
                !StringUtils.equals(definition.getOrder(),order) || !StringUtils.equals(definition.getSkin(),skin) || !StringUtils.equals(definition.getTitle(),title))
            {
                return false;
            }
            if (definition.getMenuElements() != null && menuElements != null)
            {
                if (definition.getMenuElements().size() != menuElements.size())
                {
                    return false;
                }
            }
            return true;
        }
    }
}
