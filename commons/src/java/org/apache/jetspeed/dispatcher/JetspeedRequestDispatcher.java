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
package org.apache.jetspeed.dispatcher;

import java.io.PrintWriter;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.RenderResponse;
import javax.portlet.RenderRequest;
import javax.portlet.PortletException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pluto.core.impl.RenderRequestImpl;
import org.apache.pluto.core.impl.RenderResponseImpl;

/**
 * Implements the Portlet API Request Dispatcher to dispatch to portlets
 *
 * @author <a href="mailto:david@bluesunrise.com">David Sean Taylor</a>
 * @version $Id$
 */
public class JetspeedRequestDispatcher implements PortletRequestDispatcher
{
    private RequestDispatcher requestDispatcher;
    private static final Log log = LogFactory.getLog(JetspeedRequestDispatcher.class);

    public JetspeedRequestDispatcher(RequestDispatcher requestDispatcher)
    {
    	if(requestDispatcher == null)
    	{
    		throw new IllegalArgumentException("RequestDispatcher cannot be null for JetspeedRequestDispatcher.");
    	}
        this.requestDispatcher = requestDispatcher;
    }

    // portlet-only implementation

    public void include(RenderRequest request, RenderResponse response) throws PortletException, java.io.IOException
    {
        HttpServletResponse servletResponse = null;
        try
        {
            HttpServletRequest servletRequest = (HttpServletRequest) ((RenderRequestImpl) request).getRequest();
            servletResponse = (HttpServletResponse) ((RenderResponseImpl) response).getResponse();

            this.requestDispatcher.include(servletRequest, servletResponse);

        }
        catch (Exception e)
        {
            PrintWriter pw = null;
            if (servletResponse != null)
            {
                pw = servletResponse.getWriter();
                pw.write("JetspeedRequestDispatcher failed to include servlet resources. (details below) <br/>");
                pw.write("Exception: " + e.getClass().getName() + " <br/>");
                pw.write("Message: " + e.getMessage() + " <br/>");
                writeStackTrace(e.getStackTrace(), pw);

            }
            log.error("JetspeedRequestDispatcher failed (details below)");
            log.error(
                "Begin: ******************************************* JetspeedRequestDispatcher Failure Report******************************************");
            log.error("Cause: " + e.getMessage(), e);
            if (e.getCause() != null)
            {
                log.error("Root Cause: " + e.getCause().getMessage(), e.getCause());
                if (pw != null)
                {
                    pw.write("<p>Root Cause: </p>");
                    pw.write("Message: " + e.getCause().getMessage() + " <br/>");
                    pw.write("Exception: " + e.getCause().getClass().getName() + " <br/>");
                    writeStackTrace(e.getCause().getStackTrace(), pw);
                }

                log.error(
                    "End: *******************************************JetspeedRequestDispatcher Failure Report******************************************");
                pw.flush();
                throw new PortletException(e);
            }
            else
            {
                log.error(
                    "End: *******************************************JetspeedRequestDispatcher Failure Report******************************************");
				pw.flush();
                throw new PortletException(e);
            }
        }
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
