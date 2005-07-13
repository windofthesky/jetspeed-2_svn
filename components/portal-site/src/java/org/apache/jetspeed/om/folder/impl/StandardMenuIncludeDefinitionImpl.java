/*
 * Copyright 2005 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
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

import org.apache.jetspeed.om.folder.MenuIncludeDefinition;

/**
 * This interface describes the object used to define
 * portal site menu included menus.
 * 
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id$
 */
public abstract class StandardMenuIncludeDefinitionImpl implements MenuIncludeDefinition
{
    /**
     * StandardMenuIncludeDefinitionImpl - constructor
     */
    public StandardMenuIncludeDefinitionImpl()
    {
    }

    /**
     * getName - get menu name to nest or with options to include
     *
     * @return menu name
     */
    public String getName()
    {
        return null;
    }

    /**
     * setName - set menu name to nest or with options to include
     *
     * @param name menu name
     */
    public void setName(String name)
    {
        throw new RuntimeException("StandardMenuIncludeDefinitionImpl instance immutable");
    }

    /**
     * isNest - get nesting for included menu
     *
     * @return nest options flag
     */
    public boolean isNest()
    {
        return false;
    }
    
    /**
     * setNest - set nesting for included menu
     *
     * @param nest nest menu flag
     */
    public void setNest(boolean nest)
    {
        throw new RuntimeException("StandardMenuIncludeDefinitionImpl instance immutable");
    }
}
