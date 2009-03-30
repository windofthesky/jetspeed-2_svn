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
package org.apache.jetspeed.container;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.jetspeed.factory.PortletFactory;
import org.apache.jetspeed.om.portlet.Language;
import org.apache.jetspeed.om.portlet.PortletDefinition;
import org.apache.pluto.container.impl.AbstractPortletConfigImpl;

/**
 * Implements the Portlet API Portlet Config class
 * 
 * @author <a href="mailto:david@bluesunrise.com">David Sean Taylor</a>
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 */
public class JetspeedPortletConfigImpl extends AbstractPortletConfigImpl implements JetspeedPortletConfig
{
    private PortletFactory pf;
    
    public JetspeedPortletConfigImpl(PortletFactory pf, JetspeedPortletContext portletContext, PortletDefinition portlet)
    {
        super(portletContext, portlet);
        this.pf = pf;
    }

    public void setPortletDefinition(PortletDefinition pd)
    {
        this.portlet = pd;
        // clear internal cache
        this.containerRuntimeOptions = null;
    }
    
    @Override
    public PortletDefinition getPortletDefinition()
    {
        return (PortletDefinition)portlet;
    }
    
    public ResourceBundle getResourceBundle(Locale locale)
    {
        return pf.getResourceBundle(getPortletDefinition(),locale);
    }
    
    /**
     * Overriding the default implementation from Pluto AbstractPortletConfigImpl to use the Jetspeed
     * PortletDefinition.getLanguages() instead of having to convert from Locale -> String -> Locale again
     * @Override
     */
    public Enumeration<Locale> getSupportedLocales() 
    {
        List<Locale> locales = new ArrayList<Locale>();
        for (Language l : getPortletDefinition().getLanguages())
        {
            if (l.isSupportedLocale())
            {
                locales.add(l.getLocale());
            }
        }
        return Collections.enumeration(locales);
    }
}
