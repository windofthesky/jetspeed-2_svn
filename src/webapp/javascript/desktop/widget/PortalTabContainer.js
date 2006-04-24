dojo.provide("jetspeed.ui.widget.PortalTabContainer");

dojo.require("jetspeed.desktop.core");
dojo.require("dojo.widget.*");
dojo.require("dojo.widget.TabContainer");

jetspeed.ui.widget.PortalTabContainer = function()
{    
    this.widgetType = "PortalTabContainer";
    this.js_addingTab = false;
    dojo.widget.html.TabContainer.call( this );
};

dojo.inherits(jetspeed.ui.widget.PortalTabContainer, dojo.widget.html.TabContainer);

dojo.lang.extend( jetspeed.ui.widget.PortalTabContainer,
{
    // dojo.widget.Widget create protocol
    postMixInProperties: function( args, fragment, parentComp )
    {
        this.templateCssPath = new dojo.uri.Uri( jetspeed.url.basePortalDesktopUrl() + "/javascript/desktop/widget/HtmlTabContainer.css" ) ;
        jetspeed.ui.widget.PortalTabContainer.superclass.postMixInProperties.call( this, args, fragment, parentComp );
    },
    addTab: function( /* jetspeed.om.MenuOption */ menuOpt )
    {
        if ( ! menuOpt ) return;
        this.js_addingTab = true;
        var tabDomNode = document.createElement( "div" );
        var tab = new dojo.widget.HtmlWidget();   // create a fake widget so that widget.addedTo doesn't bomb when we call this.addChild() below
        tab.domNode = tabDomNode;
        tab.menuOption = menuOpt;
        tab.label = menuOpt.getShortTitle();
        this.addChild( tab );
        //dojo.debug( "PortalTabContainer.addTab" );
        if ( jetspeed.page.isPageUrl( menuOpt.getUrl() ) )
        {
            this.selectTab( tab );   // this.selectedTab
            this.selectedTab = null;  // to keep it from matching the fake widgets with no widgetdI
        }
        this.js_addingTab = false;
    },
    selectTab: function( tab )
    {
        jetspeed.ui.widget.PortalTabContainer.superclass.selectTab.call( this, tab );
        //dojo.debug( "PortalTabContainer.selectTab " + tab.label);
        if ( ! this.js_addingTab )
            jetspeed.menuNavClick( tab.menuOption );
	},
    _showTab: function( tab )
    {
		dojo.html.addClass( tab.div, "current" );
		tab.selected = true;
	},
    _hideTab: function( tab )
    {
		dojo.html.removeClass( tab.div, "current" );
		tab.selected=false;
	},
    createTabsFromMenu: function( /* jetspeed.om.Menu */ menuObj )
    {
        if ( ! menuObj ) return;
        var menuOpts = menuObj.getOptions();
        for ( var i = 0 ; i < menuOpts.length ; i++ )
        {
            var menuOption = menuOpts[i];
            if ( menuOption.isLeaf() && menuOption.getUrl() && ! menuOption.isSeparator() )
            {
                this.addTab( menuOption );
            }
        }
    }
});

dojo.widget.tags.addParseTreeHandler("dojo:portaltabcontainer");
