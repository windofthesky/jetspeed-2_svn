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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.ajax.AjaxAction;
import org.apache.jetspeed.ajax.AjaxBuilder;
import org.apache.jetspeed.om.page.ContentPage;
import org.apache.jetspeed.om.page.Fragment;
import org.apache.jetspeed.request.RequestContext;

/**
 * Get Portlet portlet placement action
 *
 * @author <a>David Gurney</a>
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: $
 */
public class GetPortletsAction 
    extends BasePortletAction 
    implements AjaxAction, AjaxBuilder, Constants 
{
    /** Logger */
    protected Log log = LogFactory.getLog(GetPortletsAction.class);

    public GetPortletsAction(String template, String errorTemplate)
    {
        super(template, errorTemplate);
    }

    public boolean run(RequestContext requestContext, Map resultMap)
    {
        boolean success = true;

        try
        {
            resultMap.put(ACTION, "getportlets");

            // Get the fragment information from the page
            ContentPage a_oPage = requestContext.getPage();

            // David Taylor is working on a method like this
            // page.getFragmentByEntitlement();

            Fragment a_oRootFragment = a_oPage.getRootFragment();

            resultMap.put(STATUS, "success");

            // resultMap.put(FRAGMENTS, a_oFragments);

        } 
        catch (Exception e)
        {
            // Log the exception
            log.error("exception while getting portlet info", e);

            // Return a failure indicator
            success = false;
        }

        return success;
	}
}
