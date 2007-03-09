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

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.ajax.AJAXException;
import org.apache.jetspeed.ajax.AjaxAction;
import org.apache.jetspeed.ajax.AjaxBuilder;
import org.apache.jetspeed.layout.PortletActionSecurityBehavior;
import org.apache.jetspeed.om.page.Fragment;
import org.apache.jetspeed.om.page.Page;
import org.apache.jetspeed.page.PageManager;
import org.apache.jetspeed.request.RequestContext;

/**
 * Abstract portlet placement action
 *
 * @author <a>David Gurney</a>
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: $
 */
public abstract class BasePortletAction 
    implements AjaxAction, AjaxBuilder, Constants 
{
    protected Log log = LogFactory.getLog(BasePortletAction.class);    
	protected String template = null;
    protected PageManager pageManager = null;
    protected String errorTemplate = null;
    protected PortletActionSecurityBehavior securityBehavior;
    
    public BasePortletAction(String template, 
                             String errorTemplate, 
                             PortletActionSecurityBehavior securityBehavior)
    {
        this.template = template;
        this.errorTemplate = errorTemplate;
        this.securityBehavior = securityBehavior;
    }

    public BasePortletAction(String template, 
            String errorTemplate, 
            PageManager pageManager)
    {
        this.template = template;
        this.errorTemplate = errorTemplate;
        this.pageManager = pageManager;
        this.securityBehavior = null;
    }
    
    public BasePortletAction(String template, 
                             String errorTemplate, 
                             PageManager pageManager,
                             PortletActionSecurityBehavior securityBehavior)
    {
        this(template, errorTemplate, securityBehavior);
        this.pageManager = pageManager;
    }

    public boolean buildContext(RequestContext requestContext, Map responseContext)
    {
        return true;
    }

    public boolean buildErrorContext(RequestContext requestContext,
            Map responseContext) 
    {
        responseContext.put(STATUS, "failure");

        // Check for the case where we don't know basic information
        if (responseContext.get(ACTION) == null)
        {
            responseContext.put(ACTION, "unknown");
        }

        if (responseContext.get(PORTLETID) == null)
        {
            responseContext.put(PORTLETID, "unknown");
        }

        return true;
    }

    public String getErrorTemplate()
    {
        return errorTemplate;
    }

    public String getTemplate()
    {
        return template;
    }

    public boolean checkAccess(RequestContext context, String action)
    {
        boolean access = true;
        if (null != securityBehavior)
        {
            access = securityBehavior.checkAccess(context, action);
        }
        return access;
    }

    public boolean createNewPageOnEdit(RequestContext context)
    {
        return securityBehavior.createNewPageOnEdit(context);        
    }
        
    // TODO: support nested fragments
    public Fragment getFragmentIdFromLocation(int row, int column, Page page)
    {
        Fragment root = page.getRootFragment();
        Iterator fragments = root.getFragments().iterator();
        while (fragments.hasNext())
        {
            Fragment fragment = (Fragment)fragments.next();
            if (fragment.getLayoutColumn() == column &&
                fragment.getLayoutRow() == row)
            {
                return fragment;
            }
        }
        return null;
    }
    
    public boolean runBatch(RequestContext requestContext, Map resultMap) throws AJAXException
    {
        return run(requestContext, resultMap);
    }
    
    public String getActionParameter(RequestContext requestContext, String name)
    {
        String parameter = requestContext.getRequestParameter(name);
        if (parameter == null)
        {
            Object o = requestContext.getAttribute(name);
            if (o != null)
            {
                if (o instanceof String)
                    return (String)o;
            }
        }
        return parameter;
    }
    
    public Fragment getParentFragmentById(String id, Fragment root)
    {
        if ( id == null )
        {
            return null;
        }
        return searchForParentFragmentById( id, root );
    }
    
    protected Fragment searchForParentFragmentById( String id, Fragment parent )
    {   
        // find fragment by id, tracking fragment parent
        Fragment matchedParent = null;
        if( parent != null ) 
        {
            // process the children
            List children = parent.getFragments();
            for( int i = 0, cSize = children.size() ; i < cSize ; i++) 
            {
                Fragment childFrag = (Fragment)children.get( i );
                if ( childFrag != null ) 
                {
                    if ( id.equals( childFrag.getId() ) )
                    {
                        matchedParent = parent;
                        break;
                    }
                    else
                    {
                        matchedParent = searchForParentFragmentById( id, childFrag );
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
