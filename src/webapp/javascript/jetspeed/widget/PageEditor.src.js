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

dojo.provide("jetspeed.widget.PageEditor");

dojo.require("dojo.widget.*");
dojo.require("dojo.io.*");
dojo.require("dojo.event.*");
dojo.require("dojo.string.extras");
dojo.require("dojo.widget.HtmlWidget");
dojo.require("dojo.widget.Dialog");
dojo.require("dojo.widget.Select");
dojo.require("dojo.widget.Button");
dojo.require("dojo.widget.Spinner");

dojo.require("dojo.html.common");
dojo.require("dojo.html.display");
dojo.require("jetspeed.widget.PageEditPane");
dojo.require("jetspeed.widget.LayoutEditPane");

jetspeed.widget.PageEditor = function()
{
}

dojo.widget.defineWidget(
	"jetspeed.widget.PageEditor",
	dojo.widget.HtmlWidget,
	{
        // template parameters
        deletePortletDialog: null,
		deletePortletDialogBg: null,
		deletePortletDialogFg: null,

        deleteLayoutDialog: null,
		deleteLayoutDialogBg: null,
		deleteLayoutDialogFg: null,

        columnSizeDialog: null,
		columnSizeDialogBg: null,
		columnSizeDialogFg: null,


        detail: null,

        
        // fields
        editorInitiatedFromDesktop: false,

		isContainer: true,
        widgetsInTemplate: true,


        // protocol - dojo.widget.Widget create

        postMixInProperties: function( args, fragment, parent )
        {
            jetspeed.widget.PageEditor.superclass.postMixInProperties.apply( this, arguments );

            this.templateCssPath = new dojo.uri.Uri( jetspeed.url.basePortalDesktopUrl() + "/javascript/jetspeed/widget/PageEditor.css" ) ;
            this.templatePath = new dojo.uri.Uri( jetspeed.url.basePortalDesktopUrl() + "/javascript/jetspeed/widget/PageEditor.html" ) ;
        },

        fillInTemplate: function( args, fragment )
        {
            var self = this;

            this.deletePortletDialog = dojo.widget.createWidget( "dialog", { widgetsInTemplate: true, deletePortletConfirmed: function() { this.hide(); self.deletePortletConfirmed( this.portletEntityId ); } }, this.deletePortletDialog );
			this.deletePortletDialog.setCloseControl( this.deletePortletDialog.deletePortletCancel.domNode );

            this.deleteLayoutDialog = dojo.widget.createWidget( "dialog", { widgetsInTemplate: true, deleteLayoutConfirmed: function() { this.hide(); self.deleteLayoutConfirmed( this.portletEntityId ); } }, this.deleteLayoutDialog );
			this.deleteLayoutDialog.setCloseControl( this.deleteLayoutDialog.deleteLayoutCancel.domNode );

            var columnSizeParams = {};
            columnSizeParams.widgetsInTemplate = true;
            columnSizeParams.columnSizeConfirmed = function()
            {
                var columnSizesSum = 0;
                var columnSizes = new Array();
                for ( var i = 0 ; i < this.columnCount; i++ )
                {
                    var spinnerWidget = this[ "spinner" + i ];
                    var colSize = new Number( spinnerWidget.getValue() );
                    columnSizes.push( colSize );
                    columnSizesSum += colSize;
                }

                if ( columnSizesSum > 100 )
                {
                    alert( "Sum of column sizes cannot exceed 100." );
                }
                else
                {
                    this.hide();
                    self.columnSizeConfirmed( this.layoutId, columnSizes );
                }
            };

            this.columnSizeDialog = dojo.widget.createWidget( "dialog", columnSizeParams, this.columnSizeDialog );
            this.columnSizeDialog.setCloseControl( this.columnSizeDialog.columnSizeCancel.domNode );

            jetspeed.widget.PageEditor.superclass.fillInTemplate.call( this );
		},

        postCreate: function( args, fragment, parent )
        {
            this.editPageInitiate();
        },

        // initialization
        editPageInitiate: function()
        {
            var themesContentManager = null;
            if ( this.editorInitiatedFromDesktop )
                themesContentManager = new jetspeed.widget.EditPageGetThemesContentManager( this, false, false, true, true, true );
            else
                themesContentManager = new jetspeed.widget.EditPageGetThemesContentManager( this, true, true, true, false, false );
            themesContentManager.getContent();
        },
        editPageBuild: function()
        {
            jetspeed.url.loadingIndicatorHide();

            var layoutImagesUrl = jetspeed.prefs.getLayoutRootUrl() + "/images/desktop/"; 

            var pageEditorWidgets = new Array();
            var layoutEditPaneWidgets = new Array();
            var pageEditPaneWidget = dojo.widget.createWidget( "jetspeed:PageEditPane", { layoutDecoratorDefinitions: jetspeed.page.themeDefinitions.pageDecorations, portletDecoratorDefinitions: jetspeed.page.themeDefinitions.portletDecorations, layoutImagesRoot: layoutImagesUrl } );
            pageEditPaneWidget.pageEditorWidget = this;
            dojo.dom.insertAfter( pageEditPaneWidget.domNode, this.domNode );
            pageEditorWidgets.push( pageEditPaneWidget );

            var rootLayoutEditPaneWidget = dojo.widget.createWidget( "jetspeed:LayoutEditPane", { widgetId: "layoutEdit_root", layoutId: jetspeed.page.rootFragmentId, isRootLayout: true, layoutDefinitions: jetspeed.page.themeDefinitions.layouts, layoutImagesRoot: layoutImagesUrl } );
            rootLayoutEditPaneWidget.pageEditorWidget = this;
            dojo.dom.insertAfter( rootLayoutEditPaneWidget.domNode, pageEditPaneWidget.domNode );
            pageEditorWidgets.push( rootLayoutEditPaneWidget );
            layoutEditPaneWidgets.push( rootLayoutEditPaneWidget );
            
            if ( jetspeed.prefs.windowTiling )
            {
                for ( var i = 0 ; i < jetspeed.page.columns.length; i++ )
                {
                    var col = jetspeed.page.columns[i];
                    if ( col.layoutHeader )
                    {
                        var layoutEditPaneWidget = dojo.widget.createWidget( "jetspeed:LayoutEditPane", { widgetId: "layoutEdit_" + i, layoutId: col.layoutId, layoutDefinitions: jetspeed.page.themeDefinitions.layouts, layoutImagesRoot: layoutImagesUrl } );
                        layoutEditPaneWidget.pageEditorWidget = this;
                        if ( col.domNode.firstChild != null )
                            col.domNode.insertBefore( layoutEditPaneWidget.domNode, col.domNode.firstChild );
                        else
                            col.domNode.appendChild( layoutEditPaneWidget.domNode );
                        layoutEditPaneWidget.initializeDrag();
                        pageEditorWidgets.push( layoutEditPaneWidget );
                        layoutEditPaneWidgets.push( layoutEditPaneWidget );
                    }
                }
            }
            this.pageEditorWidgets = pageEditorWidgets;
            this.layoutEditPaneWidgets = layoutEditPaneWidgets;
            this.editPageSyncPortletActions();
        },
        editPageSyncPortletActions: function()
        {
            var portlets = jetspeed.page.getPortletArray()
            if ( portlets != null )
            {
                for ( var i = 0 ; i < portlets.length ; i++ )
                {
                    portlets[i].syncActions();
                }
            }
        },
        editPageHide: function()
        {
            if ( this.pageEditorWidgets != null )
            {
                for ( var i = 0 ; i < this.pageEditorWidgets.length ; i++ )
                {
                    this.pageEditorWidgets[i].hide();
                }
            }
            this.hide();
            this.editPageSyncPortletActions();
        },
        editPageShow: function()
        {
            if ( this.pageEditorWidgets != null )
            {
                for ( var i = 0 ; i < this.pageEditorWidgets.length ; i++ )
                {
                    this.pageEditorWidgets[i].editModeRedisplay();
                }
            }
            this.show();
            this.editPageSyncPortletActions();
        },


        // methods

        deletePortlet: function( portletEntityId, portletTitle )
        {
            this.deletePortletDialog.portletEntityId = portletEntityId;
            this.deletePortletDialog.portletTitle = portletTitle;
            this.deletePortletTitle.innerHTML = portletTitle;
            this.deletePortletDialog.show();
        },
        deletePortletConfirmed: function( portletEntityId )
        {
            var removePortletContentManager = new jetspeed.widget.RemovePortletContentManager( portletEntityId, this );
            removePortletContentManager.getContent();
        },
        deleteLayout: function( layoutId )
        {
            this.deleteLayoutDialog.layoutId = layoutId;
            this.deleteLayoutDialog.layoutTitle = layoutId;
            this.deleteLayoutTitle.innerHTML = layoutId;
            this.deleteLayoutDialog.show();
        },
        deleteLayoutConfirmed: function()
        {
            var removePortletContentManager = new jetspeed.widget.RemoveLayoutContentManager( this.deleteLayoutDialog.layoutId, this );
            removePortletContentManager.getContent();
        },
        openColumnSizesEditor: function( layoutId )
        {
            var currentLayout = null;
            if ( layoutId != null )
                currentLayout = jetspeed.page.layouts[ layoutId ];

            if ( currentLayout != null && currentLayout.columnSizes != null && currentLayout.columnSizes.length > 0 )
            {
                var spinnerMax = 5;   // 5 is current max
                var spinnerCount = 0;
                for ( var i = 0 ; i < spinnerMax; i++ )
                {
                    var spinnerWidget = this.columnSizeDialog[ "spinner" + i ];
                    var spinnerFieldDiv = this[ "spinner" + i + "Field" ];
                    if ( i < currentLayout.columnSizes.length )
                    {
                        spinnerWidget.setValue( currentLayout.columnSizes[i] );
                        spinnerFieldDiv.style.display = "block";
                        spinnerWidget.show();
                        spinnerCount++;
                    }
                    else
                    {
                        spinnerFieldDiv.style.display = "none";
                        spinnerWidget.hide();
                    }
                }
                this.columnSizeDialog.layoutId = layoutId;
                this.columnSizeDialog.columnCount = spinnerCount;
                this.columnSizeDialog.show();
            }            
        },
        columnSizeConfirmed: function( layoutId, columnSizes )
        {
            if ( layoutId != null && columnSizes != null && columnSizes.length > 0 )
            {   // layout name is currently required by updatepage/update-fragment
                var currentLayout = jetspeed.page.layouts[ layoutId ];
    
                var currentLayoutName = null;
                if ( currentLayout != null )
                    currentLayoutName = currentLayout.name;

                if ( currentLayoutName != null )
                {
                    var colSizesStr = "";
                    for ( var i = 0 ; i < columnSizes.length ; i++ )
                    {
                        if ( i > 0 )
                            colSizesStr += ",";
                        colSizesStr += columnSizes[i] + "%";
                    }
                    var updateFragmentContentManager = new jetspeed.widget.UpdateFragmentContentManager( layoutId, currentLayoutName, colSizesStr, this );
                    updateFragmentContentManager.getContent();
                }
            }
        },

        refreshPage: function()
        {
            dojo.lang.setTimeout( this, this._doRefreshPage, 10 );
        },
        _doRefreshPage: function()
        {
            var pageUrl = jetspeed.page.getPageUrl();
            pageUrl = jetspeed.url.addQueryParameter( pageUrl, "editPage", "true", true );
            window.location.href = pageUrl.toString();
        },

        editModeNormal: function()
        {
            // restore all portlets (that were not previously minimized)
            var portletArray = jetspeed.page.getPortletArray();
            for ( var i = 0; i < portletArray.length; i++ )
            {
                var portletWindow = portletArray[i].getPortletWindow();
                if ( portletWindow != null )
                {
                    portletWindow.restoreFromMinimizeWindowTemporarily();
                }
            }
            if ( this.layoutEditPaneWidgets != null )
            {
                for ( var i = 0 ; i < this.layoutEditPaneWidgets.length ; i++ )
                {
                    var lepWidget = this.layoutEditPaneWidgets[i];
                    if ( lepWidget.layoutMoveContainer != null )
                        lepWidget.layoutMoveContainer.domNode.style.display = "none";
                }
            }           
        },
        editModeLayoutMove: function()
        {
            // minimize all portlets
            var portletArray = jetspeed.page.getPortletArray();
            for ( var i = 0; i < portletArray.length; i++ )
            {
                var portletWindow = portletArray[i].getPortletWindow();
                if ( portletWindow != null )
                {
                    portletWindow.minimizeWindowTemporarily();
                }
            }
            // display layout-move-handle widget
            if ( this.layoutEditPaneWidgets != null )
            {
                for ( var i = 0 ; i < this.layoutEditPaneWidgets.length ; i++ )
                {
                    var lepWidget = this.layoutEditPaneWidgets[i];
                    if ( ! lepWidget.isRootLayout && lepWidget.layoutMoveContainer != null )
                        lepWidget.layoutMoveContainer.domNode.style.display = "block"
                }
            }
        }
	}
);


