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
package org.apache.jetspeed.container;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.servlet.ServletConfig;
import javax.portlet.PortletConfig;
import javax.portlet.PortletContext;

import org.apache.pluto.om.common.Language;
import org.apache.pluto.om.common.LanguageSet;
import org.apache.pluto.om.common.Parameter;
import org.apache.pluto.om.common.ParameterSet;
import org.apache.pluto.om.portlet.PortletDefinition;

/**
 * Implements the Portlet API Portlet Config class
 *
 * @author <a href="mailto:david@bluesunrise.com">David Sean Taylor</a>
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 */
public class JetspeedPortletConfig implements PortletConfig, InternalPortletConfig
{
    // private static final Log log = LogFactory.getLog(JetspeedPortletConfig.class);
    
    private ServletConfig servletConfig;
    private PortletContext portletContext;
    private PortletDefinition portletDefinition;

    public JetspeedPortletConfig(ServletConfig servletConfig, PortletContext portletContext, PortletDefinition portletEntity)
    {
        this.servletConfig = servletConfig;
        this.portletContext = portletContext;
        this.portletDefinition = portletEntity;
    }

    public String getPortletName()
    {
        return portletDefinition.getName();
    }

    public PortletContext getPortletContext()
    {
        return portletContext;
    }

    public ResourceBundle getResourceBundle(Locale locale)
    {
        LanguageSet languageSet = portletDefinition.getLanguageSet();
        
        Language lang = languageSet.get(locale);
                                                                                
        if (lang == null)
        {
            Locale defaultLocale = languageSet.getDefaultLocale();
            lang = languageSet.get(defaultLocale);
        }
        
        return lang.getResourceBundle();
    }

    public String getInitParameter(java.lang.String name)
    {
        //if (log.isDebugEnabled()) log.debug("Getting init parameter for: " + name);
        ParameterSet parameters = portletDefinition.getInitParameterSet();
        Parameter param = parameters.get(name);

        if (param != null)
        {
            // if (log.isDebugEnabled()) log.debug("Param: [[name," + name + "], [value, " + param.getValue() + "]]");
            return param.getValue();
        }

        return null;
    }

    public Enumeration getInitParameterNames()
    {
        return new java.util.Enumeration()
        {
            private ParameterSet parameters = portletDefinition.getInitParameterSet();
            private Iterator iterator = parameters.iterator();

            public boolean hasMoreElements()
            {
                return iterator.hasNext();
            }

            public Object nextElement()
            {
                if (iterator.hasNext())
                    return ((Parameter) iterator.next()).getName();
                else
                    return null;
            }
        };

    }

    //  internal portlet config implementation

    public javax.servlet.ServletConfig getServletConfig()
    {
        return servletConfig;
    }

    public PortletDefinition getPortletDefinition()
    {
        return portletDefinition;
    }

}
