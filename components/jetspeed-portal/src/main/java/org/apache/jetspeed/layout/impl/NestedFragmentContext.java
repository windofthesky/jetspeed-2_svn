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

import java.util.List;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.jetspeed.components.portletregistry.PortletRegistry;
import org.apache.jetspeed.layout.PortletPlacementException;
import org.apache.jetspeed.om.page.ContentFragment;
import org.apache.jetspeed.om.page.ContentPage;

/**
 * NestedFragmentContext
 * 
 * This object captures the nested position of a fragment
 * within a page. Given a target fragment and a page,
 * the target fragment col/row within its parent is
 * recorded, followed by the target fragment's parent
 * col/row within its parent, etc.
 * 
 * The purpose of this object is to support the
 * create-new-page-on-edit feature. For example, when
 * a fragment is moved, causing the creation of a new
 * page, the information captured by this object 
 * allows the copy of the fragment in the new page to
 * be located.
 * 
 * @author <a>Steve Milek</a>
 * @author <a href="mailto:smilek@apache.org">Steve Milek</a>
 * @version $Id: $
 */
public class NestedFragmentContext
{
    protected static final Logger log = LoggerFactory.getLogger( NestedFragmentContext.class );
    protected static final String eol = System.getProperty( "line.separator" );
	
	private ContentFragment targetFragment;
	private ContentFragment rootFragment;
	private ContentPage page;
	private List fragmentLevels;
	
	public NestedFragmentContext( ContentFragment targetFragment, ContentPage page, PortletRegistry registry )
	    throws PortletPlacementException
	{
		this.targetFragment = targetFragment;
		this.page = page;
		this.rootFragment = page.getRootFragment();
		init( registry );
	}
	
	protected void init( PortletRegistry registry )
	    throws PortletPlacementException
	{
		List nestedFragmentLevels = new ArrayList();
		ContentFragment nextTarget = targetFragment;
		ContentFragment nextParent = null;
		do
		{
			nextParent = NestedFragmentContext.getParentFragmentById( nextTarget.getId(), rootFragment );
			if ( nextParent != null )
			{
				NestedFragmentLevel level = new NestedFragmentLevel( nextTarget, nextParent, registry );
				nestedFragmentLevels.add( level );
				
				nextTarget = nextParent;
			}
			else
			{
				if ( ! nextTarget.getId().equals( rootFragment.getId() ) )
				{
					throw new PortletPlacementException( "Cannot determine complete nested structure for fragment " + targetFragment.getId() );
				}
				nextTarget = null;
			}
		}
		while ( nextTarget != null );
		this.fragmentLevels = nestedFragmentLevels;
	}
	
	public ContentFragment getFragmentOnNewPage( ContentPage newPage, PortletRegistry registry )
	    throws PortletPlacementException
	{
		ContentFragment newPageRootFragment = newPage.getRootFragment();
		
		int depth = fragmentLevels.size();
		
		ContentFragment nextFragment = newPageRootFragment;
		for ( int i = depth -1; i >= 0 ; i-- )
		{
			NestedFragmentLevel level = (NestedFragmentLevel)fragmentLevels.get(i);
			PortletPlacementContextImpl placement = new PortletPlacementContextImpl( newPage, registry, nextFragment );
			try
			{
				nextFragment = placement.getFragmentAtOldCoordinate( new CoordinateImpl( level.getChildCol(), level.getChildRow() ) );
			
			}
			catch ( PortletPlacementException ppex )
			{
				log.error( "getFragmentOnNewPage failure to locate fragment on new page (index=" + i + ") :" + eol + this.toString() + ( placement != null ? ( eol + placement.dumpFragments(null) ) : "" ) + eol, ppex );
				throw ppex;
			}
		    catch ( RuntimeException ex )
			{
				log.error( "getFragmentOnNewPage failure to locate fragment on new page (index=" + i + ") :" + eol + this.toString() + ( placement != null ? ( eol + placement.dumpFragments(null) ) : "" ) + eol, ex );
				throw ex;
			}	
		    if ( nextFragment == null )
			{
				throw new PortletPlacementException( "Cannot locate copy of fragment " + targetFragment.getId() + " in the new page structure :" + eol + this.toString() + ( placement != null ? ( eol + placement.dumpFragments(null) ) : "" ));
			}
		}
		return nextFragment;
	}
	
	public String toString()
	{
		StringBuffer out = new StringBuffer();
		int depth = fragmentLevels.size();
		int ldepth = 0;
		for ( int i = depth -1; i >= 0 ; i-- )
		{
			NestedFragmentLevel level = (NestedFragmentLevel)fragmentLevels.get(i);
			if ( ldepth > 0 )
			{
				out.append( eol );
				for ( int j = 0 ; j < ldepth ; j++ )
					out.append( "   " );
			}
			ldepth++;
			out.append( level.toString() );
		}
		return out.toString();
	}
	
	class NestedFragmentLevel
	{
		private int childRow;
		private int childCol;
		private ContentFragment child;
		private ContentFragment parent;
		
		NestedFragmentLevel( ContentFragment child, ContentFragment parent, PortletRegistry registry )
		    throws PortletPlacementException
		{
			this.child = child;
			this.parent = parent;
			PortletPlacementContextImpl placement = new PortletPlacementContextImpl( page, registry, parent );
            this.childRow = placement.getFragmentRow( child );
            this.childCol = placement.getFragmentCol( child );
		}
		
		protected int getChildRow()
		{
			return this.childRow;
		}
		protected int getChildCol()
		{
			return this.childCol;
		}
		protected ContentFragment getChild()
		{
			return this.child;
		}
		protected ContentFragment getParent()
		{
			return this.parent;
		}
		public String toString()
		{
			return child.getType() + " col=" + childCol + " row=" + childRow  + " id=" + child.getId() + " parent-id=" + parent.getId() ;
		}
	}
	
	public static ContentFragment getParentFragmentById( String id, ContentFragment parent )
    {   
        // find fragment by id, tracking fragment parent
        if ( id == null )
        {
        	return null;
        }
		
		ContentFragment matchedParent = null;
        if( parent != null ) 
        {
            // process the children
            List children = parent.getFragments();
            for( int i = 0, cSize = children.size() ; i < cSize ; i++) 
            {
                ContentFragment childFrag = (ContentFragment)children.get( i );
                if ( childFrag != null ) 
                {
                    if ( id.equals( childFrag.getId() ) )
                    {
                        matchedParent = parent;
                        break;
                    }
                    else
                    {
                        matchedParent = NestedFragmentContext.getParentFragmentById( id, childFrag );
                        if ( matchedParent != null )
                        {
                            break;
                        }
                    }
                }
            }
        }
        return matchedParent;
    }
}
