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

import org.apache.jetspeed.ajax.AjaxAction;
import org.apache.jetspeed.ajax.AjaxBuilder;
import org.apache.jetspeed.om.common.SecuredResource;
import org.apache.jetspeed.om.page.Page;
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
	protected String template = null;

    protected String errorTemplate = null;

    public BasePortletAction(String template, String errorTemplate)
    {
        this.template = template;
        this.errorTemplate = errorTemplate;
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
        Page page = context.getPage();
        try
        {
            page.checkAccess(action);
            
        }
        catch (SecurityException e)
        {
            return false;
        }     
        return true;
    }
    
}
