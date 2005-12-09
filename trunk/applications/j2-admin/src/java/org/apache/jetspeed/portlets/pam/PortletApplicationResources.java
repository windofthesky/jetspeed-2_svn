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
 * @version $Id: PortletApplicationResources.java 348264 2005-11-22 22:06:45Z taylor $
 */
public interface PortletApplicationResources
{    
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
    public final static String CURRENT_LINK = "current_link";
    public final static String PORTLET_ACTION = "portlet_action";
    public final static String REQUEST_SELECT_SITE_TAB = "selected_site_tab";
    public final static String SITE_PORTLET = "SitePortlet";
    public final static String NODE_UPDATED = "node_updated";
    
    // Message Topics
    public final static String TOPIC_PORTLET_SELECTOR = "portlet.selector";
    
    /** Messages **/
    public static final String MESSAGE_SELECTED = "selected";
    public static final String MESSAGE_CHANGED = "changed";
    public static final String MESSAGE_STATUS = "status";
    public static final String MESSAGE_REFRESH = "refresh";
    public static final String MESSAGE_FILTERED = "filtered";    
    public static final String MESSAGE_SEARCHSTRING = "searchString";
    
}
