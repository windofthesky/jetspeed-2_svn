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
import org.apache.jetspeed.om.common.UserAttributeRef;

/**
 * <p>User attribute ref implementation.</p>
 * 
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat</a>
 */
public class UserAttributeRefImpl implements UserAttributeRef
{

    /** The application id. */
    protected long appId;
    protected long id;    
    
    /**
     * <p>Default constructor.</p>
     */
    public UserAttributeRefImpl()
    {
    }

    /**
     * <p>User attribute ref constructor given a name and name link.</p>
     * @param The user attribute ref name.
     * @param The user attribute ref name link.
     */
     public UserAttributeRefImpl(String name, String nameLink)
     {
         this.name = name;
         this.nameLink = nameLink;
     }

     /**
      * <p>User attribute ref constructor given a {@link UserAttribute}.</p>
      * @param The user attribute ref name.
      * @param The user attribute ref name link.
      */
      public UserAttributeRefImpl(UserAttribute userAttribute)
      {
          this.name = userAttribute.getName();
          this.description = userAttribute.getDescription();
      }

    private String name;

    /**
     * @see org.apache.jetspeed.om.common.UserAttributeRef#getName()
     */
    public String getName()
    {
        return name;
    }

    /**
     * @see org.apache.jetspeed.om.common.UserAttributeRef#setName(java.lang.String)
     */
    public void setName(String name)
    {
        this.name = name;
    }

    private String nameLink;

    /**
     * @see org.apache.jetspeed.om.common.UserAttributeRef#getNameLink()
     */
    public String getNameLink()
    {
        return nameLink;
    }

    /**
     * @see org.apache.jetspeed.om.common.UserAttributeRef#setNameLink(java.lang.String)
     */
    public void setNameLink(String nameLink)
    {
        this.nameLink = nameLink;
    }

    private String description;

    /**
     * @see org.apache.jetspeed.om.common.UserAttributeRef#getDescription()
     */
    public String getDescription()
    {
        return description;
    }

    /**
     * @see org.apache.jetspeed.om.common.UserAttributeRef#setDescription(java.lang.String)
     */
    public void setDescription(String description)
    {
        this.description = description;
    }

    /**
     * <p>Convert {@link UserAttributeRef} to String.</p>
     * @return String value of UserAttributeRef.
     */
    public String toString()
    {
        String userAttributeRef = "[[name, " + this.name + "], [name-link, " + this.nameLink + "]]";
        return userAttributeRef;
    }

    /**
     * @return Returns the id.
     */
    public long getId()
    {
        return id;
    }
}
