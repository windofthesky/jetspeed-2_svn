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

package org.apache.jetspeed.container.impl;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Locale;

import javax.portlet.PortletMode;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.pluto.container.PortletContainer;
import org.apache.pluto.container.PortletRenderResponseContext;
import org.apache.pluto.container.PortletResourceResponseContext;
import org.apache.pluto.container.util.PrintWriterServletOutputStream;
import org.apache.jetspeed.container.PortletWindow;

/**
 * PortletResourceResponseContextImpl implements <em>both</em> PortletResourceResponseContext
 * and PortletRenderResponseContext to support Portlet 1.0 based Portlets using the 
 * Portals Bridges pre Portlet 2.0 PortletResourceURLFactory to serve resources on top of
 * a RenderURL.
 * 
 * Jetspeed still provides backwards compatibility by forwarding to the portlet and plugging
 * in this PortletResourceResponseContextImpl instead of PortletRenderResponseContextImpl,
 * which is why it needs to implement both interfaces.
 * 
 * The PortletRenderResponseContext specific methods however simply ignore any invocation.
 * 
 * @version $Id$
 *
 */
public class PortletResourceResponseContextImpl extends PortletMimeResponseContextImpl implements
                PortletResourceResponseContext, PortletRenderResponseContext
{
    private static final String DEFAULT_CONTAINER_CHARSET = "UTF-8";
    
    private OutputStream outputStream;
    private boolean charsetSet;
    
    public PortletResourceResponseContextImpl(PortletContainer container, HttpServletRequest containerRequest,
                                              HttpServletResponse containerResponse, PortletWindow window)
    {        
        super(container, containerRequest, containerResponse, window);
    }
    
    /**
     * PortletRenderResponseContext method provided to support PortletResourceURLFactory usage
     * which is served over a RenderURL. Any invocation is ignored.
     */
    public void setTitle(String title)
    {
        // ignore
    }

    /**
     * PortletRenderResponseContext method provided to support PortletResourceURLFactory usage
     * which is served over a RenderURL. Any invocation is ignored.
     */
    public void setNextPossiblePortletModes(Collection<PortletMode> portletModes)
    {
        //ignore
    }

    public void flushBuffer() throws IOException
    {
        if (!isClosed())
        {
            getServletResponse().flushBuffer();
        }
    }
    
    public int getBufferSize()
    {
        return getServletResponse().getBufferSize();
    }

    @Override
    public void close()
    {
        outputStream = null;
        super.close();
    }

    public boolean isCommitted()
    {
        return getServletResponse().isCommitted();
    }

    public OutputStream getOutputStream() throws IOException, IllegalStateException
    {
        if (isClosed())
        {
            return null;
        }
        if (outputStream == null)
        {
            try
            {
                outputStream = getServletResponse().getOutputStream();
            }
            catch (IllegalStateException e)
            {
                // handle situation where underlying ServletResponse its getWriter()
                // has been called already anyway: return a wrapped PrintWriter in that case
                if (!charsetSet)
                {
                    setCharacterEncoding(DEFAULT_CONTAINER_CHARSET);
                }
                outputStream = new PrintWriterServletOutputStream(getServletResponse().getWriter(),
                                                                   getServletResponse().getCharacterEncoding());
            }
        }
        return outputStream;
    }

    public PrintWriter getWriter() throws IOException, IllegalStateException
    {
        return isClosed() ? null : getServletResponse().getWriter();
    }

    public void reset()
    {
        if (!isClosed())
        {
            getServletResponse().reset();
        }
    }

    public void resetBuffer()
    {
        if (!isClosed())
        {
            getServletResponse().reset();
        }
    }

    public void setBufferSize(int size)
    {
        if (!isClosed())
        {
            getServletResponse().setBufferSize(size);
        }
    }

    public void setContentType(String contentType)
    {
        if (!isClosed())
        {
            getServletResponse().setContentType(contentType);
        }
    }

    public void setCharacterEncoding(String charset)
    {
        if (!isClosed())
        {
            charsetSet = true;
            getContainerResponse().setCharacterEncoding(charset);
        }
    }

    public void setContentLength(int len)
    {
        if (!isClosed())
        {
            getContainerResponse().setContentLength(len);
        }
    }

    public void setLocale(Locale locale)
    {
        if (!isClosed())
        {
            getContainerResponse().setLocale(locale);
        }
    }
}
