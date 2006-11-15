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


/**
 * jetspeed is the root variable of (almost all) our public symbols.
 */

dojo.provide("jetspeed.desktop.core");

// ... testing

// ... jetspeed base objects
if ( ! window.jetspeed )
    jetspeed = {} ;
if ( ! jetspeed.om )
    jetspeed.om = {} ;
if ( ! jetspeed.url )
    jetspeed.url = {} ;
if ( ! jetspeed.ui )
    jetspeed.ui = {} ;
if ( ! jetspeed.ui.widget )
    jetspeed.ui.widget = {} ;

// ... jetspeed version
jetspeed.version = 
{
    major: 2, minor: 1, patch: 0, flag: "dev",
    revision: "",
    toString: function() 
    {
        with (jetspeed.version) 
        {
            return major + "." + minor + "." + patch + flag + " (" + revision + ")";
        }
    }
};

// ... jetspeed.id
jetspeed.id =
{
    PAGE: "jetspeedPage",
    DESKTOP: "jetspeedDesktop",
    TASKBAR: "jetspeedTaskbar",
    COLUMNS: "jetspeedColumns",
    SELECTOR: "jetspeedSelector",
    PORTLET_STYLE_CLASS: "portlet",
    PORTLET_WINDOW_STYLE_CLASS: "dojoFloatingPane",
    PORTLET_WINDOW_GHOST_STYLE_CLASS: "ghostPane",
    PORTLET_WINDOW_ID_PREFIX: "portletWindow_",

    PORTLET_PROP_WIDGET_ID: "widgetId",
    PORTLET_PROP_CONTENT_RETRIEVER: "contentRetriever",
    PORTLET_PROP_DESKTOP_EXTENDED: "jsdesktop",
    PORTLET_PROP_WINDOW_POSITION_STATIC: "windowPositionStatic",
    PORTLET_PROP_WINDOW_HEIGHT_TO_FIT: "windowHeightToFit",
    PORTLET_PROP_WINDOW_THEME: "windowTheme",
    PORTLET_PROP_WINDOW_TITLE: "title",
    PORTLET_PROP_WINDOW_ICON: "windowIcon",
    PORTLET_PROP_WIDTH: "width",
    PORTLET_PROP_HEIGHT: "height",
    PORTLET_PROP_LEFT: "left",
    PORTLET_PROP_TOP: "top",
    PORTLET_PROP_COLUMN: "column",
    PORTLET_PROP_ROW: "row",
    PORTLET_PROP_EXCLUDE_PCONTENT: "excludePContent",
    PORTLET_PROP_WINDOW_STATE: "windowState",

    PORTLET_PROP_DESKTOP_EXTENDED_STATICPOS: "staticpos",
    PORTLET_PROP_DESKTOP_EXTENDED_FITHEIGHT: "fitheight",
    PORTLET_PROP_DESKTOP_EXTENDED_PROP_SEPARATOR: "=",
    PORTLET_PROP_DESKTOP_EXTENDED_PAIR_SEPARATOR: ";",

    // these constants for action names are defined because they have special meaning to desktop (ie. this is not a list of all supported actions)
    PORTLET_ACTION_NAME_MENU: "menu",
    PORTLET_ACTION_NAME_MINIMIZE: "minimized",
    PORTLET_ACTION_NAME_MAXIMIZE: "maximized",
    PORTLET_ACTION_NAME_RESTORE: "normal",
    PORTLET_ACTION_NAME_PRINT: "print",
    PORTLET_ACTION_NAME_CLOSE: "close",

    PORTLET_ACTION_NAME_DESKTOP_TILE: "tile",
    PORTLET_ACTION_NAME_DESKTOP_UNTILE: "untile",
    PORTLET_ACTION_NAME_DESKTOP_HEIGHT_EXPAND: "heightexpand",
    PORTLET_ACTION_NAME_DESKTOP_HEIGHT_NORMAL: "heightnormal",

    PORTLET_ACTION_TYPE_MODE: "mode",
    PORTLET_ACTION_TYPE_STATE: "state",

    MENU_WIDGET_ID_PREFIX: "jetspeed-menu-"
};

// ... jetspeed desktop preferences - defaults
jetspeed.prefs = 
{
    windowTiling: true,                 // false indicates no-columns, free-floating windows
    windowHeightExpand: false,          // only meaningful when windowTiling == true
    
    windowWidth: null,                  // last-ditch defaults for these defined in initializeDesktop
    windowHeight: null,

    desktopTheme: null,                 // do not access directly - use getDesktopTheme()
    desktopThemeRootUrl: null,          // do not access directly - use getDesktopThemeRootUrl()
    getDesktopTheme: function()
    {
        if ( jetspeed.prefs.desktopTheme == null && djConfig.jetspeed != null )
            jetspeed.prefs.desktopTheme = djConfig.jetspeed.desktopTheme;
        return jetspeed.prefs.desktopTheme;
    },
    getDesktopThemeRootUrl: function()
    {
        if ( jetspeed.prefs.desktopThemeRootUrl == null && djConfig.jetspeed != null )
            jetspeed.prefs.desktopThemeRootUrl = djConfig.jetspeed.desktopThemeRootUrl;
        return jetspeed.prefs.desktopThemeRootUrl;
    },

    portletSelectorWindowTitle: "Portlet Selector",
    portletSelectorWindowIcon: "text-x-script.png",
    portletSelectorBounds: { x: 20, y: 20, width: 400, height: 600 },

    windowActionButtonOrder: [ jetspeed.id.PORTLET_ACTION_NAME_MENU, "edit", "view", "help", jetspeed.id.PORTLET_ACTION_NAME_MINIMIZE, jetspeed.id.PORTLET_ACTION_NAME_MAXIMIZE, jetspeed.id.PORTLET_ACTION_NAME_RESTORE ],
    windowActionNotPortlet: [ jetspeed.id.PORTLET_ACTION_NAME_MENU, jetspeed.id.PORTLET_ACTION_NAME_MINIMIZE, jetspeed.id.PORTLET_ACTION_NAME_MAXIMIZE, jetspeed.id.PORTLET_ACTION_NAME_RESTORE ],
    windowActionButtonMax: 5,
    windowActionButtonHide: false,
    windowActionMenuOrder: [ jetspeed.id.PORTLET_ACTION_NAME_DESKTOP_HEIGHT_EXPAND, jetspeed.id.PORTLET_ACTION_NAME_DESKTOP_HEIGHT_NORMAL, jetspeed.id.PORTLET_ACTION_NAME_DESKTOP_TILE, jetspeed.id.PORTLET_ACTION_NAME_DESKTOP_UNTILE ],

    windowThemesAllowed: [ "tigris", "blueocean" ],
    windowTheme: "tigris",

    getWindowThemeConfig: function( windowtheme )
    {
        if ( jetspeed.prefs.windowThemeConfig == null || windowtheme == null )
            return null;
        return jetspeed.prefs.windowThemeConfig[ windowtheme ];
    }
};

// ... jetspeed debug options
jetspeed.debug =
{
    pageLoad: true,
    retrievePsml: false,
    setPortletContent: false,
    doRenderDoAction: false,
    postParseAnnotateHtml: false,
    confirmOnSubmit: false,
    createWindow: false,
    initializeWindowState: false,
    submitChangedWindowState: false,

    windowThemeRandom: true,

    debugContainerId: ( djConfig.debugContainerId ? djConfig.debugContainerId : dojo.hostenv.defaultDebugContainerId )
};
jetspeed.debugInPortletWindow = true;
//jetspeed.debugPortletEntityIdFilter = [ "dp-7", "dp-3", "dp-12", "dp-18" ]; // NOTE: uncomment causes only the listed portlets to be loaded; all others are ignored; for testing
//portlets: [dp-3 LocaleSelector, dp-16 RoleSecurityTest, dp-17 UserInfoTest, dp-22 ForgottenPasswordPortlet, dp-18 BookmarkPortlet, dp-23 UserRegistrationPortlet, dp-7 PickANumberPortlet, dp-9 IFramePortlet, dp-12 LoginPortlet]
jetspeed.debugPortletWindowIcons = [ "text-x-generic.png", "text-html.png", "application-x-executable.png" ];
//jetspeed.debugContentDumpIds = [ ".*" ];                        // dump all responses
//jetspeed.debugContentDumpIds = [ "getmenus", "getmenu-.*" ];    // dump getmenus response and all getmenu responses
//jetspeed.debugContentDumpIds = [ "page-.*" ];                   // dump page psml response
//jetspeed.debugContentDumpIds = [ "addportlet" ];                // dump portlet selector response
//jetspeed.debugContentDumpIds = [ "P-10acd169a40-10001", "P-10acd169a40-10000" ];
jetspeed.debugContentDumpIds = [ "notifyGridSelect", "P-10acd169a40-10001", "reports-select" ];

// ... load page /portlets
jetspeed.page = null ;
jetspeed.initializeDesktop = function()
{
    jetspeed.url.pathInitialize();
    if ( djConfig.jetspeed != null )
    {
        for ( var prefKey in djConfig.jetspeed )
        {
            var prefOverrideVal = djConfig.jetspeed[ prefKey ];
            if ( prefOverrideVal != null )
            {
                if ( jetspeed.debug[ prefKey ] != null )
                    jetspeed.debug[ prefKey ] = prefOverrideVal;
                else
                    jetspeed.prefs[ prefKey ] = prefOverrideVal;
            }
        }
        if ( jetspeed.prefs.windowWidth == null || isNaN( jetspeed.prefs.windowWidth ) )
            jetspeed.prefs.windowWidth = "280";
        if ( jetspeed.prefs.windowHeight == null || isNaN( jetspeed.prefs.windowHeight ) )
            jetspeed.prefs.windowHeight = "200";
        
        var windowActionDesktop = {};
        windowActionDesktop[ jetspeed.id.PORTLET_ACTION_NAME_DESKTOP_HEIGHT_EXPAND ] = true;
        windowActionDesktop[ jetspeed.id.PORTLET_ACTION_NAME_DESKTOP_HEIGHT_NORMAL ] = true;
        windowActionDesktop[ jetspeed.id.PORTLET_ACTION_NAME_DESKTOP_TILE ] = true;
        windowActionDesktop[ jetspeed.id.PORTLET_ACTION_NAME_DESKTOP_UNTILE ] = true;
        jetspeed.prefs.windowActionDesktop = windowActionDesktop;
    }
    dojo.html.insertCssFile( jetspeed.ui.getDefaultFloatingPaneTemplateCss(), document, true );

    if ( jetspeed.prefs.windowThemesAllowed == null || jetspeed.prefs.windowThemesAllowed.length == 0 )
    {
        if ( jetspeed.prefs.windowTheme != null )
            jetspeed.prefs.windowThemesAllowed = [ jetspeed.prefs.windowTheme ];
    }
    else if ( jetspeed.prefs.windowTheme == null )
    {
        jetspeed.prefs.windowTheme = jetspeed.prefs.windowThemesAllowed[0];
    }
    if ( jetspeed.prefs.windowTheme == null || jetspeed.prefs.windowThemesAllowed == null )
    {
        dojo.raise( "Cannot load page because there are no defined jetspeed window themes" );
        return;
    }

    if ( jetspeed.prefs.windowActionNoImage != null )
    {
        var noImageMap = {};
        for ( var i = 0 ; i < jetspeed.prefs.windowActionNoImage.length; i++ )
        {
            noImageMap[ jetspeed.prefs.windowActionNoImage[ i ] ] = true;
        }
        jetspeed.prefs.windowActionNoImage = noImageMap;
    }

    jetspeed.prefs.windowThemeConfig = {};
    for ( var i = 0 ; i < jetspeed.prefs.windowThemesAllowed.length ; i++ )
    {
        jetspeed.loadWindowThemeConfig( jetspeed.prefs.windowThemesAllowed[ i ] );
    }
    
    jetspeed.loadPage();
};
jetspeed.loadPage = function()
{
    jetspeed.loadDebugWindow();
    jetspeed.page = new jetspeed.om.Page();
    jetspeed.page.retrievePsml();
};
jetspeed.updatePage = function()
{
    var previousPage = jetspeed.page;
    if ( previousPage != null )
    {
        jetspeed.page = new jetspeed.om.Page();
        jetspeed.page.retrievePsml( jetspeed.om.PageContentListenerUpdate( previousPage ) );
    }
};

jetspeed.loadDebugWindow = function()
{
    if ( djConfig.isDebug && jetspeed.debugInPortletWindow && dojo.byId( jetspeed.debug.debugContainerId ) == null )
    {
        var windowParams = {};
        var debugWindowWidgetId = jetspeed.id.PORTLET_WINDOW_ID_PREFIX + "dojo-debug"
        windowParams[ jetspeed.id.PORTLET_PROP_WINDOW_POSITION_STATIC ] = false;
        windowParams[ jetspeed.id.PORTLET_PROP_WINDOW_HEIGHT_TO_FIT ] = false;
        windowParams[ jetspeed.id.PORTLET_PROP_WINDOW_THEME ] = "tigris";
        windowParams[ jetspeed.id.PORTLET_PROP_WINDOW_TITLE ] = "Dojo Debug";
        windowParams[ jetspeed.id.PORTLET_PROP_WINDOW_ICON ] = "text-x-script.png";
        windowParams[ jetspeed.id.PORTLET_PROP_WIDGET_ID ] = debugWindowWidgetId;
        windowParams[ jetspeed.id.PORTLET_PROP_WIDTH ] = "400";
        windowParams[ jetspeed.id.PORTLET_PROP_HEIGHT ] = "400";
        windowParams[ jetspeed.id.PORTLET_PROP_LEFT ] = "320";
        windowParams[ jetspeed.id.PORTLET_PROP_TOP ] = "0";
        windowParams[ jetspeed.id.PORTLET_PROP_EXCLUDE_PCONTENT ] = false;
        windowParams[ jetspeed.id.PORTLET_PROP_CONTENT_RETRIEVER ] = new jetspeed.om.DojoDebugContentRetriever();
        windowParams[ jetspeed.id.PORTLET_PROP_WINDOW_STATE ] = "minimized" ;
        var pwWidgetParams = jetspeed.widget.PortletWindow.prototype.staticDefineAsAltInitParameters( null, windowParams );
        jetspeed.ui.createPortletWindow( pwWidgetParams );
        pwWidgetParams.retrieveContent( null, null );
        var debugWindowWidget = dojo.widget.byId( debugWindowWidgetId );
        var debugContainer = dojo.byId( jetspeed.debug.debugContainerId );

        dojo.event.connect( "after", dojo.hostenv, "println", debugWindowWidget, "contentChanged" );
    }
};

// ... jetspeed.doRender
jetspeed.doRender = function( bindArgs, portletEntityId )
{
    if ( ! bindArgs )
    {
        bindArgs = {};
    }
    else if ( ( typeof bindArgs == "string" || bindArgs instanceof String ) )
    {
        bindArgs = { url: bindArgs };
    }
    var targetPortlet = jetspeed.page.getPortlet( portletEntityId );
    if ( targetPortlet )
    {
        if ( jetspeed.debug.doRenderDoAction )
            dojo.debug( "doRender [" + portletEntityId + "] url: " + bindArgs.url );
        targetPortlet.retrieveContent( null, bindArgs );
    }
};

// ... jetspeed.doRenderAll
jetspeed.doRenderAll = function( url, windowArray, isPageLoad )
{
    var debugMsg = jetspeed.debug.doRenderDoAction;
    var debugPageLoad = jetspeed.debug.pageLoad && isPageLoad;
    if ( ! windowArray )
        windowArray = jetspeed.page.getPortletArray();
    var renderMsg = "";
    var suppressGetActions = true;
    for ( var i = 0; i < windowArray.length; i++ )
    {
        var renderObj = windowArray[i];
        if ( (debugMsg || debugPageLoad) )
        {
            if ( i > 0 ) renderMsg = renderMsg + ", ";
            if ( renderObj.entityId )
            {
                renderMsg = renderMsg + renderObj.entityId;
                if ( debugPageLoad && renderObj.getProperty( jetspeed.id.PORTLET_PROP_WINDOW_TITLE ) )
                    renderMsg = renderMsg + " " + renderObj.getProperty( jetspeed.id.PORTLET_PROP_WINDOW_TITLE );
            }
            else
            {
                var widgetId = null;
                if ( renderObj.getProperty != null )
                    widgetId = renderObj.getProperty( jetspeed.id.PORTLET_PROP_WIDGET_ID );
                if ( ! widgetId )
                    widgetId = renderObj.widgetId;
                if ( ! widgetId )
                    widgetId = renderObj.toString();
                renderMsg = renderMsg + widgetId;
            }
        }
        renderObj.retrieveContent( null, { url: url }, suppressGetActions );
    }
    if ( debugMsg )
        dojo.debug( "doRenderAll [" + renderMsg + "] url: " + url );
    else if ( debugPageLoad )   // this.getPsmlUrl() ;
        dojo.debug( "doRenderAll page-url: " + jetspeed.page.getPsmlUrl() + " portlets: [" + renderMsg + "]" + ( url ? ( " url: " + url ) : "" ) );
};

// ... jetspeed.doAction
jetspeed.doAction = function( bindArgs, portletEntityId )
{
    if ( ! bindArgs )
    {
        bindArgs = {};
    }
    else if ( ( typeof bindArgs == "string" || bindArgs instanceof String ) )
    {
        bindArgs = { url: bindArgs };
    }
    var targetPortlet = jetspeed.page.getPortlet( portletEntityId );
    if ( targetPortlet )
    {
        if ( jetspeed.debug.doRenderDoAction )
        {
            if ( ! bindArgs.formNode )
                dojo.debug( "doAction [" + portletEntityId + "] url: " + bindArgs.url + " form: null" );
            else
                dojo.debug( "doAction [" + portletEntityId + "] url: " + bindArgs.url + " form: " + jetspeed.debugDumpForm( bindArgs.formNode ) );
        }
        targetPortlet.retrieveContent( new jetspeed.om.PortletActionContentListener(), bindArgs );
    }
};

jetspeed.doNothingNav = function()
{   // replacing form actions with javascript: doNothingNav() is 
    // useful for preventing form submission in cases like: <a onclick="form.submit(); return false;" >
    // JSF h:commandLink uses the above anchor onclick practice
    false;
};

jetspeed.loadWindowThemeConfig = function( windowtheme )
{
    // setup default window theme config
    var windowThemeConfig = {};
    jetspeed.prefs.windowThemeConfig[ windowtheme ] = windowThemeConfig;
    windowThemeConfig.windowActionButtonOrder = jetspeed.prefs.windowActionButtonOrder;
    windowThemeConfig.windowActionNotPortlet = jetspeed.prefs.windowActionNotPortlet;
    windowThemeConfig.windowActionButtonMax = jetspeed.prefs.windowActionButtonMax;
    windowThemeConfig.windowActionButtonHide = jetspeed.prefs.windowActionButtonHide;
    windowThemeConfig.windowActionMenuOrder = jetspeed.prefs.windowActionMenuOrder;
    windowThemeConfig.windowActionNoImage = jetspeed.prefs.windowActionNoImage;

    // load window theme config
    var windowThemeConfigUri = jetspeed.url.basePortalWindowThemeUrl( windowtheme ) + "/" + windowtheme + ".js";
    dojo.hostenv.loadUri( windowThemeConfigUri, function(hash) {
				for ( var j in hash )
                {
                    windowThemeConfig[ j ] = hash[j];
                }
                if ( windowThemeConfig.windowActionNoImage != null )
                {
                    var noImageMap = {};
                    for ( var i = 0 ; i < windowThemeConfig.windowActionNoImage.length; i++ )
                    {
                        noImageMap[ windowThemeConfig.windowActionNoImage[ i ] ] = true;
                    }
                    windowThemeConfig.windowActionNoImage = noImageMap;
                }
			});
};

