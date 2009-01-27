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

import java.io.ByteArrayOutputStream;
import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

/**
 * <p>
 * BufferedHttpServletResponse fully captures all HttpServletResponse interactions to be flushed out later.
 * This wrapper is specifically written to allow included servlets to set headers, cookies, encoding etc. which isn't allowed by
 * the servlet specification on included responses.
 * </p>
 * <p>
 * Call flush(HttpServletResponse) after the include has returned to flush out the buffered data, headers and state.
 * </p>
 * <p>
 * Note: the only method not fully supported by this buffered version is getCharacterEncoding(). Setting characterEncoding through
 * setContentType or setLocale on this class won't be reflected in the return value from getCharacterEncoding(), and calling getWriter()
 * won't set it either although calling setLocale, setContentType or setCharacterEncoding (servlet api 2.4+) after that will be ignored.
 * But, when this object is flused to a (real) response, the contentType, locale and/or characterEncoding recorded will be set on the
 * target response then.
 * </p>
 * 
 * @author <a href="mailto:ate@douma.nu">Ate Douma</a>
 * @version $Id$
 */
public class BufferedHttpServletResponse extends HttpServletResponseWrapper
{
    private static class CharArrayWriterBuffer extends CharArrayWriter
    {
        public char[] getBuffer()
        {
            return buf;
        }
        
        public int getCount()
        {
            return count;
        }
    }
    
    private ByteArrayOutputStream byteOutputBuffer;
    private CharArrayWriterBuffer charOutputBuffer;
    private ServletOutputStream outputStream;
    private PrintWriter printWriter;
    private HashMap<String, ArrayList<String>> headers;
    private ArrayList<Cookie> cookies;
    private int errorCode;
    private int statusCode;
    private String errorMessage;
    private String redirectLocation;
    private boolean committed;
    private boolean hasStatus;
    private boolean hasError;
    private Locale locale;
    private boolean closed;
    private String characterEncoding;
    private boolean setContentTypeAfterEncoding;
    private int contentLength = -1;
    private String contentType;
    private boolean flushed;
    
    public BufferedHttpServletResponse(HttpServletResponse response)
    {
        super(response);
    }
    
    public void flush(HttpServletResponse response) throws IOException
    {
        if (flushed)
        {
            throw new IllegalStateException("Already flushed");            
        }
        flushed = true;
        
        if (cookies != null)
        {
            for (Cookie cookie : cookies)
            {
                response.addCookie(cookie);
            }
            cookies = null;
        }
        if (locale != null)
        {
            response.setLocale(locale);
        }        
        
        if (contentType != null)
        {
            if (characterEncoding != null)
            {
                if (setContentTypeAfterEncoding)
                {
                    response.setCharacterEncoding(characterEncoding);
                    response.setContentType(contentType);
                }
                else
                {
                    response.setContentType(contentType);
                    response.setCharacterEncoding(characterEncoding);
                }
            }
            else
            {
                response.setContentType(contentType);
            }
        }
        else if (characterEncoding != null)
        {
            response.setCharacterEncoding(characterEncoding);
        }
        
        if (headers != null)
        {
            for (Map.Entry<String, ArrayList<String>> entry : headers.entrySet())
            {
                for (String value : entry.getValue())
                {
                    response.addHeader(entry.getKey(), value);
                }
            }
            headers = null;
        }
        if (contentLength > -1)
        {
            response.setContentLength(contentLength);
        }
        if (hasStatus)
        {
            response.setStatus(statusCode);
        }
        if (hasError)
        {
            response.sendError(errorCode, errorMessage);            
        }
        else if (redirectLocation != null)
        {
            response.sendRedirect(redirectLocation);
        }
        else
        {
            if (outputStream != null)
            {
                if (!closed)
                {
                    outputStream.flush();
                }
                ServletOutputStream realOutputStream = response.getOutputStream();
                int len = byteOutputBuffer.size();
                if (contentLength > -1 && contentLength < len)
                {
                    len = contentLength;
                }
                if (len > 0)
                {
                    realOutputStream.write(byteOutputBuffer.toByteArray(), 0, len);
                }
                outputStream.close();
                outputStream = null;
                byteOutputBuffer = null;
            }
            else if (printWriter != null)
            {
                if (!closed)
                {
                    printWriter.flush();
                    if ( charOutputBuffer.getCount() > 0)
                    {
                        response.getWriter().write(charOutputBuffer.getBuffer(), 0, charOutputBuffer.getCount());
                    }
                    printWriter.close();
                    
                    printWriter = null;
                    charOutputBuffer = null;
                }
            }
            
        }
    }
    
