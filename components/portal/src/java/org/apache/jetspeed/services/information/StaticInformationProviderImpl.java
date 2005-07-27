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
package org.apache.jetspeed.services.information;

import javax.servlet.ServletConfig;

import org.apache.jetspeed.components.portletregistry.PortletRegistry;
import org.apache.pluto.om.common.ObjectID;
import org.apache.pluto.om.portlet.PortletDefinition;
import org.apache.pluto.services.information.PortalContextProvider;
import org.apache.pluto.services.information.StaticInformationProvider;

/**
 * Provides static information to Pluto Container:
 * 
 * 1. PortletDefinition - given a unique registry id, 
 *                        retrieve the portlet definition from the portlet registry   
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class StaticInformationProviderImpl implements StaticInformationProvider
{
    private final ServletConfig config;
    private final PortalContextProvider portalContextProvider;
    private final PortletRegistry portletRegistry;

    public StaticInformationProviderImpl(ServletConfig config, PortalContextProvider portalContextProvider, PortletRegistry portletRegistry)
    {
        this.config = config;
        this.portalContextProvider = portalContextProvider;
        this.portletRegistry = portletRegistry;
    }

    /**
     *  Given a unique registry id, 
     * retrieve the portlet definition from the portlet registry
     *    
     * @param uniqueId The uniquely identifying portlet id in the registry
     */
    public PortletDefinition getPortletDefinition(String uniqueId)
    {        
        return portletRegistry.getPortletDefinitionByIdentifier(uniqueId);
    }

    /** 
     * <p>
     * getPortalContextProvider
     * </p>
     * 
     * @see org.apache.pluto.services.information.StaticInformationProvider#getPortalContextProvider()
     * @return
     */
    public PortalContextProvider getPortalContextProvider()
    {
        return portalContextProvider;
    }

    /** 
     * <p>
     * getPortletDefinition
     * </p>
     * 
     * @see org.apache.pluto.services.information.StaticInformationProvider#getPortletDefinition(org.apache.pluto.om.common.ObjectID)
     * @param arg0
     * @return
     */
    public PortletDefinition getPortletDefinition(ObjectID id)
    {        
        return portletRegistry.getPortletDefinition(id);
    }

}
