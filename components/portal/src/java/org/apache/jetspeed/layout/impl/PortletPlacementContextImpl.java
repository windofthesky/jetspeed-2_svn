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
package org.apache.jetspeed.layout.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.layout.Coordinate;
import org.apache.jetspeed.layout.PortletPlacementException;
import org.apache.jetspeed.layout.PortletPlacementContext;
import org.apache.jetspeed.om.page.Fragment;
import org.apache.jetspeed.om.page.Page;
import org.apache.jetspeed.request.RequestContext;

/**
 * Portal Placement Context
 * 
 * The purpose of the object is to provide an API that
 * can be used to move a portlet fragment on the page.
 * This includes moving, adding, removing and getting
 * information about portlets that are on the page and
 * portlets that are available to be added to the page.
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
 * @version $Id: $
 */
public class PortletPlacementContextImpl implements PortletPlacementContext 
{
    private static final String COLUMN = "column";
    private static final String ROW = "row";

    /** Logger */
    private Log log = LogFactory.getLog(PortletPlacementContextImpl.class);

	// Columns are reference by index, the rows are held
	// in the array list as shown below:
	//
	// [0]        [1]          [2]
	// ArrayList  ArrayList    ArrayList
	//  Row1Frag   Row1Frag     Row1Frag
	//  Row2Frag   Row2Frag     Row2Frag
	//  Row3Frag   Row3Frag     Row3Frag
	//  ...
	//
	protected Vector[] columnsList = null;
	
	// Used as a convience when looking up a particular fragment
	//
	// Key is Fragment, value is a Coordinate object
	protected Map fragmentCoordinateMap = new HashMap();
	
	// Used as a convience when looking up a particular fragment by id
	//
	// Key is the Fragment id (String), value is the Fragment
	protected Map fragmentMap = new HashMap();
	
	// Number of columns found
	protected int numberOfColumns = -1;
	
    protected Page page;
    protected Fragment root;
    
	public PortletPlacementContextImpl(RequestContext requestContext) 
    throws PortletPlacementException 
    {
		init(requestContext);
	}
	
	// Initialize the data structures by getting the fragments
	// from the page manager
	protected void init(RequestContext requestContext) 
    throws PortletPlacementException 
    {
        this.page = requestContext.getPage();
        this.root = page.getRootFragment();
        
        // Recursively process each fragment
        processFragment(root);

        // The final step is to populate the array with the fragments
		populateArray();
        
        //debugFragments("init");
	}
	
	/**
	 * Evaluate each portlet fragment and populate the internal data
	 * structures
	 */
	protected void processFragment(Fragment fragment) 
    throws PortletPlacementException 
    {
        int rowCount = 0;
		// Process this fragment, then its children
		if(fragment != null) 
        {
			// Only process portlet fragments
			if(fragment.getType().equalsIgnoreCase("portlet")) 
            {
				// Get the column and row of this fragment
				int col = getFragmentCol(fragment);
				int row = getFragmentRow(fragment);
		        
		        if(row < 0) 
                {
                    row = rowCount;
                }
	        	// Add this fragment to the data structures
	        	addFragmentInternal(fragment, col, row);
                rowCount++;
			}
			
			// Process the children
			List children = fragment.getFragments();
			for(int ix = 0; ix < children.size(); ix++) 
            {
				Fragment childFrag = (Fragment)children.get(ix);
				
				if(childFrag != null) 
                {
					processFragment(childFrag);
				}
			}
		}		
	}
    
    public Fragment debugFragments(String debug)
    {       
        System.out.println("*** " + debug);
        for (int ix = 0; ix < this.columnsList.length; ix++)
        {
            Vector column = this.columnsList[ix];
            System.out.println("+++ Column " + ix);
            Iterator frags = column.iterator();
            while (frags.hasNext())
            {
                Fragment f = (Fragment)frags.next();
                System.out.println("\tportlet = " 
                        + f.getId() 
                        + ", [" + f.getLayoutColumn()
                        + "," + f.getLayoutRow()
                        + "]");                        
                //root.getFragments().add(fragment);
            }
        }
        return root;
    }

