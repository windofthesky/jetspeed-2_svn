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

import java.io.IOException;
import java.util.Locale;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.apache.jetspeed.container.PortletDispatcherIncludeAware;

/**
 * Factory implementation for creating HTTP Response Wrappers
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class ServletResponseImpl extends HttpServletResponseWrapper implements PortletDispatcherIncludeAware
{
    private boolean included;
    
    public ServletResponseImpl(HttpServletResponse response)
    {
        super(response);
    }

    public void setResponse(HttpServletResponse response) 
    {
        super.setResponse(response);
    }   

    /**
     * @param included when true, JSR-168 PLT.16.3.3 rules need to be enforced
     */
    public void setPortletDispatcherIncluded(boolean included)
    {
        this.included = included;
    }
    
    /*
     * JSR-168 PLT.16.3.3 .cxxxviii
     * @deprecated use encodeRedirectURL instead
     */
    public String encodeRedirectUrl(String url)
    {
        return (included ? null : super.encodeRedirectUrl(url));
    }

    /*
     * JSR-168 PLT.16.3.3 .cxxxviii
     */
    public String encodeRedirectURL(String url)
    {
        return (included ? null : super.encodeRedirectURL(url));
    }

    /*
     * JSR-168 PLT.16.3.3 .cxl
     */
    public void addCookie(Cookie arg0)
    {
        if (!included)
        {
            super.addCookie(arg0);
        }
    }

    /*
     * JSR-168 PLT.16.3.3 .cxl
     */
    public void addDateHeader(String arg0, long arg1)
    {
        if (!included)
        {
            super.addDateHeader(arg0, arg1);
        }
    }

    /*
     * JSR-168 PLT.16.3.3 .cxl
     */
    public void addHeader(String arg0, String arg1)
    {
        if (!included)
        {
            super.addHeader(arg0, arg1);
        }
    }

    /*
     * JSR-168 PLT.16.3.3 .cxl
     */
    public void addIntHeader(String arg0, int arg1)
    {
        if (!included)
        {
            super.addIntHeader(arg0, arg1);
        }
    }

    /*
     * JSR-168 PLT.16.3.3 .cxl
     */
    public boolean containsHeader(String arg0)
    {
        return (included ? false : super.containsHeader(arg0));
    }

    /*
     * JSR-168 PLT.16.3.3 .cxl
     */
    public void sendError(int arg0, String arg1) throws IOException
    {
        if (!included)
        {
            super.sendError(arg0, arg1);
        }
    }

    /*
     * JSR-168 PLT.16.3.3 .cxl
     */
    public void sendRedirect(String arg0) throws IOException
    {
        if (!included)
        {
            super.sendRedirect(arg0);
        }
    }

    /*
     * JSR-168 PLT.16.3.3 .cxl
     */
    public void setDateHeader(String arg0, long arg1)
    {
        if (!included)
        {
            super.setDateHeader(arg0, arg1);
        }
    }

    /*
     * JSR-168 PLT.16.3.3 .cxl
     */
    public void setHeader(String arg0, String arg1)
    {
        if (!included)
        {
            super.setHeader(arg0, arg1);
        }
    }

    /*
     * JSR-168 PLT.16.3.3 .cxl
     */
    public void setIntHeader(String arg0, int arg1)
    {
        if (!included)
        {
            super.setIntHeader(arg0, arg1);
        }
    }

    /*
     * JSR-168 PLT.16.3.3 .cxl
     */
    public void setStatus(int arg0, String arg1)
    {
        if (!included)
        {
            super.setStatus(arg0, arg1);
        }
    }

    /*
     * JSR-168 PLT.16.3.3 .cxl
     */
    public void setStatus(int arg0)
    {
        if (!included)
        {
            super.setStatus(arg0);
        }
    }

    /*
     * JSR-168 PLT.16.3.3 .cxl
     */
    public void setContentLength(int arg0)
    {
        if (!included)
        {
            super.setContentLength(arg0);
        }
    }

    /*
     * JSR-168 PLT.16.3.3 .cxl
     */
    public void setContentType(String arg0)
    {
        if (!included)
        {
            super.setContentType(arg0);
        }
    }

    /*
     * JSR-168 PLT.16.3.3 .cxl
     */
    public void setLocale(Locale arg0)
    {
        if (!included)
        {
            super.setLocale(arg0);
        }
    }
}
