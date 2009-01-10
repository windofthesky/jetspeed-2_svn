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

package org.apache.jetspeed.resource;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Locale;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.pluto.PortletContainer;
import org.apache.pluto.PortletWindow;
import org.apache.pluto.internal.impl.RenderResponseImpl;

/**
 * Custom RenderResponse to be used for pre Portlet API 2.0 Resource requests
 * allowing full control over the Response state even while served as RenderResponse.
 * 
 * This ResourceRenderResponse is expected to wrap a BufferedHttpServletResponse.
 * 
 * @author <a href="mailto:ate@douma.nu">Ate Douma</a>
 * @version $Id$
 *
 */
public class ResourceRenderResponseImpl extends RenderResponseImpl
{
    /**
     * @param container
     * @param portletWindow
     * @param servletRequest
     * @param servletResponse
     */
    public ResourceRenderResponseImpl(PortletContainer container, PortletWindow portletWindow,
                                      HttpServletRequest servletRequest, HttpServletResponse servletResponse)
    {
        super(container, portletWindow, servletRequest, servletResponse);
    }
    
    @Override
    public HttpServletResponse getResponse()
    {
        return (HttpServletResponse)super.getResponse();
    }

    /* (non-Javadoc)
     * @see org.apache.pluto.internal.impl.RenderResponseImpl#getBufferSize()
     */
    @Override
    public int getBufferSize()
    {
        return getResponse().getBufferSize();
    }

    /* (non-Javadoc)
     * @see org.apache.pluto.internal.impl.RenderResponseImpl#getContentType()
     */
    @Override
    public String getContentType()
    {
        return getResponse().getContentType();
    }

    /* (non-Javadoc)
     * @see org.apache.pluto.internal.impl.RenderResponseImpl#setCharacterEncoding(java.lang.String)
     */
    @Override
    public void setCharacterEncoding(String arg0)
    {
        getResponse().setCharacterEncoding(arg0);
    }

    /* (non-Javadoc)
     * @see org.apache.pluto.internal.impl.RenderResponseImpl#setContentLength(int)
     */
    @Override
    public void setContentLength(int arg0)
    {
        getResponse().setContentLength(arg0);
    }

    /* (non-Javadoc)
     * @see org.apache.pluto.internal.impl.RenderResponseImpl#setContentType(java.lang.String)
     */
    @Override
    public void setContentType(String contentType)
    {
        getResponse().setContentType(contentType);
    }

    /* (non-Javadoc)
     * @see org.apache.pluto.internal.impl.RenderResponseImpl#setLocale(java.util.Locale)
     */
    @Override
    public void setLocale(Locale arg0)
    {
        getResponse().setLocale(arg0);
    }

    /* (non-Javadoc)
     * @see org.apache.pluto.internal.impl.MimeResponseImpl#addDateHeader(java.lang.String, long)
     */
    @Override
    public void addDateHeader(String arg0, long arg1)
    {
        getResponse().addDateHeader(arg0, arg1);
    }

    /* (non-Javadoc)
     * @see org.apache.pluto.internal.impl.MimeResponseImpl#addHeader(java.lang.String, java.lang.String)
     */
    @Override
    public void addHeader(String arg0, String arg1)
    {
        getResponse().addHeader(arg0, arg1);
    }

    /* (non-Javadoc)
     * @see org.apache.pluto.internal.impl.MimeResponseImpl#addIntHeader(java.lang.String, int)
     */
    @Override
    public void addIntHeader(String arg0, int arg1)
    {
        getResponse().addIntHeader(arg0, arg1);
    }

    /* (non-Javadoc)
     * @see org.apache.pluto.internal.impl.MimeResponseImpl#flushBuffer()
     */
    @Override
    public void flushBuffer() throws IOException
    {
        getResponse().flushBuffer();
    }

    /* (non-Javadoc)
     * @see org.apache.pluto.internal.impl.MimeResponseImpl#getCharacterEncoding()
     */
    @Override
    public String getCharacterEncoding()
    {
        return getResponse().getCharacterEncoding();
    }

    /* (non-Javadoc)
     * @see org.apache.pluto.internal.impl.MimeResponseImpl#getLocale()
     */
    @Override
    public Locale getLocale()
    {
        return getResponse().getLocale();
    }

