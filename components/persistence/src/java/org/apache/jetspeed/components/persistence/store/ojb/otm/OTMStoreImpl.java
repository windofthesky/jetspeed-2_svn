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
package org.apache.jetspeed.components.persistence.store.ojb.otm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.jetspeed.components.persistence.store.Filter;
import org.apache.jetspeed.components.persistence.store.PersistenceStore;
import org.apache.jetspeed.components.persistence.store.PersistenceStoreEventListener;
import org.apache.jetspeed.components.persistence.store.Transaction;
import org.apache.jetspeed.components.persistence.store.TransactionEventListener;
import org.apache.jetspeed.components.persistence.store.impl.LockFailedException;
import org.apache.jetspeed.components.persistence.store.impl.StoreEventInvoker;
import org.apache.jetspeed.components.persistence.store.ojb.CriteriaFilter;
import org.apache.ojb.broker.Identity;
import org.apache.ojb.broker.PBKey;
import org.apache.ojb.broker.PersistenceBrokerFactory;
import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.Query;
import org.apache.ojb.broker.query.QueryFactory;
import org.apache.ojb.otm.OTMConnection;
import org.apache.ojb.otm.kit.SimpleKit;
import org.apache.ojb.otm.lock.LockType;
import org.apache.ojb.otm.lock.LockingException;

/**
 * <p>
 * OTMStoreImpl
 * </p>
 * PersistenceStore implemnetation over the 
 * <a href="http://db.apache.org/ojb">ObjectRelationalBridge</a>
 * <a href="http://db.apache.org/ojb/otm-tutorial.html">OTM</a> api
 *
 * @
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $ $
 *
 */
public class OTMStoreImpl implements PersistenceStore
{
    private List listeners;
    private Transaction tx;
    private OTMConnection OTMConn;
    private SimpleKit kit;
    private PBKey pbKey;
    private StoreEventInvoker invoker;
    private String jcd;

	/**
	 * Name of the JavaConnectionDescriptor this store will use to access
	 * native OJB OTM functionallity.
	 * @param jcd Name of the JavaConnectionDescriptor to use.
	 * @see http://db.apache.org/ojb/repository.html#jdbc-connection-descriptor
	 */
    public OTMStoreImpl(String jcd)
    {
        
        this.listeners = new ArrayList();
        kit = SimpleKit.getInstance();
        invoker = new StoreEventInvoker(listeners, this);
        this.jcd = jcd;

        if (jcd != null)
        {
            pbKey = new PBKey(jcd);
        }
        else
        {
            pbKey = PersistenceBrokerFactory.getDefaultKey();
        }

        OTMConn = kit.acquireConnection(pbKey);

    }

    /** 
     * <p>
     * addEventListener
     * </p>
     * 
     * @see org.apache.jetspeed.components.persistence.store.PersistenceStore#addEventListener(org.apache.jetspeed.components.persistence.store.PersistenceStoreEventListener)
     * @param listener
     */
    public void addEventListener(PersistenceStoreEventListener listener)
    {
        listeners.add(listener);

    }

    /** 
     * <p>
     * close
     * </p>
     * 
     * @see org.apache.jetspeed.components.persistence.store.PersistenceStore#close()
     * 
     */
    public void close()
    {
        try
        {
            invoker.beforeClose();
            if (tx != null && tx.isOpen())
            {
                tx.commit();
            }

            OTMConn.close();
        }
        catch (RuntimeException re)
        {
            if (tx != null && tx.isOpen())
            {
                tx.rollback();
            }
            throw re;
        }
        finally
        {
            tx = null;
            invoker.afterClose();
        }

    }

    /** 
     * <p>
     * deletePersistent
     * </p>
     * 
     * @see org.apache.jetspeed.components.persistence.store.PersistenceStore#deletePersistent(java.lang.Object)
     * @param obj
     */
    public void deletePersistent(Object obj) throws LockFailedException
    {
        try
        {
            invoker.beforeDeletePersistent();
            OTMConn.deletePersistent(obj);
            invoker.afterDeletePersistent();
        }
        catch (LockingException e)
        {
            throw new LockFailedException(e.toString(), e);
        }

    }

    /** 
     * <p>
     * deleteAll
     * </p>
     * 
     * @see org.apache.jetspeed.components.persistence.store.PersistenceStore#deleteAll(java.lang.Object)
     * @param query
     */
    public void deleteAll(Object query) throws LockFailedException
    {
        Iterator itr = getCollectionByQuery(query).iterator();
        while (itr.hasNext())
        {
            deletePersistent(itr.next());
        }

    }

    /** 
     * <p>
     * getCollectionByQuery
     * </p>
     * 
     * @see org.apache.jetspeed.components.persistence.store.PersistenceStore#getCollectionByQuery(java.lang.Object)
     * @param query
     * @return
     */
    public Collection getCollectionByQuery(Object query)
    {
        return getCollectionByQuery(query, LOCK_READ);
    }