// ... jetspeed.widget.EditPageGetThemesContentManager
jetspeed.widget.EditPageGetThemesContentManager = function( pageEditorWidget, pageDecorations, portletDecorations, layouts, desktopPageDecorations, desktopPortletDecorations )
{
    this.pageEditorWidget = pageEditorWidget;
    var getThemeTypes = new Array();
    if ( pageDecorations )
        getThemeTypes.push( [ "pageDecorations" ] );
    if ( portletDecorations )
        getThemeTypes.push( [ "portletDecorations" ] );
    if ( layouts )
        getThemeTypes.push( [ "layouts" ] );
    if ( desktopPageDecorations )
        getThemeTypes.push( [ "desktopPageDecorations", "pageDecorations" ] );
    if ( desktopPortletDecorations )
        getThemeTypes.push( [ "desktopPortletDecorations", "portletDecorations" ] );
    this.getThemeTypes = getThemeTypes;
    this.getThemeTypeNextIndex = 0;
};
jetspeed.widget.EditPageGetThemesContentManager.prototype =
{
    getContent: function()
    {
        if ( this.getThemeTypes != null && this.getThemeTypes.length > this.getThemeTypeNextIndex )
        {
            var queryString = "?action=getthemes&type=" + this.getThemeTypes[ this.getThemeTypeNextIndex ][0] + "&format=json";
            var getThemesUrl = jetspeed.url.basePortalUrl() + jetspeed.url.path.AJAX_API + queryString ;
            var ajaxApiContext = new jetspeed.om.Id( "getthemes", { } );
            var bindArgs = {};
            bindArgs.url = getThemesUrl;
            bindArgs.mimetype = "text/json";
            jetspeed.url.retrieveContent( bindArgs, this, ajaxApiContext, jetspeed.debugContentDumpIds );
        }
        else
        {
            this.pageEditorWidget.editPageBuild();
        }
    },
    notifySuccess: function( /* JSON */ getThemesData, /* String */ requestUrl, domainModelObject )
    {
        if ( jetspeed.page.themeDefinitions == null )
            jetspeed.page.themeDefinitions = {};
        var themeDefKey = ( ( this.getThemeTypes[ this.getThemeTypeNextIndex ].length > 1 ) ? this.getThemeTypes[ this.getThemeTypeNextIndex ][1] : this.getThemeTypes[ this.getThemeTypeNextIndex ][0]);
        jetspeed.page.themeDefinitions[ themeDefKey ] = getThemesData;
        this.getThemeTypeNextIndex++;
        this.getContent();
    },
    notifyFailure: function( /* String */ type, /* Object */ error, /* String */ requestUrl, domainModelObject )
    {
        dojo.raise( "EditPageGetThemesContentManager notifyFailure url: " + requestUrl + " type: " + type + jetspeed.url.formatBindError( error ) );
    }
};

