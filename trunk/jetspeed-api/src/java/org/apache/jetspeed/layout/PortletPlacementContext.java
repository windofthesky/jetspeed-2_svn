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
package org.apache.jetspeed.layout;

import org.apache.jetspeed.om.page.Fragment;
import org.apache.jetspeed.om.page.Page;

/**
 * Handles portlet placement for client such as AJAX client side
 * on a per request basis. The context is associated with a request context,
 * thus it is only valid for the span of a request.  
 *
 * @author <a>David Gurney</a>
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: $
 */
public interface PortletPlacementContext 
{
	/**
     * Move a portlet fragment to a new absolute position as specified in the Coordinate parameter.
     * 
	 * @param fragment The fragment to be moved.
	 * @param coordinate The specification of the new absolute coordinate
	 * @return new coordinate location of the portlet
	 * @throws PortletPlacementException
	 */
	public Coordinate moveAbsolute(Fragment fragment, Coordinate coordinate) throws PortletPlacementException;
    
	/**
     * Move a portlet relative to its current position UP one row.
     * 
     * @param fragment The fragment to be moved.
     * @return new coordinate location of the portlet
	 * @throws PortletPlacementException
	 */
	public Coordinate moveUp(Fragment fragment) throws PortletPlacementException;

    /**
     * Move a portlet relative to its current position DOWN one row.
     * 
     * @param fragment The fragment to be moved.
     * @return new coordinate location of the portlet
     * @throws PortletPlacementException
     */    
	public Coordinate moveDown(Fragment fragment) throws PortletPlacementException;
    
    /**
     * Move a portlet relative to its current position LEFT one column.
     * 
     * @param fragment The fragment to be moved.
     * @return new coordinate location of the portlet
     * @throws PortletPlacementException
     */        
	public Coordinate moveLeft(Fragment fragment) throws PortletPlacementException;
    
    /**
     * Move a portlet relative to its current position RIGHT one column.
     * 
     * @param fragment The fragment to be moved.
     * @return new coordinate location of the portlet
     * @throws PortletPlacementException
     */            
	public Coordinate moveRight(Fragment fragment) throws PortletPlacementException;
    
	/**
     * Add a portlet to its managed page.
     * 
	 * @param fragment The Fragment to add
	 * @param coordinate The coordinate where to place the new portlet
	 * @return
	 * @throws PortletPlacementException
	 */
	public Coordinate add(Fragment fragment, Coordinate coordinate) throws PortletPlacementException;
    
	/**
     * Remove the specified fragment.
	 * @param fragment
	 * @return
	 * @throws PortletPlacementException
	 */
	public Coordinate remove(Fragment fragment) throws PortletPlacementException;
    
    /**
     * retrieve the number of columns for the managed layout.
     * 
     * @return the number of columns in the manged layout
     * @throws PortletPlacementException
     */
	public int getNumberColumns() throws PortletPlacementException;
    
    /**
     * retrieve the number of rows for the managed layout at the given column.
     * 
     * @param column the column to retrieve the number of rows for
     * @return the number of rows for the given column
     * @throws PortletPlacementException
     */    
	public int getNumberRows(int column) throws PortletPlacementException;
    
	/**
     * Retrieve a portlet fragment for the given coordinate.
     *  
	 * @param coordinate the coordinate associated to a fragment.
	 * @return the fragment associated to the given coordinate
	 * @throws PortletPlacementException
	 */
	public Fragment getFragmentAtNewCoordinate(Coordinate coordinate) throws PortletPlacementException;
    
	/**
     * Retrieve the old portlet fragment for the given coordinate (prior to placement).
     * 
	 * @param coordinate the coordinate associated to a fragment.
	 * @return the fragment associated to the given coordinate
	 * @throws PortletPlacementException
	 */
	public Fragment getFragmentAtOldCoordinate(Coordinate coordinate) throws PortletPlacementException;
    
	/**
     * Retrieve a fragment by fragment id.
     * 
	 * @param fragmentId a string key for a fragment managed on this layout.
	 * @return The fragment associated with the given fragment id.
	 * @throws PortletPlacementException
	 */
	public Fragment getFragmentById(String fragmentId) throws PortletPlacementException;
    
    /**
     * Takes the internal portlet placement state and writes it back
     * out to the root fragment for the managed page layout.
     * 
     * @return the managed page layout with updated fragment state.
     */
    public Page syncPageFragments();
        
}
