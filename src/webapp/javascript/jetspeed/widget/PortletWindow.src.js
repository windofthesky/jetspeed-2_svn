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

dojo.provide("jetspeed.widget.PortletWindow");
dojo.require("jetspeed.desktop.core");

jetspeed.widget.PortletWindow = function()
{
    this.windowInitialized = false;
    this.actionButtons = {};
    this.actionMenus = {};
    this.actionMenuWidget = null;
    this.tooltips = [];
    this.subWidgetStartIndex = -1;
    this.subWidgetEndIndex = -1;
    
    // content load vars
	this._onLoadStack = [];
	this._onUnloadStack = [];
	this._callOnUnload = false;
};

dojo.extend( jetspeed.widget.PortletWindow, {
    title: "",
    nextIndex: 1,

    resizable: true,
    movable: true,

    decName: null,      // decoration name
    decConfig: null,    // decoration config

    posStatic: false,
    heightToFit: false,

    titleMouseIn: 0,
    titleLit: false,

    portlet: null,
    altInitParams: null,
    
    inContentChgd: false,

    exclPContent: false,

    minimizeTempRestore: null,

    // see setPortletContent for info on these ContentPane settings:
    executeScripts: false,
    scriptSeparation: false,
    adjustPaths: false,
    parseContent: true,

    dbProfile: (djConfig.isDebug && jetspeed.debug.profile),
    dbOn: djConfig.isDebug,
    dbMenuDims: "Dump Dimensions",

    /*  static  */
    altInitParamsDef: function( defineIn, params )
    {
        if ( ! defineIn )
        {
            defineIn = {
                            getProperty: function( propertyName )
                            {
                                if ( ! propertyName ) return null;
                                return this.altInitParams[ propertyName ];
                            },
                            retrieveContent: function( contentListener, bindArgs )
                            {
                                var contentRetriever = this.altInitParams[ jetspeed.id.PP_CONTENT_RETRIEVER ];
                                if ( contentRetriever )
                                {
                                    contentRetriever.getContent( bindArgs, contentListener, this, jetspeed.debugPortletDumpRawContent );
                                }
                                else
                                {
                                    jetspeed.url.retrieveContent( bindArgs, contentListener, this, jetspeed.debugPortletDumpRawContent );
                                }
                            }
                       };
        }
        if ( ! params )
            params = {};
        if ( params.altInitParams )
            defineIn.altInitParams = params.altInitParams;
        else
            defineIn.altInitParams = params;
        return defineIn;
    },

    build: function( createWinParams, winContainerNode )
    {
        var jsObj = jetspeed;
        var jsId = jsObj.id;
        var jsPrefs = jsObj.prefs;
        var jsPage = jsObj.page;
        var jsCss = jsObj.css;
        var jsUI = jsObj.ui;
        var doc = document;
        var docBody = jsObj.docBody;
        var djObj = dojo;

        var winIndex = jsObj.widget.PortletWindow.prototype.nextIndex;
        this.portletIndex = winIndex;
        var ie6 = jsObj.UAie6;
        this.ie6 = ie6;

        var printMode = false;
        if ( createWinParams )
        {
            if ( createWinParams.portlet )
                this.portlet = createWinParams.portlet;
            if ( createWinParams.altInitParams )
                this.altInitParams = createWinParams.altInitParams;
            if ( createWinParams.printMode )
                printMode = true;
        }
        var tPortlet = this.portlet;
        var iP = ( tPortlet ? tPortlet.getProperties() : ( this.altInitParams ? this.altInitParams : {} ) ) ;

        var initWidgetId = iP[ jsId.PP_WIDGET_ID ];
        if ( ! initWidgetId )
        {
            if ( tPortlet )
                djObj.raise( "PortletWindow is null for portlet: " + tPortlet.entityId );
            else
                initWidgetId = jsId.PW_ID_PREFIX + winIndex;
        }
        this.widgetId = initWidgetId;
        jsObj.widget.PortletWindow.prototype.nextIndex++;

        // ... initWindowDecoration
        var decNm = iP[ jsId.PP_WINDOW_DECORATION ];
        if ( ! decNm )
        {
            decNm = this.portletDecorationName;
            if ( ! decNm )
                decNm = jsPage.getPortletDecorationDefault();
        }
        this.decName = decNm ;
        var wDC = jsObj.loadPortletDecorationStyles( decNm );
        if ( wDC == null ) wDC = {};    // this should not occur
        this.decConfig = wDC;

        var dNodeClass = wDC.dNodeClass;
        var cNodeClass = wDC.cNodeClass;
        if ( dNodeClass == null || cNodeClass == null )
        {
            dNodeClass = jsId.PWIN_CLASS;
            cNodeClass = "portletWindowClient";
            if ( decNm )
            {
                dNodeClass = decNm + " " + dNodeClass;
                cNodeClass = decNm + " " + cNodeClass;
            }
            dNodeClass = jsId.P_CLASS + " " + dNodeClass;
            cNodeClass = jsId.P_CLASS + " " + cNodeClass;
            wDC.dNodeClass = dNodeClass;
            wDC.cNodeClass = cNodeClass;
        }

        // ... create window nodes
        var dNode = doc.createElement( "div" );
        dNode.id = initWidgetId;
        dNode.className = dNodeClass;
        dNode.style.display = "none";

        var cNode = doc.createElement( "div" );
        cNode.className = cNodeClass;
        
        var tbNode = null, rbNode = null, tbIconNode = null, tbNodeCss = null;
        if ( ! printMode )
        {
            tbNode = doc.createElement( "div" );
            tbNode.className = "portletWindowTitleBar";
        
            tbIconNode = doc.createElement( "img" );
            tbIconNode.className = "portletWindowTitleBarIcon";
        
            var tbTextNode = doc.createElement( "div" );
            tbTextNode.className = "portletWindowTitleText";
    
            tbNode.appendChild( tbIconNode );
            tbNode.appendChild( tbTextNode );
    
            rbNode = doc.createElement( "div" );
            rbNode.className = "portletWindowResizebar";
    
            this.tbNode = tbNode;
            tbNodeCss = jsCss.cssBase.concat();
            this.tbNodeCss = tbNodeCss;
            this.tbIconNode = tbIconNode;
            this.tbTextNode = tbTextNode;
            this.rbNode = rbNode;
            this.rbNodeCss = jsCss.cssBase.concat();
        }
    
        if ( tbNode != null )
            dNode.appendChild( tbNode );
    
        dNode.appendChild( cNode );
    
        if ( rbNode != null )
            dNode.appendChild( rbNode );
        
        this.domNode = dNode;
        var dNodeCss = jsCss.cssPosition.concat();
        this.dNodeCss = dNodeCss;
        this.containerNode = cNode;
        var cNodeCss = jsCss.cssOverflow.concat();
        this.cNodeCss = cNodeCss;


        // ... initWindowTitle
        this.setPortletTitle( iP[ jsId.PP_WINDOW_TITLE ] );

        // ... initWindowDimensions
        var posStatic = iP[ jsId.PP_WINDOW_POSITION_STATIC ];
        this.posStatic = this.preMaxPosStatic = posStatic;
        var heightToFit = iP[ jsId.PP_WINDOW_HEIGHT_TO_FIT ];
        this.heightToFit = this.preMaxHeightToFit = heightToFit;

        var wWidth = null, wHeight = null, wLeft = null, wTop = null;
        if ( tPortlet )
        {
            var pWDims = tPortlet.getInitialWinDims();
        	wWidth = pWDims.width;
            wHeight = pWDims.height;
            wLeft = pWDims.left;
            wTop = pWDims.top;
            // NOTE: pWDims.zIndex;  - should be dealt with in the creation order
        }
        else
        {
            wWidth = iP[ jsId.PP_WIDTH ];
            wHeight = iP[ jsId.PP_HEIGHT ];
            wLeft = iP[ jsId.PP_LEFT ];
            wTop = iP[ jsId.PP_TOP ];
        }
        
        var untiledDims = {};            // untiled
        var tiledDims = { width: null }; // tiled
        
        // to allow for an initial untiled placement based on tiled position,
        //   only record dimsUntiled when value is specified (not defaulted) or if window is already untiled
        if ( wWidth != null && wWidth > 0 )
            untiledDims.width = wWidth = Math.floor( wWidth );
        else
            untiledDims.width = wWidth = jsPrefs.windowWidth;
    
        if ( wHeight != null && wHeight > 0 )
            untiledDims.height = tiledDims.height = wHeight = Math.floor(wHeight);
        else
            untiledDims.height = tiledDims.height = wHeight = jsPrefs.windowHeight;
            
        if ( wLeft != null && wLeft >= 0 )
            untiledDims.left = Math.floor( wLeft );
        else if ( ! posStatic )
            untiledDims.left = (((winIndex -2) * 30 ) + 200);
    
        if ( wTop != null && wTop >= 0 )
            untiledDims.top = Math.floor(wTop);
        else if ( ! posStatic )
            untiledDims.top = (((winIndex -2) * 30 ) + 170);
        
        this.dimsUntiled = untiledDims;
        this.dimsTiled = tiledDims;

        // xxx
        //var dimCss = "display: none; width: " + wWidth + "px" + ( ( wHeight != null && wHeight > 0 ) ? ( "; height: " + wHeight + "px" ) : "");
        //if ( ! posStatic )
        //    dimCss += "; left: " + wLeft + "px; top: " + wTop + "px;";
        //dNode.style.cssText = dimCss;

        this.exclPContent = iP[ jsId.PP_EXCLUDE_PCONTENT ];

        // --- former end of preBuild()

        jsPage.putPWin( this );

        // --- former beginning of build()

		// necessary for safari, khtml (for computing width/height)
		docBody.appendChild( dNode );

        // ... initWindowIcon
        if ( tbIconNode )
        {
            var tbIconSrc = null;
            if ( wDC.windowIconEnabled && wDC.windowIconPath != null )
            {
                var wI = iP[ jsId.PP_WINDOW_ICON ];
                if ( ! wI )
                    wI = "document.gif";
                tbIconSrc = new djObj.uri.Uri( jsObj.url.basePortalDesktopUrl() + wDC.windowIconPath + wI ) ;
                tbIconSrc = tbIconSrc.toString();
                if ( tbIconSrc.length == 0 )
                    tbIconSrc = null;
                this.iconSrc = tbIconSrc;
            }
            // <img src=""> can hang IE!  better get rid of it
		    if ( tbIconSrc )
                tbIconNode.src = tbIconSrc;
            else
            {
			    djObj.dom.removeNode( tbIconNode );
                this.tbIconNode = tbIconNode = null;
            }
		}

		if ( tbNode )
        {	
	        if ( jsObj.UAmoz || jsObj.UAsaf )
            {
                if ( jsObj.UAmoz )
                    tbNodeCss[ jsCss.cssNoSelNm ] = " -moz-user-select: ";
                else
                    tbNodeCss[ jsCss.cssNoSelNm ] = " -khtml-user-select: ";
                tbNodeCss[ jsCss.cssNoSel ] = "none";
                tbNodeCss[ jsCss.cssNoSelEnd ] = ";";
            }
            else if ( jsObj.UAie )
            {
                tbNode.unselectable = "on";
            }
            var windowTitleBarButtons = null;
            var djEvtObj = djObj.event;
            var incl, aNm;
            var mANms = new Array();
            var noImg = false;
            if ( wDC.windowActionButtonOrder != null )
            {   // all possible button actions must be added here (no support for adding action buttons after init)
                // this includes buttons for the current mode and state (which will be initially hidden)
                var btnActionNames = new Array();
                if ( tPortlet )
                {
                    for ( var aI = (wDC.windowActionButtonOrder.length-1) ; aI >= 0 ; aI-- )
                    {
                        aNm = wDC.windowActionButtonOrder[ aI ];
                        incl = false;
                        if ( tPortlet.getAction( aNm ) != null || jsPrefs.windowActionDesktop[ aNm ] != null )
                        {
                            incl = true;
                        }
                        else if ( aNm == jsId.ACT_RESTORE || aNm == jsId.ACT_MENU )
                        {
                            incl = true;
                        }
                        if ( incl )
                        {
                            btnActionNames.push( aNm );
                        }
                    }
                }
                else
                {
                    for ( var aI = (wDC.windowActionButtonOrder.length-1) ; aI >= 0 ; aI-- )
                    {
                        aNm = wDC.windowActionButtonOrder[ aI ];
                        incl = false;
                        if ( aNm == jsId.ACT_MINIMIZE || aNm == jsId.ACT_MAXIMIZE || aNm == jsId.ACT_RESTORE || aNm == jsId.ACT_MENU || jsPrefs.windowActionDesktop[ aNm ] != null )
                        {
                            incl = true;
                        }
                        if ( incl )
                        {
                            btnActionNames.push( aNm );
                        }
                    }
                }   // if ( tPortlet )
                var btnMax = ( wDC.windowActionButtonMax == null ? -1 : wDC.windowActionButtonMax );
                if ( btnMax != -1 && btnActionNames.length >= btnMax )
                {
                    var removedBtns = 0;
                    var mustRemoveBtns = btnActionNames.length - btnMax + 1;
                    for ( var i = 0 ; i < btnActionNames.length && removedBtns < mustRemoveBtns ; i++ )
                    {
                        if ( btnActionNames[i] != jsId.ACT_MENU )
                        {
                            mANms.push( btnActionNames[i] );
                            btnActionNames[i] = null;
                            removedBtns++;
                        }
                    }
                }
                if ( wDC.windowActionNoImage )
                {
                    for ( var i = 0 ; i < btnActionNames.length ; i++ )
                    {
                        if ( wDC.windowActionNoImage[ btnActionNames[ i ] ] != null )
                        {
                            if ( btnActionNames[ i ] == jsId.ACT_MENU )
                            {
                                noImg = true;
                            }
                            else
                            {
                                mANms.push( btnActionNames[i] );
                            }
                            btnActionNames[ i ] = null;
                        }
                    }
                }
                var tooltipMgr = jsPage.tooltipMgr;
                for ( var i = 0 ; i < btnActionNames.length ; i++ )
                {
                    if ( btnActionNames[i] != null )
                    {
                        this._createActionButtonNode( btnActionNames[i], doc, docBody, tooltipMgr, jsObj, jsPrefs, jsUI, djEvtObj );
                    }
                }
            }   // if ( wDC.windowActionButtonOrder != null )

            if ( wDC.windowActionMenuOrder )
            {
                if ( tPortlet )
                {
                    for ( var aI = 0 ; aI < wDC.windowActionMenuOrder.length ; aI++ )
                    {
                        aNm = wDC.windowActionMenuOrder[ aI ];
                        incl = false;
                        if ( tPortlet.getAction( aNm ) != null || jsPrefs.windowActionDesktop[ aNm ] != null )
                        {
                            incl = true;
                        }
                        if ( incl )
                        {
                            mANms.push( aNm );
                        }
                    }
                }
                else
                {
                    for ( var aI = 0 ; aI < wDC.windowActionMenuOrder.length ; aI++ )
                    {
                        aNm = wDC.windowActionMenuOrder[ aI ];
                        if ( jsPrefs.windowActionDesktop[ aNm ] != null )
                        {
                            mANms.push( aNm );
                        }
                    }
                }   // if ( tPortlet )
            }   // if ( wDC.windowActionMenuOrder != null )

            if ( mANms.length > 0 || this.dbOn )
            {
                var added = new Object();
                var finalNms = new Array();
                for ( var i = 0 ; i < mANms.length ; i++ )
                {
                    aNm = mANms[i];
                    if ( aNm != null && added[ aNm ] == null && this.actionButtons[ aNm ] == null )
                    {
                        finalNms.push( aNm );
                        added[ aNm ] = true;
                    }
                }
                
                if ( this.dbOn )
                {
                    finalNms.push( { aNm: this.dbMenuDims, dev: true } );
                }
                if ( finalNms.length > 0 )
                {
                    this._createActionMenu( finalNms, docBody );
                    if ( noImg )
                    {
                        jsUI.evtConnect( "after", tbNode, "oncontextmenu", this, "windowActionMenuOpen", djEvtObj );
                    }
                }
            }

            this.windowActionButtonSync();

            if ( wDC.windowDisableResize )
                this.resizable =  false;
            if ( wDC.windowDisableMove )
                this.movable =  false;
        }

        // --- init drag handle
        var isResizable = this.resizable;
        var rhWidget = null;
        if ( isResizable && rbNode )
        {
            var rhWidgetId = initWidgetId + "_resize";
            var rhWidget = jsObj.widget.CreatePortletWindowResizeHandler( this, jsObj );
            this.resizeHandle = rhWidget;
            if ( rhWidget )
            {
                //if ( this.posStatic && jsObj.UAmoz )  // jsObj.UAie
                    rhWidget.domNode.style.position = "static";  // until 2006-11-15, was set to absolute for all but mozilla
                                                                          // but setting to static for all seems to fix IE failure to initially display resize handle
                //else
                //    rhWidget.domNode.style.position = "absolute";
                rbNode.appendChild( rhWidget.domNode );
            }
		}
        else
        {
            this.resizable = false;
        }

		// Prevent IE bleed-through problem
        if ( ie6 )
		    this.bgIframe = new djObj.html.BackgroundIframe( dNode );

		// counteract body.appendChild above
		docBody.removeChild( dNode );

        // --- former end of build()
        
        winContainerNode.appendChild( dNode );
        
        // --- former beginning of postBuild()
    
        //dNode.style.display = "";   // xxxx

        if ( ! wDC.layoutExtents )
        {
            var dimCss = "display: block; width: " + wWidth + "px" + ( ( wHeight != null && wHeight > 0 ) ? ( "; height: " + wHeight + "px" ) : "");
            dNode.style.cssText = dimCss;
            //this.testLost();
            this._createLayoutExtents( wDC, false, dNode, cNode, tbNode, rbNode, djObj, jsObj );
        }
        
        if ( this.movable && tbNode )
        {
            this.drag = new djObj.dnd.Moveable( this, {handle: tbNode});
            this._setTitleBarDragging( true, jsCss );
        }

        this._setAsTopZIndex( jsPage, jsCss, dNodeCss, posStatic );
        this._alterCss( true, true );

        if ( ! posStatic )
            this._addUntiledEvents();

        this.windowInitialized = true;

        if ( jsObj.debug.createWindow )
            djObj.debug( "createdWindow [" + ( tPortlet ? tPortlet.entityId : initWidgetId ) + ( tPortlet ? (" / " + initWidgetId) : "" ) + "]" + " width=" + dNode.style.width + " height=" + dNode.style.height + " left=" + dNode.style.left + " top=" + dNode.style.top ) ;


        var iWS = null;
        if ( tPortlet )
            iWS = tPortlet.getCurrentActionState();
        else
            iWS = iP[ jsId.PP_WINDOW_STATE ];

        if ( iWS == jsId.ACT_MINIMIZE )
        {
            this.minimizeWindow();
            this.windowActionButtonSync();
            this.needsRenderOnRestore = true;
        }
        else if ( iWS == jsId.ACT_MAXIMIZE )
        {   // needs delay so that widths are fully realized before maximize occurs
            djObj.lang.setTimeout( this, this._postCreateMaximizeWindow, 1500 );
        }

        if ( ie6 && jsObj.widget.ie6ZappedContentHelper == null )
        {
            var ie6Helper = doc.createElement("span");
            ie6Helper.id = "ie6ZappedContentHelper";
            jsObj.widget.ie6ZappedContentHelper = ie6Helper;
        }

        if ( jsObj.widget.pwGhost == null && jsPage != null )
        {   // ... PortletWindow drag ghost
            var pwGhost = doc.createElement("div");
            pwGhost.id = "pwGhost";
            var defaultWndC = jsPage.getPortletDecorationDefault();
            if ( ! defaultWndC ) defaultWndC = decNm;
            pwGhost.className = jsId.P_CLASS + ( defaultWndC ? ( " " + defaultWndC ) : "" ) + " " + dNodeClass;
            pwGhost.style.position = "static";
            pwGhost.style.width = "";
            pwGhost.style.left = "auto";
            pwGhost.style.top = "auto";
            jsObj.widget.pwGhost = pwGhost;
        }
    },  // build()


    // build functions

    _createActionButtonNode: function( aNm, doc, docBody, tooltipMgr, jsObj, jsPrefs, jsUI, djEvtObj )
    {
        if ( aNm != null )
        {
            var aBtn = doc.createElement( "div" );
            aBtn.className = "portletWindowActionButton";
            aBtn.style.backgroundImage = "url(" + jsPrefs.getPortletDecorationBaseUrl( this.decName ) + "/images/desktop/" + aNm + ".gif)";
            aBtn.actionName = aNm;

            this.actionButtons[ aNm ] = aBtn;
            this.tbNode.appendChild( aBtn );

            jsUI.evtConnect( "after", aBtn, "onclick", this, "windowActionButtonClick", djEvtObj );
            if ( this.decConfig != null && this.decConfig.windowActionButtonTooltip )
            {
                this.tooltips.push( tooltipMgr.addNode( aBtn, this._getActionLabel( aNm ), true, jsObj, jsUI, djEvtObj ) );
            }
            else
            {
                jsUI.evtConnect( "after", aBtn, "onmousedown", djEvtObj.browser, "stopEvent", djEvtObj );
            }
        }
    },

    _getActionLabel: function( aNm )
    {
        if ( aNm == null ) return null;
        var actionlabel = null;
        var actionLabelPrefs = jetspeed.prefs.desktopActionLabels;
        if ( actionLabelPrefs != null )
            actionlabel = actionLabelPrefs[ aNm ];
        if ( actionlabel == null || actionlabel.length == 0 )
        {
            if ( this.portlet )
            {
                var portletActionDef = this.portlet.getAction( aNm );
                if ( portletActionDef != null )
                    actionlabel = portletActionDef.label;
            }
        }
        if ( actionlabel == null || actionlabel.length == 0 )
        {
            actionlabel = dojo.string.capitalize( aNm );
        }
        return actionlabel;
    },
    _createActionMenu: function( menuActionNames, docBody )
    {
        if ( menuActionNames == null || menuActionNames.length == 0 ) return;
        var pWin = this;
        var aNm, menulabel, menuitem, isDev;
        var miOnClick = function( mi ) { var _aN = mi.jsActNm; if ( ! mi.jsActDev ) pWin.windowActionProcess( _aN ); else pWin.windowActionProcessDev( _aN ); };
        var titleBarContextMenu = dojo.widget.createWidget( "PopupMenu2", { id: this.widgetId + "_ctxmenu", contextMenuForWindow: false, onItemClick: miOnClick }, null );
        for ( var i = 0 ; i < menuActionNames.length ; i++ )
        {
            aNm = menuActionNames[i];
            isDev = false;
            if ( ! aNm.dev )
                menulabel = this._getActionLabel( aNm );
            else
            {
                isDev = true;
                menulabel = aNm = aNm.aNm;
            }
            menuitem = dojo.widget.createWidget( "MenuItem2", { caption: menulabel, jsActNm: aNm, jsActDev: isDev } );
            this.actionMenus[ aNm ] = menuitem;
            titleBarContextMenu.addChild( menuitem );
        }
        docBody.appendChild( titleBarContextMenu.domNode );
        this.actionMenuWidget = titleBarContextMenu;
    },

    // layout extents static methods - used for defining cached portlet decorator layoutExtents object

    /*  static  */
    _createLayoutExtents: function( decorationConfig, forIFrameStyles, dNode, cNode, tbNode, rbNode, djObj, jsObj )
    {   // should be called once for each used portlet decorator
        var dNodeCompStyle = djObj.gcs( dNode );
        var cNodeCompStyle = djObj.gcs( cNode );
        var tbNodeCompStyle = null, rbNodeCompStyle = null;

        var layoutExtents = { dNode: this._createNodeLEs( dNode, dNodeCompStyle, djObj, jsObj ),
                              cNode: this._createNodeLEs( cNode, cNodeCompStyle, djObj, jsObj ) };
        if ( tbNode )
        {
            tbNodeCompStyle = djObj.gcs( tbNode );
            layoutExtents.tbNode = this._createNodeLEs( tbNode, tbNodeCompStyle, djObj, jsObj );
            var dragCursor = tbNodeCompStyle.cursor;
            if ( dragCursor == null || dragCursor.length == 0 )
                dragCursor = "move";
            decorationConfig.dragCursor = dragCursor;
        }

        if ( rbNode )
        {
            rbNodeCompStyle = djObj.gcs( rbNode );
            layoutExtents.rbNode = this._createNodeLEs( rbNode, rbNodeCompStyle, djObj, jsObj );
        }

        var dNodeMarginBox = djObj.getMarginBox( dNode, dNodeCompStyle, jsObj ) ;
        var dNodeContentBox = djObj.getContentBox( dNode, dNodeCompStyle, jsObj ) ;

		layoutExtents.lostHeight=
			( dNodeMarginBox.h - dNodeContentBox.h )
			+ ( tbNode ? djObj.getMarginBox(tbNode, tbNodeCompStyle, jsObj).h : 0 )
			+ ( rbNode ? djObj.getMarginBox(rbNode, rbNodeCompStyle, jsObj).h : 0 );

		layoutExtents.lostWidth = dNodeMarginBox.w - dNodeContentBox.w;

        if ( ! forIFrameStyles )
            decorationConfig.layoutExtents = layoutExtents;
        else
            decorationConfig.layoutExtentsIFrame = layoutExtents;
    },

    testLost: function()
    {
        var djObj = dojo;
        var jsObj = jetspeed;
        var dNode = this.domNode;
        var tbNode = this.tbNode;
        var rbNode = this.rbNode;
        var dNodeCompStyle = djObj.gcs( dNode );
        var tbNodeCompStyle = djObj.gcs( tbNode );
        var rbNodeCompStyle = djObj.gcs( rbNode );
        var dNodeMarginBox = djObj.getMarginBox( dNode, dNodeCompStyle, jsObj ) ;
        var dNodeContentBox = djObj.getContentBox( dNode, dNodeCompStyle, jsObj ) ;

        var tbmb = djObj.getMarginBox(tbNode, tbNodeCompStyle, jsObj);
        var rbmb = djObj.getMarginBox(rbNode, rbNodeCompStyle, jsObj);

        var lost = { id: this.widgetId, dMBw: dNodeMarginBox.w, dMBh: dNodeMarginBox.h, dCBw: dNodeContentBox.h, dCBh: dNodeContentBox.w, tbMBh: tbmb.h, rbMBh: rbmb.h, dNodePos: dNodeCompStyle.position, dNodeDis: dNodeCompStyle.display, dNodeWidth: dNodeCompStyle.width, dNodeHeight: dNodeCompStyle.height };
		lost.lostHeight =
			( dNodeMarginBox.h - dNodeContentBox.h )
			+ ( tbNode ? djObj.getMarginBox(tbNode, tbNodeCompStyle, jsObj).h : 0 )
			+ ( rbNode ? djObj.getMarginBox(rbNode, rbNodeCompStyle, jsObj).h : 0 );

		lost.lostWidth = dNodeMarginBox.w - dNodeContentBox.w;

        var lostStr = jetspeed.printobj( lost );
        if ( jetspeed.lostFirst == null )
            jetspeed.lostFirst = lostStr;

        return lostStr;
    },

    /*  static  */
    _createNodeLEs: function( node, nodeCS, djObj, jsObj )
    {
        var padborder = djObj._getPadBorderExtents( node, nodeCS );
        var margin = djObj._getMarginExtents( node, nodeCS, jsObj);
        return { padborder: padborder,
                 margin: margin,
                 lessW: ( padborder.w + margin.w ),
                 lessH: ( padborder.h + margin.h ) };
    },


    // action functions

    windowActionButtonClick: function( evt )
    {
        if ( evt == null || evt.target == null ) return;
        this.windowActionProcess( evt.target.actionName, evt );
    },
    windowActionMenuOpen: function( evt )
    {
        var aState = null;
        var aMode = null;
        if ( this.portlet )
        {
            aState = this.portlet.getCurrentActionState();
            aMode = this.portlet.getCurrentActionMode();
        }
        for ( var aNm in this.actionMenus )
        {
            var menuitem = this.actionMenus[ aNm ];
            if ( this._isWindowActionEnabled( aNm, aState, aMode ) )
            {
                menuitem.domNode.style.display = "";   // instead of menuitem.enable();
            }
            else
            {
                menuitem.domNode.style.display = "none";   // instead of menuitem.disable();
            }
        }
        this.actionMenuWidget.onOpen( evt );
    },
    windowActionProcessDev: function( /* String */ aNm, evt )
    {
        if ( aNm == this.dbMenuDims )
        {
            this.dumpPos();
        }
    },
    windowActionProcess: function( /* String */ aNm, evt )
    {   // evt arg is needed only for opening action menu
        //dojo.debug( "windowActionProcess [" + ( this.portlet ? this.portlet.entityId : this.widgetId ) + ( this.portlet ? (" / " + this.widgetId) : "" ) + "]" + " actionName=" + aNm );
        var jsObj = jetspeed;
        var jsId = jsObj.id;
        if ( aNm == null ) return;
        if ( jsObj.prefs.windowActionDesktop[ aNm ] != null )
        {
            if ( aNm == jsId.ACT_DESKTOP_TILE )
            {
                this.makeTiled();
            }
            else if ( aNm == jsId.ACT_DESKTOP_UNTILE )
            {
                this.makeUntiled();
            }
            else if ( aNm == jsId.ACT_DESKTOP_HEIGHT_EXPAND )
            {
                this.makeHeightToFit( false );
            }
            else if ( aNm == jsId.ACT_DESKTOP_HEIGHT_NORMAL )
            {
                this.makeHeightVariable( false, false );
            }
        }
        else if ( aNm == jsId.ACT_MENU )
        {
            this.windowActionMenuOpen( evt );
        }
        else if ( aNm == jsId.ACT_MINIMIZE )
        {   // make no associated content request - just notify server of change
            if ( this.portlet && this.windowState == jsId.ACT_MAXIMIZE )
            {
                this.needsRenderOnRestore = true;
            }
            this.minimizeWindow();
            if ( this.portlet )
            {
                jsObj.changeActionForPortlet( this.portlet.getId(), jsId.ACT_MINIMIZE, null );
            }
            if ( ! this.portlet )
            {
                this.windowActionButtonSync();
            }
        }
        else if ( aNm == jsId.ACT_RESTORE )
        {   // if minimized, make no associated content request - just notify server of change
            var deferRestoreWindow = false;
            if ( this.portlet )
            {
                if ( this.windowState == jsId.ACT_MAXIMIZE || this.needsRenderOnRestore )
                {
                    if ( this.needsRenderOnRestore )
                    {
                        deferRestoreWindow = true;
                        this.restoreOnNextRender = true;
                        this.needsRenderOnRestore = false;
                    }
                    if ( this.iframesInfo )
                        this.iframesInfo.iframesSize = [];
                    this.portlet.renderAction( aNm );
                }
                else
                {
                    jsObj.changeActionForPortlet( this.portlet.getId(), jsId.ACT_RESTORE, null );
                }
            }
            if ( ! deferRestoreWindow )
            {
                this.restoreWindow();
            }
            if ( ! this.portlet )
            {
                this.windowActionButtonSync();
            }
        }
        else if ( aNm == jsId.ACT_MAXIMIZE )
        {
            if ( this.portlet && this.iframesInfo )
            {
                this.iframesInfo.iframesSize = [];
            }

            this.maximizeWindow();

            if ( this.portlet )
            {
                this.portlet.renderAction( aNm );
            }
            else
            {
                this.windowActionButtonSync();
            }
        }
        else if ( aNm == jsId.ACT_REMOVEPORTLET )
        {
            if ( this.portlet )
            {
                var pageEditorWidget = dojo.widget.byId( jsId.PG_ED_WID );
                if ( pageEditorWidget != null )
                {
                    pageEditorWidget.deletePortlet( this.portlet.entityId, this.title );
                }
            }
        }
        else
        {
            if ( this.portlet )
                this.portlet.renderAction( aNm );
        }
    },

    _isWindowActionEnabled: function( aNm, aState, aMode )
    {
        var jsObj = jetspeed;
        var jsId = jsObj.id;
        var enabled = false;
        if ( this.minimizeTempRestore != null )
        {
            if ( this.portlet )
            {
                var actionDef = this.portlet.getAction( aNm );
                if ( actionDef != null )
                {
                    if ( actionDef.id == jsId.ACT_REMOVEPORTLET )
                    {
                        if ( jsObj.page.editMode && this.getLayoutActionsEnabled() )
                            enabled = true;
                    }
                }
            }
        }
        else if ( aNm == jsId.ACT_MENU )
        {
            if ( ! this._windowActionMenuIsEmpty() )
                enabled = true;
        }
        else if ( jsObj.prefs.windowActionDesktop[ aNm ] != null )
        {
            if ( this.getLayoutActionsEnabled() )
            {
                if ( aNm == jsId.ACT_DESKTOP_HEIGHT_EXPAND )
                {
                    if ( ! this.heightToFit )
                        enabled = true;
                }
                else if ( aNm == jsId.ACT_DESKTOP_HEIGHT_NORMAL )
                {
                    if ( this.heightToFit )
                        enabled = true;
                }
                else if ( aNm == jsId.ACT_DESKTOP_TILE && jsObj.prefs.windowTiling )
                {
                    if ( ! this.posStatic )
                        enabled = true;
                }
                else if ( aNm == jsId.ACT_DESKTOP_UNTILE )
                {
                    if ( this.posStatic )
                        enabled = true;
                }
            }
        }
        else if ( this.portlet )
        {
            var actionDef = this.portlet.getAction( aNm );
            if ( actionDef != null )
            {
                if ( actionDef.id == jsId.ACT_REMOVEPORTLET )
                {
                    if ( jsObj.page.editMode && this.getLayoutActionsEnabled() )
                        enabled = true;
                }
                else if ( actionDef.type == jsId.PORTLET_ACTION_TYPE_MODE )
                {
                    if ( aNm != aMode )
                    {
                        enabled = true; 
                    }
                }
                else
                {   // assume actionDef.type == jsId.PORTLET_ACTION_TYPE_STATE
                    if ( aNm != aState )
                    {
                        enabled = true;
                    }
                }
            }
            else
            {
                enabled = true;        // BOZO:NOW: debug menu item
            }
        }
        else
        {   // adjust visible action buttons - BOZO:NOW: this non-portlet case needs more attention
            if ( aNm == jsId.ACT_MAXIMIZE )
            {
                if ( aNm != this.windowState && this.minimizeTempRestore == null )
                {
                    enabled = true;
                }
            }
            else if ( aNm == jsId.ACT_MINIMIZE )
            {
                if ( aNm != this.windowState )
                {
                    enabled = true;
                }
            }
            else if ( aNm == jsId.ACT_RESTORE )
            {
                if ( this.windowState == jsId.ACT_MAXIMIZE || this.windowState == jsId.ACT_MINIMIZE )
                {
                    enabled = true;
                }
            }
            else
            {
                enabled = true;    // BOZO:NOW: debug menu item
            }
        }
        return enabled;
    },
    _windowActionMenuIsEmpty: function()
    {   // meant to be called from within _isWindowActionEnabled call for ACT_MENU
        var aState = null;
        var aMode = null;
        if ( this.portlet )
        {
            aState = this.portlet.getCurrentActionState();
            aMode = this.portlet.getCurrentActionMode();
        }
        var actionMenuIsEmpty = true;
        for ( var aNm in this.actionMenus )
        {
            var menuitem = this.actionMenus[ aNm ];
            if ( aNm != jetspeed.id.ACT_MENU && this._isWindowActionEnabled( aNm, aState, aMode ) )
            {
                actionMenuIsEmpty = false;
                break;
            }
        }
        return actionMenuIsEmpty ;
    },

    windowActionButtonSync: function()
    {
        var hideButtons = this.decConfig.windowActionButtonHide;
        var aState = null;
        var aMode = null;
        if ( this.portlet )
        {
            aState = this.portlet.getCurrentActionState();
            aMode = this.portlet.getCurrentActionMode();
        }
        for ( var aNm in this.actionButtons )
        {
            var showBtn = false;
            if ( ! hideButtons || this.titleLit )
            {
                showBtn = this._isWindowActionEnabled( aNm, aState, aMode );
            }
            var buttonNode = this.actionButtons[ aNm ];
            if ( showBtn )
                buttonNode.style.display = "";
            else
                buttonNode.style.display = "none";
        }
    },

    _postCreateMaximizeWindow: function()
    {
        this.maximizeWindow();
        this.windowActionButtonSync();
    },

    minimizeWindowTemporarily: function()
    {
        if ( this.minimizeTempRestore == null )
        {
            this.minimizeTempRestore = this.windowState;
            if ( this.windowState != jetspeed.id.ACT_MINIMIZE )
            {
                this.minimizeWindow();
            }
            this.windowActionButtonSync();
        }
    },
    restoreFromMinimizeWindowTemporarily: function()
    {
        var restoreToWindowState = this.minimizeTempRestore;
        this.minimizeTempRestore = null;
        if ( restoreToWindowState )
        {
            if ( restoreToWindowState != jetspeed.id.ACT_MINIMIZE )
            {
                this.restoreWindow();
            }
            this.windowActionButtonSync();
        }
    },
    
    minimizeWindow: function( evt )
    {
        if ( ! this.tbNode )
            return;

        var jsObj = jetspeed;
        if ( this.windowState == jetspeed.id.ACT_MAXIMIZE )
        {
            this.showAllPortletWindows() ;
            this.restoreWindow( evt );
        }

        this._updtDimsObj( false );

        var disIdx = jsObj.css.cssDis;
        this.cNodeCss[ disIdx ] = "none";
        if ( this.rbNodeCss )
            this.rbNodeCss[ disIdx ] = "none";

        //this._alterCss( true, true );

        //dojo.html.setContentBox( this.domNode, { height: dojo.html.getMarginBox( this.tbNode ).height } );

        // BOZO:WIDGET: xxxx this needs lots of attention!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!1

        this.containerNode.style.display = "none";
        if ( this.rbNode )
            this.rbNode.style.display = "none";
        dojo.html.setContentBox( this.domNode, { height: dojo.html.getMarginBox( this.tbNode ).height } );


        //this.domNode.style.height = "";         // xxxx   hack to avoid setting minimized height in dims object - call to setContentBox was used previously
        //this.containerNode.style.height = "";   // xxxx   hack to avoid setting minimized height in dims object - call to setContentBox was used previously
    
        this.windowState = jsObj.id.ACT_MINIMIZE;
    },
    showAllPortletWindows: function()
    {
        var allPWwidgets = jetspeed.page.getPWins( false );
        for ( var i = 0 ; i < allPWwidgets.length ; i++ )
        {
            var showPWwidget = allPWwidgets[i] ;
            if ( showPWwidget )
            {
                showPWwidget.domNode.style.display = "";
            }
        }
    },
    hideAllPortletWindows: function( excludeWidgetIds )
    {
        var allPWwidgets = jetspeed.page.getPWins( false );
        for ( var i = 0 ; i < allPWwidgets.length ; i++ )
        {
            var hidePWwidget = allPWwidgets[i] ;
            if ( hidePWwidget && excludeWidgetIds && excludeWidgetIds.length > 0 )
            {
                for ( var exclI = 0 ; exclI < excludeWidgetIds.length ; exclI++ )
                {
                    if ( hidePWwidget.widgetId == excludeWidgetIds[exclI] )
                    {
                        hidePWwidget = null;
                        break;
                    }
                }
            }
            if ( hidePWwidget )
            {
                hidePWwidget.domNode.style.display = "none";
            }
        }
    },
    maximizeWindow: function( evt )
    {
        var jsObj = jetspeed;
        var jsId = jsObj.id;
        var dNode = this.domNode;
        var hideAllBut = [ this.widgetId ];
        //if ( this.dbOn )
        //    hideAllBut.push( jetspeed.debugWindowId() );
        this.hideAllPortletWindows( hideAllBut ) ;
        if ( this.windowState == jsId.ACT_MINIMIZE )
        {
            this.restoreWindow( evt );
        }
        var preMaxPosStatic = this.posStatic;
        this.preMaxPosStatic = preMaxPosStatic;
        this.preMaxHeightToFit = this.heightToFit;
        var tiledStateWillChange = preMaxPosStatic;

        this._updtDimsObj( tiledStateWillChange );

        var jetspeedDesktop = document.getElementById( jsId.DESKTOP );
        var yPos = dojo.html.getAbsolutePosition( jetspeedDesktop, true ).y;    // passing true to fix position at top (so not affected by vertically scrolled window)
        var viewport = dojo.html.getViewport();
        var docPadding = dojo.html.getPadding( jsObj.docBody );
        
        // hardcoded to fill document.body width leaving 1px on each side
        this.dimsUntiledTemp = { width: viewport.width - docPadding.width - 2,
                                 height: viewport.height - docPadding.height - yPos,
                                 left: 1,
                                 top: yPos };

        this._setTitleBarDragging( true, jsObj.css, false );

        this.posStatic = false;
        this.heightToFit = false;

        this._alterCss( true, true );

        if ( preMaxPosStatic )
            jetspeedDesktop.appendChild( dNode );

		this.windowState = jsId.ACT_MAXIMIZE;
	},
	restoreWindow: function( evt )
    {
        var jsObj = jetspeed;
        var jsId = jsObj.id;
        var jsCss = jsObj.css;
        var dNode = this.domNode;
        var currentlyAbsolute = false;
        if ( dNode.style.position == "absolute" )
        {
            currentlyAbsolute = true;
        }

        var lastPI = null;
        if ( this.windowState == jsId.ACT_MAXIMIZE )
        {
            this.showAllPortletWindows() ;
            this.posStatic = this.preMaxPosStatic;
            this.heightToFit = this.preMaxHeightToFit;
            this.dimsUntiledTemp = null;
        }
        
        var disIdx = jsCss.cssDis;
        this.cNodeCss[ disIdx ] = "block";
        if ( this.rbNodeCss )
            this.rbNodeCss[ disIdx ] = "block";
        
        var dimsPrevious = this.getDimsObj( this.posStatic );
        
        this.windowState = jsId.ACT_RESTORE;  // "normal"

        this._setTitleBarDragging( true, jsObj.css );

        this._alterCss( true, true );

        if ( this.posStatic && currentlyAbsolute )
        {   // tiled window in maximized needs to be placed back in previous column/row
            if ( dimsPrevious != null && dimsPrevious.columnInfo != null && dimsPrevious.columnInfo.columnIndex != null )
            {
                var columnElmt = jsObj.page.columns[ dimsPrevious.columnInfo.columnIndex ];
                if ( dimsPrevious.columnInfo.previousSibling )
                    dojo.dom.insertAfter( dNode, dimsPrevious.columnInfo.previousSibling );
                else if ( dimsPrevious.columnInfo.nextSibling )
                    dojo.dom.insertBefore( dNode, dimsPrevious.columnInfo.nextSibling );
                else
                    columnElmt.domNode.appendChild( dNode );
            }
            else
            {
                if ( jsObj.page.columns != null && jsObj.page.columns.length > 0 )
                    dojo.dom.prependChild( dNode, jsObj.page.columns[ 0 ].domNode );
            }
        }
	},
    _updtDimsObj: function( tiledStateWillChange )
    {
        var jsObj = jetspeed;
        var jsId = jsObj.id;
        var djObj = dojo;
        var dNode = this.domNode;
        var posStatic = this.posStatic;
        var dimsCurrent = this.getDimsObj( posStatic );
        if ( posStatic )
        {
            if ( tiledStateWillChange )
            {   // record col/row location
                var columnInfo = {};
                var sibling = djObj.dom.getPreviousSiblingElement( dNode );
                if ( sibling )
                    columnInfo.previousSibling = sibling;
                else
                {
                    sibling = djObj.dom.getNextSiblingElement( dNode );
                    if ( sibling )
                        columnInfo.nextSibling = sibling;
                }
                columnInfo.columnIndex = this.getPageColumnIndex();
                dimsCurrent.columnInfo = columnInfo;
            }
            /*
            if ( this.windowState != jsId.ACT_MINIMIZE && this.windowState != jsId.ACT_MAXIMIZE )
            {
                var h = null, cssH = this.dNodeCss[ jsObj.css.cssH ];
                if ( cssH != null && cssH.length > 0 )
                    h = new Number( cssH );
                if ( isNaN( h ) )
                    h = null;
                dimsCurrent.height = h;
            }
            dimsCurrent.width = null;
            */
        }
        else
        {
            //if ( this.windowState != jsId.ACT_MINIMIZE && this.windowState != jsId.ACT_MAXIMIZE )
            //{
            //    var domNodeMarginBox = djObj.html.getMarginBox( dNode ) ;
            //    this.dimsUntiled =
            //    {
            //        width: domNodeMarginBox.width,
            //        height: domNodeMarginBox.height,
            //        left: dNode.style.left,
            //        top: dNode.style.top,
            //    };
            //}
        }
    },

    getLayoutActionsEnabled: function()
    {
        return ( this.windowState != jetspeed.id.ACT_MAXIMIZE && ( ! this.portlet || ! this.portlet.layoutActionsDisabled ) );
    },
    _setTitleBarDragging: function( suppressStyleUpdate, jsCss, enableDrag )
    {
        var tbNode = this.tbNode;
        if ( ! tbNode )
            return;

        if ( typeof enableDrag == "undefined" )
            enableDrag = this.getLayoutActionsEnabled();
        
        var resizeHandle = this.resizeHandle;
        var cursorVal = null;
        if ( enableDrag )
        {
            cursorVal = this.decConfig.dragCursor;
            if ( resizeHandle )
                resizeHandle.domNode.style.display = "";
            if ( this.drag )
                this.drag.enable();
        }
        else
        {
            cursorVal = "default";
            if ( resizeHandle )
                resizeHandle.domNode.style.display = "none";
            if ( this.drag )
                this.drag.disable();
        }
        this.tbNodeCss[ jsCss.cssCur ] = cursorVal;
        if ( ! suppressStyleUpdate )
            tbNode.style.cursor = cursorVal;
    },

    onMouseDown: function( /*Event*/ evt )
    {   // summary: callback for click anywhere in window
        this.bringToTop();
    },
    bringToTop: function( evt, inclStatic )
    {
        if ( ! this.posStatic )
        {   // bring-to-front
            var jsObj = jetspeed;
            var jsPage = jsObj.page;
            var jsCss = jsObj.css;
            var dNodeCss = this.dNodeCss;
            var zHigh = jsPage.getPWinHighZIndex();
            var zCur = dNodeCss[ jsCss.cssZIndex ];
            if ( zHigh != zCur )
            {
                var zTop = this._setAsTopZIndex( jsPage, jsCss, dNodeCss, false );
                if ( this.windowInitialized )
                {
                    this.domNode.style.zIndex = String( zTop );
                    if ( this.portlet && this.windowState != jetspeed.id.ACT_MAXIMIZE )
                        this.portlet.submitWinState();
                }
                //dojo.debug( "bringToTop [" + this.widgetId + "] zIndex   before=" + zCur + " after=" + zTop );
            }
        }
        else if ( inclStatic )
        {
            var zTop = this._setAsTopZIndex( jsPage, jsCss, dNodeCss, true );
            if ( this.windowInitialized )
            {
                this.domNode.style.zIndex = String( zTop );
            }
        }
    },
    _setAsTopZIndex: function( jsPage, jsCss, dNodeCss, posStatic )
    {
        var zTop = jsPage.getPWinTopZIndex( posStatic );
        dNodeCss[ jsCss.cssZIndex ] = zTop;
        return zTop;
    },
    makeUntiled: function()
    {
        var jsObj = jetspeed;
        this._updtDimsObj( true );
        
        this._makeUntiledDims();
        this._setAsTopZIndex( jsObj.page, jsObj.css, this.dNodeCss, false );

        this._alterCss( true, true );
        
        var addToElmt = document.getElementById( jetspeed.id.DESKTOP );
        addToElmt.appendChild( this.domNode );

        if ( this.windowState == jsObj.id.ACT_MINIMIZE )
            this.minimizeWindow();

        if ( this.portlet )
            this.portlet.submitWinState();

        this._addUntiledEvents();
    },
    _makeUntiledDims: function()
    {
        var dNode = this.domNode ;

        this.posStatic = false;

        var dimsUntiled = this.getDimsObj( false );

        if ( dimsUntiled.width == null || dimsUntiled.height == null ||
             dimsUntiled.left == null || dimsUntiled.top == null )
        {   // determine initial untiled position based on current tiled position
            var djH = dojo.html;
            var winAbsPos = djH.getAbsolutePosition( dNode, true );
            var winMarginTop = djH.getPixelValue( dNode, "margin-top", true );
            var winMarginLeft = djH.getPixelValue( dNode, "margin-left", true );
            var dNodeMarginBox = djH.getMarginBox( dNode );
            dimsUntiled.width = dNodeMarginBox.width;
            dimsUntiled.height = dNodeMarginBox.height;
            dimsUntiled.left = winAbsPos.x - winMarginTop;
            dimsUntiled.top = winAbsPos.y - winMarginLeft;
        }
    },
    makeTiled: function()
    {
        this.posStatic = true;

        var jsObj = jetspeed;
        var zTop = this._setAsTopZIndex( jsObj.page, jsObj.css, this.dNodeCss, true );
        
        this.restoreWindow();

        if ( this.portlet )
            this.portlet.submitWinState();

        this._removeUntiledEvents();
    },
    _addUntiledEvents: function()
    {
        if ( this._untiledEvts == null )
        {
            this._untiledEvts = [ jetspeed.ui.evtConnect( "after", this.domNode, "onmousedown", this, "onMouseDown" ) ];
        }
    },
    _removeUntiledEvents: function()
    {
        if ( this._untiledEvts != null )
        {
            jetspeed.ui.evtDisconnectWObjAry( this._untiledEvts );
            delete this._untiledEvts;
        }
    },

    makeHeightToFit: function( suppressSubmitChange, suppressLogging )
    {   // suppressLogging is to support contentChanged
        var domNodePrevMarginBox = dojo.html.getMarginBox( this.domNode ) ;

        this.heightToFit = true;

        this._alterCss( false, true );
                //  xxxx   previously called adjPosToDeskState twice - once before resize call, and once after

        //if ( suppressLogging == null || suppressLogging != true )
        //{   // flags are to avoid init problems with dojo-debug window when height-to-fit is set (causing stack overflow when dojo.debug() is called)
            //dojo.debug( "makeHeightToFit [" + this.widgetId + "] prev w=" + domNodePrevMarginBox.width + " h=" + domNodePrevMarginBox.height + "  new w=" + domNodeMarginBox.width + " h=" + domNodeMarginBox.height );
        //}
    
        if ( ! suppressSubmitChange && this.portlet )
            this.portlet.submitWinState();
    },
    makeHeightVariable: function( suppressSubmitChange, isResizing )
    {
        //var domNodePrevMarginBox = dojo.html.getMarginBox( this.domNode ) ;
                // xxxx   previously called getMarginBox twice - once before adjPosToDeskState call, and once after

        //dojo.debug( "makeHeightVariable [" + this.widgetId + "] prev w=" + domNodePrevMarginBox.width + " h=" + domNodePrevMarginBox.height + "  new w=" + domNodeMarginBox.width + " h=" + domNodeMarginBox.height );
        //dojo.debug( "makeHeightVariable [" + this.widgetId + "] containerNode PREV style.width=" + this.containerNode.style.width + " style.height=" + this.containerNode.style.height );
        
        var dimsCurrent = this.getDimsObj( this.posStatic );

        var domNodeMarginBox = dojo.html.getMarginBox( this.domNode ) ;
        dimsCurrent.width = domNodeMarginBox.width;
        dimsCurrent.height = domNodeMarginBox.height + 3;   // the plus 3 is mysteriously useful for avoiding initial scrollbar

        this.heightToFit = false;

        this._alterCss( false, true );
    
        //dojo.debug( "makeHeightVariable [" + this.widgetId + "] containerNode NEW style.width=" + this.containerNode.style.width + " style.height=" + this.containerNode.style.height );

        if ( ! isResizing && this.iframesInfo )
            dojo.lang.setTimeout( this, this._forceRefreshZIndex, 70 );   // needs a jolt to make iframe adjust

        if ( ! suppressSubmitChange && this.portlet )
            this.portlet.submitWinState();
    },

    resizeTo: function( w, h, force )
    {
        var dimsCurrent = this.getDimsObj( this.posStatic );

        dimsCurrent.width = w;
        dimsCurrent.height = h;

        this._alterCss( false, false, true );

        if ( ! this.windowIsSizing )
        {
            var resizeHandle = this.resizeHandle;
            if ( resizeHandle != null && resizeHandle._isSizing )
            {
                jetspeed.ui.evtConnect( "after", resizeHandle, "_endSizing", this, "endSizing" );
                // NOTE: connecting directly to document.body onmouseup results in notification for second and subsequent onmouseup
                this.windowIsSizing = true;
            }
        }
    },

    getDimsObj: function( posStatic )
    {
        return ( posStatic ? ( (this.dimsTiledTemp != null) ? this.dimsTiledTemp : this.dimsTiled ) : ( (this.dimsUntiledTemp != null) ? this.dimsUntiledTemp : this.dimsUntiled ) );
    },

    _alterCss: function( changeTiledState, changeHeightToFit, changeResize, changePosition, suppressStyleUpdate )
    {
        var jsObj = jetspeed;
        var jsCss = jsObj.css;
        var iframesInfoCur = this.iframesInfo;
        var iframeLayout = ( iframesInfoCur && iframesInfoCur.layout );
        var layoutExtents = ( ! iframeLayout ? this.decConfig.layoutExtents : this.decConfig.layoutExtentsIFrame );
        var posStatic = this.posStatic;
        var heightToFit = this.heightToFit;

        var setWidth = ( changeTiledState || ( changeResize && ! posStatic ) );
        var setHeight = ( changeHeightToFit || changeResize );
        var setPosition = ( changeTiledState || changePosition );
        var setOverflow = ( changeHeightToFit || ( changeResize && iframeLayout ) );

        var dNodeCss = this.dNodeCss, cNodeCss = null, tbNodeCss = null, rbNodeCss = null;
        var dimsCurrent = this.getDimsObj( posStatic );

        if ( changeTiledState )
        {
            dNodeCss[ jsCss.cssPos ] = ( posStatic ? "static" : "absolute" );
        }

        var setIFrame = null, setIFrameH = null;
        if ( changeHeightToFit )
        {
            if ( iframeLayout )
            {
                var ifrmInfo = this.getIFrames( false );
                if ( ifrmInfo && ifrmInfo.iframes.length == 1 && iframesInfoCur.iframesSize != null && iframesInfoCur.iframesSize.length == 1 )
                {
                    var ifrmH = iframesInfoCur.iframesSize[0].h;
                    if ( ifrmH != null )
                    {
                        setIFrame = ifrmInfo.iframes[0];
                        setIFrameH = ( heightToFit ? ifrmH : ( ! jsObj.UAie ? "100%" : "99%" ) );
                        suppressStyleUpdate = false;
                    }
                }
            }
        }

        if ( setOverflow )
        {
            cNodeCss = this.cNodeCss;
            var ofXIdx = jsCss.cssOx, ofYIdx = jsCss.cssOy;
            if ( heightToFit && ! iframeLayout )
            {
                dNodeCss[ ofYIdx ] = "visible";
                cNodeCss[ ofYIdx ] = "visible";
            }
            else
            {
                dNodeCss[ ofYIdx ] = "hidden";
                cNodeCss[ ofYIdx ] = ( ! iframeLayout ? "auto" : "hidden" );;
            }            
        }

        if ( setPosition )
        {
            var lIdx = jsCss.cssL, luIdx = jsCss.cssLU;
            var tIdx = jsCss.cssT, tuIdx = jsCss.cssTU;
            if ( posStatic )
            {
                dNodeCss[ lIdx ] = "auto";
                dNodeCss[ luIdx ] = "";
                dNodeCss[ tIdx ] = "auto";
                dNodeCss[ tuIdx ] = "";
            }
            else
            {
                dNodeCss[ lIdx ] = dimsCurrent.left;
                dNodeCss[ luIdx ] = "px";
                dNodeCss[ tIdx ] = dimsCurrent.top;
                dNodeCss[ tuIdx ] = "px";
            }
        }

        if ( setHeight )
        {
            cNodeCss = this.cNodeCss;
            var hIdx = jsCss.cssH, huIdx = jsCss.cssHU;
            if ( heightToFit )
            {
                dNodeCss[ hIdx ] = "";
                dNodeCss[ huIdx ] = "";
                cNodeCss[ hIdx ] = "";
                cNodeCss[ huIdx ] = "";
            }
            else
            {
                var h = dimsCurrent.height;
                dNodeCss[ hIdx ] = (h - layoutExtents.dNode.lessH);
                dNodeCss[ huIdx ] = "px";
                cNodeCss[ hIdx ] = (h - layoutExtents.cNode.lessH - layoutExtents.lostHeight);
                cNodeCss[ huIdx ] = "px";
            }
        }

        if ( setWidth )
        {
            cNodeCss = this.cNodeCss;
            tbNodeCss = this.tbNodeCss;
            rbNodeCss = this.rbNodeCss;
            var wIdx = jsCss.cssW, wuIdx = jsCss.cssWU;
            if ( posStatic )
            {
                dNodeCss[ wIdx ] = "";
                dNodeCss[ wuIdx ] = "";
                cNodeCss[ wIdx ] = "";
                cNodeCss[ wuIdx ] = "";
                if ( tbNodeCss )
                {
                    tbNodeCss[ wIdx ] = "";
                    tbNodeCss[ wuIdx ] = "";
                }
                if ( rbNodeCss )
                {
                    rbNodeCss[ wIdx ] = "";
                    rbNodeCss[ wuIdx ] = "";
                }
            }
            else
            {
                var w = dimsCurrent.width;
                var wChild = (w - layoutExtents.lostWidth);
                dNodeCss[ wIdx ] = (w - layoutExtents.dNode.lessW);
                dNodeCss[ wuIdx ] = "px";
                cNodeCss[ wIdx ] = (wChild - layoutExtents.cNode.lessW);
                cNodeCss[ wuIdx ] = "px";
                if ( tbNodeCss )
                {
                    tbNodeCss[ wIdx ] = (wChild - layoutExtents.tbNode.lessW);
                    tbNodeCss[ wuIdx ] = "px";
                }
                if ( rbNodeCss )
                {
                    rbNodeCss[ wIdx ] = (wChild - layoutExtents.rbNode.lessW);
                    rbNodeCss[ wuIdx ] = "px";
                }
            }
        }

        if ( ! suppressStyleUpdate )
        {
            this.domNode.style.cssText = dNodeCss.join( "" );
            if ( cNodeCss )
                this.containerNode.style.cssText = cNodeCss.join( "" );
            if ( tbNodeCss )
                this.tbNode.style.cssText = tbNodeCss.join( "" );
            if ( rbNodeCss )
                this.rbNode.style.cssText = rbNodeCss.join( "" );
        }
        if ( setIFrame && setIFrameH )
            this._deferSetIFrameH( setIFrame, setIFrameH, false, 50 );
            //window.setTimeout( function() { setIFrame.height = setIFrameH; }, 50 ) ;
    },

    _deferSetIFrameH: function( setIFrame, setIFrameH, forceRefresh, waitFor, forceRefreshWaitFor )
    {
        if ( ! waitFor ) waitFor = 100;
        var pWin = this;
        window.setTimeout( function()
        {
            //dojo.debug( "_deferSetIFrameH set iframe height to " + setIFrameH + " (current=" + setIFrame.height + ")" );
            setIFrame.height = setIFrameH;
            if ( forceRefresh )
            {
                if ( forceRefreshWaitFor == null ) forceRefreshWaitFor = 50;
                if ( forceRefreshWaitFor == 0 )
                    pWin._forceRefreshZIndexAndForget();
                else
                    dojo.lang.setTimeout( pWin, pWin._forceRefreshZIndexAndForget, forceRefreshWaitFor );
            }
        }, waitFor ) ;
    },

    _forceRefreshZIndex: function()
    {   // attempts to force a refresh with a zIndex change
        var jsObj = jetspeed;
        var zTop = this._setAsTopZIndex( jsObj.page, jsObj.css, this.dNodeCss, this.posStatic );
        this.domNode.style.zIndex = String( zTop );
    },
    _forceRefreshZIndexAndForget: function()
    {   // attempts to force a refresh with a zIndex change - does not record new zIndex value in dNodeCss
        var zTop = jetspeed.page.getPWinTopZIndex( this.posStatic );
        this.domNode.style.zIndex = String( zTop );
    },
    _forceRefreshFromCss: function()
    {
        this.domNode.style.cssText = this.dNodeCss.join( "" );
    },

    getIFrames: function( includeSize )
    {
        var ifrms = this.containerNode.getElementsByTagName( "iframe" );
        if ( ifrms && ifrms.length > 0 )
        {
            if ( ! includeSize ) return { iframes: ifrms };
            var ifrmsSize = [];
            for ( var i = 0 ; i < ifrms.length ; i++ )
            {
                var ifrm = ifrms[i];

                var w = new Number( String( ifrm.width ) );
                w = ( isNaN( w ) ? null : String( ifrm.width ) );

                var h = new Number( String( ifrm.height ) );
                h = ( isNaN( h ) ? null : String( ifrm.height ) );

                ifrmsSize.push( { w: w, h: h } );
            }
            return { iframes: ifrms, iframesSize: ifrmsSize };
        }
        return null;
    },

    contentChanged: function( evt )
    {   // currently used for dojo-debug window only
        if ( this.inContentChgd == false )
        {
            this.inContentChgd = true;
            if ( this.heightToFit )
            {
                this.makeHeightToFit( true, true );
            }
            this.inContentChgd = false;
        }
    },
 
    closeWindow: function()
    {
        var jsObj = jetspeed;
        var jsUI = jsObj.ui;
        var jsPage = jsObj.page;
        var djObj = dojo;
        var djEvtObj = djObj.event;
        var actionCtxMenu = this.actionMenuWidget;
        if ( actionCtxMenu != null )
        {
            actionCtxMenu.destroy();
            this.actionMenuWidget = actionCtxMenu = null;
        }
        
        jsPage.tooltipMgr.removeNodes( this.tooltips );
        this.tooltips = ttps = null;

        if ( this.iframesInfo )
            jsPage.unregPWinIFrameCover( this );

        var aBtns = this.actionButtons;
        if ( aBtns )
        {
            var hasTooltip = ( this.decConfig != null && this.decConfig.windowActionButtonTooltip );
            for ( var aNm in aBtns )
            {
                var aBtn = aBtns[ aNm ];
                if ( aBtn )
                {
                    jsUI.evtDisconnect( "after", aBtn, "onclick", this, "windowActionButtonClick", djEvtObj );
                    if ( ! hasTooltip )
                        jsUI.evtDisconnect( "after", aBtn, "onmousedown", djEvtObj.browser, "stopEvent", djEvtObj );
                }
            }
            this.actionButtons = aBtns = null;
        }

        if ( this.drag )
        {
            this.drag.destroy( djObj, djEvtObj, jsObj, jsUI );
            this.drag = null;
        }
    
        if ( this.resizeHandle )
        {
            this.resizeHandle.destroy( djEvtObj, jsObj, jsUI );
            this.resizeHandle = null;
        }

        if ( this.subWidgetEndIndex > this.subWidgetStartIndex )
        {
            djObj.debug( "closeWindow subwidgets " + this.subWidgetStartIndex + " / " + this.subWidgetEndIndex );
            var djWMgr = djObj.widget.manager;
            for ( var i = this.subWidgetEndIndex -1 ; i >= this.subWidgetStartIndex ; i-- )
            {
                try
                {
                    if ( djWMgr.widgets.length > i )
                    {
			            var subWidget = djWMgr.widgets[i];
                        if ( subWidget != null )
                        {
                            var swT = subWidget.widgetType;
                            var swI = subWidget.widgetId;
                            subWidget.destroy();
                            djObj.debug( "destroyed sub-widget[" + i + "]: " + swT + " " + swI ) ;
                        }
                    }
		        }
                catch(e){ }
            }
        }

        this._removeUntiledEvents();

        // BOZO:WIDGET: destroy script content

        var dNode = this.domNode;
        if ( dNode && dNode.parentNode )
            dNode.parentNode.removeChild( dNode );

        this.domNode = null;
        this.containerNode = null;
        this.tbNode = null;
        this.rbNode = null;
    },
    dumpPos: function()
    {
        var djObj = dojo;
        var djH = djObj.html;
        var dNode = this.domNode;
        var cNode = this.containerNode;
        var winAbsPos = djH.getAbsolutePosition( dNode, true );
        var dNodeMarginBox = djH.getMarginBox( dNode );
        var cNodeMarginBox = djH.getMarginBox( cNode );
        var cNodeContentBox = djH.getContentBox( cNode );
        var layoutExtents = this.decConfig.layoutExtents;
        var ind = jetspeed.debugindent;
        djObj.hostenv.println( "wnd-dims [" + this.widgetId + "]  abs.x=" + winAbsPos.x + "  abs.y=" + winAbsPos.y + "  z=" + dNode.style.zIndex );
        djObj.hostenv.println( ind + "mb.width=" + dNodeMarginBox.width + "  mb.height=" + dNodeMarginBox.height );
        djObj.hostenv.println( ind + "style.width=" + dNode.style.width + "  style.height=" + dNode.style.height );
        djObj.hostenv.println( ind + "cnt.mb.width=" + cNodeMarginBox.width + "  cnt.mb.height=" + cNodeMarginBox.height );
        djObj.hostenv.println( ind + "cnt.cb.width=" + cNodeContentBox.width + "  cnt.cb.height=" + cNodeContentBox.height );
        djObj.hostenv.println( ind + "cnt.style.width=" + cNode.style.width + "  cnt.style.height=" + cNode.style.height );
        djObj.hostenv.println( ind + "dNodeCss=" + this.dNodeCss.join("") );
        djObj.hostenv.println( ind + "cNodeCss=" + this.cNodeCss.join("") );
        djObj.hostenv.println( ind + "layoutExtents: " + "dNode.lessW=" + layoutExtents.dNode.lessW + " dNode.lessH=" + layoutExtents.dNode.lessH + " lostW=" + layoutExtents.lostWidth + " lostH=" + layoutExtents.lostHeight + " cNode.lessW=" + layoutExtents.cNode.lessW + " cNode.lessH=" + layoutExtents.cNode.lessH );
        djObj.hostenv.println( ind + "dimsTiled=" + jetspeed.printobj( this.dimsTiled ) );
        djObj.hostenv.println( ind + "dimsUntiled=" + jetspeed.printobj( this.dimsUntiled ) );
        if ( this.dimsTiledTemp != null )
            djObj.hostenv.println( ind + "dimsTiledTemp=" + jetspeed.printobj( this.dimsTiledTemp ) );
        if ( this.dimsUntiledTemp != null )
            djObj.hostenv.println( ind + "dimsUntiledTemp=" + jetspeed.printobj( this.dimsUntiledTemp ) );
        //" document-width=" + dojo.html.getMarginBox( document[ "body" ] ).width + " document-height=" + dojo.html.getMarginBox( document[ "body" ] ).height
    },
    
    getPageColumnIndex: function()
    {
        return jetspeed.page.getColIndexForNode( this.domNode );
    },
    endSizing: function(e)
    {
        jetspeed.ui.evtDisconnect( "after", this.resizeHandle, "_endSizing", this, "endSizing" );
        this.windowIsSizing = false;
        if ( this.portlet && this.windowState != jetspeed.id.ACT_MAXIMIZE )
            this.portlet.submitWinState();
    },
    endDragging: function( posObj )
    {
        var posStatic = this.posStatic;
        if ( ! posStatic )
        {
            if ( posObj && posObj.left != null && posObj.top != null )
            {
                var dimsCurrent = this.getDimsObj( posStatic );
                dimsCurrent.left = posObj.left; 
                dimsCurrent.top = posObj.top;
                this._alterCss( false, false, false, true, true );
            }
        }
        else
        {
            this._alterCss( true );
        }
        if ( this.portlet && this.windowState != jetspeed.id.ACT_MAXIMIZE )
            this.portlet.submitWinState();
        if ( this.ie6 )
            dojo.lang.setTimeout( this, this._IEPostDrag, jetspeed.widget.ie6PostDragAddDelay );
    },

    /*   // BOZO:WIDGET: titleDim feature requires event.connects onMouseOver:titleMouseOver, onMouseOut:titleMouseOut
    titleLight: function()
    {
        var lght = [];
        var aState = null;
        var aMode = null;
        if ( this.portlet )
        {
            aState = this.portlet.getCurrentActionState();
            aMode = this.portlet.getCurrentActionMode();
        }
        for ( var aNm in this.actionButtons )
        {
            var showBtn = this._isWindowActionEnabled( aNm, aState, aMode );
            if ( showBtn )
                lght.push( this.actionButtons[ aNm ] );
        }
        for ( var i = 0 ; i < lght.length ; i++ )
        {
            lght[i].style.display = "" ;
        }
        this.titleLit = true ;
    },
    titleDim: function( immediateForce )
    {
        var dim = [];
        for ( var aNm in this.actionButtons )
        {
            var buttonNode = this.actionButtons[ aNm ];
            if ( buttonNode.style.display != "none" )
            {
                dim.push( buttonNode );
            }
        }
    
        for ( var i = 0 ; i < dim.length ; i++ )
        {
            dim[i].style.display = "none" ;
        }

        this.titleLit = false ;
    },
    titleMouseOver: function( evt )
    {
        if ( this.decConfig.windowActionButtonHide )
        {
            var self = this ;
            this.titleMouseIn = 1 ;   // was ++
            window.setTimeout( function() { if ( self.titleMouseIn > 0 ) { self.titleLight(); self.titleMouseIn = 0; } }, 270 ) ;
        }
    },
    titleMouseOut: function( evt )
    {
        if ( this.decConfig.windowActionButtonHide )
        {
            var self = this ;
            var nTitleMouseIn = this.titleMouseIn ;
            if ( nTitleMouseIn > 0 )
            {
                nTitleMouseIn = 0 ; // was Math.max( 0, ( nTitleMouseIn - 1 ) );
                this.titleMouseIn = nTitleMouseIn ;
            }
            if ( nTitleMouseIn == 0 && this.titleLit )
            {
                window.setTimeout( function() { if ( self.titleMouseIn == 0 && self.titleLit ) { self.titleDim(); } }, 200 ) ;
            }
        }
    },
    */

    getCurWinState: function( volatileOnly )
    {
        var dNode = this.domNode;
        var posStatic = this.posStatic;
        if ( ! dNode ) return null;
        var dNodeStyle = dNode.style;
        var cWinState = {};
        if ( ! posStatic )
            cWinState.zIndex = dNodeStyle.zIndex;
        if ( volatileOnly )
            return cWinState;
        cWinState.width = dNodeStyle.width;
        cWinState.height = dNodeStyle.height;

        cWinState[ jetspeed.id.PP_WINDOW_POSITION_STATIC ] = posStatic;
        cWinState[ jetspeed.id.PP_WINDOW_HEIGHT_TO_FIT ] = this.heightToFit;

        if ( ! posStatic )
        {
            cWinState.left = dNodeStyle.left;
            cWinState.top = dNodeStyle.top;
        }
        else
        {
            var columnRowResult = jetspeed.page.getPortletCurColRow( dNode );
            if ( columnRowResult != null )
            {
                cWinState.column = columnRowResult.column;
                cWinState.row = columnRowResult.row;
                cWinState.layout = columnRowResult.layout;
            }
            else
            {
                dojo.raise( "Cannot not find row/col/layout for window: " + this.widgetId ) ;
                // BOZO:NOW: test this with maximize/minimize
            }
        }
        return cWinState;
    },
    getCurWinStateForPersist: function( volatileOnly )
    {
        var currentState = this.getCurWinState( volatileOnly );
        // get rid of units text
        this._mkNumProp( null, currentState, "left" );
        this._mkNumProp( null, currentState, "top" );
        this._mkNumProp( null, currentState, "width" );
        this._mkNumProp( null, currentState, "height" );
        return currentState;
    },
    _mkNumProp: function( propVal, propsObj, propName )
    {
        var setPropVal = ( propsObj != null && propName != null );
        if ( propVal == null && setPropVal )
            propVal = propsObj[ propName ];
        if ( propVal == null || propVal.length == 0 )
            propVal = 0;
        else
        {
            var sourceNum = "";
            for ( var i = 0 ; i < propVal.length ; i++ )
            {
                var sourceCh = propVal.charAt(i);
                if ( ( sourceCh >= "0" && sourceCh <= "9" ) || sourceCh == "." )
                    sourceNum += sourceCh.toString();
            }
            if ( sourceNum == null || sourceNum.length == 0 )
                sourceNum = "0";
            if ( setPropVal )
                propsObj[ propName ] = sourceNum;
            propVal = new Number( sourceNum );
        }
        return propVal;
    },

    /* IMPORTANT setContent notes:
          We are avoiding a call to ContentPane.splitAndFixPaths for these reasons:
              - it does more than we need (wasting time)
              - we want to use its script processing but we don't need executeScripts to be set to false

          So we have copied the script processing code from ContentPane.splitAndFixPaths (0.4.0), and we call our copy here.

          We set executeScripts=false to delay script execution until after call to dojo.widget.getParser().createSubComponents
              - this allows dojo.addOnLoad to work normally
              - we call ContentPane._executeScripts after calling ContentPane.setContent (which calls createSubComponents)

          We set scriptSeparation=false to opt-out of support for scoping scripts to ContentPane widget instance
              - this feature, while cool, requires script modification in most cases (e.g. if one of your scripts calls another)
              
          Although we don't call ContentPane.splitAndFixPaths, it is notable that adjustPaths=false is likely correct for portlet content
              - e.g. when set to true, security-permissions.css is still fetched but not used (not affected by scriptSeparation)

          A better use of ContentPane features, particularly, scriptSeparation=true, can be accomplished as follows:
              - code: 
                      this.executeScripts = true;
                      this.scriptSeparation = true;
                      this.adjustPaths = false;
                      this.setContent( initialHtmlStr );

              - this requires script content to follow the conventions shown in: security/permissions/view-dojo-scriptScope.vm,
                which works in both portal and desktop, should allow (at least with scripts name collisions), coexistence
                of multiple instances of same portlet (with permissions there would be id collisions among widgets)
    */

    setPortletContent: function( html, url )
    {
        var jsObj = jetspeed;
        var djObj = dojo;
        var initialHtmlStr = html.toString();
        
        if ( ! this.exclPContent )
        {
            initialHtmlStr = '<div class="PContent" >' + initialHtmlStr + '</div>';
        }

        var setContentObj = this._splitAndFixPaths_scriptsonly( initialHtmlStr, url );

        this.subWidgetStartIndex = djObj.widget.manager.widgets.length;

        this.setContent( setContentObj, djObj );

        if ( setContentObj.scripts != null && setContentObj.scripts.length != null && setContentObj.scripts.length > 0 )
        {
            this._executeScripts( setContentObj.scripts, djObj );
            this.onLoad();
        }

        if ( jsObj.debug.setPortletContent )
            djObj.debug( "setPortletContent [" + ( this.portlet ? this.portlet.entityId : this.widgetId ) + "]" );

        var cNode = this.containerNode;
        if ( this.portlet )
            this.portlet.postParseAnnotateHtml( cNode );

        var iframesInfoCur = this.iframesInfo;
        var iframesInfoNew = this.getIFrames( true );
        var setIFrame100P = null, iframeLayoutChg = false;
        if ( iframesInfoNew != null )
        {
            if ( iframesInfoCur == null )
            {
                this.iframesInfo = iframesInfoCur = {};
                var iframeCoverDiv = cNode.ownerDocument.createElement( "div" );
                var coverCl = "portletWindowIFrameCover";
                iframeCoverDiv.className = coverCl;
                cNode.appendChild( iframeCoverDiv );
                if ( jsObj.UAie )
                {
                    iframeCoverDiv.className = (coverCl + "IE") + " " + coverCl;
                    djObj.html.setOpacity( iframeCoverDiv, 0.1 );
                }
                
                iframesInfoCur.iframeCover = iframeCoverDiv;
                jsObj.page.regPWinIFrameCover( this );
            }
            var iframesSize = iframesInfoCur.iframesSize = iframesInfoNew.iframesSize;
            var iframes = iframesInfoNew.iframes;
            var iframesCurLayout = iframesInfoCur.layout;
            var iframesLayout = iframesInfoCur.layout = ( iframes.length == 1 && iframesSize[0].h != null );
            if ( iframesCurLayout != iframesLayout )
                iframeLayoutChg = true;
            if ( iframesLayout )
            {
                if ( ! this.heightToFit )
                    setIFrame100P = iframes[0];

                var wDC = this.decConfig;
                var cNode = this.containerNode;
                cNode.firstChild.className = "PContent portletIFramePContent";
                cNode.className = wDC.cNodeClass + " portletWindowIFrameClient";
                if ( ! wDC.layoutExtentsIFrame )
                {
                    this._createLayoutExtents( wDC, true, this.domNode, cNode, this.tbNode, this.rbNode, djObj, jsObj );
                }
            }
        }
        else if ( iframesInfoCur != null )
        {
            if ( iframesInfoCur.layout )
            {
                this.containerNode.className = this.decConfig.cNodeClass;
                iframeLayoutChg = true;
            }
            this.iframesInfo = null;
            jsObj.page.unregPWinIFrameCover( this );
        }
        if ( iframeLayoutChg )
        {
            this._alterCss( false, false, true );
        }

        if ( this.restoreOnNextRender )
        {
            this.restoreOnNextRender = false;
            this.restoreWindow();
        }
        if ( setIFrame100P )
        {
            this._deferSetIFrameH( setIFrame100P, ( ! jsObj.UAie ? "100%" : "99%" ), true );
        }
        this.subWidgetEndIndex = djObj.widget.manager.widgets.length;
    },

    setContent: function(data, djObj){
        // summary:
        //      Replaces old content with data content, include style classes from old content
        //  data String||DomNode:   new content, be it Document fragment or a DomNode chain
        //          If data contains style tags, link rel=stylesheet it inserts those styles into DOM
        if(this._callOnUnload){ this.onUnload(); }// this tells a remote script clean up after itself
        this._callOnUnload = true;

        this._setContent(data.xml, djObj);
        
        if(this.parseContent){
            var node = this.containerNode;
            var parser = new djObj.xml.Parse();
            var frag = parser.parseElement(node, null, true);
            // createSubComponents not createComponents because frag has already been created
            djObj.widget.getParser().createSubComponents(frag, this);
        }

        //this.onLoad();   // BOZO:WIDGET: why is this disabled?
    },
    _setContent: function(cont, djObj){
        //this.destroyChildren();    // BOZO:WIDGET: what to do here ?
        try{
            var node = this.containerNode;
            while(node.firstChild){
                djObj.html.destroyNode(node.firstChild);
            }
            node.innerHTML = cont;
        }catch(e){
            e.text = "Couldn't load content:"+e.description;
            this._handleDefaults(e, "onContentError");
        }
    },

    onLoad: function(e){
        // summary:
        //      Event hook, is called after everything is loaded and widgetified 
        this._runStack("_onLoadStack");
        this.isLoaded=true;
    },

    onUnload: function(e){
        // summary:
        //      Event hook, is called before old content is cleared
        this._runStack("_onUnloadStack");
        delete this.scriptScope;
    },

    _runStack: function(stName){
        var st = this[stName]; var err = "";
        var scope = this.scriptScope || window;
        for(var i = 0;i < st.length; i++){
            try{
                st[i].call(scope);
            }catch(e){ 
                err += "\n"+st[i]+" failed: "+e.description;
            }
        }
        this[stName] = [];

        if(err.length){
            var name = (stName== "_onLoadStack") ? "addOnLoad" : "addOnUnLoad";
            this._handleDefaults(name+" failure\n "+err, "onExecError", "debug");
        }
    },

    _executeScripts: function(scripts, djObj) {
        // loop through the scripts in the order they came in
        var self = this;
        var cacheScripts = true;
        var tmp = "", code = "";
        for(var i = 0; i < scripts.length; i++){
            if(scripts[i].path){ // remotescript
                djObj.io.bind(this._cacheSetting({
                    "url":      scripts[i].path,
                    "load":     function(type, scriptStr){
                            dojo.lang.hitch(self, tmp = ";"+scriptStr);
                    },
                    "error":    function(type, error){
                            error.text = type + " downloading remote script";
                            self._handleDefaults.call(self, error, "onExecError", "debug");
                    },
                    "mimetype": "text/plain",
                    "sync":     true
                }, cacheScripts));
                code += tmp;
            }else{
                code += scripts[i];
            }
        }


        try{
            if(this.scriptSeparation){
                // not supported
            }else{
                // exec in global, lose the _container_ feature
                var djg = djObj.global();
                if(djg.execScript){
                    djg.execScript(code);
                }else{
                    var djd = djObj.doc();
                    var sc = djd.createElement("script");
                    sc.appendChild(djd.createTextNode(code));
                    (this.containerNode||this.domNode).appendChild(sc);
                }
            }
        }catch(e){
            e.text = "Error running scripts from content:\n"+e.description;
            this._handleDefaults(e, "onExecError", "debug");
        }
    },

    _cacheSetting: function(bindObj, useCache){
        var djLang = dojo.lang;
        for(var x in this.bindArgs){
            if(djLang.isUndefined(bindObj[x])){
                bindObj[x] = this.bindArgs[x];
            }
        }

        if(djLang.isUndefined(bindObj.useCache)){ bindObj.useCache = useCache; }
        if(djLang.isUndefined(bindObj.preventCache)){ bindObj.preventCache = !useCache; }
        if(djLang.isUndefined(bindObj.mimetype)){ bindObj.mimetype = "text/html"; }
        return bindObj;
    },

    _handleDefaults: function(e, handler, messType){
        var djObj = dojo;
        if(!handler){ handler = "onContentError"; }

        if(djObj.lang.isString(e)){ e = {text: e}; }

        if(!e.text){ e.text = e.toString(); }

        e.toString = function(){ return this.text; };

        if(typeof e.returnValue != "boolean"){
            e.returnValue = true; 
        }
        if(typeof e.preventDefault != "function"){
            e.preventDefault = function(){ this.returnValue = false; };
        }
        // call our handler
        this[handler](e);
        if(e.returnValue){
            switch(messType){
                case true: // fallthrough, old compat
                case "alert":
                    alert(e.toString()); break;
                case "debug":
                    djObj.debug(e.toString()); break;
                default:
                // makes sure scripts can clean up after themselves, before we setContent
                if(this._callOnUnload){ this.onUnload(); } 
                // makes sure we dont try to call onUnLoad again on this event,
                // ie onUnLoad before 'Loading...' but not before clearing 'Loading...'
                this._callOnUnload = false;

                // we might end up in a endless recursion here if domNode cant append content
                if(arguments.callee._loopStop){
                    djObj.debug(e.toString());
                }else{
                    arguments.callee._loopStop = true;
                    this._setContent(e.toString(), djObj);
                }
            }
        }
        arguments.callee._loopStop = false;
    },

    onExecError: function(/*Object*/e){
    },

    onContentError: function(/*Object*/e){
    },

    setPortletTitle: function( newPortletTitle )
    {
        if ( newPortletTitle )
            this.title = newPortletTitle;
        else
            this.title = "";
        if ( this.windowInitialized && this.tbTextNode )
        {
            this.tbTextNode.innerHTML = this.title;
        }
    },
    getPortletTitle: function()
    {
        return this.title;
    },

    _splitAndFixPaths_scriptsonly: function( /* String */ s, /* String */ url )
    {
        var forcingExecuteScripts = true;
        var scripts = [] ;
        // deal with embedded script tags 
        // /=/=/=/=/=  begin  ContentPane.splitAndFixPaths   code  =/=/=/=/=/
        //   - only modification is: replacement of "this.executeScripts" with "forcingExecuteScripts"
        //
				var regex = /<script([^>]*)>([\s\S]*?)<\/script>/i;
				var regexSrc = /src=(['"]?)([^"']*)\1/i;
				//var regexDojoJs = /.*(\bdojo\b\.js(?:\.uncompressed\.js)?)$/;
				//var regexInvalid = /(?:var )?\bdjConfig\b(?:[\s]*=[\s]*\{[^}]+\}|\.[\w]*[\s]*=[\s]*[^;\n]*)?;?|dojo\.hostenv\.writeIncludes\(\s*\);?/g;
                //var regexDojoLoadUnload = /dojo\.(addOn(?:Un)?[lL]oad)/g;
				//var regexRequires = /dojo\.(?:(?:require(?:After)?(?:If)?)|(?:widget\.(?:manager\.)?registerWidgetPackage)|(?:(?:hostenv\.)?setModulePrefix|registerModulePath)|defineNamespace)\((['"]).*?\1\)\s*;?/;


                // " - trick emacs here after regex
				while(match = regex.exec(s)){
					if(forcingExecuteScripts && match[1]){
						if(attr = regexSrc.exec(match[1])){
							// remove a dojo.js or dojo.js.uncompressed.js from remoteScripts
							// we declare all files named dojo.js as bad, regardless of path
							//if(regexDojoJs.exec(attr[2])){
							//	dojo.debug("Security note! inhibit:"+attr[2]+" from  being loaded again.");
							//}else{
								scripts.push({path: attr[2]});
							//}
						}
					}
					if(match[2]){
						// remove all invalid variables etc like djConfig and dojo.hostenv.writeIncludes()
						var sc = match[2];//.replace(regexInvalid, "");
    						if(!sc){ continue; }
		
						// cut out all dojo.require (...) calls, if we have execute 
						// scripts false widgets dont get there require calls
						// takes out possible widgetpackage registration as well
						
                        //while(tmp = regexRequires.exec(sc)){
						//	requires.push(tmp[0]);
						//	sc = sc.substring(0, tmp.index) + sc.substr(tmp.index + tmp[0].length);
						//}
                        
                        //sc = sc.replace( regexDojoLoadUnload, "dojo.widget.byId('" + this.widgetId + "').$1" );

						if(forcingExecuteScripts){
							scripts.push(sc);
						}
					}
					s = s.substr(0, match.index) + s.substr(match.index + match[0].length);
				}
        // /=/=/=/=/=  end  ContentPane.splitAndFixPaths   code  =/=/=/=/=/

        //dojo.debug( "= = = = = =  annotated content for: " + ( url ? url : "unknown url" ) );
        //dojo.debug( initialHtmlStr );
        //if ( scripts.length > 0 )
        //{
        //    dojo.debug( "      = = =  script content for: " + ( url ? url : "unknown url" ) );
        //    for ( var i = 0 ; i < scripts.length; i++ )
        //        dojo.debug( "      =[" + (i+1) + "]:" + scripts[i] );
        //}
        //dojo.debug( "preParse  scripts: " + ( scripts ? scripts.length : "0" ) + " remoteScripts: " + ( remoteScripts ? remoteScripts.length : "0" ) );
        return {"xml": 		    s, // Object
				"styles":		[],
				"titles": 		[],
				"requires": 	[],
				"scripts": 		scripts,
				"url": 			url};
    },

    _IEPostDrag: function()
    {
        if ( ! this.posStatic ) return ;
        var colDomNode = this.domNode.parentNode;
        dojo.dom.insertAtIndex( jetspeed.widget.ie6ZappedContentHelper, colDomNode, 0 );
        dojo.lang.setTimeout( this, this._IERemoveHelper, jetspeed.widget.ie6PostDragRmDelay );
    },
    _IERemoveHelper: function()
    {
        dojo.dom.removeNode( jetspeed.widget.ie6ZappedContentHelper );
    }
});