// ... jetspeed.widget.RemovePageContentManager
jetspeed.widget.RemovePageContentManager = function( pageEditorWidget )
{
    this.pageEditorWidget = pageEditorWidget;    
};
jetspeed.widget.RemovePageContentManager.prototype =
{
    getContent: function()
    {
        var queryString = "?action=updatepage&method=remove";
        var removePageUrl = jetspeed.url.basePortalUrl() + jetspeed.url.path.AJAX_API + jetspeed.page.getPath() + queryString ;
        var ajaxApiContext = new jetspeed.om.Id( "updatepage-remove-page", { } );
        var bindArgs = {};
        bindArgs.url = removePageUrl;
        bindArgs.mimetype = "text/xml";
        jetspeed.url.retrieveContent( bindArgs, this, ajaxApiContext, jetspeed.debugContentDumpIds );
    },
    notifySuccess: function( /* XMLDocument */ data, /* String */ requestUrl, /* Portlet */ portlet )
    {
        if ( jetspeed.url.checkAjaxApiResponse( requestUrl, data, true, "updatepage-remove-page" ) )
        {
            var pageUrl = jetspeed.page.makePageUrl( "/" );
            pageUrl += "?" + jetspeed.id.PAGE_EDITOR_INITIATE_PARAMETER + "=true";
            window.location.href = pageUrl;
        }
    },
    notifyFailure: function( /* String */ type, /* Object */ error, /* String */ requestUrl, /* Portlet */ portlet )
    {
        dojo.raise( "RemovePageContentManager notifyFailure url: " + requestUrl + " type: " + type + jetspeed.url.formatBindError( error ) );
    }
};