    /** 
     * <p>
     * getCollectionByQuery
     * </p>
     * 
     * @see org.apache.jetspeed.components.persistence.store.PersistenceStore#getCollectionByQuery(java.lang.Object, int)
     * @param query
     * @param lockLevel
     * @return
     */
    public Collection getCollectionByQuery(Object query, int lockLevel)
    {
        int lock = LockType.READ_LOCK;
        // Map to the OTM LockType
        switch (lockLevel)
        {
            case LOCK_NO_LOCK :
                lock = LockType.NO_LOCK;
            case LOCK_PESSIMISTIC :
                lock = LockType.WRITE_LOCK;
            case LOCK_READ :
                lock = LockType.READ_LOCK;
        }

        invoker.beforeLookup();
        Collection results = OTMConn.getCollectionByQuery((Query) query, lock);
        invoker.afterLookup();
        return results;
    }

    /** 
     * <p>
     * getObjectByQuery
     * </p>
     * 
     * @see org.apache.jetspeed.components.persistence.store.PersistenceStore#getObjectByQuery(java.lang.Object)
     * @param query
     * @return
     */
    public Object getObjectByQuery(Object query)
    {
        Collection results = getCollectionByQuery(query);
        if (!results.isEmpty())
        {
            return results.iterator().next();
        }

        return null;
    }

    /** 
     * <p>
     * getObjectByQuery
     * </p>
     * 
     * @see org.apache.jetspeed.components.persistence.store.PersistenceStore#getObjectByQuery(java.lang.Object, int)
     * @param query
     * @param lockLevel
     * @return
     */
    public Object getObjectByQuery(Object query, int lockLevel)
    {
        Collection results = getCollectionByQuery(query, lockLevel);
        if (!results.isEmpty())
        {
            return results.iterator().next();
        }

        return null;
    }

    /** 
     * <p>
     * getObjectByIdentity
     * </p>
     * 
     * @see org.apache.jetspeed.components.persistence.store.PersistenceStore#getObjectByIdentity(java.lang.Object)
     * @param object
     * @return
     */
    public Object getObjectByIdentity(Object object) throws LockFailedException
    {
        try
        {
            Identity oid = OTMConn.getIdentity(object);
            invoker.beforeLookup();
            Object obj = OTMConn.getObjectByIdentity(oid);
            invoker.afterLookup();
            return obj;
        }
        catch (LockingException e)
        {
            throw new LockFailedException(e.toString(), e);
        }
    }

    /** 
     * <p>
     * getObjectByIdentity
     * </p>
     * 
     * @see org.apache.jetspeed.components.persistence.store.PersistenceStore#getObjectByIdentity(java.lang.Object, int)
     * @param object
     * @param lockLevel
     * @return
     */
    public Object getObjectByIdentity(Object object, int lockLevel) throws LockFailedException
    {
        try
        {
            int lock = LockType.READ_LOCK;
            // Map to the OTM LockType
            switch (lockLevel)
            {
                case LOCK_NO_LOCK :
                    lock = LockType.NO_LOCK;
                case LOCK_PESSIMISTIC :
                    lock = LockType.WRITE_LOCK;
                case LOCK_READ :
                    lock = LockType.READ_LOCK;
            }

            Identity oid = OTMConn.getIdentity(object);
            invoker.beforeLookup();
            Object obj = OTMConn.getObjectByIdentity(oid, lock);
            invoker.afterLookup();
            return obj;
        }
        catch (LockingException e)
        {
            throw new LockFailedException(e.toString(), e);
        }
    }

    /** 
     * <p>
     * getCount
     * </p>
     * 
     * @see org.apache.jetspeed.components.persistence.store.PersistenceStore#getCount(java.lang.Object)
     * @param query
     * @return
     */
    public int getCount(Object query)
    {
        return OTMConn.getCount((Query) query);
    }

    /** 
     * <p>
     * getIteratorByQuery
     * </p>
     * 
     * @see org.apache.jetspeed.components.persistence.store.PersistenceStore#getIteratorByQuery(java.lang.Object)
     * @param query
     * @return
     */
    public Iterator getIteratorByQuery(Object query)
    {
        invoker.beforeLookup();
        Iterator itr = OTMConn.getIteratorByQuery((Query) query);
        invoker.afterLookup();
        return itr;
    }

    /** 
     * <p>
     * getIteratorByQuery
     * </p>
     * 
     * @see org.apache.jetspeed.components.persistence.store.PersistenceStore#getIteratorByQuery(java.lang.Object, int)
     * @param query
     * @param lockLevel
     * @return
     */
    public Iterator getIteratorByQuery(Object query, int lockLevel)
    {
        int lock = LockType.READ_LOCK;
        // Map to the OTM LockType
        switch (lockLevel)
        {
            case LOCK_NO_LOCK :
                lock = LockType.NO_LOCK;
            case LOCK_PESSIMISTIC :
                lock = LockType.WRITE_LOCK;
            case LOCK_READ :
                lock = LockType.READ_LOCK;
        }

        invoker.beforeLookup();
        Iterator itr = OTMConn.getIteratorByQuery((Query) query, lock);
        invoker.afterLookup();
        return itr;
    }

    /** 
     * <p>
     * isClose
     * </p>
     * 
     * @see org.apache.jetspeed.components.persistence.store.PersistenceStore#isClose()
     * @return
     */
    public boolean isClosed()
    {
        return OTMConn.isClosed();
    }

