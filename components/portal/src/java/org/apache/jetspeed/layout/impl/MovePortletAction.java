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

import java.util.List;
import java.util.Map;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.JetspeedActions;
import org.apache.jetspeed.ajax.AJAXException;
import org.apache.jetspeed.ajax.AjaxAction;
import org.apache.jetspeed.ajax.AjaxBuilder;
import org.apache.jetspeed.layout.Coordinate;
import org.apache.jetspeed.layout.PortletActionSecurityBehavior;
import org.apache.jetspeed.layout.PortletPlacementException;
import org.apache.jetspeed.layout.PortletPlacementContext;
import org.apache.jetspeed.om.page.Fragment;
import org.apache.jetspeed.om.page.Page;
import org.apache.jetspeed.page.PageManager;
import org.apache.jetspeed.page.document.NodeException;
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
                             PageManager pageManager,
                             PortletActionSecurityBehavior securityBehavior)
    throws AJAXException
    {
        this( template, errorTemplate, "moveabs", pageManager, securityBehavior );
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

    public boolean runBatch(RequestContext requestContext, Map resultMap) throws AJAXException
    {
        return runAction(requestContext, resultMap, true);
    }    
    
    public boolean run(RequestContext requestContext, Map resultMap)
            throws AJAXException
    {
        return runAction(requestContext, resultMap, false);
    }
    
    protected boolean runAction( RequestContext requestContext, Map resultMap, boolean batch )  throws AJAXException
    {
        boolean success = true;
        String status = "success";
        try
        {
            resultMap.put(ACTION, sMoveType);
            // Get the necessary parameters off of the request
            String portletId = getActionParameter(requestContext, PORTLETID);
            String layoutId = getActionParameter(requestContext, LAYOUTID);
            if (portletId == null) 
            { 
                throw new Exception("portlet id not provided"); 
            }
            resultMap.put(PORTLETID, portletId);
            
            Fragment currentLayoutFragment = null;
            Fragment moveToLayoutFragment = null;
            // when layoutId is null we use old behavior, ignoring everything to do with multiple layout fragments
            if ( layoutId != null && layoutId.length() > 0 && iMoveType != CARTESIAN )
            {
                Page page = requestContext.getPage();
                currentLayoutFragment = page.getFragmentById( layoutId );
                if ( currentLayoutFragment == null )
                {
                    throw new Exception("layout id not found: " + layoutId );
                }
                else
                {
                    // determine if layoutId parameter refers to the current or the move target layout fragment
                    moveToLayoutFragment = currentLayoutFragment;
                    Iterator layoutChildIter = moveToLayoutFragment.getFragments().iterator();
                    while ( layoutChildIter.hasNext() )
                    {
                        Fragment childFrag = (Fragment)layoutChildIter.next();
                        if ( childFrag != null )
                        {
                            if ( portletId.equals( childFrag.getId() ) )
                            {
                                moveToLayoutFragment = null;
                                break;
                            }
                        }
                    }
                    if ( moveToLayoutFragment != null )
                    {
                        // figure out the current layout fragment - must know to be able to find the portlet
                        //    fragment by row/col when a new page is created
                        Fragment root = requestContext.getPage().getRootFragment();
                        currentLayoutFragment = getParentFragmentById(portletId, root);
                    }
                }
                if ( currentLayoutFragment == null )
                {
                    // report error
                    throw new Exception("parent layout id not found for portlet id:" + portletId );
                }
            }
            
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
                
                // remember current row/column of porlet fragment
                int column = fragment.getLayoutColumn();
                int row = fragment.getLayoutRow();
                
                // rememeber current row/column of parent layout fragment and move-to layout fragment
                int layoutColumn = -1, layoutRow = -1;
                int moveToLayoutColumn = -1, moveToLayoutRow = -1;
                if ( currentLayoutFragment != null )
                {
                    layoutColumn = currentLayoutFragment.getLayoutColumn();
                    layoutRow = currentLayoutFragment.getLayoutRow();
                    if ( moveToLayoutFragment != null )
                    {
                        moveToLayoutColumn = moveToLayoutFragment.getLayoutColumn();
                        moveToLayoutRow = moveToLayoutFragment.getLayoutRow();
                    }
                }
                
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
                    resultMap.put( REASON, "Failed to find new fragment for portlet id: " + portletId );
                    return success;                    
                }
                portletId = newFragment.getId();
                
                if ( currentLayoutFragment != null )
                {
                    newFragment = getFragmentIdFromLocation(layoutRow, layoutColumn, requestContext.getPage());
                    if (newFragment == null)
                    {
                        success = false;
                        resultMap.put( REASON, "Failed to find new parent layout fragment id: " + currentLayoutFragment.getId() + " for portlet id: " + portletId );
                        return success;
                    }
                    currentLayoutFragment = newFragment;
                    if ( moveToLayoutFragment != null )
                    {
                        newFragment = getFragmentIdFromLocation(moveToLayoutRow, moveToLayoutColumn, requestContext.getPage());
                        if (newFragment == null)
                        {
                            success = false;
                            resultMap.put( REASON, "Failed to find new move-to layout fragment id: " + moveToLayoutFragment.getId() + " for portlet id: " + portletId );
                            return success;
                        }
                        moveToLayoutFragment = newFragment;
                    }
                }
            }
            
            if ( moveToLayoutFragment != null )
            {
                success = moveFragment( requestContext,
                                        pageManager,
                                        batch,
                                        resultMap,
                                        portletId,
                                        moveToLayoutFragment,
                                        currentLayoutFragment ) ;
            }
            else
            {
                PortletPlacementContext placement = null;
                if ( currentLayoutFragment != null )
                    placement = new PortletPlacementContextImpl(requestContext, currentLayoutFragment, 1);
                else
                {
                    placement = new PortletPlacementContextImpl(requestContext);
                }
                Fragment fragment = placement.getFragmentById(portletId);
                if (fragment == null)
                {
                    success = false;
                    resultMap.put(REASON, "Failed to find fragment for portlet id: " + portletId );
                    return success;
                }
                Coordinate returnCoordinate = null;
                float oldX = 0f, oldY = 0f, oldZ = 0f, oldWidth = 0f, oldHeight = 0f;
                float x = -1f, y = -1f, z = -1f, width = -1f, height = -1f;
                boolean absHeightChanged = false;
                
                String posExtended = getActionParameter(requestContext, DESKTOP_EXTENDED);
                if ( posExtended != null )
                {
                    Map fragmentProperties = fragment.getProperties();
                    if ( fragmentProperties == null )
                    {
                        success = false;
                        resultMap.put(REASON, "Failed to acquire fragment properties map for portlet id: " + portletId );
                        return success;
                    }
                    String oldDeskExt = (String)fragmentProperties.get( DESKTOP_EXTENDED );
                    resultMap.put( OLD_DESKTOP_EXTENDED, ( (oldDeskExt != null) ? oldDeskExt : "" ) );
                    fragmentProperties.put( DESKTOP_EXTENDED, posExtended );
                }
                
                // Only required for moveabs
                if (iMoveType == ABS)
                {
                    Coordinate a_oCoordinate = getCoordinateFromParams(requestContext);
                    returnCoordinate = placement.moveAbsolute(fragment, a_oCoordinate);
                    String sHeight = getActionParameter(requestContext, HEIGHT);
                    if ( sHeight != null && sHeight.length() > 0 )
                    {
                        oldHeight = fragment.getLayoutHeight();
                        height = Float.parseFloat(sHeight);
                        fragment.setLayoutHeight(height);
                        absHeightChanged = true;
                    }
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
                    String sx = getActionParameter(requestContext, X);
                    String sy = getActionParameter(requestContext, Y);
                    String sz = getActionParameter(requestContext, Z);
                    String sWidth = getActionParameter(requestContext, WIDTH);
                    String sHeight = getActionParameter(requestContext, HEIGHT);
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
            
                if (pageManager != null && !batch)
                {
                    pageManager.updatePage(page);
                }
                
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
                    if ( absHeightChanged )
                    {
                        putCartesianResult(resultMap, height, oldHeight, HEIGHT, OLD_HEIGHT);
                    }
                }
            }
            resultMap.put(STATUS, status);
            resultMap.put(PORTLETID, portletId);
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

    /*

        Fragment placeFragment = placement.getFragmentById(portletId);
        if (placeFragment == null)
        {
            success = false;
            resultMap.put(REASON, "Failed to find fragment to move to another layout for portlet id: " + portletId );
            return success;                
        }

    */
    protected boolean moveFragment( RequestContext requestContext,
                                    PageManager pageManager,
                                    boolean batch,
                                    Map resultMap,
                                    String moveFragmentId,
                                    Fragment moveToLayoutFragment,
                                    Fragment removeFromLayoutFragment )
        throws PortletPlacementException, NodeException
    {
        boolean success = true;
        Fragment placeFragment = null;
        if ( removeFromLayoutFragment != null )
        {
            PortletPlacementContext placement = new PortletPlacementContextImpl( requestContext, removeFromLayoutFragment, 1 );
        
            placeFragment = placement.getFragmentById( moveFragmentId );
            if ( placeFragment == null )
            {
                success = false;
                resultMap.put( REASON, "Failed to find fragment to move to another layout for fragment id: " + moveFragmentId );
                return success;
            }
        }
        if ( placeFragment != null )
        {
            return placeFragment( requestContext,
                                  pageManager,
                                  batch,
                                  resultMap,
                                  placeFragment,
                                  moveToLayoutFragment );
        }
        return success;
    }

    protected boolean placeFragment( RequestContext requestContext,
                                     PageManager pageManager,
                                     boolean batch,
                                     Map resultMap,
                                     Fragment placeFragment,
                                     Fragment placeInLayoutFragment )
        throws PortletPlacementException, NodeException
    {
        boolean success = true;
        if ( placeFragment == null )
        {
            success = false;
            return success;
        }

        // desktop extended
        String posExtended = getActionParameter(requestContext, DESKTOP_EXTENDED);
        if ( posExtended != null )
        {
            Map fragmentProperties = placeFragment.getProperties();
            if ( fragmentProperties == null )
            {
                success = false;
                resultMap.put(REASON, "Failed to acquire fragment properties map for fragment id: " + placeFragment.getId() );
                return success;
            }
            String oldDeskExt = (String)fragmentProperties.get( DESKTOP_EXTENDED );
            resultMap.put( OLD_DESKTOP_EXTENDED, ( (oldDeskExt != null) ? oldDeskExt : "" ) );
            fragmentProperties.put( DESKTOP_EXTENDED, posExtended );
        }
                
        // add fragment
        PortletPlacementContext placement = new PortletPlacementContextImpl( requestContext, placeInLayoutFragment, 1 );
        Coordinate returnCoordinate = placement.add( placeFragment, getCoordinateFromParams( requestContext ) );
        Page page = placement.syncPageFragments();

        placeInLayoutFragment.getFragments().add( placeFragment );
        if ( pageManager != null && ! batch )
        {
            pageManager.updatePage( page );
        }

        // Need to determine what the old col and row were
        resultMap.put( OLDCOL, String.valueOf( returnCoordinate.getOldCol() ) );
        resultMap.put( OLDROW, String.valueOf( returnCoordinate.getOldRow() ) );
        // Need to determine what the new col and row were
        resultMap.put( NEWCOL, String.valueOf( returnCoordinate.getNewCol() ) );
        resultMap.put( NEWROW, String.valueOf( returnCoordinate.getNewRow() ) );

        return success;
    }
    
    protected Coordinate getCoordinateFromParams(RequestContext requestContext)
    {
        String a_sCol = getActionParameter( requestContext, COL );
        String a_sRow = getActionParameter( requestContext, ROW );

        int a_iCol = 0;
        int a_iRow = 0;

        // Convert the col and row into integers
        if ( a_sCol != null )
        {
            a_iCol = Integer.parseInt( a_sCol );
        }
        if ( a_sRow != null )
        {
            a_iRow = Integer.parseInt( a_sRow );
        }

        Coordinate a_oCoordinate = new CoordinateImpl( 0, 0, a_iCol, a_iRow );
        return a_oCoordinate;
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
