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

import org.apache.jetspeed.components.persistence.store.PersistenceStore;
import org.apache.jetspeed.components.persistence.store.PersistenceStoreEvent;
import org.apache.jetspeed.components.persistence.store.Transaction;

/**
 * <p>
 * PersistenceStoreEventImpl
 * </p>
 * 
 * 
 * @
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $ $
 *
 */
public class PersistenceStoreEventImpl implements PersistenceStoreEvent
{
	private PersistenceStore store;
	
	private Object target;
	
	
	public PersistenceStoreEventImpl(PersistenceStore store, Object target)
	{
		this.store = store;
		this.target = target;
	}

    /** 
     * <p>
     * getPersistenceStore
     * </p>
     * 
     * @see org.apache.jetspeed.components.persistence.store.PersistenceStoreEvent#getPersistenceStore()
     * @return
     */
    public PersistenceStore getPersistenceStore()
    {        
        return store;
    }

    /** 
     * <p>
     * getTransaction
     * </p>
     * 
     * @see org.apache.jetspeed.components.persistence.store.TransactionEvent#getTransaction()
     * @return
     */
    public Transaction getTransaction()
    {
        
        return store.getTransaction();
    }
    
    public Object getTarget()
    {
        return this.target;
    }

}
