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

import java.util.Iterator;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
                             PageManager pageManager,
                             PortletActionSecurityBehavior securityBehavior)
    {
        this.template = template;
        this.errorTemplate = errorTemplate;
        this.pageManager = pageManager;
        this.securityBehavior = securityBehavior;
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
        return securityBehavior.checkAccess(context, action);
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
}
