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
package org.apache.jetspeed.ajax;

import org.apache.jetspeed.layout.impl.Constants;
import org.apache.jetspeed.request.RequestContext;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;
import org.apache.velocity.tools.generic.EscapeTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * Provides a generic way to handle a Ajax request/response. Useful for AJAX since
 * the processing can be broken down into actions and builders
 */
public class AjaxRequestServiceImpl implements AjaxRequestService
{

    protected static final String CONTENT_TYPE = "text/xml";

    protected static final String AJAX_PROCESSOR = "AJAX processor";

    protected static final String DEFAULT_ERROR = "<js><status>failure</status><action>unknown</action></js>";

    // Name of the parameter that will be used to lookup the
    // command object. Default is action.
    //
    // Sample URL:
    // http://localhost/js?pipeline=layout&action=move
    //
    // In this case the parameter "action" is used to find "move"
    // "move" will be used as the key to lookup the object that will
    // handle the command
    protected static final String URL_PARAMETER_NAME = "action";

    /** Logger */
    protected Logger log = LoggerFactory.getLogger(AjaxRequestServiceImpl.class);

    // Actions that are available to execution. These actions must
    // implement either the Action interface or the Builder interface
    // or both.
    // If the Action interface is implemented, then the run method is called
    // If the Build interface is implemented, then the build methods are called
    protected Map<String,AjaxAction> actions;

    // Used to create the response XML
    protected VelocityEngine velocityEngine = null;

    // Parameter on the URL that will be used to lookup the object
    protected String urlParameterName = URL_PARAMETER_NAME;

    // Default Action if no action specified
    protected String defaultAction = "getpage";
    
    // Handy Velocity Escape Tool (is threadsafe)
    protected EscapeTool velocityEscTool = null;
    
    // Spring can be used to inject this information
    public AjaxRequestServiceImpl(Map<String,AjaxAction> objects, VelocityEngine velocityEngine)
    {
        this.actions = objects;
        this.velocityEngine = velocityEngine;
        this.velocityEscTool = new EscapeTool();
    }

    // Spring can be used to inject this information
    public AjaxRequestServiceImpl(Map<String,AjaxAction> objects, VelocityEngine velocityEngine,
            String urlParameterName)
    {
        this.actions = objects;
        this.velocityEngine = velocityEngine;
        this.urlParameterName = urlParameterName;
        this.velocityEscTool = new EscapeTool();
    }
    
    // This is the entry point for this service
    public void process(RequestContext requestContext) throws AJAXException
    {        
        // Lookup the object that is to be used
        String objectKey = requestContext.getRequestParameter(urlParameterName);        
        if (objectKey == null)
        {
            objectKey = defaultAction;
        }
        // Get the object associated with this key
        AjaxAction action = actions.get(objectKey);
        if (action != null)
        {
            Map<String,Object> resultMap = new HashMap<String,Object>();

            boolean success = true;
            try
            {
                success = processAction(action, requestContext, resultMap);
            } catch (Exception e)
            {
                success = false;
            }

            try
            {
                // Check to see if this object implements the builder
                // interface
                if (action instanceof AjaxBuilder)
                {
                    processBuilder((AjaxBuilder) action, resultMap,
                            requestContext, success);
                }
            } catch (Exception e)
            {
                // The builder failed, return an error response
                buildError(requestContext);
            }
        } else
        {
            // Log an informational message
            log.debug("could not find the object named:" + objectKey);

            // Return an error response
            buildError(requestContext);
        }
    }

    // Process the action if provided
    protected boolean processAction(AjaxAction action,
            RequestContext requestContext, Map<String,Object> resultMap)
            throws Exception
    {
        return action.run(requestContext, resultMap);
    }

    // Process the builder if provided
    protected void processBuilder(AjaxBuilder builder, Map<String,Object> inputMap,
            RequestContext requestContext, boolean actionSuccessFlag)
    {
        // Response will always be text/xml
        String format = requestContext.getRequestParameter("format");
        if (format == null)
        {
            requestContext.getResponse().setContentType(CONTENT_TYPE);
        }
        else
        {
            if (format.equals("json"))
            {
                requestContext.getResponse().setContentType("text/json");
            }
        }

        try
        {
            // Ask the builder to construct the context
            // Add the input map to the velocity context
            
            boolean result = true;

            if (actionSuccessFlag == true)
            {
                result = builder.buildContext(requestContext, inputMap);
            }
            else
            {
                result = builder.buildErrorContext(requestContext, inputMap);
            }
            
            Context context = new VelocityContext(inputMap);
            context.put("esc", this.velocityEscTool);
            
            // Check to see if we have a valid context
            if (result)
            {
                // Get the name of the template from the builder
                String templateName = null;

                if (actionSuccessFlag == true)
                {
                    templateName = builder.getTemplate();
                } 
                else
                {
                    templateName = builder.getErrorTemplate();
                }

                // Get a reader to the velocity template
                final InputStream templateStream = this.getClass()
                        .getClassLoader().getResourceAsStream(templateName);

                Reader template = new InputStreamReader(templateStream);

                // The results of the velocity template will be stored here
                StringWriter stringWriter = new StringWriter();

                // Run the velocity template
                velocityEngine.evaluate(context, stringWriter,
                        AJAX_PROCESSOR, template);

                // Get the results from the velocity processing
                String buffer = stringWriter.getBuffer().toString();

                //log.debug("output from AjaxService:" + buffer);

                // Put the response XML on the response object
                HttpServletResponse response = requestContext.getResponse();
                ServletOutputStream sos = response.getOutputStream();
                
                Writer writer = new OutputStreamWriter(sos, "UTF-8");
                writer.write(buffer);
                writer.flush();
            } 
            else
            {
                log.error("could not create builder context");
                buildError(requestContext);
            }
        } catch (Exception e)
        {
            log.error("builder failed", e);
            inputMap.put(Constants.REASON, e.toString());
            
            buildError(requestContext);
        }
    }

    // This is the last chance to handle an error to send back to the client
    // Send back a generic response.  Subclasses may want to override this 
    // method
    protected void buildError(RequestContext requestContext)
    {
        try
        {
            requestContext.getResponse().getOutputStream().print(DEFAULT_ERROR);
        } 
        catch (IOException e)
        {
            // Not much can be done here, an exception while handling an exception
            log.error("exception while trying to build an error message", e);
        }
    }
    
    /**
     * @return Returns the actions.
     */
    public Map<String,AjaxAction> getActionMap()
    {
        return actions;
    }

}