// jetspeed.purifyIdentifier
jetspeed.purifyIdentifier = function( src, replaceCh, camel )
{
    if ( src == null ) return src;
    var limit = src.length;
    if ( limit == 0 ) return src;
    if ( replaceCh == null )
        replaceCh = "_";
    var regEx = new RegExp( "[^a-z_0-9A-Z]", "g" );
    var chCode = src.charCodeAt( 0 );
    var buff = null;
    if ( ( chCode >= 65 && chCode <= 90 ) || chCode == 95 || ( chCode >= 97 && chCode <= 122 ) )
        buff = src.charAt( 0 );
    else
        buff = replaceCh;
    var hiCamel = false, loCamel = false;
    if ( camel != null )
    {
        camel = camel.toLowerCase();
        hiCamel = ( camel == "hi" ? true : false );
        loCamel = ( camel == "lo" ? true : false );
    }
    if ( limit > 1 )
    {
        if ( hiCamel || loCamel )
        {
            upNext = false;
            for ( var i = 1 ; i < limit ; i++ )
            {
                chCode = src.charCodeAt( i );
                if ( ( chCode >= 65 && chCode <= 90 ) || chCode == 95 || ( chCode >= 97 && chCode <= 122 ) || ( chCode >= 48 && chCode <= 57 ) )
                {
                    if ( upNext && ( chCode >= 97 && chCode <= 122 ) )
                        buff += String.fromCharCode( chCode - 32 );
                    else
                        buff += src.charAt( i );
                    upNext = false;
                }
                else
                {
                    upNext = true;
                    buff += replaceCh;
                }
            }
        }
        else
        {
            buff += src.substring( 1 ).replace( regEx, replaceCh );
        }
    }
    if ( hiCamel )
    {
        chCode = buff.charCodeAt( 0 );
        if ( chCode >= 97 && chCode <= 122 )
            buff = String.fromCharCode( chCode - 32 ) + buff.substring( 1 );
    }   
    return buff;
};

// ... jetspeed.notifyRetrieveAllMenusFinished
jetspeed.notifyRetrieveAllMenusFinished = function()
{   // dojo.event.connect to this or add to your page content, one of the functions that it invokes ( doMenuBuildAll() or doMenuBuild() )
    jetspeed.pageNavigateSuppress = true;

    if ( dojo.lang.isFunction( window.doMenuBuildAll ) )
    {   
        window.doMenuBuildAll();
    }
    
    var menuNames = jetspeed.page.getMenuNames();
    for ( var i = 0 ; i < menuNames.length; i++ )
    {
        var menuNm = menuNames[i];
        var menuWidget = dojo.widget.byId( jetspeed.id.MENU_WIDGET_ID_PREFIX + menuNm );
        if ( menuWidget )
        {
            menuWidget.createJetspeedMenu( jetspeed.page.getMenu( menuNm ) );
        }
    }
    
    jetspeed.pageNavigateSuppress = false;
};

// ... jetspeed.notifyRetrieveMenuFinished
jetspeed.notifyRetrieveMenuFinished = function( /* jetspeed.om.Menu */ menuObj )
{   // dojo.event.connect to this or add to your page content the function that it invokes ( doMenuBuild() )
    if ( dojo.lang.isFunction( window.doMenuBuild ) )
    {
        window.doMenuBuild( menuObj );
    }
};

jetspeed.menuNavClickWidget = function( /* Tab widget || Tab widgetId */ tabWidget, /* int || String */ selectedTab )
{
    dojo.debug( "jetspeed.menuNavClick" );
    if ( ! tabWidget ) return;
    if ( dojo.lang.isString( tabWidget ) )
    {
        var tabWidgetId = tabWidget;
        tabWidget = dojo.widget.byId( tabWidgetId );
        if ( ! tabWidget )
            dojo.raise( "menuNavClick could not find tab widget for " + tabWidgetId );
    }
    if ( tabWidget )
    {
        var jetspeedMenuName = tabWidget.jetspeedmenuname;
        if ( ! jetspeedMenuName && tabWidget.extraArgs )
            jetspeedMenuName = tabWidget.extraArgs.jetspeedmenuname;
        if ( ! jetspeedMenuName )
            dojo.raise( "menuNavClick tab widget [" + tabWidget.widgetId + "] does not define jetspeedMenuName" );
        var menuObj = jetspeed.page.getMenu( jetspeedMenuName );
        if ( ! menuObj )
            dojo.raise( "menuNavClick Menu lookup for tab widget [" + tabWidget.widgetId + "] failed: " + jetspeedMenuName );
        var menuOpt = menuObj.getOptionByIndex( selectedTab );
        
        jetspeed.menuNavClick( menuOpt );
    }
};

jetspeed.pageNavigateSuppress = false;
jetspeed.pageNavigate = function( navUrl, navTarget )
{
    if ( ! navUrl || jetspeed.pageNavigateSuppress ) return;

    if ( jetspeed.page && jetspeed.page.equalsPageUrl( navUrl ) )
        return ;

    navUrl = jetspeed.page.makePageUrl( navUrl );
    
    if ( navTarget == "top" )
        top.location.href = navUrl;
    else if ( navTarget == "parent" )
        parent.location.href = navUrl;
    else
        window.location.href = navUrl;  // BOZO:NOW: popups
};

jetspeed.loadPortletSelector = function()
{
    var windowParams = {};
    windowParams[ jetspeed.id.PORTLET_PROP_WINDOW_POSITION_STATIC ] = false;
    windowParams[ jetspeed.id.PORTLET_PROP_WINDOW_HEIGHT_TO_FIT ] = false;
    windowParams[ jetspeed.id.PORTLET_PROP_WINDOW_THEME ] = jetspeed.page.getWindowThemeDefault();
    windowParams[ jetspeed.id.PORTLET_PROP_WINDOW_TITLE ] = jetspeed.prefs.portletSelectorWindowTitle;
    windowParams[ jetspeed.id.PORTLET_PROP_WINDOW_ICON ] = jetspeed.prefs.portletSelectorWindowIcon;
    windowParams[ jetspeed.id.PORTLET_PROP_WIDGET_ID ] = jetspeed.id.PORTLET_WINDOW_ID_PREFIX + jetspeed.id.SELECTOR;
    windowParams[ jetspeed.id.PORTLET_PROP_WIDTH ] = jetspeed.prefs.portletSelectorBounds.width;
    windowParams[ jetspeed.id.PORTLET_PROP_HEIGHT ] = jetspeed.prefs.portletSelectorBounds.height;
    windowParams[ jetspeed.id.PORTLET_PROP_LEFT ] = jetspeed.prefs.portletSelectorBounds.x;
    windowParams[ jetspeed.id.PORTLET_PROP_TOP ] = jetspeed.prefs.portletSelectorBounds.y;
    windowParams[ jetspeed.id.PORTLET_PROP_EXCLUDE_PCONTENT ] = true;
    windowParams[ jetspeed.id.PORTLET_PROP_CONTENT_RETRIEVER ] = new jetspeed.om.PortletSelectorContentRetriever();
    var pwWidgetParams = jetspeed.widget.PortletWindow.prototype.staticDefineAsAltInitParameters( null, windowParams );
    jetspeed.ui.createPortletWindow( pwWidgetParams );
    pwWidgetParams.retrieveContent( null, null );
    jetspeed.getPortletDefinitions();
};

jetspeed.getPortletDefinitions = function()
{
    var contentListener = new jetspeed.om.PortletSelectorAjaxApiContentListener();
    var queryString = "?action=getportlets";
    var getPortletsUrl = jetspeed.url.basePortalUrl() + jetspeed.url.path.AJAX_API + queryString ;
    var mimetype = "text/xml";
    var ajaxApiContext = new jetspeed.om.Id( "getportlets", { } );
    jetspeed.url.retrieveContent( { url: getPortletsUrl, mimetype: mimetype }, contentListener, ajaxApiContext, jetspeed.debugContentDumpIds );
};

jetspeed.searchForPortletDefinitions = function(filter, catPortlets)
{
    var contentListener = new jetspeed.om.PortletSelectorSearchContentListener(catPortlets);
    var queryString = "?action=getportlets&filter=" + filter;
    var getPortletsUrl = jetspeed.url.basePortalUrl() + jetspeed.url.path.AJAX_API + queryString ;
    var mimetype = "text/xml";
    var ajaxApiContext = new jetspeed.om.Id( "getportlets", { } );
    jetspeed.url.retrieveContent( { url: getPortletsUrl, mimetype: mimetype }, contentListener, ajaxApiContext, jetspeed.debugContentDumpIds );
};

jetspeed.getActionsForPortlet = function( /* String */ portletEntityId, contentListener )
{
    if ( portletEntityId == null ) return;
    jetspeed.getActionsForPortlets( [ portletEntityId ], contentListener );
};
jetspeed.getActionsForPortlets = function( /* Array */ portletEntityIds, contentListener )
{
    if ( contentListener == null )
        contentListener = new jetspeed.om.PortletActionsContentListener();
    var queryString = "?action=getactions";
    if ( portletEntityIds == null )
        portletEntityIds = jetspeed.page.getPortletIds();
    for ( var i = 0 ; i < portletEntityIds.length ; i++ )
    {
        queryString += "&id=" + portletEntityIds[i];
    }
    var getActionsUrl = jetspeed.url.basePortalUrl() + jetspeed.url.path.AJAX_API + queryString ;
    var mimetype = "text/xml";
    var ajaxApiContext = new jetspeed.om.Id( "getactions", { } );
    jetspeed.url.retrieveContent( { url: getActionsUrl, mimetype: mimetype }, contentListener, ajaxApiContext, jetspeed.debugContentDumpIds );
};
jetspeed.changeActionForPortlet = function( /* String */ portletEntityId, /* String */ changeActionState, /* String */ changeActionMode, contentListener )
{
    if ( portletEntityId == null ) return;
    if ( contentListener == null )
        contentListener = new jetspeed.om.PortletChangeActionContentListener( portletEntityId );
    var queryString = "?action=window&id=" + ( portletEntityId != null ? portletEntityId : "" );
    if ( changeActionState != null )
        queryString += "&state=" + changeActionState;
    if ( changeActionMode != null )
        queryString += "&mode=" + changeActionMode;
    var changeActionUrl = jetspeed.url.basePortalUrl() + jetspeed.url.path.AJAX_API + queryString ;
    var mimetype = "text/xml";
    var ajaxApiContext = new jetspeed.om.Id( "changeaction", { } );
    jetspeed.url.retrieveContent( { url: changeActionUrl, mimetype: mimetype }, contentListener, ajaxApiContext, jetspeed.debugContentDumpIds );
};

jetspeed.addNewPortletDefinition = function( /* jetspeed.om.PortletDef */ portletDef, windowWidgetId, /* String */ psmlUrl )
{
    var addToCurrentPage = true;
    if ( psmlUrl != null )
        addToCurrentPage = false;
    var contentListener = new jetspeed.om.PortletAddAjaxApiCallbackContentListener( portletDef, windowWidgetId, addToCurrentPage );
    var queryString = "?action=add&id=" + escape( portletDef.getPortletName() );
    var addPortletUrl = null;
    if ( psmlUrl != null )
        addPortletUrl = psmlUrl + queryString;   //  psmlUrl example: http://localhost:8080/jetspeed/ajaxapi/google-maps.psml
    else
        addPortletUrl = jetspeed.page.getPsmlUrl() + queryString;
    var mimetype = "text/xml";
    var ajaxApiContext = new jetspeed.om.Id( "addportlet", { } );
    jetspeed.url.retrieveContent( { url: addPortletUrl, mimetype: mimetype }, contentListener, ajaxApiContext, jetspeed.debugContentDumpIds );
};

jetspeed.debugDumpForm = function( formNode )
{
    if ( ! formNode ) return null ;
    var formDump = formNode.toString() ;
    if ( formNode.name )
        formDump += " name=" + formNode.name;
    if ( formNode.id )
        formDump += " id=" + formNode.id;
    var queryString = dojo.io.encodeForm( formNode );
    formDump += " data=" + queryString; 
    return formDump;
};


// ... jetspeed.url
jetspeed.url.path =
{
    SERVER: null,     //   http://localhost:8080
    JETSPEED: null,   //   /jetspeed
    AJAX_API: null,   //   /jetspeed/ajaxapi
    DESKTOP: null,    //   /jetspeed/desktop
    PORTLET: null,    //   /jetspeed/portlet
    initialized: false
};

jetspeed.url.pathInitialize = function( force )
{
    if ( ! force && jetspeed.url.path.initialized ) return;
    var baseTag = null;
    var baseTags = document.getElementsByTagName( "base" );
    if ( baseTags && baseTags.length == 1 )
        baseTag = new dojo.uri.Uri( baseTags[0].href );
    else
        baseTag = new dojo.uri.Uri( window.location.href );

    var basePath = baseTag.path;
    
    var sepPos = -1;
    for( var startPos =1 ; sepPos <= startPos ; startPos++ )
    {
        sepPos = basePath.indexOf( "/", startPos );
        if ( sepPos == -1 )
            break;
    }

    var serverUri = "";
    if ( baseTag.scheme != null) { serverUri += baseTag.scheme + ":"; }
    if ( baseTag.authority != null) { serverUri += "//" + baseTag.authority; }

    var jetspeedPath = null;
    if ( sepPos == -1 )
        jetspeedPath = basePath;
    else
        jetspeedPath = basePath.substring( 0, sepPos );
    
    //dojo.debug( "pathInitialize  new-JETSPEED=" + jetspeedPath + " orig-JETSPEED=" + jetspeed.url.path.JETSPEED + " new-SERVER=" + serverUri + " orig-SERVER=" + document.location.protocol + "//" + document.location.host );
    
    jetspeed.url.path.JETSPEED = jetspeedPath;
    jetspeed.url.path.SERVER = serverUri;
    jetspeed.url.path.AJAX_API = jetspeed.url.path.JETSPEED + "/ajaxapi";
    jetspeed.url.path.DESKTOP = jetspeed.url.path.JETSPEED + "/desktop";
    jetspeed.url.path.PORTLET = jetspeed.url.path.JETSPEED + "/portlet";
    
    jetspeed.url.path.initialized = true;
}
jetspeed.url.scheme =
{   // used to make jetspeed.url.validateUrlStartsWithHttp cleaner
    HTTP_PREFIX: "http://",
    HTTP_PREFIX_LEN: "http://".length,
    HTTPS_PREFIX: "https://",
    HTTPS_PREFIX_LEN: "https://".length
};
jetspeed.url.basePortalUrl = function()
{
    if ( ! jetspeed.url.path.initialized )
        jetspeed.url.pathInitialize();
    return jetspeed.url.path.SERVER;    // return document.location.protocol + "//" + document.location.host ;
};
jetspeed.url.basePortalDesktopUrl = function()
{
    if ( ! jetspeed.url.path.initialized )
        jetspeed.url.pathInitialize();
    return jetspeed.url.basePortalUrl() + jetspeed.url.path.JETSPEED ;
};
jetspeed.url.basePortalWindowThemeUrl = function( windowtheme )
{
    return jetspeed.url.basePortalDesktopUrl() + "/javascript/jetspeed/windowthemes/" + windowtheme;
};

jetspeed.url.validateUrlStartsWithHttp = function( url )
{
    if ( url )
    {
        var len = url.length;
        var hSLen = jetspeed.url.scheme.HTTPS_PREFIX_LEN;
        if ( len > hSLen )  // has to be at least longer than as https://
        {
            var hLen = jetspeed.url.scheme.HTTP_PREFIX_LEN;
            if ( url.substring( 0, hLen ) == jetspeed.url.scheme.HTTP_PREFIX )
                return true;
            if ( url.substring( 0, hSLen ) == jetspeed.url.scheme.HTTPS_PREFIX )
                return true;
        }
    }
    return false;
};

jetspeed.url.BindArgs = function( bindArgs )
{
    dojo.lang.mixin( this, bindArgs );

    if ( ! this.mimetype )
        this.mimetype = "text/html";
};

dojo.lang.extend( jetspeed.url.BindArgs,
{
    createIORequest: function()
    {
        var ioReq = new dojo.io.Request( this.url, this.mimetype );
        ioReq.fromKwArgs( this );  // doing this cause dojo.io.Request tests arg0 for ctor == Object; we want out own obj here
        return ioReq;
    },

    load: function( type, data, evt )
    {
        //dojo.debug( "loaded content for url: " + this.url );
        //dojo.debug( "r e t r i e v e C o n t e n t . l o a d" ) ;
        //dojo.debug( "  type:" );
        //dojo.debugShallow( type ) ;
        //dojo.debug( "  evt:" );
        //dojo.debugShallow( evt ) ;
        var dmId = null;
        if ( this.debugContentDumpIds )
        {
            dmId = ( ( this.domainModelObject && dojo.lang.isFunction( this.domainModelObject.getId ) ) ? this.domainModelObject.getId() : "" );
            for ( var debugContentIndex = 0 ; debugContentIndex < this.debugContentDumpIds.length; debugContentIndex++ )
            {
                if ( dmId.match( new RegExp( this.debugContentDumpIds[ debugContentIndex ] ) ) )
                {
                    if ( dojo.lang.isString( data ) )
                        dojo.debug( "retrieveContent [" + ( dmId ? dmId : this.url ) + "] content: " + data );
                    else
                    {
                        var textContent = dojo.dom.innerXML( data );
                        if ( ! textContent )
                            textContent = ( data != null ? "!= null (IE no XMLSerializer)" : "null" );
                        dojo.debug( "retrieveContent [" + ( dmId ? dmId : this.url ) + "] xml-content: " + textContent );
                    }
                }
            }
        }
        if ( this.contentListener && dojo.lang.isFunction( this.contentListener.notifySuccess ) )
        {
            this.contentListener.notifySuccess( data, this.url, this.domainModelObject ) ;
        }
        else
        {
            dmId = ( ( this.domainModelObject && dojo.lang.isFunction( this.domainModelObject.getId ) ) ? this.domainModelObject.getId() : "" );
            dojo.debug( "retrieveContent [" + ( dmId ? dmId : this.url ) + "] no valid contentListener" );
        }
    },

    error: function( type, error )
    {
        //dojo.debug( "r e t r i e v e C o n t e n t . e r r o r" ) ;
        //dojo.debug( "  type:" );
        //dojo.debugShallow( type ) ;
        //dojo.debug( "  error:" );
        //dojo.debugShallow( error ) ;
        if ( this.contentListener && dojo.lang.isFunction( this.contentListener.notifyFailure ) )
        {
            this.contentListener.notifyFailure( type, error, this.url, this.domainModelObject );
        }
    }
});

jetspeed.url.retrieveContent = function( bindArgs, contentListener, domainModelObject, debugContentDumpIds )
{
    if ( ! bindArgs ) bindArgs = {};
    bindArgs.contentListener = contentListener ;
    bindArgs.domainModelObject = domainModelObject ;
    bindArgs.debugContentDumpIds = debugContentDumpIds ;
    
    var jetspeedBindArgs = new jetspeed.url.BindArgs( bindArgs );

    dojo.io.bind( jetspeedBindArgs.createIORequest() ) ;
};

