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

import java.util.Enumeration;
import java.util.Locale;

import org.apache.jetspeed.pipeline.PipelineException;
import org.apache.jetspeed.pipeline.valve.AbstractValve;
import org.apache.jetspeed.pipeline.valve.LocalizationValve;
import org.apache.jetspeed.pipeline.valve.ValveContext;
import org.apache.jetspeed.request.RequestContext;

/**
 * LocalizationValveImpl
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor </a>
 * @version $Id$
 */
public class LocalizationValveImpl extends AbstractValve implements LocalizationValve
{

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.jetspeed.pipeline.valve.Valve#invoke(org.apache.jetspeed.request.RequestContext,
     *      org.apache.jetspeed.pipeline.valve.ValveContext)
     */
    public void invoke( RequestContext request, ValveContext context ) throws PipelineException
    {
        // TODO Get the prefered locale from user's persistent storage if not anon user

        Locale locale =
            (Locale) request.getSessionAttribute(RequestContext.PREFERED_LOCALE_SESSION_KEY);

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
        request.getRequest().setAttribute(RequestContext.PREFERED_LOCALE_SESSION_KEY, locale);
        request.getRequest().getSession().setAttribute(RequestContext.PREFERED_LOCALE_SESSION_KEY, locale);
       
        // Pass control to the next Valve in the Pipeline
        context.invokeNext(request);

    }

    public String toString()
    {
        return "LocalizationValve";
    }

}