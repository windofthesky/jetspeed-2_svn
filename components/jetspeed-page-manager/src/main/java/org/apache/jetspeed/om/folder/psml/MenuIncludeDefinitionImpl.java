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
import org.apache.jetspeed.om.folder.MenuIncludeDefinition;

/**
 * This class implements the MenuIncludeDefinition
 * interface in a persistent object form for use by
 * the page manager component.
 * 
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id$
 */
public class MenuIncludeDefinitionImpl implements MenuIncludeDefinition
{
    /**
     * name - name of menu to include
     */
    private String name;

    /**
     * nest - nesting flag for included menu
     */
    private boolean nest;

    /**
     * MenuIncludeDefinitionImpl - constructor
     */
    public MenuIncludeDefinitionImpl()
    {
    }

    /**
     * getName - get menu name to nest or with options to include
     *
     * @return menu name
     */
    public String getName()
    {
        return name;
    }

    /**
     * setName - set menu name to nest or with options to include
     *
     * @param name menu name
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * isNest - get nesting for included menu
     *
     * @return nest options flag
     */
    public boolean isNest()
    {
        return nest;
    }
    
    /**
     * setNest - set nesting for included menu
     *
     * @param nest nest menu flag
     */
    public void setNest(boolean nest)
    {
        this.nest = nest;
    }
    
    public boolean equals(Object obj)
    {
        if (!(obj instanceof MenuIncludeDefinition))
        {
            return false;
        }
        else
        {
            MenuIncludeDefinition definition = (MenuIncludeDefinition) obj;
            if (!StringUtils.equals(definition.getName(), name)|| definition.isNest()!=nest)
            {
                return false;
            }
            return true;
        }
    }
}
