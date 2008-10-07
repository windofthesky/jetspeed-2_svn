/*
 * Created on Apr 23, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.apache.jetspeed.cache.file;

import java.io.File;
import java.util.Date;

/**
 * @author <a href="mailto:sweaver@einnovation.com">Scott T. Weaver</a>
 *
 */
public interface FileCacheEntry
{
    /**
     * Get the file descriptor
     *
     * @return the file descriptor
     */
    File getFile();

    /**
     * Set the file descriptor
     *
     * @param file the new file descriptor
     */
    void setFile( File file );

    /**
     * Set the cache's last accessed stamp
     *
     * @param lastAccessed the cache's last access stamp
     */
    void setLastAccessed( long lastAccessed );

    /**
     * Get the cache's lastAccessed stamp
     *
     * @return the cache's last accessed stamp
     */
    long getLastAccessed();

    /**
     * Set the cache's last modified stamp
     *
     * @param lastModified the cache's last modified stamp
     */
    void setLastModified( Date lastModified );

    /**
     * Get the entry's lastModified stamp (which may be stale compared to file's stamp)
     *
     * @return the last modified stamp
     */
    Date getLastModified();

    /**
     * Set the Document in the cache
     *
     * @param document the document being cached
     */
    void setDocument( Object document );

    /**
     * Get the Document
     *
     * @return the document being cached
     */
    Object getDocument();
}