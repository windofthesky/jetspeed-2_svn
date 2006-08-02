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
package org.apache.portals.gems.util;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

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
            wrappedStream = new PrintWriterServletOutputStream(writer, getResponse().getCharacterEncoding());                                                               
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
