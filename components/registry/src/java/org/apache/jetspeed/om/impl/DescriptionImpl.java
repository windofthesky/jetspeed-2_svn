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

import java.util.Locale;

import org.apache.jetspeed.util.JetspeedLocale;
import org.apache.jetspeed.om.common.MutableDescription;

/**
 * DescriptionImpl
 * <br>
 * Basic Implementation of the <code>MutableDescription</code>
 * interface.
 * 
 * @see org.apache.jetspeed.om.common.MutableDescription
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 *
 */
public abstract class DescriptionImpl implements MutableDescription 
{

    private String description;
    private Locale locale;
    
	protected long parentId;
    
	protected long id;

	/**
	* Tells OJB which class to use to materialize.  
	*/
	protected String ojbConcreteClass = DescriptionImpl.class.getName();
    
 




    public DescriptionImpl()
    {
        super();
        // always init to default locale
        locale = JetspeedLocale.getDefaultLocale();
    }

    public DescriptionImpl(Locale locale, String description)
    {
        this();
        this.locale = locale;
        this.description = description;
        
    }

    /**
     * @see org.apache.jetspeed.om.registry.Description#setDescription(java.lang.String)
     */
    public void setDescription(String description)
    {
        this.description = description;

    }

    /**
     * @see org.apache.jetspeed.om.common.MutableDescription#setLocale(java.util.Locale)
     */
    public void setLocale(Locale locale)
    {
        this.locale = locale;

    }

    /**
     * @see org.apache.jetspeed.om.registry.Description#getDescription()
     */
    public String getDescription()
    {
        return description;
    }

    /**
     * @see org.apache.pluto.om.common.Description#getLocale()
     */
    public Locale getLocale()
    {
        return locale;
    }

    public void setLanguage(String lang)
    {
        String[] localeArray = lang.split("[-|_]");
        String country = "";
        String variant = "";
        for (int i = 0; i < localeArray.length; i++)
        {
            if (i == 0)
            {
                lang = localeArray[i];
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
        this.locale = new Locale(lang, country, variant);
    }

}
