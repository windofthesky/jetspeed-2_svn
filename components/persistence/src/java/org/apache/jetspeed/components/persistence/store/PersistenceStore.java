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
package org.apache.jetspeed.components.persistence.store;

import java.util.Collection;
import java.util.Iterator;

import org.apache.jetspeed.components.persistence.store.impl.LockFailedException;

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
    
    void invalidateExtent(Class clazz) throws LockFailedException;
    
	void invalidateByQuery(Object query) throws LockFailedException;
    
    void lockForWrite(Object obj) throws LockFailedException;
    
    void makePersistent(Object obj) throws LockFailedException;
    
    Filter newFilter();
    
    Object newQuery(Class clazz, Filter filter);
    
    Collection getExtent(Class clazz);
    
	Collection getExtent(Class clazz, int lockLevel);

}
