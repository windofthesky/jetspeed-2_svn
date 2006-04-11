
dojo.provide("jetspeed.ui.widget.PortletWindow");

dojo.require("jetspeed.desktop.core");
dojo.require("dojo.widget.*");
dojo.require("dojo.widget.FloatingPane");

jetspeed.ui.widget.PortletWindow = function()
{
    dojo.widget.html.FloatingPane.call( this );
    this.widgetType = "PortletWindow";
    this.windowTiling = jetspeed.prefs.windowTiling;
    this.portletInitialized = false;
};

dojo.inherits(jetspeed.ui.widget.PortletWindow, dojo.widget.html.FloatingPane);

dojo.lang.extend(jetspeed.ui.widget.PortletWindow, {
    title: "Unknown Portlet",
    constrainToContainer: ( jetspeed.prefs.windowTiling ? 0 : 1 ),
    contentWrapper: "layout",
    displayCloseAction: true,
    displayMinimizeAction: true,
    displayMaximizeAction: true,
    taskBarId: jetspeed.id.TASKBAR,
    hasShadow: false,
    nextIndex: 1,

    windowTiling: false,
    titleMouseIn: 0,
    titleLit: false,

    // dojo.widget.Widget create protocol
    postMixInProperties: function( args, fragment, parentComp )
    {
        jetspeed.ui.widget.PortletWindow.superclass.postMixInProperties.call( this );

        var portletObj = this.portlet;
        if ( ! this.widgetId )
        {
            if ( portletObj )
            {
                this.widgetId = "portletWindow_" + portletObj.entityId ;
            }
            else
            {
                this.widgetId = "portletWindow_" + this._getNextIndex();
            }
        }
        this._incrementNextIndex();

        if ( portletObj.getProperty( "forceAbsolute" ) )
            this.windowTiling = 0;

        var windowid = null;
        var windowtheme = null;
        var windowicon = null;
        if ( ! portletObj )
        {
            dojo.raise( "PortletWindow.postMixInProperties cannot be initialized with a null portlet object" );
        }
        else
        {
            this.title = portletObj.title;
        
            windowid = portletObj.getProperty("window-id");
            windowtheme = portletObj.getProperty("window-theme");
            windowicon = portletObj.getProperty("window-icon");
        }
        
        if ( ! windowtheme )
        {
            if ( jetspeed.debugPortletWindowThemes )
            {
                windowtheme = jetspeed.debugPortletWindowThemes[Math.floor(Math.random()*jetspeed.debugPortletWindowThemes.length)];
            }
        }
        if ( windowtheme )
        {
            this.portletWindowTheme = windowtheme ;
            this.templateCssPath = new dojo.uri.Uri(jetspeed.url.basePortalDesktopUrl(), "jetspeed/javascript/desktop/windowthemes/" + windowtheme + "/css/styles.css");
        }
        this.templatePath = jetspeed.ui.getDefaultFloatingPaneTemplate();
        
        if ( ! windowicon )
        {
            if ( jetspeed.debugPortletWindowIcons )
            {
                windowicon = jetspeed.debugPortletWindowIcons[Math.floor(Math.random()*jetspeed.debugPortletWindowIcons.length)];
            }
        }
        if ( windowicon )
            this.iconSrc =  new dojo.uri.Uri(jetspeed.url.basePortalDesktopUrl(), "jetspeed/javascript/desktop/windowicons/" + windowicon ) ;
        else
            this.iconSrc =  new dojo.uri.Uri(jetspeed.url.basePortalDesktopUrl(), "jetspeed/javascript/desktop/windowicons/document.gif" ) ;
    
        if ( dojo.render.html.mozilla )  // dojo.render.html.ie
        {
            //this.hasShadow = "true";
            //        dojo.debug( "nWidget.domNode.cssText: " + 
            //nWidget.domNode.style = "overflow: visible;";   // so that drop shadow is displayed
        }

        var portletWindowState = portletObj.getLastSavedWindowState();
        var portletWidth = portletWindowState.width;
        var portletHeight = portletWindowState.height;
        var portletLeft = portletWindowState.left;
        var portletTop = portletWindowState.top;
        // NOTE: portletWindowState.zIndex;  - should be dealt with in the creation order

        if ( portletWidth != null && portletWidth > 0 ) portletWidth = Math.floor(portletWidth) + "px";
        else portletWidth = "280px";
    
        if ( portletHeight != null && portletHeight > 0 ) portletHeight = Math.floor(portletHeight) + "px";
        else portletHeight = "200px";
            
        if ( portletLeft != null && portletLeft >= 0 ) portletLeft = Math.floor(portletLeft) + "px";
        else portletLeft = (((this._getNextIndex() -2) * 30 ) + 200) + "px";
    
        if ( portletTop != null && portletTop >= 0 ) portletTop = Math.floor(portletTop) + "px";
        else portletTop = (((this._getNextIndex() -2) * 30 ) + 170) + "px";

        var source = this.getFragNodeRef( fragment );
        var dimensionsCss = "width: " + portletWidth + "; height: " + portletHeight;
        if ( ! this.windowTiling )
            dimensionsCss += "; left: " + portletLeft + "; top: " + portletTop + ";";
        
        source.style.cssText = dimensionsCss;
        
        //dojo.debug("PortletWindow  widgetId=" + this.widgetId + "  windowtheme=" + windowtheme + "  templateCssPath=" + this.templateCssPath);
    },

    _incrementNextIndex: function()
    {
        var nextI = jetspeed.ui.widget.PortletWindow.prototype.nextIndex;
        if ( ! nextI )
            jetspeed.ui.widget.PortletWindow.prototype.nextIndex = 1;
        jetspeed.ui.widget.PortletWindow.prototype.nextIndex++;
        return nextI;
    },
    _getNextIndex: function()
    {
        return jetspeed.ui.widget.PortletWindow.prototype.nextIndex;
    },

    // dojo.widget.Widget create->buildRendering protocol
    fillInTemplate: function( args, frag )
    {
        var isResizable = this.resizable;
        if ( isResizable )
        {
			this.resizeBar.style.display = "";
			var rh = dojo.widget.createWidget( "ResizeHandle", { targetElmId: this.widgetId, id: this.widgetId+"_resize" } );
            if ( this.windowTiling && dojo.render.html.mozilla )  // dojo.render.html.ie
                rh.domNode.style.position = "static";
			this.resizeBar.appendChild( rh.domNode );
		}

        jetspeed.ui.widget.PortletWindow.superclass.fillInTemplate.call( this, args, frag );
    },

    // dojo.widget.Widget create protocol
    postCreate: function( args, fragment, parentComp )
    {
        jetspeed.ui.widget.PortletWindow.superclass.postCreate.call( this );

        this.dragSource = new jetspeed.ui.widget.PortletWindowDragMoveSource( this );
        if ( this.constrainToContainer )
        {
            this.dragSource.constrainTo();
        }
        this.dragSource.setDragHandle( this.titleBar );
        
        this.domNode.id = this.widgetId;  // BOZO: must set the id here - it gets defensively cleared by dojo
        
        var domNodeClassName = this.domNode.className;
        if ( this.portletWindowTheme )
        {
            domNodeClassName = this.portletWindowTheme + ( domNodeClassName ? ( " " + domNodeClassName ) : "" );
        }
        this.domNode.className = jetspeed.id.PORTLET_STYLE_CLASS + ( domNodeClassName ? ( " " + domNodeClassName ) : "" );


        if ( this.containerNode )
        {
            var containerNodeClassName = this.containerNode.className;
            if ( this.portletWindowTheme )
            {
                containerNodeClassName = this.portletWindowTheme + ( containerNodeClassName ? ( " " + containerNodeClassName ) : "" );
            }
            this.containerNode.className = jetspeed.id.PORTLET_STYLE_CLASS + ( containerNodeClassName ? ( " " + containerNodeClassName ) : "" );
        }
        //dojo.debug( "PortletWindow.postCreate [" + this.portlet.entityId + "] setting domNode.className=" + this.domNode.className + " containerNode.className=" + this.containerNode.className );
        
        if ( jetspeed.debug.createWindow )
            dojo.debug( "createdWindow [" + this.portlet.entityId + "]" + " width=" + this.domNode.style.width + " height=" + this.domNode.style.height + " left=" + this.domNode.style.left + " top=" + this.domNode.style.top ) ;
    
        this.titleDim( true );

        this.portletInitialized = true;
    },

    isPortletWindowInitialized: function()
    {
        return this.portletInitialized;
    },

    _titleButtonInclude: function(condition, requiredResult, button, included)
    {
        if ( button == null ) return included ;
        if (dojo.lang.isFunction(condition))
        {
            if (condition.call(this) == requiredResult)
                included.push(button);
        }
        else if ( condition == requiredResult )
        {
            included.push(button);
        }
        return included;
    },
    minimizeWindow: function(evt)
    {
        var tbiWidget = dojo.widget.byId(this.widgetId + "_tbi");

        if ( tbiWidget && tbiWidget.domNode )
            dojo.fx.html.implode( this.domNode, tbiWidget.domNode, 340 ) ; // began as 300 in ff
        else
            this.hide();
    
        this.windowState = "minimized";
    },
    bringToTop: function(evt)
    {
        var beforeZIndex = this.domNode.style.zIndex;
        jetspeed.ui.widget.PortletWindow.superclass.bringToTop.call( this, evt );
        if ( this.isPortletWindowInitialized() )
        {
            this.portlet.submitChangedWindowState();
            //dojo.debug( "bringToTop [" + this.portlet.entityId + "] zIndex   before=" + beforeZIndex + " after=" + this.domNode.style.zIndex );
        }
    },

    closeWindow: function()
    {
        jetspeed.ui.widget.PortletWindow.superclass.closeWindow.call( this );
        var resizeWidget = this.getResizeHandleWidget();
        if ( resizeWidget )
            resizeWidget.destroy();
    },
    getResizeHandleWidget: function()
    {
        return dojo.widget.byId( this.widgetId + "_resize" );   // BOZO:DOJO: bad way of obtaining this reference
    },
    onResized: function()
    {
        jetspeed.ui.widget.PortletWindow.superclass.onResized.call( this );
        
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
        dojo.event.disconnect(document.body, "onmouseup", this, "endSizing");
        this.windowIsSizing = false;
        this.portlet.submitChangedWindowState();
    },
    titleLight: function()
    {
        var mightBeEnlightened = [] ;
        this._titleButtonInclude(this.displayMinimizeAction, true, this.minimizeAction, mightBeEnlightened);
        this._titleButtonInclude(this.displayMaximizeAction, true, this.maximizeAction, mightBeEnlightened);
        this._titleButtonInclude(this.displayRestoreAction, true, this.restoreAction, mightBeEnlightened);
        this._titleButtonInclude(this.displayCloseAction, true, this.closeAction, mightBeEnlightened);
        var toBeEnlightened = [] ;
        for ( var i = 0 ; i < mightBeEnlightened.length ; i++ )
        {
            var btn = mightBeEnlightened[i];
            if (btn.style.visibility == "hidden")
                toBeEnlightened.push(btn);
        }
        jetspeed.ui.fadeIn(toBeEnlightened, 325, "");
        this.titleLit = true ;
    },
    titleDim: function(immediateForce)
    {
        var mightBeExtinguished = [ this.restoreAction, this.maximizeAction, this.minimizeAction, this.closeAction ] ;
        var toBeExtinguished = [] ;
        for ( var i = 0 ; i < mightBeExtinguished.length ; i++ )
        {
            var btn = mightBeExtinguished[i];
            if (immediateForce)
                btn.style.visibility = "hidden" ;
            else if (btn.style.display != "hidden")
                toBeExtinguished.push(btn);
        }
        jetspeed.ui.fadeOut(toBeExtinguished, 280);
        this.titleLit = false ;
    },
    titleMouseOver: function(evt)
    {
        var self = this ;
        this.titleMouseIn = 1 ;   // was ++
        window.setTimeout( function() { if ( self.titleMouseIn > 0 ) { self.titleLight(); self.titleMouseIn = 0; } }, 270 ) ;
            // NOTE: setup in template HtmlFloatingPane.html: dojoAttachEvent="onMouseOver:titleMouseOver;onMouseOut:titleMouseOut"
    },
    titleMouseOut: function(evt)
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
        cWinState.zIndex = this.domNode.style.zIndex;
        return cWinState;
    },
    getCurrentWindowState: function()
    {
        if ( ! this.domNode ) return null;
        var cWinState = this.getCurrentVolatileWindowState();
        cWinState.width = this.domNode.style.width;
        cWinState.height = this.domNode.style.height;
        cWinState.left = this.domNode.style.left;
        cWinState.top = this.domNode.style.top;
        return cWinState;
    },
    setPortletContent: function( html, url )
    {
        var initialHtmlStr = html.toString();
        
        if ( ! this.portlet.getProperty( "excludePContent" ) )
            initialHtmlStr = '<div class="PContent" >' + initialHtmlStr + '</div>';   // BOZO: get this into the template ?

        var preParsePortletResult = this.portlet.preParseAnnotateHtml( initialHtmlStr );
        //this.executeScripts = true;

        var setContentObj = { titles: [], scripts: preParsePortletResult.scripts, linkStyles: [], styles: [], remoteScripts: preParsePortletResult.remoteScripts, xml: preParsePortletResult.portletContent, url: url };

        this.setContent( setContentObj );

        if ( setContentObj.scripts )
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
                    dojo.debug( "setPortletContent [" + this.portlet.entityId + "] script: " + setContentObj.scripts[i] );
                
			    eval( setContentObj.scripts[i] );
		    }
        }
        
        this._executeScripts( { scripts: [], remoteScripts: setContentObj.remoteScripts } );

        this.portlet.postParseAnnotateHtml( this.containerNode );
    }
});

