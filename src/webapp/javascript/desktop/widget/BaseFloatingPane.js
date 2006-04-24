
dojo.provide("jetspeed.ui.widget.BaseFloatingPane");

//
// taken from dojo-2006-04-06 FloatingPane and modified
// needs to be cleaned-up a bit if we're going to stick with this solution
//

dojo.require("dojo.widget.*");
dojo.require("dojo.widget.Manager");
dojo.require("dojo.html");
dojo.require("dojo.html.shadow");
dojo.require("dojo.style");
dojo.require("dojo.dom");
dojo.require("dojo.widget.ContentPane");
dojo.require("dojo.dnd.HtmlDragMove");
dojo.require("dojo.dnd.HtmlDragMoveSource");
dojo.require("dojo.dnd.HtmlDragMoveObject");
dojo.require("dojo.widget.ResizeHandle");

jetspeed.ui.widget.BaseFloatingPane = function(){
	dojo.widget.html.ContentPane.call(this);
}

dojo.inherits(jetspeed.ui.widget.BaseFloatingPane, dojo.widget.html.ContentPane);

dojo.lang.extend(jetspeed.ui.widget.BaseFloatingPane, {
	widgetType: "BaseFloatingPane",

	// Constructor arguments
	title: '',
	iconSrc: '',
	hasShadow: false,
	constrainToContainer: false,
	taskBarId: "",
	resizable: true,
	titleBarDisplay: "fancy",

	windowState: "normal",
	displayCloseAction: false,
	displayMinimizeAction: false,
	displayMaximizeAction: false,

	maxTaskBarConnectAttempts: 5,
	taskBarConnectAttempts: 0,

	templatePath: dojo.uri.dojoUri("src/widget/templates/HtmlFloatingPane.html"),
	templateCssPath: dojo.uri.dojoUri("src/widget/templates/HtmlFloatingPane.css"),

	fillInTemplate: function(args, frag){
		// Copy style info and id from input node to output node
		var source = this.getFragNodeRef(frag);
		this.domNode.style.cssText = source.style.cssText;
		dojo.html.addClass(this.domNode, dojo.html.getClass(source));

		// necessary for safari, khtml (for computing width/height)
		document.body.appendChild(this.domNode);

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

			this.minimizeAction.style.display= (this.displayMinimizeAction ? "block" : "none");
			this.maximizeAction.style.display= (this.displayMaximizeAction ? "block" : "none");
			this.restoreAction.style.display= (this.displayRestoreAction ? "none" : "none");   // no space given until displayed
			this.closeAction.style.display= (this.displayCloseAction ? "block" : "none");

			//var drag = new dojo.dnd.HtmlDragMoveSource(this.domNode);	
			//if (this.constrainToContainer) {
			//	drag.constrainTo();
			//}
			//drag.setDragHandle(this.titleBar);
		}

        if ( ! this.resizable && this.resizeBar ) { this.resizeBar.style.display = "none"; }
		//if(this.resizable){
		//	this.resizeBar.style.display="";
		//	var rh = dojo.widget.createWidget("ResizeHandle", {targetElmId: this.widgetId, id:this.widgetId+"_resize"});
		//	this.resizeBar.appendChild(rh.domNode);
		//}

		// add a drop shadow
		if(this.hasShadow){
			this.shadow=new dojo.html.shadow(this.domNode);
		}

		// Prevent IE bleed-through problem
		this.bgIframe = new dojo.html.BackgroundIframe(this.domNode);

		if( this.taskBarId ){
			this.taskBarSetup();
		}

        this.resetLostHeightWidth();
		
		if (dojo.hostenv.post_load_) {
			this.setInitialWindowState();
		} else {
			dojo.addOnLoad(this, "setInitialWindowState");
		}

		// counteract body.appendChild above
		document.body.removeChild(this.domNode);

		jetspeed.ui.widget.BaseFloatingPane.superclass.fillInTemplate.call(this, args, frag);
	},

    resetLostHeightWidth: function()
    {
        // figure out how much space is used for padding/borders etc.
		this.lostHeight=
			(dojo.style.getOuterHeight(this.domNode)-dojo.style.getContentHeight(this.domNode))
			+dojo.style.getOuterHeight(this.titleBar)
			+dojo.style.getOuterHeight(this.resizeBar);
		this.lostWidth=
			dojo.style.getOuterWidth(this.domNode)-dojo.style.getContentWidth(this.domNode);
    },

	maximizeWindow: function(evt) {
        this._setPreviousDimensions();

		this.domNode.style.left =
			dojo.style.getPixelValue(this.domNode.parentNode, "padding-left", true) + "px";
		this.domNode.style.top =
			dojo.style.getPixelValue(this.domNode.parentNode, "padding-top", true) + "px";

		if ((this.domNode.parentNode.nodeName.toLowerCase() == 'body')) {
			this.resizeTo(
				dojo.html.getViewportWidth()-dojo.style.getPaddingWidth(document.body),
				dojo.html.getViewportHeight()-dojo.style.getPaddingHeight(document.body)
			);
		} else {
			this.resizeTo(
				dojo.style.getContentWidth(this.domNode.parentNode),
				dojo.style.getContentHeight(this.domNode.parentNode)
			);
		}
		this.maximizeAction.style.display="none";
		this.restoreAction.style.display="";
		this.windowState="maximized";
	},

    _setPreviousDimensions: function() {
        this.previous={
			width: this.width,
			height: this.height,
			left: this.domNode.style.left,
			top: this.domNode.style.top,
			bottom: this.domNode.style.bottom,
			right: this.domNode.style.right
        };
    },

	minimizeWindow: function(evt) {
        if ( this.windowState != "maximized" )
            this._setPreviousDimensions();

		this.hide();
        
		this.windowState = "minimized";
	},

	restoreWindow: function(evt) {
        for(var attr in this.previous){
			this.domNode.style[attr]=this.previous[attr];
		}
        
		this.resizeTo(this.previous.width, this.previous.height);
		this.previous=null;

		this.restoreAction.style.display="none";
		this.maximizeAction.style.display=this.displayMaximizeAction ? "" : "none";

		this.windowState="normal";
	},

	closeWindow: function(evt) {
		dojo.dom.removeNode(this.domNode);
		this.destroy();
	},

	onMouseDown: function(evt) {
		this.bringToTop();
	},

	bringToTop: function() {
		var floatingPanes= dojo.widget.manager.getWidgetsByType(this.widgetType);
		var windows = [];
		for (var x=0; x<floatingPanes.length; x++) {
			if (this.widgetId != floatingPanes[x].widgetId) {
					windows.push(floatingPanes[x]);
			}
		}

		windows.sort(function(a,b) {
			return a.domNode.style.zIndex - b.domNode.style.zIndex;
		});
		
		windows.push(this);

		var floatingPaneStartingZ = 100;
		for (x=0; x<windows.length;x++) {
			windows[x].domNode.style.zIndex = floatingPaneStartingZ + x;
		}
	},

	setInitialWindowState: function() {
		if (this.windowState == "maximized") {
			this.maximizeWindow();
			this.show();
			return;
		}

		if (this.windowState=="normal") {
			this.show();
			return;
		}

		if (this.windowState=="minimized") {
			this.hide();
			return;
		}

		this.windowState="minimized";
	},

	// add icon to task bar, connected to me
	taskBarSetup: function() {
		var taskbar = dojo.widget.getWidgetById(this.taskBarId);
		if (!taskbar){
			if (this.taskBarConnectAttempts <  this.maxTaskBarConnectAttempts) {
				dojo.lang.setTimeout(this, this.taskBarSetup, 50);
				this.taskBarConnectAttempts++;
			} else {
				dojo.debug("Unable to connect to the taskBar");
			}
			return;
		}
		taskbar.addChild(this);
	},

	show: function(){
		jetspeed.ui.widget.BaseFloatingPane.superclass.show.apply(this, arguments);
		this.bringToTop();
	},

	onShow: function(){
		jetspeed.ui.widget.BaseFloatingPane.superclass.onShow.call(this);
		this.resizeTo(dojo.style.getOuterWidth(this.domNode), dojo.style.getOuterHeight(this.domNode));
	},

	resizeTo: function(w, h){
		if(w==this.width && h == this.height){
			return;
		}
		this.width=w;
		this.height=h;

		// IE won't let you decrease the width of the domnode unless you decrease the
		// width of the inner nodes first (???)
		dojo.lang.forEach(
			[this.titleBar, this.resizeBar, this.containerNode],
			function(node){ dojo.style.setOuterWidth(node, w - this.lostWidth); }, this
		);
		dojo.style.setOuterWidth(this.domNode, w);

		dojo.style.setOuterHeight(this.domNode, h);
		dojo.style.setOuterHeight(this.containerNode, h-this.lostHeight);

		this.onResized();
	}
});

//dojo.widget.tags.addParseTreeHandler("dojo:BaseFloatingPane");
