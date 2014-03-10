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
package org.apache.jetspeed.headerresource;

import org.apache.jetspeed.portlet.HeaderPhaseSupportConstants;

import java.util.Map;

/**
 * HeaderResource has tags information to put them into &lt;head&gt; tag.
 * 
 * @author <a href="mailto:shinsuke@yahoo.co.jp">Shinsuke Sugaya</a>
 * @author <a href="mailto:smilek@apache.org">Steve Milek</a>
 * @version $Id: HeaderResource.java 188569 2005-05-13 13:35:18Z weaver $
 */
public interface HeaderResource
{
    // header section types
    public final static String HEADER_TYPE_SCRIPT_BLOCK_START = "script-start";
    public final static int HEADER_TYPE_ID_SCRIPT_BLOCK_START = 1;
    
    public final static String HEADER_TYPE_SCRIPT_BLOCK = "script";
    public final static int HEADER_TYPE_ID_SCRIPT_BLOCK = 2;
    
    public final static String HEADER_TYPE_SCRIPT_BLOCK_END = "script-end";
    public final static int HEADER_TYPE_ID_SCRIPT_BLOCK_END = 3;
    
    public final static String HEADER_TYPE_SCRIPT_TAG = "script-tag";
    public final static int HEADER_TYPE_ID_SCRIPT_TAG = 4;
    
    public final static String HEADER_TYPE_STYLE_BLOCK = "style";
    public final static int HEADER_TYPE_ID_STYLE_BLOCK = 5;
    
    public final static String HEADER_TYPE_LINK_TAG = "link-tag";
    public final static int HEADER_TYPE_ID_LINK_TAG = 6;
    
    public final static String HEADER_TYPE_BASE_TAG = "base-tag";
    public final static int HEADER_TYPE_ID_BASE_TAG = 7;
    
    // header section configuration
    public final static String HEADER_CONFIG_ORDER = "header.order";
    public final static String HEADER_CONFIG_TYPES = "header.types";
    public final static String HEADER_CONFIG_REQUIREDFLAG = "header.requiredflag";
    public final static String HEADER_CONFIG_DOJO = "dojo";
    public final static String HEADER_CONFIG_DESKTOP = "desktop";
    
    public final static String HEADER_INTERNAL_INCLUDED_NAMES = "header.internal.names";  // internal use - not a configuration entry name
    
    // header section predefined names
    public final static String HEADER_SECTION_BASE_TAG = "header.basetag";
    public final static String HEADER_SECTION_NAME_PREFIX_DOJO = "header.dojo.";
    public final static String HEADER_SECTION_DOJO_PARAMETERS = "header.dojo.parameters";
    public final static String HEADER_SECTION_DOJO_PREINIT = "header.dojo.preinit";
    public final static String HEADER_SECTION_DOJO_CONFIG = HeaderPhaseSupportConstants.HEAD_ELEMENT_CONTRIBUTION_MERGE_HINT_KEY_DOJO_CONFIG;
    public final static String HEADER_SECTION_DOJO_INIT = "header.dojo.init";
    public final static String HEADER_SECTION_DOJO_REQUIRES_CORE = HeaderPhaseSupportConstants.HEAD_ELEMENT_CONTRIBUTION_MERGE_HINT_KEY_DOJO_REQUIRES + ".core";
    public final static String HEADER_SECTION_DOJO_MODULES_PATH = "header.dojo.modules.path";
    public final static String HEADER_SECTION_DOJO_REQUIRES_MODULES = HeaderPhaseSupportConstants.HEAD_ELEMENT_CONTRIBUTION_MERGE_HINT_KEY_DOJO_REQUIRES + ".modules";
    public final static String HEADER_SECTION_DOJO_WRITEINCLUDES = "header.dojo.writeincludes";
    public final static String HEADER_SECTION_DOJO_MODULES_NAMESPACE = "header.dojo.modules.namespace";
    public final static String HEADER_SECTION_DOJO_STYLE_BODYEXPAND = "header.dojo.style.bodyexpand";
    public final static String HEADER_SECTION_DOJO_STYLE_BODYEXPAND_NOSCROLL = "header.dojo.style.bodyexpand.noscroll";
    public final static String HEADER_SECTION_DESKTOP_STYLE_LAYOUT = "header.desktop.style.layout";
    public final static String HEADER_SECTION_DESKTOP_INIT = "header.desktop.init";
    