jetspeed.url.checkAjaxApiResponse = function( requestUrl, data, reportError, apiRequestDescription, dumpOutput )
{
    var success = false;
    var statusElmt = data.getElementsByTagName( "status" );
    if ( statusElmt != null )
    {
        var successVal = statusElmt[0].firstChild.nodeValue;
        if ( successVal == "success" )
        {
            success = true;
        }
    }
    if ( ( ! success && reportError ) || dumpOutput )
    {
        var textContent = dojo.dom.innerXML( data );
        if ( ! textContent )
            textContent = ( data != null ? "!= null (IE no XMLSerializer)" : "null" );
        if ( apiRequestDescription == null )
            apiRequestDescription = "ajax-api";
        if ( success )
            dojo.debug( apiRequestDescription + " success  url=" + requestUrl + "  xml-content=" + textContent );
        else
            dojo.raise( apiRequestDescription + " failure  url=" + requestUrl + "  xml-content=" + textContent );
    }
    return success;
};

jetspeed.url.formatBindError = function( /* Object */ bindError )
{
    if ( bindError == null ) return "";
    var msg = " error:";
    if ( bindError.message != null )
        msg += " " + bindError.message;
    if ( bindError.number != null && bindError.number != "0" )
    {
        msg += " (" + bindError.number;
        if ( bindError.type != null && bindError.type != "unknown" )
            msg += "/" + bindError.type;
        msg += ")";
    }
    else if ( bindError.type != null && bindError.type != "unknown" )
    {
        msg += " (" + bindError.type + ")";
    }
    return msg;
};

// ... jetspeed.om.PortletContentRetriever
jetspeed.om.PortletContentRetriever = function()
{
};
jetspeed.om.PortletContentRetriever.prototype =
{   // /* Portlet */ portlet, /* String */ requestUrl, /* PortletContentListener */ portletContentListener
    getContent: function( bindArgs, contentListener, domainModelObject, /* String[] */ debugContentDumpIds )
    {
        if ( ! bindArgs )
            bindArgs = {};
        jetspeed.url.retrieveContent( bindArgs, contentListener, domainModelObject, debugContentDumpIds );
    }
};

// ... jetspeed.om.DojoDebugContentRetriever
jetspeed.om.DojoDebugContentRetriever = function()
{
    this.initialized = false;
};
jetspeed.om.DojoDebugContentRetriever.prototype =
{
    getContent: function( bindArgs, contentListener, domainModelObject, /* String[] */ debugContentDumpIds )
    {
        if ( ! bindArgs )
            bindArgs = {};
        if ( ! this.initialized )
        {
            var content = "";
            if ( jetspeed.altDebugWindowContent )
                content = jetspeed.altDebugWindowContent();
            else
            {
                content += '<div id="' + jetspeed.debug.debugContainerId + '"></div>';
            }
            if ( ! contentListener )
                contentListener = new jetspeed.om.BasicContentListener();
            contentListener.notifySuccess( content, bindArgs.url, domainModelObject ) ;
            this.initialized = true;
        }
    }
};

// ... jetspeed.om.PortletSelectorContentRetriever
jetspeed.om.PortletSelectorContentRetriever = function()
{
};
jetspeed.om.PortletSelectorContentRetriever.prototype =
{
    getContent: function( bindArgs, contentListener, domainModelObject, /* String[] */ debugContentDumpIds )
    {
        if ( ! bindArgs )
            bindArgs = {};
        var content = '<div widgetId="' + jetspeed.id.SELECTOR + '" dojoType="PortletDefContainer"></div>';
        if ( ! contentListener )
            contentListener = new jetspeed.om.BasicContentListener();
        contentListener.notifySuccess( content, bindArgs.url, domainModelObject ) ;
    }
};

// ... jetspeed.om.PortletSelectorContentListener
jetspeed.om.PortletSelectorContentListener = function()
{
};
jetspeed.om.PortletSelectorContentListener.prototype =
{
    notifySuccess: function( /* String */ portletContent, /* String */ requestUrl, /* Portlet */ portlet )
    {
        var windowWidget = this.getPortletWindow();
        if ( windowWidget )
        {
            windowWidget.setPortletContent( portletContent, renderUrl );
        }
    },
    notifyFailure: function( /* String */ type, /* Object */ error, /* String */ requestUrl, /* Portlet */ portlet )
    {
        dojo.raise( "PortletSelectorContentListener notifyFailure url=" + requestUrl + " type=" + type + " error=" + error ) ;
    }
};

// ... jetspeed.om.PageContentListenerUpdate
jetspeed.om.PageContentListenerUpdate = function( /* jetspeed.om.Page */ previousPage )
{
    this.previousPage = previousPage;
};
jetspeed.om.PageContentListenerUpdate.prototype =
{
    notifySuccess: function( /* XMLDocument */ data, /* String */ requestUrl, /* Page */ page )
    {
        dojo.raise( "PageContentListenerUpdate notifySuccess - BUT NOT SUPPORTED - url=" + requestUrl ) ;
        //page.getPortletsFromPSML( data );  // the new getFragmentsFromPSML is not compatible with this usage
        //var updatedPortlets = page.getPortletArray();
        //for ( var i = 0 ; i < updatedPortlets.length ; i++ )
        //{
        //    var prevPortlet = this.previousPage.getPortlet( updatedPortlets[i].entityId );
        //    if ( prevPortlet == null )
        //    {
        //        dojo.debug( "PageContentListenerUpdate  new portlet definition in page: " + updatedPortlets[i].toString() ) ;
        //    }
        //}
    },
    notifyFailure: function( /* String */ type, /* Object */ error, /* String */ requestUrl, /* Page */ page )
    {
        dojo.raise( "PageContentListenerUpdate notifyFailure url=" + requestUrl + " type=" + type + " error=" + error ) ;
    }
};

// ... jetspeed.om.PageContentListenerCreateWidget
jetspeed.om.PageContentListenerCreateWidget = function()
{
};
jetspeed.om.PageContentListenerCreateWidget.prototype =
{
    notifySuccess: function( /* XMLDocument */ data, /* String */ requestUrl, /* Page */ page )
    {
        page.loadFromPSML( data );
    },
    notifyFailure: function( /* String */ type, /* Object */ error, /* String */ requestUrl, /* Page */ page )
    {
        dojo.raise( "PageContentListenerCreateWidget notifyFailure url=" + requestUrl + " type=" + type + " error=" + error ) ;
    }
};

// ... jetspeed.om.Id
jetspeed.om.Id = function( /* ... */ )  // intended as a simple, general object with an id and a getId() function
{
    var idBuff = "";
    for ( var i = 0; i < arguments.length; i++ )
    {
        if( dojo.lang.isString( arguments[i] ) )
        {
            if ( idBuff.length > 0 )
                idBuff += "-";
            idBuff += arguments[i];
        }
        else if ( dojo.lang.isObject( arguments[i] ) )
        {
            for ( var slotKey in arguments[i] )
            {
                this[ slotKey ] = arguments[i][slotKey];
            }
        }
    }
    this.jetspeed_om_id = idBuff;
};
dojo.lang.extend( jetspeed.om.Id,
{
    getId: function()
    {
        return this.jetspeed_om_id;
    }
});

