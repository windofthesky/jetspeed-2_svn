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
package org.apache.jetspeed.components.portletregistry;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.jetspeed.factory.PortletFactory;
import org.apache.jetspeed.om.portlet.PortletApplication;
import org.apache.pluto.PortletContainerException;
import org.apache.pluto.om.portlet.PortletApplicationDefinition;
import org.apache.pluto.om.portlet.PortletDefinition;
import org.apache.pluto.spi.optional.PortletRegistryListener;
import org.apache.pluto.spi.optional.PortletRegistryService;

/**
 * The PlutoRegistryAdaptor is a hybrid of a factory and actua
 * @author dtaylor
 *
 */
public class PlutoRegistryAdaptor implements PortletRegistryService
{
    private PortletRegistry registry;
    
    public PlutoRegistryAdaptor(PortletRegistry registry)
    {
        this.registry = registry;
    }
    
    public void addPortletRegistryListener(PortletRegistryListener arg0)
    {
        // TODO: 2.2
        throw new UnsupportedOperationException();
    }

    public PortletDefinition getPortlet(String applicationName, String portletName)
            throws PortletContainerException
    {
        return registry.getPortletDefinitionByUniqueName(applicationName + "::" + portletName);
    }

    public PortletApplicationDefinition getPortletApplication(String name)
            throws PortletContainerException
    {
        return registry.getPortletApplication(name);
    }

    public Iterator<String> getRegisteredPortletApplicationNames()
    {
        List<String> result = new LinkedList<String>();
        for( PortletApplication app : registry.getPortletApplications())
        {
            result.add(app.getName());
        }
        return result.iterator(); // TODO: 2.2 why an iterator?
    }

    public void removePortletRegistryListener(PortletRegistryListener arg0)
    {
        // TODO: 2.2
        throw new UnsupportedOperationException();
    }
}
