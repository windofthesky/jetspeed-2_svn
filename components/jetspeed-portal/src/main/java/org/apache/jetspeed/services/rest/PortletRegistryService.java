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
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.PathSegment;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.jetspeed.JetspeedActions;
import org.apache.jetspeed.components.portletregistry.PortletRegistry;
import org.apache.jetspeed.om.portlet.LocalizedField;
import org.apache.jetspeed.om.portlet.PortletApplication;
import org.apache.jetspeed.om.portlet.PortletDefinition;
import org.apache.jetspeed.search.ParsedObject;
import org.apache.jetspeed.search.SearchEngine;
import org.apache.jetspeed.search.SearchResults;
import org.apache.jetspeed.security.SecurityAccessController;
import org.apache.jetspeed.services.beans.PortletApplicationBean;
import org.apache.jetspeed.services.beans.PortletApplicationBeanCollection;
import org.apache.jetspeed.services.beans.PortletDefinitionBean;
import org.apache.jetspeed.services.beans.PortletDefinitionBeanCollection;
import org.apache.jetspeed.services.rest.util.PaginationUtils;
import org.apache.jetspeed.services.rest.util.SearchEngineUtils;
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
    
    private SecurityAccessController securityAccessController;
    
    private PortletRegistry portletRegistry;
    
    private SearchEngine searchEngine;
    
    public PortletRegistryService(SecurityAccessController securityAccessController, PortletRegistry portletRegistry, SearchEngine searchEngine)
    {
        this.securityAccessController = securityAccessController;
        this.portletRegistry = portletRegistry;
        this.searchEngine = searchEngine;
    }
    
    @GET
    @Path("/application/{path:.*}")
    public PortletApplicationBeanCollection getPortletApplications(@Context HttpServletRequest servletRequest,
                                                                   @Context UriInfo uriInfo,
                                                                   @PathParam("path") List<PathSegment> pathSegments,
                                                                   @QueryParam("query") String queryParam, 
                                                                   @QueryParam("begin") String beginIndexParam,
                                                                   @QueryParam("max") String maxResultsParam)
    {
        String applicationName = null;
        
        if (pathSegments != null && !pathSegments.isEmpty())
        {
            applicationName = pathSegments.get(0).getPath();
        }
        
        int beginIndex = NumberUtils.toInt(beginIndexParam, -1);
        int maxResults = NumberUtils.toInt(maxResultsParam, -1);
        
        PortletApplicationBeanCollection paBeans = new PortletApplicationBeanCollection();
        paBeans.setBeginIndex(beginIndex);
        paBeans.setTotalSize(0);
        List<PortletApplicationBean> paBeanList = new ArrayList<PortletApplicationBean>();
        
        if (!StringUtils.isBlank(queryParam))
        {
            String queryText = 
                ParsedObject.FIELDNAME_TYPE + ":\"" + ParsedObject.OBJECT_TYPE_PORTLET_APPLICATION + "\" AND ( " + queryParam + " )";
            SearchResults searchResults = searchEngine.search(queryText);
            List<ParsedObject> searchResultList = searchResults.getResults();
            paBeans.setTotalSize(searchResultList.size());
            
            for (ParsedObject parsedObject : (List<ParsedObject>) PaginationUtils.subList(searchResultList, beginIndex, maxResults))
            {
                String appName = SearchEngineUtils.getFieldAsString(parsedObject, "ID", null);
                
                if (StringUtils.isBlank(appName))
                {
                    continue;
                }
                
                PortletApplication pa = portletRegistry.getPortletApplication(appName);
                
                if (pa != null)
                {
                    paBeanList.add(new PortletApplicationBean(pa));
                }
            }
        }
        else
        {
            if (StringUtils.isBlank(applicationName))
            {
                Collection<PortletApplication> pas = portletRegistry.getPortletApplications();
                paBeans.setTotalSize(pas.size());
                
                for (PortletApplication pa : (Collection<PortletApplication>) PaginationUtils.subCollection(pas, beginIndex, maxResults))
                {
                    paBeanList.add(new PortletApplicationBean(pa));
                }
            }
            else
            {
                PortletApplication pa = portletRegistry.getPortletApplication(applicationName, true);
                
                if (pa != null)
                {
                    paBeans.setTotalSize(1);
                    paBeanList.add(new PortletApplicationBean(pa));
                }
            }
        }
        
        paBeans.setApplications(paBeanList);
        
        return paBeans;
    }
    
    @GET
    @Path("/definition/{path:.*}")
    public PortletDefinitionBeanCollection getPortletDefinitions(@Context HttpServletRequest servletRequest,
                                                                 @Context UriInfo uriInfo,
                                                                 @PathParam("path") List<PathSegment> pathSegments, 
                                                                 @QueryParam("keywords") String keywordsParam, 
                                                                 @QueryParam("query") String queryParam, 
                                                                 @QueryParam("begin") String beginIndexParam,
                                                                 @QueryParam("max") String maxResultsParam)
    {
        String applicationName = null;
        String definitionName = null;
        
        if (pathSegments != null)
        {
            if (pathSegments.size() > 0)
            {
                applicationName = pathSegments.get(0).getPath();
            }
            
            if (pathSegments.size() > 1)
            {
                definitionName = pathSegments.get(1).getPath();
            }
        }
        
        int beginIndex = NumberUtils.toInt(beginIndexParam, -1);
        int maxResults = NumberUtils.toInt(maxResultsParam, -1);
        
        PortletDefinitionBeanCollection pdBeans = new PortletDefinitionBeanCollection();
        pdBeans.setBeginIndex(beginIndex);
        pdBeans.setTotalSize(0);
        List<PortletDefinitionBean> pdBeanList = new ArrayList<PortletDefinitionBean>();
        
        if (!StringUtils.isBlank(keywordsParam) || !StringUtils.isBlank(queryParam))
        {
            String queryText = 
                ParsedObject.FIELDNAME_TYPE + ":\"" + ParsedObject.OBJECT_TYPE_PORTLET + "\" " +
                "AND NOT " + ParsedObject.FIELDNAME_TYPE + ":\"" + ParsedObject.OBJECT_TYPE_PORTLET_APPLICATION + "\" " + 
                "AND ( " + (!StringUtils.isBlank(keywordsParam) ? keywordsParam : queryParam) + " )";
            SearchResults searchResults = null;
            
            if (!StringUtils.isBlank(keywordsParam))
            {
                searchResults = searchEngine.search(queryText, ParsedObject.FIELDNAME_KEYWORDS);
            }
            else
            {
                searchResults = searchEngine.search(queryText);
            }
            
            List<ParsedObject> searchResultList = searchResults.getResults();
            ArrayList<PortletDefinition> searchedPortletDefinitions = new ArrayList<PortletDefinition>();
            
            for (ParsedObject parsedObject : searchResultList)
            {
                String uniqueName = SearchEngineUtils.getPortletUniqueName(parsedObject);
                
                if (StringUtils.isBlank(uniqueName))
                {
                    continue;
                }
                
                PortletDefinition pd = portletRegistry.getPortletDefinitionByUniqueName(uniqueName);
                
                if (pd != null && securityAccessController.checkPortletAccess(pd, JetspeedActions.MASK_VIEW))
                {
                    searchedPortletDefinitions.add(pd);
                }
            }
            
            Collection<PortletDefinition> filteredPortletDefinitions = filterPortletDefinitionsBySecurityAccess(searchedPortletDefinitions, JetspeedActions.MASK_VIEW, servletRequest);
            pdBeans.setTotalSize(filteredPortletDefinitions.size());
            
            for (PortletDefinition pd : (Collection<PortletDefinition>) PaginationUtils.subCollection(filteredPortletDefinitions, beginIndex, maxResults))
            {
                pdBeanList.add(new PortletDefinitionBean(pd));
            }
        }
        else
        {
            if (StringUtils.isBlank(applicationName) && StringUtils.isBlank(definitionName))
            {
                Collection<PortletDefinition> pds = filterPortletDefinitionsBySecurityAccess(portletRegistry.getAllDefinitions(), JetspeedActions.MASK_VIEW, servletRequest);
                pdBeans.setTotalSize(pds.size());
                
                for (PortletDefinition pd : (Collection<PortletDefinition>) PaginationUtils.subCollection(pds, beginIndex, maxResults))
                {
                    pdBeanList.add(new PortletDefinitionBean(pd));
                }
            }
            else
            {
                PortletApplication pa = portletRegistry.getPortletApplication(applicationName, true);
                
                if (pa != null)
                {
                    if (StringUtils.isBlank(definitionName))
                    {
                        Collection<PortletDefinition> pds = filterPortletDefinitionsBySecurityAccess(pa.getPortlets(), JetspeedActions.MASK_VIEW, servletRequest);
                        pdBeans.setTotalSize(pds.size());
                        
                        for (PortletDefinition pd : (List<PortletDefinition>) PaginationUtils.subCollection(pds, beginIndex, maxResults))
                        {
                            pdBeanList.add(new PortletDefinitionBean(pd));
                        }
                    }
                    else
                    {
                        PortletDefinition pd = pa.getPortlet(definitionName);
                        
                        if (pd == null)
                        {
                            pd = pa.getClone(definitionName);
                        }
                        
                        if (pd != null && securityAccessController.checkPortletAccess(pd, JetspeedActions.MASK_VIEW))
                        {
                            pdBeanList.add(new PortletDefinitionBean(pd));
                            pdBeans.setTotalSize(1);
                        }
                    }
                }
            }
        }
        
        pdBeans.setDefinitions(pdBeanList);
        
        return pdBeans;
    }
    
    private Collection<PortletDefinition> filterPortletDefinitionsBySecurityAccess(Collection<PortletDefinition> collection, int mask, HttpServletRequest servletRequest)
    {
        if (securityAccessController == null)
        {
            return collection;
        }
        
        if (collection == null || collection.isEmpty())
        {
            return Collections.emptyList();
        }
        
        ArrayList<PortletDefinition> filteredCollection = new ArrayList<PortletDefinition>();
        
        for (PortletDefinition pd : collection)
        {
            if (isFilteredOutByRole(pd, servletRequest))
            {
                continue;
            }
            
            if (securityAccessController.checkPortletAccess(pd, mask))
            {
                filteredCollection.add(pd);
            }
        }
        
        return filteredCollection;
    }
    
    private boolean isFilteredOutByRole(PortletDefinition pd, HttpServletRequest servletRequest)
    {
        boolean filteredOut = false;
        
        Collection<LocalizedField> fields = pd.getMetadata().getFields("selector.conditional.role");
        
        if (fields != null && !fields.isEmpty()) 
        {
            LocalizedField field = fields.iterator().next();
            String role = field.getValue();
            
            if (!StringUtils.isBlank(role))
            {
                if (role.equals("*"))
                {
                    filteredOut = true;
                }
                else
                {
                    filteredOut = (!servletRequest.isUserInRole(role));
                }
            }
        }
        
        return filteredOut;
    }
    
}
