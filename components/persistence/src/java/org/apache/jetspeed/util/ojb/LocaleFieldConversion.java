/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2000-2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *    "Apache Jetspeed" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Apache Jetspeed", nor may "Apache" appear in their name, without
 *    prior written permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.jetspeed.util.ojb;

import java.util.Locale;
import java.util.StringTokenizer;

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
                return Locale.getDefault();
            }
        }
        else
        {
            return arg0;
        }

    }

}
