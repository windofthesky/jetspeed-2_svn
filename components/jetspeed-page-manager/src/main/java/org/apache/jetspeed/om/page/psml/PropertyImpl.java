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

package org.apache.jetspeed.om.page.psml;

import org.apache.jetspeed.om.page.BaseFragmentPropertyImpl;
import org.apache.jetspeed.om.page.FragmentProperty;

/**
 * Bean like implementation of the FragmentProperty interface suitable for
 * Castor serialization.
 *
 * @see org.apache.jetspeed.om.registry.PsmlParameter
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class PropertyImpl extends BaseFragmentPropertyImpl implements FragmentProperty, java.io.Serializable
{
    private String name;
    private String scope;
    private String scopeValue;
    private String value;

    public PropertyImpl()
    {
    }

    public String getLayout()
    {
        // property layout name deprecated
        return null;
    }

    public void setLayout(String layout)
    {
        // property layout name deprecated
    }

    public String getName()
    {
        return this.name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getScope()
    {
        return this.scope;
    }

    public void setScope(String scope)
    {
        if ((scope != null) && !scope.equals(USER_PROPERTY_SCOPE) && 
            (!GROUP_AND_ROLE_PROPERTY_SCOPES_ENABLED || (!scope.equals(GROUP_PROPERTY_SCOPE) && !scope.equals(ROLE_PROPERTY_SCOPE))))
            {
                throw new IllegalArgumentException("Fragment property scope "+scope+" invalid or not enabled");
            }
        this.scope = scope;
    }

    public String getScopeValue()
    {
        return this.scopeValue;
    }

    public void setScopeValue(String scopeValue)
    {
        this.scopeValue = scopeValue;
    }

    public String getValue()
    {
        return this.value;
    }

    public void setValue(String value)
    {
        this.value = value;
    }

    /**
     * <p>
     * getIntValue
     * </p>
     *
     * @see org.apache.jetspeed.om.page.Property#getIntValue()
     * @return
     */
    public int getIntValue()
    {        
        return Integer.parseInt(value);
    }

    /**
     * Unchecked read access to scope value.
     * 
     * @return scope
     */
    public String getUncheckedScope()
    {
        return this.scope;
    }

    /**
     * Unchecked write access to scope value.
     * 
     * @param scope
     */
    public void setUncheckedScope(String scope)
    {
        this.scope = scope;
    }
}
