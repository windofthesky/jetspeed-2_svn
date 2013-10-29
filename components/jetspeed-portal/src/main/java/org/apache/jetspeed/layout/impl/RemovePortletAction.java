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

import org.apache.jetspeed.JetspeedActions;
import org.apache.jetspeed.ajax.AJAXException;
import org.apache.jetspeed.ajax.AjaxAction;
import org.apache.jetspeed.ajax.AjaxBuilder;
import org.apache.jetspeed.components.portletregistry.PortletRegistry;
import org.apache.jetspeed.layout.PortletActionSecurityBehavior;
import org.apache.jetspeed.layout.PortletPlacementContext;
import org.apache.jetspeed.om.page.ContentFragment;
import org.apache.jetspeed.om.page.ContentPage;
import org.apache.jetspeed.page.PageManager;
import org.apache.jetspeed.pipeline.PipelineException;
import org.apache.jetspeed.request.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;


/**
 * Remove Portlet portlet placement action
 * 
 * AJAX Parameters: 
 *    id = the fragment id of the portlet to remove
 *    page = (implied in the URL)
 *    
 * @author <a>David Gurney </a>
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor </a>
 * @version $Id: $
 */
public class RemovePortletAction 
    extends BasePortletAction 
    implements AjaxAction, AjaxBuilder, Constants
{
    protected static final Logger log = LoggerFactory.getLogger(RemovePortletAction.class);
    
    private PortletRegistry registry;

    public RemovePortletAction( String template, String errorTemplate, PortletRegistry registry )
        throws PipelineException
    {
        this( template, errorTemplate, registry, null, null );
    }

    public RemovePortletAction( String template, 
                                String errorTemplate,
                                PortletRegistry registry,
                                PageManager pageManager, 
                                PortletActionSecurityBehavior securityBehavior )
        throws PipelineException
    {
        super( template, errorTemplate, pageManager, securityBehavior );
        this.registry = registry;
    }
    
    public boolean runBatch(RequestContext requestContext, Map<String,Object> resultMap) throws AJAXException
    {
        return runAction(requestContext, resultMap, true);
    }    
    
    public boolean run(RequestContext requestContext, Map resultMap)
            throws AJAXException
    {
        return runAction(requestContext, resultMap, false);
    }
    
    public boolean runAction(RequestContext requestContext, Map<String,Object> resultMap, boolean batch)
    {
        boolean success = true;
        String status = "success";
        try
        {
            resultMap.put( ACTION, "remove" );
            // Get the necessary parameters off of the request
            String portletId = getActionParameter( requestContext, PORTLETID );
            if (portletId == null) 
            { 
                success = false;
                resultMap.put( REASON, "Portlet ID not provided" );
                return success;
            }
            resultMap.put( PORTLETID, portletId );
            if ( false == checkAccess( requestContext, JetspeedActions.EDIT ) )
            {
                ContentPage page = requestContext.getPage();
                ContentFragment fragment = page.getFragmentById( portletId );
                if ( fragment == null )
                {
                    success = false;
                    resultMap.put( REASON, "Fragment not found" );
                    return success;                    
                }
                
                NestedFragmentContext removeFragmentContext = null;
                try
                {
                	removeFragmentContext = new NestedFragmentContext( fragment, page, registry );
                }
                catch ( Exception ex )
                {
                	log.error( "Failure to construct nested context for fragment " + portletId, ex );
                	success = false;
                    resultMap.put( REASON, "Cannot construct nested context for fragment" );
                    return success;
                }
                
                if ( ! createNewPageOnEdit( requestContext ) )
                {                
                    success = false;
                    resultMap.put( REASON, "Insufficient access to edit page" );
                    return success;
                }
                status = "refresh";
                
                ContentPage newPage = requestContext.getPage();

                // using NestedFragmentContext, find portlet id for copy of target portlet in the new page 
                ContentFragment newFragment = null;
                try
                {
                	newFragment = removeFragmentContext.getFragmentOnNewPage( newPage, registry );
                }
                catch ( Exception ex )
                {
                	log.error( "Failure to locate copy of fragment " + portletId, ex );
                	success = false;
                    resultMap.put( REASON, "Failed to find new fragment for portlet id: " + portletId );
                    return success;
                }
                portletId = newFragment.getId();
            }
            
            // Use the Portlet Placement Manager to accomplish the removal
            ContentPage page = requestContext.getPage();
            ContentFragment root = page.getRootFragment();
            ContentFragment layoutContainerFragment = getParentFragmentById( portletId, root );
            PortletPlacementContext placement = null;
            ContentFragment fragment = null;
            if ( layoutContainerFragment != null )
            {
            	placement = new PortletPlacementContextImpl( page, registry, layoutContainerFragment );
            	fragment = placement.getFragmentById( portletId );
            }
            if ( fragment == null )
            {
                success = false;
                resultMap.put( REASON, "Fragment not found" );
                return success;                
            }
            placement.remove(fragment);
            page = placement.syncPageFragments();
            page.removeFragment( fragment.getId() );

            // Build the results for the response
            resultMap.put( PORTLETID, portletId );            
            resultMap.put( STATUS, status );
            resultMap.put( OLDCOL, String.valueOf( fragment.getLayoutColumn() ) );
            resultMap.put( OLDROW, String.valueOf( fragment.getLayoutRow() ) );
        } 
        catch ( Exception e )
        {
            // Log the exception
            log.error("exception while adding a portlet", e);
            resultMap.put(REASON, e.toString());
            // Return a failure indicator
            success = false;
        }

        return success;
    }
    
    protected PortletRegistry getPortletRegistry()
    {
    	return this.registry;
    }
}
