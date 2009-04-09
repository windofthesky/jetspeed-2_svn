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

package org.apache.jetspeed.util;

import java.util.List;
import java.util.Locale;

import org.apache.jetspeed.i18n.LocalizedObject;

/**
 * Class to set and get Locale settings for Jetspeed.
 *          
 * @author <a href="mailto:roger.ruttimann@earthlink.net">Roger Ruttimann</a>
 * @author <a href="mailto:shinsuke@yahoo.co.jp">Shinsuke Sugaya</a>
 * @version $Id$
 */
public class JetspeedLocale
{
    private static final String DELIM = ",";
    
    /**
     * According to PLT.21.8.1, the default locale should be English.
     */
    public static Locale getDefaultLocale()
    {
        return Locale.ENGLISH;
    }


    /**
     * Converts Locale to String.
     * 
     * @param locale
     * @return
     */
    public static String convertLocaleToString(Locale locale)
    {
        if (locale == null)
        {
            return null;
        }
        String country = locale.getCountry();
        String language = locale.getLanguage();
        String variant = locale.getVariant();
        StringBuffer buffer = new StringBuffer(40);
        if (language != null)
        {
            buffer.append(language);
        }
        buffer.append(DELIM);

        if (country != null)
        {
            buffer.append(country);
        }
        buffer.append(DELIM);

        if (variant != null)
        {
            buffer.append(variant);
        }

        return buffer.toString().trim();
    }

    /**
     * Converts String to Locale.
     * 
     * @param localeString
     * @return
     */
    public static Locale convertStringToLocale(String lang)
    {
        if (lang == null)
        {
            return null;
        }
        String country = "";
        String variant = "";
        String[] localeArray = lang.split("[-|_,]");
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
        return new Locale(lang, country, variant);
    }    
    
    public static LocalizedObject getBestLocalizedObject(List<? extends LocalizedObject> list, Locale locale)
    {
        LocalizedObject fallback = null;
        for (LocalizedObject lo : list)
        {
            if (lo.getLocale().equals(locale))
            {
                return lo;
            }
            else if (lo.getLocale().getLanguage().equals(locale.getLanguage()))
            {
                fallback = lo;
            }
            else if (fallback == null && lo.getLocale().equals(getDefaultLocale()))
            {
                fallback = lo;
            }
        }
        return fallback;
    }
}
