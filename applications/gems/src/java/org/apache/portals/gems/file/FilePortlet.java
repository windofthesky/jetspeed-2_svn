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
package org.apache.portals.gems.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.PortletPreferences;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.jetspeed.PortalReservedParameters;
import org.apache.portals.bridges.common.GenericServletPortlet;


/**
 * FilePortlet
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class FilePortlet extends GenericServletPortlet
{
    /**
     * Name of portlet preference for source file url
     */
    public static final String PARAM_SOURCE_FILE = "file";

    /**
     * Name of portlet preference for source file url
     */
    public static final String PARAM_SOURCE_BASE_PATH = "basepath";

    /**
     * Name of portlet preference for source file url
     */
    public static final String PARAM_SOURCE_FILE_PATH = "filepath";

    /**
     * Is the file stored in the webapp or outside of the webapp?
     * valid values "webapp" and "filesystem", defaults to webapp
     */
    public static final String PARAM_LOCATION = "location";
    private boolean webappLocation = true;
    
    /**
     * Default URL for the source file
     */
    private String defaultSourceFile = null;

    /**
     * Default base URL for the source file
     */
    private String defaultSourceBasePath = null;

    public void init(PortletConfig config)
        throws PortletException
    {
        super.init(config);
        this.defaultSourceFile = config.getInitParameter(PARAM_SOURCE_FILE);
        this.defaultSourceBasePath = config.getInitParameter(PARAM_SOURCE_BASE_PATH);
        String location = config.getInitParameter(PARAM_LOCATION);
        if (location != null && location.equals("filesystem"))
            webappLocation = false;
        else
            webappLocation = true;
    }


    public void doView(RenderRequest request, RenderResponse response)
    throws PortletException, IOException
    {
        // NOTE: this is Jetspeed specific
        String path = (String)request.getAttribute(PortalReservedParameters.PATH_ATTRIBUTE);
        if (null == path)
        {
            PortletPreferences prefs = request.getPreferences();
            path = prefs.getValue(PARAM_SOURCE_FILE, this.defaultSourceFile);
        }
        
        if (null == path && this.defaultSourceBasePath != null )
        {
            String filepath = (String)request.getParameter(PARAM_SOURCE_FILE_PATH);
            if (filepath == null)
            {
                filepath = (String)request.getAttribute(PARAM_SOURCE_FILE_PATH);
            }

            if (filepath != null)
            {
                path = ( ( this.defaultSourceBasePath.length() > 0 ) ? ( this.defaultSourceBasePath + "/" ) : "" ) + filepath;
            }
        }

        if (null == path)
        {
            response.setContentType("text/html");
            response.getWriter().println("Could not find source document.");            
        }
        else
        {
            // default to 'content' area
            File temp = new File(path);
            if (webappLocation)
            {
                path = "/WEB-INF/" + temp.getPath();            
            }
            setContentType(path, response);        
            renderFile(response, path);
        }        
    }

    protected void setContentType(String path, RenderResponse response)
    {
        // Note these content types need to be added to the portlet.xml
        if (path.endsWith(".html"))
        {
            response.setContentType("text/html");
        }
        else if (path.endsWith(".pdf"))
        {
            response.setContentType("application/pdf");
        }
        else if (path.endsWith(".zip"))
        {
            response.setContentType("application/zip");
        }
        else if (path.endsWith(".csv"))
        {
            response.setContentType("text/csv");
        }
        else if (path.endsWith(".xml") || path.endsWith(".xsl"))
        {
            response.setContentType("text/xml");
        }
        else
        {
            response.setContentType("text/html");
        }
    }
    
    protected void renderFile(RenderResponse response, String fileName)
    throws PortletException, IOException
    {
        InputStream is = null;
        
        if (this.webappLocation)
        {
            is = this.getPortletContext().getResourceAsStream(fileName);
        }
        else
        {
            is = new FileInputStream(fileName);
        }
        if (is == null)
        {
            byte [] bytes = ("File " + fileName + " not found.").getBytes();
            response.getPortletOutputStream().write(bytes);
            return;
        }
        drain(is, response.getPortletOutputStream());
        response.getPortletOutputStream().flush();
        is.close();        
    }
    
    
    static final int BLOCK_SIZE=4096;

    public static void drain(InputStream r,OutputStream w) throws IOException
    {
        byte[] bytes = new byte[BLOCK_SIZE];
        try
        {
            int length = r.read(bytes);
            while (length != -1)
            {
                if (length != 0)
                {
                    w.write(bytes, 0, length);
                }
                length = r.read(bytes);
            }
        }
        finally
        {
            bytes = null;
        }
    }
   
    
}