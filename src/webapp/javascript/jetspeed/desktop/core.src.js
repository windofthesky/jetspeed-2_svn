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
 *
 * author: Steve Milek
 * author: David Sean Taylor
 */

/**
 * jetspeed desktop core javascript objects and types
 *
 * 2007-02-20: this file desperately needs to be broken up once deployment support
 *             for javascript compression and aggregation is implemented
 */

dojo.provide("jetspeed.desktop.core");

dojo.require("dojo.lang.*");
dojo.require("dojo.event.*");
dojo.require("dojo.io.*");
dojo.require("dojo.uri.Uri");
dojo.require("dojo.widget.*");
dojo.require("dojo.collections.ArrayList");
dojo.require("dojo.collections.Set");
dojo.require("jetspeed.common");


// ... jetspeed base objects
if ( ! window.jetspeed )
    jetspeed = {} ;
if ( ! jetspeed.om )
    jetspeed.om = {} ;
if ( ! jetspeed.ui )
    jetspeed.ui = {} ;
if ( ! jetspeed.ui.widget )
    jetspeed.ui.widget = {} ;


// ... jetspeed.id
jetspeed.id =
{
    PAGE: "jetspeedPage",
    DESKTOP_CELL: "jetspeedDesktopCell",
    DESKTOP: "jetspeedDesktop",
    COLUMNS: "jetspeedColumns",
    PAGE_CONTROLS: "jetspeedPageControls",

    TASKBAR: "jetspeedTaskbar",
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
    PORTLET_PROP_WINDOW_DECORATION: "windowDecoration",
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
    ACTION_NAME_MENU: "menu",
    ACTION_NAME_MINIMIZE: "minimized",
    ACTION_NAME_MAXIMIZE: "maximized",
    ACTION_NAME_RESTORE: "normal",
    ACTION_NAME_PRINT: "print",
    ACTION_NAME_EDIT: "edit",
    ACTION_NAME_VIEW: "view",
    ACTION_NAME_HELP: "help",
    ACTION_NAME_ADDPORTLET: "addportlet",
    ACTION_NAME_REMOVEPORTLET: "removeportlet",

    ACTION_NAME_DESKTOP_TILE: "tile",
    ACTION_NAME_DESKTOP_UNTILE: "untile",
    ACTION_NAME_DESKTOP_HEIGHT_EXPAND: "heightexpand",
    ACTION_NAME_DESKTOP_HEIGHT_NORMAL: "heightnormal",

    ACTION_NAME_LOAD_RENDER: "loadportletrender",
    ACTION_NAME_LOAD_ACTION: "loadportletaction",
    ACTION_NAME_LOAD_UPDATE: "loadportletupdate",

    PORTLET_ACTION_TYPE_MODE: "mode",
    PORTLET_ACTION_TYPE_STATE: "state",

    MENU_WIDGET_ID_PREFIX: "jetspeed-menu-",

    PAGE_EDITOR_WIDGET_ID: "jetspeed-page-editor",
    PAGE_EDITOR_INITIATE_PARAMETER: "editPage",
    PORTAL_ORIGINATE_PARAMETER: "portal",

    DEBUG_WINDOW_TAG: "js-dojo-debug"
};

// ... jetspeed desktop preferences - defaults
jetspeed.prefs = 
{
    windowTiling: true,                 // false indicates no-columns, free-floating windows
    windowHeightExpand: false,          // only meaningful when windowTiling == true

    ajaxPageNavigation: false,
    
    windowWidth: null,                  // last-ditch defaults for these defined in initializeDesktop
    windowHeight: null,

    layoutName: null,                   // do not access directly - use getLayoutName()
    layoutRootUrl: null,                // do not access directly - use getLayoutRootUrl()
    getLayoutName: function()
    {
        if ( jetspeed.prefs.layoutName == null && djConfig.jetspeed != null )
            jetspeed.prefs.layoutName = djConfig.jetspeed.layoutName;
        return jetspeed.prefs.layoutName;
    },
    getLayoutRootUrl: function()
    {
        if ( jetspeed.prefs.layoutRootUrl == null && djConfig.jetspeed != null )
            jetspeed.prefs.layoutRootUrl = jetspeed.url.basePortalDesktopUrl() + djConfig.jetspeed.layoutDecorationPath;
        return jetspeed.prefs.layoutRootUrl;
    },
    getPortletDecorationsRootUrl: function()
    {
        if ( jetspeed.prefs.portletDecorationsRootUrl == null && djConfig.jetspeed != null )
            jetspeed.prefs.portletDecorationsRootUrl = jetspeed.url.basePortalDesktopUrl() + djConfig.jetspeed.portletDecorationsPath;
        return jetspeed.prefs.portletDecorationsRootUrl;
    },

    portletSelectorWindowTitle: "Portlet Selector",
    portletSelectorWindowIcon: "text-x-script.png",
    portletSelectorBounds: { x: 20, y: 20, width: 400, height: 600 },

    windowActionButtonOrder: [ jetspeed.id.ACTION_NAME_MENU, "edit", "view", "help", jetspeed.id.ACTION_NAME_MINIMIZE, jetspeed.id.ACTION_NAME_RESTORE, jetspeed.id.ACTION_NAME_MAXIMIZE ],
    windowActionNotPortlet: [ jetspeed.id.ACTION_NAME_MENU, jetspeed.id.ACTION_NAME_MINIMIZE, jetspeed.id.ACTION_NAME_RESTORE, jetspeed.id.ACTION_NAME_MAXIMIZE ],
    windowActionButtonMax: 5,
    windowActionButtonHide: false,
    windowActionButtonTooltip: true,
    windowActionMenuOrder: [ jetspeed.id.ACTION_NAME_DESKTOP_HEIGHT_EXPAND, jetspeed.id.ACTION_NAME_DESKTOP_HEIGHT_NORMAL, jetspeed.id.ACTION_NAME_DESKTOP_TILE, jetspeed.id.ACTION_NAME_DESKTOP_UNTILE ],
    
    windowIconEnabled: true,
    windowIconPath: "/images/portlets/small/",

    windowDecoration: "tigris",

    pageActionButtonTooltip: true,

    getPortletDecorationBaseUrl: function( portletDecorationName )
    {
        return jetspeed.prefs.getPortletDecorationsRootUrl() + "/" + portletDecorationName;
    },

    getPortletDecorationConfig: function( portletDecorationName )
    {
        if ( jetspeed.prefs.portletDecorationsConfig == null || portletDecorationName == null )
            return null;
        return jetspeed.prefs.portletDecorationsConfig[ portletDecorationName ];
    }
};

