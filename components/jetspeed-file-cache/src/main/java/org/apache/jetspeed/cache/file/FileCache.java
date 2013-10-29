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

import org.apache.jetspeed.cache.CacheElement;
import org.apache.jetspeed.cache.JetspeedCache;
import org.apache.jetspeed.cache.JetspeedCacheEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 * FileCache keeps a cache of files up-to-date with a most simple eviction policy.
 * The eviction policy will keep n items in the cache, and then start evicting
 * the items ordered-by least used first. The cache runs a thread to check for
 * both evictions and refreshes.
 *
 *  @author David S. Taylor <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 *  @version $Id$
 */

public class FileCache
{
    protected long scanRate = 300;  // every 5 minutes

    private FileCacheScanner scanner = null;
    private JetspeedCache cache = null;
    private Map<FileCacheEventListener,JetspeedCacheEventListener> listeners = new Hashtable<FileCacheEventListener,JetspeedCacheEventListener>();

    private final static Logger log = LoggerFactory.getLogger(FileCache.class);

    /**
     * Set cache
     *
     * @param cache the physical cache implementation
     */
    public FileCache(JetspeedCache cache)
    {
        this.cache = cache;
        this.scanner = new FileCacheScanner();
        this.scanner.setDaemon(true);
    }

    /**
     * Set cache, scanRate and maxSize
     *
     * @param cache the physical cache implementation
     * @param scanRate how often in seconds to refresh and evict from the cache
     */
    public FileCache(JetspeedCache cache, long scanRate)
    {
        this(cache);
        this.scanRate = scanRate;
    }

    /**
     * Set the new refresh scan rate on managed files.
     *
     * @param scanRate the new scan rate in seconds
     */
    public void setScanRate(long scanRate)
    {
        this.scanRate= scanRate;
    }

    /**
     * Get the refresh scan rate 
     *
     * @return the current refresh scan rate in seconds
     */
    public long getScanRate()
    {
        return scanRate;
    }

    /**
     * Gets an entry from the cache given a key
     *
     * @param key the key to look up the entry by
     * @return the entry
     */
    public FileCacheEntry get(String key)
    {
        FileCacheEntry entry = null;
        CacheElement element = this.cache.get(key);

        if (element != null)
        {
            entry = (FileCacheEntry) element.getContent();
        }

        return entry;
    }

    /**
     * Gets an entry from the cache given a key
     *
     * @param key the key to look up the entry by
     * @return the entry
     */
    public Object getDocument(String key)
    {
        FileCacheEntry entry = get(key);

        if (entry != null)
        {
            return entry.getDocument();
        }

        return null;
    }

    /**
     * Puts a file entry in the file cache
     *
     * @param file The file to be put in the cache
     * @param document the cached document
     */
    public void put(File file, Object document)
            throws java.io.IOException
    {
        if(!file.exists())
        {
            throw new FileNotFoundException("File to cache: "+file.getAbsolutePath()+" does not exist.");
        }

        FileCacheEntry entry = new FileCacheEntryImpl(file, document);
        CacheElement element = this.cache.createElement(file.getCanonicalPath(), entry);
        cache.put(element);
    }

    /**
     * Puts a file entry in the file cache
     *
     * @param key the full path name of the file
     * @param document the cached document
     * @param rootFile the root file handle
     */
    public void put(String key, Object document, File rootFile)
            throws java.io.IOException
    {
        File file = new File(rootFile, key);

        if(!file.exists())
        {
            throw new FileNotFoundException("File to cache: "+file.getAbsolutePath()+" does not exist.");
        }

        FileCacheEntry entry = new FileCacheEntryImpl(file, document);
        CacheElement element = this.cache.createElement(key, entry);
        this.cache.put(element);
    }

    /**
     * Removes a file entry from the file cache
     *
     * @param key the full path name of the file
     * @return the entry removed
     */
    public Object remove(String key)
    {
        boolean removed = this.cache.remove(key);
        return null;
    }


