
dojo.provide("jetspeed.ui.widget.PortletWindow");

dojo.require("jetspeed.desktop.core");
dojo.require("dojo.widget.*");
dojo.require("dojo.widget.FloatingPane");

jetspeed.ui.widget.PortletWindow = function()
{
    dojo.widget.html.FloatingPane.call( this );
    this.widgetType = "PortletWindow";
    this.portletInitialized = false;
};

dojo.inherits(jetspeed.ui.widget.PortletWindow, dojo.widget.html.FloatingPane);

dojo.lang.extend(jetspeed.ui.widget.PortletWindow, {
    title: "Unknown Portlet",
    constrainToContainer: "1",
    contentWrapper: "layout",
    displayCloseAction: true,
    displayMinimizeAction: true,
    displayMaximizeAction: true,
    taskBarId: jetspeed.id.TASKBAR,
    nextIndex: 1,
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
        
        if (! windowtheme)
        {
            if ( jetspeed.debugPortletWindowThemes )
            {
                windowtheme = jetspeed.debugPortletWindowThemes[Math.floor(Math.random()*jetspeed.debugPortletWindowThemes.length)];
            }
        }
        if (windowtheme)
        {
            this.portletWindowTheme = windowtheme ;
            this.templateCssPath = new dojo.uri.Uri(jetspeed.url.basePortalDesktopUrl(), "jetspeed/javascript/desktop/windowthemes/" + windowtheme + "/css/styles.css");
        }
        this.templatePath = jetspeed.ui.getDefaultFloatingPaneTemplate();
        
        if (! windowicon)
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
            this.hasShadow = "true";
            //        dojo.debug( "nWidget.domNode.cssText: " + 
            //nWidget.domNode.style = "overflow: visible;";   // so that drop shadow is displayed
        }

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
            this.domNode.className = domNodeClassName;
        }
        this.domNode.className = jetspeed.id.PORTLET_STYLE_CLASS + ( domNodeClassName ? ( " " + domNodeClassName ) : "" );


        if ( this.containerNode )
        {
            var containerNodeClassName = this.containerNode.className;
            if ( this.portletWindowTheme )
            {
                var existingClassName = this.containerNode.className;
                containerNodeClassName = this.portletWindowTheme + ( containerNodeClassName ? ( " " + containerNodeClassName ) : "" );
            }
            this.containerNode.className = jetspeed.id.PORTLET_STYLE_CLASS + ( containerNodeClassName ? ( " " + containerNodeClassName ) : "" );
            dojo.debug( "setting containerNode [" + this.portlet.entityId + "] className=" + this.containerNode.className );
        }
    
        var portletWindowState = this.portlet.getLastSavedWindowState();
        var portletWidth = portletWindowState.width;
        var portletHeight = portletWindowState.height;
        var portletLeft = portletWindowState.left;
        var portletTop = portletWindowState.top;
        // NOTE: portletWindowState.zIndex;  - should be dealt with in the creation order

        if ( portletWidth != null && portletWidth > 0 ) portletWidth = Math.floor(portletWidth) + "px";
        else portletWidth = "280px";
        this.domNode.style.width = portletWidth;
    
        if ( portletHeight != null && portletHeight > 0 ) portletHeight = Math.floor(portletHeight) + "px";
        else portletHeight = "200px";
        this.domNode.style.height = portletHeight;
            
        if ( portletLeft != null && portletLeft >= 0 ) portletLeft = Math.floor(portletLeft) + "px";
        else portletLeft = (((this._getNextIndex() -2) * 30 ) + 200) + "px";
        this.domNode.style.left = portletLeft;
    
        if ( portletTop != null && portletTop >= 0 ) portletTop = Math.floor(portletTop) + "px";
        else portletTop = (((this._getNextIndex() -2) * 30 ) + 170) + "px";
        this.domNode.style.top =  portletTop;
    
        if ( jetspeed.debug.createWindow )
            dojo.debug( "createWindow [" + this.portlet.entityId + "]" + " width=" + portletWidth + " height=" + portletHeight + " left=" + portletLeft + " top=" + portletTop ) ;
    
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
            if (btn.style.display == "none")
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
                btn.style.display = "none" ;
            else if (btn.style.display != "none")
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
        
        initialHtmlStr = '<div class="PContent" >' + initialHtmlStr + '</div>';    // style="overflow: auto"

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


jetspeed.ui.widget.PortletWindowDragMoveSource = function( /* jetspeed.ui.widget.PortletWindow */ portletWindow, type)
{
    this.portletWindow = portletWindow;
	dojo.dnd.HtmlDragMoveSource.call(this, portletWindow.domNode, type);
};

dojo.inherits(jetspeed.ui.widget.PortletWindowDragMoveSource, dojo.dnd.HtmlDragMoveSource);

dojo.lang.extend(jetspeed.ui.widget.PortletWindowDragMoveSource, {
	onDragStart: function()
    {
        //dojo.debug( "jetspeed.ui.widget.PortletWindowDragMoveSource.onDragStart" );
        this.portletWindow.isDragging = true;
        return jetspeed.ui.widget.PortletWindowDragMoveSource.superclass.onDragStart.call( this );
	},
    onDragEnd: function()
    {
        this.portletWindow.isDragging = false;
        // BOZO: don't do this next thing here - but it in PortletWindow
        this.portletWindow.portlet.submitChangedWindowState();
        //dojo.debug( "jetspeed.ui.widget.PortletWindowDragMoveSource.onDragEnd" );
    }
});
