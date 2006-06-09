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
        this.templateCssPath = new dojo.uri.Uri( jetspeed.prefs.getDesktopThemeRootUrl() + "/css/PortalTabContainer.css" ) ;
        jetspeed.ui.widget.PortalTabContainer.superclass.postMixInProperties.call( this, args, fragment, parentComp );
    },
    // dojo.widget.Widget create protocol
    postCreate: function( args, fragment, parentComp )
    {
        jetspeed.ui.widget.PortalTabContainer.superclass.postCreate.call( this, args, fragment, parentComp );
        
        this.contextMenuCreate();
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
        if ( jetspeed.page.equalsPageUrl( menuOpt.getUrl() ) )
        {
            this.selectTab( tab );   // this.selectedTab
            this.selectedTab = null;  // to keep it from matching the fake widgets with no widgetdI
        }
        this.js_addingTab = false;
    },
    selectTab: function( tab, _noRefresh )
    {
        jetspeed.ui.widget.PortalTabContainer.superclass.selectTab.call( this, tab );
        if ( ! this.js_addingTab && ! _noRefresh )
        {
            tab.menuOption.navigateTo();
        }
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

    createJetspeedMenu: function( /* jetspeed.om.Menu */ menuObj )
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
    },
    contextMenuCreate: function()
    {
        var taskBarContextMenu = dojo.widget.createWidget( "PopupMenu2", { id: "jstc_menu", targetNodeIds: [ this.domNode.id ], contextMenuForWindow: false }, null );
        //var resetLayoutMenuItem = dojo.widget.createWidget( "MenuItem2", { id: "jstc_menu_item1", caption: "Reset Window Layout"} );
        //var freeFormLayoutMenuItem = dojo.widget.createWidget( "MenuItem2", { id: "jstc_menu_item2", caption: "Free Flowing Layout"} );
        //var twoColummLayoutMenuItem = dojo.widget.createWidget( "MenuItem2", { id: "jstc_menu_item3", caption: "Two Column Layout"} );
        //var threeColummLayoutMenuItem = dojo.widget.createWidget( "MenuItem2", { id: "jstc_menu_item4", caption: "Three Column Layout"} );
        var openPortletSelectorMenuItem = dojo.widget.createWidget( "MenuItem2", { id: "jstc_menu_item5", caption: "Portlet Selector"} );
        
        //dojo.event.connect( resetLayoutMenuItem, "onClick", function(e) { jetspeed.page.resetWindowLayout(); } );
        //dojo.event.connect( freeFormLayoutMenuItem, "onClick", function(e) { jetspeed.prefs.windowTiling = false; jetspeed.page.resetWindowLayout(); jetspeed.page.reload(); } );
        //dojo.event.connect( twoColummLayoutMenuItem, "onClick", function(e) { jetspeed.prefs.windowTiling = 2; jetspeed.page.reload(); } );
        //dojo.event.connect( threeColummLayoutMenuItem, "onClick", function(e) { jetspeed.prefs.windowTiling = 3; jetspeed.page.reload(); } );
        dojo.event.connect( openPortletSelectorMenuItem, "onClick", function(e) { jetspeed.loadPortletSelector(); } );
        //taskBarContextMenu.addChild( resetLayoutMenuItem );
        //taskBarContextMenu.addChild( freeFormLayoutMenuItem );
        //taskBarContextMenu.addChild( twoColummLayoutMenuItem );
        //taskBarContextMenu.addChild( threeColummLayoutMenuItem );
        taskBarContextMenu.addChild( openPortletSelectorMenuItem );
        document.body.appendChild( taskBarContextMenu.domNode );
    }
});

dojo.widget.tags.addParseTreeHandler("dojo:portaltabcontainer");
