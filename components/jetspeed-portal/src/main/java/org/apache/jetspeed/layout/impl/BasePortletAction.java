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

import org.apache.jetspeed.ajax.AJAXException;
import org.apache.jetspeed.ajax.AjaxAction;
import org.apache.jetspeed.ajax.AjaxBuilder;
import org.apache.jetspeed.layout.PortletActionSecurityBehavior;
import org.apache.jetspeed.om.page.ContentFragment;
import org.apache.jetspeed.om.page.ContentPage;
import org.apache.jetspeed.page.PageManager;
import org.apache.jetspeed.request.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.Map;

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
    protected static final Logger log = LoggerFactory.getLogger(BasePortletAction.class);    
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

    public boolean buildContext(RequestContext requestContext, Map<String,Object> responseContext)
    {
        return true;
    }

    public boolean buildErrorContext(RequestContext requestContext,
            Map<String,Object> responseContext)
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
    
    public boolean isCreateNewPageOnEditEnabled()
    {
    	if (securityBehavior == null)
            return false;
    	return securityBehavior.isCreateNewPageOnEditEnabled();
    }
    public boolean isPageQualifiedForCreateNewPageOnEdit(RequestContext context)
    {
    	if (securityBehavior == null)
            return false;
    	return securityBehavior.isPageQualifiedForCreateNewPageOnEdit(context);
    }
    public boolean createNewPageOnEdit(RequestContext context)
    {
        if (securityBehavior == null)
            return false;
        
        return securityBehavior.createNewPageOnEdit(context);        
    }
    
    public ContentFragment getFragmentIdFromLocation( int row, int column, ContentPage page )
    {
    	return getFragmentIdFromLocation( row, column, page.getRootFragment() );
    }
    public ContentFragment getFragmentIdFromLocation( int row, int column, ContentFragment parentFragment )
    {
        Iterator fragments = parentFragment.getFragments().iterator();
        while ( fragments.hasNext() )
        {
            ContentFragment fragment = (ContentFragment)fragments.next();
            if ( fragment.getLayoutColumn() == column && fragment.getLayoutRow() == row )
            {
                return fragment;
            }
        }
        return null;
    }
    
    public boolean runBatch(RequestContext requestContext, Map<String,Object> resultMap) throws AJAXException
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
    
    public String getNonNullActionParameter(RequestContext requestContext, String name)
    {
        String result = getActionParameter(requestContext, name);
        if (result == null)
            return "";
        return result;
    }
    
    public ContentFragment getParentFragmentById(String id, ContentFragment root)
    {
    	return NestedFragmentContext.getParentFragmentById( id, root );
    }    
}
