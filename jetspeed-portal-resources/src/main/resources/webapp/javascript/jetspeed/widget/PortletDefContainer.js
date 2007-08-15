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

dojo.provide("jetspeed.widget.PortletDefContainer");
dojo.provide("jetspeed.widget.PortletDefContainerItem");

dojo.require("jetspeed.desktop.core");
dojo.require("dojo.widget.*");
dojo.require("dojo.widget.FloatingPane");

jetspeed.widget.PortletDefContainer = function()
{
    this.widgetType = "PortletDefContainer";

    dojo.widget.ContentPane.call( this );
};

dojo.inherits(jetspeed.widget.PortletDefContainer, dojo.widget.ContentPane);

dojo.lang.extend(jetspeed.widget.PortletDefContainer, {
    // dojo.widget.Widget create protocol
    postMixInProperties: function( args, fragment, parentComp )
    {
        this.templatePath = new dojo.uri.dojoUri( jetspeed.prefs.getPortletDecorationBaseUrl( jetspeed.page.getPortletDecorationDefault() ) + "/templates/PortletDefContainer.html");
        this.templateCssPath = new dojo.uri.Uri( jetspeed.prefs.getPortletDecorationBaseUrl( jetspeed.page.getPortletDecorationDefault() ) + "/css/PortletDefContainer.css" );

        jetspeed.widget.PortletDefContainer.superclass.postMixInProperties.call( this );

        var pdcProps = {};
        pdcProps.templateCssPath = new dojo.uri.Uri( jetspeed.prefs.getPortletDecorationBaseUrl( jetspeed.page.getPortletDecorationDefault() ) + "/css/PortletDefContainer.css" ) ;
        pdcProps.templatePath = new dojo.uri.Uri( jetspeed.prefs.getPortletDecorationBaseUrl( jetspeed.page.getPortletDecorationDefault() ) + "/templates/PortletDefContainerItemTemplate.html" ) ;

        this.portletDefContainerProps = pdcProps ;
    },

    // dojo.widget.Widget create protocol
    postCreate: function( args, fragment, parentComp )
    {
        jetspeed.widget.PortletDefContainer.superclass.postCreate.call( this );
        
        if ( ! this.domNode.id )
            this.domNode.id = this.widgetId;
    },

    addChild: function( child )
    {
        var pdcProps = {portletDef: child, caption: child.getPortletDisplayName(), iconSrc: "javascript/desktop/windowicons/document.gif" } ;
        dojo.lang.mixin(pdcProps, this.portletDefContainerProps);
        var pdc = dojo.widget.createWidget("jetspeed:PortletDefContainerItem", pdcProps);
        jetspeed.widget.PortletDefContainer.superclass.addChild.call(this,pdc);
    }
});


dojo.widget.defineWidget(
	"jetspeed.widget.PortletDefContainerItem",
	dojo.widget.TaskBarItem,
    {
        initializer: function()
        {
            this.widgetType = "PortletDefContainerItem";
		},
        onClick: function()
        {
	    },
        fillInTemplate: function()
        {
            var imgNode = this.itemIcon;
            if ( imgNode )
            {
                imgNode.src = this.iconSrc;
            }
            var textNode = this.itemText;
            textNode.appendChild( document.createTextNode( this.caption ) );
            dojo.html.disableSelection( this.domNode );
	    },
        /*
        postCreate: function()
        {
            var dragSource = new dojo.dnd.HtmlDragSource(this.domNode, "PortletDef" );
            //dragSource.constrainingContainer = dojo.byId( jetspeed.id.DESKTOP );
            //dragSource.constrainToContainer = true;

            //javascript: alert( dojo.html.getBorderBox( dojo.byId( "jetspeedDesktop" ) ).height )
            //javascript: jetspeed.loadPortletSelector()
            var dropTarget = new dojo.dnd.HtmlDropTarget( dojo.byId( jetspeed.id.DESKTOP ), ["PortletDef"] );
            var pdItemWidget = this ;
            dojo.event.connect( dragSource, "onDragEnd", function(e) {
                dojo.debug( "PortletDefContainerItem onDragEnd status=" + e.dragStatus );
            });
        }
        */
        postCreate: function()
        {
            var dragSource = new jetspeed.widget.PortletDefDragSource( this.portletDef, this );
            //var dropTarget = new dojo.dnd.HtmlDropTarget( dojo.byId( jetspeed.id.DESKTOP ), ["PortletDef"] );
            var pdItemWidget = this ;
            dojo.event.connect( dragSource, "onDragEnd", function(e) {
                dojo.debug( "PortletDefContainerItem onDragEnd status=" + e.dragStatus );
            });
        }
	}
);

//javascript: alert( dojo.html.getBorderBox( dojo.byId( "jetspeedDesktop" ) ).height )
//javascript: jetspeed.loadPortletSelector()


jetspeed.widget.PortletDefDragSource = function( /* jetspeed.om.PortletDef */ portletDef, /* PortletDefContainerItem */ pdcItem )
{
    this.portletDef = portletDef;
	dojo.dnd.HtmlDragMoveSource.call(this, pdcItem.domNode, "PortletDef");
};

dojo.inherits( jetspeed.widget.PortletDefDragSource, dojo.dnd.HtmlDragMoveSource );

