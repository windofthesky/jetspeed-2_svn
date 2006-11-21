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

dojo.provide("jetspeed.widget.PortletWindow");

dojo.require("jetspeed.desktop.core");
dojo.require("dojo.widget.*");
dojo.provide("dojo.widget.FloatingPane");

jetspeed.widget.PortletWindow = function()
{
    dojo.widget.FloatingPane.call( this );
    this.widgetType = "PortletWindow";
    this.resizable = true;
    this.portletInitialized = false;
    this.actionButtons = {};
    this.actionMenus = {};
};

dojo.inherits( jetspeed.widget.PortletWindow, dojo.widget.FloatingPane );
dojo.lang.extend( jetspeed.widget.PortletWindow, {
    title: "Unknown Portlet",
    contentWrapper: "layout",
    displayCloseAction: true,
    displayMinimizeAction: true,
    displayMaximizeAction: true,
    displayRestoreAction: true,
    //taskBarId: jetspeed.id.TASKBAR,
    hasShadow: false,
    nextIndex: 1,

    windowTheme: null,
    windowThemeConfig: null,

    windowPositionStatic: false,
    windowHeightToFit: false,
    titleMouseIn: 0,
    titleLit: false,

    portlet: null,
    jsAltInitParams: null,
    
    templateDomNodeClassName: null,
    templateContainerNodeClassName: null,

    processingContentChanged: false,

    lastUntiledPositionInfo: null,
    lastTiledPositionInfo: null,

    // see setPortletContent for info on these ContentPane settings:
    executeScripts: false,
    scriptSeparation: false,
    adjustPaths: false,

    /*  static   */
    staticDefineAsAltInitParameters: function( defineIn, params )
    {
        if ( ! defineIn )
        {
            defineIn = {
                            getProperty: function( propertyName )
                            {
                                if ( ! propertyName ) return null;
                                return this.jsAltInitParams[ propertyName ];
                            },
                            putProperty: function( propertyName, propertyValue )
                            {
                                if ( ! propertyName ) return;
                                this.jsAltInitParams[ propertyName ] = propertyValue;
                            },
                            retrieveContent: function( contentListener, bindArgs )
                            {
                                var contentRetriever = this.getProperty( jetspeed.id.PORTLET_PROP_CONTENT_RETRIEVER );
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
        if ( params.jsAltInitParams )
            defineIn.jsAltInitParams = params.jsAltInitParams;
        else
            defineIn.jsAltInitParams = params;
        return defineIn;
    },

    // init properties - to facilitate initialization with or without a jetspeed.om.Portlet object
    getInitProperty: function( propertyName, altPropertyName )
    {   
        var propVal = null;
        if ( this.portlet )
        {
            propVal = this.portlet.getProperty( propertyName );
            if ( propVal == null && altPropertyName )
                propVal = this.portlet.getProperty( altPropertyName );
        }
        else if ( this.jsAltInitParams )
        {
            propVal = this.jsAltInitParams[ propertyName ];
            if ( propVal == null && altPropertyName )
                propVal = this.jsAltInitParams[ altPropertyName ];
        }
        return propVal;
    },
    setInitProperty: function( propertyName, propertyValue )
    {
        if ( this.portlet )
        {
            dojo.raise( "PortletWindow.setInitProperty cannot be called when the window is bound to a portlet" );
        }
        else
        {
            if ( ! this.jsAltInitParams )
            {
                this.jsAltInitParams = {};
            }
            this.jsAltInitParams[ propertyName ] = propertyValue;
        }
    },

    initWindowTheme: function( fragment )
    {
        var windowtheme = this.getInitProperty( jetspeed.id.PORTLET_PROP_WINDOW_THEME );
        if ( ! windowtheme )
        {
            if ( this.portletWindowTheme )
                windowtheme = this.portletWindowTheme;
            else
                windowtheme = jetspeed.page.getWindowThemeDefault();
        }
        this.windowTheme = windowtheme ;
        this.windowThemeConfig = jetspeed.prefs.getWindowThemeConfig( windowtheme );

        var prevCssPath = ( this.templateCssPath == null ? null : this.templateCssPath.toString() );
        this.templateCssPath = new dojo.uri.Uri( jetspeed.url.basePortalWindowThemeUrl( windowtheme ) + "/css/styles.css" );

        if ( this.portletInitialized )
        {   // load new stylesheet    // BOZO: it would be nice to check if this were necessary
            if ( prevCssPath == null || prevCssPath != this.templateCssPath.toString() )
                dojo.html.insertCssFile( this.templateCssPath, null, true );
        }
    },
    initWindowTitle: function( fragment )
    {
        var windowtitle = this.getInitProperty( jetspeed.id.PORTLET_PROP_WINDOW_TITLE );
        if ( windowtitle )
            this.title = windowtitle;
        else if ( this.title == null )
            this.title = "";
        if ( this.portletInitialized )
        {
            // BOZO: update title
        }
    },
    initWindowIcon: function( fragment )
    {
        var windowicon = this.getInitProperty( jetspeed.id.PORTLET_PROP_WINDOW_ICON );
        if ( ! windowicon )
        {
            if ( jetspeed.debugPortletWindowIcons )
            {
                windowicon = jetspeed.debugPortletWindowIcons[Math.floor(Math.random()*jetspeed.debugPortletWindowIcons.length)];
            }
        }
        if ( windowicon )
            this.iconSrc = new dojo.uri.Uri(jetspeed.url.basePortalDesktopUrl() + "/javascript/jetspeed/windowicons/" + windowicon ) ;
        else
            this.iconSrc = new dojo.uri.Uri(jetspeed.url.basePortalDesktopUrl() + "/javascript/jetspeed/windowicons/document.gif" ) ;
        if ( this.portletInitialized )
        {
            if ( this.titleBarIcon )
                this.titleBarIcon.src = this.iconSrc.toString();
        }
    },

    initWindowDimensions: function( fragment )
    {
        this.windowPositionStatic = this.getInitProperty( jetspeed.id.PORTLET_PROP_WINDOW_POSITION_STATIC );
        this.windowHeightToFit = this.getInitProperty( jetspeed.id.PORTLET_PROP_WINDOW_HEIGHT_TO_FIT );
        this.windowColumnSpan = this.getInitProperty( jetspeed.id.PORTLET_PROP_COLUMN_SPAN );

        this.constrainToContainer = 0;

        var windowWidth = null, windowHeight = null, windowLeft = null, windowTop = null;
        if ( this.portlet )
        {
            var portletWindowDimensions = this.portlet.getInitialWindowDimensions();
        	windowWidth = portletWindowDimensions.width;
            windowHeight = portletWindowDimensions.height;
            windowLeft = portletWindowDimensions.left;
            windowTop = portletWindowDimensions.top;
            // NOTE: portletWindowDimensions.zIndex;  - should be dealt with in the creation order
        }
        else
        {
            windowWidth = this.getInitProperty( jetspeed.id.PORTLET_PROP_WIDTH );
            windowHeight = this.getInitProperty( jetspeed.id.PORTLET_PROP_HEIGHT );
            windowLeft = this.getInitProperty( jetspeed.id.PORTLET_PROP_LEFT );
            windowTop = this.getInitProperty( jetspeed.id.PORTLET_PROP_TOP );
        }
        
        this.lastUntiledPositionInfo = {};
        this.lastTiledPositionInfo = { width: "" };
        
        // to allow for an initial untiled placement based on tiled position,
        //   only record lastUntiledPositionInfo when value is specified (not defaulted) or if window is already untiled
        if ( windowWidth != null && windowWidth > 0 )
        {
            windowWidth = Math.floor(windowWidth);
            this.lastUntiledPositionInfo.width = windowWidth;
        }
        else
        {
            windowWidth = jetspeed.prefs.windowWidth;
            if ( ! this.windowPositionStatic )
                this.lastUntiledPositionInfo.width = windowWidth;
        }
    
        if ( windowHeight != null && windowHeight > 0 )
        {
            windowHeight = Math.floor(windowHeight);
            this.lastUntiledPositionInfo.height = windowHeight;
            this.lastTiledPositionInfo.height = windowHeight;
        }
        else
        {
            windowHeight = jetspeed.prefs.windowHeight;
            this.lastTiledPositionInfo.height = windowHeight;
            if ( ! this.windowPositionStatic )
                this.lastUntiledPositionInfo.height = windowHeight;
        }
            
        if ( windowLeft != null && windowLeft >= 0 )
        {
            windowLeft = Math.floor(windowLeft);
            this.lastUntiledPositionInfo.left = windowLeft;
        }
        else
        {
            windowLeft = (((this.portletIndex -2) * 30 ) + 200);
            if ( ! this.windowPositionStatic )
                this.lastUntiledPositionInfo.left = windowLeft;
        }
    
        if ( windowTop != null && windowTop >= 0 )
        {
            windowTop = Math.floor(windowTop);
            this.lastUntiledPositionInfo.top = windowTop;
        }
        else
        {
            windowTop = (((this.portletIndex -2) * 30 ) + 170);
            if ( ! this.windowPositionStatic )
                this.lastUntiledPositionInfo.top = windowTop;
        }
        
        windowWidth = windowWidth + "px";
        windowHeight = windowHeight + "px";
        windowLeft = windowLeft + "px";
        windowTop = windowTop + "px";

        if ( ! this.portletInitialized )
        {
            var source = this.getFragNodeRef( fragment );
            var dimensionsCss = "width: " + windowWidth + ( ( windowHeight != null && windowHeight.length > 0 ) ? ( "; height: " + windowHeight ) : "");
            if ( ! this.windowPositionStatic )
                dimensionsCss += "; left: " + windowLeft + "; top: " + windowTop + ";";
        
            source.style.cssText = dimensionsCss;
        }
        else
        {   // update dimensions
            this.domNode.style.position = "absolute";
            this.domNode.style.width = windowWidth;
            this.domNode.style.height = windowHeight;
            if ( ! this.windowPositionStatic )
            {
                this.domNode.style.left = windowLeft;
                this.domNode.style.top = windowTop;
            }
        }
    },

    portletMixinProperties: function( fragment )
    {
        this.initWindowTheme( fragment );
        this.initWindowTitle( fragment );
        this.initWindowIcon( fragment );

        //if ( dojo.render.html.mozilla )  // dojo.render.html.ie
        //{
        //    this.hasShadow = "true";
        //    this.domNode.style = "overflow: visible;";   // so that drop shadow is displayed
        //}

        this.initWindowDimensions( fragment );
    },

    // dojo.widget.Widget create protocol
    postMixInProperties: function( args, fragment, parentComp )
    {
        jetspeed.widget.PortletWindow.superclass.postMixInProperties.call( this );

        this.portletIndex = this._getNextIndex();

        var initWidgetId = this.getInitProperty( jetspeed.id.PORTLET_PROP_WIDGET_ID );
        if ( this.portlet )
        {
            if ( this.widgetId )
            {
                dojo.raise( "PortletWindow.widgetId (" + this.widgetId + ") should not be assigned directly" );
            }
            if ( ! initWidgetId )
            {
                dojo.raise( "PortletWindow.widgetId is not defined for portlet [" + this.portlet.entityId + "] - Portlet.initialize may not have been called" );
            }
            this.widgetId = initWidgetId;
        }
        else
        {
            if ( initWidgetId )
                this.widgetId = initWidgetId;
            else if ( ! this.widgetId )
                this.widgetId = jetspeed.id.PORTLET_WINDOW_ID_PREFIX + this.portletIndex;
        }
        this._incrementNextIndex();

        this.templatePath = jetspeed.ui.getDefaultFloatingPaneTemplate();
        
        this.portletMixinProperties( fragment );
    },

    _incrementNextIndex: function()
    {
        var nextI = jetspeed.widget.PortletWindow.prototype.nextIndex;
        if ( ! nextI )
            jetspeed.widget.PortletWindow.prototype.nextIndex = 1;
        jetspeed.widget.PortletWindow.prototype.nextIndex++;
        return nextI;
    },
    _getNextIndex: function()
    {
        return jetspeed.widget.PortletWindow.prototype.nextIndex;
    },

    portletInitDragHandle: function()
    {
        var isResizable = this.resizable;
        if ( isResizable )
        {
			this.resizeBar.style.display = "block";
            var rhWidgetId = this.widgetId + "_resize";
            if ( ! this.portletInitialized )
			    this.resizeHandle = dojo.widget.createWidget( "jetspeed:PortletWindowResizeHandle", { targetElmId: this.widgetId, id: rhWidgetId, portletWindow: this } );
            else
                this.resizeHandle = dojo.widget.byId( rhWidgetId );
            if ( this.resizeHandle )
            {
                //if ( this.windowPositionStatic && dojo.render.html.mozilla )  // dojo.render.html.ie
                    this.resizeHandle.domNode.style.position = "static";  // until 2006-11-15, was set to absolute for all but dojo.render.html.mozilla
                                                                          // but setting to static for all seems to fix IE failure to initially display resize handle
                //else
                //    this.resizeHandle.domNode.style.position = "absolute";
                if ( ! this.portletInitialized )
			        this.resizeBar.appendChild( this.resizeHandle.domNode );
            }
		}
    },

    // dojo.widget.Widget create->buildRendering protocol
	fillInTemplate: function(args, frag)   /* copied from FloatingPane.js 0.3.1 with changes as noted */
    {
        //dojo.debug( "fillInTemplate-begin [" + this.widgetId + "] containerNode-outerwidth: " + dojo.html.getMarginBox( this.containerNode ).width + " containerNode-contentwidth: " + dojo.html.getContentBox( this.containerNode ).width + " domNode-outerwidth: " + dojo.html.getMarginBox( this.domNode ).width );

		// Copy style info from input node to output node
		var source = this.getFragNodeRef(frag);
		dojo.html.copyStyle(this.domNode, source);

		// necessary for safari, khtml (for computing width/height)
		document.body.appendChild(this.domNode);

		// if display:none then state=minimized, otherwise state=normal
		if(!this.isShowing()){
			this.windowState = jetspeed.id.ACTION_NAME_MINIMIZE;
		}

		// <img src=""> can hang IE!  better get rid of it
		if ( this.iconSrc=="" )
        {
			dojo.dom.removeNode( this.titleBarIcon );
		}
        else
        {
			this.titleBarIcon.src = this.iconSrc.toString();    // dojo.uri.Uri obj req. toString()
		}

		if ( this.titleBarDisplay!="none" )
        {	
			this.titleBar.style.display = "";
			dojo.html.disableSelection( this.titleBar );

			this.titleBarIcon.style.display = ( this.iconSrc == "" ? "none" : "" );

            var windowTitleBarButtons = null;

            if ( this.windowThemeConfig != null )
            {
                var menuActionNames = new Array();
                var menuActionNoImage = false;
                if ( this.windowThemeConfig.windowActionButtonOrder != null )
                {
                    // all possible button actions must be added here (no support for adding action buttons after init)
                    // this including buttons for the current mode and state (which will be initially hidden)
                    var btnActionNames = new Array();
                    if ( this.portlet )
                    {
                        for ( var actionIdx = (this.windowThemeConfig.windowActionButtonOrder.length-1) ; actionIdx >= 0 ; actionIdx-- )
                        {
                            var actionName = this.windowThemeConfig.windowActionButtonOrder[ actionIdx ];
                            var includeAction = false;
                            if ( this.portlet.getAction( actionName ) != null || jetspeed.prefs.windowActionDesktop[ actionName ] != null )
                            {
                                includeAction = true;
                            }
                            else if ( actionName == jetspeed.id.ACTION_NAME_RESTORE || actionName == jetspeed.id.ACTION_NAME_MENU )
                            {
                                includeAction = true;
                            }
                            if ( includeAction )
                            {
                                btnActionNames.push( actionName );
                            }
                        }
                    }
                    else
                    {
                        for ( var actionIdx = (this.windowThemeConfig.windowActionButtonOrder.length-1) ; actionIdx >= 0 ; actionIdx-- )
                        {
                            var actionName = this.windowThemeConfig.windowActionButtonOrder[ actionIdx ];
                            var includeAction = false;
                            if ( actionName == jetspeed.id.ACTION_NAME_MINIMIZE || actionName == jetspeed.id.ACTION_NAME_MAXIMIZE || actionName == jetspeed.id.ACTION_NAME_RESTORE || actionName == jetspeed.id.ACTION_NAME_MENU || jetspeed.prefs.windowActionDesktop[ actionName ] != null )
                            {
                                includeAction = true;
                            }
                            if ( includeAction )
                            {
                                btnActionNames.push( actionName );
                            }
                        }
                    }   // if ( this.portlet )
                    var btnMax = ( this.windowThemeConfig.windowActionButtonMax == null ? -1 : this.windowThemeConfig.windowActionButtonMax );
                    if ( btnMax != -1 && btnActionNames.length >= btnMax )
                    {
                        var removedBtns = 0;
                        var mustRemoveBtns = btnActionNames.length - btnMax + 1;
                        for ( var i = 0 ; i < btnActionNames.length && removedBtns < mustRemoveBtns ; i++ )
                        {
                            if ( btnActionNames[i] != jetspeed.id.ACTION_NAME_MENU )
                            {
                                menuActionNames.push( btnActionNames[i] );
                                btnActionNames[i] = null;
                                removedBtns++;
                            }
                        }
                    }
                    if ( this.windowThemeConfig.windowActionNoImage != null )
                    {
                        for ( var i = 0 ; i < btnActionNames.length ; i++ )
                        {
                            if ( this.windowThemeConfig.windowActionNoImage[ btnActionNames[ i ] ] != null )
                            {
                                if ( btnActionNames[ i ] == jetspeed.id.ACTION_NAME_MENU )
                                {
                                    menuActionNoImage = true;
                                }
                                else
                                {
                                    menuActionNames.push( btnActionNames[i] );
                                }
                                btnActionNames[ i ] = null;
                            }
                        }
                    }
                    for ( var i = 0 ; i < btnActionNames.length ; i++ )
                    {
                        if ( btnActionNames[i] != null )
                        {
                            this._createActionButtonNode( btnActionNames[i] );
                        }
                    }
                }   // if ( this.windowThemeConfig.windowActionButtonOrder != null )
    
                if ( this.windowThemeConfig.windowActionMenuOrder != null )
                {
                    if ( this.portlet )
                    {
                        for ( var actionIdx = 0 ; actionIdx < this.windowThemeConfig.windowActionMenuOrder.length ; actionIdx++ )
                        {
                            var actionName = this.windowThemeConfig.windowActionMenuOrder[ actionIdx ];
                            var includeAction = false;
                            if ( this.portlet.getAction( actionName ) != null || jetspeed.prefs.windowActionDesktop[ actionName ] != null )
                            {
                                includeAction = true;
                            }
                            if ( includeAction )
                            {
                                menuActionNames.push( actionName );
                            }
                        }
                    }
                    else
                    {
                        for ( var actionIdx = 0 ; actionIdx < this.windowThemeConfig.windowActionMenuOrder.length ; actionIdx++ )
                        {
                            var actionName = this.windowThemeConfig.windowActionMenuOrder[ actionIdx ];
                            if ( jetspeed.prefs.windowActionDesktop[ actionName ] != null )
                            {
                                menuActionNames.push( actionName );
                            }
                        }
                    }   // if ( this.portlet )
                }   // if ( this.windowThemeConfig.windowActionMenuOrder != null )
                
                if ( menuActionNames.length > 0 )
                {
                    var addedActionNames = new Object();
                    var finalMenuActionNames = new Array();
                    for ( var i = 0 ; i < menuActionNames.length ; i++ )
                    {
                        var actionName = menuActionNames[i];
                        if ( actionName != null && addedActionNames[ actionName ] == null && this.actionButtons[ actionName ] == null )
                        {
                            finalMenuActionNames.push( actionName );
                            addedActionNames[ actionName ] = true;
                        }
                    }
                    if ( finalMenuActionNames.length > 0 )
                    {
                        this._createActionMenu( finalMenuActionNames );
                        if ( menuActionNoImage )
                        {
                    		dojo.event.kwConnect({
			                    srcObj:     this.titleBar,
			                    srcFunc:    "oncontextmenu",
			                    targetObj:  this,
			                    targetFunc: "windowActionMenuOpen",
			                    once:       true
                    		});
                        }
                    }
                }
    
                this.windowActionButtonSync();
            }

            // j2o - deletion - initialization of HtmlDragMoveSource and call to setDragHandle
            //                  equivalent is done in postCreate with PortletWindowDragMoveSource

            // j2o - deletion - dojo.event.topic.publish floatingPaneMove for dragMove event
		}

        // j2o - deletion - creation of ResizeHandle - done by portletInitDragHandle()

        this.portletInitDragHandle();    // j2o addition

		// add a drop shadow
		if(this.hasShadow){
			this.shadow=new dojo.lfx.shadow( this.domNode );
		}

		// Prevent IE bleed-through problem
		this.bgIframe = new dojo.html.BackgroundIframe(this.domNode);

		if( this.taskBarId ){
			this.taskBarSetup();
		}

        this.resetLostHeightWidth();    // j2o addition

		if (dojo.hostenv.post_load_) {
			this._setInitialWindowState();
		} else {
			dojo.addOnLoad(this, "_setInitialWindowState");
		}

		// counteract body.appendChild above
		document.body.removeChild(this.domNode);

        // j2o - deletion - call to super fillInTemplate (we've replaced FloatingPane version, and no other superclass defines an implementation)

        //dojo.debug( "fillInTemplate-end [" + this.widgetId + "] containerNode-outerwidth: " + dojo.html.getMarginBox( this.containerNode ).width + " containerNode-contentwidth: " + dojo.html.getContentBox( this.containerNode ).width + " domNode-outerwidth: " + dojo.html.getMarginBox( this.domNode ).width );
	},

    _createActionButtonNode: function( actionName )
    {
        if ( actionName != null )
        {
            var actionButton = document.createElement( "div" );
            actionButton.className = "portletWindowActionButton";
            actionButton.style.backgroundImage = "url(" + jetspeed.url.basePortalWindowThemeUrl( this.windowTheme ) + "/images/desktop/" + actionName + ".gif)";
            actionButton.actionName = actionName;

            this.actionButtons[ actionName ] = actionButton;
            this.titleBar.appendChild( actionButton );

            dojo.event.connect( actionButton, "onclick", this, "windowActionButtonClick" );

            if ( this.windowThemeConfig != null && this.windowThemeConfig.windowActionButtonTooltip )
            {   // setting isContainer=false and fastMixIn=true to avoid recursion hell when connectId is a node (could give each an id instead)
                var tooltip = dojo.widget.createWidget( "Tooltip", { isContainer: false, fastMixIn: true, caption: this._getActionLabel( actionName ), connectId: actionButton, delay: "100" } );
                document.body.appendChild( tooltip.domNode );
            }
        }
    },

    _getActionMenuPopupWidget: function()
    {
        return dojo.widget.byId( this.widgetId + "_ctxmenu" );
    },
    _getActionLabel: function( actionName )
    {
        if ( actionName == null ) return null;
        var actionlabel = null;
        var actionLabelPrefs = jetspeed.prefs.desktopActionLabels;
        if ( actionLabelPrefs != null )
            actionlabel = actionLabelPrefs[ actionName ];
        if ( actionlabel == null || actionlabel.length == 0 )
        {
            if ( this.portlet )
            {
                var portletActionDef = this.portlet.getAction( actionName );
                if ( portletActionDef != null )
                    actionlabel = portletActionDef.label;
            }
        }
        if ( actionlabel == null || actionlabel.length == 0 )
        {
            actionlabel = dojo.string.capitalize( actionName );
        }
        return actionlabel;
    },
    _createActionMenu: function( /* Array */ menuActionNames )
    {
        if ( menuActionNames == null || menuActionNames.length == 0 ) return;
        var portletWindow = this;

        var titleBarContextMenu = dojo.widget.createWidget( "PopupMenu2", { id: this.widgetId + "_ctxmenu", contextMenuForWindow: false }, null );
        for ( var i = 0 ; i < menuActionNames.length ; i++ )
        {
            var actionName = menuActionNames[i];
            var menulabel = this._getActionLabel( actionName );
            var menuitem = this._createActionMenuItem( portletWindow, menulabel, actionName );

            this.actionMenus[ actionName ] = menuitem;

            titleBarContextMenu.addChild( menuitem );
        }

        document.body.appendChild( titleBarContextMenu.domNode );
    },
    _createActionMenuItem: function( portletWindow, menulabel, actionName )
    {
        var menuitem = dojo.widget.createWidget( "MenuItem2", { caption: menulabel } );
        dojo.event.connect( menuitem, "onClick", function(e) { portletWindow.windowActionProcess( actionName ); } );
        return menuitem;
    },

    windowActionButtonClick: function( evt )
    {
        if ( evt == null || evt.target == null ) return;
        this.windowActionProcess( evt.target.actionName, evt );
    },
    windowActionMenuOpen: function( evt )
    {
        var currentPortletActionState = null;
        var currentPortletActionMode = null;
        if ( this.portlet )
        {
            currentPortletActionState = this.portlet.getCurrentActionState();
            currentPortletActionMode = this.portlet.getCurrentActionMode();
        }
        for ( var actionName in this.actionMenus )
        {
            var menuitem = this.actionMenus[ actionName ];
            if ( this._isWindowActionEnabled( actionName, currentPortletActionState, currentPortletActionMode ) )
            {
                menuitem.domNode.style.display = "";   // instead of menuitem.enable();
            }
            else
            {
                menuitem.domNode.style.display = "none";   // instead of menuitem.disable();
            }
        }
        this._getActionMenuPopupWidget().onOpen( evt );        
    },
    windowActionProcess: function( /* String */ actionName, evt )
    {   // evt arg is needed only for opening action menu
        //dojo.debug( "windowActionProcess [" + ( this.portlet ? this.portlet.entityId : this.widgetId ) + ( this.portlet ? (" / " + this.widgetId) : "" ) + "]" + " actionName=" + actionName );
        if ( actionName == null ) return;
        if ( jetspeed.prefs.windowActionDesktop[ actionName ] != null )
        {
            if ( actionName == jetspeed.id.ACTION_NAME_DESKTOP_TILE )
            {
                this.makeTiled();
            }
            else if ( actionName == jetspeed.id.ACTION_NAME_DESKTOP_UNTILE )
            {
                this.makeUntiled();
            }
            else if ( actionName == jetspeed.id.ACTION_NAME_DESKTOP_HEIGHT_EXPAND )
            {
                this.makeHeightToFit( false );
            }
            else if ( actionName == jetspeed.id.ACTION_NAME_DESKTOP_HEIGHT_NORMAL )
            {
                this.makeHeightVariable( false );
            }
        }
        else if ( actionName == jetspeed.id.ACTION_NAME_MENU )
        {
            this.windowActionMenuOpen( evt );
        }
        else if ( actionName == jetspeed.id.ACTION_NAME_MINIMIZE )
        {   // make no associated content request - just notify server of change
            this.minimizeWindow();
            if ( this.portlet )
            {
                jetspeed.changeActionForPortlet( this.portlet.getId(), jetspeed.id.ACTION_NAME_MINIMIZE, null );
            }
            if ( ! this.portlet )
            {
                this.windowActionButtonSync();
            }
        }
        else if ( actionName == jetspeed.id.ACTION_NAME_RESTORE )
        {   // if minimized, make no associated content request - just notify server of change
            if ( this.portlet )
            {
                if ( this.windowState == jetspeed.id.ACTION_NAME_MAXIMIZE )
                {
                    this.portlet.renderAction( actionName );
                }
                else
                {
                    jetspeed.changeActionForPortlet( this.portlet.getId(), jetspeed.id.ACTION_NAME_RESTORE, null );
                }
            }
            this.restoreWindow();
            if ( ! this.portlet )
            {
                this.windowActionButtonSync();
            }
        }
        else if ( actionName == jetspeed.id.ACTION_NAME_MAXIMIZE )
        {
            if ( this.portlet )
            {
                this.portlet.renderAction( actionName );
            }
            this.maximizeWindow();
            if ( ! this.portlet )
            {
                this.windowActionButtonSync();
            }
        }
        else
        {
            this.portlet.renderAction( actionName );
        }
    },

    _isWindowActionEnabled: function( actionName, currentPortletActionState, currentPortletActionMode )
    {
        var enabled = false;
        if ( actionName == jetspeed.id.ACTION_NAME_MENU )
        {
            enabled = true;
        }
        else if ( jetspeed.prefs.windowActionDesktop[ actionName ] != null )
        {
            if ( actionName == jetspeed.id.ACTION_NAME_DESKTOP_HEIGHT_EXPAND )
            {
                if ( ! this.windowHeightToFit )
                    enabled = true;
            }
            else if ( actionName == jetspeed.id.ACTION_NAME_DESKTOP_HEIGHT_NORMAL )
            {
                if ( this.windowHeightToFit )
                    enabled = true;
            }
            else if ( actionName == jetspeed.id.ACTION_NAME_DESKTOP_TILE && jetspeed.prefs.windowTiling )
            {
                if ( ! this.windowPositionStatic )
                    enabled = true;
            }
            else if ( actionName == jetspeed.id.ACTION_NAME_DESKTOP_UNTILE )
            {
                if ( this.windowPositionStatic )
                    enabled = true;
            }
        }
        else if ( this.portlet )
        {
            var actionDef = this.portlet.getAction( actionName );
            if ( actionDef != null )
            {
                if ( actionDef.type == jetspeed.id.PORTLET_ACTION_TYPE_MODE )
                {
                    if ( actionName != currentPortletActionMode )
                    {
                        enabled = true; 
                    }
                }
                else
                {   // assume actionDef.type == jetspeed.id.PORTLET_ACTION_TYPE_STATE
                    if ( actionName != currentPortletActionState )
                    {
                        enabled = true;
                    }
                }
            }
        }
        else
        {   // adjust visible action buttons - BOZO:NOW: this non-portlet case needs more attention
            if ( actionName == jetspeed.id.ACTION_NAME_MAXIMIZE )
            {
                if ( actionName != this.windowState )
                {
                    enabled = true;
                }
            }
            else if ( actionName == jetspeed.id.ACTION_NAME_MINIMIZE )
            {
                if ( actionName != this.windowState )
                {
                    enabled = true;
                }
            }
            else if ( actionName == jetspeed.id.ACTION_NAME_RESTORE )
            {
                if ( this.windowState == jetspeed.id.ACTION_NAME_MAXIMIZE || this.windowState == jetspeed.id.ACTION_NAME_MINIMIZE )
                {
                    enabled = true;
                }
            }
        }
        return enabled;
    },

    windowActionButtonSync: function()
    {
        var hideButtons = this.windowThemeConfig.windowActionButtonHide;
        var currentPortletActionState = null;
        var currentPortletActionMode = null;
        if ( this.portlet )
        {
            currentPortletActionState = this.portlet.getCurrentActionState();
            currentPortletActionMode = this.portlet.getCurrentActionMode();
        }
        for ( var actionName in this.actionButtons )
        {
            var showBtn = false;
            if ( ! hideButtons || this.titleLit )
            {
                showBtn = this._isWindowActionEnabled( actionName, currentPortletActionState, currentPortletActionMode );
            }
            var buttonNode = this.actionButtons[ actionName ];
            if ( showBtn )
                buttonNode.style.display = "";
            else
                buttonNode.style.display = "none";
        }
    },

    portletInitDimensions: function()
    {
        if ( ! this.templateDomNodeClassName )
            this.templateDomNodeClassName = this.domNode.className;
        var domNodeClassName = this.templateDomNodeClassName;
        if ( this.windowTheme )
        {
            domNodeClassName = this.windowTheme + ( domNodeClassName ? ( " " + domNodeClassName ) : "" );
        }
        this.domNode.className = jetspeed.id.PORTLET_STYLE_CLASS + ( domNodeClassName ? ( " " + domNodeClassName ) : "" );
        
        if ( this.containerNode )
        {
            if ( ! this.templateContainerNodeClassName )
                this.templateContainerNodeClassName = this.containerNode.className;
            var containerNodeClassName = this.templateContainerNodeClassName;
            if ( this.windowTheme )
            {
                containerNodeClassName = this.windowTheme + ( containerNodeClassName ? ( " " + containerNodeClassName ) : "" );
            }
            this.containerNode.className = jetspeed.id.PORTLET_STYLE_CLASS + ( containerNodeClassName ? ( " " + containerNodeClassName ) : "" );
        }

        this._adjustPositionToDesktopState();

        //this.resizeTo( null, null, true );

        //dojo.debug( "portletInitDimensions [" + this.widgetId + "] completed - domNode.style.width=" + this.domNode.style.width + " domNode.style.height=" + this.domNode.style.height );
    },

    //     resetWindow: function( /* Portlet */ portlet )
    resetWindow: function( portlet )
    {
        this.portlet = portlet;
        this.portletMixinProperties();
        this.portletInitDragHandle();
        this.portletInitDimensions();
    },

    // dojo.widget.Widget create protocol
    postCreate: function( args, fragment, parentComp )
    {   // FloatingPane 0.3.1 essentially calls resizeTo - this is done in portletInitDimensions()
        this.drag = new jetspeed.widget.PortletWindowDragMoveSource( this );
        if ( this.constrainToContainer )
        {
            this.drag.constrainTo();
        }
        this.drag.setDragHandle( this.titleBar );
        
        this.domNode.id = this.widgetId;  // BOZO: must set the id here - it gets defensively cleared by dojo
        
        this.portletInitDimensions();
        
        if ( jetspeed.debug.createWindow )
            dojo.debug( "createdWindow [" + ( this.portlet ? this.portlet.entityId : this.widgetId ) + ( this.portlet ? (" / " + this.widgetId) : "" ) + "]" + " width=" + this.domNode.style.width + " height=" + this.domNode.style.height + " left=" + this.domNode.style.left + " top=" + this.domNode.style.top ) ;

        this.portletInitialized = true;

        var initWindowState = this.getInitProperty( jetspeed.id.PORTLET_PROP_WINDOW_STATE );
        if ( initWindowState == jetspeed.id.ACTION_NAME_MINIMIZE )
        {
            this.minimizeWindow();
            this.windowActionButtonSync();
        }
    },

    // dojo.widget.ContentPane protocol
    loadContents: function()
    {   // do nothing
    },

    isPortletWindowInitialized: function()
    {
        return this.portletInitialized;
    },

    minimizeWindow: function( evt )
    {
        this._setLastPositionInfo();

        this.containerNode.style.display = "none";
        this.resizeBar.style.display = "none";
        dojo.html.setContentBox( this.domNode, { height: dojo.html.getMarginBox( this.titleBar ).height } );
    
        this.windowState = jetspeed.id.ACTION_NAME_MINIMIZE;
    },
    maximizeWindow: function( evt )
    {
        var tiledStateIsChanging = this.windowPositionStatic;
        this._setLastPositionInfo( tiledStateIsChanging, true );
        
        var jetspeedDesktop = document.getElementById( jetspeed.id.DESKTOP );
        if ( this.windowPositionStatic )
        {
            this.domNode.style.position = "absolute";
            jetspeedDesktop.appendChild( this.domNode );
        }
        jetspeed.widget.PortletWindow.superclass.bringToTop.call( this, evt );
        
        // hardcoded to fill document.body width leaving 1px on each side
        var yPos = dojo.html.getAbsolutePosition( jetspeedDesktop, true ).y;    // passing true to fix position at top (so not affected by vertically scrolled window)
		this.domNode.style.left = "1px";
		this.domNode.style.top = yPos;

        this.windowPositionStatic = false;

        var jetspeedPage = document.getElementById( jetspeed.id.PAGE );

        var viewport = dojo.html.getViewport();
        var padding = dojo.html.getPadding( dojo.body() );
        
        this.resizeTo( viewport.width - padding.width - 2, viewport.height - padding.height - yPos );

        //this.resizeTo(
        //    dojo.html.getContentBox( jetspeedPage ).width - 2,
        //    dojo.html.getBorderBox( document.body ).height - yPos
		//);

		this.windowState = jetspeed.id.ACTION_NAME_MAXIMIZE;
	},
	restoreWindow: function( evt )
    {
        var currentlyAbsolute = false;
        if ( this.domNode.style.position == "absolute" )
        {
            currentlyAbsolute = true;
        }

        var lastPositionInfo = null;
        if ( this.windowState == jetspeed.id.ACTION_NAME_MAXIMIZE )
        {
            this.windowPositionStatic = ( this.lastWindowPositionStatic != null ? this.lastWindowPositionStatic : false );
        }

        this.containerNode.style.display = "";
        this.resizeBar.style.display = "";

        if ( this.windowPositionStatic )
        {
            lastPositionInfo = this.lastTiledPositionInfo;
        }
        else
        {
            lastPositionInfo = this.lastUntiledPositionInfo;
        }

        var lpiWidth = null;
        var lpiHeight = null;
        if ( lastPositionInfo != null )
        {
            lpiWidth = lastPositionInfo.width;
            lpiHeight = lastPositionInfo.height;
            for ( var attr in lastPositionInfo )
            {
                if ( attr != "columnInfo" )
			        this.domNode.style[ attr ] = lastPositionInfo[ attr ];
		    }
        }

        this._adjustPositionToDesktopState();
        
        if ( this.windowPositionStatic && currentlyAbsolute )
        {   // tiled window in maximized needs to be placed back in previous column/row
            if ( lastPositionInfo != null && lastPositionInfo.columnInfo != null && lastPositionInfo.columnInfo.columnIndex != null )
            {
                var columnElmt = jetspeed.page.columns[ lastPositionInfo.columnInfo.columnIndex ];
                if ( lastPositionInfo.columnInfo.previousSibling )
                    dojo.dom.insertAfter( this.domNode, lastPositionInfo.columnInfo.previousSibling );
                else if ( lastPositionInfo.columnInfo.nextSibling )
                    dojo.dom.insertBefore( this.domNode, lastPositionInfo.columnInfo.nextSibling );
                else
                    columnElmt.domNode.appendChild( this.domNode );
            }
            else
            {
                if ( jetspeed.page.columns != null && jetspeed.page.columns.length > 0 )
                    dojo.dom.prependChild( this.domNode, jetspeed.page.columns[ 0 ].domNode );
            }
            this.domNode.style.position = "static";
        }
        
		this.resizeTo( lpiWidth, lpiHeight, true );

		this.windowState = jetspeed.id.ACTION_NAME_RESTORE;  // "normal"
	},

    _setLastPositionInfo: function( tiledStateIsChanging, changingToMaximized )
    {
        if ( changingToMaximized )
        {
            this.lastWindowPositionStatic = this.windowPositionStatic;
        }
        if ( this.windowPositionStatic )
        {
            if ( this.lastTiledPositionInfo == null )
            {
                this.lastTiledPositionInfo = {};
            }
            if ( tiledStateIsChanging )
            {   // record col/row location
                var columnInfo = {};
                var sibling = dojo.dom.getPreviousSiblingElement( this.domNode );
                if ( sibling )
                    columnInfo.previousSibling = sibling;
                else
                {
                    sibling = dojo.dom.getNextSiblingElement( this.domNode );
                    if ( sibling )
                        columnInfo.nextSibling = sibling;
                }
                columnInfo.columnIndex = this.getPageColumnIndex();
                this.lastTiledPositionInfo.columnInfo = columnInfo;
            }
            if ( this.windowState != jetspeed.id.ACTION_NAME_MINIMIZE && this.windowState != jetspeed.id.ACTION_NAME_MAXIMIZE )
            {
                this.lastTiledPositionInfo.height = this.domNode.style.height;
            }
            this.lastTiledPositionInfo.width = "";
        }
        else
        {
            if ( this.windowState != jetspeed.id.ACTION_NAME_MINIMIZE && this.windowState != jetspeed.id.ACTION_NAME_MAXIMIZE )
            {
                var domNodeMarginBox = dojo.html.getMarginBox( this.domNode ) ;
                this.lastUntiledPositionInfo =
                {
			        width: domNodeMarginBox.width,
			        height: domNodeMarginBox.height,
			        left: this.domNode.style.left,
			        top: this.domNode.style.top,
			        bottom: this.domNode.style.bottom,
			        right: this.domNode.style.right
                };
            }
        }
    },
    _updateLastPositionInfoPositionOnly: function()
    {
        if ( ! this.windowPositionStatic && this.lastUntiledPositionInfo != null )
        {
            this.lastUntiledPositionInfo.left = this.domNode.style.left;
            this.lastUntiledPositionInfo.top = this.domNode.style.top;
        }
    },

    bringToTop: function( evt )
    {
        var beforeZIndex = this.domNode.style.zIndex;
        jetspeed.widget.PortletWindow.superclass.bringToTop.call( this, evt );
        if ( this.portlet && ! this.windowPositionStatic && this.windowState != jetspeed.id.ACTION_NAME_MAXIMIZE && this.isPortletWindowInitialized() )
        {
            this.portlet.submitChangedWindowState();
            //dojo.debug( "bringToTop [" + this.portlet.entityId + "] zIndex   before=" + beforeZIndex + " after=" + this.domNode.style.zIndex );
        }
    },
    makeUntiled: function()
    {
        this._setLastPositionInfo( true, false );

        var winWidth = null;
        var winHeight = null;
        var winLeft = null;
        var winTop = null;

        var lastUntiledPosInfo = this.lastUntiledPositionInfo;
        if ( lastUntiledPosInfo != null &&
             lastUntiledPosInfo.width != null && lastUntiledPosInfo.height != null &&
             lastUntiledPosInfo.left != null && lastUntiledPosInfo.top != null )
        {   // use last untiled position if all properties are defined in this.lastUntiledPositionInfo
            winWidth = lastUntiledPosInfo.width;
            winHeight = lastUntiledPosInfo.height;
            winLeft = lastUntiledPosInfo.left;
            winTop = lastUntiledPosInfo.top;
        }
        else
        {   // determine initial untiled position based on current tiled position
            var positioningNode = this.domNode;
            var winAbsPos = dojo.html.getAbsolutePosition( positioningNode, true );
            var winMarginTop = dojo.html.getPixelValue( positioningNode, "margin-top", true );
            var winMarginLeft = dojo.html.getPixelValue( positioningNode, "margin-left", true );
            var domNodeMarginBox = dojo.html.getMarginBox( this.domNode ) ;
            winWidth = domNodeMarginBox.width;
            winHeight = domNodeMarginBox.height;
            winLeft = winAbsPos.x - winMarginTop;
            winTop = winAbsPos.y - winMarginLeft;
        }
        this.domNode.style.position = "absolute";
        
        this.domNode.style.left = winLeft;
        this.domNode.style.top = winTop;

        this.windowPositionStatic = false;

        this._adjustPositionToDesktopState();
        
        this.resizeTo( winWidth, winHeight, true );
        
        var addToElmt = document.getElementById( jetspeed.id.DESKTOP );
        addToElmt.appendChild( this.domNode );

        if ( this.portlet )
            this.portlet.submitChangedWindowState();
    },
    makeTiled: function()
    {
        this.windowPositionStatic = true;
        
        this.restoreWindow();

        if ( this.portlet )
            this.portlet.submitChangedWindowState();
    },

    makeHeightToFit: function( suppressSubmitChange, suppressLogging )
    {   // suppressLogging is to support contentChanged
        var domNodePrevMarginBox = dojo.html.getMarginBox( this.domNode ) ;

        this.windowHeightToFit = true;

        this._adjustPositionToDesktopState();

        if ( suppressLogging == null || suppressLogging != true )
        {   // flags are to avoid init problems with dojo-debug window when height-to-fit is set (causing stack overflow when dojo.debug() is called)
            //dojo.debug( "makeHeightToFit [" + this.widgetId + "] prev w=" + domNodePrevMarginBox.width + " h=" + domNodePrevMarginBox.height + "  new w=" + domNodeMarginBox.width + " h=" + domNodeMarginBox.height );
        }
    
        this.resizeTo( null, null, true );

        if ( ! suppressSubmitChange && this.portlet )
            this.portlet.submitChangedWindowState();
    },
    makeHeightVariable: function( suppressSubmitChange )
    {
        var domNodePrevMarginBox = dojo.html.getMarginBox( this.domNode ) ;

        this.windowHeightToFit = false;

        this._adjustPositionToDesktopState();

        //dojo.debug( "makeHeightVariable [" + this.widgetId + "] prev w=" + domNodePrevMarginBox.width + " h=" + domNodePrevMarginBox.height + "  new w=" + domNodeMarginBox.width + " h=" + domNodeMarginBox.height );
        //dojo.debug( "makeHeightVariable [" + this.widgetId + "] containerNode PREV style.width=" + this.containerNode.style.width + " style.height=" + this.containerNode.style.height );
        
        this.resizeTo( null, null, true );
    
        if ( dojo.render.html.ie )
            dojo.lang.setTimeout( this, this._IEPostResize, 10 );

        //dojo.debug( "makeHeightVariable [" + this.widgetId + "] containerNode NEW style.width=" + this.containerNode.style.width + " style.height=" + this.containerNode.style.height );

        if ( ! suppressSubmitChange && this.portlet )
            this.portlet.submitChangedWindowState();
    },

    resizeTo: function( w, h, force )
    {
        //dojo.debug( "resizeTo [" + this.widgetId + "] begin w=" + w + " h=" + h + " container[w=" + dojo.html.getMarginBox( this.containerNode ).width + " h=" + dojo.html.getMarginBox( this.containerNode ).height + "] domNode[w=" + dojo.html.getMarginBox( this.domNode ).width + " h=" + dojo.html.getMarginBox( this.domNode ).height + "]" );
        
        if ( w == null || w == 0 || isNaN( w ) || h == null || h == 0 || isNaN( h ) )
        {
            var domNodeMarginBox = dojo.html.getMarginBox( this.domNode ) ;
            if ( w == null || w == 0 || isNaN( w ) )
                w = domNodeMarginBox.width;
            if ( h == null || h == 0 || isNaN( h ) )
                h = domNodeMarginBox.height;
        }
        
		if ( w == this.lastWidthResizeTo && h == this.lastHeightResizeTo && ! force )
        {
            //dojo.debug( "resize unneeded [" + this.widgetId + "]" );
			return;
		}
		this.lastWidthResizeTo = w;
		this.lastHeightResizeTo = h;

        this.resetLostHeightWidth();

		// IE won't let you decrease the width of the domnode unless you decrease the
		// width of the inner nodes first (???)

		dojo.lang.forEach(
			[ this.titleBar, this.resizeBar, this.containerNode ],
			function( node ){ dojo.html.setMarginBox( node, { width: w - this.lostWidth } ); }, this
		);

        //dojo.debug( "resizeTo [" + this.widgetId + "] before-adjust w=" + w + " h=" + h + " container[w=" + dojo.html.getMarginBox( this.containerNode ).width + " h=" + dojo.html.getMarginBox( this.containerNode ).height + " style-width=" + this.containerNode.style.width + " style-height=" + this.containerNode.style.height + "] domNode[w=" + dojo.html.getMarginBox( this.domNode ).width + " h=" + dojo.html.getMarginBox( this.domNode ).height + "]" );

        if ( this.windowPositionStatic )
        {
            this.domNode.style.width = "";
            if ( this.titleBar )
                this.titleBar.style.width = "";
            if ( this.resizeBar )
                this.resizeBar.style.width = "";
            if ( this.containerNode )
            {
                if ( dojo.render.html.ie )
                {
                    //dojo.lang.setTimeout( this, this._IEPostResize, 10 );
                    // IE will adjust consistently if step is deferred
                    //this.containerNode.style.width = "99%";
                    this.containerNode.style.width = "100%";
                    //this.containerNode.style.marginLeft = "2px";
                }
                else
                {
                    this.containerNode.style.width = "";  // I only know that ff 1.5 likes it blanked and ie6 likes it 100%
                }
            }
        }
        else
        {
            dojo.html.setMarginBox( this.domNode, { width: w } );
        }

        this.resetLostHeightWidth();

        if ( h < ( this.lostHeight + 60 ) )
        {
            h = this.lostHeight + 60;
        }

        dojo.html.setMarginBox( this.domNode, { height: h } );
        dojo.html.setMarginBox( this.containerNode, { height: h-this.lostHeight } );

        this.bgIframe.onResized();
        if ( this.shadow )
        {
            this.shadow.size( w, h );
        }
		this.onResized();

        //dojo.debug( "resizeTo [" + this.widgetId + "] end w=" + w + " h=" + h + " container[w=" + dojo.html.getMarginBox( this.containerNode ).width + " h=" + dojo.html.getMarginBox( this.containerNode ).height + " desired-h=" + (h-this.lostHeight) + " style-width=" + this.containerNode.style.width + " style-height=" + this.containerNode.style.height + "] domNode[w=" + dojo.html.getMarginBox( this.domNode ).width + " h=" + dojo.html.getMarginBox( this.domNode ).height + "]" );
	},

    _IEPostResize: function()
    {   // IE will adjust consistently if step is deferred - setting to 99 then 100 is to force it to re-render,
        // which fixes the IE problem where part of containerNode scroll bars outside window bounds
        //
        // NOTE: not in use currently from resizeTo - slows down resize too much
        this.containerNode.style.width = "99%";
        this.containerNode.style.width = "100%";
    },

    _adjustPositionToDesktopState: function()
    {   // sets window dimension appropriatly based on 
        // this.windowPositionStatic and this.windowHeightToFit
        if ( this.windowPositionStatic )
        {            
            this.domNode.style.position = "static";  // can't be done earlier (this comment is from portletInitDimensions - not sure of full meaning here)
            this.domNode.style.left = "auto";
            this.domNode.style.top = "auto";
        }
        else
        {
            this.domNode.style.position = "absolute";
            // BOZO: untiled-window - what about left/top here?
        }

        if ( this.windowHeightToFit )
        {
            this.domNode.style.overflow = "visible";
            this.domNode.style.height = "";
        }
        else
            this.domNode.style.overflow = "hidden";

        if ( this.windowPositionStatic )
        {
            this.domNode.style.width = "";
            if ( this.titleBar )
                this.titleBar.style.width = "";
            if ( this.resizeBar )
                this.resizeBar.style.width = "";
        }
        else
        {
            // BOZO: untiled-window - what about width here?
            // BOZO: may want record values for these as early as possible
            //       keeping them up-to-date so that this method can reliable
            //       set these values
        }

        // BOZO: what about resize handle ?


        if ( this.containerNode )
        {
            if ( this.windowHeightToFit )
            {
                this.containerNode.style.overflow = "visible";
                this.containerNode.style.height = "";
            }
            else
            {
                this.containerNode.style.overflow = "auto";
            }
            if ( dojo.render.html.ie )
            {
                this.containerNode.style.width = "100%";
            }
            else
            {
                this.containerNode.style.width = "";  // I only know that ff 1.5 likes it blanked and ie6 likes it 100%
            }
        }
        //dojo.debug( "_adjustPositionToDesktopState [" + this.widgetId + "] completed - domNode.style.width=" + this.domNode.style.width + " domNode.style.height=" + this.domNode.style.height );
    },

    resetLostHeightWidth: function()
    {
        // figure out how much space is used for padding/borders etc.
        var domNodeMarginBox = dojo.html.getMarginBox( this.domNode ) ;
        var domNodeContentBox = dojo.html.getContentBox( this.domNode ) ;

		this.lostHeight=
			( domNodeMarginBox.height - domNodeContentBox.height )
			+ dojo.html.getMarginBox(this.titleBar).height
			+ dojo.html.getMarginBox(this.resizeBar).height;
		this.lostWidth = domNodeMarginBox.width - domNodeContentBox.width;
    },

    contentChanged: function( evt )
    {   // currently used for dojo-debug window only
        if ( this.processingContentChanged == false )
        {
            this.processingContentChanged = true;
            if ( this.windowHeightToFit )
            {
                this.makeHeightToFit( true, true );
            }
            this.processingContentChanged = false;
        }
    },
 
    closeWindow: function()
    {
        jetspeed.widget.PortletWindow.superclass.closeWindow.call( this );
        var resizeWidget = this.getResizeHandleWidget();
        if ( resizeWidget )
            resizeWidget.destroy();
    },
    dumpPostionInfo: function()
    {
        var winAbsPos = dojo.html.getAbsolutePosition( this.domNode, true );
        var domNodeMarginBox = dojo.html.getMarginBox( this.domNode ) ;
        var winWidth = domNodeMarginBox.width;
        var winHeight = domNodeMarginBox.height;
        var containerNodeMarginBox = dojo.html.getMarginBox( this.containerNode ) ;
        var winContainerNodeWidth = containerNodeMarginBox.width;
        var winContainerNodeHeight = containerNodeMarginBox.height;
        
        dojo.debug( "window-position [" + this.widgetId + "] x=" + winAbsPos.x + " y=" + winAbsPos.y + " width=" + winWidth + " height=" + winHeight + " cNode-width=" + winContainerNodeWidth + " cNode-height=" + winContainerNodeHeight + " document-width=" + dojo.html.getMarginBox( document[ "body" ] ).width + " document-height=" + dojo.html.getMarginBox( document[ "body" ] ).height ) ;
    },
    
    getPageColumnIndex: function()
    {
        var inColIndex = null;
        if ( ! jetspeed.page.columns ) return inColIndex;
        for ( var i = 0 ; i < jetspeed.page.columns.length ; i++ )
        {
            if ( jetspeed.page.columns[i].containsNode( this.domNode ) )
            {
                inColIndex = i;
                break;
            }
        }
        return inColIndex;
    },
    getResizeHandleWidget: function()
    {
        return dojo.widget.byId( this.widgetId + "_resize" );   // BOZO:DOJO: bad way of obtaining this reference
    },
    onResized: function()
    {
        jetspeed.widget.PortletWindow.superclass.onResized.call( this );
        //dojo.debug( "onResized [" + this.widgetId + "]" );
        if ( ! this.windowIsSizing )
        {
            var resizeWidget = this.getResizeHandleWidget();
            if ( ! resizeWidget )
                dojo.raise( "PortletWindow cannot find its resize widget" );
        
            if ( resizeWidget.isSizing )
            {
                dojo.event.connect( resizeWidget, "endSizing", this, "endSizing" );
                // NOTE: connecting directly to document.body onmouseup results in notification for second and subsequent onmouseup
                this.windowIsSizing = true;
            }
        }
    },
    endSizing: function(e)
    {
        //dojo.debug( "PortletWindow.endSizing [" + this.portlet.entityId + "]" );
        dojo.event.disconnect( document.body, "onmouseup", this, "endSizing" );
        this.windowIsSizing = false;
        if ( this.portlet && this.windowState != jetspeed.id.ACTION_NAME_MAXIMIZE )
            this.portlet.submitChangedWindowState();
    },
    endDragging: function()
    {
        if ( this.portlet && this.windowState != jetspeed.id.ACTION_NAME_MAXIMIZE )
            this.portlet.submitChangedWindowState();
    },

    titleLight: function()
    {
        var toBeEnlightened = [];
        var currentPortletActionState = null;
        var currentPortletActionMode = null;
        if ( this.portlet )
        {
            currentPortletActionState = this.portlet.getCurrentActionState();
            currentPortletActionMode = this.portlet.getCurrentActionMode();
        }
        for ( var actionName in this.actionButtons )
        {
            var showBtn = this._isWindowActionEnabled( actionName, currentPortletActionState, currentPortletActionMode );
            if ( showBtn )
            {
                var buttonNode = this.actionButtons[ actionName ];
                toBeEnlightened.push( buttonNode );
            }
        }
        for ( var i = 0 ; i < toBeEnlightened.length ; i++ )
        {
            toBeEnlightened[i].style.display = "" ;
        }
        //jetspeed.ui.fadeIn( toBeEnlightened, 325, "" );
        this.titleLit = true ;
    },
    titleDim: function( immediateForce )
    {
        var toBeExtinguished = [];
        for ( var actionName in this.actionButtons )
        {
            var buttonNode = this.actionButtons[ actionName ];
            if ( buttonNode.style.display != "none" )
            {
                toBeExtinguished.push( buttonNode );
            }
        }
    
        for ( var i = 0 ; i < toBeExtinguished.length ; i++ )
        {
            toBeExtinguished[i].style.display = "none" ;
        }
        //jetspeed.ui.fadeOut( toBeExtinguished, 280, [ this.restoreAction ] );   // nodes in 3rd arg will be set to display=none
        this.titleLit = false ;
    },
    titleMouseOver: function( evt )
    {
        if ( this.windowThemeConfig.windowActionButtonHide )
        {
            var self = this ;
            this.titleMouseIn = 1 ;   // was ++
            window.setTimeout( function() { if ( self.titleMouseIn > 0 ) { self.titleLight(); self.titleMouseIn = 0; } }, 270 ) ;
            // NOTE: setup in template HtmlFloatingPane.html: dojoAttachEvent="onMouseOver:titleMouseOver;onMouseOut:titleMouseOut"
        }
    },
    titleMouseOut: function( evt )
    {
        if ( this.windowThemeConfig.windowActionButtonHide )
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
                // NOTE: setup in template HtmlFloatingPane.html: dojoAttachEvent="onMouseOver:titleMouseOver;onMouseOut:titleMouseOut"
            }
        }
    },
    
    getCurrentVolatileWindowState: function()
    {   // window state which can be side-affected by changes to another window
        if ( ! this.domNode ) return null;
        var cWinState = {};
        if ( ! this.windowPositionStatic )
            cWinState.zIndex = this.domNode.style.zIndex;
        return cWinState;
    },
    getCurrentWindowState: function()
    {
        if ( ! this.domNode ) return null;
        var cWinState = this.getCurrentVolatileWindowState();
        cWinState.width = this.domNode.style.width;
        cWinState.height = this.domNode.style.height;

        cWinState[ jetspeed.id.PORTLET_PROP_WINDOW_POSITION_STATIC ] = this.windowPositionStatic;
        cWinState[ jetspeed.id.PORTLET_PROP_WINDOW_HEIGHT_TO_FIT ] = this.windowHeightToFit;

        if ( ! this.windowPositionStatic )
        {
            cWinState.left = this.domNode.style.left;
            cWinState.top = this.domNode.style.top;
        }
        else
        {
            var columnRowResult = jetspeed.page.getPortletCurrentColumnRow( this.domNode );
            if ( columnRowResult != null )
            {
                cWinState.column = columnRowResult.column;
                cWinState.row = columnRowResult.row;
                cWinState.layout = columnRowResult.layout;
            }
            else
            {
                dojo.raise( "PortletWindow.getCurrentWindowState cannot not find row/column/layout of window: " + this.widgetId ) ;
                // BOZO:NOW: test this with maximize/minimize
            }
        }
        return cWinState;
    },
    setPortletContent: function( html, url )
    {
        var initialHtmlStr = html.toString();
        
        if ( ! this.getInitProperty( jetspeed.id.PORTLET_PROP_EXCLUDE_PCONTENT ) )
        {
            initialHtmlStr = '<div class="PContent" >' + initialHtmlStr + '</div>';   // BOZO: get this into the template ?
        }

        /* IMPORTANT:
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

        var setContentObj = this._splitAndFixPaths_scriptsonly( initialHtmlStr, url );
        
        this.setContent( setContentObj );
        this._executeScripts( setContentObj.scripts );

        if ( jetspeed.debug.setPortletContent )
            dojo.debug( "setPortletContent [" + ( this.portlet ? this.portlet.entityId : this.widgetId ) + "]" );

        if ( this.portlet )
            this.portlet.postParseAnnotateHtml( this.containerNode );
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
				var regexDojoJs = /.*(\bdojo\b\.js(?:\.uncompressed\.js)?)$/;
				var regexInvalid = /(?:var )?\bdjConfig\b(?:[\s]*=[\s]*\{[^}]+\}|\.[\w]*[\s]*=[\s]*[^;\n]*)?;?|dojo\.hostenv\.writeIncludes\(\s*\);?/g;
				var regexRequires = /dojo\.(?:(?:require(?:After)?(?:If)?)|(?:widget\.(?:manager\.)?registerWidgetPackage)|(?:(?:hostenv\.)?setModulePrefix|registerModulePath)|defineNamespace)\((['"]).*?\1\)\s*;?/;

				while(match = regex.exec(s)){
					if(forcingExecuteScripts && match[1]){
						if(attr = regexSrc.exec(match[1])){
							// remove a dojo.js or dojo.js.uncompressed.js from remoteScripts
							// we declare all files named dojo.js as bad, regardless of path
							if(regexDojoJs.exec(attr[2])){
								dojo.debug("Security note! inhibit:"+attr[2]+" from  being loaded again.");
							}else{
								scripts.push({path: attr[2]});
							}
						}
					}
					if(match[2]){
						// remove all invalid variables etc like djConfig and dojo.hostenv.writeIncludes()
						var sc = match[2].replace(regexInvalid, "");
    						if(!sc){ continue; }
		
						// cut out all dojo.require (...) calls, if we have execute 
						// scripts false widgets dont get there require calls
						// takes out possible widgetpackage registration as well
						while(tmp = regexRequires.exec(sc)){
							requires.push(tmp[0]);
							sc = sc.substring(0, tmp.index) + sc.substr(tmp.index + tmp[0].length);
						}
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
    }
});

// ... PortletWindow drag ghost
jetspeed.widget.pwGhost = document.createElement("div");
jetspeed.widget.pwGhost.id = "pwGhost";

jetspeed.widget.PortletWindowResizeHandle = function()
{
    dojo.widget.ResizeHandle.call( this );
    this.widgetType = "PortletWindowResizeHandle";
}
dojo.inherits( jetspeed.widget.PortletWindowResizeHandle, dojo.widget.ResizeHandle );

dojo.lang.extend( jetspeed.widget.PortletWindowResizeHandle, {
    changeSizing: function(e){
        if ( this.portletWindow.windowHeightToFit )
        {
            this.portletWindow.makeHeightVariable( true );
        }

        // On IE, if you move the mouse above/to the left of the object being resized,
		// sometimes clientX/Y aren't set, apparently.  Just ignore the event.
		try{
			if(!e.clientX  || !e.clientY){ return; }
		}catch(e){
			// sometimes you get an exception accessing above fields...
			return;
		}
		var dx = this.startPoint.x - e.clientX;
		var dy = this.startPoint.y - e.clientY;

		var newW = this.startSize.w - dx;
		var newH = this.startSize.h - dy;

        if ( this.portletWindow.windowPositionStatic )
        {
            newW = this.startSize.w;
        }

		// minimum size check
		if (this.minSize) {
			if (newW < this.minSize.w) {
				newW = dojo.html.getMarginBox( this.targetWidget.domNode ).width;
			}
			if (newH < this.minSize.h) {
				newH = dojo.html.getMarginBox( this.targetWidget.domNode ).height;
			}
		}
		
		this.targetWidget.resizeTo(newW, newH);
		
		e.preventDefault();
	}
    
});

jetspeed.widget.PortletWindowDragMoveSource = function( /* jetspeed.widget.PortletWindow */ portletWindow, type)
{
    this.portletWindow = portletWindow;
	dojo.dnd.HtmlDragMoveSource.call(this, portletWindow.domNode, type);
};

dojo.inherits( jetspeed.widget.PortletWindowDragMoveSource, dojo.dnd.HtmlDragMoveSource );

dojo.lang.extend( jetspeed.widget.PortletWindowDragMoveSource, {
	onDragStart: function()
    {
        // BOZO: code copied from dojo.dnd.HtmlDragMoveSource.onDragStart to change dragObject
        var dragObj = new jetspeed.widget.PortletWindowDragMoveObject( this.portletWindow, this.dragObject, this.type );

		if ( this.constrainToContainer )
        {
			dragObj.constrainTo( this.constrainingContainer );
		}

		return dragObj;
	},
    onDragEnd: function()
    {
    }
});

jetspeed.widget.PortletWindowDragMoveObject = function( portletWindow, node, type )
{
    this.portletWindow = portletWindow;
    this.windowPositionStatic = this.portletWindow.windowPositionStatic;
	dojo.dnd.HtmlDragMoveObject.call( this, node, type );
}

dojo.inherits( jetspeed.widget.PortletWindowDragMoveObject, dojo.dnd.HtmlDragMoveObject );

dojo.lang.extend( jetspeed.widget.PortletWindowDragMoveObject, {
    onDragStart: function( e )
    {
        this.portletWindow.isDragging = true;

        var portletWindowNode = this.domNode;

        this.initialStyleWidth = portletWindowNode.style.width;
        this.initialOffsetWidth = portletWindowNode.offsetWidth;

        jetspeed.widget.PortletWindowDragMoveObject.superclass.onDragStart.call( this, e );

        // ghost placement - must happen after superclass.onDragStart
        var pwGhost = jetspeed.widget.pwGhost;

        if ( this.windowPositionStatic )
        {
            portletWindowNode.style.width = this.initialOffsetWidth;
            // ghost placement - must happen after superclass.onDragStart
            pwGhost.style.height = portletWindowNode.offsetHeight+"px";
            portletWindowNode.parentNode.insertBefore( pwGhost, portletWindowNode );

            // domNode removal from column - add to desktop for visual freeform drag
            document.getElementById( jetspeed.id.DESKTOP ).appendChild( portletWindowNode );

            var inColIndex = this.portletWindow.getPageColumnIndex();

            this.columnsX = new Array( jetspeed.page.columns.length );
            for ( var i = 0 ; i < jetspeed.page.columns.length ; i++ )
            {
                this.columnsX[ i ] = dojo.html.getAbsolutePosition( jetspeed.page.columns[i].domNode, true ).x;
            }
            
            var inCol = ( inColIndex >= 0 ? jetspeed.page.columns[ inColIndex ] : null );
            pwGhost.col = inCol;
        }

        //dojo.debug( "PortletWindowDragMoveObject [" + this.portletWindow.widgetId + "] onDragStart:  portletWindowNode.hasParent=" + dojo.dom.hasParent( portletWindowNode ) + " dragOffset.left=" + this.dragOffset.left + " dragOffset.top=" + this.dragOffset.top + " dragStartPosition.left=" + this.dragStartPosition.left + " dragStartPosition.top=" + this.dragStartPosition.top );
    },
    onDragMove: function( e )
    {
        //jetspeed.widget.PortletWindowDragMoveObject.superclass.onDragMove.call( this, e );
        // BOZO: code copied from dojo.dnd.HtmlDragMoveObject.onDragMove

        var mouse = dojo.html.getCursorPosition(e);
		this.updateDragOffset();
		var x = this.dragOffset.x + mouse.x;
		var y = this.dragOffset.y + mouse.y;
        //var x = mouse.x ;
        //var y = mouse.y ;

		if (this.constrainToContainer) {
			if (x < this.constraints.minX) { x = this.constraints.minX; }
			if (y < this.constraints.minY) { y = this.constraints.minY; }
			if (x > this.constraints.maxX) { x = this.constraints.maxX; }
			if (y > this.constraints.maxY) { y = this.constraints.maxY; }
		}

		if(!this.disableY) { this.dragClone.style.top = y + "px"; }
		if(!this.disableX) { this.dragClone.style.left = x + "px"; }

        var pwGhost = jetspeed.widget.pwGhost;

        if ( this.windowPositionStatic )
        {
            var colIndex = -1;
            for ( var i = jetspeed.page.columns.length-1 ; i >= 0  ; i-- )
            {
                //dojo.debug( "PortletWindowDragMoveObject onDragMove: col[" + i + "] columnsX=" + this.columnsX[i] + " this.domNode.offsetWidth/2=" + (this.domNode.offsetWidth/2) + " x=" + x );
                if ( ( x + ( this.domNode.offsetWidth / 2 ) ) >= this.columnsX[ i ] )
                {
                    if ( y + ( this.domNode.offsetHeight / 2 ) >=  dojo.html.getAbsolutePosition( jetspeed.page.columns[i].domNode, true ).y )
                    {
                        colIndex = i;
                        break;
                    }
                }
            }
            var col = ( colIndex >= 0 ? jetspeed.page.columns[ colIndex ] : null );
            //if ( col != null )
            //    dojo.debug( "PortletWindowDragMoveObject onDragMove: col[" + colIndex + "] columnsX=" + this.columnsX[colIndex] + " this.domNode.offsetWidth=" + this.domNode.offsetWidth + " x=" + x );
            //else
            //    dojo.debug( "PortletWindowDragMoveObject onDragMove: no col  this.domNode.offsetWidth=" + this.domNode.offsetWidth + " x=" + x );
            
            if ( pwGhost.col != col )
            {
                dojo.dom.removeNode( pwGhost );
				pwGhost.col = col;
				col.domNode.appendChild(pwGhost);
			}
            
            var portletWindowsResult = jetspeed.ui.getPortletWindowChildren( col.domNode, pwGhost );
            var portletWindowsInCol = portletWindowsResult.portletWindowNodes;
            
            if ( portletWindowsInCol )
            {
                var ghostIndex = portletWindowsResult.matchIndex;
                if ( ghostIndex > 0 )
                {
                    var yAboveWindow = dojo.html.getAbsolutePosition( portletWindowsInCol[ ghostIndex -1 ], true ).y;
                    if ( y <= yAboveWindow )
                    {
                        //dojo.debug( "onDragMove y <= yAbove [" + this.portletWindow.widgetId + "] y=" + y + " yAboveWindow=" + yAboveWindow + " ghostIndex=" + ghostIndex );
                        dojo.dom.removeNode( pwGhost );
                        dojo.dom.insertBefore( pwGhost, portletWindowsInCol[ ghostIndex -1 ], true );
                    }
                    else
                    {
                        //dojo.debug( "onDragMove noadjust y > yAbove [" + this.portletWindow.widgetId + "] y=" + y + " yAboveWindow=" + yAboveWindow + " ghostIndex=" + ghostIndex );
                    }
                }
                if ( ghostIndex != (portletWindowsInCol.length -1) )
                {
                    var yBelowWindow = dojo.html.getAbsolutePosition( portletWindowsInCol[ ghostIndex +1 ], true ).y;
                    if ( y >= yBelowWindow )
                    {
                        //dojo.debug( "onDragMove y >= yBelow [" + this.portletWindow.widgetId + "] y=" + y + " yBelowWindow=" + yBelowWindow + " ghostIndex=" + ghostIndex );
                        if ( ghostIndex + 2 < portletWindowsInCol.length )
                            dojo.dom.insertBefore( pwGhost, portletWindowsInCol[ ghostIndex +2 ], true );
                        else
                            col.domNode.appendChild( pwGhost );
                    }
                    else
                    {
                        //dojo.debug( "onDragMove noadjust y < yBelow [" + this.portletWindow.widgetId + "] y=" + y + " yBelowWindow=" + yBelowWindow + " ghostIndex=" + ghostIndex );
                    }
                }
            }
        }
    },
	onDragEnd: function( e )
    {
        if ( this.initialStyleWidth != this.domNode.style.width )
        {
            this.domNode.style.width = this.initialStyleWidth;
        }

        jetspeed.widget.PortletWindowDragMoveObject.superclass.onDragEnd.call( this, e );
        
        //dojo.debug( "PortletWindowDragMoveObject [" + this.portletWindow.widgetId + "] onDragEnd:  portletWindowNode.hasParent=" + dojo.dom.hasParent( this.domNode ) );

        var pwGhost = jetspeed.widget.pwGhost;
        
        if ( this.windowPositionStatic )
        {
            if ( pwGhost && pwGhost.col )
            {
                this.portletWindow.column = 0;
                dojo.dom.insertBefore( this.domNode, pwGhost, true );
            }
            if ( pwGhost )
                dojo.dom.removeNode( pwGhost );
            this.domNode.style.position = "static";
        }
        else if ( pwGhost ) 
        {
            dojo.dom.removeNode( pwGhost );
        }

        //jetspeed.ui.dumpPortletWindowsPerColumn();

        this.portletWindow.isDragging = false;

        if ( this.portletWindow.windowState == jetspeed.id.ACTION_NAME_MINIMIZE )
        {
            this.portletWindow._updateLastPositionInfoPositionOnly();
        }

        this.portletWindow.endDragging();
        //dojo.debug( "jetspeed.widget.PortletWindowDragMoveSource.onDragEnd" );
	}
});
