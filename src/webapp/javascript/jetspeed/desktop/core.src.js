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

dojo.provide( "jetspeed.desktop.core" );

dojo.require( "dojo.lang.*" );
dojo.require( "dojo.event.*" );
dojo.require( "dojo.io.*" );
dojo.require( "dojo.uri.Uri" );
dojo.require( "dojo.widget.*" );
dojo.require( "jetspeed.common" );


// jetspeed base objects

if ( ! window.jetspeed )
    jetspeed = {};
if ( ! jetspeed.om )
    jetspeed.om = {};
if ( ! jetspeed.debug )
    jetspeed.debug = {};


// jetspeed.id

jetspeed.id =
{
    PAGE: "jetspeedPage",
    DESKTOP_CELL: "jetspeedDesktopCell",
    DESKTOP: "jetspeedDesktop",
    COLUMNS: "jetspeedColumns",
    PAGE_CONTROLS: "jetspeedPageControls",
    
    P_CLASS: "portlet",
    PWIN_CLASS: "portletWindow",
    PWIN_GHOST_CLASS: "ghostPane",
    PW_ID_PREFIX: "pw_",

    // ... pp - portlet props
    PP_WIDGET_ID: "widgetId",
    PP_CONTENT_RETRIEVER: "contentRetriever",
    PP_DESKTOP_EXTENDED: "jsdesktop",
    PP_WINDOW_POSITION_STATIC: "windowPositionStatic",
    PP_WINDOW_HEIGHT_TO_FIT: "windowHeightToFit",
    PP_WINDOW_DECORATION: "windowDecoration",
    PP_WINDOW_TITLE: "title",
    PP_WINDOW_ICON: "windowIcon",
    PP_WIDTH: "width",
    PP_HEIGHT: "height",
    PP_LEFT: "left",
    PP_TOP: "top",
    PP_COLUMN: "column",
    PP_ROW: "row",
    PP_EXCLUDE_PCONTENT: "excludePContent",
    PP_WINDOW_STATE: "windowState",

    PP_STATICPOS: "staticpos",
    PP_FITHEIGHT: "fitheight",
    PP_PROP_SEPARATOR: "=",
    PP_PAIR_SEPARATOR: ";",

    // these constants for action names are defined because they have special meaning to desktop (ie. this is not a list of all supported actions)
    ACT_MENU: "menu",
    ACT_MINIMIZE: "minimized",
    ACT_MAXIMIZE: "maximized",
    ACT_RESTORE: "normal",
    ACT_PRINT: "print",
    ACT_EDIT: "edit",
    ACT_VIEW: "view",
    ACT_HELP: "help",
    ACT_ADDPORTLET: "addportlet",
    ACT_REMOVEPORTLET: "removeportlet",

    ACT_DESKTOP_TILE: "tile",
    ACT_DESKTOP_UNTILE: "untile",
    ACT_DESKTOP_HEIGHT_EXPAND: "heightexpand",
    ACT_DESKTOP_HEIGHT_NORMAL: "heightnormal",

    ACT_LOAD_RENDER: "loadportletrender",
    ACT_LOAD_ACTION: "loadportletaction",
    ACT_LOAD_UPDATE: "loadportletupdate",

    PORTLET_ACTION_TYPE_MODE: "mode",
    PORTLET_ACTION_TYPE_STATE: "state",

    MENU_WIDGET_ID_PREFIX: "jetspeed-menu-",

    PG_ED_WID: "jetspeed-page-editor",
    PG_ED_PARAM: "editPage",
    PORTAL_ORIGINATE_PARAMETER: "portal",

    DEBUG_WINDOW_TAG: "js-db"
};


// jetspeed desktop preferences - defaults

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

    
    windowActionButtonMax: 5,
    windowActionButtonHide: false,
    windowActionButtonTooltip: true,
    //windowActionButtonOrder, windowActionNotPortlet, windowActionMenuOrder - see jetspeed.initializeDesktop
    
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


// load page /portlets

jetspeed.page = null ;
jetspeed.initializeDesktop = function()
{
    var jsObj = jetspeed;
    var jsId = jsObj.id;
    var jsPrefs = jsObj.prefs;
    var jsDebug = jsObj.debug;
    var djObj = dojo;

    jsObj.getBody();   // sets jetspeed.docBody

    jsObj.ui.initCssObj();
    
    jsPrefs.windowActionButtonOrder = [ jsId.ACT_MENU, "edit", "view", "help", jsId.ACT_MINIMIZE, jsId.ACT_RESTORE, jsId.ACT_MAXIMIZE ];
    jsPrefs.windowActionNotPortlet = [ jsId.ACT_MENU, jsId.ACT_MINIMIZE, jsId.ACT_RESTORE, jsId.ACT_MAXIMIZE ];
    jsPrefs.windowActionMenuOrder = [ jsId.ACT_DESKTOP_HEIGHT_EXPAND, jsId.ACT_DESKTOP_HEIGHT_NORMAL, jsId.ACT_DESKTOP_TILE, jsId.ACT_DESKTOP_UNTILE ];

    jsObj.url.pathInitialize();

    var djConfJetspeed = djConfig.jetspeed;
    if ( djConfJetspeed != null )
    {
        for ( var prefKey in djConfJetspeed )
        {
            var prefOverrideVal = djConfJetspeed[ prefKey ];
            if ( prefOverrideVal != null )
            {
                if ( jsDebug[ prefKey ] != null )
                    jsDebug[ prefKey ] = prefOverrideVal;
                else
                    jsPrefs[ prefKey ] = prefOverrideVal;
            }
        }
        if ( jsPrefs.windowWidth == null || isNaN( jsPrefs.windowWidth ) )
            jsPrefs.windowWidth = "280";
        if ( jsPrefs.windowHeight == null || isNaN( jsPrefs.windowHeight ) )
            jsPrefs.windowHeight = "200";
        
        var windowActionDesktop = {};
        windowActionDesktop[ jsId.ACT_DESKTOP_HEIGHT_EXPAND ] = true;
        windowActionDesktop[ jsId.ACT_DESKTOP_HEIGHT_NORMAL ] = true;
        windowActionDesktop[ jsId.ACT_DESKTOP_TILE ] = true;
        windowActionDesktop[ jsId.ACT_DESKTOP_UNTILE ] = true;
        jsPrefs.windowActionDesktop = windowActionDesktop;
    }
    var defaultPortletWindowCSSUrl = new djObj.uri.Uri( jetspeed.url.basePortalDesktopUrl() + "/javascript/jetspeed/widget/PortletWindow.css" );
    djObj.html.insertCssFile( defaultPortletWindowCSSUrl, document, true );

    if ( jsPrefs.portletDecorationsAllowed == null || jsPrefs.portletDecorationsAllowed.length == 0 )
    {
        if ( jsPrefs.windowDecoration != null )
            jsPrefs.portletDecorationsAllowed = [ jsPrefs.windowDecoration ];
    }
    else if ( jsPrefs.windowDecoration == null )
    {
        jsPrefs.windowDecoration = jsPrefs.portletDecorationsAllowed[0];
    }
    if ( jsPrefs.windowDecoration == null || jsPrefs.portletDecorationsAllowed == null )
    {
        djObj.raise( "No portlet decorations" );
        return;
    }

    if ( jsPrefs.windowActionNoImage != null )
    {
        var noImageMap = {};
        for ( var i = 0 ; i < jsPrefs.windowActionNoImage.length; i++ )
        {
            noImageMap[ jsPrefs.windowActionNoImage[ i ] ] = true;
        }
        jsPrefs.windowActionNoImage = noImageMap;
    }

    var docUrlObj = jsObj.url.parse( window.location.href );
    var printModeOnly = jsObj.url.getQueryParameter( docUrlObj, "jsprintmode" ) == "true";
    if ( printModeOnly )
    {
        printModeOnly = {};
        printModeOnly.action = jsObj.url.getQueryParameter( docUrlObj, "jsaction" );
        printModeOnly.entity = jsObj.url.getQueryParameter( docUrlObj, "jsentity" );
        printModeOnly.layout = jsObj.url.getQueryParameter( docUrlObj, "jslayoutid" );
        jsPrefs.printModeOnly = printModeOnly;
        jsPrefs.windowTiling = true;
        jsPrefs.windowHeightExpand = true;
        jsPrefs.ajaxPageNavigation = false;
    }

    jsPrefs.portletDecorationsConfig = {};
    for ( var i = 0 ; i < jsPrefs.portletDecorationsAllowed.length ; i++ )
    {
        jsObj.loadPortletDecorationConfig( jsPrefs.portletDecorationsAllowed[ i ] );
    }

    if ( jsObj.UAie6 )
    {
        jsPrefs.ajaxPageNavigation = false;    // not advisable in IE6 or older
    }

    if ( printModeOnly )
    {
        for ( var portletDecorationName in jsPrefs.portletDecorationsConfig )
        {
            var pdConfig = jsPrefs.portletDecorationsConfig[ portletDecorationName ];
            if ( pdConfig != null )
            {
                pdConfig.windowActionButtonOrder = null;
                pdConfig.windowActionMenuOrder = null;
                pdConfig.windowDisableResize = true;
                pdConfig.windowDisableMove = true;
            }
        }
    }
    jsObj.url.loadingIndicatorShow();

    jsObj.page = new jsObj.om.Page();

    if ( ! printModeOnly && djConfig.isDebug )
    {
        if ( jsObj.debugWindowLoad )
            jsObj.debugWindowLoad();

        if ( jsObj.debug.profile && djObj.profile )
            djObj.profile.start( "initializeDesktop" );
        else
            jsObj.debug.profile = false;
    }
    else
    {
        jsObj.debug.profile = false;
    }

    jsObj.page.retrievePsml();
};
jetspeed.updatePage = function( navToPageUrl, backOrForwardPressed )
{
    var jsObj = jetspeed;
    
    var dbProfile = false;
    if ( djConfig.isDebug && jsObj.debug.profile )
    {
        dbProfile = true;
        dojo.profile.start( "updatePage" );
    }

    var currentPage = jsObj.page;
    if ( ! navToPageUrl || ! currentPage || jsObj.pageNavigateSuppress ) return;
    if ( currentPage.equalsPageUrl( navToPageUrl ) )
        return ;
    navToPageUrl = currentPage.makePageUrl( navToPageUrl );
    if ( navToPageUrl != null )
    {
        jsObj.updatePageBegin();
        var currentLayoutDecorator = currentPage.layoutDecorator;
        var currentEditMode = currentPage.editMode;
        if ( dbProfile )
            dojo.profile.start( "destroyPage" );
        currentPage.destroy();
        if ( dbProfile )
            dojo.profile.end( "destroyPage" );
        
        var retainedWindows = currentPage.portlet_windows;        
        var retainedWindowCount = currentPage.portlet_window_count;

        var newJSPage = new jsObj.om.Page( currentLayoutDecorator, navToPageUrl, (! djConfig.preventBackButtonFix && ! backOrForwardPressed), currentEditMode, currentPage.tooltipMgr, currentPage.iframeCoverByWinId );
        jsObj.page = newJSPage;

        var pWin;
        if ( retainedWindowCount > 0 )
        {
            for ( var windowId in retainedWindows )
            {
                pWin = retainedWindows[ windowId ];
                pWin.bringToTop( null, true );
            }
        }
    
        newJSPage.retrievePsml( new jsObj.om.PageCLCreateWidget( true ) );
        
        if ( retainedWindowCount > 0 )
        {
            for ( var windowId in retainedWindows )
            {
                pWin = retainedWindows[ windowId ];
                newJSPage.putPWin( pWin );
            }
        }

        window.focus();   // to prevent IE from sending alt-arrow to tab container
    }
};

jetspeed.updatePageBegin = function()
{
    var jsObj = jetspeed;
    if ( jsObj.UAie6 )
    {
        jsObj.docBody.attachEvent( "onclick", jsObj.ie6StopMouseEvts );
        jsObj.docBody.setCapture();
    }
}
jetspeed.ie6StopMouseEvts = function( e )
{
    if ( e )
    {
        e.cancelBubble = true;
        e.returnValue = false;
    }
}
jetspeed.updatePageEnd = function()
{
    var jsObj = jetspeed;
    if ( jsObj.UAie6 )
    {
        jsObj.docBody.releaseCapture();
        jsObj.docBody.detachEvent( "onclick", jsObj.ie6StopMouseEvts );
        jsObj.docBody.releaseCapture();
    }
}


