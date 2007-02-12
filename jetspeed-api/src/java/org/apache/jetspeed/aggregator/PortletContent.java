/*
 * Created on Jan 11, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.apache.jetspeed.aggregator;

import java.io.PrintWriter;

/**
 * <p>
 * PortletContent
 * </p>
 * <p>
 *
 * </p>
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @author <a href="mailto:taylor@apache.org">David S. Taylor</a>  
 * @version $Id$
 *
 */
public interface PortletContent
{
    /**
     * Retrieve the actual content of a portlet as a string
     * 
     * @return
     */
    String getContent();
    
    /** 
     * Has the renderer completed rendering the content?
     * 
     * @return
     */
    boolean isComplete();
    
    /**
     * Notify that this content is completed.
     *
     */
    void complete();
    
    /**
     * Get a writer to the content to stream content into this object
     * @return
     */
    PrintWriter getWriter();
    
    /**
     * Get the expiration setting for this content if it is cached.
     * @return
     */
    int getExpiration();
    
    /**
     * Get the cache key used to cache this content 
     * @return
     */
    String getCacheKey();
    
    /**
     * Get the title of the portlet, used during caching
     * 
     * @return
     */
    String getTitle();
    
    /**
     * Set the title of this portlet, used during caching
     * @param title
     */
    void setTitle(String title);
        
}