    public final static String HEADER_INTERNAL_JETSPEED_VAR_NAME = "jetspeed";
    public final static String HEADER_INTERNAL_DOJO_CONFIG_JETSPEED_VAR_NAME = "djConfig." + HEADER_INTERNAL_JETSPEED_VAR_NAME;  // internal use - not a configuration entry name
    public final static String HEADER_INTERNAL_DOJO_CONFIG_JETSPEED_VAR_NAME_SCOPE = HEADER_INTERNAL_DOJO_CONFIG_JETSPEED_VAR_NAME + ".";  // internal use - not a configuration entry name
    
    
    // header configuration - dojo
    public final static String HEADER_CONFIG_DOJO_ENABLE = "dojo.enable";
    public final static String HEADER_CONFIG_DOJO_PATH = "dojo.path";
    public final static String HEADER_CONFIG_DOJO_PARAM_ISDEBUG = "dojo.parameter.isDebug";
    public final static String HEADER_CONFIG_DOJO_PARAM_DEBUGALLCOSTS = "dojo.parameter.debugAtAllCosts";
    public final static String HEADER_CONFIG_DOJO_PARAM_PREVENT_BACKBUTTON_FIX = "dojo.parameter.preventBackButtonFix";
    public final static String HEADER_CONFIG_DOJO_PARAMS = "dojo.parameters";
    public final static String HEADER_CONFIG_DOJO_REQUIRES_CORE = "dojo.requires.core";
    public final static String HEADER_CONFIG_DOJO_MODULES_PATH = "dojo.modules.path";
    public final static String HEADER_CONFIG_DOJO_MODULES_NAMESPACE = "dojo.modules.namespace";
    public final static String HEADER_CONFIG_DOJO_REQUIRES_MODULES = "dojo.requires.modules";
    
    // header configuration - desktop
    public final static String HEADER_CONFIG_DESKTOP_LAYOUT_DECORATION_DEFAULT = "decoration.layout";
    public final static String HEADER_CONFIG_DESKTOP_PORTLET_DECORATION_DEFAULT = "decoration.portlet";
    public final static String HEADER_CONFIG_DESKTOP_PAGE_AJAXNAVIGATION = "page.ajaxnavigation";
    public final static String HEADER_CONFIG_DESKTOP_PAGE_ACTION_BUTTON_TOOLTIP = "page.action.button.tooltip";
    public final static String HEADER_CONFIG_DESKTOP_WINDOW_TILING = "window.tiling";
    public final static String HEADER_CONFIG_DESKTOP_WINDOW_HEIGHT_EXPAND = "window.heightexpand";
    public final static String HEADER_CONFIG_DESKTOP_WINDOW_HEIGHT = "window.height";
    public final static String HEADER_CONFIG_DESKTOP_WINDOW_WIDTH = "window.width";
    public final static String HEADER_CONFIG_DESKTOP_WINDOW_ACTION_BUTTON_ORDER = "window.action.button.order";
    public final static String HEADER_CONFIG_DESKTOP_WINDOW_ACTION_NOIMAGE = "window.action.noimage";
    public final static String HEADER_CONFIG_DESKTOP_WINDOW_ACTION_MENU_ORDER = "window.action.menu.order";
    public final static String HEADER_CONFIG_DESKTOP_WINDOW_ACTION_BUTTON_MAX = "window.action.button.maximum";
    public final static String HEADER_CONFIG_DESKTOP_WINDOW_ACTION_BUTTON_TOOLTIP = "window.action.button.tooltip";
    public final static String HEADER_CONFIG_DESKTOP_WINDOW_ICON_ENABLED = "window.icon.enabled";
    public final static String HEADER_CONFIG_DESKTOP_WINDOW_ICON_PATH = "window.icon.path";
    public final static String HEADER_CONFIG_DESKTOP_WINDOW_TITLEBAR_ENABLED = "window.titlebar.enabled";
    public final static String HEADER_CONFIG_DESKTOP_WINDOW_RESIZEBAR_ENABLED = "window.resizebar.enabled";
    
