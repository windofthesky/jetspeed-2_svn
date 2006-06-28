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

dojo.provide("jetspeed.ui.widget.EditorTable");

dojo.require("dojo.widget.html.SortableTable");

jetspeed.ui.widget.EditorTable = function()
{
    dojo.widget.html.SortableTable.call(this);
    this.widgetType = "EditorTable";
};

dojo.inherits( jetspeed.ui.widget.EditorTable, dojo.widget.html.SortableTable);

dojo.lang.extend( jetspeed.ui.widget.EditorTable, {
    saveWarningDialogWidgetId: null,
    
    /* derived class protocol - attach-to or override this methods */
    updateEditor: function( rowData )
    {

    },
    saveEntrySubmit: function( selectedRowData, /* boolean */ removeEntry )
    {

    },
    clearAndDisableEditor: function()
    {

    },
    getNewEntryPrototype: function()
    {
        return {};
    },
    
    /* base class protocol */
    buildRendering: function(args, frag){
        jetspeed.ui.widget.EditorTable.superclass.buildRendering.call( this, args, frag );
        if ( args.templateCssPath )
        {
            dojo.widget.fillFromTemplateCache(this, null, dojo.uri.dojoUri(args.templateCssPath), null, false);
        }
    },


    /* methods */
    hasRowChanged: function( rowData )
    {
        if ( ! rowData ) return false;
        var tId = rowData.Id;
        var masterData = this.getData( this.js_masterdata, tId );
        var changed = rowData.isNew ? true : false;
        if ( ! changed )
        {
            for ( var slotKey in masterData )
            {
                if ( rowData[ slotKey ] != masterData[ slotKey ] )
                {
                    //dojo.debug( "slot " + slotKey + " changed - old: " + masterData[ slotKey ] + " new: " + rowData[ slotKey ] ) ;
                    changed = true;
                    break;
                }
            }
        }
        return changed;
    },
    getSelectedRow: function()
    {
        if ( this.selected && this.selected.length == 1 )
        {
            var tId = this.selected[0].Id;
            var data = this.getData( this.data, tId );
            return data;
        }
        return null;
    },
    getData: function( tableWidgetData, matchId )
    {
        if ( ! tableWidgetData ) return null ;
        for( var i = 0 ; i < tableWidgetData.length; i++ )
        {
            if ( tableWidgetData[ i ].Id == matchId )
            {
                return tableWidgetData[ i ];
            }
        }
        return null;
    },
    getDataIndex: function( tableWidgetData, matchId )
    {
        if ( ! tableWidgetData ) return -1;
        for( var i = 0 ; i < tableWidgetData.length; i++ )
        {
            if ( tableWidgetData[ i ].Id == matchId )
            {
                return i;
            }
        }
        return -1;
    },
    processTableRowEvent: function( e )
    {
        var row = dojo.html.getParentByType( e.target, "tr" );
        var rowData = this.getObjectFromRow( row );
        updateEditor( rowData );
    },

    checkForChanges: function()
    {
        var selectedRowData = this.getSelectedRow();
    
        if ( ! selectedRowData ) return false;
        var hasChanged = this.hasRowChanged( selectedRowData );
        return ( ! hasChanged ? false : ( selectedRowData.isNew ? "new" : "modified" ) );
    },

    updateClonedData: function( fromData, toData )
    {
        if ( ! fromData || ! toData ) return;
        for ( var slotKey in fromData )
        {
            toData[ slotKey ] = fromData[ slotKey ];
        }
    },

    printSelection: function()
    {
        if ( this.selected && this.selected.length == 1 )
            dojo.debug( this.widgetId + " selection: " + jsDebugShallow( this.selected[0] ) );
        else
            dojo.debug( this.widgetId + " selection: null" );
    },

    newEntry: function()
    {
        if ( this.saveWarningDialogWidgetId )
        {
            if ( this.checkForChanges() )
            {
                dojo.widget.byId( this.saveWarningDialogWidgetId ).show();
                return;
            }
        }
    
        var newEntry = dojo.lang.shallowCopy( this.getNewEntryPrototype() );
        var tId = 1;
        for ( var i = 0 ; i < this.js_masterdata.length; i++ )
        {
            if ( this.js_masterdata[i].Id >= tId )
                tId = this.js_masterdata[i].Id + 1;
        }
        newEntry.Id = tId;
        newEntry.isNew = true;
        this.js_masterdata.push( newEntry );
        this.data.push( dojo.lang.shallowCopy( newEntry ) );
        this.selected = [ dojo.lang.shallowCopy( newEntry ) ];

        this.render(true);
        this.showSelections();

        this.updateEditor( newEntry );
    },

    deleteEntry: function()
    {
        var selectedRowData = this.getSelectedRow();
    
        if ( ! selectedRowData ) return;
        var tId = selectedRowData.Id;
    
        if ( ! selectedRowData.isNew  )
            this.saveEntrySubmit( selectedRowData, true );

        var tIndex = this.getDataIndex( this.js_masterdata, tId );
        if ( tIndex != -1 )
            this.js_masterdata.splice( tIndex, 1 );

        tIndex = this.getDataIndex( this.data, tId );
        if ( tIndex != -1 )
            this.data.splice( tIndex, 1 );

        this.selected = [];
        this.render(true);
        this.showSelections();
    
        this.clearAndDisableEditor();
    },
    saveEntry: function()
    {
        var selectedRowData = this.getSelectedRow();

        if ( ! selectedRowData ) { dojo.raise( "saveEntry can't find selectedRowData" ) ; return; } 
        var masterData = this.getData( this.js_masterdata, selectedRowData.Id );
        if ( ! masterData ) { dojo.raise( "saveEntry can't find masterdata" ) ; return; } 

        this.saveEntrySubmit( selectedRowData );

        selectedRowData.isNew = false;
        this.updateClonedData( selectedRowData, masterData );
        this.updateClonedData( selectedRowData, this.selected[0] );
        this.updateEditor( selectedRowData );
    },

    revertEntry: function()
    {
        var selectedRowData = this.getSelectedRow();
    
        if ( ! selectedRowData ) return;

        if ( selectedRowData.isNew )
        {
            deleteEntry();
        }
        else
        {
            var masterData = this.getData( this.js_masterdata, selectedRowData.Id );
            if ( ! masterData ) return;
            this.updateClonedData( masterData, selectedRowData );
            this.updateClonedData( masterData, this.selected[0] );
            this.render(true);
            this.showSelections();
            this.updateEditor( masterData );
        }
    },

    listSelectionChangeOk: function(invocation)
    {
        if ( this.saveWarningDialogWidgetId )
        {
            if ( this.checkForChanges() )
            {
                dojo.widget.byId( this.saveWarningDialogWidgetId ).show();
                return false;
            }
        }
        return invocation.proceed();
    }
});
dojo.widget.tags.addParseTreeHandler("dojo:editortable");