// ... jetspeed.widget.AddPageContentManager
jetspeed.widget.AddPageContentManager = function( pageRealPath, pagePath, pageName, layoutName, pageTitle, pageShortTitle, pageEditorWidget )
{
    this.pageRealPath = pageRealPath;
    this.pagePath = pagePath;
    this.pageName = pageName;
    if ( layoutName == null )
    {
        if ( jetspeed.page.themeDefinitions != null && jetspeed.page.themeDefinitions.layouts != null && jetspeed.page.themeDefinitions.layouts.length > 0 && jetspeed.page.themeDefinitions.layouts[0] != null && jetspeed.page.themeDefinitions.layouts[0].length == 2 )
            layoutName = jetspeed.page.themeDefinitions.layouts[0][1];
    }
    this.layoutName = layoutName;
    this.pageTitle = pageTitle;
    this.pageShortTitle = pageShortTitle;
    this.pageEditorWidget = pageEditorWidget;    
};
jetspeed.widget.AddPageContentManager.prototype =
{
    getContent: function()
    {
        if ( this.pageRealPath != null && this.pageName != null )
        {
            var queryString = "?action=updatepage&method=add&path=" + escape( this.pageRealPath ) + "&name=" + escape( this.pageName );
            if ( this.layoutName != null )
                queryString += "&defaultLayout=" + escape( this.layoutName );
            if ( this.pageTitle != null )
                queryString += "&title=" + escape( this.pageTitle );
            if ( this.pageShortTitle != null )
                queryString += "&short-title=" + escape( this.pageShortTitle );
            var addPageUrl = jetspeed.url.basePortalUrl() + jetspeed.url.path.AJAX_API + queryString ;
            var ajaxApiContext = new jetspeed.om.Id( "updatepage-add-page", { } );
            var bindArgs = {};
            bindArgs.url = addPageUrl;
            bindArgs.mimetype = "text/xml";
            jetspeed.url.retrieveContent( bindArgs, this, ajaxApiContext, jetspeed.debugContentDumpIds );
        }
    },
    notifySuccess: function( /* XMLDocument */ data, /* String */ requestUrl, /* Portlet */ portlet )
    {
        if ( jetspeed.url.checkAjaxApiResponse( requestUrl, data, true, "updatepage-add-page" ) )
        {
            var pageUrl = jetspeed.page.makePageUrl( this.pagePath );
            if ( ! dojo.string.endsWith( pageUrl, ".psml", true ) )
                pageUrl += ".psml";
            pageUrl += "?" + jetspeed.id.PAGE_EDITOR_INITIATE_PARAMETER + "=true";
            window.location.href = pageUrl;
        }
    },
    notifyFailure: function( /* String */ type, /* Object */ error, /* String */ requestUrl, /* Portlet */ portlet )
    {
        dojo.raise( "AddPageContentManager notifyFailure url: " + requestUrl + " type: " + type + jetspeed.url.formatBindError( error ) );
    }
};


