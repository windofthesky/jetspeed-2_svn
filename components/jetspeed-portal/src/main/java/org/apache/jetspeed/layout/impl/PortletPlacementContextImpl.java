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
 */
package org.apache.jetspeed.layout.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

import org.apache.jetspeed.components.portletregistry.PortletRegistry;
import org.apache.jetspeed.layout.Coordinate;
import org.apache.jetspeed.layout.PageLayoutComponent;
import org.apache.jetspeed.layout.PortletPlacementException;
import org.apache.jetspeed.layout.PortletPlacementContext;
import org.apache.jetspeed.om.page.ContentFragment;
import org.apache.jetspeed.om.page.ContentPage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Portal Placement Context
 * 
 * The purpose of the object is to provide an API that
 * can be used to move a portlet fragment on the page.
 * This includes moving, adding, removing and getting
 * information about portlets that are on the page and
 * portlets that are available to be added to the page.
 * 
 * This object represents the fragment contents of a
 * single layout fragment (i.e. nested depth cannot 
 * be captured by this object).
 * 
 * An important note about this object:
 * This object is really only intended to be used to do
 * a single operation such as "moveabs" or "add".  After
 * performing the operation, the hashmap data structures
 * are not correct and should not be used for subsequent
 * operations.  The reason they are incorrect is that when
 * a fragment is moved, the coordinate of fragments below
 * it are now different.  These could be updated, but it
 * really doesn't serve a purpose since this is a short
 * lived object.
 * 
 * @author <a>David Gurney</a>
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @author <a href="mailto:smilek@apache.org">Steve Milek</a>
 * @version $Id: $
 */
public class PortletPlacementContextImpl implements PortletPlacementContext 
{
    private static Logger log = LoggerFactory.getLogger( PortletPlacementContextImpl.class );
    protected static final String eol = System.getProperty( "line.separator" );

	// Columns are reference by index, the rows are held
	// in the columnsList as shown below:
	//
	// [0]        [1]          [2]
	// ArrayList  ArrayList    ArrayList
	//  Row0Frag   Row0Frag     Row0Frag
	//  Row1Frag   Row1Frag     Row1Frag
	//  Row2Frag   Row2Frag     Row2Frag
	//  ...
	//
	protected ArrayList[] columnsList = null;
	
	// Used as a convenience when looking up a particular fragment
	//
	// key is Fragment id (String), value is a Coordinate object
	protected Map fragmentCoordinateMap = new HashMap();
	
	// Used as a convenience when looking up a particular fragment by id
	//
	// key is the Fragment id (String), value is the Fragment
	protected Map fragmentMap = new HashMap();
	
	// Number of columns
	protected int numberOfColumns = -1;
	
    protected ContentPage page;
    private PortletRegistry registry;
    protected ContentFragment layoutContainerFragment;
        
	public PortletPlacementContextImpl( ContentPage page, PortletRegistry registry ) 
        throws PortletPlacementException 
    {
		this( page, registry, null, null );
	}
        
    public PortletPlacementContextImpl( ContentPage page, PortletRegistry registry, ContentFragment container ) 
        throws PortletPlacementException 
    {
        this( page, registry, container, null );
    }
	
    public PortletPlacementContextImpl( ContentPage page, PortletRegistry registry, ContentFragment container, ContentFragment excludeFragment ) 
        throws PortletPlacementException 
    {
        if ( page == null )
            throw new NullPointerException( "PortletPlacementContext cannot be instantiated with a null ContentPage argument" );
        if ( registry == null )
            throw new NullPointerException( "PortletPlacementContext cannot be instantiated with a null PortletRegistry argument" );
    
        this.page = page;
        this.registry = registry;
    
        init( container, excludeFragment );
    }

