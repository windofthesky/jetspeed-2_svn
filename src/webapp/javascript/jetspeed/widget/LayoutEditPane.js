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

dojo.provide("jetspeed.widget.LayoutEditPane");

dojo.require("dojo.widget.*");
dojo.require("dojo.io.*");
dojo.require("dojo.event.*");
dojo.require("dojo.widget.HtmlWidget");
dojo.require("dojo.widget.Dialog");
dojo.require("dojo.widget.Select");
dojo.require("dojo.widget.Button");

dojo.require("dojo.html.common");
dojo.require("dojo.html.display");

jetspeed.widget.LayoutEditPane = function()
{
}

dojo.widget.defineWidget(
	"jetspeed.widget.LayoutEditPane",
	dojo.widget.HtmlWidget,
	{
        // variables
        layoutId: null,
        layoutDefinitions: null,

        // template parameters
        detail: null,
        layoutNameSelect: null,

        
        // fields
		isContainer: true,
        widgetsInTemplate: true,


        // protocol - dojo.widget.Widget create

        postMixInProperties: function( args, fragment, parent )
        {
            jetspeed.widget.LayoutEditPane.superclass.postMixInProperties.apply( this, arguments );

            this.templateCssPath = new dojo.uri.Uri( jetspeed.url.basePortalDesktopUrl() + "/javascript/jetspeed/widget/LayoutEditPane.css" ) ;
            this.templatePath = new dojo.uri.Uri( jetspeed.url.basePortalDesktopUrl() + "/javascript/jetspeed/widget/LayoutEditPane.html" ) ;
        },

		fillInTemplate: function( args, fragment )
        {
            jetspeed.widget.LayoutEditPane.superclass.fillInTemplate.call( this );

            //dojo.debug( "building LayoutEditPane for " + this.layoutId + " with layoutDefinitions: " + this.layoutDefinitions );
		},

        postCreate: function( args, fragment, parent )
        {
            if ( this.layoutNameSelect != null )
            {
                var currentLayout = null;
                if ( this.layoutId != null )
                    currentLayout = jetspeed.page.layouts[ this.layoutId ];
    
                var currentLayoutName = null;
                if ( currentLayout != null )
                    currentLayoutName = currentLayout.name;
    
                var layoutNameData = [];
                if ( this.layoutDefinitions )
                {
                    for ( var i = 0 ; i < this.layoutDefinitions.length ; i++ )
                    {
                        var layoutDef = this.layoutDefinitions[i];
                        if ( layoutDef && layoutDef.length == 2 )
                        {
                            layoutNameData.push( [layoutDef[0], layoutDef[1]] );
                            if ( currentLayoutName == layoutDef[1] )
                            {
                                this.layoutNameSelect.setAllValues( layoutDef[0], layoutDef[1] );
                            }
    					}
    				}
                }
                this.layoutNameSelect.dataProvider.setData( layoutNameData );
            }
        },


        // methods

        changeLayout: function()
        {
            var updateFragmentContentManager = new jetspeed.widget.UpdateFragmentContentManager( this.layoutId, this.layoutNameSelect.getValue(), null, this.pageEditorWidget );
            updateFragmentContentManager.getContent();
        },
        openColumnSizeEditor: function()
        {
            this.pageEditorWidget.openColumnSizesEditor( this.layoutId );
        },
        addPortlet: function()
        {
            jetspeed.page.addPortletInitiate( this.layoutId );
        }
	}
);
