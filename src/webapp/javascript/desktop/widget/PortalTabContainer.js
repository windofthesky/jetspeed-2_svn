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
    selectTab: function( tab )
    {
        jetspeed.ui.widget.PortalTabContainer.superclass.selectTab.call( this, tab );
        //dojo.debug( "PortalTabContainer.selectTab " + tab.label);
        if ( ! this.js_addingTab )
            tab.menuOption.navigateTo();
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
    }
});

dojo.widget.tags.addParseTreeHandler("dojo:portaltabcontainer");