    protected void init( ContentFragment container, ContentFragment excludeFragment )
        throws PortletPlacementException 
    {
        if ( container == null )
        {
            container = page.getRootFragment();
            if ( container == null )
            	throw new PortletPlacementException( "PortletPlacementContext cannot acquire root layout fragment from page" );
        }        
        if ( ! "layout".equals( container.getType() ) )
        {
        	throw new PortletPlacementException( "PortletPlacementContext specified container fragment (" + container.getId() + ") is not a layout fragment, but is type: " + container.getType() );
        }
        this.layoutContainerFragment = container;
        
        int columnCount = PortletPlacementMetadataAccess.getColumnCountAndSizes( container, registry, null );
        if ( columnCount <= 0 )
        {
        	throw new PortletPlacementException( "PortletPlacementContext cannot detemine number of columns in layout fragment (" + container.getId() + ")" );
        }
        this.numberOfColumns = columnCount;
        
        initProcessLayoutContainerFragment( excludeFragment );
        
        //debugFragments( "init" );
	}
	
	private void initProcessLayoutContainerFragment( ContentFragment excludeFragment ) 
        throws PortletPlacementException 
    {
        List fragChildren = this.layoutContainerFragment.getFragments();
        int fragChildCount = fragChildren.size();
        
        int columnCount = this.numberOfColumns;
        
        // sort the fragments in the same manner as /portal and /desktop rendering
        FragmentLinkedListEntry[][] colLinkedLists = new FragmentLinkedListEntry[columnCount][fragChildCount];
        FragmentLinkedListInfo[] colLinkedListsInfo = new FragmentLinkedListInfo[columnCount];
        for ( int colIndex = 0 ; colIndex < columnCount ; colIndex++ )
        {
        	colLinkedListsInfo[ colIndex ] = new FragmentLinkedListInfo();
        }
        for( int fragChildIndex = 0; fragChildIndex < fragChildCount; fragChildIndex++ ) 
        {
            ContentFragment fragment = (ContentFragment)fragChildren.get( fragChildIndex );		
            if ( ( fragment != null ) && ( fragment != excludeFragment ) )
            {
            	int col = getColumnFromFragment( fragment );            	
            	
            	FragmentLinkedListEntry[] ll = colLinkedLists[col];
            	FragmentLinkedListInfo llInfo = colLinkedListsInfo[col];
            	
            	Integer rowObj = getRowFromFragment( fragment );
            	int row;
            	if ( rowObj != null )
            		row = rowObj.intValue();
            	else
            		row = llInfo.getHigh() + 1;   // fragment with unspecified row property is assigned 
                                                  //    the value of current fragment in the highest row + 1
            		                              //    - this is one of the reasons we are not using sort here
            	
            	FragmentLinkedListEntry fragLLentry = new FragmentLinkedListEntry( fragChildIndex, row );
            	int llLen = llInfo.useNextAvailableIndex();
            	ll[ llLen ] = fragLLentry;
            	if ( llLen == 0 )
            	{
            		llInfo.setHead( 0 );
            		llInfo.setTail( 0 );
            		llInfo.setHigh( row );
            	}
            	else
            	{
            		if ( row > llInfo.getHigh() )
            		{
            			ll[ llInfo.getTail() ].setNextEntry( llLen );
            			llInfo.setHigh( row );
            			llInfo.setTail( llLen );
            		}
            		else
            		{
            			int llEntryIndex = llInfo.getHead();
            			int llPrevEntryIndex = -1;
            			while ( ll[llEntryIndex].getRow() < row )
            			{
            				llPrevEntryIndex = llEntryIndex;
            				llEntryIndex = ll[llEntryIndex].getNextEntry();
            			}
            			if ( ll[llEntryIndex].getRow() == row )
            			{   // a subsequent fragment (in the document) with a row value equal to that 
            				//    of a previous fragment is inserted before the previous fragment
            				//    - this is one of the reasons we are not using sort here
            				int incrementedRow = row + 1;
            				ll[llEntryIndex].setRow( incrementedRow );
            				if ( llInfo.getTail() == llEntryIndex )
            					llInfo.setHigh( incrementedRow );
            			}
            			fragLLentry.setNextEntry( llEntryIndex );
            			if ( llPrevEntryIndex == -1 )
            				llInfo.setHead( llLen );
            			else
            				ll[llPrevEntryIndex].setNextEntry( llLen );
            		}
            	}
            }
        }

        ArrayList[] columnFragments = new ArrayList[ columnCount ];
        for ( int colIndex = 0 ; colIndex < columnCount ; colIndex++ )
        {
        	ArrayList fragmentsInColumn = new ArrayList();
        	columnFragments[ colIndex ] = fragmentsInColumn;
        	
        	FragmentLinkedListEntry[] ll = colLinkedLists[colIndex];
        	FragmentLinkedListInfo llInfo = colLinkedListsInfo[colIndex];
            
        	int rowIndex = 0;
            int nextEntryIndex = llInfo.getHead();
            while ( nextEntryIndex != -1 )
            {
                FragmentLinkedListEntry fragLLentry = ll[nextEntryIndex];
                ContentFragment fragment = (ContentFragment)fragChildren.get( fragLLentry.getFragmentIndex() );
                
                fragmentsInColumn.add( fragment );
                CoordinateImpl coordinate = new CoordinateImpl( colIndex, rowIndex );
        		this.fragmentCoordinateMap.put( fragment.getId(), coordinate );
        		this.fragmentMap.put( fragment.getId(), fragment );
        		
                nextEntryIndex = fragLLentry.getNextEntry();
        		rowIndex++;
            }
        }
        this.columnsList = columnFragments;
	}
	