    /**
     * Add a File Cache Event Listener 
     *
     * @param listener the event listener
     */
    public void addListener(final FileCacheEventListener listener)
    {
        JetspeedCacheEventListener cacheEventListener = new JetspeedCacheEventListener()
        {
            public void notifyElementRemoved(JetspeedCache cache, boolean local, Object key, Object element)
            {
            }

            public void notifyElementAdded(JetspeedCache cache, boolean local, Object key, Object element)
            {
            }

            public void notifyElementChanged(JetspeedCache cache, boolean local, Object key, Object element)
            {
                try 
                {
                    listener.refresh((FileCacheEntry) element);
                } 
                catch (Exception e) 
                {
                    e.printStackTrace();
                }
            }

            public void notifyElementEvicted(JetspeedCache cache, boolean local, Object key, Object element)
            {
                try 
                {
                    listener.evict((FileCacheEntry) element);
                } 
                catch (Exception e) 
                {
                }
            }

            public void notifyElementExpired(JetspeedCache cache, boolean local, Object key, Object element)
            {
            }
        };
        listeners.put(listener, cacheEventListener);
        this.cache.addEventListener(cacheEventListener, true);
    }

    /**
     * Remove a File Cache Event Listener 
     *
     * @param listener the event listener
     */
    public void removeListener(final FileCacheEventListener listener)
    {
        JetspeedCacheEventListener cacheEventListener = listeners.remove(listener);
        if (cacheEventListener != null)
        {
            this.cache.removeEventListener(cacheEventListener, true);
        }
    }

    /**
     * Start the file Scanner running at the current scan rate.
     *
     */
    public void startFileScanner()
    {
        try
        {
            this.scanner.start();
        }
        catch (java.lang.IllegalThreadStateException e)
        {
            log.error("Exception starting scanner", e);
        }
    }

    /**
     * Stop the file Scanner 
     *
     */
    public void stopFileScanner()
    {
        this.scanner.setStopping(true);
    }

    /**
     * Evicts all entries
     *
     */
    public void evictAll()
    {
        this.cache.clear();
    }

    /**
     * inner class that runs as a thread to scan the cache for updates or evictions
     *
     */
    protected class FileCacheScanner extends Thread
    {
        private boolean stopping = false;

        public void setStopping(boolean flag)
        {
            // stop this scanning thread
            synchronized (this)
            {
                if (!stopping && flag)
                {
                    stopping = flag;
                    notifyAll();
                }
                else
                {
                    flag = false;
                }
            }
            // wait for scanning thread to stop
            if (flag)
            {
                try
                {
                    join(FileCache.this.getScanRate() * 1000);
                }
                catch (InterruptedException ie)
                {
                }
            }
        }

        /**
         * Run the file scanner thread
         *
         */
        public synchronized void run()
        {
            boolean done = false;

            try
            {
                while(!done)
                {
                    try
                    {
                        for (Object key : getKeys())
                        {
                            CacheElement element = cache.get(key);
                            
                            if (element != null)
                            {
                                FileCacheEntry entry = (FileCacheEntry) element.getContent();
                                File file = entry.getFile();
                                Date modified = new Date(file.lastModified());
                                
                                if (modified.after(entry.getLastModified()))
                                {
                                    FileCacheEntry updatedEntry = new FileCacheEntryImpl(file, entry.getDocument());
                                    CacheElement updatedElement = cache.createElement(key, updatedEntry);
                                    cache.put(updatedElement);
                                    
                                    if (log.isDebugEnabled())
                                    {
                                        log.debug("page file has been updated: " + key);
                                    }
                                }
                            }
                        }
                    }
                    catch (Exception e)
                    {
                        log.error("FileCache Scanner: Error in iteration...", e);
                    }
                    
                    wait(FileCache.this.getScanRate() * 1000);                

                    if (this.stopping)
                    {
                        this.stopping = false;
                        done = true;
                    }
                }
            }
            catch (InterruptedException e)
            {
                log.error("FileCacheScanner: recieved interruption, exiting.", e);
            }
        }
    } // end inner class:  FileCacheScanner


    /**
     * get an iterator over the cache values
     *
     * @return iterator over the cache values
     */
    public List getKeys()
    {
        return cache.getKeys();
    }

    /**
     * get the size of the cache
     *
     * @return the size of the cache
     */
    public int getSize()
    {
        return cache.getSize();
    }
}