    /**
     * Takes the internal portlet placement state and stores back
     * out to fragment state
     * 
     * @return the managed page layout with updated fragment state. 
     */
    public Page syncPageFragments()
    {        
        for (int col = 0; col < this.columnsList.length; col++)
        {
            Vector column = this.columnsList[col];
            Iterator frags = column.iterator();
            int row = 0;
            while (frags.hasNext())
            {
                Fragment f = (Fragment)frags.next();
                if (f == null)
                    continue;
                f.setLayoutColumn(col);
                f.setLayoutRow(row);
                row++;
            }
        }
        return page;
    }
    
	// Helper method
	// The implementation will probably change to get this information
	// directly from the fragment via fragment.getFragmentCol()
	protected int getFragmentRow(Fragment fragment)
    {
        return fragment.getLayoutRow();
	}
	
	// The implementation will probably change to get this information
	// directly from the fragment via fragment.getFragmentRow()
	protected int getFragmentCol(Fragment fragment) 
    {
        int col = fragment.getLayoutColumn();
        if (col < 0)
            col = 0;
        return col;
	}
	
	// Adds the fragment to the internal data structures
	protected void addFragmentInternal(Fragment fragment, int col, int row) 
    {
		// Create a Coordinate object to hold the row and column
		CoordinateImpl coordinate = new CoordinateImpl(col, row);
		
		// Save the fragment in the lookup hash
		this.fragmentCoordinateMap.put(fragment, coordinate);
		this.fragmentMap.put(fragment.getId(), fragment);
		
		// Establish the maximum column number
		if(col > this.numberOfColumns) 
        {
			this.numberOfColumns = col + 1;
		}
	}
	
	/**
	 * Now that we know the number of columns, the array can be
	 * constructed and populated
	 */
	protected void populateArray() throws PortletPlacementException 
    {
		if(this.numberOfColumns == -1) 
        {
			//throw new PortletPlacementException("no columns found");
            this.numberOfColumns = 1; // create a new column
		}
		
		// Allocate the memory for the array of ArrayLists
		// Add one since it is zero based
		this.columnsList = new Vector[this.numberOfColumns + 1];
		
		// Put an array list into each index
		for(int i = 0; i < this.numberOfColumns + 1; i++) 
        {
			this.columnsList[i] = new Vector();
		}
		
		// Get all of the fragments from the hashmap
		Set keys = this.fragmentCoordinateMap.keySet();
		Iterator keyIterator = keys.iterator();
		while(keyIterator.hasNext()) 
        {
			// The key is a Fragment
			Fragment fragment = (Fragment) keyIterator.next();
			
			// Get the Coordinate associated with this fragment
			Coordinate coordinate = (Coordinate)this.fragmentCoordinateMap.get(fragment);
			
			// Make sure we have both
			if(fragment != null && coordinate != null) 
            {
				// Get the array list for the column
				Vector columnArray = this.columnsList[coordinate.getOldCol()];
				
				int row = coordinate.getOldRow();
				
				// Before setting the fragment in the array it might
				// be necessary to add blank rows before this row
				// An ArrayList can only set an element that already exists
				prepareList(columnArray, row);
				
				// Place the fragment in the array list using the row
				columnArray.set(row, fragment);
			}
		}
	}
	
	// Ensures that the array list has at least row number of rows
	protected void prepareList(Vector list, int row) 
    {
		if(list != null) 
        {
			int size = list.size();
			if(row + 1 > size) 
            {
				// Add one to the row since it is zero based
				for(int i = size; i < row + 1; i++) 
                {
					list.add(null);
				}
			}
		}
	}
	
	// Ensures that there is room for the fragment at the given row
	// This method will insert null rows as necessary
	protected List makeSpace(Coordinate newCoordinate) 
    {
		int newCol = newCoordinate.getNewCol();
		int newRow = newCoordinate.getNewRow();
		
		// Find the column. Note that a new column will never be created
		List column = this.columnsList[newCol];
		if(newRow + 1 > column.size()) 
        {
			// Need to add rows
			for(int i = column.size(); i < newRow + 1; i++) 
            {
				column.add(null);
			}
		}
		return column;
	}
	