jetspeed.widget.WinScroller = function( jsObj )
{
    if ( ! jsObj ) jsObj = jetspeed;
    this.UAmoz = jsObj.UAmoz;
    this.UAope = jsObj.UAope;
};
dojo.extend( jetspeed.widget.WinScroller, {
    typeNm: "WinScroller",
    V_AS_T: 32,                 // dojo.dnd.V_TRIGGER_AUTOSCROLL
    V_AS_V: 16,                 // dojo.dnd.V_AUTOSCROLL_VALUE
    autoScroll: function( e )   // dojo.dnd.autoScroll
    {
        try{   // IE can choke on accessing event properties, apparently
            var w = window;
            var dy = 0;
            if( e.clientY < this.V_AS_T )
            {
                dy = -this.V_AS_V;
            }
            else
            {   // dojo.dnd.getViewport
                var vpHeight = null;
                if ( this.UAmoz )
                    vpHeight = w.innerHeight;
                else
                {
                    var doc = document, dd = doc.documentElement;
                    if ( ! this.UAope && w.innerWidth )
    		            vpHeight = w.innerHeight;
                    else if ( ! this.UAope && dd && dd.clientWidth )
                        vpHeight = dd.clientHeight;
                    else
                    {
                        var b = jetspeed.docBody;
                        if ( b.clientWidth )
                            vpHeight = b.clientHeight;
                    }
                }
    
                if( vpHeight != null && e.clientY > vpHeight - this.V_AS_T )
                {
    		        dy = this.V_AS_V;
                }
            }
            w.scrollBy( 0, dy );
        }catch(ex){
        }
    },
    _getErrMsg: function( ex, msg, wndORlayout, prevErrMsg )
    {
        return ( ( prevErrMsg != null ? (prevErrMsg + "; ") : "" ) + this.typeNm + " " + ( wndORlayout == null ? "<unknown>" : wndORlayout.widgetId ) + " " + msg + " (" + ex.toString() + ")" );
    }
});

