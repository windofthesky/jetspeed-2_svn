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
package org.apache.jetspeed.aggregator.impl;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import org.apache.pluto.util.PrintWriterServletOutputStream;

public class HttpBufferedResponse extends javax.servlet.http.HttpServletResponseWrapper
{
    private boolean usingWriter;
    private boolean usingStream;

    /** Commons logging */
    protected final static Log log = LogFactory.getLog(HttpBufferedResponse.class);

    private ServletOutputStream wrappedStream;
    private PrintWriter writer;

    public HttpBufferedResponse(HttpServletResponse servletResponse,
                                PrintWriter writer)
    {
        super(servletResponse);
        this.writer = writer;
    }

    public ServletOutputStream getOutputStream() throws IllegalStateException, IOException
    {
        if (usingWriter)
        {
            throw new IllegalStateException("getOutputStream can't be used after getWriter was invoked");
        }

        if (wrappedStream == null)
        {
            wrappedStream = new PrintWriterServletOutputStream(getResponse().getWriter(),
                                                               getResponse().getCharacterEncoding());
        }

        usingStream = true;

        return wrappedStream;
    }

    public PrintWriter getWriter() throws UnsupportedEncodingException, IllegalStateException, IOException {

        if (usingStream)
        {
            throw new IllegalStateException("getWriter can't be used after getOutputStream was invoked");
        }

        usingWriter = true;

        return writer;
    }


    public void setBufferSize(int size)
    {
        // ignore
    }

    public int getBufferSize()
    {
        return 0;
    }

    public void flushBuffer() throws IOException
    {
        writer.flush();
    }

    public boolean isCommitted()
    {
        return false;
    }

    public void reset()
    {
        // ignore right now
    }
}
