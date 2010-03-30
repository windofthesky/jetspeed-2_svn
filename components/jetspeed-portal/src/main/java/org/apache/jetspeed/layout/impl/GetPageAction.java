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

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.jetspeed.JetspeedActions;
import org.apache.jetspeed.PortalReservedParameters;
import org.apache.jetspeed.ajax.AjaxAction;
import org.apache.jetspeed.ajax.AjaxBuilder;
import org.apache.jetspeed.ajax.AJAXException;
import org.apache.jetspeed.components.portletregistry.PortletRegistry;
import org.apache.jetspeed.decoration.DecorationValve;
import org.apache.jetspeed.decoration.PageActionAccess;
import org.apache.jetspeed.decoration.Theme;
import org.apache.jetspeed.layout.PortletActionSecurityBehavior;
import org.apache.jetspeed.om.page.BaseFragmentElement;
import org.apache.jetspeed.om.page.ContentFragment;
import org.apache.jetspeed.om.page.ContentPage;
import org.apache.jetspeed.om.page.Fragment;
import org.apache.jetspeed.om.page.Page;
import org.apache.jetspeed.page.PageManager;
import org.apache.jetspeed.portalsite.PortalSiteRequestContext;
import org.apache.jetspeed.profiler.impl.ProfilerValveImpl;
import org.apache.jetspeed.request.RequestContext;
import org.apache.jetspeed.om.portlet.InitParam;
import org.apache.jetspeed.om.portlet.PortletDefinition;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Get Page retrieves a page from the Page Manager store and PSML format
 *
 * AJAX Parameters: 
 *    page = the path and name of the page ("/_user/ronaldino/goals.psml")
 *    
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: $
 */
