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

import java.util.Map;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.jetspeed.JetspeedActions;
import org.apache.jetspeed.ajax.AJAXException;
import org.apache.jetspeed.ajax.AjaxAction;
import org.apache.jetspeed.ajax.AjaxBuilder;
import org.apache.jetspeed.components.portletregistry.PortletRegistry;
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
    protected static final Logger log = LoggerFactory.getLogger(MovePortletAction.class);
    protected static final String eol = System.getProperty( "line.separator" );
    
    private PortletRegistry registry;
    private int iMoveType = -1;
    private String sMoveType = null;

    public MovePortletAction( String template, 
            				  String errorTemplate,
                              PortletRegistry registry,
                              String sMoveType )
        throws AJAXException
    {
        this( template, errorTemplate, registry, sMoveType, null, null );
    }
    
    public MovePortletAction( String template, 
                              String errorTemplate,
                              PortletRegistry registry,
                              PageManager pageManager,
                              PortletActionSecurityBehavior securityBehavior )
        throws AJAXException
    {
        this( template, errorTemplate, registry, "moveabs", pageManager, securityBehavior );
    }

    public MovePortletAction( String template,
                              String errorTemplate,
                              PortletRegistry registry,
                              String sMoveType,
                              PageManager pageManager,
                              PortletActionSecurityBehavior securityBehavior )
        throws AJAXException
    {
        super( template, errorTemplate, pageManager, securityBehavior );
        setMoveType( sMoveType );
        this.registry = registry;
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
            String moveFragmentId = getActionParameter(requestContext, FRAGMENTID);
            String layoutId = getActionParameter(requestContext, LAYOUTID);
            if ( moveFragmentId == null ) 
            {
                throw new Exception( FRAGMENTID + " not provided; must specify portlet or layout id" ); 
            }
            resultMap.put(FRAGMENTID, moveFragmentId);
                        
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
                    // determine if layoutId parameter refers to the current layout fragment or to some other layout fragment
                    moveToLayoutFragment = currentLayoutFragment;
                    Iterator layoutChildIter = moveToLayoutFragment.getFragments().iterator();
                    while ( layoutChildIter.hasNext() )
                    {
                        Fragment childFrag = (Fragment)layoutChildIter.next();
                        if ( childFrag != null )
                        {
                            if ( moveFragmentId.equals( childFrag.getId() ) )
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
                        currentLayoutFragment = getParentFragmentById( moveFragmentId, root );
                    }
                }
                if ( currentLayoutFragment == null )
                {
                    // report error
                    throw new Exception("parent layout id not found for portlet id:" + moveFragmentId );
                }
            }
            
            if ( false == checkAccess( requestContext, JetspeedActions.EDIT ) )
            {
            	if ( ! isPageQualifiedForCreateNewPageOnEdit( requestContext ) )
            	{
            		success = false;
                    resultMap.put(REASON, "Page is not qualified for create-new-page-on-edit");
                    return success;
            	}
            	
                Page page = requestContext.getPage();
                Fragment fragment = page.getFragmentById( moveFragmentId );
                if ( fragment == null )
                {
                    success = false;
                    resultMap.put(REASON, "Fragment not found");
                    return success;
                }
                NestedFragmentContext moveFragmentContext = null;
                NestedFragmentContext moveToFragmentContext = null;
                try
                {
                	moveFragmentContext = new NestedFragmentContext( fragment, page, registry );
                	//log.info( "moveFragmentContext original : " + eol + moveFragmentContext.toString() );
                	if ( moveToLayoutFragment != null )
                	{
                		moveToFragmentContext = new NestedFragmentContext( moveToLayoutFragment, page, registry );
                		//log.info( "moveToFragmentContext original : " + eol + moveToFragmentContext.toString() );
                	}
                }
                catch ( Exception ex )
                {
                	log.error( "Failure to construct nested context for fragment " + moveFragmentId, ex );
                	success = false;
                    resultMap.put( REASON, "Cannot construct nested context for fragment" );
                    return success;
                }
                                
                //log.info("before createNewPageOnEdit page-name=" + page.getName() + " page-path=" + page.getPath() + " page-url=" + page.getUrl() );
                if ( ! createNewPageOnEdit( requestContext ) )
                {
                    success = false;
                    resultMap.put(REASON, "Insufficient access to edit page");
                    return success;
                }
                status = "refresh";
                
                Page newPage = requestContext.getPage();                
                //log.info("after createNewPageOnEdit page-name=" + newPage.getName() + " page-path=" + newPage.getPath() + " page-url=" + newPage.getUrl() );
                Fragment newPageRootFragment = newPage.getRootFragment();
                
                // using NestedFragmentContext, find portlet id for copy of target portlet in the new page 
                Fragment newFragment = null;
                try
                {
                	newFragment = moveFragmentContext.getFragmentOnNewPage( newPage, registry );
                	//log.info( "npe newFragment: " + newFragment.getType() + " " + newFragment.getId() );
                }
                catch ( Exception ex )
                {
                	log.error( "Failure to locate copy of fragment " + moveFragmentId, ex );
                	success = false;
                    resultMap.put( REASON, "Failed to find new fragment for portlet id: " + moveFragmentId );
                    return success;
                }
                
                moveFragmentId = newFragment.getId();
                currentLayoutFragment = getParentFragmentById( moveFragmentId, newPageRootFragment );
                if ( currentLayoutFragment == null )
                {
                	success = false;
                    resultMap.put( REASON, "Failed to find parent layout for copied fragment " + moveFragmentId );
                    return success;
                }
                //log.info( "npe newParentFragment: " + currentLayoutFragment.getType() + " " + currentLayoutFragment.getId() );
                if ( moveToLayoutFragment != null )
                {
                	Fragment newMoveToFragment = null;
                    try
                    {
                    	newMoveToFragment = moveToFragmentContext.getFragmentOnNewPage( newPage, registry );
                    	//log.info( "npe newMoveToFragment: " + newMoveToFragment.getType() + " " + newMoveToFragment.getId() );
                    }
                    catch ( Exception ex )
                    {
                    	log.error( "Failure to locate copy of destination fragment " + moveToLayoutFragment.getId(), ex );
                    	success = false;
                        resultMap.put( REASON, "Failed to find copy of destination fragment" );
                        return success;
                    }
                    moveToLayoutFragment = newMoveToFragment;
                }
            }
            
            if ( moveToLayoutFragment != null )
            {
                success = moveToOtherLayoutFragment( requestContext,
                                        			 batch,
                                        			 resultMap,
                                        			 moveFragmentId,
                                        			 moveToLayoutFragment,
                                        			 currentLayoutFragment ) ;
            }
            else
            {
            	PortletPlacementContext placement = null;
            	Page page = requestContext.getPage();
            	
            	if ( currentLayoutFragment == null )
            		currentLayoutFragment = getParentFragmentById( moveFragmentId, page.getRootFragment() );
            	
                if ( currentLayoutFragment != null )
                    placement = new PortletPlacementContextImpl( page, registry, currentLayoutFragment );
                else
                    placement = new PortletPlacementContextImpl( page, registry );
                
                Fragment fragment = placement.getFragmentById(moveFragmentId);
                if ( fragment == null )
                {
                    success = false;
                    resultMap.put( REASON, "Failed to find fragment for portlet id: " + moveFragmentId );
                    return success;
                }
                
                success = moveInFragment( requestContext, placement, fragment, null, resultMap, batch );
            }
            if ( success )
            {
            	resultMap.put( STATUS, status );
            }
        }
        catch ( Exception e )
        {
            // Log the exception
            log.error( "exception while moving a portlet", e );
            resultMap.put( REASON, e.toString() );
            // Return a failure indicator
            success = false;
        }

        return success;
    }
    
    protected boolean moveInFragment( RequestContext requestContext, PortletPlacementContext placement, Fragment fragment, Fragment placeInLayoutFragment, Map resultMap, boolean batch )
        throws PortletPlacementException, NodeException, AJAXException
    {
    	boolean success = true;

    	String moveFragmentId = fragment.getId();
    	boolean addFragment = (placeInLayoutFragment != null);
        Coordinate returnCoordinate = null;
        float oldX = 0f, oldY = 0f, oldZ = 0f, oldWidth = 0f, oldHeight = 0f;
        float x = -1f, y = -1f, z = -1f, width = -1f, height = -1f;
        boolean absHeightChanged = false;

        // desktop extended
        String posExtended = getActionParameter( requestContext, DESKTOP_EXTENDED );
        if ( posExtended != null )
        {
            Map fragmentProperties = fragment.getProperties();
            if ( fragmentProperties == null )
            {
                success = false;
                resultMap.put(REASON, "Failed to acquire fragment properties map for portlet id: " + moveFragmentId );
                return success;
            }
            String oldDeskExt = (String)fragmentProperties.get( DESKTOP_EXTENDED );
            resultMap.put( OLD_DESKTOP_EXTENDED, ( (oldDeskExt != null) ? oldDeskExt : "" ) );
            fragmentProperties.put( DESKTOP_EXTENDED, posExtended );
        }
                
        // only required for moveabs
        if ( iMoveType == ABS )
        {
            Coordinate newCoordinate = getCoordinateFromParams( requestContext );
            returnCoordinate = placement.moveAbsolute( fragment, newCoordinate, addFragment );
            String sHeight = getActionParameter( requestContext, HEIGHT );
            if ( sHeight != null && sHeight.length() > 0 )
            {
                oldHeight = fragment.getLayoutHeight();
                height = Float.parseFloat( sHeight );
                fragment.setLayoutHeight( height );
                absHeightChanged = true;
            }
        } 
        else if ( iMoveType == LEFT )
        {
            returnCoordinate = placement.moveLeft( fragment );
        } 
        else if ( iMoveType == RIGHT )
        {
            returnCoordinate = placement.moveRight( fragment );
        } 
        else if ( iMoveType == UP )
        {
            returnCoordinate = placement.moveUp( fragment );
        } 
        else if ( iMoveType == DOWN )
        {
            returnCoordinate = placement.moveDown( fragment );
        }
        else if ( iMoveType == CARTESIAN )
        {
            String sx = getActionParameter( requestContext, X );
            String sy = getActionParameter( requestContext, Y );
            String sz = getActionParameter( requestContext, Z );
            String sWidth = getActionParameter( requestContext, WIDTH );
            String sHeight = getActionParameter( requestContext, HEIGHT );
            if ( sx != null )
            {
                oldX = fragment.getLayoutX();
                x = Float.parseFloat( sx ); 
                fragment.setLayoutX( x );
            }
            if ( sy != null )
            {
                oldY = fragment.getLayoutY();                    
                y = Float.parseFloat( sy ); 
                fragment.setLayoutY( y );
            }                
            if ( sz != null )
            {
                oldZ = fragment.getLayoutZ();                    
                z = Float.parseFloat( sz ); 
                fragment.setLayoutZ( z );
            }                
            if ( sWidth != null )
            {
                oldWidth = fragment.getLayoutWidth();                    
                width = Float.parseFloat( sWidth ); 
                fragment.setLayoutWidth( width );
            }
            if ( sHeight != null )
            {
                oldHeight = fragment.getLayoutHeight();                    
                height = Float.parseFloat( sHeight ); 
                fragment.setLayoutHeight( height );
            }
        }
        
        // synchronize back to the page layout root fragment
        Page page = placement.syncPageFragments();
    
        if ( placeInLayoutFragment != null )
        {
            placeInLayoutFragment.getFragments().add( fragment );
        }
        
        if ( pageManager != null && ! batch )
        {
            pageManager.updatePage( page );
        }
        
        if ( iMoveType == CARTESIAN )
        {
            putCartesianResult( resultMap, x, oldX, X, OLD_X );
            putCartesianResult( resultMap, y, oldY, Y, OLD_Y );                
            putCartesianResult( resultMap, z, oldZ, Z, OLD_Z );
            putCartesianResult( resultMap, width, oldWidth, WIDTH, OLD_WIDTH );
            putCartesianResult( resultMap, height, oldHeight, HEIGHT, OLD_HEIGHT );
        }
        else
        {
            // Need to determine what the old col and row were
            resultMap.put( OLDCOL, String.valueOf( returnCoordinate.getOldCol() ) );
            resultMap.put( OLDROW, String.valueOf( returnCoordinate.getOldRow() ) );
            // Need to determine what the new col and row were
            resultMap.put( NEWCOL, String.valueOf( returnCoordinate.getNewCol() ) );
            resultMap.put( NEWROW, String.valueOf( returnCoordinate.getNewRow() ) );
            if ( absHeightChanged )
            {
                putCartesianResult( resultMap, height, oldHeight, HEIGHT, OLD_HEIGHT );
            }
        }
        
        resultMap.put( FRAGMENTID, moveFragmentId );
        
        return success;
    }

    protected boolean moveToOtherLayoutFragment( RequestContext requestContext,
                                                 boolean batch,
                                                 Map resultMap,
                                                 String moveFragmentId,
                                                 Fragment moveToLayoutFragment,
                                                 Fragment removeFromLayoutFragment )
        throws PortletPlacementException, NodeException, AJAXException
    {
        boolean success = true;
        Fragment placeFragment = null;
        if ( removeFromLayoutFragment != null )
        {
        	Page page = requestContext.getPage();
            PortletPlacementContext placement = new PortletPlacementContextImpl( page, registry, removeFromLayoutFragment );
        
            placeFragment = placement.getFragmentById( moveFragmentId );
            if ( placeFragment == null )
            {
                success = false;
                resultMap.put( REASON, "Failed to find fragment to move to another layout for fragment id: " + moveFragmentId );
                return success;
            }
            placement.remove( placeFragment );
            page = placement.syncPageFragments();
            page.removeFragmentById( moveFragmentId );
        }
        if ( placeFragment != null )
        {
            return placeFragment( requestContext,
                                  batch,
                                  resultMap,
                                  placeFragment,
                                  moveToLayoutFragment );
        }
        return success;
    }

    protected boolean placeFragment( RequestContext requestContext,
                                     boolean batch,
                                     Map resultMap,
                                     Fragment placeFragment,
                                     Fragment placeInLayoutFragment )
        throws PortletPlacementException, NodeException, AJAXException
    {
        boolean success = true;
        if ( placeFragment == null )
        {
            success = false;
            return success;
        }
        
        // add fragment
        Page page = requestContext.getPage();
        PortletPlacementContext placement = new PortletPlacementContextImpl( page, registry, placeInLayoutFragment );
        //placement.add( placeFragment, getCoordinateFromParams( requestContext ) );
        
        success = moveInFragment( requestContext, placement, placeFragment, placeInLayoutFragment, resultMap, batch );

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
    
    protected PortletRegistry getPortletRegistry()
    {
    	return this.registry;
    }
}