// ... jetspeed.om.Page
jetspeed.om.Page = function( pagePsmlPath, pageName, pageTitle )
{
    this.psmlPath = pagePsmlPath;
    if ( this.psmlPath == null )
        this.setPsmlPathFromDocumentUrl();
    this.name = pageName;
    this.title = pageTitle;
    this.layouts = {};
    this.columns = [];
    this.portlets = [];
    this.menus = [];
};
dojo.inherits( jetspeed.om.Page, jetspeed.om.Id );
dojo.lang.extend( jetspeed.om.Page,
{
    psmlPath: null,
    name: null,
    path: null,
    title: null,
    shortTitle: null,
    layoutDecorator: null,
    portletDecorator: null,

    layouts: null,
    columns: null,
    portlets: null,

    noMovePersist: false,    // BOZO:NOW: observe this setting

    menus: null,

    getId: function()  // jetspeed.om.Id protocol
    {
        var idsuffix = ( this.name != null && this.name.length > 0 ? this.name : null );
        if ( ! idsuffix )
        {
            this.getPsmlUrl();
            idsuffix = this.psmlPath;
        }
        return "page-" + idsuffix;
    },

    getPageDocumentUrl: function( pathOverride )
    {
        var docPath = document.location.pathname ;
        
        var contextAndServletPath = jetspeed.url.path.DESKTOP ;
    },
    
    setPsmlPathFromDocumentUrl: function()
    {
        var psmlPath = jetspeed.url.path.AJAX_API ;
        var docPath = document.location.pathname ;
        
        var contextAndServletPath = jetspeed.url.path.DESKTOP ;
        var contextAndServletPathPos = docPath.indexOf( contextAndServletPath ) ;
        if ( contextAndServletPathPos != -1 && docPath.length > ( contextAndServletPathPos + contextAndServletPath.length ) )
        {
            psmlPath = psmlPath + docPath.substring( contextAndServletPathPos + contextAndServletPath.length ) ;
        }
        this.psmlPath = psmlPath ;
    },
    
    getPsmlUrl: function()
    {
        if ( this.psmlPath == null )
            this.setPsmlPathFromDocumentUrl() ;

        return jetspeed.url.basePortalUrl() + this.psmlPath ;
    },
    
    retrievePsml: function( pageContentListener )
    {
        if ( pageContentListener == null )
            pageContentListener = new jetspeed.om.PageContentListenerCreateWidget();

        var psmlUrl = this.getPsmlUrl() ;
        var mimetype = "text/xml";

        if ( jetspeed.debug.retrievePsml )
            dojo.debug( "retrievePsml url: " + psmlUrl ) ;

        jetspeed.url.retrieveContent( { url: psmlUrl, mimetype: mimetype }, pageContentListener, this, jetspeed.debugContentDumpIds );
    },

    loadFromPSML: function( psml )
    {
        // parse PSML
        var parsedRootLayoutFragment = this._parsePSML( psml );

        // create layout model
        var portletsByPageColumn = this._layoutCreateModel( parsedRootLayoutFragment );

        // create columns
        if ( jetspeed.prefs.windowTiling )
        {
            this._createColumns( document.getElementById( jetspeed.id.DESKTOP ) );
        }

        // create portlet windows
        var windowsToRender = new Array();
        var colLen = this.columns.length;
        for ( var colIndex = 0 ; colIndex <= this.columns.length ; colIndex++ )  // iterate to one past last column index
        {
            var portletArray = null;
            if ( colIndex == colLen )
            {
                portletArray = portletsByPageColumn[ "z" ];
                if ( portletArray != null )
                    portletArray.sort( this._loadPortletZIndexCompare );
            }
            else
            {
                portletArray = portletsByPageColumn[ colIndex.toString() ];
            }
        
            if ( portletArray != null )
            {
                for ( var i = 0; i < portletArray.length; i++ )
                {
                    var portlet = portletArray[i].portlet;
                    windowsToRender.push( portlet );
                    portlet.createPortletWindow( colIndex );
                }
            }
        }

        // render portlets
        if ( windowsToRender && windowsToRender.length > 0 )
        {
            jetspeed.doRenderAll( null, windowsToRender, true );
        }

        // initialize portlet window state
        this._portletsInitializeWindowState( portletsByPageColumn[ "z" ] );

        // load menus
        this.retrieveAllMenus();
    },
    _parsePSML: function( psml )
    {
        var pageElements = psml.getElementsByTagName( "page" );
        if ( ! pageElements || pageElements.length > 1 )
            dojo.raise( "unexpected zero or multiple <page> elements in psml" );
        var pageElement = pageElements[0];
        var children = pageElement.childNodes;
        var simpleValueLNames = new RegExp( "(name|path|title|short-title)" );
        var rootFragment = null;
        for ( var i = 0 ; i < children.length ; i++ )
        {
            var child = children[i];
            if ( child.nodeType != dojo.dom.ELEMENT_NODE )
                continue;
            var childLName = child.nodeName;
            if ( childLName == "fragment" )
            {
                rootFragment = child;
            }
            else if ( childLName == "defaults" )
            {
                this.layoutDecorator = child.getAttribute( "layout-decorator" );
                this.portletDecorator = child.getAttribute( "portlet-decorator" );
            }
            else if ( childLName && childLName.match( simpleValueLNames  ) )
            {
                this[ jetspeed.purifyIdentifier( childLName, "", "lo" ) ] = ( ( child && child.firstChild ) ? child.firstChild.nodeValue : null );
            }
        }

        if ( rootFragment == null )
        {
            dojo.raise( "No root fragment in PSML." );
            return null;
        }
        
        var parsedRootLayoutFragment = this._parsePSMLLayoutFragment( rootFragment, 0 );    // rootFragment must be a layout fragment - /portal requires this as well
        return parsedRootLayoutFragment;
    },
    _parsePSMLLayoutFragment: function( layoutNode, layoutNodeDocumentOrderIndex )
    {
        var fragChildren = new Array();
        var layoutFragType = ( (layoutNode != null) ? layoutNode.getAttribute( "type" ) : null );
        if ( layoutFragType != "layout" )
        {
            dojo.raise( "_parsePSMLLayoutFragment called with non-layout fragment: " + layoutNode );
            return null;
        }

        var sizes = null, sizesSum = 0;
        var propertiesMap = {};
        var children = layoutNode.childNodes;
        var child, childLName, propName, propVal, fragType;
        for ( var i = 0 ; i < children.length ; i++ )
        {
            child = children[i];
            if ( child.nodeType != dojo.dom.ELEMENT_NODE )
                continue;
            childLName = child.nodeName;
            if ( childLName == "fragment" )
            {
                fragType = child.getAttribute( "type" );
                if ( fragType == "layout" )
                {
                    var parsedLayoutChildFragment = this._parsePSMLLayoutFragment( child, i );
                    if ( parsedLayoutChildFragment != null )
                    {
                        fragChildren.push( parsedLayoutChildFragment ) ;
                    }
                }
                else
                {
                    fragChildren.push( { id: child.getAttribute( "id" ), type: fragType, name: child.getAttribute( "name" ), properties: this._parsePSMLProperties( child, null ), actions: this._parsePSMLActions( child, null ), currentActionState: this._parsePSMLCurrentActionState( child ), currentActionMode: this._parsePSMLCurrentActionMode( child ), decorator: child.getAttribute( "decorator" ), documentOrderIndex: i } );
                }
            }
            else if ( childLName == "property" )
            {
                if ( this._parsePSMLProperty( child, propertiesMap ) == "sizes" )
                {
                    if ( sizes != null )
                    {
                        dojo.raise( "_parsePSMLLayoutFragment called with layout fragment that contains more than one sizes property: " + layoutNode );
                        return null;
                    }

                    propVal = child.getAttribute( "value" );
                    if ( propVal != null && propVal.length > 0 )
                    {
                        sizes = propVal.split( "," );
                        for ( var j = 0 ; j < sizes.length ; j++ )
                        {
                            var re = /^[^0-9]*([0-9]+)[^0-9]*$/;
                            sizes[j] = sizes[j].replace( re, "$1" );
                            sizesSum += new Number( sizes[j] );
                        }
                    }
                }
            }
        }

        fragChildren.sort( this._fragmentRowCompare );

        var layoutFragIndexes = new Array();
        var otherFragIndexes = new Array();        
        for ( var i = 0 ; i < fragChildren.length ; i++ )
        {
            if ( fragChildren[i].type == "layout" )
                layoutFragIndexes.push( i );
            else
                otherFragIndexes.push( i );
        }
        if ( sizes == null )
        {
            sizes = new Array();
            sizes.push( "100" );
            sizesSum = 100;
        }
        return { id: layoutNode.getAttribute( "id" ), type: layoutFragType, name: layoutNode.getAttribute( "name" ), decorator: layoutNode.getAttribute( "decorator" ), columnSizes: sizes, columnSizesSum: sizesSum, properties: propertiesMap, fragments: fragChildren, layoutFragmentIndexes: layoutFragIndexes, otherFragmentIndexes: otherFragIndexes, documentOrderIndex: layoutNodeDocumentOrderIndex };
    },
    _parsePSMLActions: function( fragmentNode, actionsMap )
    {
        if ( actionsMap == null )
            actionsMap = {};
        var actions = fragmentNode.getElementsByTagName( "action" );
        for( var actionsIdx=0; actionsIdx < actions.length; actionsIdx++ )
        {
            var actionNode = actions[actionsIdx];
            var actionName = actionNode.getAttribute( "id" );
            if ( actionName != null )
            {
                var actionType = actionNode.getAttribute( "type" );
                var actionLabel = actionNode.getAttribute( "name" );
                var actionUrl = actionNode.getAttribute( "url" );
                var actionAlt = actionNode.getAttribute( "alt" );
                actionsMap[ actionName.toLowerCase() ] = { id: actionName, type: actionType, label: actionLabel, url: actionUrl, alt: actionAlt };
            }
        }
        return actionsMap;
    },
    _parsePSMLCurrentActionState: function( fragmentNode )
    {
        var nodes = fragmentNode.getElementsByTagName( "state" );
        if ( nodes != null && nodes.length == 1 )
        {
            return nodes[0].firstChild.nodeValue;
        }
        return null;
    },
    _parsePSMLCurrentActionMode: function( fragmentNode )
    {
        var nodes = fragmentNode.getElementsByTagName( "mode" );
        if ( nodes != null && nodes.length == 1 )
        {
            return nodes[0].firstChild.nodeValue;
        }
        return null;
    },
    _parsePSMLProperties: function( fragmentNode, propertiesMap )
    {
        if ( propertiesMap == null )
            propertiesMap = {};
        var props = fragmentNode.getElementsByTagName( "property" );
        for( var propsIdx=0; propsIdx < props.length; propsIdx++ )
        {
            this._parsePSMLProperty( props[propsIdx], propertiesMap );
        }
        return propertiesMap;
    },
    _parsePSMLProperty: function( propertyNode, propertiesMap )
    {
        var propName = propertyNode.getAttribute( "name" );
        var propValue = propertyNode.getAttribute( "value" );
        propertiesMap[ propName ] = propValue;
        return propName;
    },
    _fragmentRowCompare: function( fragmentA, fragmentB )
    {
        var rowA = fragmentA.documentOrderIndex * 1000 ;  // so that frags without row property fall after those with row property
        var rowB = fragmentB.documentOrderIndex * 1000 ;

        var rowAprop = fragmentA.properties[ "row" ];
        if ( rowAprop != null )
            rowA = rowAprop;
        var rowBprop = fragmentB.properties[ "row" ];
        if ( rowBprop != null )
            rowB = rowBprop;
        return ( rowA - rowB );
    },

    _layoutCreateModel: function( parsedRootLayoutFragment )
    {
        var layoutFragment = parsedRootLayoutFragment;
        var portletsByPageColumn = {};

        // desktop layout handling rule:
        //   in order to persist portlet positions, all layout fragments must span the entire width of the page

        // does root fragment contain portlets only / layouts only / mix of layouts & portlets
        if ( layoutFragment.layoutFragmentIndexes != null && layoutFragment.layoutFragmentIndexes.length > 0 )
        {
            if ( layoutFragment.columnSizes.length > 1 )
            {   // root fragments with multiple columns can contain portlets only
                //    since a nested layout has to appear in a particular column (thus diving one column in the outer fragment into n columns)
                this.noMovePersist = true;
            }

            if ( layoutFragment.otherFragmentIndexes == null || layoutFragment.otherFragmentIndexes.length == 0 )
            {   // root fragment contains layout fragments only - ignore the root fragment
                for ( var i = 0 ; i < layoutFragment.layoutFragmentIndexes.length ; i++ )
                {
                    var layoutChildFrag = layoutFragment.fragments[ layoutFragment.layoutFragmentIndexes[i] ];
                    var hasNestedLayouts = this._layoutFragmentChildCollapse( layoutChildFrag );
                    if ( hasNestedLayouts )
                        this.noMovePersist = true;
                    var pageColumnStartIndex = this.columns.length;
                    var columnsInLayout = this._layoutRegisterAndCreateColumnsModel( layoutChildFrag );
                    this._layoutCreatePortletsModel( layoutChildFrag, columnsInLayout, pageColumnStartIndex, portletsByPageColumn );
                }
            }
            else
            {   // mixed layout and portlet fragments - collapse portlet fragments in one or more clones of the root layout
                var currentClonedLayoutFragment = null;
                var clonedLayoutFragmentCount = 0;
                for ( var i = 0 ; i <= layoutFragment.fragments.length ; i++ )  // iterate one past the last index - to catch end currentClonedLayoutFragment
                {   // fragments array is sorted by row, so a contiguous set of portlet fragments belong together in the same cloned layout fragment
                    if ( currentClonedLayoutFragment != null && ( i == layoutFragment.fragments.length || layoutFragment.fragments[i].type == "layout" ) )
                    {
                        var pageColumnStartIndex = this.columns.length;
                        var columnsInLayout = this._layoutRegisterAndCreateColumnsModel( currentClonedLayoutFragment );
                        this._layoutCreatePortletsModel( currentClonedLayoutFragment, columnsInLayout, pageColumnStartIndex, portletsByPageColumn );
                        currentClonedLayoutFragment = null;
                    }
                    if ( i < layoutFragment.fragments.length )
                    {
                        if ( layoutFragment.fragments[i].type == "layout" )
                        {
                            var layoutChildFrag = layoutFragment.fragments[ i ];  // index was: layoutFragment.layoutFragmentIndexes[i]
                            var hasNestedLayouts = this._layoutFragmentChildCollapse( layoutChildFrag );
                            if ( hasNestedLayouts )
                                this.noMovePersist = true;
                            var pageColumnStartIndex = this.columns.length;
                            var columnsInLayout = this._layoutRegisterAndCreateColumnsModel( layoutChildFrag );
                            this._layoutCreatePortletsModel( layoutChildFrag, columnsInLayout, pageColumnStartIndex, portletsByPageColumn );
                        }
                        else
                        {
                            if ( currentClonedLayoutFragment == null )
                            {
                                clonedLayoutFragmentCount++;
                                var clonedPortletLayout = {};
                                dojo.lang.mixin( clonedPortletLayout, layoutFragment );
                                clonedPortletLayout.fragments = new Array();
                                clonedPortletLayout.layoutFragmentIndexes = new Array();
                                clonedPortletLayout.otherFragmentIndexes = new Array();
                                clonedPortletLayout.documentOrderIndex = layoutFragment.fragments[i].documentOrderIndex;
                                clonedPortletLayout.clonedFromRootId = clonedPortletLayout.id;
                                clonedPortletLayout.clonedLayoutFragmentIndex = clonedLayoutFragmentCount;
                                clonedPortletLayout.id = clonedPortletLayout.id + "-rootclone_" + clonedLayoutFragmentCount;
                                currentClonedLayoutFragment = clonedPortletLayout ;
                            }
                            clonedPortletLayout.fragments.push( layoutFragment.fragments[i] );
                            clonedPortletLayout.otherFragmentIndexes.push( clonedPortletLayout.fragments.length -1 );
                        }
                    }
                }
            }
        }
        else if ( layoutFragment.otherFragmentIndexes != null && layoutFragment.otherFragmentIndexes.length > 0 )
        {   // root fragment contains portlet fragments only
            var pageColumnStartIndex = this.columns.length;
            var columnsInLayout = this._layoutRegisterAndCreateColumnsModel( layoutFragment );
            this._layoutCreatePortletsModel( layoutFragment, columnsInLayout, pageColumnStartIndex, portletsByPageColumn );
        }
        return portletsByPageColumn;
    },

    _layoutFragmentChildCollapse: function( layoutFragment, targetLayoutFragment )
    {
        var hasNestedLayouts = false;
        if ( targetLayoutFragment == null )
            targetLayoutFragment = layoutFragment;
        if ( layoutFragment.layoutFragmentIndexes != null && layoutFragment.layoutFragmentIndexes.length > 0 )
        {   // if contains nested layouts - collect their portlets into targetLayoutFragment
            hasNestedLayouts = true;
            for ( var i = 0 ; i < layoutFragment.layoutFragmentIndexes.length ; i++ )
            {
                var layoutChildFrag = layoutFragment.fragments[ layoutFragment.layoutFragmentIndexes[i] ];
                if ( layoutChildFrag.otherFragmentIndexes != null && layoutChildFrag.otherFragmentIndexes.length > 0 )
                {
                    for ( var i = 0 ; i < layoutChildFrag.otherFragmentIndexes.length ; i++ )
                    {
                        var pFrag = layoutChildFrag.fragments[ layoutChildFrag.otherFragmentIndexes[i] ];
                        pFrag.properties[ jetspeed.id.PORTLET_PROP_COLUMN ] = -1;
                        pFrag.properties[ jetspeed.id.PORTLET_PROP_ROW ] = -1;
                        // BOZO:NOW: ^^ should we set to -1 or delete row & column properties ?
                        pFrag.documentOrderIndex = targetLayoutFragment.fragments.length;
                        targetLayoutFragment.fragments.push( pFrag );
                        targetLayoutFragment.otherFragIndexes.push( targetLayoutFragment.fragments.length ) ;
                    }
                }
                this._layoutFragmentChildCollapse( layoutChildFrag, targetLayoutFragment );
            }
        }
        return hasNestedLayouts;
    },

    _layoutRegisterAndCreateColumnsModel: function( layoutFragment )
    {   // columnSizes: sizes, columnSizesSum: sizesSum
        this.layouts[ layoutFragment.id ] = layoutFragment;
        var columnsInLayout = new Array();
        if ( jetspeed.prefs.windowTiling )
        {
            var subOneLast = false;
            if ( layoutFragment.columnSizesSum == 100 )
                subOneLast = true;
            for ( var i = 0 ; i < layoutFragment.columnSizes.length ; i++ )
            {
                var size = layoutFragment.columnSizes[i];
                if ( subOneLast && i == (layoutFragment.columnSizes.length - 1) )
                    size = size - 1;
                var colModelObj = new jetspeed.om.Column( i, layoutFragment.id, size, this.columns.length );
                this.columns.push( colModelObj );
                columnsInLayout.push( colModelObj );
            }
        }
        return columnsInLayout;
    },
    _layoutCreatePortletsModel: function( layoutFragment, columnsInLayout, pageColumnStartIndex, portletsByPageColumn )
    {
        if ( layoutFragment.otherFragmentIndexes != null && layoutFragment.otherFragmentIndexes.length > 0 )
        {
            var portletsByColumn = new Array();    // for dispersing portlets when column specification is not valid
            for ( var i = 0 ; i < columnsInLayout.length; i++ )
            {
                portletsByColumn.push( new Array() );
            }
            for ( var i = 0 ; i < layoutFragment.otherFragmentIndexes.length ; i++ )
            {
                var pFrag = layoutFragment.fragments[ layoutFragment.otherFragmentIndexes[i] ];
    
                if ( jetspeed.debugPortletEntityIdFilter )
                {
                    if ( ! dojo.lang.inArray( jetspeed.debugPortletEntityIdFilter, pFrag.id ) )
                        pFrag = null;
                }
                
                if ( pFrag != null )
                {
                    var portletPageColumnKey = "z";
                    var portletWindowExtendedProperty = pFrag.properties[ jetspeed.id.PORTLET_PROP_DESKTOP_EXTENDED ];
                    
                    var portletWindowPositionStatic = jetspeed.prefs.windowTiling;
                    var portletWindowHeightToFit = jetspeed.prefs.windowHeightExpand;
                    if ( portletWindowExtendedProperty != null && jetspeed.prefs.windowTiling )
                    {
                        var extPropData = portletWindowExtendedProperty.split( jetspeed.id.PORTLET_PROP_DESKTOP_EXTENDED_PAIR_SEPARATOR );
                        var extProp = null, extPropLen = 0, extPropName = null, extPropValue = null, extPropFlag = false;
                        if ( extPropData != null && extPropData.length > 0 )
                        {
                            var propSeparator = jetspeed.id.PORTLET_PROP_DESKTOP_EXTENDED_PROP_SEPARATOR;
                            for ( var extPropIndex = 0 ; extPropIndex < extPropData.length ; extPropIndex++ )
                            {
                                extProp = extPropData[extPropIndex];
                                extPropLen = ( ( extProp != null ) ? extProp.length : 0 );
                                if ( extPropLen > 0 )
                                {
                                    var eqPos = extProp.indexOf( propSeparator );
                                    if ( eqPos > 0 && eqPos < (extPropLen-1) )
                                    {
                                        extPropName = extProp.substring( 0, eqPos );
                                        extPropValue = extProp.substring( eqPos +1 );
                                        extPropFlag = ( ( extPropValue == "true" ) ? true : false );
                                        if ( extPropName == jetspeed.id.PORTLET_PROP_DESKTOP_EXTENDED_STATICPOS )
                                            portletWindowPositionStatic = extPropFlag;
                                        else if ( extPropName == jetspeed.id.PORTLET_PROP_DESKTOP_EXTENDED_FITHEIGHT )
                                            portletWindowHeightToFit = extPropFlag;
                                    }
                                }
                            }
                        }
                    }
                    else if ( ! jetspeed.prefs.windowTiling )
                    {
                        portletWindowPositionStatic = false;
                    }
                    pFrag.properties[ jetspeed.id.PORTLET_PROP_WINDOW_POSITION_STATIC ] = portletWindowPositionStatic;
                    pFrag.properties[ jetspeed.id.PORTLET_PROP_WINDOW_HEIGHT_TO_FIT ] = portletWindowHeightToFit;
                    
                    if ( portletWindowPositionStatic && jetspeed.prefs.windowTiling )
                    {
                        var portletColumnIndex = pFrag.properties[ jetspeed.id.PORTLET_PROP_COLUMN ];
                        if ( portletColumnIndex == null || portletColumnIndex == "" || portletColumnIndex < 0 || portletColumnIndex >= columnsInLayout.length )
                        {
                            var minPortlets = -1; 
                            for ( var j = 0 ; j < columnsInLayout.length ; j++ )
                            {
                                if ( minPortlets == -1 || portletsByColumn[j].length < minPortlets )
                                {
                                    minPortlets = portletsByColumn[j].length;
                                    portletColumnIndex = j;
                                }
                            }
                        }
                        portletsByColumn[portletColumnIndex].push( pFrag.id );
                        var portletPageColumnIndex = pageColumnStartIndex + new Number( portletColumnIndex );
                        portletPageColumnKey = portletPageColumnIndex.toString();
                    }
                    var portlet = new jetspeed.om.Portlet( pFrag.name, pFrag.id, null, pFrag.properties, pFrag.actions, pFrag.currentActionState, pFrag.currentActionMode, pFrag.decorator ) ;
                    portlet.initialize();

                    this.putPortlet( portlet ) ;

                    if ( portletsByPageColumn[ portletPageColumnKey ] == null )
                    {
                        portletsByPageColumn[ portletPageColumnKey ] = new Array();
                    }
                    portletsByPageColumn[ portletPageColumnKey ].push( { portlet: portlet, layout: layoutFragment.id } );
                }
            }
        }
    },

    _portletsInitializeWindowState: function( /* Array */ portletsByPageColumnZ )
    {
        var initialColumnRowAllPortlets = {};
        this.getPortletCurrentColumnRow( null, false, initialColumnRowAllPortlets );
        for ( var portletIndex in this.portlets )
        {
            var portlet = this.portlets[portletIndex];
            var portletInitialColRow = initialColumnRowAllPortlets[ portlet.getId() ];
            if ( portletInitialColRow == null && portletsByPageColumnZ )
            {
                for ( var i = 0 ; i < portletsByPageColumnZ.length ; i++ )
                {
                    if ( portletsByPageColumnZ[i].portlet.getId() == portlet.getId() )
                    {
                        portletInitialColRow = { layout: portletsByPageColumnZ[i].layout };
                        // NOTE: if portlet is put in tiling mode it should be placed in the bottom row of column 0 of layout
                        break;
                    }
                }
            }
            if ( portletInitialColRow != null )
                portlet._initializeWindowState( portletInitialColRow, false );
            else
                dojo.raise( "page._portletsInitializeWindowState could not find window state init data for portlet: " + portlet.getId() );
        }
    },

    _loadPortletZIndexCompare: function( portletA, portletB )
    {
        var aZIndex = null;
        var bZIndex = null;
        var windowState = null;
        aZIndex = portletA.portlet._getInitialZIndex();
        bZIndex = portletB.portlet._getInitialZIndex();
        if ( aZIndex && ! bZIndex )
            return -1;
        else if ( bZIndex && ! aZIndex )
            return 1;
        else if ( aZIndex == bZIndex )
            return 0;
        return ( aZIndex - bZIndex );
    },

    _createColumns: function( columnsParent )
    {
        if ( ! this.columns || this.columns.length == 0 ) return;
        var columnContainer = document.createElement( "div" );
        columnContainer.id = jetspeed.id.COLUMNS;
        columnContainer.setAttribute( "id", jetspeed.id.COLUMNS );
        for ( var colIndex = 0 ; colIndex < this.columns.length ; colIndex++ )
        {
            var colObj = this.columns[colIndex];
            colObj.createColumn( columnContainer );
        }
        columnsParent.appendChild( columnContainer );
    },
    _removeColumns: function( /* DOM Node */ preserveWindowNodesInNode )
    {
        if ( ! this.columns || this.columns.length == 0 ) return;
        for ( var i = 0 ; i < this.columns.length ; i++ )
        {
            if ( this.columns[i] )
            {
                if ( preserveWindowNodesInNode )
                {
                    var windowNodesInColumn = jetspeed.ui.getPortletWindowChildren( this.columns[i].domNode, null );
                    dojo.lang.forEach( windowNodesInColumn,
                        function( windowNode ) { preserveWindowNodesInNode.appendChild( windowNode ); } );
                }
                dojo.dom.removeNode( this.columns[i] );
                this.columns[i] = null;
            }
        }
        var columnContainer = dojo.byId( jetspeed.id.COLUMNS );
        if ( columnContainer )
            dojo.dom.removeNode( columnContainer );
        this.columns = [];
    },

    getPortletCurrentColumnRow: function( /* DOM node */ justForPortletWindowNode, /* boolean */ includeGhosts, /* map */ currentColumnRowAllPortlets )
    {
        if ( ! this.columns || this.columns.length == 0 ) return null;
        var result = null;
        var clonedLayoutCompletedRowCount = 0;
        var currentLayout = null;
        var currentLayoutId = null;
        var currentLayoutRowCount = 0;
        var currentLayoutIsRegular = false;
        for ( var colIndex = 0 ; colIndex < this.columns.length ; colIndex++ )
        {
            var colObj = this.columns[colIndex];
            if ( currentLayoutId == null || currentLayoutId != colObj.getLayoutId() )
            {
                if ( currentLayoutId != null && ! currentLayoutIsRegular )
                {
                    clonedLayoutCompletedRowCount = clonedLayoutCompletedRowCount + currentLayoutRowCount;
                }
                currentLayoutId = colObj.getLayoutId();
                currentLayout = this.layouts[ currentLayoutId ];
                if ( currentLayout == null )
                {
                    dojo.raise( "getPortletCurrentColumnRow cannot locate layout id: " + currentLayoutId ) ;
                    return null;
                }
                currentLayoutRowCount = 0;
                currentLayoutIsRegular = false;
                if ( currentLayout.clonedFromRootId == null )
                {
                    currentLayoutIsRegular = true ;
                    clonedLayoutCompletedRowCount = clonedLayoutCompletedRowCount + 1;
                    // BOZO: should it ^^ be 0 if no portlets are contained in layout
                }
            }

            var colCurrentRow = null;
            var colChildNodes = colObj.domNode.childNodes;
            for ( var colChildIndex = 0 ; colChildIndex < colChildNodes.length ; colChildIndex++ )
            {
                var colChild = colChildNodes[colChildIndex];
                if ( dojo.html.hasClass( colChild, jetspeed.id.PORTLET_WINDOW_STYLE_CLASS ) || ( includeGhosts && dojo.html.hasClass( colChild, jetspeed.id.PORTLET_WINDOW_GHOST_STYLE_CLASS ) ) )
                {
                    colCurrentRow = ( colCurrentRow == null ? 0 : colCurrentRow + 1 );
                    if ( (colCurrentRow + 1) > currentLayoutRowCount )
                        currentLayoutRowCount = (colCurrentRow + 1);
                    if ( justForPortletWindowNode == null || colChild == justForPortletWindowNode )
                    {
                        var portletResult = { layout: currentLayoutId, column: colObj.getLayoutColumnIndex(), row: colCurrentRow };
                        if ( ! currentLayoutIsRegular )
                        {
                            portletResult.layout = currentLayout.clonedFromRootId;
                            portletResult.row = ( clonedLayoutCompletedRowCount + colCurrentRow );
                        }
                        if ( justForPortletWindowNode != null )
                        {
                            result = portletResult;
                            break;
                        }
                        else if ( currentColumnRowAllPortlets != null )
                        {
                            var portletWindowWidget = this.getPortletWindowFromNode( colChild );
                            if ( portletWindowWidget == null )
                            {
                                dojo.raise( "getPortletCurrentColumnRow cannot locate PortletWindow for node." ) ;
                            }
                            else
                            {
                                var portlet = portletWindowWidget.portlet;
                                if ( portlet == null )
                                {
                                    dojo.raise( "getPortletCurrentColumnRow PortletWindow.portlet is for widgetId: " + portletWindowWidget.widgetId ) ;
                                }
                                else
                                {
                                    currentColumnRowAllPortlets[ portlet.getId() ] = portletResult;
                                }
                            }
                        }
                    }
                }
            }
            if ( result != null )
                break;
        }
        return result;
    },

    _debugDumpPortletZIndexInfo: function()
    {
        var portletArray = this._getPortletArrayByZIndex();
        var dumpMsg = "";
        for ( var i = 0; i < portletArray.length; i++ )
        {
            var portlet = portletArray[i];
            if ( i > 0 ) dumpMsg += ", ";
            var windowState = portlet.getLastSavedWindowState();
            var zIndex = ( windowState ? windowState.zIndex : "null" );
            dumpMsg += "[" + portlet.entityId + "] zIndex=" + zIndex;
        }
        return dumpMsg;
    },
    _getPortletArrayByZIndex: function()
    {
        var portletArray = this.getPortletArray();
        if ( ! portletArray ) return portletArray;
        var filteredPortletArray = [];
        for ( var i = 0 ; i < portletArray.length; i++ )
        {
            if ( ! portletArray[i].getProperty( jetspeed.id.PORTLET_PROP_WINDOW_POSITION_STATIC ) )
                filteredPortletArray.push( portletArray[i] );
        }
        filteredPortletArray.sort( this._portletZIndexCompare );
        return filteredPortletArray;
    },
    _portletZIndexCompare: function( portletA, portletB )
    {   // uses saved state only - does not check with window widget
        var aZIndex = null;
        var bZIndex = null;
        var windowState = null;
        windowState = portletA.getLastSavedWindowState();
        aZIndex = windowState.zIndex;
        windowState = portletB.getLastSavedWindowState();
        bZIndex = windowState.zIndex;
        if ( aZIndex && ! bZIndex )
            return -1;
        else if ( bZIndex && ! aZIndex )
            return 1;
        else if ( aZIndex == bZIndex )
            return 0;
        return ( aZIndex - bZIndex );
    },

    getWindowThemeDefault: function()
    {
        var windowtheme = null;
        if ( djConfig.isDebug && jetspeed.debug.windowThemeRandom )
            windowtheme = jetspeed.prefs.windowThemesAllowed[ Math.floor( Math.random() * jetspeed.prefs.windowThemesAllowed.length ) ];
        else if ( dojo.lang.indexOf( jetspeed.prefs.windowThemesAllowed, this.getPortletDecorator() ) != -1 )
            windowtheme = this.getPortletDecorator();
        else
            windowtheme = jetspeed.prefs.windowTheme;
        return windowtheme;
    },
    getPortletArrayList: function()
    {
        var portletArrayList = new dojo.collections.ArrayList();
        for ( var portletIndex in this.portlets )
        {
            var portlet = this.portlets[ portletIndex ];
            portletArrayList.add( portlet );
        }
        return portletArrayList;
    },
    getPortletArray: function()
    {
        if (! this.portlets) return null;
        var portletArray = [];
        for ( var portletIndex in this.portlets )
        {
            var portlet = this.portlets[ portletIndex ];
            portletArray.push( portlet );
        }
        return portletArray;
    },
    getPortletIds: function()
    {
        if (! this.portlets) return null;
        var portletIdArray = [];
        for ( var portletIndex in this.portlets )
        {
            var portlet = this.portlets[ portletIndex ];
            portletIdArray.push( portlet.getId() );
        }
        return portletIdArray;

    },
    getPortletByName: function( /* String */ portletName )
    {
        if ( this.portlets && portletName )
        {
            for (var portletIndex in this.portlets)
            {
                var portlet = this.portlets[portletIndex];
                if ( portlet.name == portletName )
                    return portlet;
            }
        }
        return null;
    },
    getPortlet: function( /* String */ portletEntityId )
    {
        if ( this.portlets && portletEntityId )
            return this.portlets[portletEntityId];
        return null;
    },
    getPortletWindowFromNode: function( /* DOM node */ portletWindowNode )
    {
        var portletWindowWidget = null;
        if ( this.portlets && portletWindowNode )
        {
            for (var portletIndex in this.portlets)
            {
                var portlet = this.portlets[portletIndex];
                var portletWindow = portlet.getPortletWindow();
                if ( portletWindow != null )
                {
                    if ( portletWindow.domNode == portletWindowNode )
                    {
                        portletWindowWidget = portletWindow;
                        break;
                    }
                }
            }
        }
        return portletWindowWidget;
    },
    putPortlet: function( /* Portlet */ portlet )
    {
        if (!portlet) return ;
        if (! this.portlets) this.portlets = [] ;
        this.portlets[ portlet.entityId ] = portlet ;
    },
    removePortlet: function( /* Portlet */ portlet )
    {
        if (! portlet || ! this.portlets) return ;
        delete this.portlets[ portlet.entityId ] ;
    },
    _destroyPortlets: function()
    {
        for (var portletIndex in this.portlets)
        {
            var portlet = this.portlets[portletIndex];
            portlet._destroy();
        }
    },
    debugDumpWindowStateAllPortlets: function()
    {
        dojo.debug("==== window-state all-portlets ====" );
        for (var portletIndex in this.portlets)
        {
            var portlet = this.portlets[portletIndex];
            var portletWindowState = portlet.getCurrentWindowState();
            var dumpMsg = "portlet " + portlet.entityId + " : ";    
            if ( ! portletWindowState )
                dumpMsg = dumpMsg + "null window state";
            else
            {   
                var propCount = 0;
                for (var propName in portletWindowState)
                {
                    if (propCount++ > 0) dumpMsg = dumpMsg + ", ";
                    dumpMsg = dumpMsg + propName + "=" + portletWindowState[propName];
                }
            }
            dojo.debug( dumpMsg );
        }
    },
    resetWindowLayout: function()
    {
        for (var portletIndex in this.portlets)
        {
            var portlet = this.portlets[portletIndex];
            portlet.submitChangedWindowState( false, true );
        }
        this.reload();
    },
    reload: function()
    {
        //this._destroyPortlets();
        this._removeColumns( document.getElementById( jetspeed.id.DESKTOP ) );
        jetspeed.loadPage();
    },

    // ... columns
    getColumnFromNode: function( /* DOM node */ columnNode )
    {
        if ( columnNode == null ) return null;
        var pageColumnIndexAttr = columnNode.getAttribute( "columnIndex" );
        if ( pageColumnIndexAttr == null ) return null;
        var pageColumnIndex = new Number( pageColumnIndexAttr );
        if ( pageColumnIndex >= 0 && pageColumnIndex < this.columns.length )
            return this.columns[ pageColumnIndex ];
        return null;
    },

    // ... portlet selector
    addNewPortlet: function( portletName, portletEntityId, windowWidgetId )
    {
        var portlet = new jetspeed.om.Portlet( portletName, portletEntityId ) ;
        if ( windowWidgetId )
            portlet.putProperty( jetspeed.id.PORTLET_PROP_WIDGET_ID, windowWidgetId );
        portlet.initialize();
        this.putPortlet( portlet ) ;
        portlet.retrieveContent();
    },
    removePortletFromPage: function( /* Portlet */ portlet )
    {
        var contentListener = new jetspeed.om.PortletAddAjaxApiCallbackContentListener( portletDef, windowWidgetId, false );
        var queryString = "?action=remove&id=" + escape( portletDef.getPortletName() );
        var addPortletUrl = jetspeed.page.getPsmlUrl() + queryString;
        var mimetype = "text/xml";
        var ajaxApiContext = new jetspeed.om.Id( "removeportlet", { } );
        jetspeed.url.retrieveContent( { url: addPortletUrl, mimetype: mimetype }, contentListener, ajaxApiContext, jetspeed.debugContentDumpIds );
    },

    // ... menus
    putMenu: function( /* jetspeed.om.Menu */ menuObj )
    {
        if ( ! menuObj ) return;
        var menuName = ( menuObj.getName ? menuObj.getName() : null );
        if ( ! menuName ) dojo.raise( "Page.addMenu argument is invalid - no menu-name can be found" );
        this.menus[ menuName ] = menuObj;
    },
    getMenu: function( /* String */ menuName )
    {
        if ( menuName == null ) return null;
        return this.menus[ menuName ];
    },
    removeMenu: function( /* String || jetspeed.om.Menu */ menuToRemove )
    {
        if ( menuToRemove == null ) return;
        var menuName = null;
        if ( dojo.lang.isString( menuToRemove ) )
            menuName = menuToRemove;
        else
            menuName = ( menuToRemove.getName ? menuToRemove.getName() : null );
        if ( menuName != null )
            delete this.menus[ menuName ] ;
    },
    clearMenus: function()
    {
        this.menus = [];
    },
    getMenuNames: function()
    {
        var menuNamesArray = [];
        for ( var menuName in this.menus )
        {
            menuNamesArray.push( menuName );
        }
        return menuNamesArray;
    },
    retrieveAllMenus: function()
    {
        var contentListener = new jetspeed.om.MenusAjaxApiCallbackContentListener( true );
        this.retrieveMenuDeclarations( contentListener );
    },
    retrieveMenuDeclarations: function( contentListener )
    {
        if ( contentListener == null )
            contentListener = new jetspeed.om.MenusAjaxApiContentListener( false );

        this.clearMenus();

        var queryString = "?action=getmenus";

        var psmlMenusActionUrl = this.getPsmlUrl() + queryString;
        var mimetype = "text/xml";

        var ajaxApiContext = new jetspeed.om.Id( "getmenus", { page: this } );

        jetspeed.url.retrieveContent( { url: psmlMenusActionUrl, mimetype: mimetype }, contentListener, ajaxApiContext, jetspeed.debugContentDumpIds );
    },
    retrieveMenu: function( /* String */ menuName, /* String */ menuType, contentListener )
    {
        if ( contentListener == null )
            contentListener = new jetspeed.om.MenuAjaxApiCallbackContentListener();
        var queryString = "?action=getmenu&name=" + menuName;

        var psmlMenuActionUrl = this.getPsmlUrl() + queryString;
        var mimetype = "text/xml";

        var ajaxApiContext = new jetspeed.om.Id( "getmenu-" + menuName, { page: this, menuName: menuName, menuType: menuType } );

        jetspeed.url.retrieveContent( { url: psmlMenuActionUrl, mimetype: mimetype }, contentListener, ajaxApiContext, jetspeed.debugContentDumpIds );
    },

    getPageUrl: function()
    {
        return jetspeed.url.path.SERVER + jetspeed.url.path.DESKTOP + this.getPath();
    },

    equalsPageUrl: function( url )
    {
        if ( url == this.getPath() )
            return true;
        if ( url == this.getPageUrl() )
            return true;
        return false;
    },

    makePageUrl: function( pathOrUrl )
    {
        if ( ! pathOrUrl ) pathOrUrl = "";
        if ( ! jetspeed.url.validateUrlStartsWithHttp( pathOrUrl ) )
            return jetspeed.url.path.SERVER + jetspeed.url.path.DESKTOP + pathOrUrl;
        return pathOrUrl;
    },


    // ... access
    getName: function()
    {
        return this.name;
    },
    getPath: function()
    {
        return this.path;
    },
    getTitle: function()
    {
        return this.title;
    },
    getShortTitle: function()
    {
        return this.shortTitle;
    },
    getLayoutDecorator: function()
    {
        return this.layoutDecorator;
    },
    getPortletDecorator : function()
    {
        return this.portletDecorator;
    }
});

