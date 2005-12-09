/*
 * Created on Aug 10, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.apache.jetspeed.contentserver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 * <p>
 * ContentLocatingRequestWrapper
 * </p>
 * <p>
 *
 * </p>
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 *
 */
public class ContentLocatingRequestWrapper extends HttpServletRequestWrapper
{

    private ContentLocator contentLocator;
    private HttpServletRequest request;
    private boolean readerCalled, isCalled = false;
    
    

    public ContentLocatingRequestWrapper( HttpServletRequest request, ContentLocator contentLocator )
    {
        super(request);
        this.contentLocator = contentLocator;
        this.request = request;
        String basePath = contentLocator.getBasePath();
        this.request.setAttribute("javax.servlet.include.servlet_path", basePath);
        this.request.setAttribute("javax.servlet.include.request_uri", basePath);
    }
    
    /**
     * <p>
     * getRequestURI
     * </p>
     *
     * @see javax.servlet.http.HttpServletRequest#getRequestURI()
     * @return
     */
    public String getRequestURI()
    {
        String basePath = contentLocator.getBasePath();
        return basePath;
    }
    /**
     * <p>
     * getRequestURL
     * </p>
     *
     * @see javax.servlet.http.HttpServletRequest#getRequestURL()
     * @return
     */
    public StringBuffer getRequestURL()
    {
        StringBuffer buf = new StringBuffer(super.getRequestURL().length());
        buf.append(request.getScheme())
        .append("://")
        .append(request.getServerName())
        .append(":")
        .append(request.getServerPort())
        .append(request.getContextPath())
        .append(contentLocator.getBasePath());
        return buf;
    }   
    
    /**
     * <p>
     * getServletPath
     * </p>
     *
     * @see javax.servlet.http.HttpServletRequest#getServletPath()
     * @return
     */
    public String getServletPath()
    {   
        return contentLocator.getBasePath();
    }
    /**
     * <p>
     * getContextPath
     * </p>
     *
     * @see javax.servlet.http.HttpServletRequest#getContextPath()
     * @return
     */
    public String getContextPath()
    {
        // TODO Auto-generated method stub
        String cPath = super.getContextPath();
        return cPath;
    }
    /**
     * <p>
     * getPathInfo
     * </p>
     *
     * @see javax.servlet.http.HttpServletRequest#getPathInfo()
     * @return
     */
    public String getPathInfo()
    {
        // TODO Auto-generated method stub
        String pathInfo = super.getPathInfo();
        return pathInfo;
    }
    /**
     * <p>
     * getPathTranslated
     * </p>
     *
     * @see javax.servlet.http.HttpServletRequest#getPathTranslated()
     * @return
     */
    public String getPathTranslated()
    {
        // TODO Auto-generated method stub
        String pathTranslated = super.getPathTranslated();
        return pathTranslated;
    }
}