jetspeed.widget.CreatePortletWindowResizeHandler = function( portletWindow, jsObj )
{
    var resizeHandler = new jetspeed.widget.PortletWindowResizeHandle( portletWindow, jsObj );
    var doc = document;
    var rDivHndl = doc.createElement( "div" );
    rDivHndl.className = resizeHandler.rhClass;
    var rDivHndlInner = doc.createElement( "div" );
    rDivHndl.appendChild( rDivHndlInner );
    portletWindow.rbNode.appendChild( rDivHndl );
    resizeHandler.domNode = rDivHndl;    
    resizeHandler.build();
    return resizeHandler;
};

jetspeed.widget.PortletWindowResizeHandle = function( portletWindow, jsObj )
{
    this.pWin = portletWindow;
    jsObj.widget.WinScroller.call(this, jsObj);
};
dojo.inherits( jetspeed.widget.PortletWindowResizeHandle, jetspeed.widget.WinScroller );
dojo.extend( jetspeed.widget.PortletWindowResizeHandle, {
    typeNm: "Resize",
    rhClass: "portletWindowResizeHandle",
    build: function()
    {
        this.events = [ jetspeed.ui.evtConnect( "after", this.domNode, "onmousedown", this, "_beginSizing" ) ];
    },
    destroy: function( djEvtObj, jsObj, jsUI )
    {
        this._cleanUpLastEvt( djEvtObj, jsObj, jsUI );
        jsUI.evtDisconnectWObjAry( this.events, djEvtObj );
        this.events = this.pWin = null;
    },
    _cleanUpLastEvt: function( djEvtObj, jsObj, jsUI )
    {
        var errMsg = null;
        try
        {
            jsUI.evtDisconnectWObjAry( this.tempEvents, djEvtObj );
            this.tempEvents = null;
        }
        catch(ex)
        {
            errMsg = this._getErrMsg( ex, "event clean-up error", this.pWin, errMsg );
        }

        try
        {
            jsObj.page.displayAllPWinIFrameCovers( true );
        }
        catch(ex)
        {
            errMsg = this._getErrMsg( ex, "clean-up error", this.pWin, errMsg );
        }

        if ( errMsg != null )
            dojo.raise( errMsg );
    },
	_beginSizing: function(e){
		if ( this._isSizing ) { return false; }
        var pWin = this.pWin;
        var node = pWin.domNode;
		if ( ! node ) { return false; }
        this.targetDomNode = node;

        var jsObj = jetspeed;
        var jsUI = jsObj.ui;
        var djObj = dojo;
        var djEvtObj = djObj.event;
        var docBody = jsObj.docBody;

        if ( this.tempEvents != null )
            this._cleanUpLastEvt( djEvtObj, jsObj, jsUI );
		
		this._isSizing = true;

		this.startPoint = { x: e.pageX, y: e.pageY };
		var mb = djObj.html.getMarginBox( node );
		this.startSize = { w: mb.width, h: mb.height };

        var d = node.ownerDocument ;
        var resizeTempEvts = [];
        resizeTempEvts.push( jsUI.evtConnect( "after", docBody, "onmousemove", this, "_changeSizing", djEvtObj, 25 ) );
        resizeTempEvts.push( jsUI.evtConnect( "after", docBody, "onmouseup", this, "_endSizing", djEvtObj ) );
        // cancel text selection and text dragging
        resizeTempEvts.push( jsUI.evtConnect( "after", d, "ondragstart",   djEvtObj.browser, "stopEvent", djEvtObj ) );
        resizeTempEvts.push( jsUI.evtConnect( "after", d, "onselectstart", djEvtObj.browser, "stopEvent", djEvtObj ) );

        jsObj.page.displayAllPWinIFrameCovers( false );

        this.tempEvents = resizeTempEvts;
        
        e.preventDefault();
	},
    _changeSizing: function(e)
    {
        var pWin = this.pWin;
        if ( pWin.heightToFit )
        {
            pWin.makeHeightVariable( true, true );
        }

        // On IE, if you move the mouse above/to the left of the object being resized,
		// sometimes pageX/Y aren't set, apparently.  Just ignore the event.
		try{
			if(!e.pageX  || !e.pageY){ return; }
		}catch(ex){
			// sometimes you get an exception accessing above fields...
			return;
		}

        this.autoScroll( e );

		var dx = this.startPoint.x - e.pageX;
		var dy = this.startPoint.y - e.pageY;

		var newW = this.startSize.w - dx;
		var newH = this.startSize.h - dy;

        var posStatic = pWin.posStatic;
        if ( posStatic )
        {
            newW = this.startSize.w;
        }

		// minimum size check
		if (this.minSize) {
			var mb = dojo.html.getMarginBox( this.targetDomNode );
			if (newW < this.minSize.w) {
				newW = mb.width;
			}
			if (newH < this.minSize.h) {
				newH = mb.height;
			}
		}

        //dojo.debug( "rsh._changeSizing -  w=" + newW + "  h=" + newH + "  tbNode.width=" + pWin.tbNode.style.width );

		pWin.resizeTo( newW, newH );

        e.preventDefault();
	},
	_endSizing: function(e)
    {
        var jsObj = jetspeed;
        this._cleanUpLastEvt( dojo.event, jsObj, jsObj.ui );

		this._isSizing = false;
	}
});