	private int getColumnFromFragment( ContentFragment fragment )
	{
		// get column value in the same manner as /portal and /desktop rendering
		
		// get column from properties to distinguish between null and -1 (fragment.getLayoutColumn() is -1 when column is not specified)
		String colStr = fragment.getProperty( "column" );
        int columnCount = this.numberOfColumns;
		int col = columnCount - 1;
		if ( colStr != null )
		{
			try
    		{
				col = Integer.parseInt( colStr );
				if ( col < 0 )
					col = 0;
				else if ( col >= columnCount )
					col = columnCount - 1;
    		}
    		catch ( NumberFormatException ex )
    		{
    			col = columnCount - 1;
    		}
		}
		return col;
	}
	private Integer getRowFromFragment( ContentFragment fragment )
	{
		// get row value in the same manner as /portal and /desktop rendering
		
		// get row from properties to distinguish between null and -1 (fragment.getLayoutRow() is -1 when row is not specified)
		String rowStr = fragment.getProperty( "row" );
		if ( rowStr != null )
		{
			try
    		{
				int row = Integer.parseInt( rowStr );
				if ( row < 0 )
					row = 0;
				return new Integer( row );
    		}
    		catch ( NumberFormatException ex )
    		{
    		}
		}
		return null;
	}
	
	private int normalizeColumnIndex( int col, ArrayList[] columnFragments, int defaultForUnspecifiedCol )
	{
		int columnCount = this.numberOfColumns;
		if ( col >= columnCount )
    		col = (columnCount -1);
    	else if ( col < 0 && defaultForUnspecifiedCol >= 0 && defaultForUnspecifiedCol < columnCount )
    		col = defaultForUnspecifiedCol;
    	else if ( col < 0 )
    		col = 0;
		return col;
	}

	class FragmentLinkedListInfo
	{
		private int head = -1;
		private int tail = -1;
		private int high = -1;
		private int availableNextIndex = 0;
		
		FragmentLinkedListInfo()
		{
		}
		
		public int getHead()
		{
        	return head;
        }
		public void setHead( int newOne )
		{
        	this.head = newOne;
        }
		public int getTail()
		{
        	return tail;
        }
		public void setTail( int newOne )
		{
        	this.tail = newOne;
        }
		public int getHigh()
		{
        	return high;
        }
		public void setHigh( int newOne )
		{
        	this.high = newOne;
        }
		public int useNextAvailableIndex()
		{
			return this.availableNextIndex++;
		}
	}
	
	class FragmentLinkedListEntry
	{
		private int fragmentIndex;
		private int row;
		private int nextEntry = -1;
		