// ... jetspeed.om.Column
jetspeed.om.Column = function( layoutColumnIndex, layoutId, size, pageColumnIndex )
{
    this.layoutColumnIndex = layoutColumnIndex;
    this.layoutId = layoutId;
    this.size = size;
    this.pageColumnIndex = new Number( pageColumnIndex );
    this.domNode = null;
};
dojo.inherits( jetspeed.om.Column, jetspeed.om.Id );
dojo.lang.extend( jetspeed.om.Column,
{
    layoutColumnIndex: null,
    layoutId: null,
    size: null,
    pageColumnIndex: null,
    domNode: null,

    createColumn: function( columnContainer )
    {
        var columnClass = "desktopColumn" ;
        if ( this.isStartOfColumnSet() && this.getPageColumnIndex() > 0 )
            columnClass = "desktopColumnClear" ;
        var divElmt = document.createElement( "div" );
        divElmt.setAttribute( "columnIndex", this.getPageColumnIndex() );
        divElmt.style.width = this.size + "%";
        divElmt.style.minHeight = "40px";
        divElmt.className = columnClass;
        this.domNode = divElmt;
        columnContainer.appendChild( divElmt );
    },
    containsNode: function( node )
    {
        var contains = false;
        if ( this.domNode != null && dojo.dom.isDescendantOf( node, this.domNode, true ) )
        {
            contains = true;
        }
        return contains;
    },
    isStartOfColumnSet: function()
    {
        return this.layoutColumnIndex == 0;
    },

    getId: function()  // jetspeed.om.Id protocol
    {
        return this.layoutId + "_" + this.layoutColumnIndex;
    },
    getLayoutId: function()
    {
        return this.layoutId;
    },
    getLayoutColumnIndex: function()
    {
        return this.layoutColumnIndex;
    },
    getSize: function()
    {
        return this.size;
    },
    getPageColumnIndex: function()
    {
        return this.pageColumnIndex;
    }
});

