package org.apache.jetspeed.contentserver;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
 * Created on Dec 30, 2003
 * 
 * @author
 */

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



    /**
     * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
     */
    public void init(FilterConfig config) throws ServletException
    {
        this.config = config;        
        String dir = config.getInitParameter("content.directory");
        urlHint = config.getInitParameter("url.hint");
        this.contentDir = config.getServletContext().getRealPath(dir);
        // this.themesDir = this.contentDir + "/themes";
        this.contentDirFile = new File(this.contentDir);
        if (!contentDirFile.exists()) { throw new ServletException(
                "The specified content directory "
                        + contentDirFile.getAbsolutePath() + " does not exist!"); }

    }

    /**
     * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest,
     *      javax.servlet.ServletResponse, javax.servlet.FilterChain)
     */
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain) throws IOException, ServletException
    {
        if (request instanceof HttpServletRequest)
        {
            
            HttpServletRequest httpRequest = (HttpServletRequest) request;
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            String requestURI = httpRequest.getRequestURI();            
            String mimeType = config.getServletContext()
            .getMimeType(requestURI);
            
            if (mimeType == null) { throw new NullPointerException(
                    "MIME-TYPE for "
                    + requestURI
                    + " could not be located.  Make sure your container is properly configured to detect MIME types."); }
            
            log.debug(mimeType + " detected: " + requestURI);
            
            SimpleContentLocator contentLocator = new SimpleContentLocator(this.contentDir, urlHint, true);
            long contentLength = contentLocator.mergeContent(requestURI, getContentSearchPathes(httpRequest), response.getOutputStream());
            
            if (contentLength > -1)
            {
                response.setContentType(mimeType);
                response.setContentLength((int) contentLength);
                log.debug("Setting status to OK");
                httpResponse.setStatus(HttpServletResponse.SC_OK);
            } else
            {
                chain.doFilter(request, response);
            }
            
            return;
            
        }
        

        chain.doFilter(request, response);
    }

    /**
     * @see javax.servlet.Filter#destroy()
     */
    public void destroy()
    {

    }


    protected List getContentSearchPathes(HttpServletRequest request)
    {
        List contentPathes = (List) request.getSession().getAttribute(
                SESSION_CONTENT_PATH_ATTR);
        if (contentPathes == null)
        {
            contentPathes = new ArrayList();            
            //request.getSession()
            //        .setAttribute(SESSION_THEME_ATTR, contentPathes);            
        }

        return contentPathes;
    }

}
