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

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.JetspeedActions;
import org.apache.jetspeed.ajax.AJAXException;
import org.apache.jetspeed.ajax.AjaxAction;
import org.apache.jetspeed.ajax.AjaxBuilder;
import org.apache.jetspeed.layout.Coordinate;
import org.apache.jetspeed.layout.PortletActionSecurityBehavior;
import org.apache.jetspeed.layout.PortletPlacementContext;
import org.apache.jetspeed.om.page.Fragment;
import org.apache.jetspeed.om.page.Page;
import org.apache.jetspeed.page.PageManager;
import org.apache.jetspeed.request.RequestContext;

/**
 * Move Portlet portlet placement action
 *
 * AJAX Parameters: 
 *    id = the fragment id of the portlet to move
 *    page = (implied in the URL)
 * Additional Absolute Parameters:  
 *    row = the new row to move to
 *    col = the new column to move to
 * Additional Relative Parameters: (move left, right, up, down)
 *    none
 *    
 * @author <a>David Gurney</a>
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: $
 */
public class MovePortletAction 
    extends BasePortletAction 
    implements AjaxAction, AjaxBuilder, Constants
{
    protected Log log = LogFactory.getLog(MovePortletAction.class);
    private int iMoveType = -1;
    private String sMoveType = null;

    public MovePortletAction(String template, 
            String errorTemplate, 
            String sMoveType)
    throws AJAXException    
    {
        this(template, errorTemplate, sMoveType, null, null);
    }
    
    public MovePortletAction(String template, 
                             String errorTemplate, 
                             String sMoveType,
                             PageManager pageManager,
                             PortletActionSecurityBehavior securityBehavior)
    throws AJAXException
    {
        super(template, errorTemplate, pageManager, securityBehavior);
        setMoveType(sMoveType);
    }

    // Convert the move type into an integer
    public void setMoveType(String p_sMoveType) throws AJAXException
    {
        sMoveType = p_sMoveType;

        if (p_sMoveType.equalsIgnoreCase("moveabs"))
        {
            iMoveType = ABS;
        } 
        else if (p_sMoveType.equalsIgnoreCase("moveup"))
        {
            iMoveType = UP;
        } 
        else if (p_sMoveType.equalsIgnoreCase("movedown"))
        {
            iMoveType = DOWN;
        } 
        else if (p_sMoveType.equalsIgnoreCase("moveleft"))
        {
            iMoveType = LEFT;
        } 
        else if (p_sMoveType.equalsIgnoreCase("moveright"))
        {
            iMoveType = RIGHT;
        }
        else if (p_sMoveType.equalsIgnoreCase("move"))
        {
            iMoveType = CARTESIAN;
        }
        else
        {
            throw new AJAXException("invalid move type of:" + p_sMoveType);
        }
    }

    public boolean run(RequestContext requestContext, Map resultMap)
    {
        boolean success = true;
        String status = "success";
        try
        {
            resultMap.put(ACTION, sMoveType);
            // Get the necessary parameters off of the request
            String portletId = requestContext.getRequestParameter(PORTLETID);
            if (portletId == null) 
            { 
                throw new Exception("portlet id not provided"); 
            }            
            resultMap.put(PORTLETID, portletId);            
            if (false == checkAccess(requestContext, JetspeedActions.EDIT))
            {
                Page page = requestContext.getPage();
                Fragment fragment = page.getFragmentById(portletId);
                if (fragment == null)
                {
                    success = false;
                    resultMap.put(REASON, "Fragment not found");
                    return success;                    
                }
                int column = fragment.getLayoutColumn();
                int row = fragment.getLayoutRow();                
                if (!createNewPageOnEdit(requestContext))
                {
                    success = false;
                    resultMap.put(REASON, "Insufficient access to edit page");
                    return success;
                }
                status = "refresh";
                // translate old portlet id to new portlet id
                Fragment newFragment = getFragmentIdFromLocation(row, column, requestContext.getPage());
                if (newFragment == null)
                {
                    success = false;
                    resultMap.put(REASON, "Failed to find new fragment");
                    return success;                    
                }                
                portletId = newFragment.getId();
            }            
            PortletPlacementContext placement = new PortletPlacementContextImpl(requestContext);
            Fragment fragment = placement.getFragmentById(portletId);
            Coordinate returnCoordinate = null;
            float oldX = 0f, oldY = 0f, oldZ = 0f, oldWidth = 0f, oldHeight = 0f;
            float x = -1f, y = -1f, z = -1f, width = -1f, height = -1f;
            
            // Only required for moveabs
            if (iMoveType == ABS)
            {
                String a_sCol = requestContext.getRequestParameter(COL);
                String a_sRow = requestContext.getRequestParameter(ROW);

                // Convert the col and row into integers
                int a_iCol = Integer.parseInt(a_sCol);
                int a_iRow = Integer.parseInt(a_sRow);

                Coordinate a_oCoordinate = new CoordinateImpl(0, 0, a_iCol,
                        a_iRow);
                returnCoordinate = placement.moveAbsolute(fragment, a_oCoordinate);
            } 
            else if (iMoveType == LEFT)
            {
                returnCoordinate = placement.moveLeft(fragment);
            } 
            else if (iMoveType == RIGHT)
            {
                returnCoordinate = placement.moveRight(fragment);
            } 
            else if (iMoveType == UP)
            {
                returnCoordinate = placement.moveUp(fragment);
            } 
            else if (iMoveType == DOWN)
            {
                returnCoordinate = placement.moveDown(fragment);
            }
            else if (iMoveType == CARTESIAN)
            {
                String sx = requestContext.getRequestParameter(X);
                String sy = requestContext.getRequestParameter(Y);
                String sz = requestContext.getRequestParameter(Z);
                String sWidth = requestContext.getRequestParameter(WIDTH);
                String sHeight = requestContext.getRequestParameter(HEIGHT);
                if (sx != null)
                {
                    oldX = fragment.getLayoutX();
                    x = Float.parseFloat(sx); 
                    fragment.setLayoutX(x);
                }
                if (sy != null)
                {
                    oldY = fragment.getLayoutY();                    
                    y = Float.parseFloat(sy); 
                    fragment.setLayoutY(y);
                }                
                if (sz != null)
                {
                    oldZ = fragment.getLayoutZ();                    
                    z = Float.parseFloat(sz); 
                    fragment.setLayoutZ(z);
                }                
                if (sWidth != null)
                {
                    oldWidth = fragment.getLayoutWidth();                    
                    width = Float.parseFloat(sWidth); 
                    fragment.setLayoutWidth(width);
                }
                if (sHeight != null)
                {
                    oldHeight = fragment.getLayoutHeight();                    
                    height = Float.parseFloat(sHeight); 
                    fragment.setLayoutHeight(height);
                }                
            }
            // synchronize back to the page layout root fragment
            Page page = placement.syncPageFragments();
            
            if (pageManager != null)
                pageManager.updatePage(page);
            
            resultMap.put(STATUS, status);
            resultMap.put(PORTLETID, portletId);
            if (iMoveType == CARTESIAN)
            {
                putCartesianResult(resultMap, x, oldX, X, OLD_X);
                putCartesianResult(resultMap, y, oldY, Y, OLD_Y);                
                putCartesianResult(resultMap, z, oldZ, Z, OLD_Z);
                putCartesianResult(resultMap, width, oldWidth, WIDTH, OLD_WIDTH);
                putCartesianResult(resultMap, height, oldHeight, HEIGHT, OLD_HEIGHT);
            }
            else
            {
                // Need to determine what the old col and row were
                resultMap.put(OLDCOL, String.valueOf(returnCoordinate
                        .getOldCol()));
                resultMap.put(OLDROW, String.valueOf(returnCoordinate
                        .getOldRow()));
                // Need to determine what the new col and row were
                resultMap.put(NEWCOL, String.valueOf(returnCoordinate
                        .getNewCol()));
                resultMap.put(NEWROW, String.valueOf(returnCoordinate
                        .getNewRow()));                
            }
        } 
        catch (Exception e)
        {
            // Log the exception
            log.error("exception while moving a portlet", e);
            resultMap.put(REASON, e.toString());
            // Return a failure indicator
            success = false;
        }

        return success;
    }

    protected void putCartesianResult(Map resultMap, float value, float oldValue, String name, String oldName)
    {    
        if (value != -1F)
        {
            resultMap.put(oldName, new Float(oldValue));
            resultMap.put(name, new Float(value));
        }
    }    
}
