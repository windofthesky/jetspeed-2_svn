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
package org.apache.jetspeed.om.portlet.impl;

import java.io.Serializable;
import java.util.List;
import java.util.Locale;

import org.apache.jetspeed.om.portlet.Description;
import org.apache.jetspeed.om.portlet.SecurityRoleRef;
import org.apache.jetspeed.util.HashCodeBuilder;
import org.apache.jetspeed.util.JetspeedLocale;
import org.apache.jetspeed.util.ojb.CollectionUtils;

/**
 * 
 * SecurityRoleRefImpl
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 *
 */
public class SecurityRoleRefImpl implements SecurityRoleRef, Serializable
{
    private String link;
    private String name;
    protected List<Description> descriptions;

    public String getRoleLink()
    {
        return link;
    }

    public String getRoleName()
    {
        return name;
    }

    public void setRoleLink(String value)
    {
        this.link = value;
    }

    public void setRoleName(String name)
    {
        this.name = name;
    }

    public boolean equals(Object obj)
    {
        if (obj != null && obj instanceof SecurityRoleRef)
        {
            SecurityRoleRef aRef = (SecurityRoleRef) obj;
            //TODO: Because of a bug in OJB 1.0.rc4 fields seems not have been set
            //      before this object is put into a HashMap.
            //      Therefore, for the time being, check against null values is
            //      required.
            //      Once 1.0rc5 or higher can be used the following line should be
            //      used again.
            //return this.getRoleName().equals(aRef.getRoleName());
            return getRoleName() != null && getRoleName().equals(aRef.getRoleName());
        }

        return false;
    }
    public int hashCode()
    {

        HashCodeBuilder hasher = new HashCodeBuilder(21, 81);
        hasher.append(name);
        return hasher.toHashCode();
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
