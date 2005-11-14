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
import org.apache.jetspeed.layout.PortletPlacementManager;
import org.apache.jetspeed.om.page.Fragment;
import org.apache.jetspeed.om.page.Page;
import org.apache.jetspeed.request.RequestContext;

/**
 * Portal Placement Manager
 * 
 * The purpose of the object is to provide an API that
 * can be used to move a portlet fragment on the page.
 * This includes moving, adding, removing and getting
 * information about portlets that are on the page and
 * portlets that are available to be added to the page.
 * 
 * An important not about this object:
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
public class PortletPlacementManagerImpl implements PortletPlacementManager 
{
    private static final String COLUMN = "column";
    private static final String ROW = "row";

    /** Logger */
    private Log m_oLog = LogFactory.getLog(PortletPlacementManagerImpl.class);

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
	protected Vector[] m_oColList = null;
	
	// Used as a convience when looking up a particular fragment
	//
	// Key is Fragment, value is a Coordinate object
	protected Map m_oFragHashMap = new HashMap();
	
	// Used as a convience when looking up a particular fragment by id
	//
	// Key is the Fragment id (String), value is the Fragment
	protected Map m_oFragIdHashMap = new HashMap();
	
	// Number of columns found
	protected int m_iNumCol = -1;
	
	public PortletPlacementManagerImpl(RequestContext p_oRequestContext) throws PortletPlacementException {
		init(p_oRequestContext);
	}
	
	// Initialize the data structures by getting the fragments
	// from the page manager
	protected void init(RequestContext p_oRequestContext) throws PortletPlacementException {
        Page a_oPage = p_oRequestContext.getPage();
        Fragment a_oRootFragment = a_oPage.getRootFragment();
        
        // Recursively process each fragment
        processFragment(a_oRootFragment);

        // The final step is to populate the array with the fragments
		populateArray();
	}
	
	/**
	 * Evaluate each portlet fragment and populate the internal data
	 * structures
	 */
	protected void processFragment(Fragment p_oFragment) throws PortletPlacementException {
		// Process this fragment, then its children
		if(p_oFragment != null) {
			// Only process portlet fragments
			if(p_oFragment.getType().equalsIgnoreCase("portlet")) {
				// Get the column and row of this fragment
				int a_iCol = getFragmentCol(p_oFragment);
				int a_iRow = getFragmentRow(p_oFragment);
		        
		        if(a_iCol > -1 && a_iRow > -1) {
		        	// Add this fragment to the data structures
		        	addFragmentInternal(p_oFragment, a_iCol, a_iRow);
		        } else {
		        	// log a message
		        	m_oLog.error("fragment found without a row or column property, skipping");
		        }
			}
			
			// Process the children
			List a_oChildren = p_oFragment.getFragments();
			for(int i = 0; i < a_oChildren.size(); i++) {
				Fragment a_oChildFrag = (Fragment)a_oChildren.get(i);
				
				if(a_oChildFrag != null) {
					processFragment(a_oChildFrag);
				}
			}
		}		
	}
	
	// Helper method
	// The implementation will probably change to get this information
	// directly from the fragment via fragment.getFragmentCol()
	protected int getFragmentRow(Fragment p_oFragment) {
        return p_oFragment.getLayoutRow();
	}
	
	// The implementation will probably change to get this information
	// directly from the fragment via fragment.getFragmentRow()
	protected int getFragmentCol(Fragment p_oFragment) {
        return p_oFragment.getLayoutColumn();
	}
	
	// Adds the fragment to the internal data structures
	protected void addFragmentInternal(Fragment p_oFragment, int p_iCol, int p_iRow) {
		// Create a Coordinate object to hold the row and column
		CoordinateImpl a_oCoordinate = new CoordinateImpl(p_iCol, p_iRow);
		
		// Save the fragment in the lookup hash
		m_oFragHashMap.put(p_oFragment, a_oCoordinate);
		m_oFragIdHashMap.put(p_oFragment.getId(), p_oFragment);
		
		// Establish the maximum column number
		if(p_iCol > m_iNumCol) {
			m_iNumCol = p_iCol;
		}
	}
	
	/**
	 * Now that we know the number of columns, the array can be
	 * constructed and populated
	 */
	protected void populateArray() throws PortletPlacementException {
		if(m_iNumCol == -1) {
			throw new PortletPlacementException("no columns found");
		}
		
		// Allocate the memory for the array of ArrayLists
		// Add one since it is zero based
		m_oColList = new Vector[m_iNumCol + 1];
		
		// Put an array list into each index
		for(int i = 0; i < m_iNumCol + 1; i++) {
			m_oColList[i] = new Vector();
		}
		
		// Get all of the fragments from the hashmap
		Set a_oKeys = m_oFragHashMap.keySet();
		Iterator a_oKeyIter = a_oKeys.iterator();
		while(a_oKeyIter.hasNext()) {
			// The key is a Fragment
			Fragment a_oFrag = (Fragment) a_oKeyIter.next();
			
			// Get the Coordinate associated with this fragment
			Coordinate a_oCoordinate = (Coordinate)m_oFragHashMap.get(a_oFrag);
			
			// Make sure we have both
			if(a_oFrag != null && a_oCoordinate != null) {
				// Get the array list for the column
				Vector a_oColList = m_oColList[a_oCoordinate.getOldCol()];
				
				int a_iRow = a_oCoordinate.getOldRow();
				
				// Before setting the fragment in the array it might
				// be necessary to add blank rows before this row
				// An ArrayList can only set an element that already exists
				prepareList(a_oColList, a_iRow);
				
				// Place the fragment in the array list using the row
				a_oColList.set(a_iRow, a_oFrag);
			}
		}
	}
	
	// Ensures that the array list has at least p_iRow number of rows
	protected void prepareList(Vector p_oList, int p_iRow) {
		if(p_oList != null) {
			int a_iSize = p_oList.size();
			if(p_iRow + 1 > a_iSize) {
				// Add one to the row since it is zero based
				for(int i = a_iSize; i < p_iRow + 1; i++) {
					p_oList.add(null);
				}
			}
		}
	}
	
	// Ensures that there is room for the fragment at the given row
	// This method will insert null rows as necessary
	protected List makeSpace(Coordinate p_oNewCoordinate) {
		int a_iNewCol = p_oNewCoordinate.getNewCol();
		int a_iNewRow = p_oNewCoordinate.getNewRow();
		
		// Find the column. Note that a new column will never be created
		List a_oColumn = m_oColList[a_iNewCol];
		if(a_iNewRow + 1 > a_oColumn.size()) {
			// Need to add rows
			for(int i = a_oColumn.size(); i < a_iNewRow + 1; i++) {
				a_oColumn.add(null);
			}
		}
		return a_oColumn;
	}
	
	public Coordinate add(String p_oPortletDefinitionID, Coordinate p_oCoordinate) throws PortletPlacementException {
//        try
//        {
//            Fragment fragment = pageManager.newFragment();
//            fragment.setType(Fragment.PORTLET);
//            fragment.setId(generator.getNextPeid());
//            fragment.setName(portletId);
//            
//            Page page = pageManager.getContentPage(pageId);
//            // WARNING: under construction
//            // this is prototype and very dependent on a single depth fragment structure            
//            Fragment root = page.getRootFragment();
//            root.getFragments().add(fragment);
//            pageManager.updatePage(page);            
//        }
//        catch (Exception e)
//        {
//            log.error("failed to add portlet " + portletId + " to page: " + pageId);
//        }
		return null;
	}
	
	// Adds an existing fragment to the coordinate position
	protected Coordinate addInternal(Fragment p_oFragment, Coordinate p_oCoordinate) throws PortletPlacementException {
		int a_iNewCol = p_oCoordinate.getNewCol();
		int a_iNewRow = p_oCoordinate.getNewRow();
		
		// Check to see if the column exists
		if(a_iNewCol < 0 || a_iNewCol > m_oColList.length) {
			throw new PortletPlacementException("column out of bounds" + p_oFragment.getName());
		}
		
		Vector a_oColList = (Vector)m_oColList[a_iNewCol];

		// Make sure the list has enough room for the set
		prepareList(a_oColList, a_iNewRow);
		
		a_oColList.setElementAt(p_oFragment, a_iNewRow);
		
		// Add the fragment to the hash map
		m_oFragHashMap.put(p_oFragment, p_oCoordinate);
		
		return p_oCoordinate;
	}

	public Fragment getFragment(String p_sId) throws PortletPlacementException {
		return (Fragment)m_oFragIdHashMap.get(p_sId);
	}
	
	public Fragment getFragmentAtOldCoordinate(Coordinate p_oCoordinate) throws PortletPlacementException {
		return getFragmentAtCoordinate(p_oCoordinate, true);
	}

	public Fragment getFragmentAtNewCoordinate(Coordinate p_oCoordinate) throws PortletPlacementException {
		return getFragmentAtCoordinate(p_oCoordinate, false);
	}

	protected Fragment getFragmentAtCoordinate(Coordinate p_oCoordinate, boolean p_bOld) throws PortletPlacementException {
		int a_iCol = -1;
		int a_iRow = -1;
		if(p_bOld == true) {
			a_iCol = p_oCoordinate.getOldCol();
			a_iRow = p_oCoordinate.getOldRow();
		} else {
			a_iCol = p_oCoordinate.getNewCol();
			a_iRow = p_oCoordinate.getNewRow();
		}
		
		// Do some sanity checking about the request
		if(a_iCol < 0 || a_iCol > m_oColList.length) {
			throw new PortletPlacementException("requested column is out of bounds");
		}
		
		// Get the array list associated with the column
		Vector a_oColList = m_oColList[a_iCol];
		if(a_iRow < 0 || a_iRow > a_oColList.size()) {
			throw new PortletPlacementException("requested row is out of bounds");
		}
		
		return (Fragment)a_oColList.get(a_iRow);
	}
	
	public Fragment getFragmentById(String p_sFragmentId) throws PortletPlacementException {
		return (Fragment)m_oFragIdHashMap.get(p_sFragmentId);
	}

	public int getNumCols() throws PortletPlacementException {
		return m_oColList.length;
	}

	public int getNumRows(int p_iCol) throws PortletPlacementException {
		// Sanity check the column
		if(p_iCol < 0 || p_iCol > m_oColList.length) {
			throw new PortletPlacementException("column out of bounds");
		}
		
		return m_oColList[p_iCol].size();
	}

	public Coordinate moveAbs(Fragment p_oFragment, Coordinate p_oNewCoordinate) throws PortletPlacementException {
		// Find the fragment
		Coordinate a_oOldCoordinate = (Coordinate)m_oFragHashMap.get(p_oFragment);
		if(a_oOldCoordinate == null) {
			throw new PortletPlacementException("could not find fragment");
		}
		
		// Save the old coordinates
		int a_iOldCol = a_oOldCoordinate.getOldCol();
		int a_iOldRow = a_oOldCoordinate.getOldRow();

		// Create a new coordinate object with both the old and new positions
		int a_iNewCol = p_oNewCoordinate.getNewCol();
		int a_iNewRow = p_oNewCoordinate.getNewRow();
		
		// Make sure there is a place for the move
		//List a_oRow = makeSpace(p_oNewCoordinate);

		List a_oOldRow = m_oColList[a_iOldCol];
		
		// Remove the fragment from it's old position
		a_oOldRow.remove(a_iOldRow);

		// The next two lines must occur after the remove above.  This is
		// because the new and old columns might be the same and the remove
		// will change the number of rows
		List a_oNewRow = m_oColList[a_iNewCol];
		int a_iNumRowsNewColumn = a_oNewRow.size();
		
		// Decide whether an insert or an add is appropriate
		if(a_iNewRow > (a_iNumRowsNewColumn - 1)) {
			a_iNewRow = a_iNumRowsNewColumn;
			// Add a new row
			a_oNewRow.add(p_oFragment);
		} else {
			// Insert the fragment at the new position
			((Vector)a_oNewRow).insertElementAt(p_oFragment, a_iNewRow);		
		}

		// New coordinates after moving
		Coordinate a_oNewCoordinate = new CoordinateImpl(a_iOldCol, a_iOldRow, a_iNewCol, a_iNewRow);
		
		return a_oNewCoordinate;
	}

	protected Coordinate moveDirection(Fragment p_oFragment, int p_iDeltaCol, int p_iDeltaRow) throws PortletPlacementException {
		// Find the fragment
		Coordinate a_oFoundCoordinate = (Coordinate)m_oFragHashMap.get(p_oFragment);
		if(a_oFoundCoordinate == null) {
			throw new PortletPlacementException("could not find fragment");
		}

		// Check the coordinates to make sure that there is room to move down
		int a_iOldCol = a_oFoundCoordinate.getOldCol();
		int a_iOldRow = a_oFoundCoordinate.getOldRow();
		
		Vector a_oColList = (Vector)m_oColList[a_iOldCol];
		
		// Check the row and column boundaries to make sure there is room
		// to do the move
		if((a_iOldRow + p_iDeltaRow + 1 > a_oColList.size()) || ((a_iOldRow + p_iDeltaRow) < 0) ||
		   (a_iOldCol + p_iDeltaCol + 1 > m_oColList.length) || ((a_iOldCol + p_iDeltaCol) < 0)) {
			// Out of bounds, don't do the move
			return new CoordinateImpl(a_iOldCol, a_iOldRow, a_iOldCol, a_iOldRow);
		}else {
			return moveAbs(p_oFragment, new CoordinateImpl(a_iOldCol, a_iOldRow, a_iOldCol + p_iDeltaCol, a_iOldRow + p_iDeltaRow));
		}
	}
	
	public Coordinate moveDown(Fragment p_oFragment) throws PortletPlacementException {
		return moveDirection(p_oFragment, 0, 1);
	}

	public Coordinate moveUp(Fragment p_oFragment) throws PortletPlacementException {
		return moveDirection(p_oFragment, 0, -1);
	}

	public Coordinate moveLeft(Fragment p_oFragment) throws PortletPlacementException {
		return moveDirection(p_oFragment, -1, 0);
	}

	public Coordinate moveRight(Fragment p_oFragment) throws PortletPlacementException {
		return moveDirection(p_oFragment, 1, 0);
	}

	public Coordinate remove(Fragment p_oFragment) throws PortletPlacementException {
		// Locate the fragment
		Coordinate a_oCoordinate = (Coordinate)m_oFragHashMap.get(p_oFragment);
		if(a_oCoordinate == null) {
			throw new PortletPlacementException("fragment not found:" + p_oFragment.getName());
		}
		
		int a_iCol = a_oCoordinate.getOldCol();
		int a_iRow = a_oCoordinate.getOldRow();
		
		if(a_iCol < 0 || a_iCol > m_oColList.length) {
			throw new PortletPlacementException("column out of bounds:" + p_oFragment.getName());
		}
		
		Vector a_oColList = (Vector)m_oColList[a_iCol];
		if(a_iRow < 0 || a_iRow > a_oColList.size()) {
			throw new PortletPlacementException("row out of bounds:" + p_oFragment.getName());
		}
		
		// Remove the fragment from the array
		a_oColList.remove(a_iRow);
		
		// Remove the fragment from the hashmap
		m_oFragHashMap.remove(p_oFragment);
		m_oFragIdHashMap.remove(p_oFragment.getId());
		
		return a_oCoordinate;
	}

}
