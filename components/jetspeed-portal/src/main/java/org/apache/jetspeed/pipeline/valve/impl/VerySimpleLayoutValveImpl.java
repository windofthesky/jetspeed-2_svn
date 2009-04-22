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
package org.apache.jetspeed.pipeline.valve.impl;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Stack;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.jetspeed.pipeline.PipelineException;
import org.apache.jetspeed.pipeline.valve.AbstractValve;
import org.apache.jetspeed.pipeline.valve.LayoutValve;
import org.apache.jetspeed.pipeline.valve.ValveContext;
import org.apache.jetspeed.request.RequestContext;

/**
 * <p>
 * VerySimpleLayoutValveImpl
 * </p>
 * 
 * Like the descriptions said this is a <b><i>very</i></b> simple
 * layout valve and should not be used in production.
 * 
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 *
 */
public class VerySimpleLayoutValveImpl extends AbstractValve implements LayoutValve
{
    private static final Logger log = LoggerFactory.getLogger(VerySimpleLayoutValveImpl.class);

    /**
     * @see org.apache.jetspeed.pipeline.valve.Valve#invoke(org.apache.jetspeed.request.RequestContext, org.apache.jetspeed.pipeline.valve.ValveContext)
     */
    public void invoke(RequestContext request, ValveContext context) throws PipelineException
    {
        try
        {
            log.info("Invoking the VerySimpleLayoutValve...");
            HttpServletRequest httpRequest = request.getRequest();
            RequestDispatcher rd = httpRequest.getRequestDispatcher("/pages/SimpleLayoutHeader.jsp");
            rd.include(httpRequest, request.getResponse());

            Stack renderStack = (Stack) httpRequest.getAttribute(CleanupValveImpl.RENDER_STACK_ATTR);
            if (renderStack == null)
            {
                renderStack = new Stack();
                httpRequest.setAttribute(CleanupValveImpl.RENDER_STACK_ATTR, renderStack);
            }
            renderStack.push("/pages/SimpleLayoutFooter.jsp");

        }
        catch (Exception e)
        {
            try
            {
                log.error("VerySimpleLayout: Unable to include layout header.  Layout not processed", e);
                PrintWriter pw = request.getResponse().getWriter();
                pw.write("VerySimpleLayoutFailed failed to include servlet resources. (details below) <br/>");
                pw.write("Exception: " + e.getClass().getName() + " <br/>");
                pw.write("Message: " + e.getMessage() + " <br/>");
                writeStackTrace(e.getStackTrace(), pw);

                if (e instanceof ServletException && ((ServletException) e).getRootCause() != null)
                {
                    Throwable rootCause = ((ServletException) e).getRootCause();
                    pw.write("Root Cause: " + rootCause.getClass().getName() + " <br/>");
                    pw.write("Message: " + rootCause.getMessage() + " <br/>");
                    writeStackTrace(rootCause.getStackTrace(), pw);
                }
            }
            catch (IOException e1)
            {
                // don't worry
            }

        }
        finally
        {
            context.invokeNext(request);
        }

    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        return "VerySimpleLayoutValveImpl";
    }

    protected static final void writeStackTrace(StackTraceElement[] traceArray, PrintWriter pw)
    {
        pw.write("<p>Stack Trace: </p>");
        for (int i = 0; i < traceArray.length; i++)
        {
            pw.write("&nbsp;&nbsp;&nbsp;" + traceArray[i].toString() + "<br />");
        }
    }

}
