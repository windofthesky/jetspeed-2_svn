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
package org.apache.jetspeed.contentserver;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <p>
 * ContentFilter
 * </p>
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver </a>
 * @version $Id$
 *  
 */
public class ContentFilter implements Filter
{
    public static final String SESSION_CONTENT_PATH_ATTR = "org.apache.jetspeed.content.pathes";

    private FilterConfig config;

    private String contentDir;

    // private String themesDir;
    private File contentDirFile;

    private static final Log log = LogFactory.getLog(ContentFilter.class);

    private String urlHint;

    protected String[] urlHints;

    protected boolean useCache;

    /**
     * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
     */
    public void init( FilterConfig config ) throws ServletException
    {
        this.config = config;
        String dir = config.getInitParameter("content.directory");
        urlHint = config.getInitParameter("url.hint");
        this.contentDir = config.getServletContext().getRealPath(dir);
        // this.themesDir = this.contentDir + "/themes";
        this.contentDirFile = new File(this.contentDir);
        
        StringTokenizer hintTokenizer = new StringTokenizer(urlHint, ",");
        urlHints = new String[hintTokenizer.countTokens()];
        int i = 0;
        while (hintTokenizer.hasMoreTokens())
        {
            urlHints[i] = hintTokenizer.nextToken();
            i++;
        }
        
        String useCacheParam = config.getInitParameter("use.caching");
        if(useCacheParam == null)
        {
            useCache = true;
        }
        else
        {
            useCache = Boolean.valueOf(useCacheParam).booleanValue();
        }
        
        if (!contentDirFile.exists())
        {
            throw new ServletException("The specified content directory " + contentDirFile.getAbsolutePath()
                    + " does not exist!");
        }
    }

    /**
     * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest,
     *      javax.servlet.ServletResponse, javax.servlet.FilterChain)
     */
    public void doFilter( ServletRequest request, ServletResponse response, FilterChain chain ) throws IOException,
            ServletException
    {
        if (request instanceof HttpServletRequest)
        {
            HttpServletRequest httpRequest = (HttpServletRequest) request;
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            String requestURI = httpRequest.getRequestURI();
            
            SimpleContentLocator contentLocator = new SimpleContentLocator(this.contentDir, urlHints, useCache, httpRequest
                    .getContextPath(), requestURI, getContentSearchPathes(httpRequest));

            ContentLocatingResponseWrapper respWrapper = new ContentLocatingResponseWrapper(httpResponse,
                    contentLocator);
            
            ContentLocatingRequestWrapper reqWrapper = new ContentLocatingRequestWrapper(httpRequest,
                    contentLocator);
            httpRequest.setAttribute("org.apache.jetspeed.content.filtered", "true");
            chain.doFilter(reqWrapper, respWrapper);
            if(!respWrapper.wasLocationAttempted() && !respWrapper.outputStreamCalled && !respWrapper.writerCalled)
            {
                try
                {                   
                    httpResponse.setContentLength((int) contentLocator.writeToOutputStream(httpResponse.getOutputStream()));
                    httpResponse.setStatus(HttpServletResponse.SC_OK);
                }
                catch (FileNotFoundException e)
                {
                    httpResponse.sendError(HttpServletResponse.SC_NOT_FOUND, e.getMessage());
                }
               
                
            }

        }
        else
        {
            chain.doFilter(request, response);
        }

    }

    /**
     * @see javax.servlet.Filter#destroy()
     */
    public void destroy()
    {
    }

    protected List getContentSearchPathes( HttpServletRequest request )
    {
        List contentPathes = (List) request.getSession().getAttribute(SESSION_CONTENT_PATH_ATTR);
        if (contentPathes == null)
        {
            contentPathes = new ArrayList();
            //request.getSession()
            //        .setAttribute(SESSION_THEME_ATTR, contentPathes);
        }
        return contentPathes;
    }
}