	public Coordinate add(Fragment fragment, Coordinate coordinate) throws PortletPlacementException 
    {
        int col = coordinate.getNewCol();
        int row = coordinate.getNewRow();
        
        if (this.numberOfColumns == -1)
        {
            this.numberOfColumns = 1;
            this.columnsList = new Vector[this.numberOfColumns];
            col = 0;
        }        
        if (col > this.numberOfColumns)
        {            
            // expand
            this.numberOfColumns++;
            col = this.numberOfColumns - 1;
            Vector [] temp = new Vector[this.numberOfColumns];
            for (int ix = 0; ix < this.numberOfColumns - 1; ix++)
                temp[ix] = this.columnsList[ix];
            temp[col] = new Vector();
            this.columnsList = temp;
        }
        
        Vector column = this.columnsList[col];
        if (column != null)
        {
            for (int ix = 0; ix < column.size(); ix++)
            {                
                Fragment frag = (Fragment)column.get(ix);
                if (frag == null)
                    continue;
                Coordinate c = (Coordinate)this.fragmentCoordinateMap.get(frag);
                if (c == null)
                    continue;
                if (c.getNewCol() == row)
                {
                    row++;
                }
                
            }
            column.add(row, fragment);
            Coordinate newCoord = new CoordinateImpl(col, row, col, row);
            this.fragmentCoordinateMap.put(fragment, newCoord);
            return newCoord;
        }
        return coordinate;
	}
	
	// Adds an existing fragment to the coordinate position
	protected Coordinate addInternal(Fragment fragment, Coordinate coordinate) 
    throws PortletPlacementException 
    {
		int newCol = coordinate.getNewCol();
		int newRow = coordinate.getNewRow();
		
		// Check to see if the column exists
		if(newCol < 0 || newCol > this.columnsList.length) 
        {
			throw new PortletPlacementException("column out of bounds" + fragment.getName());
		}
		
		Vector columnArray = (Vector)this.columnsList[newCol];

		// Make sure the list has enough room for the set
		prepareList(columnArray, newRow);
		
		columnArray.setElementAt(fragment, newRow);
		
		// Add the fragment to the hash map
		this.fragmentCoordinateMap.put(fragment, coordinate);
		
		return coordinate;
	}

	public Fragment getFragment(String fragmentId) throws PortletPlacementException 
    {
		return (Fragment)this.fragmentMap.get(fragmentId);
	}
	
	public Fragment getFragmentAtOldCoordinate(Coordinate coordinate) throws PortletPlacementException 
    {
		return getFragmentAtCoordinate(coordinate, true);
	}

	public Fragment getFragmentAtNewCoordinate(Coordinate coordinate) throws PortletPlacementException 
    {
		return getFragmentAtCoordinate(coordinate, false);
	}

	protected Fragment getFragmentAtCoordinate(Coordinate coordinate, boolean isOld) throws PortletPlacementException 
    {
		int col = -1;
		int row = -1;
		if (isOld == true) 
        {
			col = coordinate.getOldCol();
			row = coordinate.getOldRow();
		} else 
        {
			col = coordinate.getNewCol();
			row = coordinate.getNewRow();
		}
		
		// Do some sanity checking about the request
		if(col < 0 || col > this.columnsList.length) 
        {
			throw new PortletPlacementException("requested column is out of bounds");
		}
		
		// Get the array list associated with the column
		Vector columnArray = this.columnsList[col];
		if(row < 0 || row > columnArray.size()) 
        {
			throw new PortletPlacementException("requested row is out of bounds");
		}
		
		return (Fragment)columnArray.get(row);
	}
	
	public Fragment getFragmentById(String fragmentId) throws PortletPlacementException 
    {
		return (Fragment)this.fragmentMap.get(fragmentId);
	}

	public int getNumberColumns() throws PortletPlacementException 
    {
		return this.columnsList.length;
	}

	public int getNumberRows(int col) throws PortletPlacementException 
    {
		// Sanity check the column
		if(col < 0 || col > this.columnsList.length) 
        {
			throw new PortletPlacementException("column out of bounds");
		}
		
		return this.columnsList[col].size();
	}

