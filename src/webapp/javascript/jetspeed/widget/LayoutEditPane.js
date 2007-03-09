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

dojo.provide("jetspeed.widget.LayoutEditPane");
dojo.provide("jetspeed.widget.LayoutEditPaneMoveHandle");

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
        pageEditContainer: null,
        pageEditLNContainer: null,
        layoutNameSelect: null,
        deleteLayoutButton: null,
        editModeLayoutMoveButton: null,
        editModeNormalButton: null,
        layoutMoveContainer: null,
        
        // fields
		isContainer: true,
        widgetsInTemplate: true,

        // drag variables
        containingColumn: null,
        windowPositionStatic: true,

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

        getCurrentLayout: function()
        {
            var currentLayout = null;
            if ( this.layoutId != null )
                currentLayout = jetspeed.page.layouts[ this.layoutId ];
            return currentLayout;
        },

        postCreate: function( args, fragment, parent )
        {
            if ( ! jetspeed.browser_IE )
            {   /* in IE6, if fieldset background color is set the fieldset will not be rendered nicely (with rounded borders) */
                if ( this.pageEditContainer != null )
                    this.pageEditContainer.style.backgroundColor = "#d3d3d3";
                if ( this.pageEditLNContainer != null )
                    this.pageEditLNContainer.style.backgroundColor = "#eeeeee";
            }

            if ( this.layoutNameSelect != null )
            {
                var currentLayout = this.getCurrentLayout();

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
            if ( this.isRootLayout )
            {
                if ( this.deleteLayoutButton != null )
                    this.deleteLayoutButton.domNode.style.display = "none";
                if ( this.editModeLayoutMoveButton != null )
                    this.editModeLayoutMoveButton.domNode.style.display = "block";
                if ( this.editModeNormalButton != null )
                    this.editModeNormalButton.domNode.style.display = "none";
            }
            else
            {
                if ( this.editModeLayoutMoveButton != null )
                    this.editModeLayoutMoveButton.domNode.style.display = "none";
                if ( this.editModeNormalButton != null )
                    this.editModeNormalButton.domNode.style.display = "none";
            }
            
            this.layoutMoveContainer = dojo.widget.createWidget( "jetspeed:LayoutEditPaneMoveHandle",
				{
					layoutImagesRoot: this.layoutImagesRoot
				});
			this.addChild( this.layoutMoveContainer );
			this.domNode.appendChild( this.layoutMoveContainer.domNode );
        },

        initializeDrag: function()
        {
            this.containingColumn = this.getContainingColumn();
            this.drag = new jetspeed.widget.LayoutDragMoveSource( this );
            this.drag.setDragHandle( this.layoutMoveContainer.domNode );
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
            var jspage = jetspeed.page.getPagePathAndQuery();
            jspage = jetspeed.url.addQueryParameter( jspage, "editPage", "true", true );
            jetspeed.page.addPortletInitiate( this.layoutId, jspage.toString() );
        },
        addLayout: function()
        {
            var currentLayout = this.getCurrentLayout();
            if ( currentLayout != null )
            {
                var addLayoutContentManager = new jetspeed.widget.AddLayoutContentManager( this.layoutId, currentLayout.name, this.pageEditorWidget );
                addLayoutContentManager.getContent();
            }
            else
            {
                alert( "Cannot add layout (error: null parent layout)." );
            }
        },
        deleteLayout: function()
        {
            this.pageEditorWidget.deleteLayout( this.layoutId );
        },
        editModeNormal: function()
        {
            this.pageEditorWidget.editModeNormal();
            if ( this.editModeLayoutMoveButton != null )
                this.editModeLayoutMoveButton.domNode.style.display = "block";
            if ( this.editModeNormalButton != null )
                this.editModeNormalButton.domNode.style.display = "none";
        },
        editModeLayoutMove: function()
        {
            this.pageEditorWidget.editModeLayoutMove();
            if ( this.editModeLayoutMoveButton != null )
                this.editModeLayoutMoveButton.domNode.style.display = "none";
            if ( this.editModeNormalButton != null )
                this.editModeNormalButton.domNode.style.display = "block";
        },

        endDragging: function()
        {
            if ( this.drag == null || this.containingColumn == null || this.containingColumn.domNode == null ) return;
            var beforeDragColumnRowInfo = this.drag.beforeDragColumnRowInfo;
            //dojo.debug( "layout (" + this.layoutId + " / " + this.widgetId + ") endDragging (a) : before " + jetspeed.printobj( beforeDragColumnRowInfo ) );
            if ( beforeDragColumnRowInfo != null )
            {
                var afterDragColumnRowInfo = jetspeed.page.getPortletCurrentColumnRow( this.containingColumn.domNode );
                //dojo.debug( "layout (" + this.layoutId + ") endDragging (b) : after " + jetspeed.printobj( afterDragColumnRowInfo ) );
                if ( afterDragColumnRowInfo != null && ( afterDragColumnRowInfo.row != beforeDragColumnRowInfo.row || afterDragColumnRowInfo.column != beforeDragColumnRowInfo.column || afterDragColumnRowInfo.layout != beforeDragColumnRowInfo.layout ) )
                {
                    //dojo.debug( "layout (" + this.layoutId + ") endDragging (c)" );
                    var moveLayoutContentManager = new jetspeed.widget.MoveLayoutContentManager( this.layoutId, afterDragColumnRowInfo.layout, afterDragColumnRowInfo.column, afterDragColumnRowInfo.row, this.pageEditorWidget );
                    moveLayoutContentManager.getContent();
                }
            }
        },

        getContainingColumn: function()
        {
            return jetspeed.page.getColumnContainingNode( this.domNode );
        },
        getPageColumnIndex: function()
        {
            return jetspeed.page.getColumnIndexContainingNode( this.domNode );
        }
	}
);