// ... jetspeed.widget.MoveLayoutContentManager
jetspeed.widget.MoveLayoutContentManager = function( layoutId, moveToLayoutId, column, row, pageEditorWidget )
{
    this.layoutId = layoutId;
    this.moveToLayoutId = moveToLayoutId;
    this.column = column;
    this.row = row;
    this.pageEditorWidget = pageEditorWidget;
};
jetspeed.widget.MoveLayoutContentManager.prototype =
{
    getContent: function()
    {
        if ( this.layoutId != null && this.moveToLayoutId != null )
        {
            var queryString = "?action=moveabs&id=" + this.layoutId + "&layoutid=" + this.moveToLayoutId;
            if ( this.column != null )
                queryString += "&col=" + this.column;
            if ( this.row != null )
                queryString += "&row=" + this.row;
            var psmlMoveActionUrl = jetspeed.url.basePortalUrl() + jetspeed.url.path.AJAX_API + jetspeed.page.getPath() + queryString ;
            var ajaxApiContext = new jetspeed.om.Id( "moveabs-layout", this.layoutId );
            var bindArgs = {};
            bindArgs.url = psmlMoveActionUrl;
            bindArgs.mimetype = "text/xml";
            jetspeed.url.retrieveContent( bindArgs, this, ajaxApiContext, jetspeed.debugContentDumpIds );
        }
    },
    notifySuccess: function( /* XMLDocument */ data, /* String */ requestUrl, /* Portlet */ portlet )
    {
        if ( jetspeed.url.checkAjaxApiResponse( requestUrl, data, true, "moveabs-layout" ) )
        {
            
        }
    },
    notifyFailure: function( /* String */ type, /* Object */ error, /* String */ requestUrl, /* Portlet */ portlet )
    {
        dojo.raise( "MoveLayoutContentManager notifyFailure url: " + requestUrl + " type: " + type + jetspeed.url.formatBindError( error ) );
    }
};

