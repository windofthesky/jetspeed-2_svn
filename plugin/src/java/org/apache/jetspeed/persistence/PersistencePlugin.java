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
