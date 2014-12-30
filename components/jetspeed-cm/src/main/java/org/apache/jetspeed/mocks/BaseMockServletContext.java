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
package org.apache.jetspeed.mocks;

import com.mockrunner.mock.web.MockServletContext;

import javax.servlet.ServletContext;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("deprecation")
public abstract class BaseMockServletContext extends MockServletContext implements ServletContext
{
    private final Map attributes = new HashMap();

    public BaseMockServletContext()
    {
        super();
    }
    public Object getAttribute(String arg0)
    {
        return attributes.get(arg0);
    }

    /*
    public Enumeration getAttributeNames()
    {
        unsupported();
        return null;
    }

    public ServletContext getContext(String arg0)
    {
        unsupported();
        return null;
    }

    public String getInitParameter(String arg0)
    {
        unsupported();
        return null;
    }

    public Enumeration getInitParameterNames()
    {
        unsupported();
        return null;
    }

    public int getMajorVersion()
    {
        return 2;
    }

    public String getMimeType(String arg0)
    {
        unsupported();
        return null;
    }

    public int getMinorVersion()
    {
        return 3;
    }

    public RequestDispatcher getNamedDispatcher(String arg0)
    {
        unsupported();
        return null;
    }

    public String getRealPath(String arg0)
    {
        unsupported();
        return null;
    }

    public RequestDispatcher getRequestDispatcher(String arg0)
    {
        unsupported();
        return null;
    }

    public URL getResource(String arg0) throws MalformedURLException
    {
        unsupported();
        return null;
    }

    public InputStream getResourceAsStream(String arg0)
    {
        unsupported();
        return null;
    }

    public Set getResourcePaths(String arg0)
    {
        unsupported();
        return null;
    }

    public String getServerInfo()
    {
        unsupported();
        return null;
    }

    public Servlet getServlet(String arg0) throws ServletException
    {
        unsupported();
        return null;
    }

    public String getServletContextName()
    {
        unsupported();
        return null;
    }

    public Enumeration getServletNames()
    {
        unsupported();
        return null;
    }

    public Enumeration getServlets()
    {
        unsupported();
        return null;
    }

    public void log(Exception arg0, String arg1)
    {
        unsupported();
        
    }

    public void log(String arg0, Throwable arg1)
    {
        unsupported();
        
    }

    public void log(String arg0)
    {
        unsupported();
        
    }
*/
    public void removeAttribute(String arg0)
    {
        attributes.remove(arg0);
        
    }

    public void setAttribute(String arg0, Object arg1)
    {
        attributes.put(arg0, arg1);
        
    }
    
    protected final void unsupported() throws UnsupportedOperationException
    {
        throw new UnsupportedOperationException("The method called has not been implemented.");
    }

    @Override
    public synchronized boolean setInitParameter(String s, String s1) {
        return super.setInitParameter(s, s1);
    }
}
