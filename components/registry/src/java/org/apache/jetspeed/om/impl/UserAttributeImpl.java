/* Copyright 2004 Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.jetspeed.om.impl;

import org.apache.jetspeed.om.common.UserAttribute;

/**
 * <p>User attribute implementation.</p>
 * 
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat</a>
 */
public class UserAttributeImpl implements UserAttribute
{

    /**
     * <p>Default constructor.</p>
     */
    public UserAttributeImpl()
    {
    }

    /**
     * <p>User attribute constructor given a name and description.</p>
     * @param The user attribute name.
     * @param The user attribute description.
     */
     public UserAttributeImpl(String name, String description)
     {
         this.name = name;
         this.description = description;
     }

    private String name;

    /**
     * @see org.apache.jetspeed.om.common.UserAttribute#getName()
     */
    public String getName()
    {
        return name;
    }

    /**
     * @see org.apache.jetspeed.om.common.UserAttribute#setName(java.lang.String)
     */
    public void setName(String name)
    {
        this.name = name;
    }

    private String description;

    /**
     * @see org.apache.jetspeed.om.common.UserAttribute#getDescription()
     */
    public String getDescription()
    {
        return description;
    }

    /**
     * @see org.apache.jetspeed.om.common.UserAttribute#setDescription(java.lang.String)
     */
    public void setDescription(String description)
    {
        this.description = description;
    }

    /**
     * <p>Convert {@link UserAttribute} to String.</p>
     * @return String value of UserAttribute.
     */
    public String toString()
    {
        String userAttribute = "[[name, " + this.name + "], [description, " + this.description + "]]";
        return userAttribute;
    }

}
