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
    PORTLET_PROP_WINDOW_POSITION_STATIC: "windowPositionStatic",
    PORTLET_PROP_COLUMN_SPAN: "windowColumnSpan",
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

    MENU_WIDGET_ID_PREFIX: "jetspeed-menu-",

    WINDOW_THEMES: [ "tigris", "blueocean" ]     // temporary validation to avoid trying to use an undefined window theme
};

// ... jetspeed desktop preferences
jetspeed.prefs = 
{
    windowTiling: 2,     // number > 0 is interpreted as number of columns; 0 or false indicates no-columns, free-floating windows
    windowTilingVariableWidth: false,   // only meaningful when windowTiling > 0
    windowTilingVariableHeight: true,   // only meaningful when windowTiling > 0
    //portalTaskBarType: "blee"  // BOZO: need pref/s to handle this ( instead of html elements in the content )
    
    defaultPortletWidth: "280",
    defaultPortletHeight: "200",

    desktopTheme: null,
    desktopThemeRootUrl: null,
    getDesktopTheme: function()
    {
        if ( jetspeed.prefs.desktopTheme == null )
            return djConfig.desktopTheme;
        return jetspeed.prefs.desktopTheme;
    },
    getDesktopThemeRootUrl: function()
    {
        if ( jetspeed.prefs.desktopThemeRootUrl == null )
            return djConfig.desktopThemeRootUrl;
        return jetspeed.prefs.desktopThemeRootUrl;
    },

    portletSelectorWindowTitle: "Portlet Selector",
    portletSelectorWindowIcon: "text-x-script.png",
    portletSelectorBounds: { x: 20, y: 20, width: 400, height: 600 }
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
    submitChangedWindowState: 0,

    debugContainerId: ( djConfig.debugContainerId ? djConfig.debugContainerId : dojo.hostenv.defaultDebugContainerId )
};
jetspeed.debugInPortletWindow = true;
//jetspeed.debugPortletEntityIdFilter = [ "dp-3", "dp-16", "dp-17", "dp-22", "dp-18", "dp-23", "dp-7", "dp-12" ];   // all default-page except IFrame
//jetspeed.debugPortletEntityIdFilter = [ "dp-7", "dp-3", "dp-12", "dp-18" ]; // NOTE: uncomment causes only the listed portlets to be loaded; all others are ignored; for testing
//portlets: [dp-3 LocaleSelector, dp-16 RoleSecurityTest, dp-17 UserInfoTest, dp-22 ForgottenPasswordPortlet, dp-18 BookmarkPortlet, dp-23 UserRegistrationPortlet, dp-7 PickANumberPortlet, dp-9 IFramePortlet, dp-12 LoginPortlet]
//jetspeed.debugPortletEntityIdFilter = [ "dp-18" ];
jetspeed.debugPortletWindowIcons = [ "text-x-generic.png", "text-html.png", "application-x-executable.png" ];
jetspeed.debugPortletWindowThemes = [ "blueocean", "tigris" ];  /* , "tigris", "blueocean" ]; */
//jetspeed.debugContentDumpIds = [ ".*" ];                        // dump all responses
//jetspeed.debugContentDumpIds = [ "getmenus", "getmenu-.*" ];    // dump getmenus response and all getmenu responses
//jetspeed.debugContentDumpIds = [ "page-.*" ];                   // dump page psml response
//jetspeed.debugContentDumpIds = [ "P-10acd169a40-10001", "P-10acd169a40-10000" ];
jetspeed.debugContentDumpIds = [ "notifyGridSelect", "P-10acd169a40-10001", "reports-select", "addportlet" ]; // , "getportlets", "dp-7", "jsfGuessNumber1", "jsfCalendar" ];    // "um-4", "dp-7", "jsfGuessNumber1", "jsfCalendar"
//jetspeed.debugContentDumpIds = [ "P-10aba.*" ];

// ... load page /portlets
jetspeed.page = null ;   // BOZO: is this it? one page at a time?
jetspeed.columns = [];
jetspeed.initializeDesktop = function( desktopThemeName, desktopThemeRootUrl )
{
    jetspeed.url.pathInitialize();
    jetspeed.prefs.desktopTheme = desktopThemeName;
    jetspeed.prefs.desktopThemeRootUrl = desktopThemeRootUrl;
    jetspeed.loadPage();
    //jetspeed.currentTaskbar = new jetspeed.ui.PortalTaskBar() ;
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
        windowParams[ jetspeed.id.PORTLET_PROP_WINDOW_POSITION_STATIC ] = false;
        windowParams[ jetspeed.id.PORTLET_PROP_WINDOW_THEME ] = "tigris";
        windowParams[ jetspeed.id.PORTLET_PROP_WINDOW_TITLE ] = "Dojo Debug";
        windowParams[ jetspeed.id.PORTLET_PROP_WINDOW_ICON ] = "text-x-script.png";
        windowParams[ jetspeed.id.PORTLET_PROP_WIDGET_ID ] = jetspeed.id.PORTLET_WINDOW_ID_PREFIX + "dojo-debug";
        windowParams[ jetspeed.id.PORTLET_PROP_WIDTH ] = "400";
        windowParams[ jetspeed.id.PORTLET_PROP_HEIGHT ] = "400";
        windowParams[ jetspeed.id.PORTLET_PROP_LEFT ] = "320";
        windowParams[ jetspeed.id.PORTLET_PROP_TOP ] = "0";
        windowParams[ jetspeed.id.PORTLET_PROP_EXCLUDE_PCONTENT ] = false;
        windowParams[ jetspeed.id.PORTLET_PROP_CONTENT_RETRIEVER ] = new jetspeed.om.DojoDebugContentRetriever();
        windowParams[ jetspeed.id.PORTLET_PROP_WINDOW_STATE ] = "minimized" ;
        var pwWidgetParams = jetspeed.ui.widget.PortletWindow.prototype.staticDefineAsAltInitParameters( null, windowParams );
        jetspeed.ui.createPortletWindow( pwWidgetParams, null, null );
        pwWidgetParams.retrieveContent( null, null );
    }
};

