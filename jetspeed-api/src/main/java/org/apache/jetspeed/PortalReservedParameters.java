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
package org.apache.jetspeed;

/**
 * PortalReservedParameters. The constants here define HTTP request parameters 
 * reserved for use by the Jetspeed Portal.
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public interface PortalReservedParameters
{
    public final static String PORTLET = "portlet";
    public final static String PORTLET_ENTITY = "entity";        
    public final static String PORTLET_MODE = "mode";
    public final static String WINDOW_STATE = "state";
    public final static String PAGE = "page";
    public final static String PIPELINE = "pipeline";
    public final static String DEFAULT_PIPELINE = "jetspeed-pipeline";
    public final static String PORTLET_PIPELINE = "portlet-pipeline";
    public final static String ACTION_PIPELINE = "action-pipeline";
    public final static String LOGIN_PIPELINE = "login-pipeline";
    public final static String CONFIG_PIPELINE = "config-pipeline";
    public final static String DESKTOP_CONFIG_PIPELINE = "dtconfig-pipeline";
    public final static String CONFIG_PIPELINE_NAME = "ConfigurePipeline";
    public final static String DESKTOP_CONFIG_PIPELINE_NAME = "DesktopConfigurePipeline";
    public final static String JETSPEED_CONFIG_PIPELINE_NAME = "JetspeedPipeline";
    
    // Session and Request attribute keys
    public static final String PAGE_ATTRIBUTE = "org.apache.jetspeed.Page";
    public static final String PAGE_EDIT_ACCESS_ATTRIBUTE = "org.apache.jetspeed.decoration.PageEditAccess";
    public static final String SESSION_KEY_SUBJECT = "org.apache.jetspeed.security.subject";
    public static final String SESSION_OPEN_ID_PROVIDER = "org.apache.jetspeed.security.openid.provider";
    public static final String REQUEST_CONTEXT_ATTRIBUTE = "org.apache.jetspeed.request.RequestContext";
    public static final String REQUEST_DIAGNOSTICS_ATTRIBUTE = "org.apache.jetspeed.request.RequestDiagnostics";
    public static final String REQUEST_CONTEXT_OBJECTS = "org.apache.jetspeed.request.RequestContextObjects";
    public static final String FRAGMENT_ATTRIBUTE = "org.apache.jetspeed.Fragment";
    public static final String MAXIMIZED_FRAGMENT_ATTRIBUTE = "org.apache.jetspeed.maximized.Fragment";    
    public static final String MAXIMIZED_LAYOUT_ATTRIBUTE = "org.apache.jetspeed.maximized.Layout";
    public static final String JETSPEED_POWER_TOOL_REQ_ATTRIBUTE = "org.apache.jetspeed.velocity.JetspeedPowerTool";
    public static final String PREFERED_LANGUAGE_ATTRIBUTE = "org.apache.jetspeed.prefered.language";
    public static final String PREFERED_LOCALE_ATTRIBUTE = "org.apache.jetspeed.prefered.locale";
    public static final String PREFERED_CHARACTERENCODING_ATTRIBUTE = "org.apache.jetspeed.prefered.characterencoding";
    public static final String PORTLET_CONTENT_ATTRIBUTE = "org.apache.jetspeed.PortletContent";
    public static final String HEADER_RESOURCE_ATTRIBUTE = "org.apache.jetspeed.headerresource";
    public static final String HEADER_CONFIGURATION_ATTRIBUTE = "org.apache.jetspeed.headerconfiguration";
    public static final String HEADER_NAMED_RESOURCE_ATTRIBUTE = "org.apache.jetspeed.headernamedresource";
    public static final String HEADER_NAMED_RESOURCE_ADDED_FRAGMENTS_ATTRIBUTE = "org.apache.jetspeed.headernamedresourceaddedfragments";
    public static final String HEADER_NAMED_RESOURCE_REGISTRY_ATTRIBUTE = "org.apache.jetspeed.headernamedresourceregistry";
    public static final String PATH_ATTRIBUTE = "org.apache.jetspeed.Path";
    public static final String CONTENT_PATH_ATTRIBUTE = "org.apache.jetspeed.ContentPath";
    public static final String PARAMETER_ALREADY_DECODED_ATTRIBUTE = "org.apache.jetspeed.parameterAlreadyDecoded";
    public static final String RESOLVER_CACHE_ATTR = "org.apache.jetspeed.resolver.cache";
    public static final String PORTLET_DEFINITION_ATTRIBUTE = "org.apache.jetspeed.portlet.definition";
    public static final String PORTLET_WINDOW_ATTRIBUTE = "org.apache.jetspeed.portlet.window";
    public static final String PORTLET_FILTER_MANAGER_ATTRIBUTE = "org.apache.jetspeed.portlet.container.filterManager";
    public static final String PAGE_THEME_ATTRIBUTE = "org.apache.jetspeed.theme";
    public static final String PAGE_THEME_RESPONSIVE = "org.apache.jetspeed.theme.responsive";
    public static final String PAGE_LAYOUT_VIEW = "org.apache.jetspeed.layout.view";
    public static final String PAGE_LAYOUT_MAX = "org.apache.jetspeed.layout.max";
    public static final String PAGE_LAYOUT_HELP = "org.apache.jetspeed.layout.help";
    public static final String PAGE_LAYOUT_EDIT = "org.apache.jetspeed.layout.edit";
    public static final String PAGE_LAYOUT_SOLO = "org.apache.jetspeed.layout.solo";
    public static final String PORTLET_CONTAINER_INVOKER_USE_FORWARD = "org.apache.jetspeed.portlet.container.invoker.useForward";

    /**
     * Setting this as a session attribute will override all themes declared in
     * psml. Sample values are "Simple", "tigris", "jetspeed"
     */
    public static final String PAGE_THEME_OVERRIDE_ATTRIBUTE = "org.apache.jetspeed.theme.override";
    public static final String PORTAL_FILTER_ATTRIBUTE = "org.apache.jetspeed.login.filter.PortalFilter";
    
    // Reserved security attribute names
    public static final String USER_HOMEPAGE_REFRESH_FLAG = "org.apache.jetspeed.profiler.refreshUserHomepage";
    public static final String USER_HOMEPAGE_TEMPLATE_PATH = "org.apache.jetspeed.profiler.userHomepageTemplate";
    
    //
    // Settings for Metadata on jetspeed-portlet.xml
    //
    /**
     * Actions can be marked as non-standard if they don't participate in
     * JSR-168 standard action behavior. By default, actions are supposed
     * to clear the cache of all other portlets on the page.
     * By setting this parameter, we can ignore the standard behavior
     * and not clear the cache on actions. This is useful for portlets
     * which never participate with other portlets.
     */
    public static final String PORTLET_EXTENDED_DESCRIPTOR_NON_STANDARD_ACTION = "nonStandardAction";
    
    /**
     * A portlet can have a specific setting for the timeout duration that the portal will wait
     * before it gives up on rendering the portlet. This value overrides the system setting.
     * The timeout value is in milliseconds 
     */
    public static final String PORTLET_EXTENDED_DESCRIPTOR_RENDER_TIMEOUT = "timeout";
    
    /**
     * A portlet can have a specific setting for the state of out-of-service that the portal will
     * give up on rendering the portlet.
     * The value is 'true' or 'false'.
     */
    public static final String PORTLET_EXTENDED_DESCRIPTOR_OUT_OF_SERVICE = "service.out";
    
    /**
     * A portlet application can have a specific setting for the portlet icon holder, either 'portlet' or 'portal'.
     * If it is set to 'portlet', then the portlet icon images are located in the portlet application resources.
     * If it is set to 'portal', then the portlet icon images are located in the portal application resources.
     * The portlet icon holder application type, either 'portlet' or 'portal'.
     */
    public static final String PORTLET_ICON_HOLDER = "portlet.icon.holder";
    
    /**
     * A portlet can have a specific setting for the context relative base path of portlet icon image resources.
     * The portlet icon resource context relative base path. 
     */
    public static final String PORTLET_ICON_BASE_PATH = "portlet.icon.base.path";
    
    /**
     * A portlet can have a specific setting for the preview image.
     * The portlet preview image url 
     */
    public static final String PORTLET_EXTENDED_DESCRIPTOR_PREVIEW_IMAGE = "portlet.preview.image";
    
    /**
     *  Until version 2.1, Jetspeed merged portal request parameters with portlet specific
     *  parameters, effectively allowing "shared" parameters.
     *  <p>
     *  This is not compliant with the JSR-168 PLT.11, so by default this is now disabled
     *  through global settings in jetspeed.properties:
     *  <pre>
     *    merge.portal.parameters.with.portlet.parameters=false
     *    merge.portal.parameters.before.portlet.parameters=false
     *  </pre>
     *  <p>
     *  To support legacy portlets still relying on the "old" behavior these default global
     *  settings can be overridden by defining these values in the portlet Metadata too.
     *  </p>
     *  <p>
     *  Setting merge.portal.parameters.with.portlet.parameters=true will "restore" the old behavior and
     *  merge the portal parameters with the portlet parameters.
     *  </p>
     */
    public static final String PORTLET_EXTENDED_DESCRIPTOR_MERGE_PORTAL_PARAMETERS_WITH_PORTLET_PARAMETERS = "merge.portal.parameters.with.portlet.parameters";

    /**
     *  Until version 2.1, Jetspeed merged portal request parameters with portlet specific
     *  parameters, effectively allowing "shared" parameters.
     *  <p>
     *  This is not compliant with the JSR-168 PLT.11, so by default this is now disabled
     *  through global settings in jetspeed.properties:
     *  <pre>
     *    merge.portal.parameters.with.portlet.parameters=false
     *    merge.portal.parameters.before.portlet.parameters=false
     *  </pre>
     *  <p>
     *  To support legacy portlets still relying on the "old" behavior these default global
     *  settings can be overridden by defining these values in the portlet Metadata too.
     *  </p>
     *  <p>
     *  In the situation of portal and portlet parameters with the same name, by default
     *  the portlet parameters will be provided first in the values array, but this
     *  can be overridden by setting merge.portal.parameters.before.portlet.parameters=true.
     *  </p>
     */
     public static final String PORTLET_EXTENDED_DESCRIPTOR_MERGE_PORTAL_PARAMETERS_BEFORE_PORTLET_PARAMETERS = "merge.portal.parameters.before.portlet.parameters";
     
     /**
      * PORTAL_SITE_REQUEST_CONTEXT_ATTR_KEY - session portal site context attribute key
      */
     public static final String PORTAL_SITE_SESSION_CONTEXT_ATTR_KEY = "org.apache.jetspeed.portalsite.PortalSiteSessionContext";

     /**
      * PORTAL_SITE_REQUEST_CONTEXT_ATTR_KEY - request portal site context attribute key
      */
     public static final String PORTAL_SITE_REQUEST_CONTEXT_ATTR_KEY = "org.apache.jetspeed.portalsite.PortalSiteRequestContext";

    /**
     * A portlet can have a specific setting for the refresh rate for automatically
     * re-rendering the portlet. If set to -1 or undefined, no refresh will be executed
     * refresh rate is in seconds
     */
    public static final String PORTLET_EXTENDED_DESCRIPTOR_REFRESH_RATE = "refreshRate";

    /**
     * A portlet can have a specific setting for the automatic refresh function that will be called
     * when refreshing the portlet. This setting is dependent on refreshRate.
     */
    public static final String PORTLET_EXTENDED_DESCRIPTOR_REFRESH_FUNCTION = "refreshFunction";

}
