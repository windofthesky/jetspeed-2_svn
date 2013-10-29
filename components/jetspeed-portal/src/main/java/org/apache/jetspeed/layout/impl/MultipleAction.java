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
import org.apache.jetspeed.ajax.AjaxRequestService;
import org.apache.jetspeed.layout.PortletActionSecurityBehavior;
import org.apache.jetspeed.page.PageManager;
import org.apache.jetspeed.request.JetspeedRequestContext;
import org.apache.jetspeed.request.RequestContext;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * 
 * @author David Gurney
 * 
 * The purpose of this object is to run several AJAX actions and aggregate the
 * results into a single response. This is useful when the client needs to make
 * more than one call as the result of a single user action.
 * 
 * The sample request URL is shown below:
 * 
 * http://host:port/ajaxapi?action=multiple&commands=(action;name,value;name,value)(action;name,value)
 * 
 * The constructor accepts a map of the actions that are available to be run.
 * The name,value pairs are parameter values needed by the action. The actions
 * are run in the order that they are found on the URL string
 * 
 */
public class MultipleAction extends BasePortletAction implements AjaxAction,
        AjaxBuilder, BeanFactoryAware
{

    protected static final String ALL_RESULTS = "results";

    protected static final String BUILD_RESULTS = "buildresults";

    protected static final String MULTIPLE_ACTION_PROCESSOR = "Multiple Action Processor";

    protected static final String COMMANDS = "commands";

    protected static final String COMMAND_TOKEN = ")";

    protected static final String PARAM_TOKEN = ";";

    protected static final String VALUE_TOKEN = ",";

    protected Map actionMap = null;

    protected VelocityEngine m_oVelocityEngine = null;

    public MultipleAction(AjaxRequestService requestService, String p_sTemplate,
            String p_sErrorTemplate, PageManager p_oPageManager,
            PortletActionSecurityBehavior p_oSecurityBehavior,
            VelocityEngine p_oVelocityEngine)
    {
        super(p_sTemplate, p_sErrorTemplate, p_oPageManager,
                p_oSecurityBehavior);
        actionMap = requestService.getActionMap();
        m_oVelocityEngine = p_oVelocityEngine;
    }

    public void setBeanFactory(BeanFactory beanFactory) throws BeansException
    {
        // get the proxied object for this, and put it in the map to avoid circular dep
        Object proxy = beanFactory.getBean("AjaxMultipleAction");
        actionMap.put("multiple", proxy);        
    }
    
    public boolean run(RequestContext p_oRequestContext, Map<String,Object> p_oResultMap)
            throws AJAXException
    {
        boolean a_bReturnSuccess = true;
        List a_oResultArray = new ArrayList();

        p_oResultMap.put(ACTION, "multiple");
        p_oResultMap.put(STATUS, "success");

        // Get the command string
        String a_sCommands = p_oRequestContext.getRequestParameter(COMMANDS);
        if (a_sCommands == null || a_sCommands.length() <= 0)
        {
            buildErrorContext(p_oRequestContext, p_oResultMap);
            p_oResultMap.put(STATUS, "failure");
            p_oResultMap.put(REASON, "command parameters not found");

            throw new AJAXException("command parameters not found");
        }

        // Tokenize the commands into single commands
        StringTokenizer a_oCommandTok = new StringTokenizer(a_sCommands,
                COMMAND_TOKEN);

        // Process each command
        while (a_oCommandTok.hasMoreTokens())
        {
            // Get the token
            String a_sCommand = a_oCommandTok.nextToken();

            // Strip off the opening (
            a_sCommand = a_sCommand.substring(1);

            // Tokenize the single commands into parameters
            StringTokenizer a_oParamTok = new StringTokenizer(a_sCommand,
                    PARAM_TOKEN);
            if (a_oParamTok == null || a_oParamTok.hasMoreTokens() == false)
            {
                buildErrorContext(p_oRequestContext, p_oResultMap);
                p_oResultMap.put(STATUS, "failure");
                p_oResultMap.put(REASON, "incorrect url request");

                throw new AJAXException("incorrect url request");
            }

            // Get the action - which is the first item in the list
            String a_sAction = a_oParamTok.nextToken();

            // Lookup the action from the action map
            Object a_oActionObject = actionMap.get(a_sAction);
            if (a_oActionObject == null
                    && !(a_oActionObject instanceof AjaxAction))
            {
                buildErrorContext(p_oRequestContext, p_oResultMap);
                p_oResultMap.put(REASON, "unknown action requested==>"
                        + a_sAction);

                throw new AJAXException("unknown action requested==>"
                        + a_sAction);
            }

            AjaxAction a_oAction = (AjaxAction) a_oActionObject;

            JetspeedRequestContext a_oJetspeedRequestContext = (JetspeedRequestContext) p_oRequestContext;

            // Process each parameter for this action
            while (a_oParamTok.hasMoreTokens())
            {
                String a_sName = a_oParamTok.nextToken(VALUE_TOKEN);
                // Strip of the leading ; if present
                if (a_sName.indexOf(';') >= 0)
                {
                    a_sName = a_sName.substring(1);
                }

                String a_sValue = a_oParamTok.nextToken();

                // Put the parameters on the request context
                a_oJetspeedRequestContext.setAttribute(a_sName, a_sValue);
            }

            // Invoke the action
            Map a_oResultMap = new HashMap();
            boolean a_bSuccess;

            try
            {
                a_bSuccess = a_oAction.runBatch(a_oJetspeedRequestContext,
                        a_oResultMap);
            } catch (Exception e)
            {
                // Move the reason into the return map
                p_oResultMap.put(REASON, a_oResultMap.get(REASON));

                throw new AJAXException(e);
            }

            // Check for success
            if (a_bSuccess)
            {
                // Invoke the builder for this action if possible
                if (a_oAction instanceof AjaxBuilder)
                {
                    processBuilder((AjaxBuilder) a_oAction, a_oResultMap,
                            p_oRequestContext, a_bSuccess);
                }

                // Get the build results
                String a_sBuildResults = (String) a_oResultMap
                        .get(BUILD_RESULTS);

                // Look for an xml tag and strip it off
                int a_iStartIndex = a_sBuildResults.indexOf("<?xml");
                if (a_iStartIndex >= 0)
                {
                    // Look for the end of the tag
                    int a_iEndIndex = a_sBuildResults.indexOf(">",
                            a_iStartIndex);
                    if (a_iEndIndex >= 0)
                    {
                        String a_sStart = a_sBuildResults.substring(0,
                                a_iStartIndex);
                        String a_sEnd = a_sBuildResults.substring(
                                a_iEndIndex + 1, a_sBuildResults.length());
                        a_sBuildResults = a_sStart + a_sEnd;
                    }
                }

                if (a_sBuildResults != null)
                {
                    // Save the results
                    a_oResultArray.add(a_sBuildResults);
                }
            } else
            {
                // Move the reason into the return map
                p_oResultMap.put(REASON, a_oResultMap.get(REASON));

                // Exit the loop
                a_bReturnSuccess = false;
                break;
            }
        }

        // Save the results for later building into the response
        p_oResultMap.put(ALL_RESULTS, a_oResultArray);

        return a_bReturnSuccess;
    }

    // Process the builder if provided
    protected void processBuilder(AjaxBuilder p_oBuilder, Map<String,Object> p_oInputMap,
            RequestContext p_oRequestContext, boolean p_oActionSuccessFlag)
    {
        try
        {
            // Ask the builder to construct the context
            // Add the input map to the velocity context
            boolean result = true;

            if (p_oActionSuccessFlag == true)
            {
                result = p_oBuilder
                        .buildContext(p_oRequestContext, p_oInputMap);
            } else
            {
                result = p_oBuilder.buildErrorContext(p_oRequestContext,
                        p_oInputMap);
            }

            Context a_oContext = new VelocityContext(p_oInputMap);

            // Check to see if we have a valid context
            if (result)
            {
                // Get the name of the template from the builder
                String a_sTemplateName = null;

                if (p_oActionSuccessFlag == true)
                {
                    a_sTemplateName = p_oBuilder.getTemplate();
                } else
                {
                    a_sTemplateName = p_oBuilder.getErrorTemplate();
                }

                // Get a reader to the velocity template
                final InputStream a_oTemplateStream = this.getClass()
                        .getClassLoader().getResourceAsStream(a_sTemplateName);

                Reader a_oTemplate = new InputStreamReader(a_oTemplateStream);

                // The results of the velocity template will be stored here
                StringWriter a_oStringWriter = new StringWriter();

                // Run the velocity template
                m_oVelocityEngine.evaluate(a_oContext, a_oStringWriter,
                        MULTIPLE_ACTION_PROCESSOR, a_oTemplate);

                // Get the results from the velocity processing
                String a_sResults = a_oStringWriter.getBuffer().toString();

                // Save the results on the input map
                p_oInputMap.put(BUILD_RESULTS, a_sResults);
            } else
            {
                log.error("could not create builder context");
            }
        } catch (Exception e)
        {
            log.error("builder failed", e);
            p_oInputMap.put(Constants.REASON, e.toString());
        }
    }

    public boolean buildContext(RequestContext p_oRequestContext,
            Map<String,Object> p_oInputMap)
    {
        boolean a_bResults = true;

        a_bResults = super.buildContext(p_oRequestContext, p_oInputMap);

        return a_bResults;
    }

}
