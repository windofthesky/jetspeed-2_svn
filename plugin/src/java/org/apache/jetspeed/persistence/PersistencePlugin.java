/*
 * Copyright 2000-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.jetspeed.persistence;

import java.sql.Connection;
import java.util.Collection;

import org.apache.jetspeed.services.plugin.Plugin;
import org.apache.jetspeed.services.plugin.PluginConfiguration;


/**
 * 
 * PersistencePlugin
 * <p>
 * This interface is implemented by any class wishing to provide
 * persistence operations as part of the PersistenceService mechanism.
 * </p>
 * <p>
 * PersistencePlugin implementations classes are required to be threadsafe
 * as there will only be a single instance that could possibly be used across
 * multiple threads.
 * <br />
 * PersistencePlugin implementors must supply a public default constructor.
 * </p>
 *  
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 *
 */
public interface PersistencePlugin extends Plugin
{

    /** Constant that specifies a transaction lock level of write */
    int LOCK_LEVEL_WRITE = 0;

    /** Constant that specifies a transaction lock level of read */
    int LOCK_LEVEL_READ = 1;
    
    void beginTransaction() throws TransactionStateException;
    
    void commitTransaction() throws TransactionStateException;
    
    void prepareForDelete(Object obj) throws TransactionStateException;
    
    void prepareForUpdate(Object obj) throws TransactionStateException;
    
	void makePersistent(Object obj) throws TransactionStateException;
    
    void rollbackTransaction() throws TransactionStateException;
    
    /**
     * Takes an object that was modified outside of the current transaction
     * and makes it so that it appears to have been modified within the 
     * current transaction.
     * @param obj Object modified outside of a transaction
     * @return An object the same type, whose object graph match those
     * of the <code>obj</code> argument but is consistent within the
     * current transaction.
     * @throws TransactionStateException
     */
    Object markDirty(Object obj) throws TransactionStateException;
    
    /**
     * Deletes all objects matching the query from the underlying persistence system.
     * 
     * @param query Query object used to identify the object to be removed from persistence.
     */
    void deleteByQuery(Object query);

    Collection getCollectionByQuery(Class clazz, Object query);
    
    Collection getExtent(Class clazz);

    Object getObjectByQuery(Class clazz, Object query);

    /**
     * Generates a query object based on the <code>criteria</code>
     * object provided that is compatible with this plugin's underlying api. 
     * @param criteria SimpleCriteria used to create the query
     * @return Object query compatible with the underlying api of this plugin
     */
    Object generateQuery(Class clazz, LookupCriteria criteria);

    /**
     * Creates a new instance of a <code>LookupCriteria</code>
     * compatible with this plugin.
     * @return LookupCriteria that is compatible with this plug in.
     */
    LookupCriteria newLookupCriteria();

    PluginConfiguration getConfiguration();

    void setDbAlias(String dbAlias) throws UnsupportedOperationException;

    String getDbAlias() throws UnsupportedOperationException;

    Connection getSqlConnection();

    void releaseSqlConnection(Connection sqlConnection);


    /**
     * Invalidates the object in the object cache, effectively forcing the object
     * to be refreshed from the database.
     * 
     * @param object The object to be invalidated from the cache
     */
    void invalidateObject(Object object);
    
    /**
     * 
     * <p>
     * clearCache
     * </p>
     * 
     * Clears out any cahcing mechanisms completely
     *
     */
    void clearCache();
    
}
