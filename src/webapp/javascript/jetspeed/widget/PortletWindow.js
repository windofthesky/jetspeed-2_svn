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

    windowPositionStatic: false,
    windowHeightToFit: false,
    windowIsColumnBound: false,
    titleMouseIn: 0,
    titleLit: false,

    portlet: null,
    jsAltInitParams: null,
    
    templateDomNodeClassName: null,
    templateContainerNodeClassName: null,

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

    setWindowTheme: function( fragment )
    {
        var windowtheme = this.getInitProperty( jetspeed.id.PORTLET_PROP_WINDOW_THEME );
        if ( ! windowtheme )
        {
            if ( this.portletWindowTheme )
                windowtheme = this.portletWindowTheme;
            else
                windowtheme = jetspeed.page.getWindowThemeDefault();
        }
        this.portletWindowTheme = windowtheme ;
        var prevCssPath = ( this.templateCssPath == null ? null : this.templateCssPath.toString() );
        this.templateCssPath = new dojo.uri.Uri( jetspeed.url.basePortalWindowThemeUrl( windowtheme ) + "/css/styles.css" );
        if ( this.portletInitialized )
        {   // load new stylesheet    // BOZO: it would be nice to check if this were necessary
            if ( prevCssPath == null || prevCssPath != this.templateCssPath.toString() )
                dojo.html.insertCssFile( this.templateCssPath, null, true );
        }
    },
    setWindowTitle: function( fragment )
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
    setWindowIcon: function( fragment )
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

    setWindowDimensions: function( fragment )
    {
        this.windowPositionStatic = this.getInitProperty( jetspeed.id.PORTLET_PROP_WINDOW_POSITION_STATIC );
        this.windowHeightToFit = this.getInitProperty( jetspeed.id.PORTLET_PROP_WINDOW_HEIGHT_TO_FIT );
        this.windowColumnSpan = this.getInitProperty( jetspeed.id.PORTLET_PROP_COLUMN_SPAN );
        if ( this.windowPositionStatic )
            this.windowIsColumnBound = true;

        this.constrainToContainer = 0;

        var portletWidth = null, portletHeight = null, portletLeft = null, portletTop = null;
        if ( this.portlet )
        {
            var portletWindowDimensions = this.portlet.getInitialWindowDimensions();
        	portletWidth = portletWindowDimensions.width;
            portletHeight = portletWindowDimensions.height;
            portletLeft = portletWindowDimensions.left;
            portletTop = portletWindowDimensions.top;
            // NOTE: portletWindowDimensions.zIndex;  - should be dealt with in the creation order
        }
        else
        {
            portletWidth = this.getInitProperty( jetspeed.id.PORTLET_PROP_WIDTH );
            portletHeight = this.getInitProperty( jetspeed.id.PORTLET_PROP_HEIGHT );
            portletLeft = this.getInitProperty( jetspeed.id.PORTLET_PROP_LEFT );
            portletTop = this.getInitProperty( jetspeed.id.PORTLET_PROP_TOP );
        }
        
        if ( portletWidth != null && portletWidth > 0 ) portletWidth = Math.floor(portletWidth) + "px";
        else portletWidth = jetspeed.prefs.windowWidth;
    
        if ( portletHeight != null && portletHeight > 0 ) portletHeight = Math.floor(portletHeight) + "px";
        else portletHeight = jetspeed.prefs.windowHeight;
            
        if ( portletLeft != null && portletLeft >= 0 ) portletLeft = Math.floor(portletLeft) + "px";
        else portletLeft = (((this.portletIndex -2) * 30 ) + 200) + "px";
    
        if ( portletTop != null && portletTop >= 0 ) portletTop = Math.floor(portletTop) + "px";
        else portletTop = (((this.portletIndex -2) * 30 ) + 170) + "px";

        if ( ! this.portletInitialized )
        {
            var source = this.getFragNodeRef( fragment );
            var dimensionsCss = "width: " + portletWidth + ( ( portletHeight != null && portletHeight.length > 0 ) ? ( "; height: " + portletHeight ) : "");
            if ( ! this.windowPositionStatic )
                dimensionsCss += "; left: " + portletLeft + "; top: " + portletTop + ";";
        
            source.style.cssText = dimensionsCss;
        }
        else
        {   // update dimensions
            this.domNode.style.position = "absolute";
            this.domNode.style.width = portletWidth;
            this.domNode.style.height = portletHeight;
            if ( ! this.windowPositionStatic )
            {
                this.domNode.style.left = portletLeft;
                this.domNode.style.top = portletTop;
            }
        }
    },

    portletMixinProperties: function( fragment )
    {
        this.setWindowTheme( fragment );
        this.setWindowTitle( fragment );
        this.setWindowIcon( fragment );

        if ( dojo.render.html.mozilla )  // dojo.render.html.ie
        {
            //this.hasShadow = "true";
            //        dojo.debug( "nWidget.domNode.cssText: " + 
            //nWidget.domNode.style = "overflow: visible;";   // so that drop shadow is displayed
        }

        this.setWindowDimensions( fragment );
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

        //dojo.debug("PortletWindow  widgetId=" + this.widgetId + "  windowtheme=" + windowtheme + "  templateCssPath=" + this.templateCssPath);
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
            var rh = null;
            var rhWidgetId = this.widgetId + "_resize";
            if ( ! this.portletInitialized )
			    rh = dojo.widget.createWidget( "jetspeed:PortletWindowResizeHandle", { targetElmId: this.widgetId, id: rhWidgetId, portletWindow: this } );
            else
                rh = dojo.widget.byId( rhWidgetId );
            if ( rh )
            {
                if ( this.windowPositionStatic && dojo.render.html.mozilla )  // dojo.render.html.ie
                    rh.domNode.style.position = "static";
                else
                    rh.domNode.style.position = "absolute";
                if ( ! this.portletInitialized )
			        this.resizeBar.appendChild( rh.domNode );
            }
		}
    },

    _createActionButtonNode: function( actionName )
    {
        if ( actionName != null )
        {
            var actionButton = document.createElement("div");
            actionButton.className = "portletWindowActionButton";
            actionButton.style.backgroundImage = "url(" + jetspeed.url.basePortalWindowThemeUrl( this.portletWindowTheme ) + "/images/desktop/" + actionName + ".gif)";
            this.actionButtons[ actionName ] = actionButton;
            this.titleBar.appendChild( actionButton );

            if ( actionName == "minimize" )
            {
                dojo.event.connect( actionButton, "onclick", this, "minimizeWindow");
                //this.minimizeAction = actionButton;
            }
            else if ( actionName == "maximize" )
            {
                dojo.event.connect( actionButton, "onclick", this, "maximizeWindow");
                //this.maximizeAction = actionButton;
            }
            else if ( actionName == "restore" )
            {
                dojo.event.connect( actionButton, "onclick", this, "restoreWindow");
                //this.restoreAction = actionButton;
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
			this.windowState="minimized";
		}

		// <img src=""> can hang IE!  better get rid of it
		if(this.iconSrc==""){
			dojo.dom.removeNode(this.titleBarIcon);
		}else{
			this.titleBarIcon.src = this.iconSrc.toString();// dojo.uri.Uri obj req. toString()
		}

		if(this.titleBarDisplay!="none"){	
			this.titleBar.style.display="";
			dojo.html.disableSelection(this.titleBar);

			this.titleBarIcon.style.display = (this.iconSrc=="" ? "none" : "");

            var windowTitleBarButtons = null;

            if ( jetspeed.prefs.windowActionButtonOrder != null )
            {
                if ( this.portlet )
                {
                    var portletActions = this.portlet.actions;
                
                    for ( var actionIdx = (jetspeed.prefs.windowActionButtonOrder.length-1) ; actionIdx >= 0 ; actionIdx-- )
                    {
                        var actionName = jetspeed.prefs.windowActionButtonOrder[ actionIdx ];
                        var includeAction = false;
                        var portletActionConfig = portletActions[ actionName ];
                        if ( portletActionConfig != null )
                        {
                            includeAction = true;
                        }
                        else
                        {
                            if ( actionName == "restore" || actionName == "menu" )
                                includeAction = true;
                        }
                        if ( includeAction )
                        {
                            this._createActionButtonNode( actionName );
                        }
                    }
                }
                else
                {
                    
                    
                }
            }
            

			//this.minimizeAction.style.display = (this.displayMinimizeAction ? "" : "none");
			//this.maximizeAction.style.display= 
			//	(this.displayMaximizeAction && this.windowState!="maximized" ? "" : "none");
			//this.restoreAction.style.display= 
			//	(this.displayMaximizeAction && this.windowState=="maximized" ? "" : "none");
			//this.closeAction.style.display= (this.displayCloseAction ? "" : "none");



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

    portletInitDimensions: function()
    {
        if ( this.windowPositionStatic )
        {            
            this.domNode.style.position = "static";  // can't be done earlier
            this.domNode.style.left = "auto";                
            this.domNode.style.top = "auto";
        }

        if ( this.windowPositionStatic && this.windowHeightToFit )
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

        if ( ! this.templateDomNodeClassName )
            this.templateDomNodeClassName = this.domNode.className;
        var domNodeClassName = this.templateDomNodeClassName;
        if ( this.portletWindowTheme )
        {
            domNodeClassName = this.portletWindowTheme + ( domNodeClassName ? ( " " + domNodeClassName ) : "" );
        }
        this.domNode.className = jetspeed.id.PORTLET_STYLE_CLASS + ( domNodeClassName ? ( " " + domNodeClassName ) : "" );
        
        if ( this.containerNode )
        {
            if ( ! this.templateContainerNodeClassName )
                this.templateContainerNodeClassName = this.containerNode.className;
            var containerNodeClassName = this.templateContainerNodeClassName;
            if ( this.portletWindowTheme )
            {
                containerNodeClassName = this.portletWindowTheme + ( containerNodeClassName ? ( " " + containerNodeClassName ) : "" );
            }
            this.containerNode.className = jetspeed.id.PORTLET_STYLE_CLASS + ( containerNodeClassName ? ( " " + containerNodeClassName ) : "" );

            if ( this.windowPositionStatic && this.windowHeightToFit )
            {
                this.containerNode.style.overflow = "visible";
                this.containerNode.style.height = "";
            }
            else
                this.containerNode.style.overflow = "auto";

            if ( this.windowPositionStatic )
            {
                //this.containerNode.style.width = "";   // commented-out with change to ie width 100% in resizeTo
                //dojo.debug( "portletInitDimensions containerNode-width: " + dojo.html.getMarginBox( this.containerNode ).width + " domNode-width: " + this.domNode.style.width );
            }
        }

        //dojo.debug( "PortletWindow.portletInitDimensions [" + this.portlet.entityId + "] setting domNode.className=" + this.domNode.className + " containerNode.className=" + this.containerNode.className );
        var domNodeMarginBox = dojo.html.getMarginBox( this.domNode ) ;
        this.width = domNodeMarginBox.width;
        this.height = domNodeMarginBox.height;
        this.resetLostHeightWidth();
        
        this.resizeTo( this.width, this.height );

        this.titleDim( true );
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

        this.createTitleBarContextMenu();
        
        if ( jetspeed.debug.createWindow )
            dojo.debug( "createdWindow [" + ( this.portlet ? this.portlet.entityId : this.widgetId ) + ( this.portlet ? (" / " + this.widgetId) : "" ) + "]" + " width=" + this.domNode.style.width + " height=" + this.domNode.style.height + " left=" + this.domNode.style.left + " top=" + this.domNode.style.top ) ;

        this.portletInitialized = true;

        var initWindowState = this.getInitProperty( jetspeed.id.PORTLET_PROP_WINDOW_STATE );
        if ( initWindowState == "minimized" )
            this.minimizeWindow();
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
        //dojo.debug( "minimize [" + this.widgetId + "] before this[w=" + this.width + " y=" + this.height + "] container[w=" + dojo.html.getMarginBox( this.containerNode ).width + " h=" + dojo.html.getMarginBox( this.containerNode ).height + "] domNode[w=" + dojo.html.getMarginBox( this.domNode ).width + " h=" + dojo.html.getMarginBox( this.domNode ).height + "]" );

        var tbiWidget = dojo.widget.byId( this.widgetId + "_tbi" );
        
        if ( this.windowState != "maximized" )
            this._setPreviousDimensions();

        this.containerNode.style.display = "none";
        this.resizeBar.style.display = "none";
        dojo.html.setContentBox( this.domNode, { height: dojo.html.getMarginBox( this.titleBar ).height } );

        //if ( tbiWidget && tbiWidget.domNode )
        //    dojo.fx.html.implode( this.domNode, tbiWidget.domNode, 340 ) ; // began as 300 in ff
        //else
        //    this.hide();
    
        this.windowState = "minimized";
        
        this.quickTitleLightAdjust();
    },
	restoreWindow: function(evt)
    {
        //dojo.debug( "restore [" + this.widgetId + "] begin container[w=" + dojo.html.getMarginBox( this.containerNode ).width + " h=" + dojo.html.getMarginBox( this.containerNode ).height + "] domNode[w=" + dojo.html.getMarginBox( this.domNode ).width + " h=" + dojo.html.getMarginBox( this.domNode ).height + "]" );
        if ( this.previous.columnIndex != null )
        {
            var columnElmt = jetspeed.page.columns[ this.previous.columnIndex ];
            if ( this.previous.previousSibling )
                dojo.dom.insertAfter( this.domNode, this.previous.previousSibling );
            else if ( this.previous.nextSibling )
                dojo.dom.insertBefore( this.domNode, this.previous.nextSibling );
            else
                columnElmt.domNode.appendChild( this.domNode );

            this.domNode.style.position = "static";
        }

		this.containerNode.style.display = "";
        this.resizeBar.style.display = "";

        for(var attr in this.previous){
			this.domNode.style[attr]=this.previous[attr];
		}
        
		this.resizeTo( this.previous.width, this.previous.height, true );
		this.previous = null;

		this.restoreAction.style.display = "none";
		this.maximizeAction.style.display = this.displayMaximizeAction ? "" : "none";

		this.windowState = "normal";

        this.quickTitleLightAdjust();

        //dojo.debug( "restore [" + this.widgetId + "] end container[w=" + dojo.html.getMarginBox( this.containerNode ).width + " h=" + dojo.html.getMarginBox( this.containerNode ).height + "] domNode[w=" + dojo.html.getMarginBox( this.domNode ).width + " h=" + dojo.html.getMarginBox( this.domNode ).height + "]" );
	},
    maximizeWindow: function( evt )
    {
        this._setPreviousDimensions();
        
        var jetspeedDesktop = document.getElementById( jetspeed.id.DESKTOP );
        if ( this.windowPositionStatic )
        {
            var sibling = dojo.dom.getPreviousSiblingElement( this.domNode );
            if ( sibling )
                this.previous.previousSibling = sibling;
            else
            {
                sibling = dojo.dom.getNextSiblingElement( this.domNode );
                if ( sibling )
                    this.previous.nextSibling = sibling;
            }
            
            this.previous.columnIndex = this.getPageColumnIndex();
            
            this.domNode.style.position = "absolute";
            jetspeedDesktop.appendChild( this.domNode );
        }
        jetspeed.widget.PortletWindow.superclass.bringToTop.call( this, evt );
        
        var yPos = dojo.html.getAbsolutePosition( jetspeedDesktop ).y;
		this.domNode.style.left =
			dojo.html.getPixelValue( jetspeedDesktop, "padding-left", true) + "px";
		this.domNode.style.top = yPos;

        this.resizeTo(
            dojo.html.getContentBox( jetspeedDesktop ).width,
            dojo.html.getBorderBox( document.body ).height - yPos
		);

		this.windowState ="maximized";
	},
    bringToTop: function( evt )
    {
        var beforeZIndex = this.domNode.style.zIndex;
        jetspeed.widget.PortletWindow.superclass.bringToTop.call( this, evt );
        if ( ! this.windowPositionStatic && this.isPortletWindowInitialized() && this.portlet )
        {
            this.portlet.submitChangedWindowState();
            //dojo.debug( "bringToTop [" + this.portlet.entityId + "] zIndex   before=" + beforeZIndex + " after=" + this.domNode.style.zIndex );
        }
    },

    resizeTo: function( w, h, force )
    {
        //dojo.debug( "resizeTo [" + this.widgetId + "] begin w=" + w + " h=" + h + " container[w=" + dojo.html.getMarginBox( this.containerNode ).width + " h=" + dojo.html.getMarginBox( this.containerNode ).height + "] domNode[w=" + dojo.html.getMarginBox( this.domNode ).width + " h=" + dojo.html.getMarginBox( this.domNode ).height + "]" );

		if(w==this.width && h == this.height && ! force){
			return;
		}
		this.width=w;
		this.height=h;

		// IE won't let you decrease the width of the domnode unless you decrease the
		// width of the inner nodes first (???)

		dojo.lang.forEach(
			[this.titleBar, this.resizeBar, this.containerNode],
			function(node){ dojo.html.setMarginBox( node, { width: w - this.lostWidth } ); }, this
		);

        //dojo.debug( "resizeTo [" + this.widgetId + "] before-adjust w=" + w + " h=" + h + " container[w=" + dojo.html.getMarginBox( this.containerNode ).width + " h=" + dojo.html.getMarginBox( this.containerNode ).height + "] domNode[w=" + dojo.html.getMarginBox( this.domNode ).width + " h=" + dojo.html.getMarginBox( this.domNode ).height + "]" );

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
                    this.containerNode.style.width = "100%";
                else
                    this.containerNode.style.width = "";  // I only know that ff 1.5 likes it blanked and ie6 likes it 100%
                //jetspeed.ui.dumpColumnWidths();
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
            this.shadow.size( width, height );
        }
		this.onResized();

        //dojo.debug( "resizeTo [" + this.widgetId + "] end w=" + w + " h=" + h + " container[w=" + dojo.html.getMarginBox( this.containerNode ).width + " h=" + dojo.html.getMarginBox( this.containerNode ).height + " desired-h=" + (h-this.lostHeight) + "] domNode[w=" + dojo.html.getMarginBox( this.domNode ).width + " h=" + dojo.html.getMarginBox( this.domNode ).height + "]" );
	},

    _setPreviousDimensions: function()
    {
        this.previous =
        {
			width: this.width,
			height: this.height,
			left: this.domNode.style.left,
			top: this.domNode.style.top,
			bottom: this.domNode.style.bottom,
			right: this.domNode.style.right
        };
    },
    _updatePositionPreviousDimensions: function()
    {
        if ( this.previous != null )
        {
            this.previous.left = this.domNode.style.left;
            this.previous.top = this.domNode.style.top;
        }
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

    makeFreeFloating: function( positioningNode )
    {
        if ( ! positioningNode )
            positioningNode = this.domNode;
        var winAbsPos = dojo.html.getAbsolutePosition( positioningNode, true );
        var winMarginTop = dojo.html.getPixelValue( positioningNode, "margin-top", true );
        var winMarginLeft = dojo.html.getPixelValue( positioningNode, "margin-left", true );
        var domNodeMarginBox = dojo.html.getMarginBox( this.domNode ) ;
        var winWidth = domNodeMarginBox.width ;
        var winHeight = domNodeMarginBox.height ;

        this.domNode.style.position = "absolute";
        
        this.domNode.style.left = winAbsPos.x - winMarginTop;
        this.domNode.style.top = winAbsPos.y - winMarginLeft;

        this.windowPositionStatic = false;
        
        this.resizeTo( winWidth, winHeight, true );
        
        var addToElmt = document.getElementById( jetspeed.id.DESKTOP );
        addToElmt.appendChild( this.domNode );

        if ( this.portlet )
            this.portlet.submitChangedWindowState();
    },

    makeHeightToFit: function( suppressSubmitChange )
    {
        if ( this.windowPositionStatic )
        {
            var domNodePrevMarginBox = dojo.html.getMarginBox( this.domNode ) ;
            this.windowHeightToFit = true;
            this.domNode.style.overflow = "visible";
            this.domNode.style.height = "";
            if ( this.containerNode )
            {
                this.containerNode.style.overflow = "visible";
                this.containerNode.style.height = "";
            }
            var domNodeMarginBox = dojo.html.getMarginBox( this.domNode ) ;
            this.width = domNodeMarginBox.width;
            this.height = domNodeMarginBox.height;
            this.resetLostHeightWidth();

            dojo.debug( "makeHeightToFit [" + this.widgetId + "] prev w=" + domNodePrevMarginBox.width + " h=" + domNodePrevMarginBox.height + "  new w=" + domNodeMarginBox.width + " h=" + domNodeMarginBox.height );
        
            this.resizeTo( this.width, this.height, true );

            if ( ! suppressSubmitChange && this.portlet )
                this.portlet.submitChangedWindowState();
        }
    },
    makeHeightVariable: function( suppressSubmitChange )
    {
        var domNodePrevMarginBox = dojo.html.getMarginBox( this.domNode ) ;
        this.windowHeightToFit = false;
        this.domNode.style.overflow = "hidden";
        if ( this.containerNode )
        {
            this.containerNode.style.overflow = "auto";
        }
        var domNodeMarginBox = dojo.html.getMarginBox( this.domNode ) ;
        this.width = domNodeMarginBox.width;
        this.height = domNodeMarginBox.height;
        this.resetLostHeightWidth();

        dojo.debug( "makeHeightVariable [" + this.widgetId + "] prev w=" + domNodePrevMarginBox.width + " h=" + domNodePrevMarginBox.height + "  new w=" + domNodeMarginBox.width + " h=" + domNodeMarginBox.height );
        
        this.resizeTo( this.width, this.height, true );

        if ( ! suppressSubmitChange && this.portlet )
            this.portlet.submitChangedWindowState();
    },

    createTitleBarContextMenu: function()
    {
        var portletWindow = this;
        var titleBarContextMenu = dojo.widget.createWidget( "PopupMenu2", { id: this.widgetId + "_ctxmenu", targetNodeIds: [ this.titleBar.id ], contextMenuForWindow: false }, null );
        var dumpPosMenuItem = dojo.widget.createWidget( "MenuItem2", { caption: "Dump Position"} );
        var makeFreeFloating = dojo.widget.createWidget( "MenuItem2", { caption: "Make Free Floating"} );
        var makeVariableHeight = dojo.widget.createWidget( "MenuItem2", { caption: "Height - Variable"} );
        var makeFitContentHeight = dojo.widget.createWidget( "MenuItem2", { caption: "Height - Fit Content"} );
        
        dojo.event.connect( dumpPosMenuItem, "onClick", function(e) { portletWindow.dumpPostionInfo(); } );
        dojo.event.connect( makeFreeFloating, "onClick", function(e) { portletWindow.makeFreeFloating(); } );
        dojo.event.connect( makeVariableHeight, "onClick", function(e) { portletWindow.makeHeightVariable( false ); } );
        dojo.event.connect( makeFitContentHeight, "onClick", function(e) { portletWindow.makeHeightToFit( false ); } );

        titleBarContextMenu.addChild( dumpPosMenuItem );
        titleBarContextMenu.addChild( makeFreeFloating );
        titleBarContextMenu.addChild( makeVariableHeight );
        titleBarContextMenu.addChild( makeFitContentHeight );

        document.body.appendChild( titleBarContextMenu.domNode );
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
        if ( this.portlet )
            this.portlet.submitChangedWindowState();
    },
    endDragging: function()
    {
        if ( this.portlet )
            this.portlet.submitChangedWindowState();
    },

    _getTitleLightNodes: function()
    {
        var shouldBeDisplayed = new dojo.collections.ArrayList();
        this._titleButtonInclude( this.displayMinimizeAction && this.windowState != "minimized", true, this.minimizeAction, shouldBeDisplayed );
        this._titleButtonInclude( this.displayMaximizeAction && this.windowState != "maximized", true, this.maximizeAction, shouldBeDisplayed );
        this._titleButtonInclude( this.displayRestoreAction && ( this.windowState == "minimized" || this.windowState == "maximized" ) , true, this.restoreAction, shouldBeDisplayed );
        this._titleButtonInclude( this.displayCloseAction, true, this.closeAction, shouldBeDisplayed );
        return shouldBeDisplayed;
    },
    _titleButtonInclude: function(condition, requiredResult, button, includedArrayList)
    {
        if ( button == null ) return includedArrayList ;
        if ( dojo.lang.isFunction( condition ) )
        {
            if ( condition.call( this ) == requiredResult )
                includedArrayList.add( button );
        }
        else if ( condition == requiredResult )
        {
            includedArrayList.add( button );
        }
        return includedArrayList;
    },

    quickTitleLightAdjust: function()
    {
        var allNodes = [ this.restoreAction, this.maximizeAction, this.minimizeAction, this.closeAction ] ;
        var shouldBeDisplayed = this._getTitleLightNodes();

        var hideNodes = dojo.collections.Set.difference( allNodes, shouldBeDisplayed ).toArray();
        for ( var i = 0 ; i < hideNodes.length ; i++ )
        {
            if ( hideNodes[i] != null )
            {
                hideNodes[i].style.visibility = "hidden";
                if ( hideNodes[i] == this.restoreAction )
                    this.restoreAction.style.display = "none";
            }
        }
        shouldBeDisplayed = shouldBeDisplayed.toArray();
        for ( var i = 0 ; i < shouldBeDisplayed.length ; i++ )
        {
            if ( shouldBeDisplayed[i] == this.restoreAction )
                shouldBeDisplayed[i].style.display = "block" ;
            shouldBeDisplayed[i].style.visibility == "";
        }
    },

    titleLight: function()
    {
        var mightBeEnlightened = this._getTitleLightNodes().toArray();
        var toBeEnlightened = [] ;
        for ( var i = 0 ; i < mightBeEnlightened.length ; i++ )
        {
            var btn = mightBeEnlightened[i];
            if ( btn == this.restoreAction )
                btn.style.display = "block" ;
            if ( btn.style.visibility == "hidden" )
                toBeEnlightened.push( btn );
        }
        for ( var i = 0 ; i < toBeEnlightened.length ; i++ )
        {
            toBeEnlightened[i].style.visibility = "" ;
        }
        //jetspeed.ui.fadeIn( toBeEnlightened, 325, "" );
        this.titleLit = true ;
    },
    titleDim: function( immediateForce )
    {
        var mightBeExtinguished = [ this.restoreAction, this.maximizeAction, this.minimizeAction, this.closeAction ] ;
        var toBeExtinguished = [] ;
        for ( var i = 0 ; i < mightBeExtinguished.length ; i++ )
        {
            var btn = mightBeExtinguished[i];
            if ( btn != null )
            {
                if ( immediateForce )
                    btn.style.visibility = "hidden" ;
                else if ( btn.style.visibility != "hidden" )
                    toBeExtinguished.push( btn );
            }
        }
        for ( var i = 0 ; i < toBeExtinguished.length ; i++ )
        {
            toBeExtinguished[i].style.visibility = "hidden" ;
        }
        if ( this.restoreAction != null )
        {
            this.restoreAction.style.display = "none";
            //jetspeed.ui.fadeOut( toBeExtinguished, 280, [ this.restoreAction ] );   // nodes in 3rd arg will be set to display=none
        }
        this.titleLit = false ;
    },
    titleMouseOver: function( evt )
    {
        var self = this ;
        this.titleMouseIn = 1 ;   // was ++
        window.setTimeout( function() { if ( self.titleMouseIn > 0 ) { self.titleLight(); self.titleMouseIn = 0; } }, 270 ) ;
            // NOTE: setup in template HtmlFloatingPane.html: dojoAttachEvent="onMouseOver:titleMouseOver;onMouseOut:titleMouseOut"
    },
    titleMouseOut: function( evt )
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
        var ppR = null;
        if ( this.portlet )
        {
            ppR = this.portlet.preParseAnnotateHtml( initialHtmlStr, url );
        }
        else
        {
            ppR = jetspeed.ui.preParseAnnotateHtml( initialHtmlStr, url );
        }
        //this.executeScripts = true;

        var setContentObj = { titles: [], scripts: ppR.preParsedScripts, linkStyles: [], styles: [], remoteScripts: ppR.preParsedRemoteScripts, xml: ppR.preParsedContent, url: url, requires: [] };

        this.setContent( setContentObj );

        if ( setContentObj.scripts && setContentObj.scripts.length > 0 )
        {   // do inline scripts  - taken from dojo ContentPane.js _executeScripts
		    var repl = null;
		    for( var i = 0; i < setContentObj.scripts.length; i++ )
            {
			    // not sure why comment and carraige return clean is needed
			    // but better safe than sorry so we keep it, Fredrik
			    // Clean up content: remove inline script  comments
                repl = new RegExp('//.*?$', 'gm');
			    setContentObj.scripts[i] = setContentObj.scripts[i].replace(repl, '\n');
	

                // BOZO: despite the comment above from the dojo code, we cannot do this (carriage returns are syntatically required in javascript)
			    // Clean up content: remove carraige returns
			    //repl = new RegExp('[\n\r]', 'g');
			    //setContentObj.scripts[i] = setContentObj.scripts[i].replace(repl, ' ');
            
			    // Execute commands
                
                if ( jetspeed.debug.setPortletContent )
                    dojo.debug( "setPortletContent [" + ( this.portlet ? this.portlet.entityId : this.widgetId ) + "] script: " + setContentObj.scripts[i] );
                
			    eval( setContentObj.scripts[i] );
		    }
        }
        else
        {
            if ( jetspeed.debug.setPortletContent )
                dojo.debug( "setPortletContent [" + ( this.portlet ? this.portlet.entityId : this.widgetId ) + "]" );
        }
        
        this._executeScripts( { scripts: [], remoteScripts: setContentObj.remoteScripts } );

        if ( this.portlet )
            this.portlet.postParseAnnotateHtml( this.containerNode );
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

        if ( this.portletWindow.windowState == "minimized" )
        {
            this.portletWindow._updatePositionPreviousDimensions();
        }

        this.portletWindow.endDragging();
        //dojo.debug( "jetspeed.widget.PortletWindowDragMoveSource.onDragEnd" );
	}
});