// jetspeed.doRender

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


// jetspeed.doAction

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
        targetPortlet.retrieveContent( new jetspeed.om.PortletActionCL( targetPortlet, bindArgs ), bindArgs );
    }
};


// jetspeed.PortletRenderer

jetspeed.PortletRenderer = function( createWindows, isPageLoad, isPageUpdate, renderUrl, suppressGetActions )
{
    var jsObj = jetspeed;
    var jsPage = jsObj.page;
    this._jsObj = jsObj;

    this.createWindows = createWindows;
    this.isPageLoad = isPageLoad;
    this.isPageUpdate = isPageUpdate;
    this.pageLoadUrl = null;
    if ( isPageLoad )
        this.pageLoadUrl = jsObj.url.parse( jsPage.getPageUrl() );
    this.renderUrl = renderUrl;
    this.suppressGetActions = suppressGetActions;

    this._colLen = jsPage.columns.length;
    this._colIndex = 0;
    this._portletIndex = 0;

    this.psByCol = jsPage.portletsByPageColumn;

    this.debugPageLoad = jsObj.debug.pageLoad && isPageLoad;
    this.debugMsg = null;
    if ( jsObj.debug.doRenderDoAction || this.debugPageLoad )
        this.debugMsg = "";
};
dojo.lang.extend( jetspeed.PortletRenderer,
{
    renderAll: function()
    {
        do
        {
            this._renderCurrent();
        } while ( this._evalNext() )

        this._finished();
    },
    renderAllTimeDistribute: function()
    {
        this._renderCurrent();
        if ( this._evalNext() )
        {
            dojo.lang.setTimeout( this, this.renderAllTimeDistribute, 10 );
        }
        else
        {
            this._finished();
        }
    },
    _finished: function()
    {
        var jsObj = this._jsObj;

        var debugMsg = this.debugMsg;
        if ( debugMsg != null )
        {
            if ( this.debugPageLoad )
                dojo.debug( "portlet-renderer page-url: " + jsObj.page.getPsmlUrl() + " portlets: [" + renderMsg + "]" + ( url ? ( " url: " + url ) : "" ) );
            else
                dojo.debug( "portlet-renderer [" + renderMsg + "] url: " + url );
        }
        
        if ( this.isPageLoad )
        {
            jsObj.page.loadPostRender( this.isPageUpdate );
        }
    },
    _renderCurrent: function()
    {
        var jsObj = this._jsObj;
        
        var colLen = this._colLen;
        var colIndex = this._colIndex;
        var portletIndex = this._portletIndex;
        
        if ( colIndex <= colLen )
        {
            var portletArray;
            if ( colIndex < colLen )
                portletArray = this.psByCol[ colIndex.toString() ];
            else
            {
                portletArray = this.psByCol[ "z" ];
                colIndex = null;
            }
            var portletLen = (portletArray != null ? portletArray.length : 0);
            if ( portletLen > 0 )
            {
                var pAryElmt = portletArray[portletIndex];
                if ( pAryElmt )
                {
                    var renderObj = pAryElmt.portlet;
                    if ( this.createWindows )
                        jsObj.ui.createPortletWindow( renderObj, colIndex, jsObj );
                    
                    var debugMsg = this.debugMsg;
                    if ( debugMsg != null )
                    {
                        if ( debugMsg.length > 0 )
                            debugMsg = debugMsg + ", ";
                        var widgetId = null;
                        if ( renderObj.getProperty != null )
                            widgetId = renderObj.getProperty( jsObj.id.PP_WIDGET_ID );
                        if ( ! widgetId )
                            widgetId = renderObj.widgetId;
                        if ( ! widgetId )
                            widgetId = renderObj.toString();
                        if ( renderObj.entityId )
                        {
                            debugMsg = debugMsg + renderObj.entityId + "(" + widgetId + ")";
                            if ( this._dbPgLd && renderObj.getProperty( jsObj.id.PP_WINDOW_TITLE ) )
                                debugMsg = debugMsg + " " + renderObj.getProperty( jsObj.id.PP_WINDOW_TITLE );
                        }
                        else
                        {
                            debugMsg = debugMsg + widgetId;
                        }
                    }

                    renderObj.retrieveContent( null, { url: this.renderUrl, jsPageUrl: this.pageLoadUrl }, this.suppressGetActions );
                }
            }
        }
    },
    _evalNext: function()
    {
        var nextFound = false;
        var colLen = this._colLen;
        var colIndex = this._colIndex;
        var portletIndex = this._portletIndex;

        var curColIndex = colIndex;
        var portletArray;

        // check if there's any portlet window in the next columns.
        for ( ++colIndex; colIndex <= colLen; colIndex++ )
        {
            portletArray = this.psByCol[ colIndex == colLen ? "z" : colIndex.toString() ];
            if ( portletIndex < (portletArray != null ? portletArray.length : 0) )
            {
                nextFound = true;
                this._colIndex = colIndex;
                break;
            }
        }
        
        // check if there's any portlet window in the previous columns.
        if ( ! nextFound )
        {
            ++portletIndex;
            for ( colIndex = 0; colIndex <= curColIndex; colIndex++ )
            {
                portletArray = this.psByCol[ colIndex == colLen ? "z" : colIndex.toString() ];
                if ( portletIndex < (portletArray != null ? portletArray.length : 0) )
                {
                    nextFound = true;
                    this._colIndex = colIndex;
                    this._portletIndex = portletIndex;
                    break;
                }
            }
        }
        return nextFound;
    }
});

