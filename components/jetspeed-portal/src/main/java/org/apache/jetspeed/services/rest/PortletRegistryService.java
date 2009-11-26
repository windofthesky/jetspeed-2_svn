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
package org.apache.jetspeed.services.rest;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.lang.StringUtils;
import org.apache.jetspeed.Jetspeed;
import org.apache.jetspeed.components.portletregistry.PortletRegistry;
import org.apache.jetspeed.om.portlet.PortletApplication;
import org.apache.jetspeed.om.portlet.PortletDefinition;
import org.apache.jetspeed.services.beans.PortletApplicationBean;
import org.apache.jetspeed.services.beans.PortletApplicationBeans;
import org.apache.jetspeed.services.beans.PortletDefinitionBean;
import org.apache.jetspeed.services.beans.PortletDefinitionBeans;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * PortletRegistryService
 * 
 * @vesion $Id$
 */

@Path("/portletregistry/")
public class PortletRegistryService
{
    
    private static Logger log = LoggerFactory.getLogger(PortletRegistryService.class);
    
    @Context
    private ServletConfig servletConfig;
    
    @Context
    private ServletContext servletContext;
    
    @Context
    private HttpServletRequest servletRequest;
    
    @Context
    private UriInfo uriInfo;
    
    private PortletRegistry portletRegistry;
    
    public PortletRegistryService()
    {
        portletRegistry = (PortletRegistry) Jetspeed.getComponentManager().getComponent(PortletRegistry.class);
    }
    
    @GET
    @Path("/application/{name}")
    public PortletApplicationBeans getPortletApplication(@PathParam("name") String name)
    {
        PortletApplicationBeans paBeans = new PortletApplicationBeans();
        List<PortletApplicationBean> paBeanList = new ArrayList<PortletApplicationBean>();
        
        if (StringUtils.isBlank(name))
        {
            for (PortletApplication pa : portletRegistry.getPortletApplications())
            {
                paBeanList.add(new PortletApplicationBean(pa));
            }
        }
        else
        {
            PortletApplication pa = portletRegistry.getPortletApplication(name, true);
            
            if (pa != null)
            {
                paBeanList.add(new PortletApplicationBean(pa));
            }
        }
        
        paBeans.setPortletApplicationBeans(paBeanList);
        return paBeans;
    }
    
    @GET
    @Path("/definition/{uniqueName}")
    public PortletDefinitionBeans getPortletDefinition(@PathParam("uniqueName") String uniqueName)
    {
        PortletDefinitionBeans pdBeans = new PortletDefinitionBeans();
        List<PortletDefinitionBean> pdBeanList = new ArrayList<PortletDefinitionBean>();
        
        if (StringUtils.isBlank(uniqueName))
        {
            for (PortletDefinition pd : portletRegistry.getAllPortletDefinitions())
            {
                pdBeanList.add(new PortletDefinitionBean(pd));
            }
        }
        else
        {
            int offset = uniqueName.indexOf("::");
            
            if (offset != -1)
            {
                PortletDefinition pd = portletRegistry.getPortletDefinitionByUniqueName(uniqueName, true);
                
                if (pd != null)
                {
                    pdBeanList.add(new PortletDefinitionBean(pd));
                }
            }
            else
            {
                PortletApplication pa = portletRegistry.getPortletApplication(uniqueName, true);
                
                if (pa != null)
                {
                    for (PortletDefinition pd : pa.getPortlets())
                    {
                        pdBeanList.add(new PortletDefinitionBean(pd));
                    }
                }
            }
        }
        
        pdBeans.setPortletApplicationBeans(pdBeanList);
        return pdBeans;
    }
    
}
