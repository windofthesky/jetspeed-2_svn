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