jetspeed.widget.ie6PostDragAddDelay = 60; jetspeed.widget.ie6PostDragRmDelay = 120;

if ( ! dojo.dnd )
    dojo.dnd = {};

dojo.dnd.Mover = function(windowOrLayoutWidget, dragNode, beforeDragColumn, moveableObj, e, djObj, jsObj){
	// summary: an object, which makes a node follow the mouse, 
	//	used as a default mover, and as a base class for custom movers
	// node: Node: a node (or node's id) to be moved
	// e: Event: a mouse event, which started the move;
	//	only pageX and pageY properties are used
    var jsUI = jsObj.ui;
    var djEvtObj = djObj.event;

    jsObj.widget.WinScroller.call(this, jsObj);

    this.moveInitiated = false;
    this.moveableObj = moveableObj;
    this.windowOrLayoutWidget = windowOrLayoutWidget;
	this.node = dragNode;
    this.posStatic = windowOrLayoutWidget.posStatic;
    if ( e.ctrlKey && this.posStatic )
        this.changeToUntiled = true ;
    this.posRecord = {};
    this.disqualifiedColumnIndexes = null;
    if ( beforeDragColumn != null )
        this.disqualifiedColumnIndexes = beforeDragColumn.getDescendantCols();

	this.marginBox = {l: e.pageX, t: e.pageY};

	var doc = this.node.ownerDocument;
    var moverEvts = [];
    var firstEvt = jsUI.evtConnect( "after", doc, "onmousemove", this, "onFirstMove", djEvtObj );
    moverEvts.push( jsUI.evtConnect( "after", doc, "onmousemove", this, "onMouseMove", djEvtObj ) );
    moverEvts.push( jsUI.evtConnect( "after", doc, "onmouseup",   this, "mouseUpDestroy", djEvtObj ) );

    // cancel text selection and text dragging
    moverEvts.push( jsUI.evtConnect( "after", doc, "ondragstart",   djEvtObj.browser, "stopEvent", djEvtObj ) );
    moverEvts.push( jsUI.evtConnect( "after", doc, "onselectstart", djEvtObj.browser, "stopEvent", djEvtObj ) );

    jsObj.page.displayAllPWinIFrameCovers( false );

    moverEvts.push( firstEvt );  // disconnected with pop() in onFirstMove
    this.events = moverEvts;

    this.isDebug = false;
    if ( jsObj.debug.dragWindow )
    {
        this.isDebug = true;
        this.devInit = false;
        this.devLastX = null; this.devLastY = null; this.devLastTime = null;
        this.devChgTh = 30;       // Th: Threshold
        this.devLrgTh = 200;
        this.devChgSubsqTh = 10;
        this.devTimeTh = 6000;
        this.devI = jsObj.debugindent; this.devIH = jsObj.debugindentH; this.devI3 = jsObj.debugindent3; this.devICH = jsObj.debugindentch;
    }
};
dojo.inherits( dojo.dnd.Mover, jetspeed.widget.WinScroller );
dojo.extend(dojo.dnd.Mover, {
    typeNm: "Mover",
	// mouse event processors
	onMouseMove: function( e ){
		// summary: event processor for onmousemove
		// e: Event: mouse event
        var jsObj = jetspeed;
        var djObj = dojo;
        var isMoz = this.UAmoz;
		this.autoScroll( e );
		var m = this.marginBox;
        var noMove = false;
        var x = m.l + e.pageX;
        var y = m.t + e.pageY;
        var debugOn = false;
        var debugTime = null, debugExcl = null, indent, indentH, indent3, indentCH;

        if ( this.isDebug )
        {
            indent = this.devI; indentH = this.devIH; indent3 = this.devI3; indentCH = this.devICH;
            if ( ! this.devInit )
            {
                var dqCols = "";
                if ( this.disqualifiedColumnIndexes != null )
                    dqCols = indentH + "dqCols=[" + this.disqualifiedColumnIndexes.split( ", " ) + "]";
                var title = this.windowOrLayoutWidget.title;
                if ( title == null ) title = this.windowOrLayoutWidget.widgetId;
                djObj.hostenv.println( 'DRAG "' + this.windowOrLayoutWidget.title + '"' + indentH + "m.l = " + m.l + indentH + "m.t = " + m.t + dqCols );
                this.devInit = true;
            }
            debugTime = (new Date().getTime());
            if ( this.devLastX == null || this.devLastY == null )
            {
                this.devLastX = x;
                this.devLastY = y;
            }
            else
            {
                var pastLgThreshold = ( Math.abs( x - this.devLastX ) > this.devLrgTh ) || ( Math.abs( y - this.devLastY ) > this.devLrgTh );
                if ( ! pastLgThreshold && this.devLastTime != null && ( (this.devLastTime + this.devTimeTh) > debugTime ) )
                {   // too soon
                }
                else
                {
                    if ( Math.abs( x - this.devLastX ) > this.devChgTh )
                    {
                        this.devLastX = x;
                        debugOn = true;
                    }
                    if ( Math.abs( y - this.devLastY ) > this.devChgTh )
                    {
                        this.devLastY = y;
                        debugOn = true;
                    }
                }
            }
        }

        if ( isMoz && this.firstEvtAdjustXY != null )
        {   // initial event pageX and pageY seem to be relative to container when window is static
            //m = this.firstEvtAdjustXY;
            x = x + this.firstEvtAdjustXY.l;
            y = y + this.firstEvtAdjustXY.t;
            this.firstEvtAdjustXY = null;
            noMove = true;
        }
        djObj.setMarginBox( this.node, x, y, null, null, djObj.gcs( this.node ), jsObj );
        
        var posRecord = this.posRecord;
        posRecord.left = x;
        posRecord.top = y;

        var pwGhost = jsObj.widget.pwGhost;

        if ( this.posStatic && ! noMove )
        {
            var colIndex = -1;
            var widthHalf = this.widthHalf;
            var heightHalf = this.heightHalf;
            var heightHalfMore = heightHalf + ( heightHalf * 0.20 );
            var noOfCols = jsObj.page.columns.length;
            var candidates = [];
            var xTest = e.pageX;
            var yTest = y + heightHalf;
            for ( var i = 0 ; i < noOfCols ; i++ )
            {
                var colDims = this.columnDimensions[ i ];
                if ( colDims != null )
                {
                    if ( xTest >= colDims.left && xTest <= colDims.right )
                    {
                        if ( yTest >= (colDims.top - 30) )
                        {
                            candidates.push( i );
                            var lowY1 = Math.min( Math.abs( yTest - ( colDims.top ) ), Math.abs( e.pageY - ( colDims.top ) ) );
                            var lowY2 = Math.min( Math.abs( yTest - ( colDims.yhalf ) ), Math.abs( e.pageY - ( colDims.yhalf ) ) );
                            var lowY = Math.min( lowY1, lowY2 );
                            candidates.push( lowY );
                        }
                        else if ( debugOn )
                        {
                            if ( debugExcl == null ) debugExcl = [];
                            var offBy = (colDims.top - 30) - yTest;
                            debugExcl.push( indent3 + djObj.string.padRight( String(i), 2, indentCH ) + " y! " + djObj.string.padRight( String(offBy), 4, indentCH ) + indentH + "t=" + colDims.top + indentH + "b=" + colDims.bottom + indentH + "l=" + colDims.left + indentH + "r=" + colDims.right );
                        }
                    }
                    else if ( debugOn && xTest > colDims.width )
                    {
                        if ( debugExcl == null ) debugExcl = [];
                        var offBy = xTest - colDims.width;
                        debugExcl.push( indent3 + djObj.string.padRight( String(i), 2, indentCH ) + " x! " + djObj.string.padRight( String(offBy), 4, indentCH ) + indentH + "t=" + colDims.top + indentH + "b=" + colDims.bottom + indentH + "l=" + colDims.left + indentH + "r=" + colDims.right );
                    }
                }
            }
            var candL = candidates.length;
            if ( candL > 0 )
            {
                var lowValIndex = -1;
                var lowVal = 0;
                var i = 1;
                while ( i < candL )
                {
                    if ( lowValIndex == -1 || lowVal > candidates[i] )
                    {
                        lowValIndex = candidates[i-1];
                        lowVal = candidates[i];
                    }
                    i = i + 2;
                }
                colIndex = lowValIndex;
            }

            var col = ( colIndex >= 0 ? jsObj.page.columns[ colIndex ] : null );

            if ( debugOn )
            {
                //djObj.debug( "eX=" + e.pageX + " eY=" + e.pageY + " mB: " + jsObj.printobj( this.marginBox ) + " nB: " + jsObj.printobj( djObj.getMarginBox( this.node, null, jsObj ) ) );
                djObj.hostenv.println( indent + "x=" + x + indentH + "y=" + y + indentH + "col=" + colIndex + indentH + "xTest=" + xTest + indentH + "yTest=" + yTest );
                var i = 0;
                while ( i < candL )
                {
                    var colI = candidates[i];
                    var colDims = this.columnDimensions[ colI ];
                    djObj.hostenv.println( indent3 + djObj.string.padRight( String(colI), 2, indentCH ) + " -> " + djObj.string.padRight( String(candidates[i+1]), 4, indentCH ) + indentH + "t=" + colDims.top + indentH + "b=" + colDims.bottom + indentH + "l=" + colDims.left + indentH + "r=" + colDims.right );
                    i = i + 2;
                }
                if ( debugExcl != null )
                {
                    for ( i = 0 ; i < debugExcl.length ; i++ )
                        djObj.hostenv.println( debugExcl[i] );
                }
                this.devLastTime = debugTime;
                this.devChgTh = this.devChgSubsqTh;
            }

            if ( pwGhost.col != col && col != null )
            {
                djObj.dom.removeNode( pwGhost );
				pwGhost.col = col;
				col.domNode.appendChild( pwGhost );
			}

            var portletWindowsResult = null, portletWindowsInCol = null;
            if ( col != null )
            {
                portletWindowsResult = jsObj.ui.getPWinChildren( col.domNode, pwGhost );
                portletWindowsInCol = portletWindowsResult.portletWindowNodes;
            }
            if ( portletWindowsInCol != null && portletWindowsInCol.length > 1 )
            {
                var ghostIndex = portletWindowsResult.matchIndex;
                var yAboveWindow = -1;
                var yBelowWindow = -1;
                if ( ghostIndex > 0 )
                {
                    var yAboveWindow = djObj.html.getAbsolutePosition( portletWindowsInCol[ ghostIndex -1 ], true ).y;
                    if ( (y - 25) <= yAboveWindow )
                    {
                        djObj.dom.removeNode( pwGhost );
                        djObj.dom.insertBefore( pwGhost, portletWindowsInCol[ ghostIndex -1 ], true );
                    }
                }
                if ( ghostIndex != (portletWindowsInCol.length -1) )
                {
                    var yBelowWindow = djObj.html.getAbsolutePosition( portletWindowsInCol[ ghostIndex +1 ], true ).y;
                    if ( (y + 10) >= yBelowWindow )
                    {
                        if ( ghostIndex + 2 < portletWindowsInCol.length )
                            djObj.dom.insertBefore( pwGhost, portletWindowsInCol[ ghostIndex +2 ], true );
                        else
                            col.domNode.appendChild( pwGhost );
                    }
                }
            }
        }
	},
	// utilities
	onFirstMove: function(){
		// summary: makes the node absolute; it is meant to be called only once
        var jsObj = jetspeed;
        var djObj = dojo;
        var wndORlayout = this.windowOrLayoutWidget;
        var node = this.node;
        var compStyle = dojo.gcs( node );
        var mP = djObj.getMarginBox( node, compStyle, jsObj );
        this.marginBoxPrev = mP;
        this.staticWidth = null;
        var pwGhost = jsObj.widget.pwGhost;
        var isMoz = this.UAmoz;
        var changeToUntiled = this.changeToUntiled;
        var m = null;
        if ( this.posStatic )
        {
            m = { w: mP.w, h: mP.h };
            var colDomNode = node.parentNode;
            var jsDNode = document.getElementById( jsObj.id.DESKTOP );
            var nodeStyle = node.style;
            this.staticWidth = nodeStyle.width;
            var nodeAbsPos = djObj.html.getAbsolutePosition( node, true );
            var nodeMargExt = djObj._getMarginExtents( node, compStyle, jsObj );
            m.l = nodeAbsPos.left - nodeMargExt.l;    // calculate manually to avoid calling getMarginBox during node insertion (mozilla is too fast to update)
            m.t = nodeAbsPos.top - nodeMargExt.t;
            if ( isMoz && ! changeToUntiled )
            {   // set early to avoid fast reaction that causes below content to shift for a split second
                djObj.setMarginBox( pwGhost, null, null, null, mP.h, null, jsObj );
                this.firstEvtAdjustXY = { l: m.l, t: m.t };
            }
            nodeStyle.position = "absolute";
            nodeStyle.zIndex = jsObj.page.getPWinHighZIndex() + 1;

            if ( ! changeToUntiled )
            {
                colDomNode.insertBefore( pwGhost, node );
                if ( ! isMoz )   // some browsers cannot set this until node is in document
                    djObj.setMarginBox( pwGhost, null, null, null, mP.h, null, jsObj );

                jsDNode.appendChild( node );

                var portletWindowsResult = jsObj.ui.getPWinChildren( colDomNode, pwGhost );
                this.prevColumnNode = colDomNode;
                this.prevIndexInCol = portletWindowsResult.matchIndex;
            }
            else
            {
                wndORlayout._updtDimsObj( true );
                jsDNode.appendChild( node );
            }
        }
        else
        {
            m = djObj.getMarginBox( node, compStyle, jsObj );
        }
        this.moveInitiated = true;
        m.l -= this.marginBox.l;
		m.t -= this.marginBox.t;
		this.marginBox = m;

        jsObj.ui.evtDisconnectWObj( this.events.pop(), djObj.event );

        if ( this.posStatic )
        {
            djObj.setMarginBox( node, m.l, m.t, mP.w, null, null, jsObj );
            this.widthHalf = mP.w / 2;
            this.heightHalf = mP.h / 2;
            if ( ! changeToUntiled )
            {
                var inColIndex = wndORlayout.getPageColumnIndex();
    
                this.columnDimensions = new Array( jsObj.page.columns.length );
                for ( var i = 0 ; i < jsObj.page.columns.length ; i++ )
                {
                    var col = jsObj.page.columns[i];
                    if ( ! col.columnContainer && ! col.layoutHeader )
                    {
                        if ( this.qualifyTargetColumn( col ) )
                        {
                            var colAbsPos = djObj.html.getAbsolutePosition( col.domNode, true );
                            var marginBox = djObj.html.getMarginBox( col.domNode );
                            var colDims = { left: (colAbsPos.x), right: (colAbsPos.x + marginBox.width), top: (colAbsPos.y), bottom: (colAbsPos.y + marginBox.height) };
                            colDims.height = colDims.bottom - colDims.top;
                            colDims.width = colDims.right - colDims.left;
                            colDims.yhalf = colDims.top + ( colDims.height / 2 )
                            this.columnDimensions[ i ] = colDims;
                        }
                    }
                }
                var inCol = ( inColIndex >= 0 ? jsObj.page.columns[ inColIndex ] : null );
                pwGhost.col = inCol;
            }
            else
            {
                /*m = djObj.getMarginBox( node, compStyle, jsObj );
                m.l -= this.marginBox.l;
		        m.t -= this.marginBox.t;
                this.marginBox = m;

                this.widthHalf = mP.w / 2;
                this.heightHalf = mP.h / 2;
                */
                wndORlayout._makeUntiledDims();
                this.posStatic = false;
            }
            //djObj.debug( "initial position: " + jsObj.printobj( djObj.getMarginBox( node, compStyle, jsObj ) ) );
        }
	},
    qualifyTargetColumn: function( column )
    {
        if ( column != null && ! column.layoutActionsDisabled )
        {
            if ( this.disqualifiedColumnIndexes != null && this.disqualifiedColumnIndexes[ column.getPageColumnIndex() ] != null )
            {
                //dojo.debug( "disqualified: " + column.toString() );
                return false;
            }
            return true;
        }
        return false;
    },
    mouseUpDestroy: function()
    {
        var djObj = dojo;
        var jsObj = jetspeed;
        this.destroy( djObj, djObj.event, jsObj, jsObj.ui );
    },
	destroy: function( djObj, djEvtObj, jsObj, jsUI ){
		// summary: stops the move, deletes all references, so the object can be garbage-collected
        var wndORlayout = this.windowOrLayoutWidget;
        var errMsg = null;
        if ( this.moveInitiated )
        {
            try
            {
                var pwGhost = jsObj.widget.pwGhost;
                if ( this.posStatic )
                {
                    var n = this.node;
                    var nStyle = n.style;
                    if ( pwGhost && pwGhost.col )
                    {
                        wndORlayout.column = 0;
                        djObj.dom.insertBefore( n, pwGhost, true );
                    }
                    else
                    {
                        djObj.dom.insertAtIndex( n, this.prevColumnNode, this.prevIndexInCol );
                    }
                    if ( pwGhost )
                        djObj.dom.removeNode( pwGhost );
                }
                wndORlayout.endDragging( this.posRecord );     // xxxx   used to pass the pos info only when window was minimized
            }
            catch(ex)
            {
                errMsg = this._getErrMsg( ex, "destroy reset-window error", wndORlayout, errMsg );
            }
        }

        try
        {
            jsUI.evtDisconnectWObjAry( this.events, djEvtObj );
            if ( this.moveableObj != null )
                this.moveableObj.mover = null;
            this.events = this.node = this.windowOrLayoutWidget = this.moveableObj = this.prevColumnNode = this.prevIndexInCol = null;
        }
        catch(ex)
        {
            errMsg = this._getErrMsg( ex, "destroy event clean-up error", wndORlayout, errMsg );
            if ( this.moveableObj != null )
                this.moveableObj.mover = null;
        }

        try
        {
            jsObj.page.displayAllPWinIFrameCovers( true );
        }
        catch(ex)
        {
            errMsg = this._getErrMsg( ex, "destroy clean-up error", wndORlayout, errMsg );
        }

        if ( errMsg != null )
            djObj.raise( errMsg );
	}
});