    private ArrayList<String> getHeaderList(String name, boolean create)
    {
        if ( headers == null )
        {
            headers = new HashMap<String, ArrayList<String>>();
        }
        ArrayList<String> headerList = headers.get(name);
        if ( headerList == null && create )
        {
            headerList = new ArrayList<String>();
            headers.put(name,headerList);
        }
        return headerList;
    }
    
    private void failIfCommitted()
    {
        if (committed)
        {
            throw new IllegalStateException("Response is already committed");
        }
    }
    
    /* (non-Javadoc)
     * @see javax.servlet.http.HttpServletResponseWrapper#addCookie(javax.servlet.http.Cookie)
     */
    public void addCookie(Cookie cookie)
    {
        if ( !committed )
        {
            if ( cookies == null )
            {
                cookies = new ArrayList<Cookie>();
            }
            cookies.add(cookie);
        }
    }

    /* (non-Javadoc)
     * @see javax.servlet.http.HttpServletResponseWrapper#addDateHeader(java.lang.String, long)
     */
    public void addDateHeader(String name, long date)
    {
        if (!committed)
        {
            addHeader(name, Long.toString(date));
        }
    }

    /* (non-Javadoc)
     * @see javax.servlet.http.HttpServletResponseWrapper#addHeader(java.lang.String, java.lang.String)
     */
    public void addHeader(String name, String value)
    {
        if (!committed)
        {
            getHeaderList(name, true).add(value);
        }
    }

    /* (non-Javadoc)
     * @see javax.servlet.http.HttpServletResponseWrapper#addIntHeader(java.lang.String, int)
     */
    public void addIntHeader(String name, int value)
    {
        if (!committed)
        {
            addHeader(name, Integer.toString(value));
        }
    }

    /* (non-Javadoc)
     * @see javax.servlet.http.HttpServletResponseWrapper#containsHeader(java.lang.String)
     */
    public boolean containsHeader(String name)
    {
        return getHeaderList(name, false) != null;
    }

