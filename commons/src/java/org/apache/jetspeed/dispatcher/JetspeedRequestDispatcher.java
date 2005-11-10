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

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.RenderResponse;
import javax.portlet.RenderRequest;
import javax.portlet.PortletException;

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
        catch (RuntimeException re)
        {
            // PLT.16.3.4 cxlii: 
            // RuntimeExceptions must be propagated back
            throw re;
        }
        catch (IOException ioe)
        {
            // PLT.16.3.4 cxlii: 
            // IOExceptions must be propagated back
            throw ioe;
        }
        catch (Exception e)
        {
            // PLT.16.3.4 cxliii: 
            // All other exceptions, including ServletExceptions must be wrapped in a PortletException 
            // with the root cause set to the original exception before propagated back

            Throwable rootCause = null;
            if ( e instanceof ServletException)
            {
                rootCause = ((ServletException)e).getRootCause();
            }
            else
            {
                rootCause = e.getCause();
            }
            throw new PortletException(rootCause != null ? rootCause : e);
        }
    }
}
