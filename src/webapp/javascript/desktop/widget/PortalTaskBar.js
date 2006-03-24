
dojo.provide("jetspeed.ui.widget.PortalTaskBar");
dojo.provide("jetspeed.ui.widget.PortalTaskBarItem");

dojo.require("dojo.widget.*");
dojo.require("dojo.widget.FloatingPane");

jetspeed.ui.widget.PortalTaskBar = function()
{    
    this.widgetType = "PortalTaskBar";

    if ( ! this.taskbarProps )
        this.taskbarProps = {} ;

    dojo.widget.html.FloatingPane.call(this);

    dojo.widget.TaskBar.call(this.taskbarProps);

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
        var btnNode = this.domNode;
        
        // simulate button click
        dojo.fx.html.fade( this.domNode, 100, 1, 0.5, function() { dojo.fx.html.fade( btnNode, 100, 0.5, 1 ); } );
        
        if ( this.window.windowState == "minimized" )
            dojo.fx.html.explode( this.domNode, this.window.domNode, 460, function() { showWindow.show(); } ) ;    // began as 300 in ff
        else
            this.window.show();
	}
});

dojo.widget.tags.addParseTreeHandler("dojo:portaltaskbar");
dojo.widget.tags.addParseTreeHandler("dojo:portaltaskbaritem");
