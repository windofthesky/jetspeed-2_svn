/*
 * Copyright 2000-2001,2004 The Apache Software Foundation.
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
package org.apache.jetspeed.components.persistence.store.ojb.pb;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.jetspeed.components.persistence.store.Filter;
import org.apache.jetspeed.components.persistence.store.PersistenceStore;
import org.apache.jetspeed.components.persistence.store.PersistenceStoreEventListener;
import org.apache.jetspeed.components.persistence.store.Transaction;
import org.apache.jetspeed.components.persistence.store.impl.LockFailedException;
import org.apache.jetspeed.components.persistence.store.impl.StoreEventInvoker;
import org.apache.jetspeed.components.persistence.store.ojb.CriteriaFilter;
import org.apache.ojb.broker.Identity;
import org.apache.ojb.broker.PBFactoryException;
import org.apache.ojb.broker.PBKey;
import org.apache.ojb.broker.PersistenceBroker;
import org.apache.ojb.broker.PersistenceBrokerFactory;
import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.Query;
import org.apache.ojb.broker.query.QueryFactory;


/**
 * <p>
 * PBStore
 * </p>
 *
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $ $
 *
 */
public class PBStore implements PersistenceStore
{
    private PBKey pbKey;
    private List listeners;
    private StoreEventInvoker invoker;
    private PersistenceBroker pb;
   