		FragmentLinkedListEntry( int fragmentIndex, int row )
		{
			this.fragmentIndex = fragmentIndex;
			this.row = row;
		}
		
		public int getFragmentIndex()
		{
			return this.fragmentIndex;
		}
		public int getRow()
		{
			return this.row;
		}
		public void setRow( int newOne )
		{
			this.row = newOne;
		}
		public int getNextEntry()
		{
			return this.nextEntry;
		}
		public void setNextEntry( int newOne )
		{
			this.nextEntry = newOne;
		}
	}	
    
	public String dumpFragments( String debug )
    {       
        StringBuffer out = new StringBuffer();
        out.append( "PortletPlacementContext - " );
        if ( debug != null )
        	out.append( debug ).append( " - " );
        out.append( "container: " ).append( this.layoutContainerFragment == null ? "<null>" : ( this.layoutContainerFragment.getId() + " / " + this.layoutContainerFragment.getType() ) ).append( " column-count=" ).append( this.numberOfColumns ).append( eol );
        for (int ix = 0; ix < this.columnsList.length; ix++)
        {
            ArrayList column = this.columnsList[ix];
            out.append( "   column " ).append( ix ).append( eol );
            Iterator frags = column.iterator();
            while ( frags.hasNext() )
            {
                ContentFragment f = (ContentFragment)frags.next();
                out.append( "      frag " ).append( f == null ? "<null>" : ( f.getId() + " / " + f.getType() ) ).append( eol );
            }
        }
        return out.toString();
    }
    public ContentFragment debugFragments( String debug )
    {
        log.info( dumpFragments( debug ) );
        return layoutContainerFragment;
    }

    /**
     * Takes the internal portlet placement state and stores back
     * out to fragment state
     * 
     * @return the managed page layout with updated fragment state. 
     */
    public ContentPage syncPageFragments()
    {
    	syncFragments(true, -1, null, null);
    	//debugFragments( "syncPage" );
    	return this.page;
    }

    public ContentPage syncPageFragments(String scope, String scopeValue)
    {
        syncFragments(true, -1, scope, scopeValue);
        //debugFragments( "syncPage" );
        return this.page;
    }
    
    protected int getLatestColumn( Coordinate coordinate )
    {
    	int col = -1;
    	if ( coordinate != null )
    	{
    		col = coordinate.getNewCol();
    		if ( col == -1 )
    			col = coordinate.getOldCol();
    	}
    	return col;
    }
    protected int getLatestRow( Coordinate coordinate )
    {
    	int row = -1;
    	if ( coordinate != null )
    	{
    		row = coordinate.getNewRow();
    		if ( row == -1 )
    			row = coordinate.getOldRow();
    	}
    	return row;
    }
    
    protected void syncFragments(boolean updateFragmentObjects, int onlyForColumnIndex, String scope, String scopeValue)
    {
        for ( int colIndex = 0; colIndex < this.columnsList.length; colIndex++ )
        {
        	if ( onlyForColumnIndex == -1 || onlyForColumnIndex == colIndex )
        	{
	            ArrayList column = this.columnsList[colIndex];
	            int colRowCount = column.size();
	        	for ( int rowIndex= 0; rowIndex < colRowCount; rowIndex++ )
	        	{
	        		ContentFragment fragment = (ContentFragment)column.get( rowIndex );
	                Coordinate coordinate = (Coordinate)this.fragmentCoordinateMap.get( fragment.getId() );
	                boolean needsReplacementCoordinate = false;
	                
	                if ( getLatestColumn( coordinate ) != colIndex || getLatestRow( coordinate ) != rowIndex )
	                	needsReplacementCoordinate = true;

	                if ( needsReplacementCoordinate )
	        		{
	        			Coordinate replacementCoordinate = new CoordinateImpl( coordinate.getOldCol(), coordinate.getOldRow(), colIndex, rowIndex );
	        			this.fragmentCoordinateMap.put( fragment.getId(), replacementCoordinate );
	        		}
	        		if ( updateFragmentObjects )
	                {
	        		    fragment.updateRowColumn(rowIndex, colIndex, scope, scopeValue);
	                }
	            }
        	}
        }
    }
    
