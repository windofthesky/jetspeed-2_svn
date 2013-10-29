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
import org.apache.jetspeed.decoration.DecorationValve;
import org.apache.jetspeed.layout.PortletActionSecurityBehavior;
import org.apache.jetspeed.om.page.ContentFragment;
import org.apache.jetspeed.om.page.ContentPage;
import org.apache.jetspeed.om.page.Fragment;
import org.apache.jetspeed.page.PageManager;
import org.apache.jetspeed.request.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Map;

/**
 * Get Portlet Actions retrieves the current set of valid actions for one or more portlet windows
 * <p/>
 * AJAX Parameters:
 * id = the fragment id of the portlet for which to retrieve the action list
 * multiple id parameters are supported
 * page = (implied in the URL)
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: $
 */
public class GetPortletActionsAction
        extends BasePortletAction
        implements AjaxAction, AjaxBuilder, Constants {
    protected static final Logger log = LoggerFactory.getLogger(GetPortletActionsAction.class);
    protected String action;
    private DecorationValve decorationValve;

    public GetPortletActionsAction(String template,
                                   String errorTemplate,
                                   String action,
                                   DecorationValve decorationValve)
            throws AJAXException {
        this(template, errorTemplate, action, decorationValve, null, null);
    }

    public GetPortletActionsAction(String template,
                                   String errorTemplate,
                                   String action,
                                   DecorationValve decorationValve,
                                   PageManager pageManager,
                                   PortletActionSecurityBehavior securityBehavior)
            throws AJAXException {
        super(template, errorTemplate, pageManager, securityBehavior);
        this.action = action;
        this.decorationValve = decorationValve;
    }

    public boolean runBatch(RequestContext requestContext, Map<String, Object> resultMap) throws AJAXException {
        return runAction(requestContext, resultMap, true);
    }

    public boolean run(RequestContext requestContext, Map<String, Object> resultMap)
            throws AJAXException {
        return runAction(requestContext, resultMap, false);
    }

    public boolean runAction(RequestContext requestContext, Map<String, Object> resultMap, boolean batch) {
        boolean success = true;
        String status = "success";
        try {
            resultMap.put(ACTION, action);

            ContentPage page = requestContext.getPage();

            // Get the necessary parameters off of the request
            ArrayList getActionsForFragments = new ArrayList();
            String[] portletIds = requestContext.getRequest().getParameterValues(PORTLETID);
            if (portletIds != null && portletIds.length > 0) {
                for (int i = 0; i < portletIds.length; i++) {
                    String portletId = portletIds[i];
                    ContentFragment fragment = (ContentFragment) page.getFragmentById(portletId);
                    if (fragment == null) {
                        throw new Exception("fragment not found for specified portlet id: " + portletId);
                    }
                    getActionsForFragments.add(fragment);
                }
                getActionsForFragments.add(page.getRootFragment());
            }

            // Run the Decoration valve to get actions
            decorationValve.initFragments(requestContext, true, getActionsForFragments);

            if (getActionsForFragments.size() > 0) {
                Fragment rootFragment = (Fragment) getActionsForFragments.remove(getActionsForFragments.size() - 1);
                resultMap.put(PAGE, rootFragment);
            }

            resultMap.put(PORTLETS, getActionsForFragments);

            resultMap.put(STATUS, status);
        } catch (Exception e) {
            // Log the exception
            log.error("exception while getting actions for a fragment", e);
            resultMap.put(REASON, e.toString());
            // Return a failure indicator
            success = false;
        }

        return success;
    }
}
