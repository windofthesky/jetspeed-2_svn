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
package org.apache.jetspeed.login.impl;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.jetspeed.pipeline.PipelineException;
import org.apache.jetspeed.pipeline.valve.AbstractValve;
import org.apache.jetspeed.pipeline.valve.LoginViewValve;
import org.apache.jetspeed.pipeline.valve.ValveContext;
import org.apache.jetspeed.request.RequestContext;

/**
 * LoginJSPViewValveImpl
 * 
 * TODO: move this class into a new component?
 * @author <a href="mailto:shinsuke@yahoo.co.jp">Shinsuke Sugaya</a>
 * @version $Id: LoginJSPViewValve.java 186726 2004-06-05 05:13:09Z shinsuke $
 */
public class LoginJSPViewValve extends AbstractValve implements LoginViewValve
{
    private static final Logger log = LoggerFactory.getLogger(LoginJSPViewValve.class);

    private static final String DEFAULT_TEMPLATE_PATH = "/WEB-INF/templates/login";

    private String templatePath;

    public LoginJSPViewValve()
    {
        templatePath = DEFAULT_TEMPLATE_PATH;
    }

    public LoginJSPViewValve(String templatePath)
    {
        this.templatePath = templatePath;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.jetspeed.pipeline.valve.AbstractValve#invoke(org.apache.jetspeed.request.RequestContext,
     *      org.apache.jetspeed.pipeline.valve.ValveContext)
     */
    public void invoke(RequestContext request, ValveContext context) throws PipelineException
    {
        String loginTemplateFile = templatePath + "/" + request.getMediaType() + "/login.jsp";

        try
        {
            RequestDispatcher rd = request.getRequest().getRequestDispatcher(loginTemplateFile);
            rd.include(request.getRequest(), request.getResponse());
        }
        catch (ServletException e)
        {
            log.warn("The included login template file threw the exception.", e);
            throw new PipelineException("The included login template file threw the exception.", e);
        }
        catch (IOException e)
        {
            log.warn("I/O error occurred on the included login template file.", e);
            throw new PipelineException("I/O error occurred on the included login template file.", e);
        }

        // Pass control to the next Valve in the Pipeline
        context.invokeNext(request);
    }

    public String toString()
    {
        return "LoginViewValve";
    }
}