    public int getFragmentRow( ContentFragment fragment )
    {
    	if ( fragment == null ) return -1;
		Coordinate coordinate = (Coordinate)this.fragmentCoordinateMap.get( fragment.getId() );
    	
		if ( coordinate == null )
			return -1;
		if ( coordinate.getNewRow() >= 0  )
			return coordinate.getNewRow();
		return coordinate.getOldRow();
    }
    
    public int getFragmentCol( ContentFragment fragment )
    {
    	if ( fragment == null ) return -1;
		Coordinate coordinate = (Coordinate)this.fragmentCoordinateMap.get( fragment.getId() );
    	
		if ( coordinate == null )
			return -1;
		if ( coordinate.getNewCol() >= 0  )
			return coordinate.getNewCol();
		return coordinate.getOldCol();
    }
			
	public ContentFragment getFragment( String fragmentId ) throws PortletPlacementException 
    {
		return (ContentFragment)this.fragmentMap.get( fragmentId );
	}
	
	public ContentFragment getFragmentAtOldCoordinate( Coordinate coordinate ) throws PortletPlacementException 
    {
		return getFragmentAtCoordinate( coordinate, true, false );
	}

	public ContentFragment getFragmentAtNewCoordinate( Coordinate coordinate ) throws PortletPlacementException 
    {
		return getFragmentAtCoordinate( coordinate, false, false );
	}

	protected ContentFragment getFragmentAtCoordinate( Coordinate coordinate, boolean useOldCoordinateValues, boolean suppressExceptions ) throws PortletPlacementException 
    {
		int col = -1;
		int row = -1;
		if ( useOldCoordinateValues ) 
        {
			col = coordinate.getOldCol();
			row = coordinate.getOldRow();
		}
		else 
        {
			col = coordinate.getNewCol();
			row = coordinate.getNewRow();
		}
		
		// Do some sanity checking about the request
		if ( col < 0 || col >= this.numberOfColumns )
        {
			if ( suppressExceptions )
				return null;
			throw new PortletPlacementException( "Requested column (" + col + ") is out of bounds (column-count=" + this.numberOfColumns + ")" );
		}
		
		// Get the array list associated with the column
		ArrayList columnArray = this.columnsList[col];
		if ( row < 0 || row >= columnArray.size() )
        {
			if ( suppressExceptions )
				return null;
			throw new PortletPlacementException( "Requested row (" + row + ") is out of bounds (col[" + col + "].row-count=" + columnArray.size() + ")" );
		}
		
		return (ContentFragment)columnArray.get( row );
	}
	
	public ContentFragment getFragmentById( String fragmentId ) throws PortletPlacementException 
    {
		return (ContentFragment)this.fragmentMap.get( fragmentId );
	}

	public int getNumberColumns() throws PortletPlacementException 
    {
        return this.numberOfColumns;
	}

	public int getNumberRows( int col ) throws PortletPlacementException 
    {
		// Sanity check the column
		if ( col < 0 || col >= this.numberOfColumns )
        {
			throw new PortletPlacementException( "Requested column (" + col + ") is out of bounds (column-count=" + this.numberOfColumns + ")" );
		}
		return this.columnsList[col].size();
	}
	
	public Coordinate add( ContentFragment fragment, Coordinate coordinate ) throws PortletPlacementException 
    {
		return moveAbsolute( fragment, coordinate, true );
	}