    public final static String DESKTOP_JSON_WINDOW_ACTION_BUTTON_ORDER = "windowActionButtonOrder";
    public final static String DESKTOP_JSON_WINDOW_ACTION_NOIMAGE = "windowActionNoImage";
    public final static String DESKTOP_JSON_WINDOW_ACTION_MENU_ORDER = "windowActionMenuOrder";
    public final static String DESKTOP_JSON_WINDOW_ACTION_BUTTON_MAX = "windowActionButtonMax";
    public final static String DESKTOP_JSON_WINDOW_ACTION_BUTTON_TOOLTIP = "windowActionButtonTooltip";
    public final static String DESKTOP_JSON_WINDOW_ICON_ENABLED = "windowIconEnabled";
    public final static String DESKTOP_JSON_WINDOW_ICON_PATH = "windowIconPath";
    public final static String DESKTOP_JSON_WINDOW_TITLEBAR_ENABLED = "windowTitlebar";
    public final static String DESKTOP_JSON_WINDOW_RESIZEBAR_ENABLED = "windowResizebar";
    
    
    public final static String HEADER_INTERNAL_CONFIG_DESKTOP_WINDOW_ACTION = "desktop.window.action";
    
    public final static String HEADER_DEBUG_REQUIRES = "jetspeed.desktop.debug";
    
    /**
     * Output all content (that has not already been output)
     * 
     * @return content string for inclusion in html &lt;head&gt;
     */
    public String toString();

    /**
     * Output all content (that has not already been output)
     * 
     * @return content string for inclusion in html &lt;head&gt;
     */
    public String getContent();
    
    /**
     * Output all unnamed content (that has not already been output)
     * 
     * @return content string for inclusion in html &lt;head&gt;
     */
    public String getUnnamedContent();
    
    /**
     * Output all getHeaderSections() content (that has not already been output)
     * 
     * @return content string for inclusion in html &lt;head&gt;
     */
    public String getNamedContent();
    
    /**
     * Output the one getHeaderSections() content entry with a key that matches headerName (if it has not already been output)
     * 
     * @return content string for inclusion in html &lt;head&gt;
     */
    public String getNamedContent( String headerName );
    
    /**
     * Output getHeaderSections() content entries with key prefixes that match headerNamePrefix (if it has not already been output)
     * 
     * @return content string for inclusion in html &lt;head&gt;
     */
    public String getNamedContentForPrefix( String headerNamePrefix );
        
    /**
     * Add text argument to the getHeaderSections() content entry with a key that matches addToHeaderName argument
     * 
     */
    public void addHeaderSectionFragment( String addToHeaderName, String text );
    
    /**
     * If no previous call using value of headerFragmentName argument has been added to any getHeaderSections() content entry,
     * add text argument to the getHeaderSections() content entry with a key that matches addToHeaderName argument
     * 
     */
    public void addHeaderSectionFragment( String headerFragmentName, String addToHeaderName, String text );
    
    /**
     * Indicate whether value of headerFragmentName argument has been used to add to any getHeaderSections() content entry
     * 
     * @return true if headerFragmentName argument has been used to add to any getHeaderSections() content entry
     */
    public boolean hasHeaderSectionFragment( String headerFragmentName );
        
    /**
     * Indicate whether value of headerName is an included header section
     * 
     * @return true if headerName argument is an included header section
     */
    public boolean isHeaderSectionIncluded( String headerName );

    /**
     * Get the type of the getHeaderSections() content entry with a key that matches headerName argument
     * 
     * @return type of header section
     */
    public String getHeaderSectionType( String headerName );
    
    /**
     * Set the type of the getHeaderSections() content entry with a key that matches headerName argument
     * to the value of the headerType argument
     */
    public void setHeaderSectionType( String headerName, String headerType  );
    
    /**
     * Get the requiredflag of the getHeaderSections() content entry with a key that matches headerName argument
     * 
     * @return requiredflag for header section
     */
    public String getHeaderSectionRequiredFlag( String headerName );
    
    
    /**
     * Set the requiredflag of the getHeaderSections() content entry with a key that matches headerName argument
     * to the value of the headerReqFlag argument
     */
    public void setHeaderSectionRequiredFlag( String headerName, String headerReqFlag );
    
    /**
     * Access modifiable header configuration settings
     * 
     * @return Map containing modifiable header configuration settings 
     */
    public Map<String, Object> getHeaderDynamicConfiguration();
    
    /**
     * Access complete header configuration settings
     * 
     * @return unmodifiable Map containing complete header configuration settings
     */
    public Map<String, Object> getHeaderConfiguration();
    
    /**
     * Is request for /desktop rather than /portal
     * 
     * @return true if request is for /desktop, false if request is for /portal
     */
    public boolean isDesktop();
    
    /**
     * Portal base url ( e.g. http://localhost:8080/jetspeed )
     * 
     * @return portal base url
     */
    public String getPortalBaseUrl();
    
