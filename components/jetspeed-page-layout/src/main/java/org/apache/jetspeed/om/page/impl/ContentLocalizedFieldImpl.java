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

import java.util.Locale;

import org.apache.jetspeed.om.portlet.LocalizedField;

/**
 * Immutable content metadata localized field implementation.
 * 
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id:$
 */
public class ContentLocalizedFieldImpl implements LocalizedField
{
    private Locale locale;
    private String name;
    private String value;
    
    /**
     * Construct content metadata localized field.
     * 
     * @param locale field locale
     * @param name field name
     * @param value field value
     */
    public ContentLocalizedFieldImpl(Locale locale, String name, String value)
    {
        this.locale = locale;
        this.name = name;
        this.value = value;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.portlet.LocalizedField#getLocale()
     */
    public Locale getLocale()
    {
        return locale;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.portlet.LocalizedField#getName()
     */
    public String getName()
    {
        return name;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.portlet.LocalizedField#getValue()
     */
    public String getValue()
    {
        return value;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.portlet.LocalizedField#setLocale(java.util.Locale)
     */
    public void setLocale(Locale locale)
    {
        throw new UnsupportedOperationException("LocalizedField.setLocale()");        
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.portlet.LocalizedField#setName(java.lang.String)
     */
    public void setName(String name)
    {
        throw new UnsupportedOperationException("LocalizedField.setName()");        
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.portlet.LocalizedField#setValue(java.lang.String)
     */
    public void setValue(String value)
    {
        throw new UnsupportedOperationException("LocalizedField.setValue()");        
    }
}
