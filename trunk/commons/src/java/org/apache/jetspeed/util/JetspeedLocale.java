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

package org.apache.jetspeed.util;

import java.util.Locale;
import java.util.StringTokenizer;

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
    public static Locale convertStringToLocale(String localeString)
    {
        if (localeString == null)
        {
            return null;
        }
        StringTokenizer tokenizer = new StringTokenizer(localeString, DELIM);

        String language = tokenizer.nextToken().trim();
        String country = null;
        String variant = null;
        if (tokenizer.hasMoreTokens())
        {
            country = tokenizer.nextToken().trim();
        }

        if (tokenizer.hasMoreTokens())
        {
            variant = tokenizer.nextToken().trim();
        }

        if (country != null && language != null && variant != null)
        {
            return new Locale(language, country, variant);
        }
        else if (country != null && language != null)
        {
            return new Locale(language, country);
        }
        else if (language != null)
        {
            return new Locale(language, ""); // JDK 1.3 compatibility
        }
        else
        {
            return null;
        }

    }


}
