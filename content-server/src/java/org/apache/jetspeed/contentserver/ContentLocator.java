/*
 * Created on Mar 12, 2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.apache.jetspeed.contentserver;

import java.io.OutputStream;


/**
 * <p>
 * ContentLocator
 * </p>
 *
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $ $
 *
 */
public interface ContentLocator
{
    /**
     * 
     * <p>
     *  mergeContent
     * </p>
     * <p>
     *  Merges the content that is located in the provided <code>URI</code>     * 
     * </p>
     * @param URI Content to locate
     * @param os OutputStream to write the content to.
     * @return int the length of actual content in bytes or -1
     * if the <code>URI</code> was not found.
     */
    long mergeContent(String URI, OutputStream os);
}