dojo.dnd.Moveable = function( windowOrLayoutWidget, opt ){
	// summary: an object, which makes a node moveable
	// node: Node: a node (or node's id) to be moved
	// opt: Object: an optional object with additional parameters;
	//	following parameters are recognized:
	//		handle: Node: a node (or node's id), which is used as a mouse handle
	//			if omitted, the node itself is used as a handle
	//		delay: Number: delay move by this number of pixels
	//		skip: Boolean: skip move of form elements
	//		mover: Object: a constructor of custom Mover
    var jsUI = jetspeed.ui;
    var djEvtObj = dojo.event;
    this.enabled = true;
    this.mover = null;
    this.windowOrLayoutWidget = windowOrLayoutWidget;
	this.handle = opt.handle;
    this.minMove = 20;
    var moveableEvts = [];
    moveableEvts.push( jsUI.evtConnect( "after", this.handle, "onmousedown", this, "onMouseDown", djEvtObj ) );

    // cancel text selection and text dragging
    moveableEvts.push( jsUI.evtConnect( "after", this.handle, "ondragstart",   djEvtObj.browser, "stopEvent", djEvtObj ) );
    moveableEvts.push( jsUI.evtConnect( "after", this.handle, "onselectstart", djEvtObj.browser, "stopEvent", djEvtObj ) );
	this.events = moveableEvts;
};

