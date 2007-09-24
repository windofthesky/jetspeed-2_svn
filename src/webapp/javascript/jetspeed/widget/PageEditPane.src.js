/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * author: Steve Milek
 */

dojo.provide("jetspeed.widget.PageEditPane");

dojo.require("dojo.widget.*");
dojo.require("dojo.io.*");
dojo.require("dojo.event.*");
dojo.require("dojo.widget.HtmlWidget");
dojo.require("dojo.widget.Dialog");
dojo.require("dojo.widget.Select");
dojo.require("dojo.widget.Button");

dojo.require("dojo.html.common");
dojo.require("dojo.html.display");

jetspeed.widget.PageEditPane = function()
{
}

dojo.widget.defineWidget(
	"jetspeed.widget.PageEditPane",
	dojo.widget.HtmlWidget,
	{
        // template parameters
        pageEditContainer: null,
        pageEditLDContainer: null,
        pageEditPDContainer: null,

        deletePageDialog: null,
		deletePageDialogBg: null,
		deletePageDialogFg: null,

        createPageDialog: null,
		createPageDialogBg: null,
		createPageDialogFg: null,

        layoutDecoratorSelect: null,
        portletDecoratorSelect: null,
        
        // fields
		isContainer: true,
        widgetsInTemplate: true,
        layoutDecoratorDefinitions: null,
        portletDecoratorDefinitions: null,


        // protocol - dojo.widget.Widget create

        postMixInProperties: function( args, fragment, parent )
        {
            jetspeed.widget.PageEditPane.superclass.postMixInProperties.apply( this, arguments );

            this.templateCssPath = new dojo.uri.Uri( jetspeed.url.basePortalDesktopUrl() + "/javascript/jetspeed/widget/PageEditPane.css" ) ;
            this.templatePath = new dojo.uri.Uri( jetspeed.url.basePortalDesktopUrl() + "/javascript/jetspeed/widget/PageEditPane.html" ) ;
        },

        fillInTemplate: function( args, fragment )
        {
            var self = this;
            this.deletePageDialog = dojo.widget.createWidget( "dialog", { widgetsInTemplate: true, deletePageConfirmed: function() { this.hide(); self.deletePageConfirmed(); } }, this.deletePageDialog );
			this.deletePageDialog.setCloseControl( this.deletePageDialog.deletePageCancel.domNode );

            var createPageParams = {};
            createPageParams.widgetsInTemplate = true;
            createPageParams.createPageConfirmed = function()
            {
                var pageName = this.createPageNameTextbox.textbox.value;
                var pageTitle = this.createPageTitleTextbox.textbox.value;
                var pageShortTitle = this.createPageShortTitleTextbox.textbox.value;
                this.hide();
                self.createPageConfirmed( pageName, pageTitle, pageShortTitle );
            };
            this.createPageDialog = dojo.widget.createWidget( "dialog", createPageParams, this.createPageDialog );
			this.createPageDialog.setCloseControl( this.createPageDialog.createPageCancel.domNode );
            
            jetspeed.widget.PageEditPane.superclass.fillInTemplate.call( this );
		},
        destroy: function()
        {
            if ( this.deletePageDialog != null )
                this.deletePageDialog.destroy();
            if ( this.createPageDialog != null )
                this.createPageDialog.destroy();
            jetspeed.widget.PageEditPane.superclass.destroy.apply( this, arguments );
        },

        postCreate: function( args, fragment, parent )
        {
            jetspeed.widget.PageEditPane.superclass.postCreate.apply( this, arguments );

            if ( ! jetspeed.UAie )
            {   /* in IE6, if fieldset background color is set the fieldset will not be rendered nicely (with rounded borders) */
                if ( this.pageEditContainer != null )
                    this.pageEditContainer.style.backgroundColor = "#d3d3d3";
                if ( this.pageEditLDContainer != null )
                    this.pageEditLDContainer.style.backgroundColor = "#eeeeee";
                if ( this.pageEditPDContainer != null )
                    this.pageEditPDContainer.style.backgroundColor = "#eeeeee";
            }

            if ( this.layoutDecoratorSelect != null )
            {    
                var currentLayoutDecorator = jetspeed.page.layoutDecorator;
    
                var layoutDecoratorData = [];
                if ( this.layoutDecoratorDefinitions )
                {
                    for ( var i = 0 ; i < this.layoutDecoratorDefinitions.length ; i++ )
                    {
                        var layoutDecoratorDef = this.layoutDecoratorDefinitions[i];
                        if ( layoutDecoratorDef && layoutDecoratorDef.length == 2 )
                        {
                            layoutDecoratorData.push( [layoutDecoratorDef[0], layoutDecoratorDef[1]] );
                            if ( currentLayoutDecorator == layoutDecoratorDef[1] )
                            {
                                this.layoutDecoratorSelect.setAllValues( layoutDecoratorDef[0], layoutDecoratorDef[1] );
                            }
    					}
    				}
                }
                this.layoutDecoratorSelect.dataProvider.setData( layoutDecoratorData );
            }

            if ( this.portletDecoratorSelect != null )
            {    
                var currentPortletDecorator = jetspeed.page.portletDecorator;
    
                var portletDecoratorData = [];
                if ( this.portletDecoratorDefinitions )
                {
                    for ( var i = 0 ; i < this.portletDecoratorDefinitions.length ; i++ )
                    {
                        var portletDecoratorDef = this.portletDecoratorDefinitions[i];
                        if ( portletDecoratorDef && portletDecoratorDef.length == 2 )
                        {
                            portletDecoratorData.push( [portletDecoratorDef[0], portletDecoratorDef[1]] );
                            if ( currentPortletDecorator == portletDecoratorDef[1] )
                            {
                                this.portletDecoratorSelect.setAllValues( portletDecoratorDef[0], portletDecoratorDef[1] );
                            }
    					}
    				}
                }
                this.portletDecoratorSelect.dataProvider.setData( portletDecoratorData );
            }
        },


        // methods

        deletePage: function()
        {
            this.deletePageDialog.show();
        },
        deletePageConfirmed: function()
        {
            var removePageContentManager = new jetspeed.widget.RemovePageContentManager( this.pageEditorWidget );
            removePageContentManager.getContent();
        },
        createPage: function()
        {
            this.createPageDialog.show();
        },
        createPageConfirmed: function( pageName, pageTitle, pageShortTitle )
        {
            if ( pageName != null && pageName.length > 0 )
            {
                var pageRealPath = jetspeed.page.getPageDirectory( true ) + pageName;
                var pagePath = jetspeed.page.getPageDirectory() + pageName;
                var addPageContentManager = new jetspeed.widget.AddPageContentManager( pageRealPath, pagePath, pageName, null, pageTitle, pageShortTitle, this.pageEditorWidget );
                addPageContentManager.getContent();
            }
        },
        changeLayoutDecorator: function()
        {
            var updatePageInfoContentManager = new jetspeed.widget.UpdatePageInfoContentManager( this.layoutDecoratorSelect.getValue(), null, this.pageEditorWidget );
            updatePageInfoContentManager.getContent();
        },
        changePortletDecorator: function()
        {
            var updatePageInfoContentManager = new jetspeed.widget.UpdatePageInfoContentManager( null, this.portletDecoratorSelect.getValue(), this.pageEditorWidget );
            updatePageInfoContentManager.getContent();
        },
        editModeRedisplay: function()
        {
            this.show();
        }
	}
);