// ... jetspeed.om.Portlet
jetspeed.om.Portlet = function( portletName, portletEntityId, alternateContentRetriever, properties, actions, currentActionState, currentActionMode, decorator )
{   // new jetspeed.om.Portlet( pFrag.name, pFrag.id, alternateContentRetriever, pFrag.properties, pFrag.decorator, portletPageColumnIndex ) ;
    // BOZO:NOW: do something with decorator arg - this is the fragment decorator override
    this.name = portletName;
    this.entityId = portletEntityId;
    if ( properties )
        this.properties = properties;
    else
        this.properties = {};
    
    if ( actions )
        this.actions = actions;
    else
        this.actions = {};

    this.currentActionState = currentActionState;
    this.currentActionMode = currentActionMode;

    if ( alternateContentRetriever )
        this.contentRetriever = alternateContentRetriever;
};
dojo.inherits( jetspeed.om.Portlet, jetspeed.om.Id);
dojo.lang.extend( jetspeed.om.Portlet,
{
    name: null,
    entityId: null,

    pageColumnIndex: null,
    
    contentRetriever: new jetspeed.om.PortletContentRetriever(),
    
    windowFactory: null,

    lastSavedWindowState: null,

    JAVASCRIPT_ACTION_PREFIX: "javascript:doAction",
    JAVASCRIPT_RENDER_PREFIX: "javascript:doRender",
    JAVASCRIPT_ARG_QUOTE: "&" + "quot;",
    PORTLET_REQUEST_ACTION: "action",
    PORTLET_REQUEST_RENDER: "render",
    
    initialize: function()
    {   // must be called once init sensitive putProperty calls are complete
        if ( ! this.getProperty( jetspeed.id.PORTLET_PROP_WIDGET_ID ) )
        {
            this.putProperty( jetspeed.id.PORTLET_PROP_WIDGET_ID, jetspeed.id.PORTLET_WINDOW_ID_PREFIX + this.entityId );
        }
        if ( ! this.getProperty( jetspeed.id.PORTLET_PROP_CONTENT_RETRIEVER ) )
        {
            this.putProperty( jetspeed.id.PORTLET_PROP_CONTENT_RETRIEVER, this.contentRetriever );
        }

        var posStatic = this.getProperty( jetspeed.id.PORTLET_PROP_WINDOW_POSITION_STATIC );
        if ( jetspeed.prefs.windowTiling )
        {
            if ( posStatic == "true" )
                posStatic = true;
            else if ( posStatic == "false" )
                posStatic = false;
            else if ( posStatic != true && posStatic != false )
                posStatic = true;
        }
        else
        {
            posStatic = false;
        }
        this.putProperty( jetspeed.id.PORTLET_PROP_WINDOW_POSITION_STATIC, posStatic );

        var heightToFit = this.getProperty( jetspeed.id.PORTLET_PROP_WINDOW_HEIGHT_TO_FIT );
        if ( heightToFit == "true" )
            heightToFit = true;
        else if ( posStatic == "false" )
            heightToFit = false;
        else if ( heightToFit != true && heightToFit != false )
            heightToFit = true;
        this.putProperty( jetspeed.id.PORTLET_PROP_WINDOW_HEIGHT_TO_FIT, heightToFit );

        var windowtitle = this.getProperty( jetspeed.id.PORTLET_PROP_WINDOW_TITLE );
        if ( ! windowtitle && this.name )
        {
            var re = (/^[^:]*:*/);
            windowtitle = this.name.replace( re, "" );
            this.putProperty( jetspeed.id.PORTLET_PROP_WINDOW_TITLE, windowtitle );
        }
    },

    parseJSPseudoUrlActionRender: function( /* String */ javascriptPseudoUrl )
    {
        var op = null;
        var justTheUrl = javascriptPseudoUrl;
        var entityId = null;
        var argsSuffix = null;
        if ( javascriptPseudoUrl && javascriptPseudoUrl.length > this.JAVASCRIPT_ACTION_PREFIX.length && javascriptPseudoUrl.indexOf( this.JAVASCRIPT_ACTION_PREFIX ) == 0 )
        {   // annotate away javascript invocation in form action
            justTheUrl = null;
            op = this.PORTLET_REQUEST_ACTION;
            argsSuffix = javascriptPseudoUrl.substring( this.JAVASCRIPT_ACTION_PREFIX.length );
        }
        else if ( javascriptPseudoUrl && javascriptPseudoUrl.length > this.JAVASCRIPT_RENDER_PREFIX.length && javascriptPseudoUrl.indexOf( this.JAVASCRIPT_RENDER_PREFIX ) == 0 )
        {
            justTheUrl = null;
            op = this.PORTLET_REQUEST_RENDER;
            argsSuffix = javascriptPseudoUrl.substring( this.JAVASCRIPT_RENDER_PREFIX.length );
        }
        if ( argsSuffix )
        {
            var quoteDelim = "\"";
            var argsEnd = argsSuffix.lastIndexOf( quoteDelim );
            var altargsEnd = argsSuffix.lastIndexOf( this.JAVASCRIPT_ARG_QUOTE );
            if ( altargsEnd > argsEnd )
            {
                quoteDelim = this.JAVASCRIPT_ARG_QUOTE;
                argsEnd = altargsEnd;
            }
            if ( argsEnd >= 0 )
            {
                argsSuffix = dojo.string.trim( argsSuffix.substring( 0, argsEnd + quoteDelim.length ) );
                var argsData = argsSuffix.split( quoteDelim );
                if ( argsData && argsData.length >=4 )
                {
                    justTheUrl = argsData[1];
                    entityId = argsData[3];
                }
            }
        }
        else
        {
            op = null;
        }
        
        if ( ! jetspeed.url.validateUrlStartsWithHttp( justTheUrl ) )
            justTheUrl = null;

        return { url: justTheUrl, operation: op, portletEntityId: entityId };
    },

    preParseAnnotateHtml: function( /* String */ portletContent )
    {
        return jetspeed.ui.preParseAnnotateHtml( portletContent );
    },
    postParseAnnotateHtml: function( /* DOMNode */ containerNode )
    {   
        if ( containerNode )
        {
            var cNode = containerNode;
            var formList = cNode.getElementsByTagName( "form" );
            var debugOn = jetspeed.debug.postParseAnnotateHtml;
            if ( formList )
            {
                for ( var i = 0 ; i < formList.length ; i++ )
                {
                    var cForm = formList[i];                    
                    var cFormAction = cForm.action;
                    var cFormPortletEntityId = this.entityId;  // BOZO:can I assume that it is always my entity-id (ignoring the one in parsedPseudoUrl)

                    var parsedPseudoUrl = this.parseJSPseudoUrlActionRender( cFormAction );
                    
                    var submitOperation = parsedPseudoUrl.operation;

                    if ( submitOperation == this.PORTLET_REQUEST_ACTION || submitOperation == this.PORTLET_REQUEST_RENDER )
                    {
                        //var replacementActionUrl = parsedPseudoUrl.url; 
                        var replacementActionUrl = this._generateJSPseudoUrlActionRender( parsedPseudoUrl, true );
                        cForm.action = replacementActionUrl;

                        var formBind = new jetspeed.om.ActionRenderFormBind( cForm, parsedPseudoUrl.url, parsedPseudoUrl.portletEntityId, submitOperation );
                        //  ^^^ formBind serves as an event hook up - retained ref is not needed
                        
                        if ( debugOn )
                            dojo.debug( "postParseAnnotateHtml [" + this.entityId + "] adding FormBind (portlet-" + submitOperation + ") and setting form action to: " + replacementActionUrl );
                    }
                    else
                    {
                        if ( djConfig.isDebug )
                            dojo.debug( "postParseAnnotateHtml [" + this.entityId + "] form action attribute doesn't match annotation criteria, leaving as is: " + cFormAction ) ;
                    }
                }
            }
            var aList = cNode.getElementsByTagName( "a" );
            if ( aList )
            {
                for ( var i = 0 ; i < aList.length ; i++ )
                {
                    var aNode = aList[i];
                    var aHref = aNode.href;
                    
                    var parsedPseudoUrl = this.parseJSPseudoUrlActionRender( aHref );
                    var replacementHref = this._generateJSPseudoUrlActionRender( parsedPseudoUrl );

                    if ( ! replacementHref )
                    {
                        if ( debugOn )
                            dojo.debug( "postParseAnnotateHtml [" + this.entityId + "] leaving href as is: " + aHref );
                    }
                    else if ( replacementHref == aHref )
                    {
                        if ( debugOn )
                            dojo.debug( "postParseAnnotateHtml [" + this.entityId + "] href parsed and regenerated identically: " + aHref );
                    }
                    else
                    {
                        if ( debugOn )
                            dojo.debug( "postParseAnnotateHtml [" + this.entityId + "] href parsed, replacing: " + aHref + " with: " + replacementHref );
                        aNode.href = replacementHref;
                    }
                }
            }
        }
    },

    _generateJSPseudoUrlActionRender: function( parsedPseudoUrl, makeDummy )
    {   // NOTE: no form can be passed in one of these
        if ( ! parsedPseudoUrl || ! parsedPseudoUrl.url || ! parsedPseudoUrl.portletEntityId ) return null;
        var hrefJScolon = "javascript:";
        var badnews = false;
        if ( makeDummy )
            hrefJScolon += "jetspeed.doNothingNav(";
        else if ( parsedPseudoUrl.operation == this.PORTLET_REQUEST_ACTION )
            hrefJScolon += "doAction(\"";
        else if ( parsedPseudoUrl.operation == this.PORTLET_REQUEST_RENDER )
            hrefJScolon += "doRender(\"";
        else badnews = true;
        if ( badnews ) return null;
        if ( ! makeDummy )
            hrefJScolon += parsedPseudoUrl.url + "\",\"" + parsedPseudoUrl.portletEntityId + "\"";
        hrefJScolon += ")";
        return hrefJScolon;
    },

    getPortletWindow: function()
    {
        var windowWidgetId = this.getProperty( jetspeed.id.PORTLET_PROP_WIDGET_ID );
        if ( windowWidgetId )
            return dojo.widget.byId( windowWidgetId );
        return null;
    },
    
    getCurrentWindowState: function( /* boolean */ volatileOnly )
    {
        var windowWidget = this.getPortletWindow();
        if ( ! windowWidget ) return null;

        var currentState = null;
        if ( volatileOnly )
            currentState = windowWidget.getCurrentVolatileWindowState();
        else
        {
            currentState = windowWidget.getCurrentWindowState();
            if ( currentState.layout == null )   // should happen only if windowPositionStatic == false
                currentState.layout = this.lastSavedWindowState.layout;
        }

        // get rid of units text
        this._purifyWindowStatePropertyAsNumber( currentState, "left" );
        this._purifyWindowStatePropertyAsNumber( currentState, "top" );
        this._purifyWindowStatePropertyAsNumber( currentState, "width" );
        this._purifyWindowStatePropertyAsNumber( currentState, "height" );

        return currentState;
    },
    _purifyWindowStatePropertyAsNumber: function( windowState, propName )
    {
        var source = windowState[ propName ];
        if ( source != null )
        {
            var sourceNum = "";
            for ( var i = 0 ; i < source.length ; i++ )
            {
                var sourceCh = source.charAt(i);
                if ( ( sourceCh >= "0" && sourceCh <= "9" ) || sourceCh == "." )
                    sourceNum += sourceCh.toString();
            }
            windowState[ propName ] = sourceNum;
        }
    },
    getLastSavedWindowState: function()
    {
        if ( ! this.lastSavedWindowState )
            dojo.raise( "portlet.getLastSavedWindowState() is null - portlet not properly initialized." );
        return this.lastSavedWindowState;
    },
    getInitialWindowDimensions: function( dimensionsObj, reset )
    {
        if ( ! dimensionsObj )
            dimensionsObj = {};

        var windowPositionStatic = this.getProperty( jetspeed.id.PORTLET_PROP_WINDOW_POSITION_STATIC );
        var windowHeightToFit = this.getProperty( jetspeed.id.PORTLET_PROP_WINDOW_HEIGHT_TO_FIT );
        
        dimensionsObj[ jetspeed.id.PORTLET_PROP_WINDOW_POSITION_STATIC ] = windowPositionStatic;
        dimensionsObj[ jetspeed.id.PORTLET_PROP_WINDOW_HEIGHT_TO_FIT ] = windowHeightToFit;
        
        var portletWidth = this.getProperty( "width" );
        if ( ! reset && portletWidth != null && portletWidth > 0 )
            dimensionsObj.width = Math.floor( portletWidth );
        else if ( reset )
            dimensionsObj.width = -1;
    
        var portletHeight = this.getProperty( "height" );
        if ( ! reset && portletHeight != null && portletHeight > 0  )
            dimensionsObj.height = Math.floor( portletHeight );
        else if ( reset )
            dimensionsObj.height = -1;

        if ( ! windowPositionStatic || ! jetspeed.prefs.windowTiling )
        {
            var portletLeft = this.getProperty( "x" );
            if ( ! reset && portletLeft != null && portletLeft >= 0 )
                dimensionsObj.left = Math.floor( ( (portletLeft > 0) ? portletLeft : 0 ) );
            else if ( reset )
                dimensionsObj.left = -1;

            var portletTop = this.getProperty( "y" );
            if ( ! reset && portletTop != null && portletTop >= 0 )
                dimensionsObj.top = Math.floor( ( (portletTop > 0) ? portletTop : 0 ) );
            else
                dimensionsObj.top = -1;

            var initialZIndex = this._getInitialZIndex( reset );
            if ( initialZIndex != null )
                dimensionsObj.zIndex = initialZIndex;
        }
        return dimensionsObj;
    },
    _initializeWindowState: function( portletInitialColRow, /* boolean */ reset )
    {   // portletInitialColRow: { layout: currentLayoutId, column: colObj.getLayoutColumnIndex(), row: colCurrentRow }
        var initialWindowState = ( portletInitialColRow ? portletInitialColRow : {} );    // BOZO:NOW: support reset argument (?)
        
        this.getInitialWindowDimensions( initialWindowState, reset );

        if ( jetspeed.debug.initializeWindowState )
        {
            var windowPositionStatic = this.getProperty( jetspeed.id.PORTLET_PROP_WINDOW_POSITION_STATIC );
            if ( ! windowPositionStatic || ! jetspeed.prefs.windowTiling )
                dojo.debug( "initializeWindowState [" + this.entityId + "] z=" + initialWindowState.zIndex + " x=" + initialWindowState.left + " y=" + initialWindowState.top + " width=" + initialWindowState.width + " height=" + initialWindowState.height );
            else
                dojo.debug( "initializeWindowState [" + this.entityId + "] column=" + initialWindowState.column + " row=" + initialWindowState.row + " width=" + initialWindowState.width + " height=" + initialWindowState.height );
        }

        this.lastSavedWindowState = initialWindowState;

        return initialWindowState;
    },
    _getInitialZIndex: function( /* boolean */ reset )
    {
        var zIndex = null;
        var portletZIndex = this.getProperty( "z" );
        if ( ! reset && portletZIndex != null && portletZIndex >= 0 )
            zIndex = Math.floor( portletZIndex );
        else if ( reset )
            zIndex = -1;
        return zIndex;
    },
    _getChangedWindowState: function( /* boolean */ volatileOnly )
    {
        var lastSaved = this.getLastSavedWindowState();
        
        if ( lastSaved && dojo.lang.isEmpty( lastSaved ) )
        {
            lastSaved = null;
            volatileOnly = false;  // so that current state we obtain is the full representation
        }
        
        var currentState = this.getCurrentWindowState( volatileOnly );
        var windowPositionStatic = currentState[ jetspeed.id.PORTLET_PROP_WINDOW_POSITION_STATIC ];
        var zIndexTrack = ! windowPositionStatic;
        if ( ! lastSaved )
        {
            var result = { state: currentState, positionChanged: true, extendedPropChanged: true };
            if ( zIndexTrack )
                result.zIndexChanged = true;   // BOZO: this could lead to an early submission for each portlet (may not be too cool?)
            return result;
        }
        
        var hasChange = false;
        var positionChange = false;
        var extendedPropChange = false;
        var zIndexChange = false;
        for (var stateKey in currentState)
        {
            //if ( stateKey == "zIndex" )
            //    dojo.debug( "portlet zIndex compare [" + this.entityId + "]  " + ( currentState[stateKey] ? currentState[stateKey] : "null" ) + " != " + ( lastSaved[stateKey] ? lastSaved[stateKey] : "null" ) );
            if ( currentState[stateKey] != lastSaved[stateKey] )
            {
                //dojo.debug( "portlet [" + this.entityId + "] windowstate changed: " + stateKey + "  " + ( currentState[stateKey] ? currentState[stateKey] : "null" ) + " != " + ( lastSaved[stateKey] ? lastSaved[stateKey] : "null" ) ) ;

                if ( stateKey == jetspeed.id.PORTLET_PROP_WINDOW_POSITION_STATIC || stateKey == jetspeed.id.PORTLET_PROP_WINDOW_HEIGHT_TO_FIT )
                {
                    hasChange = true;
                    extendedPropChange = true;
                    positionChange = true;
                }
                else if ( stateKey == "zIndex" )
                {
                    if ( zIndexTrack )
                    {
                        hasChange = true;
                        zIndexChange = true;
                    }
                }
                else
                {
                    hasChange = true;
                    positionChange = true;
                }
            }
        }
        if ( hasChange )
        {
            var result = { state: currentState, positionChanged: positionChange, extendedPropChanged: extendedPropChange };
            if ( zIndexTrack )
                result.zIndexChanged = zIndexChange;
            return result;
        }
        return null;
    },

    createPortletWindow: function( columnIndex )
    {
        jetspeed.ui.createPortletWindow( this, columnIndex );
    },

    getPortletUrl: function( bindArgs )
    {
        var queryString = "";
        if ( ! bindArgs.dontAddQueryArgs )
            queryString += "?entity=" + this.entityId + "&portlet=" + this.name + "&encoder=desktop";
        var modUrl = null;
        if ( bindArgs && bindArgs.url )
        {
            modUrl = bindArgs.url + queryString;
        }
        else if ( bindArgs && bindArgs.formNode )
        {
            var formAction = bindArgs.formNode.getAttribute( "action" );
            if ( formAction )
                modUrl = formAction + queryString;
        }
        if ( modUrl == null )
            modUrl = jetspeed.url.basePortalUrl() + jetspeed.url.path.PORTLET + queryString;
        if ( bindArgs )
            bindArgs.url = modUrl;
        return modUrl;
    },

    _submitJetspeedAjaxApi: function( /* String */ action, /* String */ queryStringFragment, contentListener )
    {
        var queryString = "?action=" + action + "&id=" + this.entityId + queryStringFragment;

        var psmlMoveActionUrl = jetspeed.page.getPsmlUrl() + queryString;
        var mimetype = "text/xml";

        var ajaxApiContext = new jetspeed.om.Id( action, this.entityId );
        ajaxApiContext.portlet = this;

        jetspeed.url.retrieveContent( { url: psmlMoveActionUrl, mimetype: mimetype }, contentListener, ajaxApiContext, null );
    },

    submitChangedWindowState: function( /* boolean */ volatileOnly, /* boolean */ reset )
    {
        var changedStateResult = null;
        if ( reset )
            changedStateResult = { state: this._initializeWindowState( null, true ) };
        else
            changedStateResult = this._getChangedWindowState( volatileOnly );
        if ( changedStateResult )
        {
            var changedState = changedStateResult.state;
            
            var windowPositionStatic = changedState[ jetspeed.id.PORTLET_PROP_WINDOW_POSITION_STATIC ];
            var windowHeightToFit = changedState[ jetspeed.id.PORTLET_PROP_WINDOW_HEIGHT_TO_FIT ];

            var windowExtendedProperty = null;
            if ( changedStateResult.extendedPropChanged )
            {
                var propSep = jetspeed.id.PORTLET_PROP_DESKTOP_EXTENDED_PROP_SEPARATOR;
                var pairSep = jetspeed.id.PORTLET_PROP_DESKTOP_EXTENDED_PAIR_SEPARATOR;
                windowExtendedProperty = jetspeed.id.PORTLET_PROP_DESKTOP_EXTENDED_STATICPOS + propSep + windowPositionStatic.toString();
                windowExtendedProperty += pairSep + jetspeed.id.PORTLET_PROP_DESKTOP_EXTENDED_FITHEIGHT + propSep + windowHeightToFit.toString();
                windowExtendedProperty = escape( windowExtendedProperty );
            }

            var queryStringFragment = "";
            var action = null;
            if ( windowPositionStatic )
            {
                action = "moveabs";
                if ( changedState.column != null )
                    queryStringFragment += "&col=" + changedState.column;
                if ( changedState.row != null )
                    queryStringFragment += "&row=" + changedState.row;
                if ( changedState.layout != null )
                    queryStringFragment += "&layoutid=" + changedState.layout;
                if ( changedState.height != null )
                    queryStringFragment += "&height=" + changedState.height;
            }
            else
            {
                action = "move";
                if ( changedState.zIndex != null )
                    queryStringFragment += "&z=" + changedState.zIndex;
                if ( changedState.width != null )
                    queryStringFragment += "&width=" + changedState.width;
                if ( changedState.height != null )
                    queryStringFragment += "&height=" + changedState.height;
                if ( changedState.left != null )
                    queryStringFragment += "&x=" + changedState.left;
                if ( changedState.top != null )
                    queryStringFragment += "&y=" + changedState.top;
            }
            if ( windowExtendedProperty != null )
                queryStringFragment += "&" + jetspeed.id.PORTLET_PROP_DESKTOP_EXTENDED + "=" + windowExtendedProperty;

            this._submitJetspeedAjaxApi( action, queryStringFragment, new jetspeed.om.MoveAjaxApiContentListener( changedState ) );

            if ( ! volatileOnly && ! reset )
            {
                if ( ! windowPositionStatic && changedStateResult.zIndexChanged )  // current condition for whether 
                {                                                                         // volatile (zIndex) changes are possible
                    var portletArrayList = jetspeed.page.getPortletArrayList();
                    var autoUpdatePortlets = dojo.collections.Set.difference( portletArrayList, [ this ] );
                    if ( ! portletArrayList || ! autoUpdatePortlets || ((autoUpdatePortlets.count + 1) != portletArrayList.count) )
                        dojo.raise( "Portlet.submitChangedWindowState invalid conditions for starting auto update" );
                    else if ( autoUpdatePortlets && autoUpdatePortlets.count > 0 )
                    {
                        dojo.lang.forEach( autoUpdatePortlets.toArray(),
                                function( portlet ) { if ( portlet.getProperty( jetspeed.id.PORTLET_PROP_WINDOW_POSITION_STATIC ) ) portlet.submitChangedWindowState( true ); } );
                    }
                }
                else if ( windowPositionStatic )
                {
                    // moveapi submission adjusts other portlets that have had their row changed because of portlet inserted or removed from higher row
                }
            }
        }
    },

    retrieveContent: function( contentListener, bindArgs, suppressGetActions )
    {
        if ( contentListener == null )
            contentListener = new jetspeed.om.PortletContentListener( suppressGetActions );

        if ( ! bindArgs )
            bindArgs = {};
        
        var portlet = this ;
        portlet.getPortletUrl( bindArgs ) ;
        
        this.contentRetriever.getContent( bindArgs, contentListener, portlet, jetspeed.debugContentDumpIds );
    },
    setPortletContent: function( portletContent, renderUrl )
    {
        var windowWidget = this.getPortletWindow();
        if ( windowWidget )
        {
            windowWidget.setPortletContent( portletContent, renderUrl );
        }
    },

    getId: function()  // jetspeed.om.Id protocol
    {
        return this.entityId;
    },

    putProperty: function( name, value )
    {
        this.properties[ name ] = value;
    },
    getProperty: function( name )
    {
        return this.properties[ name ];
    },
    removeProperty: function( name )
    {
        delete this.properties[ name ];
    },

    renderAction: function( actionName )
    {
        if ( actionName == null ) return;
        var action = this.getAction( actionName );
        if ( action == null ) return;
        if ( action.url == null ) return;
        var renderActionUrl = jetspeed.url.basePortalUrl() + jetspeed.url.path.PORTLET + "/" + action.url;
        this.retrieveContent( null, { url: renderActionUrl } );
    },
    getAction: function( name )
    {
        if ( this.actions == null ) return null;
        return this.actions[ name ];
    },
    getCurrentActionState: function()
    {
        return this.currentActionState;
    },
    getCurrentActionMode: function()
    {
        return this.currentActionMode;
    },
    updateActions: function( actions, currentActionState, currentActionMode )
    {
        if ( actions )
            this.actions = actions;
        else
            this.actions = {};

        this.currentActionState = currentActionState;
        this.currentActionMode = currentActionMode;
        var windowWidget = this.getPortletWindow();
        if ( windowWidget )
        {
            windowWidget.windowActionButtonSync();
        }
    },

    _destroy: function()
    {
        var windowWidget = this.getPortletWindow();
        if ( windowWidget )
        {
            windowWidget.closeWindow();
        }
    }
});

jetspeed.om.ActionRenderFormBind = function( /* HtmlForm */ form, /* String */ url, /* String */ portletEntityId, /* String */ submitOperation )
{
    dojo.io.FormBind.call( this, { url: url, formNode: form } );

    this.entityId = portletEntityId;
    this.submitOperation = submitOperation;
};
dojo.inherits( jetspeed.om.ActionRenderFormBind, dojo.io.FormBind );
dojo.lang.extend( jetspeed.om.ActionRenderFormBind,
{

    init: function(args) {
        var form = dojo.byId(args.formNode);

        if(!form || !form.tagName || form.tagName.toLowerCase() != "form") {
            throw new Error("FormBind: Couldn't apply, invalid form");
        } else if(this.form == form) {
            return;
        } else if(this.form) {
            throw new Error("FormBind: Already applied to a form");
        }

        dojo.lang.mixin(this.bindArgs, args);
        this.form = form;

        this.connect(form, "onsubmit", "submit");

        for(var i = 0; i < form.elements.length; i++) {
            var node = form.elements[i];
            if(node && node.type && dojo.lang.inArray(["submit", "button"], node.type.toLowerCase())) {
                this.connect(node, "onclick", "click");
            }
        }

        var inputs = form.getElementsByTagName("input");
        for(var i = 0; i < inputs.length; i++) {
            var input = inputs[i];
            if(input.type.toLowerCase() == "image" && input.form == form) {
                this.connect(input, "onclick", "click");
            }
        }

        var as = form.getElementsByTagName("a");
        for(var i = 0; i < as.length; i++) {
            dojo.event.connectBefore(as[i], "onclick", this, "click");
        }
    },

    onSubmit: function( cForm )
    {
        var proceed = true;
        if ( jetspeed.debug.confirmOnSubmit )
        {
            if ( ! confirm( "Click OK to submit." ) )
            {
                proceed = false;
            }
        }
        return proceed;
    },

    submit: function( e )
    {
        if ( e )
		    e.preventDefault();
		if( this.onSubmit( this.form ) )
        {
            if ( this.submitOperation == this.PORTLET_REQUEST_RENDER )
            {
                doRender( dojo.lang.mixin(this.bindArgs, {
				              formFilter: dojo.lang.hitch( this, "formFilter" )
			              }),
                          this.entityId );
            }
            else
            {
                doAction( dojo.lang.mixin(this.bindArgs, {
				              formFilter: dojo.lang.hitch( this, "formFilter" )
			              }),
                          this.entityId );
            }
		}
	}
});