// ... jetspeed.widget.UpdateFragmentContentManager
jetspeed.widget.UpdateFragmentContentManager = function( layoutId, layoutName, layoutSizes, pageEditorWidget )
{
    this.layoutId = layoutId;
    this.layoutName = layoutName;
    this.layoutSizes = layoutSizes;
    this.pageEditorWidget = pageEditorWidget;
};
jetspeed.widget.UpdateFragmentContentManager.prototype =
{
    getContent: function()
    {
        if ( this.layoutId != null )
        {
            var queryString = "?action=updatepage&method=update-fragment&id=" + this.layoutId;
            if ( this.layoutName != null )
                queryString += "&layout=" + escape( this.layoutName );
            if ( this.layoutSizes != null )
                queryString += "&sizes=" + escape( this.layoutSizes );
            var updatePageUrl = jetspeed.url.basePortalUrl() + jetspeed.url.path.AJAX_API + jetspeed.page.getPath() + queryString ;
            var ajaxApiContext = new jetspeed.om.Id( "updatepage-update-fragment", { } );
            var bindArgs = {};
            bindArgs.url = updatePageUrl;
            bindArgs.mimetype = "text/xml";
            jetspeed.url.retrieveContent( bindArgs, this, ajaxApiContext, jetspeed.debugContentDumpIds );
        }
    },
    notifySuccess: function( /* XMLDocument */ data, /* String */ requestUrl, /* Portlet */ portlet )
    {
        if ( jetspeed.url.checkAjaxApiResponse( requestUrl, data, true, "updatepage-update-fragment" ) )
        {
            this.pageEditorWidget.refreshPage();
        }
    },
    notifyFailure: function( /* String */ type, /* Object */ error, /* String */ requestUrl, /* Portlet */ portlet )
    {
        dojo.raise( "UpdateFragmentContentManager notifyFailure url: " + requestUrl + " type: " + type + jetspeed.url.formatBindError( error ) );
    }
};

