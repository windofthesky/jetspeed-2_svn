
dojo.provide("jetspeed.ui.widget.PortalTaskBar");
dojo.provide("jetspeed.ui.widget.PortalTaskBarItem");

dojo.require("dojo.widget.*");
dojo.require("dojo.widget.FloatingPane");

jetspeed.ui.widget.PortalTaskBar = function()
{    
    this.widgetType = "PortalTaskBar";

    dojo.widget.html.FloatingPane.call(this);

    //dojo.widget.TaskBar.call(this);  // can't call with 'this' since the widgetType will kill it  2006-03-31

    this.titleBarDisplay = "none";
}

dojo.inherits(jetspeed.ui.widget.PortalTaskBar, dojo.widget.html.FloatingPane);

dojo.lang.extend(jetspeed.ui.widget.PortalTaskBar, {
    addChild: function(child)
                {
                    var tbiProps = {windowId: child.widgetId, caption: child.title, iconSrc: child.iconSrc, widgetId: child.widgetId + "_tbi" } ;
                    dojo.lang.mixin(tbiProps, this.taskbarProps) ;
                    var tbi = dojo.widget.createWidget("PortalTaskBarItem", tbiProps);
                    jetspeed.ui.widget.PortalTaskBar.superclass.addChild.call(this,tbi);
                    //dojo.debug( "PortalTaskBarItem  widgetId=" + tbi.widgetId + " domNode.id=" + tbi.domNode.id + " child.domNode.id=" + child.widgetId ) ;
                }
});

jetspeed.ui.widget.PortalTaskBarItem = function(){
    
	dojo.widget.html.TaskBarItem(this);
    
    this.widgetType = "PortalTaskBarItem";
}
dojo.inherits(jetspeed.ui.widget.PortalTaskBarItem, dojo.widget.html.TaskBarItem);

dojo.lang.extend(jetspeed.ui.widget.PortalTaskBarItem, {
    fillInTemplate: function() {
        var tdNode = this.domNode.getElementsByTagName( "td" )[0];
		if ( this.iconSrc != '' ) {
			var img = document.createElement("img");
			img.src = this.iconSrc;
			tdNode.appendChild(img);
		}
		tdNode.appendChild(document.createTextNode(this.caption));
		dojo.html.disableSelection(this.domNode);
	},
    onClick: function() {
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
