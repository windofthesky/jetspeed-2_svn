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

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.portlet.PortletConfig;
import javax.portlet.PortletContext;
import javax.servlet.ServletConfig;

import org.apache.jetspeed.om.portlet.PortletDefinition;
import org.apache.pluto.internal.impl.PortletConfigImpl;

/**
 * Implements the Portlet API Portlet Config class
 * TODO: 2.2 deprecate ContainerInfo and use central configuration (see ContainerRuntimeOptions)
 * 
 * @author <a href="mailto:david@bluesunrise.com">David Sean Taylor</a>
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 */
public class JetspeedPortletConfig extends PortletConfigImpl implements PortletConfig, InternalPortletConfig
{
    
    public JetspeedPortletConfig(ServletConfig servletConfig, PortletContext portletContext, PortletDefinition portlet)
    {
        super(servletConfig, portletContext, portlet, portlet.getApplication());
    }

    public void setPortletDefinition(PortletDefinition pd)
    {
        this.portlet = pd;        
    }
    
    //  internal portlet config implementation
    public PortletDefinition getPortletDefinition()
    {
        return (PortletDefinition)portlet;
    }
    
    @Override
    public ResourceBundle getResourceBundle(Locale locale)
    {
        return getPortletDefinition().getResourceBundle(locale);
    }

    private List<String> DUMMY_CONFIGURATION = new LinkedList<String>(); // TODO: 2.2 implement
    
    protected List<String> getSupportedContainerRuntimeOptions()
    {
        // TODO: 2.2 - pull these out of jetspeed.properties or something similiar
        return DUMMY_CONFIGURATION;
    }
    
}