dojo.widget.defineWidget(  
    "jetspeed.widget.LayoutEditPaneMoveHandle",
	dojo.widget.HtmlWidget,
{
	// summary
	//	Internal widget used by LayoutEditPane.

	templateString: '<span class="layoutMoveContainer"><img src="${this.layoutImagesRoot}layout_move.png"></span>'

});

jetspeed.widget.LayoutDragMoveSource = function( /* jetspeed.widget.LayoutEditPane */ layoutEditPane )
{
    this.layoutEditPane = layoutEditPane;
    this.beforeDragColumnRowInfo = null;
    this.beforeDragColumn = layoutEditPane.containingColumn;
    var dragNode = ( ( this.beforeDragColumn != null ) ? this.beforeDragColumn.domNode : null );
	dojo.dnd.HtmlDragMoveSource.call( this, dragNode );
};

dojo.inherits( jetspeed.widget.LayoutDragMoveSource, dojo.dnd.HtmlDragMoveSource );

dojo.lang.extend( jetspeed.widget.LayoutDragMoveSource, {
	onDragStart: function()
    {
        this.beforeDragColumnRowInfo = null;
        var dragMoveObj = null;
        if ( this.dragObject != null )
        {
            if ( this.beforeDragColumn != null && this.beforeDragColumn.domNode != null )
                this.beforeDragColumnRowInfo = jetspeed.page.getPortletCurrentColumnRow( this.beforeDragColumn.domNode );

//jetspeed.page.getColumnContainingNode(  );

            dragMoveObj = new jetspeed.widget.LayoutDragMoveObject( this.dragObject, this.type, this.layoutEditPane, this.beforeDragColumn );
            if ( this.constrainToContainer )
            {
                dragMoveObj.constrainTo( this.constrainingContainer );
            }
        }
		return dragMoveObj;
	},
    onDragEnd: function()
    {
    }
});

dojo.declare( "jetspeed.widget.LayoutDragMoveObject", jetspeed.widget.PortletWindowDragMoveObject, {
    qualifyTargetColumn: function( /* jetspeed.om.Column */ column )
    {
        if ( column == null ) return false;
        if ( this.disqualifiedColumnIndexes[ column.getPageColumnIndex() ] != null )
            return false;
        return true;
    }
    },    
    function( node, type, /* jetspeed.widget.LayoutEditPane */ layoutEditPane, /* jetspeed.om.Column */ beforeDragColumn )
    {
        this.layoutEditPane = layoutEditPane;
    
        this.disqualifiedColumnIndexes = beforeDragColumn.getDescendantColumns();
    }
);
