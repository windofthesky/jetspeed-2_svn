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

package org.apache.jetspeed.cache.file;

import java.io.File;
import java.io.Serializable;
import java.util.Date;

/**
 * FileCache entry keeps the cached content along with last access information.
 *
 *  @author David S. Taylor <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 *  @version $Id$
 */

public class FileCacheEntryImpl implements FileCacheEntry, Serializable
{
    protected File file;
    protected Object document;

    protected long lastAccessed;
    protected Date lastModified;

    private FileCacheEntryImpl()
    {
    }

    /**
     * Constructs a FileCacheEntry object
     *
     * @param document The user specific content being cached
     * @param lastModified The document's last modified stamp
     */
    public FileCacheEntryImpl(File file, Object document)
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
        return new File(this.file.getAbsolutePath());
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


