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
package org.apache.jetspeed.om.page.impl;

import org.apache.jetspeed.om.folder.MenuDefinition;
import org.apache.jetspeed.om.folder.MenuExcludeDefinition;
import org.apache.jetspeed.om.folder.MenuIncludeDefinition;
import org.apache.jetspeed.om.folder.MenuOptionsDefinition;
import org.apache.jetspeed.om.folder.MenuSeparatorDefinition;
import org.apache.jetspeed.om.page.BasePageElement;
import org.apache.jetspeed.om.page.Fragment;
import org.apache.jetspeed.page.impl.DatabasePageManagerUtils;

import java.util.List;

/**
 * BasePageElementImpl
 *
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id$
 */
public abstract class BasePageElementImpl extends BaseFragmentsElementImpl implements BasePageElement
{
    private String skin;
    private String defaultLayoutDecorator;
    private String defaultPortletDecorator;
    private List<PageMenuDefinitionImpl> menus;

    private PageMenuDefinitionList menuDefinitions;

    /**
     * accessMenus
     *
     * Access mutable persistent collection member for List wrappers.
     *
     * @return persistent collection
     */
    List<PageMenuDefinitionImpl> accessMenus()
    {
        // create initial collection if necessary
        if (menus == null)
        {
            menus = DatabasePageManagerUtils.createList();;
        }
        return menus;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.impl.BaseElementImpl#resetCachedSecurityConstraints()
     */
    public void resetCachedSecurityConstraints()
    {
        // propagate to super and fragments
        super.resetCachedSecurityConstraints();
        BaseFragmentElementImpl rootFragment = (BaseFragmentElementImpl)getRootFragment();
        if (rootFragment != null)
        {
            rootFragment.resetCachedSecurityConstraints();
        }
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Page#getSkin()
     */
    public String getSkin()
    {
        return skin;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Page#setSkin(java.lang.String)
     */
    public void setSkin(String skinName)
    {
        this.skin = skinName;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Page#getDefaultDecorator(java.lang.String)
     */
    public String getDefaultDecorator(String fragmentType)
    {
        // retrieve supported decorator types
        if (fragmentType != null)
        {
            if (fragmentType.equals(Fragment.LAYOUT))
            {
                return defaultLayoutDecorator; 
            }
            if (fragmentType.equals(Fragment.PORTLET))
            {
                return defaultPortletDecorator; 
            }
        }
        return null;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Page#getDefaultDecorator(java.lang.String,java.lang.String)
     */
    public void setDefaultDecorator(String decoratorName, String fragmentType)
    {
        // save supported decorator types
        if (fragmentType != null)
        {
            if (fragmentType.equals(Fragment.LAYOUT))
            {
                defaultLayoutDecorator = decoratorName; 
            }
            if (fragmentType.equals(Fragment.PORTLET))
            {
                defaultPortletDecorator = decoratorName; 
            }
        }
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Page#getMenuDefinitions()
     */
    public List<MenuDefinition> getMenuDefinitions()
    {
        // return mutable menu definition list
        // by using list wrapper to manage
        // element uniqueness
        if (menuDefinitions == null)
        {
            menuDefinitions = new PageMenuDefinitionList(this);
        }
        return menuDefinitions;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Page#newMenuDefinition()
     */
    public MenuDefinition newMenuDefinition()
    {
        return new PageMenuDefinitionImpl();
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Page#newMenuExcludeDefinition()
     */
    public MenuExcludeDefinition newMenuExcludeDefinition()
    {
        return new PageMenuExcludeDefinitionImpl();
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Page#newMenuIncludeDefinition()
     */
    public MenuIncludeDefinition newMenuIncludeDefinition()
    {
        return new PageMenuIncludeDefinitionImpl();
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Page#newMenuOptionsDefinition()
     */
    public MenuOptionsDefinition newMenuOptionsDefinition()
    {
        return new PageMenuOptionsDefinitionImpl();
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Page#newMenuSeparatorDefinition()
     */
    public MenuSeparatorDefinition newMenuSeparatorDefinition()
    {
        return new PageMenuSeparatorDefinitionImpl();
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Page#setMenuDefinitions(java.util.List)
     */
    public void setMenuDefinitions(List<MenuDefinition> definitions)
    {
        // set menu definitions by replacing
        // existing entries with new elements if
        // new collection is specified
        List<MenuDefinition> menuDefinitions = getMenuDefinitions();
        if (definitions != menuDefinitions)
        {
            // replace all menu definitions
            menuDefinitions.clear();
            if (definitions != null)
            {
                menuDefinitions.addAll(definitions);
            }
        }
    }
}