dojo.widget.tags.addParseTreeHandler("dojo:portletwindow");

// ... PortletWindow drag ghost
jetspeed.ui.widget.pwGhost = document.createElement("div");
jetspeed.ui.widget.pwGhost.id = "pwGhost";

jetspeed.ui.widget.PortletWindowDragMoveSource = function( /* jetspeed.ui.widget.PortletWindow */ portletWindow, type)
{
    this.portletWindow = portletWindow;
	dojo.dnd.HtmlDragMoveSource.call(this, portletWindow.domNode, type);
};

dojo.inherits( jetspeed.ui.widget.PortletWindowDragMoveSource, dojo.dnd.HtmlDragMoveSource );

dojo.lang.extend( jetspeed.ui.widget.PortletWindowDragMoveSource, {
	onDragStart: function()
    {
        // BOZO: code copied from dojo.dnd.HtmlDragMoveSource.onDragStart to change dragObject
        this.portletWindow.isDragging = true;
        var dragObj = new jetspeed.ui.widget.PortletWindowDragMoveObject( this.portletWindow, this.dragObject, this.type );

		if ( this.constrainToContainer )
        {
			dragObj.constrainTo( this.constrainingContainer );
		}

		return dragObj;
	},
    onDragEnd: function()
    {
        this.portletWindow.isDragging = false;
        // BOZO: don't do this next thing here - but it in PortletWindow
        this.portletWindow.portlet.submitChangedWindowState();
        //dojo.debug( "jetspeed.ui.widget.PortletWindowDragMoveSource.onDragEnd" );
    }
});