    /** 
     * <p>
     * getTransaction
     * </p>
     * 
     * @see org.apache.jetspeed.components.persistence.store.PersistenceStore#getTransaction()
     * @return
     */
    public Transaction getTransaction()
    {
        if (tx == null || OTMConn.getTransaction() == null)
        {
            tx = new OTMTransactionImpl(kit.getTransaction(OTMConn), this);
            for (int i = 0; i < listeners.size(); i++)
            {
                tx.addEventListener((TransactionEventListener) listeners.get(i));
            }
        }

        //        if(OTMConn.getTransaction() != tx.getWrappedTransaction())
        //        {
        //        	
        //        	
        //        	tx = new OTMTransactionImpl(OTMConn.getTransaction(), this);
        //        }

        return tx;
    }

    /** 
     * <p>
     * invalidate
     * </p>
     * 
     * @see org.apache.jetspeed.components.persistence.store.PersistenceStore#invalidate(java.lang.Object)
     * @param obj
     */
    public void invalidate(Object obj) throws LockFailedException
    {
        try
        {
            Identity oid = OTMConn.getIdentity(obj);
            OTMConn.invalidate(oid);
        }
        catch (LockingException e)
        {
            throw new LockFailedException(e.toString(), e);
        }

    }

    /** 
     * <p>
     * lockForWrite
     * </p>
     * 
     * @see org.apache.jetspeed.components.persistence.store.PersistenceStore#lockForWrite(java.lang.Object)
     * @param obj
     */
    public void lockForWrite(Object obj) throws LockFailedException
    {
        try
        {
            OTMConn.lockForWrite(obj);
        }
        catch (LockingException e)
        {
            throw new LockFailedException(e.toString(), e);
        }

    }

    /** 
     * <p>
     * makePersistent
     * </p>
     * 
     * @see org.apache.jetspeed.components.persistence.store.PersistenceStore#makePersistent(java.lang.Object)
     * @param obj
     */
    public void makePersistent(Object obj) throws LockFailedException
    {
        try
        {
            invoker.beforeMakePersistent();
            OTMConn.makePersistent(obj);
            invoker.afterMakePersistent();
        }
        catch (LockingException e)
        {
            throw new LockFailedException(e.toString(), e);
        }

    }

    /** 
     * <p>
     * newFilter
     * </p>
     * 
     * @see org.apache.jetspeed.components.persistence.store.PersistenceStore#newFilter()
     * @return
     */
    public Filter newFilter()
    {
        return new CriteriaFilter();
    }

    /** 
     * <p>
     * newQuery
     * </p>
     * 
     * @see org.apache.jetspeed.components.persistence.store.PersistenceStore#newQuery(org.apache.jetspeed.components.persistence.store.Filter)
     * @param filter
     * @return
     */
    public Object newQuery(Class clazz, Filter filter)
    {
        Criteria c = ((CriteriaFilter) filter).getOjbCriteria();
        Query query = QueryFactory.newQuery(clazz, c);
        return query;
    }

    /** 
     * <p>
     * getExtent
     * </p>
     * 
     * @see org.apache.jetspeed.components.persistence.store.PersistenceStore#getExtent(java.lang.Class, int)
     * @param clazz
     * @param lcokLevel
     * @return
     */
    public Collection getExtent(Class clazz, int lockLevel)
    {
        Filter filter = newFilter();
        Object query = newQuery(clazz, filter);
        return getCollectionByQuery(query, lockLevel);
    }

    /** 
     * <p>
     * getExtent
     * </p>
     * 
     * @see org.apache.jetspeed.components.persistence.store.PersistenceStore#getExtent(java.lang.Class)
     * @param clazz
     * @return
     */
    public Collection getExtent(Class clazz)
    {

        return getExtent(clazz, LOCK_READ);
    }

    /**
     * @param transaction
     */
    protected void setTransaction(Transaction transaction)
    {
        tx = transaction;
    }

    /**
     * @see org.apache.jetspeed.components.persistence.store.PersistenceStore#invalidateByQuery(java.lang.Object)
     */
    public void invalidateByQuery(Object query) throws LockFailedException
    {
        Iterator itr = getCollectionByQuery(query).iterator();
        try
        {
            while (itr.hasNext())
            {
                Identity oid = OTMConn.getIdentity(itr.next());
                OTMConn.invalidate(oid);
            }
        }
        catch (LockingException e)
        {
            throw new LockFailedException(e.toString(), e);
        }

    }

    /**
     * @see org.apache.jetspeed.components.persistence.store.PersistenceStore#invalidateExtent(java.lang.Class)
     */
    public void invalidateExtent(Class clazz) throws LockFailedException
    {
		Iterator itr = getExtent(clazz).iterator();
	   try
	   {
		   while (itr.hasNext())
		   {
			   Identity oid = OTMConn.getIdentity(itr.next());
			   OTMConn.invalidate(oid);
		   }
	   }
	   catch (LockingException e)
	   {
		   throw new LockFailedException(e.toString(), e);
	   }

    }

}
