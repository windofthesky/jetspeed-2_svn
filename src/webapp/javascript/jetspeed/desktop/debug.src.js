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
 */

dojo.provide( "jetspeed.desktop.debug" );
dojo.require( "jetspeed.debug" );
dojo.require( "dojo.profile" );


// jetspeed base objects

if ( ! window.jetspeed )
    jetspeed = {};
if ( ! jetspeed.om )
    jetspeed.om = {};


// debug options

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
    initWinState: false,
    submitWinState: false,
    ajaxPageNav: false,
    dragWindow: false,

    profile: true,

    windowDecorationRandom: false,

    debugInPortletWindow: true,

    debugContainerId: ( djConfig.debugContainerId ? djConfig.debugContainerId : dojo.hostenv.defaultDebugContainerId )
};
//jetspeed.debugPortletEntityIdFilter = [ "dp-7", "dp-3" ];       // load listed portlets only
//jetspeed.debugPortletEntityIdFilter = [];                       // disable all portlets
//jetspeed.debugContentDumpIds = [ ".*" ];                        // dump all responses
//jetspeed.debugContentDumpIds = [ "getmenus", "getmenu-.*" ];    // dump getmenus response and all getmenu responses
//jetspeed.debugContentDumpIds = [ "page-.*" ];                   // dump page psml response
//jetspeed.debugContentDumpIds = [ "js-cp-selector.2" ];          // dump portlet selector content
//jetspeed.debugContentDumpIds = [ "moveabs-layout" ];            // dump move layout response
//jetspeed.debugContentDumpIds = [ "js-cp-selector.*" ];          // dump portlet selector


// debug window