jetspeed.ui.widget.PortletWindowDragMoveObject = function( portletWindow, node, type )
{
    this.portletWindow = portletWindow;
    this.windowTiling = this.portletWindow.windowTiling;
	dojo.dnd.HtmlDragMoveObject.call( this, node, type );
}

dojo.inherits( jetspeed.ui.widget.PortletWindowDragMoveObject, dojo.dnd.HtmlDragMoveObject );

dojo.lang.extend( jetspeed.ui.widget.PortletWindowDragMoveObject, {
    onDragStart: function(e) {
        var portletWindowNode = this.domNode;

        jetspeed.ui.widget.PortletWindowDragMoveObject.superclass.onDragStart.call( this, e );

        // ghost placement - must happen after superclass.onDragStart
        var pwGhost = jetspeed.ui.widget.pwGhost;

        if ( this.windowTiling )
        {
            // ghost placement - must happen after superclass.onDragStart
            pwGhost.style.height = portletWindowNode.offsetHeight+"px";
            portletWindowNode.parentNode.insertBefore( pwGhost, portletWindowNode );

            // domNode removal from column - add to desktop for visual freeform drag
            document.getElementById( jetspeed.id.DESKTOP ).appendChild( portletWindowNode );

            var inColIndex = null;
            this.columnsX = new Array( jetspeed.columns.length );
            for ( var i = 0 ; i < jetspeed.columns.length ; i++ )
            {
                var columnElmt = jetspeed.columns[i];
                this.columnsX[ i ] = dojo.style.getAbsoluteX( columnElmt, true );
                if ( dojo.dom.isDescendantOf( portletWindowNode, columnElmt, true ) )
                    inColIndex = i;
            }
            
            var inCol = ( inColIndex >= 0 ? jetspeed.columns[ inColIndex ] : null );
            pwGhost.col = inCol;
        }

        dojo.debug( "PortletWindowDragMoveObject [" + this.portletWindow.portlet.entityId + "] onDragStart:  portletWindowNode.hasParent=" + dojo.dom.hasParent( portletWindowNode ) + " dragOffset.left=" + this.dragOffset.left + " dragOffset.top=" + this.dragOffset.top + " dragStartPosition.left=" + this.dragStartPosition.left + " dragStartPosition.top=" + this.dragStartPosition.top );
    },
    onDragMove: function(e)
    {
        //jetspeed.ui.widget.PortletWindowDragMoveObject.superclass.onDragMove.call( this, e );
        // BOZO: code copied from dojo.dnd.HtmlDragMoveObject.onDragMove

        var mouse = dojo.html.getCursorPosition(e);
		this.updateDragOffset();
		var x = this.dragOffset.left + mouse.x;
		var y = this.dragOffset.top + mouse.y;
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

        var pwGhost = jetspeed.ui.widget.pwGhost;

        if ( this.windowTiling )
        {
            var colIndex = -1;
            for ( var i = jetspeed.columns.length-1 ; i >= 0  ; i-- )
            {
                //dojo.debug( "PortletWindowDragMoveObject onDragMove: col[" + i + "] columnsX=" + this.columnsX[i] + " this.domNode.offsetWidth/2=" + (this.domNode.offsetWidth/2) + " x=" + x );
                if ( ( x + ( this.domNode.offsetWidth / 2 ) ) >= this.columnsX[ i ] )
                {
                    colIndex = i;
                    break;
                }
            }
            var col = ( colIndex >= 0 ? jetspeed.columns[ colIndex ] : null );
            //if ( col != null )
            //    dojo.debug( "PortletWindowDragMoveObject onDragMove: col[" + colIndex + "] columnsX=" + this.columnsX[colIndex] + " this.domNode.offsetWidth=" + this.domNode.offsetWidth + " x=" + x );
            //else
            //    dojo.debug( "PortletWindowDragMoveObject onDragMove: no col  this.domNode.offsetWidth=" + this.domNode.offsetWidth + " x=" + x );
            
            if ( pwGhost.col != col )
            {
                dojo.dom.removeNode( pwGhost );
				pwGhost.col = col;
				col.appendChild(pwGhost);
			}
            
            var portletWindowsResult = jetspeed.ui.getPortletWindowChildren( col, pwGhost );
            var portletWindowsInCol = portletWindowsResult.portletWindowNodes;
            
            if ( portletWindowsInCol )
            {
                var ghostIndex = portletWindowsResult.matchIndex;
                if ( ghostIndex > 0 )
                {
                    var yAboveWindow = dojo.style.getAbsoluteY( portletWindowsInCol[ ghostIndex -1 ], true );
                    if ( y <= yAboveWindow )
                    {
                        dojo.debug( "onDragMove y <= yAbove [" + this.portletWindow.portlet.entityId + "] y=" + y + " yAboveWindow=" + yAboveWindow + " ghostIndex=" + ghostIndex );
                        dojo.dom.removeNode( pwGhost );
                        dojo.dom.insertBefore( pwGhost, portletWindowsInCol[ ghostIndex -1 ], true );
                    }
                    else
                    {
                        dojo.debug( "onDragMove noadjust y > yAbove [" + this.portletWindow.portlet.entityId + "] y=" + y + " yAboveWindow=" + yAboveWindow + " ghostIndex=" + ghostIndex );
                    }
                }
                if ( ghostIndex != (portletWindowsInCol.length -1) )
                {
                    var yBelowWindow = dojo.style.getAbsoluteY( portletWindowsInCol[ ghostIndex +1 ], true );
                    if ( y >= yBelowWindow )
                    {
                        dojo.debug( "onDragMove y >= yBelow [" + this.portletWindow.portlet.entityId + "] y=" + y + " yBelowWindow=" + yBelowWindow + " ghostIndex=" + ghostIndex );
                        if ( ghostIndex + 2 < portletWindowsInCol.length )
                            dojo.dom.insertBefore( pwGhost, portletWindowsInCol[ ghostIndex +2 ], true );
                        else
                            col.appendChild( pwGhost );
                    }
                    else
                    {
                        dojo.debug( "onDragMove noadjust y < yBelow [" + this.portletWindow.portlet.entityId + "] y=" + y + " yBelowWindow=" + yBelowWindow + " ghostIndex=" + ghostIndex );
                    }
                }
            }
        }
    },
	onDragEnd: function(e)
    {
        jetspeed.ui.widget.PortletWindowDragMoveObject.superclass.onDragEnd.call( this, e );
        
        //dojo.debug( "PortletWindowDragMoveObject [" + this.portletWindow.portlet.entityId + "] onDragEnd:  portletWindowNode.hasParent=" + dojo.dom.hasParent( this.domNode ) );

        var pwGhost = jetspeed.ui.widget.pwGhost;
        
        if ( this.windowTiling )
        {
            if ( pwGhost && pwGhost.col )
            {
                dojo.dom.insertBefore( this.domNode, pwGhost, true );
                dojo.dom.removeNode( pwGhost );
            }
            this.domNode.style.position = "static";
        }
        else
        {
            dojo.dom.removeNode( pwGhost );
        }

        jetspeed.ui.dumpPortletWindowsPerColumn();
	}
});