	public Coordinate moveAbsolute(Fragment fragment, Coordinate newCoordinate) 
    throws PortletPlacementException 
    {
		// Find the fragment
		Coordinate oldCoordinate = (Coordinate)this.fragmentCoordinateMap.get(fragment);
		if(oldCoordinate == null) 
        {
			throw new PortletPlacementException("could not find fragment");
		}
		
		// Save the old coordinates
		int oldCol = oldCoordinate.getOldCol();
		int oldRow = oldCoordinate.getOldRow();

		// Create a new coordinate object with both the old and new positions
		int newCol = newCoordinate.getNewCol();
		int newRow = newCoordinate.getNewRow();
		
		// Make sure there is a place for the move
		//List oldRowList = makeSpace(newCoordinate);

		List oldRowList = this.columnsList[oldCol];
		
		// Remove the fragment from it's old position
		oldRowList.remove(oldRow);

		// The next two lines must occur after the remove above.  This is
		// because the new and old columns might be the same and the remove
		// will change the number of rows
		List newRowList = this.columnsList[newCol];
		int numRowsNewColumn = newRowList.size();
		
		// Decide whether an insert or an add is appropriate
		if(newRow > (numRowsNewColumn - 1)) 
        {
			newRow = numRowsNewColumn;
			// Add a new row
			newRowList.add(fragment);
		} 
        else 
        {
			// Insert the fragment at the new position
			((Vector)newRowList).insertElementAt(fragment, newRow);		
		}

        //debugFragments("move absolute ");
        
		// New coordinates after moving
		return new CoordinateImpl(oldCol, oldRow, newCol, newRow);
	}

	protected Coordinate moveDirection(Fragment fragment, int deltaCol, int deltaRow) 
    throws PortletPlacementException 
    {
		// Find the fragment
		Coordinate foundCoordinate = (Coordinate)this.fragmentCoordinateMap.get(fragment);
		if(foundCoordinate == null) 
        {
			throw new PortletPlacementException("could not find fragment");
		}

		// Check the coordinates to make sure that there is room to move down
		int oldCol = foundCoordinate.getOldCol();
		int oldRow = foundCoordinate.getOldRow();
		
		Vector columnArray = (Vector)this.columnsList[oldCol];
		
		// Check the row and column boundaries to make sure there is room
		// to do the move
		if((oldRow + deltaRow + 1 > columnArray.size()) || ((oldRow + deltaRow) < 0) ||
		   (oldCol + deltaCol + 1 > this.columnsList.length) || ((oldCol + deltaCol) < 0)) 
        {
			// Out of bounds, don't do the move
			Coordinate c = new CoordinateImpl(oldCol, oldRow, oldCol, oldRow);
            //debugFragments("move direction (1)");
            return c;
		}
        else 
        {
			Coordinate c = moveAbsolute(fragment, new CoordinateImpl(oldCol, oldRow, oldCol + deltaCol, oldRow + deltaRow));
            //debugFragments("move direction (2)");
            return c;
		}        
	}
	
	public Coordinate moveDown(Fragment fragment) throws PortletPlacementException 
    {
		return moveDirection(fragment, 0, 1);
	}

	public Coordinate moveUp(Fragment fragment) throws PortletPlacementException 
    {
		return moveDirection(fragment, 0, -1);
	}

	public Coordinate moveLeft(Fragment fragment) throws PortletPlacementException 
    {
		return moveDirection(fragment, -1, 0);
	}

	public Coordinate moveRight(Fragment fragment) throws PortletPlacementException 
    {
		return moveDirection(fragment, 1, 0);
	}

	public Coordinate remove(Fragment fragment) throws PortletPlacementException 
    {
		// Locate the fragment
		Coordinate coordinate = (Coordinate)this.fragmentCoordinateMap.get(fragment);
		if(coordinate == null) 
        {
			throw new PortletPlacementException("fragment not found:" + fragment.getName());
		}
		
		int col = coordinate.getOldCol();
		int row = coordinate.getOldRow();
		
		if(col < 0 || col > this.columnsList.length) 
        {
			throw new PortletPlacementException("column out of bounds:" + fragment.getName());
		}
		
		Vector columnArray = (Vector)this.columnsList[col];
		if(row < 0 || row > columnArray.size()) 
        {
			throw new PortletPlacementException("row out of bounds:" + fragment.getName());
		}
		
		// Remove the fragment from the array
		columnArray.remove(row);
		
		// Remove the fragment from the hashmap
		this.fragmentCoordinateMap.remove(fragment);
		this.fragmentMap.remove(fragment.getId());
		
		return coordinate;
	}

}
