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

        layoutColumn: null,
        layoutInfo: null,
        parentLayoutInfo: null,

        // template parameters
        pageEditContainer: null,
        pageEditLNContainer: null,
        layoutNameSelect: null,
        buttonGroupRight: null,
        deleteLayoutButton: null,
        editMoveModeButton: null,
        editMoveModeExitButton: null,
        layoutMoveContainer: null,

        // fields
		isContainer: true,
        widgetsInTemplate: true,
        isLayoutPane: true,

        // drag variables
        drag: null,
        posStatic: true,

        // move modes
        moveModeLayoutRelative: "movemode_layout",
        moveModes: [ "movemode_layout", "movemode_portlet" ],


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
            var djObj = dojo;
            var djH = djObj.html;

            var pageEditorProto = jetspeed.widget.PageEditor.prototype;
            if ( this.pageEditContainer != null )
                djH.addClass( this.pageEditContainer, pageEditorProto.styleBaseAdd );
            if ( this.pageEditLNContainer != null )
                djH.addClass( this.pageEditLNContainer, pageEditorProto.styleDetailAdd );

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
            this.syncButtons();
            
            this.layoutMoveContainer = djObj.widget.createWidget( "jetspeed:LayoutEditPaneMoveHandle",
				{
					layoutImagesRoot: this.layoutImagesRoot
				});
			this.addChild( this.layoutMoveContainer );
			this.domNode.appendChild( this.layoutMoveContainer.domNode );
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
            jspage = jetspeed.url.addQueryParameter( jspage, jetspeed.id.PG_ED_PARAM, "true", true );
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
        editMoveModeExit: function()
        {
            this.pageEditorWidget.editMoveModeExit();
            if ( this.editMoveModeButton != null )
                this.editMoveModeButton.domNode.style.display = "block";
            if ( this.editMoveModeExitButton != null )
                this.editMoveModeExitButton.domNode.style.display = "none";
        },
        editMoveModeStart: function()
        {
            this.pageEditorWidget.editMoveModeStart();
            if ( this.editMoveModeButton != null )
                this.editMoveModeButton.domNode.style.display = "none";
            if ( this.editMoveModeExitButton != null )
                this.editMoveModeExitButton.domNode.style.display = "block";
        },

        _enableMoveMode: function()
        {
            if ( this.layoutMoveContainer && this.drag )
            {
                this.layoutMoveContainer.domNode.style.display = "block";
            }
        },
        _disableMoveMode: function()
        {
            if ( this.layoutMoveContainer && this.drag )
            {
                this.layoutMoveContainer.domNode.style.display = "none";
            }
        },

        initializeDrag: function()
        {
            var layoutCol = this.layoutColumn;
            if ( layoutCol != null && layoutCol.domNode != null )
            {
                this.dragStartStaticWidth = layoutCol.domNode.style.width;
                this.drag = new dojo.dnd.Moveable( this, {handle: this.layoutMoveContainer.domNode });
            }
        },
        
        startDragging: function( e, moveableObj, djObj, jsObj )
        {
            var dragLayoutColumn = this.layoutColumn;
            if ( dragLayoutColumn != null )
            {
                var dragNode = dragLayoutColumn.domNode;
                if ( dragNode )
                {
                    if ( this.buttonGroupRight )
                        this.buttonGroupRight.style.display = "none";
                    var notifyOnAbsolute = true;
                    moveableObj.beforeDragColRowInfo = jsObj.page.getPortletCurColRow( dragNode );
                    moveableObj.node = dragNode;
		            moveableObj.mover = new djObj.dnd.Mover( this, dragNode, dragLayoutColumn, moveableObj, e, notifyOnAbsolute, djObj, jsObj );
                }
            }
        },

        dragChangeToAbsolute: function( moverObj, layoutColNode, mbLayoutColNode, djObj, jsObj )
        {
            var mbLayoutColNodeFresh = djObj.getMarginBox( layoutColNode, null, jsObj );
            //dojo.debug( "dragChangeToAbsolute - passed-mb=" + jsObj.printobj( mbLayoutColNode ) + "  fresh-mb=" + jsObj.printobj( mbLayoutColNodeFresh ) );
            var reduceWidth = 400 - mbLayoutColNode.w;
            if ( reduceWidth < 0 )
            {
                mbLayoutColNode.l = mbLayoutColNode.l + ( reduceWidth * -1 );  // ( mbLayoutColNode.w + reduceWidth );
                mbLayoutColNode.w = 400;
                djObj.setMarginBox( layoutColNode, mbLayoutColNode.l, null, mbLayoutColNode.w, null, null, jsObj );
            }
            
            if ( jsObj.UAie )
            {
                var bgIframeNode = this.pageEditorWidget.bgIframe.iframe;
                this.domNode.appendChild( bgIframeNode );
                bgIframeNode.style.display = "block";
                djObj.setMarginBox( bgIframeNode, null, null, null, mbLayoutColNode.h, null, jsObj );
                //djObj.debug( "layout bgIframe mb: " + jsObj.printobj( djObj.getMarginBox( bgIframeNode, null, jsObj ) ) );
            }
        },

        endDragging: function( posObj )
        {
            var jsObj = jetspeed;
            var djObj = dojo;
            var layoutCol = this.layoutColumn;
            if ( this.drag == null || layoutCol == null || layoutCol.domNode == null ) return;
            var dNode = layoutCol.domNode;
            dNode.style.position = "static";
            dNode.style.width = this.dragStartStaticWidth;
            dNode.style.left = "auto";
            dNode.style.top = "auto";

            if ( this.buttonGroupRight )
                this.buttonGroupRight.style.display = "block";

            if ( jsObj.UAie )
            {
                this.pageEditorWidget.bgIframe.iframe.style.display = "none";
                if ( jsObj.UAie6 )
                    jsObj.page.onBrowserWindowResize();   // force resize of descendent portlet windows
            }

            var beforeDragColRowInfo = this.drag.beforeDragColRowInfo;
            var afterDragColRowInfo = jsObj.page.getPortletCurColRow( dNode );
            if ( beforeDragColRowInfo != null && afterDragColRowInfo != null )
            {
                var ind = jsObj.debugindent;
                //djObj.hostenv.println( "move-layout[" + this.layoutId + " / " + dNode.id + "]" );
                //djObj.hostenv.println( ind + "before (col=" + beforeDragColRowInfo.column + " row=" + beforeDragColRowInfo.row + " layout=" + beforeDragColRowInfo.layout + ")" );
                //djObj.hostenv.println( ind + "before-" + jetspeed.debugColumn( beforeDragColRowInfo.columnObj, true ) );
                //djObj.hostenv.println( ind + "after (col=" + afterDragColRowInfo.column + " row=" + afterDragColRowInfo.row + " layout=" + afterDragColRowInfo.layout + ")" );
                //djObj.hostenv.println( ind + "after-" + jetspeed.debugColumn( afterDragColRowInfo.columnObj, true ) );

                if ( afterDragColRowInfo != null && ( afterDragColRowInfo.row != beforeDragColRowInfo.row || afterDragColRowInfo.column != beforeDragColRowInfo.column || afterDragColRowInfo.layout != beforeDragColRowInfo.layout ) )
                {
                    var moveLayoutContentManager = new jsObj.widget.MoveLayoutContentManager( this.layoutId, afterDragColRowInfo.layout, afterDragColRowInfo.column, afterDragColRowInfo.row, this.pageEditorWidget );
                    moveLayoutContentManager.getContent();
                }
            }
        },

        getLayoutColumn: function()
        {
            return this.layoutColumn;
        },
        getPageColumnIndex: function()
        {
            if ( this.layoutColumn )
            {
                var parentColObj = jetspeed.page.getColWithNode( this.layoutColumn.domNode );
                if ( parentColObj != null )
                    return parentColObj.getPageColumnIndex();
            }
            return null;
        },
        _getLayoutInfoMoveable: function()
        {
            return this.layoutInfo;
        },
        _getWindowMarginBox: function( layoutColumnLayoutInfo, jsObj )
        {
            if ( this.layoutColumn )
            {
                var parentLayoutInfo = this.parentLayoutInfo;
                if ( jsObj.UAope && parentLayoutInfo == null )  // needs parentNode layout-info 
                {
                    var pageLayoutInfo = jsObj.page.layoutInfo;
                    var parentColIndex = jsObj.page.getColIndexForNode( this.layoutColumn.domNode );
                    if ( parentColIndex != null )
                    {
                        var parentCol = jsObj.page.columns[parentColIndex];
                        if ( parentCol.layoutHeader )
                            parentLayoutInfo = pageLayoutInfo.columnLayoutHeader;
                        else
                            parentLayoutInfo = pageLayoutInfo.column;
                    }
                    else
                    {
                        parentLayoutInfo = pageLayoutInfo.columns;
                    }
                    this.parentLayoutInfo = parentLayoutInfo;
                }
                return jsObj.ui.getMarginBox( this.layoutColumn.domNode, layoutColumnLayoutInfo, parentLayoutInfo, jsObj );
            }
            return null;
        },

        editModeRedisplay: function()
        {
            this.show();
            this.syncButtons();
        },
        syncButtons: function()
        {
            if ( this.isRootLayout )
            {
                if ( this.deleteLayoutButton != null )
                    this.deleteLayoutButton.domNode.style.display = "none";
                if ( this.editMoveModeButton != null )
                    this.editMoveModeButton.domNode.style.display = "block";
                if ( this.editMoveModeExitButton != null )
                    this.editMoveModeExitButton.domNode.style.display = "none";
            }
            else
            {
                if ( this.editMoveModeButton != null )
                    this.editMoveModeButton.domNode.style.display = "none";
                if ( this.editMoveModeExitButton != null )
                    this.editMoveModeExitButton.domNode.style.display = "none";
            }
        },

        onBrowserWindowResize: function()
        {   // called after ie6 resize window
            // nothing to do here
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
