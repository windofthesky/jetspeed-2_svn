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
package org.apache.jetspeed.services.beans;

import java.io.Serializable;
import java.util.Locale;

import javax.xml.bind.annotation.XmlRootElement;

import org.apache.jetspeed.om.portlet.DisplayName;

/**
 * DisplayNameBean
 * 
 * @version $Id$
 */
@XmlRootElement(name="displayName")
public class DisplayNameBean implements Serializable
{
    private static final long serialVersionUID = 1L;
    
    private String displayName;
    private String lang;
    private String localeString;

    public DisplayNameBean()
    {
        
    }
    
    public DisplayNameBean(final DisplayName displayName)
    {
        this.displayName = displayName.getDisplayName();
        lang = displayName.getLang();
        
        Locale locale = displayName.getLocale();
        
        if (locale != null)
        {
            localeString = locale.toString();
        }
    }

    public String getDisplayName()
    {
        return displayName;
    }

    public void setDisplayName(String displayName)
    {
        this.displayName = displayName;
    }

    public String getLang()
    {
        return lang;
    }

    public void setLang(String lang)
    {
        this.lang = lang;
    }

    public String getLocaleString()
    {
        return localeString;
    }

    public void setLocaleString(String localeString)
    {
        this.localeString = localeString;
    }
    
}
