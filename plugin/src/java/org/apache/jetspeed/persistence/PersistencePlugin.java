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

    /**
     * <p>
     * Starts a transaction to which objects being persisted through this plugin
     * can be added to using the <code>addObjectToTransaction()</code> method.
     * <br/>
     * You should not perform operations directly on the returned transaction, instead
     * use the methods supplied through the <code>PersistencePlugin</code>
     * interface.
     * </p>
     * <p>
     * <strong>NOTE:<strong> Transaction support is not a requirement to creating
     * a PersistencePlugin.  However, since almost all apis that support transactions
     * require that objects be modified within a transaction for there changes to be persisted
     * (requirements of both ODMG and JDO).  So it is a good idea to perform these
     * operations within a transaction regardless of whether or not transactions are supported
     * by this plugin.  Plugins not providing transactions should return a <code>NullTransaction</code>.  
     * object.  In addition they should also handle the <code>NullTransaction</code> object
     * gracefully within any plugin methods that deal directly with transaction i.e. ignore it
     * entirely with no exceptions being thrown.
     * </p>
     * 
     * @return Object reflecting a transaction object specific to this plugin.
     */
    Object startTransaction();

    /**
     * Adds an <code>object</code> to the <code>transaction</code>
     * started by this plugin's <code>startTransaction()</code> method.
     * @param object Object to be added to the transaction
     * @param transaction Transaction object started by this plugin's startTransaction method
     */
    void addObjectToTransaction(Object object, Object transaction, int lockLevel);

    /**
     * Sets the <code>object</code> for deletion within the current
     * <code>treansaction</code>.  Upon invoking the <code>commitTransaction()</code>
     * method, all objects scheduled for deletion are removed from persisteance.   
     * Child objects and/or collections maybe removed/deleted
     * if the underlying api provides support for cascading deletes.
     * @param object Object to be scheduled for deletion 
     * @param transaction Current transaction.
     */
    void setObjectForDeletion(Object object, Object transaction);

    /**
     * Aborts an existing transaction and all changes should be rolled back by the 
     * underlying api, if transaction are supported.
     * 
     * @param transaction Transaction to abort.
     */
    void abortTransaction(Object transaction);

    /**
     * Commits all changes made to objects that were added to the 
     * current transaction via the <code>addObjectToTransaction()</code>
     * method and deletes all objects that have been scheduled for deletion
     * via the <code>setObjectForDeletion()</code> method.
     * @param transaction Transaction to be commited.
     */
    void commitTransaction(Object transaction);

    /**
     * Convenience method that updates a persistent object.  If this PersistencePlugin supports
     * tranasctions, it should create an atomic transaction for updating this
     * object and commit it.  This transaction will ONLY include this object
     * and possibly child object and/or collections, if the underlying api or
     * persistence mechanism supports it.
     * <br/>
     * <strong>NOTE:</strong>There is no need to perform this within a 
     * transaction and may cause unexpected object locl contention if it is
     * used within a transaction.
     * 
     * @param object persistent object to be updated
     */
    void update(Object object);

    /**
     * Convenience method that makes a previously non-persistent object persistent.  
     * You should follow the same guidelines set 
     * forth by the <code>update()</code> method in regards to transactions.
     * @param object Object to be persisted.
     */
    void add(Object object);

    /**
     * Convenience method that removes a persistent object.  This operation
     * should be considered atomic as both <code>add()</code> and <code>update()</code>
     * with regards to transactions.  Child objects and/or collections maybe removed
     * if the underlying api provides support for cascading deletes.
     * 
     * @param object Object that will be removed from persistence.
     */
    void delete(Object object);

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