// ... jetspeed debug options
jetspeed.debug =
{
    pageLoad: false,
    retrievePsml: false,
    setPortletContent: false,
    doRenderDoAction: false,
    postParseAnnotateHtml: false,
    postParseAnnotateHtmlDisableAnchors: false,
    confirmOnSubmit: false,
    createWindow: false,
    initializeWindowState: false,
    submitChangedWindowState: false,
    ajaxPageNav: false,

    windowDecorationRandom: false,

    debugContainerId: ( djConfig.debugContainerId ? djConfig.debugContainerId : dojo.hostenv.defaultDebugContainerId )
};
jetspeed.debugInPortletWindow = true;                             // dojo debug in portlet window (dojo isDebug must be true)
//jetspeed.debugPortletEntityIdFilter = [ "dp-7", "dp-3" ];       // load listed portlets only
//jetspeed.debugPortletEntityIdFilter = [];                       // disable all portlets
//jetspeed.debugContentDumpIds = [ ".*" ];                        // dump all responses
//jetspeed.debugContentDumpIds = [ "getmenus", "getmenu-.*" ];    // dump getmenus response and all getmenu responses
//jetspeed.debugContentDumpIds = [ "page-.*" ];                   // dump page psml response
//jetspeed.debugContentDumpIds = [ "js-cp-selector.2" ];          // dump portlet selector content
//jetspeed.debugContentDumpIds = [ "moveabs-layout" ];            // dump move layout response
//jetspeed.debugContentDumpIds = [ "js-cp-selector.*" ];          // dump portlet selector
// ... load page /portlets
jetspeed.page = null ;
jetspeed.initializeDesktop = function()
{
    jetspeed.url.pathInitialize();
    jetspeed.browser_IE = dojo.render.html.ie;
    jetspeed.browser_IEpre7 = ( dojo.render.html.ie50 || dojo.render.html.ie55 || dojo.render.html.ie60 );
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
        windowActionDesktop[ jetspeed.id.ACTION_NAME_DESKTOP_HEIGHT_EXPAND ] = true;
        windowActionDesktop[ jetspeed.id.ACTION_NAME_DESKTOP_HEIGHT_NORMAL ] = true;
        windowActionDesktop[ jetspeed.id.ACTION_NAME_DESKTOP_TILE ] = true;
        windowActionDesktop[ jetspeed.id.ACTION_NAME_DESKTOP_UNTILE ] = true;
        jetspeed.prefs.windowActionDesktop = windowActionDesktop;
    }
    dojo.html.insertCssFile( jetspeed.ui.getDefaultFloatingPaneTemplateCss(), document, true );

    if ( jetspeed.prefs.portletDecorationsAllowed == null || jetspeed.prefs.portletDecorationsAllowed.length == 0 )
    {
        if ( jetspeed.prefs.windowDecoration != null )
            jetspeed.prefs.portletDecorationsAllowed = [ jetspeed.prefs.windowDecoration ];
    }
    else if ( jetspeed.prefs.windowDecoration == null )
    {
        jetspeed.prefs.windowDecoration = jetspeed.prefs.portletDecorationsAllowed[0];
    }
    if ( jetspeed.prefs.windowDecoration == null || jetspeed.prefs.portletDecorationsAllowed == null )
    {
        dojo.raise( "Cannot load page because there are no defined jetspeed portlet decorations" );
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

    var docUrlObj = jetspeed.url.parse( document.location.href );
    var printModeOnly = jetspeed.url.getQueryParameter( docUrlObj, "jsprintmode" ) == "true";
    if ( printModeOnly )
    {
        printModeOnly = {};
        printModeOnly.action = jetspeed.url.getQueryParameter( docUrlObj, "jsaction" );
        printModeOnly.entity = jetspeed.url.getQueryParameter( docUrlObj, "jsentity" );
        printModeOnly.layout = jetspeed.url.getQueryParameter( docUrlObj, "jslayoutid" );
        jetspeed.prefs.printModeOnly = printModeOnly;
        jetspeed.prefs.windowTiling = true;
        jetspeed.prefs.windowHeightExpand = true;
    }

    jetspeed.prefs.portletDecorationsConfig = {};
    for ( var i = 0 ; i < jetspeed.prefs.portletDecorationsAllowed.length ; i++ )
    {
        jetspeed.loadPortletDecorationConfig( jetspeed.prefs.portletDecorationsAllowed[ i ] );
    }
    
    jetspeed.debugWindowLoad();

    if ( jetspeed.prefs.printModeOnly != null )
    {
        for ( var portletDecorationName in jetspeed.prefs.portletDecorationsConfig )
        {
            var pdConfig = jetspeed.prefs.portletDecorationsConfig[ portletDecorationName ];
            if ( pdConfig != null )
            {
                pdConfig.windowActionButtonOrder = null;
                pdConfig.windowActionMenuOrder = null;
                pdConfig.windowDisableResize = true;
                pdConfig.windowDisableMove = true;
            }
        }
    }
    jetspeed.url.loadingIndicatorShow();
    jetspeed.loadPage();
};
jetspeed.loadPage = function()
{
    jetspeed.page = new jetspeed.om.Page();
    jetspeed.page.retrievePsml();
};
jetspeed.updatePage = function( navToPageUrl, backOrForwardPressed )
{
    var previousPage = jetspeed.page;
    if ( ! navToPageUrl || jetspeed.pageNavigateSuppress ) return;
    if ( previousPage && previousPage.equalsPageUrl( navToPageUrl ) )
        return ;
    navToPageUrl = jetspeed.page.makePageUrl( navToPageUrl );
    if ( previousPage != null && navToPageUrl != null )
    {
        var previousPageUrl = previousPage.getPageUrl();
        previousPage.destroy();
        var newJSPage = new jetspeed.om.Page( jetspeed.page.layoutDecorator, navToPageUrl, (! djConfig.preventBackButtonFix && ! backOrForwardPressed) );
        jetspeed.page = newJSPage;
        newJSPage.retrievePsml();
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
    var jsPageUrl = null ;
    if ( isPageLoad )
    {
        jsPageUrl = jetspeed.url.parse( jetspeed.page.getPageUrl() );
    }
    for ( var i = 0; i < windowArray.length; i++ )
    {
        var renderObj = windowArray[i];
        if ( (debugMsg || debugPageLoad) )
        {
            if ( i > 0 ) renderMsg = renderMsg + ", ";
            var widgetId = null;
            if ( renderObj.getProperty != null )
                widgetId = renderObj.getProperty( jetspeed.id.PORTLET_PROP_WIDGET_ID );
            if ( ! widgetId )
                widgetId = renderObj.widgetId;
            if ( ! widgetId )
                widgetId = renderObj.toString();
            if ( renderObj.entityId )
            {
                renderMsg = renderMsg + renderObj.entityId + "(" + widgetId + ")";
                if ( debugPageLoad && renderObj.getProperty( jetspeed.id.PORTLET_PROP_WINDOW_TITLE ) )
                    renderMsg = renderMsg + " " + renderObj.getProperty( jetspeed.id.PORTLET_PROP_WINDOW_TITLE );
            }
            else
            {
                renderMsg = renderMsg + widgetId;
            }
        }
        renderObj.retrieveContent( null, { url: url, jsPageUrl: jsPageUrl }, suppressGetActions );
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
        targetPortlet.retrieveContent( new jetspeed.om.PortletActionContentListener( targetPortlet, bindArgs ), bindArgs );
    }
};

jetspeed.portleturl =
{
    DESKTOP_ACTION_PREFIX_URL: null,
    DESKTOP_RENDER_PREFIX_URL: null,
    JAVASCRIPT_ARG_QUOTE: "&" + "quot;",
    PORTLET_REQUEST_ACTION: "action",
    PORTLET_REQUEST_RENDER: "render",
    JETSPEED_DO_NOTHING_ACTION: "javascript:jetspeed.doNothingNav()",

    parseContentUrlForDesktopActionRender: function( /* String */ contentUrl )
    {
        if ( this.DESKTOP_ACTION_PREFIX_URL == null )
            this.DESKTOP_ACTION_PREFIX_URL = jetspeed.url.basePortalUrl() + jetspeed.url.path.ACTION ;
        if ( this.DESKTOP_RENDER_PREFIX_URL == null )
            this.DESKTOP_RENDER_PREFIX_URL = jetspeed.url.basePortalUrl() + jetspeed.url.path.RENDER ;
        var op = null;
        var justTheUrl = contentUrl;
        var entityId = null;
        if ( contentUrl && contentUrl.length > this.DESKTOP_ACTION_PREFIX_URL.length && contentUrl.indexOf( this.DESKTOP_ACTION_PREFIX_URL ) == 0 )
        {   // annotate away javascript invocation in form action
            op = jetspeed.portleturl.PORTLET_REQUEST_ACTION;
        }
        else if ( contentUrl && contentUrl.length > this.DESKTOP_RENDER_PREFIX_URL.length && contentUrl.indexOf( this.DESKTOP_RENDER_PREFIX_URL ) == 0 )
        {
            op = jetspeed.portleturl.PORTLET_REQUEST_RENDER;
        }
        if ( op != null )
        {
            entityId = jetspeed.url.getQueryParameter( contentUrl, "entity" );
            //dojo.debug( "portlet-url op=" + op  + " entity=" + entityId + " url=" + contentUrl );  
        }
        
        if ( ! jetspeed.url.validateUrlStartsWithHttp( justTheUrl ) )
            justTheUrl = null;

        return { url: justTheUrl, operation: op, portletEntityId: entityId };
    },

    generateJSPseudoUrlActionRender: function( parsedPseudoUrl, makeDummy )
    {   // NOTE: no form can be passed in one of these
        if ( ! parsedPseudoUrl || ! parsedPseudoUrl.url || ! parsedPseudoUrl.portletEntityId ) return null;
        var hrefJScolon = null;
        if ( makeDummy )
        {
            hrefJScolon = jetspeed.portleturl.JETSPEED_DO_NOTHING_ACTION;
        }
        else
        {
            hrefJScolon = "javascript:";
            var badnews = false;
            if ( parsedPseudoUrl.operation == jetspeed.portleturl.PORTLET_REQUEST_ACTION )
                hrefJScolon += "doAction(\"";
            else if ( parsedPseudoUrl.operation == jetspeed.portleturl.PORTLET_REQUEST_RENDER )
                hrefJScolon += "doRender(\"";
            else badnews = true;
            if ( badnews ) return null;
            hrefJScolon += parsedPseudoUrl.url + "\",\"" + parsedPseudoUrl.portletEntityId + "\"";
            hrefJScolon += ")";
        }
        return hrefJScolon;
    }

};

jetspeed.doNothingNav = function()
{   // replacing form actions with javascript: doNothingNav() is 
    // useful for preventing form submission in cases like: <a onclick="form.submit(); return false;" >
    // JSF h:commandLink uses the above anchor onclick practice
    false;
};
jetspeed.loadPortletDecorationStyles = function( portletDecorationName )
{
    var portletDecorationConfig = jetspeed.prefs.getPortletDecorationConfig( portletDecorationName );
    if ( portletDecorationConfig != null && ! portletDecorationConfig.css_loaded )
    {
        var pdBaseUrl = jetspeed.prefs.getPortletDecorationBaseUrl( portletDecorationName );
        portletDecorationConfig.css_loaded = true;
        portletDecorationConfig.cssPathCommon = new dojo.uri.Uri( pdBaseUrl + "/css/styles.css" );
        portletDecorationConfig.cssPathDesktop = new dojo.uri.Uri( pdBaseUrl + "/css/desktop.css" );
        
        dojo.html.insertCssFile( portletDecorationConfig.cssPathCommon, null, true );
        dojo.html.insertCssFile( portletDecorationConfig.cssPathDesktop, null, true );
    }
    return portletDecorationConfig;
};
jetspeed.loadPortletDecorationConfig = function( portletDecorationName )
{
    // setup default portlet decoration config
    var pdConfig = {};
    jetspeed.prefs.portletDecorationsConfig[ portletDecorationName ] = pdConfig;
    pdConfig.windowActionButtonOrder = jetspeed.prefs.windowActionButtonOrder;
    pdConfig.windowActionNotPortlet = jetspeed.prefs.windowActionNotPortlet;
    pdConfig.windowActionButtonMax = jetspeed.prefs.windowActionButtonMax;
    pdConfig.windowActionButtonHide = jetspeed.prefs.windowActionButtonHide;
    pdConfig.windowActionButtonTooltip = jetspeed.prefs.windowActionButtonTooltip;
    pdConfig.windowActionMenuOrder = jetspeed.prefs.windowActionMenuOrder;
    pdConfig.windowActionNoImage = jetspeed.prefs.windowActionNoImage;
    pdConfig.windowIconEnabled = jetspeed.prefs.windowIconEnabled;
    pdConfig.windowIconPath = jetspeed.prefs.windowIconPath;

    // load portlet decoration config
    var portletDecorationConfigUri = jetspeed.prefs.getPortletDecorationBaseUrl( portletDecorationName ) + "/" + portletDecorationName + ".js";
    dojo.hostenv.loadUri( portletDecorationConfigUri, function(hash) {
				for ( var j in hash )
                {
                    pdConfig[ j ] = hash[j];
                }
                if ( pdConfig.windowActionNoImage != null )
                {
                    var noImageMap = {};
                    for ( var i = 0 ; i < pdConfig.windowActionNoImage.length; i++ )
                    {
                        noImageMap[ pdConfig.windowActionNoImage[ i ] ] = true;
                    }
                    pdConfig.windowActionNoImage = noImageMap;
                }
                if ( pdConfig.windowIconPath != null )
                {
                    pdConfig.windowIconPath = dojo.string.trim( pdConfig.windowIconPath );
                    if ( pdConfig.windowIconPath == null || pdConfig.windowIconPath.length == 0 )
                        pdConfig.windowIconPath = null;
                    else
                    {
                        var winIconsPath = pdConfig.windowIconPath;
                        var firstCh = winIconsPath.charAt(0);
                        if ( firstCh != "/" )
                            winIconsPath = "/" + winIconsPath;
                        var lastCh = winIconsPath.charAt( winIconsPath.length -1 );
                        if ( lastCh != "/" )
                            winIconsPath = winIconsPath + "/";
                        pdConfig.windowIconPath = winIconsPath;
                    }
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
    
    jetspeed.url.loadingIndicatorHide();
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
jetspeed.pageNavigate = function( navUrl, navTarget, force )
{
    if ( ! navUrl || jetspeed.pageNavigateSuppress ) return;

    if ( typeof force == "undefined" )
        force = false;

    if ( ! force && jetspeed.page && jetspeed.page.equalsPageUrl( navUrl ) )
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
    windowParams[ jetspeed.id.PORTLET_PROP_WINDOW_DECORATION ] = jetspeed.page.getPortletDecorationDefault();
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

jetspeed.getFolders = function(data, handler)
{
    var contentListener = new jetspeed.om.FoldersListContentListener(handler);
    var queryString = "?action=getfolders&data=" + data;
    var getPortletsUrl = jetspeed.url.basePortalUrl() + jetspeed.url.path.AJAX_API + queryString ;
    var mimetype = "text/xml";
	//alert('getPortletsUrl ' + getPortletsUrl);
    var ajaxApiContext = new jetspeed.om.Id( "getfolders", { } );
    jetspeed.url.retrieveContent( { url: getPortletsUrl, mimetype: mimetype }, contentListener, ajaxApiContext, jetspeed.debugContentDumpIds );
};

jetspeed.portletDefinitionsforSelector = function(filter,category,pagenumber,portletPerPages,catPortlets)
{
    var contentListener = new jetspeed.om.PortletSelectorSearchContentListener(catPortlets);
    var queryString = "?action=selectorPortlets&category=" + category + "&portletPerPages=" + portletPerPages + "&pageNumber=" + pagenumber + "&filter=" + filter;
    var getPortletsUrl = jetspeed.url.basePortalUrl() + jetspeed.url.path.AJAX_API + queryString ;
    var mimetype = "text/xml";
    //alert('getPortletsUrl '  + getPortletsUrl);
	var ajaxApiContext = new jetspeed.om.Id( "selectorPortlets", { } );
    jetspeed.url.retrieveContent( { url: getPortletsUrl, mimetype: mimetype }, contentListener, ajaxApiContext, jetspeed.debugContentDumpIds );
};
jetspeed.getActionsForPortlet = function( /* String */ portletEntityId )
{
    if ( portletEntityId == null ) return;
    jetspeed.getActionsForPortlets( [ portletEntityId ] );
};
jetspeed.getActionsForPortlets = function( /* Array */ portletEntityIds )
{
    if ( portletEntityIds == null )
        portletEntityIds = jetspeed.page.getPortletIds();
    var contentListener = new jetspeed.om.PortletActionsContentListener( portletEntityIds );
    var queryString = "?action=getactions";
    for ( var i = 0 ; i < portletEntityIds.length ; i++ )
    {
        queryString += "&id=" + portletEntityIds[i];
    }
    var getActionsUrl = jetspeed.url.basePortalUrl() + jetspeed.url.path.AJAX_API + jetspeed.page.getPath() + queryString;
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
    var changeActionUrl = jetspeed.url.basePortalUrl() + jetspeed.url.path.AJAX_API + jetspeed.page.getPath() + queryString ;
    var mimetype = "text/xml";
    var ajaxApiContext = new jetspeed.om.Id( "changeaction", { } );
    jetspeed.url.retrieveContent( { url: changeActionUrl, mimetype: mimetype }, contentListener, ajaxApiContext, jetspeed.debugContentDumpIds );
};

jetspeed.addNewPortletDefinition = function( /* jetspeed.om.PortletDef */ portletDef, windowWidgetId, /* String */ psmlUrl, /* String */ layoutId )
{
    var addToCurrentPage = true;
    if ( psmlUrl != null )
    {
        addToCurrentPage = false;
    }
    var contentListener = new jetspeed.om.PortletAddAjaxApiCallbackContentListener( portletDef, windowWidgetId, addToCurrentPage );
    var queryString = "?action=add&id=" + escape( portletDef.getPortletName() );
    if ( layoutId != null && layoutId.length > 0 )
    {
        queryString += "&layoutid=" + escape( layoutId );
    }
    var addPortletUrl = null;
    if ( psmlUrl != null )
    {
        addPortletUrl = psmlUrl + queryString;   //  psmlUrl example: http://localhost:8080/jetspeed/ajaxapi/google-maps.psml
    }
    else
    {
        addPortletUrl = jetspeed.page.getPsmlUrl() + queryString;
    }
    var mimetype = "text/xml";
    var ajaxApiContext = new jetspeed.om.Id( "addportlet", { } );
    jetspeed.url.retrieveContent( { url: addPortletUrl, mimetype: mimetype }, contentListener, ajaxApiContext, jetspeed.debugContentDumpIds );
};

jetspeed.editPageInitiate = function()
{
    if ( ! jetspeed.page.editMode )
    {
        var fromDesktop = true;
        var fromPortal = jetspeed.url.getQueryParameter( document.location.href, jetspeed.id.PORTAL_ORIGINATE_PARAMETER );
        if ( fromPortal != null && fromPortal == "true" )
            fromDesktop = false;
        jetspeed.page.editMode = true;
        var pageEditorWidget = dojo.widget.byId( jetspeed.id.PAGE_EDITOR_WIDGET_ID );
        if ( pageEditorWidget == null )
        {
            try
            {
                jetspeed.url.loadingIndicatorShow( "loadpageeditor" );
                pageEditorWidget = dojo.widget.createWidget( "jetspeed:PageEditor", { widgetId: jetspeed.id.PAGE_EDITOR_WIDGET_ID, editorInitiatedFromDesktop: fromDesktop } );
                var allColumnsContainer = document.getElementById( jetspeed.id.COLUMNS );
                allColumnsContainer.insertBefore( pageEditorWidget.domNode, allColumnsContainer.firstChild );
            }
            catch (e)
            {
                jetspeed.url.loadingIndicatorHide();
            }
        }
        else
        {
            pageEditorWidget.editPageShow();
        }
        jetspeed.page.syncPageControls();
    }
};
jetspeed.editPageTerminate = function()
{
    if ( jetspeed.page.editMode )
    {
        var pageEditorWidget = dojo.widget.byId( jetspeed.id.PAGE_EDITOR_WIDGET_ID );
        pageEditorWidget.editModeNormal();  // in case we're in move-mode
        jetspeed.page.editMode = false;
        if ( ! pageEditorWidget.editorInitiatedFromDesktop )
        {
            var portalPageUrl = jetspeed.page.getPageUrl( true );
            portalPageUrl = jetspeed.url.removeQueryParameter( portalPageUrl, jetspeed.id.PAGE_EDITOR_INITIATE_PARAMETER );
            portalPageUrl = jetspeed.url.removeQueryParameter( portalPageUrl, jetspeed.id.PORTAL_ORIGINATE_PARAMETER );
            window.location.href = portalPageUrl;
        }
        else
        {
            if ( pageEditorWidget != null )
            {
                pageEditorWidget.editPageHide();
            }
            jetspeed.page.syncPageControls();
        }
    }
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
        dojo.raise( "PortletSelectorContentListener notifyFailure url: " + requestUrl + " type: " + type + jetspeed.url.formatBindError( error ) );
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
        dojo.raise( "PageContentListenerUpdate notifyFailure url: " + requestUrl + " type: " + type + jetspeed.url.formatBindError( error ) );
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
        dojo.raise( "PageContentListenerCreateWidget error url: " + requestUrl + " type: " + type + jetspeed.url.formatBindError( error ) );
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
jetspeed.om.Page = function( requiredLayoutDecorator, navToPageUrl, addToHistory )
{
    if ( requiredLayoutDecorator != null && navToPageUrl != null )
    {
        this.requiredLayoutDecorator = requiredLayoutDecorator;
        this.setPsmlPathFromDocumentUrl( navToPageUrl );
        this.pageUrlFallback = navToPageUrl;
    }
    else
    {
        this.setPsmlPathFromDocumentUrl();
    }
    this.addToHistory = addToHistory;
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
    pageUrl: null,
    pagePathAndQuery: null,
    title: null,
    shortTitle: null,
    layoutDecorator: null,
    portletDecorator: null,

    requiredLayoutDecorator: null,
    pageUrlFallback: null,

    layouts: null,
    columns: null,
    portlets: null,

    editMode: false,
    themeDefinitions: null,

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
    
    setPsmlPathFromDocumentUrl: function( navToPageUrl )
    {
        var psmlPath = jetspeed.url.path.AJAX_API;
        var docPath = null;
        if ( navToPageUrl == null )
            docPath = document.location.pathname;
        else
        {
            var uObj = jetspeed.url.parse( navToPageUrl );
            docPath = uObj.path;
        }
        var contextAndServletPath = jetspeed.url.path.DESKTOP;
        var contextAndServletPathPos = docPath.indexOf( contextAndServletPath );
        if ( contextAndServletPathPos != -1 && docPath.length > ( contextAndServletPathPos + contextAndServletPath.length ) )
        {
            psmlPath = psmlPath + docPath.substring( contextAndServletPathPos + contextAndServletPath.length );
        }
        this.psmlPath = psmlPath;
    },
    
    getPsmlUrl: function()
    {
        if ( this.psmlPath == null )
            this.setPsmlPathFromDocumentUrl();

        var psmlUrl = jetspeed.url.basePortalUrl() + this.psmlPath;
        if ( jetspeed.prefs.printModeOnly != null )
        {
            psmlUrl = jetspeed.url.addQueryParameter( psmlUrl, "layoutid", jetspeed.prefs.printModeOnly.layout );
            psmlUrl = jetspeed.url.addQueryParameter( psmlUrl, "entity", jetspeed.prefs.printModeOnly.entity ).toString();
        }
        return psmlUrl;
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
        if ( parsedRootLayoutFragment == null ) return;

        // create layout model
        var portletsByPageColumn = {};
        this.columnsStructure = this._layoutCreateModel( parsedRootLayoutFragment, null, portletsByPageColumn, true );

        this.rootFragmentId = parsedRootLayoutFragment.id ;

        // create columns
        if ( jetspeed.prefs.windowTiling )
        {
            this._createColumnsStart( document.getElementById( jetspeed.id.DESKTOP ) );
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

        if ( jetspeed.prefs.printModeOnly == null )
        {
            // render portlets
            if ( windowsToRender && windowsToRender.length > 0 )
            {
                jetspeed.doRenderAll( null, windowsToRender, true );
            }

            // initialize portlet window state
            this._portletsInitializeWindowState( portletsByPageColumn[ "z" ] );

            // load menus
            this.retrieveAllMenus();
    
            // render page buttons
            this.renderPageControls();
            this.syncPageControls();
    
            // detect edit mode force - likely to be temporary
            var pageEditorInititate = jetspeed.url.getQueryParameter( document.location.href, jetspeed.id.PAGE_EDITOR_INITIATE_PARAMETER );
            if ( ( pageEditorInititate != null && pageEditorInititate == "true" ) || this.actions[ jetspeed.id.ACTION_NAME_VIEW ] != null )
            {
                if ( this.actions != null && ( this.actions[ jetspeed.id.ACTION_NAME_EDIT ] != null || this.actions[ jetspeed.id.ACTION_NAME_VIEW ] != null ) )
                    jetspeed.editPageInitiate();
            }

        }
        else
        {
            var portlet = null;
            for ( var portletIndex in this.portlets )
            {
                portlet = this.portlets[portletIndex];
                break;
            }
            if ( portlet != null )
            {
                portlet.renderAction( null, jetspeed.prefs.printModeOnly.action );

                this._portletsInitializeWindowState( portletsByPageColumn[ "z" ] );
            }
        }
    },
    _parsePSML: function( psml )
    {
        var pageElements = psml.getElementsByTagName( "page" );
        if ( ! pageElements || pageElements.length > 1 )
            dojo.raise( "unexpected zero or multiple <page> elements in psml" );
        var pageElement = pageElements[0];
        var children = pageElement.childNodes;
        var simpleValueLNames = new RegExp( "(name|path|profiledPath|title|short-title)" );
        var rootFragment = null;
        var rootFragmentActions = {};
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
            else if ( childLName == "action" )
            {
                this._parsePSMLAction( child, rootFragmentActions ) ;
            }
        }
        this.actions = rootFragmentActions;

        if ( rootFragment == null )
        {
            dojo.raise( "No root fragment in PSML." );
            return null;
        }
        if ( this.requiredLayoutDecorator != null && this.pageUrlFallback != null )
        {
            if ( this.layoutDecorator != this.requiredLayoutDecorator )
            {
                if ( jetspeed.debug.ajaxPageNav ) 
                    dojo.debug( "ajaxPageNavigation _parsePSML different layout decorator (" + this.requiredLayoutDecorator + " != " + this.layoutDecorator + ") - fallback to normal page navigation - " + this.pageUrlFallback );
                jetspeed.pageNavigate( this.pageUrlFallback, null, true );
                return null;
            }
            else if ( this.addToHistory )
            {
                var currentPageUrl = this.getPageUrl();
                dojo.undo.browser.addToHistory({
	    	        back: function() { if ( jetspeed.debug.ajaxPageNav ) dojo.debug( "back-nav-button: " + currentPageUrl ); jetspeed.updatePage( currentPageUrl, true ); },
		            forward: function() { if ( jetspeed.debug.ajaxPageNav ) dojo.debug( "forward-nav-button: " + currentPageUrl ); jetspeed.updatePage( currentPageUrl, true ); },
		            changeUrl: false
		        });
            }
        }
        else if ( ! djConfig.preventBackButtonFix && jetspeed.prefs.ajaxPageNavigation )
        {
            var currentPageUrl = this.getPageUrl();
            dojo.undo.browser.setInitialState({
                back: function() { if ( jetspeed.debug.ajaxPageNav ) dojo.debug( "back-nav-button initial: " + currentPageUrl ); jetspeed.updatePage( currentPageUrl, true ); },
                forward: function() { if ( jetspeed.debug.ajaxPageNav ) dojo.debug( "forward-nav-button initial: " + currentPageUrl ); jetspeed.updatePage( currentPageUrl, true ); },
                changeUrl: false
            });
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
        var layoutActionsDisabled = false;
        var layoutFragNameAttr = layoutNode.getAttribute( "name" );
        if ( layoutFragNameAttr != null )
        {
            layoutFragNameAttr = layoutFragNameAttr.toLowerCase();
            if ( layoutFragNameAttr.indexOf( "noactions" ) != -1 )
            {
                layoutActionsDisabled = true;
            }
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
                    var portletProps = this._parsePSMLProperties( child, null );
                    var portletIcon = portletProps[ jetspeed.id.PORTLET_PROP_WINDOW_ICON ];
                    if ( portletIcon == null || portletIcon.length == 0 )
                    {
                        portletIcon = this._parsePSMLIcon( child );
                        if ( portletIcon != null && portletIcon.length > 0 )
                        {
                            portletProps[ jetspeed.id.PORTLET_PROP_WINDOW_ICON ] = portletIcon;
                        }
                    }
                    fragChildren.push( { id: child.getAttribute( "id" ), type: fragType, name: child.getAttribute( "name" ), properties: portletProps, actions: this._parsePSMLActions( child, null ), currentActionState: this._parsePSMLCurrentActionState( child ), currentActionMode: this._parsePSMLCurrentActionMode( child ), decorator: child.getAttribute( "decorator" ), layoutActionsDisabled: layoutActionsDisabled, documentOrderIndex: i } );
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
                    if ( jetspeed.prefs.printModeOnly != null )
                    {
                        sizes = [ "100" ];
                        sizesSum = 100;
                    }
                    else
                    {
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
        return { id: layoutNode.getAttribute( "id" ), type: layoutFragType, name: layoutNode.getAttribute( "name" ), decorator: layoutNode.getAttribute( "decorator" ), columnSizes: sizes, columnSizesSum: sizesSum, properties: propertiesMap, fragments: fragChildren, layoutFragmentIndexes: layoutFragIndexes, otherFragmentIndexes: otherFragIndexes, layoutActionsDisabled: layoutActionsDisabled, documentOrderIndex: layoutNodeDocumentOrderIndex };
    },
    _parsePSMLActions: function( fragmentNode, actionsMap )
    {
        if ( actionsMap == null )
            actionsMap = {};
        var actionChildren = fragmentNode.getElementsByTagName( "action" );
        for( var actionsIdx=0; actionsIdx < actionChildren.length; actionsIdx++ )
        {
            var actionNode = actionChildren[actionsIdx];
            this._parsePSMLAction( actionNode, actionsMap );
        }
        return actionsMap;
    },
    _parsePSMLAction: function( actionNode, actionsMap )
    {
        var actionName = actionNode.getAttribute( "id" );
        if ( actionName != null )
        {
            var actionType = actionNode.getAttribute( "type" );
            var actionLabel = actionNode.getAttribute( "name" );
            var actionUrl = actionNode.getAttribute( "url" );
            var actionAlt = actionNode.getAttribute( "alt" );
            actionsMap[ actionName.toLowerCase() ] = { id: actionName, type: actionType, label: actionLabel, url: actionUrl, alt: actionAlt };
        }
    },
    _parsePSMLCurrentActionState: function( fragmentNode )
    {
        var nodes = fragmentNode.getElementsByTagName( "state" );
        if ( nodes != null && nodes.length == 1 && nodes[0].firstChild != null )
        {
            return nodes[0].firstChild.nodeValue;
        }
        return null;
    },
    _parsePSMLCurrentActionMode: function( fragmentNode )
    {
        var nodes = fragmentNode.getElementsByTagName( "mode" );
        if ( nodes != null && nodes.length == 1 && nodes[0].firstChild != null )
        {
            return nodes[0].firstChild.nodeValue;
        }
        return null;
    },
    _parsePSMLIcon: function( fragmentNode )
    {
        var nodes = fragmentNode.getElementsByTagName( "icon" );
        if ( nodes != null && nodes.length == 1 && nodes[0].firstChild != null )
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

    _layoutCreateModel: function( layoutFragment, parentColumn, portletsByPageColumn, omitLayoutHeader )
    {  // layoutFragmentParentColumnIndex, parentColumnsInLayout
        var allColumnsStartIndex = this.columns.length;
        var colModelResult = this._layoutRegisterAndCreateColumnsModel( layoutFragment, parentColumn, omitLayoutHeader );
        var columnsInLayout = colModelResult.columnsInLayout;
        if ( colModelResult.addedLayoutHeaderColumn )
            allColumnsStartIndex++;
        var columnsInLayoutLen = ( columnsInLayout == null ? 0 : columnsInLayout.length ) ;

        if ( layoutFragment.layoutFragmentIndexes != null && layoutFragment.layoutFragmentIndexes.length > 0 )
        {   // layout contains child layout fragments
            var currentClonedLayoutFragByCol = null;
            var clonedLayoutFragmentCount = 0;
            if ( layoutFragment.otherFragmentIndexes != null && layoutFragment.otherFragmentIndexes.length > 0 )
                currentClonedLayoutFragByCol = new Array();

            for ( var i = 0 ; i < layoutFragment.fragments.length ; i++ )
            {
                var childFrag = layoutFragment.fragments[ i ];
            }
            
            var columnsInLayoutPopulated = new Array();
            for ( var i = 0 ; i < columnsInLayoutLen ; i++ )
            {
                if ( currentClonedLayoutFragByCol != null )
                    currentClonedLayoutFragByCol.push( null );
                columnsInLayoutPopulated.push( false );
            }
            for ( var i = 0 ; i < layoutFragment.fragments.length ; i++ )
            {
                var childFrag = layoutFragment.fragments[ i ];
                var childFragInColIndex = i;
                if ( childFrag.properties && childFrag.properties[ jetspeed.id.PORTLET_PROP_COLUMN ] >= 0 )
                {
                    if ( childFrag.properties[ jetspeed.id.PORTLET_PROP_COLUMN ] != null && childFrag.properties[ jetspeed.id.PORTLET_PROP_COLUMN ] >= 0 )
                        childFragInColIndex = childFrag.properties[ jetspeed.id.PORTLET_PROP_COLUMN ] ;
                }
                if ( childFragInColIndex >= columnsInLayoutLen )
                {
                    childFragInColIndex = ( columnsInLayoutLen > 0 ? ( columnsInLayoutLen -1 ) : 0 );
                }

                var currentClonedLayoutFragForCol = ( (currentClonedLayoutFragByCol == null) ? null : currentClonedLayoutFragByCol[ childFragInColIndex ] );
                if ( childFrag.type == "layout" )
                {
                    columnsInLayoutPopulated[ childFragInColIndex ] = true;
                    if ( currentClonedLayoutFragForCol != null )
                    {
                        this._layoutCreateModel( currentClonedLayoutFragForCol, columnsInLayout[childFragInColIndex], portletsByPageColumn, true ) ;
                        currentClonedLayoutFragByCol[ childFragInColIndex ] = null;
                    }
                    this._layoutCreateModel( childFrag, columnsInLayout[childFragInColIndex], portletsByPageColumn, false ) ;
                }
                else
                {
                    if ( currentClonedLayoutFragForCol == null )
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
                        clonedPortletLayout.columnSizes = [ "100" ];
                        clonedPortletLayout.columnSizesSum = [ 100 ];
                        clonedPortletLayout.id = clonedPortletLayout.id + "-jsclone_" + clonedLayoutFragmentCount;
                        currentClonedLayoutFragByCol[ childFragInColIndex ] = clonedPortletLayout;
                        currentClonedLayoutFragForCol = clonedPortletLayout;
                    }
                    currentClonedLayoutFragForCol.fragments.push( childFrag );
                    currentClonedLayoutFragForCol.otherFragmentIndexes.push( currentClonedLayoutFragForCol.fragments.length -1 );
                }
            }
            if ( currentClonedLayoutFragByCol != null )
            {
                for ( var i = 0 ; i < columnsInLayoutLen ; i++ )
                {
                    var currentClonedLayoutFragForCol = currentClonedLayoutFragByCol[ i ];
                    if ( currentClonedLayoutFragForCol != null )
                    {
                        columnsInLayoutPopulated[ i ] = true;
                        this._layoutCreateModel( currentClonedLayoutFragForCol, columnsInLayout[i], portletsByPageColumn, true ) ;
                    }
                }
            }
            for ( var i = 0 ; i < columnsInLayoutLen ; i++ )
            {
                if ( columnsInLayoutPopulated[ i ] )
                    columnsInLayout[i].columnContainer = true;   // column cannot contain portlets
            }
            if ( layoutFragment.otherFragmentIndexes != null && layoutFragment.otherFragmentIndexes.length > 0 )
            {
                var correctedFragments = new Array();
                for ( var i = 0 ; i < layoutFragment.fragments.length ; i++ )
                {
                    var includeFrag = true;
                    for ( var j = 0 ; j < layoutFragment.otherFragmentIndexes.length ; j++ )
                    {
                        if ( layoutFragment.otherFragmentIndexes[j] == i )
                        {
                            includeFrag = false;
                            break;
                        }
                    }
                    if ( includeFrag )
                        correctedFragments.push( layoutFragment.fragments[ i ] );
                }
                layoutFragment.fragments = correctedFragments;
                layoutFragment.otherFragmentIndexes = new Array();
            }
        }
        this._layoutCreatePortletsModel( layoutFragment, columnsInLayout, allColumnsStartIndex, portletsByPageColumn ) ;

        return columnsInLayout;
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

    _layoutRegisterAndCreateColumnsModel: function( layoutFragment, parentColumn, omitLayoutHeader )
    {
        this.layouts[ layoutFragment.id ] = layoutFragment;
        var addedLayoutHeaderColumn = false;
        var columnsInLayout = new Array();
        if ( jetspeed.prefs.windowTiling && layoutFragment.columnSizes.length > 0 )
        {
            var subOneLast = false;
            if ( jetspeed.browser_IE ) // IE can't deal with 100% here on any nested column - so subtract 0.1% - bug not fixed in IE7
                subOneLast = true;
            
            if ( parentColumn != null && ! omitLayoutHeader )
            {
                var layoutHeaderColModelObj = new jetspeed.om.Column( 0, layoutFragment.id, ( subOneLast ? layoutFragment.columnSizesSum-0.1 : layoutFragment.columnSizesSum ), this.columns.length, layoutFragment.layoutActionsDisabled );
                layoutHeaderColModelObj.layoutHeader = true;
                this.columns.push( layoutHeaderColModelObj );
                if ( parentColumn.columnChildren == null )
                    parentColumn.columnChildren = new Array();
                parentColumn.columnChildren.push( layoutHeaderColModelObj );
                parentColumn = layoutHeaderColModelObj;
                addedLayoutHeaderColumn = true;
            }
            
            for ( var i = 0 ; i < layoutFragment.columnSizes.length ; i++ )
            {
                var size = layoutFragment.columnSizes[i];
                if ( subOneLast && i == (layoutFragment.columnSizes.length - 1) )
                    size = size - 0.1;
                var colModelObj = new jetspeed.om.Column( i, layoutFragment.id, size, this.columns.length, layoutFragment.layoutActionsDisabled );
                this.columns.push( colModelObj );
                if ( parentColumn != null )
                {
                    if ( parentColumn.columnChildren == null )
                        parentColumn.columnChildren = new Array();
                    parentColumn.columnChildren.push( colModelObj );
                }
                columnsInLayout.push( colModelObj );
            }
        }
        return { columnsInLayout: columnsInLayout, addedLayoutHeaderColumn: addedLayoutHeaderColumn };
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
                    if ( portletWindowExtendedProperty != null && jetspeed.prefs.windowTiling && jetspeed.prefs.printModeOnly == null )
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
                    var portlet = new jetspeed.om.Portlet( pFrag.name, pFrag.id, null, pFrag.properties, pFrag.actions, pFrag.currentActionState, pFrag.currentActionMode, pFrag.decorator, pFrag.layoutActionsDisabled );
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

    _createColumnsStart: function( allColumnsParent )
    {
        if ( ! this.columnsStructure || this.columnsStructure.length == 0 ) return;
        var columnContainerNode = document.createElement( "div" );
        columnContainerNode.id = jetspeed.id.COLUMNS;
        columnContainerNode.setAttribute( "id", jetspeed.id.COLUMNS );
        for ( var colIndex = 0 ; colIndex < this.columnsStructure.length ; colIndex++ )
        {
            var colObj = this.columnsStructure[colIndex];
            this._createColumns( colObj, columnContainerNode ) ;
        }
        allColumnsParent.appendChild( columnContainerNode );
    },

    _createColumns: function( column, columnContainerNode )
    {
        column.createColumn() ;
        if ( column.columnChildren != null && column.columnChildren.length > 0 )
        {
            for ( var colIndex = 0 ; colIndex < column.columnChildren.length ; colIndex++ )
            {
                var colObj = column.columnChildren[ colIndex ];
                this._createColumns( colObj, column.domNode ) ;
            }
        }
        columnContainerNode.appendChild( column.domNode );
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
        var columnContainerNode = dojo.byId( jetspeed.id.COLUMNS );
        if ( columnContainerNode )
            dojo.dom.removeNode( columnContainerNode );
        this.columns = [];
    },

    getPortletCurrentColumnRow: function( /* DOM node */ justForPortletWindowNode, /* boolean */ includeGhosts, /* map */ currentColumnRowAllPortlets )
    {
        if ( ! this.columns || this.columns.length == 0 ) return null;
        var result = null;
        var includeLayouts = ( ( justForPortletWindowNode != null ) ? true : false );
        var clonedLayoutCompletedRowCount = 0;
        var currentLayout = null;
        var currentLayoutId = null;
        var currentLayoutRowCount = 0;
        var currentLayoutIsRegular = false;
        for ( var colIndex = 0 ; colIndex < this.columns.length ; colIndex++ )
        {
            var colObj = this.columns[colIndex];
            var colChildNodes = colObj.domNode.childNodes;
            if ( currentLayoutId == null || currentLayoutId != colObj.getLayoutId() )
            {
                //if ( currentLayoutId != null && ! currentLayoutIsRegular )
                //{
                //    clonedLayoutCompletedRowCount = clonedLayoutCompletedRowCount + currentLayoutRowCount;
                //}
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
                    //clonedLayoutCompletedRowCount = clonedLayoutCompletedRowCount + 1;
                    // BOZO: should it ^^ be 0 if no portlets are contained in layout
                }
                else
                {
                    var parentColObj = this.getColumnFromColumnNode( colObj.domNode.parentNode );
                    if ( parentColObj == null )
                    {
                        dojo.raise( "getPortletCurrentColumnRow cannot locate parent column for column: " + colObj ) ;
                        return null;
                    }
                    colObj = parentColObj;
                }
            }

            var colCurrentRow = null;
            for ( var colChildIndex = 0 ; colChildIndex < colChildNodes.length ; colChildIndex++ )
            {
                var colChild = colChildNodes[colChildIndex];

                if ( dojo.html.hasClass( colChild, jetspeed.id.PORTLET_WINDOW_STYLE_CLASS ) || ( includeGhosts && dojo.html.hasClass( colChild, jetspeed.id.PORTLET_WINDOW_GHOST_STYLE_CLASS ) ) || ( includeLayouts && dojo.html.hasClass( colChild, "desktopColumn" ) ) )
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
                            //portletResult.row = ( clonedLayoutCompletedRowCount + colCurrentRow );
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

    getPortletDecorationDefault: function()
    {
        var pd = null;
        if ( djConfig.isDebug && jetspeed.debug.windowDecorationRandom )
        {
            pd = jetspeed.prefs.portletDecorationsAllowed[ Math.floor( Math.random() * jetspeed.prefs.portletDecorationsAllowed.length ) ];
        }
        else
        {
            var defaultpd = this.getPortletDecorator();
            if ( dojo.lang.indexOf( jetspeed.prefs.portletDecorationsAllowed, defaultpd ) != -1 )
                pd = defaultpd;
            else
                pd = jetspeed.prefs.windowDecoration;
        }
        return pd;
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

    debugLayoutInfo: function()
    {
        var dumpMsg = "";
        var i = 0;
        for ( var layoutId in this.layouts )
        {
            if ( i > 0 ) dumpMsg += "\r\n";
            dumpMsg += "layout[" + layoutId + "]: " + jetspeed.printobj( this.layouts[ layoutId ], true, true, true );
            i++;
        }
        return dumpMsg;
    },
    debugColumnInfo: function()
    {
        var dumpMsg = "";
        for ( var i = 0; i < this.columns.length; i++ )
        {
            if ( i > 0 ) dumpMsg += "\r\n";
            dumpMsg += this.columns[i].toString();
        }
        return dumpMsg;
    },
    debugDumpLastSavedWindowState: function()
    {
        return this._debugDumpLastSavedWindowStateAllPortlets( true );
    },
    debugDumpWindowState: function()
    {
        return this._debugDumpLastSavedWindowStateAllPortlets( false );
    },
    debugPortletActions: function()
    {
        var portletArray = this.getPortletArray();
        var dumpMsg = "";
        for ( var i = 0; i < portletArray.length; i++ )
        {
            var portlet = portletArray[i];
            if ( i > 0 ) dumpMsg += "\r\n";
            dumpMsg += "portlet [" + portlet.name + "] actions: {";
            for ( var actionKey in portlet.actions )
                dumpMsg += actionKey + "={" + jetspeed.printobj( portlet.actions[actionKey], true ) + "} ";
            dumpMsg += "}";
        }
        return dumpMsg;
    },
    _debugDumpLastSavedWindowStateAllPortlets: function( useLastSaved )
    {
        var portletArray = this.getPortletArray();
        var dumpMsg = "";
        for ( var i = 0; i < portletArray.length; i++ )
        {
            var portlet = portletArray[i];
            if ( i > 0 ) dumpMsg += "\r\n";
            var windowState = null;
            try
            {
                if ( useLastSaved )
                    windowState = portlet.getLastSavedWindowState();
                else
                    windowState = portlet.getCurrentWindowState();
            }
            catch (e) { }
            dumpMsg += "[" + portlet.name + "] " + ( (windowState == null) ? "null" : jetspeed.printobj( windowState, true ) );
        }
        return dumpMsg;
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
    destroy: function()
    {
        this._destroyPortlets();
        this._removeColumns( document.getElementById( jetspeed.id.DESKTOP ) );
        this._destroyPageControls();
    },

    // ... columns
    getColumnFromColumnNode: function( /* DOM node */ columnNode )
    {
        if ( columnNode == null ) return null;
        var pageColumnIndexAttr = columnNode.getAttribute( "columnIndex" );
        if ( pageColumnIndexAttr == null ) return null;
        var pageColumnIndex = new Number( pageColumnIndexAttr );
        if ( pageColumnIndex >= 0 && pageColumnIndex < this.columns.length )
            return this.columns[ pageColumnIndex ];
        return null;
    },
    getColumnIndexContainingNode: function( /* DOM node */ node )
    {
        var inColIndex = null;
        if ( ! this.columns ) return inColIndex;
        for ( var i = 0 ; i < this.columns.length ; i++ )
        {
            if ( this.columns[i].containsNode( node ) )
            {
                inColIndex = i;
                break;
            }
        }
        return inColIndex;
    },
    getColumnContainingNode: function( /* DOM node */ node )
    {
        var inColIndex = this.getColumnIndexContainingNode( node );
        return ( (inColIndex != null && inColIndex >= 0) ? this.columns[inColIndex] : null );
    },
    getDescendantColumns: function( /* jetspeed.om.Column */ column )
    {
        var dMap = {};
        if ( column == null ) return dMap;
        for ( var i = 0 ; i < this.columns.length ; i++ )
        {
            var col = this.columns[i];
            if ( col != column && column.containsDescendantNode( col.domNode ) )
                dMap[ i ] = col;
        }
        return dMap;
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
        if ( menuName != null )
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
        this.retrieveMenuDeclarations( true );
    },
    retrieveMenuDeclarations: function( includeMenuDefs )
    {
        contentListener = new jetspeed.om.MenusAjaxApiContentListener( includeMenuDefs );

        this.clearMenus();

        var queryString = "?action=getmenus";
        if ( includeMenuDefs )
            queryString += "&includeMenuDefs=true";

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

    // ... page buttons
    syncPageControls: function()
    {
        if ( this.actionButtons == null ) return;
        for ( var actionName in this.actionButtons )
        {
            var enabled = false;
            if ( actionName == jetspeed.id.ACTION_NAME_EDIT )
            {
                if ( ! this.editMode )
                    enabled = true;
            }
            else if ( actionName == jetspeed.id.ACTION_NAME_VIEW )
            {
                if ( this.editMode )
                    enabled = true;
            }
            else if ( actionName == jetspeed.id.ACTION_NAME_ADDPORTLET )
            {
                if ( ! this.editMode )
                    enabled = true;
            }
            else
            {
                enabled = true;
            }
            if ( enabled )
                this.actionButtons[ actionName ].style.display = "";
            else
                this.actionButtons[ actionName ].style.display = "none";
        }
    },
    renderPageControls: function()
    {
        var actionButtonNames = [];
        if ( this.actions != null )
        {
            for ( var actionName in this.actions )
            {
                if ( actionName != jetspeed.id.ACTION_NAME_HELP )
                {   // ^^^ page help is currently not supported
                    actionButtonNames.push( actionName );
                }
                if ( actionName == jetspeed.id.ACTION_NAME_EDIT )
                {
                    actionButtonNames.push( jetspeed.id.ACTION_NAME_ADDPORTLET );
                }
            }
            if ( this.actions[ jetspeed.id.ACTION_NAME_EDIT ] != null )
            {
                if ( this.actions[ jetspeed.id.ACTION_NAME_VIEW ] == null )
                {
                    actionButtonNames.push( jetspeed.id.ACTION_NAME_VIEW );
                }
            }
            if ( this.actions[ jetspeed.id.ACTION_NAME_VIEW ] != null )
            {
                if ( this.actions[ jetspeed.id.ACTION_NAME_EDIT ] == null )
                {
                    actionButtonNames.push( jetspeed.id.ACTION_NAME_EDIT );
                }
            }
        }

        var pageControlsContainer = dojo.byId( jetspeed.id.PAGE_CONTROLS );
        if ( pageControlsContainer != null && actionButtonNames != null && actionButtonNames.length > 0 )
        {
            if ( this.actionButtons == null )
            {
                this.actionButtons = {};
                this.actionButtonTooltips = [];
            }
            
            for ( var i = 0 ; i < actionButtonNames.length ; i++ )
            {
                var actionName = actionButtonNames[ i ];
                var actionButton = document.createElement( "div" );
                actionButton.className = "portalPageActionButton";
                actionButton.style.backgroundImage = "url(" + jetspeed.prefs.getLayoutRootUrl() + "/images/desktop/" + actionName + ".gif)";
                actionButton.actionName = actionName;
                this.actionButtons[ actionName ] = actionButton;
                pageControlsContainer.appendChild( actionButton );
    
                dojo.event.connect( actionButton, "onclick", this, "pageActionButtonClick" );

                if ( jetspeed.prefs.pageActionButtonTooltip )
                {   // setting isContainer=false and fastMixIn=true to avoid recursion hell when connectId is a node (could give each an id instead)
                    var actionlabel = null;
                    if ( jetspeed.prefs.desktopActionLabels != null )
                        actionlabel = jetspeed.prefs.desktopActionLabels[ actionName ];
                    if ( actionlabel == null || actionlabel.length == 0 )
                        actionlabel = dojo.string.capitalize( actionName );
                    var tooltip = dojo.widget.createWidget( "Tooltip", { isContainer: false, fastMixIn: true, caption: actionlabel, connectId: actionButton, delay: "100" } );
                    this.actionButtonTooltips.push( tooltip );
                    document.body.appendChild( tooltip.domNode );
                }
            }
        }
    },
    _destroyPageControls: function()
    {
        var pageControlsContainer = dojo.byId( jetspeed.id.PAGE_CONTROLS );
        if ( pageControlsContainer != null && pageControlsContainer.childNodes && pageControlsContainer.childNodes.length > 0 )
        {
            for ( var i = (pageControlsContainer.childNodes.length -1) ; i >= 0 ; i-- )
            {
                dojo.dom.removeNode( pageControlsContainer.childNodes[i] );
            }
        }
        if ( this.actionButtonTooltips && this.actionButtonTooltips.length > 0 )
        {
            for ( var i = (this.actionButtonTooltips.length -1); i >= 0 ; i-- )
            {
                this.actionButtonTooltips[i].destroy();
                this.actionButtonTooltips[i] = null;
            }
            this.actionButtonTooltips = [];
        }
        this.actionButtons == null;
    },
    pageActionButtonClick: function( evt )
    {
        if ( evt == null || evt.target == null ) return;
        this.pageActionProcess( evt.target.actionName, evt );
    },
    pageActionProcess: function( /* String */ actionName )
    {
        if ( actionName == null ) return;
        if ( actionName == jetspeed.id.ACTION_NAME_ADDPORTLET )
        {
            this.addPortletInitiate();
        }
        else if ( actionName == jetspeed.id.ACTION_NAME_EDIT )
        {
            jetspeed.editPageInitiate();
        }
        else if ( actionName == jetspeed.id.ACTION_NAME_VIEW )
        {
            jetspeed.editPageTerminate();
        }
        else
        {
            var action = this.getPageAction( actionName );
            alert( "pageAction " + actionName + " : " + action );
            if ( action == null ) return;
            if ( action.url == null ) return;
            var pageActionUrl = jetspeed.url.basePortalUrl() + jetspeed.url.path.DESKTOP + "/" + action.url;
            jetspeed.pageNavigate( pageActionUrl );
        }
    },
    getPageAction: function( name )
    {
        if ( this.actions == null ) return null;
        return this.actions[ name ];
    },

    // ... add portlet
    addPortletInitiate: function( /* String */ layoutId, /* String */ jspage )
    {
        if ( ! jspage )
            jspage = escape( this.getPagePathAndQuery() );
        else
            jspage = escape( jspage );
        var addportletPageUrl = jetspeed.url.basePortalUrl() + jetspeed.url.path.DESKTOP + "/system/customizer/selector.psml?jspage=" + jspage;
        if ( layoutId != null )
            addportletPageUrl += "&jslayoutid=" + escape( layoutId );
        jetspeed.changeActionForPortlet( this.rootFragmentId, null, jetspeed.id.ACTION_NAME_EDIT, new jetspeed.om.PageChangeActionContentListener( addportletPageUrl ) );
    },

    // ... edit mode
    setPageModePortletActions: function( /* Portlet */ portlet )
    {
        if ( portlet == null || portlet.actions == null ) return;
        if ( portlet.actions[ jetspeed.id.ACTION_NAME_REMOVEPORTLET ] == null )
        {
            portlet.actions[ jetspeed.id.ACTION_NAME_REMOVEPORTLET ] = { id: jetspeed.id.ACTION_NAME_REMOVEPORTLET };
        }
    },

    // ... page url access

    getPageUrl: function( forPortal )
    {
        if ( this.pageUrl != null && ! forPortal )
            return this.pageUrl;
        var pageUrl = jetspeed.url.path.SERVER + ( ( forPortal ) ? jetspeed.url.path.PORTAL : jetspeed.url.path.DESKTOP ) + this.getPath();
        var pageUrlObj = jetspeed.url.parse( pageUrl );
        var docUrlObj = null;
        if ( this.pageUrlFallback != null )
            docUrlObj = jetspeed.url.parse( this.pageUrlFallback );
        else
            docUrlObj = jetspeed.url.parse( document.location.href );
        if ( pageUrlObj != null && docUrlObj != null )
        {
            var docUrlQuery = docUrlObj.query;
            if ( docUrlQuery != null && docUrlQuery.length > 0 )
            {
                var pageUrlQuery = pageUrlObj.query;
                if ( pageUrlQuery != null && pageUrlQuery.length > 0 )
                {
                    pageUrl = pageUrl + "&" + docUrlQuery;
                }
                else
                {
                    pageUrl = pageUrl + "?" + docUrlQuery;
                }
            }
        }
        if ( ! forPortal )
            this.pageUrl = pageUrl;
        return pageUrl;
    },
    getPagePathAndQuery: function()
    {
        if ( this.pagePathAndQuery != null )
            return this.pagePathAndQuery;
        var pagePath = this.getPath();
        var pagePathObj = jetspeed.url.parse( pagePath );
        var docUrlObj = null;
        if ( this.pageUrlFallback != null )
            docUrlObj = jetspeed.url.parse( this.pageUrlFallback );
        else
            docUrlObj = jetspeed.url.parse( document.location.href );
        if ( pagePathObj != null && docUrlObj != null )
        {
            var docUrlQuery = docUrlObj.query;
            if ( docUrlQuery != null && docUrlQuery.length > 0 )
            {
                var pageUrlQuery = pagePathObj.query;
                if ( pageUrlQuery != null && pageUrlQuery.length > 0 )
                {
                    pagePath = pagePath + "&" + docUrlQuery;
                }
                else
                {
                    pagePath = pagePath + "?" + docUrlQuery;
                }
            }
        }
        this.pagePathAndQuery = pagePath;
        return pagePath;
    },
    getPageDirectory: function( useRealPath )
    {
        var pageDir = "/";
        var pagePath = ( useRealPath ? this.getRealPath() : this.getPath() );
        if ( pagePath != null )
        {
            var lastSep = pagePath.lastIndexOf( "/" );
            if ( lastSep != -1 )
            {
                if ( (lastSep +1) < pagePath.length )
                    pageDir = pagePath.substring( 0, lastSep +1 );
                else
                    pageDir = pagePath;
            }
        }
        return pageDir;
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
        return this.profiledPath;
    },
    getRealPath: function()
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
jetspeed.om.Column = function( layoutColumnIndex, layoutId, size, pageColumnIndex, layoutActionsDisabled )
{
    this.layoutColumnIndex = layoutColumnIndex;
    this.layoutId = layoutId;
    this.size = size;
    this.pageColumnIndex = new Number( pageColumnIndex );
    if ( typeof layoutActionsDisabled != "undefined" )
        this.layoutActionsDisabled = layoutActionsDisabled ;
    this.id = "jscol_" + pageColumnIndex; //  + "_" + this.layoutColumnIndex + "_" + this.layoutId;
    this.domNode = null;
};
dojo.inherits( jetspeed.om.Column, jetspeed.om.Id );
dojo.lang.extend( jetspeed.om.Column,
{
    layoutColumnIndex: null,
    layoutId: null,
    size: null,
    pageColumnIndex: null,
    layoutActionsDisabled: false,
    domNode: null,

    columnContainer: false,
    layoutHeader: false,

    createColumn: function( columnContainerNode )
    {
        var columnClass = "desktopColumn" ;
        if ( this.isStartOfColumnSet() && this.getPageColumnIndex() > 0 )
            columnClass = "desktopColumn desktopColumnClear";
        var divElmt = document.createElement( "div" );
        divElmt.setAttribute( "columnIndex", this.getPageColumnIndex() );
        divElmt.style.width = this.size + "%";
        if ( this.layoutHeader )
        {
            columnClass = "desktopColumn desktopLayoutHeader";
        }
        else
        {
            divElmt.style.minHeight = "40px";
        }
        divElmt.className = columnClass;
        divElmt.id = this.getId();
        this.domNode = divElmt;
        if ( columnContainerNode != null )
            columnContainerNode.appendChild( divElmt );
    },
    containsNode: function( node )
    {
        return ( ( this.domNode != null && node != null && this.domNode == node.parentNode ) ? true : false );
    },
    containsDescendantNode: function( node )
    {
        return ( ( this.domNode != null && node != null && dojo.dom.isDescendantOf( node, this.domNode, true ) ) ? true : false );
    },
    getDescendantColumns: function()
    {
        return jetspeed.page.getDescendantColumns( this );
    },
    isStartOfColumnSet: function()
    {
        return this.layoutColumnIndex == 0;
    },
    toString: function()
    {
        var out = "column[" + this.pageColumnIndex + "]";
        out += " layoutCol=" + this.layoutColumnIndex + " layoutId=" + this.layoutId + " size=" + this.size + ( this.columnChildren == null ? "" : ( " column-child-count=" + this.columnChildren.length ) ) + ( this.columnContainer ? " colContainer=true" : "" ) + ( this.layoutHeader ? " layoutHeader=true" : "" );
        if ( this.domNode != null )
        {
            var colAbsPos = dojo.html.getAbsolutePosition( this.domNode, true );
            var marginBox = dojo.html.getMarginBox( this.domNode );
            out += " dims={" + "left:" + (colAbsPos.x) + ", right:" + (colAbsPos.x + marginBox.width) + ", top:" + (colAbsPos.y) + ", bottom:" + (colAbsPos.y + marginBox.height) + "}";
        }
        return out;
    },
    getId: function()  // jetspeed.om.Id protocol
    {
        return this.id; // this.layoutId + "_" + this.layoutColumnIndex;
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
jetspeed.om.Portlet = function( portletName, portletEntityId, alternateContentRetriever, properties, actions, currentActionState, currentActionMode, decorator, layoutActionsDisabled )
{   // new jetspeed.om.Portlet( pFrag.name, pFrag.id, alternateContentRetriever, pFrag.properties, pFrag.decorator, portletPageColumnIndex ) ;
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

    jetspeed.page.setPageModePortletActions( this );

    this.currentActionState = currentActionState;
    this.currentActionMode = currentActionMode;

    if ( alternateContentRetriever )
        this.contentRetriever = alternateContentRetriever;

    if ( decorator != null && decorator.length > 0 )
    {
        if ( dojo.lang.indexOf( jetspeed.prefs.portletDecorationsAllowed, decorator ) != -1 )
        {
            this.putProperty( jetspeed.id.PORTLET_PROP_WINDOW_DECORATION, decorator );
        }
    }

    this.layoutActionsDisabled = false ;
    if ( typeof layoutActionsDisabled != "undefined" )
        this.layoutActionsDisabled = layoutActionsDisabled ;
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

    postParseAnnotateHtml: function( /* DOMNode */ containerNode )
    {   
        if ( containerNode )
        {
            var cNode = containerNode;
            var formList = cNode.getElementsByTagName( "form" );
            var debugOn = jetspeed.debug.postParseAnnotateHtml;
            var disableAnchorConversion = jetspeed.debug.postParseAnnotateHtmlDisableAnchors;
            if ( formList )
            {
                for ( var i = 0 ; i < formList.length ; i++ )
                {
                    var cForm = formList[i];                    
                    var cFormAction = cForm.action;

                    var parsedPseudoUrl = jetspeed.portleturl.parseContentUrlForDesktopActionRender( cFormAction );
                    
                    var submitOperation = parsedPseudoUrl.operation;

                    if ( submitOperation == jetspeed.portleturl.PORTLET_REQUEST_ACTION || submitOperation == jetspeed.portleturl.PORTLET_REQUEST_RENDER )
                    {
                        //var replacementActionUrl = parsedPseudoUrl.url; 
                        var replacementActionUrl = jetspeed.portleturl.generateJSPseudoUrlActionRender( parsedPseudoUrl, true );
                        cForm.action = replacementActionUrl;

                        var formBind = new jetspeed.om.ActionRenderFormBind( cForm, parsedPseudoUrl.url, parsedPseudoUrl.portletEntityId, submitOperation );
                        //  ^^^ formBind serves as an event hook up - retained ref is not needed
                        
                        if ( debugOn )
                            dojo.debug( "postParseAnnotateHtml [" + this.entityId + "] adding FormBind (" + submitOperation + ") for form with action: " + cFormAction );
                    }
                    else if ( cFormAction == null || cFormAction.length == 0 )
                    {
                        var formBind = new jetspeed.om.ActionRenderFormBind( cForm, null, this.entityId, null );
                        //  ^^^ formBind serves as an event hook up - retained ref is not needed
                        
                        if ( debugOn )
                            dojo.debug( "postParseAnnotateHtml [" + this.entityId + "] form action attribute is empty - adding FormBind with expectation that form action will be set via script" ) ;
                    }
                    else
                    {
                        if ( debugOn )
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
                    
                    var parsedPseudoUrl = jetspeed.portleturl.parseContentUrlForDesktopActionRender( aHref );
                    var replacementHref = null;
                    if ( ! disableAnchorConversion )
                        replacementHref = jetspeed.portleturl.generateJSPseudoUrlActionRender( parsedPseudoUrl );

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
        var currentState = windowWidget.getCurrentWindowStateForPersistence( volatileOnly );
        if ( ! volatileOnly )
        {
            if ( currentState.layout == null )   // should happen only if windowPositionStatic == false
                currentState.layout = this.lastSavedWindowState.layout;
        }
        return currentState;
    },
    getLastSavedWindowState: function()
    {
        if ( ! this.lastSavedWindowState )
            dojo.raise( "portlet.getLastSavedWindowState() is null - portlet (" + this.name + ") not properly initialized." );
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
        var modUrl = null;
        if ( bindArgs && bindArgs.url )
        {
            modUrl = bindArgs.url;
        }
        else if ( bindArgs && bindArgs.formNode )
        {
            var formAction = bindArgs.formNode.getAttribute( "action" );
            if ( formAction )
                modUrl = formAction;
        }
        if ( modUrl == null )
            modUrl = jetspeed.url.basePortalUrl() + jetspeed.url.path.PORTLET + jetspeed.page.getPath();

        if ( ! bindArgs.dontAddQueryArgs )
        {
            modUrl = jetspeed.url.parse( modUrl );
            modUrl = jetspeed.url.addQueryParameter( modUrl, "entity", this.entityId, true );
            modUrl = jetspeed.url.addQueryParameter( modUrl, "portlet", this.name, true );
            modUrl = jetspeed.url.addQueryParameter( modUrl, "encoder", "desktop", true );
            if ( bindArgs.jsPageUrl != null )
            {
                var jsPageUrlQuery = bindArgs.jsPageUrl.query;
                if ( jsPageUrlQuery != null && jsPageUrlQuery.length > 0 )
                {
                    modUrl = modUrl.toString() + "&" + jsPageUrlQuery;
                }
            }
        }
        
        if ( bindArgs )
            bindArgs.url = modUrl.toString();
        return modUrl;
    },

    _submitJetspeedAjaxApi: function( /* String */ action, /* String */ queryStringFragment, contentListener )
    {
        var queryString = "?action=" + action + "&id=" + this.entityId + queryStringFragment;

        var psmlMoveActionUrl = jetspeed.url.basePortalUrl() + jetspeed.url.path.AJAX_API + jetspeed.page.getPath() + queryString;
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

            this._submitJetspeedAjaxApi( action, queryStringFragment, new jetspeed.om.MoveAjaxApiContentListener( this, changedState ) );

            if ( ! volatileOnly && ! reset )
            {
                if ( ! windowPositionStatic && changedStateResult.zIndexChanged )  // current condition for whether 
                {                                                                  // volatile (zIndex) changes are possible
                    var portletArrayList = jetspeed.page.getPortletArrayList();
                    var autoUpdatePortlets = dojo.collections.Set.difference( portletArrayList, [ this ] );
                    if ( ! portletArrayList || ! autoUpdatePortlets || ((autoUpdatePortlets.count + 1) != portletArrayList.count) )
                        dojo.raise( "Portlet.submitChangedWindowState invalid conditions for starting auto update" );
                    else if ( autoUpdatePortlets && autoUpdatePortlets.count > 0 )
                    {
                        dojo.lang.forEach( autoUpdatePortlets.toArray(),
                                function( portlet ) { if ( ! portlet.getProperty( jetspeed.id.PORTLET_PROP_WINDOW_POSITION_STATIC ) ) portlet.submitChangedWindowState( true ); } );
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
            contentListener = new jetspeed.om.PortletContentListener( this, suppressGetActions, bindArgs );

        if ( ! bindArgs )
            bindArgs = {};
        
        var portlet = this ;
        portlet.getPortletUrl( bindArgs ) ;
        
        this.contentRetriever.getContent( bindArgs, contentListener, portlet, jetspeed.debugContentDumpIds );
    },
    setPortletContent: function( portletContent, renderUrl, portletTitle )
    {
        var windowWidget = this.getPortletWindow();
        if ( portletTitle != null && portletTitle.length > 0 )
        {
            this.putProperty( jetspeed.id.PORTLET_PROP_WINDOW_TITLE, portletTitle );
            if ( windowWidget && ! this.loadingIndicatorIsShown() )
                windowWidget.setPortletTitle( portletTitle );
        }
        if ( windowWidget )
        {
            windowWidget.setPortletContent( portletContent, renderUrl );
        }
    },
    loadingIndicatorIsShown: function()
    {
        var actionlabel1 = this._getLoadingActionLabel( jetspeed.id.ACTION_NAME_LOAD_RENDER );
        var actionlabel2 = this._getLoadingActionLabel( jetspeed.id.ACTION_NAME_LOAD_ACTION );
        var actionlabel3 = this._getLoadingActionLabel( jetspeed.id.ACTION_NAME_LOAD_UPDATE );
        var windowWidget = this.getPortletWindow();
        if ( windowWidget && ( actionlabel1 || actionlabel2 ) )
        {
            var windowTitle = windowWidget.getPortletTitle();
            if ( windowTitle && ( windowTitle == actionlabel1 || windowTitle == actionlabel2 ) )
                return true;
        }
        return false;
    },
    _getLoadingActionLabel: function( actionName )
    {
        var actionlabel = null;
        if ( jetspeed.prefs != null && jetspeed.prefs.desktopActionLabels != null )
        {
            actionlabel = jetspeed.prefs.desktopActionLabels[ actionName ];
            if ( actionlabel != null && actionlabel.length == 0 )
                actionlabel = null;
        }
        return actionlabel;
    },  
    loadingIndicatorShow: function( actionName )
    {
        if ( actionName && ! this.loadingIndicatorIsShown() )
        {
            var actionlabel = this._getLoadingActionLabel( actionName );
            var windowWidget = this.getPortletWindow();
            if ( windowWidget && actionlabel )
            {
                windowWidget.setPortletTitle( actionlabel );
            }
        }
    },
    loadingIndicatorHide: function()
    {
        var windowWidget = this.getPortletWindow();
        if ( windowWidget )
        {
            windowWidget.setPortletTitle( this.getProperty( jetspeed.id.PORTLET_PROP_WINDOW_TITLE ) );
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

    renderAction: function( actionName, actionUrlOverride )
    {
        var action = null;
        if ( actionName != null )
            action = this.getAction( actionName );
        var actionUrl = actionUrlOverride;
        if ( actionUrl == null && action != null )
            actionUrl = action.url;
        if ( actionUrl == null ) return;
        var renderActionUrl = jetspeed.url.basePortalUrl() + jetspeed.url.path.PORTLET + "/" + actionUrl + jetspeed.page.getPath();
        if ( actionName != jetspeed.id.ACTION_NAME_PRINT )
            this.retrieveContent( null, { url: renderActionUrl } );
        else
        {
            var printmodeUrl = jetspeed.page.getPageUrl();
            printmodeUrl = jetspeed.url.addQueryParameter( printmodeUrl, "jsprintmode", "true" );
            printmodeUrl = jetspeed.url.addQueryParameter( printmodeUrl, "jsaction", escape( action.url ) );
            printmodeUrl = jetspeed.url.addQueryParameter( printmodeUrl, "jsentity", this.entityId );
            printmodeUrl = jetspeed.url.addQueryParameter( printmodeUrl, "jslayoutid", this.lastSavedWindowState.layout );
            window.open( printmodeUrl.toString(), "jsportlet_print", "status,scrollbars,resizable,menubar" );
        }
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

        this.syncActions();
    },
    syncActions: function()
    {
        jetspeed.page.setPageModePortletActions( this );
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
    this.formSubmitInProgress = false;
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

        form.oldSubmit = form.submit;  // Isn't really used anymore, but cache it
        form.submit = function()
        {
            form.onsubmit();
        };
    },

    onSubmit: function( cForm )
    {
        var proceed = true;
        if ( this.isFormSubmitInProgress() )
            proceed = false;
        else if ( jetspeed.debug.confirmOnSubmit )
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
        if ( this.isFormSubmitInProgress() )
        {
            // do nothing
        }
		else if( this.onSubmit( this.form ) )
        {
            var parsedPseudoUrl = jetspeed.portleturl.parseContentUrlForDesktopActionRender( this.form.action );

            var mixInBindArgs = {};
            if ( parsedPseudoUrl.operation == jetspeed.portleturl.PORTLET_REQUEST_ACTION || parsedPseudoUrl.operation == jetspeed.portleturl.PORTLET_REQUEST_RENDER )
            {   // form action set via script
                var replacementActionUrl = jetspeed.portleturl.generateJSPseudoUrlActionRender( parsedPseudoUrl, true );
                this.form.action = replacementActionUrl;
                this.submitOperation = parsedPseudoUrl.operation;
                this.entityId = parsedPseudoUrl.portletEntityId;
                mixInBindArgs.url = parsedPseudoUrl.url;
            }

            if ( this.submitOperation == jetspeed.portleturl.PORTLET_REQUEST_RENDER || this.submitOperation == jetspeed.portleturl.PORTLET_REQUEST_ACTION )
            {
                this.isFormSubmitInProgress( true );
                mixInBindArgs.formFilter = dojo.lang.hitch( this, "formFilter" );
                mixInBindArgs.submitFormBindObject = this;
                if ( this.submitOperation == jetspeed.portleturl.PORTLET_REQUEST_RENDER )
                {
                    jetspeed.doRender( dojo.lang.mixin(this.bindArgs, mixInBindArgs ), this.entityId );
                }
                else
                {
                    jetspeed.doAction( dojo.lang.mixin(this.bindArgs, mixInBindArgs ), this.entityId );
                }
            }
            else
            {
                //var errMsg = "ActionRenderFormBind.submit cannot process form submit with action:" + this.form.action;
                //alert( errMsg );
            }
		}
	},
    isFormSubmitInProgress: function( setVal )
    {
        if ( setVal != undefined )
        {
            this.formSubmitInProgress = setVal;
        }
        return this.formSubmitInProgress;
    }
});

// ... jetspeed.om.FolderDef
jetspeed.om.FolderDef = function( /* String */ folderName, /* String */ folderPath)
{
    this.folderName = folderName;
    this.folderPath = folderPath;
};
dojo.inherits( jetspeed.om.FolderDef, jetspeed.om.Id);
dojo.lang.extend( jetspeed.om.FolderDef,
{
    folderName: null,
    folderPath: null,
    getName: function()  // jetspeed.om.Id protocol
    {
        return this.folderName;
    },
    getPath: function()
    {
        return this.folderPath;
    }
});
// ... jetspeed.om.PortletDef
jetspeed.om.PortletDef = function( /* String */ portletName, /* String */ portletDisplayName, /* String */ portletDescription, /* String */ portletImage,portletCount)
{
    this.portletName = portletName;
    this.portletDisplayName = portletDisplayName;
    this.portletDescription = portletDescription;
    this.image = portletImage;
	this.count = portletCount;
};
dojo.inherits( jetspeed.om.PortletDef, jetspeed.om.Id);
dojo.lang.extend( jetspeed.om.PortletDef,
{
    portletName: null,
    portletDisplayName: null,
    portletDescription: null,
    portletImage: null,
	portletCount: null,
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
	getPortletCount: function()
    {
        return this.portletCount;
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
        dojo.raise( "BasicContentListener notifyFailure url: " + requestUrl + " type: " + type + jetspeed.url.formatBindError( error ) );
    }
};

// ... jetspeed.om.PortletContentListener
jetspeed.om.PortletContentListener = function( /* Portlet */ portlet, suppressGetActions, bindArgs )
{
    this.portlet = portlet;
    this.suppressGetActions = suppressGetActions;
    this.submittedFormBindObject = null;
    if ( bindArgs != null && bindArgs.submitFormBindObject != null )
    {
        this.submittedFormBindObject = bindArgs.submitFormBindObject;
    }
    this._setPortletLoading( true );
};
jetspeed.om.PortletContentListener.prototype =
{
    _setPortletLoading: function( /* boolean */ showLoading )
    {
        if ( this.portlet == null ) return;
        if ( showLoading )
            this.portlet.loadingIndicatorShow( jetspeed.id.ACTION_NAME_LOAD_RENDER );
        else
            this.portlet.loadingIndicatorHide();
    },
    notifySuccess: function( /* String */ portletContent, /* String */ requestUrl, /* Portlet */ portlet, http )
    {
        var portletTitle = null;
        if ( http != null )
        {
            portletTitle = http.getResponseHeader("JS_PORTLET_TITLE");
            if ( portletTitle != null )
                portletTitle = unescape( portletTitle );
        }
        portlet.setPortletContent( portletContent, requestUrl, portletTitle );
        if ( this.suppressGetActions == null || this.suppressGetActions == false )
            jetspeed.getActionsForPortlet( portlet.getId() );
        else
            this._setPortletLoading( false );
        if ( this.submittedFormBindObject != null )
        {
            this.submittedFormBindObject.isFormSubmitInProgress( false );
        }
    },
    notifyFailure: function( /* String */ type, /* Object */ error, /* String */ requestUrl, /* Portlet */ portlet )
    {
        this._setPortletLoading( false );
        if ( this.submittedFormBindObject != null )
        {
            this.submittedFormBindObject.isFormSubmitInProgress( false );
        }
        dojo.raise( "PortletContentListener notifyFailure url: " + requestUrl + " type: " + type + jetspeed.url.formatBindError( error ) );
    }
};

// ... jetspeed.om.PortletActionContentListener
jetspeed.om.PortletActionContentListener = function( /* Portlet */ portlet, bindArgs )
{
    this.portlet = portlet;
    this.submittedFormBindObject = null;
    if ( bindArgs != null && bindArgs.submitFormBindObject != null )
    {
        this.submittedFormBindObject = bindArgs.submitFormBindObject;
    }
    this._setPortletLoading( true );
};
jetspeed.om.PortletActionContentListener.prototype =
{
    _setPortletLoading: function( /* boolean */ showLoading )
    {
        if ( this.portlet == null ) return;
        if ( showLoading )
            this.portlet.loadingIndicatorShow( jetspeed.id.ACTION_NAME_LOAD_ACTION );
        else
            this.portlet.loadingIndicatorHide();
    },
    notifySuccess: function( /* String */ portletContent, /* String */ requestUrl, /* Portlet */ portlet, http )
    {
        var renderUrl = null;
        var navigatedPage = false;
        var parsedPseudoUrl = jetspeed.portleturl.parseContentUrlForDesktopActionRender( portletContent );
        if ( parsedPseudoUrl.operation == jetspeed.portleturl.PORTLET_REQUEST_ACTION || parsedPseudoUrl.operation == jetspeed.portleturl.PORTLET_REQUEST_RENDER )
        {
            if ( jetspeed.debug.doRenderDoAction )
                dojo.debug( "PortletActionContentListener " + parsedPseudoUrl.operation + "-url in response body: " + portletContent + "  url: " + parsedPseudoUrl.url + " entity-id: " + parsedPseudoUrl.portletEntityId ) ;
            renderUrl = parsedPseudoUrl.url;
        }
        else
        {
            if ( jetspeed.debug.doRenderDoAction )
                dojo.debug( "PortletActionContentListener other-url in response body: " + portletContent )
            renderUrl = portletContent;
            if ( renderUrl )
            {
                var portletUrlPos = renderUrl.indexOf( jetspeed.url.basePortalUrl() + jetspeed.url.path.PORTLET );
                if ( portletUrlPos == -1 )
                {
                    //dojo.debug( "PortletActionContentListener window.location.href navigation=" + renderUrl );
                    navigatedPage = true;
                    window.location.href = renderUrl;
                    renderUrl = null;
                }
                else if ( portletUrlPos > 0 )
                {
                    this._setPortletLoading( false );
                    dojo.raise( "PortletActionContentListener cannot interpret portlet url in action response: " + portletContent );
                    renderUrl = null;
                }
            }
        }
        if ( renderUrl != null )
        {
            if ( jetspeed.debug.doRenderDoAction )
                dojo.debug( "PortletActionContentListener calling doRenderAll=" + renderUrl );
            jetspeed.doRenderAll( renderUrl );
        }
        else
        {
            this._setPortletLoading( false );
        }
        if ( ! navigatedPage && this.portlet )
            jetspeed.getActionsForPortlet( this.portlet.entityId );
        if ( this.submittedFormBindObject != null )
        {
            this.submittedFormBindObject.isFormSubmitInProgress( false );
        }
    },
    notifyFailure: function( /* String */ type, /* Object */ error, /* String */ requestUrl, /* Portlet */ portlet )
    {
        this._setPortletLoading( false );
        if ( this.submittedFormBindObject != null )
        {
            this.submittedFormBindObject.isFormSubmitInProgress( false );
        }
        dojo.raise( "PortletActionContentListener notifyFailure type: " + type + jetspeed.url.formatBindError( error ) );
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
                if ( ! jetspeed.prefs.ajaxPageNavigation )
                {
                    jetspeed.pageNavigate( navUrl, this.getTarget() );
                }
                else
                {
                    jetspeed.updatePage( navUrl );
                }
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

// ... jetspeed.om.MenuAjaxApiContentListener
jetspeed.om.MenuAjaxApiContentListener = function()
{
};
dojo.lang.extend( jetspeed.om.MenuAjaxApiContentListener,
{
    notifySuccess: function( /* XMLDocument */ data, /* String */ requestUrl, domainModelObject )
    {
        var menuObj = this.parseMenu( data, domainModelObject.menuName, domainModelObject.menuType );
        domainModelObject.page.putMenu( menuObj );
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

// ... jetspeed.om.MenusAjaxApiContentListener
jetspeed.om.MenusAjaxApiContentListener = function( /* boolean */ includeMenuDefs )
{
    this.includeMenuDefs = includeMenuDefs;
};
dojo.inherits( jetspeed.om.MenusAjaxApiContentListener, jetspeed.om.MenuAjaxApiContentListener);
dojo.lang.extend( jetspeed.om.MenusAjaxApiContentListener,
{
    notifySuccess: function( /* XMLDocument */ data, /* String */ requestUrl, domainModelObject )
    {
        var menuDefs = this.getMenuDefs( data, requestUrl, domainModelObject );
        for ( var i = 0 ; i < menuDefs.length; i++ )
        {
            var mObj = menuDefs[i];
            domainModelObject.page.putMenu( mObj );
        }
        this.notifyFinished( domainModelObject );
    },
    getMenuDefs: function( /* XMLDocument */ data, /* String */ requestUrl, domainModelObject )
    {
        var menuDefs = [];
        var menuDefElements = data.getElementsByTagName( "menu" );
        for( var i = 0; i < menuDefElements.length; i++ )
        {
            var menuType = menuDefElements[i].getAttribute( "type" );
            if ( this.includeMenuDefs )
                menuDefs.push( this.parseMenuObject( menuDefElements[i], new jetspeed.om.Menu( null, menuType ) ) );
            else
            {
                var menuName = menuDefElements[i].firstChild.nodeValue;
                menuDefs.push( new jetspeed.om.Menu( menuName, menuType ) );
            }
        }
        return menuDefs;
    },
    
    notifyFailure: function( /* String */ type, /* Object */ error, /* String */ requestUrl, domainModelObject )
    {
        dojo.raise( "MenusAjaxApiContentListener error [" + domainModelObject.toString() + "] url: " + requestUrl + " type: " + type + jetspeed.url.formatBindError( error ) );
    },

    notifyFinished: function( domainModelObject )
    {
        if ( this.includeMenuDefs )
            jetspeed.notifyRetrieveAllMenusFinished();
    }
});

// ... jetspeed.om.PortletChangeActionContentListener
jetspeed.om.PortletChangeActionContentListener = function( /* String */ portletEntityId )
{
    this.portletEntityId = portletEntityId;
    this._setPortletLoading( true );
};
dojo.lang.extend( jetspeed.om.PortletChangeActionContentListener,
{
    notifySuccess: function( /* XMLDocument */ data, /* String */ requestUrl, domainModelObject )
    {
        if ( jetspeed.url.checkAjaxApiResponse( requestUrl, data, true, "portlet-change-action" ) )
            jetspeed.getActionsForPortlet( this.portletEntityId );
        else
            this._setPortletLoading( false );
    },
    _setPortletLoading: function( /* boolean */ showLoading )
    {
        var portlet = jetspeed.page.getPortlet( this.portletEntityId ) ;
        if ( portlet )
        {
            if ( showLoading )
                portlet.loadingIndicatorShow( jetspeed.id.ACTION_NAME_LOAD_UPDATE );
            else
                portlet.loadingIndicatorHide();
        }
    },
    notifyFailure: function( /* String */ type, /* Object */ error, /* String */ requestUrl, domainModelObject )
    {
        this._setPortletLoading( false );
        dojo.raise( "PortletChangeActionContentListener error [" + domainModelObject.toString() + "] url: " + requestUrl + " type: " + type + jetspeed.url.formatBindError( error ) );
    }
});

// ... jetspeed.om.PageChangeActionContentListener
jetspeed.om.PageChangeActionContentListener = function( /* String */ pageActionUrl )
{
    this.pageActionUrl = pageActionUrl;
};
dojo.lang.extend( jetspeed.om.PageChangeActionContentListener,
{
    notifySuccess: function( /* XMLDocument */ data, /* String */ requestUrl, domainModelObject )
    {
        if ( jetspeed.url.checkAjaxApiResponse( requestUrl, data, true, "page-change-action" ) )
        {
            if ( this.pageActionUrl != null && this.pageActionUrl.length > 0 )
                jetspeed.pageNavigate( this.pageActionUrl ); 
        }
    },
    notifyFailure: function( /* String */ type, /* Object */ error, /* String */ requestUrl, domainModelObject )
    {
        dojo.raise( "PageChangeActionContentListener error [" + domainModelObject.toString() + "] url: " + requestUrl + " type: " + type + jetspeed.url.formatBindError( error ) );
    }
});

// ... jetspeed.om.PortletActionsContentListener
jetspeed.om.PortletActionsContentListener = function( /* String[] */ portletEntityIds )
{
    this.portletEntityIds = portletEntityIds;
    this._setPortletLoading( true );
};
dojo.lang.extend( jetspeed.om.PortletActionsContentListener,
{
    _setPortletLoading: function( /* boolean */ showLoading )
    {
        if ( this.portletEntityIds == null || this.portletEntityIds.length == 0 ) return ;
        for ( var i = 0 ; i < this.portletEntityIds.length ; i++ )
        {
            var portlet = jetspeed.page.getPortlet( this.portletEntityIds[i] ) ;
            if ( portlet )
            {
                if ( showLoading )
                    portlet.loadingIndicatorShow( jetspeed.id.ACTION_NAME_LOAD_UPDATE );
                else
                    portlet.loadingIndicatorHide();
            }
        }
    },
    notifySuccess: function( /* XMLDocument */ data, /* String */ requestUrl, domainModelObject )
    {
        this._setPortletLoading( false );
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
        this._setPortletLoading( false );
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
		var count=0;//portletImage = node.getAttribute( "image" );
        return new jetspeed.om.PortletDef( portletName, portletDisplayName, portletDescription, portletImage,count ) ;
    }
});

// ... jetspeed.om.FoldersListContentListener
jetspeed.om.FoldersListContentListener = function(finishedFunction)
{
    this.notifyFinished = finishedFunction;
};
dojo.lang.extend( jetspeed.om.FoldersListContentListener,
{
    notifySuccess: function( /* XMLDocument */ data, /* String */ requestUrl, domainModelObject )
    {
        var folderlist = this.parseFolders( data );
		var pagesList = this.parsePages( data );
		var linksList = this.parseLinks( data );
        if ( dojo.lang.isFunction( this.notifyFinished ) )
        {
            this.notifyFinished( domainModelObject, folderlist,pagesList,linksList);
        }
    },
    notifyFailure: function( /* String */ type, /* String */ error, /* String */ requestUrl, domainModelObject )
    {
        dojo.raise( "FoldersListContentListener error [" + domainModelObject.toString() + "] url: " + requestUrl + " type: " + type + jetspeed.url.formatBindError( error ) );
    },    
    parseFolders: function( /* XMLNode */ node )
    {
        var folderlist = [];
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
            if ( childLName == "folders" )
            {
                var portletsNode = child ;
                var portletChildren = portletsNode.childNodes ;
                for ( var pI = 0 ; pI < portletChildren.length ; pI++ )
                {
                    var pChild = portletChildren[pI];
                    if ( pChild.nodeType != dojo.dom.ELEMENT_NODE )
                        continue;
                    var pChildLName = pChild.nodeName;
                    if (pChildLName == "folder")
                    {
                        var folderdef = this.parsePortletElement( pChild );
                        folderlist.push( folderdef ) ;
                    }					
                }
            }
        }
        return folderlist ;
    },
	parsePages: function( /* XMLNode */ node )
    {
		var pageslist = [];
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
            if ( childLName == "folders" )
            {
                var portletsNode = child ;
                var portletChildren = portletsNode.childNodes ;
                for ( var pI = 0 ; pI < portletChildren.length ; pI++ )
                {
                    var pChild = portletChildren[pI];
                    if ( pChild.nodeType != dojo.dom.ELEMENT_NODE )
                        continue;
                    var pChildLName = pChild.nodeName;
                    if (pChildLName == "page")
                    {
                        var folderdef = this.parsePortletElement( pChild );
                        pageslist.push( folderdef ) ;
                    }
					
                }
            }
        }
        return pageslist ;
    },
	parseLinks: function( /* XMLNode */ node )
    {
		var linkslist = [];
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
            if ( childLName == "folders" )
            {
                var portletsNode = child ;
                var portletChildren = portletsNode.childNodes ;
                for ( var pI = 0 ; pI < portletChildren.length ; pI++ )
                {
                    var pChild = portletChildren[pI];
                    if ( pChild.nodeType != dojo.dom.ELEMENT_NODE )
                        continue;
                    var pChildLName = pChild.nodeName;
                    if (pChildLName == "link")
                    {
                        var folderdef = this.parsePortletElement( pChild );
                        linkslist.push( folderdef ) ;
                    }
					
                }
            }
        }
        return linkslist ;
    },
    parsePortletElement: function( /* XMLNode */ node )
    {
        var folderName = node.getAttribute( "name" );
        var folderPath = node.getAttribute( "path" );
        return new jetspeed.om.FolderDef( folderName, folderPath) ;
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
		var count = this.parsList(data);
        if ( dojo.lang.isFunction( this.notifyFinished ) )
        {
            this.notifyFinished( domainModelObject, portletList,count );
        }
    },
    notifyFailure: function( /* String */ type, /* String */ error, /* String */ requestUrl, domainModelObject )
    {
        dojo.raise( "PortletSelectorAjaxApiContentListener error [" + domainModelObject.toString() + "] url: " + requestUrl + " type: " + type + jetspeed.url.formatBindError( error ) );
    },
    parsList: function( /* XMLNode */ node )
    {
        var count;
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
            if ( childLName == "resultCount" )
            {
				count =child.textContent;
            }
        }
        return count ;
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
		var count = node.getAttribute( "count" );
        return new jetspeed.om.PortletDef( portletName, portletDisplayName, portletDescription, portletImage,count ) ;
    }
});


// ... jetspeed.om.MoveAjaxApiContentListener
jetspeed.om.MoveAjaxApiContentListener = function( /* Portlet */ portlet, changedState )
{
    this.portlet = portlet;
    this.changedState = changedState;
    this._setPortletLoading( true );
};
jetspeed.om.MoveAjaxApiContentListener.prototype =
{
    _setPortletLoading: function( /* boolean */ showLoading )
    {
        if ( this.portlet == null ) return;
        if ( showLoading )
            this.portlet.loadingIndicatorShow( jetspeed.id.ACTION_NAME_LOAD_UPDATE );
        else
            this.portlet.loadingIndicatorHide();
    },
    notifySuccess: function( /* String */ data, /* String */ requestUrl, domainModelObject )
    {
        this._setPortletLoading( false );
        dojo.lang.mixin( domainModelObject.portlet.lastSavedWindowState, this.changedState );
        var reportError = false;
        if ( djConfig.isDebug && jetspeed.debug.submitChangedWindowState )
            reportError = true;
        jetspeed.url.checkAjaxApiResponse( requestUrl, data, reportError, ("move-portlet [" + domainModelObject.portlet.entityId + "]"), jetspeed.debug.submitChangedWindowState );
    },
    notifyFailure: function( /* String */ type, /* Object */ error, /* String */ requestUrl, domainModelObject )
    {
        this._setPortletLoading( false );
        dojo.debug( "submitChangedWindowState error [" + domainModelObject.entityId + "] url: " + requestUrl + " type: " + type + jetspeed.url.formatBindError( error ) );
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

jetspeed.ui.dumpPortletWindowWidgets = function()
{
    var portletWindows = jetspeed.ui.getAllPortletWindowWidgets();
    var pwOut = "";
    for ( var i = 0 ; i < portletWindows.length; i++ )
    {
        if ( i > 0 )
            pwOut += ", ";
        pwOut += portletWindows[i].widgetId;
    }
    dojo.debug( "PortletWindow widgets: " + pwOut );
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

jetspeed.debugWindowLoad = function()
{
    if ( djConfig.isDebug && jetspeed.debugInPortletWindow && dojo.byId( jetspeed.debug.debugContainerId ) == null )
    {
        var debugWindowState = jetspeed.debugWindowReadCookie( true );
        var windowParams = {};
        var debugWindowWidgetId = jetspeed.id.PORTLET_WINDOW_ID_PREFIX + jetspeed.id.DEBUG_WINDOW_TAG;
        windowParams[ jetspeed.id.PORTLET_PROP_WINDOW_POSITION_STATIC ] = false;
        windowParams[ jetspeed.id.PORTLET_PROP_WINDOW_HEIGHT_TO_FIT ] = false;
        windowParams[ jetspeed.id.PORTLET_PROP_WINDOW_DECORATION ] = jetspeed.prefs.windowDecoration;
        windowParams[ jetspeed.id.PORTLET_PROP_WINDOW_TITLE ] = "Dojo Debug";
        windowParams[ jetspeed.id.PORTLET_PROP_WINDOW_ICON ] = "text-x-script.png";
        windowParams[ jetspeed.id.PORTLET_PROP_WIDGET_ID ] = debugWindowWidgetId;
        windowParams[ jetspeed.id.PORTLET_PROP_WIDTH ] = debugWindowState.width;
        windowParams[ jetspeed.id.PORTLET_PROP_HEIGHT ] = debugWindowState.height;
        windowParams[ jetspeed.id.PORTLET_PROP_LEFT ] = debugWindowState.left;
        windowParams[ jetspeed.id.PORTLET_PROP_TOP ] = debugWindowState.top;
        windowParams[ jetspeed.id.PORTLET_PROP_EXCLUDE_PCONTENT ] = false;
        windowParams[ jetspeed.id.PORTLET_PROP_CONTENT_RETRIEVER ] = new jetspeed.om.DojoDebugContentRetriever();
        windowParams[ jetspeed.id.PORTLET_PROP_WINDOW_STATE ] = debugWindowState.windowState;
        var pwWidgetParams = jetspeed.widget.PortletWindow.prototype.staticDefineAsAltInitParameters( null, windowParams );
        jetspeed.ui.createPortletWindow( pwWidgetParams );
        pwWidgetParams.retrieveContent( null, null );
        var debugWindowWidget = dojo.widget.byId( debugWindowWidgetId );
        var debugContainer = dojo.byId( jetspeed.debug.debugContainerId );

        dojo.event.connect( "after", dojo.hostenv, "println", debugWindowWidget, "contentChanged" );
    
        dojo.event.connect( debugWindowWidget, "windowActionButtonSync", jetspeed, "debugWindowSave" );
        dojo.event.connect( debugWindowWidget, "endSizing", jetspeed, "debugWindowSave" );
        dojo.event.connect( debugWindowWidget, "endDragging", jetspeed, "debugWindowSave" );
    }
};
jetspeed.debugWindowReadCookie = function( useDefaults )
{
    var debugState = {};
    if ( useDefaults )
        debugState = { width: "400", height: "400", left: "320", top: "0", windowState: jetspeed.id.ACTION_NAME_MINIMIZE };
    var stateCookieVal = dojo.io.cookie.getCookie( jetspeed.id.DEBUG_WINDOW_TAG );
    if ( stateCookieVal != null && stateCookieVal.length > 0 )
    {
        var debugStateRaw = stateCookieVal.split( "|" );
        if ( debugStateRaw && debugStateRaw.length >= 4 )
        {
            debugState.width = debugStateRaw[0]; debugState.height = debugStateRaw[1]; debugState.top = debugStateRaw[2]; debugState.left = debugStateRaw[3];
            if ( debugStateRaw.length > 4 && debugStateRaw[4] != null && debugStateRaw[4].length > 0 )
                debugState.windowState=debugStateRaw[4];
        }
    }
    return debugState;
};
jetspeed.debugWindowRestore = function()
{
    var debugWindowWidgetId = jetspeed.id.PORTLET_WINDOW_ID_PREFIX + jetspeed.id.DEBUG_WINDOW_TAG;
    var debugWindowWidget = dojo.widget.byId( debugWindowWidgetId );
    if ( ! debugWindowWidget ) return;
    debugWindowWidget.restoreWindow();
};
jetspeed.debugWindow = function()
{
    var debugWindowWidgetId = jetspeed.id.PORTLET_WINDOW_ID_PREFIX + jetspeed.id.DEBUG_WINDOW_TAG;
    return dojo.widget.byId( debugWindowWidgetId );
};
jetspeed.debugWindowSave = function()
{
    var debugWindowWidgetId = jetspeed.id.PORTLET_WINDOW_ID_PREFIX + jetspeed.id.DEBUG_WINDOW_TAG;
    var debugWindowWidget = dojo.widget.byId( debugWindowWidgetId );
    if ( ! debugWindowWidget ) return null;
    if ( ! debugWindowWidget.windowPositionStatic )
    {
        var currentState = debugWindowWidget.getCurrentWindowStateForPersistence( false );
        var cWidth = currentState.width; var cHeight = currentState.height; var cTop = currentState.top; var cLeft = currentState.left;
        if ( debugWindowWidget.windowState == jetspeed.id.ACTION_NAME_MINIMIZE )
        {
            var lastPositionInfo = debugWindowWidget.getLastPositionInfo();
            if ( lastPositionInfo != null )
            {
                if ( lastPositionInfo.height != null && lastPositionInfo.height > 0 )
                    cHeight = lastPositionInfo.height;
            }
            else
            {
                var debugWindowState = jetspeed.debugWindowReadCookie( false );
                if ( debugWindowState.height != null && debugWindowState.height > 0 )
                    cHeight = debugWindowState.height;
            }
        }

        var stateCookieVal = cWidth + "|" + cHeight + "|" + cTop + "|" + cLeft + "|" + debugWindowWidget.windowState;
        dojo.io.cookie.setCookie( jetspeed.id.DEBUG_WINDOW_TAG, stateCookieVal, 30, "/" );
    }
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
