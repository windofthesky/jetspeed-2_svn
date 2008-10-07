/*
 * Copyright 2000-2004 The Apache Software Foundation.
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
package org.apache.jetspeed.om.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.om.common.MutableDescription;
import org.apache.jetspeed.om.common.SecurityRoleRefComposite;
import org.apache.jetspeed.util.HashCodeBuilder;
import org.apache.jetspeed.util.JetspeedLocale;
import org.apache.pluto.om.common.Description;
import org.apache.pluto.om.common.DescriptionSet;
import org.apache.pluto.om.common.SecurityRoleRef;

/**
 * 
 * SecurityRoleRefImpl
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 *
 */
public class SecurityRoleRefImpl implements SecurityRoleRefComposite, Serializable
{

    protected long id;
    protected long portletId;
    private String link;
    private String name;
    private Collection descriptions;
    private DescriptionSetImpl descCollWrapper = new DescriptionSetImpl(DescriptionImpl.TYPE_SEC_ROLE_REF);

    private static final Log log = LogFactory.getLog(SecurityRoleRefImpl.class);

    /**
     * @see org.apache.pluto.om.common.SecurityRoleRef#getRoleLink()
     */
    public String getRoleLink()
    {
        return link;
    }

    /**
     * @see org.apache.pluto.om.common.SecurityRoleRef#getRoleName()
     */
    public String getRoleName()
    {
        return name;
    }

    /**
     * @see org.apache.pluto.om.common.SecurityRoleRefCtrl#setRoleLink(java.lang.String)
     */
    public void setRoleLink(String value)
    {
        this.link = value;
    }

    /**
     * @see org.apache.pluto.om.common.SecurityRoleRefCtrl#setRoleName(java.lang.String)
     */
    public void setRoleName(String name)
    {
        this.name = name;
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj)
    {
        if (obj != null && obj instanceof SecurityRoleRef)
        {
            SecurityRoleRef aRef = (SecurityRoleRef) obj;
            return this.getRoleName().equals(aRef.getRoleName());
        }

        return false;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    public int hashCode()
    {

        HashCodeBuilder hasher = new HashCodeBuilder(21, 81);
        hasher.append(name);
        return hasher.toHashCode();
    }

    /**
     * @see org.apache.pluto.om.common.SecurityRoleRef#getDescription(java.util.Locale)
     */
    public Description getDescription(Locale arg0)
    {
        if (descriptions != null)
        {
            descCollWrapper.setInnerCollection(descriptions);
            return descCollWrapper.get(arg0);
        }
        return null;
    }

    /**
     * @see org.apache.jetspeed.om.common.SecurityRoleRefComposite#addDescription(org.apache.pluto.om.common.Description)
     */
    public void addDescription(Description description)
    {
        if (descriptions == null)
        {
            descriptions = new ArrayList();
        }
        descCollWrapper.setInnerCollection(descriptions);
        descCollWrapper.addDescription(description);
    }

    /**
     * @see org.apache.jetspeed.om.common.SecurityRoleRefComposite#setDescriptionSet(org.apache.pluto.om.common.DescriptionSet)
     */
    public void setDescriptionSet(DescriptionSet descriptions)
    {
        this.descriptions = ((DescriptionSetImpl) descriptions).getInnerCollection();

    }

    /**
     * We should be using one of the more locale-specific methods.
     * 
     * 
     * @see org.apache.pluto.om.common.SecurityRoleRefCtrl#setDescription(java.lang.String)
     * @param arg0
     */
    public void setDescription(String arg0)
    {
        MutableDescription descObj =new SecurityRoleRefDescriptionImpl();
            
        
        descObj.setLocale(JetspeedLocale.getDefaultLocale());
        descObj.setDescription(arg0);

    }

}
