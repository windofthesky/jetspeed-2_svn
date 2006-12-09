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
        deletePageDialog: null,
		deletePageDialogBg: null,
		deletePageDialogFg: null,

        createPageDialog: null,
		createPageDialogBg: null,
		createPageDialogFg: null,

        detail: null,

        
        // fields
		isContainer: true,
        widgetsInTemplate: true,


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
			dojo.html.setOpacity( this.deletePageDialogBg, 0.8 );
			dojo.html.setOpacity( this.deletePageDialogFg, 1 );

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
			dojo.html.setOpacity( this.createPageDialogBg, 0.8 );
			dojo.html.setOpacity( this.createPageDialogFg, 1 );
            
            jetspeed.widget.PageEditPane.superclass.fillInTemplate.call( this );
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
                var pagePath = jetspeed.page.getPageDirectory() + pageName;
                var addPageContentManager = new jetspeed.widget.AddPageContentManager( pagePath, pageName, null, pageTitle, pageShortTitle, this.pageEditorWidget );
                addPageContentManager.getContent();
            }
        },
        addPortlet: function()
        {
            jetspeed.page.addPortletInitiate();
        }
	}
);
