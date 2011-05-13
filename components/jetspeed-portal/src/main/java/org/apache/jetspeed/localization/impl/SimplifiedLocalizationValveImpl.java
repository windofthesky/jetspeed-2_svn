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
package org.apache.jetspeed.localization.impl;

import java.util.Enumeration;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.jetspeed.PortalReservedParameters;
import org.apache.jetspeed.i18n.CurrentLocale;
import org.apache.jetspeed.pipeline.PipelineException;
import org.apache.jetspeed.pipeline.valve.AbstractValve;
import org.apache.jetspeed.pipeline.valve.LocalizationValve;
import org.apache.jetspeed.pipeline.valve.ValveContext;
import org.apache.jetspeed.request.RequestContext;

/**
 * LocalizationValveImpl
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor </a>
 * @version $Id: LocalizationValveImpl.java 378091 2006-02-15 21:12:28Z taylor $
 */
public class SimplifiedLocalizationValveImpl extends AbstractValve implements LocalizationValve
{
    private static final Logger log = LoggerFactory.getLogger(LocalizationValveImpl.class);
    private Locale defaultLocale = null;
    
    public SimplifiedLocalizationValveImpl() {}
    
    public SimplifiedLocalizationValveImpl(String defaultLanguage)
    {
        String language = defaultLanguage != null ? defaultLanguage.trim() : "";
        if (language.length()>0)
        {
            // Code taken from LocaleSelectorPorltet
            String country = "";
            String variant = "";
            int countryIndex = language.indexOf('_');
            if (countryIndex > -1)
            {
                country = language.substring(countryIndex + 1).trim();
                language = language.substring(0, countryIndex).trim();
                int vDash = country.indexOf("_");
                if (vDash > 0)
                {
                    String cTemp = country.substring(0, vDash);
                    variant = country.substring(vDash + 1);
                    country = cTemp;
                }
            }

            defaultLocale = new Locale(language, country, variant);
            if ( defaultLocale.getLanguage().length() == 0 )
            {
                // not a valid language
                defaultLocale = null;
                log.warn("Invalid or unrecognized default language: "+language);
            }
            else
            {
                log.info("Default language set: "+defaultLocale);
            }
                
        }
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.apache.jetspeed.pipeline.valve.Valve#invoke(org.apache.jetspeed.request.RequestContext,
     *      org.apache.jetspeed.pipeline.valve.ValveContext)
     */
    public void invoke( RequestContext request, ValveContext context ) throws PipelineException
    {
        Locale locale = (Locale)request.getRequest().getSession().getAttribute(PortalReservedParameters.PREFERED_LOCALE_ATTRIBUTE);
        
        if ( locale == null && defaultLocale != null )
        {
            locale = defaultLocale;
        }

        if (locale == null)
        {
            locale = request.getRequest().getLocale();
        }
        
        if (locale == null)
        {
            Enumeration preferedLocales = request.getRequest().getLocales();
            while (preferedLocales.hasMoreElements() && locale == null)
            {
                locale = (Locale) preferedLocales.nextElement();
            }
        }

        if (locale == null)
        {
            locale = Locale.getDefault();
        }

        request.setLocale(locale);
        request.getRequest().setAttribute(PortalReservedParameters.PREFERED_LOCALE_ATTRIBUTE, locale);
        request.getRequest().getSession().setAttribute(PortalReservedParameters.PREFERED_LOCALE_ATTRIBUTE, locale);
        CurrentLocale.set(locale);
       
        // Pass control to the next Valve in the Pipeline
        try
        {
            context.invokeNext(request);
        }
        finally
        {
            // ensure clearing of ThreadLocal state after request
            CurrentLocale.set(null);
        }
    }

    public String toString()
    {
        return "LocalizationValve";
    }

}