    /* (non-Javadoc)
     * @see javax.servlet.http.HttpServletResponseWrapper#sendError(int, java.lang.String)
     */
    public void sendError(int errorCode, String errorMessage) throws IOException
    {
        failIfCommitted();
        committed = true;
        closed = true;
        hasError = true;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    /* (non-Javadoc)
     * @see javax.servlet.http.HttpServletResponseWrapper#sendError(int)
     */
    public void sendError(int errorCode) throws IOException
    {
        sendError(errorCode, null);
    }

    /* (non-Javadoc)
     * @see javax.servlet.http.HttpServletResponseWrapper#sendRedirect(java.lang.String)
     */
    public void sendRedirect(String redirectLocation) throws IOException
    {
        failIfCommitted();
        closed = true;
        committed = true;
        this.redirectLocation = redirectLocation;
    }

    /* (non-Javadoc)
     * @see javax.servlet.http.HttpServletResponseWrapper#setDateHeader(java.lang.String, long)
     */
    public void setDateHeader(String name, long date)
    {
        if (!committed)
        {
            setHeader(name, Long.toString(date));
        }
    }

    /* (non-Javadoc)
     * @see javax.servlet.http.HttpServletResponseWrapper#setHeader(java.lang.String, java.lang.String)
     */
    public void setHeader(String name, String value)
    {
        if (!committed)
        {
            ArrayList<String> headerList = getHeaderList(name, true);
            headerList.clear();
            headerList.add(value);
        }
    }

    /* (non-Javadoc)
     * @see javax.servlet.http.HttpServletResponseWrapper#setIntHeader(java.lang.String, int)
     */
    public void setIntHeader(String name, int value)
    {
        if (!committed)
        {
            setHeader(name, Integer.toString(value));
        }
    }

    /* (non-Javadoc)
     * @see javax.servlet.http.HttpServletResponseWrapper#setStatus(int, java.lang.String)
     */
    public void setStatus(int statusCode, String message)
    {
        throw new UnsupportedOperationException("This method is deprecated and no longer available");
    }

    /* (non-Javadoc)
     * @see javax.servlet.http.HttpServletResponseWrapper#setStatus(int)
     */
    public void setStatus(int statusCode)
    {
        if (!committed)
        {
            this.statusCode = statusCode;
            this.hasStatus = true;
            resetBuffer();
        }
    }

    /* (non-Javadoc)
     * @see javax.servlet.ServletResponseWrapper#flushBuffer()
     */
    public void flushBuffer() throws IOException
    {
        if (!closed)
        {
            committed = true;
        }
    }

    /* (non-Javadoc)
     * @see javax.servlet.ServletResponseWrapper#getBufferSize()
     */
    public int getBufferSize()
    {
        return Integer.MAX_VALUE;
    }

    /* (non-Javadoc)
     * @see javax.servlet.ServletResponseWrapper#getCharacterEncoding()
     */
    public String getCharacterEncoding()
    {
        return characterEncoding != null ? characterEncoding : "ISO-8859-1";
    }

    /* (non-Javadoc)
     * @see javax.servlet.ServletResponseWrapper#getLocale()
     */
    public Locale getLocale()
    {
        return locale != null ? locale : super.getLocale();
    }

    /* (non-Javadoc)
     * @see javax.servlet.ServletResponseWrapper#getOutputStream()
     */
    public ServletOutputStream getOutputStream() throws IOException
    {
        if (outputStream == null)
        {
            if (printWriter != null)
            {
                throw new IllegalStateException("getWriter() has already been called on this response");
            }
            byteOutputBuffer = new ByteArrayOutputStream();
            outputStream = new ServletOutputStream()
            {
                public void write(int b) throws IOException
                {
                    if (!closed)
                    {
                        byteOutputBuffer.write(b);
                        if (contentLength>-1 && byteOutputBuffer.size()>=contentLength)
                        {
                            committed = true;
                            closed = true;
                        }
                    }
                }
            };
        }
        return outputStream;
    }

    /* (non-Javadoc)
     * @see javax.servlet.ServletResponseWrapper#getWriter()
     */
    public PrintWriter getWriter() throws IOException
    {
        if (printWriter == null)
        {
            if (outputStream != null)
            {
                throw new IllegalStateException("getOutputStream() has already been called on this response");
            }
            charOutputBuffer = new CharArrayWriterBuffer();
            printWriter = new PrintWriter(charOutputBuffer);
        }
        return printWriter;
    }

    /* (non-Javadoc)
     * @see javax.servlet.ServletResponseWrapper#isCommitted()
     */
    public boolean isCommitted()
    {
        return committed;
    }

    /* (non-Javadoc)
     * @see javax.servlet.ServletResponseWrapper#reset()
     */
    public void reset()
    {
        resetBuffer(); // fails if committed
        headers = null;
        cookies = null;
        hasStatus = false;
        contentLength = -1;
        if (printWriter == null)
        {
            contentType = null;
            characterEncoding = null;
            locale = null;
        }
    }

    /* (non-Javadoc)
     * @see javax.servlet.ServletResponseWrapper#resetBuffer()
     */
    public void resetBuffer()
    {
        failIfCommitted();
        if (outputStream != null)
        {
            try { outputStream.flush(); } catch (Exception e){}
            byteOutputBuffer.reset();
        }
        else if (printWriter != null)
        {
            printWriter.flush();
            charOutputBuffer.reset();
        }
    }

    /* (non-Javadoc)
     * @see javax.servlet.ServletResponseWrapper#setBufferSize(int)
     */
    public void setBufferSize(int size)
    {
        failIfCommitted();
        if ( (charOutputBuffer != null && charOutputBuffer.size() > 0)
                || (byteOutputBuffer != null && byteOutputBuffer.size() > 0) )
        {
            throw new IllegalStateException("Content has already been written");
        }
    }

    /* (non-Javadoc)
     * @see javax.servlet.ServletResponseWrapper#setCharacterEncoding(java.lang.String)
     */
    public void setCharacterEncoding(String charset)
    {
        if (charset != null && !committed && printWriter == null)
        {
            characterEncoding = charset;
            setContentTypeAfterEncoding = false;
        }
    }

    /* (non-Javadoc)
     * @see javax.servlet.ServletResponseWrapper#setContentLength(int)
     */
    public void setContentLength(int len)
    {
        if (!committed && printWriter == null && len > 0)
        {
            contentLength = len;
            if (outputStream != null)
            {
                try { outputStream.flush(); } catch (Exception e){}
            }
            if ( !closed && byteOutputBuffer != null && byteOutputBuffer.size() >= len )
            {
                committed = true;
                closed = true;
            }
        }
    }

    /* (non-Javadoc)
     * @see javax.servlet.ServletResponseWrapper#setContentType(java.lang.String)
     */
    public void setContentType(String type)
    {
        if (!committed)
        {
            contentType = type;
            setContentTypeAfterEncoding = false;
            if (printWriter == null)
            {
                // TODO: parse possible encoding for better return value from getCharacterEncoding()
            }
        }
    }

    /* (non-Javadoc)
     * @see javax.servlet.ServletResponseWrapper#setLocale(java.util.Locale)
     */
    public void setLocale(Locale locale)
    {
        if (!committed)
        {
            this.locale = locale;
        }
    }
}
