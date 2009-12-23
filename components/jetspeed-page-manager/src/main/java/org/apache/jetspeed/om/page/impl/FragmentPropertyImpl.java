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
package org.apache.jetspeed.om.page.impl;

import org.apache.jetspeed.om.page.BaseFragmentElement;
import org.apache.jetspeed.om.page.BaseFragmentPropertyImpl;
import org.apache.jetspeed.om.page.FragmentProperty;

/**
 * FragmentPropertyImpl
 *
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id$
 */
public class FragmentPropertyImpl extends BaseFragmentPropertyImpl implements FragmentProperty
{
    private int id;
    private BaseFragmentElement fragment;
    private String name;
    private String scope;
    private String scopeValue;
    private String value;

    /**
     * Get implementation identity key.
     * 
     * @return identity key.
     */
    public int getIdentity()
    {
        return id;
    }

    /**
     * Get owning fragment instance.
     * 
     * @return owning fragment
     */
    public BaseFragmentElement getFragment()
    {
        return fragment;
    }
    
    /**
     * Set owning fragment instance.
     * 
     * @param fragment owning fragment
     */
    public void setFragment(BaseFragmentElement fragment)
    {
        this.fragment = fragment;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.FragmentProperty#getName()
     */
    public String getName()
    {
        return name;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.FragmentProperty#getScope()
     */
    public String getScope()
    {
        return scope;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.FragmentProperty#getScopeValue()
     */
    public String getScopeValue()
    {
        return scopeValue;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.FragmentProperty#getValue()
     */
    public String getValue()
    {
        return value;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.FragmentProperty#setName(java.lang.String)
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.FragmentProperty#setScope(java.lang.String)
     */
    public void setScope(String scope)
    {
        if ((scope != null) && !scope.equals(USER_PROPERTY_SCOPE) && 
            (!GROUP_AND_ROLE_PROPERTY_SCOPES_ENABLED || (!scope.equals(GROUP_PROPERTY_SCOPE) && !scope.equals(ROLE_PROPERTY_SCOPE))))
        {
            throw new IllegalArgumentException("Fragment property scope "+scope+" invalid or not enabled");
        }
        this.scope = scope;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.FragmentProperty#setScopeValue(java.lang.String)
     */
    public void setScopeValue(String scopeValue)
    {
        this.scopeValue = scopeValue;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.FragmentProperty#setValue(java.lang.String)
     */
    public void setValue(String value)
    {
        this.value = value;
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object o)
    {
        if (o instanceof FragmentPropertyImpl)
        {
            FragmentPropertyImpl other = (FragmentPropertyImpl)o;
            if (this == other)
            {
                return true;
            }
            if ((id != 0) || (other.id != 0))
            {
                return (id == other.id);
            }
            return ((((name != null) && name.equals(other.name)) || ((name == null) && (other.name == null))) &&
                    (((scope != null) && scope.equals(other.scope)) || ((scope == null) && (other.scope == null))) &&
                    (((scopeValue != null) && scopeValue.equals(other.scopeValue)) || ((scopeValue == null) && (other.scopeValue == null))) &&
                    (((value != null) && value.equals(other.value)) || ((value == null) && (other.value == null))));
        }
        return false;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    public int hashCode()
    {
        if (id != 0)
        {
            return id;
        }
        int hashcode = 0;
        hashcode *= ((name != null) ? name.hashCode() : 0);
        hashcode *= ((scope != null) ? scope.hashCode() : 0);
        hashcode *= ((scopeValue != null) ? scopeValue.hashCode() : 0);
        hashcode *= ((value != null) ? value.hashCode() : 0);
        return hashcode;
    }
    
    /**
     * Test whether property object matches.
     * 
     * @param other match candidate
     * @return match flag
     */
    protected boolean match(FragmentProperty other)
    {
        return ((((name != null) && name.equals(other.getName())) || ((name == null) && (other.getName() == null))) &&
                (((scope != null) && scope.equals(other.getScope())) || ((scope == null) && (other.getScope() == null))) &&
                (((scopeValue != null) && scopeValue.equals(other.getScopeValue())) || ((scopeValue == null) && (other.getScopeValue() == null))));
    }
}
