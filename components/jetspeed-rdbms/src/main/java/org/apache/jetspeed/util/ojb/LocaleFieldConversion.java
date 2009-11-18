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
package org.apache.jetspeed.util.ojb;

import java.util.Locale;
import java.util.StringTokenizer;

import org.apache.jetspeed.util.JetspeedLocale;
import org.apache.ojb.broker.accesslayer.conversions.ConversionException;
import org.apache.ojb.broker.accesslayer.conversions.FieldConversion;

/**
 * <p style="font-weight: bold">
 * ObjectRelationalBridge field conversion.
 * </p>
 * Helps transparently map Locale objects into a database table
 * that contains country, langauge and variant field and vice versa.
 * 
 * field should be tokenized with commas
 */
public class LocaleFieldConversion implements FieldConversion
{
    private static final String DELIM = ",";

    /**
     * @see org.apache.ojb.broker.accesslayer.conversions.FieldConversion#javaToSql(java.lang.Object)
     */
    public Object javaToSql(Object arg0) throws ConversionException
    {
        if (arg0 instanceof Locale)
        {

            Locale locale = (Locale) arg0;
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
        else
        {
            return arg0;
        }
    }

    /**
     * @see org.apache.ojb.broker.accesslayer.conversions.FieldConversion#sqlToJava(java.lang.Object)
     */
    public Object sqlToJava(Object arg0) throws ConversionException
    {
        if (arg0 instanceof String)
        {
            String localeString = (String) arg0;
            StringTokenizer tokenizer = new StringTokenizer(localeString, DELIM);
            if(tokenizer.hasMoreTokens() == false)            
            {
                return JetspeedLocale.getDefaultLocale();    
            }
            String language = tokenizer.nextToken().trim();
            String country = null;
            String variant = null;
            if(tokenizer.hasMoreTokens())
            {
                country = tokenizer.nextToken().trim();
            }           
            if(tokenizer.hasMoreTokens())
            {
                variant = tokenizer.nextToken().trim();
            }
            if (country != null && language != null && variant != null)
            {
                return new Locale (language, country, variant);
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
                return JetspeedLocale.getDefaultLocale();
            }
        }
        else
        {
            return arg0;
        }

    }

}