public class GetPageAction 
    extends BaseGetResourceAction 
    implements AjaxAction, AjaxBuilder, Constants
{
    protected Logger log = LoggerFactory.getLogger( GetPageAction.class );
    
    private PortletRegistry registry;
    private DecorationValve decorationValve;
    
    public GetPageAction( String template, 
                          String errorTemplate, 
                          PageManager pageManager,
                          PortletActionSecurityBehavior securityBehavior,
                          PortletRegistry registry,
                          DecorationValve decorationValve )
    {
        super(template, errorTemplate, pageManager, securityBehavior);
        this.registry = registry;
        this.decorationValve = decorationValve;
    }

    public boolean run(RequestContext requestContext, Map resultMap)
    {
        boolean success = true;
        String status = "success";
        try
        {
            resultMap.put( ACTION, "getpage" );
            if ( false == checkAccess( requestContext, JetspeedActions.VIEW ) )
            {
                resultMap.put( REASON, "Insufficient access to view page" );
                success = false;
                return success;
            }            
            
            // Run the Decoration valve to get actions
            decorationValve.invoke( requestContext, null );
            
            ContentPage page = requestContext.getPage();                        
            String pageName = getActionParameter( requestContext, PAGE );
//            if ( pageName != null )
//            {
//                page = retrievePage( requestContext, pageName );
//            }
            if (page == null)
            {
                throw new AJAXException("Missing current page or '" + PAGE + "' parameter");
            }            
            resultMap.put( STATUS, status );
            resultMap.put( PAGE, page );
            
            Theme theme = (Theme)requestContext.getAttribute( PortalReservedParameters.PAGE_THEME_ATTRIBUTE );
            String pageDecoratorName = null;
            if ( theme != null )
            {
                pageDecoratorName = theme.getPageLayoutDecoration().getName();
            }
            else
            {
                pageDecoratorName = page.getDefaultDecorator( LAYOUT );
            }
            if ( pageDecoratorName != null )
                resultMap.put( DEFAULT_LAYOUT, pageDecoratorName );
                    
            PortalSiteRequestContext siteRequestContext = (PortalSiteRequestContext)requestContext.getAttribute( ProfilerValveImpl.PORTAL_SITE_REQUEST_CONTEXT_ATTR_KEY );
            if ( siteRequestContext == null )
            {
                success = false;
                resultMap.put( REASON, "Missing portal site request context from ProfilerValve" );
                return success;
            }
            
            String profiledPath = siteRequestContext.getPageOrTemplate().getPath();
            resultMap.put( PROFILED_PATH, profiledPath );
            putSecurityInformation( resultMap, page.getPageOrTemplate() ); //TODO: REVIEW: RANDY 
     
            PageActionAccess pageActionAccess = (PageActionAccess)requestContext.getAttribute( PortalReservedParameters.PAGE_EDIT_ACCESS_ATTRIBUTE );
            Boolean userIsAnonymous = Boolean.TRUE;
            if ( pageActionAccess != null )
            	userIsAnonymous = new Boolean( pageActionAccess.isAnonymous() );
            resultMap.put( USER_IS_ANONYMOUS, userIsAnonymous.toString() );
     
            Boolean isPageQualifiedForCreateNewPageOnEdit = Boolean.FALSE;
            if ( ! userIsAnonymous.booleanValue() )
            	isPageQualifiedForCreateNewPageOnEdit = new Boolean( isPageQualifiedForCreateNewPageOnEdit( requestContext ) );
            resultMap.put( PAGE_QUALIFIED_CREATE_ON_EDIT, isPageQualifiedForCreateNewPageOnEdit.toString() );
            
            String fragments = getActionParameter( requestContext, FRAGMENTS );
            if ( fragments == null )
            {
                resultMap.put( FRAGMENTS, "true" );
            }
            else
            {
                if ( fragments.equalsIgnoreCase( "true" ) )
                {
                    resultMap.put( FRAGMENTS, "true" );
                }
                else
                {
                    resultMap.put( FRAGMENTS, "false" );
                    return success;
                }
            }
            
            Map fragSizes = new HashMap();
            Map portletIcons = new HashMap();
            
            String singleLayoutId = getActionParameter( requestContext, LAYOUTID );
            if ( singleLayoutId != null )
            {   // build page representation with single layout
//                BaseFragmentElement singleLayoutFragment = page.getPage().getFragmentById( singleLayoutId ); //TODO: REVIEW: RANDY
//                if ( ! ( singleLayoutFragment instanceof ContentFragment) )
//                {
//                    throw new Exception( "layout id not found: " + singleLayoutId );
//                }
//                ContentFragment currentLayoutFragment =  singleLayoutFragment;
//                ContentFragment currentPortletFragment = null;
//                
//                String singlePortletId = getActionParameter( requestContext, PORTLETENTITY );
//                if ( singlePortletId != null )
//                {
//                    Iterator layoutChildIter = currentLayoutFragment.getFragments().iterator();
//                    while ( layoutChildIter.hasNext() )
//                    {
//                        Fragment childFrag = (Fragment)layoutChildIter.next();
//                        if ( childFrag != null )
//                        {
//                            if ( singlePortletId.equals( childFrag.getId() ) )
//                            {
//                                currentPortletFragment = childFrag;
//                                break;
//                            }
//                        }
//                    }
//                    if ( currentPortletFragment == null )
//                    {
//                        throw new Exception( "portlet id " + singlePortletId + " not found in layout " + singleLayoutId );
//                    }
//                    resultMap.put( "portletsingleId", currentPortletFragment.getId() );
//                }
//                
//                retrieveFragmentSpecialProperties( requestContext, currentLayoutFragment, fragSizes, portletIcons );
//                resultMap.put( "layoutsingle", currentLayoutFragment );
            }
            else if (page.getRootFragment() instanceof ContentFragment)
            {
                retrieveFragmentSpecialProperties( requestContext, page.getRootFragment(), fragSizes, portletIcons );
            }
            else
            {
                throw new Exception( "root layout not found for page: " + page.getId() );                
            }
            resultMap.put( SIZES, fragSizes );
            resultMap.put( "portletIcons", portletIcons );
        }
        catch ( Exception e )
        {
            // Log the exception
            log.error( "exception while getting page", e );

            // Return a failure indicator
            success = false;
        }

        return success;
	}
    
    protected Page retrievePage( RequestContext requestContext, String pageName )
        throws Exception
    {        
        if ( pageName == null )
        {
            pageName = "/";
        }
        Page page = pageManager.getPage( pageName );
        return page;
    }        
    
    
    protected void retrieveFragmentSpecialProperties( RequestContext requestContext, ContentFragment frag, Map fragSizes, Map portletIcons )
    {
        if ( frag == null )
        {
            return;
        }
        
    	if ( "layout".equals( frag.getType() ) )
    	{   // get layout fragment sizes
    		if ( fragSizes != null )
    		{
    			PortletPlacementMetadataAccess.getColumnCountAndSizes( frag, registry, fragSizes );
    		}
    		
    		List childFragments = frag.getFragments();
    		if ( childFragments != null )
    		{
    			Iterator childFragIter = childFragments.iterator();
    			while ( childFragIter.hasNext() )
    			{
    				ContentFragment childFrag = (ContentFragment)childFragIter.next();
                    retrieveFragmentSpecialProperties( requestContext, childFrag, fragSizes, portletIcons );
    			}
    		}
    	}
        else if ( portletIcons != null && "portlet".equals( frag.getType() ) )
        {   // get portlet icon and locale specific portlet display name
            String portletName = frag.getName();
            if ( portletName != null && portletName.length() > 0 )
            {
                PortletDefinition portletDef = registry.getPortletDefinitionByUniqueName( portletName, true );
                
                if ( portletDef != null && portletIcons != null )
                {
                    InitParam iconParam = portletDef.getInitParam("portlet-icon");
                    String iconParamVal = ( iconParam == null ) ? null : iconParam.getParamValue();
                    if ( iconParamVal != null && iconParamVal.length() > 0 )
                    {
                        portletIcons.put( frag.getId(), iconParamVal );
                    }
                }
                else if ( portletDef == null )
                {
                    log.error( "GetPageAction could not obtain PortletDefinition for portlet " + portletName );
                }
            }
        }
    }
    
    
}