/* start - new layout management (not yet used) */
jetspeed.ui.spacerwindows = {};
jetspeed.ui.getSpacerWindow = function( associatedWidgetId, instanceNumber )
{
    var spacerWin = jetspeed.ui.spacerwindows[ jetspeed.ui.getSpacerDomNodeId( associatedWidgetId, instanceNumber ) ];
    return spacerWin;
};
jetspeed.ui.getSpacerWindowFromNode = function( spacerWindowDomNode )
{
    if ( ! spacerWindowDomNode ) return null;
    var domNodeId = spacerWindowDomNode.id;
    if ( ! domNodeId ) return null;
    return jetspeed.ui.spacerwindows[ domNodeId ];
}
jetspeed.ui.getSpacerWindows = function( associatedWidgetId, minNeeded )
{
    if ( ! minNeeded )
        minNeeded = null;
    var spacerWindows = [];
    var instanceNum = 1;
    var spacerWin = null;
    while ( instanceNum == 1 || spacerWin != null )
    {
        spacerWin = jetspeed.ui.spacerwindows[ jetspeed.ui.getSpacerDomNodeId( associatedWidgetId, instanceNum ) ];
        if ( spacerWin != null )
        {
            spacerWindows.push( spacerWin );
        }
        instanceNum++;
    }
    if ( minNeeded && minNeeded > 0 && spacerWindows.length < minNeeded )
    {
        instanceNum = spacerWindows.length + 1;
        while ( instanceNum <= minNeeded )
        {
            spacerWin = new jetspeed.ui.SpacerWindow( associatedWidgetId, instanceNum );
            jetspeed.ui.spacerwindows[ spacerWin.domNodeId ] = spacerWin;
            spacerWindows.push( spacerWin );
        }
    }
    return spacerWindows;
};
jetspeed.ui.getSpacerDomNodeIdPrefix = function( associatedWidgetId )
{
    return associatedWidgetId + "_spacer_";
};
jetspeed.ui.getSpacerDomNodeId = function( associatedWidgetId, instanceNumber )
{
    return associatedWidgetId + "_spacer_" + instanceNumber;
};
jetspeed.ui.SpacerWindow = function( associatedWidgetId, instanceNumber )
{   // use getSpacerWindows for create - do not call ctor directly
    this.windowWidgetId = associatedWidgetId;
    this.instanceNumber = instanceNumber;
    this.domNodeIdPrefix = jetspeed.ui.getSpacerDomNodeIdPrefix( associatedWidgetId );
    this.domNodeId = jetspeed.ui.getSpacerDomNodeId( associatedWidgetId, instanceNumber );
};
dojo.lang.extend( jetspeed.ui.SpacerWindow, {
    sizeDomNode: function( height )
    {
        var spacerDomNode = this.getDomNode();
        if ( spacerDomNode == null )
        {
            spacerDomNode = document.createElement( "div" );
            dojo.html.setClass( spacerDomNode, jetspeed.id.PORTLET_WINDOW_GHOST_STYLE_CLASS ) ;
            spacerDomNode.id = this.domNodeId;
            this.domNode = spacerDomNode;
        }
        spacerDomNode.style.height = height + "px";
    },
    getGroupNodes: function()
    {
        return this._getSiblings( true );
    },
    getGroupSiblings: function()
    {
        return this._getSiblings( false );
    },
    _getGroupSiblings: function( /* boolean */ includeSelf )
    {
        if ( ! jetspeed.page.columns ) return null;
        var siblings = [];
        for ( var i = 0 ; i < jetspeed.page.columns.length ; i++ )
        {
            var columnElmt = jetspeed.page.columns[i];
            var spacerChildren = jetspeed.ui.getPortletWindowChildren( columnElmt.domNode, null, true, true );
            if ( spacerChildren == null ) continue;
            for ( var j = 0 ; j < spacerChildren.length ; j++ )
            {
                var tId = spacerChildren[j].id;
                if ( tId != null && tId.indexOf( this.domNodeIdPrefix ) == 0 )
                {
                    if ( includeSelf || this.domNodeId != tId )
                        siblings.push( spacerChildren[j] );
                }
            }
        }
        return spacerChildren;
    },
    getPortletWindow: function()
    {
        
    },
    getDomNode: function()
    {
        if ( this.domNode != null ) return this.domNode ;
        this.domNode = document.getElementById( this.domNodeId );
        return this.domNode;
    }
});
/* end - new layout management (not yet used) */
