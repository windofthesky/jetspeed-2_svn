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

import java.util.Enumeration;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.portlet.PortletConfig;
import javax.portlet.PortletContext;
import javax.xml.namespace.QName;

import org.apache.jetspeed.om.portlet.Language;
import org.apache.pluto.om.portlet.InitParam;
import org.apache.jetspeed.om.portlet.PortletDefinition;
import org.apache.jetspeed.util.JetspeedLocale;

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
    
    private PortletContext portletContext;
    private PortletDefinition portlet;

    public JetspeedPortletConfig(PortletContext portletContext, PortletDefinition portletEntity)
    {
        this.portletContext = portletContext;
        this.portlet = portletEntity;
    }

    public String getPortletName()
    {
        return portlet.getPortletName();
    }

    public PortletContext getPortletContext()
    {
        return portletContext;
    }

    public ResourceBundle getResourceBundle(Locale locale)
    {
        Language lang = portlet.getLanguage(locale);
        
        if (lang == null)
        {
            lang = portlet.getLanguage(JetspeedLocale.getDefaultLocale());
        }
        return lang.getResourceBundle();
    }

    public String getInitParameter(java.lang.String name)
    {
        if ( name == null )
        {
            throw new IllegalArgumentException("Required parameter name is null");
        }
        //if (log.isDebugEnabled()) log.debug("Getting init parameter for: " + name);
        for (InitParam param : portlet.getInitParams())
        {
            if (param.getParamName().equals(name))
            {
                return param.getParamValue();
            }
        }
        return null;
    }

    public Enumeration<String> getInitParameterNames()
    {
        return new java.util.Enumeration<String>()
        {
            private Iterator<InitParam> iterator = portlet.getInitParams().iterator();

            public boolean hasMoreElements()
            {
                return iterator.hasNext();
            }

            public String nextElement()
            {
                if (iterator.hasNext())
                {
                    return iterator.next().getParamName();
                }
                return null;
            }
        };
    }

    public void setPortletDefinition(PortletDefinition pd)
    {
        this.portlet = pd;        
    }
    
    //  internal portlet config implementation
    public PortletDefinition getPortletDefinition()
    {
        return portlet;
    }

    public Map<String, String[]> getContainerRuntimeOptions()
    {
        return null;
    }

    public String getDefaultNamespace()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public Enumeration<QName> getProcessingEventQNames()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public Enumeration<String> getPublicRenderParameterNames()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public Enumeration<QName> getPublishingEventQNames()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public Enumeration<Locale> getSupportedLocales()
    {
        // TODO Auto-generated method stub
        return null;
    }
}