jetspeed.loadPortletWindows = function( portletWindowFactory )
{
    if ( jetspeed.prefs.windowTiling > 0 )
    {
        //var numberOfColumns = 3 ; // jetspeed.page.getNumberOfColumns();
        var numberOfColumns = jetspeed.page.getNumberOfColumns();
        jetspeed.ui.createColumns( document.getElementById( jetspeed.id.DESKTOP ), numberOfColumns );
    }

    var windowsToRender = [];

    var portletArray = jetspeed.page.getPortletArrayByColumnRow();

    jetspeed.ui._loadPortletWindows( portletArray, windowsToRender, portletWindowFactory );
    
    portletArray = jetspeed.page.getPortletArrayByZIndex();

    jetspeed.ui._loadPortletWindows( portletArray, windowsToRender, portletWindowFactory );

    if ( windowsToRender && windowsToRender.length > 0 )
    {
        jetspeed.doRenderAll( null, windowsToRender, true );
    }
    jetspeed.page.retrieveAllMenus();   // BOZO: should not be happening here!
};
jetspeed.ui._loadPortletWindows = function( /* Portlet[] */ portletArray, windowsToRender, portletWindowFactory )
{
    if ( portletArray )
    {
        for ( var i = 0; i < portletArray.length; i++ )
        {
            var portlet = portletArray[i];
            if ( jetspeed.debugPortletEntityIdFilter )
            {
                if ( ! dojo.lang.inArray( jetspeed.debugPortletEntityIdFilter, portlet.entityId ) )
                    portlet = null;
            }
            if ( portlet )
            {
                windowsToRender.push( portlet );
                portlet.createPortletWindow( portletWindowFactory, null );
            }
        }
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
        renderObj.retrieveContent( null, { url: url } );
    }
    if ( debugMsg )
        dojo.debug( "doRenderAll [" + renderMsg + "] url: " + url );
    else if ( debugPageLoad )   // this.getPsmlUrl() ;
        dojo.debug( "doRenderAll page-url: " + jetspeed.page.getPsmlUrl() + " portlets: [" + renderMsg + "]" + ( url ? ( " url: " + url ) : "" ) );
};

jetspeed.doNothingNav = function()
{   // replacing form actions with javascript: doNothingNav() is 
    // useful for preventing form submission in cases like: <a onclick="form.submit(); return false;" >
    // JSF h:commandLink uses the above anchor onclick practice
    false;
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
    var pwWidgetParams = jetspeed.ui.widget.PortletWindow.prototype.staticDefineAsAltInitParameters( null, windowParams );
    jetspeed.ui.createPortletWindow( pwWidgetParams, null, null );
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

jetspeed.addPortletDefinition = function( /* jetspeed.om.PortletDef */ portletDef, windowWidgetId )
{
    var contentListener = new jetspeed.om.PortletAddAjaxApiCallbackContentListener( portletDef, windowWidgetId );
    var queryString = "?action=add&id=" + escape( portletDef.getPortletName() );
    var addPortletUrl = jetspeed.page.getPsmlUrl() + queryString;
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
    return jetspeed.url.basePortalDesktopUrl() + "/javascript/desktop/windowthemes/" + windowtheme;
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

jetspeed.url.checkAjaxApiResponse = function( requestUrl, data, reportError, dumpOutput )
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
        if ( success )
            dojo.debug( "ajax-api output url=" + requestUrl + "  xml-content=" + textContent );
        else
            dojo.raise( "ajax-api failure url=" + requestUrl + "  xml-content=" + textContent );
    }
    return success;
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
    notifyFailure: function( /* String */ type, /* String */ error, /* String */ requestUrl, /* Portlet */ portlet )
    {
        dojo.debug( "PortletSelectorContentListener notifyFailure url=" + requestUrl + " type=" + type + " error=" + error ) ;
    }
};

