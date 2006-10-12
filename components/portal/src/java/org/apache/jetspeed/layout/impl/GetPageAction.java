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
import java.util.HashMap;
import java.util.List;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.JetspeedActions;
import org.apache.jetspeed.ajax.AjaxAction;
import org.apache.jetspeed.ajax.AjaxBuilder;
import org.apache.jetspeed.components.portletregistry.PortletRegistry;
import org.apache.jetspeed.layout.PortletActionSecurityBehavior;
import org.apache.jetspeed.om.page.Link;
import org.apache.jetspeed.om.page.Page;
import org.apache.jetspeed.om.page.Fragment;
import org.apache.jetspeed.page.PageManager;
import org.apache.jetspeed.request.RequestContext;
import org.apache.pluto.om.portlet.PortletDefinition;
import org.apache.pluto.om.common.ParameterSet;
import org.apache.pluto.om.common.Parameter;

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
    extends BasePortletAction 
    implements AjaxAction, AjaxBuilder, Constants
{
    protected Log log = LogFactory.getLog(GetPageAction.class);
    
    private PortletRegistry registry;
    
    public GetPageAction(String template, 
            String errorTemplate, 
            PageManager pageManager,
            PortletActionSecurityBehavior securityBehavior,
            PortletRegistry registry)
    {
        super(template, errorTemplate, pageManager, securityBehavior);
        this.registry = registry;
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
            //String filter = getActionParameter(requestContext, FILTER);
            Page page = requestContext.getPage();
            String pageName = getActionParameter(requestContext, PAGE);
            if (pageName != null)
            {
                page = retrievePage(requestContext, pageName);
            }
            resultMap.put(STATUS, status);
            resultMap.put(PAGE, page);
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
            retrieveLayoutFragmentSizes( page.getRootFragment(), fragSizes );
            resultMap.put( SIZES, fragSizes );
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
    
    
    protected void retrieveLayoutFragmentSizes( Fragment frag, Map fragSizes )
    {
    	if ( frag != null && "layout".equals( frag.getType() ) )
    	{
    		String sizesVal = frag.getProperty( "sizes" );
    		if ( sizesVal == null || sizesVal.length() == 0 )
    		{
    			String layoutName = frag.getName();
    			if ( layoutName != null && layoutName.length() > 0 )
    			{
    				// logic below is copied from org.apache.jetspeed.portlets.MultiColumnPortlet
    				PortletDefinition portletDef = registry.getPortletDefinitionByUniqueName(frag.getName());
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
    				retrieveLayoutFragmentSizes( childFrag, fragSizes );
    			}
    		}
    	}
    }   
}
