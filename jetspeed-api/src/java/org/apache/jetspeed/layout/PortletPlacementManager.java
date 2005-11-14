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

/**
 * Handles portlet placement for client such as AJAX client side 
 *
 * @author <a>David Gurney</a>
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: $
 */
public interface PortletPlacementManager 
{
	public Coordinate moveAbs(Fragment p_oFragment, Coordinate p_oNewCoordinate) throws PortletPlacementException;
	public Coordinate moveUp(Fragment p_oFragment) throws PortletPlacementException;
	public Coordinate moveDown(Fragment p_oFragment) throws PortletPlacementException;
	public Coordinate moveLeft(Fragment p_oFragment) throws PortletPlacementException;
	public Coordinate moveRight(Fragment p_oFragment) throws PortletPlacementException;
	public Coordinate add(String p_oPortletDefinitionID, Coordinate p_oNewCoordinate) throws PortletPlacementException;
	public Coordinate remove(Fragment p_oFragment) throws PortletPlacementException;
	public int getNumCols() throws PortletPlacementException;
	public int getNumRows(int p_iCol) throws PortletPlacementException;
	public Fragment getFragmentAtNewCoordinate(Coordinate p_oCoordinate) throws PortletPlacementException;
	public Fragment getFragmentAtOldCoordinate(Coordinate p_oCoordinate) throws PortletPlacementException;
	public Fragment getFragmentById(String p_sId) throws PortletPlacementException;
}
