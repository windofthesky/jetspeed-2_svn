/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2000-2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *    "Apache Jetspeed" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Apache Jetspeed", nor may "Apache" appear in their name, without
 *    prior written permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.jetspeed.pipeline.valve.impl;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Stack;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
    private static final Log log = LogFactory.getLog(VerySimpleLayoutValveImpl.class);

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
