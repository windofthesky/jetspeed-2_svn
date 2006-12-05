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
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.JetspeedActions;
import org.apache.jetspeed.ajax.AjaxAction;
import org.apache.jetspeed.ajax.AjaxBuilder;
import org.apache.jetspeed.components.portletregistry.PortletRegistry;
import org.apache.jetspeed.decoration.DecorationValve;
import org.apache.jetspeed.layout.PortletActionSecurityBehavior;
import org.apache.jetspeed.om.page.Fragment;
import org.apache.jetspeed.om.page.Page;
import org.apache.jetspeed.page.PageManager;
import org.apache.jetspeed.request.RequestContext;
import org.apache.pluto.om.common.Parameter;
import org.apache.pluto.om.common.ParameterSet;
import org.apache.pluto.om.portlet.PortletDefinition;
import org.apache.pluto.om.common.DisplayName;

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
    protected Log log = LogFactory.getLog(GetPageAction.class);
    
    private PortletRegistry registry;
    private DecorationValve decorationValve;
    
    public GetPageAction(String template, 
            String errorTemplate, 
            PageManager pageManager,
            PortletActionSecurityBehavior securityBehavior,
            PortletRegistry registry,
            DecorationValve decorationValve)
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
            resultMap.put(ACTION, "getpage");
            if (false == checkAccess(requestContext, JetspeedActions.VIEW))
            {
                resultMap.put(REASON, "Insufficient access to view page");
                success = false;
                return success;
            }            
            
            // Run the Decoration valve to get actions
            decorationValve.invoke(requestContext, null);
            
            //String filter = getActionParameter(requestContext, FILTER);            
            Page page = requestContext.getPage();
            String pageName = getActionParameter(requestContext, PAGE);
            if (pageName != null)
            {
                page = retrievePage(requestContext, pageName);
            }
            resultMap.put(STATUS, status);
            resultMap.put(PAGE, page);
            putSecurityInformation(resultMap, page);                        
            String fragments = getActionParameter(requestContext, FRAGMENTS);
            if (fragments == null)
            {
                resultMap.put(FRAGMENTS, "true");
            }
            else
            {
                if (fragments.equalsIgnoreCase("true"))
                {
                    resultMap.put(FRAGMENTS, "true");
                }
                else
                {
                    resultMap.put(FRAGMENTS, "false");
                    return success;
                }
            }
            Map fragSizes = new HashMap();
            Map portletIcons = new HashMap();
            retrieveFragmentSpecialProperties( requestContext, page.getRootFragment(), fragSizes, portletIcons );
            resultMap.put( SIZES, fragSizes );
            resultMap.put( "portletIcons", portletIcons );
        } 
        catch (Exception e)
        {
            // Log the exception
            log.error("exception while getting page", e);

            // Return a failure indicator
            success = false;
        }

        return success;
	}
    
    protected Page retrievePage(RequestContext requestContext, String pageName)
    throws Exception
    {        
        if (pageName == null)
        {
            pageName = "/";
        }
        Page page = pageManager.getPage(pageName);
        return page;
    }        
    
    
    protected void retrieveFragmentSpecialProperties( RequestContext requestContext, Fragment frag, Map fragSizes, Map portletIcons )
    {
        if ( frag == null )
        {
            return;
        }
    	if ( fragSizes != null && "layout".equals( frag.getType() ) )
    	{   // get layout fragment sizes
    		String sizesVal = frag.getProperty( "sizes" );
    		if ( sizesVal == null || sizesVal.length() == 0 )
    		{
    			String layoutName = frag.getName();
    			if ( layoutName != null && layoutName.length() > 0 )
    			{
    				// logic below is copied from org.apache.jetspeed.portlets.MultiColumnPortlet
    				PortletDefinition portletDef = registry.getPortletDefinitionByUniqueName( layoutName );
                    
    				ParameterSet paramSet = portletDef.getInitParameterSet();
    				Parameter sizesParam = paramSet.get( "sizes" );
    				String sizesParamVal = ( sizesParam == null ) ? null : sizesParam.getValue();
    				if ( sizesParamVal != null && sizesParamVal.length() > 0 )
    				{
    					fragSizes.put( frag.getId(), sizesParamVal );
    					//log.info( "GetPageAction settings sizes for " + frag.getId() + " to " + sizesParamVal);
    				}
    				else
    				{
    					Parameter colsParam = paramSet.get( "columns" );
    					String colsParamVal = ( colsParam == null ) ? null : colsParam.getValue();
    					if ( colsParamVal != null && colsParamVal.length() > 0 )
    					{
    						int cols = 0;
    						try
    						{
    							cols = Integer.parseInt( colsParamVal );
    						}
    						catch ( NumberFormatException ex )
    						{
    						}
    						if ( cols < 1 )
    						{
    							cols = 2;
    						}
    						switch (cols)
    			            {
    			            	case 1: sizesParamVal = "100%"; break;
    			            	case 2: sizesParamVal = "50%,50%"; break;
    			            	case 3: sizesParamVal = "34%,33%,33%"; break;
    			            	default: sizesParamVal = "50%,50%"; break;
    			            }
    						fragSizes.put( frag.getId(), sizesParamVal );
    						//log.info( "GetPageAction defaulting sizes for " + frag.getId() + " to " + sizesParamVal);
    					}
    				}
    			}
    		}
    		List childFragments = frag.getFragments();
    		if ( childFragments != null )
    		{
    			Iterator childFragIter = childFragments.iterator();
    			while ( childFragIter.hasNext() )
    			{
    				Fragment childFrag = (Fragment)childFragIter.next();
                    retrieveFragmentSpecialProperties( requestContext, childFrag, fragSizes, portletIcons );
    			}
    		}
    	}
        else if ( portletIcons != null && "portlet".equals( frag.getType() ) )
        {   // get portlet icon and locale specific portlet display name
            String portletName = frag.getName();
            if ( portletName != null && portletName.length() > 0 )
            {
                PortletDefinition portletDef = registry.getPortletDefinitionByUniqueName( portletName );
                
                if ( portletDef != null && portletIcons != null )
                {
                    ParameterSet paramSet = portletDef.getInitParameterSet();
                    Parameter iconParam = paramSet.get( "portlet-icon" );
                    String iconParamVal = ( iconParam == null ) ? null : iconParam.getValue();
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