	public Coordinate moveAbsolute( ContentFragment fragment, Coordinate newCoordinate )
        throws PortletPlacementException 
    {
		return moveAbsolute( fragment, newCoordinate, false );
    }
	public Coordinate moveAbsolute( ContentFragment fragment, Coordinate newCoordinate, boolean okToAddFragment )
        throws PortletPlacementException 
    {
		if ( fragment == null )
    		throw new NullPointerException( "PortletPlacementContext moveAbsolute() cannot accept a null Fragment argument" );

		Coordinate currentCoordinate = (Coordinate)this.fragmentCoordinateMap.get( fragment.getId() );
		int currentCol = getLatestColumn( currentCoordinate );
		int currentRow = getLatestRow( currentCoordinate );
		
		int newCol = normalizeColumnIndex( getLatestColumn( newCoordinate ), this.columnsList, currentCol );
		int newRow = getLatestRow( newCoordinate );

		if ( currentCoordinate == null )
		{
			if ( ! okToAddFragment )
				throw new NullPointerException( "PortletPlacementContext moveAbsolute() cannot add fragment (" + fragment.getId() + ") unless the okToAddFragment argument is set to true" );
			
			// add fragment
			ArrayList newColumn = this.columnsList[newCol];
			if ( newRow < 0 || newRow >= newColumn.size() )
				newRow = newColumn.size();
			newColumn.add( newRow, fragment );
			
			CoordinateImpl coordinate = new CoordinateImpl( newCol, newRow );
        	this.fragmentCoordinateMap.put( fragment.getId(), coordinate );
			this.fragmentMap.put( fragment.getId(), fragment );
			syncFragments(false, newCol, PageLayoutComponent.USER_PROPERTY_SCOPE, null);
		}
		else
		{
			boolean columnChanged = ( currentCol != newCol );
			boolean rowChanged = ( currentRow != newRow );

			if ( columnChanged || rowChanged )
			{
				verifyFragmentAtExpectedCoordinate( currentCol, currentRow, fragment, "moveAbsolute()" );
				
				ArrayList currentColumn = this.columnsList[currentCol];
				currentColumn.remove( currentRow );
				
				ArrayList newColumn = currentColumn;
				if ( columnChanged )
					newColumn = this.columnsList[newCol];

				if ( newRow < 0 || newRow >= newColumn.size() )
					newColumn.add( fragment );
				else
					newColumn.add( newRow, fragment );
				
				this.fragmentMap.put( fragment.getId(), fragment );
				
				syncFragments(false, currentCol, PageLayoutComponent.USER_PROPERTY_SCOPE, null);
				if ( columnChanged )
					syncFragments(false, newCol, PageLayoutComponent.USER_PROPERTY_SCOPE, null);
			}
		}
		return (Coordinate)this.fragmentCoordinateMap.get( fragment.getId() );
	}

	protected Coordinate moveDirection( ContentFragment fragment, int deltaCol, int deltaRow ) 
        throws PortletPlacementException 
    {
		if ( fragment == null )
    		throw new NullPointerException( "PortletPlacementContext moveDirection() cannot accept a null Fragment argument" );

		if ( deltaCol != 0 || deltaRow != 0 )
		{
			Coordinate currentCoordinate = (Coordinate)this.fragmentCoordinateMap.get( fragment.getId() );
			if ( currentCoordinate == null )
				throw new NullPointerException( "PortletPlacementContext moveDirection() cannot locate target fragment (" + fragment.getId() + ")" );
	
			int currentCol = getLatestColumn( currentCoordinate );
			int currentRow = getLatestRow( currentCoordinate );
			
			verifyFragmentAtExpectedCoordinate( currentCol, currentRow, fragment, "moveDirection()" );
			
			int newCol = currentCol + deltaCol;
			int newRow = currentRow + deltaRow;
			if ( newCol >= 0 && newCol < this.numberOfColumns )
			{
				ArrayList currentColumn = this.columnsList[currentCol];
				ArrayList newColumn = currentColumn;
				if ( newCol != currentCol )
					newColumn = this.columnsList[newCol];
				
				currentColumn.remove( currentRow );
					
				if ( newRow < 0 || newRow >= newColumn.size() )
					newColumn.add( fragment );
				else
					newColumn.add( newRow, fragment );
				
				this.fragmentMap.put( fragment.getId(), fragment );
				
				syncFragments(false, currentCol, PageLayoutComponent.USER_PROPERTY_SCOPE, null);
				if ( newCol != currentCol )
					syncFragments(false, newCol, PageLayoutComponent.USER_PROPERTY_SCOPE, null);
			}
		}
		return (Coordinate)this.fragmentCoordinateMap.get( fragment.getId() );
	}
	
