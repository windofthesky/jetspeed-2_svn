/*
 * Created on Apr 23, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.apache.jetspeed.cache.file;

import java.io.File;
import java.util.Iterator;

/**
 * @author <a href="mailto:sweaver@einnovation.com">Scott T. Weaver</a>
 *
 */
public interface FileCache
{
    /**
     * Set the new refresh scan rate on managed files.
     *
     * @param scanRate the new scan rate in seconds
     */
    void setScanRate( long scanRate );

    /**
     * Get the refresh scan rate 
     *
     * @return the current refresh scan rate in seconds
     */
    long getScanRate();

    /**
     * Set the new maximum size of the cache 
     *
     * @param maxSize the maximum size of the cache
     */
    void setMaxSize( int maxSize );

    /**
     * Get the maximum size of the cache 
     *
     * @return the current maximum size of the cache
     */
    int getMaxSize();

    /**
     * Gets an entry from the cache given a key
     *
     * @param key the key to look up the entry by
     * @return the entry
     */
    FileCacheEntry get( String key );

    /**
     * Gets an entry from the cache given a key
     *
     * @param key the key to look up the entry by
     * @return the entry
     */
    Object getDocument( String key );

    /**
     * Puts a file entry in the file cache
     *
     * @param file The file to be put in the cache
     * @param document the cached document
     */
    void put( File file, Object document ) throws java.io.IOException;

    /**
     * Puts a file entry in the file cache
     *
     * @param path the full path name of the file
     * @param document the cached document
     */
    void put( String key, Object document ) throws java.io.IOException;

    /**
     * Removes a file entry from the file cache
     *
     * @param key the full path name of the file
     * @return the entry removed
     */
    Object remove( String key );

    /**
     * Add a File Cache Event Listener 
     *
     * @param listener the event listener
     */
    void addListener( FileCacheEventListener listener );

    /**
     * Start the file Scanner running at the current scan rate.
     *
     */
    void startFileScanner();

    /**
     * Stop the file Scanner 
     *
     */
    void stopFileScanner();

    /**
     * Comparator function for sorting by last accessed during eviction
     *
     */
    int compare( Object o1, Object o2 );

    /**
     * get an iterator over the cache values
     *
     * @return iterator over the cache values
     */
    Iterator getIterator();

    /**
     * get the size of the cache
     *
     * @return the size of the cache
     */
    int getSize();
}