dojo.lang.extend( jetspeed.widget.PortletDefDragSource, {
	onDragStart: function()
    {
        this.windowWidget = this.createPortletWindow( this.portletDef );

        this.windowWidget.makeFreeFloating( this.dragObject );
        if ( jetspeed.prefs.windowTiling )
            this.windowWidget.windowPositionStatic = true;   // BOZO: shouldn't happen this way!

        var dragObj = new jetspeed.widget.PortletWindowDragMoveObject( this.windowWidget, this.windowWidget.domNode, this.type );

        jetspeed.addNewPortletDefinition( this.portletDef, this.windowWidget.widgetId );

		return dragObj;
	},
    createPortletWindow: function( /* jetspeed.om.PortletDef */ portletDef )
    {
        var baseWidgetId = jetspeed.id.PORTLET_WINDOW_ID_PREFIX + jetspeed.purifyIdentifier( this.portletDef.getPortletName() );
        var uniquePart = 1; 
        var widgetId = null;
        while ( widgetId == null )
        {
            widgetId = baseWidgetId + "_" + uniquePart;
            if ( dojo.widget.byId( widgetId ) != null )
            {
                widgetId = null;
                uniquePart++;
            }
        }

        var windowParams = {};
        windowParams[ jetspeed.id.PORTLET_PROP_WINDOW_POSITION_STATIC ] = true;
        windowParams[ jetspeed.id.PORTLET_PROP_WINDOW_DECORATION ] = jetspeed.page.getPortletDecorationDefault();
        windowParams[ jetspeed.id.PORTLET_PROP_WINDOW_TITLE ] = this.portletDef.getPortletDisplayName();
        windowParams[ jetspeed.id.PORTLET_PROP_WIDGET_ID ] = widgetId;
        windowParams[ jetspeed.id.PORTLET_PROP_WIDTH ] = jetspeed.prefs.windowWidth;
        windowParams[ jetspeed.id.PORTLET_PROP_HEIGHT ] = jetspeed.prefs.windowHeight;
        windowParams[ jetspeed.id.PORTLET_PROP_EXCLUDE_PCONTENT ] = false;
        windowParams[ jetspeed.id.PORTLET_PROP_CONTENT_RETRIEVER ] = null;
        var pwWidgetParams = jetspeed.widget.PortletWindow.prototype.staticDefineAsAltInitParameters( null, windowParams );
        jetspeed.ui.createPortletWindow( pwWidgetParams, null, null );


        return dojo.widget.byId( windowParams[ jetspeed.id.PORTLET_PROP_WIDGET_ID ] ) ;
    },
    onDragEnd: function()
    {
        
    }
});


jetspeed.widget.PortletDefDragObject = function(/* jetspeed.om.PortletDef */ portletDef, node, type)
{
    this.portletDef = portletDef;
    dojo.dnd.HtmlDragObject.call( this, node, type );
}

dojo.inherits(jetspeed.widget.PortletDefDragObject, dojo.dnd.HtmlDragObject);

dojo.lang.extend(jetspeed.widget.PortletDefDragObject, {
    createDragNode: function() {

        var windowParams = {};
        windowParams[ jetspeed.id.PORTLET_PROP_WINDOW_POSITION_STATIC ] = false;
        windowParams[ jetspeed.id.PORTLET_PROP_WINDOW_DECORATION ] = "tigris";
        windowParams[ jetspeed.id.PORTLET_PROP_WINDOW_TITLE ] = this.portletDef.getPortletDisplayName();
        windowParams[ jetspeed.id.PORTLET_PROP_WIDGET_ID ] = jetspeed.id.PORTLET_WINDOW_ID_PREFIX + "bleep";
        windowParams[ jetspeed.id.PORTLET_PROP_WIDTH ] = jetspeed.prefs.windowWidth;
        windowParams[ jetspeed.id.PORTLET_PROP_HEIGHT ] = jetspeed.prefs.windowHeight;
        windowParams[ jetspeed.id.PORTLET_PROP_EXCLUDE_PCONTENT ] = false;
        windowParams[ jetspeed.id.PORTLET_PROP_CONTENT_RETRIEVER ] = null;
        var pwWidgetParams = jetspeed.widget.PortletWindow.prototype.staticDefineAsAltInitParameters( null, windowParams );
        jetspeed.ui.createPortletWindow( pwWidgetParams, null, null );


        var newWidget = dojo.widget.byId( windowParams[ jetspeed.id.PORTLET_PROP_WIDGET_ID ] ) ;
        
		var node = newWidget.domNode; // this.domNode.cloneNode(true);
		if(this.dragClass) { dojo.html.addClass(node, this.dragClass); }
		if(this.opacity < 1) { dojo.html.setOpacity(node, this.opacity); }
		if(dojo.render.html.ie && this.createIframe){
			with(node.style) {
				top="0px";
				left="0px";
			}
			var outer = document.createElement("div");
			outer.appendChild(node);
			this.bgIframe = new dojo.html.BackgroundIframe(outer);
			outer.appendChild(this.bgIframe.iframe);
			node = outer;
		}
		node.style.zIndex = 999;
		return node;
	}
    
});
