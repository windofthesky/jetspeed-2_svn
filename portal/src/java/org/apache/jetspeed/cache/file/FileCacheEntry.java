/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2000-2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *    "Apache Jetspeed" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Apache Jetspeed", nor may "Apache" appear in their name, without
 *    prior written permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

package org.apache.jetspeed.cache.file;

import java.io.File;
import java.util.Date;

/**
 * FileCache entry keeps the cached content along with last access information.
 *
 *  @author David S. Taylor <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 *  @version $Id$
 */

public class FileCacheEntry
{
    protected File file;
    protected Object document;

    protected long lastAccessed;
    protected Date lastModified;

    private FileCacheEntry()
    {
    }

    /**
     * Constructs a FileCacheEntry object
     *
     * @param document The user specific content being cached
     * @param lastModified The document's last modified stamp
     */
    public FileCacheEntry(File file, Object document)
    {
        this.file = file;
        this.document = document;
        this.lastModified = new Date(file.lastModified());
        this.lastAccessed = new Date().getTime();
    }

    /**
     * Get the file descriptor
     *
     * @return the file descriptor
     */
    public File getFile()
    {
        return this.file;
    }

    /**
     * Set the file descriptor
     *
     * @param file the new file descriptor
     */
    public void setFile(File file)
    {
        this.file = file;
    }

    /**
     * Set the cache's last accessed stamp
     *
     * @param lastAccessed the cache's last access stamp
     */
    public void setLastAccessed(long lastAccessed)
    {
        this.lastAccessed = lastAccessed;
    }

    /**
     * Get the cache's lastAccessed stamp
     *
     * @return the cache's last accessed stamp
     */
    public long getLastAccessed()
    {
        return this.lastAccessed;
    }

    /**
     * Set the cache's last modified stamp
     *
     * @param lastModified the cache's last modified stamp
     */
    public void setLastModified(Date lastModified)
    {
        this.lastModified = lastModified;
    }

    /**
     * Get the entry's lastModified stamp (which may be stale compared to file's stamp)
     *
     * @return the last modified stamp
     */
    public Date getLastModified()
    {
        return this.lastModified;
    }

    /**
     * Set the Document in the cache
     *
     * @param document the document being cached
     */
    public void setDocument(Object document)
    {
        this.document = document;
    }

    /**
     * Get the Document
     *
     * @return the document being cached
     */
    public Object getDocument()
    {
        return this.document;
    }

}


