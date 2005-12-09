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
package org.apache.jetspeed.localization.impl;

import java.security.Principal;
import java.util.Enumeration;
import java.util.Locale;
import java.util.prefs.Preferences;

import javax.security.auth.Subject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.Jetspeed;
import org.apache.jetspeed.PortalReservedParameters;
import org.apache.jetspeed.i18n.CurrentLocale;
import org.apache.jetspeed.pipeline.PipelineException;
import org.apache.jetspeed.pipeline.valve.AbstractValve;
import org.apache.jetspeed.pipeline.valve.LocalizationValve;
import org.apache.jetspeed.pipeline.valve.ValveContext;
import org.apache.jetspeed.request.RequestContext;
import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.security.SecurityHelper;
import org.apache.jetspeed.security.User;
import org.apache.jetspeed.security.UserManager;
import org.apache.jetspeed.security.UserPrincipal;
import org.apache.jetspeed.util.JetspeedLocale;

/**
 * LocalizationValveImpl
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor </a>
 * @version $Id$
 */
public class LocalizationValveImpl extends AbstractValve implements LocalizationValve
{
    private static final Log log = LogFactory.getLog(LocalizationValveImpl.class);
    private Locale defaultLocale = null;
    
    public LocalizationValveImpl() {}
    
    public LocalizationValveImpl(String defaultLanguage)
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
        Locale locale = null;

        // Get the prefered locale from user's preferences(persistent storage) if not anon user
        Subject subject = request.getSubject();
        if (null != subject)
        {
            Principal userPrincipal = SecurityHelper.getPrincipal(subject, UserPrincipal.class);
            if (null != userPrincipal)
            {
                log.debug("Got user principal: " + userPrincipal.getName());
                UserManager userMgr = (UserManager) Jetspeed.getComponentManager().getComponent(UserManager.class);
                try
                {
                    if (!userMgr.getAnonymousUser().equals(userPrincipal.getName())
                            && userMgr.userExists(userPrincipal.getName()))
                    {
                        User user = userMgr.getUser(userPrincipal.getName());
                        // TODO if preferred lang or locale is defined in PLT.D, it's better to use it
                        Preferences prefs = user.getPreferences();
                        String localeString = prefs.get(PortalReservedParameters.PREFERED_LOCALE_ATTRIBUTE, null);
                        if (localeString != null)
                        {
                            locale = JetspeedLocale.convertStringToLocale(localeString);
                        }
                    }
                }
                catch (SecurityException e)
                {
                    log.warn("Unexpected SecurityException in UserInfoManager", e);
                }
            }
        }

        if (locale == null)
        {
            locale = (Locale) request.getSessionAttribute(PortalReservedParameters.PREFERED_LOCALE_ATTRIBUTE);
        }
        
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
        context.invokeNext(request);

    }

    public String toString()
    {
        return "LocalizationValve";
    }

}