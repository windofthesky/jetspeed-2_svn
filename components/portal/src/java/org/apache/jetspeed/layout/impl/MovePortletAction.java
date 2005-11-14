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
import org.apache.jetspeed.ajax.AJAXException;
import org.apache.jetspeed.ajax.AjaxAction;
import org.apache.jetspeed.ajax.AjaxBuilder;
import org.apache.jetspeed.layout.Coordinate;
import org.apache.jetspeed.layout.PortletPlacementManager;
import org.apache.jetspeed.om.page.Fragment;
import org.apache.jetspeed.request.RequestContext;

/**
 * Move Portlet portlet placement action
 *
 * @author <a>David Gurney</a>
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: $
 */
public class MovePortletAction 
    extends BasePortletAction 
    implements AjaxAction, AjaxBuilder, Constants
{
    /** Logger */
    protected Log log = LogFactory.getLog(MovePortletAction.class);

    private int iMoveType = -1;

    private String sMoveType = null;

    public MovePortletAction(String template, String errorTemplate, String sMoveType)
    throws AJAXException
    {
        super(template, errorTemplate);
        setMoveType(sMoveType);
    }

    // Convert the move type into an integer
    public void setMoveType(String p_sMoveType) throws AJAXException
    {
        sMoveType = p_sMoveType;

        if (p_sMoveType.equalsIgnoreCase("moveabs"))
        {
            iMoveType = ABS;
        } else if (p_sMoveType.equalsIgnoreCase("moveup"))
        {
            iMoveType = UP;
        } else if (p_sMoveType.equalsIgnoreCase("movedown"))
        {
            iMoveType = DOWN;
        } else if (p_sMoveType.equalsIgnoreCase("moveleft"))
        {
            iMoveType = LEFT;
        } else if (p_sMoveType.equalsIgnoreCase("moveright"))
        {
            iMoveType = RIGHT;
        } else
        {
            throw new AJAXException("invalid move type of:" + p_sMoveType);
        }
    }

    public boolean run(RequestContext requestContext, Map resultMap)
    {
        boolean success = true;

        try
        {
            resultMap.put(ACTION, sMoveType);

            // Get the necessary parameters off of the request
            String portletId = requestContext
                    .getRequestParameter(PORTLETID);
            if (portletId == null) { throw new Exception(
                    "portlet id not provided"); }

            resultMap.put(PORTLETID, portletId);

            PortletPlacementManager ppm = new PortletPlacementManagerImpl(requestContext);
            Fragment fragment = ppm.getFragmentById(portletId);
            Coordinate returnCoordinate = null;

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
                returnCoordinate = ppm
                        .moveAbs(fragment, a_oCoordinate);
            } 
            else if (iMoveType == LEFT)
            {
                returnCoordinate = ppm.moveLeft(fragment);
            } 
            else if (iMoveType == RIGHT)
            {
                returnCoordinate = ppm.moveRight(fragment);
            } 
            else if (iMoveType == UP)
            {
                returnCoordinate = ppm.moveUp(fragment);
            } 
            else if (iMoveType == DOWN)
            {
                returnCoordinate = ppm.moveDown(fragment);
            }

            // Use dummy values for now
            resultMap.put(STATUS, "success");

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
        catch (Exception e)
        {
            // Log the exception
            log.error("exception while adding a portlet", e);

            // Return a failure indicator
            success = false;
        }

        return success;
    }
}