// ... jetspeed.om.PortletDef
jetspeed.om.PortletDef = function( /* String */ portletName, /* String */ portletDisplayName, /* String */ portletDescription, /* String */ portletImage)
{
    this.portletName = portletName;
    this.portletDisplayName = portletDisplayName;
    this.portletDescription = portletDescription;
    this.image = portletImage;
};
dojo.inherits( jetspeed.om.PortletDef, jetspeed.om.Id);
dojo.lang.extend( jetspeed.om.PortletDef,
{
    portletName: null,
    portletDisplayName: null,
    portletDescription: null,
    portletImage: null,
    getId: function()  // jetspeed.om.Id protocol
    {
        return this.portletName;
    },
    getPortletName: function()
    {
        return this.portletName;
    },
    getPortletDisplayName: function()
    {
        return this.portletDisplayName;
    },
    getPortletDescription: function()
    {
        return this.portletDescription;
    }
});

// ... jetspeed.om.BasicContentListener
jetspeed.om.BasicContentListener = function()
{
};
jetspeed.om.BasicContentListener.prototype =
{
    notifySuccess: function( /* String */ content, /* String */ requestUrl, domainModelObject )
    {
        var windowWidgetId = domainModelObject.getProperty( jetspeed.id.PORTLET_PROP_WIDGET_ID );
        if ( windowWidgetId )
        {
            var windowWidget = dojo.widget.byId( windowWidgetId );
            if ( windowWidget )
            {
                windowWidget.setPortletContent( content, requestUrl );
            }
        }
    },
    notifyFailure: function( /* String */ type, /* Object */ error, /* String */ requestUrl, domainModelObject )
    {
        dojo.raise( "BasicContentListener notifyFailure url=" + requestUrl + " type=" + type + " error=" + error ) ;
    }
};

// ... jetspeed.om.PortletContentListener
jetspeed.om.PortletContentListener = function( suppressGetActions )
{
    this.suppressGetActions = suppressGetActions;
};
jetspeed.om.PortletContentListener.prototype =
{
    notifySuccess: function( /* String */ portletContent, /* String */ requestUrl, /* Portlet */ portlet )
    {
        portlet.setPortletContent( portletContent, requestUrl );
        if ( this.suppressGetActions == null || this.suppressGetActions == false )
            jetspeed.getActionsForPortlet( portlet.getId() );
    },
    notifyFailure: function( /* String */ type, /* Object */ error, /* String */ requestUrl, /* Portlet */ portlet )
    {
        dojo.raise( "PortletContentListener notifyFailure url=" + requestUrl + " type=" + type + " error=" + error ) ;
    }
};

// ... jetspeed.om.PortletActionContentListener
jetspeed.om.PortletActionContentListener = function()
{
};
jetspeed.om.PortletActionContentListener.prototype =
{
    notifySuccess: function( /* String */ portletContent, /* String */ requestUrl, /* Portlet */ portlet )
    {
        var renderUrl = null;
        var parsedPseudoUrl = portlet.parseJSPseudoUrlActionRender( portletContent );
        if ( parsedPseudoUrl.operation == portlet.PORTLET_REQUEST_ACTION || parsedPseudoUrl.operation == portlet.PORTLET_REQUEST_RENDER )
        {
            dojo.debug( "PortletActionContentListener extracted from javascript-pseudo-url: " + portletContent + "  url: " + parsedPseudoUrl.url + " operation: " + parsedPseudoUrl.operation + " entity-id: " + parsedPseudoUrl.portletEntityId ) ;
            renderUrl = parsedPseudoUrl.url;
        }
        else
        {
            dojo.debug( "PortletActionContentListener: " + portletContent ) ;
            renderUrl = portletContent;
        }
        if ( renderUrl )
        {
            jetspeed.doRenderAll( renderUrl );    // render all portlets
            //  portlet.retrieveContent(null, { url: renderUrl } );    // render just the one portlet
        }
    },
    notifyFailure: function( /* String */ type, /* Object */ error, /* String */ requestUrl, /* Portlet */ portlet )
    {
        dojo.raise( "PortletActionContentListener notifyFailure type=" + type ) ;
        dojo.debugShallow( error );
    }
};

// ... jetspeed.om.MenuOption
jetspeed.om.MenuOption = function()
{
};
dojo.lang.extend( jetspeed.om.MenuOption,
{
    // operations
    navigateTo: function()
    {
        if ( this.isLeaf() )
        {
            var navUrl = this.getUrl();
            if ( navUrl )
            {
                jetspeed.pageNavigate( navUrl, this.getTarget() );
            }
        }
    },
    navigateUrl: function()
    {
        return jetspeed.page.makePageUrl( this.getUrl() );
    },

    // data
    getType: function()
    {
        return this.type;
    },
    getTitle: function()
    {
        return this.title;
    },
    getShortTitle: function()
    {
        return this[ "short-title" ];
    },
    getSkin: function()
    {
        return this.skin;
    },
    getUrl: function()
    {
        return this.url;
    },
    getTarget: function()
    {
        return this.target;
    },
    getHidden: function()
    {
        return this.hidden;
    },
    getSelected: function()
    {
        return this.selected;
    },
    getText: function()
    {
        return this.text;
    },
    isLeaf: function()
    {
        return true;
    },
    isMenu: function()
    {
        return false;
    },
    isSeparator: function()
    {
        return false;
    }
});
// ... jetspeed.om.MenuOptionSeparator
jetspeed.om.MenuOptionSeparator = function()
{
};
dojo.inherits( jetspeed.om.MenuOptionSeparator, jetspeed.om.MenuOption);
dojo.lang.extend( jetspeed.om.MenuOptionSeparator,
{
    isSeparator: function()
    {
        return true;
    }
});
// ... jetspeed.om.Menu
jetspeed.om.Menu = function( /* String */ menuName, /* String */ menuType )
{
    this._is_parsed = false;
    this.name = menuName;
    this.type = menuType;
};
dojo.inherits( jetspeed.om.Menu, jetspeed.om.MenuOption);
dojo.lang.extend( jetspeed.om.Menu,
{
    setParsed: function()
    {
        this._is_parsed = true;
    },
    isParsed: function()
    {
        return this._is_parsed;
    },
    getName: function()
    {
        return this.name;
    },
    addOption: function( /* MenuOption */ menuOptionObj )
    {
        if ( ! menuOptionObj ) return;
        if ( ! this.options )
            this.options = new Array();
        this.options.push( menuOptionObj );
    },
    getOptions: function()
    {
        var tAry = new Array();
        return ( this.options ? tAry.concat( this.options ) : tAry );
    },
    getOptionByIndex: function( optionIndex )
    {
        if ( ! this.hasOptions() ) return null;
        if ( optionIndex == 0 || optionIndex > 0 )
        {
            if ( optionIndex >= this.options.length )
                dojo.raise( "Menu.getOptionByIndex argument index out of bounds" );
            else
                return this.options[ optionIndex ];
        }
    },
    hasOptions: function()
    {
        return ( ( this.options && this.options.length > 0 ) ? true : false );
    },
    isMenu: function()
    {
        return true;
    },
    isLeaf: function()
    {
        return false;
    },
    hasNestedMenus: function()
    {
        if ( ! this.options ) return false;
        for ( var i = 0; i < this.options.length ; i++ )
        {
            var mOptObj = this.options[i];
            if ( mOptObj instanceof jetspeed.om.Menu )
                return true;
        }
        return false;
    }
    
});

// ... jetspeed.om.MenusAjaxApiContentListener
jetspeed.om.MenusAjaxApiContentListener = function( /* boolean */ retrieveEachMenu )
{
    this.retrieveEachMenu = retrieveEachMenu;
};
dojo.lang.extend( jetspeed.om.MenusAjaxApiContentListener,
{
    notifySuccess: function( /* XMLDocument */ data, /* String */ requestUrl, domainModelObject )
    {
        var menuDefs = this.getMenuDefs( data, requestUrl, domainModelObject );
        var menuContentListener = new jetspeed.om.MenuAjaxApiContentListener( this, menuDefs.length )
        for ( var i = 0 ; i < menuDefs.length; i++ )
        {
            var mObj = menuDefs[i];
            domainModelObject.page.putMenu( mObj );
            if ( this.retrieveEachMenu )
            {
                domainModelObject.page.retrieveMenu( mObj.getName(), mObj.getType(), menuContentListener );
            }
            else if ( i == (menuDefs.length -1) )
            {
                if ( dojo.lang.isFunction( this.notifyFinished ) )
                {
                    this.notifyFinished( domainModelObject );
                }
            }
        }  
    },
    getMenuDefs: function( /* XMLDocument */ data, /* String */ requestUrl, domainModelObject )
    {
        var menuDefs = [];
        var menuDefElements = data.getElementsByTagName( "menu" );
        for( var i = 0; i < menuDefElements.length; i++ )
        {
            var menuType = menuDefElements[i].getAttribute( "type" );
            var menuName = menuDefElements[i].firstChild.nodeValue;
            menuDefs.push( new jetspeed.om.Menu( menuName, menuType ) );
        }
        return menuDefs;
    },
    
    notifyFailure: function( /* String */ type, /* Object */ error, /* String */ requestUrl, domainModelObject )
    {
        dojo.raise( "MenusAjaxApiContentListener error [" + domainModelObject.toString() + "] url: " + requestUrl + " type: " + type + jetspeed.url.formatBindError( error ) );
    }
});

// ... jetspeed.om.MenusAjaxApiCallbackContentListener
jetspeed.om.MenusAjaxApiCallbackContentListener = function( /* boolean */ retrieveEachMenu )
{
    jetspeed.om.MenusAjaxApiContentListener.call( this, retrieveEachMenu );
};
dojo.inherits( jetspeed.om.MenusAjaxApiCallbackContentListener, jetspeed.om.MenusAjaxApiContentListener );
dojo.lang.extend( jetspeed.om.MenusAjaxApiCallbackContentListener,
{
    notifyFinished: function( domainModelObject )
    {
        jetspeed.notifyRetrieveAllMenusFinished();
    }
});

// ... jetspeed.om.MenuAjaxApiContentListener
jetspeed.om.MenuAjaxApiContentListener = function( /* jetspeed.om.MenusAjaxApiContentListener */ parentNotifyFinishedListener, /* int */ parentNotifyFinishedAfterIndex )
{
    this.parentNotifyFinishedListener = parentNotifyFinishedListener;
    this.parentNotifyFinishedAfterIndex = parentNotifyFinishedAfterIndex;
    this.parentNotified = false;
    this.notifyCount = 0;
};
dojo.lang.extend( jetspeed.om.MenuAjaxApiContentListener,
{
    notifySuccess: function( /* XMLDocument */ data, /* String */ requestUrl, domainModelObject )
    {
        this.notifyCount++;
        var menuObj = this.parseMenu( data, domainModelObject.menuName, domainModelObject.menuType );
        domainModelObject.page.putMenu( menuObj );
        if ( ! this.parentNotified && this.parentNotifyFinishedListener != null && this.notifyCount >= this.parentNotifyFinishedAfterIndex && dojo.lang.isFunction( this.parentNotifyFinishedListener.notifyFinished ) )
        {
            this.parentNotified = true;
            this.parentNotifyFinishedListener.notifyFinished( domainModelObject );
        }
        if ( dojo.lang.isFunction( this.notifyFinished ) )
        {
            this.notifyFinished( domainModelObject, menuObj );
        }
    },
    notifyFailure: function( /* String */ type, /* Object */ error, /* String */ requestUrl, domainModelObject )
    {
        this.notifyCount++;
        dojo.raise( "MenuAjaxApiContentListener error [" + domainModelObject.toString() + "] url: " + requestUrl + " type: " + type + jetspeed.url.formatBindError( error ) );
    },

    parseMenu: function( /* XMLNode */ node, /* String */ menuName, /* String */ menuType )
    {
        var menu = null;
        var jsElements = node.getElementsByTagName( "js" );
        if ( ! jsElements || jsElements.length > 1 )
            dojo.raise( "unexpected zero or multiple <js> elements in menu xml" );
        var children = jsElements[0].childNodes;
        
        for ( var i = 0 ; i < children.length ; i++ )
        {
            var child = children[i];
            if ( child.nodeType != dojo.dom.ELEMENT_NODE )
                continue;
            var childLName = child.nodeName;
            if ( childLName == "menu" )
            {
                if ( menu != null )
                    dojo.raise( "unexpected multiple top level <menu> elements in menu xml" );
                menu = this.parseMenuObject( child, new jetspeed.om.Menu() );
            }
        }
        if ( menu != null )
        {
            if ( menu.name == null )
                menu.name == menuName;
            if ( menu.type == null )
                menu.type = menuType;
        }
        return menu;
    },
    parseMenuObject: function( /* XMLNode */ node, /* MenuOption */ mObj )
    {
        var constructObj = null;
        var children = node.childNodes;
        for ( var i = 0 ; i < children.length ; i++ )
        {
            var child = children[i];
            if ( child.nodeType != dojo.dom.ELEMENT_NODE )
                continue;
            var childLName = child.nodeName;
            if ( childLName == "menu" )
            {
                if ( mObj.isLeaf() )
                    dojo.raise( "unexpected nested <menu> in <option> or <separator>" );
                else
                    mObj.addOption( this.parseMenuObject( child, new jetspeed.om.Menu() ) );
            }
            else if ( childLName == "option" )
            {
                if ( mObj.isLeaf() )
                    dojo.raise( "unexpected nested <option> in <option> or <separator>" );
                else
                    mObj.addOption( this.parseMenuObject( child, new jetspeed.om.MenuOption() ) );
            }
            else if ( childLName == "separator" )
            {
                if ( mObj.isLeaf() )
                    dojo.raise( "unexpected nested <separator> in <option> or <separator>" );
                else
                    mObj.addOption( this.parseMenuObject( child, new jetspeed.om.MenuOptionSeparator() ) );
            }
            else if ( childLName )
                mObj[ childLName ] = ( ( child && child.firstChild ) ? child.firstChild.nodeValue : null );
        }
        if ( mObj.setParsed )
            mObj.setParsed();
        return mObj;
    }
});

// ... jetspeed.om.MenusAjaxApiCallbackContentListener
jetspeed.om.MenuAjaxApiCallbackContentListener = function( /* boolean */ retrieveEachMenu )
{
    jetspeed.om.MenusAjaxApiContentListener.call( this, retrieveEachMenu );
};
dojo.inherits( jetspeed.om.MenuAjaxApiCallbackContentListener, jetspeed.om.MenuAjaxApiContentListener );
dojo.lang.extend( jetspeed.om.MenuAjaxApiCallbackContentListener,
{
    notifyFinished: function( domainModelObject, menuObj )
    {
        jetspeed.notifyRetrieveMenuFinished( menuObj );
    }
});

// ... jetspeed.om.PortletChangeActionContentListener
jetspeed.om.PortletChangeActionContentListener = function( /* String */ portletEntityId )
{
    this.portletEntityId = portletEntityId;
};
dojo.lang.extend( jetspeed.om.PortletChangeActionContentListener,
{
    notifySuccess: function( /* XMLDocument */ data, /* String */ requestUrl, domainModelObject )
    {
        if ( jetspeed.url.checkAjaxApiResponse( requestUrl, data, true, "portlet-change-action" ) )
        {
            jetspeed.getActionsForPortlet( this.portletEntityId );
        }
    },
    notifyFailure: function( /* String */ type, /* Object */ error, /* String */ requestUrl, domainModelObject )
    {
        dojo.raise( "PortletChangeActionContentListener error [" + domainModelObject.toString() + "] url: " + requestUrl + " type: " + type + jetspeed.url.formatBindError( error ) );
    }
});

// ... jetspeed.om.PortletActionsContentListener
jetspeed.om.PortletActionsContentListener = function()
{
};
dojo.lang.extend( jetspeed.om.PortletActionsContentListener,
{
    notifySuccess: function( /* XMLDocument */ data, /* String */ requestUrl, domainModelObject )
    {
        if ( jetspeed.url.checkAjaxApiResponse( requestUrl, data, true, "portlet-actions" ) )
        {
            this.processPortletActionsResponse( data );
        }
    },
    processPortletActionsResponse: function( /* XMLNode */ node )
    {   // derived class should override this method
        var results = this.parsePortletActionsResponse( node );
        for ( var i = 0 ; i < results.length ; i++ )
        {
            var resultsObj = results[i];
            var entityId = resultsObj.id;
            var portlet = jetspeed.page.getPortlet( entityId );
            if ( portlet != null )
                portlet.updateActions( resultsObj.actions, resultsObj.currentActionState, resultsObj.currentActionMode );
        }
    },

    parsePortletActionsResponse: function( /* XMLNode */ node )
    {
        var results = new Array();
        var jsElements = node.getElementsByTagName( "js" );
        if ( ! jsElements || jsElements.length > 1 )
        {
            dojo.raise( "unexpected zero or multiple <js> elements in portlet selector xml" );
            return results;
        }
        var children = jsElements[0].childNodes;
        for ( var i = 0 ; i < children.length ; i++ )
        {
            var child = children[i];
            if ( child.nodeType != dojo.dom.ELEMENT_NODE )
                continue;
            var childLName = child.nodeName;
            if ( childLName == "portlets" )
            {
                var portletsNode = child ;
                var portletChildren = portletsNode.childNodes ;
                for ( var pI = 0 ; pI < portletChildren.length ; pI++ )
                {
                    var pChild = portletChildren[pI];
                    if ( pChild.nodeType != dojo.dom.ELEMENT_NODE )
                        continue;
                    var pChildLName = pChild.nodeName;
                    if ( pChildLName == "portlet" )
                    {
                        var portletResult = this.parsePortletElement( pChild );
                        if ( portletResult != null )
                            results.push( portletResult );
                    }
                }
            }
        }
        return results;
    },
    parsePortletElement: function( /* XMLNode */ node )
    {
        var portletId = node.getAttribute( "id" );
        if ( portletId != null )
        {
            var actions = jetspeed.page._parsePSMLActions( node, null );
            var currentActionState = jetspeed.page._parsePSMLCurrentActionState( node );
            var currentActionMode = jetspeed.page._parsePSMLCurrentActionMode( node );
            return { id: portletId, actions: actions, currentActionState: currentActionState, currentActionMode: currentActionMode };
        }
        return null;
    },
    notifyFailure: function( /* String */ type, /* Object */ error, /* String */ requestUrl, domainModelObject )
    {
        dojo.raise( "PortletActionsContentListener error [" + domainModelObject.toString() + "] url: " + requestUrl + " type: " + type + jetspeed.url.formatBindError( error ) );
    }
});


// ... jetspeed.om.PortletAddAjaxApiCallbackContentListener
jetspeed.om.PortletAddAjaxApiCallbackContentListener = function(  /* jetspeed.om.PortletDef */ portletDef, windowWidgetId, addToCurrentPage )
{
    this.portletDef = portletDef;
    this.windowWidgetId = windowWidgetId;
    this.addToCurrentPage = addToCurrentPage;
};
dojo.lang.extend( jetspeed.om.PortletAddAjaxApiCallbackContentListener,
{
    notifySuccess: function( /* XMLDocument */ data, /* String */ requestUrl, domainModelObject )
    {
        if ( jetspeed.url.checkAjaxApiResponse( requestUrl, data, true, "add-portlet" ) )
        {
            var entityId = this.parseAddPortletResponse( data );
            if ( entityId && this.addToCurrentPage )
            {
                jetspeed.page.addNewPortlet( this.portletDef.getPortletName(), entityId, this.windowWidgetId );
            }
        }
    },
    parseAddPortletResponse: function( /* XMLNode */ node )
    {
        var entityId = null;
        var jsElements = node.getElementsByTagName( "js" );
        if ( ! jsElements || jsElements.length > 1 )
            dojo.raise( "unexpected zero or multiple <js> elements in portlet selector xml" );
        var children = jsElements[0].childNodes;
        
        for ( var i = 0 ; i < children.length ; i++ )
        {
            var child = children[i];
            if ( child.nodeType != dojo.dom.ELEMENT_NODE )
                continue;
            var childLName = child.nodeName;
            if ( childLName == "entity" )
            {
                entityId = ( ( child && child.firstChild ) ? child.firstChild.nodeValue : null );
                break;
            }
        }
        return entityId;
    },
    notifyFailure: function( /* String */ type, /* Object */ error, /* String */ requestUrl, domainModelObject )
    {
        dojo.raise( "PortletAddAjaxApiCallbackContentListener error [" + domainModelObject.toString() + "] url: " + requestUrl + " type: " + type + jetspeed.url.formatBindError( error ) );
    }
});

