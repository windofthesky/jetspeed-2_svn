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
package org.apache.jetspeed.engine.servlet;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;

import org.apache.jetspeed.request.RequestContext;
import org.apache.pluto.om.window.PortletWindow;

/**
 * Factory implementation for creating HTTP Request Wrappers
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class ServletRequestFactoryImpl
    implements ServletRequestFactory
{
    private ServletConfig servletConfig;
    
    public void init(javax.servlet.ServletConfig config, Map properties) 
    throws Exception
    {
        servletConfig = config;
    }
    
    public void destroy()
    throws Exception
    {
    }

    protected HttpServletRequest createRequest(HttpServletRequest request, PortletWindow window)
    {
        return new ServletRequestImpl(request, window);        
    }
    
    public HttpServletRequest getServletRequest(HttpServletRequest request, PortletWindow window)
    {
        HttpServletRequest servletRequest = createRequest(request, window);
        
        // Set page encoding in order to parse the form data correctly        
        String preferedEnc = (String) request.getAttribute(RequestContext.PREFERED_CHARACTERENCODING_KEY);
        if (preferedEnc != null)
        {
            try
            {
                servletRequest.setCharacterEncoding(preferedEnc);
            }
            catch (UnsupportedEncodingException e)
            {
                ;
            }
        }

        return servletRequest;
    }
    
}
