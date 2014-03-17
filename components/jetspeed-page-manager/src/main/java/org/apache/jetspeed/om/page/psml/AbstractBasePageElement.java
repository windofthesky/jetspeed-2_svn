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

package org.apache.jetspeed.om.page.psml;

import org.apache.jetspeed.idgenerator.IdGenerator;
import org.apache.jetspeed.om.folder.MenuDefinition;
import org.apache.jetspeed.om.folder.MenuExcludeDefinition;
import org.apache.jetspeed.om.folder.MenuIncludeDefinition;
import org.apache.jetspeed.om.folder.MenuOptionsDefinition;
import org.apache.jetspeed.om.folder.MenuSeparatorDefinition;
import org.apache.jetspeed.om.folder.psml.MenuDefinitionImpl;
import org.apache.jetspeed.om.folder.psml.MenuExcludeDefinitionImpl;
import org.apache.jetspeed.om.folder.psml.MenuIncludeDefinitionImpl;
import org.apache.jetspeed.om.folder.psml.MenuOptionsDefinitionImpl;
import org.apache.jetspeed.om.folder.psml.MenuSeparatorDefinitionImpl;
import org.apache.jetspeed.om.page.BasePageElement;

import java.util.List;

/**
 * AbstractBasePageElement
 *
 * @version $Id:$
 */
public abstract class AbstractBasePageElement extends AbstractBaseFragmentsElement implements BasePageElement
{
    private static final long serialVersionUID = 1L;

    private DefaultsImpl defaults = new DefaultsImpl();

    /**
     * menuDefinitions - menu definitions for page
     */
    private List<MenuDefinition> menuDefinitions;
    
    public String getSkin()
    {
        return defaults.getSkin();
    }

    public void setSkin( String skinName )
    {
        defaults.setSkin(skinName);
    }

    public String getDefaultDecorator( String fragmentType )
    {
        return defaults.getDecorator(fragmentType);
    }

    public void setDefaultDecorator( String decoratorName, String fragmentType )
    {
        defaults.setDecorator(fragmentType, decoratorName);
    }

    public DefaultsImpl getDefaults()
    {
        return this.defaults;
    }

    public void setDefaults( DefaultsImpl defaults )
    {
        this.defaults = defaults;
    }

    /**
     * getMenuDefinitions - get list of menu definitions
     *
     * @return definition list
     */
    public List<MenuDefinition> getMenuDefinitions()
    {
        return menuDefinitions;
    }

    /**
     * newMenuDefinition - creates a new empty menu definition
     *
     * @return a newly created MenuDefinition object for use in Page
     */
    public MenuDefinition newMenuDefinition()
    {
        return new MenuDefinitionImpl();
    }

    /**
     * newMenuExcludeDefinition - creates a new empty menu exclude definition
     *
     * @return a newly created MenuExcludeDefinition object for use in Page
     */
    public MenuExcludeDefinition newMenuExcludeDefinition()
    {
        return new MenuExcludeDefinitionImpl();
    }

    /**
     * newMenuIncludeDefinition - creates a new empty menu include definition
     *
     * @return a newly created MenuIncludeDefinition object for use in Page
     */
    public MenuIncludeDefinition newMenuIncludeDefinition()
    {
        return new MenuIncludeDefinitionImpl();
    }

    /**
     * newMenuOptionsDefinition - creates a new empty menu options definition
     *
     * @return a newly created MenuOptionsDefinition object for use in Page
     */
    public MenuOptionsDefinition newMenuOptionsDefinition()
    {
        return new MenuOptionsDefinitionImpl();
    }

    /**
     * newMenuSeparatorDefinition - creates a new empty menu separator definition
     *
     * @return a newly created MenuSeparatorDefinition object for use in Page
     */
    public MenuSeparatorDefinition newMenuSeparatorDefinition()
    {
        return new MenuSeparatorDefinitionImpl();
    }

    /**
     * setMenuDefinitions - set list of menu definitions
     *
     * @param definitions definition list
     */
    public void setMenuDefinitions(List<MenuDefinition> definitions)
    {
        menuDefinitions = definitions;
    }

    /**
     * unmarshalled - notification that this instance has been
     *                loaded from the persistent store
     * @param generator id generator
     * @return dirty flag
     */
    public boolean unmarshalled(IdGenerator generator)
    {
        // notify super class implementation
        boolean dirty = super.unmarshalled(generator);

        // propagate unmarshalled notification
        // to all menu definitions
        if (menuDefinitions != null)
        {
            for (MenuDefinition menuDefinition : menuDefinitions)
            {
                ((MenuDefinitionImpl)menuDefinition).unmarshalled();
            }
        }
        
        return dirty;
    }

    /**
     * marshalling - notification that this instance is to
     *               be saved to the persistent store
     */
    public void marshalling()
    {
        // propagate marshalling notification
        // to all menu definitions
        if (menuDefinitions != null)
        {
            for (MenuDefinition menuDefinition : menuDefinitions)
            {
                ((MenuDefinitionImpl)menuDefinition).marshalling();
            }
        }

        // notify super class implementation
        super.marshalling();
    }
}
