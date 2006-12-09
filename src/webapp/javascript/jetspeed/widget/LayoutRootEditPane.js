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

dojo.provide("jetspeed.widget.LayoutRootEditPane");

dojo.require("dojo.widget.*");
dojo.require("dojo.io.*");
dojo.require("dojo.event.*");
dojo.require("dojo.widget.HtmlWidget");
dojo.require("dojo.widget.Dialog");
dojo.require("dojo.widget.Select");
dojo.require("dojo.widget.Button");

dojo.require("dojo.html.common");
dojo.require("dojo.html.display");

dojo.require("jetspeed.widget.LayoutEditPane");

jetspeed.widget.LayoutRootEditPane = function()
{
}

dojo.widget.defineWidget(
	"jetspeed.widget.LayoutRootEditPane",
	jetspeed.widget.LayoutEditPane,
	{
        // variables
        layoutDecoratorDefinitions: null,

        // template parameters
        layoutDecoratorSelect: null,
        portletDecoratorSelect: null,
        desktopThemeSelect: null,
        
        // fields



        // protocol - dojo.widget.Widget create

        postMixInProperties: function( args, fragment, parent )
        {
            jetspeed.widget.LayoutRootEditPane.superclass.postMixInProperties.apply( this, arguments );

            this.templateCssPath = new dojo.uri.Uri( jetspeed.url.basePortalDesktopUrl() + "/javascript/jetspeed/widget/LayoutRootEditPane.css" ) ;
            this.templatePath = new dojo.uri.Uri( jetspeed.url.basePortalDesktopUrl() + "/javascript/jetspeed/widget/LayoutRootEditPane.html" ) ;
        },

		fillInTemplate: function( args, fragment )
        {
            jetspeed.widget.LayoutRootEditPane.superclass.fillInTemplate.call( this );

            //dojo.debug( "building LayoutRootEditPane for " + this.layoutId + " with layoutDefinitions: " + this.layoutDefinitions );
		},

        postCreate: function( args, fragment, parent )
        {
            jetspeed.widget.LayoutRootEditPane.superclass.postCreate.apply( this, arguments );

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

            if ( this.desktopThemeSelect != null )
            {    
                var currentDesktopTheme = jetspeed.prefs.getDesktopTheme();
    
                var desktopThemeData = [];
                if ( this.desktopThemeDefinitions )
                {
                    for ( var i = 0 ; i < this.desktopThemeDefinitions.length ; i++ )
                    {
                        var desktopThemeDef = this.desktopThemeDefinitions[i];
                        if ( desktopThemeDef && desktopThemeDef.length == 2 )
                        {
                            desktopThemeData.push( [desktopThemeDef[0], desktopThemeDef[1]] );
                            if ( currentDesktopTheme == desktopThemeDef[1] )
                            {
                                this.desktopThemeSelect.setAllValues( desktopThemeDef[0], desktopThemeDef[1] );
                            }
    					}
    				}
                }
                this.desktopThemeSelect.dataProvider.setData( desktopThemeData );
            }
        },


        // methods

        changeLayoutDecorator: function()
        {
            var updatePageInfoContentManager = new jetspeed.widget.UpdatePageInfoContentManager( this.layoutDecoratorSelect.getValue(), null, null, this.pageEditorWidget );
            updatePageInfoContentManager.getContent();
        },
        changePortletDecorator: function()
        {
            var updatePageInfoContentManager = new jetspeed.widget.UpdatePageInfoContentManager( null, this.portletDecoratorSelect.getValue(), null, this.pageEditorWidget );
            updatePageInfoContentManager.getContent();
        },
        changeDesktopTheme: function()
        {
            var updatePageInfoContentManager = new jetspeed.widget.UpdatePageInfoContentManager( null, null, this.desktopThemeSelect.getValue(), this.pageEditorWidget );
            updatePageInfoContentManager.getContent();
        }
	}
);