jetspeed.debugWindowLoad = function()
{
    var jsObj = jetspeed;
    var jsId = jsObj.id;
    var djObj = dojo;
    if ( djConfig.isDebug && jsObj.debug.debugInPortletWindow && djObj.byId( jsObj.debug.debugContainerId ) == null )
    {
        var dbWSt = jsObj.debugWindowReadCookie( true );
        var wP = {};
        var dbWId = jsId.PW_ID_PREFIX + jsId.DEBUG_WINDOW_TAG;
        wP[ jsId.PP_WINDOW_POSITION_STATIC ] = false;
        wP[ jsId.PP_WINDOW_HEIGHT_TO_FIT ] = false;
        wP[ jsId.PP_WINDOW_DECORATION ] = jsObj.prefs.windowDecoration;
        wP[ jsId.PP_WINDOW_TITLE ] = "Dojo Debug";
        wP[ jsId.PP_WINDOW_ICON ] = "text-x-script.png";
        wP[ jsId.PP_WIDGET_ID ] = dbWId;
        wP[ jsId.PP_WIDTH ] = dbWSt.width;
        wP[ jsId.PP_HEIGHT ] = dbWSt.height;
        wP[ jsId.PP_LEFT ] = dbWSt.left;
        wP[ jsId.PP_TOP ] = dbWSt.top;
        wP[ jsId.PP_EXCLUDE_PCONTENT ] = false;
        wP[ jsId.PP_CONTENT_RETRIEVER ] = new jsObj.om.DojoDebugContentRetriever();
        wP[ jsId.PP_WINDOW_STATE ] = dbWSt.windowState;
        var pwP = jsObj.widget.PortletWindow.prototype.altInitParamsDef( null, wP );
        jsObj.ui.createPortletWindow( pwP, null, jsObj );
        pwP.retrieveContent( null, null );
        var dbWW = jsObj.page.getPWin( dbWId );

        djObj.event.connect( "after", djObj.hostenv, "println", dbWW, "contentChanged" );
    
        djObj.event.connect( dbWW, "windowActionButtonSync", jsObj, "debugWindowSave" );
        djObj.event.connect( dbWW, "endSizing", jsObj, "debugWindowSave" );
        djObj.event.connect( dbWW, "endDragging", jsObj, "debugWindowSave" );
    }
};
jetspeed.debugWindowReadCookie = function( useDefaults )
{
    var debugState = {};
    if ( useDefaults )
        debugState = { width: "400", height: "400", left: "320", top: "0", windowState: jetspeed.id.ACT_MINIMIZE };
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
    var debugWindowWidget = jetspeed.debugWindow();
    if ( ! debugWindowWidget ) return;
    debugWindowWidget.restoreWindow();
};
jetspeed.debugWindow = function()
{
    var debugWindowWidgetId = jetspeed.id.PW_ID_PREFIX + jetspeed.id.DEBUG_WINDOW_TAG;
    return jetspeed.page.getPWin( debugWindowWidgetId );
};
jetspeed.debugWindowId = function()
{
    return jetspeed.id.PW_ID_PREFIX + jetspeed.id.DEBUG_WINDOW_TAG;
};
jetspeed.debugWindowSave = function()
{
    var debugWindowWidget = jetspeed.debugWindow();
    if ( ! debugWindowWidget ) return null;
    if ( ! debugWindowWidget.posStatic )
    {
        var currentState = debugWindowWidget.getCurWinStateForPersist( false );
        var cWidth = currentState.width; var cHeight = currentState.height; var cTop = currentState.top; var cLeft = currentState.left;
        if ( debugWindowWidget.windowState == jetspeed.id.ACT_MINIMIZE )
        {
            var dimsCurrent = debugWindowWidget.getDimsObj( debugWindowWidget.posStatic );
            if ( dimsCurrent != null )
            {
                if ( dimsCurrent.height != null && dimsCurrent.height > 0 )
                    cHeight = dimsCurrent.height;
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
            var jsObj = jetspeed;
            var content = "";
            var dbNodeId = jsObj.debug.debugContainerId;
            var dbWindow = jsObj.debugWindow();

            if ( jsObj.altDebugWindowContent )
                content = jsObj.altDebugWindowContent();
            else
                content += '<div id="' + dbNodeId + '"></div>';

            if ( contentListener )
                contentListener.notifySuccess( content, bindArgs.url, domainModelObject );
            else if ( dbWindow )
                dbWindow.setPortletContent( content, bindArgs.url );

            this.initialized = true;

            if ( dbWindow )
            {
                var clearJS = "javascript: void(document.getElementById('" + dbNodeId + "').innerHTML='')";
                var indent = "";
                for ( var i = 0 ; i < 20 ; i++ )
                    indent += "&nbsp;";
                var titleWithClearAnchor = dbWindow.title + indent + '<a href="' + clearJS + '"><span style="font-size: xx-small; font-weight: normal">Clear</span></a>';
                dbWindow.tbTextNode.innerHTML = titleWithClearAnchor;
            }
        }
    }
};

// debug info functions

jetspeed.debugDumpColWidths = function()
{
    for ( var i = 0 ; i < jetspeed.page.columns.length ; i++ )
    {
        var columnElmt = jetspeed.page.columns[i];
        dojo.debug( "jetspeed.page.columns[" + i + "] outer-width: " + dojo.html.getMarginBox( columnElmt.domNode ).width );
    }
};
jetspeed.debugDumpWindowsPerCol = function()
{
    for ( var i = 0 ; i < jetspeed.page.columns.length ; i++ )
    {
        var columnElmt = jetspeed.page.columns[i];
        var windowNodesInColumn = jetspeed.ui.getPWinChildren( columnElmt.domNode, null );
        var portletWindowsInColumn = jetspeed.ui.getPWinsFromNodes( windowNodesInColumn.portletWindowNodes );
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

jetspeed.debugDumpWindows = function()
{
    var portletWindows = jetspeed.page.getPWins();
    var pwOut = "";
    for ( var i = 0 ; i < portletWindows.length; i++ )
    {
        if ( i > 0 )
            pwOut += ", ";
        pwOut += portletWindows[i].widgetId;
    }
    dojo.debug( "PortletWindows: " + pwOut );
};

jetspeed.debugLayoutInfo = function()
{
    var jsPage = jetspeed.page;
    var dumpMsg = "";
    var i = 0;
    for ( var layoutId in jsPage.layouts )
    {
        if ( i > 0 ) dumpMsg += "\r\n";
        dumpMsg += "layout[" + layoutId + "]: " + jetspeed.printobj( jsPage.layouts[ layoutId ], true, true, true );
        i++;
    }
    return dumpMsg;
};
jetspeed.debugColumnInfo = function()
{
    var jsPage = jetspeed.page;
    var dumpMsg = "";
    for ( var i = 0; i < jsPage.columns.length; i++ )
    {
        if ( i > 0 ) dumpMsg += "\r\n";
        dumpMsg += jsPage.columns[i].toString();
    }
    return dumpMsg;
};
jetspeed.debugSavedWinState = function()
{
    return jetspeed.debugWinStateAll( true );
};
jetspeed.debugWinState = function()
{
    return jetspeed.debugWinStateAll( false );
};
jetspeed.debugPortletActions = function()
{
    var portletArray = jetspeed.page.getPortletArray();
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
};
jetspeed.debugWinStateAll = function( useLastSaved )
{
    var portletArray = jetspeed.page.getPortletArray();
    var dumpMsg = "";
    for ( var i = 0; i < portletArray.length; i++ )
    {
        var portlet = portletArray[i];
        if ( i > 0 ) dumpMsg += "\r\n";
        var windowState = null;
        try
        {
            if ( useLastSaved )
                windowState = portlet.getSavedWinState();
            else
                windowState = portlet.getCurWinState();
        }
        catch (e) { }
        dumpMsg += "[" + portlet.name + "] " + ( (windowState == null) ? "null" : jetspeed.printobj( windowState, true ) );
    }
    return dumpMsg;
};


// profile functions

if ( jetspeed.debug.profile )
{
    dojo.profile.clearItem = function(name) {
    	// summary:	clear the profile times for a particular entry
    	return (this._profiles[name] = {iters: 0, total: 0});
    }
    dojo.profile.debugItem = function(name,clear) {
    	// summary:	write profile information for a particular entry to the debug console
    	var profile = this._profiles[name];
    	if (profile == null) return null;
    	
    	if (profile.iters == 0) {
    		return [name, " not profiled."].join("");
    	}
    	var output = [name, " took ", profile.total, " msec for ", profile.iters, " iteration"];
    	if (profile.iters > 1) {
    		output.push("s (", (Math.round(profile.total/profile.iters*100)/100), " msec each)");
    	}
    
    	// summary: print profile information for a single item out to the debug log
    	dojo.debug(output.join(""));
        if ( clear )
            this.clearItem( name );
    }
    dojo.profile.debugAllItems = function(clear) {
        for(var x=0; x < this._pns.length; x++){
            this.debugItem( this._pns[x], clear );
        }
    }        
}