// ... jetspeed.widget.UpdateFragmentContentManager
jetspeed.widget.UpdatePageInfoContentManager = function( layoutDecorator, portletDecorator, pageEditorWidget )
{
    this.refreshPage = ( ( pageEditorWidget.editorInitiatedFromDesktop ) ? true : false ) ;
    this.layoutDecorator = layoutDecorator;
    this.portletDecorator = portletDecorator;
    this.pageEditorWidget = pageEditorWidget;
};
jetspeed.widget.UpdatePageInfoContentManager.prototype =
{
    getContent: function()
    {
        var queryString = "?action=updatepage&method=info";
        if ( this.layoutDecorator != null )
            queryString += "&layout-decorator=" + escape( this.layoutDecorator );
        if ( this.portletDecorator != null )
            queryString += "&portlet-decorator=" + escape( this.portletDecorator );
        var updatePageUrl = jetspeed.url.basePortalUrl() + jetspeed.url.path.AJAX_API + jetspeed.page.getPath() + queryString ;
        var ajaxApiContext = new jetspeed.om.Id( "updatepage-info", { } );
        var bindArgs = {};
        bindArgs.url = updatePageUrl;
        bindArgs.mimetype = "text/xml";
        jetspeed.url.retrieveContent( bindArgs, this, ajaxApiContext, jetspeed.debugContentDumpIds );
    },
    notifySuccess: function( /* XMLDocument */ data, /* String */ requestUrl, /* Portlet */ portlet )
    {
        if ( jetspeed.url.checkAjaxApiResponse( requestUrl, data, true, "updatepage-info" ) )
        {
            if ( this.refreshPage )
                this.pageEditorWidget.refreshPage();
        }
    },
    notifyFailure: function( /* String */ type, /* Object */ error, /* String */ requestUrl, /* Portlet */ portlet )
    {
        dojo.raise( "UpdatePageInfoContentManager notifyFailure url: " + requestUrl + " type: " + type + jetspeed.url.formatBindError( error ) );
    }
};

// ... jetspeed.widget.RemovePortletContentManager
jetspeed.widget.RemovePortletContentManager = function( portletEntityId, pageEditorWidget )
{
    this.portletEntityId = portletEntityId;
    this.pageEditorWidget = pageEditorWidget;
};
jetspeed.widget.RemovePortletContentManager.prototype =
{
    getContent: function()
    {
        if ( this.portletEntityId != null )
        {
            var queryString = "?action=remove&id=" + this.portletEntityId;
            var removePortletUrl = jetspeed.url.basePortalUrl() + jetspeed.url.path.AJAX_API + jetspeed.page.getPath() + queryString ;
            var ajaxApiContext = new jetspeed.om.Id( "removeportlet", { } );
            var bindArgs = {};
            bindArgs.url = removePortletUrl;
            bindArgs.mimetype = "text/xml";
            jetspeed.url.retrieveContent( bindArgs, this, ajaxApiContext, jetspeed.debugContentDumpIds );
        }
    },
    notifySuccess: function( /* XMLDocument */ data, /* String */ requestUrl, /* Portlet */ portlet )
    {
        if ( jetspeed.url.checkAjaxApiResponse( requestUrl, data, true, "removeportlet" ) )
        {
            this.pageEditorWidget.refreshPage();
        }
    },
    notifyFailure: function( /* String */ type, /* Object */ error, /* String */ requestUrl, /* Portlet */ portlet )
    {
        dojo.raise( "RemovePortletContentManager notifyFailure url: " + requestUrl + " type: " + type + jetspeed.url.formatBindError( error ) );
    }
};

