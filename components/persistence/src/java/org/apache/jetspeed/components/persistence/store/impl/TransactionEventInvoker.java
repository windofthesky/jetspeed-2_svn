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

import java.util.Collection;
import java.util.Iterator;

import org.apache.jetspeed.components.persistence.store.PersistenceStore;
import org.apache.jetspeed.components.persistence.store.TransactionEventListener;

/**
 * <p>
 * TransactionEventInvoker
 * </p>
 * 
 * 
 * @
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $ $
 *
 */
public class TransactionEventInvoker
{
	
	protected Collection listeners;
	protected PersistenceStore store;
	
	public TransactionEventInvoker(Collection eventListeners, PersistenceStore store)
	{
		this.listeners = eventListeners;
		this.store = store;
	}

    /** 
     * <p>
     * afterCommit
     * </p>
     * 
     * @see org.apache.jetspeed.components.persistence.store.TransactionEventListener#afterCommit(org.apache.jetspeed.components.persistence.store.PersistenceStoreEvent)
     * @param event
     */
    public void afterCommit()
    {
        Iterator itr = listeners.iterator();
        while(itr.hasNext())
        {
        	TransactionEventListener tel = (TransactionEventListener) itr.next();
        	tel.afterCommit(new PersistenceStoreEventImpl(store, null));
        }

    }

    /** 
     * <p>
     * beforeCommit
     * </p>
     * 
     * @see org.apache.jetspeed.components.persistence.store.TransactionEventListener#beforeCommit(org.apache.jetspeed.components.persistence.store.PersistenceStoreEvent)
     * @param event
     */
    public void beforeCommit()
    {
		Iterator itr = listeners.iterator();
		while(itr.hasNext())
		{
			TransactionEventListener tel = (TransactionEventListener) itr.next();
			tel.beforeCommit(new PersistenceStoreEventImpl(store, null));
		}

    }

    /** 
     * <p>
     * afterRollback
     * </p>
     * 
     * @see org.apache.jetspeed.components.persistence.store.TransactionEventListener#afterRollback(org.apache.jetspeed.components.persistence.store.PersistenceStoreEvent)
     * @param event
     */
    public void afterRollback()
    {
		Iterator itr = listeners.iterator();
		while(itr.hasNext())
		{
			TransactionEventListener tel = (TransactionEventListener) itr.next();
			tel.afterRollback(new PersistenceStoreEventImpl(store, null));
		}

    }

    /** 
     * <p>
     * beforeRollback
     * </p>
     * 
     * @see org.apache.jetspeed.components.persistence.store.TransactionEventListener#beforeRollback(org.apache.jetspeed.components.persistence.store.PersistenceStoreEvent)
     * @param event
     */
    public void beforeRollback()
    {
        Iterator itr = listeners.iterator();
		while(itr.hasNext())
		{
			TransactionEventListener tel = (TransactionEventListener) itr.next();
			tel.beforeRollback(new PersistenceStoreEventImpl(store, null));
		}

    }

    /** 
     * <p>
     * afterBegin
     * </p>
     * 
     * @see org.apache.jetspeed.components.persistence.store.TransactionEventListener#afterBegin(org.apache.jetspeed.components.persistence.store.PersistenceStoreEvent)
     * @param event
     */
    public void afterBegin()
    {
		Iterator itr = listeners.iterator();
		while(itr.hasNext())
		{
			TransactionEventListener tel = (TransactionEventListener) itr.next();
			tel.afterBegin(new PersistenceStoreEventImpl(store, null));
		}

    }

    /** 
     * <p>
     * beforeBegin
     * </p>
     * 
     * @see org.apache.jetspeed.components.persistence.store.TransactionEventListener#beforeBegin(org.apache.jetspeed.components.persistence.store.PersistenceStoreEvent)
     * @param event
     */
    public void beforeBegin()
    {
		Iterator itr = listeners.iterator();
		while(itr.hasNext())
		{
			TransactionEventListener tel = (TransactionEventListener) itr.next();
			tel.beforeBegin(new PersistenceStoreEventImpl(store, null));
		}

    }

}