// ... jetspeed.om.PortletSelectorAjaxApiContentListener
jetspeed.om.PortletSelectorAjaxApiContentListener = function()
{
};
dojo.lang.extend( jetspeed.om.PortletSelectorAjaxApiContentListener,
{
    notifySuccess: function( /* XMLDocument */ data, /* String */ requestUrl, domainModelObject )
    {
        var portletList = this.parsePortlets( data );

        var portletSelector = dojo.widget.byId( jetspeed.id.SELECTOR ) ;
        if ( portletSelector != null )
        {
            for ( var i = 0 ; i < portletList.length ; i++ )
            {
                portletSelector.addChild( portletList[i] );
            }
        }

        if ( dojo.lang.isFunction( this.notifyFinished ) )
        {
            this.notifyFinished( domainModelObject, portletList );
        }
    },
    notifyFailure: function( /* String */ type, /* Object */ error, /* String */ requestUrl, domainModelObject )
    {
        dojo.raise( "PortletSelectorAjaxApiContentListener error [" + domainModelObject.toString() + "] url: " + requestUrl + " type: " + type + jetspeed.url.formatBindError( error ) );
    },

    parsePortlets: function( /* XMLNode */ node )
    {
        var portletList = [];
        var jsElements = node.getElementsByTagName( "js" );
        if ( ! jsElements || jsElements.length > 1 )
            dojo.raise( "unexpected zero or multiple <js> elements in portlet selector xml" );
        var children = jsElements[0].childNodes;
        
        for ( var i = 0 ; i < children.length ; i++ )
        {
            var child = children[i];
            if ( child.nodeType != dojo.dom.ELEMENT_NODE )
                continue;
            var childLName = child.nodeName;
            if ( childLName == "portlets" )
            {
                var portletsNode = child ;
                var portletChildren = portletsNode.childNodes ;
                for ( var pI = 0 ; pI < portletChildren.length ; pI++ )
                {
                    var pChild = portletChildren[pI];
                    if ( pChild.nodeType != dojo.dom.ELEMENT_NODE )
                        continue;
                    var pChildLName = pChild.nodeName;
                    if ( pChildLName == "portlet" )
                    {
                        var portletDef = this.parsePortletElement( pChild );
                        //dojo.debug( "parsePortlets  portletDef  name=" + portletDef.getPortletName() + "  displayName=" + portletDef.getPortletDisplayName() + "  description=" + portletDef.getPortletDescription() ) ;
                        portletList.push( portletDef ) ;
                    }
                }
            }
        }
        return portletList ;
    },
    parsePortletElement: function( /* XMLNode */ node )
    {
        var portletName = node.getAttribute( "name" );
        var portletDisplayName = node.getAttribute( "displayName" );
        var portletDescription = node.getAttribute( "description" );
        var portletImage = node.getAttribute( "image" );
        return new jetspeed.om.PortletDef( portletName, portletDisplayName, portletDescription, portletImage ) ;
    }
});

// ... jetspeed.om.PortletSelectorSearchContentListener
jetspeed.om.PortletSelectorSearchContentListener = function(finishedFunction)
{
    this.notifyFinished = finishedFunction;
};
dojo.lang.extend( jetspeed.om.PortletSelectorSearchContentListener,
{
    notifySuccess: function( /* XMLDocument */ data, /* String */ requestUrl, domainModelObject )
    {
        var portletList = this.parsePortlets( data );
        if ( dojo.lang.isFunction( this.notifyFinished ) )
        {
            this.notifyFinished( domainModelObject, portletList );
        }
    },
    notifyFailure: function( /* String */ type, /* String */ error, /* String */ requestUrl, domainModelObject )
    {
        dojo.raise( "PortletSelectorAjaxApiContentListener error [" + domainModelObject.toString() + "] url: " + requestUrl + " type: " + type + " error: " + error );
    },

    parsePortlets: function( /* XMLNode */ node )
    {
        var portletList = [];
        var jsElements = node.getElementsByTagName( "js" );
        if ( ! jsElements || jsElements.length > 1 )
            dojo.raise( "unexpected zero or multiple <js> elements in portlet selector xml" );
        var children = jsElements[0].childNodes;
        
        for ( var i = 0 ; i < children.length ; i++ )
        {
            var child = children[i];
            if ( child.nodeType != dojo.dom.ELEMENT_NODE )
                continue;
            var childLName = child.nodeName;
            if ( childLName == "portlets" )
            {
                var portletsNode = child ;
                var portletChildren = portletsNode.childNodes ;
                for ( var pI = 0 ; pI < portletChildren.length ; pI++ )
                {
                    var pChild = portletChildren[pI];
                    if ( pChild.nodeType != dojo.dom.ELEMENT_NODE )
                        continue;
                    var pChildLName = pChild.nodeName;
                    if ( pChildLName == "portlet" )
                    {
                        var portletDef = this.parsePortletElement( pChild );
                        //dojo.debug( "parsePortlets  portletDef  name=" + portletDef.getPortletName() + "  displayName=" + portletDef.getPortletDisplayName() + "  description=" + portletDef.getPortletDescription() ) ;
                        portletList.push( portletDef ) ;
                    }
                }
            }
        }
        return portletList ;
    },
    parsePortletElement: function( /* XMLNode */ node )
    {
        var portletName = node.getAttribute( "name" );
        var portletDisplayName = node.getAttribute( "displayName" );
        var portletDescription = node.getAttribute( "description" );
        var portletImage = node.getAttribute( "image" );
        return new jetspeed.om.PortletDef( portletName, portletDisplayName, portletDescription, portletImage ) ;
    }
});


// ... jetspeed.om.MoveAjaxApiContentListener
jetspeed.om.MoveAjaxApiContentListener = function( changedState )
{
    this.changedState = changedState;
};
jetspeed.om.MoveAjaxApiContentListener.prototype =
{
    notifySuccess: function( /* String */ data, /* String */ requestUrl, domainModelObject )
    {
        dojo.lang.mixin( domainModelObject.portlet.lastSavedWindowState, this.changedState );
        jetspeed.url.checkAjaxApiResponse( requestUrl, data, true, ("move-portlet [" + domainModelObject.portlet.entityId + "]"), jetspeed.debug.submitChangedWindowState );
    },
    notifyFailure: function( /* String */ type, /* Object */ error, /* String */ requestUrl, domainModelObject )
    {
        dojo.raise( "submitChangedWindowState error [" + domainModelObject.entityId + "] url: " + requestUrl + " type: " + type + jetspeed.url.formatBindError( error ) );
    }
};

// ... jetspeed.ui methods

jetspeed.ui.getPortletWindowChildren = function( /* DOM node */ parentNode, /* DOM node */ matchNodeIfFound, /* boolean */ includeGhosts, /* boolean */ includeGhostsOnly )
{
    if ( includeGhosts || includeGhostsOnly )
        includeGhosts = true;

    var nodesPW = null;
    var nodeMatchIndex = -1;
    if ( parentNode )
    {
        nodesPW = [];
        var children = parentNode.childNodes;
        if ( children != null && children.length > 0 )
        {
            for ( var i = 0 ; i < children.length ; i++ )
            {
                var child = children[i];
                if ( ( ! includeGhostsOnly && dojo.html.hasClass( child, jetspeed.id.PORTLET_WINDOW_STYLE_CLASS ) ) || ( includeGhosts && dojo.html.hasClass( child, jetspeed.id.PORTLET_WINDOW_GHOST_STYLE_CLASS ) ) )
                {
                    nodesPW.push( child );
                    if ( matchNodeIfFound && child == matchNodeIfFound )
                        nodeMatchIndex = nodesPW.length -1;
                }
                else if ( matchNodeIfFound && child == matchNodeIfFound )
                {
                    nodesPW.push( child );
                    nodeMatchIndex = nodesPW.length -1;
                }
            }
        }
    }
    return { portletWindowNodes: nodesPW, matchIndex: nodeMatchIndex };
};
jetspeed.ui.getPortletWindowsFromNodes = function( /* DOM node [] */ portletWindowNodes )
{
    var portletWindows = null;
    if ( portletWindowNodes )
    {
        portletWindows = new Array();
        for ( var i = 0 ; i < portletWindowNodes.length ; i++ )
        {
            var widget = dojo.widget.byNode( portletWindowNodes[ i ] );
            if ( widget )
                portletWindows.push( widget ) ;
        }
    }
    return portletWindows;
};
jetspeed.ui.dumpColumnWidths = function()
{
    for ( var i = 0 ; i < jetspeed.page.columns.length ; i++ )
    {
        var columnElmt = jetspeed.page.columns[i];
        dojo.debug( "jetspeed.page.columns[" + i + "] outer-width: " + dojo.html.getMarginBox( columnElmt.domNode ).width );
    }
};
jetspeed.ui.dumpPortletWindowsPerColumn = function()
{
    for ( var i = 0 ; i < jetspeed.page.columns.length ; i++ )
    {
        var columnElmt = jetspeed.page.columns[i];
        var windowNodesInColumn = jetspeed.ui.getPortletWindowChildren( columnElmt.domNode, null );
        var portletWindowsInColumn = jetspeed.ui.getPortletWindowsFromNodes( windowNodesInColumn.portletWindowNodes );
        var dumpClosure = { dumpMsg: "" };
        if ( portletWindowsInColumn != null )
        {
            dojo.lang.forEach( portletWindowsInColumn,
                                    function(portletWindow) { dumpClosure.dumpMsg = dumpClosure.dumpMsg + ( dumpClosure.dumpMsg.length > 0 ? ", " : "" ) + portletWindow.portlet.entityId; } );
        }
        dumpClosure.dumpMsg = "column " + i + ": " + dumpClosure.dumpMsg;
        dojo.debug( dumpClosure.dumpMsg );
    }
};

jetspeed.ui.getAllPortletWindowWidgets = function()
{
    var windowNodesUntiled = jetspeed.ui.getPortletWindowChildren( dojo.byId( jetspeed.id.DESKTOP ), null );
    var portletWindows = jetspeed.ui.getPortletWindowsFromNodes( windowNodesUntiled.portletWindowNodes );
    if ( portletWindows == null )
        portletWindows = new Array();
    for ( var i = 0 ; i < jetspeed.page.columns.length ; i++ )
    {
        var columnElmt = jetspeed.page.columns[i];
        var windowNodesInColumn = jetspeed.ui.getPortletWindowChildren( columnElmt.domNode, null );
        var portletWindowsInColumn = jetspeed.ui.getPortletWindowsFromNodes( windowNodesInColumn.portletWindowNodes );
        if ( portletWindowsInColumn != null )
        {
            portletWindows = portletWindows.concat( portletWindowsInColumn );
        }
    }
    return portletWindows;
};
jetspeed.ui.getDefaultFloatingPaneTemplate = function()
{
    return new dojo.uri.Uri(jetspeed.url.basePortalDesktopUrl() + "/javascript/jetspeed/widget/HtmlFloatingPane.html");   // BOZO: improve this junk
};
jetspeed.ui.getDefaultFloatingPaneTemplateCss = function()
{
    return new dojo.uri.Uri(jetspeed.url.basePortalDesktopUrl() + "/javascript/jetspeed/widget/HtmlFloatingPane.css");   // BOZO: improve this junk
};

jetspeed.ui.createPortletWindow = function( windowConfigObject, columnIndex )
{
    var winPositionStatic = windowConfigObject.getProperty( jetspeed.id.PORTLET_PROP_WINDOW_POSITION_STATIC );
    if ( winPositionStatic == null )
        winPositionStatic = ( jetspeed.prefs.windowTiling ? true : false );    // BOZO: what to do about setting the value here ( putProperty )
    else if ( ! jetspeed.prefs.windowTiling )
        winPositionStatic = false;
    
    var windowWidget = dojo.widget.byId( windowConfigObject.getProperty( jetspeed.id.PORTLET_PROP_WIDGET_ID ) );   // get existing window widget

    if ( windowWidget )
    {
        windowWidget.resetWindow( windowConfigObject );
    }   
    else
    {
        windowWidget = jetspeed.ui.createPortletWindowWidget( windowConfigObject );
    }

    if ( windowWidget )
    {
        if ( ! winPositionStatic || columnIndex >= jetspeed.page.columns.length )
        {
            windowWidget.domNode.style.position = "absolute";
            var addToElmt = document.getElementById( jetspeed.id.DESKTOP );
            addToElmt.appendChild( windowWidget.domNode );
        }
        else
        {
            var useColumnObj = null;
            var useColumnIndex = -1;

            var preferredColumn = columnIndex;
            if ( preferredColumn != null && preferredColumn >= 0 && preferredColumn < jetspeed.page.columns.length )
            {
                useColumnIndex = preferredColumn;
                useColumnObj = jetspeed.page.columns[ useColumnIndex ];
            }
            if ( useColumnIndex == -1 )
            {   // select a column based on least populated (least number of child nodes)
                for ( var i = 0 ; i < jetspeed.page.columns.length ; i++ )
                {
                    var columnElmt = jetspeed.page.columns[i];
                    if ( ! columnElmt.domNode.hasChildNodes() )
                    {
                        useColumnObj = columnElmt;
                        useColumnIndex = i;
                        break;
                    }
                    if ( useColumnObj == null || useColumnObj.domNode.childNodes.length > columnElmt.domNode.childNodes.length )
                    {
                        useColumnObj = columnElmt;
                        useColumnIndex = i;
                    }
                }
            }
            if ( useColumnObj )
            {
                useColumnObj.domNode.appendChild( windowWidget.domNode );
            }
        }
    }
};
jetspeed.ui.createPortletWindowWidget = function( windowConfigObject, createWidgetParams )
{
    if ( ! createWidgetParams )
        createWidgetParams = {};
    if ( windowConfigObject instanceof jetspeed.om.Portlet )
    {
        createWidgetParams.portlet = windowConfigObject;
    }
    else
    {
        jetspeed.widget.PortletWindow.prototype.staticDefineAsAltInitParameters( createWidgetParams, windowConfigObject );
    }

    // NOTE: other parameters, such as widgetId could be set here (to override what PortletWindow does)
    var nWidget = dojo.widget.createWidget( "jetspeed:PortletWindow", createWidgetParams );
    
    return nWidget;
};

jetspeed.ui.preParseAnnotateHtml = function( /* String */ initialHtmlStr, /* String */ url )
{   // deal with embedded script tags -  /=/=/=/=/=  taken from dojo ContentPane.js  splitAndFixPaths()  =/=/=/=/=/
    var scripts = [];
    var remoteScripts = [];
    // cut out all script tags, stuff them into scripts array
    var match = [];
    while ( match )
    {
        match = initialHtmlStr.match(/<script([^>]*)>([\s\S]*?)<\/script>/i);
        if(!match){ break; }
        if(match[1]){
            attr = match[1].match(/src=(['"]?)([^"']*)\1/i);
            if ( attr )
            {
                // remove a dojo.js or dojo.js.uncompressed.js from remoteScripts
                if ( (attr[2].search(/\/?\bdojo.js(?:\.uncompressed.js)?/i) != -1) && (dojo.hostenv.getBaseScriptUri() == attr[2].match(/[.\/]*/)[0]) )
                {	
                    dojo.debug("Security note! inhibit:"+attr[2]+" from  beeing loaded again.");
                }
                else
                {
                    remoteScripts.push( attr[2] );
                }
            }
        }
        if ( match[2] )
        {
            // get rid of html comment blanket
            var scriptText = match[2].replace(/^\s*<!--/, "");
            scriptText = scriptText.replace(/-->\s*$/, "");

            scriptText = scriptText.replace(/function\s+([a-zA-Z_][a-zA-Z0-9_]*)\s*\(/g, "window.$1 = function(" );

            // strip out all djConfig variables from script tags nodeValue
            // this is ABSOLUTLY needed as reinitialize djConfig after dojo is initialised
            // makes a dissaster greater than Titanic                
            scripts.push(scriptText.replace(/(?:var )?\bdjConfig\b(?:[\s]*=[\s]*\{[^}]+\}|\.[\w]*[\s]*=[\s]*[^;\n]*)?;?|dojo.hostenv.writeIncludes\(\s*\);?/g, ""));
        }
        initialHtmlStr = initialHtmlStr.replace(/<script[^>]*>[\s\S]*?<\/script>/i, "");
    }
    //dojo.debug( "= = = = = =  annotated content for: " + ( url ? url : "unknown url" ) );
    //dojo.debug( initialHtmlStr );
    //if ( scripts.length > 0 )
    //{
    //    dojo.debug( "      = = =  script content for: " + ( url ? url : "unknown url" ) );
    //    for ( var i = 0 ; i < scripts.length; i++ )
    //        dojo.debug( "      =[" + (i+1) + "]:" + scripts[i] );
    //}
    //     /=/=/=/=/=  end of taken from dojo ContentPane.js  splitAndFixPaths()  =/=/=/=/=/
    //dojo.debug( "preParse  scripts: " + ( scripts ? scripts.length : "0" ) + " remoteScripts: " + ( remoteScripts ? remoteScripts.length : "0" ) );
    return { preParsedContent: initialHtmlStr, preParsedScripts: scripts, preParsedRemoteScripts: remoteScripts };
};

// ... fade-in convenience methods (work with set of nodes)
jetspeed.ui.fadeIn = function(nodes, duration, visibilityStyleValue)
{
    jetspeed.ui.fade(nodes, duration, visibilityStyleValue, 0, 1);
};
jetspeed.ui.fadeOut = function(nodes, duration, nodesToChgDisplayNone)
{
    jetspeed.ui.fade(nodes, duration, "hidden", 1, 0, nodesToChgDisplayNone);
};
jetspeed.ui.fade = function(nodes, duration, visibilityStyleValue, startOpac, endOpac, nodesToChgDisplayNone)
{
    if ( nodes.length > 0 )
    {   // mimick dojo.lfx.html.fade, but for all objects together
        for ( var i = 0 ; i < nodes.length ; i++ )
        {
            dojo.lfx.html._makeFadeable(nodes[i]);
            if (visibilityStyleValue != "none")
                nodes[i].style.visibility = visibilityStyleValue ;
        }
        var anim = new dojo.animation.Animation(
		                new dojo.math.curves.Line([startOpac],[endOpac]),
		                duration, 0);
	    dojo.event.connect(anim, "onAnimate", function(e) {
            for ( var mi = 0 ; mi < nodes.length ; mi++ )
            {
                dojo.html.setOpacity(nodes[mi], e.x);
	        }});
        
        if (visibilityStyleValue == "hidden")
        {
            dojo.event.connect(anim, "onEnd", function(e) {
			    for ( var mi = 0 ; mi < nodes.length ; mi++ )
                    nodes[mi].style.visibility = visibilityStyleValue ;
                if ( nodesToChgDisplayNone )
                {
                    for ( var mi = 0; mi < nodesToChgDisplayNone.length ; mi++ )
                        nodesToChgDisplayNone[mi].style.display = "none";
                }
		    });
        }
        anim.play(true);
    }
};
