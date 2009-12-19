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
package org.apache.jetspeed.om.page.impl;

import org.apache.jetspeed.om.page.FragmentProperty;

/**
 * Immutable content fragment property element implementation.
 * 
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id:$
 */
public class ContentFragmentPropertyImpl implements FragmentProperty
{
    private String name;
    private String scope;
    private String scopeValue;
    private String value;

    /**
     * Construct content fragment property element.
     * 
     * @param name property name
     * @param scope property scope
     * @param scopeValue property scope value
     * @param value property value
     */
    public ContentFragmentPropertyImpl(String name, String scope, String scopeValue, String value)
    {
        this.name = name;
        this.scope = scope;
        this.scopeValue = scopeValue;
        this.value = value;
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
        throw new UnsupportedOperationException("FragmentProperty.setName()");
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.FragmentProperty#setScope(java.lang.String)
     */
    public void setScope(String scope)
    {
        throw new UnsupportedOperationException("FragmentProperty.setScope()");
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.FragmentProperty#setScopeValue(java.lang.String)
     */
    public void setScopeValue(String scopeValue)
    {
        throw new UnsupportedOperationException("FragmentProperty.setScopeValue()");
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.FragmentProperty#setValue(java.lang.String)
     */
    public void setValue(String value)
    {
        throw new UnsupportedOperationException("FragmentProperty.setValue()");
    }
}
