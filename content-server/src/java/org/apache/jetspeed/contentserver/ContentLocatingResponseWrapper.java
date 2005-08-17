/*
 * Copyright 2000-2001,2004 The Apache Software Foundation.
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
package org.apache.jetspeed.contentserver;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import org.apache.pluto.util.PrintWriterServletOutputStream;

/**
 * <p>
 * ContentLocatingResponseWrapper
 * </p>
 * <p>
 * 
 * </p>
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver </a>
 * @version $Id$
 *  
 */
public class ContentLocatingResponseWrapper extends HttpServletResponseWrapper
{

    private ContentLocator contentLocator;
    private HttpServletResponse response;
    private boolean _404sent = false;
    private boolean locationAttempted = false;
    protected boolean outputStreamCalled;
    protected boolean writerCalled;
    protected PrintWriter writer;
    protected ServletOutputStream outputStream;

    /**
     * @param arg0
     */
    public ContentLocatingResponseWrapper( HttpServletResponse response, ContentLocator contentLocator )
    {
        super(response);
        this.contentLocator = contentLocator;
        this.response = response;    
        
        this.response.setHeader("Last-Modified", ContentFilter.HEADER_DATE_FORMAT.format(contentLocator.getLastModified()));
        this.response.setHeader("Cache-Control", "max-age=3600, must-revalidate, proxy-revalidate");
        this.response.setHeader("Apache-Jetspeed-Info", "real-path="+this.contentLocator.getBasePath());
    }

     /**
     * <p>
     * sendError
     * </p>
     * 
     * @see javax.servlet.http.HttpServletResponse#sendError(int,
     *      java.lang.String)
     * @param arg0
     * @param arg1
     * @throws java.io.IOException
     */
    public void sendError( int errorCode, String arg1 ) throws IOException
    {
        handleError(errorCode, arg1);
    }

    /**
     * <p>
     * sendError
     * </p>
     * 
     * @see javax.servlet.http.HttpServletResponse#sendError(int)
     * @param arg0
     * @throws java.io.IOException
     */
    public void sendError( int errorCode ) throws IOException
    {
        handleError(errorCode, null);
    }

    /**
     * <p>
     * handleError
     * </p>
     * 
     * @param errorCode
     * @throws IOException
     */
    protected void handleError( int errorCode, String message ) throws IOException
    {
        if (errorCode == SC_NOT_FOUND)
        {
            _404sent = true;
            try
            {
                locationAttempted = true;
                setContentLength((int) contentLocator.writeToOutputStream(getOutputStream()));
                setStatus(SC_OK);
            }
            catch (FileNotFoundException e)
            {
                super.sendError(SC_NOT_FOUND, e.getMessage());
            }
        }
        else
        {
            if (message != null)
            {
                super.sendError(errorCode, message);
            }
            else
            {
                super.sendError(errorCode);
            }
        }
    }

    public boolean was404sent()
    {
        return _404sent;
    }

    /**
     * @return Returns the locationAttempted.
     */
    public boolean wasLocationAttempted()
    {
        return locationAttempted;
    }
    /**
     * <p>
     * getOutputStream
     * </p>
     *
     * @see javax.servlet.ServletResponse#getOutputStream()
     * @return
     * @throws java.io.IOException
     */
    public ServletOutputStream getOutputStream() throws IOException
    {
        outputStreamCalled = true;
        if ( outputStream == null )
        {
          if ( writerCalled )
          {
            outputStream = new PrintWriterServletOutputStream(writer);
          }
          else
          {
            outputStream = super.getOutputStream();
          }
        }
        return outputStream;
    }
    /**
     * <p>
     * getWriter
     * </p>
     *
     * @see javax.servlet.ServletResponse#getWriter()
     * @return
     * @throws java.io.IOException
     */
    public PrintWriter getWriter() throws IOException
    {
        writerCalled = true;
        if ( writer == null )
        {
          if ( outputStreamCalled )
          {
            writer = new PrintWriter(outputStream);
          }
          else
          {
            writer = super.getWriter();
          }
        }
        return writer;
    }
}
