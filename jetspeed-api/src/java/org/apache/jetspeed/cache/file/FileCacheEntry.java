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
import java.util.Date;

/**
 * @author <a href="weaver@apache.org">Scott T. Weaver</a>
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