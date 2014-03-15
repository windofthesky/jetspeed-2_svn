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

import org.apache.jetspeed.om.folder.MenuDefinitionElement;

import java.io.Serializable;

/**
 * This class implements a wrapper used to implement
 * the ordered polymorphic menu elements collection.
 * 
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id$
 */
public class MenuElementImpl implements Serializable
{
    /**
     * element - wrapped menu element
     */
    private MenuDefinitionElement element;

    /**
     * MenuElementImpl - constructor
     */
    public MenuElementImpl()
    {
    }

    /**
     * MenuElementImpl - constructor
     */
    public MenuElementImpl(MenuDefinitionElement element)
    {
        this.element = element;
    }

    /**
     * getElement - get wrapped menu element
     */
    public MenuDefinitionElement getElement()
    {
        return element;
    }

    /**
     * getOption - get wrapped menu options definition
     */
    public MenuOptionsDefinitionImpl getOptions()
    {
        if (element instanceof MenuOptionsDefinitionImpl)
        {
            return (MenuOptionsDefinitionImpl)element;
        }
        return null;
    }

    /**
     * setOption - set wrapped menu options definition
     *
     * @param options menu options definition
     */
    public void setOptions(MenuOptionsDefinitionImpl options)
    {
        this.element = options;
    }

    /**
     * getMenu - get wrapped menu menu definition
     */
    public MenuDefinitionImpl getMenu()
    {
        if (element instanceof MenuDefinitionImpl)
        {
            return (MenuDefinitionImpl)element;
        }
        return null;
    }

    /**
     * setMenu - set wrapped menu menu definition
     *
     * @param menu menu definition
     */
    public void setMenu(MenuDefinitionImpl menu)
    {
        this.element = menu;
    }

    /**
     * getSeparator - get wrapped menu separator definition
     */
    public MenuSeparatorDefinitionImpl getSeparator()
    {
        if (element instanceof MenuSeparatorDefinitionImpl)
        {
            return (MenuSeparatorDefinitionImpl)element;
        }
        return null;
    }

    /**
     * setSeparator - set wrapped menu separator definition
     *
     * @param separator menu separator definition
     */
    public void setSeparator(MenuSeparatorDefinitionImpl separator)
    {
        this.element = separator;
    }

    /**
     * getInclude - get wrapped menu include definition
     */
    public MenuIncludeDefinitionImpl getInclude()
    {
        if (element instanceof MenuIncludeDefinitionImpl)
        {
            return (MenuIncludeDefinitionImpl)element;
        }
        return null;
    }

    /**
     * setInclude - set wrapped menu include definition
     *
     * @param include menu include definition
     */
    public void setInclude(MenuIncludeDefinitionImpl include)
    {
        this.element = include;
    }

    /**
     * getExclude - get wrapped menu exclude definition
     */
    public MenuExcludeDefinitionImpl getExclude()
    {
        if (element instanceof MenuExcludeDefinitionImpl)
        {
            return (MenuExcludeDefinitionImpl)element;
        }
        return null;
    }

    /**
     * setExclude - set wrapped menu exclude definition
     *
     * @param exclude menu exclude definition
     */
    public void setExclude(MenuExcludeDefinitionImpl exclude)
    {
        this.element = exclude;
    }
}
