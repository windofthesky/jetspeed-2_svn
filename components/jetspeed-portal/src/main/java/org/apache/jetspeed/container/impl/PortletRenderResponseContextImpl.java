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

import javax.portlet.MimeResponse;
import javax.portlet.PortletMode;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.jetspeed.aggregator.PortletContent;
import org.apache.jetspeed.container.PortletWindow;
import org.apache.jetspeed.portlet.HeadElement;
import org.apache.jetspeed.util.DOMUtils;
import org.apache.pluto.container.PortletContainer;
import org.apache.pluto.container.PortletRenderResponseContext;
import org.apache.pluto.container.util.PrintWriterServletOutputStream;
import org.w3c.dom.Element;

/**
 * @version $Id$
 *
 */
public class PortletRenderResponseContextImpl extends PortletMimeResponseContextImpl implements
                PortletRenderResponseContext
{
    private boolean committed;
    private PortletContent portletContent;
    private OutputStream outputStream;
    
    public PortletRenderResponseContextImpl(PortletContainer container, HttpServletRequest containerRequest,
                                            HttpServletResponse containerResponse, PortletWindow window)
    {
        super(container, containerRequest, containerResponse, window);
        this.portletContent = window.getFragment().getPortletContent();
    }

    public void flushBuffer() throws IOException
    {
        committed = true;
    }

    public int getBufferSize()
    {
        return Integer.MAX_VALUE;
    }
    
    public boolean isCommitted()
    {
        return committed;
    }
    
    public OutputStream getOutputStream() throws IOException, IllegalStateException
    {
        if (isClosed())
        {
            return null;
        }
        if (outputStream == null)
        {
            outputStream = new PrintWriterServletOutputStream(portletContent.getWriter(),
                                                              getServletResponse().getCharacterEncoding());
        }
        return outputStream;
    }

    public PrintWriter getWriter() throws IOException, IllegalStateException
    {
        return portletContent.getWriter();
    }
    
    public void setNextPossiblePortletModes(Collection<PortletMode> portletModes)
    {
        //TODO
    }

    public void reset()
    {
        if (!isClosed())
        {
            portletContent.reset();
        }
    }

    public void resetBuffer()
    {
        if (!isClosed())
        {
            portletContent.resetBuffer();
        }
    }

    public void setBufferSize(int size)
    {
        // ignore
    }

    public void setContentType(String contentType)
    {
        if (!isClosed())
        {
            portletContent.setContentType(contentType);
        }
    }

    public void setTitle(String title)
    {
        if (!isClosed())
        {
            portletContent.setTitle(title);
        }
    }
    
    @Override
    public void addProperty(String key, Element element)
    {
        if (MimeResponse.MARKUP_HEAD_ELEMENT.equals(key))
        {
            HeadElement headElement = null;
            
            // Note that element can be null.
            // According to the SPEC, the property with this key can be removed with null element.
            if (element != null)
            {
                headElement = new HeadElementImpl(element);
            }
            
            // ID attribute of element is used as keyHint for the head element if available.
            this.portletContent.addHeadElement(headElement, DOMUtils.getIdAttribute(element));
        }
        else
        {
            super.addProperty(key, element);
        }
    }
    
}
