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
package org.apache.jetspeed.om.portlet;
import java.util.Locale;

import org.apache.jetspeed.om.portlet.LocalizedField;
import org.apache.jetspeed.util.HashCodeBuilder;

/**
 * LocalizedFieldImpl
 * <br/>
 * Implementation that represents a string value and the locale of that string
 * 
 * @author <a href="mailto:jford@apache.org">Jeremy Ford</a>
 * @version $Id$
 *
 */
public class LocalizedFieldImpl implements LocalizedField
{
    protected String value;
    protected String name;
    protected Locale locale;
    
    public LocalizedFieldImpl()
    {
        
    }
    
    public LocalizedFieldImpl(Locale locale, String value)
    {
        this.locale = locale;
        this.value = value;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.common.LocalizedField#getLocale()
     */
    public Locale getLocale()
    {
        return locale;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.common.LocalizedField#setLocale(java.util.Locale)
     */
    public void setLocale(Locale locale)
    {
        this.locale = locale;        
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.common.LocalizedField#getValue()
     */
    public String getValue()
    {
        return value;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.common.LocalizedField#setValue(java.lang.String)
     */
    public void setValue(String value)
    {
        this.value = value;        
    }

    public void setLanguage(String language)
    {
		if (language != null)
        {
            String[] localeArray = language.split("[-|_]");
            String country = "";
            String variant = "";
            for (int i = 0; i < localeArray.length; i++)
            {
                if (i == 0)
                {
                    language = localeArray[i];
                }
                else if (i == 1)
                {
                    country = localeArray[i];
                }
                else if (i == 2)
                {
                    variant = localeArray[i];
                }
            }

            this.locale = new Locale(language, country, variant);
        }
		else
		{
            this.locale = null;		    
		}
    }

    public String getLanguage()
    {
        if (this.locale != null)
        {
            return this.locale.toString();
        }
        return null;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.common.LocalizedField#getName()
     */
    public String getName()
    {
        return name;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.common.LocalizedField#setName(java.lang.String)
     */
    public void setName(String name)
    {
        this.name = name;        
    }
    
    public String toString()
    {
        return "Name: " + name + " Value: " + value + " Locale: " + locale;
    }
    
    public boolean equals(Object o)
    {
        boolean result = false;
        
        if(o instanceof LocalizedFieldImpl && o != null)
        {
            LocalizedFieldImpl localField = (LocalizedFieldImpl)o;
            
            result = (this.name == null) ? (localField.name == null) : (this.name.equals(localField.name));
            result = result && ((this.value == null) ? (localField.value == null) : (this.value.equals(localField.value)));
            result = result && ((this.locale == null) ? (localField.locale == null) : (this.locale.equals(localField.locale)));
        }
        
        return result;
    }
    
    /**
     * @see java.lang.Object#hashCode()
     */
    public int hashCode()
    {
        HashCodeBuilder hasher = new HashCodeBuilder(27, 101);
        hasher.append(name).append(value);
        if(locale != null)
        {    
            hasher.append(locale.getCountry()).append(locale.getLanguage()).append(locale.getVariant());
        }
        return hasher.toHashCode();
    }
}