dojo.extend(dojo.dnd.Moveable, {
	// mouse event processors
	onMouseDown: function( e ){
		// summary: event processor for onmousedown, creates a Mover for the node
		// e: Event: mouse event
        if ( e && e.button == 2 ) return ;
        if ( this.mover != null || this.tempEvents != null )
        {
            var djObj = dojo;
            var djEvtObj = djObj.event;
            var jsObj = jetspeed;
            this._cleanUpLastEvt( djObj, djEvtObj, jsObj, jsObj.ui );
            djEvtObj.browser.stopEvent( e );
        }
        else if ( this.enabled )
        {
            var jsUI = jetspeed.ui;
            var djEvtObj = dojo.event;
            var moveableTempEvts = [];
            moveableTempEvts.push( jsUI.evtConnect( "after", this.handle, "onmousemove", this, "onMouseMove", djEvtObj ) );
            moveableTempEvts.push( jsUI.evtConnect( "after", this.handle, "onmouseup", this, "onMouseUp", djEvtObj ) );
            moveableTempEvts.push( jsUI.evtConnect( "after", this.handle, "onmouseout", this, "onMouseOut", djEvtObj ) );
            this.tempEvents = moveableTempEvts;
            
            this._lastX = e.pageX;
            this._lastY = e.pageY;
            this._mDownEvt = e;
        }
	},

    onMouseOut: function( e )
    {
        this.onMouseMove( e, true ) ;
    },

    onMouseMove: function( e, force )
    {
        var djObj = dojo;
        var djEvtObj = djObj.event;
        if ( force || Math.abs(e.pageX - this._lastX) > this.minMove || Math.abs(e.pageY - this._lastY) > this.minMove )
        {
            var jsObj = jetspeed;

            this._cleanUpLastEvt( djObj, djEvtObj, jsObj, jsObj.ui );

            var dragNode = null;
            var wndORlayout = this.windowOrLayoutWidget;
            var beforeDragColumn = null;
            this.beforeDragColumnRowInfo = null;
            if ( ! wndORlayout.isLayoutPane )
            {
                dragNode = wndORlayout.domNode;
            }
            else
            {
                beforeDragColumn = wndORlayout.containingColumn;
                if ( beforeDragColumn != null )
                {
                    dragNode = beforeDragColumn.domNode;
                    if ( dragNode != null )
                        this.beforeDragColumnRowInfo = jsObj.page.getPortletCurColRow( dragNode );
                }
            }
            if ( dragNode != null )
            {
                this.node = dragNode;
		        this.mover = new djObj.dnd.Mover( wndORlayout, dragNode, beforeDragColumn, this, e, djObj, jsObj );
            }
        }
        djEvtObj.browser.stopEvent( e );
    },
    onMouseUp: function( e )
    {
        var djObj = dojo;
        var jsObj = jetspeed;
        this._cleanUpLastEvt( djObj, djObj.event, jsObj, jsObj.ui );
    },
    _cleanUpLastEvt: function( djObj, djEvtObj, jsObj, jsUI )
    {
        if ( this._mDownEvt != null )
        {
            djEvtObj.browser.stopEvent( this._mDownEvt );
            this._mDownEvt = null;
        }
        if ( this.mover != null )
        {
            this.mover.destroy( djObj, djEvtObj, jsObj, jsUI );
            this.mover = null;
        }
        // disconnect temp event handlers which were added in onMouseDown
        jsUI.evtDisconnectWObjAry( this.tempEvents, djEvtObj );
        this.tempEvents = null;
    },
	destroy: function( djObj, djEvtObj, jsObj, jsUI )
    {
        this._cleanUpLastEvt( djObj, djEvtObj, jsObj, jsUI );
        jsUI.evtDisconnectWObjAry( this.events, djEvtObj );
		this.events = this.node = this.handle = this.windowOrLayoutWidget = this.beforeDragColumnRowInfo = null;
	},
    enable: function() { this.enabled = true; },
    disable: function() { this.enabled = false; }
});

	dojo.getMarginBox = function(node, computedStyle, jsObj){
		var s = computedStyle||dojo.gcs(node), me = dojo._getMarginExtents(node, s, jsObj);
		var	l = node.offsetLeft - me.l,	t = node.offsetTop - me.t; 
		if(jsObj.UAmoz){
			// Mozilla:
			// If offsetParent has a computed overflow != visible, the offsetLeft is decreased
			// by the parent's border.
			// We don't want to compute the parent's style, so instead we examine node's
			// computed left/top which is more stable.
			var sl = parseFloat(s.left), st = parseFloat(s.top);
			if (!isNaN(sl) && !isNaN(st)) {
				l = sl, t = st;
			} else {
				// If child's computed left/top are not parseable as a number (e.g. "auto"), we
				// have no choice but to examine the parent's computed style.
				var p = node.parentNode;
				if (p) {
					var pcs = dojo.gcs(p);
					if (pcs.overflow != "visible"){
						var be = dojo._getBorderExtents(p, pcs);
						l += be.l, t += be.t;
					}
				}
			}
		}
		// On Opera, offsetLeft includes the parent's border
		else if(jsObj.UAope){
			var p = node.parentNode;
			if(p){
				var be = dojo._getBorderExtents(p);
				l -= be.l, t -= be.t;
			}
		}
		return { 
			l: l, 
			t: t, 
			w: node.offsetWidth + me.w, 
			h: node.offsetHeight + me.h 
		};
	};

	dojo.getContentBox = function(node, computedStyle, jsObj){
		// clientWidth/Height are important since the automatically account for scrollbars
		// fallback to offsetWidth/Height for special cases (see #3378)
		var s=computedStyle||gcs(node), pe=dojo._getPadExtents(node, s), be=dojo._getBorderExtents(node, s), w=node.clientWidth, h;
		if (!w) {
			w=node.offsetWidth, h=node.offsetHeight;
		} else {
			h=node.clientHeight, be.w = be.h = 0; 
		}
		// On Opera, offsetLeft includes the parent's border
		if(jsObj.UAope){ pe.l += be.l; pe.t += be.t; };
		return { 
			l: pe.l, 
			t: pe.t, 
			w: w - pe.w - be.w, 
			h: h - pe.h - be.h
		};
	};

	dojo.setMarginBox = function(node, leftPx, topPx, widthPx, heightPx, computedStyle, jsObj){
		var s = computedStyle || dojo.gcs(node);
		// Some elements have special padding, margin, and box-model settings. 
		// To use box functions you may need to set padding, margin explicitly.
		// Controlling box-model is harder, in a pinch you might set dojo.boxModel.
		var bb=dojo._usesBorderBox(node), pb=bb ? { l:0, t:0, w:0, h:0 } : dojo._getPadBorderExtents(node, s), mb=dojo._getMarginExtents(node, s, jsObj);
		if(widthPx != null && widthPx>=0){ widthPx = Math.max(widthPx - pb.w - mb.w, 0); }
		if(heightPx != null && heightPx>=0){ heightPx = Math.max(heightPx - pb.h - mb.h, 0); }
		dojo._setBox(node, leftPx, topPx, widthPx, heightPx);
	};

	dojo._setBox = function(node, l, t, w, h, u){
		u = u || "px";
		with(node.style){
			if(l != null && !isNaN(l)){ left = l+u; }
			if(t != null && !isNaN(t)){ top = t+u; }
			if(w != null && w >=0){ width = w+u; }
			if(h != null && h >=0){ height = h+u; }
		}
	};

    dojo._usesBorderBox = function(node){
		// We could test the computed style of node to see if a particular box
		// has been specified, but there are details and we choose not to bother.
		var n = node.tagName;
		// For whatever reason, TABLE and BUTTON are always border-box by default.
		// If you have assigned a different box to either one via CSS then
		// box functions will break.
		return false; // (dojo.boxModel=="border-box")||(n=="TABLE")||(n=="BUTTON");
	};

	dojo._getPadExtents = function(n, computedStyle){
		// Returns special values specifically useful 
		// for node fitting.
		// l/t = left/top padding (respectively)
		// w = the total of the left and right padding 
		// h = the total of the top and bottom padding
		// If 'node' has position, l/t forms the origin for child nodes. 
		// The w/h are used for calculating boxes.
		// Normally application code will not need to invoke this directly,
		// and will use the ...box... functions instead.
		var s=computedStyle||dojo.gcs(n), px=dojo._toPixelValue, l=px(n, s.paddingLeft), t=px(n, s.paddingTop);
		return { 
			l: l,
			t: t,
			w: l+px(n, s.paddingRight),
			h: t+px(n, s.paddingBottom)
		};
	};

	dojo._getPadBorderExtents = function(n, computedStyle){
		// l/t = the sum of left/top padding and left/top border (respectively)
		// w = the sum of the left and right padding and border
		// h = the sum of the top and bottom padding and border
		// The w/h are used for calculating boxes.
		// Normally application code will not need to invoke this directly,
		// and will use the ...box... functions instead.
		var s=computedStyle||dojo.gcs(n), p=dojo._getPadExtents(n, s), b=dojo._getBorderExtents(n, s);
		return { 
			l: p.l + b.l,
			t: p.t + b.t,
			w: p.w + b.w,
			h: p.h + b.h
		};
	};

	dojo._getMarginExtents = function(n, computedStyle, jsObj){
		var 
			s=computedStyle||dojo.gcs(n), 
			px=dojo._toPixelValue,
			l=px(n, s.marginLeft),
			t=px(n, s.marginTop),
			r=px(n, s.marginRight),
			b=px(n, s.marginBottom);
		if (jsObj.UAsaf && (s.position != "absolute")){
			// FIXME: Safari's version of the computed right margin
			// is the space between our right edge and the right edge 
			// of our offsetParent. 
			// What we are looking for is the actual margin value as 
			// determined by CSS.
			// Hack solution is to assume left/right margins are the same.
			r = l;
		}
		return { 
			l: l,
			t: t,
			w: l+r,
			h: t+b
		};
	};


	dojo._getBorderExtents = function(n, computedStyle){
		// l/t = the sum of left/top border (respectively)
		// w = the sum of the left and right border
		// h = the sum of the top and bottom border
		// The w/h are used for calculating boxes.
		// Normally application code will not need to invoke this directly,
		// and will use the ...box... functions instead.
		var 
			ne='none',
			px=dojo._toPixelValue, 
			s=computedStyle||dojo.gcs(n), 
			bl=(s.borderLeftStyle!=ne ? px(n, s.borderLeftWidth) : 0),
			bt=(s.borderTopStyle!=ne ? px(n, s.borderTopWidth) : 0);
		return { 
			l: bl,
			t: bt,
			w: bl + (s.borderRightStyle!=ne ? px(n, s.borderRightWidth) : 0),
			h: bt + (s.borderBottomStyle!=ne ? px(n, s.borderBottomWidth) : 0)
		};
	};

	if(!jetspeed.UAie){
		// non-IE branch
		var dv = document.defaultView;
		dojo.getComputedStyle = ((jetspeed.UAsaf) ? function(node){
				var s = dv.getComputedStyle(node, null);
				if(!s && node.style){ 
					node.style.display = ""; 
					s = dv.getComputedStyle(node, null);
				}
				return s || {};
			} : function(node){
				return dv.getComputedStyle(node, null);
			}
		)

		dojo._toPixelValue = function(element, value){
			// style values can be floats, client code may want
			// to round for integer pixels.
			return (parseFloat(value) || 0); 
		}
	}else{
		// IE branch
		dojo.getComputedStyle = function(node){
			return node.currentStyle;
		}

		dojo._toPixelValue = function(element, avalue){
			if(!avalue){return 0;}
			// style values can be floats, client code may
			// want to round this value for integer pixels.
			if(avalue.slice&&(avalue.slice(-2)=='px')){ return parseFloat(avalue); }
			with(element){
				var sLeft = style.left;
				var rsLeft = runtimeStyle.left;
				runtimeStyle.left = currentStyle.left;
				try{
					// 'avalue' may be incompatible with style.left, which can cause IE to throw
					// this has been observed for border widths using "thin", "medium", "thick" constants
					// those particular constants could be trapped by a lookup
					// but perhaps there are more
					style.left = avalue;
					avalue = style.pixelLeft;
				}catch(e){
					avalue = 0;
				}
				style.left = sLeft;
				runtimeStyle.left = rsLeft;
			}
			return avalue;
		}
	};

    dojo.gcs = dojo.getComputedStyle;