    /* (non-Javadoc)
     * @see org.apache.pluto.internal.impl.MimeResponseImpl#getOutputStream()
     */
    @Override
    public ServletOutputStream getOutputStream() throws IllegalStateException, IOException
    {
        return getResponse().getOutputStream();
    }

    /* (non-Javadoc)
     * @see org.apache.pluto.internal.impl.MimeResponseImpl#getWriter()
     */
    @Override
    public PrintWriter getWriter() throws IOException
    {
        return getResponse().getWriter();
    }

    /* (non-Javadoc)
     * @see org.apache.pluto.internal.impl.MimeResponseImpl#isCommitted()
     */
    @Override
    public boolean isCommitted()
    {
        return getResponse().isCommitted();
    }

    /* (non-Javadoc)
     * @see org.apache.pluto.internal.impl.MimeResponseImpl#reset()
     */
    @Override
    public void reset()
    {
        getResponse().reset();
    }

    /* (non-Javadoc)
     * @see org.apache.pluto.internal.impl.MimeResponseImpl#resetBuffer()
     */
    @Override
    public void resetBuffer()
    {
        getResponse().resetBuffer();
    }

    /* (non-Javadoc)
     * @see org.apache.pluto.internal.impl.MimeResponseImpl#sendRedirect(java.lang.String)
     */
    @Override
    public void sendRedirect(String arg0) throws IOException
    {
        getResponse().sendRedirect(arg0);
    }

    /* (non-Javadoc)
     * @see org.apache.pluto.internal.impl.MimeResponseImpl#setBufferSize(int)
     */
    @Override
    public void setBufferSize(int size)
    {
        getResponse().setBufferSize(size);
    }

    /* (non-Javadoc)
     * @see org.apache.pluto.internal.impl.MimeResponseImpl#setDateHeader(java.lang.String, long)
     */
    @Override
    public void setDateHeader(String arg0, long arg1)
    {
        getResponse().setDateHeader(arg0, arg1);
    }

    /* (non-Javadoc)
     * @see org.apache.pluto.internal.impl.MimeResponseImpl#setHeader(java.lang.String, java.lang.String)
     */
    @Override
    public void setHeader(String arg0, String arg1)
    {
        getResponse().setHeader(arg0, arg1);
    }

    /* (non-Javadoc)
     * @see org.apache.pluto.internal.impl.MimeResponseImpl#setIntHeader(java.lang.String, int)
     */
    @Override
    public void setIntHeader(String arg0, int arg1)
    {
        getResponse().setIntHeader(arg0, arg1);
    }

    /* (non-Javadoc)
     * @see org.apache.pluto.internal.impl.MimeResponseImpl#setStatus(int, java.lang.String)
     */
    @Override
    public void setStatus(int arg0, String arg1)
    {
        getResponse().setStatus(arg0, arg1);
    }

    /* (non-Javadoc)
     * @see org.apache.pluto.internal.impl.MimeResponseImpl#setStatus(int)
     */
    @Override
    public void setStatus(int arg0)
    {
        getResponse().setStatus(arg0);
    }

    /* (non-Javadoc)
     * @see org.apache.pluto.internal.impl.PortletResponseImpl#addCookie(javax.servlet.http.Cookie)
     */
    @Override
    public void addCookie(Cookie arg0)
    {
        getResponse().addCookie(arg0);
    }

    /* (non-Javadoc)
     * @see org.apache.pluto.internal.impl.PortletResponseImpl#containsHeader(java.lang.String)
     */
    @Override
    public boolean containsHeader(String arg0)
    {
        return getResponse().containsHeader(arg0);
    }

    /* (non-Javadoc)
     * @see org.apache.pluto.internal.impl.PortletResponseImpl#sendError(int, java.lang.String)
     */
    @Override
    public void sendError(int arg0, String arg1) throws IOException
    {
        getResponse().sendError(arg0, arg1);
    }

    /* (non-Javadoc)
     * @see org.apache.pluto.internal.impl.PortletResponseImpl#sendError(int)
     */
    @Override
    public void sendError(int arg0) throws IOException
    {
        getResponse().sendError(arg0);
    }
}
