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
package org.apache.jetspeed.portlets.pam;

/**
 * Common resources used by Portlet Application Manager
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public interface PortletApplicationResources
{
    public final static String CPS_SEARCH_COMPONENT = "cps:SearchComponent";
    public final static String CPS_REGISTRY_COMPONENT = "cps:PortletRegistryComponent";
    public final static String CPS_USER_MANAGER_COMPONENT = "cps:UserManager";
    public final static String CPS_PAGE_MANAGER_COMPONENT = "cps:PageManager";
    public final static String CPS_ROLE_MANAGER_COMPONENT = "cps:RoleManager";
    public final static String CPS_GROUP_MANAGER_COMPONENT = "cps:GroupManager";
    public final static String CPS_PROFILER_COMPONENT = "cps:Profiler";
    public final static String CPS_SSO_COMPONENT = "cps:SSO";    
    public final static String CPS_APPLICATION_SERVER_MANAGER_COMPONENT = "cps:ApplicationServerManager";
    public final static String CPS_PORTLET_FACTORY_COMPONENT = "cps:PortletFactory";    
    public final static String CPS_DEPLOYMENT_MANAGER_COMPONENT = "cps:DeploymentManager";    
    
    /** the selected non-leaf node in the tree view */
    public final static String REQUEST_NODE = "node";
    /** the selected leaf node in the tree view */
    public final static String REQUEST_SELECT_NODE = "select_node";
    
    public final static String PORTLET_URL = "portlet_url";
    public final static String REQUEST_SELECT_PORTLET = "select_portlet";
    public final static String REQUEST_SELECT_TAB = "selected_tab";
    public final static String PAM_CURRENT_PA = "org.apache.jetspeed.pam.pa";
    public final static String CURRENT_FOLDER = "current_folder";
    public final static String CURRENT_PAGE = "current_page";
    public final static String PORTLET_ACTION = "portlet_action";
    public final static String REQUEST_SELECT_SITE_TAB = "selected_site_tab";
    public final static String SITE_PORTLET = "SitePortlet";
    
}
