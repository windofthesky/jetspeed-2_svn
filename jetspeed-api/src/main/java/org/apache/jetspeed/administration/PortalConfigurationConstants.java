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
package org.apache.jetspeed.administration;

/**
 * Portal Configuration constants to read only portal global settings. These constants are the keys in the portal
 * configuration store jetspeed.properties and override.properties.
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: $
 */
public interface PortalConfigurationConstants
{
    /** EMAIL */
    /** email of the system administrator */
    static final String EMAIL_ADMIN = "email.admin";
    /** email of the system manager */
    static final String EMAIL_MANAGER = "email.manager";
    /** email sender */
    static final String EMAIL_SENDER = "email.sender";
    /** email user info attribute **/
    static final String EMAIL_USERINFO_ATTRIBUTE = "email.userinfo.attribute";
    
    /** LAYOUT */
    /** the default page layout if none is specified */
    static final String LAYOUT_PAGE_DEFAULT = "layout.page.default";
    
    /** Decorators */
    /** default page decorator if none specified */
    static final String DECORATOR_PAGE_DEFAULT = "decorator.page.default";
    /** default portlet decorator if none specified */    
    static final String DECORATOR_PORTLET_DEFAULT = "decorator.portlet.default";
    
    /** PSML **/
    /** default psml page */
    static final String PSML_PAGE_DEFAULT = "psml.page.default";
    
    /** PSML Templates */
    /** PSML Folder Template to copy during new user creation and registration */
    static final String PSML_TEMPLATE_FOLDER = "psml.template.folder";
    
    /** PROFILER **/
    static final String PROFILER_RULE_NAMES_DEFAULT = "profiler.rule.names.default";
    static final String PROFILER_RULE_VALUES_DEFAULT = "profiler.rule.values.default";
    
    /** Registration */ 
    /** Registration default Roles assigned during registration or new user creation **/
    static final String REGISTRATION_ROLES_DEFAULT = "registration.roles.default";
    /** Registration default groups assigned during registration or new user creation **/    
    static final String REGISTRATION_GROUPS_DEFAULT = "registration.groups.default";
    /** Registration default profiling rules assigned during registration or new user creation **/        
    static final String REGISTRATION_RULES_DEFAULT = "registration.rules.default";
    
    /** Users */
    static final String USERS_DEFAULT_ADMIN = "default.admin.user";
    static final String USERS_DEFAULT_GUEST = "default.user.principal";    
    static final String ROLES_DEFAULT_ADMIN = "default.admin.role";    
    static final String ROLES_DEFAULT_MANAGER = "default.manager.role";
    static final String ROLES_DEFAULT_USER = "default.user.role";
    static final String ROLES_DEFAULT_GUEST = "default.guest.role";
    
    /** Jetui */
    static final String JETUI_CUSTOMIZATION_METHOD = "jetui.customization.method";
    static final String JETUI_CUSTOMIZATION_SERVER = "server";
    static final String JETUI_CUSTOMIZATION_AJAX = "ajax";
    static final String JETUI_LAYOUT_VIEW = "jetui.layout.view";
    static final String JETUI_LAYOUT_MAX = "jetui.layout.max";
    static final String JETUI_LAYOUT_SOLO = "jetui.layout.solo";
    static final String JETUI_RENDER_TEMPLATE = "jetui.render.template";
    static final String JETUI_RENDER_ENGINE = "jetui.render.engine";
    static final String JETUI_CSRE = "CSRE";
    static final String JETUI_SSRE = "SSRE";
    static final String JETUI_AJAX_TRANSPORT = "jetui.ajax.transport";
    static final String JETUI_TRANSPORT_JSON = "json";
    static final String JETUI_TRANSPORT_XML = "xml";
    static final String JETUI_DRAG_MODE = "jetui.drag.mode";
    static final String JETUI_DRAG_FULL = "full";
    static final String JETUI_DRAG_YAHOO = "yahoo";
    static final String JETUI_STYLE_PORTLET = "jetui.style.portlet";
    static final String JETUI_STYLE_LAYOUT = "jetui.style.layout";
    static final String JETUI_STYLE_DRAG_HANDLE = "jetui.style.drag.handle";
    static final String JETUI_REDIRECT_HOME_SPACE = "jetui.redirect.home.space";
    
    /** Portlet Modes, Window States: return string arrays **/
    static final String SUPPORTED_WINDOW_STATES = "supported.windowstate";
    static final String SUPPORTED_PORTLET_MODES = "supported.portletmode";

    /** Preferences Performance **/
    static final String ENABLED_PREFERENCES_SESSION_CACHE = "preferences.session.cache.enabled";

    /** XSS */
    static final String XSS_FILTER_REQUEST = "xss.filter.request";
    static final String XSS_FILTER_POST = "xss.filter.post";
    static final String XSS_REGEX = "xss.filter.regexes";
    static final String XSS_FLAGS = "xss.filter.flags";

    /** AutoRefresh */
    static final String AUTO_REFRESH_ENABLED = "autorefresh.enabled";
}
