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

dojo.provide("jetspeed.ui.widget.PortalAccordionContainer");
dojo.provide("jetspeed.ui.widget.PortalMenuOptionLink");

dojo.require("jetspeed.desktop.core");
dojo.require("dojo.widget.*");
dojo.require("dojo.widget.TabContainer");

jetspeed.ui.widget.PortalAccordionContainer = function()
{    
    this.widgetType = "PortalAccordionContainer";
    this.isContainer = true;
    //this.templateString = '<div id="navcolumn"><table cellpadding="0" cellspacing="4" border="0" width="100%"><tr><td><div dojoAttachPoint="containerNode" class="toolgroup"></div></td></tr></table></div>';
    this.templateString = '<div dojoAttachPoint="containerNode" class="toolgroup"></div>';
    dojo.widget.HtmlWidget.call(this);
};

dojo.inherits(jetspeed.ui.widget.PortalAccordionContainer, dojo.widget.HtmlWidget);

dojo.lang.extend( jetspeed.ui.widget.PortalAccordionContainer,
{
    // dojo.widget.Widget create protocol
    postMixInProperties: function( args, fragment, parentComp )
    {
        this.templateCssPath = new dojo.uri.Uri( jetspeed.prefs.getDesktopThemeRootUrl() + "/css/PortalAccordionContainer.css" ) ;
        jetspeed.ui.widget.PortalAccordionContainer.superclass.postMixInProperties.call( this, args, fragment, parentComp );
    },
    createAndAddPane: function( /* jetspeed.om.MenuOption */ labelMenuOption, accordionPaneProps )
    {
        if ( ! accordionPaneProps )
            accordionPaneProps = {};
        if ( labelMenuOption )
        {
            accordionPaneProps.label = labelMenuOption.getText();
            if ( labelMenuOption.getHidden() )
                accordionPaneProps.open = false;
            else
                accordionPaneProps.open = true;
            
            accordionPaneProps.labelNodeClass = "label";
            accordionPaneProps.containerNodeClass = "FolderList";
            accordionPaneProps.templatePath = new dojo.uri.Uri( jetspeed.url.basePortalDesktopUrl() + "/javascript/desktop/widget/TitlePane.html" ) ;
        }

        var accordionPaneWidget = dojo.widget.createWidget( "PortalAccordionPane", accordionPaneProps );
        this.addChild( accordionPaneWidget );
        return accordionPaneWidget;
    },
    addLinksToPane: function( accordionPaneWidget, /* Array */ menuOptions )
    {
        if ( ! menuOptions || ! accordionPaneWidget ) return;

        var linkWidget;
        for ( var i = 0; i < menuOptions.length; i++ )
        {
            linkWidget = dojo.widget.createWidget( "PortalMenuOptionLink", { menuOption: menuOptions[i] } );
            accordionPaneWidget.addChild( linkWidget );
        }
    },
    createJetspeedMenu: function( /* jetspeed.om.Menu */ menuObj )
    {
        if ( ! menuObj ) return;
        var menuOpts = menuObj.getOptions();
        var currentLinkGroup = [], currentLinkGroupOpt = null, menuOption = null, menuOptIndex = 0;
        while ( currentLinkGroup != null )
        {
            menuOption = null;
            if ( menuOptIndex < menuOpts.length )
            {   // another one
                menuOption = menuOpts[menuOptIndex];
                
                menuOptIndex++;
            }
            if ( menuOption == null || menuOption.isSeparator() )
            {
                if ( currentLinkGroup != null && currentLinkGroup.length > 0 )
                {   // add pane
                    var accordionPaneWidget = this.createAndAddPane( currentLinkGroupOpt );
                    this.addLinksToPane( accordionPaneWidget, currentLinkGroup );
                }
                currentLinkGroupOpt = null;
                currentLinkGroup = null;
                if ( menuOption != null )
                {
                    currentLinkGroupOpt = menuOption;
                    currentLinkGroup = [];
                }
            }
            else if ( menuOption.isLeaf() && menuOption.getUrl() )
            {
                currentLinkGroup.push( menuOption );
            }
        }        
    }
});


jetspeed.ui.widget.PortalAccordionPane = function()
{  
    dojo.widget.html.AccordionPane.call(this);
    this.widgetType = "PortalAccordionPane";
};

dojo.inherits( jetspeed.ui.widget.PortalAccordionPane, dojo.widget.html.AccordionPane );

dojo.lang.extend( jetspeed.ui.widget.PortalAccordionPane,
{
    setSizes: function()
    {
        this.siblingWidgets = [];    // to keep label click from collapsing all siblings
    }

});

jetspeed.ui.widget.PortalMenuOptionLink = function()
{    
	dojo.widget.HtmlWidget.call(this);
    
    this.widgetType = "PortalMenuOptionLink";
    this.templateString = '<div dojoAttachPoint="containerNode"><a href="" dojoAttachPoint="menuOptionLinkNode" dojoAttachEvent="onClick" class="Link"></a></div>';
};
dojo.inherits(jetspeed.ui.widget.PortalMenuOptionLink, dojo.widget.HtmlWidget);

dojo.lang.extend(jetspeed.ui.widget.PortalMenuOptionLink, {
    fillInTemplate: function()
    {
		if ( this.iconSrc )
        {
			var img = document.createElement("img");
			img.src = this.iconSrc;
            this.menuOptionLinkNode.appendChild( img );
		}
        this.menuOptionLinkNode.href = this.menuOption.navigateUrl();
		this.menuOptionLinkNode.appendChild( document.createTextNode( this.menuOption.getShortTitle() ) );
		dojo.html.disableSelection( this.domNode );
	},
    onClick: function( evt )
    {
        this.menuOption.navigateTo();
        dojo.event.browser.stopEvent( evt );
	}
});

dojo.widget.tags.addParseTreeHandler("dojo:portalmenuoptionlink");
dojo.widget.tags.addParseTreeHandler("dojo:portalaccordioncontainer");
dojo.widget.tags.addParseTreeHandler("dojo:portalaccordionpane");