    /**
     * Portal base url ( e.g. http://localhost:8080/jetspeed )
     * 
     * @return portal base url
     */
    public String getPortalBaseUrl( boolean encode );
    
    /**
     * Portal base url with relativePath argument appended ( e.g. http://localhost:8080/jetspeed/javascript/dojo/ )
     * 
     * @return portal base url with relativePath argument appended
     */
    public String getPortalResourceUrl( String relativePath );
    
    /**
     * Portal base url with relativePath argument appended ( e.g. http://localhost:8080/jetspeed/javascript/dojo/ )
     * 
     * @return portal base url with relativePath argument appended
     */
    public String getPortalResourceUrl( String relativePath, boolean encode );
    
    /**
     * Portal base servlet url ( e.g. http://localhost:8080/jetspeed/desktop/ )
     * 
     * @return portal base servlet url
     */
    public String getPortalUrl();
    
    /**
     * Portal base servlet url ( e.g. http://localhost:8080/jetspeed/desktop/ )
     * 
     * @return portal base servlet url
     */
    public String getPortalUrl( boolean encode );
    
    /**
     * Portal base servlet url with relativePath argument appended ( e.g. http://localhost:8080/jetspeed/desktop/default-page.psml )
     * 
     * @return portal base servlet url with relativePath argument appended
     */
    public String getPortalUrl( String relativePath );
    
    /**
     * Portal base servlet url with relativePath argument appended ( e.g. http://localhost:8080/jetspeed/desktop/default-page.psml )
     * 
     * @return portal base servlet url with relativePath argument appended
     */
    public String getPortalUrl( String relativePath, boolean encode );
    
    
    
    //  dojo - special convenience methods
    
    /**
     * If no previous call using value of dojoRequire argument has been added to any getHeaderSections() content entry,
     * add text argument to getHeaderSections() content entry for dojo core require statements
     * 
     */
    public void dojoAddCoreLibraryRequire( String dojoRequire );
    
    /**
     * Split dojoRequires argument using ';' delimiter and for each resulting dojoRequire value, if no previous call
     * using dojoRequire value has been added to any getHeaderSections() content entry,
     * add text argument to getHeaderSections() content entry for dojo core require statements
     * 
     */
    public void dojoAddCoreLibraryRequires( String dojoRequires );
    
    /**
     * If no previous call using value of dojoRequire argument has been added to any getHeaderSections() content entry,
     * add text argument to getHeaderSections() content entry for dojo library module require statements
     * 
     */
    public void dojoAddModuleLibraryRequire( String dojoRequire );
    
    /**
     * Split dojoRequires argument using ';' delimiter and for each resulting dojoRequire value, if no previous call
     * using dojoRequire value has been added to any getHeaderSections() content entry,
     * add text argument to getHeaderSections() content entry for dojo library module require statements
     * 
     */
    public void dojoAddModuleLibraryRequires( String dojoRequires );
    
    /**
     * Assure that header section name for dojo body expand style is included
     * 
     */
    public void dojoAddBodyExpandStyle( boolean omitWindowScrollbars );
    
    /**
     * Enable dojo by setting appropriate modifiable header configuration setting
     * 
     */
    public void dojoEnable();
    
    
    
    
    /**
     * Add tag information to this instance.
     * 
     * For example, if you want to add the following tag into &lt;head&gt;,
     * 
     * &lt;foo a="1" b="2"&gt;FOO FOO&lt;/foo&gt;
     * 
     * Java code is:
     * 
     * HashMap map=new HashMap();
     * map.put("a","1");
     * map.put("b","2");
     * headerResouce.addHeaderInfo("foo",map,"FOO FOO");
     * 
     * @param elementName Tag's name
     * @param attributes Tag's attributes
     * @param text Tag's content
     */
    public void addHeaderInfo(String elementName, Map<String, String> attributes, String text);

    /**
     * Add text as-is to this instance.
     * 
     * @param text content
     */
    public void addHeaderInfo(String text);
    
    /**
     * Convenient method to add &lt;script&gt; tag with defer option.
     * 
     * @param path Javascript file path
     * @param defer defer attributes for &lt;script&gt; tag.
     */
    public void addJavaScript(String path, boolean defer);

    /**
     * Convenient method to add &lt;script&gt; tag.
     * 
     * @param path Javascript file path
     */
    public void addJavaScript(String path);

    /**
     * Convenient method to add &lt;link&gt; tag.
     * 
     * @param path CSS file path
     */
    public void addStyleSheet(String path);
}
