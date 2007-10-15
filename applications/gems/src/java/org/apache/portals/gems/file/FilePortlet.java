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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.jetspeed.PortalReservedParameters;
import org.apache.jetspeed.om.folder.Folder;
import org.apache.jetspeed.request.RequestContext;
import org.apache.portals.bridges.common.GenericServletPortlet;

/**
 * FilePortlet
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class FilePortlet extends GenericServletPortlet
{
    public static final String PARAM_USE_LANGUAGE = "use-language";
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
     * Is the file stored in the webapp or outside of the webapp? valid values
     * "webapp" and "filesystem", defaults to webapp
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

    private boolean useLanguage = false;
    
    public void init(PortletConfig config) throws PortletException
    {
        super.init(config);
        String use = config.getInitParameter(PARAM_USE_LANGUAGE);
        if (use != null && use.equalsIgnoreCase("true"))
            this.useLanguage = true;
        this.defaultSourceFile = config.getInitParameter(PARAM_SOURCE_FILE);
        this.defaultSourceBasePath = config
                .getInitParameter(PARAM_SOURCE_BASE_PATH);
        String location = config.getInitParameter(PARAM_LOCATION);
        if (location != null && location.equals("filesystem"))
            webappLocation = false;
        else
            webappLocation = true;
    }

    private RequestContext getRequestContext(PortletRequest request)
    {
        return (RequestContext) request
                .getAttribute(PortalReservedParameters.REQUEST_CONTEXT_ATTRIBUTE);

    }

    private HttpServletRequest getHttpServletRequest(PortletRequest pRequest)
    {
        return getRequestContext(pRequest).getRequest();

    }

    public void doView(RenderRequest request, RenderResponse response)
            throws PortletException, IOException
    {
        // NOTE: this is Jetspeed specific
        HttpServletRequest req = getHttpServletRequest(request);
        String fileName = (String) req.getSession().getAttribute("file");
        if (fileName != null && !fileName.equals(""))
        {
            InputStream is = null;
            try
            {
                fileName = getFilePath(fileName);
                is = new FileInputStream(fileName);
                if (is == null)
                {
                    byte[] bytes = ("File " + fileName + " not found.")
                            .getBytes();
                    response.getPortletOutputStream().write(bytes);
                    return;
                }
                setContentType(fileName, response);
                drain(is, response.getPortletOutputStream());
                response.getPortletOutputStream().flush();
                is.close();
                req.getSession().removeAttribute("file");
            } catch (Exception e)
            {
                if (is != null) is.close();
                byte[] bytes = ("File " + fileName + " not found.").getBytes();
                req.getSession().removeAttribute("file");
                response.setContentType("text/html");
                response.getPortletOutputStream().write(bytes);
                return;
            }
        } 
        else
        {
            String path = (String) request.getAttribute(PortalReservedParameters.PATH_ATTRIBUTE);
            if (null == path)
            {
                PortletPreferences prefs = request.getPreferences();
                path = prefs.getValue(PARAM_SOURCE_FILE, this.defaultSourceFile);
            }

            if (null == path && this.defaultSourceBasePath != null)
            {
                String filepath = request.getParameter(PARAM_SOURCE_FILE_PATH);
                if (filepath == null)
                {
                    filepath = (String) request
                            .getAttribute(PARAM_SOURCE_FILE_PATH);
                }

                if (filepath != null)
                {
                    path = ((this.defaultSourceBasePath.length() > 0) ? (this.defaultSourceBasePath + "/")
                            : "")
                            + filepath;
                }
            }

            if (null == path)
            {
                response.setContentType("text/html");
                response.getWriter().println("Could not find source document.");
            } 
            else
            {
                setContentType(path, response);                
                List paths = fallback(path, request.getLocale().getLanguage());
                renderFile(response, paths);
            }
        }
    }

    protected List fallback(String path, String language)
    {
        List paths = new LinkedList();
        if (this.useLanguage)
        {
            if (webappLocation)
            {
                path = concatenatePaths("/WEB-INF/", path);                
            }            
            String fallbackPath = path;
            File temp = new File(path);
            String parentPath = temp.getParent();
            String name = temp.getName();
            path = concatenatePaths(parentPath, language);
            path = concatenatePaths(path, name);
            paths.add(path);
            paths.add(fallbackPath);
        }
        else
        {
            if (webappLocation)
            {
                path = concatenatePaths("/WEB-INF/", path);                
            }                        
            paths.add(path);
        }
        return paths;
    }
    
    protected void setContentType(String path, RenderResponse response)
    {
        // Note these content types need to be added to the portlet.xml
        if (path.endsWith(".html"))
        {
            response.setContentType("text/html");
        } else if (path.endsWith(".pdf"))
        {
            response.setContentType("application/pdf");
        } else if (path.endsWith(".zip"))
        {
            response.setContentType("application/zip");
        } else if (path.endsWith(".csv"))
        {
            response.setContentType("text/csv");
        } else if (path.endsWith(".xml") || path.endsWith(".xsl"))
        {
            response.setContentType("text/xml");
        } else if (path.endsWith(".psml") || path.endsWith(".link"))
        {
            response.setContentType("text/xml");
        } else
        {
            response.setContentType("text/html");
        }
    }

    protected void renderFile(RenderResponse response, List paths)
            throws PortletException, IOException
    {
        boolean drained = false;
        Iterator it = paths.iterator();
        while (it.hasNext())
        {
            String fileName = (String)it.next();
            InputStream is = null;
            try
            {
                if (this.webappLocation)
                {
                    is = this.getPortletContext().getResourceAsStream(fileName);
                } else
                {
                    is = new FileInputStream(fileName);
                }
                if (is != null)
                {
                    drain(is, response.getPortletOutputStream());
                    response.getPortletOutputStream().flush();
                    is.close();
                    drained = true;
                    break;
                }
            }
            catch (Exception e)
            {
                // do nothing, find next file
            }
        }
        if (!drained)
        {
            String fileName = (String)paths.get(0);
            byte[] bytes = ("File " + fileName + " not found.").getBytes();
            response.getPortletOutputStream().write(bytes);
            return;            
        }
    }

    static final int BLOCK_SIZE = 4096;

    public static void drain(InputStream r, OutputStream w) throws IOException
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
        } finally
        {
            bytes = null;
        }
    }

    private String getFilePath(String path)
    {
        String pageRoot = System.getProperty("java.io.tmpdir");
        String sep = System.getProperty("file.separator");
        if (sep == null || sep.equals("")) sep = "/";

        String ar[] = path.split("_");
        if (ar.length == 1) return pageRoot + sep + path;
        return pageRoot + sep + ar[0] + sep + ar[1];
    }
    
    protected static String concatenatePaths(String base, String path)
    {
        String result = "";
        if (base == null)
        {
            if (path == null)
            {
                return result;
            }
            return path;
        }
        else
        {
            if (path == null)
            {
                return base;
            }
        }
        if (base.endsWith(Folder.PATH_SEPARATOR)) 
        {
            if (path.startsWith(Folder.PATH_SEPARATOR))
            {
                result = base.concat(path.substring(1));
                return result;
            }
        
        }
        else
        {
            if (!path.startsWith(Folder.PATH_SEPARATOR)) 
            {
                result = base.concat(Folder.PATH_SEPARATOR).concat(path);
                return result;
            }
        }
        return base.concat(path);
    }
    
}