jetspeed.portleturl =
{
    DESKTOP_ACTION_PREFIX_URL: null,
    DESKTOP_RENDER_PREFIX_URL: null,
    JAVASCRIPT_ARG_QUOTE: "&" + "quot;",
    PORTLET_REQUEST_ACTION: "action",
    PORTLET_REQUEST_RENDER: "render",
    JETSPEED_DO_NOTHING_ACTION: "javascript:jetspeed.doNothingNav()",

    parseContentUrl: function( /* String */ contentUrl )   // parseContentUrlForDesktopActionRender
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
        
        if ( ! jetspeed.url.urlStartsWithHttp( justTheUrl ) )
            justTheUrl = null;

        return { url: justTheUrl, operation: op, portletEntityId: entityId };
    },

    genPseudoUrl: function( parsedPseudoUrl, makeDummy )   // generateJSPseudoUrlActionRender
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
    if ( portletDecorationConfig != null && ! portletDecorationConfig._initialized )
    {
        var pdBaseUrl = jetspeed.prefs.getPortletDecorationBaseUrl( portletDecorationName );
        portletDecorationConfig._initialized = true;
        portletDecorationConfig.cssPathCommon = new dojo.uri.Uri( pdBaseUrl + "/css/styles.css" );
        portletDecorationConfig.cssPathDesktop = new dojo.uri.Uri( pdBaseUrl + "/css/desktop.css" );
        
        dojo.html.insertCssFile( portletDecorationConfig.cssPathCommon, null, true );
        dojo.html.insertCssFile( portletDecorationConfig.cssPathDesktop, null, true );
    }
    return portletDecorationConfig;
};
jetspeed.loadPortletDecorationConfig = function( portletDecorationName )
{   // setup default portlet decoration config
    var jsPrefs = jetspeed.prefs;
    var pdConfig = {};
    jsPrefs.portletDecorationsConfig[ portletDecorationName ] = pdConfig;
    pdConfig.windowActionButtonOrder = jsPrefs.windowActionButtonOrder;
    pdConfig.windowActionNotPortlet = jsPrefs.windowActionNotPortlet;
    pdConfig.windowActionButtonMax = jsPrefs.windowActionButtonMax;
    pdConfig.windowActionButtonHide = jsPrefs.windowActionButtonHide;
    pdConfig.windowActionButtonTooltip = jsPrefs.windowActionButtonTooltip;
    pdConfig.windowActionMenuOrder = jsPrefs.windowActionMenuOrder;
    pdConfig.windowActionNoImage = jsPrefs.windowActionNoImage;
    pdConfig.windowIconEnabled = jsPrefs.windowIconEnabled;
    pdConfig.windowIconPath = jsPrefs.windowIconPath;

    // load portlet decoration config
    var portletDecorationConfigUri = jsPrefs.getPortletDecorationBaseUrl( portletDecorationName ) + "/" + portletDecorationName + ".js";
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

jetspeed.notifyRetrieveAllMenusFinished = function()
{   // dojo.event.connect to this or add to your page content, one of the functions that it invokes ( doMenuBuildAll() or doMenuBuild() )
    var jsObj = jetspeed;
    jsObj.pageNavigateSuppress = true;

    if ( dojo.lang.isFunction( window.doMenuBuildAll ) )
    {   
        window.doMenuBuildAll();
    }
    
    var menuNames = jsObj.page.getMenuNames();
    for ( var i = 0 ; i < menuNames.length; i++ )
    {
        var menuNm = menuNames[i];
        var menuWidget = dojo.widget.byId( jsObj.id.MENU_WIDGET_ID_PREFIX + menuNm );
        if ( menuWidget )
        {
            menuWidget.createJetspeedMenu( jsObj.page.getMenu( menuNm ) );
        }
    }
    
    jsObj.url.loadingIndicatorHide();
    jsObj.pageNavigateSuppress = false;
};

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
            dojo.raise( "Tab widget not found: " + tabWidgetId );
    }
    if ( tabWidget )
    {
        var jetspeedMenuName = tabWidget.jetspeedmenuname;
        if ( ! jetspeedMenuName && tabWidget.extraArgs )
            jetspeedMenuName = tabWidget.extraArgs.jetspeedmenuname;
        if ( ! jetspeedMenuName )
            dojo.raise( "Tab widget is invalid: " + tabWidget.widgetId );
        var menuObj = jetspeed.page.getMenu( jetspeedMenuName );
        if ( ! menuObj )
            dojo.raise( "Tab widget " + tabWidget.widgetId + " no menu: " + jetspeedMenuName );
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

jetspeed.getActionsForPortlet = function( /* String */ portletEntityId )
{
    if ( portletEntityId == null ) return;
    jetspeed.getActionsForPortlets( [ portletEntityId ] );
};
jetspeed.getActionsForPortlets = function( /* Array */ portletEntityIds )
{
    if ( portletEntityIds == null )
        portletEntityIds = jetspeed.page.getPortletIds();
    var contentListener = new jetspeed.om.PortletActionsCL( portletEntityIds );
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
        contentListener = new jetspeed.om.PortletChangeActionCL( portletEntityId );
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

jetspeed.editPageInitiate = function()
{
    var jsObj = jetspeed;
    if ( ! jsObj.page.editMode )
    {
        var fromDesktop = true;
        var fromPortal = jsObj.url.getQueryParameter( window.location.href, jsObj.id.PORTAL_ORIGINATE_PARAMETER );
        if ( fromPortal != null && fromPortal == "true" )
            fromDesktop = false;
        jsObj.page.editMode = true;
        var pageEditorWidget = dojo.widget.byId( jsObj.id.PG_ED_WID );
        if ( jsObj.UAie6 )
            jsObj.page.displayAllPWins( true );
        if ( pageEditorWidget == null )
        {
            try
            {
                jsObj.url.loadingIndicatorShow( "loadpageeditor" );
                pageEditorWidget = dojo.widget.createWidget( "jetspeed:PageEditor", { widgetId: jsObj.id.PG_ED_WID, editorInitiatedFromDesktop: fromDesktop } );
                var allColumnsContainer = document.getElementById( jsObj.id.COLUMNS );
                allColumnsContainer.insertBefore( pageEditorWidget.domNode, allColumnsContainer.firstChild );
            }
            catch (e)
            {
                jsObj.url.loadingIndicatorHide();
                if ( jsObj.UAie6 )
                    jsObj.page.displayAllPWins();
            }
        }
        else
        {
            pageEditorWidget.editPageShow();
        }
        jsObj.page.syncPageControls();
    }
};
jetspeed.editPageTerminate = function()
{
    if ( jetspeed.page.editMode )
    {
        var pageEditorWidget = dojo.widget.byId( jetspeed.id.PG_ED_WID );
        pageEditorWidget.editModeNormal();  // in case we're in move-mode
        jetspeed.page.editMode = false;
        if ( ! pageEditorWidget.editorInitiatedFromDesktop )
        {
            var portalPageUrl = jetspeed.page.getPageUrl( true );
            portalPageUrl = jetspeed.url.removeQueryParameter( portalPageUrl, jetspeed.id.PG_ED_PARAM );
            portalPageUrl = jetspeed.url.removeQueryParameter( portalPageUrl, jetspeed.id.PORTAL_ORIGINATE_PARAMETER );
            window.location.href = portalPageUrl;
        }
        else
        {
            var pageEditorInititate = jetspeed.url.getQueryParameter( window.location.href, jetspeed.id.PG_ED_PARAM );
            if ( pageEditorInititate != null && pageEditorInititate == "true" )
            {   // because of parameter, we must navigate
                var dtPageUrl = window.location.href; // jetspeed.page.getPageUrl( false );
                dtPageUrl = jetspeed.url.removeQueryParameter( dtPageUrl, jetspeed.id.PG_ED_PARAM );
                window.location.href = dtPageUrl;
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
    }
};

// ... jetspeed.om.PortletContentRetriever
jetspeed.om.PortletContentRetriever = function()
{
};
jetspeed.om.PortletContentRetriever.prototype =
{   // /* Portlet */ portlet, /* String */ requestUrl, /* PortletCL */ portletCL
    getContent: function( bindArgs, contentListener, domainModelObject, /* String[] */ debugContentDumpIds )
    {
        if ( ! bindArgs )
            bindArgs = {};
        jetspeed.url.retrieveContent( bindArgs, contentListener, domainModelObject, debugContentDumpIds );
    }
};

// ... jetspeed.om.PageCLCreateWidget
jetspeed.om.PageCLCreateWidget = function( isPageUpdate )
{
    if ( typeof isPageUpdate == "undefined" )
        isPageUpdate = false ;
    this.isPageUpdate = isPageUpdate ;
};
jetspeed.om.PageCLCreateWidget.prototype =
{
    notifySuccess: function( /* XMLDocument */ data, /* String */ requestUrl, /* Page */ page )
    {
        page.loadFromPSML( data, this.isPageUpdate );
    },
    notifyFailure: function( /* String */ type, /* Object */ error, /* String */ requestUrl, /* Page */ page )
    {
        dojo.raise( "PageCLCreateWidget error url: " + requestUrl + " type: " + type + jetspeed.url.formatBindError( error ) );
    }
};

// ... jetspeed.om.Page
jetspeed.om.Page = function( requiredLayoutDecorator, navToPageUrl, addToHistory, editMode, tooltipMgr, iframeCoverByWinId )
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
    if ( typeof addToHistory != "undefined" )
        this.addToHistory = addToHistory;
    if ( typeof editMode != "undefined" )
        this.editMode = editMode;
    this.layouts = {};
    this.columns = [];
    this.portlets = [];
    this.portlet_count = 0;
    this.portlet_windows = {};
    this.portlet_window_count = 0;
    if ( iframeCoverByWinId != null )
        this.iframeCoverByWinId = iframeCoverByWinId;
    else
        this.iframeCoverByWinId = {};
    this.portlet_tiled_high_z = 10;
    this.portlet_untiled_high_z = -1;
    this.menus = [];
    
    if ( tooltipMgr != null )
        this.tooltipMgr = tooltipMgr;
    else
    {
        this.tooltipMgr = dojo.widget.createWidget( "jetspeed:PortalTooltipManager", { isContainer: false, fastMixIn: true } );
        // setting isContainer=false and fastMixIn=true to avoid recursion hell when connectId is a node (could give each an id instead)
        jetspeed.docBody.appendChild( this.tooltipMgr.domNode );
    }
};
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
    addToHistory: false,

    layouts: null,
    columns: null,
    portlets: null,
    portletsByPageColumn: null,

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
        {
            docPath = window.location.pathname;
            if ( ! djConfig.preventBackButtonFix && jetspeed.prefs.ajaxPageNavigation )
            {
                var hash = window.location.hash;
                if ( hash != null && hash.length > 0 )
                {
                    if ( hash.indexOf( "#" ) == 0 )
                    {
                        hash = ( hash.length > 1 ? hash.substring(1) : "" );
                    }
                    if ( hash != null && hash.length > 1 && hash.indexOf( "/" ) == 0 )
                    {
                        this.psmlPath = jetspeed.url.path.AJAX_API + hash;
                        return;
                    }
                }
            }
        }
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
            pageContentListener = new jetspeed.om.PageCLCreateWidget();

        var psmlUrl = this.getPsmlUrl() ;
        var mimetype = "text/xml";

        if ( jetspeed.debug.retrievePsml )
            dojo.debug( "retrievePsml url: " + psmlUrl ) ;

        jetspeed.url.retrieveContent( { url: psmlUrl, mimetype: mimetype }, pageContentListener, this, jetspeed.debugContentDumpIds );
    },

    loadFromPSML: function( psml, isPageUpdate )
    {
        var jsObj = jetspeed;
        var printModeOnly = jsObj.prefs.printModeOnly ;
        if ( djConfig.isDebug && jsObj.debug.profile && printModeOnly == null )
        {
            dojo.profile.start( "loadFromPSML" );
        }

        // parse PSML
        var parsedRootLayoutFragment = this._parsePSML( psml );
        if ( parsedRootLayoutFragment == null ) return;

        // create layout model
        this.portletsByPageColumn = {};
        this.columnsStructure = this._layoutCreateModel( parsedRootLayoutFragment, null, this.portletsByPageColumn, true );

        this.rootFragmentId = parsedRootLayoutFragment.id ;

        var initiateEditMode = false;
        if ( this.editMode )
        {
            this.editMode = false;
            if ( printModeOnly == null )
                initiateEditMode = true;
        }

        // create columns
        if ( jsObj.prefs.windowTiling )
        {
            this._createColsStart( document.getElementById( jsObj.id.DESKTOP ) );
        }
        
        var portletArray = this.portletsByPageColumn[ "z" ];
        if ( portletArray )
        {
            portletArray.sort( this._loadPortletZIndexCompare );
        }

        var renderer = new jetspeed.PortletRenderer( true, true, isPageUpdate, null, true );
        renderer.renderAllTimeDistribute();
        //renderer.renderAll();
    },
    loadPostRender: function( isPageUpdate )
    {
        var jsObj = jetspeed;
        var printModeOnly = jsObj.prefs.printModeOnly ;
        if ( printModeOnly == null )
        {
            this._portletsInitWinState( this.portletsByPageColumn[ "z" ] );
    
            var initiateEditMode = false;
            if ( this.editMode )
                initiateEditMode = true;
            
            // detect edit mode force - likely to be temporary
            var pageEditorInititate = jsObj.url.getQueryParameter( window.location.href, jsObj.id.PG_ED_PARAM );
            if ( initiateEditMode || ( pageEditorInititate != null && pageEditorInititate == "true" ) || this.actions[ jsObj.id.ACT_VIEW ] != null )
            {
                initiateEditMode = false;
                if ( this.actions != null && ( this.actions[ jsObj.id.ACT_EDIT ] != null || this.actions[ jsObj.id.ACT_VIEW ] != null ) )
                    initiateEditMode = true;
            }
    
            // load menus
            this.retrieveMenuDeclarations( true, initiateEditMode, isPageUpdate );
    
            // render page buttons
            this.renderPageControls();
            this.syncPageControls();
        }
        else
        {
            for ( var portletIndex in this.portlets )
            {
                var portlet = this.portlets[portletIndex];
                if ( portlet != null )
                    portlet.renderAction( null, printModeOnly.action );
                break;
            }
            if ( isPageUpdate )
                jsObj.updatePageEnd() ;
        }
    },
    
    _parsePSML: function( psml )
    {
        var jsObj = jetspeed;
        var pageElements = psml.getElementsByTagName( "page" );
        if ( ! pageElements || pageElements.length > 1 || pageElements[0] == null )
            dojo.raise( "Expected one <page> in PSML" );
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
                if ( childLName == "short-title" )
                    childLName = "shortTitle";
                this[ childLName ] = ( ( child && child.firstChild ) ? child.firstChild.nodeValue : null );
            }
            else if ( childLName == "action" )
            {
                this._parsePSMLAction( child, rootFragmentActions ) ;
            }
        }
        this.actions = rootFragmentActions;

        if ( rootFragment == null )
        {
            dojo.raise( "No root fragment in PSML" );
            return null;
        }
        if ( this.requiredLayoutDecorator != null && this.pageUrlFallback != null )
        {
            if ( this.layoutDecorator != this.requiredLayoutDecorator )
            {
                if ( jsObj.debug.ajaxPageNav ) 
                    dojo.debug( "ajaxPageNavigation _parsePSML different layout decorator (" + this.requiredLayoutDecorator + " != " + this.layoutDecorator + ") - fallback to normal page navigation - " + this.pageUrlFallback );
                jsObj.pageNavigate( this.pageUrlFallback, null, true );
                return null;
            }
            else if ( this.addToHistory )
            {
                var currentPageUrl = this.getPageUrl();
                dojo.undo.browser.addToHistory({
	    	        back: function() { if ( jsObj.debug.ajaxPageNav ) dojo.debug( "back-nav-button: " + currentPageUrl ); jsObj.updatePage( currentPageUrl, true ); },
		            forward: function() { if ( jsObj.debug.ajaxPageNav ) dojo.debug( "forward-nav-button: " + currentPageUrl ); jsObj.updatePage( currentPageUrl, true ); },
		            changeUrl: escape( this.getPath() )
		        });
            }
        }
        else if ( ! djConfig.preventBackButtonFix && jsObj.prefs.ajaxPageNavigation )
        {
            var currentPageUrl = this.getPageUrl();
            dojo.undo.browser.setInitialState({
                back: function() { if ( jsObj.debug.ajaxPageNav ) dojo.debug( "back-nav-button initial: " + currentPageUrl ); jsObj.updatePage( currentPageUrl, true ); },
                forward: function() { if ( jsObj.debug.ajaxPageNav ) dojo.debug( "forward-nav-button initial: " + currentPageUrl ); jsObj.updatePage( currentPageUrl, true ); },
                changeUrl: escape( this.getPath() )
            });
        }

        var parsedRootLayoutFragment = this._parsePSMLFrag( rootFragment, 0 );    // rootFragment must be a layout fragment - /portal requires this as well
        return parsedRootLayoutFragment;
    },
    _parsePSMLFrag: function( layoutNode, layoutNodeDocumentOrderIndex )
    {
        var jsObj = jetspeed;
        var fragChildren = new Array();
        var layoutFragType = ( (layoutNode != null) ? layoutNode.getAttribute( "type" ) : null );
        if ( layoutFragType != "layout" )
        {
            dojo.raise( "Expected layout fragment: " + layoutNode );
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
                    var parsedLayoutChildFragment = this._parsePSMLFrag( child, i );
                    if ( parsedLayoutChildFragment != null )
                    {
                        fragChildren.push( parsedLayoutChildFragment ) ;
                    }
                }
                else
                {
                    var portletProps = this._parsePSMLProps( child, null );
                    var portletIcon = portletProps[ jsObj.id.PP_WINDOW_ICON ];
                    if ( portletIcon == null || portletIcon.length == 0 )
                    {
                        portletIcon = this._parsePSMLIcon( child );
                        if ( portletIcon != null && portletIcon.length > 0 )
                        {
                            portletProps[ jsObj.id.PP_WINDOW_ICON ] = portletIcon;
                        }
                    }
                    fragChildren.push( { id: child.getAttribute( "id" ), type: fragType, name: child.getAttribute( "name" ), properties: portletProps, actions: this._parsePSMLActions( child, null ), currentActionState: this._parsePSMLActionState( child ), currentActionMode: this._parsePSMLActionMode( child ), decorator: child.getAttribute( "decorator" ), layoutActionsDisabled: layoutActionsDisabled, documentOrderIndex: i } );
                }
            }
            else if ( childLName == "property" )
            {
                if ( this._parsePSMLProp( child, propertiesMap ) == "sizes" )
                {
                    if ( sizes != null )
                    {
                        dojo.raise( "Layout fragment has multiple sizes definitions: " + layoutNode );
                        return null;
                    }
                    if ( jsObj.prefs.printModeOnly != null )
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
    _parsePSMLActionState: function( fragmentNode )
    {
        var nodes = fragmentNode.getElementsByTagName( "state" );
        if ( nodes != null && nodes.length == 1 && nodes[0].firstChild != null )
        {
            return nodes[0].firstChild.nodeValue;
        }
        return null;
    },
    _parsePSMLActionMode: function( fragmentNode )
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
    _parsePSMLProps: function( fragmentNode, propertiesMap )
    {
        if ( propertiesMap == null )
            propertiesMap = {};
        var props = fragmentNode.getElementsByTagName( "property" );
        for( var propsIdx=0; propsIdx < props.length; propsIdx++ )
        {
            this._parsePSMLProp( props[propsIdx], propertiesMap );
        }
        return propertiesMap;
    },
    _parsePSMLProp: function( propertyNode, propertiesMap )
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
        var jsObj = jetspeed;
        var djObj = dojo;
        var allColumnsStartIndex = this.columns.length;
        var colModelResult = this._layoutCreateColsModel( layoutFragment, parentColumn, omitLayoutHeader );
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
                if ( childFrag.properties && childFrag.properties[ jsObj.id.PP_COLUMN ] >= 0 )
                {
                    if ( childFrag.properties[ jsObj.id.PP_COLUMN ] != null && childFrag.properties[ jsObj.id.PP_COLUMN ] >= 0 )
                        childFragInColIndex = childFrag.properties[ jsObj.id.PP_COLUMN ] ;
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
                        djObj.lang.mixin( clonedPortletLayout, layoutFragment );
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

    _layoutFragChildCollapse: function( layoutFragment, targetLayoutFragment )
    {
        var jsObj = jetspeed;
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
                        pFrag.properties[ jsObj.id.PP_COLUMN ] = -1;
                        pFrag.properties[ jsObj.id.PP_ROW ] = -1;
                        // BOZO:NOW: ^^ should we set to -1 or delete row & column properties ?
                        pFrag.documentOrderIndex = targetLayoutFragment.fragments.length;
                        targetLayoutFragment.fragments.push( pFrag );
                        targetLayoutFragment.otherFragIndexes.push( targetLayoutFragment.fragments.length ) ;
                    }
                }
                this._layoutFragChildCollapse( layoutChildFrag, targetLayoutFragment );
            }
        }
        return hasNestedLayouts;
    },

    _layoutCreateColsModel: function( layoutFragment, parentColumn, omitLayoutHeader )
    {
        var jsObj = jetspeed;
        this.layouts[ layoutFragment.id ] = layoutFragment;
        var addedLayoutHeaderColumn = false;
        var columnsInLayout = new Array();
        if ( jsObj.prefs.windowTiling && layoutFragment.columnSizes.length > 0 )
        {
            var subOneLast = false;
            if ( jsObj.UAie ) // IE can't deal with 100% here on any nested column - so subtract 0.1% - bug not fixed in IE7
                subOneLast = true;
            
            if ( parentColumn != null && ! omitLayoutHeader )
            {
                var layoutHeaderColModelObj = new jsObj.om.Column( 0, layoutFragment.id, ( subOneLast ? layoutFragment.columnSizesSum-0.1 : layoutFragment.columnSizesSum ), this.columns.length, layoutFragment.layoutActionsDisabled );
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
                var colModelObj = new jsObj.om.Column( i, layoutFragment.id, size, this.columns.length, layoutFragment.layoutActionsDisabled );
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
        var jsObj = jetspeed;
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
    
                if ( jsObj.debugPortletEntityIdFilter )
                {
                    if ( ! dojo.lang.inArray( jsObj.debugPortletEntityIdFilter, pFrag.id ) )
                        pFrag = null;
                }
                
                if ( pFrag != null )
                {
                    var portletPageColumnKey = "z";
                    var portletWindowExtendedProperty = pFrag.properties[ jsObj.id.PP_DESKTOP_EXTENDED ];
                    
                    var portletWindowPositionStatic = jsObj.prefs.windowTiling;
                    var portletWindowHeightToFit = jsObj.prefs.windowHeightExpand;
                    if ( portletWindowExtendedProperty != null && jsObj.prefs.windowTiling && jsObj.prefs.printModeOnly == null )
                    {
                        var extPropData = portletWindowExtendedProperty.split( jsObj.id.PP_PAIR_SEPARATOR );
                        var extProp = null, extPropLen = 0, extPropName = null, extPropValue = null, extPropFlag = false;
                        if ( extPropData != null && extPropData.length > 0 )
                        {
                            var propSeparator = jsObj.id.PP_PROP_SEPARATOR;
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
                                        if ( extPropName == jsObj.id.PP_STATICPOS )
                                            portletWindowPositionStatic = extPropFlag;
                                        else if ( extPropName == jsObj.id.PP_FITHEIGHT )
                                            portletWindowHeightToFit = extPropFlag;
                                    }
                                }
                            }
                        }
                    }
                    else if ( ! jsObj.prefs.windowTiling )
                    {
                        portletWindowPositionStatic = false;
                    }
                    pFrag.properties[ jsObj.id.PP_WINDOW_POSITION_STATIC ] = portletWindowPositionStatic;
                    pFrag.properties[ jsObj.id.PP_WINDOW_HEIGHT_TO_FIT ] = portletWindowHeightToFit;
                    
                    if ( portletWindowPositionStatic && jsObj.prefs.windowTiling )
                    {
                        var portletColumnIndex = pFrag.properties[ jsObj.id.PP_COLUMN ];
                        if ( portletColumnIndex == null || portletColumnIndex == "" || portletColumnIndex < 0 )
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
                        else if ( portletColumnIndex >= columnsInLayout.length )
                        {
                            portletColumnIndex = columnsInLayout.length -1;
                        }
                        
                        portletsByColumn[portletColumnIndex].push( pFrag.id );
                        var portletPageColumnIndex = pageColumnStartIndex + new Number( portletColumnIndex );
                        portletPageColumnKey = portletPageColumnIndex.toString();
                    }
                    var portlet = new jsObj.om.Portlet( pFrag.name, pFrag.id, null, pFrag.properties, pFrag.actions, pFrag.currentActionState, pFrag.currentActionMode, pFrag.decorator, pFrag.layoutActionsDisabled );
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

    _portletsInitWinState: function( /* Array */ portletsByPageColumnZ )
    {
        var initialColumnRowAllPortlets = {};
        this.getPortletCurColRow( null, false, initialColumnRowAllPortlets );
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
                portlet._initWinState( portletInitialColRow, false );
            else
                dojo.raise( "Window state data not found for portlet: " + portlet.getId() );
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

    _createColsStart: function( allColumnsParent )
    {
        if ( ! this.columnsStructure || this.columnsStructure.length == 0 ) return;
        var columnContainerNode = document.createElement( "div" );
        columnContainerNode.id = jetspeed.id.COLUMNS;
        columnContainerNode.setAttribute( "id", jetspeed.id.COLUMNS );
        for ( var colIndex = 0 ; colIndex < this.columnsStructure.length ; colIndex++ )
        {
            var colObj = this.columnsStructure[colIndex];
            this._createCols( colObj, columnContainerNode ) ;
        }
        allColumnsParent.appendChild( columnContainerNode );
    },

    _createCols: function( column, columnContainerNode )
    {
        column.createColumn() ;
        if ( column.columnChildren != null && column.columnChildren.length > 0 )
        {
            for ( var colIndex = 0 ; colIndex < column.columnChildren.length ; colIndex++ )
            {
                var colObj = column.columnChildren[ colIndex ];
                this._createCols( colObj, column.domNode ) ;
            }
        }
        columnContainerNode.appendChild( column.domNode );
    },
    _removeCols: function( /* DOM Node */ preserveWindowNodesInNode )
    {
        if ( ! this.columns || this.columns.length == 0 ) return;
        for ( var i = 0 ; i < this.columns.length ; i++ )
        {
            if ( this.columns[i] )
            {
                if ( preserveWindowNodesInNode )
                {
                    var windowNodesInColumn = jetspeed.ui.getPWinChildren( this.columns[i].domNode, null );
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

    getPortletCurColRow: function( /* DOM node */ justForPortletWindowNode, /* boolean */ includeGhosts, /* map */ currentColumnRowAllPortlets )
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
                    dojo.raise( "Layout not found: " + currentLayoutId ) ;
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
                    var parentColObj = this.getColFromColNode( colObj.domNode.parentNode );
                    if ( parentColObj == null )
                    {
                        dojo.raise( "Parent column not found: " + colObj ) ;
                        return null;
                    }
                    colObj = parentColObj;
                }
            }

            var colCurrentRow = null;
            var jsObj = jetspeed;
            var djObj = dojo;
            for ( var colChildIndex = 0 ; colChildIndex < colChildNodes.length ; colChildIndex++ )
            {
                var colChild = colChildNodes[colChildIndex];

                if ( djObj.html.hasClass( colChild, jsObj.id.PWIN_CLASS ) || ( includeGhosts && djObj.html.hasClass( colChild, jsObj.id.PWIN_GHOST_CLASS ) ) || ( includeLayouts && djObj.html.hasClass( colChild, "desktopColumn" ) ) )
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
                            var portletWindowWidget = this.getPWinFromNode( colChild );
                            if ( portletWindowWidget == null )
                            {
                                djObj.raise( "PortletWindow not found for node" ) ;
                            }
                            else
                            {
                                var portlet = portletWindowWidget.portlet;
                                if ( portlet == null )
                                {
                                    djObj.raise( "PortletWindow for node has null portlet: " + portletWindowWidget.widgetId ) ;
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
        var jsObj = jetspeed;
        var portletArray = this.getPortletArray();
        if ( ! portletArray ) return portletArray;
        var filteredPortletArray = [];
        for ( var i = 0 ; i < portletArray.length; i++ )
        {
            if ( ! portletArray[i].getProperty( jsObj.id.PP_WINDOW_POSITION_STATIC ) )
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
        windowState = portletA.getSavedWinState();
        aZIndex = windowState.zIndex;
        windowState = portletB.getSavedWinState();
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
        var jsObj = jetspeed;
        var pd = null;
        if ( djConfig.isDebug && jsObj.debug.windowDecorationRandom )
        {
            pd = jsObj.prefs.portletDecorationsAllowed[ Math.floor( Math.random() * jsObj.prefs.portletDecorationsAllowed.length ) ];
        }
        else
        {
            var defaultpd = this.getPortletDecorator();
            if ( dojo.lang.indexOf( jsObj.prefs.portletDecorationsAllowed, defaultpd ) != -1 )
                pd = defaultpd;
            else
                pd = jsObj.prefs.windowDecoration;
        }
        return pd;
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
            for ( var portletIndex in this.portlets )
            {
                var portlet = this.portlets[ portletIndex ];
                if ( portlet.name == portletName )
                    return portlet;
            }
        }
        return null;
    },
    getPortlet: function( /* String */ portletEntityId )
    {
        if ( this.portlets && portletEntityId )
            return this.portlets[ portletEntityId ];
        return null;
    },
    getPWinFromNode: function( /* DOM node */ portletWindowNode )
    {
        var portletWindowWidget = null;
        if ( this.portlets && portletWindowNode )
        {
            for ( var portletIndex in this.portlets )
            {
                var portlet = this.portlets[ portletIndex ];
                var portletWindow = portlet.getPWin();
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
        if ( !portlet ) return;
        if ( ! this.portlets ) this.portlets = [];
        this.portlets[ portlet.entityId ] = portlet;
        this.portlet_count++;
    },
    putPWin: function( portletWindow )
    {
        if ( !portletWindow ) return;
        var windowId = portletWindow.widgetId;
        if ( ! windowId )
            dojo.raise( "PortletWindow id is null" );
        this.portlet_windows[ windowId ] = portletWindow;
        this.portlet_window_count++;
    },
    getPWin: function( portletWindowId )
    {
        if ( this.portlet_windows && portletWindowId )
            return this.portlet_windows[ portletWindowId ];
        return null;
    },
    getPWins: function( portletsOnly )
    {
        var pWins = this.portlet_windows;
        var pWin;
        var resultpWins = [];
        for ( var windowId in pWins )
        {
            pWin = pWins[ windowId ];
            if ( pWin && ( ! portletsOnly || pWin.portlet ) )
            {
                resultpWins.push( pWin );
            }
        }
        return resultpWins;
    },
    getPWinTopZIndex: function( posStatic )
    {
        var winZIndex = 0;
        if ( posStatic )
        {
            winZIndex = this.portlet_tiled_high_z + 1;
            this.portlet_tiled_high_z = winZIndex;
        }
        else
        {
            if ( this.portlet_untiled_high_z == -1 )
                this.portlet_untiled_high_z = 200;
            winZIndex = this.portlet_untiled_high_z + 1;
            this.portlet_untiled_high_z = winZIndex;
        }
        return winZIndex;
    },
    getPWinHighZIndex: function()
    {
        return Math.max( this.portlet_tiled_high_z, this.portlet_untiled_high_z );
    },

    displayAllPWins: function( hideAll )
    {
        var portletArray = this.getPortletArray();
        for ( var i = 0; i < portletArray.length; i++ )
        {
            var portlet = portletArray[i];
            var pWin = portlet.getPWin();
            if ( pWin )
            {
                if ( hideAll )
                    pWin.domNode.style.display = "none";
                else
                    pWin.domNode.style.display = "";
            }
        }
    },
    regPWinIFrameCover: function( portletWindow )
    {
        if ( !portletWindow ) return;
        this.iframeCoverByWinId[ portletWindow.widgetId ] = true;
    },
    unregPWinIFrameCover: function( portletWindow )
    {
        if ( !portletWindow ) return;
        delete this.iframeCoverByWinId[ portletWindow.widgetId ];
    },
    displayAllPWinIFrameCovers: function( hideAll, excludePWinId )
    {
        var pWins = this.portlet_windows;
        var pWinIdsWithIFrameCover = this.iframeCoverByWinId;
        if ( ! pWins || ! pWinIdsWithIFrameCover ) return;
        for ( var pWinId in pWinIdsWithIFrameCover )
        {
            if ( pWinId == excludePWinId ) continue;
            var pWin = pWins[ pWinId ];
            var pWinIFrameCover = ( pWin && pWin.iframesInfo ? pWin.iframesInfo.iframeCover : null );
            if ( pWinIFrameCover )
            {
                if ( hideAll )
                    pWinIFrameCover.style.display = "none";
                else
                    pWinIFrameCover.style.display = "block";
            }
        }
    },

    destroy: function()
    {
        // destroy portlets
        var pWins = this.portlet_windows;
        var pWinsToClose = this.getPWins( true );
        var pWin, pWinId;
        for ( var i = 0 ; i < pWinsToClose.length ; i++ )
        {
            pWin = pWinsToClose[i];
            pWinId = pWin.widgetId;
            pWin.closeWindow();
            delete pWins[ pWinId ] ;
            this.portlet_window_count--;
        }
        this.portlets = [];
        this.portlet_count = 0;

        // destroy edit page
        var pageEditorWidget = dojo.widget.byId( jetspeed.id.PG_ED_WID );
        if ( pageEditorWidget != null )
        {
            pageEditorWidget.editPageDestroy();
        }

        // destroy columns
        this._removeCols( document.getElementById( jetspeed.id.DESKTOP ) );

        // destroy page controls
        this._destroyPageControls();
    },

    // ... columns
    getColFromColNode: function( /* DOM node */ columnNode )
    {
        if ( columnNode == null ) return null;
        var pageColumnIndexAttr = columnNode.getAttribute( "columnIndex" );
        if ( pageColumnIndexAttr == null ) return null;
        var pageColumnIndex = new Number( pageColumnIndexAttr );
        if ( pageColumnIndex >= 0 && pageColumnIndex < this.columns.length )
            return this.columns[ pageColumnIndex ];
        return null;
    },
    getColIndexForNode: function( /* DOM node */ node )
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
    getColWithNode: function( /* DOM node */ node )
    {
        var inColIndex = this.getColIndexForNode( node );
        return ( (inColIndex != null && inColIndex >= 0) ? this.columns[inColIndex] : null );
    },
    getDescendantCols: function( /* jetspeed.om.Column */ column )
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
    retrieveMenuDeclarations: function( includeMenuDefs, initiateEditMode, isPageUpdate )
    {
        contentListener = new jetspeed.om.MenusApiCL( includeMenuDefs, initiateEditMode, isPageUpdate );

        this.clearMenus();

        var queryString = "?action=getmenus";
        if ( includeMenuDefs )
            queryString += "&includeMenuDefs=true";

        var psmlMenusActionUrl = this.getPsmlUrl() + queryString;
        var mimetype = "text/xml";

        var ajaxApiContext = new jetspeed.om.Id( "getmenus", { page: this } );

        jetspeed.url.retrieveContent( { url: psmlMenusActionUrl, mimetype: mimetype }, contentListener, ajaxApiContext, jetspeed.debugContentDumpIds );
    },

    // ... page buttons
    syncPageControls: function()
    {
        var jsId = jetspeed.id;
        if ( this.actionButtons == null ) return;
        for ( var actionName in this.actionButtons )
        {
            var enabled = false;
            if ( actionName == jsId.ACT_EDIT )
            {
                if ( ! this.editMode )
                    enabled = true;
            }
            else if ( actionName == jsId.ACT_VIEW )
            {
                if ( this.editMode )
                    enabled = true;
            }
            else if ( actionName == jsId.ACT_ADDPORTLET )
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
        var jsObj = jetspeed;
        var jsId = jsObj.id;
        var djObj = dojo;
        var actionButtonNames = [];
        if ( this.actions != null )
        {
            for ( var actionName in this.actions )
            {
                if ( actionName != jsId.ACT_HELP )
                {   // ^^^ page help is currently not supported
                    actionButtonNames.push( actionName );
                }
                if ( actionName == jsId.ACT_EDIT )
                {
                    actionButtonNames.push( jsId.ACT_ADDPORTLET );
                }
            }
            if ( this.actions[ jsId.ACT_EDIT ] != null )
            {
                if ( this.actions[ jsId.ACT_VIEW ] == null )
                {
                    actionButtonNames.push( jsId.ACT_VIEW );
                }
            }
            if ( this.actions[ jsId.ACT_VIEW ] != null )
            {
                if ( this.actions[ jsId.ACT_EDIT ] == null )
                {
                    actionButtonNames.push( jsId.ACT_EDIT );
                }
            }
        }

        var pageControlsContainer = djObj.byId( jsId.PAGE_CONTROLS );
        if ( pageControlsContainer != null && actionButtonNames != null && actionButtonNames.length > 0 )
        {
            var jsPrefs = jsObj.prefs;
            var jsUI = jsObj.ui;
            var djEvtObj = djObj.event;
            var tooltipMgr = jsObj.page.tooltipMgr;
            if ( this.actionButtons == null )
            {
                this.actionButtons = {};
                this.actionButtonTooltips = [];
            }
            var actBtnTts = this.actionButtonTooltips;
            for ( var i = 0 ; i < actionButtonNames.length ; i++ )
            {
                var actionName = actionButtonNames[ i ];
                var actionButton = document.createElement( "div" );
                actionButton.className = "portalPageActionButton";
                actionButton.style.backgroundImage = "url(" + jsPrefs.getLayoutRootUrl() + "/images/desktop/" + actionName + ".gif)";
                actionButton.actionName = actionName;
                this.actionButtons[ actionName ] = actionButton;
                pageControlsContainer.appendChild( actionButton );
    
                jsUI.evtConnect( "after", actionButton, "onclick", this, "pageActionButtonClick", djEvtObj );

                if ( jsPrefs.pageActionButtonTooltip )
                {   // setting isContainer=false and fastMixIn=true to avoid recursion hell when connectId is a node (could give each an id instead)
                    var actionlabel = null;
                    if ( jsPrefs.desktopActionLabels != null )
                        actionlabel = jsPrefs.desktopActionLabels[ actionName ];
                    if ( actionlabel == null || actionlabel.length == 0 )
                        actionlabel = djObj.string.capitalize( actionName );
                    actBtnTts.push( tooltipMgr.addNode( actionButton, actionlabel, true, jsObj, jsUI, djEvtObj ) );
                }
            }
        }
    },

    _destroyPageControls: function()
    {
        var jsObj = jetspeed;
        if ( this.actionButtons )
        {
            for ( var actionName in this.actionButtons )
            {
                var actionButton = this.actionButtons[ actionName ] ;
                if ( actionButton )
                    jsObj.ui.evtDisconnect( "after", actionButton, "onclick", this, "pageActionButtonClick" );
            }
        }
        var pageControlsContainer = dojo.byId( jsObj.id.PAGE_CONTROLS );
        if ( pageControlsContainer != null && pageControlsContainer.childNodes && pageControlsContainer.childNodes.length > 0 )
        {
            for ( var i = (pageControlsContainer.childNodes.length -1) ; i >= 0 ; i-- )
            {
                dojo.dom.removeNode( pageControlsContainer.childNodes[i] );
            }
        }
        jsObj.page.tooltipMgr.removeNodes( this.actionButtonTooltips );
        this.actionButtonTooltips = null;

        this.actionButtons == null;
    },
    pageActionButtonClick: function( evt )
    {
        if ( evt == null || evt.target == null ) return;
        this.pageActionProcess( evt.target.actionName, evt );
    },
    pageActionProcess: function( /* String */ actionName )
    {
        var jsObj = jetspeed;
        if ( actionName == null ) return;
        if ( actionName == jsObj.id.ACT_ADDPORTLET )
        {
            this.addPortletInitiate();
        }
        else if ( actionName == jsObj.id.ACT_EDIT )
        {
            jsObj.editPageInitiate();
        }
        else if ( actionName == jsObj.id.ACT_VIEW )
        {
            jsObj.editPageTerminate();
        }
        else
        {
            var action = this.getPageAction( actionName );
            alert( "pageAction " + actionName + " : " + action );
            if ( action == null ) return;
            if ( action.url == null ) return;
            var pageActionUrl = jsObj.url.basePortalUrl() + jsObj.url.path.DESKTOP + "/" + action.url;
            jsObj.pageNavigate( pageActionUrl );
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
        var jsObj = jetspeed;
        if ( ! jspage )
            jspage = escape( this.getPagePathAndQuery() );
        else
            jspage = escape( jspage );
        var addportletPageUrl = jsObj.url.basePortalUrl() + jsObj.url.path.DESKTOP + "/system/customizer/selector.psml?jspage=" + jspage;
        if ( layoutId != null )
            addportletPageUrl += "&jslayoutid=" + escape( layoutId );
        jsObj.changeActionForPortlet( this.rootFragmentId, null, jsObj.id.ACT_EDIT, new jetspeed.om.PageChangeActionCL( addportletPageUrl ) );
    },

    // ... edit mode
    setPageModePortletActions: function( /* Portlet */ portlet )
    {
        if ( portlet == null || portlet.actions == null ) return;
        var jsId = jetspeed.id;
        if ( portlet.actions[ jsId.ACT_REMOVEPORTLET ] == null )
        {
            portlet.actions[ jsId.ACT_REMOVEPORTLET ] = { id: jsId.ACT_REMOVEPORTLET };
        }
    },

    // ... page url access

    getPageUrl: function( forPortal )
    {
        if ( this.pageUrl != null && ! forPortal )
            return this.pageUrl;
        var jsU = jetspeed.url;
        var pageUrl = jsU.path.SERVER + ( ( forPortal ) ? jsU.path.PORTAL : jsU.path.DESKTOP ) + this.getPath();
        var pageUrlObj = jsU.parse( pageUrl );
        var docUrlObj = null;
        if ( this.pageUrlFallback != null )
            docUrlObj = jsU.parse( this.pageUrlFallback );
        else
            docUrlObj = jsU.parse( window.location.href );
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
        var jsU = jetspeed.url;
        var pagePath = this.getPath();
        var pagePathObj = jsU.parse( pagePath );
        var docUrlObj = null;
        if ( this.pageUrlFallback != null )
            docUrlObj = jsU.parse( this.pageUrlFallback );
        else
            docUrlObj = jsU.parse( window.location.href );
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
        var jsU = jetspeed.url;
        if ( ! jsU.urlStartsWithHttp( pathOrUrl ) )
            return jsU.path.SERVER + jsU.path.DESKTOP + pathOrUrl;
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
}); // jetspeed.om.Page

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
    getDescendantCols: function()
    {
        return jetspeed.page.getDescendantCols( this );
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
}); // jetspeed.om.Column

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
            this.properties[ jetspeed.id.PP_WINDOW_DECORATION ] = decorator;
        }
    }

    this.layoutActionsDisabled = false ;
    if ( typeof layoutActionsDisabled != "undefined" )
        this.layoutActionsDisabled = layoutActionsDisabled ;
};
dojo.lang.extend( jetspeed.om.Portlet,
{
    name: null,
    entityId: null,
    isPortlet: true,

    pageColumnIndex: null,
    
    contentRetriever: new jetspeed.om.PortletContentRetriever(),
    
    windowFactory: null,

    lastSavedWindowState: null,
    
    initialize: function()
    {   // must be called once init sensitive properties are in place
        var jsObj = jetspeed;
        var jsId = jsObj.id;
        if ( ! this.properties[ jsId.PP_WIDGET_ID ] )
        {
            this.properties[ jsId.PP_WIDGET_ID ] = jsId.PW_ID_PREFIX + this.entityId;
        }
        if ( ! this.properties[ jsId.PP_CONTENT_RETRIEVER ] )
        {
            this.properties[ jsId.PP_CONTENT_RETRIEVER ] = this.contentRetriever;
        }

        var posStatic = this.properties[ jsId.PP_WINDOW_POSITION_STATIC ];
        if ( jsObj.prefs.windowTiling )
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
        this.properties[ jsId.PP_WINDOW_POSITION_STATIC ] = posStatic;

        var heightToFit = this.properties[ jsId.PP_WINDOW_HEIGHT_TO_FIT ];
        if ( heightToFit == "true" )
            heightToFit = true;
        else if ( posStatic == "false" )
            heightToFit = false;
        else if ( heightToFit != true && heightToFit != false )
            heightToFit = true;
        this.properties[ jsId.PP_WINDOW_HEIGHT_TO_FIT ] = heightToFit;

        var windowtitle = this.properties[ jsId.PP_WINDOW_TITLE ];
        if ( ! windowtitle && this.name )
        {
            var re = (/^[^:]*:*/);
            windowtitle = this.name.replace( re, "" );
            this.properties[ jsId.PP_WINDOW_TITLE ] = windowtitle;
        }
    },

    postParseAnnotateHtml: function( /* DOMNode */ containerNode )
    {   
        var jsObj = jetspeed;
        var jsPUrl = jsObj.portleturl;
        if ( containerNode )
        {
            var cNode = containerNode;
            var formList = cNode.getElementsByTagName( "form" );
            var debugOn = jsObj.debug.postParseAnnotateHtml;
            var disableAnchorConversion = jsObj.debug.postParseAnnotateHtmlDisableAnchors;
            if ( formList )
            {
                for ( var i = 0 ; i < formList.length ; i++ )
                {
                    var cForm = formList[i];                    
                    var cFormAction = cForm.action;

                    var parsedPseudoUrl = jsPUrl.parseContentUrl( cFormAction );
                    
                    var submitOperation = parsedPseudoUrl.operation;

                    if ( submitOperation == jsPUrl.PORTLET_REQUEST_ACTION || submitOperation == jsPUrl.PORTLET_REQUEST_RENDER )
                    {
                        //var replacementActionUrl = parsedPseudoUrl.url; 
                        var replacementActionUrl = jsPUrl.genPseudoUrl( parsedPseudoUrl, true );
                        cForm.action = replacementActionUrl;

                        var formBind = new jsObj.om.ActionRenderFormBind( cForm, parsedPseudoUrl.url, parsedPseudoUrl.portletEntityId, submitOperation );
                        //  ^^^ formBind serves as an event hook up - retained ref is not needed
                        
                        if ( debugOn )
                            dojo.debug( "postParseAnnotateHtml [" + this.entityId + "] adding FormBind (" + submitOperation + ") for form with action: " + cFormAction );
                    }
                    else if ( cFormAction == null || cFormAction.length == 0 )
                    {
                        var formBind = new jsObj.om.ActionRenderFormBind( cForm, null, this.entityId, null );
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
                    
                    var parsedPseudoUrl = jsPUrl.parseContentUrl( aHref );
                    var replacementHref = null;
                    if ( ! disableAnchorConversion )
                        replacementHref = jsPUrl.genPseudoUrl( parsedPseudoUrl );

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

    getPWin: function()
    {
        var jsObj = jetspeed;
        var windowWidgetId = this.properties[ jsObj.id.PP_WIDGET_ID ];
        if ( windowWidgetId )
            return jsObj.page.getPWin( windowWidgetId );
        return null;
    },
    
    getCurWinState: function( volatileOnly )
    {
        var windowWidget = this.getPWin();
        if ( ! windowWidget ) return null;
        var currentState = windowWidget.getCurWinStateForPersist( volatileOnly );
        if ( ! volatileOnly )
        {
            if ( currentState.layout == null )   // should happen only if windowPositionStatic == false
                currentState.layout = this.lastSavedWindowState.layout;
        }
        return currentState;
    },
    getSavedWinState: function()
    {
        if ( ! this.lastSavedWindowState )
            dojo.raise( "Portlet not initialized: " + this.name );
        return this.lastSavedWindowState;
    },
    getInitialWinDims: function( dimensionsObj, reset )
    {
        var jsObj = jetspeed;
        var jsId = jsObj.id;
        if ( ! dimensionsObj )
            dimensionsObj = {};

        var windowPositionStatic = this.properties[ jsId.PP_WINDOW_POSITION_STATIC ];
        var windowHeightToFit = this.properties[ jsId.PP_WINDOW_HEIGHT_TO_FIT ];
        
        dimensionsObj[ jsId.PP_WINDOW_POSITION_STATIC ] = windowPositionStatic;
        dimensionsObj[ jsId.PP_WINDOW_HEIGHT_TO_FIT ] = windowHeightToFit;
        
        var portletWidth = this.properties[ "width" ];
        if ( ! reset && portletWidth != null && portletWidth > 0 )
            dimensionsObj.width = Math.floor( portletWidth );
        else if ( reset )
            dimensionsObj.width = -1;
    
        var portletHeight = this.properties[ "height" ];
        if ( ! reset && portletHeight != null && portletHeight > 0  )
            dimensionsObj.height = Math.floor( portletHeight );
        else if ( reset )
            dimensionsObj.height = -1;

        if ( ! windowPositionStatic || ! jsObj.prefs.windowTiling )
        {
            var portletLeft = this.properties[ "x" ];
            if ( ! reset && portletLeft != null && portletLeft >= 0 )
                dimensionsObj.left = Math.floor( ( (portletLeft > 0) ? portletLeft : 0 ) );
            else if ( reset )
                dimensionsObj.left = -1;

            var portletTop = this.properties[ "y" ];
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
    _initWinState: function( portletInitialColRow, /* boolean */ reset )
    {   // portletInitialColRow: { layout: currentLayoutId, column: colObj.getLayoutColumnIndex(), row: colCurrentRow }
        var jsObj = jetspeed;
        var initialWindowState = ( portletInitialColRow ? portletInitialColRow : {} );    // BOZO:NOW: support reset argument (?)
        
        this.getInitialWinDims( initialWindowState, reset );

        if ( jsObj.debug.initWinState )
        {
            var windowPositionStatic = this.properties[ jsObj.id.PP_WINDOW_POSITION_STATIC ];
            if ( ! windowPositionStatic || ! jsObj.prefs.windowTiling )
                dojo.debug( "initWinState [" + this.entityId + "] z=" + initialWindowState.zIndex + " x=" + initialWindowState.left + " y=" + initialWindowState.top + " width=" + initialWindowState.width + " height=" + initialWindowState.height );
            else
                dojo.debug( "initWinState [" + this.entityId + "] column=" + initialWindowState.column + " row=" + initialWindowState.row + " width=" + initialWindowState.width + " height=" + initialWindowState.height );
        }

        this.lastSavedWindowState = initialWindowState;

        return initialWindowState;
    },
    _getInitialZIndex: function( /* boolean */ reset )
    {
        var zIndex = null;
        var portletZIndex = this.properties[ "z" ];
        if ( ! reset && portletZIndex != null && portletZIndex >= 0 )
            zIndex = Math.floor( portletZIndex );
        else if ( reset )
            zIndex = -1;
        return zIndex;
    },
    _getChangedWindowState: function( /* boolean */ volatileOnly )
    {
        var jsId = jetspeed.id;
        var lastSaved = this.getSavedWinState();
        
        if ( lastSaved && dojo.lang.isEmpty( lastSaved ) )
        {
            lastSaved = null;
            volatileOnly = false;  // so that current state we obtain is the full representation
        }
        
        var currentState = this.getCurWinState( volatileOnly );
        var windowPositionStatic = currentState[ jsId.PP_WINDOW_POSITION_STATIC ];
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

                if ( stateKey == jsId.PP_WINDOW_POSITION_STATIC || stateKey == jsId.PP_WINDOW_HEIGHT_TO_FIT )
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

    getPortletUrl: function( bindArgs )
    {
        var jsObj = jetspeed;
        var jsUrl = jsObj.url;
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
            modUrl = jsUrl.basePortalUrl() + jsUrl.path.PORTLET + jsObj.page.getPath();

        if ( ! bindArgs.dontAddQueryArgs )
        {
            modUrl = jsUrl.parse( modUrl );
            modUrl = jsUrl.addQueryParameter( modUrl, "entity", this.entityId, true );
            modUrl = jsUrl.addQueryParameter( modUrl, "portlet", this.name, true );
            modUrl = jsUrl.addQueryParameter( modUrl, "encoder", "desktop", true );
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

    _submitAjaxApi: function( /* String */ action, /* String */ queryStringFragment, contentListener )
    {
        var jsObj = jetspeed;
        var queryString = "?action=" + action + "&id=" + this.entityId + queryStringFragment;

        var psmlMoveActionUrl = jsObj.url.basePortalUrl() + jsObj.url.path.AJAX_API + jsObj.page.getPath() + queryString;
        var mimetype = "text/xml";

        var ajaxApiContext = new jsObj.om.Id( action, this.entityId );
        ajaxApiContext.portlet = this;

        jsObj.url.retrieveContent( { url: psmlMoveActionUrl, mimetype: mimetype }, contentListener, ajaxApiContext, null );
    },

    submitWinState: function( /* boolean */ volatileOnly, /* boolean */ reset )
    {
        var jsObj = jetspeed;
        var jsId = jsObj.id;
        var changedStateResult = null;
        if ( reset )
            changedStateResult = { state: this._initWinState( null, true ) };
        else
            changedStateResult = this._getChangedWindowState( volatileOnly );
        if ( changedStateResult )
        {
            var changedState = changedStateResult.state;
            
            var windowPositionStatic = changedState[ jsId.PP_WINDOW_POSITION_STATIC ];
            var windowHeightToFit = changedState[ jsId.PP_WINDOW_HEIGHT_TO_FIT ];

            var windowExtendedProperty = null;
            if ( changedStateResult.extendedPropChanged )
            {
                var propSep = jsId.PP_PROP_SEPARATOR;
                var pairSep = jsId.PP_PAIR_SEPARATOR;
                windowExtendedProperty = jsId.PP_STATICPOS + propSep + windowPositionStatic.toString();
                windowExtendedProperty += pairSep + jsId.PP_FITHEIGHT + propSep + windowHeightToFit.toString();
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
                queryStringFragment += "&" + jsId.PP_DESKTOP_EXTENDED + "=" + windowExtendedProperty;

            this._submitAjaxApi( action, queryStringFragment, new jsObj.om.MoveApiCL( this, changedState ) );

            if ( ! volatileOnly && ! reset )
            {
                if ( ! windowPositionStatic && changedStateResult.zIndexChanged )  // current condition for whether 
                {                                                                  // volatile (zIndex) changes are possible
                    var portletArray = jsObj.page.getPortletArray();
                    if ( portletArray && ( portletArray.length -1 ) > 0 )
                    {
                        for ( var i = 0 ; i < portletArray.length ; i++ )
                        {
                            var tPortlet = portletArray[i];
                            if ( tPortlet && tPortlet.entityId != this.entityId )
                            {
                                if ( ! tPortlet.properties[ jsObj.id.PP_WINDOW_POSITION_STATIC ] )
                                    tPortlet.submitWinState( true );
                            }
                        }
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
            contentListener = new jetspeed.om.PortletCL( this, suppressGetActions, bindArgs );

        if ( ! bindArgs )
            bindArgs = {};
        
        var portlet = this ;
        portlet.getPortletUrl( bindArgs ) ;
        
        this.contentRetriever.getContent( bindArgs, contentListener, portlet, jetspeed.debugContentDumpIds );
    },
    setPortletContent: function( portletContent, renderUrl, portletTitle )
    {
        var windowWidget = this.getPWin();
        if ( portletTitle != null && portletTitle.length > 0 )
        {
            this.properties[ jetspeed.id.PP_WINDOW_TITLE ] = portletTitle;
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
        var jsId = jetspeed.id;
        var actionlabel1 = this._getLoadingActionLabel( jsId.ACT_LOAD_RENDER );
        var actionlabel2 = this._getLoadingActionLabel( jsId.ACT_LOAD_ACTION );
        var actionlabel3 = this._getLoadingActionLabel( jsId.ACT_LOAD_UPDATE );
        var windowWidget = this.getPWin();
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
            var windowWidget = this.getPWin();
            if ( windowWidget && actionlabel )
            {
                windowWidget.setPortletTitle( actionlabel );
            }
        }
    },
    loadingIndicatorHide: function()
    {
        var windowWidget = this.getPWin();
        if ( windowWidget )
        {
            windowWidget.setPortletTitle( this.properties[ jetspeed.id.PP_WINDOW_TITLE ] );
        }
    },

    getId: function()  // jetspeed.om.Id protocol
    {
        return this.entityId;
    },

    getProperty: function( name )
    {
        return this.properties[ name ];
    },
    getProperties: function()
    {
        return this.properties;
    },

    renderAction: function( actionName, actionUrlOverride )
    {
        var jsObj = jetspeed;
        var jsUrl = jsObj.url;
        var action = null;
        if ( actionName != null )
            action = this.getAction( actionName );
        var actionUrl = actionUrlOverride;
        if ( actionUrl == null && action != null )
            actionUrl = action.url;
        if ( actionUrl == null ) return;
        var renderActionUrl = jsUrl.basePortalUrl() + jsUrl.path.PORTLET + "/" + actionUrl + jsObj.page.getPath();
        if ( actionName != jsObj.id.ACT_PRINT )
            this.retrieveContent( null, { url: renderActionUrl } );
        else
        {
            var printmodeUrl = jsObj.page.getPageUrl();
            printmodeUrl = jsUrl.addQueryParameter( printmodeUrl, "jsprintmode", "true" );
            printmodeUrl = jsUrl.addQueryParameter( printmodeUrl, "jsaction", escape( action.url ) );
            printmodeUrl = jsUrl.addQueryParameter( printmodeUrl, "jsentity", this.entityId );
            printmodeUrl = jsUrl.addQueryParameter( printmodeUrl, "jslayoutid", this.lastSavedWindowState.layout );
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
        var windowWidget = this.getPWin();
        if ( windowWidget )
        {
            windowWidget.windowActionButtonSync();
        }
    }
}); // jetspeed.om.Portlet

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
    init: function(args)
    {
        var form = dojo.byId( args.formNode );

        if(!form || !form.tagName || form.tagName.toLowerCase() != "form") {
            throw new Error("FormBind: Couldn't apply, invalid form");
        } else if(this.form == form) {
            return;
        } else if(this.form) {
            throw new Error("FormBind: Already applied to a form");
        }

        dojo.lang.mixin(this.bindArgs, args);
        this.form = form;

        this.eventConfMgr( false );

        form.oldSubmit = form.submit;  // Isn't really used anymore, but cache it
        form.submit = function()
        {
            form.onsubmit();
        };
    },

    eventConfMgr: function( disconnect )
    {
        var fn = (disconnect) ? "disconnect" : "connect";
        var djEvt = dojo.event;
        var form = this.form;
        djEvt[ fn ]( "after", form, "onsubmit", this, "submit", null );

        for(var i = 0; i < form.elements.length; i++) {
            var node = form.elements[i];
            if(node && node.type && dojo.lang.inArray(["submit", "button"], node.type.toLowerCase())) {
                djEvt[ fn ]( "after", node, "onclick", this, "click", null );
            }
        }

        var inputs = form.getElementsByTagName("input");
        for(var i = 0; i < inputs.length; i++) {
            var input = inputs[i];
            if(input.type.toLowerCase() == "image" && input.form == form) {
                djEvt[ fn ]( "after", input, "onclick", this, "click", null );
            }
        }

        var as = form.getElementsByTagName("a");
        for(var i = 0; i < as.length; i++) {
            djEvt[ fn ]( "before", as[i], "onclick", this, "click", null );
        }
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
            var parsedPseudoUrl = jetspeed.portleturl.parseContentUrl( this.form.action );

            var mixInBindArgs = {};
            if ( parsedPseudoUrl.operation == jetspeed.portleturl.PORTLET_REQUEST_ACTION || parsedPseudoUrl.operation == jetspeed.portleturl.PORTLET_REQUEST_RENDER )
            {   // form action set via script
                var replacementActionUrl = jetspeed.portleturl.genPseudoUrl( parsedPseudoUrl, true );
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


// ... jetspeed.om.PortletCL
jetspeed.om.PortletCL = function( /* Portlet */ portlet, suppressGetActions, bindArgs )
{
    this.portlet = portlet;
    this.suppressGetActions = suppressGetActions;
    this.formbind = null;
    if ( bindArgs != null && bindArgs.submitFormBindObject != null )
    {
        this.formbind = bindArgs.submitFormBindObject;
    }
    this._loading( true );
};
jetspeed.om.PortletCL.prototype =
{
    _loading: function( /* boolean */ showLoading )
    {
        if ( this.portlet == null ) return;
        if ( showLoading )
            this.portlet.loadingIndicatorShow( jetspeed.id.ACT_LOAD_RENDER );
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
            this._loading( false );
        if ( this.formbind != null )
        {
            this.formbind.isFormSubmitInProgress( false );
        }
    },
    notifyFailure: function( /* String */ type, /* Object */ error, /* String */ requestUrl, /* Portlet */ portlet )
    {
        this._loading( false );
        if ( this.formbind != null )
        {
            this.formbind.isFormSubmitInProgress( false );
        }
        dojo.raise( "PortletCL notifyFailure url: " + requestUrl + " type: " + type + jetspeed.url.formatBindError( error ) );
    }
};

// ... jetspeed.om.PortletActionCL
jetspeed.om.PortletActionCL = function( /* Portlet */ portlet, bindArgs )
{
    this.portlet = portlet;
    this.formbind = null;
    if ( bindArgs != null && bindArgs.submitFormBindObject != null )
    {
        this.formbind = bindArgs.submitFormBindObject;
    }
    this._loading( true );
};
jetspeed.om.PortletActionCL.prototype =
{
    _loading: function( /* boolean */ showLoading )
    {
        if ( this.portlet == null ) return;
        if ( showLoading )
            this.portlet.loadingIndicatorShow( jetspeed.id.ACT_LOAD_ACTION );
        else
            this.portlet.loadingIndicatorHide();
    },
    notifySuccess: function( /* String */ portletContent, /* String */ requestUrl, /* Portlet */ portlet, http )
    {
        var jsObj = jetspeed;
        var renderUrl = null;
        var navigatedPage = false;
        var parsedPseudoUrl = jsObj.portleturl.parseContentUrl( portletContent );
        if ( parsedPseudoUrl.operation == jsObj.portleturl.PORTLET_REQUEST_ACTION || parsedPseudoUrl.operation == jsObj.portleturl.PORTLET_REQUEST_RENDER )
        {
            if ( jsObj.debug.doRenderDoAction )
                dojo.debug( "PortletActionCL " + parsedPseudoUrl.operation + "-url in response body: " + portletContent + "  url: " + parsedPseudoUrl.url + " entity-id: " + parsedPseudoUrl.portletEntityId ) ;
            renderUrl = parsedPseudoUrl.url;
        }
        else
        {
            if ( jsObj.debug.doRenderDoAction )
                dojo.debug( "PortletActionCL other-url in response body: " + portletContent )
            renderUrl = portletContent;
            if ( renderUrl )
            {
                var portletUrlPos = renderUrl.indexOf( jsObj.url.basePortalUrl() + jsObj.url.path.PORTLET );
                if ( portletUrlPos == -1 )
                {
                    //dojo.debug( "PortletActionCL window.location.href navigation=" + renderUrl );
                    navigatedPage = true;
                    window.location.href = renderUrl;
                    renderUrl = null;
                }
                else if ( portletUrlPos > 0 )
                {
                    this._loading( false );
                    dojo.raise( "Cannot interpret portlet url in action response: " + portletContent );
                    renderUrl = null;
                }
            }
        }
        if ( renderUrl != null )
        {
            if ( jsObj.debug.doRenderDoAction )
                dojo.debug( "PortletActionCL starting portlet-renderer with renderUrl=" + renderUrl );
            var renderer = new jetspeed.PortletRenderer( false, false, false, renderUrl, true );
            renderer.renderAll();
        }
        else
        {
            this._loading( false );
        }
        if ( ! navigatedPage && this.portlet )
            jsObj.getActionsForPortlet( this.portlet.entityId );
        if ( this.formbind != null )
        {
            this.formbind.isFormSubmitInProgress( false );
        }
    },
    notifyFailure: function( /* String */ type, /* Object */ error, /* String */ requestUrl, /* Portlet */ portlet )
    {
        this._loading( false );
        if ( this.formbind != null )
        {
            this.formbind.isFormSubmitInProgress( false );
        }
        dojo.raise( "PortletActionCL notifyFailure type: " + type + jetspeed.url.formatBindError( error ) );
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
                dojo.raise( "Menu.getOptionByIndex index out of bounds" );
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

// ... jetspeed.om.MenuApiCL
jetspeed.om.MenuApiCL = function()
{
};
dojo.lang.extend( jetspeed.om.MenuApiCL,
{
    notifySuccess: function( /* XMLDocument */ data, /* String */ requestUrl, domainModelObject )
    {
        var menuObj = this.parseMenu( data, domainModelObject.menuName, domainModelObject.menuType );
        domainModelObject.page.putMenu( menuObj );
    },
    notifyFailure: function( /* String */ type, /* Object */ error, /* String */ requestUrl, domainModelObject )
    {
        this.notifyCount++;
        dojo.raise( "MenuApiCL error [" + domainModelObject.toString() + "] url: " + requestUrl + " type: " + type + jetspeed.url.formatBindError( error ) );
    },

    parseMenu: function( /* XMLNode */ node, /* String */ menuName, /* String */ menuType )
    {
        var menu = null;
        var jsElements = node.getElementsByTagName( "js" );
        if ( ! jsElements || jsElements.length > 1 )
            dojo.raise( "Expected one <js> in menu xml" );
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
                    dojo.raise( "Expected one root <menu> in menu xml" );
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
                    dojo.raise( "Unexpected nested <menu>" );
                else
                    mObj.addOption( this.parseMenuObject( child, new jetspeed.om.Menu() ) );
            }
            else if ( childLName == "option" )
            {
                if ( mObj.isLeaf() )
                    dojo.raise( "Unexpected nested <option>" );
                else
                    mObj.addOption( this.parseMenuObject( child, new jetspeed.om.MenuOption() ) );
            }
            else if ( childLName == "separator" )
            {
                if ( mObj.isLeaf() )
                    dojo.raise( "Unexpected nested <separator>" );
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

// ... jetspeed.om.MenusApiCL
jetspeed.om.MenusApiCL = function( /* boolean */ includeMenuDefs, /* boolean */ initiateEditMode, /* boolean */ isPageUpdate )
{
    this.includeMenuDefs = includeMenuDefs;
    this.initiateEditMode = initiateEditMode;
    this.isPageUpdate = isPageUpdate ;
};
dojo.inherits( jetspeed.om.MenusApiCL, jetspeed.om.MenuApiCL);
dojo.lang.extend( jetspeed.om.MenusApiCL,
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
        dojo.raise( "MenusApiCL error [" + domainModelObject.toString() + "] url: " + requestUrl + " type: " + type + jetspeed.url.formatBindError( error ) );
    },

    notifyFinished: function( domainModelObject )
    {
        var jsObj = jetspeed;
        if ( this.includeMenuDefs )
            jsObj.notifyRetrieveAllMenusFinished();
        if ( this.initiateEditMode )
            jsObj.editPageInitiate();
        if ( this.isPageUpdate )
            jsObj.updatePageEnd();

        if ( djConfig.isDebug && jsObj.debug.profile )
        {
            dojo.profile.end( "loadFromPSML" );
            if ( ! this.isPageUpdate )
                dojo.profile.end( "initializeDesktop" );
            else
                dojo.profile.end( "updatePage" );
            dojo.profile.debugAllItems( true );
            dojo.debug( "-------------------------" );
        }
    }
});

// ... jetspeed.om.PortletChangeActionCL
jetspeed.om.PortletChangeActionCL = function( /* String */ portletEntityId )
{
    this.portletEntityId = portletEntityId;
    this._loading( true );
};
dojo.lang.extend( jetspeed.om.PortletChangeActionCL,
{
    notifySuccess: function( /* XMLDocument */ data, /* String */ requestUrl, domainModelObject )
    {
        if ( jetspeed.url.checkAjaxApiResponse( requestUrl, data, true, "portlet-change-action" ) )
            jetspeed.getActionsForPortlet( this.portletEntityId );
        else
            this._loading( false );
    },
    _loading: function( /* boolean */ showLoading )
    {
        var portlet = jetspeed.page.getPortlet( this.portletEntityId ) ;
        if ( portlet )
        {
            if ( showLoading )
                portlet.loadingIndicatorShow( jetspeed.id.ACT_LOAD_UPDATE );
            else
                portlet.loadingIndicatorHide();
        }
    },
    notifyFailure: function( /* String */ type, /* Object */ error, /* String */ requestUrl, domainModelObject )
    {
        this._loading( false );
        dojo.raise( "PortletChangeActionCL error [" + domainModelObject.toString() + "] url: " + requestUrl + " type: " + type + jetspeed.url.formatBindError( error ) );
    }
});

// ... jetspeed.om.PageChangeActionCL
jetspeed.om.PageChangeActionCL = function( /* String */ pageActionUrl )
{
    this.pageActionUrl = pageActionUrl;
};
dojo.lang.extend( jetspeed.om.PageChangeActionCL,
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
        dojo.raise( "PageChangeActionCL error [" + domainModelObject.toString() + "] url: " + requestUrl + " type: " + type + jetspeed.url.formatBindError( error ) );
    }
});

// ... jetspeed.om.PortletActionsCL
jetspeed.om.PortletActionsCL = function( /* String[] */ portletEntityIds )
{
    this.portletEntityIds = portletEntityIds;
    this._loading( true );
};
dojo.lang.extend( jetspeed.om.PortletActionsCL,
{
    _loading: function( /* boolean */ showLoading )
    {
        if ( this.portletEntityIds == null || this.portletEntityIds.length == 0 ) return ;
        for ( var i = 0 ; i < this.portletEntityIds.length ; i++ )
        {
            var portlet = jetspeed.page.getPortlet( this.portletEntityIds[i] ) ;
            if ( portlet )
            {
                if ( showLoading )
                    portlet.loadingIndicatorShow( jetspeed.id.ACT_LOAD_UPDATE );
                else
                    portlet.loadingIndicatorHide();
            }
        }
    },
    notifySuccess: function( /* XMLDocument */ data, /* String */ requestUrl, domainModelObject )
    {
        this._loading( false );
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
            dojo.raise( "Expected one <js> in portlet selector xml" );
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
            var currentActionState = jetspeed.page._parsePSMLActionState( node );
            var currentActionMode = jetspeed.page._parsePSMLActionMode( node );
            return { id: portletId, actions: actions, currentActionState: currentActionState, currentActionMode: currentActionMode };
        }
        return null;
    },
    notifyFailure: function( /* String */ type, /* Object */ error, /* String */ requestUrl, domainModelObject )
    {
        this._loading( false );
        dojo.raise( "PortletActionsCL error [" + domainModelObject.toString() + "] url: " + requestUrl + " type: " + type + jetspeed.url.formatBindError( error ) );
    }
});

// ... jetspeed.om.MoveApiCL
jetspeed.om.MoveApiCL = function( /* Portlet */ portlet, changedState )
{
    this.portlet = portlet;
    this.changedState = changedState;
    this._loading( true );
};
jetspeed.om.MoveApiCL.prototype =
{
    _loading: function( /* boolean */ showLoading )
    {
        if ( this.portlet == null ) return;
        if ( showLoading )
            this.portlet.loadingIndicatorShow( jetspeed.id.ACT_LOAD_UPDATE );
        else
            this.portlet.loadingIndicatorHide();
    },
    notifySuccess: function( /* String */ data, /* String */ requestUrl, domainModelObject )
    {
        this._loading( false );
        dojo.lang.mixin( domainModelObject.portlet.lastSavedWindowState, this.changedState );
        var reportError = false;
        if ( djConfig.isDebug && jetspeed.debug.submitWinState )
            reportError = true;
        jetspeed.url.checkAjaxApiResponse( requestUrl, data, reportError, ("move-portlet [" + domainModelObject.portlet.entityId + "]"), jetspeed.debug.submitWinState );
    },
    notifyFailure: function( /* String */ type, /* Object */ error, /* String */ requestUrl, domainModelObject )
    {
        this._loading( false );
        dojo.debug( "submitWinState error [" + domainModelObject.entityId + "] url: " + requestUrl + " type: " + type + jetspeed.url.formatBindError( error ) );
    }
};

// ... jetspeed.ui methods

jetspeed.ui = {
    initCssObj: function()
    {
        var cssBase = [ "display: ", "block", ";",
                        " cursor: ", "default", ";",
                        " width: ", "", "", ";",
                        "", "", "" ];

        var cssHeight = cssBase.concat( [ " height: ", "", "", ";" ] );
    
        var cssOverflow = cssHeight.concat( [ " overflow-y: ", "", ";",
                                              " overflow-x: ", "hidden", ";" ] );
    
        var cssPosition = cssOverflow.concat( [ " position: ", "static", ";",
                                                " top: ", "auto", "", ";",
                                                " left: ", "auto", "", ";",
                                                " z-index: ", "", ";" ] );
    
        jetspeed.css = { cssBase: cssBase,
                         cssHeight: cssHeight,
                         cssOverflow: cssOverflow,
                         cssPosition: cssPosition,
                         cssDis: 1,
                         cssCur: 4,
                         cssW: 7,
                         cssWU: 8,
                         cssNoSelNm: 10,
                         cssNoSel: 11,
                         cssNoSelEnd: 12,
                         cssH: 14,
                         cssHU: 15,
                         cssOy: 18,
                         cssOx: 21,
                         cssPos: 24,
                         cssT: 27,
                         cssTU: 28,
                         cssL: 31,
                         cssLU: 32,
                         cssZIndex: 35 };
    },
    
    getPWinChildren: function( /* DOM node */ parentNode, /* DOM node */ matchNodeIfFound, /* boolean */ includeGhosts, /* boolean */ includeGhostsOnly )
    {
        if ( includeGhosts || includeGhostsOnly )
            includeGhosts = true;
    
        var djH = dojo.html;
        var jsId = jetspeed.id;
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
                    if ( ( ! includeGhostsOnly && djH.hasClass( child, jsId.PWIN_CLASS ) ) || ( includeGhosts && djH.hasClass( child, jsId.PWIN_GHOST_CLASS ) ) )
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
    },
    
    getPWinsFromNodes: function( /* DOM node [] */ portletWindowNodes )
    {
        var jsPage = jetspeed.page;
        var portletWindows = null;
        if ( portletWindowNodes )
        {
            portletWindows = new Array();
            for ( var i = 0 ; i < portletWindowNodes.length ; i++ )
            {
                var widget = jsPage.getPWin( portletWindowNodes[ i ].id );
                if ( widget )
                    portletWindows.push( widget ) ;
            }
        }
        return portletWindows;
    },
    
    createPortletWindow: function( windowConfigObject, columnIndex, jsObj )
    {
        var dbProfile = false;
        if ( djConfig.isDebug && jsObj.debug.profile )
        {
            dbProfile = true;
    	    dojo.profile.start( "createPortletWindow" );
        }
    
        var winPositionStatic = ( columnIndex != null );    
        var windowMakeAbsolute = false;
        var windowContainerNode = null;
        if ( winPositionStatic && columnIndex < jsObj.page.columns.length && columnIndex >= 0 )
            windowContainerNode = jsObj.page.columns[ columnIndex ].domNode;
        if ( windowContainerNode == null )
        {
            windowMakeAbsolute = true;
            windowContainerNode = document.getElementById( jsObj.id.DESKTOP );
        }
        if ( windowContainerNode == null ) return;
    
        var createWindowParams = {};
        if ( windowConfigObject.isPortlet )
        {
            createWindowParams.portlet = windowConfigObject;
            if ( jsObj.prefs.printModeOnly != null )
                createWindowParams.printMode = true;
            if ( windowMakeAbsolute )
                windowConfigObject.properties[ jsObj.id.PP_WINDOW_POSITION_STATIC ] = false ;
        }
        else
        {
            var pwP = jsObj.widget.PortletWindow.prototype.altInitParamsDef( createWindowParams, windowConfigObject );
            if ( windowMakeAbsolute )
                pwP.altInitParams[ jsObj.id.PP_WINDOW_POSITION_STATIC ] = false ;
        }
    
        var wndObj = new jsObj.widget.PortletWindow();
        wndObj.build( createWindowParams, windowContainerNode );    
    
        if ( dbProfile )
            dojo.profile.end( "createPortletWindow" );
    },
    
    evtConnect: function( adviceType, srcObj, srcFuncName, adviceObj, adviceFuncName, djEvtObj, rate )
    {   // if arg check is needed, use dojo.event.connect()
        if ( ! rate ) rate = 0;
        var cParams = { adviceType: adviceType, srcObj: srcObj, srcFunc: srcFuncName, adviceObj: adviceObj, adviceFunc: adviceFuncName, rate: rate };
        if ( djEvtObj == null ) djEvtObj = dojo.event;
        djEvtObj.connect( cParams );
        return cParams;
    },
    
    evtDisconnect: function( adviceType, srcObj, srcFuncName, adviceObj, adviceFuncName, djEvtObj )
    {   // if arg check is needed, use dojo.event.disconnect()
        if ( djEvtObj == null ) djEvtObj = dojo.event;
        djEvtObj.disconnect( { adviceType: adviceType, srcObj: srcObj, srcFunc: srcFuncName, adviceObj: adviceObj, adviceFunc: adviceFuncName } );
    },
    
    evtDisconnectWObj: function( kwArgs, djEvtObj )
    {
        if ( djEvtObj == null ) djEvtObj = dojo.event;
        //jetspeed.println( "evtD-single: " + jetspeed.printobj( kwArgs ) );
        djEvtObj.disconnect( kwArgs );
    },
    
    evtDisconnectWObjAry: function( kwArgsArray, djEvtObj )
    {
        if ( kwArgsArray && kwArgsArray.length > 0 )
        {
            //jetspeed.dumpary( kwArgsArray, "evtD-multi" );
            if ( djEvtObj == null ) djEvtObj = dojo.event;
            for ( var i = 0 ; i < kwArgsArray.length ; i++ )
            {
                djEvtObj.disconnect( kwArgsArray[i] );
            }
        }
    }
};