// ... jetspeed.om.PageContentListenerCreateWidget
jetspeed.om.PageContentListenerUpdate = function( /* jetspeed.om.Page */ previousPage )
{
    this.previousPage = previousPage;
};
jetspeed.om.PageContentListenerUpdate.prototype =
{
    notifySuccess: function( /* XMLDocument */ data, /* String */ requestUrl, /* Page */ page )
    {
        page.getPortletsFromPSML( data );
        var updatedPortlets = page.getPortlets();
        for ( var i = 0 ; i < updatedPortlets.length ; i++ )
        {
            var prevPortlet = this.previousPage.getPortlet( updatedPortlets[i].entityId );
            if ( prevPortlet == null )
            {
                dojo.debug( "PageContentListenerUpdate  new portlet definition in page: " + updatedPortlets[i].toString() ) ;
            }
        }
    },
    notifyFailure: function( /* String */ type, /* String */ error, /* String */ requestUrl, /* Page */ page )
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
        page.getPortletsFromPSML( data );
        jetspeed.loadPortletWindows();
    },
    notifyFailure: function( /* String */ type, /* String */ error, /* String */ requestUrl, /* Page */ page )
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
    this.psmlPath = pagePsmlPath ;
    if ( this.psmlPath == null )
        this.setPsmlPathFromDocumentUrl() ;
    this.name = pageName ;
    this.title = pageTitle ;
    this.portlets = [] ;
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
    portlets: null,
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

    getPortletsFromPSML: function( psml )
    {
        var pageElements = psml.getElementsByTagName( "page" );
        if ( ! pageElements || pageElements.length > 1 )
            dojo.raise( "unexpected zero or multiple <page> elements in psml" );
        var pageElement = pageElements[0];
        var children = pageElement.childNodes;
        var simpleValueLNames = new RegExp( "(name|path|title|short-title)" );
        for ( var i = 0 ; i < children.length ; i++ )
        {
            var child = children[i];
            if ( child.nodeType != dojo.dom.ELEMENT_NODE )
                continue;
            var childLName = child.nodeName;
            if ( childLName == "defaults" )
            {
                this.layoutDecorator = child.getAttribute( "layout-decorator" );
                this.portletDecorator = child.getAttribute( "portlet-decorator" );
            }
            else if ( childLName && childLName.match( simpleValueLNames  ) )
            {
                this[ jetspeed.purifyIdentifier( childLName, "", "lo" ) ] = ( ( child && child.firstChild ) ? child.firstChild.nodeValue : null );
            }
        }
        
        var lis = pageElement.getElementsByTagName( "fragment" );
        for( var x=0; x < lis.length; x++ )
        {
            var fragType = lis[x].getAttribute( "type" );
            if ( fragType == "portlet" )
            {
                var portletName = lis[x].getAttribute( "name" );
                var portletEntityId = lis[x].getAttribute( "id" );
                var portlet = new jetspeed.om.Portlet( portletName, portletEntityId ) ;

                var props = lis[x].getElementsByTagName( "property" );
                for( var propsIdx=0; propsIdx < props.length; propsIdx++ )
                {
                    var propName = props[propsIdx].getAttribute( "name" ) ;
                    var propValue = props[propsIdx].getAttribute( "value" ) ;
                    portlet.putProperty( propName, propValue ) ;
                }

                portlet.initialize();

                this.putPortlet( portlet ) ;
            }
        }
    },

    addPortlet: function( portletName, portletEntityId, windowWidgetId )
    {
        var portlet = new jetspeed.om.Portlet( portletName, portletEntityId ) ;
        if ( windowWidgetId )
            portlet.putProperty( jetspeed.id.PORTLET_PROP_WIDGET_ID, windowWidgetId );
        portlet.initialize();
        this.putPortlet( portlet ) ;
        portlet.retrieveContent();
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

    _portletColumnRowCompare: function( portletA, portletB )
    {   // uses saved state only - does not check with window widget
        var windowState = portletA.getLastSavedWindowState();
        var col = ( windowState.column == null ? 50 : windowState.column );
        var row = ( windowState.row == null ? 0 : windowState.row );
        var aVal = ( col * 1000 ) + row;
        windowState = portletB.getLastSavedWindowState();
        col = ( windowState.column == null ? 50 : windowState.column );
        row = ( windowState.row == null ? 0 : windowState.row );
        var bVal = ( col * 1000 ) + row;
        return ( aVal - bVal );
    },

    _debugDumpPortletZIndexInfo: function()
    {
        var portletArray = this.getPortletArrayByZIndex();
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
    getNumberOfColumns: function()
    {
        var numberOfColumns = 1;
        if ( this.columns != null )
            return this.columns;
        var portletArray = this.getPortletArray();
        if ( ! portletArray ) return portletArray;
        var filteredPortletArray = [];
        for ( var i = 0 ; i < portletArray.length; i++ )
        {
            if ( portletArray[i].getProperty( jetspeed.id.PORTLET_PROP_WINDOW_POSITION_STATIC ) )
            {
                var windowState = portletArray[i].getLastSavedWindowState();
                if ( windowState && windowState.column != null && (windowState.column + 1) > numberOfColumns )
                    numberOfColumns = new Number( windowState.column ) + 1;
            }
        }
        return numberOfColumns;
    },
    getWindowThemeDefault: function()
    {
        var windowtheme = null;
        if ( dojo.lang.indexOf( jetspeed.id.WINDOW_THEMES, this.getPortletDecorator() ) != -1 )
            windowtheme = this.getPortletDecorator();
        else if ( djConfig.isDebug && jetspeed.debugPortletWindowThemes != null )
            windowtheme = jetspeed.debugPortletWindowThemes[ Math.floor( Math.random() * jetspeed.debugPortletWindowThemes.length ) ];
        else if ( jetspeed.id.WINDOW_THEMES )
            windowtheme = jetspeed.id.WINDOW_THEMES[0];
        return windowtheme;
    },
    getPortletArrayByZIndex: function()
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
    getPortletArrayByColumnRow: function()
    {
        var portletArray = this.getPortletArray();
        if ( ! portletArray ) return portletArray;
        var filteredPortletArray = [];
        for ( var i = 0 ; i < portletArray.length; i++ )
        {
            var posStatic = portletArray[i].getProperty( jetspeed.id.PORTLET_PROP_WINDOW_POSITION_STATIC );
            var colSpan = portletArray[i].getProperty( jetspeed.id.PORTLET_PROP_COLUMN_SPAN );
            if ( posStatic || colSpan != null )
            {
                filteredPortletArray.push( portletArray[i] );
            }
        }
        filteredPortletArray.sort( this._portletColumnRowCompare );

        return filteredPortletArray;
    },
    getPortletArrayList: function()
    {
        var portletArrayList = new dojo.collections.ArrayList();
        for ( var portletIndex in this.portlets )
        {
            var portlet = this.portlets[portletIndex];
            portletArrayList.add(portlet);
        }
        return portletArrayList;
    },
    getPortletArray: function()
    {
        if (! this.portlets) return null;
        var portletArray = [];
        for (var portletIndex in this.portlets)
        {
            var portlet = this.portlets[portletIndex];
            portletArray.push(portlet);
        }
        return portletArray;
    },
    getPortlets: function()
    {
        if ( this.portlets )
            return dojo.lang.shallowCopy(this.portlets) ;
        return null ;
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
    removePortletFromPage: function( /* Portlet */ portlet )
    {
        var contentListener = new jetspeed.om.PortletAddAjaxApiCallbackContentListener( portletDef, windowWidgetId );
        var queryString = "?action=remove&id=" + escape( portletDef.getPortletName() );
        var addPortletUrl = jetspeed.page.getPsmlUrl() + queryString;
        var mimetype = "text/xml";
        var ajaxApiContext = new jetspeed.om.Id( "removeportlet", { } );
        jetspeed.url.retrieveContent( { url: addPortletUrl, mimetype: mimetype }, contentListener, ajaxApiContext, jetspeed.debugContentDumpIds );
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
        jetspeed.ui.removeColumns( document.getElementById( jetspeed.id.DESKTOP ) );
        jetspeed.initializeDesktop();
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
    notifyFailure: function( /* String */ type, /* String */ error, /* String */ requestUrl, domainModelObject )
    {
        dojo.debug( "BasicContentListener notifyFailure url=" + requestUrl + " type=" + type + " error=" + error ) ;
    }
};

// ... jetspeed.om.PortletContentListener
jetspeed.om.PortletContentListener = function()
{
};
jetspeed.om.PortletContentListener.prototype =
{
    notifySuccess: function( /* String */ portletContent, /* String */ requestUrl, /* Portlet */ portlet )
    {
        portlet.setPortletContent( portletContent, requestUrl );
    },
    notifyFailure: function( /* String */ type, /* String */ error, /* String */ requestUrl, /* Portlet */ portlet )
    {
        dojo.debug( "PortletContentListener notifyFailure url=" + requestUrl + " type=" + type + " error=" + error ) ;
    }
};

// ... jetspeed.om.PortletContentListener
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
    notifyFailure: function( /* String */ type, /* String */ error, /* String */ requestUrl, /* Portlet */ portlet )
    {
        dojo.debug( "PortletActionContentListener notifyFailure type=" + type ) ;
        dojo.debugShallow( error );
    }
};


// ... jetspeed.om.PortletWindowFactory
jetspeed.om.PortletWindowFactory = function()
{
};
jetspeed.om.PortletWindowFactory.prototype =
{
    create: function( windowConfigObject )
    {
        return jetspeed.ui.createPortletWindowWidget( windowConfigObject );
    },
    reset: function( windowConfigObject, /* PortletWindow */ portletWindowWidget )
    {
        portletWindowWidget.resetWindow( windowConfigObject );
    },
    layout: function( windowConfigObject, /* PortletWindow */ portletWindowWidget )
    {
        portletWindowWidget.domNode.style.position = "absolute";
        var addToElmt = document.getElementById( jetspeed.id.DESKTOP );
        addToElmt.appendChild( portletWindowWidget.domNode );
    }
};

jetspeed.om.PortletTilingWindowFactory = function()
{
};
jetspeed.om.PortletTilingWindowFactory.prototype =
{
    create: function( windowConfigObject )
    {
        return jetspeed.ui.createPortletWindowWidget( windowConfigObject );
    },
    reset: function( windowConfigObject, /* PortletWindow */ portletWindowWidget )
    {
        portletWindowWidget.resetWindow( windowConfigObject );
    },
    layout: function( windowConfigObject, /* PortletWindow */ portletWindowWidget )
    {
        var windowPositionStatic = windowConfigObject.getProperty( jetspeed.id.PORTLET_PROP_WINDOW_POSITION_STATIC );
        if ( ! windowPositionStatic )
        {
            portletWindowWidget.domNode.style.position = "absolute";
            var addToElmt = document.getElementById( jetspeed.id.DESKTOP );
            addToElmt.appendChild( portletWindowWidget.domNode );
        }
        else
        {
            var useColumnElmt = null;
            var useColumnIndex = -1;

            var preferredColumn = windowConfigObject.getProperty( jetspeed.id.PORTLET_PROP_COLUMN );
            if ( preferredColumn >= 0 && preferredColumn < jetspeed.columns.length )
            {
                useColumnIndex = preferredColumn;
                useColumnElmt = jetspeed.columns[ useColumnIndex ];
            }
            if ( useColumnIndex == -1 )
            {   // select a column based on least populated (least number of child nodes)
                for ( var i = 0 ; i < jetspeed.columns.length ; i++ )
                {
                    var columnElmt = jetspeed.columns[i];
                    if ( ! columnElmt.hasChildNodes() )
                    {
                        useColumnElmt = columnElmt;
                        useColumnIndex = i;
                        break;
                    }
                    if ( useColumnElmt == null || useColumnElmt.childNodes.length > columnElmt.childNodes.length )
                    {
                        useColumnElmt = columnElmt;
                        useColumnIndex = i;
                    }
                }
            }
            if ( useColumnElmt )
            {
                useColumnElmt.appendChild( portletWindowWidget.domNode );
            }
        }
    }
};


// ... jetspeed.om.Portlet
jetspeed.om.Portlet = function( /* String */ portletName, /* String */ portletEntityId, /* special */ alternateContentRetriever )
{
    this.name = portletName;
    this.entityId = portletEntityId;
    this.properties = {};
    if ( alternateContentRetriever )
        this.contentRetriever = alternateContentRetriever;
};
dojo.inherits( jetspeed.om.Portlet, jetspeed.om.Id);
dojo.lang.extend( jetspeed.om.Portlet,
{
    name: null,
    entityId: null,
    
    contentRetriever: new jetspeed.om.PortletContentRetriever(),
    
    windowFactory: null,

    lastSavedWindowState: null,

    JAVASCRIPT_ACTION_PREFIX: "javascript:doAction(",
    JAVASCRIPT_RENDER_PREFIX: "javascript:doRender(",
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
        var colSpan = this.getProperty( jetspeed.id.PORTLET_PROP_COLUMN_SPAN );
        if ( colSpan != null )
        {
            if ( ! jetspeed.prefs.windowTiling || ! dojo.lang.isNumber( colSpan ) || colSpan <= 1 )
            {
                colSpan = null ;
                this.putProperty( jetspeed.id.PORTLET_PROP_COLUMN_SPAN, null );
            }
        }
        var posStatic = this.getProperty( jetspeed.id.PORTLET_PROP_WINDOW_POSITION_STATIC );
        if ( posStatic != null && posStatic && ( ! jetspeed.prefs.windowTiling || colSpan != null ) )
        {
            posStatic = false ;
            this.putProperty( jetspeed.id.PORTLET_PROP_WINDOW_POSITION_STATIC, false );
        }
        else if ( posStatic == null )
        {
            if ( jetspeed.prefs.windowTiling )
                this.putProperty( jetspeed.id.PORTLET_PROP_WINDOW_POSITION_STATIC, true );
            else
                this.putProperty( jetspeed.id.PORTLET_PROP_WINDOW_POSITION_STATIC, false );
        }

        

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

        var rawState = null;
        if ( volatileOnly )
            rawState = windowWidget.getCurrentVolatileWindowState();
        else
            rawState = windowWidget.getCurrentWindowState();

        var currentState = {};
        var pxre = (/px/i);
        for ( var stateKey in rawState )
        {
            currentState[stateKey] = new String( rawState[stateKey] ).replace( pxre, "" );
        }
        return currentState;
    },
    getLastSavedWindowState: function()
    {
        if ( ! this.lastSavedWindowState )
            this._initializeWindowState();
        return this.lastSavedWindowState;
    },
    _getChangedWindowState: function( /* boolean */ volatileOnly )
    {
        var lastSaved = this.getLastSavedWindowState();
        var hasChange = false;
        var windowPositionStatic = this.getProperty( jetspeed.id.PORTLET_PROP_WINDOW_POSITION_STATIC );
        var zIndexTrack = ! windowPositionStatic;
        var zIndexChange = false;

        if ( lastSaved && dojo.lang.isEmpty( lastSaved ) )
        {
            lastSaved = null;
            volatileOnly = false;  // so that current state we obtain is the full representation
        }
        
        var currentState = this.getCurrentWindowState( volatileOnly );
        if ( ! lastSaved )
        {
            var result = { state: currentState };
            if ( zIndexTrack )
                result.zIndexChanged = true;   // BOZO: this could lead to an early submission for each portlet (may not be too cool?)
            return result;
        }
        
        for (var stateKey in currentState)
        {
            //if ( stateKey == "zIndex" )
            //    dojo.debug( "portlet zIndex compare [" + this.entityId + "]  " + ( currentState[stateKey] ? currentState[stateKey] : "null" ) + " != " + ( lastSaved[stateKey] ? lastSaved[stateKey] : "null" ) );
            if ( currentState[stateKey] != lastSaved[stateKey] )
            {
                hasChange = true;

                //dojo.debug( "portlet [" + this.entityId + "] windowstate changed: " + stateKey + "  " + ( currentState[stateKey] ? currentState[stateKey] : "null" ) + " != " + ( lastSaved[stateKey] ? lastSaved[stateKey] : "null" ) ) ;

                if ( ! zIndexTrack )
                    break;
                else if ( stateKey == "zIndex" )
                    zIndexChange = true;
            }
        }
        if ( hasChange )
        {
            var result = { state: currentState };
            if ( zIndexTrack )
                result.zIndexChanged = zIndexChange;
            return result;
        }
        return null;
    },
    
    _initializeWindowState: function( /* boolean */ reset )
    {
        var initialWindowState = {};
        var windowPositionStatic = this.getProperty( jetspeed.id.PORTLET_PROP_WINDOW_POSITION_STATIC );
        
        var portletWidth = this.getProperty( "width" );
        if ( ! reset && portletWidth != null && portletWidth > 0 )
            initialWindowState.width = Math.floor( portletWidth );
        else if ( reset )
            initialWindowState.width = -1;
    
        var portletHeight = this.getProperty( "height" );
        if ( ! reset && portletHeight != null && portletHeight > 0  )
            initialWindowState.height = Math.floor( portletHeight );
        else if ( reset )
            initialWindowState.height = -1;
        
        if ( ! windowPositionStatic )
        {
            var portletLeft = this.getProperty( "x" );
            if ( ! reset && portletLeft != null && portletLeft >= 0 )
                initialWindowState.left = Math.floor( ( (portletLeft > 0) ? portletLeft : 0 ) );
            else if ( reset )
                initialWindowState.left = -1;

            var portletTop = this.getProperty( "y" );
            if ( ! reset && portletTop != null && portletTop >= 0 )
                initialWindowState.top = Math.floor( ( (portletTop > 0) ? portletTop : 0 ) );
            else
                initialWindowState.top = -1;

            var portletZIndex = this.getProperty( "z" );
            if ( ! reset && portletZIndex != null && portletZIndex >= 0 )
                initialWindowState.zIndex = Math.floor( portletZIndex );
            else if ( reset )
                initialWindowState.zIndex = -1;
        }
        else
        {
            var portletColumn = this.getProperty( jetspeed.id.PORTLET_PROP_COLUMN );
            initialWindowState.column = portletColumn;
            var portletRow = this.getProperty( jetspeed.id.PORTLET_PROP_ROW );
            initialWindowState.row = portletRow;
        }

        if ( jetspeed.debug.initializeWindowState )
        {
            if ( ! windowPositionStatic )
                dojo.debug( "initializeWindowState [" + this.entityId + "] z=" + initialWindowState.zIndex + " x=" + initialWindowState.left + " y=" + initialWindowState.top + " width=" + initialWindowState.width + " height=" + initialWindowState.height );
            else
                dojo.debug( "initializeWindowState [" + this.entityId + "] column=" + initialWindowState.column + " row=" + initialWindowState.row + " width=" + initialWindowState.width + " height=" + initialWindowState.height );
        }

        this.lastSavedWindowState = initialWindowState;

        return initialWindowState;
    },
    createPortletWindow: function( portletWindowFactory, portletContentListener )
    {
        jetspeed.ui.createPortletWindow( this, portletWindowFactory, portletContentListener );
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
            changedStateResult = { state: this._initializeWindowState( true ) };
        else
            changedStateResult = this._getChangedWindowState( volatileOnly );
        if ( changedStateResult )
        {
            var changedState = changedStateResult.state;
            
            var windowPositionStatic = this.getProperty( jetspeed.id.PORTLET_PROP_WINDOW_POSITION_STATIC );

            var queryStringFragment = "";
            var action = null;
            if ( windowPositionStatic )
            {
                action = "moveabs";
                if ( changedState.column != null )
                    queryStringFragment += "&col=" + changedState.column;
                if ( changedState.row != null )
                    queryStringFragment += "&row=" + changedState.row;
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

    retrieveContent: function( contentListener, bindArgs )
    {
        if ( contentListener == null )
            contentListener = new jetspeed.om.PortletContentListener() ;

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
    putProperty: function(name, value)
    {
        this.properties[name] = value;
    },
    getProperty: function(name)
    {
        return this.properties[name];
    },
    removeProperty: function(name)
    {        delete properties[name];
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
jetspeed.om.PortletDef = function( /* String */ portletName, /* String */ portletDisplayName, /* String */ portletDescription )
{
    this.portletName = portletName;
    this.portletDisplayName = portletDisplayName;
    this.portletDescription = portletDescription;
};
dojo.inherits( jetspeed.om.PortletDef, jetspeed.om.Id);
dojo.lang.extend( jetspeed.om.PortletDef,
{
    portletName: null,
    portletDisplayName: null,
    portletDescription: null,
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
    
    notifyFailure: function( /* String */ type, /* String */ error, /* String */ requestUrl, domainModelObject )
    {
        dojo.raise( "MenusAjaxApiContentListener error [" + domainModelObject.toString() + "] url: " + requestUrl + " type: " + type + " error: " + error );
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
    notifyFailure: function( /* String */ type, /* String */ error, /* String */ requestUrl, domainModelObject )
    {
        this.notifyCount++;
        dojo.raise( "MenuAjaxApiContentListener error [" + domainModelObject.toString() + "] url: " + requestUrl + " type: " + type + " error: " + error );
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


// ... jetspeed.om.MenusAjaxApiCallbackContentListener
jetspeed.om.PortletAddAjaxApiCallbackContentListener = function(  /* jetspeed.om.PortletDef */ portletDef, windowWidgetId )
{
    this.portletDef = portletDef;
    this.windowWidgetId = windowWidgetId;
};
dojo.lang.extend( jetspeed.om.PortletAddAjaxApiCallbackContentListener,
{
    notifySuccess: function( /* XMLDocument */ data, /* String */ requestUrl, domainModelObject )
    {
        if ( jetspeed.url.checkAjaxApiResponse( requestUrl, data, true ) )
        {
            var entityId = this.parseAddPortletResponse( data );
            if ( entityId )
                jetspeed.page.addPortlet( this.portletDef.getPortletName(), entityId, this.windowWidgetId );
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
    notifyFailure: function( /* String */ type, /* String */ error, /* String */ requestUrl, domainModelObject )
    {
        dojo.raise( "PortletAddAjaxApiCallbackContentListener error [" + domainModelObject.toString() + "] url: " + requestUrl + " type: " + type + " error: " + error );
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
        
        return new jetspeed.om.PortletDef( portletName, portletDisplayName, portletDescription ) ;
    }
});


// ... jetspeed.om.MoveAjaxApiContentListener
jetspeed.om.MoveAjaxApiContentListener = function( changedState )
{
    this.changedState = changedState;
};
jetspeed.om.MoveAjaxApiContentListener.prototype =
{
    notifySuccess: function( /* String */ content, /* String */ requestUrl, domainModelObject )
    {
        if ( jetspeed.debug.submitChangedWindowState )
            dojo.debug( "submitChangedWindowState [" + domainModelObject.portlet.entityId + "] query: " + queryString + ( jetspeed.debug.submitChangedWindowState > 1 ? (" content: " + content) : "" ) );
        dojo.lang.mixin( domainModelObject.portlet.lastSavedWindowState, this.changedState );
    },
    notifyFailure: function( /* String */ type, /* String */ error, /* String */ requestUrl, domainModelObject )
    {
        dojo.raise( "submitChangedWindowState error [" + domainModelObject.entityId + "] url: " + requestUrl + " type: " + type + " error: " + error );
    }
};

// ... jetspeed.ui methods

jetspeed.ui.createPortalTaskBar = function( taskbarParameters )
{
    if ( ! taskbarParameters )
        taskbarParameters = {};
    if ( ! taskbarParameters.widgetId )
        taskbarParameters.widgetId = jetspeed.id.TASKBAR;
    
    var nWidget = dojo.widget.createWidget( 'PortalTaskBar', taskbarParameters );
    nWidget.domNode.style.cssText = "background-color: #666; width: 100%; bottom: 5px; height: 110px";

    var addToElmt = document.getElementById( jetspeed.id.DESKTOP );
    addToElmt.appendChild( nWidget.domNode );
};
jetspeed.ui.createColumns = function( columnsParent, columnTotal )
{
    if ( columnTotal > 0 )
    {
        jetspeed.columns = new Array( columnTotal );
        var columnContainer = document.createElement( "div" );
        columnContainer.id = jetspeed.id.COLUMNS;
        for ( var i = 0 ; i < columnTotal ; i++ )
        {
            jetspeed.ui.createColumn( columnContainer, i, columnTotal );
        }
        columnsParent.appendChild( columnContainer );
    }
};
jetspeed.ui.removeColumns = function( /* DOM Node */ preserveWindowNodes )
{
    if ( jetspeed.columns && jetspeed.columns.length > 0 )
    {
        for ( var i = 0 ; i < jetspeed.columns.length ; i++ )
        {
            if ( jetspeed.columns[i] )
            {
                if ( preserveWindowNodes )
                {
                    var windowNodesInColumn = jetspeed.ui.getPortletWindowChildren( jetspeed.columns[i], null );
                    dojo.lang.forEach( windowNodesInColumn,
                        function( windowNode ) { preserveWindowNodes.appendChild( windowNode ); } );
                }
                dojo.dom.removeNode( jetspeed.columns[i] );
                jetspeed.columns[i] = null;
            }
        }
        var columnContainer = dojo.byId( jetspeed.id.COLUMNS );
        if ( columnContainer )
            dojo.dom.removeNode( columnContainer );
    }
    jetspeed.columns = [];
};
jetspeed.ui.createColumn = function( columnContainer, columnIndex, columnTotal )
{
    var divElmt = document.createElement("div");
    divElmt.setAttribute("columnIndex", columnIndex);
    var colWidthPctg = Math.round(100/columnTotal);
    if ( columnIndex == (columnTotal-1) && ( (columnTotal * colWidthPctg) >= 100 ) )
        colWidthPctg = colWidthPctg -1;
    //if ( columnIndex == 0 )
    //{
    //    colWidthPctg = "74";
    //    divElmt.className = "desktopColumn";
    //}
    //else if ( columnIndex == 1 )
    //{
    //    colWidthPctg = "25";
    //    divElmt.className = "desktopColumn";
    //}
    //else if ( columnIndex == 2 )
    //{
    //    colWidthPctg = "99";
    //    divElmt.className = "desktopColumnClear";
    //}
    divElmt.style.width = colWidthPctg + "%";
    divElmt.style.minHeight = "40px";
    divElmt.className = "DesktopColumn";
    
    jetspeed.columns[columnIndex]  = divElmt;
    columnContainer.appendChild(divElmt);
};
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
    return { portletWindowNodes: nodesPW, matchIndex: nodeMatchIndex };
};
jetspeed.ui.getPortletWindowColumnRow = function( /* DOM node */ portletWindowNode )
{
    if ( ! jetspeed.columns || jetspeed.columns.length == 0 ) return null;
    var foundInRow = null;
    var foundInColumn = null;
    for ( var colIndex = 0 ; colIndex < jetspeed.columns.length ; colIndex++ )
    {
        var cRow = null;
        var columnChildren = jetspeed.columns[colIndex].childNodes;
        for ( var colChildIndex = 0 ; colChildIndex < columnChildren.length ; colChildIndex++ )
        {
            var child = columnChildren[colChildIndex];
            if ( dojo.html.hasClass( child, jetspeed.id.PORTLET_WINDOW_STYLE_CLASS ) || dojo.html.hasClass( child, jetspeed.id.PORTLET_WINDOW_GHOST_STYLE_CLASS ) )
            {
                cRow = ( cRow == null ? 0 : cRow + 1 );
                if ( child == portletWindowNode )
                {
                    foundInColumn = colIndex;
                    foundInRow = cRow;
                    break;
                }
            }
        }
    }
    if ( foundInRow != null && foundInColumn != null )
        return { column: foundInColumn, row: foundInRow };
    return null;
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
    for ( var i = 0 ; i < jetspeed.columns.length ; i++ )
    {
        var columnElmt = jetspeed.columns[i];
        dojo.debug( "jetspeed.columns[" + i + "] outer-width: " + dojo.style.getOuterWidth( columnElmt ) );
    }
};
jetspeed.ui.dumpPortletWindowsPerColumn = function()
{
    for ( var i = 0 ; i < jetspeed.columns.length ; i++ )
    {
        var columnElmt = jetspeed.columns[i];
        var windowNodesInColumn = jetspeed.ui.getPortletWindowChildren( columnElmt, null );
        var portletWindowsInColumn = jetspeed.ui.getPortletWindowsFromNodes( windowNodesInColumn.portletWindowNodes );
        var dumpClosure = { dumpMsg: "" };
        dojo.lang.forEach( portletWindowsInColumn,
                                function(portletWindow) { dumpClosure.dumpMsg = dumpClosure.dumpMsg + ( dumpClosure.dumpMsg.length > 0 ? ", " : "" ) + portletWindow.portlet.entityId; } );
        dumpClosure.dumpMsg = "column " + i + ": " + dumpClosure.dumpMsg;
        dojo.debug( dumpClosure.dumpMsg );
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
        jetspeed.ui.widget.PortletWindow.prototype.staticDefineAsAltInitParameters( createWidgetParams, windowConfigObject );
    }

    // NOTE: other parameters, such as widgetId could be set here (to override what PortletWindow does)
    var nWidget = dojo.widget.createWidget( 'PortletWindow', createWidgetParams );
    
    return nWidget;
};

jetspeed.ui.getDefaultFloatingPaneTemplate = function()
{
    return new dojo.uri.Uri(jetspeed.url.basePortalDesktopUrl() + "/javascript/desktop/widget/HtmlFloatingPane.html");   // BOZO: improve this junk
};
jetspeed.ui.getDefaultFloatingPaneTemplateCss = function()
{
    return new dojo.uri.Uri(jetspeed.url.basePortalDesktopUrl() + "/javascript/desktop/widget/HtmlFloatingPane.css");   // BOZO: improve this junk
};
jetspeed.ui.createPortletWindow = function( windowConfigObject, portletWindowFactory, portletContentListener )
{
    if ( portletWindowFactory == null )
    {
        var winPositionStatic = windowConfigObject.getProperty( jetspeed.id.PORTLET_PROP_WINDOW_POSITION_STATIC );
        if ( winPositionStatic == null )
            winPositionStatic = ( jetspeed.prefs.windowTiling ? true : false );    // BOZO: what to do about setting the value here ( putProperty )
        if ( ! winPositionStatic )
            portletWindowFactory = new jetspeed.om.PortletWindowFactory() ;
        else
            portletWindowFactory = new jetspeed.om.PortletTilingWindowFactory() ;
    }
    
    var windowWidget = dojo.widget.byId( windowConfigObject.getProperty( jetspeed.id.PORTLET_PROP_WIDGET_ID ) );   // get existing window widget

    if ( windowWidget )
    {
        portletWindowFactory.reset( windowConfigObject, windowWidget ) ;
    }   
    else
    {
        windowWidget = portletWindowFactory.create( windowConfigObject ) ;
    }

    if ( windowWidget )
    {
        portletWindowFactory.layout( windowConfigObject, windowWidget );
    }
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
    {   // mimick dojo.fx.html.fade, but for all objects together
        for ( var i = 0 ; i < nodes.length ; i++ )
        {
            dojo.fx.html._makeFadeable(nodes[i]);
            if (visibilityStyleValue != "none")
                nodes[i].style.visibility = visibilityStyleValue ;
        }
        var anim = new dojo.animation.Animation(
		                new dojo.math.curves.Line([startOpac],[endOpac]),
		                duration, 0);
	    dojo.event.connect(anim, "onAnimate", function(e) {
            for ( var mi = 0 ; mi < nodes.length ; mi++ )
            {
                dojo.style.setOpacity(nodes[mi], e.x);
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
