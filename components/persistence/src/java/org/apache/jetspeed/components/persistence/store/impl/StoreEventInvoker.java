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
package org.apache.jetspeed.components.persistence.store.impl;

import java.util.Iterator;
import java.util.List;

import org.apache.jetspeed.components.persistence.store.PersistenceStore;
import org.apache.jetspeed.components.persistence.store.PersistenceStoreEventListener;

/**
 * <p>
 * StoreEventInvoker
 * </p>
 * 
 * 
 * @
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $ $
 *
 */
public class StoreEventInvoker extends TransactionEventInvoker
{

    /**
     * @param eventListeners
     * @param store
     */
    public StoreEventInvoker(List eventListeners, PersistenceStore store)
    {
        super(eventListeners, store);        
    }

    /** 
     * <p>
     * afterDeletePersistent
     * </p>
     * 
     * @see org.apache.jetspeed.components.persistence.store.PersistenceStoreEventListener#afterDeletePersistent(org.apache.jetspeed.components.persistence.store.PersistenceStoreEvent)
     * @param event
     */
    public void afterDeletePersistent()
    {
		Iterator itr = listeners.iterator();
		while(itr.hasNext())
		{
			PersistenceStoreEventListener psel = (PersistenceStoreEventListener) itr.next();
			psel.afterDeletePersistent(new PersistenceStoreEventImpl(store));
		}

    }

    /** 
     * <p>
     * afterLookup
     * </p>
     * 
     * @see org.apache.jetspeed.components.persistence.store.PersistenceStoreEventListener#afterLookup(org.apache.jetspeed.components.persistence.store.PersistenceStoreEvent)
     * @param event
     */
    public void afterLookup()
    {
		Iterator itr = listeners.iterator();
		while(itr.hasNext())
		{
			PersistenceStoreEventListener psel = (PersistenceStoreEventListener) itr.next();
			psel.afterLookup(new PersistenceStoreEventImpl(store));
		}

    }

    /** 
     * <p>
     * afterMakePersistent
     * </p>
     * 
     * @see org.apache.jetspeed.components.persistence.store.PersistenceStoreEventListener#afterMakePersistent(org.apache.jetspeed.components.persistence.store.PersistenceStoreEvent)
     * @param event
     */
    public void afterMakePersistent()
    {
		Iterator itr = listeners.iterator();
		while(itr.hasNext())
		{
			PersistenceStoreEventListener psel = (PersistenceStoreEventListener) itr.next();
			psel.afterMakePersistent(new PersistenceStoreEventImpl(store));
		}

    }

    /** 
     * <p>
     * beforeDeletePersistent
     * </p>
     * 
     * @see org.apache.jetspeed.components.persistence.store.PersistenceStoreEventListener#beforeDeletePersistent(org.apache.jetspeed.components.persistence.store.PersistenceStoreEvent)
     * @param event
     */
    public void beforeDeletePersistent()
    {
		Iterator itr = listeners.iterator();
		while(itr.hasNext())
		{
			PersistenceStoreEventListener psel = (PersistenceStoreEventListener) itr.next();
			psel.beforeDeletePersistent(new PersistenceStoreEventImpl(store));
		}

    }

    /** 
     * <p>
     * beforeLookup
     * </p>
     * 
     * @see org.apache.jetspeed.components.persistence.store.PersistenceStoreEventListener#beforeLookup(org.apache.jetspeed.components.persistence.store.PersistenceStoreEvent)
     * @param event
     */
    public void beforeLookup()
    {
		Iterator itr = listeners.iterator();
		while(itr.hasNext())
		{
			PersistenceStoreEventListener psel = (PersistenceStoreEventListener) itr.next();
			psel.beforeLookup(new PersistenceStoreEventImpl(store));
		}

    }

    /** 
     * <p>
     * beforeMakePersistent
     * </p>
     * 
     * @see org.apache.jetspeed.components.persistence.store.PersistenceStoreEventListener#beforeMakePersistent(org.apache.jetspeed.components.persistence.store.PersistenceStoreEvent)
     * @param event
     */
    public void beforeMakePersistent()
    {
		Iterator itr = listeners.iterator();
		while(itr.hasNext())
		{
			PersistenceStoreEventListener psel = (PersistenceStoreEventListener) itr.next();
			psel.beforeMakePersistent(new PersistenceStoreEventImpl(store));
		}

    }
    
	public void beforeClose()
	{
		Iterator itr = listeners.iterator();
		while(itr.hasNext())
		{
			PersistenceStoreEventListener psel = (PersistenceStoreEventListener) itr.next();
			psel.beforeClose(new PersistenceStoreEventImpl(store));
		}

	}
	
	public void afterClose()
	{
		Iterator itr = listeners.iterator();
		while(itr.hasNext())
		{
			PersistenceStoreEventListener psel = (PersistenceStoreEventListener) itr.next();
			psel.afterClose(new PersistenceStoreEventImpl(store));
		}

	}

}
