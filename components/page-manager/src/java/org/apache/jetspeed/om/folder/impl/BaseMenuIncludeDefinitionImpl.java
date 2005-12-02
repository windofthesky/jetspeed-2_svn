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
 * BaseMenuIncludeDefinitionImpl
 * 
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id:$
 */
public abstract class BaseMenuIncludeDefinitionImpl extends BaseMenuDefinitionElement implements MenuIncludeDefinition
{
    private String name;
    private boolean nest;

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.folder.MenuIncludeDefinition#getName()
     */
    public String getName()
    {
        return name;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.folder.MenuIncludeDefinition#setName(java.lang.String)
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.folder.MenuIncludeDefinition#isNest()
     */
    public boolean isNest()
    {
        return nest;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.folder.MenuIncludeDefinition#setNest(boolean)
     */
    public void setNest(boolean nest)
    {
        this.nest = nest;
    }
}