	public Coordinate moveDown( ContentFragment fragment ) throws PortletPlacementException 
    {
		return moveDirection( fragment, 0, 1 );
	}

	public Coordinate moveUp( ContentFragment fragment ) throws PortletPlacementException 
    {
		return moveDirection( fragment, 0, -1 );
	}

	public Coordinate moveLeft( ContentFragment fragment ) throws PortletPlacementException 
    {
		return moveDirection( fragment, -1, 0 );
	}

	public Coordinate moveRight( ContentFragment fragment ) throws PortletPlacementException 
    {
		return moveDirection( fragment, 1, 0 );
	}

	public Coordinate remove( ContentFragment fragment ) throws PortletPlacementException 
    {
		if ( fragment == null )
    		throw new NullPointerException( "PortletPlacementContext remove() cannot accept a null Fragment argument" );
		
		Coordinate currentCoordinate = (Coordinate)this.fragmentCoordinateMap.get( fragment.getId() );
		if ( currentCoordinate == null )
			throw new NullPointerException( "PortletPlacementContext remove() cannot locate target fragment (" + fragment.getId() + ")" );

		int currentCol = getLatestColumn( currentCoordinate );
		int currentRow = getLatestRow( currentCoordinate );
		
		verifyFragmentAtExpectedCoordinate( currentCol, currentRow, fragment, "moveDirection()" );

		ArrayList currentColumn = this.columnsList[currentCol];
		
		currentColumn.remove( currentRow );
		
		this.fragmentCoordinateMap.remove( fragment.getId() );
		this.fragmentMap.remove( fragment.getId() );
		
		syncFragments(false, currentCol, null, null);
		
		return currentCoordinate;
	}
	
	protected ContentFragment verifyFragmentAtExpectedCoordinate( int colIndex, int rowIndex, ContentFragment fragment, String sourceDesc )
		throws PortletPlacementException
	{
		CoordinateImpl coordinate = new CoordinateImpl( colIndex, rowIndex );
		
		boolean suppressExceptions = ( fragment != null );
		ContentFragment foundFragment = getFragmentAtCoordinate( coordinate, true, suppressExceptions );
		
		if ( fragment != null )
		{
            if ( foundFragment == null || !foundFragment.getId().equals(fragment.getId()) )               
			{
				sourceDesc = ( sourceDesc == null ? "getFragmentAtExpectedCoordinate" : sourceDesc );
				
				ArrayList column = null;
				int colFragCount = -1;
				if ( colIndex >= 0 && colIndex < this.numberOfColumns )
				{
					column = this.columnsList[colIndex];
					colFragCount = column.size();
				}
				StringBuffer out = new StringBuffer();
				out.append( "PortletPlacementContext " ).append( sourceDesc ).append( " has encountered unexpected results");
				out.append( " using the current instance state to locate fragment " ).append( fragment.getId() ).append( " (" );
				if ( foundFragment == null )
					out.append( "no fragment" );
				else
					out.append( "different fragment" );
				out.append( " in row " ).append( rowIndex ).append( " of column " ).append( colIndex );
				if ( column == null )
				{
					out.append( " - column is out of bounds, column-count=" ).append( this.numberOfColumns );
				}
				else
				{
					out.append( " - " );
					if ( rowIndex < 0 || rowIndex >= colFragCount )
						out.append( "row is out of bounds, " );
					out.append( "row-count=" ).append( colFragCount );
				}
                if(foundFragment != null)
                {
                    out.append(" - found fragment ").append(foundFragment.getId());
                }                
				out.append( ")" );
				throw new PortletPlacementException( out.toString() );
			}
		}
		return fragment;
	}
}