// ... jetspeed.widget.RemoveLayoutContentManager
jetspeed.widget.RemoveLayoutContentManager = function( layoutId, pageEditorWidget )
{
    this.layoutId = layoutId;
    this.pageEditorWidget = pageEditorWidget;
};
jetspeed.widget.RemoveLayoutContentManager.prototype =
{
    getContent: function()
    {
        if ( this.layoutId != null )
        {
            var queryString = "?action=updatepage&method=remove-fragment&id=" + this.layoutId;
            var removeLayoutUrl = jetspeed.url.basePortalUrl() + jetspeed.url.path.AJAX_API + jetspeed.page.getPath() + queryString ;
            var ajaxApiContext = new jetspeed.om.Id( "removelayout", { } );
            var bindArgs = {};
            bindArgs.url = removeLayoutUrl;
            bindArgs.mimetype = "text/xml";
            jetspeed.url.retrieveContent( bindArgs, this, ajaxApiContext, jetspeed.debugContentDumpIds );
        }
    },
    notifySuccess: function( /* XMLDocument */ data, /* String */ requestUrl, /* Portlet */ portlet )
    {
        if ( jetspeed.url.checkAjaxApiResponse( requestUrl, data, true, "removeportlet" ) )
        {
            this.pageEditorWidget.refreshPage();
        }
    },
    notifyFailure: function( /* String */ type, /* Object */ error, /* String */ requestUrl, /* Portlet */ portlet )
    {
        dojo.raise( "RemoveLayoutContentManager notifyFailure url: " + requestUrl + " type: " + type + jetspeed.url.formatBindError( error ) );
    }
};

// ... jetspeed.widget.AddLayoutContentManager
jetspeed.widget.AddLayoutContentManager = function( parentLayoutId, layoutName, pageEditorWidget )
{
    this.parentLayoutId = parentLayoutId;
    this.layoutName = layoutName;
    this.pageEditorWidget = pageEditorWidget;
};
jetspeed.widget.AddLayoutContentManager.prototype =
{
    getContent: function()
    {
        if ( this.parentLayoutId != null )
        {
            var queryString = "?action=updatepage&method=add-fragment&layoutid=" + this.parentLayoutId + ( this.layoutName != null ? ( "&layout=" + this.layoutName ) : "" );
            var addLayoutUrl = jetspeed.url.basePortalUrl() + jetspeed.url.path.AJAX_API + jetspeed.page.getPath() + queryString ;
            var ajaxApiContext = new jetspeed.om.Id( "addlayout", { } );
            var bindArgs = {};
            bindArgs.url = addLayoutUrl;
            bindArgs.mimetype = "text/xml";
            jetspeed.url.retrieveContent( bindArgs, this, ajaxApiContext, jetspeed.debugContentDumpIds );
        }
    },
    notifySuccess: function( /* XMLDocument */ data, /* String */ requestUrl, /* Portlet */ portlet )
    {
        if ( jetspeed.url.checkAjaxApiResponse( requestUrl, data, true, "addportlet" ) )
        {
            this.pageEditorWidget.refreshPage();
        }
    },
    notifyFailure: function( /* String */ type, /* Object */ error, /* String */ requestUrl, /* Portlet */ portlet )
    {
        dojo.raise( "AddLayoutContentManager notifyFailure url: " + requestUrl + " type: " + type + jetspeed.url.formatBindError( error ) );
    }
};
