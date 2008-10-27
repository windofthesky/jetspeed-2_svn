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

import java.util.Locale;

import org.apache.jetspeed.util.JetspeedLocale;
import org.apache.jetspeed.om.portlet.DisplayName;

/**
 * DisplayNameImpl
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 *
 */
public class DisplayNameImpl implements DisplayName
{
    protected String value;
    protected String lang = null;
    protected Locale locale = null;

    public String getDisplayName()
    {
        return value;
    }

    public void setDisplayName(String value)
    {
        this.value = value;
    }

    public String getLang()
    {
        return lang == null ? JetspeedLocale.getDefaultLocale().toString() : lang;
    }

    public void setLang(String value)
    {
        lang = value;
    }

    public Locale getLocale()
    {
        return locale == null ? deriveLocale() : locale;
    }
    
    protected Locale deriveLocale()
    {
        String lang = this.getLang();
        String country = "";
        String variant = "";
        String[] localeArray = lang.split("[-|_]");
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
        locale = new Locale(lang, country, variant);
        return locale;
    }
}
