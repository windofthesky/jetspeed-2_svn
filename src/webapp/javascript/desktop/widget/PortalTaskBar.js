
dojo.provide("jetspeed.ui.widget.PortalTaskBar");
dojo.provide("jetspeed.ui.widget.PortalTaskBarItem");

dojo.require("jetspeed.desktop.core");
dojo.require("dojo.widget.*");
dojo.require("dojo.widget.FloatingPane");

jetspeed.ui.widget.PortalTaskBar = function()
{    
    this.widgetType = "PortalTaskBar";

    dojo.widget.html.FloatingPane.call( this );

    //dojo.widget.TaskBar.call(this);  // can't call with 'this' since the widgetType will kill it  2006-03-31

    this.titleBarDisplay = "none";
};

dojo.inherits(jetspeed.ui.widget.PortalTaskBar, dojo.widget.html.FloatingPane);

dojo.lang.extend(jetspeed.ui.widget.PortalTaskBar, {

    // dojo.widget.Widget create protocol
    postMixInProperties: function( args, fragment, parentComp )
    {
        jetspeed.ui.widget.PortalTaskBar.superclass.postMixInProperties.call( this );

        //if ( ! this.widgetId )
        //    this.widgetId = jetspeed.id.TASKBAR;

        var tbProps = {};
        tbProps.templateCssPath = new dojo.uri.Uri( jetspeed.prefs.getDesktopThemeRootUrl() + "/css/PortalTaskBar.css" ) ;
        tbProps.templatePath = new dojo.uri.Uri( jetspeed.url.basePortalDesktopUrl() + "/javascript/desktop/widget/HtmlTaskBarItemTemplate.html" ) ;
        // BOZO: improve this junk ^^^ 

        this.templatePath = jetspeed.ui.getDefaultFloatingPaneTemplate();
        this.templateCssPath = jetspeed.ui.getDefaultFloatingPaneTemplateCss();   // BOZO: this currently is responsible for assuring that 
                                                                                  //       the base FloatingPane styles get included;
                                                                                  //       so, if the taskbar is not included and/or an override
                                                                                  //       css file is needed, the base FloatingPane styles may be absent
        this.taskbarProps = tbProps ;
    },

    // dojo.widget.Widget create protocol
    postCreate: function( args, fragment, parentComp )
    {
        jetspeed.ui.widget.PortalTaskBar.superclass.postCreate.call( this );
        
        //this.domNode.id = "jetspeedTaskbar";  // BOZO: must set the id here - it gets defensively cleared by dojo
        //this.domNode.style.cssText = "background-color: #666; width: 100%; bottom: 5px; height: 100px";
        if ( ! this.domNode.id )
            this.domNode.id = this.widgetId;

        this.contextMenuCreate();
    },

    addChild: function( child )
    {
        var tbiProps = {windowId: child.widgetId, caption: child.title, iconSrc: child.iconSrc, widgetId: child.widgetId + "_tbi" } ;
        dojo.lang.mixin(tbiProps, this.taskbarProps) ;
        var tbi = dojo.widget.createWidget("PortalTaskBarItem", tbiProps);
        jetspeed.ui.widget.PortalTaskBar.superclass.addChild.call(this,tbi);
        //dojo.debug( "PortalTaskBarItem  widgetId=" + tbi.widgetId + " domNode.id=" + tbi.domNode.id + " child.domNode.id=" + child.widgetId ) ;
    },
    contextMenuCreate: function()
    {
        var taskBarContextMenu = dojo.widget.createWidget( "PopupMenu2", { id: "jstb_menu", targetNodeIds: [ this.domNode.id ], contextMenuForWindow: false }, null );
        var resetLayoutMenuItem = dojo.widget.createWidget( "MenuItem2", { id: "jstb_menu_item1", caption: "Reset Window Layout"} );
        var freeFormLayoutMenuItem = dojo.widget.createWidget( "MenuItem2", { id: "jstb_menu_item2", caption: "Free Flowing Layout"} );
        var twoColummLayoutMenuItem = dojo.widget.createWidget( "MenuItem2", { id: "jstb_menu_item3", caption: "Two Column Layout"} );
        var threeColummLayoutMenuItem = dojo.widget.createWidget( "MenuItem2", { id: "jstb_menu_item4", caption: "Three Column Layout"} );
        
        dojo.event.connect( resetLayoutMenuItem, "onClick", function(e) { jetspeed.page.resetWindowLayout(); } );
        dojo.event.connect( freeFormLayoutMenuItem, "onClick", function(e) { jetspeed.prefs.windowTiling = false; jetspeed.page.resetWindowLayout(); jetspeed.page.reload(); } );
        dojo.event.connect( twoColummLayoutMenuItem, "onClick", function(e) { jetspeed.prefs.windowTiling = 2; jetspeed.page.reload(); } );
        dojo.event.connect( threeColummLayoutMenuItem, "onClick", function(e) { jetspeed.prefs.windowTiling = 3; jetspeed.page.reload(); } );
        taskBarContextMenu.addChild( resetLayoutMenuItem );
        taskBarContextMenu.addChild( freeFormLayoutMenuItem );
        taskBarContextMenu.addChild( twoColummLayoutMenuItem );
        taskBarContextMenu.addChild( threeColummLayoutMenuItem );
        document.body.appendChild( taskBarContextMenu.domNode );
    }
});




jetspeed.ui.widget.PortalTaskBarItem = function(){
    
	dojo.widget.html.TaskBarItem( this );
    
    this.widgetType = "PortalTaskBarItem";
};
dojo.inherits(jetspeed.ui.widget.PortalTaskBarItem, dojo.widget.html.TaskBarItem);

dojo.lang.extend(jetspeed.ui.widget.PortalTaskBarItem, {
    fillInTemplate: function()
    {
        var tdNode = this.domNode.getElementsByTagName( "td" )[0];
		if ( this.iconSrc != '' ) {
			var img = document.createElement("img");
			img.src = this.iconSrc;
			tdNode.appendChild( img );
		}
		tdNode.appendChild( document.createTextNode( this.caption ) );
		dojo.html.disableSelection( this.domNode );
	},
    onClick: function()
    {
        var showWindow = this.window;
        var showWindowNode = showWindow.domNode;
        var btnNode = this.domNode;
        
        // sequencing these effects makes IE happier
        //   - we fadeOut the button to 50% opacity
        //   - we fadeIn the button back to normal
        //   - we explode or show the window
        
        var showWindowCallback = function()
        {
            if (dojo.render.html.ie)
                dojo.lang.setTimeout( function() { showWindow.show(); }, 100 );
            else
                showWindow.show();
        }
        var explodeCallback = function()
        {   
            if ( showWindow.windowState == "minimized" )
                dojo.fx.html.explode( btnNode, showWindowNode, 300, showWindowCallback ) ;    // began as 300 in ff
            else
                showWindow.show();
        }
        var fadeCallback = function()
        {
            dojo.fx.html.fade( btnNode, 75, 0.5, 1, explodeCallback );
        }
        dojo.fx.html.fade( btnNode, 80, 1, 0.5, fadeCallback );
	}
});

dojo.widget.tags.addParseTreeHandler("dojo:portaltaskbar");
dojo.widget.tags.addParseTreeHandler("dojo:portaltaskbaritem");
