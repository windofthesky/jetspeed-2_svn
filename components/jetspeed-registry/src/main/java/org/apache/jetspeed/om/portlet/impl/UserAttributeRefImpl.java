/* 
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.jetspeed.om.portlet.impl;

import java.util.List;
import java.util.Locale;

import org.apache.jetspeed.om.portlet.Description;
import org.apache.jetspeed.om.portlet.UserAttribute;
import org.apache.jetspeed.om.portlet.UserAttributeRef;
import org.apache.jetspeed.util.JetspeedLocale;
import org.apache.jetspeed.util.ojb.CollectionUtils;

/**
 * <p>User attribute ref implementation.</p>
 * 
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat</a>
 */
public class UserAttributeRefImpl implements UserAttributeRef
{
    private long id;
    private String name;
    private String nameLink;
    private List<Description> descriptions;

    public UserAttributeRefImpl()
    {
    }

    /**
     * <p>User attribute ref constructor given a {@link UserAttribute}.</p>
     * @param The user attribute ref name.
     * @param The user attribute ref name link.
     */
    public UserAttributeRefImpl(UserAttribute userAttribute)
    {
        this.name = userAttribute.getName();
        for (Description d : userAttribute.getDescriptions())
        {
            addDescription(d.getLang()).setDescription(d.getDescription());
        }
    }
    
    public long getId()
    {
        return this.id;
    }
    
    public void setId(long id)
    {
        this.id = id;
    }
     
    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getNameLink()
    {
        return nameLink;
    }

    public void setNameLink(String nameLink)
    {
        this.nameLink = nameLink;
    }

    public Description getDescription(Locale locale)
    {
        return (Description)JetspeedLocale.getBestLocalizedObject(getDescriptions(), locale);
    }

    @SuppressWarnings("unchecked")
    public List<Description> getDescriptions()
    {
        if (descriptions == null)
        {
            descriptions = CollectionUtils.createList();
        }
        return descriptions;
    }
    
    public Description addDescription(String lang)
    {
        DescriptionImpl d = new DescriptionImpl(this, lang);
        for (Description desc : getDescriptions())
        {
            if (desc.getLocale().equals(d.getLocale()))
            {
                throw new IllegalArgumentException("Description for language: "+d.getLocale()+" already defined");
            }
        }
        descriptions.add(d);
        return d;
    }
}