    protected Set toBeStored;
    
    
    public PBStore(String jcd)
    {
        try
        {
            pbKey = new PBKey(jcd);
   
            if (jcd != null)
            {
                pbKey = new PBKey(jcd);
            }
            else
            {
                pbKey = PersistenceBrokerFactory.getDefaultKey();
            }
            this.listeners = new ArrayList();        
            invoker = new StoreEventInvoker(listeners, this);
            pb = PersistenceBrokerFactory.createPersistenceBroker(pbKey);        
            toBeStored = new HashSet();
        }
        catch (Throwable e)
        {
            // TODO Auto-generated catch block
            System.out.println("==========================>"+e.getCause());
            if(e.getCause() != null)
            {
           e.getCause().printStackTrace();
            }
        }        
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.components.persistence.store.PersistenceStore#addEventListener(org.apache.jetspeed.components.persistence.store.PersistenceStoreEventListener)
     */
    public void addEventListener(PersistenceStoreEventListener listener)
    {
        listeners.add(listener);

    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.components.persistence.store.PersistenceStore#close()
     */
    public void close()
    {
        invoker.beforeClose();
        if (!pb.isClosed())
        {
            pb.close();
        }
        invoker.afterClose();

    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.components.persistence.store.PersistenceStore#deletePersistent(java.lang.Object)
     */
    public void deletePersistent(Object obj) throws LockFailedException
    {
        invoker.beforeDeletePersistent();        
        pb.delete(obj);
        invoker.afterDeletePersistent();

    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.components.persistence.store.PersistenceStore#deleteAll(java.lang.Object)
     */
    public void deleteAll(Object query) throws LockFailedException
    {
        Collection deletes = pb.getCollectionByQuery((Query) query);
        Iterator itr = deletes.iterator();
        while(itr.hasNext())
        {
            invoker.beforeDeletePersistent();  
            pb.delete(itr.next());
            invoker.afterDeletePersistent();
        }

    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.components.persistence.store.PersistenceStore#getCollectionByQuery(java.lang.Object)
     */
    public Collection getCollectionByQuery(Object query)
    {
        invoker.beforeLookup();
        try
        {
            if(pb.isClosed())
            {
                PersistenceBrokerFactory.createPersistenceBroker(pbKey);
            }
            return pb.getCollectionByQuery((Query) query);
        }
        finally
        {
            invoker.afterLookup();
        }
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.components.persistence.store.PersistenceStore#getCollectionByQuery(java.lang.Object, int)
     */
    public Collection getCollectionByQuery(Object query, int lockLevel)
    {
        
        return getCollectionByQuery(query);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.components.persistence.store.PersistenceStore#getObjectByQuery(java.lang.Object)
     */
    public Object getObjectByQuery(Object query)
    {        
        invoker.beforeLookup();
        try
        {
            if(pb.isClosed())
            {
                PersistenceBrokerFactory.createPersistenceBroker(pbKey);
            }
            return pb.getObjectByQuery((Query) query);
        }
        finally
        {
            invoker.afterLookup();
        }
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.components.persistence.store.PersistenceStore#getObjectByQuery(java.lang.Object, int)
     */
    public Object getObjectByQuery(Object query, int lockLevel)
    {
        return getObjectByQuery(query);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.components.persistence.store.PersistenceStore#getObjectByIdentity(java.lang.Object)
     */
    public Object getObjectByIdentity(Object object) throws LockFailedException
    {
        invoker.beforeLookup();
        try
        {
            if(pb.isClosed())
            {
                PersistenceBrokerFactory.createPersistenceBroker(pbKey);
            }
            return pb.getObjectByIdentity(new Identity(object, pb));
        }
        finally
        {
            invoker.afterLookup();
        }
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.components.persistence.store.PersistenceStore#getObjectByIdentity(java.lang.Object, int)
     */
    public Object getObjectByIdentity(Object object, int lockLevel) throws LockFailedException
    {

        return getObjectByIdentity(object);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.components.persistence.store.PersistenceStore#getCount(java.lang.Object)
     */
    public int getCount(Object query)
    {
        if (pb.isClosed())
        {
            PersistenceBrokerFactory.createPersistenceBroker(pbKey);
        }
        return pb.getCount((Query) query);
       
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.components.persistence.store.PersistenceStore#getIteratorByQuery(java.lang.Object)
     */
    public Iterator getIteratorByQuery(Object query)
    {
        invoker.beforeLookup();
        try
        {
            if(pb.isClosed())
            {
                PersistenceBrokerFactory.createPersistenceBroker(pbKey);
            }
            return pb.getIteratorByQuery((Query) query);
        }
        finally
        {
            invoker.afterLookup();
        }
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.components.persistence.store.PersistenceStore#getIteratorByQuery(java.lang.Object, int)
     */
    public Iterator getIteratorByQuery(Object query, int lockLevel)
    {
        return getIteratorByQuery(query);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.components.persistence.store.PersistenceStore#isClosed()
     */
    public boolean isClosed()
    {
        return pb.isClosed();
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.components.persistence.store.PersistenceStore#getTransaction()
     */
    public Transaction getTransaction()
    {
        return new PBTransaction(pb, this);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.components.persistence.store.PersistenceStore#invalidate(java.lang.Object)
     */
    public void invalidate(Object obj) throws LockFailedException
    {
        pb.removeFromCache(obj);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.components.persistence.store.PersistenceStore#invalidateExtent(java.lang.Class)
     */
    public void invalidateExtent(Class clazz) throws LockFailedException
    {
        Iterator itr = pb.getCollectionByQuery(QueryFactory.newQuery(clazz, new Criteria())).iterator();
        while(itr.hasNext())
        {
            invalidate(itr.next());
        }

    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.components.persistence.store.PersistenceStore#invalidateByQuery(java.lang.Object)
     */
    public void invalidateByQuery(Object query) throws LockFailedException
    {
        Iterator itr = pb.getCollectionByQuery((Query)query).iterator();
        while(itr.hasNext())
        {
            invalidate(itr.next());
        }

    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.components.persistence.store.PersistenceStore#lockForWrite(java.lang.Object)
     */
    public void lockForWrite(Object obj) throws LockFailedException
    {
        toBeStored.add(obj);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.components.persistence.store.PersistenceStore#makePersistent(java.lang.Object)
     */
    public void makePersistent(Object obj) throws LockFailedException
    {
        store(obj);
    }

    /**
     * <p>
     *  store
     * </p>
     * 
     * @param obj
     */
    protected void store(Object obj)
    {
        try
        {
            invoker.beforeMakePersistent();
            if(pb.isClosed())
            {
                PersistenceBrokerFactory.createPersistenceBroker(pbKey);
            }
            pb.store(obj);
            invoker.afterMakePersistent();
        }
        finally
        {
            toBeStored.remove(obj);
        }
        
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.components.persistence.store.PersistenceStore#newFilter()
     */
    public Filter newFilter()
    {
        return new CriteriaFilter();
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.components.persistence.store.PersistenceStore#newQuery(java.lang.Class, org.apache.jetspeed.components.persistence.store.Filter)
     */
    public Object newQuery(Class clazz, Filter filter)
    {
        return QueryFactory.newQuery(clazz, ((CriteriaFilter)filter).getOjbCriteria());
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.components.persistence.store.PersistenceStore#getExtent(java.lang.Class)
     */
    public Collection getExtent(Class clazz)
    {
        invoker.beforeLookup();
        try
        {
            if(pb.isClosed())
            {
                PersistenceBrokerFactory.createPersistenceBroker(pbKey);
            }
            return pb.getCollectionByQuery(QueryFactory.newQuery(clazz, new Criteria()));
        }
        finally
        {
            invoker.afterLookup();
        }
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.components.persistence.store.PersistenceStore#getExtent(java.lang.Class, int)
     */
    public Collection getExtent(Class clazz, int lockLevel)
    {
        return getExtent(clazz);
    }
    /* (non-Javadoc)
     * @see org.apache.jetspeed.components.persistence.store.PersistenceStore#invalidateAll()
     */
    public void invalidateAll() throws LockFailedException
    {
        if(pb.isClosed())
        {
            PersistenceBrokerFactory.createPersistenceBroker(pbKey);
        }
        pb.clearCache();
    }
    
   

}
