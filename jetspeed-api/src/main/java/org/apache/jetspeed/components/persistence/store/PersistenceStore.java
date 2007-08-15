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
package org.apache.jetspeed.components.persistence.store;

import java.util.Collection;
import java.util.Iterator;

import org.apache.jetspeed.components.persistence.store.LockFailedException;

/**
 * <p>
 * PersistenceStore
 * </p>
 * 
 * <p>
 * The persistence store allows access to the persistent
 * storage mechanism within the application.
 * <br/>
 * PersistenceStore instances <strong>ARE NOT</strong> 
 * thread safe.  The best practices approach for using
 * the persistence store is to use 
 * <code>PersistenceStoreContainer.getStoreForThread()</code>
 * any time the store is to be accessed.
 *  
 * </p>
 * 
 * @
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $ $
 *
 */
public interface PersistenceStore
{
    /** 
     * No lock at all aka the object is read only changes WILL NOT be persisted
     * at checkPoints or commits.
     */
    int LOCK_NO_LOCK = 0;

    /** 
     * changes to the object will be written to the database, in this case the lock 
     * will be automatically upgraded to the write lock on transaction
     */
    int LOCK_READ = 1;

    /**
     * changes to the object will be written to the database. 
     */
    int LOCK_PESSIMISTIC = 2;
    
	void addEventListener(PersistenceStoreEventListener listener);
    
    void close();

    void deletePersistent(Object obj) throws LockFailedException;
    
	void deleteAll(Object query) throws LockFailedException;

    Collection getCollectionByQuery(Object query);

    Collection getCollectionByQuery(Object query, int lockLevel);
    
	Object getObjectByQuery(Object query);

	Object getObjectByQuery(Object query, int lockLevel);
	
	Object getObjectByIdentity(Object object) throws LockFailedException;

	Object getObjectByIdentity(Object object, int lockLevel) throws LockFailedException;

    int getCount(Object query);

    Iterator getIteratorByQuery(Object query) ;

    Iterator getIteratorByQuery(Object query, int lockLevel);
    
    /**
     * 
     * <p>
     * isClosed
     * </p>
     * <p>
     * indicates whether or not this <code>PersistenceStore</code>
     * instance has been closed.  A closed store will generally
     * throw exceptions when any operation is performed upon it.
     * </p>
     * 
     * @return
     *
     */
    boolean isClosed();
    
    /**
     * 
     * <p>
     * getTransaction
     * </p>
     * <p>
     * Returns the current <code>Transaction</code> for thsis
     * <code>PersistenceStore</code> instance.  The transaction
     * will always be the same for the lifetime of the
     * <code>PersistenceStore</code> instance. 
     * </p>
     * 
     * @return <code>Transaction</code> for this 
     * <code>PersistenceStore</code>
     *
     */
    Transaction getTransaction();
    
    void invalidate(Object obj) throws LockFailedException;
    
    void invalidateAll() throws LockFailedException;
    
    void invalidateExtent(Class clazz) throws LockFailedException;
    
	void invalidateByQuery(Object query) throws LockFailedException;
    
    void lockForWrite(Object obj) throws LockFailedException;
    
    void makePersistent(Object obj) throws LockFailedException;
    
    Filter newFilter();
    
    Object newQuery(Class clazz, Filter filter);
    
    Collection getExtent(Class clazz);
    
	Collection getExtent(Class clazz, int lockLevel);

}
